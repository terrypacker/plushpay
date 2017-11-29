package com.payyourself.banking.bai2.records;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;

/**
 * 
 * @author tpacker
 *
 * BAI2 Format
 *
 * File Header Record (01)
 * 
 * This marks the beginning of a file,
 * identifies the sender and reciever,
 * and describes the structure of the file.
 *
 */
public class Bai2FileHeader extends Bai2Record{
	
	//file code
	private String senderId; //9 Digit ABA Number
	private String recieverId; //
	private Calendar creationDate; //YYMMDD Format (Eastern Time NY)
	//Creation time //0001 to 2400 (can be obtained from creation Date)
	private String fileId; //A unique number for each file within a day
	private String recordLength; //Size in bytes of this record (Sometimes not used, so leave empty in that case)
	private String blockSize; //Size of block?  (If not used just leave blank)
	private static int BAI_VERSION = 2; //Version of record (always 2)

	
	/**
	 * Construct a header from the fields.
	 * 
	 * @param senderID
	 * @param recieverID
	 * @param creationDate
	 * @param fileId
	 * @param recordLength
	 * @param blockSize
	 * @throws Exception
	 */
	public Bai2FileHeader(String senderId,
			String recieverId, Calendar creationDate, String fileId,
			String recordLength, String blockSize) throws Exception {
		super(Bai2RecordCode.FILE_HEADER);
		
		//Check for invalid chars
		if((senderId.contains("/"))||(senderId.contains(","))){
			throw new Exception("Invalid Character , or / in: " + senderId);
		}
		this.senderId = senderId;
		
		//Check for invalid chars
		if((recieverId.contains("/"))||(recieverId.contains(","))){
			throw new Exception("Invalid Character , or / in: " + recieverId);
		}
		this.recieverId = recieverId;
		
		this.creationDate = creationDate;
		this.fileId = fileId;
		this.recordLength = recordLength;
		this.blockSize = blockSize;
	}
	
	/**
	 * Construct a file header from a String
	 * @param line
	 * @throws Exception 
	 */
	public Bai2FileHeader(String line) throws Exception{
		super(Bai2RecordCode.FILE_HEADER);
		
		//Chop off the ,/ at the end because it can lead to issues if the line ends with ,/ which does not indicate a value
		line = line.replace(",/", "");
		String[] fields = line.split(","); //Split the line on ,
		if(fields.length != 9){
			throw new Exception("Unable to parse line, " + fields.length + " parts found, expected 9");
		}

		//Setup the date formatter
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");

		//code is fields[0] but we already know that
		// so check to make sure we are doing the right thing
		// we won't check to see if it exists because this is done higher up in the file parser.
		
		Bai2RecordCode thisCode = Bai2RecordCode.parseCode(Integer.parseInt(fields[0]));
		if(thisCode != Bai2RecordCode.FILE_HEADER){
			throw new Exception("Can't create type " + thisCode + " using File Header Constructor.");
		}
		this.senderId = fields[1]; 
		this.recieverId = fields[2];
		
		this.creationDate = Calendar.getInstance();
		this.creationDate.setTime(sdf.parse(fields[3] + fields[4]));
		
		this.fileId = fields[5];

		//Confirm that there is info there
		this.recordLength = fields[6];
		
		this.blockSize = fields[7];
		
		if(!fields[8].contains("/")){
			throw new Exception("Line has bad format, didn't find / on the end: " + line);
		}
		
		String[] lastFieldParts = fields[8].split("/");
		
		if(lastFieldParts[0].equals("")){
			//There is no version information... hmmmm what to do.
			throw new Exception("No BAI Version found. Bailing out.");
		}else{
			int ver = Integer.parseInt(lastFieldParts[0]);
			
			if( ver != Bai2FileHeader.BAI_VERSION){
				throw new Exception("Incorrect BAI Version found: " + ver + ", expected " + Bai2FileHeader.BAI_VERSION);
			}
		}
		


		
	}
	


	@Override
	public String generateRecord() throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String date = sdf.format(this.creationDate.getTime());
		
		sdf = new SimpleDateFormat("HHmm");
		String time = sdf.format(this.creationDate.getTime());
		
		String out = this.code.value() + "," + this.senderId + "," + this.recieverId + "," + date + "," + 
		time + "," + this.fileId + "," + this.recordLength + "," + 
		this.blockSize + "," + Bai2FileHeader.BAI_VERSION + "/" + "\n";
		
		if(out.length() > 80){
			throw new Exception("Record too long, 88 Type not applicable: " + out);
		}
		
		return out;
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

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(String recordLength) {
		this.recordLength = recordLength;
	}

	public String getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(String blockSize) {
		this.blockSize = blockSize;
	}

	public static int getBAI_VERSION() {
		return BAI_VERSION;
	}
	
	
	

}
