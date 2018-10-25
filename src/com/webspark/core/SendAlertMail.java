package com.webspark.core;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.filefilter.AgeFileFilter;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.dao.SettingsDao;
import com.inventory.config.settings.model.SettingsModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.server.VaadinServlet;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SMail;
import com.webspark.dao.SchedulerDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 14, 2015
 */
public final class SendAlertMail implements Runnable,Serializable{
	
	private static final long serialVersionUID = 2506784785638918400L;
	SMail mail;
	public SendAlertMail(SMail smail) {
		this.mail=smail;
	}
	SchedulerDao dao=new SchedulerDao();
	SettingsDao setDao=new SettingsDao();
	CustomerDao custDao=new CustomerDao();
	
    public void run() {
    	
    	try {
    		
    		List orgList=dao.getAllAlertEnabledOrganizations();
    		Iterator orgIter=orgList.iterator();
    		SettingsModel orgSett;
    		S_OfficeModel ofcSett;
    		SettingsValuePojo settings;
    		CustomerModel cust;
    		InternetAddress emailAddr;
    		boolean send=false;
			
    		while (orgIter.hasNext()) {
    			orgSett= (SettingsModel) orgIter.next();
    			List officeList=dao.getAllAlertEnabledOfficeSettings(orgSett.getLevel_id());
    			Iterator ofcIter=officeList.iterator();
    			
    			while (ofcIter.hasNext()) {
    				ofcSett=(S_OfficeModel) ofcIter.next();
	    			settings=setDao.getAllSettings(orgSett.getLevel_id(), ofcSett.getId());
	    			StringBuffer content = new StringBuffer();
	    			if(settings.isALERT_EMAIL()&&settings.getALERT_EMAILIDS()!=null){
	    			List list = custDao.getCustomersCreditDetails(ofcSett.getId());
					if(list!=null&&list.size()>0){
	    			

					content.append("<table>");
					content.append("<tr></tr>");
					content.append("<tr><td><b><u> Customers Under Credit Limit "
							+ "</b></td></tr>");
					content.append("<tr><td></td></tr>");
					content.append("<tr><td></td></tr>");
					content.append("</table>");
					content.append("<table border=1 cellspacing=0 style=width:500px>");

					content.append("<tr bgcolor=lightgrey><td><b>Customer</td><td><b>Credit Limit</td><td><b>Current Balance</td><td><b>Phone</td></tr>");

					Iterator iter = list.iterator();

					while (iter.hasNext()) {
						cust = (CustomerModel) iter.next();
						content.append("<tr><td>"
								+ cust.getName()
								+ "</td><td>" + cust.getCredit_limit()
								+ "</td><td>" + Math.round(cust.getLedger().getCurrent_balance())
								+ "</td><td>" + cust.getAddress().getPhone()
								+ "</td></tr>");
					}

					content.append("</table>");
					
					}
					
					
					list = custDao
							.getAllCustomersNamesList(ofcSett.getId());
					
					Iterator it = list.iterator();

					CustomerModel obj;
					Calendar cal;
					List list2;
					double payed = 0, total = 0;
					Iterator it2;
					SalesModel salObj;
					String items="";
					while (it.hasNext()) {

						obj = (CustomerModel) it.next();

						cal = Calendar.getInstance();
						cal.setTime(new java.util.Date());
						cal.set(cal.DAY_OF_YEAR,1);
						cal.set(cal.MONTH,0);
						Date yearStrt=cal.getTime();

						cal.add(Calendar.DAY_OF_MONTH,
								-obj.getMax_credit_period());

						list2 = custDao.getAllSalesDetailsForCustomer(obj
										.getLedger().getId(),
										CommonUtil.getSQLDateFromUtilDate(yearStrt), CommonUtil.getSQLDateFromUtilDate(new java.util.Date()));

						if (list2.size() > 0) {

							payed = 0;
							total = 0;
							it2 = list2.iterator();
							while (it2.hasNext()) {
								salObj = (SalesModel) it2.next();

								payed += salObj.getPayment_amount();
								total += salObj.getAmount();

							}
							
							items+="<tr><td>" + obj.getName()
									+ "</td><td>" + Math.round(total)
									+ "</td><td>" + Math.round(total - payed)
									+ "</td></tr>";
							
						}

					}
					if(items.trim().length()>0){
					content.append("<table>");
					content.append("<tr><td><b><u> Customers Exceedes Credit Period "
							+ "</b></td></tr>");
					content.append("<tr><td></td></tr>");
					content.append("<tr><td></td></tr>");
					content.append("</table>");
					content.append("<table border=1 cellspacing=0 style=width:500px>");

					content.append("<tr bgcolor=lightgrey><td><b>Customer</td><td><b>Total Amount</td><td><b>Balance</td></tr>");
					
					content.append(items);
					content.append("</table>");
					
					}
					
					Address[] addr;
					if(settings.getALERT_EMAILIDS().contains(",")){
						String[] addrStr = settings.getALERT_EMAILIDS().split(",");
						addr=new Address[addrStr.length];
//						System.arraycopy(addrStr, 0, addr, 0, addrStr.length);
						
						for(int i=0;i<addrStr.length;i++){
							emailAddr = new InternetAddress(addrStr[i]);
							emailAddr.validate();
							addr[i]=emailAddr;
						}
						
					}else{
						addr=new Address[1];
						if(settings.getALERT_EMAILIDS().length()>0){
							emailAddr = new InternetAddress(settings.getALERT_EMAILIDS());
							emailAddr.validate();
							addr[0]=emailAddr;
						}
					}
					

					if(content.length()>0&&addr!=null&&addr.length>0){
						try{
							String mailContent="<table><tr><td>Organization :</td><td><b>"+ofcSett.getOrganization().getName()+"</b></td></tr>" +
									"<tr><td>Office :</td><td><b>"+ofcSett.getName()+"</b></td></tr>" +
									"<tr><td>Date :</td><td><b>"+CommonUtil.formatDateToDDMMMYYYY(new Date())+"</b></td></tr>" +
									"</table>";
							mailContent+=content.toString();
							
							mailContent+="<table><tr><td><i>This is an autogenerated email from REVERP. Do not reply.</i></td></tr></table>";
							
							
						mail.sendAlertMailFromScheduler(addr, mailContent,
							"Alert From Reverp",settings.getAPPLICATION_EMAIL_HOST(),
							settings.getAPPLICATION_EMAIL(),settings.getAPPLICATION_EMAIL_PASSWORD());
						}catch(Exception e){
						}
					}
	    			}
				}
			}
    		
    		System.gc();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		try {
			deleteOldFiles(new File(VaadinServlet.getCurrent().getServletContext()
					.getRealPath("Reports/")), 2);
		} catch (Exception e) {
		}
    }
	
	public void deleteOldFiles(File file,int olderThanDate) {
		
		Calendar cal=Calendar.getInstance();
		cal.add(cal.DAY_OF_MONTH, -olderThanDate);
		
	    Iterator<File> filesToDelete = org.apache.commons.io.FileUtils.iterateFiles(file, new AgeFileFilter(cal.getTime()), null);
	    
	    while (filesToDelete.hasNext()) {
			File deleteFile = (File) filesToDelete.next();
			deleteFile.delete();
	    }
	}
	
  }
  
