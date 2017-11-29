package com.payyourself.testing.currency;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.ConnectionPoolDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.payyourself.currency.PyCurrency;
import com.payyourself.currency.type.PyCurrencyTypeHibernation;
import com.payyourself.persistence.HibernateUtil;
import com.payyourself.persistence.PayYourselfDatasource;


public class PyCurrencyJUnit {

	
	@Before
	public void setUp(){
		
	try{
		
		PayYourselfDatasource.bind(); //Bind a datasource to the JNDI
		
	} catch (Exception e){
		System.out.println("SetupJNDIDataSource err: " + e.getMessage());
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
