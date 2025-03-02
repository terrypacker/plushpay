package com.payyourself.trading.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.payyourself.currency.PyCurrency;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.group.TraderGroup;

public class Creature implements Comparable<Creature>{
	
	
	private static final long MAX_TRADE_VALUE = 1000000000; //$100,000 AUD dollars
	
	//Fitness parameters
	private long gain; //Sum of the two
	private long cost; //Cost of Investment
	private float roi; //Return on Investment = ((Gain on invest)-(cost of invest))/(cost of invest)
	private float fitness; //Fitness of creature 0 to 1 (Assuming MAX_TRADE is correct)
	
	//Fate
	private Random fate;
	
	//chromosomes of creature
	private List<Trader> buyers;
	private List<Trader> sellers;

	private PyCurrency sellersCurrencyToSell;
	private PyCurrency sellersCurrencyToBuy;
	
	private PyCurrency buyersCurrencyToSell;
	private PyCurrency buyersCurrencyToBuy;
	
	
	
	public Creature(List<Trader> buyers, List<Trader> sellers) throws Exception{
		
		this.fate = new Random();

		
		this.buyers = buyers;
		this.sellers = sellers;

		//Compute currency to buy
		this.buyersCurrencyToBuy = this.buyers.get(0).getCurrencyToBuy();
		this.buyersCurrencyToSell = this.buyers.get(0).getCurrencyToSell();
		
		for(int i=1; i<this.buyers.size(); i++){
			
			this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.
				add(this.buyers.get(i).getCurrencyToBuy());
			this.buyersCurrencyToSell = this.buyersCurrencyToSell.
				add(this.buyers.get(i).getCurrencyToSell());
		}
		
		//Compute currency to sell
		this.sellersCurrencyToSell = this.sellers.get(0).getCurrencyToSell();
		this.sellersCurrencyToBuy = this.sellers.get(0).getCurrencyToBuy();
		
		for(int i=1; i<this.sellers.size(); i++){
			this.sellersCurrencyToSell = this.sellersCurrencyToSell.
				add(this.sellers.get(i).getCurrencyToSell());
			this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.
				add(this.sellers.get(i).getCurrencyToBuy());
		}

		this.computeFitnessCriteria();

	}
	
	
	public Creature(Trader buyer, Trader seller) throws Exception {
		this.fate = new Random();

		
		this.buyers = new ArrayList<Trader>();
		this.buyers.add(buyer);
		
		this.sellers = new ArrayList<Trader>();
		this.sellers.add(seller);

		//Compute currency to buy
		this.buyersCurrencyToBuy = this.buyers.get(0).getCurrencyToBuy();
		this.buyersCurrencyToSell = this.buyers.get(0).getCurrencyToSell();
		
		for(int i=1; i<this.buyers.size(); i++){
			
			this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.
				add(this.buyers.get(i).getCurrencyToBuy());
			this.buyersCurrencyToSell = this.buyersCurrencyToSell.
				add(this.buyers.get(i).getCurrencyToSell());
		}
		
		//Compute currency to sell
		this.sellersCurrencyToSell = this.sellers.get(0).getCurrencyToSell();
		this.sellersCurrencyToBuy = this.sellers.get(0).getCurrencyToBuy();
		
		for(int i=1; i<this.sellers.size(); i++){
			this.sellersCurrencyToSell = this.sellersCurrencyToSell.
				add(this.sellers.get(i).getCurrencyToSell());
			this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.
				add(this.sellers.get(i).getCurrencyToBuy());
		}

		this.computeFitnessCriteria();
		
	}


	/**
	 * Copy Constructor
	 * @param creature
	 */
	public Creature(Creature creature) {
		
		this.gain = creature.getGain();
		this.cost = creature.getCost();
		this.roi = creature.getRoi();	
		this.fitness = creature.fitness;
		
		this.buyers = new ArrayList<Trader>(creature.getBuyers());
		this.sellers = new ArrayList<Trader>(creature.getSellers());
		
		this.fate = new Random();
		
		this.buyersCurrencyToBuy = new PyCurrency(creature.buyersCurrencyToBuy);
		this.buyersCurrencyToSell = new PyCurrency(creature.buyersCurrencyToSell);
		
		this.sellersCurrencyToBuy = new PyCurrency(creature.sellersCurrencyToBuy);
		this.sellersCurrencyToSell = new PyCurrency(creature.sellersCurrencyToSell);
		
		
	}


