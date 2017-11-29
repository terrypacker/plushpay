package com.payyourself.trading.exact.sorted;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.currency.PyCurrency;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.group.TraderGroup;


public class Combination implements Comparable<Combination>{

	private long gain; //Sum of the two
	private long cost; //Cost of Investment
	private float roi;
	
	private long valueSum;
	private long weightSum;
	
	private List<Trader> buyers;
	private List<Trader> sellers;

	private PyCurrency sellersCurrencyToSell;
	private PyCurrency sellersCurrencyToBuy;
	
	private PyCurrency buyersCurrencyToSell;
	private PyCurrency buyersCurrencyToBuy;
	private long sellerProfit;
	private long buyerProfit;
	
	
	
	/**
	 * Create a combination and compute values
	 * for this group of buyers and sellers
	 * @param buyers
	 * @param sellers
	 * @throws Exception
	 */

	public Combination(Trader buyer, Trader seller) throws Exception{
		
		valueSum = 0;
		weightSum = 0;

		this.buyers = new ArrayList<Trader>();
		this.sellers = new ArrayList<Trader>();

		this.buyers.add(buyer);
		this.sellers.add(seller);
		
		//Compute currency to buy
		this.buyersCurrencyToBuy = this.buyers.get(0).getCurrencyToBuy();
		this.buyersCurrencyToSell = this.buyers.get(0).getCurrencyToSell();
		valueSum = valueSum + buyers.get(0).getCurrencyToSell().getValue()*buyers.get(0).getCurrencyToSell().getType().getRateToBase();
		weightSum = weightSum + buyers.get(0).getCurrencyToSell().getValue();
		
		for(int i=1; i<this.buyers.size(); i++){
			
			this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.
				add(this.buyers.get(i).getCurrencyToBuy());
			this.buyersCurrencyToSell = this.buyersCurrencyToSell.
				add(this.buyers.get(i).getCurrencyToSell());
			valueSum = valueSum + buyers.get(i).getCurrencyToSell().getValue()*buyers.get(i).getCurrencyToSell().getType().getRateToBase();
			weightSum = weightSum + buyers.get(i).getCurrencyToSell().getValue();
		}
		
		//Compute currency to sell
		this.sellersCurrencyToSell = this.sellers.get(0).getCurrencyToSell();
		this.sellersCurrencyToBuy = this.sellers.get(0).getCurrencyToBuy();
		valueSum = valueSum + sellers.get(0).getCurrencyToBuy().getValue() *sellers.get(0).getCurrencyToBuy().getType().getRateToBase();
		weightSum = weightSum + sellers.get(0).getCurrencyToBuy().getValue();

		
		for(int i=1; i<this.sellers.size(); i++){
			this.sellersCurrencyToSell = this.sellersCurrencyToSell.
				add(this.sellers.get(i).getCurrencyToSell());
			this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.
				add(this.sellers.get(i).getCurrencyToBuy());
			
			valueSum = valueSum + sellers.get(i).getCurrencyToBuy().getValue() *sellers.get(i).getCurrencyToBuy().getType().getRateToBase();
			weightSum = weightSum + sellers.get(i).getCurrencyToBuy().getValue();

		}
		//The sign of these numbers is with reference to our Bank Account
		this.sellerProfit = this.sellersCurrencyToSell.getValue() - this.buyersCurrencyToBuy.getValue();
		this.buyerProfit = this.buyersCurrencyToSell.getValue() - this.sellersCurrencyToBuy.getValue();

		
		float virtualRate = (float)valueSum/(float)weightSum;

		this.buyerProfit = (long) (this.buyerProfit*(virtualRate/10000f));

		this.gain = this.buyerProfit + this.sellerProfit;
		
		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(this.sellerProfit < 0){
			this.cost = this.cost + -this.sellerProfit;
		}else{
			roiGain = roiGain + this.sellerProfit;
		}
		
		if(this.buyerProfit<0){
			cost = cost + -this.buyerProfit;
		}else{
			roiGain = roiGain + this.buyerProfit;
		}
	
		if(cost == 0){
			this.cost = 1;
		}

		if(cost != 0){
			//TODO improve this by removing / and making a long with 10 decimal precision
			this.roi = ((float)roiGain-(float)cost)/(float)cost;
		}else{
			//TODO this will never happen
			this.roi = Float.POSITIVE_INFINITY;
		}
		
	}

	
	public long testBuyer(Trader buyer){
		
		
		//The sign of these numbers is with reference to our Bank Account

		float virtualRate = (float)(this.valueSum + buyer.getCurrencyToSell().getValue()*
				buyer.getCurrencyToSell().getType().getRateToBase())/(float)(this.weightSum+buyer.getCurrencyToSell().getValue());


		long newBuyerProfit = this.buyerProfit + buyer.getCurrencyToSell().getValue();
		long newSellerProfit = this.sellerProfit - buyer.getCurrencyToBuy().getValue();
		
		newBuyerProfit = (long) (newBuyerProfit*(virtualRate/10000f));
		
		long newGain = newBuyerProfit + newSellerProfit;
		//Compute our roi
		long newCost = 0;
		long roiGain = 0;
		
		if(newSellerProfit < 0){
			newCost = newCost + -newSellerProfit;
		}else{
			roiGain = roiGain + newSellerProfit;
		}
		
		if(newBuyerProfit<0){
			newCost = newCost + -newBuyerProfit;
		}else{
			roiGain = roiGain + newBuyerProfit;
		}
	
		if(newCost == 0){
			newCost = 1;
		}

		return newGain;
		//return ((float)roiGain-(float)newCost)/(float)newCost;
	}
	
