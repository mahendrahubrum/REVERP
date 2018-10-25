package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.reports.bean.AegingReportBean;
import com.webspark.dao.SHibernate;

public class AegingReportDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	public List<Object> getPurchaseDetails(long supplierId,
			Date fromDate, Date toDate, long officeId,String condition1)
			throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = condition1;
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseModel where date BETWEEN :fromDate AND :toDate" +
							" AND payment_done ='N' "
									+ condition+
											" ORDER BY date")
								.setDate("fromDate", fromDate)
								.setDate("toDate", toDate).list();
			commit();

		} catch (Exception e) {
			list = new ArrayList<Object>();
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Object> getSalesDetails(long customerId,
			Date fromDate, Date toDate, long officeId,String condition1)
			throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = condition1;
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (customerId != 0) {
				condition += " and customer.id=" + customerId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from SalesModel where date BETWEEN :fromDate AND :toDate" +
							" AND payment_done ='N' "
									+ condition+
											" ORDER BY date")
								.setDate("fromDate", fromDate)
								.setDate("toDate", toDate).list();
			commit();

		} catch (Exception e) {
			list = new ArrayList<Object>();
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return list;
	}
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getBalanceAfterDateOfPurchase(long supplierId,
			Date fromDate, long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> list = new ArrayList<AegingReportBean>();

		try {
			begin();

			String condition = "";
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if(reportType == 1){
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(supplier.id, COALESCE(SUM(amount + debit_note" +
										" - paymentAmount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from PurchaseModel where date > :fromDate"+
								" AND payment_done ='N' "
										+ condition+
												" GROUP BY supplier.id")								
									.setDate("fromDate", fromDate).list();
			} else {
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(id, COALESCE(SUM(amount + debit_note" +
										" - paymentAmount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from PurchaseModel where date > :fromDate"
										+" AND payment_done ='N' "
										+ condition+
												" GROUP BY id")								
									.setDate("fromDate", fromDate).list();
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

		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getBalanceAfterDateOfSales(long customerId,
			Date fromDate, long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> list = new ArrayList<AegingReportBean>();

		try {
			begin();

			String condition = "";
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (customerId != 0) {
				condition += " and customer.id=" + customerId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if(reportType == 1){
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(customer.id, COALESCE(SUM(amount + debit_note" +
										" - payment_amount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from SalesModel where date > :fromDate"+
								" AND payment_done ='N' "
										+ condition+
												" GROUP BY customer.id")								
									.setDate("fromDate", fromDate).list();
			} else {
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(id, COALESCE(SUM(amount + debit_note" +
										" - payment_amount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from SalesModel where date > :fromDate"
										+" AND payment_done ='N' "
										+ condition+
												" GROUP BY id")								
									.setDate("fromDate", fromDate).list();
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

		return list;
	}
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getPurchaseOpeningBalance(long supplierId,
			Date fromDate, long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> list = new ArrayList<AegingReportBean>();

		try {
			begin();

			String condition = "";
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if(reportType == 1){
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(supplier.id, COALESCE(SUM(amount + debit_note" +
										" - paymentAmount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from PurchaseModel where date < :fromDate"+
								" AND payment_done ='N' "
										+ condition+
												" GROUP BY supplier.id")								
									.setDate("fromDate", fromDate).list();
			} else {
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(id, COALESCE(SUM(amount + debit_note" +
										" - paymentAmount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from PurchaseModel where date < :fromDate"
										+" AND payment_done ='N' "
										+ condition+
												" GROUP BY id")								
									.setDate("fromDate", fromDate).list();
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

		return list;
	}
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getSalesOpeningBalance(long customerId,
			Date fromDate, long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> list = new ArrayList<AegingReportBean>();

		try {
			begin();

			String condition = "";
			/*if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}*/
			if (customerId != 0) {
				condition += " and customer.id=" + customerId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if(reportType == 1){
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(customer.id, COALESCE(SUM(amount + debit_note" +
										" - payment_amount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from SalesModel where date < :fromDate"+
								" AND payment_done ='N' "
										+ condition+
												" GROUP BY customer.id")								
									.setDate("fromDate", fromDate).list();
			} else {
				list = getSession()
						.createQuery("SELECT new com.inventory.reports.bean.AegingReportBean(id, COALESCE(SUM(amount + debit_note" +
										" - payment_amount - credit_note - paid_by_payment - expenseCreditAmount),0))" +
								" from SalesModel where date < :fromDate"
										+" AND payment_done ='N' "
										+ condition+
												" GROUP BY id")								
									.setDate("fromDate", fromDate).list();
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

		return list;
	}
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getUnAdjustedAmountOfSupplier(long supplierLedgerId,
			Date fromDate, Date toDate,long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> unAdjustedAmountList = new ArrayList<AegingReportBean>();
		try{
			begin();		
			String queryString;
			if(reportType == 1){
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.account.id, COALESCE(SUM(b.amount),0))" +
						" FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.account.id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.account.id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.account.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.account.id, COALESCE(SUM(b.amount),0))" +
						" FROM CashAccountPaymentModel a JOIN a.cash_account_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.account.id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.account.id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.account.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.to_id, COALESCE(SUM(b.amount),0))" +
						" FROM PdcPaymentModel a JOIN a.pdc_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.to_id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.to_id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.from_id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId).list());
			} else {
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.account.id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.account.id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM CashAccountPaymentModel a JOIN a.cash_account_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.account.id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.account.id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM PdcPaymentModel a JOIN a.pdc_payment_list b, PurchaseModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.supplier.id = b.to_id" +
						" AND m.date = a.date";
				if(supplierLedgerId != 0){
					queryString +=" AND b.to_id = "+supplierLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId).list());
			}
			
			System.out.println("====unAdjustedAmountList.size ====  "+unAdjustedAmountList.size());
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		
		return unAdjustedAmountList;		
	}
	
	@SuppressWarnings("unchecked")
	public List<AegingReportBean> getUnAdjustedAmountOfCustomer(long customerLedgerId,
			Date fromDate, Date toDate,long officeId,int reportType)
			throws Exception {
		List<AegingReportBean> unAdjustedAmountList = new ArrayList<AegingReportBean>();
		try{
			begin();			
			String queryString;
			if(reportType == 1){
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.account.id, COALESCE(SUM(b.amount),0))" +
						" FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.account.id" +
						" AND m.date = a.date" ;
					
				if(customerLedgerId != 0){
					queryString +=" AND b.account.id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.account.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.account.id, COALESCE(SUM(b.amount),0))" +
						" FROM CashAccountDepositModel a JOIN a.cash_account_deposit_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.account.id" +
						" AND m.date = a.date" ;
				if(customerLedgerId != 0){
					queryString +=" AND b.account.id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.account.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(b.from_id, COALESCE(SUM(b.amount),0))" +
						" FROM PdcPaymentModel a JOIN a.pdc_payment_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.from_id" +
						" AND m.date = a.date" ;
				if(customerLedgerId != 0){
					queryString +=" AND b.from_id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY b.from_id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId).list());
			} else {

				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.account.id" +
						" AND m.date = a.date" ;
					
				if(customerLedgerId != 0){
					queryString +=" AND b.account.id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM CashAccountDepositModel a JOIN a.cash_account_deposit_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.account.id" +
						" AND m.date = a.date" ;
				if(customerLedgerId != 0){
					queryString +=" AND b.account.id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId)
									.list());
				
				queryString = "SELECT new com.inventory.reports.bean.AegingReportBean(m.id, COALESCE(SUM(b.amount),0))" +
						" FROM PdcPaymentModel a JOIN a.pdc_payment_list b,SalesModel m" +
						" WHERE a.date BETWEEN :fromDate AND :toDate" +
						" AND a.office_id = :office_id" +
						" AND m.customer.id = b.from_id" +
						" AND m.date = a.date" ;
				if(customerLedgerId != 0){
					queryString +=" AND b.from_id = "+customerLedgerId;
				}
						queryString += " AND LENGTH(TRIM(b.bill_no)) = 0 " +
						" GROUP BY m.id";
				unAdjustedAmountList.addAll(getSession()
						.createQuery(queryString)								
									.setDate("fromDate", fromDate)
									.setDate("toDate", toDate)
									.setLong("office_id", officeId).list());
			
			}
			
			System.out.println("====unAdjustedAmountList.size ====  "+unAdjustedAmountList.size());
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		
		return unAdjustedAmountList;		
	}
	
	
//	@SuppressWarnings("unchecked")
//	public List<AegingReportBean> getCreditPeriodDetails(long supplierId, long officeId)
//			throws Exception {
//		List<AegingReportBean> list = null;
//
//		try {
//			begin();
//
//		String condition;
//			/*if (purchaseId != 0) {
//				condition += " and id=" + purchaseId;
//			}*/
//			if (supplierId != 0) {
//				condition += " and supplier.id=" + supplierId;
//			}
//			if (officeId != 0) {
//				condition += " and office.id=" + officeId;
//			}
//			list = getSession()
//					.createQuery(
//							"SELECT new com.inventory.reports.ui.AegingReportBean(id, credit_period)" +
//							" FROM SupplierModel where date BETWEEN :fromDate AND :toDate" +
//							" AND payment_done ='N' "
//									+ condition+
//											" ORDER BY date")
//								.setDate("fromDate", fromDate)
//								.setDate("toDate", toDate).list();
//			commit();
//
//		} catch (Exception e) {
//			list = new ArrayList<Object>();
//			e.printStackTrace();
//			rollback();
//			close();
//		} finally {
//			flush();
//			close();
//		}
//
//		return list;
//	}
	
}
