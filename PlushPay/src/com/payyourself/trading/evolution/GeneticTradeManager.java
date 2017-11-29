package com.payyourself.trading.evolution;

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


public class GeneticTradeManager extends Thread{

	private static final String CSV_HEADER = "Algo Interval,Buyer Profit,Seller Profit,Number Buyers,NumberSellers,Evolution Time (ms),Population Size\n";

	private static final int DESTROY_COUNT = 100;

	private Logger log;
	
	private Population population;
	
	
	private List<Trader> currentSellers;
	private List<Trader> currentBuyers;

	private volatile boolean shutdown;


	private boolean writeToFile;


	private BufferedWriter paramOutputFile;


	private long lastEvolutionTime;

	private int apocalypse;

	private long minProfit;

	
	private static GeneticTradeManager singleton;
	
	public static GeneticTradeManager getGeneticTradeManager(){
		if (GeneticTradeManager.singleton == null)
			// it's ok, we can call this constructor
			GeneticTradeManager.singleton = new GeneticTradeManager();		
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
	private GeneticTradeManager(){
		super("Geneict Trade Manager");
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		this.reset();
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
				this.paramOutputFile.write(GeneticTradeManager.CSV_HEADER);
				
			} catch (URISyntaxException e) {
				//We have a real problem
				this.log.debug("Unable to Start Writing Param Data.",e);
			} catch (IOException e) {
				this.log.debug("Unable to Start Writing Param Data.",e);			}

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
				this.evolveSolution();
			} catch (Exception e1) {
				this.log.error("Problem inserting trader!",e1);
			}
			

			//Never creating trades for now.
			Creature best = this.foundTrade();
			if(best != null){

				this.log.debug("Creating Trade: Buyer Profit " + best.getBuyerProfit() + " Seller Profit " + best.getSellerProfit());

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
				TraderGroup buyersGroup = best.getBuyerAsTraderGroup();
				
				
				/*Remove the 0 value trader AND set sellers status to DEPOST*/
				for(int i=0; i<best.getSellers().size(); i++){
					best.getSellers().get(i).setStatus(TraderStatus.DEPOSIT.name());

				}

				//Create the sellers Group
				TraderGroup sellersGroup = best.getSellersAsTraderGroup();
				
				
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
				
			}else{//end if we got a trade.
				if(this.apocalypse == GeneticTradeManager.DESTROY_COUNT){
					this.reset();
				}else{
					this.apocalypse++;
				}
				//No Trade found this iteration
			}//end else if we got a trade

			
			try {
				Thread.sleep(250);
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


	

	

	private void writeStats(Creature best) throws IOException {
		
		if(this.writeToFile){
			String data = this.apocalypse + "," + best.getBuyerProfit() + ","+ best.getSellerProfit()  + "," +
				 + best.getBuyers().size() + "," +
				best.getSellers().size() + ","
				+ this.lastEvolutionTime + "," + this.population.size() + "\n";
			this.paramOutputFile.write(data);
			this.paramOutputFile.flush();
		}
		
	}

	/**
	 * Evolve the solution using the available 
	 * traders
	 * @throws  
	 */
	private void evolveSolution() {
		
		this.population.addAvailableBuyers(this.getNewBuyers());
		this.population.addAvaiableSellers(this.getNewSellers());
		
		try{
			long now = System.nanoTime();
			this.population.evolve();
			this.lastEvolutionTime = System.nanoTime()-now;
		}catch(Exception e){
			this.log.error("Evolution Failed: ",e);
		}
		
		this.log.info("Population Size: " + this.population.size() );
		
		
	}

	private Creature foundTrade() {
		
		Creature best= this.population.getBest();

		if(best == null)
			return null;

		
		try {
			this.writeStats(best);
		} catch (IOException e) {
			this.log.error(e);
		}
		
		this.log.info("Interval ("+ this.apocalypse  + ") took " + ((float)this.lastEvolutionTime*.000001f) + "ms :"  + " Best Fitness: " + best.getFitness() + " Best Buyer Profit: " + best.getBuyerProfit() +
				" Best Seller Profit: " + best.getSellerProfit());
		
				
		
		if((best.getBuyerProfit() + best.getSellerProfit() > this.minProfit)){
			return best;
		}else{
			return null;
		}
		
	}

	private void reset() {
		
		this.log.info("Resetting the population.");
		
		this.currentBuyers = new ArrayList<Trader>();
		this.currentSellers = new ArrayList<Trader>();
		
		this.population = new Population();
		this.minProfit = 100000; //USD
		this.apocalypse = 0; //Reset the impending doom for all creatures.
		this.writeToFile = true; //Log the operation
		
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
