package com.payyourself.banking.au.nab.nai.records;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;
import com.payyourself.banking.bai2.records.Bai2Record;

/**
 * NAB Banking Interface File Header
 * Based on the BAI2 File header
 * 
 * Differences to BAI2:
 * 
 *  NAI does not populate the Sender Identification field.
 *	NAI file sequence number is always 1, BAI2 increments the number for each file sent to the customer.
 *	BAI2 has an additional field, Version Number (always '2')
 * 
 * @author tpacker
 *
 */
public class NaiFileHeader extends Bai2Record {

	private List<NaiGroupHeader> groups;
	private NaiFileTrailer trailer; 
	
	//file code
	private String senderId; //Not populated with NAI
	private String recieverId; //
	private String creationDate; //YYMMDD Format (AEST)
	private String creationTime; //Creation time //0001 to 2400 (can be obtained from creation Date)
	private String fileId; //Always 1 for NAB
	private int recordLength; //Size in bytes of this record (Sometimes not used, so leave empty in that case)
	private int blockSize; //Size of block?  (If not used just leave blank)
	
	

	
	/**
	 * Construct a record
	 * @param recieverId
	 * @param creationDate
	 * @param creationTime
	 * @param fileId
	 * @param recordLength
	 * @param blockSize
	 * @throws Exception 
	 */
	public NaiFileHeader(String recieverId, String creationDate, String creationTime,
			int recordLength,
			int blockSize) throws Exception {
		super(Bai2RecordCode.FILE_HEADER);
		
		//Check for invalid chars
		if((recieverId.contains("/"))||(recieverId.contains(","))){
			throw new Exception("Invalid Character , or / in: " + recieverId);
		}
		this.recieverId = recieverId;
		this.senderId = "";
		this.creationDate = creationDate;
		this.creationTime = creationTime;
		this.fileId = "1";
		this.recordLength = recordLength;
		this.blockSize = blockSize;
		
		this.groups = new ArrayList<NaiGroupHeader>();
		
	}
	
	/**
	 * Constructor using a calendar
	 * @param recieverId
	 * @param creationDate
	 * @param recordLength
	 * @param blockSize
	 */
	public NaiFileHeader(String recieverId, Calendar creationDate, int recordLength, int blockSize){
		super(Bai2RecordCode.FILE_HEADER);
		
		this.senderId = "";
		this.recieverId = recieverId;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		this.creationDate = sdf.format(creationDate.getTime());
		
		sdf = new SimpleDateFormat("HHmm");
		this.creationTime = sdf.format(creationDate.getTime());
		this.fileId = "1";
		this.recordLength = recordLength;
		this.blockSize = blockSize;
		
		this.groups = new ArrayList<NaiGroupHeader>();
		
	}
	
	/**
	 * Construct a record from a string
	 * @param line
	 * @throws Exception 
	 */
	public NaiFileHeader(String line) throws Exception{
		super(Bai2RecordCode.FILE_HEADER);
		
		this.groups = new ArrayList<NaiGroupHeader>();

		
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
		
		if(fields.length != 8){
			throw new Exception("Unable to parse line, " + fields.length + " parts found, expected 8");
		}

		//code is fields[0] but we already know that
		// so check to make sure we are doing the right thing
		// we won't check to see if it exists because this is done higher up in the file parser.
		
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
		if(thisCode != Bai2RecordCode.FILE_HEADER){
			throw new Exception("Can't create type " + thisCode + " using File Header Constructor.");
		}
		this.senderId = fields[1]; 
		this.recieverId = fields[2];
		
		this.creationDate = fields[3];
		this.creationTime = fields[4];
		
		this.fileId = fields[5];

		//Confirm that there is info there
		if(fields[6].equals("")){
			this.recordLength = -1;
		}else{
			this.recordLength = Integer.parseInt(fields[6]);
		}
		
		if(fields[7].equals("")){
			this.blockSize = -1;
		}else{
			this.blockSize = Integer.parseInt(fields[7]);
		}


		
	}
	
	@Override
	public String generateRecord() throws Exception {
		
		String out = this.code.value() + "," + this.senderId + "," + this.recieverId + "," + this.creationDate + "," + 
		this.creationTime + "," + this.fileId + ",";
		
		if(this.recordLength < 0){
			out = out + ",";
		}else{
			out = out + this.recordLength + ",";
		}
		
		//Determine Termination
		if(this.blockSize < 0){
			out = out + ",/";
		}else{
			out = out + this.blockSize + "/\n";
		}
		if(out.length() > 80){
			throw new Exception("Record too long, 88 Type not applicable: " + out);
		}
		
		//Add any groups
		for(int i=0; i<this.groups.size(); i++){
			out = out + this.groups.get(i).generateRecord();
		}
		
		if(this.trailer != null)
			out = out + this.trailer.generateRecord();
		
		return out;
	}
	
	
	
	public List<NaiTransactionDetail> getCredits(String accountNumber) {
		List<NaiTransactionDetail> details = new ArrayList<NaiTransactionDetail>();
		
		for(int i=0; i<this.groups.size(); i++){
			details.addAll(this.groups.get(i).getCredits(accountNumber));
		}
		
		return details;
		
	}

	
	public void setCreationDate(Calendar date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		this.creationDate = sdf.format(date.getTime());
		

	}
	
	public void setCreationTime(Calendar time){
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		this.creationTime = sdf.format(time.getTime());
	}
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getRecieverId() {
		return recieverId;
	}
	public void setRecieverId(String recieverId) {
		this.recieverId = recieverId;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getRecordLength() {
		return recordLength;
	}
	public void setRecordLength(String recordLength) {
		this.recordLength = Integer.parseInt(recordLength);
	}
	
	public void setRecordLength(int recordLength){
		this.recordLength = recordLength;
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(String blockSize) {
		this.blockSize = Integer.parseInt(blockSize);
	}
	
	public void setBlockSize(int size){
		this.blockSize = size;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setGroups(List<NaiGroupHeader> groups) {
		this.groups = groups;
	}

	public List<NaiGroupHeader> getGroups() {
		return groups;
	}

	public void addGroup(NaiGroupHeader groupHeader) {
		
		this.groups.add(groupHeader);
		
	}

	public void setTrailer(NaiFileTrailer trailer) {
		this.trailer = trailer;
	}

	public NaiFileTrailer getTrailer() {
		return trailer;
	}


	
	
}
