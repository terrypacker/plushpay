package com.payyourself.trading.trader.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.payyourself.currency.CurrencyConverter;
import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.trading.trader.Trader;

/**
 * Perhaps we need a Util group that can manage trader lists as pointers.
 * 
 * This would quicken up the process of creating a copy of a group.
 * 
 * You create the group using a pointer to a list and then 
 * add any new members to a additional list.
 * 
 * @author tpacker
 *
 */
public class TraderGroupUtil {

	/* List of traders in group */
	private TraderGroup group;
	private List<Trader> traders = new ArrayList<Trader>(0);
	private PyCurrency currencyToSell;
	private PyCurrency currencyToBuy;
	
	/**
	 * Create a group of traders that all have the same currency to sell
	 * @param traders
	 * @param currencyToSell
	 * @param currencyToBuy
	 * @param baseTotal
	 * @throws Exception 
	 */
	public TraderGroupUtil(List<Trader> traders) throws Exception {
		super();
		this.traders = traders;
		this.currencyToSell = traders.get(0).getCurrencyToSell();
		this.currencyToBuy = traders.get(0).getCurrencyToBuy();
		
		/* Add all trader amount to total */
		for(int i=1; i<traders.size(); i++){
			this.currencyToSell = this.currencyToSell.add(traders.get(i).getCurrencyToSell());
			this.currencyToBuy = this.currencyToBuy.add(traders.get(i).getCurrencyToBuy());			
		}
	}
	
	public TraderGroupUtil(Trader trader){
		
		this.traders = new ArrayList<Trader>();
		this.traders.add(trader);
		this.currencyToSell = new PyCurrency(trader.getCurrencyToSell().getValue(),
									trader.getCurrencyToSell().getType());
		this.currencyToBuy = new PyCurrency(trader.getCurrencyToBuy().getValue(),
									trader.getCurrencyToBuy().getType());
	}
	
	/**
	 * Empty Constructor to create an empty group.
	 */
	public TraderGroupUtil(PyCurrencyType toBuy, PyCurrencyType toSell){
		this.traders = new ArrayList<Trader>();
		this.currencyToBuy = new PyCurrency(0,toBuy);
		this.currencyToSell = new PyCurrency(0,toSell);
	}
	
	
	/**
	 * Copy Constructor
	 * @param group
	 */
	public TraderGroupUtil(TraderGroupUtil group) {
		if(group.getGroup() != null){
			this.setGroup(new TraderGroup(group.getGroup()));
		}
		this.traders = new ArrayList<Trader>();
	//	for (int i = 0; i < group.getTraders().size(); i++) {
			//this.traders.add(new Trader(group.get(i)));
//			this.traders.add(group.get(i)); //In the matching algo we don't every change any trader's values.

		//}
		
		this.traders.addAll(group.getTraders());
		
		if(group.getCurrencyToBuy() != null)
			this.currencyToBuy = new PyCurrency(group.getCurrencyToBuy());
		if(group.getCurrencyToSell() != null)
			this.currencyToSell = new PyCurrency(group.getCurrencyToSell());
	}
	
	public TraderGroupUtil() {
		this.traders = new ArrayList<Trader>();
		this.currencyToSell = null;
		this.currencyToBuy = null;
	}

	/**
	 * Add a trader and increase total Base Amount.
	 * @param trader
	 * @throws Exception 
	 */
	public void add(Trader trader) throws Exception{
		//Could check currency types of trader here...but the add will do that for us
		this.traders.add(trader);
		this.currencyToSell = this.currencyToSell.add(trader.getCurrencyToSell());
		this.currencyToBuy = this.currencyToBuy.add(trader.getCurrencyToBuy());			
	}
	
	/**
	 * Remove a trader from the group.
	 * @param pos
	 * @throws Exception 
	 */
	public void remove(int pos) throws Exception{
		this.currencyToBuy = this.currencyToBuy.minus(this.traders.get(pos).getCurrencyToBuy());
		this.currencyToSell = this.currencyToSell.minus(this.traders.get(pos).getCurrencyToSell());
		this.traders.remove(pos);
	}
	
	



	
	public int size(){
		return this.traders.size();
	}
	
	
	public TraderGroup getAsTraderGroup(){
		return new TraderGroup(this.currencyToSell, this.currencyToBuy,
				this.traders);
	}
	
	
	/**
	 * Method for use on jsp page to get size of traders
	 * @return
	 */
	public int getNumberOfTraders(){
		return this.traders.size();
	}
	
	public Trader get(int location){
		return this.traders.get(location);
	}

	public List<Trader> getTraders() {
		return traders;
	}

	public void setTraders(List<Trader> traders) {
		this.traders = traders;
	}

	public PyCurrency getCurrencyToSell() {
		return currencyToSell;
	}

	public void setCurrencyToSell(PyCurrency currencyToSell) {
		this.currencyToSell = currencyToSell;
	}

	public PyCurrency getCurrencyToBuy() {
		return currencyToBuy;
	}

	public void setCurrencyToBuy(PyCurrency currencyToBuy) {
		this.currencyToBuy = currencyToBuy;
	}

	public void setGroup(TraderGroup group) {
		this.group = group;
	}

	public TraderGroup getGroup() {
		return group;
	}

/*	public Trader getSmallestTrader() {
		float smallestBaseValue = this.getTraders().get(0).getCurrencyToBuy().getValue();
		int i=0;
		for(int j=1; j<this.getTraders().size(); j++){
			if(smallestBaseValue > this.getTraders().get(j).getCurrencyToBuy().getBaseValue()){
				i=j;
			}
		}
		
		return this.getTraders().get(i);
		
	}
*/
	public String toString(){
		
		CurrencyConverter conv = new CurrencyConverter();
		
		String output = "Number of Members: " + this.traders.size();
		output = output + "Buying: " + conv.getAsString(null, null, this.currencyToBuy) + "\n";
		output = output + "Selling Base: " + conv.getAsString(null, null, this.currencyToSell) + "\n";
		return output;
	}

	/**
	 * Replace the trader at pos
	 * 
	 * UPdate totals.
	 * 
	 * @param pos
	 * @param trader
	 * @throws Exception 
	 */
	public void set(int pos, Trader trader) throws Exception {
		
		//Subtract old
		this.currencyToBuy = this.currencyToBuy.minus(this.traders.get(pos).getCurrencyToBuy());
		this.currencyToSell = this.currencyToSell.minus(this.traders.get(pos).getCurrencyToSell());
		
		this.traders.set(pos, trader);
		
		//Add new
		this.currencyToBuy  = this.currencyToBuy.add(trader.getCurrencyToBuy());
		this.currencyToSell = this.currencyToSell.add(trader.getCurrencyToSell());
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currencyToBuy == null) ? 0 : currencyToBuy.hashCode());
		result = prime * result
				+ ((currencyToSell == null) ? 0 : currencyToSell.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((traders == null) ? 0 : traders.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraderGroupUtil other = (TraderGroupUtil) obj;
		if (currencyToBuy == null) {
			if (other.currencyToBuy != null)
				return false;
		} else if (!currencyToBuy.equals(other.currencyToBuy))
			return false;
		if (currencyToSell == null) {
			if (other.currencyToSell != null)
				return false;
		} else if (!currencyToSell.equals(other.currencyToSell))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (traders == null) {
			if (other.traders != null)
				return false;
		} else if (!traders.equals(other.traders))
			return false;
		return true;
	}
	
	
	

}
