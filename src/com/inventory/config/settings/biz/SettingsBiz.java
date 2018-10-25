package com.inventory.config.settings.biz;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.dao.SettingsDao;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SessionUtil;

public class SettingsBiz {
	
	WrappedSession session= new SessionUtil().getHttpSession();
	
	public SettingsValuePojo updateSettingsValue(long organization_id, long office_id) {
		
		SettingsValuePojo settings=null;
		try {
			
			settings=new SettingsDao().getAllSettings(organization_id,office_id);
			session.setAttribute("show_supplier_specific_item_in_purchase", settings.isSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE());
			session.setAttribute("items_in_multi_lang", settings.isITEMS_IN_MULTIPLE_LANGUAGE());
			session.setAttribute("item_group_filtering_in_sales", settings.isITEM_GROUP_FILTER_IN_SALES());
			session.setAttribute("tax_enabled", settings.isTAX_ENABLED());
			session.setAttribute("cess_enabled", settings.isCESS_ENABLED());
			session.setAttribute("cess_percentage", settings.getCESS_PERCENTAGE());
			session.setAttribute("hide_organization_details", settings.isHIDE_ORGANIZATION_DETAILS());
			
			if(settings.getCASH_GROUP()==0 || settings.getCUSTOMER_GROUP()==0 || settings.getSUPPLIER_GROUP()==0|| settings.getCLEARING_AGENT_GROUP()==0 || settings.getCASH_ACCOUNT()==0|| settings.getCHEQUE_ACCOUNT()==0 ) { 
				session.setAttribute("settings_not_set",true);
			}
			else
				session.removeAttribute("settings_not_set");
			
			session.setAttribute("manuf_date_enable", settings.isMANUFACTURING_DATES_ENABLE());
			session.setAttribute("discount_enable", settings.isDISCOUNT_ENABLE());
//			session.setAttribute("excise_duty_enable", settings.isEXCISE_DUTY_ENABLE());
//			session.setAttribute("shipping_charge_enable", settings.isSHIPPINGCHARGEENABLE());
			
			session.setAttribute("date_format", settings.getDATE_FORMAT());
			
			if(settings.getSTOCK_MANAGEMENT()==1)
				session.setAttribute("isFIFO", true);
			else
				session.setAttribute("isFIFO", false);
			
			session.setAttribute("settings", settings);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return settings;
		
	}
	

}
