package com.payyourself.simulation;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.payyourself.banking.au.AudBankSimulator;
import com.payyourself.banking.au.AudTradeCreditManager;
import com.payyourself.banking.au.AudTradeDebitManager;
import com.payyourself.banking.au.AudTraderDepositSimulator;
import com.payyourself.currency.rate.RateCollectorThread;
import com.payyourself.currency.rate.RateTestDataCollector;
import com.payyourself.trading.evolution.GeneticTradeManager;
import com.payyourself.trading.tradeProfitTree.TradeProfitTreeManager;
import com.payyourself.trading.traderSimulation.TraderTestDataSimulation;
import com.payyourself.trading.traderSimulation.TradersSimulation;

/**
 * Application Lifecycle Listener implementation class SimulationStartupListener
 *
 */
public class SimulationStartupListener implements ServletContextListener {

	private boolean running = false;
	
    /**
     * Default constructor. 
     */
    public SimulationStartupListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
       
    	//Start the simulation
    	if(this.running){
    		RateTestDataCollector.getRateTestDataCollector().startUp();
    		
    		AudBankSimulator.getAudBankSimulator().startUp();
    		
    		AudTradeCreditManager.getAudTradeCreditManager().startUp();
    		
    		AudTradeDebitManager.getAudTradeDebitManager().startUp();
    		
    		AudTraderDepositSimulator.getAudTraderDeptositSimulator().startUp();
    		
    		//TradeProfitTreeManager.getTradeProfitTreeManager().startUp();
    		GeneticTradeManager.getGeneticTradeManager().startUp();
    		
    		//TraderTestDataSimulation.getTraderTestDataSimulation().startUp();
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        
    	RateTestDataCollector.getRateTestDataCollector().shutDown();
    	AudBankSimulator.getAudBankSimulator().shutDown();
    	AudTradeCreditManager.getAudTradeCreditManager().shutDown();
    	AudTradeDebitManager.getAudTradeDebitManager().shutDown();
		AudTraderDepositSimulator.getAudTraderDeptositSimulator().shutDown();
		TradeProfitTreeManager.getTradeProfitTreeManager().shutDown();
		TraderTestDataSimulation.getTraderTestDataSimulation().shutDown();
    	
    }
	
}
