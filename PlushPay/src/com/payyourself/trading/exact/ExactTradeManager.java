package com.payyourself.trading.exact;

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

import com.payyourself.currency.code.CurrencyCodeEnum;

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

public class ExactTradeManager extends Thread{

	private static final String CSV_HEADER = "Algo Interval,ROI,Gain,Cost,Number Buyers,NumberSellers,Computation Time,Number of Combinations\n";


	private Logger log;

	private TradeCombinationGenerator gen;
	private Combination best;
	
	private List<Trader> currentSellers;
	private List<Trader> currentBuyers;

	private volatile boolean shutdown;

	private boolean writeToFile;

	private BufferedWriter paramOutputFile;

	private long minGain;

	private long maxCost;

	private long lastComputationTime;
	private long maxComputationTime; //in ms

	//Tuning params
	private int numBuyers;
	private int numSellers;
	private int buyerStartPos;
	private int sellerEndPos;


	private int buyerEndPos;


	private int sellerStartPos;
	
	
	private static ExactTradeManager singleton;
	
	public static ExactTradeManager getExactTradeManager(){
		if (ExactTradeManager.singleton == null)
			// it's ok, we can call this constructor
			ExactTradeManager.singleton = new ExactTradeManager();		
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
	private ExactTradeManager(){
		super("Geneict Trade Manager");
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
	}

	/**
	 * 
	 */
	public void run() {

		this.log.debug("Starting Genetic Trade Manager.");
		

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
				this.paramOutputFile.write(ExactTradeManager.CSV_HEADER);
				
			} catch (URISyntaxException e) {
				//We have a real problem
				this.log.debug("Unable to Start Writing Param Data.",e);
			} catch (IOException e) {
				this.log.debug("Unable to Start Writing Param Data.",e);			
			}

		}
		
		Session tradeSession = HibernateUtil.getSessionFactory().getCurrentSession();
		tradeSession.beginTransaction();
		
		//TODO CLose Hibernate Session and re-open for create trade
		
		this.reset(); //Setup the initial params
		
