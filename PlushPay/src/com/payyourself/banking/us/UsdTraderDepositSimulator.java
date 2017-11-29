package com.payyourself.banking.us;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.banking.au.nab.directEntry.NabDirectEntryFile;
import com.payyourself.banking.au.nab.directEntry.NabDirectEntryFileFilter;
import com.payyourself.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.payyourself.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.payyourself.banking.au.nab.directEntry.records.NabDetailRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabFileTotalRecord;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trade.TradeHibernation;
import com.payyourself.trading.trade.TradeStatus;
import com.payyourself.trading.trader.Trader;


/**
 * Simulator to create a deposit for all traders in
 * DEPOSIT_FUNDS state trades.
 * 
 * @author tpacker
 *
 */
public class UsdTraderDepositSimulator extends Thread{

	private Logger log;
	private volatile boolean shutdown;
	private long pollPeriod;
	private String folder;
	
	private List<Long> processedTrades;

	private static UsdTraderDepositSimulator singleton;
	
	public static UsdTraderDepositSimulator getAudTraderDeptositSimulator(){
		if (UsdTraderDepositSimulator.singleton == null)
			// it's ok, we can call this constructor
			UsdTraderDepositSimulator.singleton = new UsdTraderDepositSimulator();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	private UsdTraderDepositSimulator(){
		super("Aud Trader Deposit Simulator");
		this.shutdown = false;
		this.pollPeriod = 5000;

		//Pick the folder location
		this.folder = Thread.currentThread().getContextClassLoader().getResource("com").getPath(); //Get the directory to com
		
		//Move up to the nab folder in web root
		this.folder = this.folder + "../../../nab/";

		
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		this.processedTrades = new ArrayList<Long>();
		
	}
	
	private List<Trade> getTradersReadyForDeposit() {
		//First find a list of all traders that need to deposit AUD funds
		TradeHibernation th = new TradeHibernation();
		return th.loadAllWithStatusExcluding(TradeStatus.DEPOSIT,this.processedTrades);
		
	}

	public void run() {
		
		List<Trade> newTrades;
		
		Session sesh = null;
		while(!this.shutdown){
			
			try {
				sesh = HibernateUtil.getSessionFactory().getCurrentSession();
				sesh.beginTransaction();
				newTrades = this.getTradersReadyForDeposit();
				this.simulateDeposits(newTrades);
				
			} catch (Exception e1) {
				this.log.error("Unable to Simulate Trader Deposits.",e1);
			}finally{
				if(sesh!=null)
					sesh.close(); //In case we faultered
			}
			
			
			try {
				Thread.sleep(this.pollPeriod);
			} catch (InterruptedException e) {
				if(shutdown){
					break;
				}else{
					this.log.error("Interrupted.",e);
				}
				
			}
		}//end while
		this.log.info("Shutting Down.");
		
	}

	/**
	 * Simulate any deposits that should be made into our account 
	 * by traders selling AUD
	 * @throws Exception
	 */
	public void simulateDeposits(List<Trade> trades) throws Exception {
		
		
		if(trades.size() > 0){
		
			long totalCredits = 0;
			List<NabDetailRecord> credits = new ArrayList<NabDetailRecord>();
			NabDetailRecord credit = null;
			long amount,withholdingTax;
			List<Trader> traders = null;
			
			//Create a DirectEntry File with a credit for each trader
			for(int i=0; i<trades.size(); i++){
				
				//Get the group selling AUD
				//We need to detect the Seller of AUD as they will be crediting our account
	 			if(trades.get(i).getBuyerGroup().getCurrencyToSell().getType().getCode().equals("AUD")){
					traders = trades.get(i).getBuyerGroup().getTraders();
				}else if(trades.get(i).getSellerGroup().getCurrencyToSell().getType().getCode().equals("AUD")){
					traders = trades.get(i).getSellerGroup().getTraders();
				}else{
					traders = new ArrayList<Trader>(); //Create emtpy list if neither
				}

	 			
				for(int j=0; j<traders.size(); j++){
					String bsbNumber = "999-000";
					String accountNumber = "123456789";
					NabDirectEntryIndicator indicator = NabDirectEntryIndicator.NONE;
					NabTransactionCode code = NabTransactionCode.CREDIT;
					amount = traders.get(j).getCurrencyToSell().getValue()/100; //WATCH ROUNDING!!!!
					totalCredits = totalCredits + amount; //add it to the total
					String accountTitle = "Trader Account Title";
					String lodgementReference = String.valueOf(trades.get(i).getTradeId());
					String userBsb = UsdBankSimulator.getBsbNumber();
					String userAccountNumber = UsdBankSimulator.getAccountNumber();
					String remitter = traders.get(j).getTraderid() + "";
					withholdingTax = 0;
					
					//Create a debit for each
					credit = new NabDetailRecord(bsbNumber,
								accountNumber,
								indicator,
								code,
								amount,
								accountTitle,
								lodgementReference,
								userBsb,
								userAccountNumber,
								remitter,
								withholdingTax);
					credits.add(credit);
					
					this.log.info("Simulating deposit of " + (amount/100) + " for " + remitter + " in trade " + trades.get(i).getTradeId());
				}
			
				this.processedTrades.add(trades.get(i).getTradeId());
				
			}// End for trades


			//Write file to bank
			//Create a file
			NabDirectEntryFile file = new NabDirectEntryFile();
			file.setDetails(credits);
			
			//We need to add total
			NabFileTotalRecord fileTotal = new NabFileTotalRecord(UsdBankSimulator.getBsbNumber(), totalCredits, totalCredits, 0, credits.size());
			file.setTotalRecord(fileTotal);

			//Write it out
			 //Write it to disk
			 Calendar now = Calendar.getInstance();
			 SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");
			 
			 NabDirectEntryFileFilter nabFilter = new NabDirectEntryFileFilter();
			 this.log.info("Writing Nab File: dep_" +sdf.format(now.getTime()) + nabFilter.getExtension());
			 
			 File output = new File(this.folder + "dep_" + sdf.format(now.getTime()) + nabFilter.getExtension());
			 FileWriter fw = new FileWriter(output);
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write(file.generateFile());
			 bw.flush();
			 bw.close();
			
		}
		
		
	}


	public void shutDown() {
		//Shut'er down
		this.shutdown = true;
		this.interrupt();
		
	}

}
