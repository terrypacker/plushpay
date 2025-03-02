package com.payyourself.trading.chain;

import com.payyourself.trading.trader.group.TraderGroupUtil;

/**
 * A Trade link is a link of Trader Groups that can be used 
 * to make a trade chain.
 * 
 * 
 * @author tpacker
 *
 */
public class CopyOfTradeLink {

	TraderGroupUtil buyers;
	TraderGroupUtil sellers;
	float mismatch;
	
	public CopyOfTradeLink(TraderGroupUtil buyers, TraderGroupUtil sellers){
		this.buyers = buyers;
		this.sellers = sellers;
		
		//Removed to avoid showing an error in eclipse while we wait for this.
		//this.mismatch = Math.abs(this.buyers.getCurrencyToSell().getValue() -  this.sellers.getBaseTotalToSell());
	}
	
	/**
	 * Create a string representation of a TradeLink;
	 */
	public String toString(){
		String link = new String();
		link = "Buyers:\n" + this.buyers.toString() + "\n";
		link = link + "Sellers:\n" + this.sellers.toString() + "\n";
		link = link + "Mismatch: " + this.mismatch + "\n";
		return link;
	}
	
	public TraderGroupUtil getBuyers(){
		return this.buyers;
	}
	
	public TraderGroupUtil getSellers(){
		return this.sellers;
	}

	public float getMismatch() {
		return mismatch;
	}

	public void setMismatch(float mismatch) {
		this.mismatch = mismatch;
	}

	public void setBuyers(TraderGroupUtil buyers) {
		this.buyers = buyers;
	}

	public void setSellers(TraderGroupUtil sellers) {
		this.sellers = sellers;
	}
}
