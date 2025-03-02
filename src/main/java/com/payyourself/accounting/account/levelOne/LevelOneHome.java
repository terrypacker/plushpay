package com.payyourself.accounting.account.levelOne;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class LevelOne.
 * @see com.payyourself.accounting.account.levelOne.LevelOne
 * @author Hibernate Tools
 */
public class LevelOneHome {

	private static final Log log = LogFactory.getLog(LevelOneHome.class);

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

	public void persist(LevelOne transientInstance) {
		log.debug("persisting LevelOne instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(LevelOne instance) {
		log.debug("attaching dirty LevelOne instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(LevelOne instance) {
		log.debug("attaching clean LevelOne instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(LevelOne persistentInstance) {
		log.debug("deleting LevelOne instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public LevelOne merge(LevelOne detachedInstance) {
		log.debug("merging LevelOne instance");
		try {
			LevelOne result = (LevelOne) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public LevelOne findById(int id) {
		log.debug("getting LevelOne instance with id: " + id);
		try {
			LevelOne instance = (LevelOne) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.accounting.account.levelOne.LevelOne",
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

	public List<LevelOne> findByExample(LevelOne instance) {
		log.debug("finding LevelOne instance by example");
		try {
			List<LevelOne> results = (List<LevelOne>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.accounting.account.levelOne.LevelOne")
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
