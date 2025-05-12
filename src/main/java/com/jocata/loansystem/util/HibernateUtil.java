/*
package com.jocata.loansystem.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    // Initialize the SessionFactory
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    private SessionFactory buildSessionFactory() {
        // Build SessionFactory from DataSource
        Configuration configuration = new Configuration();
        configuration.setDataSource(dataSource); // Set the Spring DataSource
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.format_sql", "true");

        // Return the SessionFactory
        return configuration.buildSessionFactory();
    }

    // Generic method to open session
    public <T> T find(Class<T> clazz, long id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.get(clazz, id);
        }
    }

    // Generic method to save an entity
    public <T> void save(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        }
    }

    // Generic method to update an entity
    public <T> void update(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
        }
    }

    // Generic method to delete an entity
    public <T> void delete(Class<T> clazz, long id) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            T entity = session.get(clazz, id);
            if (entity != null) {
                session.delete(entity);
            }
            session.getTransaction().commit();
        }
    }

    // Generic method to list all entities
    public <T> List<T> list(Class<T> clazz) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("from " + clazz.getName(), clazz).getResultList();
        }
    }
}

*/
