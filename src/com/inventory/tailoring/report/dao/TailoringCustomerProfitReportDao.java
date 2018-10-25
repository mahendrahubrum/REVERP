package com.inventory.tailoring.report.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.tailoring.model.MaterialMappingInventoryDetailsModel;
import com.inventory.tailoring.model.MaterialMappingModel;
import com.webspark.bean.ReportBean;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 * WebSpark.
 *
 * Dec 23 2014
 */
public class TailoringCustomerProfitReportDao extends SHibernate implements Serializable {
	
	private static final long serialVersionUID = -1071364669984964064L;
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	public List<Object> getSalesProfitDetails(long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		ItemStockModel stkObj=null;
//		UnitModel unitObj=null;
		try {
			
			begin();
			
			int count=0;
			String condition = "";
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			if (custId != 0) {
				
				LedgerModel cust = (LedgerModel) getSession().get(LedgerModel.class, custId);
				
				List list = getSession().createQuery("select new com.inventory.sales.model.TailoringSalesModel(id,amount)" +
								" from TailoringSalesModel where date>=:fromDate and date<=:toDate and active=true "
										+ condition).setParameter("fromDate", fromDate).setParameter("toDate", toDate).list();
				
				ReportBean rptModel;
				TailoringSalesModel orderObj;
				MaterialMappingModel salObj;
				MaterialMappingInventoryDetailsModel invObj;
				double purchase_amt=0;
				double averageAmount=0;
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
					
					orderObj = (TailoringSalesModel) it.next();
					
					Iterator itr3=getSession().createQuery("from MaterialMappingModel where orderId=:ord").setLong("ord", orderObj.getId())
								.list().iterator();
					
					while(itr3.hasNext()) {
						salObj=(MaterialMappingModel) itr3.next();
						averageAmount = 0;
						rptModel.setId(salObj.getId());
						Iterator it2 = salObj.getInventory_details_list()
								.iterator();
						while (it2.hasNext()) {
							invObj = (MaterialMappingInventoryDetailsModel) it2.next();
							count = 0;
							stockId = 0;
							qty = 0;
							purchase_amt = 0;
							if (invObj.getStock_ids().length() > 0) {
								stks = invObj.getStock_ids().toString().split(",");
								for (int i = 0; i < stks.length; i++) {
									try {
										
										stockId = Long.parseLong(stks[0].split(":")[0]);
										qty = Double.parseDouble(stks[0].split(":")[1]);
										
										obj = getSession().createQuery("from ItemStockModel where id="
														+ stockId).uniqueResult();
										if (obj != null) {
											stkObj=(ItemStockModel) obj;
											
											purchase_amt += stkObj.getRate();
											count++;
										}
									}
									catch (Exception e) {
									}
	
								}
							}
							if (purchase_amt != 0)
								averageAmount += (purchase_amt / count)
										* invObj.getQuantity_in_basic_unit();
						}
	
						purchasedValue += averageAmount;
						
					}
					
					saledValue += orderObj.getAmount();
					
				}
				
				if(saledValue>0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue-purchasedValue);
					
					resultList.add(rptModel);
				}
			}
			else {
				
				List custList = getSession()
						.createQuery(
								"select new com.inventory.config.acct.model.CustomerModel(ledger.id, name)"
										+ " from CustomerModel where ledger.office.id=:ofc order by name")
						.setParameter("ofc", officeId).list();
				
				ReportBean rptModel;
				TailoringSalesModel orderObj;
				MaterialMappingModel salObj;
				MaterialMappingInventoryDetailsModel invObj;
				double purchase_amt=0;
				double averageAmount=0;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				Iterator itr1=custList.iterator();
				while (itr1.hasNext()) {
					CustomerModel cust = (CustomerModel) itr1.next();
					
					List list = getSession().createQuery(
									"select new com.inventory.sales.model.TailoringSalesModel(id,amount) from TailoringSalesModel where date>=:fromDate and date<=:toDate and active=true and customer.id=" + cust.getId()+" "
											+ condition).setParameter("fromDate", fromDate)
											.setParameter("toDate", toDate).list();
					
					stkObj=null;
					purchase_amt=0;
					stockId=0;
					qty=0; saledValue=0; purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(cust.getName());
					
					Iterator it = list.iterator();
					while (it.hasNext()) {

						orderObj = (TailoringSalesModel) it.next();
						obj = null;
						Iterator itr3 = getSession()
								.createQuery(
										"from MaterialMappingModel where orderId=:ord")
								.setLong("ord", orderObj.getId())
								.list().iterator();

						while(itr3.hasNext()) {
							salObj=(MaterialMappingModel) itr3.next();

							rptModel.setId(salObj.getId());
							averageAmount = 0;

							Iterator it2 = salObj.getInventory_details_list()
									.iterator();
							while (it2.hasNext()) {
								invObj = (MaterialMappingInventoryDetailsModel) it2
										.next();

								purchase_amt = 0;
								count = 0;
								stockId = 0;
								qty = 0;
								if (invObj.getStock_ids().length() > 0) {
									stks = invObj.getStock_ids().toString()
											.split(",");
									for (int i = 0; i < stks.length; i++) {
										try {

											stockId = Long.parseLong(stks[i]
													.split(":")[0]);
											qty = Double.parseDouble(stks[i]
													.split(":")[1]);

											obj = getSession().createQuery(
													"from ItemStockModel where id="
															+ stockId)
													.uniqueResult();
											if (obj != null) {
												stkObj = (ItemStockModel) obj;

												purchase_amt += stkObj
														.getRate();
												count++;
											}

										} catch (Exception e) {
										}

									}
								}
								if (purchase_amt != 0)
									averageAmount += (purchase_amt / count)
											* invObj.getQuantity_in_basic_unit();
							}
							
							purchasedValue += averageAmount;

						}
						saledValue += orderObj.getAmount();
					}
					
					if(saledValue>0) {
						rptModel.setInwards(saledValue);
						rptModel.setOutwards(purchasedValue);
						rptModel.setProfit(saledValue-purchasedValue);
						
						resultList.add(rptModel);
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
		return resultList;
	}
	
}
