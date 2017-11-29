package com.payyourself.trading.greedy;

import java.util.List;

import com.payyourself.trading.trader.Trader;

public class GreedySolver {

	private List<Trader> buyers;
	private List<Trader> sellers;
	
	public GreedySolver(List<Trader> currentBuyers, List<Trader> currentSellers) {
		
		this.buyers = currentBuyers;
		this.sellers = currentSellers;
		
	}

	
	public Combination solve() throws Exception {

		Combination best = new Combination(buyers.get(0),sellers.get(0));

		this.buyers.remove(0);
		this.sellers.remove(0);
		
		int size = 100;
		long newValue = 0;
		long bestValue = 0;
		int bestPos = 0;
		
		for(int i=0; i<size; i++){
			
			if(this.buyers.size() == 0){
				break;
			}
			
			bestValue = best.testBuyer(this.buyers.get(0));
			bestPos = 0;
			for(int j=1; j<this.buyers.size(); j++){
				
				newValue = best.testBuyer(this.buyers.get(j));
				if(bestValue < newValue){
					bestPos = j;
					bestValue = newValue;
				}
			}

			System.out.println("Buyer Old: " + best.getRoi() + " New: " + newValue);
			//Add the best
			best.addBuyer(this.buyers.get(bestPos));
			this.buyers.remove(bestPos);

			if(this.sellers.size() == 0)
				break;
			
			bestValue =  best.testSeller(sellers.get(0));
			bestPos = 0;
			for(int k=1; k<this.sellers.size(); k++){
				//Try a seller and add it if it improves the situation
				newValue =  best.testSeller(sellers.get(k));
				if(bestValue <newValue){
					bestPos = k;
					bestValue = newValue;
					
				}
			}
			System.out.println("Seller Old: " + best.getRoi() + " New: " + newValue);

			//Add the best
			best.addSeller(sellers.get(bestPos));
			this.sellers.remove(bestPos);

		}
		
		return best;
		
		
	}
	
	
	public Combination solveBasic() throws Exception {

		Combination best = new Combination(buyers.get(0),sellers.get(0));
		
		int size = 0;
		if(this.buyers.size() > this.sellers.size()){
			size = this.sellers.size();
		}else{
			size = this.buyers.size();
		}
		for(int i=1; i<size; i++){
			
			if(best.getRoi() < best.testBuyer(this.buyers.get(i))){
				best.addBuyer(this.buyers.get(i));
			}
			
			//Try a seller and add it if it improves the situation
			if(best.getRoi() < best.testSeller(sellers.get(i))){
				best.addSeller(sellers.get(i));
			}
			
		}
		
		
		return best;
		
		
	}

}
