package com.payyourself.currency;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import com.payyourself.currency.type.PyCurrencyType;

/**
 * This is the base concept for trading.  Every currency is converted to a baseRate 			upon creation.  This base rate is used for all computation and comparison 			as this allows cross currency comparison and inter-currency comparison  			even when multiple exchange rates are used.
 */
public class PyCurrency implements Comparable, java.io.Serializable {

	private long currencyId;
	private long value;
	private PyCurrencyType type;

	public PyCurrency() {
	}

	public PyCurrency(long value, PyCurrencyType type) {
		this.value = value;
		this.type = type;
	}

	public long getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

	public long getValue() {
		return this.value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public PyCurrencyType getType() {
		return this.type;
	}

	public void setType(PyCurrencyType type) {
		this.type = type;
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [\n");
		buffer.append("currencyId").append("='").append(getCurrencyId())
				.append("'\n");
		buffer.append("value").append("='").append(getValue()).append("'\n");
		buffer.append("type").append("='").append(getType()).append("'\n");
		buffer.append("]\n");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PyCurrency))
			return false;
		PyCurrency castOther = (PyCurrency) other;

		return (this.getValue() == castOther.getValue())
				&& ((this.getType() == castOther.getType()) || (this.getType() != null
						&& castOther.getType() != null && this.getType()
						.equals(castOther.getType())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getValue();
		result = 37 * result
				+ (getType() == null ? 0 : this.getType().hashCode());
		return result;
	}

	// The following is extra code specified in the hbm.xml files
	/**
	 * Copy Constructor
	 *
	 */
	public PyCurrency(PyCurrency original) {

		this.value = original.getValue();
		if (original.getType() != null)
			this.type = new PyCurrencyType(original.getType());
		this.currencyId = original.getCurrencyId();

	}

	/**
	 * Add 2 PyCurrencies of the same type.
	 * @param addend
	 * @return
	 * @throws Exception
	 */
	public PyCurrency add(PyCurrency addend) throws Exception {
		PyCurrency sum = new PyCurrency();

		if (!this.getType().getCode().equals(addend.getType().getCode())) {
			throw new Exception(
					"Unable to add currencies of different types! ("
							+ this.getType().getCode() + " and "
							+ addend.getType().getCode());
		}
		sum.setValue(addend.getValue() + this.getValue());
		sum.setType(this.getType());

		return sum;
	}

	/**
	 * Subtract toSub from this PyCurrency
	 * @param toSub
	 * @return
	 * @throws Exception
	 */
	public PyCurrency minus(PyCurrency toSub) throws Exception {
		PyCurrency sum = new PyCurrency();

		if (!this.getType().getCode().equals(toSub.getType().getCode())) {
			throw new Exception(
					"Unable to subtract currencies of different types! ("
							+ this.getType().getCode() + " and "
							+ toSub.getType().getCode());
		}
		sum.setValue(this.getValue() - toSub.getValue());
		sum.setType(this.getType());

		return sum;
	}

	/**
	 * Compare to method.
	 * Return 0 if they are same
	 * Return 1 if this is >
	 * Return -1 if this is <
	 */
	public int compareTo(Object o) {
		PyCurrency comp = (PyCurrency) o;
		//TODO this comparison is wrong as differing types will return as same
		if ((comp.getValue() > this.value)
				&& (comp.getType().equals(this.type))) {
			return -1;
		} else if ((comp.getValue() < this.value)
				&& (comp.getType().equals(this.type))) {
			return 1;
		} else {
			//Must be the same
			return 0;
		}

	}
	// end of extra code specified in the hbm.xml files

}
