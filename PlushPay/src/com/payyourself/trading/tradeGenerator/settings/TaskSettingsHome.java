package com.payyourself.trading.tradeGenerator.settings;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class TaskSettings.
 * @see com.payyourself.trading.tradeGenerator.settings.TaskSettings
 * @author Hibernate Tools
 */
public class TaskSettingsHome {

	private static final Log log = LogFactory.getLog(TaskSettingsHome.class);

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

	public void persist(TaskSettings transientInstance) {
		log.debug("persisting TaskSettings instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TaskSettings instance) {
		log.debug("attaching dirty TaskSettings instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TaskSettings instance) {
		log.debug("attaching clean TaskSettings instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TaskSettings persistentInstance) {
		log.debug("deleting TaskSettings instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TaskSettings merge(TaskSettings detachedInstance) {
		log.debug("merging TaskSettings instance");
		try {
			TaskSettings result = (TaskSettings) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TaskSettings findById(long id) {
		log.debug("getting TaskSettings instance with id: " + id);
		try {
			TaskSettings instance = (TaskSettings) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.trading.tradeGenerator.settings.TaskSettings",
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

	public List<TaskSettings> findByExample(TaskSettings instance) {
		log.debug("finding TaskSettings instance by example");
		try {
			List<TaskSettings> results = (List<TaskSettings>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.trading.tradeGenerator.settings.TaskSettings")
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
