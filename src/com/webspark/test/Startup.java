package com.webspark.test;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import com.webspark.core.SScheduler;
import com.webspark.dao.LoginDao;

@WebListener
public class Startup implements javax.servlet.ServletContextListener {

	public void contextDestroyed(ServletContextEvent contextEvent) {
	        System.out.println("Context Destroyed");
	        
	}

	public void contextInitialized(ServletContextEvent context) {
		try {
			
//			System.out.println("Hai Scheduler Start");
			new LoginDao().doLogoutAllUsers();
//			Calendar cal1 = Calendar.getInstance();
//			cal1.set(Calendar.HOUR_OF_DAY, 24-cal1.get(Calendar.HOUR_OF_DAY));
//			SScheduler scheduler=new SScheduler(cal1.getTime().getHours(), 24, 0,TimeUnit.HOURS);
//			scheduler.activateScheduler();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}