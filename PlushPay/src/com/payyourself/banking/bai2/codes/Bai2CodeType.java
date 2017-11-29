package com.payyourself.banking.bai2.codes;

public enum Bai2CodeType {

	ACCOUNT_STATUS(0), //Provide info on Account Status balances, avaliable funds etc
	ACCOUNT_SUMMARY(1), //Summarize account credit/debit activity
	TRANSACTION_DETAIL(2), //Details on individual credit/debit transactions in account
	FILE_RECORD(3);

	private int value;
	
	private Bai2CodeType(int value){
		this.value = value;
	}
	
	
	
}
