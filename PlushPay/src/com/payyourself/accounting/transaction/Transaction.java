package com.payyourself.accounting.transaction;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

import com.payyourself.accounting.account.Account;
import com.payyourself.accounting.journal.Journal;
import com.payyourself.accounting.period.Period;
import com.payyourself.currency.PyCurrency;
import com.payyourself.userManagement.user.User;
import java.util.Calendar;

/**
 * Transaction generated by hbm2java
 */
public class Transaction implements java.io.Serializable {

	private long id;
	private boolean credit;
	private Calendar date;
	private Period period;
	private Account account;
	private PyCurrency amount;
	private User user;
	private Journal journal;

	public Transaction() {
	}

	public Transaction(boolean credit, Calendar date, Period period,
			Account account, PyCurrency amount, User user, Journal journal) {
		this.credit = credit;
		this.date = date;
		this.period = period;
		this.account = account;
		this.amount = amount;
		this.user = user;
		this.journal = journal;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isCredit() {
		return this.credit;
	}

	public void setCredit(boolean credit) {
		this.credit = credit;
	}

	public Calendar getDate() {
		return this.date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Period getPeriod() {
		return this.period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public PyCurrency getAmount() {
		return this.amount;
	}

	public void setAmount(PyCurrency amount) {
		this.amount = amount;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Journal getJournal() {
		return this.journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [\n");
		buffer.append("id").append("='").append(getId()).append("'\n");
		buffer.append("]\n");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Transaction))
			return false;
		Transaction castOther = (Transaction) other;

		return (this.getId() == castOther.getId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) this.getId();

		return result;
	}

}
