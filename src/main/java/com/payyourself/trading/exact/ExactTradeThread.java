package com.payyourself.trading.exact;

import java.util.List;

import com.payyourself.trading.trader.Trader;

public class ExactTradeThread extends Thread {

	private List<Trader> buyers;
	private List<Trader> sellers;
	private Combination best;
	
	private boolean running;
	
	public ExactTradeThread(List<Trader> buyers, List<Trader> sellers) throws Exception{
		this.buyers = buyers;
		this.sellers = sellers;
		this.running = false;
	}
	
	
	public void startThread(){
		
		this.running = true;
		this.start();
		
	}
	
	
	public void run(){
	
		try {
			TradeCombinationGenerator gen = new TradeCombinationGenerator(buyers,sellers);
			this.best = gen.getBest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		this.running = false;
		
	}
	
	
	public Combination getBest(){
		return this.best;
	}


	public boolean isRunning() {
		return this.running;
	}
	
}
