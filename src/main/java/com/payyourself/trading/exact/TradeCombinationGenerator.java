package com.payyourself.trading.exact;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.payyourself.trading.trader.Trader;

public class TradeCombinationGenerator {


	private List<Combination> combinations;

	private Combination best; //Find the best while we create the combinations

	private boolean logForGrapher = false;
	private BufferedWriter output;
	
	/**
	 * Generate all possible combinations
	 * @param buyers
	 * @param sellers
	 * @throws Exception
	 */
	public TradeCombinationGenerator(List<Trader> buyers, List<Trader> sellers) throws Exception{
	
		if(this.logForGrapher){
			File outfile = new File("data.txt");
			FileWriter fw = new FileWriter(outfile);
			this.output = new BufferedWriter(fw);
		}
		
		this.combinations = this.generateCombinations(buyers, sellers);


		
	}
	
	
	private List<Combination> generateCombinations(List<Trader> buyers, List<Trader> sellers) throws Exception{
		
		//First generate all the possible combinations
		OptimizedDoubleCombinationGenerator<Trader> dGen = new OptimizedDoubleCombinationGenerator<Trader>();
		//DoubleCombinationGenerator<Trader> dGen = new DoubleCombinationGenerator<Trader>();

		List<List<Trader>> traderCombs = dGen.getAllCombinations(buyers, sellers);
		
		Combination comb;
		
		//Create an arrayList for all of the combinations;
		List<Combination> combinations = new ArrayList<Combination>(traderCombs.size());
		
		this.best = new Combination(traderCombs.get(0));
		combinations.add(this.best);
		
		int y=0;
		int lastX = this.best.getBuyers().size(); 
		
		//Now find the best combination
		for(int i=1; i<traderCombs.size(); i++){
			
			comb = new Combination(traderCombs.get(i));
			combinations.add(comb);
			
			if(this.best.compareTo(comb) < 1){
				
				//We have a new best
				this.best = comb;
				
			}
			
			if(this.logForGrapher ){
			
				y = y + 1;
				if(comb.getBuyers().size() > lastX){
					lastX = comb.getBuyers().size();
					y = 1;
				}
				this.output.write(comb.getBuyers().size() + "\t" + y + "\t" + comb.getRoi() + "\n");

			}

		}
		
		if(this.logForGrapher){
			this.output.flush();
			this.output.close();

		}
		return combinations;
		
	}


	public Combination getBest() {
		return this.best;
	}


	public int size() {
		return this.combinations.size();
	}
		
	
}
