package com.payyourself.banking.au.nab.directEntry.records;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Record type 0 - Descriptive Record
 * @author tpacker
 *
 */
public class NabDescriptiveRecord extends NabDirectEntryRecord{

	private String blank0; //blank 17 char entry;
	private String reelSequenceNumber; //Right just 0 filled size 2
	private String userInstitution; //Approved abbriviation (See APCA Publication BSB Numbers) 3
	private String blank1; //blank 7 char entry;
	private String userName; //Left Justified, blank filled FULL EBCDIC char set valid cannot be all blanks 26
	private String userIdNumber; //User ID number assigned by APCA and Financial Institution (Numeric, Right Just, 0 filled) 6
	private String fileDescription; //"Payroll" 12 chars left just, blank filled 12
	private Calendar processDate;
	
	private String blank2; //40 
	
	
	/**
	 * Base constructor
	 * @param reelSequenceNumber
	 * @param userInstitution
	 * @param userName
	 * @param userIdNumber
	 * @param fileDescription
	 * @param processDate
	 * @throws Exception 
	 */
	public NabDescriptiveRecord(String reelSequenceNumber,
			String userInstitution, String userName,
			String userIdNumber, String fileDescription, Calendar processDate) throws Exception {
		super(NabDirectEntryRecordType.DESCRIPTIVE);
		this.blank0 = "                 "; 
		
		this.reelSequenceNumber = reelSequenceNumber;
		
		this.userInstitution = userInstitution; //Should be 3 (not checking here, do that at output)
		this.blank1 = "       ";
		this.userName = userName;
		this.userIdNumber = userIdNumber;
		this.fileDescription = reelSequenceNumber;
		this.processDate = processDate;
		this.blank2 = "                                        "; // 40 chars blank
	}


	public NabDescriptiveRecord(String line) throws ParseException{
		super(NabDirectEntryRecordType.DESCRIPTIVE);

		this.blank0 = "                 "; 
		this.reelSequenceNumber = line.substring(19,20).trim();
		this.userInstitution = line.substring(21,23).trim();
		this.blank1 = "       ";
		this.userName = line.substring(31,56).trim();
		this.userIdNumber = line.substring(57,62).trim();
		this.fileDescription = line.substring(63,74).trim();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy");
		sdf.parse(line.substring(75,80));
		this.blank2 = "                                        "; // 40 chars blank
		
	}
	

	/**
	 * Returns the record as a string
	 * @return
	 * @throws Exception 
	 */
	public String getRecord() throws Exception{
		
		String record = this.recordType.value();
		record = record + this.blank0;
		record = record + this.createFixedLengthString(this.reelSequenceNumber, 2, DirectEntryFormat.RIGHT_JUSTIFIED,DirectEntryFormat.ZERO_FILL);
		record = record + this.userInstitution;
		record = record + this.blank1;
		record = record + this.createFixedLengthString(this.userName, 26, DirectEntryFormat.LEFT_JUSTIFIED,DirectEntryFormat.BLANK_FILL);
		record = record + this.createFixedLengthString(this.userIdNumber, 6, DirectEntryFormat.RIGHT_JUSTIFIED,DirectEntryFormat.ZERO_FILL);
		record = record + this.createFixedLengthString(this.fileDescription, 12, DirectEntryFormat.LEFT_JUSTIFIED,DirectEntryFormat.BLANK_FILL);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy");
		
		String dateString = sdf.format(this.processDate);
		
		record = record + dateString + this.blank2;
		
		if(record.length() != 120){
			throw new Exception("Incorrect Length for Descriptive Record, calculated: " + record.length() + " should be 120.");
		}
		
		return record + "\n";
	}
	
}
