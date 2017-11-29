package com.payyourself.banking.bai2.records;

import com.payyourself.banking.bai2.codes.Bai2AccountCode;

/**
 * For use in type 3 records
 * 
 * @author tpacker
 *
 */
public class Bai2AccountSummaryDetails implements Comparable<Bai2AccountSummaryDetails>{

	private Bai2AccountCode typeCode; //Identifies the type of Summary or Status 
	private String amount; //Amount in cents. (can be negative -100000)
	private String itemCount; //For summary codes number of items making up total must have default
	private String fundsType; //Unknown (Not used)
	
	/**
	 * Constructor
	 * @param typeCode
	 * @param amount
	 * @param itemCount
	 * @param fundsType
	 */
	public Bai2AccountSummaryDetails(Bai2AccountCode typeCode, String amount,
			String itemCount, String fundsType) {
		super();
		this.typeCode = typeCode;
		this.amount = amount;
		this.itemCount = itemCount;
		this.fundsType = fundsType;
	}

	public Bai2AccountCode getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(Bai2AccountCode typeCode) {
		this.typeCode = typeCode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	public String getFundsType() {
		return fundsType;
	}

	public void setFundsType(String fundsType) {
		this.fundsType = fundsType;
	}

	/**
	 * Order by type code.
	 * @param d
	 * @return
	 */
	public int compareTo(Bai2AccountSummaryDetails d) {
		
		return this.typeCode.compareTo(d.getTypeCode());
		
	}

	@Override
	public String toString() {
		return "Bai2AccountSummaryDetails [amount=" + amount + "\n, fundsType="
				+ fundsType + "\n, itemCount=" + itemCount + "\n, typeCode="
				+ typeCode + "]";
	}

	/**
	 * Generate the record
	 * @return
	 */
	public String generateRecord(){
		
		return this.typeCode.getCode() + "," + this.amount + "," + this.itemCount + "," + this.fundsType+ ",";
		
	}
	
	
}
