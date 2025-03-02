package com.payyourself.testing;

import com.payyourself.testing.currency.rate.testData.RateTestDataTest;
import com.payyourself.testing.currency.type.TestCurrencyType;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.payyourself.testing");
		//$JUnit-BEGIN$
		suite.addTestSuite(RateTestDataTest.class);
		suite.addTestSuite(TestCurrencyType.class);

		suite.addTestSuite(TradeTest.class);

		suite.addTestSuite(TestAllHibernateMappings.class); //TODO This doesn't do shit
		//suite.addTestSuite(TestTradeSimulation.class);

		suite.addTestSuite(TestThreading.class);	
		
		//$JUnit-END$
		return suite;
	}

}
