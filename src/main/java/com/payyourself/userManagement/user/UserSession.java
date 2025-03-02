/**
 * 
 */
package com.payyourself.userManagement.user;

import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.payyourself.persistence.HibernateUtil;

/**
 * @author tpacker
 *
 */
public class UserSession implements HttpSessionListener {
	
	private com.payyourself.userManagement.user.User user;

	public UserSession() {
		this.user = new User();
	}

	public com.payyourself.userManagement.user.User getUser() {
		UserHibernation userh = new UserHibernation();
		Session session = userh.getSessionFactory().getCurrentSession();
		session.beginTransaction();
	    this.user = (User) session.load(User.class, "tpacker");
	    
	    session.getTransaction().commit(); 

		return user;
	}

	public void setUser(com.payyourself.userManagement.user.User user) {
		this.user = user;
	}

	public void sessionCreated(HttpSessionEvent arg0) {

		UserHibernation userh = new UserHibernation();
		//Session session = userh.getSessionFactory().getCurrentSession();
		//session.beginTransaction(); 
	    //this.user = (User) session.createQuery("Select u from User where u.username = 'tpacker'").uniqueResult();
	    //createQuery("select p from Person p left join fetch p.events where p.id = :pid") 
	    //session.getTransaction().commit(); 

		
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
