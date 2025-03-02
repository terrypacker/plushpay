package com.payyourself.banking.au.nab.directEntry.records;

/**
 * Nab FIle Total record type 7
 * @author tpacker
 *
 */
public class NabFileTotalRecord extends NabDirectEntryRecord {

	private String bsbNumber; //999-999 always
	private String blank0; //12
	private long fileNetAmount; //right just 0 filled 21-30
	private long fileCreditAmount; //right just 0 filled  (debit total in self balanced or net total in non balanced) 10
	private long fileDebitAmount; //right just 0 fill ''
	private String blank1; //24
	private int record1Count; //total number of 1 records right just 0 fill, 6 len
	private String blank2; //40
	
	
	



	public NabFileTotalRecord(String bsbNumber,
			long fileNetAmount, long fileCreditAmount,
			long fileDebitAmount, int record1Count) {
		super(NabDirectEntryRecordType.TOTAL);
		this.bsbNumber = bsbNumber;
		this.blank0 = "            ";
		this.fileNetAmount = fileNetAmount;
		this.fileCreditAmount = fileCreditAmount;
		this.fileDebitAmount = fileDebitAmount;
		this.blank1 = "                        ";
		this.record1Count = record1Count;
		this.blank2 = "                                        "; //40
	}



	public NabFileTotalRecord(String line){

		super(NabDirectEntryRecordType.TOTAL);
		this.bsbNumber = line.substring(1,8).trim();
		this.blank0 = "            ";
		this.setFileNetAmount(line.substring(20,30).trim());
		this.setFileCreditAmount(line.substring(30,40).trim());
		this.setFileDebitAmount(line.substring(40,50).trim());
		this.blank1 = "                        ";
		this.setRecord1Count(line.substring(74,80).trim());
		this.blank2 = "                                        "; //40
		
		
		
	}



	@Override
	public String getRecord() throws Exception {
		
		String out = this.recordType.value();
		out = out + this.bsbNumber;
		out = out + this.blank0;
		out = out + this.createFixedLengthString(Long.toString(this.fileNetAmount), 10, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);
		out = out + this.createFixedLengthString(Long.toString(this.fileCreditAmount), 10, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);
		out = out + this.createFixedLengthString(Long.toString(this.fileDebitAmount), 10, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);
		out = out + this.blank1;		
		out = out + this.createFixedLengthString(Integer.toString(this.record1Count), 6, DirectEntryFormat.RIGHT_JUSTIFIED, DirectEntryFormat.ZERO_FILL);
		out = out + blank2;
		
		return out;

	}
	
	
	public String getBsbNumber() {
		return bsbNumber;
	}



	public void setBsbNumber(String bsbNumber) {
		this.bsbNumber = bsbNumber;
	}



	public long getFileNetAmount() {
		return fileNetAmount;
	}


	public void setFileNetAmount(long fileNetAmount){
		this.fileNetAmount = fileNetAmount;
	}

	public void setFileNetAmount(String fileNetAmount) {
		this.fileNetAmount = Long.parseLong(fileNetAmount);
	}



	public long getFileCreditAmount() {
		return fileCreditAmount;
	}



	public void setFileCreditAmount(String fileCreditAmount) {
		this.fileCreditAmount = Long.parseLong(fileCreditAmount);
	}


	public void setFileCreditAmount(long fileCreditAmount){
		this.fileCreditAmount = fileCreditAmount;
	}

	public long getFileDebitAmount() {
		return fileDebitAmount;
	}



	public void setFileDebitAmount(String fileDebitAmount) {
		this.fileDebitAmount = Long.parseLong(fileDebitAmount);
	}

	public void setFilDebitAmount(long fileDebitAmount){
		this.fileDebitAmount = fileDebitAmount;
	}


	public int getRecord1Count() {
		return record1Count;
	}



	public void setRecord1Count(String record1Count) {
		this.record1Count = Integer.parseInt(record1Count);
	}

	public void setRecord1Count(int record1Count){
		this.record1Count = record1Count;
	}
	
}
