package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;

public class Bai2FileTrailer extends Bai2Record {

	private String fileControlTotal; //Sum of all group control totals in this file.
	private String numberOfGroups; //Number of 02 records in this group
	private String numberOfRecords; //Number of 02,03,16,88,49 and this 99 record (There must be one 98 record for each 02 record)
	
	
	
	/**
	 * 
	 * @param fileControlTotal
	 * @param numberOfGroups
	 * @param numberOfRecords
	 */
	public Bai2FileTrailer(String fileControlTotal,
			String numberOfGroups, String numberOfRecords) {
		super(Bai2RecordCode.FILE_TRAILER);
		this.fileControlTotal = fileControlTotal;
		this.numberOfGroups = numberOfGroups;
		this.numberOfRecords = numberOfRecords;
	}


	/**
	 * Construct a record from a line of text
	 * @param line
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public Bai2FileTrailer(String line) throws Exception{
		super(Bai2RecordCode.FILE_TRAILER);
		
		line = line.replace("/\n", "");
		String fields[] = line.split(",");
		
		if(Bai2RecordCode.parseCode(Integer.parseInt(fields[0])) != Bai2RecordCode.FILE_TRAILER){
			throw new Exception("Invalid record type: " + fields[0] + " for File Trailer Constructor.");
		}
		
		this.fileControlTotal = fields[1];
		this.numberOfGroups = fields[2];
		this.numberOfRecords = fields[3];
		
		
	}

	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.fileControlTotal + ","
			+ this.numberOfGroups + "," + this.numberOfRecords + "/\n";
		return out;
	}



	public String getFileControlTotal() {
		return fileControlTotal;
	}



	public void setFileControlTotal(String fileControlTotal) {
		this.fileControlTotal = fileControlTotal;
	}



	public String getNumberOfGroups() {
		return numberOfGroups;
	}



	public void setNumberOfGroups(String numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}



	public String getNumberOfRecords() {
		return numberOfRecords;
	}



	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

}
