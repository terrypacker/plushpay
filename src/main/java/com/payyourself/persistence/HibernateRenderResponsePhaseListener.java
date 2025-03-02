package com.payyourself.persistence;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.hibernate.SessionFactory;

/**
 * Application Lifecycle Listener implementation class HibernateRenderResponsePhaseListener
 * This Phase Listener will close the session via a commit or rollback AFTER 
 * all the code has been processed for a response.
 */
public class HibernateRenderResponsePhaseListener implements PhaseListener {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 534234949761431504L;

	public void afterPhase(PhaseEvent event) {
	    // Here set the Hibernate commit or roll back transaction ...
	    SessionFactory sessionFactory = 

	                    HibernateUtil.getSessionFactory();
	    try {
	      sessionFactory.getCurrentSession().getTransaction().commit();
	    } catch (Throwable ex) {
	      System.err.println("Very unsuitable error occured ..." + ex.getLocalizedMessage());
	      if (sessionFactory.getCurrentSession().

	          getTransaction().isActive()) {
	              sessionFactory.getCurrentSession().

	              getTransaction().rollback();
	      }
	    }
	  }

	  public void beforePhase(PhaseEvent event) {
	  }

	  public PhaseId getPhaseId() {
	    return PhaseId.RENDER_RESPONSE;
	  }
}
