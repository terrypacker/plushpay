package com.payyourself.currency.rate.testData;

import java.util.Calendar;

public class RateTestDataEntry {

	private String ticker;
	private Calendar date;
	private float open;
	private float high;
	private float low;
	private float close;
	
	
	
	
	public RateTestDataEntry(String ticker, Calendar date, float open2, float high2,
			float low2, float close2) {
		super();
		this.ticker = ticker;
		this.date = date;
		this.open = open2;
		this.high = high2;
		this.low = low2;
		this.close = close2;
	}
	/**
	 * @return the ticker
	 */
	public String getTicker() {
		return ticker;
	}
	/**
	 * @param ticker the ticker to set
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
	/**
	 * @return the open
	 */
	public float getOpen() {
		return open;
	}
	/**
	 * @param open the open to set
	 */
	public void setOpen(int open) {
		this.open = open;
	}
	/**
	 * @return the high
	 */
	public float getHigh() {
		return high;
	}
	/**
	 * @param high the high to set
	 */
	public void setHigh(int high) {
		this.high = high;
	}
	/**
	 * @return the low
	 */
	public float getLow() {
		return low;
	}
	/**
	 * @param low the low to set
	 */
	public void setLow(int low) {
		this.low = low;
	}
	/**
	 * @return the close
	 */
	public float getClose() {
		return close;
	}
	/**
	 * @param close the close to set
	 */
	public void setClose(int close) {
		this.close = close;
	}
	
	
	
	
	
	
}
