package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.List;

public class DoubleCombinationGenerator<E> {

	
	public DoubleCombinationGenerator(){
		
	}
	
    /**
     * Compute all combinations of 2 sets
     * @param buyers
     * @param sellers
     * @return
     */
    public List<List<E>> getAllCombinations(List<E> buyers, List<E> sellers){
    	
		CombinationGenerator<E> genBuyers;
		CombinationGenerator<E> genSellers;
		CombinationGenerator<E> genAll;
		
		
		//Find the number of combinations to compute so we can optimize 
		// our memory/object usage
		//int totalCombs = (int)CombinationMath.factorial(buyers.size());
		
		 // Approximate n! => sqrt{2*pi}  n^{n+1/2}  e^{-n}
         
        int totalCombs = (int) (Math.sqrt(2*Math.PI) * Math.pow(buyers.size(), buyers.size()+.5) * Math.exp(-buyers.size()));
         
		//Final list of lists
		List<List<E>> solution = new ArrayList<List<E>>(totalCombs);
		
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

				//Get a generator for groups of size j in setB 
				for(int j=1; j<sellers.size()+1; j++){
					genSellers = new CombinationGenerator<E>(sellers,j);
					
					//Take each of the sets, and produce all possible combinations
					// between the 2 sets
					for(List<E> sellerComb : genSellers){
						
						//Start with the first seller comb
						concat = new ArrayList<E>(sellerComb);
						concat.addAll(buyerComb); //Add the buyers to it
						solution.add(concat);
						
						/*
						//TODO THIS IS WRONG! as it generates some groups that would be only buyers or sellers
						concat = new ArrayList<E>(buyerComb); //Make a copy
						concat.addAll(sellerComb);  //Combine the sets
						
						//Create a generator for the new combined list
						genAll = new CombinationGenerator<E>(concat,j+i);
						
						//Add all the combinations into the final solution
						for(List<E> combAll : genAll){
							solution.add(combAll);
						}//end for combAll
						*/	
						
					}//end for combB
				
				}//end for j size group of set B
			} //end for combA
			
			
		}//end for i size group in setA

		return solution;
    }

    
	
}
