package com.payyourself.trading.trader.group;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import com.payyourself.currency.PyCurrency;
import com.payyourself.trading.trader.Trader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Group of Traders Buying and Selling the same currency types.
 */
public class TraderGroup implements java.io.Serializable {

	private long groupid;
	private PyCurrency currencyToSell;
	private PyCurrency currencyToBuy;
	private List<Trader> traders = new ArrayList<Trader>(0);

	public TraderGroup() {
	}

	public TraderGroup(PyCurrency currencyToSell, PyCurrency currencyToBuy,
			List<Trader> traders) {
		this.currencyToSell = currencyToSell;
		this.currencyToBuy = currencyToBuy;
		this.traders = traders;
	}

	public long getGroupid() {
		return this.groupid;
	}

	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}

	public PyCurrency getCurrencyToSell() {
		return this.currencyToSell;
	}

	public void setCurrencyToSell(PyCurrency currencyToSell) {
		this.currencyToSell = currencyToSell;
	}

	public PyCurrency getCurrencyToBuy() {
		return this.currencyToBuy;
	}

	public void setCurrencyToBuy(PyCurrency currencyToBuy) {
		this.currencyToBuy = currencyToBuy;
	}

	public List<Trader> getTraders() {
		return this.traders;
	}

	public void setTraders(List<Trader> traders) {
		this.traders = traders;
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [\n");
		buffer.append("currencyToSell").append("='")
				.append(getCurrencyToSell()).append("'\n");
		buffer.append("currencyToBuy").append("='").append(getCurrencyToBuy())
				.append("'\n");
		buffer.append("]\n");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TraderGroup))
			return false;
		TraderGroup castOther = (TraderGroup) other;

		return ((this.getCurrencyToSell() == castOther.getCurrencyToSell()) || (this
				.getCurrencyToSell() != null
				&& castOther.getCurrencyToSell() != null && this
				.getCurrencyToSell().equals(castOther.getCurrencyToSell())))
				&& ((this.getCurrencyToBuy() == castOther.getCurrencyToBuy()) || (this
						.getCurrencyToBuy() != null
						&& castOther.getCurrencyToBuy() != null && this
						.getCurrencyToBuy()
						.equals(castOther.getCurrencyToBuy())))
				&& ((this.getTraders() == castOther.getTraders()) || (this
						.getTraders() != null
						&& castOther.getTraders() != null && this.getTraders()
						.equals(castOther.getTraders())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getCurrencyToSell() == null ? 0 : this.getCurrencyToSell()
						.hashCode());
		result = 37
				* result
				+ (getCurrencyToBuy() == null ? 0 : this.getCurrencyToBuy()
						.hashCode());
		result = 37 * result
				+ (getTraders() == null ? 0 : this.getTraders().hashCode());
		return result;
	}

	// The following is extra code specified in the hbm.xml files
	//Additional Code By Terry Packer

	/**
	 * Copy Constructor
	 */
	public TraderGroup(TraderGroup group) {

		this.setGroupid(group.getGroupid());
		if (group.getCurrencyToBuy() != null)
			this.setCurrencyToBuy(new PyCurrency(group.getCurrencyToBuy()));
		if (group.getCurrencyToSell() != null)
			this.setCurrencyToSell(new PyCurrency(group.getCurrencyToSell()));

	}
	// end of extra code specified in the hbm.xml files

}
