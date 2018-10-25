package com.webspark.mailclient.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import com.webspark.mailclient.bean.EmailDetailsBean;
import com.webspark.mailclient.dao.EmailConfigDao;
import com.webspark.mailclient.dao.MailDao;
import com.webspark.mailclient.model.EmailConfigurationModel;

public class GetMail {
	
	
	String host = "gmail-smtp-msa.l.google.com";
	String username = "sparkmailer2@gmail.com";
	String password = "spark2123";
	Properties props = new Properties();
	Session session = Session.getDefaultInstance(props, null);
	static int max_no_of_emails=10;
	
	MailDao daoObj=new MailDao();

	public GetMail(long user_id) {
		EmailConfigurationModel mdl=null;
		try {
			mdl = new EmailConfigDao().getEmailConfiguration(user_id);
			
			props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true");
//	        props.put("mail.smtp.host", "smtp.gmail.com");
//	        props.put("mail.smtp.port", "587");
			
			if(mdl!=null) {
				host=mdl.getHost_name();
				username=mdl.getUsername();
				password=mdl.getPassword();
				max_no_of_emails=mdl.getMax_no_emails();
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



	/*public static void main(String args[]) throws Exception {
		
		// Get server and login information
		String host = "gmail-smtp-msa.l.google.com";
		String username = "richin.sparknova@gmail.com";
		String password = "spark#nova*";

		Properties props = new Properties();

		Session session = Session.getDefaultInstance(props, null);

		// Make connection

		Store store = session.getStore("imaps");

		store.connect(host, username, password);

		// Get folder and messages

		Folder folder = store.getFolder("INBOX");

		folder.open(Folder.READ_ONLY);

		Message message[] = folder.getMessages();
		
		// Check mail

		for (int i = message.length - 1; i >= 0; i--) {
			
			System.out.println(i + ": " + message[i].getFrom()[0]+ "\t" + message[i].getSubject()+"\n");
			System.out.println("Details : "+message[i].getDescription()+"\n");
//			Message msg=message[i];
			
			System.out.println(getMessageContent(message[i]));
			
			MimeMultipart mail=(MimeMultipart) message[i].getContent();
			System.out.println(message[i].getDescription());
			System.out.println(mail.getBodyPart(0));
			
		}
		
		// Close things out

		folder.close(false);
		
		store.close();

	}*/
	
	/*public List getInboxMails() throws Exception {

		List lst = new ArrayList();

		// Get server and login information

		// Make connection
		List<MessageBean> listMessages = new ArrayList<MessageBean> ();
		

		
		
		Pop3Server popServer = new Pop3Server(host,
	            new SimpleAuthenticator(username, password));
		
		ImapServer imapServer = new ImapSslServer(host, username, password);
	    ReceiveMailSession session = imapServer.createSession();
//	    ReceiveMailSession session = popServer.createSession();
	    session.open();
	    System.out.println(session.getMessageCount());
	    ReceivedEmail[] emails = session.receiveEmail();
	    if (emails != null) {
	        for (ReceivedEmail email : emails) {
	            System.out.println("\n\n===[" + email.getMessageNumber() + "]===");

	            // common info
	            System.out.println("FROM:" + email.getFrom());
	            System.out.println("TO:" + email.getTo()[0]);
	            System.out.println("SUBJECT:" + email.getSubject());
	            System.out.println("PRIORITY:" + email.getPriority());
	            System.out.println("SENT DATE:" + email.getSentDate());
	            System.out.println("RECEIVED DATE: " + email.getReceiveDate());

	            // process messages
	            List<EmailMessage> mesgs = email.getAllMessages();
	            for (EmailMessage msg : mesgs) {
	                System.out.println("------");
	                System.out.println(msg.getEncoding());
	                System.out.println(msg.getMimeType());
	                System.out.println(msg.getContent());
	            }

	            // process attachments
	            List<EmailAttachment> attachments = email.getAttachments();
	            if (attachments != null) {
	                System.out.println("+++++");
	                for (EmailAttachment attachment : attachments) {
	                    System.out.println("name: " + attachment.getName());
	                    System.out.println("cid: " + attachment.getContentId());
	                    System.out.println("size: " + attachment.getSize());
	                    attachment.writeToFile(new File("d:\\", attachment.getName()));
	                }
	            }
	        }
	    }
	    session.close();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Store store = session.getStore("imaps");

		store.connect(host, username, password);

		// Get folder and messages

		Folder inbox = store.getFolder("INBOX");

		inbox.open(Folder.READ_ONLY);

		Message messages[] = inbox.getMessages();

		// Store store = null;
		// store = session.getStore("imaps");
		// store.connect();

		// Folder inbox = store.getFolder("Inbox");
		// inbox.open(Folder.READ_WRITE);
//		Flags seen = new Flags(Flags.Flag.SEEN);
//		FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		// SearchTerm st = new AndTerm(new
		// SubjectTerm(subjectSubstringToSearch), unseenFlagTerm);

		// Get some message references

		// Message [] messages = inbox.search(st);

		// System.out.println(messages.length + " -- Messages amount");

		// Message[] messages = inbox.search(new FlagTerm(new
		// Flags(Flags.Flag.SEEN), false));
		ArrayList<String> attachments = new ArrayList<String>();

		List<MessageBean> listMessages = getPart(messages, attachments);
		for (String s : attachments) {
			System.out.println(s);
		}
		inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		for (int i = 0, j = messages.length; i < j; i++) {
			messages[i].setFlag(Flags.Flag.SEEN, true);
		}
		inbox.close(true);
		store.close();
		return listMessages;
	}

	private static List<MessageBean> getPart(Message[] messages,
			ArrayList<String> attachments) throws MessagingException,
			IOException {
		List<MessageBean> listMessages = new ArrayList<MessageBean>();
		
		try {
			
			
			
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = messages.length; i >= messages.length-max_no_of_emails; i--) {
	
				
				try {
					
				
					Message inMessage=messages[i];
					
					if(inMessage!=null) {
						System.out.println(inMessage.getSubject());
						System.out.println(getMessageContent(inMessage));
					}
					
					attachments.clear();
					if (inMessage.isMimeType("text/plain")) {
						MessageBean message = new MessageBean(
								inMessage.getMessageNumber(),
								MimeUtility.decodeText(inMessage.getSubject()),
								inMessage.getFrom()[0].toString(), null,
								inMessage.getSentDate(),
								(String) inMessage.getContent(), false, null);
						listMessages.add(message);
						System.out.println("text/plain");
					} else if (inMessage.isMimeType("multipart/*")) {
						System.out.println("multipart");
						Multipart mp = (Multipart) inMessage.getContent();
						MessageBean message = null;
						System.out.println(mp.getCount());
						for (int j = 0; j < mp.getCount(); j++) {
							Part part = mp.getBodyPart(j);
							if ((part.getFileName() == null || part.getFileName() == "")
									&& part.isMimeType("text/plain")) {
								System.out.println(inMessage.getSentDate());
								message = new MessageBean(inMessage.getMessageNumber(),
										inMessage.getSubject(),
										inMessage.getFrom()[0].toString(), null,
										inMessage.getSentDate(),
										(String) part.getContent(), false, null);
							} else if (part.getFileName() != null
									|| part.getFileName() != "") {
								if ((part.getDisposition() != null)
										&& (part.getDisposition()
												.equals(Part.ATTACHMENT))) {
									System.out.println(part.getFileName());
									attachments.add(saveFile(
											MimeUtility.decodeText(part.getFileName()),
											part.getInputStream()));
									if (message != null) {
										message.setAttachments(attachments);
									}
								}
							}
						}
						listMessages.add(message);
					}
				} catch (Exception e) {
					System.out.println("Error");
					// TODO: handle exception
				}
			}
			
		} catch (Exception e) {
			System.out.println("Error");
			// TODO: handle exception
		}
			
		return listMessages;
	}

	// method for saving attachment on local disk
	private static String saveFile(String filename, InputStream input) {
		String strDirectory = "";
		try {
			// Create one directory
			boolean success = (new File(strDirectory)).mkdir();
			if (success) {
				System.out.println("Directory: " + strDirectory + " created");
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		String path =  filename;
		try {
			byte[] attachment = new byte[input.available()];
			input.read(attachment);
			File file = new File(path);
			FileOutputStream out = new FileOutputStream(file);
			out.write(attachment);
			input.close();
			out.close();
			return path;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}*/
	
	
	public static void parsemessage(Message message) throws MessagingException, IOException {
	      System.out.println( "<"+message.getFrom()[0] + "> " + message.getSubject());
	      Multipart multipart = (Multipart)message.getContent();
	      System.out.println("     > Message has "+multipart.getCount()+" multipart elements");
	        for (int j = 0; j < multipart.getCount(); j++) {
	            BodyPart bodyPart = multipart.getBodyPart(j);
	            if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
	                if (bodyPart.getContent().getClass().equals(MimeMultipart.class)) {
	                    MimeMultipart mimemultipart = (MimeMultipart)bodyPart.getContent();
	                    System.out.println("Number of embedded multiparts "+mimemultipart.getCount());
	                    for (int k=0;k<mimemultipart.getCount();k++) {
	                        if (mimemultipart.getBodyPart(k).getFileName() != null) {
	                            System.out.println("     > Creating file with name : "+mimemultipart.getBodyPart(k).getFileName());
	                            savefile(mimemultipart.getBodyPart(k).getFileName(), mimemultipart.getBodyPart(k).getInputStream());
	                        }
	                    }
	                }
	              continue;
	            }
	            System.out.println("     > Creating file with name : "+bodyPart.getFileName());
	            savefile(bodyPart.getFileName(), bodyPart.getInputStream());
	        }
	    }
	
	public static void savefile(String FileName, InputStream is) throws IOException {
        File f = new File("files/" + FileName);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[4096];
        int bytesRead;
        while((bytesRead = is.read(buf))!=-1) {
            fos.write(buf, 0, bytesRead);
        }
        fos.close();
    }
	
	
	
	public List getInBoxMails(long user_id, long last_no) throws Exception {
		
		List lst=new ArrayList();
		
		// Get server and login information
		

		// Make connection

		Store store = session.getStore("imaps");

		store.connect(host, username, password);

		// Get folder and messages

		Folder folder = store.getFolder("INBOX");

		folder.open(Folder.READ_ONLY);

		Message message[] = folder.getMessages();
		
		// Check mail
		
		boolean avail=false;
		int lmt=message.length-max_no_of_emails;
		if(last_no!=0)
			lmt=message.length-200;
		
		for (int i = message.length-1; i >= lmt; i--) {
			
			try {
				
				if(message[i].getMessageNumber()==last_no)
					break;
				
				avail=false;
				avail=daoObj.isMailExist(user_id, message[i].getMessageNumber(), 1, 1);
				
				if(!avail) {
					String send="";
					if(message[i].getFrom()!=null) {
						for (int j = 0; j < message[i].getFrom().length; j++) {
							send+=message[i].getFrom()[j]+", ";
						}
					}
					lst.add(new EmailDetailsBean(message[i].getMessageNumber(), send, message[i].getSubject(), getMessageContent(message[i]), message[i].getSentDate()));
				}
				else {
					break;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		// Close things out

		folder.close(false);

		store.close();
		
		return lst;

	}
	
	
	public List getOutBoxMails(long user_id, long last_no) throws Exception {
		
		List lst=new ArrayList();
		
		// Get server and login information
		

		// Make connection

		Store store = session.getStore("imaps");

		store.connect(host, username, password);

		// Get folder and messages
		String folderName="[Gmail]/Sent Mail";
		if(host.equals("imap.next.mail.yahoo.com"))
			folderName="Sent";

		Folder folder = store.getFolder(folderName);

		folder.open(Folder.READ_ONLY);

		Message message[] = folder.getMessages();
		
		// Check mail
		
		boolean avail=false;
		
		int lmt=message.length-max_no_of_emails;
		if(last_no!=0)
			lmt=message.length-200;

		for (int i = message.length-1; i >= lmt; i--) {
			
			try {
				System.out.println(message[i]);
				System.out.println(message[i].getMessageNumber());
				if(message[i].getMessageNumber()==last_no)
					break;
				
				avail=false;
				avail=daoObj.isMailExist(user_id, message[i].getMessageNumber(), 1, 2);
				
				if(!avail) {
					String send="";
					if(message[i].getAllRecipients()!=null) {
						for (int j = 0; j < message[i].getAllRecipients().length; j++) {
							send+=message[i].getAllRecipients()[j]+", ";
						}
					}
					lst.add(new EmailDetailsBean(message[i].getMessageNumber(), send, message[i].getSubject(), getMessageContent(message[i]), message[i].getSentDate()));
				}
				else {
					break;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		// Close things out

		folder.close(false);

		store.close();
		
		return lst;

	}
	
	
	public List getDraftMails(long user_id, long last_no) throws Exception {
		
		List lst=new ArrayList();
		
		// Get server and login information
		

		// Make connection
		Store store = session.getStore("imaps");
		;
		store.connect(host, username, password);
		
		// Get folder and messages
		String folderName="[Gmail]/Drafts";
		if(host.equals("imap.next.mail.yahoo.com"))
			folderName="Drafts";

		Folder folder = store.getFolder(folderName);

		folder.open(Folder.READ_ONLY);

		Message message[] = folder.getMessages();
		
		// Check mail
		
		boolean avail=false;
		
		int lmt=message.length-max_no_of_emails;
		if(last_no!=0)
			lmt=message.length-200;

		for (int i = message.length-1; i >= lmt; i--) {
			
			try {
				
				if(message[i].getMessageNumber()==last_no)
					break;
				
				avail=false;
				avail=daoObj.isMailExist(user_id, message[i].getMessageNumber(), 1, 3);
				
				if(!avail) {
					String send="";
					if(message[i].getAllRecipients()!=null) {
						for (int j = 0; j < message[i].getAllRecipients().length; j++) {
							send+=message[i].getAllRecipients()[j]+", ";
						}
					}
					lst.add(new EmailDetailsBean(message[i].getMessageNumber(), send, message[i].getSubject(), getMessageContent(message[i]), message[i].getSentDate()));
				}
				else {
					break;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		// Close things out

		folder.close(false);

		store.close();
		
		return lst;

	}
	
	
	
	
	public static String getMessageContent(Message message)
		    throws Exception {
		        Object content = message.getContent();
		        if (content instanceof Multipart) {
		            StringBuffer messageContent = new StringBuffer();
		            Multipart multipart = (Multipart) content;
		            for (int i = 0; i < multipart.getCount(); i++) {
		                Part part = (Part) multipart.getBodyPart(i);
		                if (part.isMimeType("text/plain")) {
		                    messageContent.append(part.getContent().toString());
		                }
		            }
		            return messageContent.toString();
		        } else {
		            return content.toString();
		        }
		    }
		    
	
	

}
