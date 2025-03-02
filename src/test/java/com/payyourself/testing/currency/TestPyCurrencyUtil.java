package com.payyourself.testing.currency;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.apache.cactus.ServletTestCase;
import org.junit.Test;

import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.PyCurrencyUtil;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;

public class TestPyCurrencyUtil{

	@Test
	public void testCreateCurrency(){
		
		PyCurrencyType usdType = new PyCurrencyType(CurrencyCodeEnum.USD,10000,"$",Calendar.getInstance(),true);
		PyCurrencyType audType = new PyCurrencyType(CurrencyCodeEnum.AUD,9200,"$",Calendar.getInstance(),true);

		PyCurrency usd = PyCurrencyUtil.createCurrency(10000, audType, usdType);
		
		PyCurrency aud = PyCurrencyUtil.createCurrency(10000, usdType, audType);
		
		assertEquals(10869,aud.getValue()); //1 USD gets you 1.08 AUD
		
		assertEquals(9200,usd.getValue()); //1AUD gets you .92 USD
		
	}
	
	
	
}
