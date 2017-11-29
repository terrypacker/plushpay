package com.payyourself.banking;

public enum FundsType {

	IMMEDIATE("0"), //Immediate Availablity
	ONE_DAY("1"), //One day availibitiy
	TWO_DAY("2"), //Two or More days avail
	DISTRIBUTED("S"), //Available in parts imm,one day and two day
	VALUE("V"), //Value dated
	UNKNOWN("Z");

	private String value;
	
	private FundsType(String value){
		this.value = value;
	}
	
	
	/**
	 * Parse the code from a text string.
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static FundsType parseCode(String code) throws Exception{
		if(code.equals("0"))
			return IMMEDIATE;
		else if(code.equals("1"))
			return ONE_DAY;
		else if(code.equals("2"))
			return TWO_DAY;
		else if(code.equals("S"))
			return DISTRIBUTED;
		else if(code.equals("V"))
			return VALUE;
		else if(code.equals("Z"))
			return UNKNOWN;
		
		throw new Exception("Unknown Funds Type Code: " + code);
	}
	
	public String value(){
		return this.value;
	}
	
}
