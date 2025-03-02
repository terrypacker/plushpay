package com.payyourself.trading.tradeTree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.trading.trade.Trade;
import com.payyourself.trading.trade.TradeHibernation;
import com.payyourself.trading.trade.TradeStatus;
import com.payyourself.trading.tradeGenerator.TradeEmailGenerator;
import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;
import com.payyourself.trading.trader.group.TraderGroup;
import com.payyourself.trading.trader.group.TraderGroupHibernation;
import com.payyourself.trading.trader.group.TraderGroupUtil;
import com.payyourself.userManagement.user.User;

public class TraderCombinationRedBlackTreeManager implements ServletContextListener,Runnable{

	private static final boolean RUNNING = false; //Generate Trades?


	private static final String CSV_HEADER = "numBuyers,numSellers,bestMismatch,treeSize,computationTimeNs,computationTimeMs,computationTimeS,cropAbove,trimWithin,adjustmentFactor,maxComputationtime,maxCropAbove,minCropAbove,cropAboveIncrement,cropAboveDecrement,maxTrimWithin,minTrimWithin,trimWithinIncrement,trimWithinDecrement\n";


	private Logger log;
	

	private TraderCombinationRedBlackTree tree;
	
	private List<Trader> currentSellers;
	private List<Trader> currentBuyers;
	
	private PyCurrencyType buyingCurrency;
	private PyCurrencyType sellingCurrency;

	private boolean shutdown;

	private double trimWithin;

	private double cropAbove; //Crop any node on tree with mismatch in this top % range. 


	private long maxMismatchAllowed;


	private long maxComputationTime;


	private double adjustableTrimWithin;


	private double adjustableCropAbove;


	private boolean attemptedMatch;


	private double minTrimWithin;


	private double maxTrimWithin;


	private double maxCropAbove;


	private double trimWithinIncrement;


	private double cropAboveIncrement;


	private double trimWithinDecrement;


	private double minCropAbove;


	private double cropAboveDecrement;


	//For logging param data to CSV
	private boolean writeToFile;
	private BufferedWriter paramOutputFile;


	private int sameBestCtr;


	private long lastBest;
	
	
	/**
	 * 
	 * @param buy - Currency the buyers are buying
	 * @param sell - Currency the sellers are selling
	 */
	public TraderCombinationRedBlackTreeManager(PyCurrencyType buy, PyCurrencyType sell){
		
		HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		
		this.buyingCurrency = buy;
		this.sellingCurrency = sell;
		
		PyCurrency buyZero = new PyCurrency();
		buyZero.setType(buy);
		buyZero.setValue(0);
		buyZero.setBaseValue(0);
		
		PyCurrency sellZero = new PyCurrency();
		sellZero.setType(sell);
		sellZero.setValue(0);
		sellZero.setBaseValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TraderCombinationNode zeroNode = new TraderCombinationNode(zeroBuyers, zeroSellers);
		
		this.tree = new TraderCombinationRedBlackTree();
		
	}
	
	/**
	 * 
	 * @param buy - Currency the buyers are buying
	 * @param sell - Currency the sellers are selling
	 */
	public TraderCombinationRedBlackTreeManager(){
		

	}
	
	/**
	 * Insert a new buyer AND seller, might as well do 2 
	 * because we can do 2 as fast as 1.
	 * @param newBuyer
	 * @param newSeller
	 * @throws Exception 
	 */
	public void insertTraders(Trader newBuyer, Trader newSeller) throws Exception{
		

		
	}
	/**
	 * 
	 * Insert new traders cropping any mismatch
	 * above within cropAbove% of the worst mismatch. 
	 * 
	 * @param cropAbove
	 * @throws Exception
	 */
	private void insertNewTraders(double cropAbove) throws Exception {
		
		List<Trader> newSellers = this.getNewSellers();
		List<Trader> newBuyers = this.getNewBuyers();
		long now;
		//Insert one at a time as long as they are same size
		if(newSellers.size()>newBuyers.size()){

			//Insert all buyers then insert remaining sellers
			for(int i=0; i<newBuyers.size(); i++){
				
				//Insert seller
				now = System.nanoTime(); //Record time
				this.insertSeller(newSellers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
				
				
				//Insert buyer
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
				
			}
			
			for(int i=newBuyers.size(); i<newSellers.size(); i++){
				
				now = System.nanoTime();
				this.insertSeller(newSellers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
			}
			
		}else{

			//Insert all sellers then insert remaining buyers
			//Insert all buyers then insert remaining sellers
			for(int i=0; i<newSellers.size(); i++){
				
				now = System.nanoTime();
				this.insertSeller(newSellers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
				
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
			}
			
			for(int i=newSellers.size(); i<newBuyers.size(); i++){
				//insert buyer
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i),cropAbove);
				
				//Check if we are good to be done?
				if(this.tree.getBest().getTotalMismatch() < this.maxMismatchAllowed){
					return;
				}
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size(), this.tree.getBest().getTotalMismatch(),
						this.tree.getBest().getBuyers().size(),this.tree.getBest().getSellers().size());
			}
		}
		
		
	}
	