	public long testSeller(Trader seller){
		//The sign of these numbers is with reference to our Bank Account

		float virtualRate = (float)(this.valueSum + seller.getCurrencyToBuy().getValue()*
				seller.getCurrencyToBuy().getType().getRateToBase())/(float)(this.weightSum+seller.getCurrencyToBuy().getValue());

		long newBuyerProfit = this.buyerProfit - seller.getCurrencyToBuy().getValue();
		long newSellerProfit = this.sellerProfit + seller.getCurrencyToSell().getValue();
		
		newBuyerProfit = (long) (newBuyerProfit*(virtualRate/10000f));
		
		//Compute our roi
		long newCost = 0;
		long roiGain = 0;
		
		if(newSellerProfit < 0){
			newCost = newCost + -newSellerProfit;
		}else{
			roiGain = roiGain + newSellerProfit;
		}
		
		if(newBuyerProfit<0){
			newCost = newCost + -newBuyerProfit;
		}else{
			roiGain = roiGain + newBuyerProfit;
		}
	
		if(newCost == 0){
			newCost = 1;
		}

		//return ((float)roiGain-(float)newCost)/(float)newCost;
		long newGain = newBuyerProfit + newSellerProfit;
		return newGain;
	}
	
	
	
	
	/**
	 * Create a combination and compute values
	 * for this group of buyers and sellers
	 * @param buyers
	 * @param sellers
	 * @throws Exception
	 */

