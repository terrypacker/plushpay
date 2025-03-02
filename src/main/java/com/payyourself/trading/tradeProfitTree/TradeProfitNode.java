package com.payyourself.trading.tradeProfitTree;

import com.payyourself.trading.trader.group.TraderGroupUtil;

public class TradeProfitNode implements Comparable<TradeProfitNode>{
	

	//Groups
	private TraderGroupUtil buyers;
	private TraderGroupUtil sellers;
	
	//Links
	private TradeProfitNode leftNode;
	private TradeProfitNode rightNode;
	private TradeProfitNode parent;
	
	//Key
	private TradeProfitKey key;
	
	
	/**
	 * Constructor
	 * @param buyers
	 * @param sellers
	 */
	public  TradeProfitNode(TraderGroupUtil buyers, TraderGroupUtil sellers){
		this.buyers = buyers;
		this.sellers = sellers;
		
		//The sign of these numbers is with reference to our Bank Account
		long buyerBuy = this.sellers.getCurrencyToSell().getValue() - this.buyers.getCurrencyToBuy().getValue();
		long buyerSell = this.buyers.getCurrencyToSell().getValue() - this.sellers.getCurrencyToBuy().getValue();
		
		this.key =  new TradeProfitKey(buyerBuy,buyerSell);
	}
	


	/**
	 * Create a string representation of the object.
	 */
	public String toString(){
		String newString = "Number Sellers: " + this.sellers.size() + "\n";
		newString = newString + "Number Buyers: "+ this.buyers.size() + "\n";
		newString = newString + this.key.toString() + "\n";
		return newString;
	}



	
	/**
	 * Compare the keys of the nodes
	 * 
	 * Return 1 if > than
	 * Return -1 if less than
	 * Return 0 if equal
	 */
	public int compareTo(TradeProfitNode node) {

		return this.key.compareTo(node.getKey());
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyers == null) ? 0 : buyers.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((leftNode == null) ? 0 : leftNode.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result
				+ ((rightNode == null) ? 0 : rightNode.hashCode());
		result = prime * result + ((sellers == null) ? 0 : sellers.hashCode());
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
		TradeProfitNode other = (TradeProfitNode) obj;
		if (buyers == null) {
			if (other.buyers != null)
				return false;
		} else if (!buyers.equals(other.buyers))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (leftNode == null) {
			if (other.leftNode != null)
				return false;
		} else if (!leftNode.equals(other.leftNode))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (rightNode == null) {
			if (other.rightNode != null)
				return false;
		} else if (!rightNode.equals(other.rightNode))
			return false;
		if (sellers == null) {
			if (other.sellers != null)
				return false;
		} else if (!sellers.equals(other.sellers))
			return false;
		return true;
	}



	public void setParent(TradeProfitNode parent) {
		this.parent = parent;
	}

	public TradeProfitNode getParent() {
		return parent;
	}
	public void setKey(TradeProfitKey key) {
		this.key = key;
	}

	public TradeProfitKey getKey() {
		return key;
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

	public TradeProfitNode getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(TradeProfitNode leftNode) {
		this.leftNode = leftNode;
	}

	public TradeProfitNode getRightNode() {
		return rightNode;
	}

	public void setRightNode(TradeProfitNode rightNode) {
		this.rightNode = rightNode;
	}
}
