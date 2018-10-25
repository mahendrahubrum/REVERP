package com.webspark.dao;

import org.hibernate.FlushMode;
import org.hibernate.Session;

/**
 * @Author Jinshad P.T.
 */


public class SHibernate {

	private static final ThreadLocal THREAD = new ThreadLocal();

	protected SHibernate() {
	}

	public static Session getSession() throws Exception {

		Session session = (Session) THREAD.get();

		if (session == null) {
			session = SHibernateUtil.getSessionFactory().openSession();
			THREAD.set(session);
			getSession().setFlushMode(FlushMode.COMMIT);
		}
		return session;

	}

	protected static void begin() throws Exception {
		getSession().beginTransaction().begin();
	}

	protected static void commit() throws Exception {
		getSession().getTransaction().commit();
	}

	protected static void rollback() throws Exception {
		getSession().getTransaction().rollback();
		getSession().close();
		THREAD.set(null);
	}

	protected static void flush() throws Exception {
		getSession().flush();
	}

	protected static void close() throws Exception {
		getSession().close();
		THREAD.set(null);
	}

}
