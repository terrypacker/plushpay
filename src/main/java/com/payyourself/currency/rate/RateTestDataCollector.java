package com.payyourself.currency.rate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.rate.testData.RateTestData;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;

public class RateTestDataCollector extends Thread{

	private PyCurrencyTypeHibernation persister;
	
	//private PyCurrencyType usdType;
	//private PyCurrencyType audType;
	
	private Logger log;
	private volatile boolean shutdown;
	private long period;

	private RateTestData data;
	private int currentDataPos;
	
	private static RateTestDataCollector singleton;
	
	public static RateTestDataCollector getRateTestDataCollector(){
		if (RateTestDataCollector.singleton == null)
			// it's ok, we can call this constructor
			RateTestDataCollector.singleton = new RateTestDataCollector();		
		return singleton;
	}
	
	  public Object clone()
		throws CloneNotSupportedException
	  {
	    throw new CloneNotSupportedException(); 
	    // that'll teach 'em
	  }
	
	private RateTestDataCollector(){
		super("Rate Test Data Collector");

		this.log = LogfileFactory.getHTMLLogger(Level.ALL, this.getClass());
		this.shutdown = false;
		
		this.period = 10000; //1000*60*60; //For now, later we will get this from the DB.
		
		this.data = new RateTestData();
		this.persister = new PyCurrencyTypeHibernation();
		
		this.currentDataPos = 0;
	}
	
	
	public void shutDown() {
		this.shutdown = true;
		
		this.interrupt(); //Interrupt thread with shutdown signal
		
	}

	public void run() {
		
		this.log.info("Starting Rate Test Data Collection Thread.");
		
		//Get the test file name
		//Pick the folder location
		//String filename = Thread.currentThread().getContextClassLoader().getResource("com").getPath(); //Get the directory to com
		//File file = new File(filename+"/hourlyExRates.csv");
		
		URL url = RateTestDataCollector.class.getResource("/com/payyourself/testing/data/hourlyExRates.csv");
		try {
			this.data.loadData(new File(url.getFile()));
		} catch (ParseException e2) {
			this.log.error("Unable to parse data file.");
		} catch (IOException e2) {
			this.log.error("Unable to read from data file");
		}
		
		//Endless Loop, wake up every 10 minutes and check for new Rates
		while(!this.shutdown){


			//Begin Transaction
			Session sesh = HibernateUtil.getSessionFactory().getCurrentSession();
			sesh.beginTransaction();
			
			//Get all the types we use
			//this.audType = this.persister.getCurrentAud();
			
			
			PyCurrencyType audType = this.getNextAudRate();

			audType = this.persister.merge(audType);
			sesh.flush();

			this.log.info("Setting " + audType.getCode() + " rate to " + audType.getRateToBase() + " with id " + audType.getId());
			

			try {
				Thread.sleep(this.period); //Collect data once per hour.
			} catch (InterruptedException e) {
				if(this.shutdown){
					break;
				}else{
					this.log.error("Problem in Rate collection thread.");
				}
			}
			
			//Be sure to reset it
			if(this.currentDataPos == this.data.getData().size()){
				this.currentDataPos = 0;
			}
			
		}//end while
		this.log.info("Shutting Down");
		
	}

	/**
	 * Get the next rates from the data
	 */
	private PyCurrencyType getNextAudRate() {

		Calendar now = Calendar.getInstance();
		long newAudRate = (long)(this.data.getData().get(this.currentDataPos).getClose()*10000);
		
		PyCurrencyType aud = new PyCurrencyType(CurrencyCodeEnum.AUD,newAudRate,"$",now);
		this.currentDataPos++; //Move to next
		
		return aud;
		
	}

	public void startUp() {
		// TODO Check thread state before starting
		this.shutdown = false;
		this.start();
		
	}
	
}
