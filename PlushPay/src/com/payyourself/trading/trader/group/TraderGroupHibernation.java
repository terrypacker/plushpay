package com.payyourself.trading.trader.group;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.TraderHibernation;
import com.payyourself.trading.trader.TraderStatus;

public class TraderGroupHibernation extends TraderGroupHome {

	/**
	 * Merge the group then update the status of the members to DEPOSIT
	 * and update the group ID
	 * @param group
	 * @return
	 */
	public TraderGroup mergeAndUpdateMembers(TraderGroup group) {
		
		//Merge the group
		group = this.merge(group);
		
		TraderHibernation th = new TraderHibernation();
		
		//TODO Ensure this is the correct way to update this type of link
		//Now update the traders with the new Number
		for(int i=0; i<group.getTraders().size(); i++){
			group.getTraders().get(i).setGroup(group);
			group.getTraders().get(i).setStatus(TraderStatus.DEPOSIT.name());
		}
		
		this.persist(group);
		
		return group;
		
		
	}



}
