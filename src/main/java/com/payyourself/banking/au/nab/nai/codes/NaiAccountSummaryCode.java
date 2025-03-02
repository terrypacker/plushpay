package com.payyourself.banking.au.nab.nai.codes;

import java.text.DecimalFormat;

public enum NaiAccountSummaryCode {

	CLOSING_BALANCE(15),
	TOTAL_CREDITS(100),
	TOTAL_CREDIT_TRANSACTIONS(102),
	TOTAL_DEBITS(400),
	TOTAL_DEBIT_TRANSACTIONS(402),
	ACCRUED_CREDIT_INTEREST(500),
	ACCRUED_DEBIT_INTEREST(501),
	ACCOUNT_LIMIT(502),
	AVAILABLE_LIMIT(503),
	EFFECTIVE_DEBIT_INTEREST_RATE(965),
	EFFECTIVE_CREDIT_INTEREST_RATE(966),
	ACCRUED_STATE_GOVT_DUTY(967),
	ACCRUED_GOVT_CREDIT_TAX(968),
	ACCRUED_GOVT_DEBIT_TAX(969),
	UNKNOWN(000);
	
	private int value;
	
	private NaiAccountSummaryCode(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 15:
			return "Closing Balance";
		case 100:
			return "Total Credits";
		case 102:
			return "Number of Credit Transactions";
		case 400:
			return "Total Debits";
		case 402:
			return "Number of Debit Transactions";
		case 500:
			return "Accrued Credit Interest";
		case 501:
			return "Accrued Debit Interest";
		case 502:
			return "Account Limit";
		case 503:
			return "Available Account Limit";
		case 965:
			return "Effective Debit Interest Rate";
		case 966:
			return "Effective Credit Interest Rate";
		case 967:
			return "Accrued State Government Duty";
		case 968:
			return "Accrued Government Credit Tax";
		case 969:
			return "Accrued Government Debit Tax";
		default:
			return "Unknown Code: " + this.value;
				
		}
	}

	/**
	 * Create an enum that correlates to the integer code.
	 * 
	 * Note that the leading 0 is removed from the int
	 * because I am not sure how to keep it there when using Integer.parseInt(int)
	 * 
	 * When the code is output as a string the 0 is there.
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static NaiAccountSummaryCode parseCode(int code) throws Exception {
		
		switch(code){
			case 15:
				return CLOSING_BALANCE;
			case 100:
				return TOTAL_CREDITS;
			case 102:
				return TOTAL_CREDIT_TRANSACTIONS;
			case 400:
				return TOTAL_DEBITS;
			case 402:
				return TOTAL_DEBIT_TRANSACTIONS;
			case 500:
				return ACCRUED_CREDIT_INTEREST;
			case 501:
				return ACCRUED_DEBIT_INTEREST;
			case 502:
				return ACCOUNT_LIMIT;
			case 503:
				return AVAILABLE_LIMIT;
			case 965:
				return EFFECTIVE_DEBIT_INTEREST_RATE;
			case 966:
				return EFFECTIVE_CREDIT_INTEREST_RATE;
			case 967:
				return 	ACCRUED_STATE_GOVT_DUTY;
			case 968:
				return ACCRUED_GOVT_CREDIT_TAX;
			case 969:
				return ACCRUED_GOVT_DEBIT_TAX;
			default:
				throw new Exception("Unknown NAI Status code: " + code);
		}
	}
	
	
	/**
	 * Get the value of the code as a string 
	 * @return
	 */
	public String value(){
		DecimalFormat format = new DecimalFormat("000"); //Format with 2 digits
		
		return format.format(this.value);
	}
	
}
