package com.payyourself.trading.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.payyourself.log.LogfileFactory;
import com.payyourself.trading.trader.Trader;

public class Population {

	private static final int NUM_PARENTS = 2;
	private List<Creature> creatures;
	
	//Tuning Parameters
	private float unfitThreshold; //level of fitness required to be a 'fit' creature
	private float mutationRatio; //Percentage of population to mutate
	private float unfitSelectionRatio; //Percentage of selectionRatio that should be below the unfitThreshold
	private int maxPopulationSize;
	private int minPopulationSize; 
	
	private Random fate; //Some random fate factor for mutations
	
	private List<Trader> availableBuyers;
	private List<Trader> availableSellers;
	
	private Creature best;
	
	private Logger log;
	
	public Population(int maxPopulationSize){
		
		this.maxPopulationSize = maxPopulationSize;
		this.minPopulationSize = 9000;
		
		this.creatures = new ArrayList<Creature>();
		
		this.availableBuyers = new ArrayList<Trader>();
		this.availableSellers = new ArrayList<Trader>();
		
		this.unfitThreshold = .1f; //Larger = more removed
		this.mutationRatio = .001f; //Larger = more mutations
		this.unfitSelectionRatio = .8f; //Larger more unfit will be removed
		
		this.fate = new Random();
		
		this.log = LogfileFactory.getHTMLLogger(this.getClass());

		
	}
	
	/**
	 * Mate the population
	 * to produce 2 children for every
	 * 2 parents
	 * @throws Exception 
	 */
	public void crossoverAll() throws Exception{
		
		List<Creature> kids = new ArrayList<Creature>();
		
		//Try adding the best into the population several times
		for(int i=0; i<10; i++){
			this.creatures.add(this.best);
		}
		
		for(int i=1; i<this.creatures.size(); i = i + Population.NUM_PARENTS){
			kids.addAll(this.creatures.get(i).breedBySplice(this.creatures.get(i-1)));
		
		}
		
		this.log.info("Created " + kids.size() + " new creatures.");
		
		//TODO improve the efficiency of this
		this.creatures.addAll(kids);
		
	}
	
	

	/**
	 * Mate the population
	 * to produce 2 children for every
	 * 2 parents
	 * @throws Exception 
	 */
	public void crossoverFittest() throws Exception{
		
		List<Creature> kids = new ArrayList<Creature>();
		
		int numCrossovers = this.creatures.size()/3; //Breed 1/3 of population

		int bestPos = 0;
		int secondBestPos = 0;
		
		//Cycle through population and select the fitest members to breed
		for(int i=0; i<numCrossovers; i++){

			//Start with the first 2
			
			if(this.creatures.get(1).getFitness() > creatures.get(0).getFitness()){
				bestPos = 1;  
				secondBestPos = 0;
			}else{
				secondBestPos = 1;
				bestPos = 0;
			}
			
			//Only breed the best
			for(int j=2; j<this.creatures.size(); j++){
				
				if(this.creatures.get(j).getFitness() > creatures.get(bestPos).getFitness()){
					secondBestPos = bestPos;
					bestPos = j;
				
				}else if(this.creatures.get(j).getFitness() > creatures.get(secondBestPos).getFitness()){
					secondBestPos = j;
				}
			}
			
			//Breed them and move them to the new population
			kids.addAll(this.creatures.get(bestPos).breed(this.creatures.get(secondBestPos)));
			
			kids.add(this.creatures.get(bestPos));
			kids.add(this.creatures.get(secondBestPos));
			
			//When we do this the list will shift so
			if(bestPos>secondBestPos){
				this.creatures.remove(bestPos);
				this.creatures.remove(secondBestPos);
			}else{
				this.creatures.remove(secondBestPos);
				this.creatures.remove(bestPos);
			}
			
			
		}
		
		
		this.log.info("Created " + kids.size() + " new creatures.");
		
		//TODO improve the efficiency of this
		this.creatures.addAll(kids);
		
	}

	/**
	 * Select some of the population 
	 * to live and some to die
	 * 
	 * Return the percentage of the population
	 * that is fit.
	 * 
	 */
	public float selection(){
		
		int killed = 0;
		
		int totalFit = 0;
		
		for(int i=0; i<this.creatures.size(); i++){
			if(this.creatures.get(i).getFitness() < this.unfitThreshold){

				//Remove some of the unfit creatures
				if(this.fate.nextFloat() < this.unfitSelectionRatio){
					
					//We need a population of at least 2 to keep breeding
					if(this.creatures.size() > 2){
						this.creatures.remove(i);
						i--; //We don't want to skip any
						killed++;
					}
				}
			}else{
				totalFit++; //Increment the total number of fit creatures
			}
		}
		
		this.log.info("Killed " + killed + " creatures.");
		
		return (float)totalFit/(float)this.creatures.size();
		
	}
	
