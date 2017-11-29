package com.payyourself.testing;

import java.io.IOException;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;
import org.apache.naming.NamingContext;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.payyourself.testing.currency.TestPyCurrency;

public class BasePyTest {

	public void setUpJNDI(Class clazz){
		
		//Configuration config = new Configuration();
		
		//URL url = clazz.getResource("/com/payyourself/testing/testHibernate.cfg.xml");
		
		//config.configure(url);
		
		
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, 
                "org.apache.naming");            
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:/comp");
            ic.createSubcontext("java:/comp/env");
            ic.createSubcontext("java:/comp/env/jdbc");
           
            // Construct DataSource
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            ds.setURL("jdbc:mysql://localhost/payYourself");
            ds.setUser("PayYourselfUsers");
            ds.setPassword("shithead");
            
            ic.bind("java:/comp/env/jdbc/SessionFactory", ds);
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
		
	}
	
}