	public void computeFitnessCriteria(){
		
		
		//The sign of these numbers is with reference to our Bank Account
		long buyerBuy = this.sellersCurrencyToSell.getValue() - this.buyersCurrencyToBuy.getValue();
		long buyerSell = this.buyersCurrencyToSell.getValue() - this.sellersCurrencyToBuy.getValue();

		
		//Set our gain
		this.gain = buyerBuy + buyerSell;
		
		//Compute our roi
		this.cost = 0;
		long roiGain = 0;
		
		if(buyerBuy < 0){
			this.cost = this.cost + -buyerBuy;
		}else{
			roiGain = roiGain + buyerBuy;
		}
		
		if(buyerSell<0){
			cost = cost + -buyerSell;
		}else{
			roiGain = roiGain + buyerSell;
		}
	
		if(cost == 0){
			this.cost = 1;
		}

		if(cost != 0){
			//TODO improve this by removing / and making a long with 10 decimal precision
			this.roi = ((float)roiGain-(float)cost)/(float)cost;
		}else{
			//TODO this will never happen
			this.roi = Float.POSITIVE_INFINITY;
		}
		
		float maxRoi = (this.sellers.size() + this.buyers.size())*(float)Creature.MAX_TRADE_VALUE;
		
		this.fitness =  this.roi; //this.roi/maxRoi;
		
		
	}
	
	/**
	 * Return the fitness of this creature
	 * @return
	 */
	public float getFitness(){
		
		return fitness;
	}


	public List<Creature> breedBySortedSplice(Creature creature) throws Exception {
		
		List<Creature> kids = new ArrayList<Creature>();
		
		Creature kid = null;
		
		//Split each buyer/seller groups at some point
		List<List<Trader>> myBuyers = this.selectSortedBuyers();
		List<List<Trader>> mySellers = this.selectSortedSellers();
		
		List<List<Trader>> thoseBuyers = creature.selectSortedBuyers();
		List<List<Trader>> thoseSellers = creature.selectSortedSellers();
		
		//Create the buyers for kid 1
		List<Trader> kid1Buyers = myBuyers.get(0);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseBuyers.get(0).size(); i++){
			if(!kid1Buyers.contains(thoseBuyers.get(0).get(i))){
				kid1Buyers.add(thoseBuyers.get(0).get(i));
			}
		}
		
