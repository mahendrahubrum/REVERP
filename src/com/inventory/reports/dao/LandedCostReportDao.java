package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.LandedCostReportBean;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

public class LandedCostReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<LandedCostReportBean> getPurchaseLandedCostReportDetails(
			long officeId, long itemId, long supplierId, long purchaseId,
			Date fromDate, Date toDate, String currency) throws Exception{
		List<LandedCostReportBean> reportList = new ArrayList<LandedCostReportBean>();
		List<PurchaseModel> purchaseModelList = new ArrayList<PurchaseModel>();
		try {
			begin();
			StringBuffer queryBuffer = new StringBuffer();
//			queryBuffer
//					.append("SELECT new com.inventory.reports.bean.LandedCostReportBean(a.purchase_no,"
//							+ "cast(a.date as string),"
//							+ "a.supplier.name,"
//							+ "b.item.name,"
//							+ "b.qty_in_basic_unit,"
//							+ "b.unit.symbol,"
//							+ "b.unit_price,"
//							+ "b.qty_in_basic_unit * b.unit_price,"
//							+ "(b.qty_in_basic_unit * b.unit_price) - a.expenseAmount + a.expenseCreditAmount)"
//							+ " FROM PurchaseModel a JOIN a.purchase_details_list b "
//							+ " WHERE a.office.id = :office_id"
//							+ " AND a.date BETWEEN :from_date AND :to_date")
//					.append(itemId != 0 ? " AND b.item.id = :item_id" : "")
//					.append(supplierId != 0 ? " AND a.supplier.id = :supplier_id"
//							: " ")
//					.append(purchaseId != 0 ? " AND a.id = :purchase_id" : " ")
//					.append(" ORDER BY a.date");
//			
			queryBuffer
			.append("SELECT a FROM PurchaseModel a JOIN a.purchase_details_list b "
					+ " WHERE a.office.id = :office_id"
					+ " AND a.date BETWEEN :from_date AND :to_date")
			.append(itemId != 0 ? " AND b.item.id = :item_id" : "")
			.append(supplierId != 0 ? " AND a.supplier.id = :supplier_id"
					: " ")
			.append(purchaseId != 0 ? " AND a.id = :purchase_id" : " ")
			.append(" ORDER BY a.date");
	
			Query query = getSession().createQuery(queryBuffer.toString())
					.setLong("office_id", officeId)
					.setDate("from_date", fromDate)
					.setDate("to_date", toDate);
			if (itemId != 0) {
				query.setLong("item_id", itemId);
			}
			if (supplierId != 0) {
				query.setLong("supplier_id", supplierId);
			}
			if (purchaseId != 0) {
				query.setLong("purchase_id", purchaseId);
			}
			purchaseModelList.addAll(query.list());
			LandedCostReportBean bean = null;
			PurchaseInventoryDetailsModel purchaseItemList = null;
			long currId , prevId = 0; 
			for(PurchaseModel model : purchaseModelList){
				bean = new LandedCostReportBean();
				currId = model.getId();
				purchaseItemList = model.getPurchase_details_list().get(0);
				/*if(currId != prevId){
					bean.setBillNo(model.getPurchase_no());
					bean.setDate(CommonUtil.formatDateToDDMMYYYY(model.getDate()));
					bean.setTag(model.getSupplier().getName());						
				} else {
					bean.setBillNo("");
					bean.setDate("");
					bean.setTag("");			
				}*/
				bean.setBillNo(model.getPurchase_no());
				bean.setCurrency(currency);
				bean.setDate(CommonUtil.formatDateToDDMMYYYY(model.getDate()));
				bean.setTag(model.getSupplier().getName());			
				bean.setItem(purchaseItemList.getItem().getName());
				bean.setQuantity(purchaseItemList.getQunatity());
				bean.setUnit(purchaseItemList.getUnit().getSymbol());
				bean.setUnitPrice(purchaseItemList.getUnit_price());
				bean.setAmount(purchaseItemList.getQunatity() * purchaseItemList.getUnit_price());
				bean.setLandedCost(bean.getAmount() - model.getExpenseAmount() + model.getExpenseCreditAmount());
				
				reportList.add(bean);
				
				prevId = currId;
				
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
		return reportList;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<LandedCostReportBean> getSalesLandedCostReportDetails(
			long officeId, long itemId, long customerId, long salesId,
			Date fromDate, Date toDate,String currency) throws Exception{
		List<LandedCostReportBean> reportList = new ArrayList<LandedCostReportBean>();
		List<SalesModel> salesModelList = new ArrayList<SalesModel>();
		try {
			begin();
			StringBuffer queryBuffer = new StringBuffer();
//			queryBuffer
//					.append("SELECT new com.inventory.reports.bean.LandedCostReportBean(a.purchase_no,"
//							+ "cast(a.date as string),"
//							+ "a.supplier.name,"
//							+ "b.item.name,"
//							+ "b.qty_in_basic_unit,"
//							+ "b.unit.symbol,"
//							+ "b.unit_price,"
//							+ "b.qty_in_basic_unit * b.unit_price,"
//							+ "(b.qty_in_basic_unit * b.unit_price) - a.expenseAmount + a.expenseCreditAmount)"
//							+ " FROM PurchaseModel a JOIN a.purchase_details_list b "
//							+ " WHERE a.office.id = :office_id"
//							+ " AND a.date BETWEEN :from_date AND :to_date")
//					.append(itemId != 0 ? " AND b.item.id = :item_id" : "")
//					.append(supplierId != 0 ? " AND a.supplier.id = :supplier_id"
//							: " ")
//					.append(purchaseId != 0 ? " AND a.id = :purchase_id" : " ")
//					.append(" ORDER BY a.date");
//			
			queryBuffer
			.append("SELECT a FROM SalesModel a JOIN a.inventory_details_list b "
					+ " WHERE a.office.id = :office_id"
					+ " AND a.date BETWEEN :from_date AND :to_date")
			.append(itemId != 0 ? " AND b.item.id = :item_id" : "")
			.append(customerId != 0 ? " AND a.customer.id = :customer_id"
					: " ")
			.append(salesId != 0 ? " AND a.id = :sales_id" : " ")
			.append(" ORDER BY a.date");
	
			Query query = getSession().createQuery(queryBuffer.toString())
					.setLong("office_id", officeId)
					.setDate("from_date", fromDate)
					.setDate("to_date", toDate);
			if (itemId != 0) {
				query.setLong("item_id", itemId);
			}
			if (customerId != 0) {
				query.setLong("customer_id", customerId);
			}
			if (salesId != 0) {
				query.setLong("sales_id", salesId);
			}
			salesModelList.addAll(query.list());
			LandedCostReportBean bean = null;
			SalesInventoryDetailsModel salesItemList = null;
			long currId , prevId = 0; 
			for(SalesModel model : salesModelList){
				bean = new LandedCostReportBean();
				currId = model.getId();
				salesItemList = model.getInventory_details_list().get(0);
				/*if(currId != prevId){
					bean.setBillNo(model.getPurchase_no());
					bean.setDate(CommonUtil.formatDateToDDMMYYYY(model.getDate()));
					bean.setTag(model.getSupplier().getName());						
				} else {
					bean.setBillNo("");
					bean.setDate("");
					bean.setTag("");			
				}*/
				bean.setBillNo(model.getSales_number());
				bean.setCurrency(currency);
				bean.setDate(CommonUtil.formatDateToDDMMYYYY(model.getDate()));
				bean.setTag(model.getCustomer().getName());			
				bean.setItem(salesItemList.getItem().getName());
				bean.setQuantity(salesItemList.getQunatity());
				bean.setUnit(salesItemList.getUnit().getSymbol());
				bean.setUnitPrice(salesItemList.getUnit_price());
				bean.setAmount(salesItemList.getQunatity() * salesItemList.getUnit_price());
				bean.setLandedCost(bean.getAmount() - model.getExpenseAmount() + model.getExpenseCreditAmount());
				
				reportList.add(bean);
				
				prevId = currId;
				
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
		return reportList;
	}

}
