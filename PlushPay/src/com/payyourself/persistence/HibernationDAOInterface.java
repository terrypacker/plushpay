package com.payyourself.persistence;

import java.util.List;

/**
 * Example DAO Interface that all DAO's should 
 * implement of some type.
 * @author tpacker
 *
 */
public interface HibernationDAOInterface {

	public Object persist(Object obj);
	
	public void delete(Object obj);
	

	
}
