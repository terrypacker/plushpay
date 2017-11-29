package com.payyourself.banking.bai2.codes;

import java.text.DecimalFormat;

/**
 * Code for the records contained in 
 * BAI2 Formated Files
 * 
 * @author tpacker
 *
 */
public enum Bai2RecordCode {

	FILE_HEADER(01),
	GROUP_HEADER(02),
	ACCOUNT_SUMMARY(03),
	TRANSACTION_DETAIL(16),
	CONTINUATION_RECORD(88),
	ACCOUNT_TRAILER(49),
	GROUP_TRAILER(98),
	FILE_TRAILER(99);
	
	
	private int value;
	
	private Bai2RecordCode(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 1:
			return "File Header";
		case 2:
			return "Group Header";
		case 3:
			return "Account Summary";
		case 16:
			return "Transaction Details and Summary";
		case 88:
			return "Continuation Record";
		case 49:
			return "Account Trailer";
		case 98:
			return "Group Trailer";
		case 99:
			return "File Trailer";
		default:
			return "Unknown Code: " + this.value;
				
		
		}
	}

	/**
	 * return the value of the Enum
	 * @return
	 */
	public String value() {
		
		DecimalFormat format = new DecimalFormat("00"); //Format with 2 digits
		
		return format.format(this.value);
	}

	/**
	 * Create an enum from an int
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Bai2RecordCode parseCode(int code) throws Exception {
		switch(code){
		case 1:
			return FILE_HEADER;
		case 2:
			return GROUP_HEADER;
		case 3:
			return ACCOUNT_SUMMARY;
		case 16:
			return TRANSACTION_DETAIL;
		case 88:
			return CONTINUATION_RECORD;
		case 49:
			return ACCOUNT_TRAILER;
		case 98:
			return GROUP_TRAILER;
		case 99:
			return FILE_TRAILER;
		default:
			throw new Exception("Unable to create a Bai2 record of type: " + code);
				
		
		}
	}
	
}
