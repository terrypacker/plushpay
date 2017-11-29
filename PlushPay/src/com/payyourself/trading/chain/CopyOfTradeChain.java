package com.payyourself.trading.chain;

import java.util.ArrayList;
import java.util.List;


/**
 * Used in a trade to link Trader Groups to 
 * form a chain of Quality
 * @author tpacker
 *
 */
public class CopyOfTradeChain {

	List<TradeLink> links;

	/**
	 * Construct an empty TradeChain
	 */
	public CopyOfTradeChain(){
		this.links = new ArrayList<TradeLink>();
	}
	
	public void addLink(TradeLink link){
		this.links.add(link);
	}
	
	public TradeLink getLink(int pos){
		return this.links.get(pos);
	}
	
	public int size(){
		return this.links.size();
	}
	
	public String toString(){
		String chain = new String();
		
		for(int i=0; i<this.links.size(); i++){
			chain = chain + this.links.get(i).toString() + "\n";
		}
		return chain;
	}
}
