package com.payyourself.trading.trader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.payyourself.currency.PyCurrencyHibernation;
import com.payyourself.currency.code.CurrencyCodeEnum;
import com.payyourself.currency.type.PyCurrencyType;
import com.payyourself.trading.trader.group.TraderGroup;
import com.payyourself.userManagement.user.User;


public class TraderHibernation extends TraderHome{

	public TraderHibernation(){
		super();
	}

	public void insert(Object obj) {
		Session session = this.getSessionFactory().getCurrentSession();
		if(!session.isOpen()){
			session = this.getSessionFactory().openSession();
		}
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.update(obj);
			tx.commit();
		}catch(HibernateException ex){
			if(tx!=null) tx.rollback();
			throw ex;
		} finally {

		}
		
	}
	
	public List<Trader> merge(List<Trader> traders){
		
		List<Trader> merged = new ArrayList<Trader>();
		
		for(int i=0; i<traders.size(); i++){
			merged.add(this.merge(traders.get(i)));
			
		}
		
		return merged;
	}

	public List<Trader> loadAll() {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from Trader";
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<Trader> slist = new ArrayList<Trader>();
		 
		 for(Iterator<Trader> it=query.iterate();it.hasNext();){
		       Trader row =  it.next(); 
		       slist.add(row);
		     }
		 
		 return slist;

	}

	/**
	 * Get all Traders in group with id=groupid
	 * @param groupid
	 * @return
	 */
	public List<Trader> getTradersInGroup(long groupid) {
		
		Criteria traderCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trader.class);
		
		Criteria groupCriteria = traderCriteria.createCriteria("group");
		
		groupCriteria.add(Restrictions.eq("groupid", groupid));
		
		return traderCriteria.list();
		
		
	}
	
	/**
	 * Loads all traders not assigned to a TraderGroup
	 * @return
	 */
	public List<Trader> loadFreeTraders() {
		Session session = this.getSessionFactory().getCurrentSession();

		//session.beginTransaction();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from Trader where tradergroupid is null";
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<Trader> slist = new ArrayList<Trader>();
		 
		 for(Iterator<Trader> it=query.iterate();it.hasNext();){
		       Trader row =  it.next(); 
		       slist.add(row);
		     }
		 
		 return slist;

	}

	
	
	/**
	 * Load all Free Traders with buying and selling 
	 * currencies.
	 * @param buying
	 * @param selling
	 * @return
	 */
	public List<Trader> loadFreeTraders(PyCurrencyType buying, PyCurrencyType selling){
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		/*Add some restrictions to the currency being sold */
		Criteria sellingCurrencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria sellingCurrencyTypeCriteria = sellingCurrencyCriteria.createCriteria("type");
		sellingCurrencyTypeCriteria.add(Restrictions.eq("code", selling.getCode()));
		
		/*Add some restrictions to the currency being bought */
		Criteria buyingCurrencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		
		/*Add a restriction to the type of currency being bought */
		Criteria buyingCurrencyTypeCriteria = buyingCurrencyCriteria.createCriteria("type");
		buyingCurrencyTypeCriteria.add(Restrictions.eq("code", buying.getCode()));

		return traderCriteria.list();
		
	}

	/**
	 * Load all Free Traders with buying and selling 
	 * currencies. Sorted in ascending order by buying currency
	 * @param buying
	 * @param selling
	 * @return
	 */
	public List<Trader> loadFreeTradersSorted(PyCurrencyType buying, PyCurrencyType selling){
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		/*Add some restrictions to the currency being sold */
		Criteria sellingCurrencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria sellingCurrencyTypeCriteria = sellingCurrencyCriteria.createCriteria("type");
		sellingCurrencyTypeCriteria.add(Restrictions.eq("code", selling.getCode()));
		
		/*Add some restrictions to the currency being bought */
		Criteria buyingCurrencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		buyingCurrencyCriteria.addOrder(Order.asc("baseValue"));
		
		/*Add a restriction to the type of currency being bought */
		Criteria buyingCurrencyTypeCriteria = buyingCurrencyCriteria.createCriteria("type");
		buyingCurrencyTypeCriteria.add(Restrictions.eq("code", buying.getCode()));
		
		
		return traderCriteria.list();
		
	}
	
	/**
	 * Load all Free Traders with buying and selling 
	 * currencies. Sorted in ascending order by buying currency
	 * @param buying
	 * @param selling
	 * @return
	 */
	public List<Trader> loadFreeTradersSortedDescending(PyCurrencyType buying, PyCurrencyType selling){
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		/*Add some restrictions to the currency being sold */
		Criteria sellingCurrencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria sellingCurrencyTypeCriteria = sellingCurrencyCriteria.createCriteria("type");
		sellingCurrencyTypeCriteria.add(Restrictions.eq("code", selling.getCode()));
		
		/*Add some restrictions to the currency being bought */
		Criteria buyingCurrencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		buyingCurrencyCriteria.addOrder(Order.desc("baseValue"));
		
		/*Add a restriction to the type of currency being bought */
		Criteria buyingCurrencyTypeCriteria = buyingCurrencyCriteria.createCriteria("type");
		buyingCurrencyTypeCriteria.add(Restrictions.eq("code", buying.getCode()));
		
		
		return traderCriteria.list();
		
	}

	
	/**
	 * Load all Traders not belonging to a group AND lock the rows that are returned.
	 * @param buying
	 * @param selling
	 * @return
	 */
	public List<Trader> loadAndLockFreeTraders(PyCurrencyType buying, PyCurrencyType selling){
		
		return null;
	}

	/**
	 * Load all traders with no tradergroup 
	 * that are selling the currencyType type
	 * @param type
	 * @return
	 */
	public List loadFreeTradersSelling(PyCurrencyType type){
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		/*Add some restrictions to the currency being sold */
		Criteria currencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria currencyTypeCriteria = currencyCriteria.createCriteria("type");
		currencyTypeCriteria.add(Restrictions.eq("code", type.getCode()));
		
		/*Set Lock Mode*/
		traderCriteria.setLockMode(LockMode.UPGRADE);

		return traderCriteria.list();
		
	}

	/**
	 * Load all traders with no tradergroup 
	 * that are selling the currencyType type
	 * @param type
	 * @return
	 */
	public List loadFreeTradersBuying(PyCurrencyType type){
		Session session = this.getSessionFactory().getCurrentSession();
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		/*Add some restrictions to the currency being sold */
		Criteria currencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		
		/*Add a restriction to the type of currency being sold */
		Criteria currencyTypeCriteria = currencyCriteria.createCriteria("type");
		currencyTypeCriteria.add(Restrictions.eq("code", type.getCode()));
		
		/* Set Lock Mode */
		traderCriteria.setLockMode(LockMode.UPGRADE);

		return traderCriteria.list();
		
	}
	
	
	public List loadFreeTradersExcept(int traderid) {
		Session session = this.getSessionFactory().getCurrentSession();
		//session.beginTransaction();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from Trader where tradergroupid is null and traderid != :tradersID";

		 Query query = session.createQuery(SQL_QUERY);
		 query.setInteger("tradersID", traderid);
		 
		 List<Trader> slist = query.list();
		 		 
		 return slist;

	}
	
	public List<Trader> loadFreeTradersExcept(List<Long> ids, CurrencyCodeEnum buying, CurrencyCodeEnum selling){

		Session session = this.getSessionFactory().getCurrentSession();
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		//Add the exceptions
		for(int i=0; i<ids.size(); i++){
			traderCriteria.add(Restrictions.ne("traderid", ids.get(i)));
		}
		
		/*Add some restrictions to the currency being sold */
		Criteria sellingCurrencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria sellingCurrencyTypeCriteria = sellingCurrencyCriteria.createCriteria("type");
		sellingCurrencyTypeCriteria.add(Restrictions.eq("code", selling));
		
		/*Add some restrictions to the currency being bought */
		Criteria buyingCurrencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		
		/*Add a restriction to the type of currency being bought */
		Criteria buyingCurrencyTypeCriteria = buyingCurrencyCriteria.createCriteria("type");
		buyingCurrencyTypeCriteria.add(Restrictions.eq("code", buying));

		
		
		return traderCriteria.list();
		 		 
	}
	
	/**
	 * Sort the returned list on NON USD currency rate
	 * @param ids
	 * @param buying
	 * @param selling
	 * @return
	 */
	public List<Trader> loadSortedFreeTradersExcept(List<Long> ids, CurrencyCodeEnum buying, CurrencyCodeEnum selling){

		Session session = this.getSessionFactory().getCurrentSession();
		
		boolean sortOnBuying = true;
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		//Add the exceptions
		for(int i=0; i<ids.size(); i++){
			traderCriteria.add(Restrictions.ne("traderid", ids.get(i)));
		}
		
		if(buying.equals(CurrencyCodeEnum.USD)){
			sortOnBuying = false;
		}
		
		/*Add some restrictions to the currency being sold */
		Criteria sellingCurrencyCriteria = traderCriteria.createCriteria("currencyToSell");
		
		/*Add a restriction to the type of currency being sold */
		Criteria sellingCurrencyTypeCriteria = sellingCurrencyCriteria.createCriteria("type");
		sellingCurrencyTypeCriteria.add(Restrictions.eq("code", selling));
		
		/*Add some restrictions to the currency being bought */
		Criteria buyingCurrencyCriteria = traderCriteria.createCriteria("currencyToBuy");
		
		/*Add a restriction to the type of currency being bought */
		Criteria buyingCurrencyTypeCriteria = buyingCurrencyCriteria.createCriteria("type");
		buyingCurrencyTypeCriteria.add(Restrictions.eq("code", buying));

		if(sortOnBuying){
			/* Sort this by value */
			buyingCurrencyTypeCriteria.addOrder(Order.asc("rateToBase"));
			buyingCurrencyCriteria.addOrder(Order.asc("value"));

		}else{
			
			//The order here is important, the first order is the rate and then it sorts the rate groupings by value
			sellingCurrencyTypeCriteria.addOrder(Order.asc("rateToBase"));			
			sellingCurrencyCriteria.addOrder(Order.asc("value"));
		}
		
		
		return traderCriteria.list();
		 		 
	}
	
	public List<Trader> loadTradersWithGroupId(long groupId){
		
		Session session = this.getSessionFactory().getCurrentSession();
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		Criteria groupCriteria = traderCriteria.createCriteria("group");
		groupCriteria.add(Restrictions.eq("groupid",groupId));
		
		return traderCriteria.list();
		
	}

	
	public Object loadOne(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Find Traders with User
	 * @param user
	 * @return
	 */
	public List<Trader> findTradersWithUser(User user) {
		Session session = this.getSessionFactory().getCurrentSession();
		
		/* We want to get Traders */
		Criteria traderCriteria = session.createCriteria(Trader.class);
		
		Criteria userCriteria = traderCriteria.createCriteria("user");
		
		userCriteria.add(Restrictions.eq("username", user.getUsername()));
		
		
		return traderCriteria.list();
	}
	
	
	public List<Trader> loadOpenTradersForUser(User user) {
		
		/* We want to get Traders */
		Criteria traderCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trader.class);
		
		/*Add some restrictions (criterion) to the Traders */
		traderCriteria.add(Restrictions.isNull("group")); //Tradergroupid is null
		
		//Add a username restriction
		Criteria userCriteria  = traderCriteria.createCriteria("user");
		userCriteria.add(Restrictions.eq("username", user.getUsername()));
		
		return traderCriteria.list();
		
	}
	
	public List<Trader> loadClosedTradersForUser(User user){
		
		return null;
	}

	/**
	 * Load any traders with status=status and code=code
	 * @param code
	 */
	public List<Trader> loadTradersSelling(TraderStatus status, CurrencyCodeEnum code) {
		
		Criteria traderCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trader.class);
		traderCriteria.add(Restrictions.isNotNull("group")); //They must be in a group (in a trade)
		traderCriteria.add(Restrictions.eq("status", status.name())); //They must be waiting for deposit
		
		Criteria currencyCriteria = traderCriteria.createCriteria("currencyToSell"); //Create a criteria on currency
		Criteria currencyTypeCriteria = currencyCriteria.createCriteria("type");
		currencyTypeCriteria.add(Restrictions.eq("code", code));
		
		return traderCriteria.list();
		
	}
	
	
	/**
	 * For use in the Deposit Simulator
	 * @param ids
	 * @param status
	 * @param code
	 * @return
	 */
	public List<Trader> loadTradersSelling(List<Long> ids, TraderStatus status, CurrencyCodeEnum code){
		
		Criteria traderCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trader.class);
		traderCriteria.add(Restrictions.isNotNull("group")); //They must be in a group (in a trade)
		traderCriteria.add(Restrictions.eq("status", status.name())); //They must be waiting for deposit
		
		if(ids != null){
			//Add the exceptions
			for(int i=0; i<ids.size(); i++){
				traderCriteria.add(Restrictions.ne("traderid", ids.get(i)));
			}
		}
		Criteria currencyCriteria = traderCriteria.createCriteria("currencyToSell"); //Create a criteria on currency
		Criteria currencyTypeCriteria = currencyCriteria.createCriteria("type");
		currencyTypeCriteria.add(Restrictions.eq("code", code));
		
		return traderCriteria.list();
		
	}

	/**
	 * Load traders with the following ids
	 * @param buyingAud
	 */
	public List<Trader> loadTraders(List<Integer> traderIds) {
		
		Criteria traderCriteria = this.getSessionFactory().getCurrentSession().createCriteria(Trader.class);
		
		
		//Add the exceptions
		for(int i=0; i<traderIds.size(); i++){
			traderCriteria.add(Restrictions.ne("traderid", traderIds.get(i)));
		}
		
		return traderCriteria.list();
		
	}

	
}