	/**
	 * 
	 * Not currently cropping at each insert.
	 * 
	 * @param sellers
	 * @throws Exception
	 */
	public void insertBuyer(Trader buyer, double cropAbove) throws Exception{

		this.log.info("Inserting Buyer, Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();
		//this.tree.insertBuyer(buyer, this.adjustableCropAbove/this.tree.size());
		this.tree.insertBuyer(buyer);

		/*long nanoNow = System.nanoTime();
		this.tree.trimTree(this.adjustableTrimWithin/this.tree.size());
		this.log.info("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
		*/
		
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.info("Insterting Buyer took: " + (later-now) + "ms");
		
		TraderCombinationNode best = this.tree.getBest();
		this.log.info("Best Group: " + best.getTotalMismatchInDollars() + " with " + (best.getBuyers().size()+best.getSellers().size()) + " members.");
		
		

	}

	
	/**
	 * 
	 * @param sellers
	 * @throws Exception
	 */
	public void insertSeller(Trader seller, double cropAbove) throws Exception{

		this.log.info("Inserting Seller, Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();
		//this.tree.insertSeller(seller, this.adjustableCropAbove/this.tree.size());
		this.tree.insertSeller(seller);

		/*long nanoNow = System.nanoTime();
		this.tree.trimTree(this.adjustableTrimWithin/this.tree.size());
		this.log.info("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
		*/
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.info("Insterting Seller took: " + (later-now) + "ms");
		
		TraderCombinationNode best = this.tree.getBest();
		this.log.info("Best Group: " + best.getTotalMismatchInDollars() + " with " + (best.getBuyers().size()+best.getSellers().size()) + " members.");
		
		

	}
	


	
	
	/**
	 * 
	 */
	public void run() {
		
		//Should package this into the settings structure

		
		this.maxMismatchAllowed = 5000; //50c max mismatch both ways.
		this.maxComputationTime = 100000000; //500*1000000;//in nanoSeconds to insert one buyer

		
		//Should go through and re-work these so their meanings are not inverted.  STUPID
		//Adjustable parameters - Larger values mean faster but less accurate
		this.cropAbove = .5f; //.999999999f;  //Any group above this 1-(cropAbove/numNodes)% of the largest mismatch is removed
		this.adjustableCropAbove = this.cropAbove;
		this.maxCropAbove = .99999f;
		this.minCropAbove = .0000000001f;
		this.cropAboveIncrement = .05f;//.05f; //Speed that the algo can react larger is faster (.01 for 100k)
		this.cropAboveDecrement = .5f; //.05f; //Speed that the algo can react larger is faster (.08 for 100k)
		
		this.trimWithin = .5f; //.3f; //Trim list so final answer is within trimWithin % of best possible (so if max amnt tradeable is 100,000 to be within 1 dollar we need .0000001
		this.maxTrimWithin = .99999f;
		this.minTrimWithin = .0000000001f; //Too low and we keep the tree size up and can't add members.
		this.adjustableTrimWithin = this.trimWithin;
		this.trimWithinIncrement = .05f; //.05f;//Speed that the algo can react larger is faster (.01 for 100k)
		this.trimWithinDecrement = .5f;//.05f;//Speed that the algo can react larger is faster (.08 for 100k)
		
		this.writeToFile = true; //Write the param data to CSV
		
		//END settings package

		//Setup write to CSV
		if(this.writeToFile){
			String temp = null;
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				temp = loader.getResource("com").toURI().getPath();
				temp = temp + "../../../csvfiles/";
				
				Calendar now = Calendar.getInstance();
				 SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmmss");
				
				FileWriter writer = new FileWriter(temp + sdf.format(now.getTime()) + "paramData.csv");
				this.paramOutputFile = new BufferedWriter(writer);
				
				//Write the header line
				this.paramOutputFile.write(TraderCombinationRedBlackTreeManager.CSV_HEADER);
				
			} catch (URISyntaxException e) {
				//We have a real problem
				this.log.info("Unable to Start Writing Param Data.",e);
			} catch (IOException e) {
				this.log.info("Unable to Start Writing Param Data.",e);			}

		}
		
		HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		
		PyCurrencyTypeHibernation pyctch = new PyCurrencyTypeHibernation();
		List<PyCurrencyType> allTypes = pyctch.loadAllTypes();
		
		
		this.buyingCurrency = allTypes.get(1);
		this.sellingCurrency = allTypes.get(0);
		
		PyCurrency buyZero = new PyCurrency();
		buyZero.setType(this.buyingCurrency);
		buyZero.setValue(0);
		buyZero.setBaseValue(0);
		
		PyCurrency sellZero = new PyCurrency();
		sellZero.setType(this.sellingCurrency);
		sellZero.setValue(0);
		sellZero.setBaseValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TraderCombinationNode zeroNode = new TraderCombinationNode(zeroBuyers, zeroSellers);
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		this.tree = new TraderCombinationRedBlackTree();
		this.tree.put(0,zeroNode);

		
		
	
		while(true){
			
			
			//Need a way to determine if we need to rebuild the tree and adjust tuning params.
			//this.resetTree();

			//THEN CROP for best results.
			try {
				this.insertNewTraders(this.adjustableCropAbove);
			} catch (Exception e1) {
				this.log.error("Problem inserting trader!",e1);
			}
			


			TraderCombinationNode best = this.tree.getBest();

			//Test to see if we have a best.
			if(best != null){
				this.log.info("Best Group: " + best.getTotalMismatchInDollars() + " with " + (best.getBuyers().size()+best.getSellers().size()) + " members.");
	
				//Create a trade if we are within spec.
				//If the a match was created, we need to persist it into the DB.
				if(best.getTotalMismatch()<this.maxMismatchAllowed){
					this.log.info("Creating Trade!");
					
					//Write the parameter data to a CSV file for analysis later
					//This should be re-written to take a settings object
					try {
						this.writeParamData(0,0L,this.tree.size(),
								this.tree.getBest().getTotalMismatch(),
								this.tree.getBest().getBuyers().size(),
								this.tree.getBest().getSellers().size());
					} catch (IOException e1) {
						this.log.error("Unable to write  to CSV file.");
					}
					
					//TradeLink link = new TradeLink();
					//link.setBuyerBuyCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToBuy().getValue()-best.getSellers().getCurrencyToSell().getValue()));
					//link.setBuyerSellCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToSell().getValue()-best.getSellers().getCurrencyToBuy().getValue()));
					
	
					TraderGroupHibernation tgh = new TraderGroupHibernation();
					
					TraderGroup buyersGroup = new TraderGroup();
					buyersGroup.setCurrencyToBuy(best.getBuyers().getCurrencyToBuy());
					buyersGroup.setCurrencyToSell(best.getBuyers().getCurrencyToSell());			
					buyersGroup = tgh.merge(buyersGroup);
					
					/* Debug */
					//link.setBuyers(buyersGroup);
	
					TraderHibernation th = new TraderHibernation();
					
					/*Remove the 0 value trader AND set status to DEPOSIT*/

					
					for(int i=0; i<best.getBuyers().size(); i++){
						//Set all trader status to DEPOSIT, awaiting deposit.
						best.getBuyers().get(i).setStatus(TraderStatus.DEPOSIT.toString());
						if(best.getBuyers().get(i).getCurrencyToBuy().getValue() == 0){
							try {
								best.getBuyers().remove(i);
								i--; //Step back because we just removed one
							} catch (Exception e) {
								this.log.info("Problem removing 0 value trader.",e);
							}
						}

					}
					
					/*Persist the buyers with the new groupID */
					for(int i=0; i<best.getBuyers().size(); i++){
						best.getBuyers().get(i).setGroup(buyersGroup);
						th.merge(best.getBuyers().get(i));
						
					}
					
					
					TraderGroup sellersGroup = new TraderGroup();
					sellersGroup.setCurrencyToBuy(best.getSellers().getCurrencyToBuy());
					sellersGroup.setCurrencyToSell(best.getSellers().getCurrencyToSell());
					sellersGroup = tgh.merge(sellersGroup);
					
					/*Remove the 0 value trader AND set status to DEPOST*/
					for(int i=0; i<best.getSellers().size(); i++){
						best.getSellers().get(i).setStatus(TraderStatus.DEPOSIT.toString());
						if(best.getSellers().get(i).getCurrencyToBuy().getValue() == 0){
							try {
								best.getSellers().remove(i);
								i--; //Step back because we just removed one
							} catch (Exception e) {
								this.log.info("Error removing 0 seller.",e);
							}
						}

					}
					
					/*Persist the sellers with the new groupID */
					for(int i=0; i<best.getSellers().size(); i++){
						best.getSellers().get(i).setGroup(sellersGroup);
						th.merge(best.getSellers().get(i));
						
					}
					
					/*Create Trade In DB*/
					Trade newTrade = new Trade();
					newTrade.setBuyerGroup(buyersGroup);
					newTrade.setSellerGroup(sellersGroup);
					newTrade.setDateCreated(Calendar.getInstance());
					newTrade.setStatus(TradeStatus.DEPOSIT.toString()); //Set the trade status
					//Should set the expiry date too
					
					
					TradeHibernation trh = new TradeHibernation();
					newTrade = trh.merge(newTrade);
					
					//Notify the memebers of this trade that they need to put thier funds into the Bank
					this.log.info("Sending Trade Emails");
					TradeEmailGenerator genEmail = new TradeEmailGenerator(newTrade);
					genEmail.sendEmails();
	
					
					
					/*Debug*/
					//link.setSellers(sellersGroup);
					//Release lock on traders
					//Check to see if lock is still valid
					//if not return "trade_failed";
			
					//Add to existing trade chain or create new
						
					
					
					this.resetTree();
					
					this.resetTuningParameters();
				}else{//end if we got a trade.
					
					//Try something new, if we have the same best for n interations then we should try rebuilding the tree?
					if(this.lastBest != this.tree.getBest().getTotalMismatch()){
						this.lastBest = this.tree.getBest().getTotalMismatch();
						this.sameBestCtr = 0; //Reset counter
					}else{
						//It was the same, increment the counter
						this.sameBestCtr++;
					}
					
					if(this.sameBestCtr > 10){
						this.lastBest = 0;
						this.log.info("Not Getting any better, rebuilding tree.");
						this.sameBestCtr = 0;//reset counter
						this.resetTree(); //Reset Tree and params
						//this.resetTuningParameters();
					
					}
				}//end else if we got a trade
				
			} //end null
			
			//Write the current params to the log
			this.logCurrentParameters();
			
			
			if(this.shutdown){
				if(this.writeToFile){
					try {
						this.paramOutputFile.flush();
						this.paramOutputFile.close();
					} catch (IOException e) {
						this.log.error("Unable to close logfile.",e);
					}

				}
				return;
			}
			

			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				this.log.error("Interrupted while sleeping.",e);
			}
		}
		
	}


