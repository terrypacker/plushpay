package com.payyourself.banking.au.nab.directEntry.records;

import com.payyourself.banking.au.nab.directEntry.codes.NabDirectEntryIndicator;
import com.payyourself.banking.au.nab.directEntry.codes.NabTransactionCode;

public class NabDetailRecord extends NabDirectEntryRecord{

	private String bsbNumber; //numberic with hypehn at pos 5 xxx-xxx 7 long
	private String accountNumber; //Account number to be credited/debited
	private NabDirectEntryIndicator indicator; //1 N,T,W,X or Y
	private NabTransactionCode code; // 2
	private long amount; //right just 0 filled in cents length 10
	private String accountTitle; //Left Justified blank filled len 32 (Surname. GivenName1 GivenName 2)
	private String lodgementReference; // blank filled statement narrative at NAB 18
	private String userBsb; //xxx-xxx 
	private String userAccountNumber; //right justified blank filled (remove any hyphens if size exceeds 9) 9
	private String remitter; //statement narrative left justified blank filled) 16
	private long withholdingTax; //Numeric only right just zero filled 8
	
	
	
	/**
	 * Should check the size of each param and throw exception if 
	 * any are too long
	 * 
	 * @param bsbNumber
	 * @param accountNumber
	 * @param indicator
	 * @param code
	 * @param amount
	 * @param accountTitle
	 * @param lodgementReference
	 * @param userBsb
	 * @param userAccountNumber
	 * @param remitter
	 * @param withholdingTax
	 */
	public NabDetailRecord(String bsbNumber,
			String accountNumber, NabDirectEntryIndicator indicator,
			NabTransactionCode code, long amount, String accountTitle,
			String lodgementReference, String userBsb, String userAccountNumber,
			String remitter, long withholdingTax) {
		super(NabDirectEntryRecordType.DETAIL);
		this.bsbNumber = bsbNumber;
		this.accountNumber = accountNumber;
		this.indicator = indicator;
		this.code = code;
		this.amount = amount;
		
		this.accountTitle = accountTitle;
		this.lodgementReference = lodgementReference;
		this.userBsb = userBsb;
		this.userAccountNumber = userAccountNumber;
		this.remitter = remitter;
		this.withholdingTax = withholdingTax;
		
		
	}


	public NabDetailRecord(String line) throws Exception{
		super(NabDirectEntryRecordType.DETAIL);
		
		this.bsbNumber = line.substring(1,8).trim();
		this.accountNumber = line.substring(8,17).trim();
		this.indicator = NabDirectEntryIndicator.parse(line.substring(17,18)); //Don't trim this it can be a space
		
		this.code = NabTransactionCode.parse(Integer.parseInt(line.substring(18,20).trim()));
		
		this.setAmount(line.substring(20,30).trim());
		
		this.accountTitle = line.substring(30,62).trim();
		
		this.lodgementReference = line.substring(62,80).trim();
		
		this.userBsb = line.substring(80,87).trim();
		
		this.userAccountNumber = line.substring(87,96).trim();
		
		this.remitter = line.substring(96,112).trim();
		
		this.setWithholdingTax(line.substring(112,120).trim());
		
	}

	@Override
	public String getRecord() throws Exception {
		
		//SHould check max lengths of all members before writing out.
		
		String out = this.recordType.value();
		out = out + this.bsbNumber;
		out = out +  this.createFixedLengthString(this.accountNumber,9, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.BLANK_FILL);
		out = out + this.indicator.value();
		out = out + this.code.value();
		out = out +  this.createFixedLengthString(Long.toString(this.amount),10, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);
		
		out = out +  this.createFixedLengthString(this.accountTitle,32, DirectEntryFormat.LEFT_JUSTIFIED, DirectEntryFormat.BLANK_FILL);

		out = out +  this.createFixedLengthString(this.lodgementReference,18, DirectEntryFormat.LEFT_JUSTIFIED, DirectEntryFormat.BLANK_FILL);

		out = out +  this.userBsb;
		out = out +  this.createFixedLengthString(this.userAccountNumber,9, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.BLANK_FILL);

		out = out +  this.createFixedLengthString(this.remitter,16, DirectEntryFormat.LEFT_JUSTIFIED, DirectEntryFormat.BLANK_FILL);

		out = out +  this.createFixedLengthString(Long.toString(this.withholdingTax),8, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);

		
		return out;

		
	}


	public String getBsbNumber() {
		return bsbNumber;
	}


	public void setBsbNumber(String bsbNumber) {
		this.bsbNumber = bsbNumber;
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public NabDirectEntryIndicator getIndicator() {
		return indicator;
	}


	public void setIndicator(NabDirectEntryIndicator indicator) {
		this.indicator = indicator;
	}


	public NabTransactionCode getCode() {
		return code;
	}


	public void setCode(NabTransactionCode code) {
		this.code = code;
	}


	public void setAmount(String amount) {
		this.amount = Long.parseLong(amount);
	}

	public void setAmount(long amount){
		this.amount = amount;
	}

	public long getAmount(){
		return this.amount;
	}
	
	public String getAccountTitle() {
		return accountTitle;
	}


	public void setAccountTitle(String accountTitle) {
		this.accountTitle = accountTitle;
	}


	public String getLodgementReference() {
		return lodgementReference;
	}


	public void setLodgementReference(String lodgementReference) {
		this.lodgementReference = lodgementReference;
	}


	public String getUserBsb() {
		return userBsb;
	}


	public void setUserBsb(String userBsb) {
		this.userBsb = userBsb;
	}


	public String getUserAccountNumber() {
		return userAccountNumber;
	}


	public void setUserAccountNumber(String userAccountNumber) {
		this.userAccountNumber = userAccountNumber;
	}


	public String getRemitter() {
		return remitter;
	}


	public void setRemitter(String remitter) {
		this.remitter = remitter;
	}


	public long getWithholdingTax() {
		return withholdingTax;
	}


	public void setWithholdingTax(String withholdingTax) {
		this.withholdingTax = Long.parseLong(withholdingTax);
	}
	
	public void setWithholdingTax(long withholdingTax) {
		this.withholdingTax = withholdingTax;
	}
	
	
	
}