		//Create the sellers for kid 1
		List<Trader> kid1Sellers = mySellers.get(0);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseSellers.get(0).size(); i++){
			if(!kid1Sellers.contains(thoseSellers.get(0).get(i))){
				kid1Sellers.add(thoseSellers.get(0).get(i));
			}
		}
		
		if((kid1Buyers.size() > 0)&&(kid1Sellers.size() > 0)){
			kid = new Creature(kid1Buyers,kid1Sellers);		
			kids.add(kid);
		}
		
		
		//Create kid 2
		//Create the buyers for kid 1
		List<Trader> kid2Buyers = myBuyers.get(1);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseBuyers.get(1).size(); i++){
			if(!kid2Buyers.contains(thoseBuyers.get(1).get(i))){
				kid2Buyers.add(thoseBuyers.get(1).get(i));
			}
		}
		
		//Create the sellers for kid 1
		List<Trader> kid2Sellers = mySellers.get(1);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseSellers.get(1).size(); i++){
			if(!kid2Sellers.contains(thoseSellers.get(1).get(i))){
				kid2Sellers.add(thoseSellers.get(1).get(i));
			}
		}
	
		if((kid2Buyers.size() > 0)&&(kid2Sellers.size() > 0)){
			kid = new Creature(kid2Buyers,kid2Sellers);
			kids.add(kid);
		}
		
		return kids;
		
		
	}
	
	public List<Creature> breedBySplice(Creature creature) throws Exception {
		
		List<Creature> kids = new ArrayList<Creature>();
		
		Creature kid = null;
		
		//Split each buyer/seller groups at some point
		List<List<Trader>> myBuyers = this.selectBuyers();
		List<List<Trader>> mySellers = this.selectSellers();
		
		List<List<Trader>> thoseBuyers = creature.selectBuyers();
		List<List<Trader>> thoseSellers = creature.selectSellers();
		
		//Create the buyers for kid 1
		List<Trader> kid1Buyers = myBuyers.get(0);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseBuyers.get(0).size(); i++){
			if(!kid1Buyers.contains(thoseBuyers.get(0).get(i))){
				kid1Buyers.add(thoseBuyers.get(0).get(i));
			}
		}
		
		//Create the sellers for kid 1
		List<Trader> kid1Sellers = mySellers.get(0);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseSellers.get(0).size(); i++){
			if(!kid1Sellers.contains(thoseSellers.get(0).get(i))){
				kid1Sellers.add(thoseSellers.get(0).get(i));
			}
		}
		
		if((kid1Buyers.size() > 0)&&(kid1Sellers.size() > 0)){
			kid = new Creature(kid1Buyers,kid1Sellers);		
			kids.add(kid);
		}
		
		
		//Create kid 2
		//Create the buyers for kid 1
		List<Trader> kid2Buyers = myBuyers.get(1);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseBuyers.get(1).size(); i++){
			if(!kid2Buyers.contains(thoseBuyers.get(1).get(i))){
				kid2Buyers.add(thoseBuyers.get(1).get(i));
			}
		}
		
		//Create the sellers for kid 1
		List<Trader> kid2Sellers = mySellers.get(1);
		//Ensure no duplicates exist in list
		for(int i=0; i<thoseSellers.get(1).size(); i++){
			if(!kid2Sellers.contains(thoseSellers.get(1).get(i))){
				kid2Sellers.add(thoseSellers.get(1).get(i));
			}
		}
	
		if((kid2Buyers.size() > 0)&&(kid2Sellers.size() > 0)){
			kid = new Creature(kid2Buyers,kid2Sellers);
			kids.add(kid);
		}
		
		return kids;
		
		
	}
	
	public List<Creature> breedByBestHalf(Creature creature) throws Exception{

		List<Creature> kids = new ArrayList<Creature>();
		
		List<Trader> newBuyers = new ArrayList<Trader>();
		List<Trader> newSellers = new ArrayList<Trader>();
		
		List<Trader> thoseBuyers = creature.getBuyers();
		List<Trader> thoseSellers = creature.getSellers();

		
		Trader temp;
		
		if(this.buyers.size() > 4){
			//Sort the buyers by gain
			Collections.sort(this.buyers);
		
			for(int i=0; i<this.buyers.size()/2; i++){
				newBuyers.add(this.buyers.get(i));
			}
		}else{
			newBuyers.addAll(this.buyers);
		}
		
		if(thoseBuyers.size() > 4){
			//Sort the buyers by gain
			Collections.sort(thoseBuyers);
		
			for(int i=0; i<thoseBuyers.size()/2; i++){
				newBuyers.add(thoseBuyers.get(i));
			}
		}else{
				newBuyers.addAll(thoseBuyers);
		}
	
		
		if(this.sellers.size() > 4){
			
			//Sort sellers by gain
			Collections.sort(this.sellers);
			
			
			for(int i=0; i<this.sellers.size()/2; i++){
				newSellers.add(this.sellers.get(i));
			}
		}else{
			newSellers.addAll(this.sellers);
		}
		
		
		if(thoseSellers.size() > 4){
			
			//Sort sellers by gain
			Collections.sort(thoseSellers);
			
			
			for(int i=0; i<thoseSellers.size()/2; i++){
				
				newSellers.add(thoseSellers.get(i));
			}
		}else{
			newSellers.addAll(thoseSellers);
		}

		

		return kids;
		
	}

	
	public List<Creature> breedByBest(Creature creature) throws Exception {
		
		List<Creature> kids = new ArrayList<Creature>();
		
		List<Trader> newBuyers = new ArrayList<Trader>();
		List<Trader> newSellers = new ArrayList<Trader>();
		
		List<Trader> thoseBuyers = creature.getBuyers();
		List<Trader> thoseSellers = creature.getSellers();
		
		//We want to maximize BuyerSell-BuyerBuy
		//Select the best combination to create one super-kid

		Trader temp;
		
		if(this.buyers.size() > 4){
			//Sort the buyers by gain
			Collections.sort(this.buyers);
		
			for(int i=0; i<this.buyers.size(); i++){
				
				temp = this.buyers.get(i);
				
				if((temp.getCurrencyToSell().getValue() - temp.getCurrencyToBuy().getValue()) > 0){
					if(!newBuyers.contains(this.buyers.get(i))){
						newBuyers.add(this.buyers.get(i));
					}
				}
			}
			
		}else{
			newBuyers.addAll(this.buyers);
		}
		
		if(thoseBuyers.size() > 4){
			//Sort the buyers by gain
			Collections.sort(thoseBuyers);
		
			for(int i=0; i<thoseBuyers.size(); i++){
				temp = thoseBuyers.get(i);
				if((temp.getCurrencyToSell().getValue() - temp.getCurrencyToBuy().getValue()) > 0){
					if(!newBuyers.contains(thoseBuyers.get(i))){
					newBuyers.add(thoseBuyers.get(i));
					}
				}
			}
		}else{
			for(int i=0; i<thoseBuyers.size(); i++){
				if(!newBuyers.contains(thoseBuyers.get(i))){
					newBuyers.add(thoseBuyers.get(i));
				}
			}
		}

		//If we didn't get any buyers then we need at least one from each parent
		if(newBuyers.size() == 0){
			newBuyers.add(this.buyers.get(0));			
			if(!newBuyers.contains(thoseBuyers.get(0)))
				newBuyers.add(thoseBuyers.get(0));
		}
		
		
		if(this.sellers.size() > 4){
			
			//Sort sellers by gain
			Collections.sort(this.sellers);
			
			
			for(int i=0; i<this.sellers.size(); i++){
				
				temp = this.sellers.get(i);
				if((temp.getCurrencyToSell().getValue() - temp.getCurrencyToBuy().getValue()) > 0){
				
					if(!newSellers.contains(this.sellers.get(i))){
						newSellers.add(this.sellers.get(i));
					}
				}
			}
		}else{
			newSellers.addAll(this.sellers);
		}
		
		
		if(thoseSellers.size() > 4){
			
			//Sort sellers by gain
			Collections.sort(thoseSellers);
			
			
			for(int i=0; i<thoseSellers.size(); i++){
				temp = thoseSellers.get(i);
				if((temp.getCurrencyToSell().getValue() - temp.getCurrencyToBuy().getValue()) > 0){
					if(!newSellers.contains(thoseSellers.get(i))){
						newSellers.add(thoseSellers.get(i));
					}
				}
			}
		}else{
			for(int i=0; i<thoseSellers.size(); i++){
				if(!newSellers.contains(thoseSellers.get(i))){
					newSellers.add(thoseSellers.get(i));
				}
			}
		}

		//If we didn't find any sellers we need one from each parent
		if(newSellers.size() == 0){
			newSellers.add(this.sellers.get(0));
			if(!newSellers.contains(thoseSellers.get(0))){
				newSellers.add(thoseSellers.get(0));
			}
		}
				
		kids.add(new Creature(newBuyers,newSellers));
		
		
		return kids;
		
		
	}

	/**
	 * Mutate the creature by adding a buyer and seller to it
	 * @param buyer
	 * @param seller
	 * @throws Exception 
	 */
	public void mutate(Trader buyer, Trader seller) throws Exception {
		
		this.buyers.add(buyer);
		this.buyersCurrencyToBuy = this.buyersCurrencyToBuy.add(buyer.getCurrencyToBuy());
		this.buyersCurrencyToSell = this.buyersCurrencyToSell.add(buyer.getCurrencyToSell());
		
		this.sellers.add(seller);
		this.sellersCurrencyToSell = this.sellersCurrencyToSell.add(seller.getCurrencyToSell());
		this.sellersCurrencyToBuy = this.sellersCurrencyToBuy.add(seller.getCurrencyToBuy());
		
		//Recompute the fitness
		this.computeFitnessCriteria();
		
	}


	/**
	 * Return 2 lists of sellers 
	 * one for each child
	 * @return
	 */
	private List<List<Trader>> selectSellers() {
		//Get the ratios to create a kid
		float sellerRatio = this.fate.nextFloat();
	
		List<List<Trader>> sellerLists = new ArrayList<List<Trader>>();
		
		//Get the first list
		int cutoffPoint = (int) (this.sellers.size()*sellerRatio);
		
		List<Trader> group = new ArrayList<Trader>();
		
		for(int i=0; i<cutoffPoint; i++){
			group.add(this.sellers.get(i));
		}
		
		
		sellerLists.add(group);
		
		group = new ArrayList<Trader>();
		
		for(int i=cutoffPoint; i<this.sellers.size(); i++){
			group.add(this.sellers.get(i));
		}
		
		sellerLists.add(group);
		
		return sellerLists;
		
	}
	/**
	 * Return 2 lists of sellers 
	 * one for each child
	 * @return
	 */
	private List<List<Trader>> selectSortedSellers() {
		//Get the ratios to create a kid
		float sellerRatio = this.fate.nextFloat();
	
		List<List<Trader>> sellerLists = new ArrayList<List<Trader>>();
		
		//Get the first list
		int cutoffPoint = (int) (this.sellers.size()*sellerRatio);
		
		//One kid will get the better sellers
		Collections.sort(this.sellers);
		
		List<Trader> group = new ArrayList<Trader>();
		
		for(int i=0; i<cutoffPoint; i++){
			group.add(this.sellers.get(i));
		}
		
		
		sellerLists.add(group);
		
		group = new ArrayList<Trader>();
		
		for(int i=cutoffPoint; i<this.sellers.size(); i++){
			group.add(this.sellers.get(i));
		}
		
		sellerLists.add(group);
		
		return sellerLists;
		
	}

	/**
	 * How to divide the buyers between the children
	 * @return
	 */
	private List<List<Trader>> selectSortedBuyers() {
		
		float buyerRatio = this.fate.nextFloat(); //0 to 1 
		
		List<List<Trader>> buyerLists = new ArrayList<List<Trader>>();
		
		//Sort the buyers so one kid gets the best
		Collections.sort(this.buyers);
		
		int cutoffPoint = (int)(this.buyers.size()*buyerRatio);
	
		List<Trader> group = new ArrayList<Trader>();
		
		for(int i=0; i<cutoffPoint; i++){
			group.add(this.buyers.get(i));
		}
		
		
		buyerLists.add(group);
		
		group = new ArrayList<Trader>();
		
		for(int i=cutoffPoint; i<this.buyers.size(); i++){
			group.add(this.buyers.get(i));
		}
		
		buyerLists.add(group);
		
		return buyerLists;
	
		
	}

	/**
	 * How to divide the buyers between the children
	 * @return
	 */
	private List<List<Trader>> selectBuyers() {
		
		float buyerRatio = this.fate.nextFloat(); //0 to 1 
		
		List<List<Trader>> buyerLists = new ArrayList<List<Trader>>();
		
		
		int cutoffPoint = (int)(this.buyers.size()*buyerRatio);
	
		List<Trader> group = new ArrayList<Trader>();
		
		for(int i=0; i<cutoffPoint; i++){
			group.add(this.buyers.get(i));
		}
		
		
		buyerLists.add(group);
		
		group = new ArrayList<Trader>();
		
		for(int i=cutoffPoint; i<this.buyers.size(); i++){
			group.add(this.buyers.get(i));
		}
		
		buyerLists.add(group);
		
		return buyerLists;
	
		
	}


	/**
	 * @return the gain
	 */
	public long getGain() {
		return gain;
	}


	/**
	 * @param gain the gain to set
	 */
	public void setGain(long gain) {
		this.gain = gain;
	}


	/**
	 * @return the cost
	 */
	public long getCost() {
		return cost;
	}


	/**
	 * @param cost the cost to set
	 */
	public void setCost(long cost) {
		this.cost = cost;
	}


	/**
	 * @return the roi
	 */
	public float getRoi() {
		return roi;
	}


	/**
	 * @param roi the roi to set
	 */
	public void setRoi(float roi) {
		this.roi = roi;
	}




	public List<Trader> getSellers() {
		return this.sellers;
	}
	
	public List<Trader> getBuyers(){
		return this.buyers;
	}


	public TraderGroup getBuyerAsTraderGroup() {
		return new TraderGroup(this.buyersCurrencyToSell,
				this.buyersCurrencyToBuy,
				this.buyers);
	}


	public TraderGroup getSellersAsTraderGroup() {
		return new TraderGroup(this.sellersCurrencyToSell,
				this.sellersCurrencyToBuy,
				this.sellers);
	}
	
	
	public String toString(){
		String out = "Buyers: " + this.buyers.size() + "\n"; 
		out = out + "Sellers: " + this.sellers.size() + "\n";
		out = out + "Gain: " + this.gain + "\n";
		out = out + "Cost: " + this.cost + "\n";
		out = out + "Fitness: " + this.getFitness()+ "\n";
		return out;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyers == null) ? 0 : buyers.hashCode());
		result = prime
				* result
				+ ((buyersCurrencyToBuy == null) ? 0 : buyersCurrencyToBuy
						.hashCode());
		result = prime
				* result
				+ ((buyersCurrencyToSell == null) ? 0 : buyersCurrencyToSell
						.hashCode());
		result = prime * result + (int) (cost ^ (cost >>> 32));
		result = prime * result + ((fate == null) ? 0 : fate.hashCode());
		result = prime * result + Float.floatToIntBits(fitness);
		result = prime * result + (int) (gain ^ (gain >>> 32));
		result = prime * result + Float.floatToIntBits(roi);
		result = prime * result + ((sellers == null) ? 0 : sellers.hashCode());
		result = prime
				* result
				+ ((sellersCurrencyToBuy == null) ? 0 : sellersCurrencyToBuy
						.hashCode());
		result = prime
				* result
				+ ((sellersCurrencyToSell == null) ? 0 : sellersCurrencyToSell
						.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Creature other = (Creature) obj;
		if (buyers == null) {
			if (other.buyers != null)
				return false;
		} else if (!buyers.equals(other.buyers))
			return false;
		if (buyersCurrencyToBuy == null) {
			if (other.buyersCurrencyToBuy != null)
				return false;
		} else if (!buyersCurrencyToBuy.equals(other.buyersCurrencyToBuy))
			return false;
		if (buyersCurrencyToSell == null) {
			if (other.buyersCurrencyToSell != null)
				return false;
		} else if (!buyersCurrencyToSell.equals(other.buyersCurrencyToSell))
			return false;
		if (cost != other.cost)
			return false;
/* We don't care about the creatures fate
 * 		if (fate == null) {
			if (other.fate != null)
				return false;
		} else if (!fate.equals(other.fate))
			return false;
*/ 
		if (Float.floatToIntBits(fitness) != Float
				.floatToIntBits(other.fitness))
			return false;
		if (gain != other.gain)
			return false;
		if (Float.floatToIntBits(roi) != Float.floatToIntBits(other.roi))
			return false;
		if (sellers == null) {
			if (other.sellers != null)
				return false;
		} else if (!sellers.equals(other.sellers))
			return false;
		if (sellersCurrencyToBuy == null) {
			if (other.sellersCurrencyToBuy != null)
				return false;
		} else if (!sellersCurrencyToBuy.equals(other.sellersCurrencyToBuy))
			return false;
		if (sellersCurrencyToSell == null) {
			if (other.sellersCurrencyToSell != null)
				return false;
		} else if (!sellersCurrencyToSell.equals(other.sellersCurrencyToSell))
			return false;
		return true;
	}


	/**
	 * TODO Untested
	 */
	public int compareTo(Creature o) {
		
		if(this.fitness > o.fitness){
			return 1;
		}else if(this.fitness < o.fitness){
			return -1;
		}else{
			return 0;
		}
		
	}
	
	
	
	
}
