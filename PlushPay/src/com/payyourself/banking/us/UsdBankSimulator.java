package com.payyourself.banking.us;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.payyourself.banking.DebitCredit;
import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.PyCurrency;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.log.LogfileFactory;


/**
 * Singleton class for one simulator
 * @author tpacker
 *
 */
public class UsdBankSimulator extends Thread{
	
	private String accountNumber = "9999999";
	private PyCurrency accountBalance; //In Usd cents
	private PyCurrencyType type;
	
	private String folder; //Location on server where files will be dropped and picked up
	private boolean shutdown; //Flag to shutdown thread;
	
	private List<UsdBankTransaction> transactionHistory;
	private List<UsdBankTransaction> withdrawls;
	private List<UsdBankTransaction> deposits;
	
	private Logger log; //Error logger
	
	private long pollPeriod;

	//Singleton instance
	private static UsdBankSimulator singleton;
	
	/**
	 * Singleton Access
	 * @return
	 */
	public static UsdBankSimulator getUsdBankSimulator(){
		if (UsdBankSimulator.singleton == null)
			// it's ok, we can call this constructor
			UsdBankSimulator.singleton = new UsdBankSimulator();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	private UsdBankSimulator(){
		super("Usd Bank Simulator");
		
		//Pick the folder location
		this.folder = Thread.currentThread().getContextClassLoader().getResource("com").getPath(); //Get the directory to com
		
		//Move up to the nab folder in web root
		this.folder = this.folder + "../../../nab/";
		
		this.type = new PyCurrencyType("USD",10000,"$");
		this.accountBalance = new PyCurrency(0,0,type);
		
		this.transactionHistory = new ArrayList<UsdBankTransaction>();
		this.deposits = new ArrayList<UsdBankTransaction>();
		this.withdrawls = new ArrayList<UsdBankTransaction>();
		
		this.shutdown = false;
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
		this.pollPeriod = 5000;
	}
	
	
	public String getAccountBalance(){
		CurrencyConverter conv = new CurrencyConverter();
		return conv.getAsString(null, null, this.accountBalance);
	}
	
	



	public void shutDown() {
		this.shutdown = true; //Shut'er down Mike
		
		this.interrupt(); //Interrupt execution
	}


	public void run() {
		
		while(true){
			try {
				
				
				Thread.sleep(this.pollPeriod);

			} catch (InterruptedException e) {
				if(this.shutdown){
					this.log.info("Shutting down Usd Bank Simulator");
					return;
				}else{
					this.log.error("Interrupted Exception: " ,e);
				}
			} catch (Exception e) {
				this.log.error("Bank Simulator Error",e);
			}
		}
		
	}

	public synchronized void addWithdrawl(PyCurrency amount, long traderId, long tradeId) throws Exception{
		
		//
		
		UsdBankTransaction newTrans = new UsdBankTransaction(DebitCredit.DEBIT,traderId,tradeId,amount.getBaseValue()/100);
		this.transactionHistory.add(newTrans);
		this.withdrawls.add(newTrans);
		this.accountBalance = this.accountBalance.minus(amount);
		
		
		
	}
	
	public synchronized void addDeposit(PyCurrency amount, long traderId,long tradeId) throws Exception{
		
		UsdBankTransaction newTrans = new UsdBankTransaction(DebitCredit.CREDIT,traderId,tradeId,amount.getBaseValue()/100);
		this.transactionHistory.add(newTrans);
		this.deposits.add(newTrans);
		this.accountBalance = this.accountBalance.add(amount);
	}
	
	
	/**
	 * Get any deposits made since last call of this function
	 * @return
	 */
	public synchronized List<UsdBankTransaction> getDeposits(){
		
		List<UsdBankTransaction> deposits = this.deposits;
		this.deposits = new ArrayList<UsdBankTransaction>();
		return deposits;
		
		
	}
	
	/**
	 * Get any withdrawls made since last call of this function
	 * @return
	 */
	public synchronized List<UsdBankTransaction> getWithdrawls(){
		
		List<UsdBankTransaction> withdrawls = this.withdrawls;
		this.withdrawls = new ArrayList<UsdBankTransaction>();
		return withdrawls;
		
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	
	
	
}
