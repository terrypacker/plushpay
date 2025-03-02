package com.payyourself.currency.type;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class PyCurrencyType.
 * @see com.payyourself.currency.type.PyCurrencyType
 * @author Hibernate Tools
 */
public class PyCurrencyTypeHome {

	private static final Log log = LogFactory.getLog(PyCurrencyTypeHome.class);

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

	public void persist(PyCurrencyType transientInstance) {
		log.debug("persisting PyCurrencyType instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(PyCurrencyType instance) {
		log.debug("attaching dirty PyCurrencyType instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(PyCurrencyType instance) {
		log.debug("attaching clean PyCurrencyType instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(PyCurrencyType persistentInstance) {
		log.debug("deleting PyCurrencyType instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public PyCurrencyType merge(PyCurrencyType detachedInstance) {
		log.debug("merging PyCurrencyType instance");
		try {
			PyCurrencyType result = (PyCurrencyType) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public PyCurrencyType findById(long id) {
		log.debug("getting PyCurrencyType instance with id: " + id);
		try {
			PyCurrencyType instance = (PyCurrencyType) sessionFactory
					.getCurrentSession().get(
							"com.payyourself.currency.type.PyCurrencyType", id);
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

	public List<PyCurrencyType> findByExample(PyCurrencyType instance) {
		log.debug("finding PyCurrencyType instance by example");
		try {
			List<PyCurrencyType> results = (List<PyCurrencyType>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.currency.type.PyCurrencyType")
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
