package com.payyourself.banking.bai2.codes;

import com.payyourself.banking.DebitCredit;

/**
 * Master class to wrap all account code types
 * because I want to keep the separate as types
 * instead of bulk packing them together in one
 * huge enum class.
 * @author tpacker
 *
 */
public class Bai2AccountCode implements Comparable<Bai2AccountCode>{

	private int code;
	private String description;
	private Bai2CodeType codeType;
	private DebitCredit transactionType;
	private String value;
	

	/**
	 * Create an account code from an integer input
	 * @param code
	 * @throws Exception 
	 */
	public Bai2AccountCode(int code) throws Exception{
		
		this.setCode(code);
		
		//Try to create a code, see if it exists
		try{
			Bai2StatusCode tempCode = Bai2StatusCode.parseCode(code);
			this.description = tempCode.toString();
			this.codeType = Bai2CodeType.ACCOUNT_STATUS;
			this.transactionType = DebitCredit.NA;
			this.value = tempCode.value();
			return;
		}catch(Exception e){
			//Do nothing yet
		}
		
		try{
			Bai2SummaryCode tempCode = Bai2SummaryCode.parseCode(code);
			this.description = tempCode.toString();
			this.codeType = Bai2CodeType.ACCOUNT_SUMMARY;
			this.transactionType = tempCode.getType();
			this.value = tempCode.value();
			return;
		}catch(Exception e){
			//Do nothing yet
		}
		
		try{
			Bai2DetailCode tempCode = Bai2DetailCode.parseCode(code);
			this.description = tempCode.toString();
			this.codeType = Bai2CodeType.TRANSACTION_DETAIL;
			this.transactionType = tempCode.getType();
			this.value = tempCode.value();
			return;
		}catch(Exception e){
			throw new Exception("BAI2 Code unknown: " + code);
		}
		
		
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Bai2CodeType getCodeType() {
		return codeType;
	}


	public void setCodeType(Bai2CodeType codeType) {
		this.codeType = codeType;
	}


	public DebitCredit getTransactionType() {
		return transactionType;
	}


	public void setTransactionType(DebitCredit transactionType) {
		this.transactionType = transactionType;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public int getCode() {
		return code;
	}


	/**
	 * Make comparable on the code
	 */
	public int compareTo(Bai2AccountCode o) {
	
		if(this.code> o.getCode())
			return 1;
		else if(this.code < o.getCode())
			return -1;
		else 
			return 0;
		
	}
	
	public String toString(){
		return "(" + this.code + ") " +  this.description;
	}
	
	public String value(){
		return this.value;
	}
	
}
