package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.PeriodicalAnalysisReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class PeriodicalAnalysisReportDao extends SHibernate implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.util.Calendar cal = Calendar.getInstance();

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<PeriodicalAnalysisReportBean> getPurchasePeriodicalAnalysisReport(
			long officeId, long itemId, Date fromDate, Date toDate, String currency)
			throws Exception {
		List<PeriodicalAnalysisReportBean> beanList = new ArrayList<PeriodicalAnalysisReportBean>();
		List<ItemModel> itemModelList = null;
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer
				.append(" FROM ItemModel  WHERE office.id = :officeId")
				.append(itemId != 0 ? " AND id = :itemId" : "")
				.append(" ORDER BY name");
		try {
			begin();
			Query query = getSession()
					.createQuery(queryStringBuffer.toString()).setLong(
							"officeId", officeId);
			if (itemId != 0) {
				query.setLong("itemId", itemId);
			}
			itemModelList = (List<ItemModel>) query.list();
			Iterator<ItemModel> itr = itemModelList.iterator();
			PeriodicalAnalysisReportBean bean = null;
			ItemModel model = null;

			toDate = getMonthEndDate(toDate);
			Date fromDateTemp = fromDate;
			int slNo = 0;
			double openingQty = 0;
			//	double issuedQty;
				double receivedQty;
				
				double openingReceiveQty =0;
			while (itr.hasNext()) {
				model = itr.next();
				fromDate = fromDateTemp;
				slNo++;
				openingReceiveQty =0;
				if(fromDate.compareTo(model.getOpening_stock_date())>0){					
					openingQty = model.getOpening_balance();
				} else {
					openingQty = 0;
					openingReceiveQty = model.getOpening_balance();
				}
				
				while (fromDate.compareTo(toDate) < 0) {
					
					
					
					bean = new PeriodicalAnalysisReportBean(model.getId(),
							model.getName());
					openingQty += getPurchaseOpeningQty(model.getId(),fromDate);
					
					receivedQty = getPurchasedQty(bean.getItemId(),
							fromDate, getMonthEndDate(fromDate));
					
					receivedQty += openingReceiveQty;
					openingReceiveQty = 0;
					
					
					bean.setSlNo(slNo);
					bean.setCurrency(currency);
					bean.setMonth(CommonUtil.getMonthName(fromDate.getMonth())
							.toUpperCase()
							+ " - "
							+ (1900 + fromDate.getYear()));
					bean.setOpening(openingQty);
					bean.setPurchaseOrSale(receivedQty);
					bean.setAmount(getPurchasedAmount(bean.getItemId(),
							fromDate, getMonthEndDate(fromDate)));
					fromDate = changeDateByMonthly(fromDate);

					beanList.add(bean);
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

		return beanList;
	}

	private double getPurchasedQty(long itemId, Date from_date, Date to_date)
			throws Exception {
		double purchasedQty = 0;
		Object obj = null;
		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0)" +

						" FROM PurchaseModel a JOIN a.purchase_details_list b "
								+

								" WHERE b.item.id = :item_id"
								+ " AND b.grn_id = 0"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			purchasedQty += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0)" +

						" FROM PurchaseGRNModel a JOIN a.grn_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			purchasedQty += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0)" +

						"  FROM SalesReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			purchasedQty += Double.parseDouble(obj.toString());
		}
		
		purchasedQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(a.quantity),0) FROM ItemStockModel a " +
				" WHERE a.item.id = :item_id" +
				" AND DATE(a.date_time) BETWEEN :from_date AND :to_date" +
				" AND a.purchase_type = :purchase_type")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date)
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).uniqueResult();	

		return purchasedQty;
	}

	private double getPurchasedAmount(long itemId, Date from_date, Date to_date)
			throws Exception {
		double purchasedAmount = 0;
		Object obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) * b.unit_price"
								+

								" FROM PurchaseModel a JOIN a.purchase_details_list b "
								+

								" WHERE b.item.id = :item_id"
								+ " AND b.grn_id = 0"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			purchasedAmount += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) * b.unit_price"
								+

								" FROM PurchaseGRNModel a JOIN a.grn_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			purchasedAmount += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) * b.unit_price"
								+

								"  FROM SalesReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			purchasedAmount += Double.parseDouble(obj.toString());
		}
		
		purchasedAmount += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(a.quantity * rate),0) FROM ItemStockModel a " +
				" WHERE a.item.id = :item_id" +
				" AND DATE(a.date_time) BETWEEN :from_date AND :to_date" +
				" AND a.purchase_type = :purchase_type")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date)
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).uniqueResult();

		return purchasedAmount;
		// return null;
	}

	@SuppressWarnings("static-access")
	private Date changeDateByMonthly(Date date) {
		cal.setTime(date);
		cal.set(cal.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		return CommonUtil.getSQLDateFromUtilDate(cal.getTime());

	}

	@SuppressWarnings("static-access")
	private Date getMonthEndDate(Date date) {
		cal.setTime(date);
		cal.set(cal.DAY_OF_MONTH,
				cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		return CommonUtil.getSQLDateFromUtilDate(cal.getTime());
	}
	private double getPurchaseOpeningQty(long itemId, Date fromDate) throws Exception {
		double opening_bal = 0;
		
		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND b.grn_id = 0"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();
		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();

		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(a.quantity),0) FROM ItemStockModel a " +
				" WHERE a.item.id = :item_id" +
				" AND DATE(a.date_time) < :from_date" +
				" AND a.purchase_type = :purchase_type")
				.setLong("item_id", itemId)
				.setDate("from_date", fromDate)				
				.setInteger("purchase_type", SConstants.stockPurchaseType.STOCK_TRANSFER).uniqueResult();	
		
		
		
		// ==================================================================================================
		// double saledQty = 0;
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesModel a JOIN a.inventory_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND b.delivery_id = 0"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
//
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseReturnModel a JOIN a.inventory_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
		return opening_bal;
	}
	private double getSalesOpeningQty(long itemId, Date fromDate) throws Exception {
		double opening_bal = 0;
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND b.grn_id = 0"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
//
//		opening_bal += (Double) getSession()
//				.createQuery(
//						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesReturnModel a JOIN a.inventory_details_list b "
//								+ " WHERE b.item.id = :item_id"
//								+ " AND a.date < :from_date")
//				.setLong("item_id", itemId).setDate("from_date", fromDate)
//				.uniqueResult();
		// ==================================================================================================
		// double saledQty = 0;
		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND b.delivery_id = 0"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();
		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();

		opening_bal += (Double) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date < :from_date")
				.setLong("item_id", itemId).setDate("from_date", fromDate)
				.uniqueResult();
		
		opening_bal += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.id = :item_id" +
				" AND a.transfer_date < :from_date")
				.setLong("item_id", itemId)
				.setDate("from_date", fromDate).uniqueResult();	
		return opening_bal;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<PeriodicalAnalysisReportBean> getSalesPeriodicalAnalysisReport(
			long officeId, long itemId, Date fromDate, Date toDate,String currency)
			throws Exception {
		List<PeriodicalAnalysisReportBean> beanList = new ArrayList<PeriodicalAnalysisReportBean>();
		List<ItemModel> itemModelList = null;
		StringBuffer queryStringBuffer = new StringBuffer();
		queryStringBuffer
				.append(" FROM ItemModel  WHERE office.id = :officeId")
				.append(itemId != 0 ? " AND id = :itemId" : "")
				.append(" ORDER BY name");
		try {
			begin();
			Query query = getSession()
					.createQuery(queryStringBuffer.toString()).setLong(
							"officeId", officeId);
			if (itemId != 0) {
				query.setLong("itemId", itemId);
			}
			itemModelList = (List<ItemModel>) query.list();
			Iterator<ItemModel> itr = itemModelList.iterator();
			PeriodicalAnalysisReportBean bean = null;
			ItemModel model = null;

			toDate = getMonthEndDate(toDate);
			Date fromDateTemp = fromDate;
			double openingStock = 0;
			int slNo = 0;
			while (itr.hasNext()) {
				model = itr.next();
				fromDate = fromDateTemp;
				slNo++;
				if(fromDate.compareTo(model.getOpening_stock_date()) > 0){
					openingStock = model.getOpening_balance();
				} else {
					openingStock = 0;
				}
				while (fromDate.compareTo(toDate) < 0) {
					
					bean = new PeriodicalAnalysisReportBean(model.getId(),
							model.getName());
					bean.setCurrency(currency);
					bean.setSlNo(slNo);
					bean.setMonth(CommonUtil.getMonthName(fromDate.getMonth())
							.toUpperCase()
							+ " - "
							+ (1900 + fromDate.getYear()));
					bean.setOpening(openingStock+getSalesOpeningQty(bean.getItemId(), fromDate));
					bean.setPurchaseOrSale(getSaledQty(bean.getItemId(),
							fromDate, getMonthEndDate(fromDate)));
					bean.setAmount(getSaledAmount(bean.getItemId(),
							fromDate, getMonthEndDate(fromDate)));
					fromDate = changeDateByMonthly(fromDate);

					beanList.add(bean);
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

		return beanList;
	}

	private double getSaledQty(long itemId, Date from_date, Date to_date)
			throws Exception {
		double saledQty = 0;
		Object obj = null;
		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0.0)" +

						" FROM SalesModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND b.delivery_id = 0"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			saledQty += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0.0)" +

						" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			saledQty += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0.0)" +

						" FROM PurchaseReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			saledQty += Double.parseDouble(obj.toString());
		}
		
		saledQty += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.id = :item_id" +
				" AND a.transfer_date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		System.out.println("====== SALED QTY ======= " + saledQty);

		return saledQty;
		// return null;
	}

	private double getSaledAmount(long itemId, Date from_date, Date to_date)
			throws Exception {
		double saledAmount = 0;
		Object obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) * b.unit_price"
								+

								" FROM SalesModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND b.delivery_id = 0"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			saledAmount += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) * b.unit_price"
								+

								" FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();
		if (obj != null) {
			saledAmount += Double.parseDouble(obj.toString());
		}

		obj = (Object) getSession()
				.createQuery(
						"SELECT COALESCE(SUM(b.qty_in_basic_unit),0) * b.unit_price"
								+

								" FROM PurchaseReturnModel a JOIN a.inventory_details_list b "
								+ " WHERE b.item.id = :item_id"
								+ " AND a.date BETWEEN :from_date AND :to_date"
								+ " group by b.item.id")
				.setLong("item_id", itemId).setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();

		if (obj != null) {
			saledAmount += Double.parseDouble(obj.toString());
		}
		
		saledAmount += (Double) getSession()
				.createQuery("SELECT COALESCE(SUM(b.quantity_in_basic_unit * b.stock_id.rate),0)" +
						" FROM StockTransferModel a JOIN a.inventory_details_list b " +
				" WHERE b.stock_id.item.id = :item_id" +
				" AND a.transfer_date BETWEEN :from_date AND :to_date")
				.setLong("item_id", itemId)
				.setDate("from_date", from_date)
				.setDate("to_date", to_date).uniqueResult();	
		System.out.println("====== PURCHASE QTY ======= " + saledAmount);

		return saledAmount;
		// return null;
	}
}
