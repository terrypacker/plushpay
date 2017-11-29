package com.payyourself.trading.beneficiary;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Beneficiary.
 * @see com.payyourself.trading.beneficiary.Beneficiary
 * @author Hibernate Tools
 */
public class BeneficiaryHome {

	private static final Log log = LogFactory.getLog(BeneficiaryHome.class);

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

	public void persist(Beneficiary transientInstance) {
		log.debug("persisting Beneficiary instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Beneficiary instance) {
		log.debug("attaching dirty Beneficiary instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Beneficiary instance) {
		log.debug("attaching clean Beneficiary instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Beneficiary persistentInstance) {
		log.debug("deleting Beneficiary instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Beneficiary merge(Beneficiary detachedInstance) {
		log.debug("merging Beneficiary instance");
		try {
			Beneficiary result = (Beneficiary) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Beneficiary findById(long id) {
		log.debug("getting Beneficiary instance with id: " + id);
		try {
			Beneficiary instance = (Beneficiary) sessionFactory
					.getCurrentSession().get(
							"com.payyourself.trading.beneficiary.Beneficiary",
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

	public List<Beneficiary> findByExample(Beneficiary instance) {
		log.debug("finding Beneficiary instance by example");
		try {
			List<Beneficiary> results = (List<Beneficiary>) sessionFactory
					.getCurrentSession().createCriteria(
							"com.payyourself.trading.beneficiary.Beneficiary")
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
