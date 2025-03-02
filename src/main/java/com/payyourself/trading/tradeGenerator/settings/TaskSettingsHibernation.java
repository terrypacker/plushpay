package com.payyourself.trading.tradeGenerator.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class TaskSettingsHibernation extends TaskSettingsHome {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4420621366317100073L;

	public List<TaskSettings> loadAllSettings() {
		Session session = this.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from com.payyourself.trading.tradeGenerator.settings.TaskSettings";
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<TaskSettings> slist = new ArrayList<TaskSettings>();
		 
		 for(Iterator<TaskSettings> it=query.iterate();it.hasNext();){
		       TaskSettings row =  it.next(); 
		       slist.add(row);
		     }
		 
		 return query.list();
	}

}
