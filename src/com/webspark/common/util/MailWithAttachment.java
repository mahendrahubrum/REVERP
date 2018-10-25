package com.webspark.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;

/**
 * Document   : Mail
 * Created on : May 29, 2013, 5:59:19 PM
 * @Author Jinshad P.T.
 */

public class MailWithAttachment {

//    private static String HOST = "smtp.gmail.com";
    private static String HOST = "gmail-smtp-msa.l.google.com";
    
    
    
    private static String USER = "spafmangement@gmail.com";   //prep4e@gmail.com
    private static String PASSWORD = "paf123456";	//cortest01
    private static String PORT = "465";
    private static String FROM = "";
    private static String STARTTLS = "true";
    private static String AUTH = "true";
    private static String DEBUG = "true";
    private static String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static String SUBJECT = "KAP India BOQ exceeding  alert";
//    private static DateOperations dateUtil = new DateOperations();
//    HttpServletRequest request = ServletActionContext.getRequest();
//    HttpSession session = request.getSession();
    WrappedSession session= new SessionUtil().getHttpSession();

    public synchronized void sendMail(Address[] mailTo,String Content,String type,String email_subject) throws Exception {
//        String TO = mailTo;
        String TEXT="";
        
        TEXT =Content+" \n";
        
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
         
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.user", USER);
        props.put("mail.smtp.auth", AUTH);
        props.put("mail.smtp.starttls.enable", STARTTLS);
        props.put("mail.smtp.debug", DEBUG);
        props.put("mail.smtp.socketFactory.port", PORT);
        props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");
      
      
        
        
        
        
        
        try {
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            /*message.setText(TEXT);
            message.setSubject(email_subject);
            message.setFrom(new InternetAddress(FROM));
//            for(int i=0;i<mailTo.length;i++){
            message.addRecipients(RecipientType.TO, mailTo);
//            }
            message.saveChanges();*/
            
            
  	      // Recipient's email ID needs to be mentioned.
  	      String to = "ani@sparknovaplc.com";

  	      // Sender's email ID needs to be mentioned
  	      String from = "jinshadkoottil@gmail.com";
            
            message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));
            
            
            BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText("Attachment Mail Success...!!!");
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         File file=new File("attachment.txt");
	         try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	         file.
	         messageBodyPart = new MimeBodyPart();
	         String filename = "attachment.txt";
	         DataSource source = new FileDataSource(filename);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart );
            
            
            
            
            
            
            
            
            Transport transport = session.getTransport("smtp");
            transport.connect(HOST, USER, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
        	System.out.println(e);
            throw new Exception("Exception in Mail.sendMail" + e.getMessage());
        }
    }
    
    
    
    
    public synchronized void sendAuthorMail(Address[] mailTo,String Content,String type,String email_subject,String fromAddress) throws Exception {
//      String TO = mailTo;
      String TEXT="";
      TEXT =Content+" \n";
      
      Properties props = new Properties();
      props.put("mail.smtp.host", HOST);
       
      props.put("mail.smtp.port", PORT);
      props.put("mail.smtp.user", USER);
      props.put("mail.smtp.auth", AUTH);
      props.put("mail.smtp.starttls.enable", STARTTLS);
      props.put("mail.smtp.debug", DEBUG);
      props.put("mail.smtp.socketFactory.port", PORT);
      props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
      props.put("mail.smtp.socketFactory.fallback", "false");
    
    
      
      
      
      
      
      try {
          Session session = Session.getDefaultInstance(props, null);
          session.setDebug(true);
          MimeMessage message = new MimeMessage(session);
          message.setText(TEXT);
          message.setSubject(email_subject);
          message.setFrom(new InternetAddress(fromAddress));
//          for(int i=0;i<mailTo.length;i++){
          message.addRecipients(RecipientType.TO, mailTo);
//          }
          message.saveChanges();
          Transport transport = session.getTransport("smtp");
          transport.connect(HOST, USER, PASSWORD);
          transport.sendMessage(message, message.getAllRecipients());
          transport.close();
      } catch (Exception e) {
      	System.out.println(e);
          throw new Exception("Exception in Mail.sendMail" + e.getMessage());
      }
  }
    
    
    public synchronized void sendStudentMail(Address[] mailTo,String Content,String email_subject,String fromAddress) throws Exception {
//      String TO = mailTo;
      String TEXT="";
      TEXT = Content+" \n";
      
      Properties props = new Properties();
      props.put("mail.smtp.host", HOST);
       
      props.put("mail.smtp.port", PORT);
      props.put("mail.smtp.user", USER);
      props.put("mail.smtp.auth", AUTH);
      props.put("mail.smtp.starttls.enable", STARTTLS);
      props.put("mail.smtp.debug", DEBUG);
      props.put("mail.smtp.socketFactory.port", PORT);
      props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
      props.put("mail.smtp.socketFactory.fallback", "false");
    
    
      
      
      
      
      
      try {
          Session session = Session.getDefaultInstance(props, null);
          session.setDebug(true);
          MimeMessage message = new MimeMessage(session);
          message.setText(TEXT);
          message.setSubject(email_subject);
          message.setFrom(new InternetAddress(fromAddress));
//          for(int i=0;i<mailTo.length;i++){
          message.addRecipients(RecipientType.TO, mailTo);
//          }
          message.saveChanges();
          Transport transport = session.getTransport("smtp");
          transport.connect(HOST, USER, PASSWORD);
          transport.sendMessage(message, message.getAllRecipients());
          transport.close();
      } catch (Exception e) {
      	System.out.println(e);
          throw new Exception("Exception in Mail.sendMail" + e.getMessage());
      }
  }
    
    
    
}







	/*import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

	public class MailWithAttachment
	{
	   public void sendMail()
	   {
	      
	      // Recipient's email ID needs to be mentioned.
	      String to = "jinshadkoottil@gmail.com";

	      // Sender's email ID needs to be mentioned
	      String from = "web@gmail.com";

	      // Assuming you are sending email from localhost
	      String host = "localhost";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("This is the Subject Line!");

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText("This is message body");
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         File file=new File("asd.txt");
	         try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	         file.
	         messageBodyPart = new MimeBodyPart();
	         String filename = "asd.txt";
	         DataSource source = new FileDataSource(filename);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart );

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	   }
	}*/