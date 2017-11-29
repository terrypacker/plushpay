package com.payyourself.banking.au.nab.directEntry.codes;

import java.text.DecimalFormat;

public enum NabDirectEntryIndicator {

	NEW_BSB("N"),
	TNA_DRAWING("T"),
	DOUBLE_DIVIDEND("W"), //Doulbe tax agreement dividend
	DIVIDEND("X"),
	INTEREST("Y"),
	NONE(" ");
	
	private NabDirectEntryIndicator(String value){
		this.value = value;
	}
	
	private String value;

	public static NabDirectEntryIndicator parse(String code) throws Exception {
		if(code.equals("N")){
			return NEW_BSB;
		}else if(code.equals("T")){
			return TNA_DRAWING;
		}else if(code.equals("W")){
			return DOUBLE_DIVIDEND;
		}else if(code.equals("X")){
			return DIVIDEND;
		}else if(code.equals("Y")){
			return INTEREST;
		}else if(code.equals(" ")){
			return NONE;
		}else{
			throw new Exception("Unknown Direct Entry Indicator: " + code);
		}
	}
	
	/**
	 * Get the value of the code as a string 
	 * @return
	 */
	public String value(){
		
		return this.value;
	}
	
}
