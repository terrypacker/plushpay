package com.payyourself.trading.greedy;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.payyourself.persistence.PayYourselfDatasource;

public class GreedyTest {

	@Before
	public void setUp() throws Exception {
		
		PayYourselfDatasource.bind(); //Bind the datasource for use outside of container
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void runMainTest(){
		
		GreedyTradeManager.getGreedyTradeManager().startUp();
		while(true){
			//Run forever 
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
