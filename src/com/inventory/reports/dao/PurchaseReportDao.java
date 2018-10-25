package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.SupplierModel;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.inventory.purchase.model.PurchaseQuotationModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 8, 2013
 */
public class PurchaseReportDao extends SHibernate implements Serializable {

	private static final long serialVersionUID = 8947836720742424569L;

	public List<Object> getPurchaseDetails(long purchaseId, long supplierId,
			Date fromDate, Date toDate, long officeId,  long orgId,String condition1)
			throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = condition1;
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseModel where date>=:fromDate and date<=:toDate "
									+ condition+" and office.organization.id=:orgId")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	
	public List<Object> getPurchaseGRNDetails(long purchaseId, long supplierId,
			Date fromDate, Date toDate, long officeId,  long orgId,String condition1)
			throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = condition1;
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseGRNModel where date>=:fromDate and date<=:toDate "
									+ condition+" and office.organization.id=:orgId")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getPurchaseDetailsConsolidated(long purchaseId, long supplierId, Date fromDate, Date toDate, long officeId,  long orgId,String condition1)
			throws Exception {
		List list = new ArrayList();

		try {
			begin();

			String condition = condition1;
			String cdn = "";
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
//				condition += " and supplier.id=" + supplierId;
				cdn+=" and ledger.id="+supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			List suppList=getSession().createQuery("from SupplierModel where ledger.office.id=:ofc and ledger.status=:val "+cdn+" order by name")
					.setParameter("ofc", officeId)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
			
			if(suppList.size()>0){
				Iterator supitr=suppList.iterator();
				while (supitr.hasNext()) {
					SupplierModel cust=(SupplierModel)supitr.next();
					
					List lst=getSession().createQuery(" from PurchaseModel where date>=:fromDate and date<=:toDate "+ condition+
							" and office.organization.id=:orgId and supplier.id=:ledger")
							.setParameter("orgId", orgId)
							.setParameter("fromDate", fromDate)
							.setParameter("ledger", cust.getLedger().getId())
							.setParameter("toDate", toDate).list();
					
					if(lst.size()>0)
						list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean(supplier.id, supplier.name ," +
								"coalesce(sum(amount / conversionRate),0)," +
								"currency_id) " +
								" from PurchaseModel where date>=:fromDate and date<=:toDate "+ condition+
								" and office.organization.id=:orgId and supplier.id=:ledger" )
//								" GROUP BY supplier.id, supplier.name,currency_id")
								.setParameter("orgId", orgId)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
				}
			}
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
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> getPurchaseGRNDetailsConsolidated(long purchaseId, long supplierId, Date fromDate, Date toDate, long officeId,  long orgId,String condition1)
			throws Exception {
		List list = new ArrayList();

		try {
			begin();

			String condition = condition1;
			String cdn = "";
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
//				condition += " and supplier.id=" + supplierId;
				cdn+=" and ledger.id="+supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			List suppList=getSession().createQuery("from SupplierModel where ledger.office.id=:ofc and ledger.status=:val "+cdn+" order by name")
					.setParameter("ofc", officeId)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
			
			if(suppList.size()>0){
				Iterator supitr=suppList.iterator();
				while (supitr.hasNext()) {
					SupplierModel cust=(SupplierModel)supitr.next();
					
					List lst=getSession().createQuery(" from PurchaseGRNModel where date>=:fromDate and date<=:toDate "+ condition+
							" and office.organization.id=:orgId and supplier.id=:ledger")
							.setParameter("orgId", orgId)
							.setParameter("fromDate", fromDate)
							.setParameter("ledger", cust.getLedger().getId())
							.setParameter("toDate", toDate).list();
					
					if(lst.size()>0)
						list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean(supplier.id, supplier.name ,coalesce(sum(amount/conversionRate),0)) " +
								" from PurchaseGRNModel where date>=:fromDate and date<=:toDate "+ condition+
								" and office.organization.id=:orgId and supplier.id=:ledger")
								.setParameter("orgId", orgId)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
				}
			}
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
	
	
	public List<Object> getPurchaseOrderDetails(long purchaseId, long supplierId,
			Date fromDate, Date toDate, long officeId)
					throws Exception {
		List<Object> list = null;
		
		try {
			begin();
			
			String condition = "";
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseOrderModel where date between :fromDate and :toDate and active=true "
									+ condition)
									.setParameter("fromDate", fromDate)
									.setParameter("toDate", toDate).list();
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
	
	public List<Object> getPurchaseInquiryDetails(long purchaseId, long supplierId,
			Date fromDate, Date toDate, long officeId)
					throws Exception {
		List<Object> list = null;
		
		try {
			begin();
			
			String condition = "";
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseInquiryModel where date between :fromDate and :toDate and active=true "
									+ condition)
									.setParameter("fromDate", fromDate)
									.setParameter("toDate", toDate).list();
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
	
	public List<Object> getPurchaseQuotationDetails(long purchaseId, long supplierId,
			Date fromDate, Date toDate, long officeId)
					throws Exception {
		List<Object> list = null;
		
		try {
			begin();
			
			String condition = "";
			if (purchaseId != 0) {
				condition += " and id=" + purchaseId;
			}
			if (supplierId != 0) {
				condition += " and supplier.id=" + supplierId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from PurchaseQuotationModel where date between :fromDate and :toDate and active=true "
									+ condition)
									.setParameter("fromDate", fromDate)
									.setParameter("toDate", toDate).list();
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

	public List getAllPurchaseNumbersAsComment(long ofc_id) throws Exception {
		List resultList = null;
		try {
			String condition = "";
			if (ofc_id != 0) {
				condition = " and office.id=" + ofc_id;
			}
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseModel(id,cast(purchase_number as string) )"
									+ " from PurchaseModel where active=true " + condition)
					.list();
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
	
	public List getPurchaseRateReport(long officeId,long empId, Date fromDate, Date toDate)
			throws Exception {
		List resultList = null;
		String condition = "";
		try {
			begin();
			
			if (officeId != 0) {
				condition = " and a.office.id=" + officeId;
			}
			if (empId != 0) {
				
				condition = " and a.login.id=" + empId;
			}
			
			resultList = getSession()
					.createQuery("select new com.inventory.reports.bean.PurchaseRateReportBean(a.date, b.item.name, b.qunatity,b.unit.symbol,b.unit_price)"+
							" from PurchaseModel a join a.purchase_details_list b where a.date between :fromDt and :toDt and active=true "
									+ condition + " group by b.item.id,b.unit.id,b.unit_price order by a.date,b.item.id")
					.setParameter("fromDt", fromDate)
					.setParameter("toDt", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public List<PurchaseInquiryModel> getAllPurchaseInquiryForSupplier(
			long supplier_id, long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			System.out.println("==========supplier_id=========== "+supplier_id);
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseInquiryModel(a.id,concat(a.inquiry_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseInquiryModel a join a.inquiry_details_list b" +
									" where a.office.id=:ofc and a.supplier.id=:sup and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("sup", supplier_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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
	
	
	
	public List<PurchaseInquiryModel> getAllPurchaseInquiryForOffice(long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
		
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseInquiryModel(a.id,concat(a.inquiry_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseInquiryModel a join a.inquiry_details_list b" +
									" where a.office.id=:ofc and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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
	public List<PurchaseQuotationModel> getAllPurchaseQuotationForOffice(long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
		
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseQuotationModel(a.id,concat(a.quotation_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseQuotationModel a join a.quotation_details_list b" +
									" where a.office.id=:ofc and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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

	public List<PurchaseQuotationModel> getAllPurchaseQuotationForSupplier(
			long supplier_id, long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			System.out.println("==========supplier_id=========== "+supplier_id);
			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.PurchaseQuotationModel(a.id,concat(a.quotation_no, '  - ', a.supplier.name, ' - ',a.date) )"
									+ " from PurchaseQuotationModel a join a.quotation_details_list b" +
									" where a.office.id=:ofc and a.supplier.id=:sup and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("sup", supplier_id)
									.setParameter("from_date", fromDate)
									.setParameter("to_date", toDate).list();
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
	
}
