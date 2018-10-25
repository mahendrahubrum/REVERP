package com.webspark.test.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 * WebSpark.
 *
 * Jan 23 2014
 */
public class SalesStockUpdateDao extends SHibernate implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1071364669984964064L;

	CommonMethodsDao comDao=new CommonMethodsDao();
	
	
	public Set<Long> getIssueHappenedSalesIDs(long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		
		Set<Long> salesIds=new HashSet<Long>();

		try {
			begin();
			
			

			String condition = "";
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			if (custId != 0) {
				
				LedgerModel cust = (LedgerModel) getSession().get(LedgerModel.class, custId);
			
				List list = getSession()
						.createQuery("from SalesModel where date>=:fromDate and date<=:toDate and active=true "
										+ condition).setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).list();
				
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				rptModel=new ReportBean();
				
				rptModel.setClient_name(cust.getName());
//				rptModel.setDate(CommonUtil.formatSQLDateToDDMMMYYYY(salObj.getDate()));
				
//				rptModel.setParticulars(String.valueOf(salObj.getSales_number()));
				
				Iterator it=list.iterator();
				while (it.hasNext()) {
					
					salObj=(SalesModel) it.next();
					
					
					purchase_amt=0;
					Iterator it2=salObj.getInventory_details_list().iterator();
					while (it2.hasNext()) {
						invObj=(SalesInventoryDetailsModel) it2.next();
						
						stockId=0;qty=0;
						if(invObj.getStock_ids().length()>0) {
							stks=invObj.getStock_ids().toString().split(",");
							for (int i = 0; i < stks.length; i++) {
								try {
									
									stockId=Long.parseLong(stks[i].split(":")[0]);
									qty=Double.parseDouble(stks[i].split(":")[1]);
									
									obj=getSession().createQuery(
											"select rate from ItemStockModel where id="+stockId).uniqueResult();
									if(obj!=null) {
									}
									else
										salesIds.add(salObj.getId());
//										System.out.println("Sales No. :"+salObj.getSales_number()+", Item :"+invObj.getItem().getName());
									
									
								} catch (Exception e) {
									// TODO: handle exception
								}
								
							}
						}
					}
				}
			}
			else {
				
				List custList = getSession()
						.createQuery(
								"select new com.inventory.config.acct.model.CustomerModel(ledger.id, name)"
										+ " from CustomerModel where ledger.office.id=:ofc order by name")
						.setParameter("ofc", officeId).list();
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				Iterator itr1=custList.iterator();
				while (itr1.hasNext()) {
					CustomerModel cust = (CustomerModel) itr1.next();
					
					List list = getSession().createQuery(
									"from SalesModel where date>=:fromDate and date<=:toDate and active=true and customer.id=" + cust.getId()+" "
											+ condition)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate).list();
					
					purchase_amt=0;
					stockId=0;
					qty=0; saledValue=0; purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(cust.getName());
					
					long salNo=0;
					Iterator it=list.iterator();
					while (it.hasNext()) {
						
						salObj=(SalesModel) it.next();
						
						
						purchase_amt=0;
						Iterator it2=salObj.getInventory_details_list().iterator();
						while (it2.hasNext()) {
							invObj=(SalesInventoryDetailsModel) it2.next();
							
							
							stockId=0;qty=0;
							if(invObj.getStock_ids().length()>0) {
								stks=invObj.getStock_ids().toString().split(",");
								for (int i = 0; i < stks.length; i++) {
									try {
										
										stockId=Long.parseLong(stks[i].split(":")[0]);
										qty=Double.parseDouble(stks[i].split(":")[1]);
										
										obj=getSession().createQuery(
												"select rate from ItemStockModel where id="+stockId).uniqueResult();
										if(obj==null) {
											salesIds.add(salObj.getId());
										}
//										else
											
//											if(salNo!=salObj.getSales_number())
//												System.out.println("Sales No. :"+salObj.getSales_number());
//											salNo=salObj.getSales_number();
//											System.out.println("Sales No. :"+salObj.getSales_number()+", Item :"+invObj.getItem().getName());
//											
//										}
										
									} catch (Exception e) {
										// TODO: handle exception
									}
									
								}
							}
						}
						
					}
				}
				
				
				
				
				
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return salesIds;
	}
	
	
	
	
	
	public void programeticalUpdate(long salesId)
			throws Exception {
		try {
			
			begin();
			
			SalesModel newobj=(SalesModel) getSession().get(SalesModel.class, salesId);
			
			SalesInventoryDetailsModel invObj;
			List list;
			Iterator<SalesInventoryDetailsModel> it = newobj.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

//				getSession().createQuery(
//								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getQuantity_in_basic_unit())
//						.executeUpdate();

				// For Stock Update
				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
			}
			
			// getSession().delete(obj);
			
			flush();
			
			// Save
			
			List<SalesInventoryDetailsModel> invList = new ArrayList<SalesInventoryDetailsModel>();
			String stockIDs;
			Iterator<SalesInventoryDetailsModel> it1 = newobj.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();
				
//				getSession()
//						.createQuery(
//								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
//						.setParameter("id", invObj.getItem().getId())
//						.setParameter("qty", invObj.getQuantity_in_basic_unit())
//						.executeUpdate();
				
				stockIDs=comDao.decreaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());
				
				invObj.setStock_ids(stockIDs);
				
				flush();
				
				invList.add(invObj);
			}
			
			newobj.setInventory_details_list(invList);
			
			// Transaction Related
			
			getSession().update(newobj);


			commit();
			
			System.out.println("Updated :"+newobj.getSales_number());

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
	
	public List<Long> getAllPurchases(Date frm, Date todt, long ofc_id) throws Exception {
		List<Long> resultList=null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select id from PurchaseModel where office.id=:ofc and active=true and date between :frm and :to")
					.setParameter("ofc", ofc_id).setParameter("frm", frm).setParameter("to", todt).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public double getConvertionRate(long item_id, long unit_id, int sales_type) throws Exception {
		double rate=1;
		try {
			
			Object obj = getSession()
					.createQuery(
							"select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt " +
							" and sales_type=:st").setLong("itm", item_id)
									.setLong("alt", unit_id).setLong("st", sales_type).uniqueResult();
			if(obj!=null)
				rate=(Double) obj;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		return rate;
	}
	
	
	public void programeticalPurchaseUpdate(long salesId)
			throws Exception {
		try {
			
			begin();
			
			PurchaseModel newobj=(PurchaseModel) getSession().get(PurchaseModel.class, salesId);
			
			PurchaseInventoryDetailsModel invObj;
			List list;
			Iterator<PurchaseInventoryDetailsModel> it = newobj.getPurchase_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
				
				double convQty=getConvertionRate(invObj.getItem().getId(),invObj.getUnit().getId(), 0);
				double oldQtyInBasicUnit=invObj.getQty_in_basic_unit();
				double newQtyInBasicUnit=CommonUtil.roundNumber(convQty*invObj.getQunatity());
				
				if(oldQtyInBasicUnit!=newQtyInBasicUnit) {
					invObj.setQty_in_basic_unit(newQtyInBasicUnit);
					Object obj=getSession().createQuery("from ItemStockModel where inv_det_id="+invObj.getId()).uniqueResult();
					if(obj!=null) {
						ItemStockModel stk=(ItemStockModel) obj;
						stk.setBalance(CommonUtil.roundNumber(convQty*invObj.getQunatity()));
						stk.setQuantity(CommonUtil.roundNumber(convQty*invObj.getQunatity()));
						stk.setRate(CommonUtil.roundNumber(invObj.getQunatity()*invObj.getUnit_price()/invObj.getQty_in_basic_unit()));
						
						getSession().update(stk);
					}
					
					getSession().update(invObj);
					
					getSession().createQuery(
							"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
					.setParameter("id", invObj.getItem().getId())
					.setParameter("qty", newQtyInBasicUnit-oldQtyInBasicUnit)
					.executeUpdate();
					
					
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
	
	
	
	
}
