package com.inventory.process.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transaction;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.process.model.FinancialYearsModel;
import com.inventory.process.model.ItemClosingStockModel;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.inventory.subscription.model.SubscriptionPaymentModel;
import com.inventory.subscription.ui.SubscriptionPayment;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_IDGeneratorSettingsModel;
import com.webspark.model.S_IDValueCompoundKey;
import com.webspark.model.S_IDValueModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */

public class EndProcessDao extends SHibernate implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9194732019184774037L;
	List resultList=new ArrayList();
	
	
	public List getProcesses(String process_type) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("from EndProcessModel where status=:sts and type=:typ")
							.setLong("sts", 1).setString("typ", process_type).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public void changeOfficeWorkingDate(long office_id) throws Exception {
		try {
			begin();
			
			S_OfficeModel ofc=(S_OfficeModel) getSession().get(S_OfficeModel.class, office_id);
			
			Date dt=new Date(ofc.getWorkingDate().getTime());
			Calendar cal=Calendar.getInstance();
			cal.setTime(dt);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			ofc.setWorkingDate(new java.sql.Date(cal.getTime().getTime()));
			
			getSession().update(ofc);
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	public void updateClosingStock(long office_id, java.sql.Date date) throws Exception {
		try {
			begin();
			
			getSession().createQuery(
					"delete from ItemClosingStockModel where office_id=:ofc and date=:dt")
						.setParameter("ofc", office_id).setParameter("dt", date).executeUpdate();
			
			List itemsList = getSession()
					.createQuery(
							"select id"
									+ " from ItemModel where office.id=:ofc and status=:sts")
					.setParameter("ofc", office_id).setLong("sts", SConstants.statuses.ITEM_ACTIVE).list();
			
			int count=0;
			Iterator it=itemsList.iterator();
			long item_id;
			double inwards_qty=0, outward_qty=0;
			Object obj;
			Object clsObj;
			double closing_stk=0;
			java.sql.Date dayBefore;
			ItemClosingStockModel itmClsStkObj;
			while(it.hasNext()) {
				
				item_id=(Long) it.next();
				inwards_qty=0;
				outward_qty=0;
				
				// Quantity Inwards
				
				obj = getSession()
						.createQuery(
								"select sum(b.qty_in_basic_unit) " +
									"from PurchaseModel a join a.inventory_details_list b where b.item.id=:itemid and a.office.id=:ofc" +
									" and date=:dt").setParameter("dt", date)
						.setLong("itemid", item_id).setLong("ofc", office_id).uniqueResult();
				
				if(obj!=null)
					inwards_qty+=(Double)obj;
				
				obj=null;
				obj = getSession()
						.createQuery(
								"select sum(b.quantity_in_basic_unit) " +
									"from SalesReturnModel a join a.inventory_details_list b where b.item.id=:itemid and a.office.id=:ofc" +
									" and date=:dt").setParameter("dt", date)
						.setLong("itemid", item_id).setLong("ofc", office_id).uniqueResult();
				
				if(obj!=null)
					inwards_qty+=(Double)obj;
				
				
				// Quantity Outwards
				obj=null;
				obj = getSession()
						.createQuery(
								"select sum(b.quantity_in_basic_unit) " +
									"from SalesModel a join a.inventory_details_list b where b.item.id=:itemid and a.office.id=:ofc" +
									" and date=:dt").setParameter("dt", date)
						.setLong("itemid", item_id).setLong("ofc", office_id).uniqueResult();
				
				if(obj!=null)
					outward_qty+=(Double)obj;
				
				obj=null;
				obj = getSession()
						.createQuery(
								"select sum(b.qty_in_basic_unit) " +
									"from PurchaseReturnModel a join a.inventory_details_list b where b.item.id=:itemid and a.office.id=:ofc" +
									" and date=:dt").setParameter("dt", date)
						.setLong("itemid", item_id).setLong("ofc", office_id).uniqueResult();
				
				if(obj!=null)
					outward_qty+=(Double)obj;
				
				obj=null;
				obj = getSession()
						.createQuery(
								"select sum(b.quantity_in_basic_unit) " +
									"from DeliveryNoteModel a join a.inventory_details_list b where b.item.id=:itemid and a.office.id=:ofc" +
									" and date=:dt").setParameter("dt", date)
						.setLong("itemid", item_id).setLong("ofc", office_id).uniqueResult();
				
				if(obj!=null)
					outward_qty+=(Double)obj;
				
				dayBefore=new java.sql.Date(date.getTime()-86400000);
				
				clsObj=getSession().createQuery(
						"select closing_stock from ItemClosingStockModel where item.id=:itm and date=:dt")
							.setParameter("itm", item_id).setParameter("dt", dayBefore).uniqueResult();
				
				closing_stk=0;
				if(clsObj!=null)
					closing_stk=(Double) clsObj;
				
				itmClsStkObj=new ItemClosingStockModel();
				
				itmClsStkObj.setItem(new ItemModel(item_id));
				itmClsStkObj.setInwards_qty(inwards_qty);
				itmClsStkObj.setOutwards_qty(outward_qty);
				itmClsStkObj.setClosing_stock(closing_stk+inwards_qty-outward_qty);
				itmClsStkObj.setDate(date);
				itmClsStkObj.setOffice_id(office_id);
				
				getSession().save(itmClsStkObj);
				
				count++;
				if(count%SConstants.HIB_FLUSH_LIMIT==0){
					flush();
				}
				
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void updateSubscriptionDue(SubscriptionPaymentModel mdl,TransactionModel transaction) throws Exception {
		try {
			begin();
			getSession().save(transaction);
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			TransactionDetailsModel tr;
			while (aciter.hasNext()) {
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();
//				flush();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			mdl.setTransaction_id(transaction.getTransaction_id());
			getSession().save(mdl);
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
	
	
	
	public void refreshIDGenerator(S_IDGeneratorSettingsModel obj) throws Exception {
			try {
				
				List orgnzns,offices;
				S_IDValueModel idValue;
				S_IDValueCompoundKey compKey;
				begin();
				
				
				getSession().createQuery("delete from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId")
								.setParameter("mastId", obj.getId()).executeUpdate();
				flush();
				
				switch(obj.getScope()){
				
				case SConstants.scopes.LOGIN_LEVEL:
					
					orgnzns = getSession()
							.createQuery(
									"select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel")
							.list();

					if (orgnzns.size() > 0) {
						S_OrganizationModel org;
						S_OfficeModel ofc;
						List logins;

						for (Object orgObj : orgnzns) {

							org = (S_OrganizationModel) orgObj;

							offices = getSession().createQuery(
											"select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
											.setParameter("org", org).list();

							if (offices.size() > 0) {

								for (Object ofcObj : offices) {

									ofc = (S_OfficeModel) ofcObj;
									
									logins=null;
									logins = getSession().createQuery(
													"select new com.webspark.model.S_LoginModel(id) from S_LoginModel where office=:ofc")
													.setParameter("ofc", ofc).list();

									if (logins.size() > 0) {

										for (Object logObj : logins) {

											idValue = new S_IDValueModel();
											compKey = new S_IDValueCompoundKey();

											compKey.setMast_id(obj);
											compKey.setOrganization_id(org.getId());
											compKey.setOffice_id(ofc.getId());
											compKey.setLogin_id(((S_LoginModel) logObj).getId());
											idValue.setValue(obj.getInitial_value()-1);

											idValue.setId_val_comp_key(compKey);

											getSession().save(idValue);

										}

									}

								}

							}

						}
					}
					
					break;
					
				case SConstants.scopes.OFFICE_LEVEL:
					
					orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
					
					if(orgnzns.size()>0){
						
						S_OrganizationModel org;
						for (Object orgObj : orgnzns) {
							
							org=(S_OrganizationModel) orgObj;
							
							offices=getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
									.setParameter("org", org).list();
							
							if(offices.size()>0){
								
								for (Object ofcObj : offices) {
									
									idValue=new S_IDValueModel();
									compKey=new S_IDValueCompoundKey();
									
									compKey.setMast_id(obj);
									compKey.setOrganization_id(org.getId());
									compKey.setOffice_id(((S_OfficeModel)ofcObj).getId());
									idValue.setValue(obj.getInitial_value()-1);
									
									idValue.setId_val_comp_key(compKey);
									
									getSession().save(idValue);
									
								}
							
							}
							
						}
						
						
					}
					break;
					
				case SConstants.scopes.ORGANIZATION_LEVEL:
					
					orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
					
					if(orgnzns.size()>0){
						
						for (Object orgObj : orgnzns) {
							idValue=new S_IDValueModel();
							compKey=new S_IDValueCompoundKey();
							
							compKey.setMast_id(obj);
							compKey.setOrganization_id(((S_OrganizationModel)orgObj).getId());
							idValue.setValue(obj.getInitial_value()-1);
							
							idValue.setId_val_comp_key(compKey);
							
							getSession().save(idValue);
							
						}
						
						
					}
					
					break;
					
				default:
					
					idValue=new S_IDValueModel();
					compKey=new S_IDValueCompoundKey();
					compKey.setMast_id(obj);
					
					compKey.setOrganization_id(0);
					compKey.setOffice_id(0);
					compKey.setLogin_id(0);
					
					idValue.setValue(obj.getInitial_value()-1);
					
					idValue.setId_val_comp_key(compKey);
					
					getSession().save(idValue);
					
					break;
					
			
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
	
	
	
	public void createNewFinancialYearAndSetToOffice(FinancialYearsModel fin) throws Exception {
		try {
			begin();
			getSession().save(fin);
			
			getSession().createQuery("update S_OfficeModel set fin_start_date=:st, fin_end_date=:end" +
					" where id=:ofc")
							.setParameter("st", fin.getStart_date()).setParameter("end", fin.getEnd_date())
							.setLong("ofc", fin.getOffice_id()).executeUpdate();
			
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
}
