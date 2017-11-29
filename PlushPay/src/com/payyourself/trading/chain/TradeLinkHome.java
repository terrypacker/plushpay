package com.payyourself.trading.chain;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class TradeLink.
 * @see com.payyourself.trading.chain.TradeLink
 * @author Hibernate Tools
 */
public class TradeLinkHome {

	private static final Log log = LogFactory.getLog(TradeLinkHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(TradeLink transientInstance) {
		log.debug("persisting TradeLink instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TradeLink instance) {
		log.debug("attaching dirty TradeLink instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TradeLink instance) {
		log.debug("attaching clean TradeLink instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TradeLink persistentInstance) {
		log.debug("deleting TradeLink instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TradeLink merge(TradeLink detachedInstance) {
		log.debug("merging TradeLink instance");
		try {
			TradeLink result = (TradeLink) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TradeLink findById(int id) {
		log.debug("getting TradeLink instance with id: " + id);
		try {
			TradeLink instance = (TradeLink) sessionFactory.getCurrentSession()
					.get("com.payyourself.trading.chain.TradeLink", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<TradeLink> findByExample(TradeLink instance) {
		log.debug("finding TradeLink instance by example");
		try {
			List<TradeLink> results = (List<TradeLink>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.trading.chain.TradeLink").add(
							create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
