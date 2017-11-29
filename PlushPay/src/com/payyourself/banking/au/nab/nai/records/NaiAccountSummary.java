package com.payyourself.banking.au.nab.nai.records;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.payyourself.banking.au.nab.nai.codes.NaiAccountSummaryCode;
import com.payyourself.banking.au.nab.nai.codes.NaiTransactionCode;
import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;
import com.payyourself.currency.code.CurrencyCodeEnum;

/**
 * Type 03 Record
 * 
 * @author tpacker
 *
 */
public class NaiAccountSummary extends Bai2Record {


	private List<NaiTransactionDetail> transactions;
	
	private NaiAccountTrailer trailer;
	
	private static CurrencyCodeEnum DEFAULT_CURRENCY = CurrencyCodeEnum.AUD;
	private String accountNumber; //Your NAB Account Number
	private CurrencyCodeEnum currencyCode; //SWIFT Currency Code
	
	private List<NaiAccountSummaryDetail> detailsSection;
	
	
	/**
	 * Create file for an account
	 * @param accountNumber
	 * @param currencyCode
	 */
	public NaiAccountSummary(String accountNumber,CurrencyCodeEnum currencyCode){
		super(Bai2RecordCode.ACCOUNT_SUMMARY);
		
		this.accountNumber = accountNumber;
		this.currencyCode = currencyCode;
		
		this.transactions = new ArrayList<NaiTransactionDetail>();
		this.detailsSection = new ArrayList<NaiAccountSummaryDetail>();
	}
	
	/**
	 * Construct an account summary
	 * @param code
	 * @param accountNumber
	 * @param currencyCode
	 * @param detailsSection
	 */
	public NaiAccountSummary(String accountNumber,
			CurrencyCodeEnum currencyCode,
			List<NaiAccountSummaryDetail> detailsSection,
			List<NaiTransactionDetail> transactions) {
		super(Bai2RecordCode.ACCOUNT_SUMMARY);
		this.accountNumber = accountNumber;
		this.currencyCode = currencyCode;
		this.detailsSection = detailsSection;
		this.transactions = transactions;
	}
	
