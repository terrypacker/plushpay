package com.payyourself.banking.bai2.codes;

import java.text.DecimalFormat;

import com.payyourself.banking.DebitCredit;

public enum Bai2SummaryCode {

	TOTAL_CREDITS(100),
	TOTAL_LOCKBOX_DEPOSITS(110),
	TOTAL_DTC_CREDITS(131),
	TOTAL_ACH_CREDITS(140),
	ACH_SETTLEMENT_CREDITS(167),
	TOTAL_OTHER_CHECK_DEPOSITS(170),
	TOTAL_LOAN_PROCEEDS(180),
	TOTAL_INCOMING_MONEY_TRANSFERS(190),
	TOTAL_INTERNATIONAL_CREDITS(210),
	TOTAL_LETTERS_OF_CREDIT_CREDITS(215),
	TOTAL_SECURITY_CREDITS(230),
	TOTAL_CHECKS_POSTED_AND_RETURNED(250),
	TOTAL_DEBIT_REVERSALS(251),
	TOTAL_REJECTED_CREDITS(260),
	TOTAL_ZBA_CREDITS(270),
	TOTAL_TRUST_CREDITS(307),
	TOTAL_CASH_CENTER_CREDITS(352),
	TOTAL_CREDIT_ADJUSTMENTS(356),
	TOTAL_BACK_VALUE_CREDITS(370),
	TOTAL_MISCELLANEOUS_CREDITS(390),
	TOTAL_DEBITS(400),
	TOTAL_LOCKBOX_DEBITS(416),
	TOTAL_ACH_DISBURSEMENT_FUNDING_DEBITS(446),
	TOTAL_ACH_DEBITS(450),
	ACH_SETTLEMENT_DEBITS(467),
	TOTAL_CHECKS_PAID(470),
	TOTAL_LOAN_PAYMENTS(480),
	TOTAL_OUTGOING_MONEY_TRANSFERS(490),
	TOTAL_INTERNATIONAL_DEBITS(510),
	TOTAL_LETTERS_OF_CREDIT_DEBITS(515),
	TOTAL_SECURITY_DEBITS(530),
	TOTAL_DEPOSITED_ITEMS_RETURNED(550),
	TOTAL_CREDIT_REVERSALS(551),
	TOTAL_ZBA_DEBITS(570),
	TOTAL_PRESENTMENT(580),
	TOTAL_DISBURSING_CHECKS_PAID_EARLY(583),
	TOTAL_DISBURSING_CHECKS_PAID_LATER(584),
	DISBURSING_FUNDING_REQUIREMENT(585),
	LATE_DEBITS(578),
	TOTAL_CASH_CENTER_DEBITS(628),
	TOTAL_DEBIT_ADJUSTMENTS(630),
	TOTAL_TRUST_DEBITS(632),
	TOTAL_INVESTMENT_INTEREST_DEBITS(655),
	TOTAL_BACK_VALUE_DEBITS(670),
	TOTAL_MISCELLANEOUS_DEBITS(690);
	
	
	private int value;
	
