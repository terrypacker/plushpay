package com.payyourself.trading.tradeProfitTree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
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
import com.payyourself.trading.trader.group.TraderGroupUtil;

public class TradeProfitTreeManager extends Thread{

	private static final String CSV_HEADER = "tradeId,numBuyers,numSellers,buyerProfit,sellerProfit,roi,treeSize,computationTimeNs,computationTimeMs,computationTimeS,trimWithin,adjustmentFactor,maxComputationtime,maxTrimWithin,minTrimWithin,trimWithinIncrement,trimWithinDecrement\n";


	private Logger log;
	

	private TradeProfitTree tree;
	
	private List<Trader> currentSellers;
	private List<Trader> currentBuyers;

	private volatile boolean shutdown;

	private double trimWithin;

	private long maxComputationTime;

	private double adjustableTrimWithin;
	

	private double minTrimWithin;
	private double maxTrimWithin;
	
	private double trimWithinIncrement;
	
	private double trimWithinDecrement;

	//For new gain/roi methods
	private long minGain;
	private long maxCost;
	

	//For logging param data to CSV
	private boolean writeToFile;
	private BufferedWriter paramOutputFile;
	
	private static TradeProfitTreeManager singleton;
	
	public static TradeProfitTreeManager getTradeProfitTreeManager(){
		if (TradeProfitTreeManager.singleton == null)
			// it's ok, we can call this constructor
			TradeProfitTreeManager.singleton = new TradeProfitTreeManager();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	
	/**
	 * Empty Constructor for bean use.
	 */
	private TradeProfitTreeManager(){
		super("Trade Profit Tree Manager");
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
	}

	
	/**
	 * Insert a new buyer AND seller, might as well do 2 
	 * because we can do 2 as fast as 1.
	 * @param newBuyer
	 * @param newSeller
	 * @throws Exception 
	 */
	private void insertTraders(List<Trader> newBuyers, List<Trader> newSellers) throws Exception{
		
		long now;
		
		//Insert one at a time as long as they are same size
		if(newSellers.size()>newBuyers.size()){

			//Insert all buyers then insert remaining sellers
			for(int i=0; i<newBuyers.size(); i++){
				
				//Insert seller
				now = System.nanoTime(); //Record time
				this.insertSeller(newSellers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade()!= null){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
				
				
				//Insert buyer
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade()!= null){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
				
			}
			
			for(int i=newBuyers.size(); i<newSellers.size(); i++){
				
				now = System.nanoTime();
				this.insertSeller(newSellers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade()!= null){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
				
			}
			
		}else{

			//Insert all sellers then insert remaining buyers
			//Insert all buyers then insert remaining sellers
			for(int i=0; i<newSellers.size(); i++){
				
				now = System.nanoTime();
				this.insertSeller(newSellers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade()!= null){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
				
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade() != null){
					return;
				}
				
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
			}
			
			for(int i=newSellers.size(); i<newBuyers.size(); i++){
				//insert buyer
				now = System.nanoTime();
				this.insertBuyer(newBuyers.get(i));
				
				//Check if we are good to be done?
				if(this.foundTrade() != null){
					return;
				}
				//Adjust params
				this.adjustParameters(System.nanoTime()-now,this.tree.size());
			}
		}

		
	}
	
	/**
	 * 
	 * Insert new traders by 
	 * collecting traders from DB and 
	 * merge with existing list.
	 * 
	 * @param cropAbove
	 * @throws Exception
	 */
	private void insertNewTraders() throws Exception {
		
		List<Trader> newSellers = this.getNewSellers();
		List<Trader> newBuyers = this.getNewBuyers();
		this.insertTraders(newBuyers, newSellers);
		
		
	}
	
	/**
	 * 
	 * Insert one buyer into the tree
	 * 
	 * @param sellers
	 * @throws Exception
	 */
	private void insertBuyer(Trader buyer) throws Exception{

		this.log.debug("Inserting Buyer, Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();
		
		this.tree.insertBuyer(buyer);
		
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.debug("Insterting Buyer took: " + (later-now) + "ms");
		
		TradeProfitNode best = this.tree.getBest(this.minGain,this.maxCost);
		if(best != null)
			this.log.debug("Best Group: \n" + best.toString());
		
		

	}

	
	/**
	 * 
	 * @param sellers
	 * @throws Exception
	 */
	private void insertSeller(Trader seller) throws Exception{

		this.log.debug("Inserting Seller, Tree size: " + this.tree.size());
		long now = Calendar.getInstance().getTimeInMillis();

		this.tree.insertSeller(seller);
		long later = Calendar.getInstance().getTimeInMillis();
		this.log.debug("Insterting Seller took: " + (later-now) + "ms");
		
		TradeProfitNode best = this.tree.getBest(this.minGain,this.maxCost);
		if(best!=null)
			this.log.debug("Best Group: " + best.toString());
		
		

	}
	


	
	
	/**
	 * Check to see if best match in the tree
	 * is a valid trade.
	 * @return
	 */
	private TradeProfitNode foundTrade() {
		

		//TradeProfitNode best = this.tree.getBest();
		
		TradeProfitNode best = this.tree.getBest(this.minGain,this.maxCost); 
		
		if(best == null){
			return null;
		}
		
		//Is the overall gain good enough?
		if(best.getKey().getGain() >= this.minGain){
			
			//Are the losses on both sides acceptable?
			//if((best.getKey().getBuyerBuyTotal() > -10000)&&(best.getKey().getBuyerSellTotal() > -10000))
			if((best.getBuyers().size() > 1)&&(best.getSellers().size() > 1))
				return best;
		}
		
		return null;
	}



	/**
	 * 
	 */
	public void run() {

		this.log.debug("Starting Trade Profit Tree Thread.");
		
		//Should package this into the settings structure
		this.maxComputationTime = 100000000 * 2; //100000000; //500*1000000;//in nanoSeconds to insert one buyer

		
		//Should go through and re-work these so their meanings are not inverted.  STUPID
		//Adjustable parameters - Larger values mean faster but less accurate
		
		this.trimWithin = 0f; //.3f; //Trim list so final answer is within trimWithin % of best possible (so if max amnt tradeable is 100,000 to be within 1 dollar we need .0000001
		this.maxTrimWithin = .99999f;
		this.minTrimWithin = .0000000001f; //Too low and we keep the tree size up and can't add members.
		this.adjustableTrimWithin = this.trimWithin;
		this.trimWithinIncrement = .05f; //Speed that we can decrease tree size (Speed up)
		this.trimWithinDecrement = .2f;//Speed that we can increase accuracy (Slow down)
		
		this.writeToFile = true; //Write the param data to CSV
		
		this.minGain = 100000; //
		this.maxCost = 10000; 
		 
		int rebuildTree = 0; //Loop counter to force a rebuild of tree
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
				this.paramOutputFile.write(TradeProfitTreeManager.CSV_HEADER);
				
			} catch (URISyntaxException e) {
				//We have a real problem
				this.log.debug("Unable to Start Writing Param Data.",e);
			} catch (IOException e) {
				this.log.debug("Unable to Start Writing Param Data.",e);			}

		}
		
		Session tradeSession = HibernateUtil.getSessionFactory().getCurrentSession();
		tradeSession.beginTransaction();
		
		PyCurrencyTypeHibernation pyctch = new PyCurrencyTypeHibernation();
		
		PyCurrencyType buyingCurrency = pyctch.getCurrentUsd();
		PyCurrencyType sellingCurrency = pyctch.getCurrentAud();
		
		PyCurrency buyZero = new PyCurrency(0,buyingCurrency);
		//buyZero.setType(this.buyingCurrency);
		//buyZero.setValue(0);
		
		PyCurrency sellZero = new PyCurrency(0,sellingCurrency);
		//sellZero.setType(this.sellingCurrency);
		//sellZero.setValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TradeProfitNode zeroNode = new TradeProfitNode(zeroBuyers, zeroSellers);
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		this.tree = new TradeProfitTree();
		this.tree.put(zeroNode);
		
		//TODO CLose Hibernate Session and re-open for create trade
		
		
	
		while(!this.shutdown){
			
			
			//Need a way to determine if we need to rebuild the tree and adjust tuning params.
			//this.resetTree();

			//THEN CROP for best results.
			try {
				this.insertNewTraders();
			} catch (Exception e1) {
				this.log.error("Problem inserting trader!",e1);
			}
			

			//Never creating trades for now.
			TradeProfitNode best = this.foundTrade();
			if(best != null){

				this.log.debug("Creating Trade!");

				//TradeLink link = new TradeLink();
				//link.setBuyerBuyCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToBuy().getValue()-best.getSellers().getCurrencyToSell().getValue()));
				//link.setBuyerSellCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToSell().getValue()-best.getSellers().getCurrencyToBuy().getValue()));
				//link.setBuyers(buyersGroup);
				
				/*Remove the 0 value trader AND set all buyers status to DEPOSIT*/
				for(int i=0; i<best.getBuyers().size(); i++){
					//Set all trader status to DEPOSIT, awaiting deposit.
					best.getBuyers().get(i).setStatus(TraderStatus.DEPOSIT.name());
					if(best.getBuyers().get(i).getCurrencyToBuy().getValue() == 0){
						try {
							best.getBuyers().remove(i);
							i--; //Step back because we just removed one
						} catch (Exception e) {
							this.log.error("Problem removing 0 value trader.",e);
						}
					}

				}
				
				//Create Buyers Group
				TraderGroup buyersGroup = best.getBuyers().getAsTraderGroup();
				
				
				/*Remove the 0 value trader AND set sellers status to DEPOST*/
				for(int i=0; i<best.getSellers().size(); i++){
					best.getSellers().get(i).setStatus(TraderStatus.DEPOSIT.name());
					if(best.getSellers().get(i).getCurrencyToBuy().getValue() == 0){
						try {
							best.getSellers().remove(i);
							i--; //Step back because we just removed one
						} catch (Exception e) {
							this.log.error("Error removing 0 seller.",e);
						}
					}

				}

				//Create the sellers Group
				TraderGroup sellersGroup = best.getSellers().getAsTraderGroup();
				
				
				/*Create Trade In DB*/
				Trade newTrade = new Trade();
				newTrade.setBuyerGroup(buyersGroup);
				newTrade.setSellerGroup(sellersGroup);
				newTrade.setDateCreated(Calendar.getInstance());
				newTrade.setStatus(TradeStatus.DEPOSIT.name()); //Set the trade status
				//Should set the expiry date too
				
				
				
				
				//Compute the profit and log it
				long buyerProfit = buyersGroup.getCurrencyToSell().getValue() - sellersGroup.getCurrencyToBuy().getValue();
				long sellerProfit = sellersGroup.getCurrencyToSell().getValue() - buyersGroup.getCurrencyToBuy().getValue();
				this.log.debug("Trade Profits: " + buyerProfit + buyersGroup.getCurrencyToSell().getType().getCode() +
						" " + sellerProfit + sellersGroup.getCurrencyToSell().getType().getCode());
				
				TradeHibernation trh = new TradeHibernation();
				newTrade = trh.mergeAndUpdateMembers(newTrade);
				tradeSession.flush(); //Flush to DB
				
				
				//WRite an empty line to indicate a trade was made
				try {
					this.writeParamData(0.0,0L,this.tree.size(),
							buyerProfit,sellerProfit,
							best.getKey().getRoi(),
							best.getBuyers().size(),
							best.getSellers().size(),
							newTrade.getTradeId());
					
				} catch (IOException e) {
					this.log.error("Unable to write data to csv.");
				}

				
				//Notify the members of this trade that they need to put their funds into the Bank
				this.log.debug("Sending Trade Emails");
				TradeEmailGenerator genEmail = new TradeEmailGenerator(newTrade);
				genEmail.sendEmails();

				
				
				//link.setSellers(sellersGroup);
		
				//Add to existing trade chain or create new

				this.resetTree();
				
				this.resetTuningParameters();
			}else{//end if we got a trade.
				
				//No Trade found this iteration
				if(rebuildTree == 10){
					this.log.info("Forcing a rebuild of Tree.");
					rebuildTree = 0;
					this.resetTree();
					
					this.resetTuningParameters();
				}else{
					rebuildTree++;
				}
				/*//Try something new, if we have the same best for n interations then we should try rebuilding the tree?
				if(this.lastBest != this.tree.getBest().getTotalMismatch()){
					this.lastBest = this.tree.getBest().getTotalMismatch();
					this.sameBestCtr = 0; //Reset counter
				}else{
					//It was the same, increment the counter
					this.sameBestCtr++;
				}
				
				if(this.sameBestCtr > 10){
					this.lastBest = 0;
					this.log.debug("Not Getting any better, rebuilding tree.");
					this.sameBestCtr = 0;//reset counter
					this.resetTree(); //Reset Tree and params
					//this.resetTuningParameters();
				
				}*/
			}//end else if we got a trade

			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				if(this.shutdown){
					break;
				}
				this.log.error("Interrupted while sleeping.",e);
			}//end catch
		}//end while
		
		if(this.writeToFile){
			try {
				this.paramOutputFile.flush();
				this.paramOutputFile.close();
			} catch (IOException e1) {
				this.log.error("Unable to close logfile.",e1);
			}
		}

		this.log.info("Shutting Down.");
		
	}


	/**
	 * Reset the tuning parameters to their initial values
	 */
	private void resetTuningParameters() {
		
		this.adjustableTrimWithin = this.trimWithin;
		
	}
	
	/**
	 * Reset tree to empty 
	 */
	private void resetTree(){
		
		//TODO Fix when we open/close session during tree operation
		//Session tradeSession = HibernateUtil.getSessionFactory().getCurrentSession();
		//tradeSession.beginTransaction();
		
		PyCurrencyTypeHibernation pyctch = new PyCurrencyTypeHibernation();
		
		PyCurrencyType buyingCurrency = pyctch.getCurrentUsd();
		PyCurrencyType sellingCurrency = pyctch.getCurrentAud();
		
		
		PyCurrency buyZero = new PyCurrency();
		buyZero.setType(buyingCurrency);
		buyZero.setValue(0);
		
		PyCurrency sellZero = new PyCurrency();
		sellZero.setType(sellingCurrency);
		sellZero.setValue(0);
		
		Trader zeroBuyer = new Trader();
		zeroBuyer.setCurrencyToBuy(buyZero);
		zeroBuyer.setCurrencyToSell(sellZero);
		
		Trader zeroSeller = new Trader();
		zeroSeller.setCurrencyToBuy(sellZero);
		zeroSeller.setCurrencyToSell(buyZero);
		
		TraderGroupUtil zeroBuyers = new TraderGroupUtil(zeroBuyer);
		TraderGroupUtil zeroSellers = new TraderGroupUtil(zeroSeller);
		TradeProfitNode zeroNode = new TradeProfitNode(zeroBuyers, zeroSellers);
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		this.tree.resetTree(); //Clear the tree
		this.tree.put(zeroNode);
		
		//TODO Close session here
		
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
	private void adjustParameters(long computationTime, int treeSize) throws Exception{

		//long computedRate = (long) (computationTime/treeSize);
		this.log.debug("Computation Time: " + computationTime + " Max Allowed is: " + this.maxComputationTime);
		
		//Compute a factor to increase or decrease the parameters by.
		double factor = (double)(computationTime)/(double)this.maxComputationTime;
		//double factor = 1; // if it is dependent on time we will never get repeatable results due to scheduling in the CPU
		
		//TODO The factor should also depend on Tree Size
		
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
				this.log.debug("Reducing Trim Within from " + oldValue + " to " + this.adjustableTrimWithin);
			}else{
				this.log.debug("Trim Within at Minimum Allowed.");
			}
			
			
		}else{
			//Decrease tree size to speed us up.

			
			//Crop the tree
			/*this.log.debug("Cropping Tree, size: " + this.tree.size());
			nanoNow = System.nanoTime();
			this.tree.cropTree(this.adjustableCropAbove); //Hack off a big section of tree
			this.log.debug("Cropping Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
			this.log.debug("Tree size now: " + this.tree.size());
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
					this.log.debug("Increasing Trim Within from " + oldValue + " to " + this.adjustableTrimWithin);
				}else{
					this.log.debug("Trim Within at Maximum Allowed.");
				}
				
			
				
				//Trim the tree
				this.log.debug("Trimming Tree, size: " + this.tree.size());
				long nanoNow = System.nanoTime();
				
				//this.tree.trimTree(this.adjustableTrimWithin/this.tree.size());
				this.tree.trimTree(this.adjustableTrimWithin);
				//Should check the amount trimmed from tree and use this debugrmation.
				
				this.log.debug("Trimming Tree took: " + ((System.nanoTime()-nanoNow)/1000000) + "ms");
				this.log.debug("Tree size now: " + this.tree.size());
				
				//IF we are really getting slow.
				if(computationTime > 2* this.maxComputationTime){
					//We need to do something drastic, we are blowing out
					this.log.debug("Disaster Control Needed, tree size: " + this.tree.size());
				}

		}

		TradeProfitNode best = this.tree.getBest(this.minGain,this.maxCost);
		long buyerProfit = 0;
		long sellerProfit = 0;
		float roi = 0;
		int numBuyers = 0;
		int numSellers = 0;
		
		if(best != null){
			buyerProfit = best.getBuyers().getCurrencyToSell().getValue() - best.getSellers().getCurrencyToBuy().getValue();
			sellerProfit = best.getSellers().getCurrencyToSell().getValue() - best.getBuyers().getCurrencyToBuy().getValue();
			roi = best.getKey().getRoi();
			numBuyers = best.getBuyers().size();
			numSellers = best.getSellers().size();
		}
		
		//Write the parameter data to a CSV file for analysis later
		//This should be re-written to take a settings object
		this.writeParamData(factor,computationTime,this.tree.size(),
				buyerProfit,sellerProfit,
				roi,
				numBuyers,
				numSellers,-1);

		
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
	private void writeParamData(double factor,long computationTime, int treeSize, long buyerProfit, 
			long sellerProfit, float roi, int numBuyers, int numSellers, long tradeId) throws IOException {
		
		String out = tradeId + "," + numBuyers + "," + numSellers + "," + ((float)buyerProfit/10000f) + "," + ((float)sellerProfit/10000f) + "," + roi + "," + treeSize + "," +  computationTime + "," + (computationTime/1000000) + "," +
					((float)computationTime/1000000000f) + "," +
					+ this.adjustableTrimWithin + ","+ factor + "," +
					this.maxComputationTime + ","  +
					this.maxTrimWithin + "," + this.minTrimWithin + "," +
					this.trimWithinIncrement + "," + this.trimWithinDecrement + "\n";
					
		
		//Write the data to a file
		this.paramOutputFile.write(out);
		this.paramOutputFile.flush();
	}

	
	/**
	 * Collect sellers from DB that are not already in the tree
	 * @return
	 */
	private List<Trader> getNewSellers() {
		//Load in all free sellers of this currency Type and lock them
		TraderHibernation th = new TraderHibernation();
	
		List<Long> ids = new ArrayList<Long>();
		for(int i=0; i<this.currentSellers.size(); i++){
			ids.add(this.currentSellers.get(i).getTraderid());
		}
		List<Trader> newSellers =  th.loadFreeTradersExcept(ids,CurrencyCodeEnum.AUD,CurrencyCodeEnum.USD);
		
		this.log.debug("Adding " + newSellers.size() + " Sellers.");
		
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
	
		List<Long> ids = new ArrayList<Long>();
		for(int i=0; i<this.currentBuyers.size(); i++){
			ids.add(this.currentBuyers.get(i).getTraderid());
		}
		List<Trader> newBuyers =  th.loadFreeTradersExcept(ids,CurrencyCodeEnum.USD,CurrencyCodeEnum.AUD);
		
		this.log.debug("Adding " + newBuyers.size() + " Buyers.");

		
		this.currentBuyers.addAll(newBuyers);

		return newBuyers;
	}

	

	/**
	 * Shutdown of matcher
	 */
	public void shutDown() {

		this.shutdown = true;
		this.interrupt();

	}

	public void startUp() {
		// TODO Check thread state before starting
		this.shutdown = false;
		this.start();
		
	}




	
}
