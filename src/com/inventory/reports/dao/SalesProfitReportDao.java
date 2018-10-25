package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Nov 22, 2013
 */
public class SalesProfitReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1071364669984964064L;

	CommonMethodsDao comDao = new CommonMethodsDao();

	public List<Object> getSalesProfitDetails(long salesNo, long custId,
			Date fromDate, Date toDate, long officeId, int profitCalculationType) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		ItemStockModel stkObj=null;
//		UnitModel unitObj=null;
		try {
			begin();

			String condition = "";
			if (salesNo != 0) {
				condition += " and id =" + salesNo;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			List list = getSession()
					.createQuery(
							"from SalesModel where date>=:fromDate and date<=:toDate and active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			
			int count=0;
			ReportBean rptModel;
			SalesModel salObj;
			SalesInventoryDetailsModel invObj;
			Object obj;
			double outward_amt=0;
			Query stockQuery = null;
//			if(profitCalculationType == SConstants.profitCalcutaion.AVERAGE){
				stockQuery = getSession().createQuery("SELECT COALESCE(rate,0) FROM ItemStockModel " +
						" WHERE DATE(date_time) = :sale_date" +
						" AND item.id = :item_id" +
						" AND quantity != 0 and id=:id");
////			} else if (profitCalculationType == SConstants.profitCalcutaion.FIFO){				
////				stockQuery = getSession().createQuery("SELECT COALESCE(SUM(rate),0)/COUNT(id) FROM ItemStockModel stk" +
////							" WHERE DATE(date_time) = :sale_date" +
////							" AND item.id = :item_id" +
////							" AND id = (SELECT MIN(stk1.id)" +
////												" FROM ItemStockModel stk1" +
////												" WHERE stk1.item.id = stk.item.id" +
////												" AND DATE(date_time) = :sale_date" +
////												" AND stk1.quantity != 0)" +
////							" AND quantity != 0");				
////			} else if (profitCalculationType == SConstants.profitCalcutaion.LIFO){				
//				stockQuery = getSession().createQuery("SELECT COALESCE(SUM(rate),0)/COUNT(id) FROM ItemStockModel stk" +
//						" WHERE DATE(date_time) = :sale_date" +
//						" AND item.id = :item_id" +
//						" AND id = (SELECT MAX(stk1.id)" +
//											" FROM ItemStockModel stk1" +
//											" WHERE stk1.item.id = stk.item.id" +
//											" AND DATE(date_time) = :sale_date" +
//											" AND stk1.quantity != 0)" +
//						" AND quantity != 0");				
//		}
			
			
//			IN (SELECT b.item.id FROM SalesModel a JOIN a.inventory_details_list b" +
//					" WHERE a.id = :sales_id)
			
			
			Iterator it=list.iterator();
			while (it.hasNext()) {
				rptModel=new ReportBean();
				salObj=(SalesModel) it.next();
				rptModel.setId(salObj.getId());
				rptModel.setClient_name(salObj.getCustomer().getName());
				rptModel.setDate(CommonUtil.formatSQLDateToDDMMMYYYY(salObj.getDate()));
				rptModel.setInwards(salObj.getAmount());
				rptModel.setParticulars(String.valueOf(salObj.getSales_number()));
				
				outward_amt=0;
				Iterator it2=salObj.getInventory_details_list().iterator();
				while (it2.hasNext()) {
					count = 0;
					invObj=(SalesInventoryDetailsModel) it2.next();
					obj = stockQuery
							.setDate("sale_date", salObj.getDate())
							.setLong("item_id", invObj.getItem().getId())						
							.setLong("id", invObj.getStock_id())						
							.uniqueResult();
					if (obj != null) {
						outward_amt += invObj.getQuantity_in_basic_unit() * Double.parseDouble(obj.toString());
					}
				}
				
				
				
				
//				Iterator it2=salObj.getInventory_details_list().iterator();
//				while (it2.hasNext()) {
//					count = 0;
//					invObj=(SalesInventoryDetailsModel) it2.next();
//					purchase_amt=0;
//					obj = stockQuery
//							.setDate(arg0, arg1)
//							
//							.uniqueResult();
//					if (obj != null) {
//						stkObj=(ItemStockModel) obj;
//						
//							purchase_amt += stkObj.getRate();
//						count++;
//					}
//					if(invObj.getStock_ids().length()>0) {
//						stks=invObj.getStock_ids().toString().split(",");
//						for (int i = 0; i < stks.length; i++) {
//							stockId=Long.parseLong(stks[i].split(":")[0]);
//							qty=Double.parseDouble(stks[i].split(":")[1]);
//							
//							obj = getSession().createQuery("from ItemStockModel where id="
//									+ stockId).uniqueResult();
//							if (obj != null) {
//								stkObj=(ItemStockModel) obj;
//								
//									purchase_amt += stkObj.getRate();
//								count++;
//							}
//						}
//						if(purchase_amt!=0)
//							purchase_amt = purchase_amt*invObj.getQuantity_in_basic_unit()/count;
//						outward_amt+=purchase_amt;
//					}
		//		}
				rptModel.setOutwards(outward_amt);
				rptModel.setProfit(rptModel.getInwards()-outward_amt);
				
				
				resultList.add(rptModel);
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

	// Added By Jinshad @ Nov 22 2013

	public List getAllSalesNumbersAsCommentWithInDates(long officeId,
			Date fromDate, Date toDate) throws Exception {
		List resultList = null;
		try {
			begin();
			String condition = "";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel where date between :fromDate and :toDate"
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			resultList = new ArrayList();
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

}
