package com.payyourself.banking.bai2.records;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.payyourself.banking.FundsType;
import com.payyourself.banking.bai2.codes.Bai2AccountCode;
import com.payyourself.banking.bai2.codes.Bai2RecordCode;

/**
 * Bai2 Transaction Details
 * 
 * Only one detailed transaction may be reported by each Type 16 record. Record 16 
 *	cannot report status or summary amounts. Status and summary are reported in 
 *	record 03. 
 *	Transaction detail records report activity in accounts identified by 03 records. All type 
 *	16 records following an 03 record refer to the account identified in the Customer 
 *	Account Number field. For more information, see Ò03 Ð Account Identifier and 
 *	Summary Status,Ó above. 
 *	The transactions are to be sort ascending by BAI Type Code (field 2), then in 
 *	descending order by Amount (field 3). 
 *	If the transaction is stored in the database as having 3+ day float, the total of the 2 
 *	day and 3+ day float fields are added together to arrive at the 2+ day float. This 
 *	applies to Funds Type S. 
 *	The end of the text field is indicated by the beginning of the next record (unless it is a 
 *	type 88 continuation record). If the type 16 record does not include text, the end of 
 *	the logical record is indicated by the adjacent delimiters (,/) following the Customer 
 *	Reference Number field.
 * @author tpacker
 *
 */
public class Bai2TransactionDetail extends Bai2Record {

	private Bai2AccountCode typeCode; //
	private String amount; //Dollar amount of transaction
	private FundsType type;
	private String bankReferenceNumber;
	private String customerReferenceNumber;
	private List<String> text; //The text of the transaction broken up into 88 record payloads for simplicity
	
	//Optional Fields depening on FundsType
	private String immediateAmount; //Type S
	private String oneDayAmount; //Type S
	private String twoDayAmount; //Type S
	private Calendar valueDate; //Date the funds are available (type V)
	

	/**
	 * Construct a type S detials
	 * @param typeCode
	 * @param amount
	 * @param type
	 * @param immediateAmount
	 * @param oneDayAmount
	 * @param twoDayAmount
	 * @param bankReferenceNumber
	 * @param customerReferenceNumber
	 * @param text
	 * @throws Exception 
	 */
	public Bai2TransactionDetail(
			Bai2AccountCode typeCode, String amount, FundsType type,
			String immediateAmount, String oneDayAmount, String twoDayAmount,
			String bankReferenceNumber, String customerReferenceNumber,
			String text) throws Exception {
		super(Bai2RecordCode.TRANSACTION_DETAIL);
		
		switch(type){
		case DISTRIBUTED:
			break;
		default:
			throw new Exception("Invalid constructor for Funds Type Details: " + type);
		}
		this.typeCode = typeCode;
		this.amount = amount;
		this.type = type;
		this.immediateAmount = immediateAmount;
		this.oneDayAmount = oneDayAmount;
		this.twoDayAmount = twoDayAmount;
		this.bankReferenceNumber = bankReferenceNumber;
		this.customerReferenceNumber = customerReferenceNumber;
		this.text = this.parseText(text);

	}

