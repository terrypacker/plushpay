package com.payyourself.banking.bai2.records;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.currency.code.CurrencyCodeEnum;

public class Bai2GroupHeader extends Bai2Record {

	private String ultimateReciever; //Same as RecieverID from record 1
	private String originatorId; //ABA or SWIFT BIC associated with all accounts until a 98 record
	private String groupStatus; //1=Update (not sure about this one)
	private Calendar asOfDate; //YYMMDD the as of date of the transactions 
	//As of time //0001 to 2400 (Easter Time US)
	private CurrencyCodeEnum currencyCode; // blank these are always on the Account Record 03
	private String asOfDateModifier; //2 previous day data, 3 Interim same day data
	
	
	
	/**
	 * Construct using fields
	 * 
	 * @param ultimateReciever
	 * @param originatorId
	 * @param groupStatus
	 * @param asOfDate
	 * @param code
	 * @param asOfDateModifier
	 * @throws Exception
	 */
	public Bai2GroupHeader(String ultimateReciever,
			String originatorId, String groupStatus, Calendar asOfDate,
			CurrencyCodeEnum currencyCode,
			String asOfDateModifier) throws Exception {
		super(Bai2RecordCode.GROUP_HEADER);
		
		//Check for invalid chars
		if((ultimateReciever.contains("/"))||(ultimateReciever.contains(","))){
			throw new Exception("Invalid Character , or / in: " + ultimateReciever);
		}		
		this.ultimateReciever = ultimateReciever;
		//Check for invalid chars
		if((originatorId.contains("/"))||(ultimateReciever.contains(","))){
			throw new Exception("Invalid Character , or / in: " + originatorId);
		}
		this.originatorId = originatorId;
		
		this.groupStatus = groupStatus;
		this.asOfDate = asOfDate;
		this.currencyCode = currencyCode;
		this.asOfDateModifier = asOfDateModifier;
	}


	/**
	 * Construct using Input from a line
	 * @param line
	 * @throws Exception 
	 */
	public Bai2GroupHeader(String line) throws Exception{
		super(Bai2RecordCode.GROUP_HEADER);
		
		String[] fields = line.split(","); //Split the line on ,
		if(fields.length != 8){
			throw new Exception("Unable to parse line, " + fields.length + " parts found, expected 8");
		}

		//Setup the date formatter
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");

		//code is fields[0] but we already know that
		this.ultimateReciever = fields[1]; 
		this.originatorId= fields[2];
		
		this.groupStatus = fields[3];
		
		
		this.asOfDate = Calendar.getInstance();
		this.asOfDate.setTime(sdf.parse(fields[4] + fields[5]));
		
		if(fields[6] == ""){
			this.currencyCode = null;
		}else{
			this.currencyCode = CurrencyCodeEnum.valueOf(fields[6]);
		}
		
		if(!fields[7].contains("/")){
			throw new Exception("Line has bad format, didn't find / on the end: " + line);
		}
		
		String[] lastFieldParts = fields[7].split("/");
		
		this.asOfDateModifier = lastFieldParts[0];
		
	}


	@Override
	public String generateRecord() throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String date = sdf.format(this.asOfDate.getTime());
		
		sdf = new SimpleDateFormat("HHmm");
		String time = sdf.format(this.asOfDate.getTime());

		String code;
		if(this.currencyCode == null){
			code = "";
		}else{
			code = this.currencyCode.toString();
		}
		
		String out = this.code.value()+ "," + this.ultimateReciever + "," + this.originatorId + "," + this.groupStatus 
		 	+ "," + date  + "," + time  + "," + code   + "," + this.asOfDateModifier + "/\n";
		
		if(out.length() > 80){
			throw new Exception("Record > 80 chars, type 88 not applicable: " + out);
		}
		
		return out;
		
	}


	public String getUltimateReciever() {
		return ultimateReciever;
	}


	public void setUltimateReciever(String ultimateReciever) {
		this.ultimateReciever = ultimateReciever;
	}


	public String getOriginatorId() {
		return originatorId;
	}


	public void setOriginatorId(String originatorId) {
		this.originatorId = originatorId;
	}


	public String getGroupStatus() {
		return groupStatus;
	}


	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}


	public Calendar getAsOfDate() {
		return asOfDate;
	}


	public void setAsOfDate(Calendar asOfDate) {
		this.asOfDate = asOfDate;
	}


	public CurrencyCodeEnum getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(CurrencyCodeEnum currencyCode) {
		this.currencyCode = currencyCode;
	}


	public String getAsOfDateModifier() {
		return asOfDateModifier;
	}


	public void setAsOfDateModifier(String asOfDateModifier) {
		this.asOfDateModifier = asOfDateModifier;
	}

}