	private Bai2SummaryCode(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 100:
			return "Total Credits";
		case 110:
			return "Total Lockbox Deposits";
		case 131:
			return "Total DTC Credits";
		case 140:
			return "Total ACH Credits";
		case 167:
			return "Total ACH Settlement Credits";
		case 170:
			return "Total Other Check Deposits";
		case 180:
			return "Total Loan Proceeds";
		case 190:
			return "Total Incoming Money Transfers";
		case 210:
			return "Total International Credits";
		case 215:
			return "Total Letters Of Credit";
		case 230:
			return "Total Security Credits";
		case 250:
			return "Total Checks Posted and Returned";
		case 251:
			return "Total Debit Reversals";
		case 260:
			return "Total Rejected Credits";
		case 270:
			return "Total ZBA Credits";
		case 307:
			return "Total Trust Credits";
		case 352:
			return "Total Cash Center Credits";
		case 356:
			return "Total Credit Adjustment";
		case 370:
			return "Total Back Value Credits";
		case 390:
			return "Total Miscellaneous Credits";
		case 400:
			return "Total Debits";
		case 416:
			return "Total Lockbox Debits";
		case 446:
			return "Total ACH Disbursement Funding Debits";
		case 450:
			return "Total ACH Debits";
		case 467:
			return "ACH Settlement Debits";
		case 470:
			return "Total Checks Paid";
		case 480:
			return "Total Loan Payments";
		case 490:
			return "Total Outgoing Money Transfers";
		case 510:
			return "Total Inernational Debits";
		case 515:
			return "Total Letters of Credit";
		case 530:
			return "Total Security Debits";
		case 550:
			return "Total Deposited Items Returned";
		case 551:
			return "Total Credit Reversals";
		case 570:
			return "Total ZBA Debits";
		case 580:
			return "Total Presentment";
		case 583:
			return "Total Disbursing Checks Paid Early";
		case 584:
			return "Total Disbursing Checks Paid Later";
		case 585:
			return "Disbursing Funding Requirements";
		case 578:
			return "Late Debits";
		case 628:
			return "Total Cash Center Debits";
		case 630:
			return "Total Debit Adjustments";
		case 632:
			return "Total Trust Debits";
		case 655:
			return "Total Investment Interest Debits";
		case 670:
			return "Total Back Value Debits";
		case 690:
			return "Miscellaneous Debits";
		default:
			return "Unknown Code: " + this.value;
		}

	}
	
	public DebitCredit getType() throws Exception{
		switch(this.value){
		case 100:
			return DebitCredit.CREDIT;
		case 110:
			return DebitCredit.CREDIT;
		case 131:
			return DebitCredit.CREDIT;
		case 140:
			return DebitCredit.CREDIT;
		case 167:
			return DebitCredit.CREDIT;
		case 170:
			return DebitCredit.CREDIT;
		case 180:
			return DebitCredit.CREDIT;
		case 190:
			return DebitCredit.CREDIT;
		case 210:
			return DebitCredit.CREDIT;
		case 215:
			return DebitCredit.CREDIT;
		case 230:
			return DebitCredit.CREDIT;
		case 250:
			return DebitCredit.CREDIT;
		case 251:
			return DebitCredit.CREDIT;
		case 260:
			return DebitCredit.CREDIT;
		case 270:
			return DebitCredit.CREDIT;
		case 307:
			return DebitCredit.CREDIT;
		case 352:
			return DebitCredit.CREDIT;
		case 356:
			return DebitCredit.CREDIT;
		case 370:
			return DebitCredit.CREDIT;
		case 390:
			return DebitCredit.CREDIT;
		case 400:
			return DebitCredit.DEBIT;
		case 416:
			return DebitCredit.DEBIT;
		case 446:
			return DebitCredit.DEBIT;
		case 450:
			return DebitCredit.DEBIT;
		case 467:
			return DebitCredit.DEBIT;
		case 470:
			return DebitCredit.DEBIT;
		case 480:
			return DebitCredit.DEBIT;
		case 490:
			return DebitCredit.DEBIT;
		case 510:
			return DebitCredit.DEBIT;
		case 515:
			return DebitCredit.DEBIT;
		case 530:
			return DebitCredit.DEBIT;
		case 550:
			return DebitCredit.DEBIT;
		case 551:
			return DebitCredit.DEBIT;
		case 570:
			return DebitCredit.DEBIT;
		case 580:
			return DebitCredit.DEBIT;
		case 583:
			return DebitCredit.DEBIT;
		case 584:
			return DebitCredit.DEBIT;
		case 585:
			return DebitCredit.DEBIT;
		case 578:
			return DebitCredit.DEBIT;
		case 628:
			return DebitCredit.DEBIT;
		case 630:
			return DebitCredit.DEBIT;
		case 632:
			return DebitCredit.DEBIT;
		case 655:
			return DebitCredit.DEBIT;
		case 670:
			return DebitCredit.DEBIT;
		case 690:
			return DebitCredit.DEBIT;
		default:
			throw new Exception("Unknown Code.");
		}
	}

	/**
	 * Parse an int
	 * @param code
	 * @return
	 */
	public static Bai2SummaryCode parseCode(int code) throws Exception{
		
		switch(code){
		case 100:
			return TOTAL_CREDITS;
		case 110:
			return TOTAL_LOCKBOX_DEPOSITS;
		case 131:
			return TOTAL_DTC_CREDITS;
		case 140:
			return TOTAL_ACH_CREDITS;
		case 167:
			return TOTAL_ACH_CREDITS;
		case 170:
			return TOTAL_OTHER_CHECK_DEPOSITS;
		case 180:
			return TOTAL_LOAN_PROCEEDS; 
		case 190:
			return TOTAL_INCOMING_MONEY_TRANSFERS;
		case 210:
			return TOTAL_INTERNATIONAL_CREDITS;
		case 215:
			return TOTAL_LETTERS_OF_CREDIT_CREDITS;
		case 230:
			return TOTAL_SECURITY_CREDITS;
		case 250:
			return TOTAL_CHECKS_POSTED_AND_RETURNED;
		case 251:
			return TOTAL_DEBIT_REVERSALS;
		case 260:
			return TOTAL_REJECTED_CREDITS;
		case 270:
			return TOTAL_ZBA_CREDITS;
		case 307:
			return TOTAL_TRUST_CREDITS;
		case 352:
			return TOTAL_CASH_CENTER_CREDITS;
		case 356:
			return TOTAL_CREDIT_ADJUSTMENTS;
		case 370:
			return TOTAL_BACK_VALUE_CREDITS;
		case 390:
			return TOTAL_MISCELLANEOUS_CREDITS;
		case 400:
			return TOTAL_DEBITS;
		case 416:
			return TOTAL_LOCKBOX_DEBITS;
		case 446:
			return TOTAL_ACH_DISBURSEMENT_FUNDING_DEBITS;
		case 450:
			return TOTAL_ACH_DEBITS;
		case 467:
			return ACH_SETTLEMENT_DEBITS;
		case 470:
			return TOTAL_CHECKS_PAID;
		case 480:
			return TOTAL_LOAN_PAYMENTS;
		case 490:
			return TOTAL_OUTGOING_MONEY_TRANSFERS;
		case 510:
			return TOTAL_INTERNATIONAL_DEBITS;
		case 515:
			return TOTAL_LETTERS_OF_CREDIT_DEBITS;
		case 530:
			return TOTAL_SECURITY_DEBITS;
		case 550:
			return TOTAL_DEPOSITED_ITEMS_RETURNED;
		case 551:
			return TOTAL_CREDIT_REVERSALS;
		case 570:
			return TOTAL_ZBA_DEBITS;
		case 580:
			return TOTAL_PRESENTMENT;
		case 583:
			return TOTAL_DISBURSING_CHECKS_PAID_EARLY;
		case 584:
			return TOTAL_DISBURSING_CHECKS_PAID_LATER;
		case 585:
			return DISBURSING_FUNDING_REQUIREMENT;
		case 578:
			return LATE_DEBITS;
		case 628:
			return TOTAL_CASH_CENTER_DEBITS;
		case 630:
			return TOTAL_DEBIT_ADJUSTMENTS;
		case 632:
			return TOTAL_TRUST_DEBITS;
		case 655:
			return TOTAL_INVESTMENT_INTEREST_DEBITS;
		case 670:
			return TOTAL_BACK_VALUE_DEBITS;
		case 690:
			return TOTAL_MISCELLANEOUS_DEBITS;			
		default:
			throw new Exception("Unknown Summary Code: " + code);
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
