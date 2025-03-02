package com.payyourself.trading.trader;

public enum TraderStatus {

	CONFIRMED("Confirmed"), //Trader is in system, but not attached to a trade
	DEPOSIT("Deposit Funds"), //Trader is attached to a trade but trade is OPEN
	FINALIZED("Finalized"), //Trader is attached to trade and funds have been delivered
	FAILED("Failed"); //Trader is attached to trade but something went wrong, trade is now dead.
	
	private String value;
	
	private TraderStatus(String value){
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
}
