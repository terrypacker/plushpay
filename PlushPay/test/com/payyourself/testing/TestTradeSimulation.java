package com.payyourself.testing;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.apache.cactus.ServletTestCase;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.payyourself.banking.au.AudBankSimulator;
import com.payyourself.log.LogfileFactory;
import com.payyourself.persistence.HibernateUtil;


/**
 * Test Trading system.  This assumes that all simulation 
 * and trading subsystems are running.
 * 
 * 
 * 
 * @author tpacker
 *
 */
public class TestTradeSimulation extends ServletTestCase {

	private Logger log;
	private Session sesh;
	
	
	
	public TestTradeSimulation(){
		this.log = LogfileFactory.getHTMLLogger(this.getClass());
		
	}
	
	public void setUp(){
		this.sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		this.sesh.beginTransaction();
	}
	
	public void tearDown(){
		this.sesh.flush();
		this.sesh.disconnect();
		this.sesh.close();
	}

	
	/**
	 * Compare the bank's transaction history 
	 * with the closed trades in the database.
	 */
	public void testTransactionHistory(){
		
		//Step 1 Get the Bank's transaction History
		//AudBankSimulator sim = (AudBankSimulator)this.getBean("audBank");
		
		
		//Step 2 Get the FINALIZED trades from the DB
		
		//Compare the Bank Credits to the FINALIZED TRADES (Should be exact to the cent)
		
		//Compare the Bank Debits to the FINALIZED TRADES (Could be off if a debit has not been made yet)
		
		
	}
	
	
	   /**
     * Get a bean from the faces context
     * @param beanName
     * @return
     */
	public Object getBean(String beanName){
	       ELContext elContext = FacesContext.getCurrentInstance().getELContext();
	       
	       return FacesContext.getCurrentInstance().
	       	getApplication().getELResolver().getValue(elContext, null, beanName);
	}
	
}
