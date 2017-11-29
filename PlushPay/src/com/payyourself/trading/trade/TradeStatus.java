package com.payyourself.trading.trade;

public enum TradeStatus {

	DEPOSIT("Deposit Funds"), //Trade created and Traders notified, awaiting funds deposit
	CLOSED("Closed"), //Trade Completed Successfully
	FAILED("Failed"); //Trade Failed
	
	private String value;
	
	private TradeStatus(String value){
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
}
