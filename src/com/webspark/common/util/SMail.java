package com.webspark.common.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.server.WrappedSession;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.model.EmailConfigurationModel;

/**
 * Document : Mail Created on : May 29, 2013, 5:59:19 PM
 * 
 * @Author Jinshad P.T.
 */

public class SMail implements Serializable{

	private static final long serialVersionUID = 6520379807391863176L;
	
//	 private static String HOST = "smtp.gmail.com";
	private static String HOST1 = "gmail-smtp-msa.l.google.com";
	private static String HOST2 = "smtp.mail.yahoo.com";
	private static String HOST3 = "rs16.websitehostserver.net";

	private static String USER = "mail@zinespark.com"; // prep4e@gmail.com
	private static String PASSWORD = "welcome"; // cortest01
	private static String PORT = "25";
	private static String FROM = "mail@zinespark.com";
	private static String STARTTLS = "true";
	private static String AUTH = "true";
	private static String DEBUG = "true";
	private static String SSL = "false";
	private static String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
	// private static DateOperations dateUtil = new DateOperations();
	// HttpServletRequest request = ServletActionContext.getRequest();
	// HttpSession session = request.getSession();
	WrappedSession session = new SessionUtil().getHttpSession();

	public synchronized void sendMail(Address[] mailTo, String Content,
			String email_subject, String from) throws Exception {
		// String TO = mailTo;
		String TEXT = "";
		
		try {
			if (from != null && from.length() > 0) {
				new InternetAddress(from);
				FROM = from;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		WrappedSession vaadinSession = new SessionUtil().getHttpSession();
		SettingsValuePojo settings = (SettingsValuePojo) vaadinSession
				.getAttribute("settings");

		String mailId = settings.getSYSTEM_EMAIL();
		String mailPasswd = settings.getSYSTEM_EMAIL_PASSWORD();
		
		String host = HOST3;

		if (mailId != null) {
			if (mailId.contains("gmail")) {
				host = HOST1;
			} else if (mailId.contains("yahoo")) {
				host = HOST2;
			}
		}
			

		TEXT = Content + " \n";

		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.user", USER);
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.starttls.enable", STARTTLS);
		props.put("mail.smtp.debug", DEBUG);
		props.put("mail.smtp.ssl.enable", SSL);
//		props.put("mail.smtp.socketFactory.port", PORT);
//		props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//		props.put("mail.smtp.socketFactory.fallback", "false");

		try {
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(true);
			MimeMessage message = new MimeMessage(session);
			message.setText(TEXT);
			message.setSubject(email_subject);
			message.setFrom(new InternetAddress(FROM));
			// for(int i=0;i<mailTo.length;i++){
			message.addRecipients(RecipientType.TO, mailTo);
			// }
			message.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(HOST1, USER, PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			System.out.println(e);
			throw new Exception("Exception in Mail.sendMail" + e.getMessage());
		}
	}
	

	public synchronized void sendSparkMail(Address[] mailTo, String content,
			String email_subject, File file,boolean isSale) throws Exception {

		WrappedSession vaadinSession = new SessionUtil().getHttpSession();
		SettingsValuePojo settings = (SettingsValuePojo) vaadinSession
				.getAttribute("settings");

		String mailId = settings.getSYSTEM_EMAIL();
		String mailPasswd = settings.getSYSTEM_EMAIL_PASSWORD();
		String host = settings.getSYSTEM_EMAIL_HOST();

		if(isSale){
			host=settings.getSALES_EMAIL_HOST();
			mailId=settings.getSALES_EMAIL();
			mailPasswd = settings.getSALES_EMAIL_PASSWORD();
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.user", USER);
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.starttls.enable", STARTTLS);
		props.put("mail.smtp.debug", DEBUG);
		props.put("mail.smtp.ssl.enable", SSL);
//		props.put("mail.smtp.socketFactory.port", PORT);
//		props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//		props.put("mail.smtp.socketFactory.fallback", "false");

		try {
			InternetAddress emailAddr = new InternetAddress(mailId);
			emailAddr.validate();
		} catch (Exception ex) {
			mailId = USER;
			mailPasswd = PASSWORD;
		}

		try {
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(true);
			
			
			
			MimeMessage message = new MimeMessage(session);
			message.setSubject(email_subject);
			message.setFrom(new InternetAddress(mailId));
			message.addRecipients(RecipientType.TO, mailTo);
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(content);
			messageBodyPart.setContent(content, "text/html");
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			if (file != null && file.exists()) {
				MimeBodyPart attachPart = new MimeBodyPart();
				attachPart.attachFile(file);
				attachPart.setFileName(file.getName());
				multipart.addBodyPart(attachPart);

			}
			
			message.setContent(multipart);
			message.saveChanges();

			Transport transport = session.getTransport("smtp");
			transport.connect(host, mailId, mailPasswd);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	public synchronized void sendMailWithFromAddress(Address[] mailTo, String content,
				String email_subject, String from) throws Exception {
		
		try {
			if(from!=null && from.length()>0) {
				new InternetAddress(from).validate();
				FROM=from;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

			WrappedSession vaadinSession = new SessionUtil().getHttpSession();
			SettingsValuePojo settings = (SettingsValuePojo) vaadinSession
					.getAttribute("settings");

			String mailId = settings.getSYSTEM_EMAIL();
			String mailPasswd = settings.getSYSTEM_EMAIL_PASSWORD();
			
			String host = HOST3;

			if (mailId != null) {
				if (mailId.contains("gmail")) {
					host = HOST1;
				} else if (mailId.contains("yahoo")) {
					host = HOST2;
				}
			}

			Properties props = new Properties();
			props.put("mail.smtp.host", host);

			props.put("mail.smtp.port", PORT);
			props.put("mail.smtp.user", USER);
			props.put("mail.smtp.auth", AUTH);
			props.put("mail.smtp.starttls.enable", STARTTLS);
			props.put("mail.smtp.debug", DEBUG);
			props.put("mail.smtp.ssl.enable", SSL);
//			props.put("mail.smtp.socketFactory.port", PORT);
//			props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//			props.put("mail.smtp.socketFactory.fallback", "false");

			try {
				InternetAddress emailAddr = new InternetAddress(mailId);
				emailAddr.validate();
			} catch (Exception ex) {
				mailId = USER;
				mailPasswd = PASSWORD;
			}

			try {
				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(true);
				MimeMessage message = new MimeMessage(session);
				message.setText(content);
				message.setSubject(email_subject);
				message.setFrom(new InternetAddress(mailId));
				message.addRecipients(RecipientType.TO, mailTo);
				message.setContent(content,"text/html");

				message.saveChanges();

				Transport transport = session.getTransport("smtp");
				transport.connect(host, mailId, mailPasswd);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	
	
	public synchronized void sendMailFromSystemMail(Address[] mailTo, String content,
			String email_subject, File attachment, String from) throws Exception {
	
		try {
			if(from!=null && from.length()>0) {
				new InternetAddress(from).validate();
				FROM=from;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	
			WrappedSession vaadinSession = new SessionUtil().getHttpSession();
			SettingsValuePojo settings = (SettingsValuePojo) vaadinSession
					.getAttribute("settings");
	
			String mailId = settings.getSYSTEM_EMAIL();
			String mailPasswd = settings.getSYSTEM_EMAIL_PASSWORD();
			
			String host = HOST3;
	
			if (mailId != null) {
				if (mailId.contains("gmail")) {
					host = HOST1;
				} else if (mailId.contains("yahoo")) {
					host = HOST2;
				}
			}
	
			Properties props = new Properties();
			props.put("mail.smtp.host", host);
	
			props.put("mail.smtp.port", PORT);
			props.put("mail.smtp.user", USER);
			props.put("mail.smtp.auth", AUTH);
			props.put("mail.smtp.starttls.enable", STARTTLS);
			props.put("mail.smtp.debug", DEBUG);
			props.put("mail.smtp.ssl.enable", SSL);
//			props.put("mail.smtp.socketFactory.port", PORT);
//			props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//			props.put("mail.smtp.socketFactory.fallback", "false");
	
			try {
				InternetAddress emailAddr = new InternetAddress(mailId);
				emailAddr.validate();
			} catch (Exception ex) {
				mailId = USER;
				mailPasswd = PASSWORD;
			}
	
			try {
				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(true);
				
				MimeMessage message = new MimeMessage(session);
				message.setSubject(email_subject);
				message.setFrom(new InternetAddress(mailId));
				message.addRecipients(RecipientType.TO, mailTo);
				
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(content);
				messageBodyPart.setContent(content, "text/html");
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				if (attachment != null && attachment.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					attachPart.attachFile(attachment);
					attachPart.setFileName(attachment.getName());
					multipart.addBodyPart(attachPart);

				}
				message.setContent(multipart);
				message.saveChanges();
	
				Transport transport = session.getTransport("smtp");
				transport.connect(host, mailId, mailPasswd);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	
	
	public synchronized void sendMailWithFromAddress(Address[] mailTo,String Content,String email_subject,File attachment,long userId) throws Exception {
    	
	      EmailConfigurationModel configModel=new EmailConfigDao().getEmailConfiguration(userId);
	      String TEXT="";
	      
	      TEXT =Content+" \n";
	      
	      Properties props = new Properties();
	      props.put("mail.smtp.host", configModel.getHost_name());
	      
	      props.put("mail.smtp.port", PORT);
	      props.put("mail.smtp.user", configModel.getUsername());
	      props.put("mail.smtp.auth", AUTH);
	      props.put("mail.smtp.starttls.enable", STARTTLS);
	      props.put("mail.smtp.debug", DEBUG);
	      props.put("mail.smtp.ssl.enable", SSL);
//	      props.put("mail.smtp.socketFactory.port", PORT);
//	      props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//	      props.put("mail.smtp.socketFactory.fallback", "false");
	      
	      try {

    	  	Session session = Session.getDefaultInstance(props, null);
    	  	session.setDebug(true);
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(configModel.getUsername()));
			message.addRecipients(RecipientType.TO, mailTo);
			message.setSubject(email_subject);

			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(TEXT);
			multipart.addBodyPart(messageBodyPart);
			if (attachment != null && attachment.exists()) {
				
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment);
				mimeBodyPart.setDataHandler(new DataHandler(source));
				mimeBodyPart.setFileName(attachment.getName());
				multipart.addBodyPart(mimeBodyPart);
				
			}
			message.setContent(multipart);
			message.saveChanges();

			Transport transport = session.getTransport("smtp");
			transport.connect(configModel.getHost_name(),  configModel.getUsername(),  configModel.getPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
				
	      } catch (Exception e) {
	      	e.printStackTrace();
	          throw new Exception("Exception in Mail" + e.getMessage());
	      }
	  }
	
	
	public synchronized void sendMailFromUserEmail(Address[] mailTo,String Content,String email_subject,File attachment,long userId, String from) throws Exception {
    	
	      EmailConfigurationModel configModel=new EmailConfigDao().getEmailConfiguration(userId);
	      String TEXT="";
	      
	      try {
				if(from!=null && from.length()>0) {
					new InternetAddress(from).validate();
					FROM=from;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
	      
	      TEXT =Content+" \n";
	      
	      Properties props = new Properties();
	      props.put("mail.smtp.host", configModel.getHost_name());
	      
	      props.put("mail.smtp.port", PORT);
	      props.put("mail.smtp.user", configModel.getUsername());
	      props.put("mail.smtp.auth", AUTH);
	      props.put("mail.smtp.starttls.enable", STARTTLS);
	      props.put("mail.smtp.debug", DEBUG);
	      props.put("mail.smtp.ssl.enable", SSL);
//	      props.put("mail.smtp.socketFactory.port", PORT);
//	      props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//	      props.put("mail.smtp.socketFactory.fallback", "false");
	    
	    
	      
	      try {

				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(true);
				
				MimeMessage message = new MimeMessage(session);
				message.setSubject(email_subject);
				message.setFrom(new InternetAddress( configModel.getUsername()));
				message.addRecipients(RecipientType.TO, mailTo);
				
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(TEXT);
				messageBodyPart.setContent(TEXT, "text/html");
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				if (attachment != null && attachment.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					attachPart.attachFile(attachment);
					attachPart.setFileName(attachment.getName());
					multipart.addBodyPart(attachPart);
				}
				message.setContent(multipart);
				message.saveChanges();

				Transport transport = session.getTransport("smtp");
				transport.connect(configModel.getHost_name(),  configModel.getUsername(),  configModel.getPassword());
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
				
	      } catch (Exception e) {
	      	System.out.println(e);
	          throw new Exception("Exception in Mail.sendMail" + e.getMessage());
	      }
	  }
	
	
	public synchronized void sendMailFromAppMail(Address[] mailTo, String content,
			String email_subject, File attachment) throws Exception {
	
	
			WrappedSession vaadinSession = new SessionUtil().getHttpSession();
			SettingsValuePojo settings = (SettingsValuePojo) vaadinSession
					.getAttribute("settings");
	
			String mailId = settings.getAPPLICATION_EMAIL();
			String mailPasswd = settings.getAPPLICATION_EMAIL_PASSWORD();
			String host=settings.getAPPLICATION_EMAIL_HOST();
			
			
			try {
				InternetAddress emailAddr = new InternetAddress(mailId);
				emailAddr.validate();
			} catch (Exception ex) {
				mailId = USER;
				mailPasswd = PASSWORD;
				host=HOST1;
			}
	
			Properties props = new Properties();
			props.put("mail.smtp.host", host);
	
			props.put("mail.smtp.port", PORT);
			props.put("mail.smtp.user", USER);
			props.put("mail.smtp.auth", AUTH);
			props.put("mail.smtp.starttls.enable", STARTTLS);
			props.put("mail.smtp.debug", DEBUG);
			props.put("mail.smtp.ssl.enable", SSL);
//			props.put("mail.smtp.socketFactory.port", PORT);
//			props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//			props.put("mail.smtp.socketFactory.fallback", "false");
	
			
	
			try {
				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(true);
				
				MimeMessage message = new MimeMessage(session);
				message.setSubject(email_subject);
				message.setFrom(new InternetAddress(mailId));
				message.addRecipients(RecipientType.TO, mailTo);
				
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(content);
				messageBodyPart.setContent(content, "text/html");
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				if (attachment != null && attachment.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					attachPart.attachFile(attachment);
					attachPart.setFileName(attachment.getName());
					multipart.addBodyPart(attachPart);

				}
				message.setContent(multipart);
	
				message.saveChanges();
	
				Transport transport = session.getTransport("smtp");
				transport.connect(host, mailId, mailPasswd);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}

	
	public synchronized void sendAlertMailFromScheduler(Address[] mailTo, String content,String email_subject,String fromHost,String fromUser,String fromUserPwd) throws Exception {

		Properties props = new Properties();
		props.put("mail.smtp.host", fromHost);

		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.user", USER);
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.starttls.enable", STARTTLS);
		props.put("mail.smtp.debug", DEBUG);
		props.put("mail.smtp.ssl.enable", SSL);
//		props.put("mail.smtp.socketFactory.port", PORT);
//		props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//		props.put("mail.smtp.socketFactory.fallback", "false");

		try {
			InternetAddress emailAddr = new InternetAddress(fromUser);
			emailAddr.validate();
		} catch (Exception ex) {
		}

		try {
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(true);
			MimeMessage message = new MimeMessage(session);
			message.setText(content);
			message.setSubject(email_subject);
			message.setFrom(new InternetAddress(fromUser));
			message.addRecipients(RecipientType.TO, mailTo);
			message.setContent(content,"text/html");
			message.saveChanges();

			Transport transport = session.getTransport("smtp");
			transport.connect(fromHost, fromUser, fromUserPwd);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	public synchronized void sendMailFromUserEmailWithAttachList(Address[] mailTo,String Content,String email_subject,ArrayList attachmentList,long userId, String from) throws Exception {
    	
	      EmailConfigurationModel configModel=new EmailConfigDao().getEmailConfiguration(userId);
	      String TEXT="";
	      
	      try {
				if(from!=null && from.length()>0) {
					new InternetAddress(from).validate();
					FROM=from;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
	      
	      TEXT =Content+" \n";
	      
	      Properties props = new Properties();
	      props.put("mail.smtp.host", configModel.getHost_name());
	      
	      props.put("mail.smtp.port", PORT);
	      props.put("mail.smtp.user", configModel.getUsername());
	      props.put("mail.smtp.auth", AUTH);
	      props.put("mail.smtp.starttls.enable", STARTTLS);
	      props.put("mail.smtp.debug", DEBUG);
	      props.put("mail.smtp.ssl.enable", SSL);
//	      props.put("mail.smtp.socketFactory.port", PORT);
//	      props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
//	      props.put("mail.smtp.socketFactory.fallback", "false");
	    
	    
	      
	      try {

				Session session = Session.getDefaultInstance(props, null);
				session.setDebug(true);
				
				MimeMessage message = new MimeMessage(session);
				message.setSubject(email_subject);
				message.setFrom(new InternetAddress( configModel.getUsername()));
				message.addRecipients(RecipientType.TO, mailTo);
				
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(TEXT);
				messageBodyPart.setContent(TEXT, "text/html");
				
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				File attachment ;
				Iterator iter=attachmentList.iterator();
				while (iter.hasNext()) {
					attachment= (File) iter.next();
					if (attachment != null && attachment.exists()) {
						MimeBodyPart attachPart = new MimeBodyPart();
						attachPart.attachFile(attachment);
						attachPart.setFileName(attachment.getName());
						multipart.addBodyPart(attachPart);
					}
				}
				
				
				message.setContent(multipart);
				message.saveChanges();

				Transport transport = session.getTransport("smtp");
				transport.connect(configModel.getHost_name(),  configModel.getUsername(),  configModel.getPassword());
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
				
	      } catch (Exception e) {
	      	System.out.println(e);
	          throw new Exception("Exception in Mail.sendMail" + e.getMessage());
	      }
	  }
	
	
}
