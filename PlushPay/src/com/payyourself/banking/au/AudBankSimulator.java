package com.payyourself.banking.au;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;


import com.payyourself.banking.au.nab.directEntry.NabDirectEntryFile;
import com.payyourself.banking.au.nab.directEntry.NabDirectEntryFileFilter;
import com.payyourself.banking.au.nab.directEntry.NabDirectEntryFileParser;
import com.payyourself.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.payyourself.banking.au.nab.directEntry.codes.NabTransactionCode;
import com.payyourself.banking.au.nab.directEntry.records.NabDetailRecord;
import com.payyourself.banking.au.nab.directEntry.records.NabFileTotalRecord;
import com.payyourself.banking.au.nab.nai.NaiFile;
import com.payyourself.banking.au.nab.nai.NaiFileFilter;
import com.payyourself.banking.au.nab.nai.codes.NaiAccountSummaryCode;
import com.payyourself.banking.au.nab.nai.codes.NaiTransactionCode;
import com.payyourself.banking.au.nab.nai.records.NaiAccountSummary;
import com.payyourself.banking.au.nab.nai.records.NaiAccountSummaryDetail;
import com.payyourself.banking.au.nab.nai.records.NaiAccountTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiFileHeader;
import com.payyourself.banking.au.nab.nai.records.NaiFileTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiGroupHeader;
import com.payyourself.banking.au.nab.nai.records.NaiGroupTrailer;
import com.payyourself.banking.au.nab.nai.records.NaiTransactionDetail;
import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.log.LogfileFactory;


/**
 * Singleton class for one simulator
 * @author tpacker
 *
 */
public class AudBankSimulator extends Thread{
	
	private static String bsbNumber = "833-866";
	private static String accountNumber = "123456789";
	private static String pyCustomerId = "PYID";
	private static String bankId = "12345678";
	private static String accountTitle = "Pay Yourself Account";

	private PyCurrency accountBalance; //In Aud cents
	private PyCurrencyType type;
	
	
	private String folder; //Location on server where files will be dropped and picked up
	private boolean shutdown; //Flag to shutdown thread;
	
	private List<NaiTransactionDetail> transactions;
	private List<NaiTransactionDetail> transactionHistory;
	
	private Logger log; //Error logger
	
	private long pollPeriod;
	
	//Singleton instance
	private static AudBankSimulator singleton;
	