	public void evolve() throws Exception{
		
		//We can't evolve anything without an initial population
		if(this.creatures.size() == 0){
			
			//If either buyers or sellers are 0 we can't do this
			if((this.availableBuyers.size() == 0 )||(this.availableSellers.size()==0))
				return;
			
			//More Sellers
			if(this.availableSellers.size() > this.availableBuyers.size()){
				while(this.availableBuyers.size() > 0){
					Creature newCreature = new Creature
						(this.availableBuyers.get(0),this.availableSellers.get(0));
					this.creatures.add(newCreature);
					this.availableBuyers.remove(0);
					this.availableSellers.remove(0);
				}
			}else{
				//more buyers
				while(this.availableSellers.size() > 0){
					Creature newCreature = new Creature
					(this.availableBuyers.get(0),this.availableSellers.get(0));
				this.creatures.add(newCreature);
				this.availableBuyers.remove(0);
				this.availableSellers.remove(0);
					
				}

			}
			
			//Get any creature and call it the best for now
			//it will be replaced at end of this function anyway.
			this.best = new Creature(this.creatures.get(0)); 
			
		}//end if no population
		
		//Do the evolution of the existing population
		
		//TODO include all operations in one large loop to save time.
		
		//Control size of population
		this.populationControl();
		
		
		//Assess the fitness and kill unfit members
		float populationFitness = this.selection();
		
		
		//Breed the population
		this.crossoverAll();
		
		//Mutate the population
		this.mutate();
		
		//Save the best one as to never mutate or kill it (Outside of population)
		this.findBest();
		
	}

	/**
	 * Find and make a copy of the best creature
	 */
	private void findBest() {
		
		boolean foundBest = false;
		
		//Loop over all creatures and find the best one
		for(int i=0; i<this.creatures.size(); i++){
			if(this.creatures.get(i).getFitness() > this.best.getFitness()){
				this.best = this.creatures.get(i); //Save the location
			}
			
			if(this.creatures.get(i).equals(this.best)){
				foundBest = true;
			}
		}
		
		//Make sure that the best solution stays in the population
		if(!foundBest){
			this.creatures.add(this.best);
		}
		//Make a copy so we don't change it later on
		this.best = new Creature(this.best);
		
	}

	/**
	 * Mutate the population using all available buyers and sellers
	 * @throws Exception 
	 */
	private void mutate() throws Exception {
		
		
		//TODO create a way to add buyers or sellers when lists are same size
		
		if(this.availableBuyers.size() > this.availableSellers.size()){
			//More Buyers
			while(this.availableSellers.size() > 0){
				this.mutate(this.availableBuyers.get(0), this.availableSellers.get(0));
				this.availableBuyers.remove(0);
				this.availableSellers.remove(0);
			}
		}else{
			//More Sellers
			while(this.availableBuyers.size() > 0){
				this.mutate(this.availableBuyers.get(0),this.availableSellers.get(0));
				this.availableBuyers.remove(0);
				this.availableSellers.remove(0);
			
			}
		}
		
	}

	/**
	 * Add new buyers and sellers to 
	 * some of the population
	 * @throws Exception 
	 */
	private void mutate(Trader buyer, Trader seller) throws Exception{
		
		int mutations = 0;
		
		for(int i=0; i<this.creatures.size(); i++){
			if(this.fate.nextFloat()< this.mutationRatio){
				this.creatures.get(i).mutate(buyer,seller);
				mutations ++;
			}
		}		
		
		this.log.info("Mutated " + mutations + " creatures.");
	}

	public void populationControl(){
		
		//To avoid blowout
		if(this.creatures.size() > this.maxPopulationSize * 3){
			
			this.unfitThreshold = this.best.getFitness()/((float)this.creatures.size()/(float)this.maxPopulationSize);
			this.log.info("Population Blowout Detected, jacking up unfit threshold to: " + this.unfitThreshold);
			return;
		}
		
		//Do we need to have less restrictive threshold
		if(this.creatures.size() > this.maxPopulationSize){
			
			float inc = ((float)this.creatures.size() - (float)this.maxPopulationSize)/(float)this.maxPopulationSize;
			
			
			
			this.unfitThreshold = this.unfitThreshold + inc;
			this.log.info("Incrementing unfit Threshold by: " + inc);
		}
		
		if(this.creatures.size() < this.minPopulationSize){
			
			float inc = ((float)this.minPopulationSize - (float)this.creatures.size())/(float)this.minPopulationSize;
			
			if(this.unfitThreshold > -1){
				this.unfitThreshold = this.unfitThreshold - inc;

				this.log.info("Decrementing unfit Threshold by: " + inc);
			}
			
			if(this.unfitThreshold < -1){
				this.unfitThreshold = -1f;
			}
		}
		
		this.log.info("Unfit Threshold: " + this.unfitThreshold);
		
	}

	/**
	 * Return the fittest creature
	 */
	public Creature getBest(){
		
		return best;
	}

	public int size() {
		return this.creatures.size();
	}

	public void addAvailableBuyers(List<Trader> newBuyers) {
		this.availableBuyers.addAll(newBuyers);
		
	}

	public void addAvaiableSellers(List<Trader> newSellers) {
		this.availableSellers.addAll(newSellers);
		
	}
	
	
	
}