	/**
	 * Reset the tuning parameters to their initial values
	 */
	private void resetTuningParameters() {
		
		this.adjustableTrimWithin = this.trimWithin;
		this.adjustableCropAbove = this.cropAbove;
		
	}
	
	/**
	 * Reset tree to empty 
	 */
	private void resetTree(){
		
		PyCurrency buyZero = new PyCurrency();
		buyZero.setType(this.buyingCurrency);
		buyZero.setValue(0);
		buyZero.setBaseValue(0);
		
		PyCurrency sellZero = new PyCurrency();
		sellZero.setType(this.sellingCurrency);
		sellZero.setValue(0);
		sellZero.setBaseValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TraderCombinationNode zeroNode = new TraderCombinationNode(zeroBuyers, zeroSellers);
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		this.tree.resetTree(); //Clear the tree
		this.tree.put(zeroNode);
	}


	/**
	 * Adjust the tuning parameters to keep the
	 * algorithm running at best performance
	 * 
	 * If it runs quicker than the maxComputationTime, 
	 * then the accuracy is increased.  If it runs slower,
	 * then the accuracy is reduced.
	 * 
	 * If no more items to add and accuracy is as good as we can get
	 * then we should re-build the tree in an attempt to
	 * get a match.
	 * 
	 * These adjustments should be dependent on tree size.
	 * 
	 * We linearize the computation time depending on the tree size.
	 * So we can more accurately detect exponential blow-out of the tree.
	 * 
	 * The rate of expansion to time of adding a buyer is roughly .05ms/sizeoftree (5*10-8 ns)
	 * 
	 * 
	 * Multiply ms * 1000000
	 * 
	 * @param computationTime in nanoSeconds 1*10^-9  for insert a trader.
	 */
	private void adjustParameters(long computationTime, int treeSize,long mismatch,int numBuyers, int numSellers) throws Exception{

		//long computedRate = (long) (computationTime/treeSize);
		this.log.info("Computation Time: " + computationTime + " Max Allowed is: " + this.maxComputationTime);
		
		//Compute a factor to increase or decrease the parameters by.
		double factor = (double)(computationTime)/(double)this.maxComputationTime;
		
		//The largest factor can be 1
		if(factor > 1){
			factor = 1;
		}
		
		double oldValue = 0;
		
		
		if(computationTime < this.maxComputationTime){
			
			//Increase accuracy
			if(this.adjustableTrimWithin > this.minTrimWithin){
				//Adjust by time factor

				//this.adjustableTrimWithin = this.adjustableTrimWithin - this.trimWithinDecrement*factor;
				oldValue = this.adjustableTrimWithin;
				this.adjustableTrimWithin = this.adjustableTrimWithin - this.trimWithinDecrement*factor;
				
				if(this.adjustableTrimWithin < this.minTrimWithin){
					this.adjustableTrimWithin = this.minTrimWithin;
				}
				this.log.info("Reducing Trim Within from " + oldValue + " to " + this.adjustableTrimWithin);
			}else{
				this.log.info("Trim Within at Minimum Allowed.");
			}
			
			if(this.adjustableCropAbove > this.minCropAbove){
				//Compute the factor 
				//this.adjustableCropAbove = this.adjustableCropAbove - this.cropAboveDecrement*factor;
				oldValue = this.adjustableCropAbove;
				this.adjustableCropAbove = this.adjustableCropAbove - this.cropAboveDecrement*factor;
				
				if(this.adjustableCropAbove < this.minCropAbove){
					this.adjustableCropAbove = this.minCropAbove;
				}
				this.log.info("Reducing Crop Above from " + oldValue + " to " + this.adjustableCropAbove );
			}else{
				this.log.info("Crop Above at Minumum Allowed.");
			}
			
		}else{
			//Decrease tree size to speed us up.

			
			//Crop the tree
			/*this.log.info("Cropping Tree, size: " + this.tree.size());
			nanoNow = System.nanoTime();
			this.tree.cropTree(this.adjustableCropAbove); //Hack off a big section of tree
			this.log.info("Cropping Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
			this.log.info("Tree size now: " + this.tree.size());
			*/
			//Compute the time factor
			//float factor = (this.maxComputationTime/computationTime);
			
				if(this.adjustableTrimWithin < this.maxTrimWithin){
					//this.adjustableTrimWithin = this.adjustableTrimWithin + this.trimWithinIncrement*factor;
					oldValue = this.adjustableTrimWithin;
					this.adjustableTrimWithin = this.adjustableTrimWithin+ this.trimWithinIncrement*factor;
					
					if(this.adjustableTrimWithin > this.maxTrimWithin){
						this.adjustableTrimWithin = this.maxTrimWithin;
					}
					this.log.info("Increasing Trim Within from " + oldValue + " to " + this.adjustableTrimWithin);
				}else{
					this.log.info("Trim Within at Maximum Allowed.");
				}
				
				if(this.adjustableCropAbove < this.maxCropAbove){
					//this.adjustableCropAbove = this.adjustableCropAbove + this.cropAboveIncrement * factor;
					oldValue = this.adjustableCropAbove;
					this.adjustableCropAbove = this.adjustableCropAbove + this.cropAboveIncrement * factor;
					
					if(this.adjustableCropAbove > this.maxCropAbove){
						this.adjustableCropAbove = this.maxCropAbove;
					}
					this.log.info("Increasing CropAbove from " +oldValue + " to " + this.adjustableCropAbove);
				}else{
					this.log.info("Crop Above is at Maximum Allowed.");
				}
			
				
				//Trim the tree
				this.log.info("Trimming Tree, size: " + this.tree.size());
				long nanoNow = System.nanoTime();
				
				//this.tree.trimTree(this.adjustableTrimWithin/this.tree.size());
				this.tree.trimTree(this.adjustableTrimWithin);
				//Should check the amount trimmed from tree and use this information.
				
				this.log.info("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
				this.log.info("Tree size now: " + this.tree.size());
				
				//IF we are really getting slow.
				if(computationTime > 2* this.maxComputationTime){
					//We need to do something drastic, we are blowing out
					this.log.info("Disaster Control, tree size: " + this.tree.size() + " Cropping Tree by: " + this.adjustableCropAbove);
					this.tree.cropTree(this.adjustableCropAbove); //Hack off a big section of tree
					this.log.info("Tree size now: " + this.tree.size());
				}

		}

		//Write the parameter data to a CSV file for analysis later
		//This should be re-written to take a settings object
		this.writeParamData(factor,computationTime,this.tree.size(),
				this.tree.getBest().getTotalMismatch(),
				this.tree.getBest().getBuyers().size(),
				this.tree.getBest().getSellers().size());

		
	}

	
	/**
	 * Write the parameter Data to a file
	 * 
	 * numBuyers,numSellers,bestMismatch,treeSize, computationTimeNs,computationTimeMs,computationTimeS
	 * cropAbove,trimWithin,adjustmentFactor,maxComputationTime,maxCropAbove,minCropAbove,cropAboveIncrement,cropAboveDecrement,
	 * maxTrimWithin,minTrimWithin,trimWithinIncrement,trimWithinDecrement;
	 * @throws IOException 
	 * 
	 */
	private void writeParamData(double factor,long computationTime, int treeSize, long bestMismatch, int numBuyers, int numSellers) throws IOException {
		
		String out = numBuyers + "," + numSellers + "," + ((float)bestMismatch/10000f) + "," + treeSize + "," +  computationTime + "," + (computationTime/1000000) + "," +
					((float)computationTime/1000000000f) + "," +
					this.adjustableCropAbove + "," + this.adjustableTrimWithin + ","+ factor + "," +
					this.maxComputationTime + "," + this.maxCropAbove + "," + this.minCropAbove + "," + 
					this.cropAboveIncrement + "," + this.cropAboveDecrement + "," +
					this.maxTrimWithin + "," + this.minTrimWithin + "," +
					this.trimWithinIncrement + "," + this.trimWithinDecrement + "\n";
					
		
		//Write the data to a file
		this.paramOutputFile.write(out);
		this.paramOutputFile.flush();
	}

	/**
	 * Log adjustable params
	 */
	private void logCurrentParameters(){
		
		this.log.info("Trim By Removing anyone within: " + ((1f-(this.adjustableTrimWithin/(float)this.tree.size()))*100f)+ "% of previous member. " + "(" + this.adjustableTrimWithin + ")");
		this.log.info("Crop Any Group with Mismatch above: " + ((1-this.adjustableCropAbove) * 100f) + "% of Worst Group. " + "(" + this.adjustableCropAbove + ")");

	}
	
	/**
	 * Collect sellers from DB that are not already in the tree
	 * @return
	 */
	private List<Trader> getNewSellers() {
		//Load in all free sellers of this currency Type and lock them
		TraderHibernation th = new TraderHibernation();
	
		List<Integer> ids = new ArrayList<Integer>();
		for(int i=0; i<this.currentSellers.size(); i++){
			ids.add(this.currentSellers.get(i).getTraderid());
		}
		List<Trader> newSellers =  th.loadFreeTradersExcept(ids,this.sellingCurrency,this.buyingCurrency);
		
		this.log.info("Adding " + newSellers.size() + " Sellers.");
		
		this.currentSellers.addAll(newSellers);

		return newSellers;
	}

	/**
	 * Collect buyers from the DB that are not already in the tree
	 * @return
	 */
	private List<Trader> getNewBuyers() {
		//Load in all free sellers of this currency Type and lock them
		TraderHibernation th = new TraderHibernation();
	
		List<Integer> ids = new ArrayList<Integer>();
		for(int i=0; i<this.currentBuyers.size(); i++){
			ids.add(this.currentBuyers.get(i).getTraderid());
		}
		List<Trader> newBuyers =  th.loadFreeTradersExcept(ids,this.buyingCurrency,this.sellingCurrency);
		
		this.log.info("Adding " + newBuyers.size() + " Buyers.");

		
		this.currentBuyers.addAll(newBuyers);

		return newBuyers;
	}

	

	/**
	 * Shutdown of matcher
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		
		//Should probably shutdown the thread via thread.interrupt()
		this.shutdown = true;
	}


	/**
	 * Startup of matcher
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		
		this.log = LogfileFactory.getHTMLLogger(Level.ALL, this.getClass());
		this.log.info("Starting Trade Tree Thread.");
		
		if(!TraderCombinationRedBlackTreeManager.RUNNING){
			return;
		}
	
		this.shutdown = false;

		
		
		/*Start the thread*/
		Thread newThread = new Thread(this);
		newThread.setName("Trade Tree Manager");
		newThread.start();
		
	}
	
