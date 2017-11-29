package com.payyourself.currency;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class PyCurrency.
 * @see com.payyourself.currency.PyCurrency
 * @author Hibernate Tools
 */
public class PyCurrencyHome {

	private static final Log log = LogFactory.getLog(PyCurrencyHome.class);

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

	public void persist(PyCurrency transientInstance) {
		log.debug("persisting PyCurrency instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(PyCurrency instance) {
		log.debug("attaching dirty PyCurrency instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(PyCurrency instance) {
		log.debug("attaching clean PyCurrency instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(PyCurrency persistentInstance) {
		log.debug("deleting PyCurrency instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public PyCurrency merge(PyCurrency detachedInstance) {
		log.debug("merging PyCurrency instance");
		try {
			PyCurrency result = (PyCurrency) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public PyCurrency findById(long id) {
		log.debug("getting PyCurrency instance with id: " + id);
		try {
			PyCurrency instance = (PyCurrency) sessionFactory
					.getCurrentSession().get(
							"com.payyourself.currency.PyCurrency", id);
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

	public List<PyCurrency> findByExample(PyCurrency instance) {
		log.debug("finding PyCurrency instance by example");
		try {
			List<PyCurrency> results = (List<PyCurrency>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.currency.PyCurrency").add(
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
