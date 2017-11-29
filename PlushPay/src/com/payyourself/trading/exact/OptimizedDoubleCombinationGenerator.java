package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.List;

import com.payyourself.math.CombinationMath;

public class OptimizedDoubleCombinationGenerator<E> {

	private List<List<E>> sellerCombinations; 
	private boolean init;
	
	public OptimizedDoubleCombinationGenerator(){
		this.init = false;
	}
	
    /**
     * Compute all combinations of 2 sets
     * @param buyers
     * @param sellers
     * @return
     * @throws Exception 
     */
    public List<List<E>> getAllCombinations(List<E> buyers, List<E> sellers) throws Exception{
    	
		CombinationGenerator<E> genBuyers;
		CombinationGenerator<E> genSellers;
		//CombinationGenerator<E> genAll;
		
		
		//Find the number of combinations to compute so we can optimize 
		// our memory/object usage
		//int totalCombs = (int)CombinationMath.factorial(buyers.size());
		
		 // Approximate n! => sqrt{2*pi}  n^{n+1/2}  e^{-n}
        //int totalCombs = (int) (Math.sqrt(2*Math.PI) * Math.pow(buyers.size(), buyers.size()+.5) * Math.exp(-buyers.size()));
         
        CombinationMath math = new CombinationMath();
		//Final list of lists
        int totalCombs = (int) math.computeNumberOfPossibleCombinations(buyers.size(), sellers.size());
		List<List<E>> solution = new ArrayList<List<E>>(totalCombs);
		
		//Init the seller combs list
		//TODO compute the numberSellerCombs accurately
		int numberSellerCombs =  (int) math.computeNumberOfPossibleCombinations(sellers.size());
		this.sellerCombinations = new ArrayList<List<E>>(numberSellerCombs);
		
		//This loop gets a combination for each buyer group i -> buyers.size();
		//Then gets a combination for each seller group, size j -> seller.size()
		//At each j it combines the buyercombs and sellercombs into one list
		// and computes the number of combinations for groups of size i + j
		List<E> concat;
		
		for(int i=1; i<buyers.size()+1; i++){
			
			//Get a generator for i size group in setA
			genBuyers = new CombinationGenerator<E>(buyers,i);
			
			//List each combination of size i for group A
			for(List<E> buyerComb : genBuyers) {

				if(this.init){
					for(List<E> sellerComb : this.sellerCombinations){
						//We already have the seller combinations
						concat = new ArrayList<E>(buyerComb.size() + sellerComb.size());
						concat.addAll(sellerComb);
						concat.addAll(buyerComb);
						solution.add(concat);
					}
				}else{
					//Get a generator for groups of size j in setB 
					for(int j=1; j<sellers.size()+1; j++){
						genSellers = new CombinationGenerator<E>(sellers,j);
						
						//Take each of the sets, and produce all possible combinations
						// between the 2 sets
						for(List<E> sellerComb : genSellers){
							
							//Start with the first seller comb
							this.sellerCombinations.add(sellerComb);
							
							concat = new ArrayList<E>(sellerComb);
							concat.addAll(buyerComb); //Add the buyers to it
							solution.add(concat);
							
							/*
							concat = new ArrayList<E>(buyerComb); //Make a copy
							concat.addAll(sellerComb);  //Combine the sets
							
							//Create a generator for the new combined list
							genAll = new CombinationGenerator<E>(concat,j+i);
							
							//Add all the combinations into the final solution
							for(List<E> combAll : genAll){
								solution.add(combAll);
							}//end for combAll
							*/	
							
						}//end for sellerComb
					
					}//end for j size group of set seller
					this.init = true;
				}//end else init
				
			} //end for combA
			
			
		}//end for i size group in setA

		return solution;
    }

    
	
}

