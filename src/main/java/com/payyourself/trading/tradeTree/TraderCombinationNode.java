package com.payyourself.trading.tradeTree;

import com.payyourself.trading.trader.group.TraderGroupUtil;

public class TraderCombinationNode implements Comparable<Object>{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (totalMismatch ^ (totalMismatch >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraderCombinationNode other = (TraderCombinationNode) obj;
		if (totalMismatch != other.totalMismatch)
			return false;
		return true;
	}


	//Groups
	private TraderGroupUtil buyers;
	private TraderGroupUtil sellers;
	
	//Links
	private TraderCombinationNode leftNode;
	private TraderCombinationNode rightNode;
	private TraderCombinationNode parent;
	
	//Value of node, this represents the mismatch to minimize but not really the true value of dollars in any
	// currency.
	private long totalMismatch; 
	
	
	public  TraderCombinationNode(TraderGroupUtil buyers, TraderGroupUtil sellers){
		this.buyers = buyers;
		this.sellers = sellers;
		
		this.totalMismatch = (Math.abs(buyers.getCurrencyToBuy().getValue()-sellers.getCurrencyToSell().getValue()) +
								Math.abs(buyers.getCurrencyToSell().getValue() - sellers.getCurrencyToBuy().getValue()));
		
	}
	
	public TraderGroupUtil getBuyers() {
		return buyers;
	}



	public void setBuyers(TraderGroupUtil buyers) {
		this.buyers = buyers;
	}



	public TraderGroupUtil getSellers() {
		return sellers;
	}



	public void setSellers(TraderGroupUtil sellers) {
		this.sellers = sellers;
	}



	public TraderCombinationNode getLeftNode() {
		return leftNode;
	}



	public void setLeftNode(TraderCombinationNode leftNode) {
		this.leftNode = leftNode;
	}



	public TraderCombinationNode getRightNode() {
		return rightNode;
	}



	public void setRightNode(TraderCombinationNode rightNode) {
		this.rightNode = rightNode;
	}


	public long getTotalMismatch() {
		return totalMismatch;
	}

	public float getTotalMismatchInDollars(){
		return (float)totalMismatch/10000f;
	}
	
	public String toString(){
		String newString = "Number Sellers: " + this.sellers.size() + "\n";
		newString = newString + "Number Buyers: "+ this.buyers.size() + "\n";
		newString = newString + "Total Mismatch: " + this.totalMismatch + "\n";
		return newString;
	}

	public void setParent(TraderCombinationNode parent) {
		this.parent = parent;
	}

	public TraderCombinationNode getParent() {
		return parent;
	}

	
	/**
	 * Return 1 if > than
	 * Return -1 if less than
	 * Return 0 if equal
	 */
	public int compareTo(Object o) {
		
		TraderCombinationNode node = (TraderCombinationNode)o;
		
		return (int)(this.totalMismatch - node.getTotalMismatch());
	}
	
	

}
