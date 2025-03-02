package com.payyourself.testing.currency.rate.testData;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

import com.payyourself.currency.rate.testData.RateTestData;
import com.payyourself.currency.rate.testData.RateTestDataEntry;

public class RateTestDataTest extends TestCase{

	@Test
	public void testRateTestData() {
		
		RateTestData data = new RateTestData();
		assertNotNull(data);
		
	}

	@Test
	public void testLoadData() {
		
		RateTestData data = new RateTestData();
		
		URL url = RateTestData.class.getResource("/com/payyourself/testing/data/hourlyExRates.csv");
		
		try {
			data.loadData(new File(url.getFile()));
		} catch (ParseException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	@Test
	public void testGetData() {
		RateTestData data = new RateTestData();

		URL url = RateTestData.class.getResource("/com/payyourself/testing/data/hourlyExRates.csv");
		
		try {
			data.loadData(new File(url.getFile()));
		} catch (ParseException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		assertNotNull(data.getData());
		
		
		//Test the first data
		RateTestDataEntry entry = data.getData().get(0);
		
		assertTrue("AUDUSD".equals(entry.getTicker()));
		
		assertTrue(1==entry.getDate().get(Calendar.DAY_OF_MONTH));
		assertTrue(1==entry.getDate().get(Calendar.MONTH));
		assertTrue(2010==entry.getDate().get(Calendar.YEAR));
		
		
		assertTrue(.8828f==entry.getOpen());
		assertTrue(.8829f==entry.getHigh());
		assertTrue(.8827f==entry.getLow());
		assertTrue(.8829f==entry.getClose());
	
		
		
		
	}

}
