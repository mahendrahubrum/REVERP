package com.webspark.Components;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.gcm.service.GCMPushing;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SMail;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.IDGeneratorDao;
import com.webspark.model.ActivityLogModel;
import com.webspark.model.CurrencyModel;
import com.webspark.model.ReportIssueModel;
import com.webspark.model.ReviewModel;
import com.webspark.model.SessionActivityModel;
import com.webspark.uac.dao.InvoiceFormatMappingDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.InvoiceFormatModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.ui.InvoiceFormatMappingUI;
import com.webspark.ui.NewMainLayout;

public abstract class SparkLogic extends SWindow {

	private SPanel mainPanel = null;
	WrappedSession session = null;
	long id = 0;
	String value = "";
	ResourceBundle bundle;
	CommonMethodsDao comDao=new CommonMethodsDao();

	public SparkLogic() {

		if (getHttpSession().getAttribute("property_file") != null)
			bundle = ResourceBundle.getBundle(getHttpSession().getAttribute("property_file").toString());

		mainPanel = getGUI();

		if (mainPanel == null) {

			System.out.println("Panel is NULL");

		} else {
			mainPanel.focus();
			System.out.println("Got IT !");
		}

		setContent(mainPanel);

	}

	public abstract SPanel getGUI();

	public abstract Boolean isValid();

	public abstract Boolean getHelp();

	public void init(VaadinRequest request) {
		// TODO Auto-generated method stub

	}

