package com.payyourself.currency.type;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.payyourself.currency.code.CurrencyCodeEnum;

public class PyCurrencyTypeHibernation extends PyCurrencyTypeHome {

	public PyCurrencyTypeHibernation(){
		super();
	}


	/**
	 * This needs work after we add a "default" or "base" column to the table.
	 * @return
	 * @throws Exception 
	 */
	public PyCurrencyType getBaseCurrency(){

		Session session = this.getSessionFactory().getCurrentSession();

		String SQL_QUERY = "from PyCurrencyType as currencyType where base = true order by currencyType.id desc";
		
		Query query = session.createQuery(SQL_QUERY);
		query.setMaxResults(1); //To do quickly

		return (PyCurrencyType) query.list().get(0);
	}
	
	public PyCurrencyType getType(long id){
		return this.findById(id);
		
	}
	
	
	public List<PyCurrencyType> copyAllTypes() {
		Session session = this.getSessionFactory().getCurrentSession();
		
	    //Create Select Clause SQL
	     String SQL_QUERY ="from PyCurrencyType";
		 Query query = session.createQuery(SQL_QUERY);
		 
		 List<PyCurrencyType> slist = new ArrayList<PyCurrencyType>();
		 
		 for(Iterator<PyCurrencyType> it=query.iterate();it.hasNext();){
		       PyCurrencyType row =  new PyCurrencyType(it.next());
		       slist.add(row);
		     }

		 return slist;
	}


	public void persist(List<PyCurrencyType> rates) {
		
		for(int i=0; i<rates.size(); i++){
			this.persist(rates.get(i));
		}
		
	}
	
	
	public PyCurrencyType getCurrentUsd(){
		
		return this.getCurrentType(CurrencyCodeEnum.USD);		
	}
	
	public PyCurrencyType getCurrentAud(){

		return this.getCurrentType(CurrencyCodeEnum.AUD);
		
	}

	/**
	 * Return the most current entry for this code
	 * @param code
	 * @return
	 */
	private PyCurrencyType getCurrentType(CurrencyCodeEnum code){
		
		Session session = this.getSessionFactory().getCurrentSession();

		String SQL_QUERY = "from PyCurrencyType as currencyType where code = '";
		SQL_QUERY = SQL_QUERY + code.name() + "' order by currencyType.id desc";
		
		Query query = session.createQuery(SQL_QUERY);
		query.setMaxResults(1); //To do quickly
		
		return (PyCurrencyType) query.list().get(0);
		
		
		
	}
	
	
	/**
	 * Load the most recent types for all currencies
	 * @return
	 */
	public List<PyCurrencyType> loadAllTypes() {
		List<PyCurrencyType> types = new ArrayList<PyCurrencyType>();
        CurrencyCodeEnum[] list = CurrencyCodeEnum.values();
        for( CurrencyCodeEnum enumItem : list ) {
           types.add(this.getCurrentType(enumItem));
        }
        return types;
	}
	
}
