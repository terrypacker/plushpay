package com.payyourself.banking.us;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.banking.au.nab.nai.NaiFileFilter;
import com.payyourself.banking.au.nab.nai.NaiFileParser;
import com.payyourself.banking.au.nab.nai.records.NaiTransactionDetail;
import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trade.TradeHibernation;
import com.payyourself.trading.trade.TradeStatus;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;


/**
 * This class is designed to continually check the NAI Files
 * from Nab (in the real system every day at 7AM) and mark
 * all traders whom have input their funds. 
 * 
 * Also we will mark a trade with all members having input their funds,
 * as ''??
 * 
 * @author tpacker
 *
 */
public class UsdTradeCreditManager extends Thread{
	
	private Logger log;
	private volatile boolean shutdown;
	private long pollPeriod;
	private String folder;
	private String accountNumber;

	private CurrencyConverter conv;
	
	private static UsdTradeCreditManager singleton;
	
	public static UsdTradeCreditManager getUsdTradeCreditManager(){
		if (UsdTradeCreditManager.singleton == null)
			// it's ok, we can call this constructor
			UsdTradeCreditManager.singleton = new UsdTradeCreditManager();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	
	private UsdTradeCreditManager(){
		super("Usd Trade Credit Manager");

		
		//Pick the folder location
		this.folder = Thread.currentThread().getContextClassLoader().getResource("com").getPath(); //Get the directory to com
		
		//Move up to the nab folder in web root
		this.folder = this.folder + "../../../nab/";
		
		this.shutdown = false;
		this.pollPeriod = 5000;
		this.accountNumber = UsdBankSimulator.getUsdBankSimulator().getAccountNumber();
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		this.conv = new CurrencyConverter();
	}
	
	/**
	 * check for new deposits
	 * @throws Exception
	 */
	public void processNewDeposits() throws Exception{
		
		//Get any new deposits from bank.
		List<UsdBankTransaction> deposits = UsdBankSimulator.getUsdBankSimulator().getDeposits();
		
		if(deposits.size() > 0)
			this.markTraderCredits(deposits);
	}
	
	/**
	 * Process the trades in the DB to see if everyone is in 
	 * and we can finalize a trade.
	 */
	public void processOpenTrades() {
		
		//Collect all trades
		TradeHibernation th = new TradeHibernation();
		List<Trade> trades = th.loadAllWithStatus(TradeStatus.DEPOSIT);

		List<Trader> buyers = null;
		//List<Trader> sellers = null;
		
		boolean tradeClosed = true;
		
		//For each trade check all traders
		for(int i=0; i<trades.size(); i++){
			
			//We need to detect the Seller of AUD as they will be crediting our account
 			if(trades.get(i).getBuyerGroup().getCurrencyToSell().getType().getCode().equals("USD")){
				// TODO buyers = trh.loadTradersWithGroupId(trades.get(i).getBuyerGroup().getGroupid()); (Doesn't work, why?)
				buyers = trades.get(i).getBuyerGroup().getTraders();
				//sellers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid());
			}else if(trades.get(i).getSellerGroup().getCurrencyToSell().getType().getCode().equals("USD")){
				//TODO buyers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid()); (Doesn't work, why?)
				buyers = trades.get(i).getSellerGroup().getTraders();
				//sellers = trh.loadTradersWithGroupId(trades.get(i).getSellerGroup().getGroupid());
			}
			
			for(int j=0; j<buyers.size(); j++){
				//If any buyer is not finalized then the trade cannot change states
				if(!buyers.get(j).getStatus().equals(TraderStatus.FINALIZED.name())){
 					tradeClosed = false;
 					this.log.info("Trader " + buyers.get(j).getTraderid() + " not finalized, unable to close trade: " + trades.get(i).getTradeId());
					break;
				}
			}
			
			if(buyers.size() == 0){
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
			if(tradeClosed){
				//yes then close trade
				trades.get(i).setStatus(TradeStatus.CLOSED.toString());
				th.persist(trades.get(i));
				this.log.info("Closing Trade : " + trades.get(i).getTradeId());
				
			}else{
				tradeClosed = true;
			}
			
			
		}
		
		
	}

	/**
	 * Collect open trades from DB and mark inputs for traders
	 * who have submitted funds
	 * @param naiFile
	 */
	private void markTraderCredits(List<UsdBankTransaction> deposits) {
		
		if(deposits.size() > 0){
			TraderHibernation th = new TraderHibernation();
			
			//Get the traders to compare to
			List<Trader> traders = th.loadTradersSelling(TraderStatus.DEPOSIT,CurrencyCodeEnum.USD);
		
			for(int i=0; i<traders.size(); i++){
				for(int j=0; j<deposits.size(); j++){
					//Check to see if the account credit matches the trader
					//TODO also compare amounts
					if(deposits.get(j).getTraderId() == traders.get(i).getTraderid()){
						this.log.info("Marking Trader: " + traders.get(i).getTraderid() +
								" as Finalized for deposit of " +
								(deposits.get(j).getAmount()));
						//If the trader ID == the reference number (for now)
						traders.get(i).setStatus(TraderStatus.FINALIZED.name());
						th.persist(traders.get(i)); //Update in DB.
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


		Session sesh = null;
		while(!this.shutdown){
			
			//Check for new files
			try {
				sesh = HibernateUtil.getSessionFactory().getCurrentSession();
				sesh.beginTransaction();
				this.processNewDeposits();
				sesh.flush();
			} catch (Exception e1) {
				this.log.error("Unable to process new Nai Files." ,e1);
			}finally{
				if(sesh!=null)
				sesh.close();
			}
			
			//Check open trades to see if all traders are Finalized
			sesh = HibernateUtil.getSessionFactory().getCurrentSession();
			sesh.beginTransaction();
			this.processOpenTrades();
			sesh.flush();
			sesh.close();
			
			
			
			try {
				Thread.sleep(this.pollPeriod);
			} catch (InterruptedException e) {
				if(this.shutdown){

					break;
				}
				this.log.error("Interrupted?",e);
			}
			
		}//end while
		
		this.log.info("Shutting Down Aud Trade Credit Manager.");
		
	}

	
	
	
	
	
}
