package com.payyourself.registration;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class RegistrationInfo.
 * @see com.payyourself.registration.RegistrationInfo
 * @author Hibernate Tools
 */
public class RegistrationInfoHome {

	private static final Log log = LogFactory
			.getLog(RegistrationInfoHome.class);

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

	public void persist(RegistrationInfo transientInstance) {
		log.debug("persisting RegistrationInfo instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(RegistrationInfo instance) {
		log.debug("attaching dirty RegistrationInfo instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RegistrationInfo instance) {
		log.debug("attaching clean RegistrationInfo instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(RegistrationInfo persistentInstance) {
		log.debug("deleting RegistrationInfo instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RegistrationInfo merge(RegistrationInfo detachedInstance) {
		log.debug("merging RegistrationInfo instance");
		try {
			RegistrationInfo result = (RegistrationInfo) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public RegistrationInfo findById(int id) {
		log.debug("getting RegistrationInfo instance with id: " + id);
		try {
			RegistrationInfo instance = (RegistrationInfo) sessionFactory
					.getCurrentSession()
					.get("com.payyourself.registration.RegistrationInfo", id);
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

	public List<RegistrationInfo> findByExample(RegistrationInfo instance) {
		log.debug("finding RegistrationInfo instance by example");
		try {
			List<RegistrationInfo> results = (List<RegistrationInfo>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.registration.RegistrationInfo")
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
