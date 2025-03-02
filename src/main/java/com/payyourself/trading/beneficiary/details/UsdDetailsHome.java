package com.payyourself.trading.beneficiary.details;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class UsdDetails.
 * @see com.payyourself.trading.beneficiary.details.UsdDetails
 * @author Hibernate Tools
 */
public class UsdDetailsHome {

	private static final Log log = LogFactory.getLog(UsdDetailsHome.class);

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

	public void persist(UsdDetails transientInstance) {
		log.debug("persisting UsdDetails instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UsdDetails instance) {
		log.debug("attaching dirty UsdDetails instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UsdDetails instance) {
		log.debug("attaching clean UsdDetails instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UsdDetails persistentInstance) {
		log.debug("deleting UsdDetails instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UsdDetails merge(UsdDetails detachedInstance) {
		log.debug("merging UsdDetails instance");
		try {
			UsdDetails result = (UsdDetails) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UsdDetails findById(long id) {
		log.debug("getting UsdDetails instance with id: " + id);
		try {
			UsdDetails instance = (UsdDetails) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.trading.beneficiary.details.UsdDetails",
							id);
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

	public List<UsdDetails> findByExample(UsdDetails instance) {
		log.debug("finding UsdDetails instance by example");
		try {
			List<UsdDetails> results = (List<UsdDetails>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.trading.beneficiary.details.UsdDetails")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
