package com.payyourself.registration;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


public class RegistrationInfoHibernation extends RegistrationInfoHome{

	/**
	 * Select all entries in the registrationinfo table with username=username
	 * @param username
	 * @return
	 */
	public List<RegistrationInfo> getUsingUsername(String username){
		
		Session session = this.getSessionFactory().getCurrentSession();
		
		if(!session.isOpen()){
			session = this.getSessionFactory().openSession();
		}
		
		/* We want to get Traders */
		Criteria usernameCriteria = session.createCriteria(RegistrationInfo.class);
		
		/*Add some restrictions (criterion) to the  */
		usernameCriteria.add(Restrictions.eq("username", username)); //username == username
		
		return usernameCriteria.list();
		
	}
	
}
