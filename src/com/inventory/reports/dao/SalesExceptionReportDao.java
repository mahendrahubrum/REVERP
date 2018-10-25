package com.inventory.reports.dao;

/**
 *
 */
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import org.hibernate.Query;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.ExceptionReportBean;
import com.inventory.reports.ui.PurchaseExceptionReportUI;
import com.inventory.reports.ui.SalesExceptionReportUI;
import com.webspark.dao.SHibernate;

public class SalesExceptionReportDao extends SHibernate implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<ExceptionReportBean> getSalesExceptionReport(
			long office_id, long item_id, Date from_date, Date to_date,
			int report_type) throws Exception {

		List<ItemModel> itemModelList = null;
		List<ExceptionReportBean> salesExceptionReportBeanList = null;
		try {
			begin();
			StringBuffer queryBuffer = new StringBuffer();
			queryBuffer.append(
					"SELECT id FROM ItemModel  WHERE office.id = :office_id")
					.append((item_id != 0) ? " AND id = :item_id" : "");
			Query query = getSession().createQuery(queryBuffer.toString())
					.setLong("office_id", office_id);
			if (item_id != 0) {
				query.setLong("item_id", item_id);
			}

			itemModelList = query.list();
			/*
			 * for(ItemModel m : itemModelList){
			 * System.out.println("==== "+m.getId()+"========="+m.getName()); }
			 */
			// purchaseReturnReportBeanList = new
			// ArrayList<PurchaseExceptionReportBean>();
			if (report_type == SalesExceptionReportUI.SALES_ORDER_AND_DELIVERY_NOTE) {
				// System.out.println("========LIST ============= "+itemModelList
				salesExceptionReportBeanList = getSession()
						.createQuery(
								"SELECT new com.inventory.reports.bean.ExceptionReportBean"
										+ "(so_list.item.name,"
										+ "so.customer.name,"
										+

										"so.order_no,"
										+ "cast(so.date as string),"
										+ "so_list.qty_in_basic_unit,"
										+

										"dn.deliveryNo,"
										+ "cast(dn.date as string),"
										+ "dn_list.qty_in_basic_unit)"
										+

										" FROM SalesOrderModel so JOIN so.order_details_list so_list , DeliveryNoteModel dn JOIN dn.delivery_note_details_list dn_list"
										+

										" WHERE so_list.item.id IN( :item_id)"
										+ " AND dn_list.item.id = so_list.item.id"
										+ " AND dn_list.order_id = so.id"
										+ " AND so.date BETWEEN :from_date AND :to_date")
						.setParameterList("item_id", itemModelList)
						.setDate("from_date", from_date)
						.setDate("to_date", to_date).list();
			} else if (report_type == SalesExceptionReportUI.SALES_ORDER_AND_SALES) {
				salesExceptionReportBeanList = getSession()
						.createQuery(
								"SELECT new com.inventory.reports.bean.ExceptionReportBean"
										+ "(so_list.item.name,"
										+ "so.customer.name,"
										+

										"so.order_no,"
										+ "cast(so.date as string),"
										+ "so_list.qty_in_basic_unit,"
										+

										"s.sales_number,"
										+ "cast(s.date as string),"
										+ "s_list.quantity_in_basic_unit)"
										+

										" FROM SalesOrderModel so JOIN so.order_details_list so_list , SalesModel s JOIN s.inventory_details_list s_list"
										+

										" WHERE so_list.item.id IN( :item_id)"
										+ " AND s_list.item.id = so_list.item.id"
										+ " AND s_list.order_id = so.id"
										+ " AND so.date BETWEEN :from_date AND :to_date")
						.setParameterList("item_id", itemModelList)
						.setDate("from_date", from_date)
						.setDate("to_date", to_date).list();
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

		return salesExceptionReportBeanList;
	}
	/*
	 * private String getPreviousDate(Date from_date) { Calendar cal =
	 * Calendar.getInstance(); cal.setTime(from_date); cal.add(Calendar.DATE,
	 * -1); return CommonUtil.getSQLDateFromUtilDate(cal.getTime()).toString();
	 * }
	 * 
	 * private double getOpeningQty(long itemId, Date from_date,double
	 * opening_bal) throws Exception{ // double receivedQty = 0;
	 * 
	 * opening_bal += (Double) getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseModel a JOIN a.purchase_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND b.grn_id = 0" +
	 * " AND a.date < :from_date") .setLong("item_id", itemId)
	 * .setDate("from_date", from_date).uniqueResult(); opening_bal += (Double)
	 * getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseGRNModel a JOIN a.grn_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND a.date < :from_date")
	 * .setLong("item_id", itemId) .setDate("from_date",
	 * from_date).uniqueResult();
	 * 
	 * opening_bal += (Double) getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesReturnModel a JOIN a.inventory_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND a.date < :from_date")
	 * .setLong("item_id", itemId) .setDate("from_date",
	 * from_date).uniqueResult();
	 * //=============================================
	 * ===================================================== // double saledQty
	 * = 0; opening_bal -= (Double) getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.quantity_in_basic_unit),0) FROM SalesModel a JOIN a.inventory_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND b.delivery_id = 0" +
	 * " AND a.date < :from_date") .setLong("item_id", itemId)
	 * .setDate("from_date", from_date).uniqueResult(); opening_bal -= (Double)
	 * getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND a.date < :from_date")
	 * .setLong("item_id", itemId) .setDate("from_date",
	 * from_date).uniqueResult();
	 * 
	 * opening_bal -= (Double) getSession() .createQuery(
	 * "SELECT COALESCE(SUM(b.qty_in_basic_unit),0) FROM PurchaseReturnModel a JOIN a.inventory_details_list b "
	 * + " WHERE b.item.id = :item_id" + " AND a.date < :from_date")
	 * .setLong("item_id", itemId) .setDate("from_date",
	 * from_date).uniqueResult(); return opening_bal; }
	 * 
	 * @SuppressWarnings("unchecked") private List<StockLedgerBean>
	 * getIssuedList(long itemId,Date from_date,Date to_date) throws Exception{
	 * 
	 * List<StockLedgerBean> stockLedgerList = (List<StockLedgerBean>)
	 * getSession() .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.customer.name," + "CONCAT( 'Sales (' ,a.sales_number, ')' )," +
	 * "0.0," + // "0.0)" + "COALESCE(SUM(b.quantity_in_basic_unit),0.0))" +
	 * 
	 * " FROM SalesModel a JOIN a.inventory_details_list b " +
	 * " WHERE b.item.id = :item_id" + " AND b.delivery_id = 0" +
	 * " AND a.date BETWEEN :from_date AND :to_date" + " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list(); stockLedgerList.addAll(getSession()
	 * .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.customer.name," + "CONCAT( 'Delivery Note (' , a.deliveryNo, ')' ),"
	 * + "0.0," + // "0.0)" + "COALESCE(SUM(b.qty_in_basic_unit),0))" +
	 * 
	 * " FROM DeliveryNoteModel a JOIN a.delivery_note_details_list b " +
	 * " WHERE b.item.id = :item_id" +
	 * " AND a.date BETWEEN :from_date AND :to_date" + " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list());
	 * 
	 * stockLedgerList.addAll(getSession() .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.supplier.name," + " CONCAT('Purchase Return (', a.return_no, ')' ),"
	 * + "0.0," + //"0.0)" + "COALESCE(SUM(b.qty_in_basic_unit),0))" +
	 * 
	 * " FROM PurchaseReturnModel a JOIN a.inventory_details_list b " +
	 * " WHERE b.item.id = :item_id" +
	 * " AND a.date BETWEEN :from_date AND :to_date" + " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list());
	 * System.out.println("Sales List Count ===========  "
	 * +stockLedgerList.size());
	 * 
	 * return stockLedgerList; }
	 * 
	 * @SuppressWarnings("unchecked") private List<StockLedgerBean>
	 * getReceivedList(long itemId,Date from_date,Date to_date) throws
	 * Exception{
	 * 
	 * List<StockLedgerBean> stockLedgerList = (List<StockLedgerBean>)
	 * getSession() .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.supplier.name," + "CONCAT( 'Purchase (',  a.purchase_no , ')' )," +
	 * "COALESCE(SUM(b.qty_in_basic_unit),0)," + "0.0)" +
	 * " FROM PurchaseModel a JOIN a.purchase_details_list b " +
	 * " WHERE b.item.id = :item_id" + " AND b.grn_id = 0" +
	 * " AND a.date BETWEEN :from_date AND :to_date" + " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list();
	 * 
	 * stockLedgerList.addAll(getSession() .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.supplier.name," + "CONCAT('Purchase GRN (' , a.grn_no , ')')," +
	 * "COALESCE(SUM(b.qty_in_basic_unit),0)," + "0.0)" +
	 * " FROM PurchaseGRNModel a JOIN a.grn_details_list b " +
	 * " WHERE b.item.id = :item_id" +
	 * " AND a.date BETWEEN :from_date AND :to_date" + " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list());
	 * 
	 * 
	 * stockLedgerList.addAll(getSession() .createQuery(
	 * "SELECT new com.inventory.reports.bean.StockLedgerBean(cast(a.date as string),"
	 * + "a.customer.name," + " CONCAT('Sales Return (', a.return_no, ')')," +
	 * "COALESCE(SUM(b.quantity_in_basic_unit),0)," + "0.0)" +
	 * "  FROM SalesReturnModel a JOIN a.inventory_details_list b " +
	 * " WHERE b.item.id = :item_id" +
	 * " AND a.date BETWEEN :from_date AND :to_date"+ " group by b.item.id")
	 * .setLong("item_id", itemId) .setDate("from_date", from_date)
	 * .setDate("to_date", to_date).list());
	 * System.out.println("====== PURCHASE TOTAL ======= "
	 * +stockLedgerList.size());
	 * 
	 * return stockLedgerList; //return null; }
	 */

}