	/**
	 * Test simulation
	 */
	public void test() {
	
	String usdCode = "USD";
	long usdRateToBase = 10000;
	String usdSymbol = "$";
	PyCurrencyType usd = new PyCurrencyType(usdCode,usdRateToBase,usdSymbol);
	
	String audCode = "AUD";
	long audRateToBase = 10000;
	String audSymbol = "$";
	PyCurrencyType aud = new PyCurrencyType(audCode,audRateToBase,audSymbol);
	
	/*Create the util */
	PyCurrencyUtil audUtil = new PyCurrencyUtil(aud);
	PyCurrencyUtil usdUtil = new PyCurrencyUtil(usd);
	
	/* Setup Group 1 To Buy AUD and Sell USD*/
	
	/* Fill group 1 */
	TraderGroupUtil buyerUtil = new TraderGroupUtil(aud,usd);
	User buyerUser = new User("tpacker","Terry","Packer","shithead","tpacker@terrypacker.com");
	List<Trader> buyers = new ArrayList<Trader>();
	
	
	/* Generate a The buyer values */
	PyCurrency toBuy = audUtil.toPyCurrencyFromValue(104);
	PyCurrency toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	Trader newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		buyerUtil.add(newTrader);
		buyers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = audUtil.toPyCurrencyFromValue(102);
	toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		buyerUtil.add(newTrader);
		buyers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = audUtil.toPyCurrencyFromValue(201);
	toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		buyerUtil.add(newTrader);
		buyers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = audUtil.toPyCurrencyFromValue(101);
	toSell = usdUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		buyerUtil.add(newTrader);
		buyers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


	
	
	/* Setup group 2 To Buy USD and Sell AUD */
	TraderGroupUtil sellerUtil = new TraderGroupUtil(usd,aud);
	List<Trader> sellers = new ArrayList<Trader>();
	User sellerUser = new User("jallemann","Jeanne","Allemann","shithead","jeanneallemann@hotmail.com");

	toBuy = usdUtil.toPyCurrencyFromValue(101);
	toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		sellerUtil.add(newTrader);
		sellers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = usdUtil.toPyCurrencyFromValue(102);
	toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		sellerUtil.add(newTrader);
		sellers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = usdUtil.toPyCurrencyFromValue(201);
	toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		sellerUtil.add(newTrader);
		sellers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	toBuy = usdUtil.toPyCurrencyFromValue(101);
	toSell = audUtil.toPyCurrencyFromBaseValue(toBuy.getBaseValue());
	newTrader = new Trader();
	newTrader.setCurrencyToBuy(toBuy);
	newTrader.setCurrencyToSell(toSell);
	try {
		sellerUtil.add(newTrader);
		sellers.add(newTrader);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	this.trimWithin = .000000001f;
	
	
	this.buyingCurrency = aud;
	this.sellingCurrency = usd;
	
	PyCurrency buyZero = new PyCurrency();
	buyZero.setType(this.buyingCurrency);
	buyZero.setValue(0);
	buyZero.setBaseValue(0);
	
	PyCurrency sellZero = new PyCurrency();
	sellZero.setType(this.sellingCurrency);
	sellZero.setValue(0);
	sellZero.setBaseValue(0);
	
	Trader zeroBuyer = new Trader();
	zeroBuyer.setCurrencyToBuy(buyZero);
	zeroBuyer.setCurrencyToSell(sellZero);
	
	Trader zeroSeller = new Trader();
	zeroSeller.setCurrencyToBuy(sellZero);
	zeroSeller.setCurrencyToSell(buyZero);
	
	TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
	TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
	TraderCombinationNode zeroNode = new TraderCombinationNode(zeroBuyers, zeroSellers);
	
	this.log = Logger.getLogger(this.getClass());
	this.log.info("Starting Trade Tree Thread.");
	
	this.currentBuyers = new ArrayList<Trader>();
	this.currentSellers = new ArrayList<Trader>();
	this.tree = new TraderCombinationRedBlackTree();//new TraderCombinationsTree(zeroNode);
	this.tree.put(0,zeroNode);
	
	try {
		this.insertBuyers(buyers);
		this.insertSellers(sellers);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	this.tree.printKeys();
	
	
}

/**
 * 
 * @param args
 * @throws IOException
 */
public static void main(String[] args) throws IOException{
	

	try {
		TraderCombinationRedBlackTreeManager treeMan = new TraderCombinationRedBlackTreeManager();
		treeMan.test();
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
/**
 * 
 * @param sellers
 * @throws Exception
 */
@Deprecated
public void insertSellers(List<Trader> sellers) throws Exception{
	
	
	for(int i=0; i<sellers.size(); i++){
		this.log.info("Inserting Seller " + i + ", Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();
		try{
			this.tree.insertSeller(sellers.get(i), this.cropAbove/this.tree.size());
			//this.tree.insertSeller(sellers.get(i), this.delta/this.tree.size(), this.cropAbove/this.tree.size());
			//this.insertSeller(sellers.get(i),this.maxGroupSize);
			//this.insertSeller(sellers.get(i));
		}catch (Exception e){
			this.log.info("Problem inserting seller: " + sellers.get(i).toString(),e);
		}
		//Trim the tree down
		long nanoNow = System.nanoTime();
		//this.tree.trimAndCropTree(this.delta/this.tree.getSize(), this.cropAbove/this.tree.getSize());
		this.tree.trimTree(this.trimWithin/this.tree.size());
		this.log.info("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
		
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.info("Insterting Seller took: " + (later-now) + "ms");
		TraderCombinationNode best = this.tree.getBest();
		this.log.info("Best Group: " + best.getTotalMismatch() + " with " + (best.getBuyers().size()+best.getSellers().size()) + " members.");
	}

}

/**
 * 
 * @param newSeller
 * @throws Exception
 */
@Deprecated
public void insertSeller(Trader newSeller) throws Exception{
	//Get a reference to current nodes in the tree
	List<TraderCombinationNode> nodes = this.tree.getNodes();
	
	//Mark the start size of tree, we will add nodes
	//  but it won't matter because the will be at the end of the list
	int startSize = nodes.size();
	
	//Group we will create
	TraderGroupUtil newBuyersGroup;
	TraderGroupUtil newSellersGroup;
	TraderCombinationNode newNode;
	
	//Add the trader to each
	//Insert these into tree
	for(int i=0; i<startSize; i++){
		newBuyersGroup = new TraderGroupUtil(nodes.get(i).getBuyers());
		newSellersGroup = new TraderGroupUtil(nodes.get(i).getSellers());
		newSellersGroup.add(newSeller);
		
		//Create the new Node
		newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
		this.tree.put(newNode);
		
	}

	
	
}

/**
 * Insert a seller with a list having maxSize
 * 
 * @param newSeller
 * @param maxSize
 * @throws Exception
 */
@Deprecated
public void insertSeller(Trader newSeller,int maxSize) throws Exception{
	//Get a reference to current nodes in the tree
	List<TraderCombinationNode> nodes = this.tree.getNodes();
	
	//Mark the start size of tree, we will add nodes
	//  but it won't matter because the will be at the end of the list
	int startSize = nodes.size();
	this.log.info("Creating " + startSize + " new Seller Groups");
	//Group we will create
	TraderGroupUtil newBuyersGroup;
	TraderGroupUtil newSellersGroup;
	TraderCombinationNode newNode;
	
	//Add the trader to each
	//Insert these into tree
	for(int i=0; i<startSize; i++){
		//Create the newGroup to add
		newBuyersGroup = new TraderGroupUtil(nodes.get(i).getBuyers());
		newSellersGroup = new TraderGroupUtil(nodes.get(i).getSellers());
		
		//If the list is not too big just add the buyer
		if(nodes.get(i).getSellers().size()<maxSize-1){

			//Add the buyer
			newSellersGroup.add(newSeller);
			//Create the new Node
			newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
			this.tree.put(newNode);
		}else{ //List is at max size, remove a buyer that will make the mismatch better
			
			//Get mismatch as real value
			long mismatch = Math.abs(nodes.get(i).getBuyers().getCurrencyToBuy().getValue() - nodes.get(i).getSellers().getCurrencyToSell().getValue()); 
			long bestMismatch = 0;
			long thisMismatch = 0;
			
			//Do we need to adjust (increase or decrease) the total
			if(mismatch != 0){
				//decrease
				bestMismatch = Math.abs(mismatch -newSeller.getCurrencyToSell().getValue() +newSellersGroup.get(0).getCurrencyToSell().getValue());
				int replace=0; //Mark postion of buyer to replace
				
				//It would help to have the sellers in a tree too
				for(int j=1; j<newSellersGroup.size(); j++){
					//We want to replace a buyer so that
					//Sellers->selling - Buyers->buying = newBuyer - oldBuyer
					thisMismatch = Math.abs(mismatch - newSeller.getCurrencyToSell().getValue() + newSellersGroup.get(j).getCurrencyToSell().getValue());
					if(thisMismatch<bestMismatch){
						bestMismatch = thisMismatch;
						replace = j;
					}
					
				}
				//Ok, now just replace the buyer at replace
				newSellersGroup.set(replace,newSeller);
				//Create the new Node
				newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
				this.tree.put(newNode);

			}
		}//end else at max size
		
	}
	//this.tree.trimTree(this.delta/this.tree.getSize());
	
}

/**
 * 
 * @param buyers
 * @throws Exception
 */
@Deprecated
public void insertBuyers(List<Trader> buyers) throws Exception{
	
	
	for(int i=0; i<buyers.size(); i++){
		this.log.info("Inserting Buyer " + i + ", Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();
		if(i > 7){
			int stuff = 1;
			stuff++;
		}
		try{
			this.tree.insertBuyer(buyers.get(i), this.cropAbove/this.tree.size());
			//this.tree.insertBuyer(buyers.get(i), this.delta/this.tree.size(), this.cropAbove/this.tree.size());
			//this.insertBuyer(buyers.get(i),this.maxGroupSize);
			//this.insertBuyer(buyers.get(i));
		}catch(Exception e){
			this.log.info("Problem Inserting Buyer: " + buyers.get(i),e);
		}

		//Trim the tree down
		long nanoNow = System.nanoTime();
		//this.tree.trimAndCropTree(this.delta/this.tree.getSize(),this.cropAbove/this.tree.getSize());
		this.tree.trimTree(this.trimWithin/this.tree.size());
		this.log.info("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");

		
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.info("Insterting Buyer took: " + (later-now) + "ms");
		TraderCombinationNode best = this.tree.getBest();
		this.log.info("Best Group: " + best.getTotalMismatch() + " with " + (best.getBuyers().size()+best.getSellers().size()) + " members.");
	}

}


/**
 * Insert a buyer into the tree.
 * 
 * @param newBuyer
 * @throws Exception
 */
@Deprecated
public void insertBuyer(Trader newBuyer) throws Exception{
	//Get a reference to current nodes in the tree
	List<TraderCombinationNode> nodes = this.tree.getNodes();
	
	//Mark the start size of tree, we will add nodes
	//  but it won't matter because the will be at the end of the list
	int startSize = nodes.size();
	
	//Group we will create
	TraderGroupUtil newBuyersGroup;
	TraderGroupUtil newSellersGroup;
	TraderCombinationNode newNode;
	
	//Add the trader to each
	//Insert these into tree
	for(int i=0; i<startSize; i++){
		newBuyersGroup = new TraderGroupUtil(nodes.get(i).getBuyers());
		newBuyersGroup.add(newBuyer);
		newSellersGroup = new TraderGroupUtil(nodes.get(i).getSellers());
		
		//Create the new Node
		newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
		this.tree.put(newNode);
		
	}
	
}

/**
 * Insert a buyer into the Tree, but limit the lists to maxSize.
 * @param newBuyer
 * @param maxSize
 * @throws Exception
 */
@Deprecated
public void insertBuyer(Trader newBuyer,int maxSize) throws Exception{
	long now,total;
	//Get a reference to current nodes in the tree
	List<TraderCombinationNode> nodes = this.tree.getNodes();
	//Mark the start size of tree, we will add nodes
	//  but it won't matter because the will be at the end of the list
	int startSize = nodes.size();

	this.log.info("Creating " + startSize + " new Buyer Groups");

	//Group we will create
	TraderGroupUtil newBuyersGroup;
	TraderGroupUtil newSellersGroup;
	TraderCombinationNode newNode;
	
	//Add the trader to each
	//Insert these into tree
	for(int i=0; i<startSize; i++){

		//Create the newGroup to add
		newBuyersGroup = new TraderGroupUtil(nodes.get(i).getBuyers());
		newSellersGroup = new TraderGroupUtil(nodes.get(i).getSellers());
		
		
		//If the list is not too big just add the buyer
		if(nodes.get(i).getBuyers().size()<maxSize-1){

			//Add the buyer
			newBuyersGroup.add(newBuyer);
			//Create the new Node
			newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
			this.tree.put(newNode);
	
		}else{ //List is at max size, remove a buyer that will make the mismatch better
			//Get mismatch as real value
			long mismatch = Math.abs(nodes.get(i).getBuyers().getCurrencyToBuy().getValue() - nodes.get(i).getSellers().getCurrencyToSell().getValue()); 
			long bestMismatch = 0;
			long thisMismatch = 0;
			
			//Do we need to adjust (increase or decrease) the total
			if(mismatch != 0){
				//decrease
				bestMismatch = Math.abs(mismatch + newBuyer.getCurrencyToBuy().getValue() - newBuyersGroup.get(0).getCurrencyToBuy().getValue());
				int replace=0; //Mark postion of buyer to replace
				//It would help to have the buyers in a tree too
				for(int j=1; j<newBuyersGroup.size(); j++){
					//We want to replace a buyer so that
					//Sellers->selling - Buyers->buying = newBuyer - oldBuyer
					thisMismatch = Math.abs(mismatch + newBuyer.getCurrencyToBuy().getValue() - newBuyersGroup.get(j).getCurrencyToBuy().getValue() );
					if(thisMismatch<bestMismatch){
						bestMismatch = thisMismatch;
						replace = j;
					}
					
				}
				//Ok, now just replace the buyer at replace
				newBuyersGroup.set(replace,newBuyer);
				//Create the new Node
				newNode = new TraderCombinationNode(newBuyersGroup,newSellersGroup);
				this.tree.put(newNode);
			}
		}//end else at max size
		
	}
	
}

	
}
