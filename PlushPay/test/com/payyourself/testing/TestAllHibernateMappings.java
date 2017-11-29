package com.payyourself.testing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.EntityPersister;


import com.payyourself.persistence.HibernateUtil;

import junit.framework.TestCase;


public class TestAllHibernateMappings extends TestCase {
    

    public void testEverything() throws Exception {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Map metadata = sessionFactory.getAllClassMetadata();
        for (Iterator i = metadata.values().iterator(); i.hasNext(); ) {
            Session session = sessionFactory.openSession();
            try {
                EntityPersister persister = (EntityPersister) i.next();
                String className = persister.getClassMetadata().getEntityName();
                List result = session.createQuery("from " + className + " c").list();
            } finally {
                session.close();
            }
        }
    }
    
    
}

