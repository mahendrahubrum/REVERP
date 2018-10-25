package com.inventory.config.settings.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.model.AccountSettingsModel;
import com.inventory.config.settings.model.SettingsModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.MobileAppSettingsModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jul 25, 2013
 */

public class SettingsDao extends SHibernate implements Serializable {

	List resultList = new ArrayList();

	public void saveGlobalSettings(List settingsList, long org_id)
			throws Exception {

		try {

			begin();

			long ct = (Long) getSession()
					.createQuery(
							"select count(id) from SettingsModel where level=:lvl and level_id=:lvid")
					.setParameter("lvl", SConstants.scopes.SYSTEM_LEVEL)
					.setLong("lvid", org_id).uniqueResult();

			SettingsModel obj;
			if (ct != settingsList.size()) {
				System.out.println("Delete Insert");
				getSession()
						.createQuery(
								"delete from SettingsModel where level=:lvl and level_id=:lvid")
						.setParameter("lvl", SConstants.scopes.SYSTEM_LEVEL)
						.setLong("lvid", org_id).executeUpdate();

				Iterator it = settingsList.iterator();
				while (it.hasNext()) {
					obj = (SettingsModel) it.next();
					obj.setLevel_id(org_id);
					getSession().save(obj);
				}
			} else {
				System.out.println("Update");
				Iterator it = settingsList.iterator();
				while (it.hasNext()) {
					obj = (SettingsModel) it.next();
					getSession()
							.createQuery(
									"update SettingsModel set value=:val where settings_name=:name  and level_id=:lvid")
							.setParameter("val", obj.getValue())
							.setParameter("name", obj.getSettings_name())
							.setLong("lvid", org_id).executeUpdate();
				}
			}

			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public List getGlobalSettings(long org_id) throws Exception {

		try {
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.settings.model.SettingsModel("
									+ "settings_name, value) from SettingsModel where level=:lvl and level_id=:lvid")
					.setParameter("lvl", SConstants.scopes.SYSTEM_LEVEL)
					.setLong("lvid", org_id).list();
			commit();

			return resultList;

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();

		}
	}

	public List getSettings(long ofc_id) throws Exception {

		try {
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.settings.model.SettingsModel("
									+ "settings_name, value) from SettingsModel where level=:lvl and level_id=:lvlid")
					.setParameter("lvl", SConstants.scopes.OFFICE_LEVEL_GENERAL)
					.setParameter("lvlid", ofc_id).list();
			commit();

			return resultList;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public void saveOrganizationSettings(List settingsList, long ofc_id)
			throws Exception {

		try {

			begin();

			long ct = (Long) getSession()
					.createQuery(
							"select count(id) from SettingsModel where level=:lvl and level_id=:lvlid")
					.setParameter("lvl", SConstants.scopes.OFFICE_LEVEL_GENERAL)
					.setParameter("lvlid", ofc_id).uniqueResult();

			if (ct != settingsList.size()) {

				getSession()
						.createQuery(
								"delete from SettingsModel where level=:lvl and level_id=:lvlid")
						.setParameter("lvl",
								SConstants.scopes.OFFICE_LEVEL_GENERAL)
						.setParameter("lvlid", ofc_id).executeUpdate();

				Iterator it = settingsList.iterator();
				while (it.hasNext()) {
					getSession().save(it.next());
				}
			} else {
				SettingsModel obj;
				Iterator it = settingsList.iterator();
				while (it.hasNext()) {
					obj = (SettingsModel) it.next();
					getSession()
							.createQuery(
									"update SettingsModel set value=:val where settings_name=:name and level_id=:lvlid")
							.setParameter("val", obj.getValue())
							.setParameter("lvlid", ofc_id)
							.setParameter("name", obj.getSettings_name())
							.executeUpdate();
				}

			}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public SettingsValuePojo getAllSettings(long org_id, long office_id)
			throws Exception {

		SettingsValuePojo settings = new SettingsValuePojo();
		try {
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.settings.model.SettingsModel("
									+ "settings_name, value) from SettingsModel where (level=:lvl1 or level=:lvl2) and level_id=:lvlid)")
					.setParameter("lvl1", SConstants.scopes.SYSTEM_LEVEL)
					.setParameter("lvl2", SConstants.scopes.ORGANIZATION_LEVEL)
					.setParameter("lvlid", org_id).list();

			List generalOfficeLevelSett = getSession()
					.createQuery(
							"select new com.inventory.config.settings.model.SettingsModel("
									+ "settings_name, value) from SettingsModel where level=:lvl and level_id=:lvlid)")
					.setParameter("lvl", SConstants.scopes.OFFICE_LEVEL_GENERAL)
					.setParameter("lvlid", office_id).list();

			List acctSettingstList = getSession()
					.createQuery(
							"select new com.inventory.config.settings.model.AccountSettingsModel("
									+ "settings_name, value) from AccountSettingsModel where office_id=:ofcid")
					.setParameter("ofcid", office_id).list();

			commit();

			SettingsModel obj;
			Iterator it = resultList.iterator();
			while (it.hasNext()) {
				obj = (SettingsModel) it.next();

				if (obj.getSettings_name().equals(
						SConstants.settings.DATE_FORMAT)) {
					settings.setDATE_FORMAT(obj.getValue());
				} else if (obj.getSettings_name().equals(
						SConstants.settings.DEFAULT_DATE_SELECTION)) {
					settings.setDEFAULT_DATE_SELECTION(Integer.parseInt(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.STOCK_MANAGEMENT)) {
					settings.setSTOCK_MANAGEMENT(Integer.parseInt(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.THEME)) {
					settings.setTHEME(Integer.parseInt(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL_HOST)) {
					settings.setSYSTEM_EMAIL_HOST(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL)) {
					settings.setSYSTEM_EMAIL(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SYSTEM_EMAIL_PASSWORD)) {
					settings.setSYSTEM_EMAIL_PASSWORD(String.valueOf(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL_HOST)) {
					settings.setSALES_EMAIL_HOST(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL)) {
					settings.setSALES_EMAIL(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_EMAIL_PASSWORD)) {
					settings.setSALES_EMAIL_PASSWORD(String.valueOf(obj
							.getValue()));
					
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL_HOST)) {
					settings.setAPPLICATION_EMAIL_HOST(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL)) {
					settings.setAPPLICATION_EMAIL(String.valueOf(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.APPLICATION_EMAIL_PASSWORD)) {
					settings.setAPPLICATION_EMAIL_PASSWORD(String.valueOf(obj
							.getValue()));
				}
			}
			
			Iterator it2 = generalOfficeLevelSett.iterator();
			while (it2.hasNext()) {
				obj = (SettingsModel) it2.next();

				if (obj.getSettings_name().equals(
						SConstants.settings.CESS_ENABLED)) {
					settings.setCESS_ENABLED(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CESS_PERCENTAGE)) {
					settings.setCESS_PERCENTAGE(Double.parseDouble(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.CESS_ACCOUNT)) {
					settings.setCESS_ACCOUNT(Long.parseLong(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.TAX_ENABLED)) {
					settings.setTAX_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}

				else if (obj.getSettings_name().equals(
						SConstants.settings.MANUFACTURING_DATES_ENABLE)) {
					settings.setMANUFACTURING_DATES_ENABLE(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.DISCOUNT_ENABLE)) {
					settings.setDISCOUNT_ENABLE(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SALES_DISCOUNT_ENABLED)) {
					settings.setSALES_DISCOUNT_ENABLE(Boolean.parseBoolean(obj.getValue()));
//				} else if (obj.getSettings_name().equals(
//						SConstants.settings.EXCISE_DUTY_ENABLE)) {
//					settings.setEXCISE_DUTY_ENABLE(Boolean.parseBoolean(obj
//							.getValue()));
//				} else if (obj.getSettings_name().equals(
//						SConstants.settings.SHIPPINGCHARGEENABLE)) {
//					settings.setSHIPPINGCHARGEENABLE(Boolean.parseBoolean(obj
//							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.FIN_YEAR_BACK_ENTRY_ENABLE)) {
					settings.setFIN_YEAR_BACK_ENTRY_ENABLE(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.HIDE_ORGANIZATION_DETAILS)) {
					settings.setHIDE_ORGANIZATION_DETAILS(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.EXPENDETURE_SHOW_ACCOUNTS)) {
					settings.setEXPENDETURE_SHOW_ACCOUNTS(Integer.parseInt(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.SHOW_ALL_EMPLOYEES_ON_PAYROLL)) {
					settings.setSHOW_ALL_EMPLOYEES_ON_PAYROLL(Boolean
							.parseBoolean(obj.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.BARCODE_ENABLED)) {
					settings.setBARCODE_ENABLED(Boolean.parseBoolean(obj
							.getValue()));
				} else if (obj.getSettings_name().equals(
						SConstants.settings.DEFAULT_CUSTOMER)) {
					settings.setDEFAULT_CUSTOMER(Long.parseLong(obj.getValue()));
				}
				
				else if (obj.getSettings_name().equals(
						SConstants.settings.KEEP_DELETED_DATA)) {
					settings.setKEEP_DELETED_DATA(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_SUBGROUP_CODE)) {
					settings.setAUTO_CREATE_SUBGROUP_CODE(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_SUPPLIER_CODE)) {
					settings.setAUTO_CREATE_SUPPLIER_CODE(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.AUTO_CREATE_CUSTOMER_CODE)) {
					settings.setAUTO_CREATE_CUSTOMER_CODE(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.USE_SALES_NO_IN_SALES_ORDER)) {
					settings.setUSE_SALES_NO_IN_SALES_ORDER(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.USE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL)) {
					settings.setUSE_SYSTEM_MAIL_FOR_SEND_CUSTOMERMAIL(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.KEEP_OTHER_WINDOWS)) {
					settings.setKEEP_OTHER_WINDOWS(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.HIDE_ALERTS)) {
					settings.setHIDE_ALERTS(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.GRADING_ENABLED)) {
					settings.setGRADING_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
//				else if (obj.getSettings_name().equals(SConstants.settings.LOCAL_FOREIGN_TYPE_ENABLED)) {
//					settings.setLOCAL_FOREIGN_TYPE_ENABLED(Boolean.parseBoolean(obj.getValue()));
//				}
				else if (obj.getSettings_name().equals(SConstants.settings.RACK_ENABLED)) {
					settings.setRACK_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.USE_GROSS_AND_NET_WEIGHT)) {
					settings.setUSE_GROSS_AND_NET_WEIGHT(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.MULTIPLE_CURRENCY_ENABLED)) {
					settings.setMULTIPLE_CURRENCY_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.SALE_PRICE_EDITABLE)) {
					settings.setSALE_PRICE_EDITABLE(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.PAYMENT_BILL_SELECTION_MANDATORY)) {
					settings.setPAYMENT_BILL_SELECTION_MANDATORY(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.DISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT)) {
					settings.setDISABLE_SALES_FOR_CUSTOMERS_UNDER_CR_LIMIT(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(
						SConstants.settings.ALERT_FOR_UNDER_CREDIT_LIMIT)) {
					settings.setALERT_FOR_UNDER_CREDIT_LIMIT(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.USE_SALES_RATE_FROM_STOCK)) {
					settings.setUSE_SALES_RATE_FROM_STOCK(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SHOW_STOCK_IN_PROFIT_REPORT)) {
					settings.setSHOW_STOCK_IN_PROFIT_REPORT(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.UPDATE_RATE_AND_CONV_QTY)) {
					settings.setUPDATE_RATE_AND_CONV_QTY(Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.CURRENCY_FORMAT)) {
					settings.setCURRENCY_FORMAT(Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.PROFIT_CALCULATION)) {
					settings.setPROFIT_CALCULATION(Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.BARCODE_TYPE)) {
					settings.setBARCODE_TYPE(Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.PAYROLL_CALCULATION)) {
					settings.setPAYROLL_CALCULATION(Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SALES_NO_CREATION_MANUAL)) {
					settings.setSALES_NO_CREATION_MANUAL(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SHOW_CONTAINER_NO)) {
					settings.setSHOW_CONTAINER_NO(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SHOW_ITEM_ATTRIBUTES)) {
					settings.setSHOW_ITEM_ATTRIBUTES(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.ALERT_EMAIL)) {
					settings.setALERT_EMAIL(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.ALERT_NOTIFICATION)) {
					settings.setALERT_NOTIFICATION(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.ALERT_EMAILIDS)) {
					settings.setALERT_EMAILIDS(obj.getValue().toString());
				}
				else if (obj.getSettings_name().equals(SConstants.settings.DEPARTMENT_ENABLED)) {
					settings.setDEPARTMENT_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.DIVISION_ENABLED)) {
					settings.setDIVISION_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.COMMISSION_SALARY_ENABLED)) {
					settings.setCOMMISSION_SALARY_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.SALES_MAN_WISE_SALES)) {
					settings.setSALES_MAN_WISE_SALES(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.PURCHSE_ORDER_EXPIRY)) {
					settings.setPURCHSE_ORDER_EXPIRY_ENABLED(Boolean.parseBoolean(obj.getValue()));
				}
				else if (obj.getSettings_name().equals(SConstants.settings.ITEMS_IN_MULTIPLE_LANGUAGE)) {
					settings.setITEMS_IN_MULTIPLE_LANGUAGE(Boolean.parseBoolean(obj.getValue()));
				}else if (obj.getSettings_name().equals(SConstants.settings.SHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE)) {
					settings.setSHOW_SUPPLIER_SPECIFIC_ITEM_IN_PURCHASE((Boolean.parseBoolean(obj.getValue())));
				}else if (obj.getSettings_name().equals(SConstants.settings.ITEM_GROUP_FILTER_IN_SALES)) {
					settings.setITEM_GROUP_FILTER_IN_SALES(Boolean.parseBoolean(obj.getValue()));
				}else if (obj.getSettings_name().equals(SConstants.settings.SALES_ORDER_FOR_SALES)) {
					settings.setSALES_ORDER_FOR_SALES(Boolean.parseBoolean(obj.getValue()));
				}
			}
			
			AccountSettingsModel obj1;
			Iterator it1 = acctSettingstList.iterator();
			while (it1.hasNext()) {
				obj1 = (AccountSettingsModel) it1.next();

				if (obj1.getSettings_name().equals(
						SConstants.settings.INVENTORY_ACCOUNT)) {
					settings.setINVENTORY_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.PROFIT_ACCOUNT)) {
					settings.setPROFIT_ACCOUNT(Long.parseLong(obj1.getValue()));
				}
				 else if (obj1.getSettings_name().equals(
							SConstants.settings.LOSS_ACCOUNT)) {
						settings.setLOSS_ACCOUNT(Long.parseLong(obj1.getValue()));
					}
				
				else if (obj1.getSettings_name().equals(
						SConstants.settings.CASH_ACCOUNT)) {
					settings.setCASH_ACCOUNT(Long.parseLong(obj1.getValue()));
				}
				
				else if (obj1.getSettings_name().equals(
						SConstants.settings.CHEQUE_ACCOUNT)) {
					settings.setCHEQUE_ACCOUNT(Long.parseLong(obj1.getValue()));
				}

				else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_ACCOUNT)) {
					settings.setSALES_ACCOUNT(Long.parseLong(obj1.getValue()));
				} 
				else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_RETURN_ACCOUNT)) {
					settings.setSALES_RETURN_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.CGS_ACCOUNT)) {
					settings.setCGS_ACCOUNT(Long.parseLong(obj1.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_TAX_ACCOUNT)) {
					settings.setSALES_TAX_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_SHIPPING_CHARGE_ACCOUNT)) {
					settings.setSALES_SHIPPING_CHARGE_ACCOUNT(Long
							.parseLong(obj1.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.CESS_ACCOUNT)) {
					settings.setCESS_ACCOUNT(Long.parseLong(obj1.getValue()));
				
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_DESCOUNT_ACCOUNT)) {
					settings.setSALES_DESCOUNT_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALES_REVENUE_ACCOUNT)) {
					settings.setSALES_REVENUE_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				}
				else if (obj1.getSettings_name().equals(
						SConstants.settings.PURCHASE_ACCOUNT)) {
					settings.setPURCHASE_ACCOUNT(Long.parseLong(obj1.getValue()));
				} 
				else if (obj1.getSettings_name().equals(
						SConstants.settings.PURCHASE_DISCOUNT_ACCOUNT)) {
					settings.setPURCHASE_DESCOUNT_ACCOUNT(Long.parseLong(obj1.getValue()));
				} 
				else if (obj1.getSettings_name().equals(
						SConstants.settings.PURCHASE_RETURN_ACCOUNT)) {
					settings.setPURCHASE_RETURN_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.PURCHASE_TAX_ACCOUNT)) {
					settings.setPURCHASE_TAX_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.PURCHASE_SHIPPING_CHARGE_ACCOUNT)) {
					settings.setPURCHASE_SHIPPING_CHARGE_ACCOUNT(Long
							.parseLong(obj1.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.FOREX_DIFFERENCE_ACCOUNT)) {
					settings.setFOREX_DIFFERENCE_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.CUSTOMER_GROUP)) {
					settings.setCUSTOMER_GROUP(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SUPPLIER_GROUP)) {
					settings.setSUPPLIER_GROUP(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.CLEARING_AGENT_GROUP)) {
					settings.setCLEARING_AGENT_GROUP(Long.parseLong(obj1
							.getValue()));
				}
				else if (obj1.getSettings_name().equals(
						SConstants.settings.CASH_GROUP)) {
					settings.setCASH_GROUP(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALARY_ACCOUNT)) {
					settings.setSALARY_ACCOUNT(Long
							.parseLong(obj1.getValue()));
				} 
				else if (obj1.getSettings_name().equals(
						SConstants.settings.SALARY_PAYABLE_ACCOUNT)) {
					settings.setSALARY_PAYABLE_ACCOUNT(Long.parseLong(obj1.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALARY_ADVANCE_ACCOUNT)) {
					settings.setSALARY_ADVANCE_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				} else if (obj1.getSettings_name().equals(
						SConstants.settings.SALARY_LOAN_ACCOUNT)) {
					settings.setSALARY_LOAN_ACCOUNT(Long.parseLong(obj1
							.getValue()));
				}
			}

			return settings;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();

		}
	}

	public void saveMobileSettings(List list, long loginId) throws Exception {

		try {
			begin();
			MobileAppSettingsModel mdl = null;
			getSession()
					.createQuery(
							"delete from MobileAppSettingsModel where level_id=:logn")
					.setParameter("logn", loginId).executeUpdate();

			for (int i = 0; i < list.size(); i++) {
				mdl = (MobileAppSettingsModel) list.get(i);
				if (mdl != null)
					getSession().save(mdl);
			}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public List getMobileAlerts(long login) throws Exception {

		List resList = null;
		try {
			begin();
			MobileAppSettingsModel mdl = null;
			resList = getSession()
					.createQuery(
							"from MobileAppSettingsModel where level_id=:logn")
					.setParameter("logn", login).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resList;
	}

}