	public Combination(List<Trader> buyers, List<Trader> sellers) throws Exception{
		
		valueSum = 0;
		weightSum = 0;

		this.buyers = buyers;
		this.sellers = sellers;

		//Compute currency to buy
		this.buyersCurrencyToBuy = this.buyers.get(0).getCurrencyToBuy();
		this.buyersCurrencyToSell = this.buyers.get(0).getCurrencyToSell();
		valueSum = valueSum + buyers.get(0).getCurrencyToSell().getValue()*buyers.get(0).getCurrencyToSell().getType().getRateToBase();
		weightSum = weightSum + buyers.get(0).getCurrencyToSell().getValue();
		
		for(int i=1; i<this.buyers.size(); i++){
			
			this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.
				add(this.buyers.get(i).getCurrencyToBuy());
			this.buyersCurrencyToSell = this.buyersCurrencyToSell.
				add(this.buyers.get(i).getCurrencyToSell());
			valueSum = valueSum + buyers.get(i).getCurrencyToSell().getValue()*buyers.get(i).getCurrencyToSell().getType().getRateToBase();
			weightSum = weightSum + buyers.get(i).getCurrencyToSell().getValue();
		}
		
		//Compute currency to sell
		this.sellersCurrencyToSell = this.sellers.get(0).getCurrencyToSell();
		this.sellersCurrencyToBuy = this.sellers.get(0).getCurrencyToBuy();
		valueSum = valueSum + sellers.get(0).getCurrencyToBuy().getValue() *sellers.get(0).getCurrencyToBuy().getType().getRateToBase();
		weightSum = weightSum + sellers.get(0).getCurrencyToBuy().getValue();

		
		for(int i=1; i<this.sellers.size(); i++){
			this.sellersCurrencyToSell = this.sellersCurrencyToSell.
				add(this.sellers.get(i).getCurrencyToSell());
			this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.
				add(this.sellers.get(i).getCurrencyToBuy());
			
			valueSum = valueSum + sellers.get(i).getCurrencyToBuy().getValue() *sellers.get(i).getCurrencyToBuy().getType().getRateToBase();
			weightSum = weightSum + sellers.get(i).getCurrencyToBuy().getValue();

		}
		//The sign of these numbers is with reference to our Bank Account
		this.sellerProfit = this.sellersCurrencyToSell.getValue() - this.buyersCurrencyToBuy.getValue();
		this.buyerProfit = this.buyersCurrencyToSell.getValue() - this.sellersCurrencyToBuy.getValue();

		
		float virtualRate = (float)valueSum/(float)weightSum;

		this.buyerProfit = (long) (this.buyerProfit*(virtualRate/10000f));

		this.gain = this.buyerProfit + this.sellerProfit;
		
		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(this.sellerProfit < 0){
			this.cost = this.cost + -this.sellerProfit;
		}else{
			roiGain = roiGain + this.sellerProfit;
		}
		
		if(this.buyerProfit<0){
			cost = cost + -this.buyerProfit;
		}else{
			roiGain = roiGain + this.buyerProfit;
		}
	
		if(cost == 0){
			this.cost = 1;
		}

		if(cost != 0){
			//TODO improve this by removing / and making a long with 10 decimal precision
			this.roi = ((float)roiGain-(float)cost)/(float)cost;
		}else{
			//TODO this will never happen
			this.roi = Float.POSITIVE_INFINITY;
		}
		
	}


	
	public void addBuyer(Trader buyer){
		
		this.buyers.add(buyer);
		
		//The sign of these numbers is with reference to our Bank Account

		this.valueSum = this.valueSum + buyer.getCurrencyToSell().getValue()*
			buyer.getCurrencyToSell().getType().getRateToBase();
		this.weightSum = this.weightSum+buyer.getCurrencyToSell().getValue();
			
		float virtualRate = (float)this.valueSum/(float)this.weightSum;


		this.buyerProfit = this.buyerProfit + buyer.getCurrencyToSell().getValue();
		this.sellerProfit = this.sellerProfit - buyer.getCurrencyToBuy().getValue();
		
		this.buyerProfit = (long) (this.buyerProfit*(virtualRate/10000f));
		
		this.gain = this.buyerProfit + this.sellerProfit;

		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(this.sellerProfit < 0){
			this.cost = this.cost + -this.sellerProfit;
		}else{
			roiGain = roiGain + this.sellerProfit;
		}
		
		if(this.buyerProfit<0){
			this.cost = this.cost + -this.buyerProfit;
		}else{
			roiGain = roiGain + this.buyerProfit;
		}
	
		if(this.cost == 0){
			this.cost = 1;
		}

		this.roi = ((float)roiGain-(float)this.cost)/(float)this.cost;
	}

