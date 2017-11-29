package com.payyourself.trading.beneficiary;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.userManagement.user.User;

public class BeneficiaryHibernation extends BeneficiaryHome {

	/**
	 * Update the entry
	 * @param bene
	 */
	public void update(Beneficiary bene){
		this.getSessionFactory().getCurrentSession().update(bene);
	}

	/**
	 * This should be done in one query, not sure yet how to
	 * @param username
	 * @param type
	 * @return
	 */
	public List<Beneficiary> findAllOfTypeForTrader(String username, CurrencyCodeEnum type) {
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Beneficiaries From a User*/
		Criteria userCriteria = session.createCriteria(User.class);
		

		userCriteria.add(Restrictions.eq("username", username)); //Tradergroupid is null
		
		List<User> users = userCriteria.list();
		
		
		List<Beneficiary> benies = users.get(0).getBeneficiaries();
		List<Beneficiary> beniesOfType = new ArrayList<Beneficiary>();
		
		//TODO should add this as a Restriction on the criteria
		for(int i=0; i<benies.size(); i++){
			if(benies.get(i).getType().equals(type)){
				beniesOfType.add(benies.get(i));
			}
		}
		
		return beniesOfType;
	}
	
}