	public long getNextSequence(String type, long loginId) throws Exception {
		long newId = 0;
		try {
			newId = new IDGeneratorDao().generateID(type, loginId,
					getOfficeID(), getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return newId;
	}
	
	public String getNextSequence(String type, long loginId, long office, Date date) throws Exception {
		String newId = "";
		long id=0;
		Calendar calendar=Calendar.getInstance();
		try {
			id = new IDGeneratorDao().generateID(type, loginId, office, getOrganizationID());
			InvoiceFormatModel mdl=new InvoiceFormatMappingDao().getInvoiceFormatModel(office, type);
			calendar.setTime(date);
			if(mdl!=null){
				newId=new InvoiceFormatMappingUI().getStringFormat(mdl.getInvocieFormat().trim(), date).trim();
				if(newId.contains("$No$")){
					newId=newId.replaceAll("\\$No\\$", id+"");
				}
			}
			else{
				newId=id+"";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return newId;
	}

	public WrappedSession getHttpSession() {
		if (session == null)
			session = new SessionUtil().getHttpSession();
		return session;
	}

	public void setSize(int width, int height) {
		setWidth(width + "px");
		setHeight(height + "px");
	}

	public void setRelatedOptions(Vector options) {

	}

	public void setRequiredError(AbstractComponent component,
			String fieldNameToDisplay, boolean enable) {
		if (enable) {
			component.setComponentError(new SUserError(
					"<i style='font-size: 13px;'>" + fieldNameToDisplay,
					ContentMode.HTML, ErrorLevel.CRITICAL));
		} else
			component.setComponentError(null);
	}

	// Geting login_id from Session
	public long getLoginID() {
		if (getHttpSession().getAttribute("login_id") != null) {
			id = (Long) getHttpSession().getAttribute("login_id");
		}
		return id;
	}
	
	// Geting language_id from Session
	public long getLanguageID() {
		id=0;
		if (getHttpSession().getAttribute("language_id") != null) {
			id = (Long) getHttpSession().getAttribute("language_id");
		}
		return id;
	}

	// Geting user_id from Session
	public long getUserID() {
		if (getHttpSession().getAttribute("user_id") != null) {
			id = (Long) getHttpSession().getAttribute("user_id");
		}
		return id;
	}

	/* Geting role_id from Session */
	public long getRoleID() {
		if (getHttpSession().getAttribute("role_id") != null) {
			id = (Long) getHttpSession().getAttribute("role_id");
		}
		return id;
	}

	// Geting login_name from Session
	public String getLoginName() {
		if (getHttpSession().getAttribute("login_name") != null) {
			value = (String) getHttpSession().getAttribute("login_name");
		}
		return value;
	}

	// Geting office_id from Session
	public long getOfficeID() {
		if (getHttpSession().getAttribute("office_id") != null) {
			id = (Long) getHttpSession().getAttribute("office_id");
		}
		return id;
	}

	public String getOfficeName() {
		String name = "";
		if (getHttpSession().getAttribute("office_name") != null) {
			name = getHttpSession().getAttribute("office_name").toString();
		}
		return name;
	}

	// Geting country ID from Session
	public long getCountryID() {
		if (getHttpSession().getAttribute("country_id") != null) {
			id = (Long) getHttpSession().getAttribute("country_id");
		}
		return id;
	}

	// Geting currency_id from Session
	public long getCurrencyID() {
		if (getHttpSession().getAttribute("currency_id") != null) {
			id = (Long) getHttpSession().getAttribute("currency_id");
		}
		return id;
	}

	// Geting organization_id from Session
	public long getOrganizationID() {
		if (getHttpSession().getAttribute("organization_id") != null) {
			id = (Long) getHttpSession().getAttribute("organization_id");
		}
		return id;
	}

	// Geting working_date from Session
	public Date getWorkingDate() {
		Date date = null;
		if (getHttpSession().getAttribute("working_date") != null) {
			date = (Date) getHttpSession().getAttribute("working_date");
		}
		return date;
	}

	// Getting Option ID from Session
	public long getOptionId() {
		id=0;
		if (getHttpSession().getAttribute("option_id") != null) {
			id = (Long) getHttpSession().getAttribute("option_id");
		}
		return id;
	}

	public Date getFinStartDate() {
		Date date = null;
		if (getHttpSession().getAttribute("fin_start") != null) {
			date = (Date) getHttpSession().getAttribute("fin_start");
		}
		return date;
	}

	public Date getFinEndDate() {
		Date date = null;
		if (getHttpSession().getAttribute("fin_end") != null) {
			date = (Date) getHttpSession().getAttribute("fin_end");
		}
		return date;
	}

	public boolean isFinYearBackEntry() {
		boolean allow = false;
		if (getHttpSession().getAttribute("fin_yr_back_entry") != null) {
			allow = (Boolean) getHttpSession()
					.getAttribute("fin_yr_back_entry");
		}
		return allow;
	}

	// Geting settings from Session
	public SettingsValuePojo getSettings() {
		SettingsValuePojo settings = null;
		if (getHttpSession().getAttribute("settings") != null) {
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");
		}
		return settings;
	}

	public boolean isCessEnableOnItem(long item_id) {
		boolean enable = false;
		if (getHttpSession().getAttribute("cess_enabled") != null) {
			if ((Boolean) getHttpSession().getAttribute("cess_enabled") == true) {
				try {
					enable = new ItemDao().isCessEnabled(item_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return enable;
	}

	public boolean isCessEnable() {
		boolean enable = false;
		if (getHttpSession().getAttribute("cess_enabled") != null) {
			enable = (Boolean) getHttpSession().getAttribute("cess_enabled");

		}
		return enable;
	}

	public boolean isTaxEnable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("tax_enabled") != null) {
			enable = (Boolean) getHttpSession().getAttribute("tax_enabled");

		}
		return enable;
	}

	public boolean isManufDateEnable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("manuf_date_enable") != null) {
			enable = (Boolean) getHttpSession().getAttribute(
					"manuf_date_enable");
		}
		return enable;
	}

	public boolean isDiscountEnable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("discount_enable") != null) {
			enable = (Boolean) getHttpSession().getAttribute("discount_enable");

		}
		return enable;
	}

	public boolean isExciceDutyEnable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("excise_duty_enable") != null) {
			enable = (Boolean) getHttpSession().getAttribute(
					"excise_duty_enable");

		}
		return enable;
	}