	/**
	 * Construct a type V details
	 * @param typeCode
	 * @param amount
	 * @param type
	 * @param valueDate
	 * @param bankReferenceNumber
	 * @param customerReferenceNumber
	 * @param text
	 * @throws Exception 
	 */
	public Bai2TransactionDetail(
			Bai2AccountCode typeCode, String amount, FundsType type,
			Calendar valueDate,
			String bankReferenceNumber, String customerReferenceNumber,
			String text) throws Exception {
		super(Bai2RecordCode.TRANSACTION_DETAIL);
		
		switch(type){
		case VALUE:
			break;
		default:
			throw new Exception("Invalid constructor for Funds Type Details: " + type);
		}
		
		this.typeCode = typeCode;
		this.amount = amount;
		this.type = type;
		this.valueDate = valueDate;
		this.bankReferenceNumber = bankReferenceNumber;
		this.customerReferenceNumber = customerReferenceNumber;
		this.text = this.parseText(text);

	}
	
	
	/**
	 * Construct any details except S or V
	 * @param typeCode
	 * @param amount
	 * @param type
	 * @param bankReferenceNumber
	 * @param customerReferenceNumber
	 * @param text
	 * @throws Exception 
	 */
	public Bai2TransactionDetail(
			Bai2AccountCode typeCode, String amount, FundsType type,
			String bankReferenceNumber, String customerReferenceNumber,
			String text) throws Exception {
		super(Bai2RecordCode.TRANSACTION_DETAIL);
		
		switch(type){
			case VALUE:
			case DISTRIBUTED:
				throw new Exception("Invalid constructor for Funds Type Details: " + type);
		}
		
		this.typeCode = typeCode;
		this.amount = amount;
		this.type = type;
		this.bankReferenceNumber = bankReferenceNumber;
		this.customerReferenceNumber = customerReferenceNumber;
		this.text = this.parseText(text);

	}


	/**
	 * Parse the text into 80 char or less lines
	 * @param text2
	 * @return
	 */
	private List<String> parseText(String text2) {
		
		List<String> lines = new ArrayList<String>();
		
		if(text2.equals("")){
			return lines;
		}
		
		//Compute how to break up this line
		if(text2.length() > 80){
			String part = "";
			int partSize = 0;
			//We need to break it up
			for(int i=0; i<text2.length(); i++){
				
				part = part + text2.charAt(i);
				partSize++;
				
				//Add lines of size 75
				if(partSize == 75){
					lines.add(part);
					part = "";
					partSize = 0;
				}
			}
			
			//Now add the last one (if it exists)
			if(!part.equals("")){
				lines.add(part);
			}
			
		}else{
			lines.add(text2); //Just add it as one line
		}
		
		return lines;
		
	}