		while(!this.shutdown){
			
			
			//Need a way to determine if we need to rebuild the tree and adjust tuning params.
			//this.resetTree();

			//THEN CROP for best results.
			try {
				this.computeSolution();
			} catch (Exception e1) {
				this.log.error("Problem inserting trader!",e1);
			}
			

			//Never creating trades for now.
			Combination best = this.foundTrade();
			if(best != null){

				this.createTrade(best,tradeSession);
				
			}else{//end if we got a trade.
				//No Trade found this iteration
			}//end else if we got a trade

			
			try {
				Thread.sleep(this.lastComputationTime/4); //Duty cycle of 75% (To regulate CPU work)
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


	

	

	private void createTrade(Combination best, Session tradeSession) {
		
		this.log.debug("Creating Trade: Gain of " + best.getGain() + " Cost of " + best.getCost());

		//TradeLink link = new TradeLink();
		//link.setBuyerBuyCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToBuy().getValue()-best.getSellers().getCurrencyToSell().getValue()));
		//link.setBuyerSellCurrencyMismatch(Math.abs(best.getBuyers().getCurrencyToSell().getValue()-best.getSellers().getCurrencyToBuy().getValue()));
		//link.setBuyers(buyersGroup);
		
		/*Remove the 0 value trader AND set all buyers status to DEPOSIT*/
		for(int i=0; i<best.getBuyers().size(); i++){
			//Set all trader status to DEPOSIT, awaiting deposit.
			best.getBuyers().get(i).setStatus(TraderStatus.DEPOSIT.name());

		}
		
		//Create Buyers Group
		TraderGroup buyersGroup = null;
		try {
			buyersGroup = best.getBuyerAsTraderGroup();
		} catch (Exception e1) {
			this.log.error(e1);
			return;
		}
		
		
		/*Remove the 0 value trader AND set sellers status to DEPOST*/
		for(int i=0; i<best.getSellers().size(); i++){
			best.getSellers().get(i).setStatus(TraderStatus.DEPOSIT.name());

		}

		//Create the sellers Group
		TraderGroup sellersGroup = null;
		try {
			sellersGroup = best.getSellersAsTraderGroup();
		} catch (Exception e) {
			this.log.error(e);
			return;
		}
		
		
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
		
		
		//WRite an empty line to the file to indicate a trade was made
		
		
		//Notify the members of this trade that they need to put their funds into the Bank
		this.log.debug("Sending Trade Emails");
		TradeEmailGenerator genEmail = new TradeEmailGenerator(newTrade);
		genEmail.sendEmails();

		
		
		//link.setSellers(sellersGroup);

		//Add to existing trade chain or create new

		this.reset();

		
	}

	private void writeStats(Combination best) throws IOException {
		
		if(this.writeToFile){
			String data = best.getRoi() + ","+ best.getGain()  + "," + best.getCost() + "," +
				 + best.getBuyers().size() + "," +
				best.getSellers().size() + ","
				+ this.lastComputationTime + "," + this.gen.size() + "\n";
			this.paramOutputFile.write(data);
			this.paramOutputFile.flush();
		}
		
	}

	/**
	 * Compute the solution using the available 
	 * traders in numThreads number of threads.
	 * 
	 * 
	 * @throws  
	 */
	private void computeSolutionThreaded() {

		//TODO Find the available memory and do a quick calculation of how many buyers/sellers we can process at once.
		
		System.out.println("Num Processors: " + Runtime.getRuntime().availableProcessors());
		//Collect a few options and try them
		List<ExactTradeThread> threads = new ArrayList<ExactTradeThread>();
		int numThreads = 10;
		try{
			long nowish = System.nanoTime();
			//Create and start the threads
			for(int i=0; i<numThreads; i++){
				ExactTradeThread thread = new ExactTradeThread(this.currentBuyers.subList(this.buyerStartPos, this.buyerEndPos),
						this.currentSellers.subList(this.sellerStartPos, this.sellerEndPos));
				this.log.info("Starting Thread: " + i);
				thread.startThread();
				threads.add(thread);
				
				//Compute the selection positions for next computation
				this.sellerEndPos = this.sellerEndPos - 1;
				//Keep walking
				this.sellerStartPos = this.sellerEndPos - this.numSellers;
				
				if(this.sellerStartPos < 0){
					//Stop here and wait for buyers to finish walking
					this.sellerStartPos = 0;
					this.sellerEndPos = this.sellerEndPos + 1;
				}
				
				this.buyerStartPos = this.buyerStartPos + 1;
				//Keep walking
				this.buyerEndPos = this.buyerStartPos + this.numBuyers;			
				
				if(this.buyerEndPos >= this.currentBuyers.size()){
					//Stop here and wait for sellers to finish walking
					this.buyerEndPos = this.currentBuyers.size()-1;
					this.buyerStartPos = this.buyerStartPos - 1; //Step back one to stay in same place
				}else{
					if(this.sellerStartPos == 0){
						//TODO THis logic isn't totally correct.
						this.reset();
					}
				}
			}
			
			//Wait for them to finish
			while(threads.size() > 0){
				Thread.yield();
				if(!threads.get(0).isRunning()){
					this.log.info("Thread Stopped.");
					if(this.best == null){
						this.best = threads.get(0).getBest();
						
					}else{
						if(this.best.getRoi()< threads.get(0).getBest().getRoi()){
							this.best = threads.get(0).getBest();
						}
					}
					threads.remove(0);
				}//end if running
				
			}
			this.lastComputationTime = (long) ((System.nanoTime() - nowish)*.000001);
		}catch(Exception e){
			this.log.error(e);
		}
		
		//Did we find a trade?
		this.foundTrade();
		
		
		
	}

	/**
	 * Compute one solution from 
	 * a selection of buyers and sellers.
	 */
	private void computeSolution(){
				
		
		try{
			long now = System.nanoTime();
			this.gen = new TradeCombinationGenerator(this.currentBuyers.subList(this.buyerStartPos, this.buyerEndPos),
					this.currentSellers.subList(this.sellerStartPos, this.sellerEndPos));
			//this.best = this.gen.getBest();
			
			//List<List<Trader>> traders = this.findBestGrouping();
			
			//this.gen  = new OptimizedTradeCombinationGenerator(traders.get(0),traders.get(1));

			
			//this.gen  = new OptimizedTradeCombinationGenerator(this.currentBuyers.subList(this.buyerStartPos, this.buyerEndPos),
			//		this.currentSellers.subList(this.sellerStartPos, this.sellerEndPos));

			this.best = this.gen.getBest();
			this.lastComputationTime = (long) ((System.nanoTime()-now)*.000001);
		}catch(Exception e){
			this.log.error("Evolution Failed: ",e);
		}
		
		this.log.info("Number of combinations: " + this.gen.size() );

		//Compute the selection positions for next computation
		this.buyerEndPos = this.buyerEndPos - 1;
		//Keep walking
		this.buyerStartPos = this.buyerEndPos - this.numBuyers;
		
		if(this.buyerStartPos < 0){
			//Stop here and wait for buyers to finish walking
			this.buyerStartPos = 0;
			this.buyerEndPos = this.buyerEndPos + 1;
		}
		
		this.sellerStartPos = this.sellerStartPos + 1;
		//Keep walking
		this.sellerEndPos = this.sellerStartPos + this.numSellers;			
		
		if(this.sellerEndPos >= this.currentSellers.size()){
			//Stop here and wait for sellers to finish walking
			this.sellerEndPos = this.currentSellers.size()-1;
			this.sellerStartPos = this.sellerStartPos - 1; //Step back one to stay in same place
		}else{
			if(this.sellerStartPos == 0){
				//TODO THis logic isn't totally correct.
				this.reset();
			}
		}

	}
	
	/**
	 * Search the available Traders and find a
	 * grouping that contains no doubles of rates buyer-seller
	 * @return
	 */
	private List<List<Trader>> findBestGrouping() {
		
		List<List<Trader>> traders = new ArrayList<List<Trader>>();
		
		//Get some buyers (already sorted)
		List<Trader> buyers = this.currentBuyers.subList(this.buyerStartPos, this.buyerEndPos);
		
		List<Trader> sellers = this.currentSellers.subList(this.sellerStartPos, this.sellerEndPos);

		//We want variations on the rate from buyer to seller
		float avgBuyerRate = 0f;
		float avgSellerRate = 0f;
		
		for(Trader buyer : buyers){
			avgBuyerRate = avgBuyerRate + buyer.getCurrencyToSell().getType().getRateToBase();
		}
		avgBuyerRate = avgBuyerRate/(float)buyers.size();
		
		for(Trader seller : sellers){
			avgSellerRate = avgSellerRate + seller.getCurrencyToBuy().getType().getRateToBase();
		}
		avgSellerRate = avgSellerRate/(float)sellers.size();
	
		this.log.info("Buyer Rate: " + avgBuyerRate);
		this.log.info("Seller Rate: " + avgSellerRate);
		
		//Using this compute the value we will require to get ten dollars of profit
		float value = Math.abs(avgBuyerRate - avgSellerRate);
		
		value = 10000*10000/value;
		
		this.log.info("Required Volume: " + value);
		
		traders.add(buyers);
		traders.add(sellers);
		return traders;
		
	}

	/**
	 * Using maxCost and minGain
	 * determine if best is good 
	 * enough for a trade.
	 * @return
	 */
	private Combination foundTrade() {

		if(this.best == null)
			return null;

		this.log.info("Best Gain:"  + best.getGain() + " Best Cost: " + best.getCost() + " Best ROI: " + best.getRoi() 
				+ " Computation Time (ms):" + this.lastComputationTime);
		
		try {
			this.writeStats(best);
		} catch (IOException e) {
			this.log.error(e);
		}
		
				
		
		if((best.getCost() < this.maxCost)&&(best.getGain() > this.minGain)){
			return best;
		}else{
			return null;
		}
		
	}

	private void reset() {
		
		this.log.info("Resetting the population.");
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		
		this.best = null;
		this.minGain = 100000;
		this.maxCost = 10000;
		this.writeToFile = false; //Log the operation
		
		//Get new data from DB
		this.getNewBuyers();
		this.getNewSellers();

		this.numBuyers = 5;
		this.numSellers = 5;

		this.buyerStartPos = this.currentBuyers.size() - this.numBuyers;
		this.buyerEndPos = this.currentBuyers.size();
		
		this.sellerEndPos = this.numSellers;
		this.sellerStartPos = 0;
		
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
		List<Trader> newSellers =  th.loadSortedFreeTradersExcept(ids,CurrencyCodeEnum.AUD,CurrencyCodeEnum.USD);
		
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
		List<Trader> newBuyers =  th.loadSortedFreeTradersExcept(ids,CurrencyCodeEnum.USD,CurrencyCodeEnum.AUD);
		
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
