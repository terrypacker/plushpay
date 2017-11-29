package com.payyourself.banking.au.nab.nai.records;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

public class NaiGroupHeader extends Bai2Record {

	
	private List<NaiAccountSummary> accounts;
	private NaiGroupTrailer trailer;
	
	private String ultimateReciever; //Same as RecieverID from record 1
	private String originatorId; //Bank ID (8 Digit alphanumeric)
	private String groupStatus; //Always a 1 in NAI
	private String asOfDate; //yyMMdd the as of date of the transactions 
	private String asOfTime; //HHmm 0001 to 2400 (Easter Time US)

	
	
	/**
	 * 
	 * @param ultimateReciever
	 * @param originatorId
	 * @param asOfDate
	 * @param asOfTime
	 */
	public NaiGroupHeader(String ultimateReciever,
			String originatorId, String asOfDate, String asOfTime) {
		super(Bai2RecordCode.GROUP_HEADER);
		this.ultimateReciever = ultimateReciever;
		this.originatorId = originatorId;
		this.groupStatus = "1"; //Always
		this.asOfDate = asOfDate;
		this.asOfTime = asOfTime;
		
		this.accounts = new ArrayList<NaiAccountSummary>();
		
	}

	
	/**
	 * 
	 * @param ultimateReciever
	 * @param originatorId
	 * @param asOfDate
	 */
	public NaiGroupHeader(String ultimateReciever, String originatorId,
			Calendar asOfDate) {
		super(Bai2RecordCode.GROUP_HEADER);
		this.ultimateReciever = ultimateReciever;
		this.originatorId = originatorId;
		this.groupStatus = "1";

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		this.asOfDate = sdf.format(asOfDate.getTime());
		
		sdf = new SimpleDateFormat("HHmm");
		this.asOfTime = sdf.format(asOfDate.getTime());
		this.accounts = new ArrayList<NaiAccountSummary>();

	
	}
	
	/**
	 * Create a record from a line.
	 * @param line
	 * @throws Exception 
	 */
	public NaiGroupHeader(String line) throws Exception{
		super(Bai2RecordCode.GROUP_HEADER);
		
		this.accounts = new ArrayList<NaiAccountSummary>();
		
		if(!line.endsWith("/")){
			throw new Exception("Line has bad format, didn't find / on the end: " + line);
		}

		String[] parts = line.split(","); //Split the line on ,
		String[] fields;
		if(line.endsWith(",/")){
			//This line ends with en empty value ,,/
			//Just copy all but last part (last one is the /\n)
			fields = new String[parts.length-1];
			for(int i=0; i<parts.length-1; i++){
				fields[i] = parts[i];
			}
		}else{
			//The line ends with a value/\n
			parts[parts.length-1] = parts[parts.length-1].replace("/","");
			fields = parts;
		}
		
		if(fields.length != 6){
			throw new Exception("Unable to parse line, " + fields.length + " parts found, expected 8");
		}
		
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
		if(thisCode != Bai2RecordCode.GROUP_HEADER){
			throw new Exception("Can't create type " + thisCode + " using Group Header Constructor.");
		}
		
		this.ultimateReciever = fields[1];
		this.originatorId = fields[2];
		this.groupStatus = fields[3];
		this.asOfDate = fields[4];
		this.asOfTime = fields[5];
		
	}
	
	
	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + 
			this.ultimateReciever + "," + 
			this.originatorId + "," + 
			this.groupStatus + "," +
			this.asOfDate + ",";
		
		//Determine Termination
		if(this.asOfTime.equals("")){
			out = out + ",/\n";
		}else{
			out = out + this.asOfTime + "/\n";
		}
		if(out.length() > 80){
			throw new Exception("Record too long, 88 Type not applicable: " + out);
		}
		
		//Add accounts if there are any
		for(int i=0; i<this.accounts.size(); i++){
			out = out + this.accounts.get(i).generateRecord();
		}
		
		//Add Trailer
		if(this.trailer != null)
			out = out + this.trailer.generateRecord();
		
		return out;
	}
	
	public void setAsOfDate(Calendar date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		this.asOfDate = sdf.format(date.getTime());
		

	}
	
	public void setAsOfTime(Calendar time){
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		this.asOfTime = sdf.format(time.getTime());
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



	public String getAsOfDate() {
		return asOfDate;
	}



	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}



	public String getAsOfTime() {
		return asOfTime;
	}



	public void setAsOfTime(String asOfTime) {
		this.asOfTime = asOfTime;
	}


	public void setAccounts(List<NaiAccountSummary> accounts) {
		this.accounts = accounts;
	}


	public List<NaiAccountSummary> getAccounts() {
		return accounts;
	}
	
	public void addAccount(NaiAccountSummary account){
		this.accounts.add(account);
	}


	public void setTrailer(NaiGroupTrailer trailer) {
		this.trailer = trailer;
	}


	public NaiGroupTrailer getTrailer() {
		return trailer;
	}


	public Collection<? extends NaiTransactionDetail> getCredits(
			String accountNumber) {
		List<NaiTransactionDetail> details = new ArrayList<NaiTransactionDetail>();
		
		for(int i=0; i<this.accounts.size(); i++){
			details.addAll(this.accounts.get(i).getCredits(accountNumber));
		}
		
		return details;
		
	}

}
