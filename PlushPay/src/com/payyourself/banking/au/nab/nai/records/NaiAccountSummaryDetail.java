package com.payyourself.banking.au.nab.nai.records;

import java.text.DecimalFormat;

import com.payyourself.banking.au.nab.nai.codes.NaiAccountSummaryCode;

/**
 * Multiple Summary Details are contained in one Type 03 record
 * @author tpacker
 *
 */
public class NaiAccountSummaryDetail {

	//These 2 are repeated for each summary code in the record
	private NaiAccountSummaryCode summaryCode;
	private long amount; //Amount pertaining to summary code
	
	/**
	 * 
	 * @param summaryCode
	 * @param amount
	 */
	public NaiAccountSummaryDetail(NaiAccountSummaryCode summaryCode, long amount){
		this.summaryCode = summaryCode;
		this.amount = amount;
	}
	
	
	/**
	 * Generate the record
	 * @return
	 */
	public String generateRecord(){
		
		String out = this.summaryCode.value() + ",";
		if(this.amount<0){
			DecimalFormat formatter = new DecimalFormat("0;0-");
			out = out + formatter.format(this.amount);
		}else{
			out = out + this.amount;
		}
		return out;
	}
	
}
