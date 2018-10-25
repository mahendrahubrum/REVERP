package com.inventory.rent.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.dao.DeleteDao;
import com.inventory.rent.model.RentDetailsModel;
import com.inventory.rent.model.RentInventoryDetailsModel;
import com.inventory.rent.model.RentPaymentModel;
import com.inventory.rent.model.RentReturnItemDetailModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Aswathy
 * 
 * WebSpark.
 *
 * May 2, 2014
 */
public class RentDetailsDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -944448344502407640L;
	
List resultList = new ArrayList();
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	ItemDao itemDao=new ItemDao();

	public long save(RentDetailsModel obj, TransactionModel transaction, double payingAmt)
			throws Exception {

		try {

			begin();

			// Transaction Related

			getSession().save(transaction);
			
			flush();

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transaction
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			
//			if(payingAmt!=0)
//				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", payingAmt).setLong("id", obj.getCustomer().getId()).executeUpdate();
			
			
			
			// Transaction Related
			
			obj.setTransaction_id(transaction.getTransaction_id());
			
			RentInventoryDetailsModel invObj;
			List<RentInventoryDetailsModel> invList = new ArrayList<RentInventoryDetailsModel>();
			Iterator<RentInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();

				invList.add(invObj);
			}

			obj.setInventory_details_list(invList);

			getSession().save(obj);
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
			return obj.getId();
		}
	}
	
	public List getAllRentIdsAsComment(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.rent.model.RentDetailsModel(id,concat(customer.name, '; Rent No : ',rent_number) )"
									+ " from RentDetailsModel where office.id=:ofc and type<2 order by rent_number desc")
					.setParameter("ofc", ofc_id).list();
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
		return resultList;
	}
	
	public List getAllRentIdsBasedoncustomers(long ofc_id, long cus_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) )"
									+ " from RentDetailsModel where office.id=:ofc and type<2 and customer.id=:cus order by rent_number desc")
					.setParameter("ofc", ofc_id)
			.setParameter("cus", cus_id).list();
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
			return resultList;
		}
	}
	
	public List getActiveRentunderofficeandCustomers(long org_id, long off_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and office.id=:ofc and type<2 order by rent_number desc")
					.setParameter("org", org_id)
					.setParameter("ofc", off_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentunderofficeandemployees(long employee_id, long off_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where responsible_person=:resper and office.id=:ofc and type<2 order by rent_number desc")
					.setParameter("resper", employee_id)
					.setParameter("ofc", off_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentunderCustomers(long org_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and type<2 order by rent_number desc")
					.setParameter("org", org_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public Double getActiveRentdetail(long rent_id) throws Exception {
		Double ls=null;
		try {
			begin();
			ls=(Double) getSession().createQuery("select new com.inventory.rent.model.RentReturnItemDetailModel(total_amount) " +
					"from RentReturnItemDetailModel where rent_number=:rentno")
					.setParameter("rentno", rent_id).uniqueResult();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getActiveRentdetails(long rent_id) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select total_amount " +
					"from RentReturnItemDetailModel where rent_number=:rentno and id = (Select MAX(id) from RentReturnItemDetailModel where rent_number=:rentno)")
					.setParameter("rentno", rent_id).uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getRentdetails(long rent_id,long childId) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select total_amount " +
					"from RentReturnItemDetailModel where rent_number=:rentno and rent_inventory_id =:childId and id = (Select MAX(id) from RentReturnItemDetailModel where rent_inventory_id=:childId)")
					.setParameter("rentno", rent_id).setParameter("childId", childId).uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getPaymentDetails(long cid,Date start,Date end) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(sum(payment_amount),0) " +
					"from RentPaymentModel where customer.id=:cid and date between :start and :end )")
					.setParameter("cid", cid).setParameter("start", start)
					.setParameter("end", end).uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getPaymentDue(long cid,Date start) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select balance " +
					"from RentPaymentModel where customer.id=:cid and date<:start and date= (Select MAX(date) from RentPaymentModel where customer.id=:cid and date<:start)")
					.setParameter("cid", cid).setParameter("start", start)
					.uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public double getPaymentDueTotal(long cid,Date start) throws Exception {
		double ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select balance " +
					"from RentPaymentModel where customer.id=:cid and date<:start and date= (Select MAX(date) from RentPaymentModel where customer.id=:cid and date<:start)")
					.setParameter("cid", cid).setParameter("start", start)
					.uniqueResult();
				if(obj != null){
					ls = (Double) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	
	
	public List getPaymentList(long cid,Date start,Date end) throws Exception {
		List ls=new ArrayList();
		try {
			begin();
			Object obj = getSession().createQuery("from RentPaymentModel where customer.id=:cid and date between :start and :end order by date ASC )")
					.setParameter("cid", cid).setParameter("start", start)
					.setParameter("end", end).list();
				if(obj != null){
					ls = (List) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public long getPaymentCount(long cid,Date start,Date end) throws Exception {
		long ls=0;
		try {
			begin();
			Object obj = getSession().createQuery("select count(payment_amount) " +
					"from RentPaymentModel where customer.id=:cid and date between :start and :end )")
					.setParameter("cid", cid).setParameter("start", start)
					.setParameter("end", end).uniqueResult();
				if(obj != null){
					ls = (Long) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public Date getReturnDateDetails(long rent_id,long childId) throws Exception {
		Date date=null;
		try {
			begin();
			Object obj = getSession().createQuery("select return_date " +
					"from RentReturnItemDetailModel where rent_number=:rentno and rent_inventory_id =:childId and id = (Select MIN(id) from RentReturnItemDetailModel where rent_inventory_id=:childId)")
					.setParameter("rentno", rent_id).setParameter("childId", childId).uniqueResult();
				if(obj != null){
					date = (Date) obj;
				}
				
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return date;
	}
	
	
	public Date getReturnedDate(long childId) throws Exception {
		Date date = null;
		try {
			begin();
			Object obj = getSession().createQuery("select returned_date " +
					"from RentInventoryDetailsModel where id=:id")
					.setParameter("id", childId).uniqueResult();
				if(obj != null){
					date = (Date) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return date;
	}
	
	public Date getReturned(long childId) throws Exception {
		Date date = null;
		try {
			begin();
			Object obj = getSession().createQuery("select returned_date " +
					"from RentInventoryDetailsModel where id=:id and returned_status=:status")
					.setParameter("id", childId).setParameter("status", "Returned").uniqueResult();
				if(obj != null){
					date = (Date) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return date;
	}
	
	public Double getMonthlyReturnedItems(Date start,Date end,long rentId) throws Exception {
		Double amount=null;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(sum(total_return),0) " +
					"from RentReturnItemDetailModel where return_date between :start and :end and rent_inventory_id=:rid")
					.setParameter("start", start).setParameter("end", end).setParameter("rid", rentId).uniqueResult();
				if(obj != null){
					amount = (Double) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return amount;
	}
	
	public long getMonthlyReturnCount(Date start,Date end,long rentId) throws Exception {
		long count=0;
		try {
			begin();
			Object obj = getSession().createQuery("select count(total_return) " +
					"from RentReturnItemDetailModel where return_date between :start and :end and rent_inventory_id=:rid")
					.setParameter("start", start).setParameter("end", end).setParameter("rid", rentId).uniqueResult();
				if(obj != null){
					count = (Long) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return count;
	}
	
	
	public Double getPreviousReturnedItems(Date start,long rentId) throws Exception {
		Double amount=null;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(sum(total_return),0) " +
					"from RentReturnItemDetailModel where return_date < :start and rent_inventory_id=:rid")
					.setParameter("start", start).setParameter("rid", rentId).uniqueResult();
				if(obj != null){
					amount = (Double) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return amount;
	}
	
	public Long getMonthlyReturnedCount(Date start,Date end,long rentId) throws Exception {
		Long amount=null;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(count(total_return),0) " +
					"from RentReturnItemDetailModel where return_date between :start and :end and rent_inventory_id=:rid")
					.setParameter("start", start).setParameter("end", end).setParameter("rid", rentId).uniqueResult();
				if(obj != null){
					amount = (Long) obj;
				}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return amount;
	}
	
	public List getRentItemReturndetail(long rent_id, Date from_date, Date to_date) throws Exception {
		List ls = null;
		try {
			begin();
			ls= getSession()
					.createQuery("from RentReturnItemDetailModel where rent_inventory_id=:rid and return_date between :fromDate and :toDate")
					.setParameter("fromDate", from_date)
					.setParameter("toDate", to_date)
					.setParameter("rid", rent_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public RentReturnItemDetailModel getItemReturndetail(long rent_id, Date from_date, Date to_date) throws Exception {
		RentReturnItemDetailModel ls=null;
		try {
			begin();
			ls= (RentReturnItemDetailModel) getSession().createQuery("select new com.inventory.rent.model.RentReturnItemDetailModel(quantity,rent_date) " +
					"from RentReturnItemDetailModel where rent_number=:rentno and return_date between :fromDate and :toDate")
					.setParameter("fromDate", from_date)
					.setParameter("toDate", to_date)
					.setParameter("rentno", rent_id).uniqueResult();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentunderemployees(long org_id, long employee_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and type<2 and responsible_person=:resper order by rent_number desc")
					.setParameter("org", org_id)
					.setParameter("resper", employee_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentundereachCustomer(long cus_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where customer.id=:cus and type<2 order by rent_number desc")
					.setParameter("cus", cus_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentundereachCustomerwithnotreturnedqty(long cus_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(a.id,cast(a.rent_number as string) ) " +
					"from RentDetailsModel a join a.inventory_details_list b where a.customer.id=:cus and a.type<2 and b.returned_qty != b.qunatity order by a.rent_number desc")
					.setParameter("cus", cus_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveRentundereachCustomerwithnotreturnedqtywithdate(long cus_id, Date from_date) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(a.id,cast(a.rent_number as string) ) " +
					"from RentDetailsModel a join a.inventory_details_list b where a.customer.id=:cus and a.type<2 and b.returned_qty != b.qunatity and a.date >= :fdate order by a.rent_number desc")
					.setParameter("cus", cus_id)
					.setParameter("fdate", from_date).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveCashRentunderCustomers(long org_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and type<2 and cash_credit_sale = 1 order by rent_number desc")
					.setParameter("org", org_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveCustomersReturned(long org_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and type<2 and cash_credit_sale = 1 order by rent_number desc")
					.setParameter("org", org_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List getActiveCreditRentunderCustomers(long org_id) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) " +
					"from RentDetailsModel where office.organization.id=:org and type<2 and cash_credit_sale = 2  order by rent_number desc")
					.setParameter("org", org_id).list();
					
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return ls;
	}
	
	public List<Object> getAllRentNumbersForCustomer(long officeId, long custId,
			Date fromDate, Date toDate, String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) )"
									+ " from RentDetailsModel where customer.id=:custId and date between :fromDate and :toDate and active=true"+condition)
					.setParameter("custId", custId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	public List getRentReport(long org_id, long off_id, long custId, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(org_id !=0){
			condition += "office.organization.id="  +org_id+ " and "; 
		}
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		if(custId !=0){
			condition += "customer.id=" +custId+ " and ";
		}
		
try {
			
			begin();
			resultList = getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) ) from RentDetailsModel where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
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
					return resultList;
				}
}
	
	public List getAllRentReport(long off_id, Date from_date, Date to_date) throws Exception {
		String condition = "";
	
		if(off_id !=0){
			condition += "office.id=" +off_id+ " and ";
		}
		
try {
			
			begin();
			resultList = getSession().createQuery("from RentDetailsModel where " +condition+ "date between :fdate and :tdate")
					  .setParameter("fdate", from_date)
					  .setParameter("tdate", to_date)
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
					return resultList;
				}
}
	
	public List<Object> getAllRentidsByDate(long officeId,
			Date fromDate, Date toDate, String condition1) throws Exception {

		try {
			begin();
			String condition=condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			resultList = getSession()
					.createQuery(
							"select new com.inventory.rent.model.RentDetailsModel(id,cast(rent_number as string) )"
									+ " from RentDetailsModel where date between :fromDate and :toDate and active=true"+condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	public List getbudgetMaster(long rent_no) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList=getSession().createQuery("from RentDetailsModel where rent_number=:rent")
					.setLong("rent", rent_no)
					.list();
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
	
	public List getbudgetMasterList(long rent_no) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList=getSession().createQuery("from RentDetailsModel where id=:rent")
					.setLong("rent", rent_no)
					.list();
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
	
	public List getbudgetMastertimewiseList(long rent_no, long from_date) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList=getSession().createQuery("from RentDetailsModel where id=:rent and date >= :fdte")
					.setLong("rent", rent_no)
					.setParameter("fdte", from_date)
					.list();
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
	
	public List getIncentiveList(long ofcId) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList=getSession().createQuery("select DISTINCT responsible_person from RentPaymentModel where office.id=:ofc")
					.setLong("ofc", ofcId).list();
			
//			resultList= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.id=:login order by a.first_name")
//	.setLong("ofc", ofcId)
//	.setParameter("login", idLst).list();
				
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
	
	public List getEmployeeName(long employeeid) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.id=:id order by a.first_name")
					.setLong("id", employeeid).list();
			
//			resultList= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.id=:login order by a.first_name")
//	.setLong("ofc", ofcId)
//	.setParameter("login", idLst).list();
				
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
	
	public List getRentreturndetails(long rentid, long itemid) throws Exception {
		List resultList=null;
		try {
			begin();
			resultList = getSession().createQuery("from RentReturnItemDetailModel where rent_number=:id and itemname.id=:item order by return_date ASC")
					.setParameter("id", rentid)
					.setParameter("item", itemid)
					.list();
			
//			resultList= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.id=:login order by a.first_name")
//	.setLong("ofc", ofcId)
//	.setParameter("login", idLst).list();
				
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
	
	public List getRentdetails(long employeeid, Date from_date, Date to_date) throws Exception {
		List rentList = null;
		try {
			begin();
			rentList = getSession().createQuery("select new com.inventory.rent.model.RentPaymentModel(payment_amount,amount, balance, date, rent_number) from RentPaymentModel where responsible_person=:id and date between :fromDate and :toDate")
					.setLong("id", employeeid)
					.setParameter("fromDate", from_date)
					.setParameter("toDate", to_date).list();
			
//			resultList= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.id=:login order by a.first_name")
//	.setLong("ofc", ofcId)
//	.setParameter("login", idLst).list();
				
			commit();

			
		 
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return rentList;
	}
	
	public double getRentAmnt(long employeeid, Date from_date, Date to_date) throws Exception {
		double rentAmt=0;
		try {
			begin();
			Object obj = getSession().createQuery("select SUM(payment_amount) from RentPaymentModel a where a.responsible_person=:id and date between :fromDate and :toDate")
					.setLong("id", employeeid)
					.setParameter("fromDate", from_date)
					.setParameter("toDate", to_date).uniqueResult();
			
//			resultList= getSession().createQuery("select new com.webspark.uac.model.UserModel(a.loginId.id, concat(a.first_name,' ', a.middle_name,' ', a.last_name,'  ( ', a.employ_code ,' ) ')) from UserModel a where a.loginId.office.id=:ofc and loginId.userType.id>1 and a.loginId.id=:login order by a.first_name")
//	.setLong("ofc", ofcId)
//	.setParameter("login", idLst).list();
				
			commit();

			if (obj != null)
				rentAmt = (Double) obj;

		 
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return rentAmt;
	}
	
	public void delete(long id) throws Exception {
		try {
			begin();
			RentDetailsModel obj = (RentDetailsModel) getSession()
					.get(RentDetailsModel.class, id);

			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}
			
			

			getSession().delete(transObj);
			
			flush();

			// Transaction Related

			RentInventoryDetailsModel invObj;
			List list;
			Iterator<RentInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();
			
				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQuantity_in_basic_unit())
						.executeUpdate();
				
				flush();
				
				comDao.increaseStock(invObj.getItem().getId(), invObj.getQuantity_in_basic_unit());

			}
			List retlist = getSession()
					.createQuery(
							"select a from RentReturnItemDetailModel a where rent_number=:rid")
					.setParameter("rid", obj.getId()).list();
			Iterator retitr = retlist.iterator();
			RentReturnItemDetailModel mdl;
			while (retitr.hasNext()) {
				mdl = (RentReturnItemDetailModel) retitr.next();
				getSession().delete(mdl);
			}
			List idlist = getSession()
					.createQuery(
							"select a from RentPaymentModel a where office.id=:id and rent_number=:rid")
					.setParameter("id", obj.getOffice().getId())
					.setParameter("rid", obj.getId()).list();
			Iterator itr = idlist.iterator();
			RentPaymentModel rpMdl;
			while (itr.hasNext()) {
				rpMdl = (RentPaymentModel) itr.next();
				getSession().delete(rpMdl);
			}

			getSession().delete(obj);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		}
		flush();
		close();
	}
	
	public RentDetailsModel getrent(long id) throws Exception {
		RentDetailsModel pur = null;
		try {
			begin();
			pur = (RentDetailsModel) getSession().get(RentDetailsModel.class, id);
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
			return pur;
		}
	}
	
	public RentInventoryDetailsModel getchild(long id) throws Exception {
		RentInventoryDetailsModel rent = null;
		try {
			begin();
			rent = (RentInventoryDetailsModel) getSession().get(RentInventoryDetailsModel.class, id);
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
			return rent;
		}
	}
	
	public void updateRent(RentDetailsModel newobj, TransactionModel transaction) throws Exception
	{
		try {
			begin();
			
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
	}
	
	public void update(RentDetailsModel newobj, TransactionModel transaction, double payingAmt)
			throws Exception {
		try 
		{

			begin();
			// Delete
			
			getSession().createSQLQuery("DELETE FROM `i_rent_inventory_details` WHERE id not in(Select item_details_id from rent_inv_link)").executeUpdate();
			List old_AcctnotDeletedLst = new ArrayList();
			List AcctDetLst = getSession().createQuery(
					"select b from TransactionModel a join a.transaction_details_list b "+"where a.id=" + transaction.getTransaction_id()).list();
			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = AcctDetLst.iterator();
			while (aciter.hasNext()) 
			{
				tr = aciter.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
				old_AcctnotDeletedLst.add(tr.getId());
			}
			
			List oldLst = getSession().createQuery(
							"select b from RentDetailsModel a join a.inventory_details_list b where a.id=:id")
							.setLong("id", newobj.getId()).list();

			RentInventoryDetailsModel invObj;
			List list;
			Iterator<RentInventoryDetailsModel> it = oldLst.iterator();
			while (it.hasNext()) {
				invObj = it.next();
				getSession().createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
								.setParameter("id", invObj.getItem().getId())
								.setParameter("qty", invObj.getQuantity_in_basic_unit())
								.executeUpdate();
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getReturned_qty())
						.executeUpdate();
				flush();
				// For Stock Update
				try
				{
					comDao.increaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				}
				catch(Exception e)
				{
					continue;
				}
				
			}
			// getSession().delete(obj);
			flush();
			
		getSession().clear();
			// Save
			List<RentInventoryDetailsModel> invList = new ArrayList<RentInventoryDetailsModel>();
			Iterator<RentInventoryDetailsModel> it1 = newobj
					.getInventory_details_list().iterator();
			while (it1.hasNext()) {
				invObj = it1.next();
				getSession().createQuery(
								"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
								.setParameter("id", invObj.getItem().getId())
								.setParameter("qty", invObj.getQuantity_in_basic_unit())
								.executeUpdate();
				
				getSession().createQuery(
						"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getReturned_qty())
						.executeUpdate();
				flush();
				comDao.decreaseStockByStockID(invObj.getStk_id(), invObj.getQuantity_in_basic_unit());
				
			}
			
			getSession().update(newobj);
			
			
			// Transaction Related
			getSession().update(transaction);
			flush();
			Iterator<TransactionDetailsModel> aciter1 = transaction.getTransaction_details_list().iterator();
			while (aciter1.hasNext()) 
			{
				tr = aciter1.next();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getFromAcct().getId())
								.executeUpdate();
				getSession().createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tr.getAmount())
								.setLong("id", tr.getToAcct().getId()).executeUpdate();
				flush();
			}
			
			flush();
			getSession().createQuery(
							"delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst",(Collection)old_AcctnotDeletedLst)
							.executeUpdate();
			commit();
		} 
		catch (Exception e) 
		{
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		finally 
		{
			flush();
			close();
		}
	}
	
	public void updatepaymnt(RentDetailsModel mdl) throws Exception {
		try {
			begin();
			getSession().update(mdl);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		
	}
	
	public void updatemdl(RentDetailsModel mdl) throws Exception {
		try {
			begin();
			getSession().update(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		
	}
	
	public void updatechildmdl(RentInventoryDetailsModel mdl) throws Exception {
		try {
			begin();
			getSession().update(mdl);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		
	}
	
	public RentDetailsModel getRentDetailsModel(long id) throws Exception 
	{
		RentDetailsModel mdl=null;
		try {
			begin();
			mdl=(RentDetailsModel) getSession().get(RentDetailsModel.class,id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl;
	}
	
	public RentInventoryDetailsModel getRentInventoryDetailsModel(long id) throws Exception {
		RentInventoryDetailsModel mdl=null;
		try {
			begin();
			mdl=(RentInventoryDetailsModel) getSession().get(RentInventoryDetailsModel.class,id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		
		return mdl;
	}
	
	public List getAllRentItemsWithRealStck(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			Iterator it = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' ), current_balalnce)"
									+ " from ItemModel where office.id=:ofc and affect_type=5 order by name")
					.setParameter("ofc", ofc_id).list().iterator();
			
			double bal=0;
			while(it.hasNext()) {
				ItemModel obj=(ItemModel) it.next();
				
				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm and status=3")
					.setLong("itm", obj.getId()).uniqueResult();
					
				obj.setName(obj.getName()+(obj.getCurrent_balalnce()-bal));
				resultList.add(obj);
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
			return resultList;
		}
	}
	
	public ItemModel getItem(long id) throws Exception {
		ItemModel itm = null;
		try {
			begin();
			itm = (ItemModel) getSession().get(ItemModel.class, id);
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
			return itm;
		}
	}
	
	public ItemUnitMangementModel getItemUnit(long id) throws Exception {
		ItemUnitMangementModel itm = null;
		try {
			begin();
			itm = (ItemUnitMangementModel) getSession().get(ItemUnitMangementModel.class, id);
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
			return itm;
		}
	}
	
	@SuppressWarnings("finally")
	public TransactionModel getTransaction(long id) throws Exception {
		TransactionModel tran = null;
		try {
			begin();
			tran = (TransactionModel) getSession().get(TransactionModel.class,
					id);
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
			return tran;
		}
	}
	
	public List getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdminforrentdetails(long officeId, long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(loginId.id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) from UserModel "
									+ "where "
									+ "((loginId.userType.id=:semiad and loginId.office.organization.id=:org) or loginId.office.id=:ofc) and loginId.userType.id>1  order by first_name")
					.setLong("ofc", officeId).setLong("semiad", 3).setLong("ofc", officeId).setLong("org", org_id).list();
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
			return resultList;
		}
	}
	
	public List getEmployeesWithFullNameAndCodeUnderOfficeIncludingSemiAdminforrentdetails(long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(loginId.id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) from UserModel "
									+ "where "
									+ "(loginId.office.organization.id=:org) and loginId.userType.id>1  order by first_name")
					.setLong("org", org_id).list();
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
			return resultList;
		}
	}
	
	public List getAllActiveCustomerNamesWithOrgID(long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ " from CustomerModel where ledger.office.organization.id=:org and   ledger.status=:val")
					.setLong("org", org_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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
			return resultList;
		}
	}
	
	public List getAllActiveCustomerNamesWithLedgerID(long ofc_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ " from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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
			return resultList;
		}
	}
	
	public CustomerModel getCustomerFromLedger(long led_id) throws Exception {
		CustomerModel cust = null;
		try {
			begin();
			cust = (CustomerModel) getSession()
					.createQuery("from CustomerModel where ledger.id=:led")
					.setLong("led", led_id).uniqueResult();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return cust;
		}
	}
	
	public RentDetailsModel getCustomerFromRent(long rent_id) throws Exception {
		RentDetailsModel cust = null;
		try {
			begin();
			cust = (RentDetailsModel) getSession()
					.createQuery("from RentDetailsModel where id=:rentno")
					.setLong("rentno", rent_id).uniqueResult();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return cust;
		}
	}
	
	public List getAllItemUnitDetails(long item_id) throws Exception {

		try {

			List unitIdList = new ArrayList();

			begin();
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct basicUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0")
					.setLong("itm", item_id).list());
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct alternateUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0")
					.setLong("itm", item_id).list());

			if (unitIdList.size() > 0) {
				resultList = getSession()
						.createQuery(
								"select new com.inventory.config.unit.model.UnitModel(id, symbol)"
										+ " from UnitModel where status=:val and id in (:ids)")
						.setParameter("val", SConstants.statuses.RACK_ACTIVE)
						.setParameterList("ids", unitIdList).list();

			}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getStocks(long item_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			
			begin();
			resultList.addAll(getSession()
					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat(' Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and (status=1 or status=2)  order by id")
									.setLong("itm", item_id).list());
			
			if(resultList==null || resultList.size()<=0) {
				
				 Object obj= getSession()
						.createQuery("select max(id) from ItemStockModel where item.id=:itm and (status=1 or status=2)")
										.setLong("itm", item_id).uniqueResult();
				 
				 if(obj!=null)
					 resultList.addAll(getSession()
								.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
												+ " id, concat(' Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where id=:id")
												.setLong("id", (Long) obj).list());
				
			}
			
//			resultList.addAll(getSession()
//					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
//									+ " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3  order by id")
//									.setLong("itm", item_id).list());
			
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
	
	public Long getDefaultStockToSelect(long item_id) throws Exception {
		long stk_id=0;
		try {
			
			begin();
			stk_id = (Long) getSession()
					.createQuery(
							"select coalesce(min(id),0) from ItemStockModel where item.id=:itm and balance>0 and status=1")
					.setLong("itm", item_id).uniqueResult();

			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk_id;
		}
		
	}
	
	public double getConvertionRate(long item_id, long unit_id, int sales_type)
			throws Exception {
		double conv_rate = 1;
		try {
			begin();
			Object obj = getSession()
					.createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			commit();

			if (obj != null)
				conv_rate = (Double) obj;

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return conv_rate;
		}
	}
	
	public double getItemPrice(long item_id, long unit_id, int sales_type)
			throws Exception {
		double price = 0;
		try {
			
			begin();
			Object obj = getSession()
					.createQuery(
							"select item_price from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			commit();
			

			if (obj != null) {
				price = (Double) obj;
			}
			else {
				Object obj1 = getSession()
						.createQuery("select rate from ItemModel where id="+item_id)
								.uniqueResult();
				if(obj1!=null)
					price=(Double) obj1;
			}
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return price;
		}
		
		
		
	}
	
	public TaxModel gettaxDetailsinoffice(long ofc_id) throws Exception {
		TaxModel itmm = null;
	
try {
			
			begin();
			itmm = (TaxModel) getSession().createQuery("from TaxModel where office.id=:ofcid")
					  .setParameter("ofcid", ofc_id).uniqueResult();
					 
					
			        
					commit();
				} catch (Exception e) {
					rollback();
					close();
					e.printStackTrace();
					throw e;
				} finally {
					flush();
					close();
					return itmm;
				}
}
	
	public TaxModel gettaxDetails(long id) throws Exception {
		TaxModel itm = null;
		try {
			begin();
			itm = (TaxModel) getSession().get(TaxModel.class, id);
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
			return itm;
		}
	}
	
	public RentPaymentModel getpaymntDetails(long id) throws Exception {
		RentPaymentModel mdl = null;
		try {
			begin();
			mdl = (RentPaymentModel) getSession().get(RentPaymentModel.class, id);
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
			return mdl;
		}
	}
	
	public RentPaymentModel getpaymntDetailsinoffice(long id) throws Exception {
		RentPaymentModel itmm = null;
	
try {
			List lis = new ArrayList();
			begin();
			lis = getSession().createQuery("from RentPaymentModel where rent_number=:rentid order by id desc")
					.setParameter("rentid", id)
					.list();
			
			if(lis != null && lis.size() >0){
					 
			itmm = (RentPaymentModel) lis.get(0);	
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
					return itmm;
				}
}
	
	public List getAllItemsunderrendid(long rent_id)
			throws Exception {
		try {
			begin();
//			resultList = getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(b.item.id,b.item.name,b.returned_qty,b.qunatity) " +
//					"from RentDetailsModel a join a.inventory_details_list b where b.id=:rent and a.type<2 and b.returned_qty != b.qunatity")
//					.setParameter("rent", rent_id).list();concat(name,' ( '," +"item_code,' )  Bal : ' ), current_balalnce)
			
			resultList = getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(b.item.id,concat(b.item.name,' Bal : ',b.quantity_in_basic_unit)) from RentDetailsModel a join a.inventory_details_list b where a.id=:rent and a.type<2 and b.returned_qty != b.qunatity")
					.setParameter("rent", rent_id).list();
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
			return resultList;
		}
	}
	
	public long getAllItemsdetailsunderrendid(long rent_id, long itemid)
			throws Exception {
		long id=0;
		try {
			
			begin();
//			resultList = getSession().createQuery("select new com.inventory.rent.model.RentDetailsModel(b.item.id,b.item.name,b.returned_qty,b.qunatity) " +
//					"from RentDetailsModel a join a.inventory_details_list b where b.id=:rent and a.type<2 and b.returned_qty != b.qunatity")
//					.setParameter("rent", rent_id).list();
			
			id = (Long) getSession().createQuery("select b.id " +
					"from RentDetailsModel a join a.inventory_details_list b where a.id=:rent and " +
					"b.item.id =:item and b.returned_qty != b.qunatity")
					.setParameter("rent", rent_id)
					.setParameter("item", itemid).uniqueResult();
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
			return id;
		}
	}
	
}