package com.payyourself.persistence;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Servlet Filter implementation class HibernateSessionFilter
 */
public class HibernateSessionFilter implements Filter {

	SessionFactory sf;
	
    /**
     * Default constructor. 
     */
    public HibernateSessionFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	   throws IOException, ServletException { 
	  //this.sf = HibernateUtil.getSessionFactory(); Done in init
	System.out.println("Running Hibernate Filter");
	  Session session = this.sf.getCurrentSession(); 
	  try { 
	   session.beginTransaction(); 
	   chain.doFilter(request, response); 
	   session.getTransaction().commit(); 
	  } catch (Throwable e) { 
	   if (session.getTransaction().isActive()) { 
	    session.getTransaction().rollback(); 
	   } 
	   throw new ServletException(e); 
	  } 
	 } 
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.sf = HibernateUtil.getSessionFactory();
	}
	

}
