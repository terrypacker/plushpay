package com.payyourself.banking.bai2.codes;

import java.text.DecimalFormat;

public enum Bai2StatusCode {

	OPENING_LEDGER(010),
	CLOSING_LEDGER(015),
	CURRENT_LEDGER(030),
	OPENING_AVAILABLE(040),
	AVERAGE_AVAILABLE(043),
	CLOSING_AVAILABLE(045),
	AVG_CLOSING_AVAILABLE_MTD(050),
	AVG_CLOSING_AVAILABLE_YTD(055),
	TOTAL_INVESTMENT_POSITION(057),
	CURRENT_AVAILABLE(060),
	TOTAL_FLOAT(063),
	ONE_DAY_FLOAT(072),
	FLOAT_ADJUSTMENT(073),
	TWO_OR_MORE_DAYS_FLOAT(074),
	THREE_OR_MORE_DAYS_FLOAT(075),
	ADJ_TO_BALANCES(076);
	
	
	private int value;
	
	private Bai2StatusCode(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 010:
			return "Opening Ledger";
		case 015:
			return "Closing Ledger";
		case 030:
			return "Current Ledger";
		case 040:
			return "Opening Available";
		case 043:
			return "Average Available";
		case 045:
			return "Closing Available";
		case 050:
			return "Average Closing Available MTD";
		case 055:
			return "Average Closing Available YTD";
		case 057:
			return "Total Investment Position";
		case 060:
			return "Current Available";
		case 063:
			return "Total Float";
		case 072:
			return "1 Day Float";
		case 073:
			return "Float Adjustment";
		case 074:
			return "2 or More Days Float";
		case 075:
			return "3 or More Days Float";
		case 076:
			return "Adjustment To Balances";
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
	public static Bai2StatusCode parseCode(int code) throws Exception {
		
		switch(code){
			case 10:
				return OPENING_LEDGER;
			case 15:
				return CLOSING_LEDGER;
			case 30:
				return CURRENT_LEDGER;
			case 40:
				return OPENING_AVAILABLE;
			case 43:
				return AVERAGE_AVAILABLE;
			case 45:
				return CLOSING_AVAILABLE;
			case 50:
				return AVG_CLOSING_AVAILABLE_MTD;
			case 55:
				return AVG_CLOSING_AVAILABLE_YTD;
			case 57:
				return TOTAL_INVESTMENT_POSITION;
			case 60:
				return CURRENT_AVAILABLE;
			case 63:
				return TOTAL_FLOAT;
			case 72:
				return ONE_DAY_FLOAT;
			case 73:
				return FLOAT_ADJUSTMENT;
			case 74:
				return TWO_OR_MORE_DAYS_FLOAT;
			case 75:
				return THREE_OR_MORE_DAYS_FLOAT;
			case 76:
				return ADJ_TO_BALANCES;
			default:
				throw new Exception("Unknown BAI2 Status code: " + code);
		}
	}
	
	/**
	 * Get the value of the code as a string with a leading 0 (000)
	 * @return
	 */
	public String value(){
		DecimalFormat format = new DecimalFormat("000"); //Format with 2 digits
		
		return format.format(this.value);
	}
	
	
}
