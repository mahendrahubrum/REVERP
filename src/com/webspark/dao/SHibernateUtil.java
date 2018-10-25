package com.webspark.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * @Author Jinshad P.T.
 */

public class SHibernateUtil implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2798194380832622120L;
	private static final SessionFactory sessionFactory;

    static {
        try {
        	
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() throws Exception {
        try {
        } catch (Exception exp) {
            throw new Exception(exp.getMessage());
        } finally {
            return sessionFactory;
        }
    }
}