	/**
	 * Singleton Access
	 * @return
	 */
	public static AudBankSimulator getAudBankSimulator(){
		if (AudBankSimulator.singleton == null)
			// it's ok, we can call this constructor
			AudBankSimulator.singleton = new AudBankSimulator();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	private AudBankSimulator(){
		super("Aud Bank Simulator");
		
		//Pick the folder location
		this.folder = Thread.currentThread().getContextClassLoader().getResource("com").getPath(); //Get the directory to com
		
		//Move up to the nab folder in web root
		this.folder = this.folder + "../../../nab/";
		
		this.type = new PyCurrencyType(CurrencyCodeEnum.AUD,9200,"$",Calendar.getInstance());
		this.accountBalance = new PyCurrency(0,type);

		this.transactions = new ArrayList<NaiTransactionDetail>();
		this.transactionHistory = new ArrayList<NaiTransactionDetail>();
		this.shutdown = false;
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
		this.pollPeriod = 5000;
	}
	
	
	public NaiFile generateNaiFile(){
		
		int recordLength=0;
		int blockSize=0;
		
		//Setup the file Header
		NaiFileHeader header = new NaiFileHeader(AudBankSimulator.pyCustomerId,
				Calendar.getInstance(),
				recordLength,blockSize);
		
		//Create the Account Groups (1)
		NaiGroupHeader groupHeader = new NaiGroupHeader(AudBankSimulator.pyCustomerId, AudBankSimulator.bankId,
				Calendar.getInstance());
		
		//Create an Account Summary
		NaiAccountSummary accountSummary = new NaiAccountSummary(AudBankSimulator.accountNumber,CurrencyCodeEnum.AUD);
		
		//Add transactions to account summary
		accountSummary.setTransactions(this.transactions);
		
		//TODO Should add the old transaction to a history.
		for(int i=0; i<this.transactions.size(); i++){
			this.transactionHistory.add(transactions.get(i));
		}
		//Clear out the transactions now
		this.transactions = new ArrayList<NaiTransactionDetail>();
		
		//Add details to the account summary
		long sumTotA=0;
		long sumTotB=0;
		NaiAccountSummaryDetail closingBalance = new NaiAccountSummaryDetail(NaiAccountSummaryCode.CLOSING_BALANCE,this.accountBalance.getValue());
		accountSummary.addSummaryDetail(closingBalance);
		sumTotA = sumTotA + this.accountBalance.getValue();
		

		//Add an account Trailer
		long accountTotalA=sumTotA; //Sum of amounts fields in 03 records, including 965,966,967,968,969 codes and 16 and 88 records
		long accountTotalB=sumTotB; //	The sum of all amount fields in record types Ô03Õ 
									 //(excluding the amounts for account summary codes 965,966,967,968,969), Ô16Õ and Ô88Õ for the account.
		NaiAccountTrailer accountTrailer = new NaiAccountTrailer(accountTotalA,accountTotalB);
		accountSummary.setTrailer(accountTrailer);
		
		//Add accountSummary
		groupHeader.addAccount(accountSummary);

		
		//Add a group trailer
		long groupTotalA=accountTotalA; //	The sum of the Account control totals A in all Account trailer (record type Ô49Õ) records in this group.
		int numberOfAccounts=1; //	The number of accounts in this group. That is the number of Account identifier and summary status (record type Ô03Õ) records in this group.
		long groupTotalB=accountTotalA; //	The sum of the Account control totals B in all Account trailer (record type Ô49Õ) records in this group
		
		//Account Trailer
		NaiGroupTrailer groupTrailer = new NaiGroupTrailer(groupTotalA,numberOfAccounts,groupTotalB);
		groupHeader.setTrailer(groupTrailer);
		

		
		header.addGroup(groupHeader);

		
		long fileTotalA = groupTotalA;//	The sum of the Group control totals A in all  Group Trailer (record type Ô98Õ) records in this file.
		int numberOfGroups = 1;//	The number of groups in this file. That is, the number of Group header (record type Ô02Õ) records in this file.
		int numberOfRecords=7 + this.transactions.size();//	The total number of records in this file. This includes the File header and File trailer records but excludes any device-oriented or job control records
		long fileTotalB=groupTotalB;  //	The sum of the Group control totals B in all Group Trailer (record type Ô98Õ) records in this file.
		
		NaiFileTrailer fileTrailer = new NaiFileTrailer(fileTotalA,numberOfGroups,numberOfRecords,fileTotalB);
		header.setTrailer(fileTrailer);
		
		NaiFile file = new NaiFile(header);
		
		return file;
		
	}
	/**
	 * Process the direct entry file make transactions
	 * @throws Exception 
	 */
	public void processTransactions(NabDirectEntryFile file) throws Exception{
		
		
		NaiTransactionDetail transaction;
		
		for(int i=0; i<file.getDetails().size(); i++){
			
			if(file.getDetails().get(i).getUserAccountNumber().equals(AudBankSimulator.accountNumber)){
				//Is it for our account?
				NabTransactionCode code = file.getDetails().get(i).getCode();
				
				//Do any of the following codes
				switch(code){
				case DEBIT:
					this.log.info("Debiting Account: " + (file.getDetails().get(i).getAmount()/100) + " for trader " +
							file.getDetails().get(i).getRemitter() + " in trade " + file.getDetails().get(i).getLodgementReference() );
					//Remove from account total
					//multiply by 100
					this.accountBalance = this.accountBalance.minus(new PyCurrency(file.getDetails().get(i).getAmount()*100, this.type));
					
					//Add a transaction
					transaction = new NaiTransactionDetail(NaiTransactionCode.TRANSFER_DEBIT,
							file.getDetails().get(i).getAmount(),
							file.getDetails().get(i).getRemitter(),
							file.getDetails().get(i).getLodgementReference());
					
					
					this.transactions.add(transaction);
					
					break;
				case CREDIT:
					this.log.info("Crediting Account: " + (file.getDetails().get(i).getAmount()/100) + " for trader " +
							file.getDetails().get(i).getRemitter() + " in group " + file.getDetails().get(i).getLodgementReference() );
					//Add to account total (multiply by 100)
					this.accountBalance = this.accountBalance.add(new PyCurrency(file.getDetails().get(i).getAmount()*100,this.type));
					//Add a transaction
					transaction = new NaiTransactionDetail(NaiTransactionCode.TRANSFER_CREDIT,
							file.getDetails().get(i).getAmount(),
							file.getDetails().get(i).getRemitter(),
							file.getDetails().get(i).getLodgementReference());
					
					
					this.transactions.add(transaction);

					break;
				}//end case

				
			}//end if
			
		}//end for
		
		if(file.getDetails().size()>0){
			this.log.info("Account Balance: " + this.getAccountBalance());
		}
		
	}
	
	
	/**
	 * Generate NaiFile whenever there isn't one in the folder
	 * and we have transactions to output.
	 * @throws Exception 
	 * 
	 * 
	 */
	public void outputNaiFile() throws Exception{
		
		if(this.transactions.size() == 0){
			return;
		}
		
		 File dir = new File(this.folder);

		 //Find if there is a Nai File
		 NaiFileFilter naiFilter = new NaiFileFilter();
		 File[] naiFiles = dir.listFiles(naiFilter);
		 
		 //If there isn't one then generate one
		 if(naiFiles.length==0){
			 NaiFile file = this.generateNaiFile();

			 //Write it to disk
			 Calendar now = Calendar.getInstance();
			 SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");
			 
			 File output = new File(this.folder + sdf.format(now.getTime()) + naiFilter.getExtension());
			 FileWriter fw = new FileWriter(output);
			 BufferedWriter bw = new BufferedWriter(fw);
			 bw.write(file.generateFile());
			 bw.flush();
			 bw.close();
			 this.log.info("Generated new Nai File: " + sdf.format(now.getTime()) + naiFilter.getExtension());
			 
		 }
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
				

				
				//Check for new files to process
				this.checkAndProcessNewFile();
				
				this.outputNaiFile();
				
				Thread.sleep(this.pollPeriod);

				
			} catch (InterruptedException e) {
				if(this.shutdown){
					this.log.info("Shutting down Aud Bank Simulator");
					return;
				}else{
					this.log.error("Interrupted Exception: " ,e);
				}
			} catch (Exception e) {
				this.log.error("Bank Simulator Error",e);
			}
		}
		
	}

	
	 public void checkAndProcessNewFile() throws Exception {
		
		 File dir = new File(this.folder);

		 //Now do the direct entry files
		 
		 NabDirectEntryFileFilter directEntryFilter = new NabDirectEntryFileFilter();
		 File[] directEntryFiles = dir.listFiles(directEntryFilter);
		 
		 NabDirectEntryFileParser deParser = new NabDirectEntryFileParser();
		 //Process each file
		 for(int i=0; i<directEntryFiles.length; i++){
			 
			 //Parse it in
			 try {
				deParser.parseInputFile(directEntryFiles[i]);
			} catch (Exception e) {
				this.log.error("Unable to parse file: " + directEntryFiles[i].getName(),e);
			}
			 //Delete it
			 if(!directEntryFiles[i].delete()){
				 this.log.error("Unable to delete Nab File: " + directEntryFiles[i].getName());
			 }
		 }
		 
		 //now process the files
		 for(int i=0; i<deParser.getFiles().size(); i++){
			 this.log.info("Processing File " + directEntryFiles[i].getName());
			 this.processTransactions(deParser.getFiles().get(i));
		 }

		 

		 
		 
		
	}


	/**
	  * Basic main to test functionality
	  * @param args
	  * @throws IOException
	  */
	 public static void main(String[] args) throws IOException{
	 
		 AudBankSimulator sim = new AudBankSimulator();
		 
		 System.out.println("Account Balance: " + sim.getAccountBalance());
		 NabDirectEntryFile file = new NabDirectEntryFile();
		 
		 
		 String bsbNumber = AudBankSimulator.bsbNumber;
		 String accountNumber = AudBankSimulator.accountNumber;
		 NabDirectEntryIndicator indicator = NabDirectEntryIndicator.NONE;
		 NabTransactionCode code = NabTransactionCode.DEBIT;
		 long amount = 1000000; //Amount in cents
		 String accountTitle = AudBankSimulator.accountTitle;
		 String lodgementReference = "Stuff";
		 String userBsb = "888-000";
		 String userAccountNumber = "890890";
		 String remitter = "Terry Packer";
		 long withholdingTax = 0;
		 
		 //Create a transaction
		 NabDetailRecord detail = new NabDetailRecord(bsbNumber,
					accountNumber, indicator,
					code, amount,accountTitle,
					lodgementReference, userBsb, userAccountNumber,
					remitter, withholdingTax);
		 
		 file.addDetail(detail);
		 
		 detail = new NabDetailRecord(bsbNumber,
						accountNumber, indicator,
						NabTransactionCode.DEBIT, 2000000,accountTitle,
						lodgementReference, userBsb, userAccountNumber,
						remitter, withholdingTax);
		 
		 file.addDetail(detail);
		 
		 //SEtup the total
		 long fileNetAmount = 30000000;
		 long fileCreditAmount = 0;
		 long fileDebitAmount = 30000000;
		 int record1Count = 2;
		 NabFileTotalRecord totalRecord = new NabFileTotalRecord(bsbNumber,
					fileNetAmount,fileCreditAmount,
					fileDebitAmount,record1Count);
		 file.setTotalRecord(totalRecord);
		 
		 try {
			sim.processTransactions(file);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 
		 System.out.println("Account Balance: " + sim.getAccountBalance());
		 
		 try {
			System.out.println("Nai File Output:\n" + sim.generateNaiFile().getHeader().generateRecord());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Nab Direct Entry File Output:\n" + file.generateFile());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*Start the thread */
		//sim.contextInitialized(null);
		
		//try {
		//	Thread.sleep(60000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} //for 60s
		
		//Shutdown
		//sim.contextDestroyed(null);
		
	}


	public static String getAccountNumber() {
		return AudBankSimulator.accountNumber;
	}
	
	public static String getBsbNumber(){
		return AudBankSimulator.bsbNumber;
	}

	public void startUp() {
		//TODO Check thread state before starting
		this.shutdown = false;
		this.start();
		
	}
	

}
