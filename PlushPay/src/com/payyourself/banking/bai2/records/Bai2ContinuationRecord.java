package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;

/**
 * BAI2 Continuation Record:
 * 
 * A continuation of the preceding record. The format is 
 *exactly the same as in the preceding record. 
 *If the preceding record ended with a text field, the text 
 *continues in the 88 record. If the preceding record did not 
 *end within a text field, the 88 record continues with 
 *whatever field follows the final field in the preceding 
 *physical record. 
 *Do not split non-text fields between records. If a non-text 
 *field is started in one record, it must be completed in that 
 *record. The 88 record that follows may continue with the 
 *next field. 
 * @author tpacker
 *
 */
public class Bai2ContinuationRecord extends Bai2Record {

	String continuationData;
	
	/**
	 * Construct a continuation record from the string's payload
	 * 
	 * The payload is assumed to have the required 
	 * termination ie. / for non-text fields
	 * 
	 * @param data
	 * @throws Exception 
	 */
	public Bai2ContinuationRecord(String data) throws Exception {
		super(Bai2RecordCode.CONTINUATION_RECORD);
		
		/* Check to see if the payload is too long */
		if(data.length() > 76 ){
			throw new Exception("Contintuation Record Payload too large: " + data.length());
		}
		this.continuationData = data;
	}

	@Override
	public String generateRecord() throws Exception {
		String out = this.code.value() + "," + this.continuationData;
		return out;
	}
	
	
	

}
