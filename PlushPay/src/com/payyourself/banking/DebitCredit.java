package com.payyourself.banking;

public enum DebitCredit {

	CREDIT(0),
	DEBIT(1),
	NA(2);
	
	private int value;
	
	private DebitCredit(int value){
		this.value = value;
	}
	
	public String toString(){
		switch(this.value){
		case 0:
			return "Credit";
		case 1:
			return "Debit";
		case 2:
			return "N/A";
		default:
			return "Unknown Value: " + this.value;
		}
	}
	
	
	
	
}
