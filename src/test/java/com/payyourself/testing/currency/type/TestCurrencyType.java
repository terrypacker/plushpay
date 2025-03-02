package com.payyourself.testing.currency.type;


import java.util.Calendar;

import org.apache.cactus.ServletTestCase;
import org.hibernate.Session;

import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.persistence.PayYourselfDatasource;


public class TestCurrencyType extends ServletTestCase{
	
	private Session sesh;
	
	
	public void setUp(){
		
		try {
			PayYourselfDatasource.bind();
		} catch (Exception e) {
			e.printStackTrace();
		} //Bind it for standalone operation.
		
		this.sesh = HibernateUtil.getSessionFactory().getCurrentSession();
		this.sesh.beginTransaction();
	}
	
	public void tearDown(){
		this.sesh.flush();
		this.sesh.disconnect();
		this.sesh.close();
	}
	
	
	
	public void testInsertType(){
	
		PyCurrencyType usd = new PyCurrencyType(CurrencyCodeEnum.USD,0000,"$",Calendar.getInstance(),true);
		PyCurrencyTypeHibernation pycth = new PyCurrencyTypeHibernation();
		PyCurrencyType aud = new PyCurrencyType(CurrencyCodeEnum.AUD,100,"$",Calendar.getInstance());
		
		pycth.persist(usd);
		pycth.persist(aud);
		
		usd = new PyCurrencyType(CurrencyCodeEnum.USD,10000,"$",Calendar.getInstance(),true);
		aud = new PyCurrencyType(CurrencyCodeEnum.AUD,9200,"$",Calendar.getInstance());
		
		pycth.persist(usd);
		pycth.persist(aud);
		
		
	}
	
	public void testGetTypes(){
		
		PyCurrencyTypeHibernation pycth = new PyCurrencyTypeHibernation();
		
		PyCurrencyType usd = pycth.getCurrentUsd();
		assertTrue(usd.getRateToBase()==10000);
		
		PyCurrencyType aud = pycth.getCurrentAud();
		assertTrue(aud.getRateToBase()==9200);
		
		try {
			PyCurrencyType base = pycth.getBaseCurrency();
			
			assertTrue(usd.equals(base));
			
		} catch (Exception e) {
			fail();
		}
		
		
		
	
		
	}
	
	


}
