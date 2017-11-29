package com.payyourself.trading.trade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.payyourself.trading.trader.Trader;
import com.payyourself.trading.trader.group.TraderGroupHibernation;
import com.payyourself.userManagement.user.User;


public class TradeHibernation extends TradeHome {

	public TradeHibernation(){
		
	}

	public List loadAll() {
		
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from Trade";
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<Trade> slist = new ArrayList<Trade>();
		 
		 for(Iterator<Trade> it=query.iterate();it.hasNext();){
		       Trade row =  it.next(); 
		       slist.add(row);
		     }
		 
		 return slist;
	}

	public Object loadOne(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void insert(Object obj) {
		
		Session session = this.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.update(obj);
			tx.commit();
		}catch(HibernateException ex){
			if(tx!=null) tx.rollback();
			throw ex;
		} finally {
			session.close();
		}
	}

	public List<Trade> loadAllWithStatus(TradeStatus status) {
		Criteria tradeCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trade.class);
		
		tradeCriteria.add(Restrictions.eq("status", status.name()));
		
		return (List<Trade>)tradeCriteria.list();
	}

	/**
	 * Load all trades with status, excluding the following ids
	 * @param status
	 * @param exclude
	 * @return
	 */
	public List<Trade> loadAllWithStatusExcluding(TradeStatus status,
			List<Long> exclude) {
		Criteria tradeCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trade.class);
		
		tradeCriteria.add(Restrictions.eq("status", status.name()));
		
		for(int i=0; i<exclude.size(); i++){
			tradeCriteria.add(Restrictions.ne("tradeId", exclude.get(i)));
		}
		
		return (List<Trade>)tradeCriteria.list();
	}

	public Trade mergeAndUpdateMembers(Trade newTrade) {
		//TODO is the the correct way to do this
		
		TraderGroupHibernation tgh = new TraderGroupHibernation();
		
		//We need to get the new Group ID into each trader
		// TODO SHOULDN"T DO IT THIS WAY 
		tgh.mergeAndUpdateMembers(newTrade.getBuyerGroup());
		tgh.mergeAndUpdateMembers(newTrade.getSellerGroup());
		
		return this.merge(newTrade);
		
		
		
	}


}
