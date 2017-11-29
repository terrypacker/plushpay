package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.math.CombinationMath;
import com.payyourself.trading.trader.Trader;

public class OptimizedTradeCombinationGenerator {

	private static final float SELLER_DECIMATION = .80f;
	private static final float BUYER_DECIMATION = .80f;
	private List<List<Trader>> sellerCombinations;
	private List<Trader> buyers;
	private List<Trader> sellers;
	private boolean init;
	private Combination best;
	private int size; 
		
	public OptimizedTradeCombinationGenerator(List<Trader> buyers, List<Trader> sellers){
		this.init = false;
		this.buyers = buyers;
		this.sellers = sellers;
		this.size = 0;
	}
	
    /**
     * Compute all combinations of 2 sets
     * @param buyers
     * @param sellers
     * @return
     * @throws Traderxception 
     */
    public Combination getBest() throws Exception{
    	
		CombinationGenerator<Trader> genBuyers;
		CombinationGenerator<Trader> genSellers;
         
        CombinationMath math = new CombinationMath();
		//Final list of lists
        //int totalCombs = (int) math.computeNumberOfPossibleCombinations(buyers.size(), sellers.size());
		
		//Init the seller combs list
        //2,147,483,647 max size of an integer
        long longSellerCombs = math.computeNumberOfPossibleCombinations(sellers.size());
        if(longSellerCombs > 2147483647){
        	throw new Exception("Too many combinations to use an int.");
        }
        
		int numberSellerCombs = (int) (longSellerCombs*(1-OptimizedTradeCombinationGenerator.SELLER_DECIMATION));
		this.sellerCombinations = new ArrayList<List<Trader>>(numberSellerCombs);
		
		Combination comb;
		
		//This loop gets a combination for each buyer group i -> buyers.size();
		//Then gets a combination for each seller group, size j -> seller.size()
		//At each j it combines the buyercombs and sellercombs into one list
		// and computes the number of combinations for groups of size i + j
		
		int numBuyers = (int) (buyers.size()*OptimizedTradeCombinationGenerator.BUYER_DECIMATION);
		
		int numSellers = (int) (this.sellers.size()*OptimizedTradeCombinationGenerator.SELLER_DECIMATION);
		
		for(int i=this.buyers.size(); i>numBuyers; i--){
			
			//Get a generator for i size group in setA
			genBuyers = new CombinationGenerator<Trader>(buyers,i);
			
			//List each combination of size i for group A
			for(List<Trader> buyerComb : genBuyers) {

				if(this.init){
					for(List<Trader> sellerComb : this.sellerCombinations){
						this.size++;
						//We already have the seller combinations
						comb = new Combination(buyerComb,sellerComb);
						
						if(this.best.compareTo(comb) < 1){
							
							//We have a new best
							this.best = comb;
							
						}

						
					}
				}else{
					//Get a generator for groups of size j in setB 
					for(int j=sellers.size(); j>numSellers; j--){
						genSellers = new CombinationGenerator<Trader>(sellers,j);
						
						//Take each of the sets, and produce all possible combinations
						// between the 2 sets
						for(List<Trader> sellerComb : genSellers){
							this.size++;
							//Start with the first seller comb
							this.sellerCombinations.add(sellerComb);
							
							comb = new Combination(buyerComb,sellerComb);
							
							if(this.best == null){
								this.best = comb;
							}else if(this.best.compareTo(comb) < 1){
								
								//We have a new best
								this.best = comb;
								
							}

							
							
						}//end for sellerComb
					
					}//end for j size group of set seller
					this.init = true;
				}//end else init
				
			} //end for combA
			
			
		}//end for i size group in setA

		return this.best;
    }

	public int size() {
		return this.size;
	}

    
	
}