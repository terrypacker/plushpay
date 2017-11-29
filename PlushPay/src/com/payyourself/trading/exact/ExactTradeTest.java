package com.payyourself.trading.exact;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.payyourself.persistence.PayYourselfDatasource;

public class ExactTradeTest {

	@Before
	public void setUp() throws Exception {
		
		PayYourselfDatasource.bind(); //Bind the datasource for use outside of container
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void runMainTest(){
		
		ExactTradeManager.getExactTradeManager().startUp();
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
	
	@Test
	public void testSet(){
	}
	
}
