package com.plushpay.service.banking.us;

import com.plushpay.repository.trade.Trade;
import com.plushpay.repository.trade.TradeStatus;
import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trader.TraderService;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


/**
 * This class is designed to continually check the NAI Files from Nab (in the real system every day
 * at 7AM) and mark all traders whom have input their funds.
 * <p>
 * Also we will mark a trade with all members having input their funds, as ''??
 *
 * @author tpacker
 */
@ConditionalOnProperty(value = "com.plushpay.simulation.enabled", havingValue = "true")
@Component
public class UsdTradeCreditManager extends Thread {

    private Log log = LogFactory.getLog(getClass());
    private volatile boolean shutdown;
    private long pollPeriod;
    private String folder;
    private String accountNumber;

    private final UsdBankSimulator usdBankSimulator;
    private final TradeService tradeService;
    private final TraderService traderService;


    public UsdTradeCreditManager(UsdBankSimulator usdBankSimulator, TradeService tradeService,
        TraderService traderService) {
        super("Usd Trade Credit Manager");
        this.usdBankSimulator = usdBankSimulator;
        this.tradeService = tradeService;
        this.traderService = traderService;

        //Pick the folder location
        this.folder = Thread.currentThread().getContextClassLoader().getResource("com")
            .getPath(); //Get the directory to com

        //Move up to the nab folder in web root
        this.folder = this.folder + "../../../nab/";

        this.shutdown = false;
        this.pollPeriod = 5000;
        this.accountNumber = usdBankSimulator.getAccountNumber();
    }

    /**
     * check for new deposits
     *
     * @throws Exception
     */
    public void processNewDeposits() throws Exception {

        //Get any new deposits from bank.
        List<UsdBankTransaction> deposits = usdBankSimulator.getDeposits();

        if (deposits.size() > 0) {
            this.markTraderCredits(deposits);
        }
    }

    /**
     * Process the trades in the DB to see if everyone is in and we can finalize a trade.
     */
    public void processOpenTrades() {

        //Collect all trades
        List<Trade> trades = tradeService.getAllWithStatus(TradeStatus.DEPOSIT).toList();

        List<Trader> buyers = null;
        //List<Trader> sellers = null;

        boolean tradeClosed = true;

        //For each trade check all traders
        for (int i = 0; i < trades.size(); i++) {

            //We need to detect the Seller of AUD as they will be crediting our account
            if (trades.get(i).getBuyerGroup().getCurrencyToSell().getType().getCode()
                .equals("USD")) {
                // TODO buyers = trh.loadTradersWithGroupId(trades.get(i).getBuyerGroup().getGroupid()); (Doesn't work, why?)
                buyers = trades.get(i).getBuyerGroup().getTraders();
                //sellers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid());
            } else if (trades.get(i).getSellerGroup().getCurrencyToSell().getType().getCode()
                .equals("USD")) {
                //TODO buyers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid()); (Doesn't work, why?)
                buyers = trades.get(i).getSellerGroup().getTraders();
                //sellers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid());
            }

            for (int j = 0; j < buyers.size(); j++) {
                //If any buyer is not finalized then the trade cannot change states
                if (!buyers.get(j).getStatus().equals(TraderStatus.FINALIZED.name())) {
                    tradeClosed = false;
                    this.log.info("Trader " + buyers.get(j).getId()
                        + " not finalized, unable to close trade: " + trades.get(i).getId());
                    break;
                }
            }

            if (buyers.size() == 0) {
                tradeClosed = false;
            }
			
			/*//Check sellers if we need to (NOT YET)
			if(tradeClosed){
				for(int j=0; j<sellers.size(); j++){
					//If any seller is not finalized then the trade cannot change states
					if(!sellers.get(j).getStatus().equals(TraderStatus.FINALIZED.name())){
						tradeClosed = false;
						break;
					}
				}
			}*/

            //Is everyone in?
            if (tradeClosed) {
                //yes then close trade
                trades.get(i).setStatus(TradeStatus.CLOSED);
                tradeService.save(trades.get(i));
                this.log.info("Closing Trade : " + trades.get(i).getId());

            } else {
                tradeClosed = true;
            }


        }


    }

    /**
     * Collect open trades from DB and mark inputs for traders who have submitted funds
     *
     * @param deposits
     */
    private void markTraderCredits(List<UsdBankTransaction> deposits) {

        if (deposits.size() > 0) {
            //Get the traders to compare to
            List<Trader> traders = traderService.getSelling(TraderStatus.DEPOSIT,
                CurrencyCodeEnum.USD).toList();

            for (int i = 0; i < traders.size(); i++) {
                for (int j = 0; j < deposits.size(); j++) {
                    //Check to see if the account credit matches the trader
                    //TODO also compare amounts
                    if (deposits.get(j).getTraderId() == traders.get(i).getId()) {
                        this.log.info("Marking Trader: " + traders.get(i).getId() +
                            " as Finalized for deposit of " +
                            (deposits.get(j).getAmount()));
                        //If the trader ID == the reference number (for now)
                        traders.get(i).setStatus(TraderStatus.FINALIZED);
                        traderService.save(traders.get(i)); //Update in DB.
                    }
                }
            }


        }//if size > 0
    }

    /**
     * To Shutdown
     */
    public void shutDown() {

        //Shut'er down
        this.shutdown = true;
        this.interrupt(); //Might help if we are sleeping we will shutdown immediately
    }

    public void startUp() {
        //TODO Check thread state before starting
        this.shutdown = false;
        this.start();
    }


    public void run() {

        while (!this.shutdown) {

            //Check for new files
            try {
                this.processNewDeposits();
            } catch (Exception e1) {
                this.log.error("Unable to process new Nai Files.", e1);
            }

            //Check open trades to see if all traders are Finalized
            this.processOpenTrades();

            try {
                Thread.sleep(this.pollPeriod);
            } catch (InterruptedException e) {
                if (this.shutdown) {

                    break;
                }
                this.log.error("Interrupted?", e);
            }

        }//end while

        this.log.info("Shutting Down Aud Trade Credit Manager.");

    }


}
