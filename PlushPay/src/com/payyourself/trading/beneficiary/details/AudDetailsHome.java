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
 * Home object for domain model class AudDetails.
 * @see com.payyourself.trading.beneficiary.details.AudDetails
 * @author Hibernate Tools
 */
public class AudDetailsHome {

	private static final Log log = LogFactory.getLog(AudDetailsHome.class);

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

	public void persist(AudDetails transientInstance) {
		log.debug("persisting AudDetails instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(AudDetails instance) {
		log.debug("attaching dirty AudDetails instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(AudDetails instance) {
		log.debug("attaching clean AudDetails instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(AudDetails persistentInstance) {
		log.debug("deleting AudDetails instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public AudDetails merge(AudDetails detachedInstance) {
		log.debug("merging AudDetails instance");
		try {
			AudDetails result = (AudDetails) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public AudDetails findById(long id) {
		log.debug("getting AudDetails instance with id: " + id);
		try {
			AudDetails instance = (AudDetails) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.trading.beneficiary.details.AudDetails",
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

	public List<AudDetails> findByExample(AudDetails instance) {
		log.debug("finding AudDetails instance by example");
		try {
			List<AudDetails> results = (List<AudDetails>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.trading.beneficiary.details.AudDetails")
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
