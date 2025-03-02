package com.payyourself.trading.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.payyourself.log.LogfileFactory;
import com.payyourself.trading.trader.Trader;

public class Population {

	private static final int NUM_PARENTS = 2; //Something wrong when > 2???
	private static final int NUM_KIDS = 5;
	private static final int NEIGHBORHOOD_SIZE = 20000; //Smaller neighborhood means slower convergence to one soln?
	private static final int MAX_POPULATION_SIZE = 20000;
	private static int MIN_POPULATION_SIZE = 12000; //adjusting population size is same as adjusting unfit threshold.
	
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
	
	private Logger log;
	
	private Creature best;
	
	public Population(){
		
		this.maxPopulationSize = Population.MAX_POPULATION_SIZE;
		this.minPopulationSize = Population.MIN_POPULATION_SIZE ;
		
		this.creatures = new ArrayList<Creature>();
		
		this.availableBuyers = new ArrayList<Trader>();
		this.availableSellers = new ArrayList<Trader>();
		
		this.unfitThreshold = .4f; //Larger = more removed during selection
		this.mutationRatio = 1; // .001Larger = more mutations
		this.unfitSelectionRatio = .2f; //Larger more unfit will be removed
		
		this.fate = new Random();
		
		this.log = LogfileFactory.getHTMLLogger(this.getClass());

		
	}
	
