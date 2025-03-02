package com.plushpay.currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.plushpay.service.currency.PyCurrency;
import com.plushpay.currency.type.PyCurrencyTypeHibernation;
import com.plushpay.persistence.HibernateUtil;
import com.plushpay.persistence.PayYourselfDatasource;


public class PyCurrencyJUnit {

	
	@Before
	public void setUp(){
		
	try{
		
		PayYourselfDatasource.bind(); //Bind a datasource to the JNDI
		
	} catch (Exception e){
		System.out.println("SetupJNDIDataSourceerr:" + e.getMessage());
		e.printStackTrace();
	}
		
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
	
	@Test
	public void testAgain(){
		PyCurrencyTypeHibernation pycth = new PyCurrencyTypeHibernation();
		pycth.loadAllTypes();
	}
}
