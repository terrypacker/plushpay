package com.payyourself.banking.bai2.records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.payyourself.banking.bai2.codes.Bai2AccountCode;
import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.currency.code.CurrencyCodeEnum;


public class Bai2AccountSummary extends Bai2Record {

	//Default for decoding records
	private static CurrencyCodeEnum DEFAULT_CURRENCY = CurrencyCodeEnum.USD;
	
	private String customerAccountNumber; //Up to 35 alpha numeric chars no , or / allowed (may have leading 0's)
	private CurrencyCodeEnum currencyCode; //ISO Code
	
	//Fields 4-7 are repeated for each code and as such are broken out into a new class
	//These fields are to be sorted in SummaryCode order
	private List<Bai2AccountSummaryDetails> detailsSection;
	
	
	
	
	/**
	 * Constructor from fields
	 * 
	 * @param customerAccountNumber
	 * @param currencyCode
	 * @param detailsSection
	 * @throws Exception 
	 */
	public Bai2AccountSummary(String customerAccountNumber, CurrencyCodeEnum currencyCode,
			List<Bai2AccountSummaryDetails> detailsSection) throws Exception {
		super(Bai2RecordCode.ACCOUNT_SUMMARY);
		
		if(customerAccountNumber.length() > 35){
			throw new Exception("Customer Account Number too long: " + customerAccountNumber.length() + " max 35.");
		}
		
		this.customerAccountNumber = customerAccountNumber;
		this.currencyCode = currencyCode;
		//Order them correctly 
		Collections.sort(detailsSection);
		this.detailsSection = detailsSection;
	}

	
	/**
	 * Construct a record from a line of text,
	 * if this summary has 88 codes after it, they should be 
	 * added to the input to this constructor.
	 * 
	 * CHECK EDITS TO NAI INterface for bugfixes.
	 * 
	 * @param line
	 * @throws Exception 
	 */
	public Bai2AccountSummary(List<String> lines) throws Exception{
		super(Bai2RecordCode.ACCOUNT_SUMMARY);

		
		
		//New up the details section
		this.detailsSection = new ArrayList<Bai2AccountSummaryDetails>();
		
		//Get all the parts in a matrix
		String[][] fields =  new String[lines.size()][];
		//We will loop over all the lines
		for(int i=0; i<lines.size(); i++){
			
			if(!lines.get(i).endsWith("/\n")){
				throw new Exception("Line has bad format, didn't find / on the end: " + lines.get(i));
			}
			////Chop off the ,/ at the end because it can lead to issues if the line ends with ,/ which does not indicate a value
			//if(lines.get(i).endsWith(",/\n")){
			//	lines.set(i, lines.get(i).replace("/", ""));
			//}
			//What about lines that end in something other than ,/ ???
			
			fields[i]  = lines.get(i).split(","); //Split the line on ,
			
		}
		
		//The first code MUST be account summary
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0][0]));
		if(thisCode != Bai2RecordCode.ACCOUNT_SUMMARY){
			throw new Exception("Can't create type " + thisCode + " using an Account Summary Constructor.");
		}
		//Loop break out condition
		int lineNumber = 0; //Current line being parsed
		int linePos = 0; //Current position on line
		Bai2AccountSummaryDetails deets;
		
		this.customerAccountNumber = fields[lineNumber][1];
		if(fields[lineNumber][2] == ""){
			this.currencyCode = Bai2AccountSummary.DEFAULT_CURRENCY; //Default Currency 
		}else{
			this.currencyCode = CurrencyCodeEnum.valueOf(fields[lineNumber][2]);
		}
		
		linePos = 3;

		//Now just loop over the details that remain in the matrix
		while(lineNumber < fields.length){
			//Continually check to see if we need to go to next line
			if(linePos >= fields[lineNumber].length -1){ //Don't process the last entry as it is the /field
				lineNumber++;
				if(lineNumber >= fields.length)
					break;
				linePos = 1; //Skip the 88 code
			}
			
			Bai2AccountCode statusCode = new Bai2AccountCode(Integer.parseInt(fields[lineNumber][linePos++]));
			//Continually check to see if we need to go to next line
			if(linePos>= fields[lineNumber].length-1){
				lineNumber++;
				if(lineNumber >= fields.length)
					break;

				linePos = 1; //Skip the 88 code
			}
			
			String amount = fields[lineNumber][linePos++];
			//Continually check to see if we need to go to next line
			if(linePos>= fields[lineNumber].length-1){
				lineNumber++;
				if(lineNumber >= fields.length)
					break;

				linePos = 1;
			}
			
			String itemCount = fields[lineNumber][linePos++];
			//Continually check to see if we need to go to next line
			if(linePos>= fields[lineNumber].length-1){
				lineNumber++;
				if(lineNumber >= fields.length)
					break;

				linePos = 1;
			}
			String fundsType = fields[lineNumber][linePos++];
			
			deets = new Bai2AccountSummaryDetails(statusCode,
					amount,itemCount,fundsType);
			this.detailsSection.add(deets);
		}
		

			
	}
	
	



	@Override
	public String generateRecord() throws Exception {
		
		//Add the standard 03 Items
		String out = "";
		//Current record to output
		String currentRecord = this.code.value() + "," + this.customerAccountNumber + "," + this.currencyCode.toString() + ",";
		
		//Now output the rest and break into 88 records if required.
		for(int i=0; i<this.detailsSection.size(); i++){
			
			String details = this.detailsSection.get(i).generateRecord();
			if(currentRecord.length() + details.length() < 78){ //Save space for the / on the end
				currentRecord = currentRecord + details; //Add the next record
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
		
		return out;
		
	}


	public String getCustomerAccountNumber() {
		return customerAccountNumber;
	}


	public void setCustomerAccountNumber(String customerAccountNumber) {
		this.customerAccountNumber = customerAccountNumber;
	}


	public CurrencyCodeEnum getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(CurrencyCodeEnum currencyCode) {
		this.currencyCode = currencyCode;
	}


	public List<Bai2AccountSummaryDetails> getDetailsSection() {
		return detailsSection;
	}


	public void setDetailsSection(List<Bai2AccountSummaryDetails> detailsSection) {
		this.detailsSection = detailsSection;
	}

}
