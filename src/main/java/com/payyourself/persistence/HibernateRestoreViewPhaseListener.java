package com.payyourself.persistence;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;


import org.hibernate.SessionFactory;

/**
 * Application Lifecycle Listener implementation class HibernateRestoreViewPhaseListener
 * 
 * This phase listener will open a session and create a transaction for
 * each request made to the app.  It is to be closed in the afterphase of
 * the RenderResponse phase listener.
 *
 */
public class HibernateRestoreViewPhaseListener implements PhaseListener {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1411751203144098855L;

	public void afterPhase(PhaseEvent event) {
		HibernateUtil.getSessionFactory();
	  }

	  public void beforePhase(PhaseEvent event) {
	    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	    sessionFactory.getCurrentSession().beginTransaction();
	  }

	  public PhaseId getPhaseId() {
	    return PhaseId.RESTORE_VIEW;
	  }

	}