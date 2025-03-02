package com.payyourself.banking.au.nab.directEntry.records;

/**
 * Base Class for Nab Direct Entry records
 * @author tpacker
 *
 */
public abstract class NabDirectEntryRecord {

	protected NabDirectEntryRecordType recordType; //Type of record: size 1
	
	
	public NabDirectEntryRecord(NabDirectEntryRecordType type){
		this.recordType = type;
	}
	

	

	/**
	 * Method to return the record as a string /n terminated.
	 * @return
	 * @throws Exception 
	 */
	public abstract String getRecord() throws Exception;
	

	
	/**
	 * Format a string, returning a formatted copy of the string, original not modified
	 * 
	 * All padding is done with 0s
	 * 
	 * @param str
	 * @param len
	 * @return
	 * @throws Exception 
	 */
	protected String createFixedLengthString(String str, int len, DirectEntryFormat justification, DirectEntryFormat fill) throws Exception{
		
		//TODO NOt working for empty strings
		String output = new String(str);
		
		switch(justification){
		case RIGHT_JUSTIFIED:
			
			switch(fill){
			case ZERO_FILL://add leading 0's
				for(int i=str.length(); i<len; i++){
					output = "0" + output;
				}
				break;
			case BLANK_FILL:
				for(int i=str.length(); i<len; i++){
					output = " " + output;
				}
				break;
			default:
				throw new Exception("Invalid Format Fill Parameter when creating direct entry record: " + fill);
			}
			break;
			
		case LEFT_JUSTIFIED:

			switch(fill){
			case ZERO_FILL://add leading 0's
				for(int i=str.length(); i<len; i++){
					output = output + "0";
				}
				break;
			case BLANK_FILL:
				for(int i=str.length(); i<len; i++){
					output = output + " ";
				}
				break;
			default:
				throw new Exception("Invalid Format Fill Parameter when creating direct entry record: " + fill);
			}
			break;

		default:
			throw new Exception("Invalid Format Justification Parameter when creating direct entry record: " + justification);
		}
		
		return output;
	}
	
}
