package com.webspark.Components;

import java.sql.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.Vector;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.IDGeneratorDao;
import com.webspark.model.ActivityLogModel;
	
public class SContainerPanel extends SPanel{
	
	WrappedSession session=null;
	long id=0;
	String value="";
	
	ResourceBundle bundle ;
	
	public SContainerPanel() {
		try {
			if(getHttpSession().getAttribute("property_file")!=null)
				bundle = ResourceBundle.getBundle(getHttpSession().getAttribute("property_file").toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		// TODO Auto-generated constructor stub
		
	}
	
	public long getNextSequence(String type, long loginId) throws Exception{
		long newId=0;
		try {
			newId=new IDGeneratorDao().generateID(type, loginId, getOfficeID(), getOrganizationID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return newId;
	}
	
	public WrappedSession getHttpSession(){
		if(session==null)
			session=new SessionUtil().getHttpSession();
		return session;
	}
	
	public void setSize(int width, int height){
		setWidth(width+"px");
		setHeight(height+"px");
	}
	
	public void setRelatedOptions(Vector options){
		
	}
	
	public void setRequiredError(AbstractComponent component, String fieldNameToDisplay, boolean enable){
		if(enable){
			component.setComponentError(new SUserError("<i style='font-size: 13px;'>"+fieldNameToDisplay, ContentMode.HTML, ErrorLevel.CRITICAL));
		}
		else
			component.setComponentError(null);
	}
	
	// Geting login_id from Session
	public long getLoginID(){
		if(getHttpSession().getAttribute("login_id")!=null){
			id=(Long) getHttpSession().getAttribute("login_id");
		}
		return id;
	}
	// Geting user_id from Session
	public long getUserID(){
		if(getHttpSession().getAttribute("user_id")!=null){
			id=(Long) getHttpSession().getAttribute("user_id");
		}
		return id;
	}
	/* Geting role_id from Session */
	public long getRoleID(){
		if(getHttpSession().getAttribute("role_id")!=null){
			id=(Long) getHttpSession().getAttribute("role_id");
		}
		return id;
	}
	// Geting login_name from Session
	public String getLoginName(){
		if(getHttpSession().getAttribute("login_name")!=null){
			value=(String) getHttpSession().getAttribute("login_name");
		}
		return value;
	}
	// Geting office_id from Session
	public long getOfficeID(){
		if(getHttpSession().getAttribute("office_id")!=null){
			id=(Long) getHttpSession().getAttribute("office_id");
		}
		return id;
	}
	// Geting currency_id from Session
	public long getCurrencyID(){
		if(getHttpSession().getAttribute("currency_id")!=null){
			id=(Long) getHttpSession().getAttribute("currency_id");
		}
		return id;
	}
	// Geting organization_id from Session
	public long getOrganizationID(){
		if(getHttpSession().getAttribute("organization_id")!=null){
			id=(Long) getHttpSession().getAttribute("organization_id");
		}
		return id;
	}
	// Geting working_date from Session
	public Date getWorkingDate(){
		Date date=null;
		if(getHttpSession().getAttribute("working_date")!=null){
			date=(Date) getHttpSession().getAttribute("working_date");
		}
		return date;
	}
	
	// Geting country ID from Session
		public long getCountryID() {
			if (getHttpSession().getAttribute("country_id") != null) {
				id = (Long) getHttpSession().getAttribute("country_id");
			}
			return id;
		}
	
	// Geting settings from Session
	public SettingsValuePojo getSettings(){
		SettingsValuePojo settings=null;
		if(getHttpSession().getAttribute("working_date")!=null){
			settings=(SettingsValuePojo) getHttpSession().getAttribute("settings");
		}
		return settings;
	}

	
	
	public boolean isCessEnableOnItem(long item_id){
		boolean enable=false;
		if(getHttpSession().getAttribute("cess_enabled")!=null){
			if((Boolean) getHttpSession().getAttribute("cess_enabled")==true){
				try {
					enable=new ItemDao().isCessEnabled(item_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return enable;
	}
	
	public boolean isCessEnable(){
		boolean enable=false;
		if(getHttpSession().getAttribute("cess_enabled")!=null){
			enable=(Boolean) getHttpSession().getAttribute("cess_enabled");
				
		}
		return enable;
	}
	
	public boolean isManufDateEnable(){
		boolean enable=true;
		if(getHttpSession().getAttribute("manuf_date_enable")!=null){
			enable=(Boolean) getHttpSession().getAttribute("manuf_date_enable");
		}
		return enable;
	}
	public boolean isDiscountEnable(){
		boolean enable=true;
		if(getHttpSession().getAttribute("discount_enable")!=null){
			enable=(Boolean) getHttpSession().getAttribute("discount_enable");
				
		}
		return enable;
	}
	public boolean isExciceDutyEnable(){
		boolean enable=true;
		if(getHttpSession().getAttribute("excise_duty_enable")!=null){
			enable=(Boolean) getHttpSession().getAttribute("excise_duty_enable");
				
		}
		return enable;
	}
	public boolean isShippingChargeEnable(){
		boolean enable=true;
		if(getHttpSession().getAttribute("shipping_charge_enable")!=null){
			enable=(Boolean) getHttpSession().getAttribute("shipping_charge_enable");
				
		}
		return enable;
	}
	
	public boolean isOrganizationAdmin(){
		boolean enable=true;
		if(getHttpSession().getAttribute("isOrganizationAdmin")!=null){
			enable=(Boolean) getHttpSession().getAttribute("isOrganizationAdmin");
				
		}
		return enable;
	}
	public boolean isOfficeAdmin(){
		boolean enable=true;
		if(getHttpSession().getAttribute("isOfficeAdmin")!=null){
			enable=(Boolean) getHttpSession().getAttribute("isOfficeAdmin");
				
		}
		return enable;
	}
	public boolean isDepartmentAdmin(){
		boolean enable=true;
		if(getHttpSession().getAttribute("isDepartmentAdmin")!=null){
			enable=(Boolean) getHttpSession().getAttribute("isDepartmentAdmin");
				
		}
		return enable;
	}
	
	
	
	
	
	public double getCessPercentage(){
			return (Double) getHttpSession().getAttribute("cess_percentage");
	}
	
	public String asString(Object nonString){
		return String.valueOf(nonString);
	}
	public double toDouble(String stringValue){
		return Double.parseDouble(stringValue);
	}
	public long toLong(String stringValue){
		return Long.parseLong(stringValue);
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
	
	
	public boolean isTaxEnable(){
		boolean enable=true;
		if(getHttpSession().getAttribute("tax_enabled")!=null){
			enable=(Boolean) getHttpSession().getAttribute("tax_enabled");
				
		}
		return enable;
	}
	public String getBillName(int type) {

		String billName = "";
		try {
			billName = new CommonMethodsDao().getBillName(getOfficeID(), type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billName;
	}

	public boolean isSystemAdmin() {
		boolean flag = false;
		try {
			if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SYSTEM_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public boolean isSuperAdmin() {
		boolean flag = false;
		try {
			if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SUPER_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public boolean isSemiAdmin() {
		boolean flag = false;
		try {
			if (((Long)session.getAttribute("role_id")) == SConstants.SEMI_ADMIN)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public void saveActivity(long optionId, String log){
		try {
			ActivityLogModel activityLogModel=new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(0);
			new CommonMethodsDao().saveActivityLog(activityLogModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveActivity(long optionId, String log,long billId){
		try {
			ActivityLogModel activityLogModel=new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(getLoginID());
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(billId);
			new CommonMethodsDao().saveActivityLog(activityLogModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getPropertyName(String name) {
		try {
			if(bundle!=null)
				name=bundle.getString(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;
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
	public SComboField getBillNoFiled(){
		return new SComboField();
	}
	
	public String getDateFormat() {
        return getHttpSession().getAttribute("date_format").toString();
	}
	 public java.util.Calendar getCalendar(){
         return java.util.Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
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
}
