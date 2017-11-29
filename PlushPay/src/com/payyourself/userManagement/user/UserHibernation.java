package com.payyourself.userManagement.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;


/**
 * Should this be a wrapper class?
 * @author tpacker
 *
 */
public class UserHibernation extends UserHome {

	public UserHibernation(){
		super();
	}

	public List<User> loadAllUsers() {
		Session session = this.getSessionFactory().getCurrentSession();
		
		Criteria userCriteria = session.createCriteria(User.class);
		
		return userCriteria.list();
		
		/*
	    //Create Select Clause SQL
	     String SQL_QUERY ="from User";
	     
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<User> slist = new ArrayList<User>();
		 
		 for(Iterator<User> it=query.iterate();it.hasNext();){
		       User row =  it.next();
		       slist.add(row);
		     }

		 return slist;	*/
	}

}
