package com.payyourself.trading.exact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.payyourself.math.CombinationMath;

public class SimpleTest {

	
	public static void main(String args[]){
		
		
		List<Character> setA = Arrays.asList('A','B','C');
		List<Character> setB = Arrays.asList('F','G');


		//Number of combinations for size k = n^_k/k! where n^_k = n(n-1) * (n-2) * (n-k+1)
		
		//Estimate the total number of combinations
		//Estimate of n! = sqrt(2PI) * n^(n+.5) * e^-n
		int nFact = (int) CombinationMath.factorial(setA.size());
        int nFactEst = (int) (Math.sqrt(2*Math.PI) * Math.pow(setA.size(), setA.size()+.5) * Math.exp(-setA.size()));

        System.out.println("n!: " + nFact);
        System.out.println("n! Est: " + nFactEst);

//        System.out.println("Total Combinations: " + totalCombs);
		
		
		CombinationGenerator<Character> genA;
		CombinationGenerator<Character> genB;
		CombinationGenerator<Character> genAll;
		
		List<List<Character>> solution = new ArrayList<List<Character>>();
		
		List<Character> concat;
		
		for(int i=1; i<setA.size()+1; i++){
			
			//Get a generator for i size group in setA
			genA = new CombinationGenerator<Character>(setA,i);
			
			//List each combination of size i for group A
			for(List<Character> combA : genA) {
				
				//Get a generator for groups of size j in setB 
				for(int j=1; j<setB.size()+1; j++){
					genB = new CombinationGenerator<Character>(setB,j);
					
					//Take each of the sets, combine them and produce all possible combinations
					// of those
					for(List<Character> combB : genB){
						
						concat = new ArrayList<Character>(combA);
						concat.addAll(combB);  //Combine the sets
						//Get a generator for k size group in set concat
						//for(int k=1; k<concat.size()+1; k++){
						//	genAll = new CombinationGenerator<Character>(concat,k);
						//	
						//	//Add all the combinations into the final solution
						//	for(List<Character> combAll : genAll){
						//		solution.add(combAll);
						//	}//end for combAll
						//	
						//}//end for k size group of combinations
						
						genAll = new CombinationGenerator<Character>(concat,j+i);
						//Add all the combinations into the final solution
						for(List<Character> combAll : genAll){
							solution.add(combAll);
						}//end for combAll
							
						
					}//end for combB
				
				}//end for j size group of set B
			} //end for combA
			
			
		}//end for i size group in setA
		
	
		for(int i=0; i<solution.size(); i++){
			System.out.print("Solution(" + i + "):"); 
			for(int j=0; j<solution.get(i).size(); j++){
				
				System.out.print(solution.get(i).get(j));
				
			}
			System.out.print("\n");
		}
	
	}
	
	
	
}
