package com.payyourself.persistence;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class PayYourselfDatasource {
	
	private static String user = "payYourselfUsers";
	private static String pwd = "shithead";
	private static String host = "localhost";
	private static int port = 3306;
	private static String database = "payyourself";
	
	private static String dataSourceName = "java:comp/env/PayYourselfDatasource";
	
	
	/**
	 * Bind the datasource to the JNDI Namespace 
	 * for the life of the invoking JVM.
	 * @return
	 * @throws Exception
	 */
	public static DataSource bind() throws Exception {
		
		//Start the registry
		PayYourselfDatasource.startRegistry();

		
		//Determine if the a datasource is already bound to this name
		try{
			Context initCtx = new InitialContext();
			
			//Context envCtx = (Context) initCtx.lookup("java:comp/env");
	
			// Look up our data source
			DataSource ds = (DataSource)
			  initCtx.lookup(PayYourselfDatasource.dataSourceName); //Lookup the name of the source (defined in context.xml and web.xml)
	
			if(ds != null){
				return ds; //Datasource is in JNDI namespace
			}
		}catch(NameNotFoundException e){
			//Do nothing that just means it isn't there so we can create it
			System.out.println("No Name: " + e.getMessage() + " so we can create it.");
		} catch (NamingException e) {
			System.out.println("Naming problem: " + e.getMessage());
		}
		


		
		
		//Data source is not bound, we will create it and then bind it
		
		
		MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser(PayYourselfDatasource.user);
		dataSource.setPassword(PayYourselfDatasource.pwd);
		dataSource.setServerName(PayYourselfDatasource.host);
		dataSource.setPort(PayYourselfDatasource.port);
		dataSource.setDatabaseName(PayYourselfDatasource.database);
		
		//InitialContext context = createContext();
		Context context = new InitialContext();
		context.rebind(PayYourselfDatasource.dataSourceName, dataSource);
		context.close();

		return dataSource;
	}
	
	
	
	
	private static void startRegistry() throws RemoteException {
		
		Registry reg = null;
		try{
			reg = LocateRegistry.getRegistry(1099);
			LocateRegistry.createRegistry(1099);
			
		}catch(RemoteException e){
			//Do Nothing
			System.out.println("Remote Except.");
		}
	}

	
	
	
}
