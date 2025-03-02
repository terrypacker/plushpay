package com.payyourself.persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class HibernateListener
 *
 */
public class HibernateListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public HibernateListener() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	HibernateUtil.getSessionFactory(); // Just call the static initializer of that class

    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	HibernateUtil.getSessionFactory().close(); // Free all resources
    }
	
}
