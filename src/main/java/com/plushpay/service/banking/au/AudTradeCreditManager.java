package com.plushpay.service.banking.au;

import com.plushpay.repository.trade.Trade;
import com.plushpay.repository.trade.TradeStatus;
import com.plushpay.repository.trader.Trader;
import com.plushpay.repository.trader.TraderStatus;
import com.plushpay.service.banking.au.nab.nai.NaiFileFilter;
import com.plushpay.service.banking.au.nab.nai.NaiFileParser;
import com.plushpay.service.banking.au.nab.nai.records.NaiTransactionDetail;
import com.plushpay.service.currency.code.CurrencyCodeEnum;
import com.plushpay.service.trade.TradeService;
import com.plushpay.service.trader.TraderService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class AudTradeCreditManager extends Thread {

    private final Log log = LogFactory.getLog(getClass());

    private static final String CSV_HEADER = "Trade ID, Trader ID, USD, AUD,Rate,ROI,USD Sold,USD Bought,Aud Sold,Aud Bought,Total USD, Total AUD\n";
    private BufferedWriter tradeOutputFile;

    private volatile boolean shutdown;
    private long pollPeriod;
    private String folder;
    private String accountNumber;

    private final AudBankSimulator audBankSimulator;
    private final TradeService tradeService;
    private final TraderService traderService;


    public AudTradeCreditManager(AudBankSimulator audBankSimulator, TradeService tradeService,
        TraderService traderService) {
        super("Aud Trade Credit Manager");
        this.audBankSimulator = audBankSimulator;
        this.tradeService = tradeService;
        this.traderService = traderService;

        //Pick the folder location
        this.folder = Thread.currentThread().getContextClassLoader().getResource("com")
            .getPath(); //Get the directory to com

        //Move up to the nab folder in web root
        this.folder = this.folder + "../../../nab/";

        this.shutdown = false;
        this.pollPeriod = 5000;
        this.accountNumber = audBankSimulator.getAccountNumber();

        //Setup for logging trades to csv
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String temp = loader.getResource("com").toURI().getPath();
            temp = temp + "../../../csvfiles/";

            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");

            FileWriter writer = new FileWriter(temp + sdf.format(now.getTime()) + "tradeData.csv");
            this.tradeOutputFile = new BufferedWriter(writer);

            //Write the header line
            this.tradeOutputFile.write(AudTradeCreditManager.CSV_HEADER);

        } catch (URISyntaxException e) {
            //We have a real problem
            this.log.debug("Unable to Start Writing Param Data.", e);
        } catch (IOException e) {
            this.log.debug("Unable to Start Writing Param Data.", e);
        }


    }

    /**
     * check for new nai files to see if we have any new deposits
     *
     * @throws Exception
     */
    public void processNewFiles() throws Exception {

        File dir = new File(this.folder);

        //Find if there is a Nai File
        NaiFileFilter naiFilter = new NaiFileFilter();
        File[] naiFiles = dir.listFiles(naiFilter);

        NaiFileParser parser = new NaiFileParser();

        //Process all of them
        for (int i = 0; i < naiFiles.length; i++) {

            parser.parseInputFile(naiFiles[i]);

            //TODO Delete it (Should check delete return boolean)
            naiFiles[i].delete();

        }

        //Create a list to use
        List<NaiTransactionDetail> details = new ArrayList<NaiTransactionDetail>();

        //Now process each of the files
        for (int i = 0; i < parser.getFiles().size(); i++) {
            //Compile a list of all the deposits(credits) into the account
            details.addAll(parser.getFiles().get(i).getCredits(this.accountNumber));

        }

        this.markTraderCredits(details);

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
                == CurrencyCodeEnum.AUD) {
                // TODO buyers = trh.loadTradersWithGroupId(trades.get(i).getBuyerGroup().getGroupid()); (Doesn't work, why?)
                buyers = trades.get(i).getBuyerGroup().getTraders();
                //sellers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid());
            } else if (trades.get(i).getSellerGroup().getCurrencyToSell().getType().getCode()
                == CurrencyCodeEnum.AUD) {
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

                try {
                    this.writeCSVData(trades.get(i));
                } catch (IOException e) {
                    this.log.error("Unable to write data to csv.", e);
                }

            } else {
                tradeClosed = true;
            }
        }
    }


    private void writeCSVData(Trade trade) throws IOException {
        long tradeId = trade.getId();
        float usdAmount;
        float audAmount;
        float audRate;
        float usdSoldTotal;
        float audSoldTotal;
        float usdBoughtTotal;
        float audBoughtTotal;

        boolean buyersBuyingAud = false;

        float roi;
        //Compute our roi
        float cost = 0;
        float roiGain = 0;

        //What currency are the buyers buying?
        if (trade.getBuyerGroup().getCurrencyToBuy().getType().getCode() == CurrencyCodeEnum.AUD) {
            buyersBuyingAud = true;
        }

        if (buyersBuyingAud) {
            usdSoldTotal = trade.getBuyerGroup().getCurrencyToSell().getValue() / 10000f;
            usdBoughtTotal = -trade.getSellerGroup().getCurrencyToBuy().getValue() / 10000f;
            audSoldTotal = trade.getSellerGroup().getCurrencyToSell().getValue() / 10000f;
            audBoughtTotal = -trade.getBuyerGroup().getCurrencyToBuy().getValue() / 10000f;

        } else {
            usdSoldTotal = trade.getSellerGroup().getCurrencyToSell().getValue() / 10000f;
            usdBoughtTotal = -trade.getBuyerGroup().getCurrencyToBuy().getValue() / 10000f;
            audSoldTotal = trade.getBuyerGroup().getCurrencyToSell().getValue() / 10000f;
            audBoughtTotal = -trade.getSellerGroup().getCurrencyToBuy().getValue() / 10000f;

        }

        //Return on Investment = ((Gain on invest)-(cost of invest))/(cost of invest)
        float totalAUD = (audBoughtTotal + audSoldTotal);
        float totalUSD = (usdBoughtTotal + usdSoldTotal);

        //roiGain = totalAUD + totalUSD;
        //cost = -audBoughtTotal + -usdBoughtTotal;
        //Cost is any value < 0
        if (totalAUD < 0) {
            cost = totalAUD;
        } else {
            roiGain = totalAUD;
        }

        if (totalUSD < 0) {
            cost = cost + totalUSD;
        } else {
            roiGain = roiGain + totalUSD;
        }

        roi = ((float) roiGain - (float) cost) / (float) cost;

        //For each trader, get the buying and selling data
        String line;
        List<Trader> traders = trade.getBuyerGroup().getTraders();
        for (int i = 0; i < traders.size(); i++) {

            if (buyersBuyingAud) {
                usdAmount = traders.get(i).getCurrencyToSell().getValue() / 10000f;
                audAmount = -traders.get(i).getCurrencyToBuy().getValue() / 10000f;
                audRate = traders.get(i).getCurrencyToBuy().getType()
                    .getRateToBase(); //(((float)traders.get(i).getCurrencyToBuy().getValue())/(float)traders.get(i).getCurrencyToBuy().getBaseValue());
            } else {
                usdAmount = -traders.get(i).getCurrencyToBuy().getValue() / 10000f;
                audAmount = traders.get(i).getCurrencyToSell().getValue() / 10000f;
                audRate = traders.get(i).getCurrencyToSell().getType()
                    .getRateToBase(); //(((float)traders.get(i).getCurrencyToSell().getValue())/(float)traders.get(i).getCurrencyToSell().getBaseValue());
            }

            line = tradeId + "," + traders.get(i).getId() +
                "," + usdAmount + "," + audAmount + "," + audRate +
                "," + roi + "," + usdSoldTotal + "," + usdBoughtTotal +
                "," + audSoldTotal + "," + audBoughtTotal + "," + totalAUD + "," + totalUSD + "\n";

            this.tradeOutputFile.write(line);
        }

        traders = trade.getSellerGroup().getTraders();
        for (int i = 0; i < traders.size(); i++) {

            if (buyersBuyingAud) {
                usdAmount = -traders.get(i).getCurrencyToBuy().getValue() / 10000f;
                audAmount = traders.get(i).getCurrencyToSell().getValue() / 10000f;
                //TODO change base value to RATE
                audRate = traders.get(i).getCurrencyToSell().getType()
                    .getRateToBase(); // (((float)traders.get(i).getCurrencyToSell().getValue())/(float)traders.get(i).getCurrencyToSell().getBaseValue());

            } else {
                usdAmount = traders.get(i).getCurrencyToSell().getValue() / 10000f;
                audAmount = -traders.get(i).getCurrencyToBuy().getValue() / 10000f;
                //TODO change base value to RATE
                audRate = traders.get(i).getCurrencyToBuy().getType()
                    .getRateToBase(); //(((float)traders.get(i).getCurrencyToBuy().getValue())/(float)traders.get(i).getCurrencyToBuy().getBaseValue());

            }

            line = tradeId + "," + traders.get(i).getId() +
                "," + usdAmount + "," + audAmount + "," + audRate +
                "," + roi + "," + usdSoldTotal + "," + usdBoughtTotal +
                "," + audSoldTotal + "," + audBoughtTotal + "," + totalAUD + "," + totalUSD + "\n";

            this.tradeOutputFile.write(line);
        }

        this.tradeOutputFile.flush(); //Flush these numbers to file
    }

    /**
     * Collect open trades from DB and mark inputs for traders who have submitted funds
     *
     * @param details
     */
    private void markTraderCredits(List<NaiTransactionDetail> details) {

        if (details.size() > 0) {
            //Get the traders to compare to
            List<Trader> traders = traderService.getSelling(TraderStatus.DEPOSIT,
                CurrencyCodeEnum.AUD).toList();

            for (int i = 0; i < traders.size(); i++) {
                for (int j = 0; j < details.size(); j++) {
                    //Check to see if the account credit matches the trader
                    //TODO also compare amounts
                    if (details.get(j).getReferenceNumber()
                        .equals(String.valueOf(traders.get(i).getId()))) {
                        this.log.info("Marking Trader: " + traders.get(i).getId() +
                            " as Finalized for deposit of " +
                            (details.get(j).getAmount() / 100));
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


    public void run() {

        while (!this.shutdown) {

            //Check for new files
            try {
                this.processNewFiles();
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

        if (this.tradeOutputFile != null) {
            try {
                this.tradeOutputFile.flush();
                this.tradeOutputFile.close();
            } catch (IOException e1) {
                this.log.error("Unable to close logfile.", e1);
            }
        }

        this.log.info("Shutting Down Aud Trade Credit Manager.");

    }

    public void startUp() {
        // TODO Check thread state before starting
        this.shutdown = false;
        this.start();

    }


}
