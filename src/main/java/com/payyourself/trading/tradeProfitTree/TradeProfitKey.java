package com.payyourself.trading.tradeProfitTree;


public class TradeProfitKey implements Comparable<TradeProfitKey>{
	

	//With reference to our bank account, Bought dollars are withdrawn and sold dollars are deposited.
	private long buyerBuyTotal;  //-Buyer Buy total + Seller Sell Total 
	private long buyerSellTotal; //Buyer Sell total - Seller buy total
	
	private long gain; //Sum of the two
	private long cost; //Cost of Investment
	private float roi; //Return on Investment = ((Gain on invest)-(cost of invest))/(cost of invest)
	
	/**
	 * 
	 * @param buy - Buyer Buying Currency Gain with reference to our bank account
	 * @param sell - Seller Sell currency Gain with reference to our bank account
	 */
	public TradeProfitKey(long buy, long sell){
		
		//Set the values
		this.buyerBuyTotal = buy;
		this.buyerSellTotal = sell;
	
		//Set our gain
		this.gain = buy + sell;
		
		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(buy < 0){
			this.cost = this.cost + -buy;
		}else{
			roiGain = roiGain + buy;
		}
		
		if(sell<0){
			cost = cost + -sell;
		}else{
			roiGain = roiGain + sell;
		}
	
		if(cost == 0){
			this.cost = 1;
		}

		if(cost != 0){
			//TODO improve this by removing / and making a long with 10 decimal precision
			this.roi = ((float)roiGain-(float)cost)/(float)cost;
		}else{
			this.roi = Float.POSITIVE_INFINITY;
		}
	
	}
	
	/**
	 * Compare 2 profit keys
	 * 
	 *  Return -1 if this > than
	 *  Return 1 if this less than
	 *  Return 0 if this equal
	 *  
	 * 
	 *  INVERSE OF THIS LOGIC
	 *  Return 1 if this > than
	 *  Return -1 if this less than
	 *  Return 0 if this equal
	 *  
	 *  Doing the inverse now (to order tree as largest) 
	 *  
	 */
	public int compareTo(TradeProfitKey key) {
		
		//Compare the roi's
		if(this.roi > key.getRoi()){
			return -1;
		}else if(this.roi < key.getRoi()){
			return 1;
		}else{
			
			//Compare the gains
			if(this.gain > key.getGain()){
				if(this.cost < key.getCost())
					return -1;
				else
					return 1;
			}else if(this.gain < key.getGain()){
				return 1;
			}else{
				if(this.cost < key.getCost())
					return -1;
				else if(this.cost > key.getCost())
					return 1;
				else
					return 0;
			}
			
		}
		
		
	}
		
	/**
	 * Compare 2 profit keys
	 * 
	 *  Return 1 if this > than
	 *  Return -1 if this less than
	 *  Return 0 if this equal
	 *  
	 *  Doing the inverse now (to order tree as largest 
	 *  
	 */
	public int compareToOLD2(TradeProfitKey key) {
		//Compare the roi's
		if(this.roi > key.getRoi()){
			return -1;
		}else if(this.roi < key.getRoi()){
			return 1;
		}else{
			
			//Compare the gains
			if(this.gain > key.getGain()){
				return -1;
			}else if(this.gain < key.getGain()){
				return 1;
			}else{
				return 0;
			}
			
		}
		
		
		
	}
	
	/**
	 * Compare 2 profit keys
	 * 
	 *  Return 1  to place key before this
	 *  Return -1 to place key after this
	 *  Return 0 if equal
	 *  
	 *  We want to have the largest gain
	 *  as the first entry in the tree,
	 *  so we will invert the normal compareTo logic 
	 *  
	 */
	public int compareToOLD(TradeProfitKey key) {
		
			//Compare the gains
			if(this.gain > key.gain){
				return -1; //Place key after this
			}else if(this.gain < key.gain){
				return 1; //Place key before this
			}else{
				//They are same compare costs
				if(this.cost > key.cost){
					return 1; //Place key before this
				}else if(this.cost < key.cost){
					return -1; //Place key after this
				}else{
					return 0; //Same
				}
			}
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (buyerBuyTotal ^ (buyerBuyTotal >>> 32));
		result = prime * result
				+ (int) (buyerSellTotal ^ (buyerSellTotal >>> 32));
		result = prime * result + (int) (gain ^ (gain >>> 32));
		result = prime * result + Float.floatToIntBits(roi);
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
		TradeProfitKey other = (TradeProfitKey) obj;
		if (buyerBuyTotal != other.buyerBuyTotal)
			return false;
		if (buyerSellTotal != other.buyerSellTotal)
			return false;
		if (gain != other.gain)
			return false;
		if (Float.floatToIntBits(roi) != Float.floatToIntBits(other.roi))
			return false;
		return true;
	}

	/**
	 * 
	 */
	public String toString(){
		String out = "BuyerBuyTotal: " + this.buyerBuyTotal + "\n";
		out = out + "BuyerSellTotal: " + this.buyerSellTotal + "\n";
		out = out + "Cost: " + this.cost + "\n";
		out = out + "Gain: " + this.gain + "\n";
		out = out + "Roi: " + this.roi + "\n";
		return out;
	}

	public long getGain() {
		return gain;
	}

	public void setGain(long gain) {
		this.gain = gain;
	}
	
	public float getRoi() {
		return roi;
	}
	
	public void setRoi(float roi) {
		this.roi = roi;
	}

	public long getBuyerBuyTotal() {
		return buyerBuyTotal;
	}

	public void setBuyerBuyTotal(long buyerBuyTotal) {
		this.buyerBuyTotal = buyerBuyTotal;
	}

	public long getBuyerSellTotal() {
		return buyerSellTotal;
	}

	public void setBuyerSellTotal(long buyerSellTotal) {
		this.buyerSellTotal = buyerSellTotal;
	}

	/**
	 * @return the cost
	 */
	public long getCost() {
		return cost;
	}

	

}
