package com.payyourself.banking.au.nab.directEntry.codes;

import java.text.DecimalFormat;

public enum NabTransactionCode {

	DEBIT(13),
	CREDIT(50),
	INTEREST(51),
	FAMILY_PAYMENT(52),
	PAY(53),
	PENSION(54),
	ALLOTMENT(55),
	DIVIDEND(56),
	DEBENTURE_INTEREST(57);
	
	private NabTransactionCode(int value){
		this.value = value;
	}
	
	private int value;


	public static NabTransactionCode parse(int code) throws Exception {
		
		switch(code){
		case 13:
			return DEBIT;
		case 50:
			return CREDIT;
		case 51:
			return INTEREST;
		case 52:
			return FAMILY_PAYMENT;
		case 53:
			return PAY;
		case 54:
			return PENSION;
		case 55:
			return ALLOTMENT;
		case 56:
			return DIVIDEND;
		case 57:
			return DEBENTURE_INTEREST;
		default:
			throw new Exception("Unknown Nab Transaction Code: " + code);
		}
		
	}
	
	/**
	 * Get the value of the code as a string 
	 * @return
	 */
	public String value(){
		DecimalFormat format = new DecimalFormat("00"); //Format with 2 digits
		
		return format.format(this.value);
	}
	
}
