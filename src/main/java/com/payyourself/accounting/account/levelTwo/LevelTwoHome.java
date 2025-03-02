package com.payyourself.accounting.account.levelTwo;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class LevelTwo.
 * @see com.payyourself.accounting.account.levelTwo.LevelTwo
 * @author Hibernate Tools
 */
public class LevelTwoHome {

	private static final Log log = LogFactory.getLog(LevelTwoHome.class);

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

	public void persist(LevelTwo transientInstance) {
		log.debug("persisting LevelTwo instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(LevelTwo instance) {
		log.debug("attaching dirty LevelTwo instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(LevelTwo instance) {
		log.debug("attaching clean LevelTwo instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(LevelTwo persistentInstance) {
		log.debug("deleting LevelTwo instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public LevelTwo merge(LevelTwo detachedInstance) {
		log.debug("merging LevelTwo instance");
		try {
			LevelTwo result = (LevelTwo) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public LevelTwo findById(int id) {
		log.debug("getting LevelTwo instance with id: " + id);
		try {
			LevelTwo instance = (LevelTwo) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.accounting.account.levelTwo.LevelTwo",
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

	public List<LevelTwo> findByExample(LevelTwo instance) {
		log.debug("finding LevelTwo instance by example");
		try {
			List<LevelTwo> results = (List<LevelTwo>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.accounting.account.levelTwo.LevelTwo")
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
