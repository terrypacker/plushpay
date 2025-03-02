package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;

public class Bai2AccountTrailer extends Bai2Record {

	private String accountControlTotal; //Sum of all Amount feilds in the preceding type 03 record
	private String numberOfRecords; //Number of records for account include 03,16,88 and this 49 record
	
	public Bai2AccountTrailer(String accountControlTotal, String numberOfRecords){
		super(Bai2RecordCode.ACCOUNT_TRAILER);
		
		this.accountControlTotal = accountControlTotal;
		this.numberOfRecords = numberOfRecords;
		
		
	}
	
	public Bai2AccountTrailer(String line) throws Exception{
		super(Bai2RecordCode.ACCOUNT_TRAILER);

		line = line.replace("/\n", ""); //Remove the /
		String[] fields = line.split(",");
		
		if(Bai2RecordCode.parseCode(Integer.parseInt(fields[0])) != Bai2RecordCode.ACCOUNT_TRAILER){
			throw new Exception("Invalid record type: " + fields[0] + " for Account Trailer Constructor.");
		}
		
		this.accountControlTotal = fields[1];
		this.numberOfRecords = fields[2];
		
	}
	
	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.accountControlTotal + "," + this.numberOfRecords + "/\n";
		return out;
	}

	public String getAccountControlTotal() {
		return accountControlTotal;
	}

	public void setAccountControlTotal(String accountControlTotal) {
		this.accountControlTotal = accountControlTotal;
	}

	public String getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