	public void addSeller(Trader seller){
		
		this.sellers.add(seller);
		//The sign of these numbers is with reference to our Bank Account

		this.valueSum = this.valueSum +seller.getCurrencyToBuy().getValue()*
			seller.getCurrencyToBuy().getType().getRateToBase();
		this.weightSum = this.weightSum+seller.getCurrencyToBuy().getValue();
			
		float virtualRate = (float)this.valueSum/(float)this.weightSum;


		this.buyerProfit = this.buyerProfit - seller.getCurrencyToBuy().getValue();
		this.sellerProfit = this.sellerProfit + seller.getCurrencyToSell().getValue();
		
		this.buyerProfit = (long) (this.buyerProfit*(virtualRate/10000f));
		this.gain = this.buyerProfit + this.sellerProfit;

		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(this.sellerProfit < 0){
			this.cost = this.cost + -this.sellerProfit;
		}else{
			roiGain = roiGain + this.sellerProfit;
		}
		
		if(this.buyerProfit<0){
			this.cost = this.cost + -this.buyerProfit;
		}else{
			roiGain = roiGain + this.buyerProfit;
		}
	
		if(this.cost == 0){
			this.cost = 1;
		}

		this.roi = ((float)roiGain-(float)this.cost)/(float)this.cost;
	}


	/**
	 * Compare via roi
	 * @param o
	 * @return
	 */
	public int compareTo(Combination o) {
		
		if(this.roi > o.roi){
			return 1;
		}else if(this.roi < o.roi){
			return -1;
		}else{
			return 0;
		}
	}


	/**
	 * @return the gain
	 */
	public long getGain() {
		return gain;
	}


	/**
	 * @return the cost
	 */
	public long getCost() {
		return cost;
	}


	/**
	 * @return the roi
	 */
	public float getRoi() {
		return roi;
	}


	/**
	 * @return the buyers
	 */
	public List<Trader> getBuyers() {
		return buyers;
	}


	/**
	 * @return the sellers
	 */
	public List<Trader> getSellers() {
		return sellers;
	}


	/**
	 * @return the sellersCurrencyToSell
	 */
	public PyCurrency getSellersCurrencyToSell() {
		return sellersCurrencyToSell;
	}


	/**
	 * @return the sellersCurrencyToBuy
	 */
	public PyCurrency getSellersCurrencyToBuy() {
		return sellersCurrencyToBuy;
	}


	/**
	 * @return the buyersCurrencyToSell
	 */
	public PyCurrency getBuyersCurrencyToSell() {
		return buyersCurrencyToSell;
	}


	/**
	 * @return the buyersCurrencyToBuy
	 */
	public PyCurrency getBuyersCurrencyToBuy() {
		return buyersCurrencyToBuy;
	}

	public TraderGroup getBuyerAsTraderGroup() throws Exception {

		//Check to make sure that no traders are the same
		for(int i=0; i<this.buyers.size(); i++){
			for(int j=0; j<this.buyers.size(); j++){
				if(this.buyers.get(i).equals(this.buyers.get(j))){
					if(i!=j){
						throw new Exception("Two occurances of same Buyer in group!");
					}
				}
			}
		}

		
		return new TraderGroup(this.buyersCurrencyToSell,
				this.buyersCurrencyToBuy,
				this.buyers);
	}


	public TraderGroup getSellersAsTraderGroup() throws Exception {
		
		//Check to make sure that no traders are the same
		for(int i=0; i<this.sellers.size(); i++){
			for(int j=0; j<this.sellers.size(); j++){
				if(this.sellers.get(i).equals(this.sellers.get(j))){
					if(i!=j)
						throw new Exception("Two occurances of same Sellers in group!");
				}
			}
		}
		return new TraderGroup(this.sellersCurrencyToSell,
				this.sellersCurrencyToBuy,
				this.sellers);
	}
	
	
	
	
	
}
