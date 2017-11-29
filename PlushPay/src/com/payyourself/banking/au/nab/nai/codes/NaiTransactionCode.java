package com.payyourself.banking.au.nab.nai.codes;

import java.text.DecimalFormat;

import com.payyourself.banking.DebitCredit;

public enum NaiTransactionCode {
	
	UNKNOWN(000),
	CHEQUES(175),
	TRANSFER_CREDIT(195), //Transfers into our account 
	DIVIDEND(238),
	REVERSAL(252),
	ADJUSTMENT_CREDIT(357),
	MISC_CREDIT(399),
	CHEQUES_PAID(475),
	TRANSFER_DEBIT(495), //Transfers out of our account
	AUTO_DRAW(501),
	DOCUMENTARY_LC(512),
	DISHONOURED_CHEQUE(555),
	LOAN_FEE(564),
	FLEXI_PAY(595),
	DEBIT_ADJUSTMENT(631),
	INTEREST(654),
	MISC_DEBIT(699),
	DEBENTURE(905),
	NATIONAL_NOMINEE_CREDIT(906),
	CASH(910),
	CASH_CHEQUES(911),
	AGENT_CREDIT(915),
	INTERBANK_CREDIT(920),
	BANKCARD_CREDIT(925),
	CREDIT_BALANCE_TRANSFER(930),
	CREDITS_SUMMARY(935),
	EFTPOS(936),
	COCA_CREDIT(938),
	LOAN_EST_FEE(950),
	ACCOUNT_FEE(951),
	UNUSED_LIMIT_FEE(952),
	SECURITY_FEE(953),
	CHARGE(955),
	NATIONAL_NOMINEE_DEBIT(956),
	STAMP_DUTY_CHEQUE_BOOK(960),
	STAMP_DUTY(961),
	STATE_GOVT_TAX(970),
	FED_GOVT_TAX(971),
	BANKCARD_DEBIT(975),
	BALANCE_TRANSFER_DEBIT(980),
	DEBIT_SUMMARY(985),
	CHEQUE_SUMMARY(986),
	NON_CHEQUE_SUMMARY(987),
	COCA_DEBIT(988);
	
		private int value;
	
