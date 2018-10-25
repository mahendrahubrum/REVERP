package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class BillViewDao extends SHibernate {

	public List getBillNo(int type, Date fromdate, Date todate, long officeid) throws Exception {
		List billList = new ArrayList();

		try {
			begin();
			switch (type) {
			case SConstants.BillViewDetails.PURCHASE_ENQUIRY:
				billList = getSession()
						.createQuery(
								"select new com.inventory.purchase.model.PurchaseInquiryModel(id,inquiry_no) from PurchaseInquiryModel "
										+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
						.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
						.list();
				break;
			case SConstants.BillViewDetails.PURCHASE_QUOTATION:
				billList = getSession()
				.createQuery(
						"select new com.inventory.purchase.model.PurchaseQuotationModel(id,quotation_no) from PurchaseQuotationModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.PURCHASE_ORDER:
				billList = getSession()
				.createQuery(
						"select new com.inventory.purchase.model.PurchaseOrderModel(id,order_no) from PurchaseOrderModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.GRN:
				billList = getSession()
				.createQuery(
						"select new com.inventory.purchase.model.PurchaseGRNModel(id,grn_no) from PurchaseGRNModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.PURCHASE:
				billList = getSession()
				.createQuery(
						"select new com.inventory.purchase.model.PurchaseModel(id,purchase_no) from PurchaseModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.PURCHASE_RETURN:
				billList = getSession()
				.createQuery(
						"select new com.inventory.purchase.model.PurchaseReturnModel(id,return_no) from PurchaseReturnModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.SALES_ENQIRY:
				billList = getSession()
				.createQuery(
						"select new com.inventory.sales.model.SalesInquiryModel(id,inquiry_no) from SalesInquiryModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.SALES_QUOTATION:
				billList = getSession()
				.createQuery(
						"select new com.inventory.sales.model.QuotationModel(id,quotation_no) from QuotationModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.SALES_ORDER:
				billList = getSession()
				.createQuery(
						"select new com.inventory.sales.model.SalesOrderModel(id,order_no) from SalesOrderModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.DELIVERY_NOTE:
				billList = getSession()
				.createQuery(
						"select new com.inventory.sales.model.DeliveryNoteModel(id,deliveryNo) from DeliveryNoteModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.SALES:
				billList = getSession()
				.createQuery(
						"select new com.inventory.sales.model.SalesModel(id,sales_number) from SalesModel "
								+ "where office.id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.CASH_DEPOSIT:
				billList = getSession()
				.createQuery(
						"select new com.inventory.config.acct.model.CashAccountDepositModel(id,bill_no) from CashAccountDepositModel "
								+ "where office_id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.CASH_PAYMENT:
				billList = getSession()
				.createQuery(
						"select new com.inventory.config.acct.model.CashAccountPaymentModel(id,bill_no) from CashAccountPaymentModel "
								+ "where office_id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.BANK_DEPOSIT:
				billList = getSession()
				.createQuery(
						"select new com.inventory.config.acct.model.BankAccountDepositModel(id,bill_no) from BankAccountDepositModel "
								+ "where office_id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.BANK_PAYMENT:
				billList = getSession()
				.createQuery(
						"select new com.inventory.config.acct.model.BankAccountPaymentModel(id,bill_no) from BankAccountPaymentModel "
								+ "where office_id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;
			case SConstants.BillViewDetails.JOURNEL:
				billList = getSession()
				.createQuery(
						"select new com.inventory.journal.model.JournalModel(id,bill_no) from JournalModel "
								+ "where office_id=:office and active=true and date between :from and :to order by id DESC")
				.setParameter("from", fromdate).setParameter("to", todate).setParameter("office", officeid)
				.list();
				break;

				
			default:
				break;
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		}finally{
			
		}

		return billList;
	}

}
