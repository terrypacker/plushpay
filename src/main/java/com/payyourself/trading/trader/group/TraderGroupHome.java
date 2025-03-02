package com.payyourself.trading.trader.group;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.Collections;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class TraderGroup.
 * @see com.payyourself.trading.trader.group.TraderGroup
 * @author Hibernate Tools
 */
public class TraderGroupHome {

	private static final Log log = LogFactory.getLog(TraderGroupHome.class);

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

	public void persist(TraderGroup transientInstance) {
		log.debug("persisting TraderGroup instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TraderGroup instance) {
		log.debug("attaching dirty TraderGroup instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TraderGroup instance) {
		log.debug("attaching clean TraderGroup instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TraderGroup persistentInstance) {
		log.debug("deleting TraderGroup instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TraderGroup merge(TraderGroup detachedInstance) {
		log.debug("merging TraderGroup instance");
		try {
			TraderGroup result = (TraderGroup) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TraderGroup findById(long id) {
		log.debug("getting TraderGroup instance with id: " + id);
		try {
			TraderGroup instance = (TraderGroup) sessionFactory
					.getCurrentSession().get(
							"com.payyourself.trading.trader.group.TraderGroup",
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

	public List<TraderGroup> findByExample(TraderGroup instance) {
		log.debug("finding TraderGroup instance by example");
		try {
			List<TraderGroup> results = (List<TraderGroup>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.trading.trader.group.TraderGroup")
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
