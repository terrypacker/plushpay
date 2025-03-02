/**
 * 
 */
package com.payyourself.trading.tradeGenerator;

import java.util.List;

import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;

/**
 * @author tpacker
 *
 */
public class TradeGeneratorTraders {
	
	private List<Trader> freeTraders;
	private List<Trader> allTraders;
	
	public TradeGeneratorTraders() {
		
		TraderHibernation th = new TraderHibernation();
		this.freeTraders = th.loadFreeTraders();
		this.setAllTraders(th.loadAll());
		
	}

	public void setFreeTraders(List<Trader> freeTraders) {
		this.freeTraders = freeTraders;
	}

	public List<Trader> getFreeTraders() {
		return freeTraders;
	}
	
	public int getNumFreeTraders(){
		return this.freeTraders.size();
	}
	
	public int getNumAllTraders(){
		return this.allTraders.size();
	}

	public void setAllTraders(List<Trader> allTraders) {
		this.allTraders = allTraders;
	}

	public List<Trader> getAllTraders() {
		return allTraders;
	}
	
}
