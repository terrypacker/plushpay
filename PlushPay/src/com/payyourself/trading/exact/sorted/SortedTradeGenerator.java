package com.payyourself.trading.exact.sorted;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.trading.trader.Trader;

public class SortedTradeGenerator {

	private List<Trader> buyers;
	private List<Trader> sellers;
	
	
	
	/**
	 * The lists should be sorted with high rates and values to low
	 * @param buyers
	 * @param sellers
	 */
	public SortedTradeGenerator(List<Trader> buyers, List<Trader> sellers) {

	
		this.buyers = buyers;
		this.sellers = sellers;
	
		
		
		
	
	}

	public Combination getBest() throws Exception {
		

		//Sort the lists to get groups with differing rates
		List<Trader> bestBuyers = new ArrayList<Trader>();
		List<Trader> bestSellers = new ArrayList<Trader>();
		
		long ss = 0;
		long sb = 0;
		
		long bb = 0;
		long bs = 0;
		
		long gUsd = ss - bb;
		long gAud = bs - sb;
		
		long tmpSs,tmpBb,tmpBs,tmpSb;
		
		long tmpGusd,tmpGaud;

		
		//The dynamic programming solution is to find the best match each time
		if(this.buyers.size() > this.sellers.size()){
			
			for(int i=this.buyers.size()-2; i>0; i--){
				
				tmpBs = this.buyers.get(i).getCurrencyToSell().getValue();
				tmpBb = this.buyers.get(i).getCurrencyToBuy().getValue();
				
				for(int j=0; j<this.sellers.size(); j++){
					tmpSb = this.sellers.get(j).getCurrencyToBuy().getValue();
					tmpSs = this.sellers.get(j).getCurrencyToSell().getValue();
					
					tmpGusd = gUsd + tmpSs - tmpSb;
					tmpGaud = gAud + tmpBs - tmpSb;
					//Find the best match for seller(i)
					if((tmpGusd > gUsd)&&(tmpGaud > gAud)){
						gUsd = gUsd + tmpGusd;
						gAud = gAud + tmpGaud;
						
						bestSellers.add(this.sellers.get(j));
						bestBuyers.add(this.buyers.get(i));
						
						break;
					}
					
					
				}
				
			}
			
		}else{
			int k= this.sellers.size()-2;
			for(int i=1; i<this.buyers.size(); i++){
				
				
			}
		}

		return null;
		
	}

	public int size() {
		
		return 0;
	}

}