	public boolean isShippingChargeEnable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("shipping_charge_enable") != null) {
			enable = (Boolean) getHttpSession().getAttribute(
					"shipping_charge_enable");

		}
		return enable;
	}

	public boolean isOrganizationAdmin() {
		boolean enable = true;
		if (getHttpSession().getAttribute("isOrganizationAdmin") != null) {
			enable = (Boolean) getHttpSession().getAttribute(
					"isOrganizationAdmin");

		}
		return enable;
	}

	public boolean isOfficeAdmin() {
		boolean enable = true;
		if (getHttpSession().getAttribute("isOfficeAdmin") != null) {
			enable = (Boolean) getHttpSession().getAttribute("isOfficeAdmin");

		}
		return enable;
	}

	public boolean isDepartmentAdmin() {
		boolean enable = true;
		if (getHttpSession().getAttribute("isDepartmentAdmin") != null) {
			enable = (Boolean) getHttpSession().getAttribute(
					"isDepartmentAdmin");

		}
		return enable;
	}

	public double getCessPercentage() {
		return (Double) getHttpSession().getAttribute("cess_percentage");
	}

	public String getDateFormat() {
		return getHttpSession().getAttribute("date_format").toString();
	}

	public String asString(Object nonString) {
		return String.valueOf(nonString);
	}

	public double toDouble(String stringValue) {
		return Double.parseDouble(stringValue);
	}

	public long toLong(String stringValue) {
		return Long.parseLong(stringValue);
	}

	public int toInt(String stringValue) {
		return Integer.parseInt(stringValue);
	}

	public double roundNumber(double val) {
		int Rpl = 2;
		if (getHttpSession().getAttribute("no_of_precisions") != null) {
			Rpl = (Integer) session.getAttribute("no_of_precisions");
		}
		double p = (double) Math.pow(10, Rpl);
		val *= p;
		double tmp = Math.round(val);
		return (double) tmp / p;
	}
	public String roundNumberToString(double val) {
		int Rpl = 2;
		if (getHttpSession().getAttribute("no_of_precisions") != null) {
			Rpl = (Integer) session.getAttribute("no_of_precisions");
		}
		DecimalFormat df = new DecimalFormat(session.getAttribute("no_of_precisions_hash").toString());
		return new BigDecimal(df.format(val)).setScale(Rpl,BigDecimal.ROUND_HALF_EVEN).toString();
	}

	public String getFormattedAmount(double amount) {

		String dec = "";
		double pow = 0;
		if (getHttpSession().getAttribute("no_of_precisions") != null) {
			pow = (Integer) session.getAttribute("no_of_precisions");
		}
		if (pow > 0) {
			while (dec.length() < pow) {
				dec += "0";
			}
		}
		DecimalFormat format = new DecimalFormat("0." + dec);
		return format.format(amount);
	}

	public String getBillName(int type) {

		String billName = "";
		try {
			billName = comDao.getBillName(getOfficeID(), type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billName;
	}

	public boolean isSystemAdmin() {
		boolean flag = false;
		try {
			if (((Long) session.getAttribute("role_id")) == SConstants.ROLE_SYSTEM_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean isSuperAdmin() {
		boolean flag = false;
		try {
			if (((Long) session.getAttribute("role_id")) == SConstants.ROLE_SUPER_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean isSemiAdmin() {
		boolean flag = false;
		try {
			if (((Long) session.getAttribute("role_id")) == SConstants.SEMI_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean isCustomer() {
		boolean flag = false;
		try {
			if (((Long) session.getAttribute("isCustomer")) == SConstants.ROLE_CUSTOMER)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean isSupplier() {
		boolean flag = false;
		try {
			if (((Long) session.getAttribute("isSupplier")) == SConstants.ROLE_SUPPLIER)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public void saveActivity(long optionId, String log) {
		try {
			ActivityLogModel activityLogModel = new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOffice_id(getOfficeID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(0);
			comDao.saveActivityLog(activityLogModel);
			
			if(getSettings().getTHEME()==SConstants.REVERP_THEME)
				((NewMainLayout)getUI().getCurrent().getContent()).createRecentMenu();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveActivity(long optionId, String log, long billId) {
		try {
			ActivityLogModel activityLogModel = new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOffice_id(getOfficeID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(billId);
			comDao.saveActivityLog(activityLogModel);
			
			if(getSettings().getTHEME()==SConstants.REVERP_THEME)
				((NewMainLayout)getUI().getCurrent().getContent()).createRecentMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void saveReportedIssue(long optionId, String issue, long billId, long toUser) throws Exception{
		try {
			ReportIssueModel activityLogModel = new ReportIssueModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setIssue(issue);
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOffice_id(getOfficeID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(billId);
			activityLogModel.setTo_user(toUser);
			activityLogModel.setStatus(1);
			comDao.saveReportedIssue(activityLogModel);
			
			if(getSettings().getTHEME()==SConstants.REVERP_THEME)
				((NewMainLayout)getUI().getCurrent().getContent()).createIssueNotification();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	
	public void saveSessionActivity(long optionId, long billId, String details) {
		try {
			SessionActivityModel activityLogModel = new SessionActivityModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOffice_id(getOfficeID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(billId);
			activityLogModel.setDetails(details);
			comDao.saveSessionActivity(activityLogModel);
			
			if(getSettings().getTHEME()==SConstants.REVERP_THEME)
				((NewMainLayout)getUI().getCurrent().getContent()).createSavedSessionsMenu();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveReview(long optionId, String title,	String comments, long userID,	String reportFile) {
		try {
			
			File oldFile=new File(reportFile);
			
			ReviewModel activityLogModel = new ReviewModel();
			activityLogModel.setDate(CommonUtil.getTimestampFromUtilDate(getWorkingDate()));
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOffice_id(getOfficeID());
			activityLogModel.setOption(optionId);
			activityLogModel.setTitle(title);
			activityLogModel.setDetails(comments);
			activityLogModel.setFileName(oldFile.getName());
			comDao.saveReview(activityLogModel);
			
			String rootPath = VaadinServlet.getCurrent().getServletContext()
					.getRealPath("/");
			rootPath+="VAADIN/themes/testappstheme/Reviews/"+getLoginID();
			
			
			File newFilePath=new File(rootPath);
			if(!newFilePath.exists()){
				newFilePath.mkdir();
			}
			
			FileUtils.copyFile(oldFile, new File(rootPath+"/"+oldFile.getName()));
			
//			if(getSettings().getTHEME()==SConstants.REVERP_THEME)
//				((NewMainLayout)getUI().getCurrent().getContent()).createSavedSessionsMenu();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public boolean isSaleEditable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("sale_editable") != null) {
			enable = (Boolean) getHttpSession().getAttribute("sale_editable");

		}
		return enable;
	}

	public boolean isSalePrintable() {
		boolean enable = true;
		if (getHttpSession().getAttribute("sale_printable") != null) {
			enable = (Boolean) getHttpSession().getAttribute("sale_printable");

		}
		return enable;
	}

	public String getAmountInWords(double amount) {
		String amountString = "";
		try {

			S_OfficeModel ofc = new OfficeDao().getOffice(getOfficeID());
			NumberToWords numberToWords = new NumberToWords();
			amountString = numberToWords.convertNumber(roundNumberToString(amount), ofc
					.getCurrency().getInteger_part(), ofc.getCurrency()
					.getFractional_part());

		} catch (Exception e) {
		}

		return amountString;
	}
	

	public String getAmountInWords(double amount,long currencyId) {
		String amountString = "";
		try {

			CurrencyModel curr=new CurrencyManagementDao().getselecteditem(currencyId);
			NumberToWords numberToWords = new NumberToWords();
			amountString = numberToWords.convertNumber(roundNumberToString(amount), curr.getInteger_part(), curr
					.getFractional_part());

		} catch (Exception e) {
		}

		return amountString;
	}


	public java.util.Date getFormattedTime(java.util.Date date) {
		DateFormat format = DateFormat.getDateTimeInstance();
		if (getHttpSession().getAttribute("time_zone") != null)
			format.setTimeZone(TimeZone.getTimeZone(getHttpSession()
					.getAttribute("time_zone").toString()));
		return new java.util.Date(format.format(date));
	}

	public String getPropertyName(String name) {
		try {
			if (bundle != null)
				name = bundle.getString(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;
	}
	
	public java.util.Date getMonthStartDate(){
		
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		cal.setTime(getWorkingDate());
		cal.set(cal.DAY_OF_MONTH, 1);
		
		return cal.getTime();
		
	}
	
	public java.util.Date getMonthEndDate(){
		
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		cal.setTime(getWorkingDate());
		cal.set(cal.DAY_OF_MONTH,cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		
		return cal.getTime();
		
	}
	
	public SComboField getBillNoFiled(){
		return new SComboField();
	}

	public java.util.Calendar getCalendar(){
		return java.util.Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		
	}
	

	public void sendAlert(String content) {
		try {
			
			if (getSettings().isALERT_EMAIL()) {

				SMail mail = new SMail();
				String[] arr = getSettings().getALERT_EMAILIDS().split(",");
				if (arr != null && arr.length > 0) {
					Address[] ads = new Address[arr.length];
					InternetAddress emailAddr = null;

					for (int i = 0; i < arr.length; i++) {

						if (arr[i].toString().trim().length() > 0) {
							try {
								emailAddr = new InternetAddress(arr[i]);
							} catch (AddressException e) {
							}

							ads[i] = emailAddr;
						}
					}
					mail.sendMailFromAppMail(ads, content, "Alert From REVERP",
							null);
				}

			}
			if (getSettings().isALERT_NOTIFICATION()) {
					new GCMPushing().sendPushNotification(content);
			}
			
		} catch (Exception e) {
		}
	}
}
