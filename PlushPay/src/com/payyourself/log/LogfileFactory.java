package com.payyourself.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

public class LogfileFactory {

 
	private static final String HOSTNAME = "http://localhost:8080/";

	/*For logger management*/
	//List to store any loggers that are created.
	private static List<Logger> allLoggers = new ArrayList<Logger>(10);
	
	/* For the display page */
	private static List<String> logfileNames = new ArrayList<String>(10);
	private String logfileToDisplay;
	
	
	public LogfileFactory(){

	}
	  
	public List<Logger> getAllLoggers() {
		return allLoggers;
	}

	public List<String> getLoggerNames(){
		
		return logfileNames;
	}


	/**
	 * 
	 * @return
	 */
	public String getLogfileLocation(){
		

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		String temp = null;
		
		temp = LogfileFactory.HOSTNAME + "PayYourself/logfiles/" + this.logfileToDisplay + ".html";
		
		return temp;
		
		
	}
	
	
	/**
	 * Globally Define Level for all loggers
	 * @param clazz
	 * @return
	 */
	public static Logger getHTMLLogger(Class clazz){
		
		return LogfileFactory.getHTMLLogger(Level.ALL, clazz);
	}
	
	/**
	 * Create and setup an HTML Logger
	 * 
	 * It will be added to the master list
	 * 
	 * @param file
	 * @param clazz
	 * @return
	 */
	public static Logger getHTMLLogger(Level level, Class clazz) {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		String temp = null;
		try {
			temp = loader.getResource("com").toURI().getPath();
		} catch (URISyntaxException e1) {
			//We have a real problem
			e1.printStackTrace();
			return null;
		}
		
		temp = temp + "../../../logfiles/";
		int firstChar;

		String classname = clazz.getName();
	    firstChar = classname.lastIndexOf ('.') + 1;
	    if ( firstChar > 0 ) {
	    	classname = classname.substring ( firstChar );
	     }
	
	    
		Logger logger = Logger.getLogger(clazz);

		FileOutputStream file = null;
		try {
			
			file = new FileOutputStream(temp+classname+".html");
			LogfileFactory.logfileNames.add(classname);
			PyHTMLLayout layout = new PyHTMLLayout();
			layout.setTitle("Logger for: " + clazz.getName());

			WriterAppender appender = new WriterAppender(layout, file);

			logger.addAppender(appender);
			logger.setLevel(level);

			LogfileFactory.allLoggers.add(logger);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//Just put the logfile in the local dir NOPE too messy
			//try {
			//	file = new FileOutputStream(classname+".html");
			//	LogfileFactory.logfileNames.add(classname);
			//} catch (FileNotFoundException e1) {
			//	
			//	e1.printStackTrace();
			//	return null;
			//}
			
		}
		

		logger.setLevel(level);
		LogfileFactory.allLoggers.add(logger);

		
		logger.info("Starting Logger.");
		return logger;

	}

	public void setLogfileToDisplay(String logfileToDisplay) {
		this.logfileToDisplay = logfileToDisplay;
	}

	public String getLogfileToDisplay() {
		return logfileToDisplay;
	}
	
}
