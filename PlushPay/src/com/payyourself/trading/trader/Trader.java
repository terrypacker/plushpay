package com.payyourself.trading.trader;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import com.payyourself.currency.PyCurrency;
import com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary;
import com.payyourself.trading.trader.group.TraderGroup;
import com.payyourself.userManagement.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Trader generated by hbm2java
 */
public class Trader implements Comparable, java.io.Serializable {

	private long traderid;
	private TraderGroup group;
	private User user;
	private PyCurrency currencyToBuy;
	private PyCurrency currencyToSell;
	private String status;
	private List<TradeBeneficiary> beneficiaries = new ArrayList<TradeBeneficiary>(
			0);

	public Trader() {
	}

	public Trader(TraderGroup group, User user, PyCurrency currencyToBuy,
			PyCurrency currencyToSell, String status,
			List<TradeBeneficiary> beneficiaries) {
		this.group = group;
		this.user = user;
		this.currencyToBuy = currencyToBuy;
		this.currencyToSell = currencyToSell;
		this.status = status;
		this.beneficiaries = beneficiaries;
	}

	public long getTraderid() {
		return this.traderid;
	}

	public void setTraderid(long traderid) {
		this.traderid = traderid;
	}

	public TraderGroup getGroup() {
		return this.group;
	}

	public void setGroup(TraderGroup group) {
		this.group = group;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PyCurrency getCurrencyToBuy() {
		return this.currencyToBuy;
	}

	public void setCurrencyToBuy(PyCurrency currencyToBuy) {
		this.currencyToBuy = currencyToBuy;
	}

	public PyCurrency getCurrencyToSell() {
		return this.currencyToSell;
	}

	public void setCurrencyToSell(PyCurrency currencyToSell) {
		this.currencyToSell = currencyToSell;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<TradeBeneficiary> getBeneficiaries() {
		return this.beneficiaries;
	}

	public void setBeneficiaries(List<TradeBeneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [\n");
		buffer.append("traderid").append("='").append(getTraderid()).append(
				"'\n");
		buffer.append("group").append("='").append(getGroup()).append("'\n");
		buffer.append("user").append("='").append(getUser()).append("'\n");
		buffer.append("currencyToBuy").append("='").append(getCurrencyToBuy())
				.append("'\n");
		buffer.append("currencyToSell").append("='")
				.append(getCurrencyToSell()).append("'\n");
		buffer.append("]\n");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Trader))
			return false;
		Trader castOther = (Trader) other;

		return (this.getTraderid() == castOther.getTraderid())
				&& ((this.getGroup() == castOther.getGroup()) || (this
						.getGroup() != null
						&& castOther.getGroup() != null && this.getGroup()
						.equals(castOther.getGroup())))
				&& ((this.getUser() == castOther.getUser()) || (this.getUser() != null
						&& castOther.getUser() != null && this.getUser()
						.equals(castOther.getUser())))
				&& ((this.getCurrencyToBuy() == castOther.getCurrencyToBuy()) || (this
						.getCurrencyToBuy() != null
						&& castOther.getCurrencyToBuy() != null && this
						.getCurrencyToBuy()
						.equals(castOther.getCurrencyToBuy())))
				&& ((this.getCurrencyToSell() == castOther.getCurrencyToSell()) || (this
						.getCurrencyToSell() != null
						&& castOther.getCurrencyToSell() != null && this
						.getCurrencyToSell().equals(
								castOther.getCurrencyToSell())))
				&& ((this.getStatus() == castOther.getStatus()) || (this
						.getStatus() != null
						&& castOther.getStatus() != null && this.getStatus()
						.equals(castOther.getStatus())))
				&& ((this.getBeneficiaries() == castOther.getBeneficiaries()) || (this
						.getBeneficiaries() != null
						&& castOther.getBeneficiaries() != null && this
						.getBeneficiaries()
						.equals(castOther.getBeneficiaries())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getTraderid();
		result = 37 * result
				+ (getGroup() == null ? 0 : this.getGroup().hashCode());
		result = 37 * result
				+ (getUser() == null ? 0 : this.getUser().hashCode());
		result = 37
				* result
				+ (getCurrencyToBuy() == null ? 0 : this.getCurrencyToBuy()
						.hashCode());
		result = 37
				* result
				+ (getCurrencyToSell() == null ? 0 : this.getCurrencyToSell()
						.hashCode());
		result = 37 * result
				+ (getStatus() == null ? 0 : this.getStatus().hashCode());
		result = 37
				* result
				+ (getBeneficiaries() == null ? 0 : this.getBeneficiaries()
						.hashCode());
		return result;
	}

	// The following is extra code specified in the hbm.xml files
	/** * Setup to order traders by the currency they * want to Buy. */
	public int compareTo(Object o) {
		Trader comp = (Trader) o;
		return this.currencyToBuy.compareTo(comp.getCurrencyToBuy());
	}

	/** * Add a beneficiary to the list. * @param bene */
	public void addBeneficiary(TradeBeneficiary bene) {
		this.beneficiaries.add(bene);
	}
	// end of extra code specified in the hbm.xml files

}