	/**
	 * Create an account summary using multiple lines
	 * first line 03 and then any additional 88 records
	 * @param lines
	 * @throws Exception 
	 */
	public NaiAccountSummary(List<String> lines) throws Exception{
		super(Bai2RecordCode.ACCOUNT_SUMMARY);
		
		//New up the transactions so we can add some later
		this.transactions = new ArrayList<NaiTransactionDetail>();
		
		//New up the details section
		this.detailsSection = new ArrayList<NaiAccountSummaryDetail>();
		
		//Get all the parts in a matrix
		String[][] fields =  new String[lines.size()][];
		String[] parts;
		//We will loop over all the lines
		for(int i=0; i<lines.size(); i++){
			
			if(!lines.get(i).endsWith("/")){
				throw new Exception("Line has bad format, didn't find / on the end: " + lines.get(i));
			}
			
			parts  = lines.get(i).split(","); //Split the line on ,
			//Manage the end of line
			if(lines.get(i).endsWith(",/")){
				//This line ends with en empty value ,,/
				//Just copy all but last part (last one is the /\n)
				fields[i] = new String[parts.length-1];
				for(int j=0; j<parts.length-1; j++){
					fields[i][j] = parts[j];
				}
			}else{
				//The line ends with a value/\n
				parts[parts.length-1] = parts[parts.length-1].replace("/","");
				fields[i] = parts;
			}
			
		}
		
		//The first code MUST be account summary
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0][0]));
		if(thisCode != Bai2RecordCode.ACCOUNT_SUMMARY){
			throw new Exception("Can't create type " + thisCode + " using an Account Summary Constructor.");
		}
		//Loop break out condition
		int lineNumber = 0; //Current line being parsed
		int linePos = 0; //Current position on line
		NaiAccountSummaryDetail deets;
		
		this.accountNumber = fields[lineNumber][1];
		
		if(fields[lineNumber][2].equals("")){
			this.currencyCode = NaiAccountSummary.DEFAULT_CURRENCY; //Default Currency 
		}else{
			this.currencyCode = CurrencyCodeEnum.valueOf(fields[lineNumber][2]);
		}
		
		linePos = 3;

		NaiAccountSummaryCode summaryCode = null;
		
		
		long amount;
		//Now just loop over the details that remain in the matrix
		while(lineNumber < fields.length){
			//Continually check to see if we need to go to next line
			if(linePos >= fields[lineNumber].length ){ 
				lineNumber++;
				if(lineNumber >= fields.length)
					break;
				linePos = 1; //Skip the 88 code
			}
			
			//Get the code
			if(fields[lineNumber][linePos].equals("")){
				summaryCode = NaiAccountSummaryCode.UNKNOWN;
				linePos++;
			}else{
				summaryCode = NaiAccountSummaryCode.parseCode(Integer.parseInt(fields[lineNumber][linePos++]));
			}
			
			//Continually check to see if we need to go to next line
			if(linePos>= fields[lineNumber].length){
				lineNumber++;
				if(lineNumber >= fields.length)
					break;

				linePos = 1; //Skip the 88 code
			}
			
			//Check to see if the value is - 
			if(fields[lineNumber][linePos].endsWith("-")){
				amount = Long.parseLong(fields[lineNumber][linePos++].replace("-", ""));
				amount = -amount;
			}else{
				amount = Long.parseLong(fields[lineNumber][linePos++]);
			}

			//Add the new details
			deets = new NaiAccountSummaryDetail(summaryCode,amount);
			this.detailsSection.add(deets);
			
			//Continually check to see if we need to go to next line
			if(linePos>= fields[lineNumber].length){
				lineNumber++;
				if(lineNumber >= fields.length)
					break;

				linePos = 1;
			}
			

		}
		
		
		
	}




	@Override
	public String generateRecord() throws Exception {
		//Add the standard 03 Items
		String out = "";
		//Current record to output
		String currentRecord = this.code.value() + "," + this.accountNumber + "," + this.currencyCode.toString();
		
		//Now output the rest and break into 88 records if required.
		for(int i=0; i<this.detailsSection.size(); i++){

			String details = this.detailsSection.get(i).generateRecord();
			if(currentRecord.length() + details.length() < 78){ //Save space for the / on the end
				currentRecord = currentRecord + "," + details; //Add the next record" +
						
			}else{
				//Need to start an 88 record
				out = out + currentRecord + "/\n"; //MOve current record to output and add EOL
				//create a new current record of type 88
				currentRecord = Bai2RecordCode.CONTINUATION_RECORD.value() + "," + details;
				
			}

		}
		
		out = out + currentRecord; //Add the last record
		
		//Now just check to make sure the string ends in a /
		if(!out.endsWith("/\n")){
			out = out + "/\n";
		}
		
		//Add the Transactions
		for(int i=0; i<this.transactions.size(); i++){
			out = out + this.transactions.get(i).generateRecord();
		}
		
		//Add the trailer
		if(this.trailer != null)
			out = out + this.trailer.generateRecord();
		
		return out;
	}




	public String getAccountNumber() {
		return accountNumber;
	}




	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}




	public CurrencyCodeEnum getCurrencyCode() {
		return currencyCode;
	}




	public void setCurrencyCode(CurrencyCodeEnum currencyCode) {
		this.currencyCode = currencyCode;
	}




	public List<NaiAccountSummaryDetail> getDetailsSection() {
		return detailsSection;
	}




	public void setDetailsSection(List<NaiAccountSummaryDetail> detailsSection) {
		this.detailsSection = detailsSection;
	}

	public void setTransactions(List<NaiTransactionDetail> transactions) {
		this.transactions = transactions;
	}

	public List<NaiTransactionDetail> getTransactions() {
		return transactions;
	}

	public void setTrailer(NaiAccountTrailer trailer) {
		this.trailer = trailer;
	}

	public NaiAccountTrailer getTrailer() {
		return trailer;
	}

	public void addTransaction(NaiTransactionDetail transactionDetail) {
		this.transactions.add(transactionDetail);
	}

	public void addSummaryDetail(NaiAccountSummaryDetail detail) {
		this.detailsSection.add(detail);
		
	}

	public Collection<? extends NaiTransactionDetail> getCredits(
			String accountNumber) {
		
		List<NaiTransactionDetail> details = new ArrayList<NaiTransactionDetail>();
		if(this.accountNumber.equals(accountNumber)){
			//Cycle through and get only the Credits
			for(int i=0; i<this.transactions.size(); i++){
				if(this.transactions.get(i).getTransactionCode().equals(NaiTransactionCode.TRANSFER_CREDIT)){
					details.add(this.transactions.get(i));
				}
			} //end for
		}//end if
		return details;

	}

}
