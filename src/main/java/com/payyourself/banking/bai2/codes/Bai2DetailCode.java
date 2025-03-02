package com.payyourself.banking.bai2.codes;

import java.text.DecimalFormat;

import com.payyourself.banking.DebitCredit;

/**
 * Detail Codes
 * 
 * Many Detail Codes can be associated with one Summary code.
 * 
 * @author tpacker
 *
 */
public enum Bai2DetailCode {

	PRE_AUTHORIZED_ACH_CREDIT(165),
	CHECK_DEPOSIT_PACKAGE(175);
	
	
	private int value;
	
	private Bai2DetailCode(int code){
		this.value = code;
	}

	public static Bai2DetailCode parseCode(int code) throws Exception {
		switch(code){
		case 165:
			return PRE_AUTHORIZED_ACH_CREDIT;
		case 175:
			return CHECK_DEPOSIT_PACKAGE;
		default:
			throw new Exception("Unknown BAI2 Detail Code: " + code);
		}
	}

	public DebitCredit getType() throws Exception {
		
		switch(this.value){
		case 165:
			return DebitCredit.CREDIT;
		case 175:
			return DebitCredit.CREDIT;
		default: 
			throw new Exception("Unknown Debit/Credit Type for BAI2 Detail Code: " + this.value);
		}
		
	}

	public String value() {
		DecimalFormat format = new DecimalFormat("000"); //Format with 2 digits
		
		return format.format(this.value);
	}

}
