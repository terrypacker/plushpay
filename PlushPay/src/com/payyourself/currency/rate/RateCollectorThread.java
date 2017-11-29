package com.payyourself.currency.rate;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;

public class RateCollectorThread extends Thread {

	
	private RateCollector collector;
	private PyCurrencyTypeHibernation persister;
	private List<PyCurrencyType> rates;
	private Logger log;
	private volatile boolean shutdown;
	private long period;

	private static RateCollectorThread singleton;
	
	public static RateCollectorThread getRateCollectorThread(){
		if (RateCollectorThread.singleton == null)
			// it's ok, we can call this constructor
			RateCollectorThread.singleton = new RateCollectorThread();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	private RateCollectorThread(){
		super("Rate Collector");
		this.collector = RateCollector.getInstance(); //Get singleton
		
		
		this.log = LogfileFactory.getHTMLLogger(Level.ALL, this.getClass());
		this.shutdown = false;
		
		this.period = 5000; //1000*60*60; //For now, later we will get this from the DB.

		
	}
	
	
	public void shutDown() {
		this.shutdown = true;
		
		this.interrupt(); //Interrupt thread with shutdown signal
		
	}

	public void run() {
		
		this.log.info("Starting Rate Collection Thread.");
		this.persister = new PyCurrencyTypeHibernation();
		
		//Endless Loop, wake up every 10 minutes and check for new Rates
		while(!this.shutdown){


			//Begin Transaction
			Session sesh = HibernateUtil.getSessionFactory().getCurrentSession();
			sesh.beginTransaction();
			
			//Get all the types we use
			this.rates = persister.loadAllTypes();

			try {
				this.collector.getRates(this.rates);
			} catch (IOException e1) {
				this.log.error("Unable to get Rates.",e1);
			} catch (ParseException e1) {	
				this.log.error("Unable to get Rates.",e1);
			}

			this.log.info("Inserting New Rates into Database.");
			for(int i=0; i<this.rates.size(); i++){
				this.log.info("Setting: " + this.rates.get(i).getCode() + " to " + this.rates.get(i).getRateToBase());
			}
			this.persister.persist(this.rates);
			sesh.flush();

			try {
				Thread.sleep(this.period); //Collect data once per hour.
			} catch (InterruptedException e) {
				if(this.shutdown){
					break;
				}else{
					this.log.error("Problem in Rate collection thread.");
				}
			}
			
		}//end while
		this.log.info("Shutting Down");
		
	}
	
	
	
	
}