	/**
	 * Mate the population
	 * to produce 2 children for every
	 * 2 parents
	 * @throws Exception 
	 */
	public void crossoverInNeighborhood() throws Exception{
		
		//Try adding the best into the population several times
		//for(int i=0; i<10; i++){
		//	this.creatures.add(this.best);
		//}
		
		for(int i=0; i<10; i++){
			this.creatures.add(this.best);
		}
		
		int numberBreeders = (this.maxPopulationSize - this.creatures.size()); //If 2 parents = 2 children
		
		

		int pos =0;
		int pos2 = 0;
		Creature firstParent;
		Creature secondParent;
		List<Creature> kids;
		
		for(int i=0; i<numberBreeders; i++){
			
			//Select first breeder
			pos = (int) (this.fate.nextFloat()*this.creatures.size()-1);
			firstParent = this.creatures.get(pos);
			
			//Get the position in the neighborhood for the second parent
			pos2 = (int) (this.fate.nextFloat() * Population.NEIGHBORHOOD_SIZE);
			
			//Invert the offset sometimes
			if(this.fate.nextFloat()>.5){
				pos2 = -pos2;
			}
			
			//Get the actual location in the creatures list
			pos2 = pos + pos2;
			
			if(pos2>this.creatures.size()-1){
				pos2 = this.creatures.size()-1;
			}
			
			if(pos2 < 0){
				pos2 = 0;
			}
			
			secondParent = this.creatures.get(pos2);
			
			
			//Now do the breeding
			kids = firstParent.breedBySplice(secondParent);
			
			if(kids.size() > 0){
				//Place the first child in the neighborhood of the first parent
				if(this.fate.nextFloat()>.5){
					pos = pos + (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}else{
					pos = pos - (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}
				
				//Make sure pos is within the range
				if(pos>this.creatures.size()-1){
					pos = this.creatures.size()-1;
				}
				
				if(pos < 0){
					pos = 0;
				}
			}
			this.creatures.add(pos,kids.get(0));
			
			if(kids.size() > 1){

				//Place the second child in the neighborhood of the second parent
				if(this.fate.nextFloat()>.5){
					pos2 = pos2 + (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}else{
					pos2 = pos2 - (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}
				
				//Make sure pos is within the range
				if(pos2>this.creatures.size()-1){
					pos2 = this.creatures.size()-1;
				}
				
				if(pos2 < 0){
					pos2 = 0;
				}
				
				this.creatures.add(pos2,kids.get(1));
			
			}
			
			
		}
		
		
		this.log.info("Created " + (numberBreeders*2) + " new creatures.");
		
	}
	
	
	/**
	 * Mate the population
	 * to produce 2 children for every
	 * 2 parents
	 * @throws Exception 
	 */
	public void crossoverInNeighborhoodComplex() throws Exception{
		
		//Try adding the best into the population several times
		//for(int i=0; i<10; i++){
		//	this.creatures.add(this.best);
		//}
		
		int numberBreeders = (this.maxPopulationSize - this.creatures.size())/Population.NUM_KIDS; 

		int pos =0;
		int pos2 = 0;
		Creature firstParent;
		List<Creature> kids;
		List<Creature> partners;
		
		for(int i=0; i<numberBreeders; i++){
			
			//Select first breeder
			pos = (int) (this.fate.nextFloat()*this.creatures.size()-1);
			firstParent = this.creatures.get(pos);
			
			partners = new ArrayList<Creature>();
			
			//Select the other partners
			for(int j=0; j<Population.NUM_PARENTS-1; j++){
				//Get the position in the neighborhood for the second parent
				pos2 = (int) (this.fate.nextFloat() * Population.NEIGHBORHOOD_SIZE);
				
				//Invert the offset sometimes
				if(this.fate.nextFloat()>.5){
					pos2 = -pos2;
				}
				
				//Get the actual location in the creatures list
				pos2 = pos + pos2;
				
				if(pos2>this.creatures.size()-1){
					pos2 = this.creatures.size()-1;
				}
				
				if(pos2 < 0){
					pos2 = 0;
				}
				
				partners.add(this.creatures.get(pos2));
			}
			
			//Now do the breeding
			kids = firstParent.breedBySelection(partners, Population.NUM_KIDS);
			
			//Insert the kids
			for(int j=0; j<kids.size(); j++){
				
				//Place the first children in the neighborhood of the first parent
				if(this.fate.nextFloat()>.5){
					pos = pos + (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}else{
					pos = pos - (int) (this.fate.nextFloat()* Population.NEIGHBORHOOD_SIZE);
				}
				
				//Make sure pos is within the range
				if(pos>this.creatures.size()-1){
					pos = this.creatures.size()-1;
				}
				
				if(pos < 0){
					pos = 0;
				}
				this.creatures.add(pos,kids.get(j));
				
			}
			
		}
		
		
		this.log.info("Created " + (numberBreeders*Population.NUM_KIDS) + " new creatures.");
		
	}
	
	public List<List<Creature>> randomSelection(int numBreedingPairs){
		
		List<List<Creature>> breeders = new ArrayList<List<Creature>>();
		List<Creature> group1 = new ArrayList<Creature>();
		List<Creature> group2 = new ArrayList<Creature>();
		
		
		for(int i=1; i<numBreedingPairs; i++){
			
			group1.add(this.creatures.get((int) (this.fate.nextFloat()*this.creatures.size())));
			group2.add(this.creatures.get((int) (this.fate.nextFloat()*this.creatures.size())));

		}
		
		breeders.add(group1);
		breeders.add(group2);
		
		return breeders;
	}
	
	/**
	 * Select a 
	 * @return
	 */
	public List<List<Creature>> localSelection(int numBreedingPairs){
		
		List<List<Creature>> breeders = new ArrayList<List<Creature>>();
		List<Creature> group1 = new ArrayList<Creature>();
		List<Creature> group2 = new ArrayList<Creature>();
		
		int pos = 0;
		int pos2 = 0;
		
		for(int i=0; i<numBreedingPairs; i++){
			
			//Select first breeder
			pos = (int) (this.fate.nextFloat()*this.creatures.size());
			group1.add(this.creatures.get(pos));
			
			//Get the position in the neighborhood
			pos2 = (int) (this.fate.nextFloat() * Population.NEIGHBORHOOD_SIZE);
			
			//Invert the offset sometimes
			if(this.fate.nextFloat()>.5){
				pos2 = -pos2;
			}
			
			//Get the actual location in the creatures list
			pos2 = pos + pos2;
			
			if(pos2>this.creatures.size()-1){
				pos2 = this.creatures.size()-1;
			}
			
			if(pos2 < 0){
				pos2 = 0;
			}
			
			group2.add(this.creatures.get(pos2));
			
		}
		
		breeders.add(group1);
		breeders.add(group2);
		
		return breeders;
	}
	
	

	/**
	 * Mate the population
	 * to produce 2 children for every
	 * 2 parents
	 * @throws Exception 
	 */
	public void crossoverFittest() throws Exception{
		
		
		//TODO add in new buyers and sellers here
		
		List<Creature> kids = new ArrayList<Creature>();
		
		int numCrossovers = this.creatures.size()/3; //Breed 1/3 of population
		
		//Cycle through population and select the fitest members to breed
		for(int i=1; i<numCrossovers; i=i+Population.NUM_PARENTS){
			
			kids.addAll(this.creatures.get(i).breedBySplice(this.creatures.get(i+1)));
			
		}		
		
		this.log.info("Created " + kids.size() + " new creatures.");
		
		//TODO improve the efficiency of this
		this.creatures.addAll(kids);
		
	}

	/**
	 * Select some of the population 
	 * to live and some to die
	 * 
	 * 
	 */
	public void selection(){
			
		int killed = 0;
		
		//This is where we keep the population in check
		int numToKill = (int)( (float)this.creatures.size()*this.unfitThreshold); //Kill a percentage of population
		
		//Do we need to keep more to keep the population size up?
		//int needToKeep = this.minPopulationSize - (this.creatures.size() - numToKill);
		//if(needToKeep > 0){
		//	numToKill = numToKill - needToKeep;
		//}
		
		//int extraToKill = this.creatures.size() - this.maxPopulationSize;

		
		//if(extraToKill > 0){
		//	numToKill = numToKill + extraToKill;
		//}
		

		int toRemove = 0;
		
		
		for(int i=0; i<numToKill; i++){
			
			toRemove = (int)(((float)this.creatures.size()*1f)*this.fate.nextFloat()); //Remove from bottom half
			if(toRemove == 0){ //Never remove the best.
				toRemove = 1;
			}
			this.creatures.remove(toRemove); //Remove the least fittest
			killed++;
		
		}
		
		this.log.info("Killed " + killed + " creatures.");
		
	}
	
	public void evolve() throws Exception{
		

		//Do we need an initial population?
		this.createInitialPopulation();

		

		this.addNewTraders();
		
		//Do the evolution of the existing population
		
		//TODO include all operations in one large loop to save time.
		
		//Control the population size
		//this.populationControl();
		
		//Assess the fitness and kill unfit members
		this.selection();
		
		
		//Breed the population
		this.crossoverInNeighborhoodComplex();

		//Mutate the population
		this.mutate();

		//Sort the new population
		this.sortCreatures();

	}


	/**
	 * If necessary create an initial population
	 * @throws Exception 
	 * 
	 */
	private void createInitialPopulation() throws Exception {
		
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
			
			this.sortCreatures(); //and find best
		}//end if no population		
	}

	/**
	 * Add any new Traders into the population
	 * @throws Exception 
	 */
	private void addNewTraders() throws Exception {
		
		
		//We need to add in the 
		if(this.availableBuyers.size() > this.availableSellers.size()){
			//More Buyers
			while(this.availableSellers.size() > 0){
				Creature newCreature = new Creature
					(this.availableBuyers.get(0),this.availableSellers.get(0));
				
				this.creatures.add(newCreature);
				this.availableBuyers.remove(0);
				this.availableSellers.remove(0);
			}
		}else{
			//More Sellers
			while(this.availableBuyers.size() > 0){
				Creature newCreature = new Creature
					(this.availableBuyers.get(0),this.availableSellers.get(0));
				
				this.creatures.add(newCreature);
				
				this.availableBuyers.remove(0);
				this.availableSellers.remove(0);
			
			}
		}

		
	}

	public void sortCreatures(){
		Collections.sort(this.creatures);
		
		this.best = new Creature(this.creatures.get(0));
		
	}


	/**
	 * Mutate the population using all available buyers and sellers
	 * @throws Exception 
	 */
	private void mutate() throws Exception {
	
		//Compute an index to take some buyers from
		int index = (int) (this.fate.nextFloat()*this.creatures.size()-1);
		
		//Select the buyers and sellers for the mutation
		this.mutate(this.creatures.get(index).getBuyers().get(0),this.creatures.get(index).getSellers().get(0));
		
		
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
		
		return this.creatures.get(0);
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
