package com.payyourself.banking.au.nab.directEntry.records;

import java.text.DecimalFormat;

public enum NabDirectEntryRecordType {

	DESCRIPTIVE(0), DETAIL(1), TOTAL(7);
	
	private int value;
	
	private NabDirectEntryRecordType(int value){
		this.value = value;
	}
	
	/**
	 * Get the value of the code as a string 
	 * @return
	 */
	public String value(){
		DecimalFormat format = new DecimalFormat("0"); //Format with 1 digits
		
		return format.format(this.value);
	}

	public static NabDirectEntryRecordType parseCode(int code) throws Exception {
		
		switch(code){
		case 0:
			return DESCRIPTIVE;
		case 1:
			return DETAIL;
		case 7:
			return TOTAL;
		default:
			throw new Exception("Unknown Nab Direct Entry Record: "+ code);
				
		}//edn case
	}
}
