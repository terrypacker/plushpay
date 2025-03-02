package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2RecordCode;


/**
 * Base class for records.
 * 
 * Records have , separated fields and are terminated with a / and CRLF
 * 
 * @author tpacker
 *
 */
public abstract class Bai2Record {

	protected Bai2RecordCode code;

	public Bai2Record(Bai2RecordCode code){
		this.code = code;
	}

	/**
	 * Output the record as a formatted string
	 */
	public abstract String generateRecord() throws Exception;
	
}