	private NaiTransactionCode(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 175:
			return "Cheques/Cash";
		case 195:
			return "Transfer Credit";
		case 238:
			return "Dividend";
		case 252:
			return "Reversal";
		case 357:
			return "Adjustment Credit";
		case 399:
			return "Miscellaneous Credit";
		case 475:
			return "Cheques Paid";
		case 495:
			return "Transfer Debit";
		case 501:
			return "Auto Draw";
		case 512:
			return "Documentary L/C Drawings/Fees";
		case 555:
			return "Dishounoured Cheques";
		case 564:
			return "Load Fee";
		case 595:
			return "Flexi Pay";
		case 631:
			return "Debit Adjustment";
		case 654:
			return "Interest";
		case 699:
			return "Miscellaneous Debit";
		case 905:
			return "Debenture";
		case 906:
			return "National Nominees Credit";
		case 910:
			return "Cash";
		case 911:
			return "Cash Cheques";
		case 915:
			return "Agent Credit";
		case 920:
			return "Interbank Credit";
		case 925:
			return "Bankcard Credit";
		case 930:
			return "Credit Balance Transfer";
		case 935:
			return "Credits Summary";
		case 936:
			return "Eftpos";
		case 938:
			return "Coca Credit";
		case 950:
			return "Load Establishment Fee";
		case 951:
			return "Account Fee";
		case 952:
			return "Unused Limit Fee";
		case 953:
			return "Security Fee";
		case 955:
			return "Charge";
		case 956:
			return "National Nominee Debit";
		case 960:
			return "Stamp Duty Cheque Book";
		case 961:
			return "Stamp Duty";
		case 970:
			return "State Government Tax";
		case 971:
			return "Federal Government Tax";
		case 975:
			return "Bankcard Debit";
		case 980:
			return "Balance Transfer Debit";
		case 985:
			return "Debit Summary";
		case 986:
			return "Cheque Summary";
		case 987:
			return "Non Cheque Summary";
		case 988:
			return "Coca Debit";
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
	public static NaiTransactionCode parseCode(int code) throws Exception {
		
		switch(code){
		case 175:
			return CHEQUES;
		case 195:
			return TRANSFER_CREDIT;
		case 238:
			return DIVIDEND;
		case 252:
			return REVERSAL;
		case 357:
			return ADJUSTMENT_CREDIT;
		case 399:
			return MISC_CREDIT;
		case 475:
			return CHEQUES_PAID;
		case 495:
			return TRANSFER_DEBIT;
		case 501:
			return AUTO_DRAW;
		case 512:
			return DOCUMENTARY_LC;
		case 555:
			return DISHONOURED_CHEQUE;
		case 564:
			return LOAN_FEE;
		case 595:
			return FLEXI_PAY;
		case 631:
			return DEBIT_ADJUSTMENT;
		case 654:
			return INTEREST;
		case 699:
			return MISC_DEBIT;
		case 905:
			return DEBENTURE;
		case 906:
			return NATIONAL_NOMINEE_CREDIT;
		case 910:
			return CASH;
		case 911:
			return CASH_CHEQUES;
		case 915:
			return AGENT_CREDIT;
		case 920:
			return INTERBANK_CREDIT;
		case 925:
			return BANKCARD_CREDIT;
		case 930:
			return CREDIT_BALANCE_TRANSFER;
		case 935:
			return CREDITS_SUMMARY;
		case 936:
			return EFTPOS;
		case 938:
			return COCA_CREDIT;
		case 950:
			return LOAN_EST_FEE;
		case 951:
			return ACCOUNT_FEE;
		case 952:
			return UNUSED_LIMIT_FEE;
		case 953:
			return SECURITY_FEE;
		case 955:
			return CHARGE;
		case 956:
			return NATIONAL_NOMINEE_DEBIT;
		case 960:
			return STAMP_DUTY_CHEQUE_BOOK;
		case 961:
			return STAMP_DUTY;
		case 970:
			return STATE_GOVT_TAX;
		case 971:
			return FED_GOVT_TAX;
		case 975:
			return BANKCARD_DEBIT;
		case 980:
			return BALANCE_TRANSFER_DEBIT;
		case 985:
			return DEBIT_SUMMARY;
		case 986:
			return CHEQUE_SUMMARY;
		case 987:
			return NON_CHEQUE_SUMMARY;
		case 988:
			return COCA_DEBIT;
			default:
				throw new Exception("Unknown NAI Status code: " + code);
		}
	}
	
	/**
	 * Return the type of the code
	 * Debit or Credit
	 * @return
	 * @throws Exception
	 */
	public DebitCredit type() throws Exception{
		switch(this.value){
		case 175:
			return DebitCredit.CREDIT;
		case 195:
			return DebitCredit.CREDIT;
		case 238:
			return DebitCredit.CREDIT;
		case 252:
			return DebitCredit.CREDIT;
		case 357:
			return DebitCredit.CREDIT;
		case 399:
			return DebitCredit.CREDIT;
		case 475:
			return DebitCredit.DEBIT;
		case 495:
			return DebitCredit.DEBIT;
		case 501:
			return DebitCredit.DEBIT;
		case 512:
			return DebitCredit.DEBIT;
		case 555:
			return DebitCredit.DEBIT;
		case 564:
			return DebitCredit.DEBIT;
		case 595:
			return DebitCredit.DEBIT;
		case 631:
			return DebitCredit.DEBIT;
		case 654:
			return DebitCredit.DEBIT;
		case 699:
			return DebitCredit.DEBIT;
		case 905:
			return DebitCredit.CREDIT;
		case 906:
			return DebitCredit.CREDIT;
		case 910:
			return DebitCredit.CREDIT;
		case 911:
			return DebitCredit.CREDIT;
		case 915:
			return DebitCredit.CREDIT;
		case 920:
			return DebitCredit.CREDIT;
		case 925:
			return DebitCredit.CREDIT;
		case 930:
			return DebitCredit.CREDIT;
		case 935:
			return DebitCredit.CREDIT;
		case 936:
			return DebitCredit.CREDIT;
		case 938:
			return DebitCredit.CREDIT;
		case 950:
			return DebitCredit.DEBIT;
		case 951:
			return DebitCredit.DEBIT;
		case 952:
			return DebitCredit.DEBIT;
		case 953:
			return DebitCredit.DEBIT;
		case 955:
			return DebitCredit.DEBIT;
		case 956:
			return DebitCredit.DEBIT;
		case 960:
			return DebitCredit.DEBIT;
		case 961:
			return DebitCredit.DEBIT;
		case 970:
			return DebitCredit.DEBIT;
		case 971:
			return DebitCredit.DEBIT;
		case 975:
			return DebitCredit.DEBIT;
		case 980:
			return DebitCredit.DEBIT;
		case 985:
			return DebitCredit.DEBIT;
		case 986:
			return DebitCredit.DEBIT;
		case 987:
			return DebitCredit.DEBIT;
		case 988:
			return DebitCredit.DEBIT;
		default:
			throw new Exception("Unknown Nai Transaction Code.");
				
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
