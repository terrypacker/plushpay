package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.group.TraderGroup;


public class Combination implements Comparable<Combination>{

	private long gain; //Sum of the two
	private long cost; //Cost of Investment
	private float roi;
	
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

	public Combination(List<Trader> buyers, List<Trader> sellers) throws Exception{
		
		long valueSum = 0;
		long weightSum = 0;

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


	/**
	 * Generate a Combination by sorting traders
	 * into buyers and sellers
	 * @param list
	 * @throws Exception 
	 */
	public Combination(List<Trader> traders) throws Exception {
	
		this.buyers = new ArrayList<Trader>(traders.size()); //Extra space, but that is faster
		this.sellers = new ArrayList<Trader>(traders.size());
		
		long valueSum = 0;
		long weightSum = 0;

		
		//Buyers are buying USD
		for(int i=0; i<traders.size(); i++){
			
			if(traders.get(i).getCurrencyToBuy().getType().getCode().equals(CurrencyCodeEnum.USD)){

				//We are a buyer
				this.buyers.add(traders.get(i));
				if(this.buyersCurrencyToBuy == null){
					this.buyersCurrencyToBuy = traders.get(i).getCurrencyToBuy();
				}else{
					this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.add(traders.get(i).getCurrencyToBuy());
				}
				
				if(this.buyersCurrencyToSell == null){
					this.buyersCurrencyToSell = traders.get(i).getCurrencyToSell();
				}else{
					this.buyersCurrencyToSell = this.buyersCurrencyToSell.add(traders.get(i).getCurrencyToSell());
				}
				
				valueSum = valueSum + traders.get(i).getCurrencyToSell().getValue()*traders.get(i).getCurrencyToSell().getType().getRateToBase();
				weightSum = weightSum + traders.get(i).getCurrencyToSell().getValue();
				
			}else{
				//We are a seller
				//We are a buyer
				this.sellers.add(traders.get(i));
				if(this.sellersCurrencyToBuy == null){
					this.sellersCurrencyToBuy = traders.get(i).getCurrencyToBuy();
				}else{
					this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.add(traders.get(i).getCurrencyToBuy());
				}
				
				if(this.sellersCurrencyToSell == null){
					this.sellersCurrencyToSell = traders.get(i).getCurrencyToSell();
				}else{
					this.sellersCurrencyToSell = this.sellersCurrencyToSell.add(traders.get(i).getCurrencyToSell());
				}
				
				valueSum = valueSum + traders.get(i).getCurrencyToBuy().getValue() *traders.get(i).getCurrencyToBuy().getType().getRateToBase();
				weightSum = weightSum + traders.get(i).getCurrencyToBuy().getValue();

			}
			
		}//end for all traders
		
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


	public Combination(Trader[] buyerComb, Trader[] sellerComb) {
		// TODO Auto-generated constructor stub
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
