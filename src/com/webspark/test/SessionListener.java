package com.webspark.test;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.webspark.dao.LoginDao;

/**
 * @Author Jinshad P.T.
 * @Date Jan 6 2013
 */

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

		if (arg0.getSession() != null) {
			try {

				new LoginDao().doRecordUserLogout((Long) arg0.getSession()
						.getAttribute("login_history_id"));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}