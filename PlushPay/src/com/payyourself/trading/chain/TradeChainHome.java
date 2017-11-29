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
 * Home object for domain model class TradeChain.
 * @see com.payyourself.trading.chain.TradeChain
 * @author Hibernate Tools
 */
public class TradeChainHome {

	private static final Log log = LogFactory.getLog(TradeChainHome.class);

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

	public void persist(TradeChain transientInstance) {
		log.debug("persisting TradeChain instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TradeChain instance) {
		log.debug("attaching dirty TradeChain instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TradeChain instance) {
		log.debug("attaching clean TradeChain instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TradeChain persistentInstance) {
		log.debug("deleting TradeChain instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TradeChain merge(TradeChain detachedInstance) {
		log.debug("merging TradeChain instance");
		try {
			TradeChain result = (TradeChain) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TradeChain findById(int id) {
		log.debug("getting TradeChain instance with id: " + id);
		try {
			TradeChain instance = (TradeChain) sessionFactory
					.getCurrentSession().get(
							"com.payyourself.trading.chain.TradeChain", id);
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

	public List<TradeChain> findByExample(TradeChain instance) {
		log.debug("finding TradeChain instance by example");
		try {
			List<TradeChain> results = (List<TradeChain>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.trading.chain.TradeChain").add(
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
