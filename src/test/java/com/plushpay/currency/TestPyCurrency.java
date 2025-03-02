package com.plushpay.currency;

import org.apache.cactus.ServletTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plushpay.service.currency.PyCurrency;
import com.plushpay.currency.type.PyCurrencyTypeHibernation;
import com.plushpay.persistence.HibernateUtil;

//TO use these tests we have added catalina.jar from TOmcat lib

public class TestPyCurrency extends ServletTestCase{

	@Before
	public void setUp(){
		
		HibernateUtil.getSessionFactory().getCurrentSession();//.openSession();

		HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
	}
	
	@After
	public void tearDown(){
		HibernateUtil.getSessionFactory().getCurrentSession().close();
	}
	
	@Test
	public void testInsert(){
		
		PyCurrencyTypeHibernation pycth = new PyCurrencyTypeHibernation();
		pycth.loadAllTypes();
		
		
		PyCurrency currency = new PyCurrency();
	}
}
