package com.payyourself.banking.us;

import com.payyourself.banking.DebitCredit;

public class UsdBankTransaction {

	private DebitCredit type;
	private long traderId;
	private long tradeId;
	private long amount;
	
	
	
	public UsdBankTransaction(DebitCredit type, long traderId, long tradeId,
			long amount) {
		super();
		this.type = type;
		this.traderId = traderId;
		this.tradeId = tradeId;
		this.amount = amount;
	}
	/**
	 * @return the type
	 */
	public DebitCredit getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(DebitCredit type) {
		this.type = type;
	}
	/**
	 * @return the traderId
	 */
	public long getTraderId() {
		return traderId;
	}
	/**
	 * @param traderId the traderId to set
	 */
	public void setTraderId(long traderId) {
		this.traderId = traderId;
	}
	/**
	 * @return the tradeId
	 */
	public long getTradeId() {
		return tradeId;
	}
	/**
	 * @param tradeId the tradeId to set
	 */
	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}
	/**
	 * @return the amount
	 */
	public long getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	
	
	
}
