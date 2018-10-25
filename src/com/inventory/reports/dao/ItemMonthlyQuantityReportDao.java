package com.inventory.reports.dao;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.model.ItemModel;
import com.webspark.bean.ReportBean;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 4, 2014
 */
public class ItemMonthlyQuantityReportDao extends SHibernate{

	public List getItemMonthlyQuantityReport(long officeId, long itemId,long subgroupId, long groupId,Date fromDate,Date toDate) throws Exception {
		List list = null;
		List reportList = new ArrayList();
		DecimalFormat twoDForm = new DecimalFormat("#.00");
		try {
			begin();
			
			if (itemId != 0) {
				list = getSession().createQuery("from ItemModel where id=:id")
						.setParameter("id", itemId).list();
			} else if (itemId == 0 && subgroupId != 0) {
				list = getSession()
						.createQuery(
								"from ItemModel where sub_group.id=:id and office.id=:ofc order by name")
						.setParameter("id", subgroupId)
						.setParameter("ofc", officeId).list();
			} else if (itemId == 0 && subgroupId == 0 && groupId != 0) {
				list = getSession()
						.createQuery(
								"from ItemModel where sub_group.group.id=:id and office.id=:ofc order by name")
						.setParameter("id", groupId)
						.setParameter("ofc", officeId).list();
			} else {
				list = getSession()
						.createQuery("from ItemModel where office.id=:ofc order by name")
						.setParameter("ofc", officeId).list();
			}
			

			if(list!=null&&list.size()>0){
				ItemModel itemModel=null;
				ReportBean reportBean=null;
				double purchaseQty=0,purchaseRtnQty=0,saleQty=0,saleRtnQty=0;
				Object poObj,poRtnObj,salObj,salRtnObj;
				Iterator iter=list.iterator();
				while (iter.hasNext()) {
					itemModel= (ItemModel) iter.next();
					
					purchaseQty=0;
					purchaseRtnQty=0;
					saleQty=0;
					saleRtnQty=0;
					
					poObj =  getSession()
							.createQuery(
									"select SUM(b.qty_in_basic_unit) from PurchaseModel a join a.purchase_details_list b" +
									" where b.item.id=:item and date between :from and :to and a.office.id=:ofc and a.active=true").setParameter("item", itemModel.getId())
									.setParameter("ofc", officeId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();

					poRtnObj =  getSession()
							.createQuery(
									"select SUM(b.qty_in_basic_unit) from PurchaseReturnModel a join a.inventory_details_list b" +
									" where b.item.id=:item and date between :from and :to and a.office.id=:ofc and a.active=true").setParameter("item", itemModel.getId())
									.setParameter("ofc", officeId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();
					
					salObj =  getSession()
							.createQuery(
									"select SUM(b.quantity_in_basic_unit) from SalesModel a join a.inventory_details_list b" +
									" where b.item.id=:item and date between :from and :to and a.office.id=:ofc and a.active=true").setParameter("item", itemModel.getId())
									.setParameter("ofc", officeId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();
					
					salRtnObj =  getSession()
							.createQuery(
									"select SUM(b.quantity_in_basic_unit) from SalesReturnModel a join a.inventory_details_list b" +
									" where b.item.id=:item and date between :from and :to and a.office.id=:ofc and a.active=true").setParameter("item", itemModel.getId())
									.setParameter("ofc", officeId).setParameter("from", fromDate).setParameter("to", toDate).uniqueResult();
					
					if(poObj!=null)
						purchaseQty=(Double) poObj;
					
					if(poRtnObj!=null)
						purchaseRtnQty=(Double) poRtnObj;
					
					if(salObj!=null)
						saleQty=(Double) salObj;
					
					if(salRtnObj!=null)
						saleRtnQty=(Double) saleRtnQty;
					
					if(purchaseQty!=0||purchaseRtnQty!=0||saleQty!=0||saleRtnQty!=0){
						reportBean = new ReportBean(itemModel.getName(),
								Double.parseDouble((twoDForm.format(purchaseQty))),
								Double.parseDouble((twoDForm.format(purchaseRtnQty))),
								Double.parseDouble((twoDForm.format(saleQty))) ,
								Double.parseDouble((twoDForm.format(saleRtnQty))));
						reportBean.setUnit(itemModel.getUnit().getSymbol());
						reportList.add(reportBean);
					}
					
				}
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} 
		flush();
		close();

		return reportList;
		
	}
}
