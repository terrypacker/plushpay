package com.payyourself.trading.beneficiary.tradeBeneficiary;

// Generated Apr 24, 2010 1:54:06 PM by Hibernate Tools 3.2.5.Beta and Terry Packer

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class TradeBeneficiary.
 * @see com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary
 * @author Hibernate Tools
 */
public class TradeBeneficiaryHome {

	private static final Log log = LogFactory
			.getLog(TradeBeneficiaryHome.class);

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

	public void persist(TradeBeneficiary transientInstance) {
		log.debug("persisting TradeBeneficiary instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TradeBeneficiary instance) {
		log.debug("attaching dirty TradeBeneficiary instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TradeBeneficiary instance) {
		log.debug("attaching clean TradeBeneficiary instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TradeBeneficiary persistentInstance) {
		log.debug("deleting TradeBeneficiary instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TradeBeneficiary merge(TradeBeneficiary detachedInstance) {
		log.debug("merging TradeBeneficiary instance");
		try {
			TradeBeneficiary result = (TradeBeneficiary) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TradeBeneficiary findById(long id) {
		log.debug("getting TradeBeneficiary instance with id: " + id);
		try {
			TradeBeneficiary instance = (TradeBeneficiary) sessionFactory
					.getCurrentSession()
					.get(
							"com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary",
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

	public List<TradeBeneficiary> findByExample(TradeBeneficiary instance) {
		log.debug("finding TradeBeneficiary instance by example");
		try {
			List<TradeBeneficiary> results = (List<TradeBeneficiary>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"com.payyourself.trading.beneficiary.tradeBeneficiary.TradeBeneficiary")
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