	/**
	 * Construct a record from a text input
	 * include any 88 records on additional lines
	 * @param lines
	 * @throws Exception
	 */
	public Bai2TransactionDetail(List<String> lines) throws Exception{
		super(Bai2RecordCode.TRANSACTION_DETAIL);

		//We will loop over all the lines to check correctness ,

		if(!lines.get(0).endsWith("/\n")){
			throw new Exception("Line has bad format, didn't find / on the end: " + lines.get(0));
		}
		
		if(lines.get(0).endsWith(",/\n")){
			//There is no text with this (no 88 record)
		}else{
			//There is 88 text but we need to remove the / on the end first
			lines.set(0, lines.get(0).replace("/\n",""));
		}
		
		String[] fields = lines.get(0).split(","); //Split the first line on ,
		
		//Loop break out condition
		int linePos = 0; //Current position on line		

		//The first code MUST be account summary
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[linePos++]));
		if(thisCode != Bai2RecordCode.TRANSACTION_DETAIL){
			throw new Exception("Can't create type " + thisCode + " using an Account Transaction Detail Constructor.");
		}


		
		this.typeCode = new Bai2AccountCode(Integer.parseInt(fields[linePos++]));
		this.amount = fields[linePos++];
		this.type = FundsType.parseCode(fields[linePos++]);
		
		switch(this.type){
		case IMMEDIATE:
		case ONE_DAY:
		case TWO_DAY:
		case UNKNOWN:
			break;
		case DISTRIBUTED:
			//The next 3 fields are Immediate avail amnt, One day avail amnt, Two Day+ avail amnt
			//Amount value is Signed (Distributed ONLY)
			this.immediateAmount = fields[linePos++];
			this.oneDayAmount = fields[linePos++];
			this.twoDayAmount = fields[linePos++];
			break;
		case VALUE:
			//The next 2 fields are value date YYMMDD and value time 2400 referring to originator's business day/time zone
			//Setup the date formatter
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
			this.valueDate = Calendar.getInstance();
			this.valueDate.setTime(sdf.parse(fields[linePos++] + fields[linePos++]));
			break;
		}
		
		this.bankReferenceNumber = fields[linePos++];
		
		this.customerReferenceNumber = fields[linePos++];
		
		
		this.text = new ArrayList<String>();
		
		//Now get the text if there is any (It will be on the second line and any additional lines.
		for(int i=1; i<lines.size(); i++){
			
			//Make sure the format is correct
			if(lines.get(i).startsWith("88,")){
				lines.set(i, lines.get(i).replace("88,","")); //Chop off the leading record matcher
			}else{
				throw new Exception("One of the additional input lines is not an 88 record: " + lines.get(i));
			}
			
			this.text.add(lines.get(i));
			
		}
		
		
	}



	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.typeCode.value() + "," + this.amount + ","  + this.type.value() + ",";
		
		//Format different depeding on type
		switch(this.type){
		case IMMEDIATE:
		case ONE_DAY:
		case TWO_DAY:
		case UNKNOWN:
			break;
		case DISTRIBUTED:
			//The next 3 fields are Immediate avail amnt, One day avail amnt, Two Day+ avail amnt
			//Amount value is Signed (Distributed ONLY)
			out = out + this.immediateAmount + "," + this.oneDayAmount + "," + this.twoDayAmount + ",";
			break;
		case VALUE:
			//The next 2 fields are value date YYMMDD and value time 2400 referring to originator's business day/time zone
			//Setup the date formatter
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			out = out + sdf.format(this.valueDate.getTime()) + ",";
			sdf = new SimpleDateFormat("HHmm");
			out = out + sdf.format(this.valueDate.getTime()) + ",";
			break;
		}
		
		
		out = out + this.bankReferenceNumber + "," + this.customerReferenceNumber;
		
		//Now determine if there is TEXT to go with it
		if(this.text.size() > 0){
			out = out + "/\n";
			String record;
			for(int i=0; i<this.text.size(); i++){
				
				record = "88," + this.text.get(i) + "\n";
				out = out + record;
				
			}
			
			
			
			
		}else{
			//No text
			out = out +  "," + "/\n";

		}
		
		return out;
		
	}





	public Bai2AccountCode getTypeCode() {
		return typeCode;
	}





	public void setAccountCode(Bai2AccountCode accountCode) {
		this.typeCode = accountCode;
	}





	public String getAmount() {
		return amount;
	}





	public void setAmount(String amount) {
		this.amount = amount;
	}





	public FundsType getType() {
		return type;
	}





	public void setType(FundsType type) {
		this.type = type;
	}





	public String getBankReferenceNumber() {
		return bankReferenceNumber;
	}





	public void setBankReferenceNumber(String bankReferenceNumber) {
		this.bankReferenceNumber = bankReferenceNumber;
	}





	public String getCustomerReferenceNumber() {
		return customerReferenceNumber;
	}





	public void setCustomerReferenceNumber(String customerReferenceNumber) {
		this.customerReferenceNumber = customerReferenceNumber;
	}





	public List<String> getText() {
		return text;
	}





	/**
	 * Set the list of 88 record payload strings for this record
	 * @param text
	 */
	public void setText(List<String> text) {
		this.text = text;
	}

	public String getImmediateAmount() {
		return immediateAmount;
	}

	public void setImmediateAmount(String immediateAmount) {
		this.immediateAmount = immediateAmount;
	}

	public String getOneDayAmount() {
		return oneDayAmount;
	}

	public void setOneDayAmount(String oneDayAmount) {
		this.oneDayAmount = oneDayAmount;
	}

	public String getTwoDayAmount() {
		return twoDayAmount;
	}

	public void setTwoDayAmount(String twoDayAmount) {
		this.twoDayAmount = twoDayAmount;
	}

	public Calendar getValueDate() {
		return valueDate;
	}

	public void setValueDate(Calendar valueDate) {
		this.valueDate = valueDate;
	}

	public void setTypeCode(Bai2AccountCode typeCode) {
		this.typeCode = typeCode;
	}

}
