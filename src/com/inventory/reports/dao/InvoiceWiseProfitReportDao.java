package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.bean.ReportBean;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 4, 2014
 */
public class InvoiceWiseProfitReportDao extends SHibernate {

	CommonMethodsDao comDao = new CommonMethodsDao();

	public List getAllActiveItemsUnderSaleNumber(long salesId) throws Exception {
		List resultList;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select distinct b.item from SalesModel a join a.inventory_details_list b where a.id=:id order by b.item.name")
					.setParameter("id", salesId).list();
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
		return resultList;
	}

	public List<Object> getInvoiceWiseProfitReport(long itmId, long salesID,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		ItemStockModel stkObj = null;
		// UnitModel unitObj=null;
		try {
			begin();

			String condition = "";
			if (salesID != 0) {
				condition += " and a.id=" + salesID;
			}
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			if (itmId != 0) {
				condition += " and b.item.id=" + itmId;
			}

			ReportBean rptModel;
			SalesModel salObj;
			double purchase_amt = 0;
			SalesInventoryDetailsModel invObj;
			String[] stks;
			long stockId;
			double quantity = 0;
			double qty = 0, saledValue = 0, purchasedValue = 0, profit = 0;
			int count = 0;
			Object obj;
			Iterator mainIter;
			Iterator it;

			List list = getSession()
					.createQuery(
							"select distinct a from SalesModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and active=true"
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();

			mainIter = list.iterator();

			while (mainIter.hasNext()) {
				salObj = (SalesModel) mainIter.next();
				rptModel = new ReportBean();
				rptModel.setId(salObj.getId());
				rptModel.setClient_name(salObj.getCustomer().getName());
				rptModel.setEmployee(salObj.getSales_number() + "");
				it = salObj.getInventory_details_list().iterator();
				saledValue = 0;
				purchasedValue = 0;
				profit = 0;
				double avg_rate=0;
				while (it.hasNext()) {
					invObj = (SalesInventoryDetailsModel) it.next();
					stockId = 0;
					qty = 0;
					purchase_amt = 0;
					count = 0;

					if (itmId != 0) {

						if (invObj.getItem().getId() == itmId) {
							avg_rate=0;
							quantity = invObj.getQunatity();
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

											purchase_amt += stkObj.getRate();
											count++;
										}

									} catch (Exception e) {
									}
								}
								
								avg_rate=purchase_amt/count;
								
							}

							saledValue += (invObj.getQunatity() * invObj
									.getUnit_price());
							if (purchase_amt != 0)
								purchasedValue +=avg_rate
										* invObj.getQuantity_in_basic_unit();
						}
					} else {
						avg_rate=0;
						quantity = invObj.getQunatity();
						if (invObj.getStock_ids().length() > 0) {
							stks = invObj.getStock_ids().toString().split(",");
							for (int i = 0; i < stks.length; i++) {
								try {

									stockId = Long
											.parseLong(stks[i].split(":")[0]);
									qty = Double
											.parseDouble(stks[i].split(":")[1]);

									obj = getSession().createQuery(
											"from ItemStockModel where id="
													+ stockId).uniqueResult();
									if (obj != null) {
										stkObj = (ItemStockModel) obj;
										purchase_amt += stkObj.getRate();
										count++;
									}

								} catch (Exception e) {
								}
							}
							
							avg_rate=purchase_amt / count;
						}

						saledValue += (invObj.getQunatity() * invObj
								.getUnit_price());
						if (purchase_amt != 0)
							purchasedValue += avg_rate
									* invObj.getQuantity_in_basic_unit();
					}
				}
				if (saledValue > 0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue - purchasedValue);

					resultList.add(rptModel);
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

	public List getAllSalesNumbersAsComment(Long officeId, Date fromDate,
			Date toDate) throws Exception {
		List resultList = null;
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where office.id=:ofc and type<2  and date between :frm and :to and active=true order by sales_number desc")
					.setParameter("ofc", officeId)
					.setParameter("frm", fromDate).setParameter("to", toDate)
					.list();
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
		return resultList;
	}

}
