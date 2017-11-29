package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;

public class Bai2GroupTrailer extends Bai2Record {

	private String groupControlTotal; //Sum of all account control totals in this group
	private String numberOfAccounts; //Number of 03 records
	private String numberOfRecords; //Total number of all records in this group, inlcude 02,03,16,88,49 and this 98
	
	
	
	/**
	 * 
	 * @param groupControlTotal
	 * @param numberOfAccounts
	 * @param numberOfRecords
	 */
	public Bai2GroupTrailer(String groupControlTotal,
			String numberOfAccounts, String numberOfRecords) {
		super(Bai2RecordCode.GROUP_TRAILER);
		this.groupControlTotal = groupControlTotal;
		this.numberOfAccounts = numberOfAccounts;
		this.numberOfRecords = numberOfRecords;
	}

	/**
	 * Construct the record from a line
	 * @param line
	 * @throws Exception 
	 */
	public Bai2GroupTrailer(String line) throws Exception{
		super(Bai2RecordCode.GROUP_TRAILER);
		
		line = line.replace("/\n", ""); //Chop off end
		String fields[] = line.split(",");
		
		if(Bai2RecordCode.parseCode(Integer.parseInt(fields[0])) != Bai2RecordCode.GROUP_TRAILER){
			throw new Exception("Invalid record type: " + fields[0] + " for Group Trailer Constructor.");
		}
		
		this.groupControlTotal = fields[1];
		this.numberOfAccounts = fields[2];
		this.numberOfRecords = fields[3]; 
		
		
	}

	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + ","+ this.groupControlTotal+ ","
			+ this.numberOfAccounts + ","  + this.numberOfRecords + "/\n";
		return out;
	}

	public String getGroupControlTotal() {
		return groupControlTotal;
	}

	public void setGroupControlTotal(String groupControlTotal) {
		this.groupControlTotal = groupControlTotal;
	}

	public String getNumberOfAccounts() {
		return numberOfAccounts;
	}

	public void setNumberOfAccounts(String numberOfAccounts) {
		this.numberOfAccounts = numberOfAccounts;
	}

	public String getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
