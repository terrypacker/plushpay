package com.payyourself.testing;

import java.lang.Thread.State;

import org.apache.cactus.ServletTestCase;

import com.payyourself.banking.au.AudTradeCreditManager;
import com.payyourself.banking.au.AudTradeDebitManager;
import com.payyourself.banking.au.AudTraderDepositSimulator;
import com.payyourself.currency.rate.RateCollectorThread;
import com.payyourself.currency.rate.RateTestDataCollector;
import com.payyourself.trading.tradeProfitTree.TradeProfitTreeManager;
import com.payyourself.trading.traderSimulation.TradersSimulation;

public class TestThreading extends ServletTestCase{
	
	
	public void testStartup(){
		RateCollectorThread.getRateCollectorThread();
		RateTestDataCollector.getRateTestDataCollector();
		AudTradeCreditManager.getAudTradeCreditManager();
		AudTradeDebitManager.getAudTradeDebitManager();
		AudTraderDepositSimulator.getAudTraderDeptositSimulator();
		TradeProfitTreeManager.getTradeProfitTreeManager();
		TradersSimulation.getTradersSimulation();
		
		assertEquals(State.NEW,RateCollectorThread.getRateCollectorThread().getState());
		assertEquals(State.NEW,RateTestDataCollector.getRateTestDataCollector().getState());
		assertEquals(State.NEW,AudTradeCreditManager.getAudTradeCreditManager().getState());
		assertEquals(State.NEW,AudTradeDebitManager.getAudTradeDebitManager().getState());
		assertEquals(State.NEW,AudTraderDepositSimulator.getAudTraderDeptositSimulator().getState());
		assertEquals(State.NEW,TradeProfitTreeManager.getTradeProfitTreeManager().getState());
		assertEquals(State.NEW,TradersSimulation.getTradersSimulation().getState());
		
		RateCollectorThread.getRateCollectorThread().start();
		RateTestDataCollector.getRateTestDataCollector().start();
		AudTradeCreditManager.getAudTradeCreditManager().start();
		AudTradeDebitManager.getAudTradeDebitManager().start();
		AudTraderDepositSimulator.getAudTraderDeptositSimulator().start();
		TradeProfitTreeManager.getTradeProfitTreeManager().start();
		TradersSimulation.getTradersSimulation().start();

		assertTrue((State.RUNNABLE==RateCollectorThread.getRateCollectorThread().getState())||
				(State.BLOCKED==RateCollectorThread.getRateCollectorThread().getState()));
		
		assertTrue((State.RUNNABLE==RateTestDataCollector.getRateTestDataCollector().getState())||
				(State.BLOCKED==RateTestDataCollector.getRateTestDataCollector().getState()));
		
		assertTrue((State.RUNNABLE==AudTradeCreditManager.getAudTradeCreditManager().getState())||
				(State.BLOCKED==AudTradeCreditManager.getAudTradeCreditManager().getState()));
		
		assertTrue((State.RUNNABLE==AudTradeDebitManager.getAudTradeDebitManager().getState())||
				(State.BLOCKED==AudTradeDebitManager.getAudTradeDebitManager().getState()));
		
		assertTrue((State.RUNNABLE==AudTraderDepositSimulator.getAudTraderDeptositSimulator().getState())||
				(State.BLOCKED==AudTraderDepositSimulator.getAudTraderDeptositSimulator().getState()));
		
		assertTrue((State.RUNNABLE==TradeProfitTreeManager.getTradeProfitTreeManager().getState())||
				(State.BLOCKED==TradeProfitTreeManager.getTradeProfitTreeManager().getState()));

		assertTrue((State.RUNNABLE==TradersSimulation.getTradersSimulation().getState())||
				(State.BLOCKED==TradersSimulation.getTradersSimulation().getState()));

	}
	
	
	public void testShutdown(){

		RateCollectorThread.getRateCollectorThread().shutDown();
		
		RateTestDataCollector.getRateTestDataCollector().shutDown();
		
		AudTradeCreditManager.getAudTradeCreditManager().shutDown();

		AudTradeDebitManager.getAudTradeDebitManager().shutDown();
		
		AudTraderDepositSimulator.getAudTraderDeptositSimulator().shutDown();
		
		TradeProfitTreeManager.getTradeProfitTreeManager().shutDown();

		TradersSimulation.getTradersSimulation().shutDown();
		
		//Wait a bit to see if they all can shutdown
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		assertEquals(State.TERMINATED,RateCollectorThread.getRateCollectorThread().getState());
		assertEquals(State.TERMINATED,RateTestDataCollector.getRateTestDataCollector().getState());
		assertEquals(State.TERMINATED,AudTradeCreditManager.getAudTradeCreditManager().getState());
		assertEquals(State.TERMINATED,AudTradeDebitManager.getAudTradeDebitManager().getState());
		assertEquals(State.TERMINATED,AudTraderDepositSimulator.getAudTraderDeptositSimulator().getState());
		assertEquals(State.TERMINATED,TradeProfitTreeManager.getTradeProfitTreeManager().getState());
		assertEquals(State.TERMINATED,TradersSimulation.getTradersSimulation().getState());
		
	}
	
	


}
