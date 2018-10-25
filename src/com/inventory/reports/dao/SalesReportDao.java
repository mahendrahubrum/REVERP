package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.stock.model.ContainerModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.sales.model.QuotationModel;
import com.inventory.sales.model.SalesInquiryModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 12, 2013
 */
public class SalesReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6241049407965920098L;

	CommonMethodsDao methodsDao = new CommonMethodsDao();

	@SuppressWarnings("unchecked")
	public List<Object> getSalesDetails(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							"from SalesModel where date>=:fromDate and date<=:toDate   "//and (type=0 or type=1)
									+ condition
									+ " and office.organization.id=:orgId")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	public List<Object> getSalesDetailsWithMode(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId,long modeId) throws Exception {
		List<Object> list = null;
		List<Object> finalList = new ArrayList();
		
		try {
			
			begin();
			
			String condition = condition1;
			if (salesId != 0) {
				condition += " and a.id=" + salesId;
			}
			if (custId != 0) {
				condition += " and a.customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			if (modeId != 0) {
				condition += " and b.paymentMode.id=" + modeId;
				finalList = getSession()
						.createQuery(
							"select a from SalesModel a join a.sales_payment_mode_list b where a.date between :fromDate and :toDate   "//and (type=0 or type=1)
							+ condition
							+ " and a.office.organization.id=:orgId group by a.id")
							.setParameter("orgId", orgId)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate).list();
			
			}else{
				finalList = getSession()
						.createQuery(
								"select a from SalesModel a where a.date>=:fromDate and a.date<=:toDate   "//and (type=0 or type=1)
										+ condition
										+ " and a.office.organization.id=:orgId")
						.setParameter("orgId", orgId)
						.setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).list();
			}
			
//			if (modeId != 0) {
//				List detList;
//				Iterator siter=list.iterator();
//				SalesModel mdl;
//				SalesPaymentModeDetailsModel payModel;
//				while (siter.hasNext()) {
//					mdl= (SalesModel) siter.next();
//					Hibernate.initialize(mdl.getSales_payment_mode_list());
//					detList=mdl.getSales_payment_mode_list();
//					Iterator subIter=detList.iterator();
//					while (subIter.hasNext()) {
//						payModel = (SalesPaymentModeDetailsModel) subIter.next();
//						if(payModel.getPaymentMode().getId()==modeId){
//							finalList.add(mdl);
//						}
//					}
//				}
//			}else{
//				finalList.addAll(list);
//			}
			
			commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		
		return finalList;
	}
	
	
	@SuppressWarnings({ "unchecked", "null", "rawtypes" })
	public List<Object> getSalesDetailsConsolidated(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId) throws Exception {
		List list = new ArrayList();

		try {

			begin();

			String condition = condition1;
			String cdn = "";
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (custId != 0) {
//				condition += " and customer.id=" + custId;
				cdn+=" and ledger.id="+custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			List customerList=getSession().createQuery("from CustomerModel where ledger.office.id=:ofc and ledger.status=:val "+cdn+" order by name")
					.setParameter("ofc", officeId)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
			if(customerList.size()>0){
				Iterator cusitr=customerList.iterator();
				while (cusitr.hasNext()) {
					CustomerModel cust=(CustomerModel)cusitr.next();
					List lst=getSession().createQuery("from SalesModel where date>=:fromDate and date<=:toDate" +
						//	"   and (type=0 or type=1)"
					 condition+ " and office.organization.id=:orgId and customer.id=:ledger ")
							.setParameter("orgId", orgId)
							.setParameter("fromDate", fromDate)
							.setParameter("ledger", cust.getLedger().getId())
							.setParameter("toDate", toDate).list();
					
					if(lst.size()>0)
						list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean" +
								"(customer.id, customer.name ,coalesce(sum(amount/conversionRate),0),currency_id) " +
								" from SalesModel where date>=:fromDate and date<=:toDate  " 
								//" and (type=0 or type=1)"
								+ condition
								+ " and office.organization.id=:orgId and customer.id=:ledger ")
								.setParameter("orgId", orgId)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
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

		return list;
	}
	@SuppressWarnings({ "unchecked", "null", "rawtypes" })
	public List<Object> getSalesDetailsConsolidatedWithMode(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId,long modeId) throws Exception {
		List list = new ArrayList();
		
		try {
			
			begin();
			
			String condition = condition1;
			String cdn = "";
			if (salesId != 0) {
				condition += " and a.id=" + salesId;
			}
			if (custId != 0) {
//				condition += " and customer.id=" + custId;
				cdn+=" and a.ledger.id="+custId;
			}
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			
			List customerList=getSession().createQuery("from CustomerModel where ledger.office.id=:ofc and ledger.status=:val "+cdn+" order by name")
					.setParameter("ofc", officeId)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
			if(customerList.size()>0){
				Iterator cusitr=customerList.iterator();
				while (cusitr.hasNext()) {
					CustomerModel cust=(CustomerModel)cusitr.next();
				
					
					if (modeId != 0) {
						
						List lst=getSession().createQuery("select a from SalesModel a join a.sales_payment_mode_list b where a.date>=:fromDate and a.date<=:toDate" +
								//	"   and (type=0 or type=1)"
								condition+ " and a.office.organization.id=:orgId and a.customer.id=:ledger group by a.id")
								.setParameter("orgId", orgId)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list();
						
						if(lst.size()>0){
						condition += " and b.paymentMode.id=" + modeId;
						list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean" +
								"(a.customer.id, a.customer.name ,coalesce(sum(a.amount/a.conversionRate),0),a.currency_id) " +
								" from SalesModel a join a.sales_payment_mode_list b where a.date between :fromDate and :toDate  " 
								//" and (type=0 or type=1)"
								+ condition
								+ " and a.office.organization.id=:orgId and a.customer.id=:ledger group by a.customer.id")
								.setParameter("orgId", orgId)
								.setParameter("fromDate", fromDate)
								.setParameter("ledger", cust.getLedger().getId())
								.setParameter("toDate", toDate).list());
						}
					}else{
						
						List lst=getSession().createQuery("select a from SalesModel a where a.date>=:fromDate and a.date<=:toDate" +
								//	"   and (type=0 or type=1)"
							 condition+ " and a.office.organization.id=:orgId and a.customer.id=:ledger ")
									.setParameter("orgId", orgId)
									.setParameter("fromDate", fromDate)
									.setParameter("ledger", cust.getLedger().getId())
									.setParameter("toDate", toDate).list();
							
							if(lst.size()>0){
								list.addAll(getSession().createQuery("select new com.inventory.reports.bean.SalesReportBean" +
										"(a.customer.id, a.customer.name ,coalesce(sum(a.amount/a.conversionRate),0),a.currency_id) " +
										" from SalesModel a where a.date between :fromDate and :toDate  " 
										//" and (type=0 or type=1)"
										+ condition
										+ " and a.office.organization.id=:orgId and a.customer.id=:ledger group by a.customer.id")
										.setParameter("orgId", orgId)
										.setParameter("fromDate", fromDate)
										.setParameter("ledger", cust.getLedger().getId())
										.setParameter("toDate", toDate).list());
							}
					}
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
		
		return list;
	}

	public List<Object> getSalesManWiseSalesDetails(long salesId, long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId, long salesManId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if (salesManId != 0) {
				condition += " and responsible_employee=" + salesManId;
			}
			list = getSession()
					.createQuery(
							"from SalesModel where date>=:fromDate and date<=:toDate  "
									+ condition
									+ " and office.organization.id=:orgId order by date desc")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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

	public List<Object> getSalesManWiseCollectionDetails(long salesId,
			long custId, long officeId, String condition1, long orgId,
			long salesManId, List idList, int type, Date frmDate, Date todate)
			throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (salesId != 0) {
				condition += " and id=" + salesId;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if (salesManId != 0) {
				condition += " and responsible_person=" + salesManId;
			}
			String condn1="";
			if (type == 1) {

				if (idList.size() > 0) {
					if(salesManId != 0)
						condn1 += " or (id in "
								+ idList.toString().replace('[', '(')
										.replace(']', ')') + "and responsible_person=" + salesManId+")";
					else
						condn1 += " or (id in "
							+ idList.toString().replace('[', '(')
									.replace(']', ')') + ")";
				}
				
					list = getSession()
							.createQuery(
									"from SalesModel where  (type=0 or type=1) and payment_amount!=0"
											+ " and office.organization.id=:orgId and date between :frm and :todt "+condition+condn1+" order by date desc ")
							.setParameter("orgId", orgId).setParameter("frm", frmDate).setParameter("todt", todate).list();
				
			} else if (type == 2) { 

				list = getSession()
						.createQuery(
								"from SalesModel where (type=0 or type=1) and date between :frm and :todt"
										+ condition
										+ " and office.organization.id=:orgId order by date desc ")
						.setParameter("orgId", orgId)
						.setParameter("frm", frmDate)
						.setParameter("todt", todate).list();
			} else {
				String newcondition = "";
				if (idList.size() > 0)
					newcondition += " or (id in "
							+ idList.toString().replace('[', '(')
									.replace(']', ')') + ")";
				newcondition += ")";
				list = getSession()
						.createQuery(
								"from SalesModel where office.organization.id=:orgId "
										+ "and (((type=0 or type=1) and date between :frm and :todt)"
										+ newcondition + condition
										+ " order by date desc ")
						.setParameter("orgId", orgId)
						.setParameter("frm", frmDate)
						.setParameter("todt", todate).list();

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

	public List<Object> getLaundrySalesDetails(long salesNo, long custId,
			Date fromDate, Date toDate, long officeId, String condition1)
			throws Exception {
		List<Object> list = new ArrayList<Object>();

		try {
			begin();

			String condition = condition1;
			if (salesNo != 0) {
				condition += " and sales_number=" + salesNo;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							"from LaundrySalesModel where date between :fromDate and :toDate and active=true and (type=0 or type=1) and active=true"
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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

	public List<Object> getGRVSalesDetails(long salesNo, long custId,
			Date fromDate, Date toDate, long officeId, String condition1)
			throws Exception {
		List<Object> list = null;

		try {
			begin();

			String condition = condition1;
			if (salesNo != 0) {
				condition += " and sales_number=" + salesNo;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							"from SalesModel where date>=:fromDate and date<=:toDate and active=true and type=2"
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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

	public List getAllSalesNumbersAsComment(long officeId) throws Exception {
		List resultList = null;
		try {
			begin();
			String condition = "";
			if (officeId != 0) {
				condition += " where office.id=" + officeId
						+ " and active=true";
			} else
				condition += " where active=true";

			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(id,cast(sales_number as string) )"
									+ " from SalesModel " + condition).list();
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
									+ " from SalesModel where date between :fromDate and :toDate and active=true"
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

	// Added By Jinshad On 20 Nov 2013

	@SuppressWarnings("unchecked")
	public List<Object> getItemWiseSalesDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getSItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});
				
				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						rptObj.setDescription(methodsDao.getItemBalanceAtDateNew(
								itemObj.getId(), toDate,itemObj.getOffice().getId())
								+ " "
								+ itemObj.getUnit().getSymbol());
						resultList.add(rptObj);
					}
				}
			} else {

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = getSItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							rptObj.setTotal(sum);
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDate(itemObj.getId(),
											toDate)
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}

				}

			}

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
	
	@SuppressWarnings("unchecked")
	public List<Object> showItemWiseSalesDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}
			
			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;
			

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();
				
				List tempList = showSItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});
			
				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						rptObj.setDescription(methodsDao.getItemBalanceAtDate(
								itemObj.getId(), toDate)
								+ " "
								+ itemObj.getUnit().getSymbol());
						resultList.add(rptObj);
					}
				}
			} else {
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;
				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = showSItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							rptObj.setTotal(sum);
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDate(itemObj.getId(),
											toDate)
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}

				}

			}

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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> showItemWiseSalesDetailsConsolidated(long itemID, long custId, Date fromDate, Date toDate, long officeId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			String cdn="";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}
			if (itemID != 0) {
				cdn+=" and a.id="+itemID;
			}
			if (groupId != 0) {
				cdn+=" and sub_group.group.id=" + groupId;
			}
			List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts"+cdn)
										.setParameter("ofc", officeId)
										.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
										.list();
			if(itemsList.size()>0){
				Iterator itr=itemsList.iterator();
				while (itr.hasNext()) {
					ItemModel itemObj = (ItemModel) itr.next();
					
					List list=getSession().createQuery(" from SalesModel a join a.inventory_details_list b where date between :fromDate and :toDate and a.active=true and b.item.id=:item"
							+ condition1)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate)
							.setParameter("item", itemObj.getId()).list();
					if(list.size()>0)
						resultList.addAll(getSession().createQuery(
								"select new com.webspark.bean.ReportBean(b.item.id,b.item.name, coalesce(sum(b.quantity_in_basic_unit),0))" +
								" from SalesModel a join a.inventory_details_list b where date between :fromDate and :toDate and a.active=true and b.item.id=:item"
								+ condition1 + " order by a.date")
						.setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate)
						.setParameter("item", itemObj.getId()).list());
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
	
	public List getSItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name, a.customer.name, b.qunatity, b.quantity_in_basic_unit,cast(a.date as string),b.unit.symbol,b.unit_price,a.sales_number) from SalesModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	public List showSItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			
			begin();
			// Constr 58
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(a.id,b.item.name, a.customer.name, b.qunatity, b.quantity_in_basic_unit,a.date,b.unit.symbol,b.unit_price,a.sales_number) from SalesModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public double getSSum(Date fromDate, Date toDate, String condition)
			throws Exception {
		double sum = 0;
		try {

			Object obj = getSession()
					.createQuery(
							"select sum(b.qunatity) from SalesModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).uniqueResult();

			if (obj != null)
				sum = (Double) obj;

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return sum;
	}

	// Sales Order

	public List<Object> getItemWiseSalesOrderDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID ;/*+ " and a.status<="
						+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED;*/

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getSOItemWise(fromDate, toDate, condition1
						+ condition2);

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSOSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getQuantity();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
				//		rptObj.setDate(CommonUtil.)
						resultList.add(rptObj);
					}
				}
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId()
							/*+ " and a.status<="
							+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED*/;
					List tempList = getSOItemWise(fromDate, toDate, condition1
							+ condition2);

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSOSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getQuantity();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							resultList.add(rptObj);
						}
					}
				}
			}

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
	
	public List<Object> showItemWiseSalesOrderDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID; /*+ " and a.status<="
						+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED;*/

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = showSOItemWise(fromDate, toDate, condition1 + condition2);

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSOSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getQuantity();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						resultList.add(rptObj);
					}
				}
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit)" +
								" from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId()/*
							+ " and a.status<="
							+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED*/;
					List tempList = showSOItemWise(fromDate, toDate, condition1 + condition2);

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSOSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getQuantity();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							resultList.add(rptObj);
						}
					}
				}
			}

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

	// Customer Added Sales Order

	public List<Object> getItemWiseCustomerSalesOrderDetails(long itemID,
			long custId, Date fromDate, Date toDate, long officeId,long groupId)
			throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID + " and a.status>="
						+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED;

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getSOItemWise(fromDate, toDate, condition1
						+ condition2);

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getSOSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getQuantity();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						resultList.add(rptObj);
					}
				}
			} else {

				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;
				
				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId()
							+ " and a.status>="
							+ SConstants.statuses.SALES_ORDER_CUSTOMER_APPROVED;
					List tempList = getSOItemWise(fromDate, toDate, condition1
							+ condition2);

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getSOSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getQuantity();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							resultList.add(rptObj);
						}
					}
				}
			}

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

	public List getSOItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			// Constructor 55
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.id,b.item.name, a.customer.name, b.qunatity, b.qty_in_basic_unit,cast(a.date as string),b.unit.symbol)" +
							" from SalesOrderModel a join a.order_details_list b"
									+ " where date between :fromDate and :toDate and active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	public List showSOItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			//Constructor 55
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(a.id,b.item.name, a.customer.name, b.qunatity, b.qty_in_basic_unit,cast(a.date as string),b.unit.symbol)" +
							" from SalesOrderModel a join a.order_details_list b"
									+ " where date between :fromDate and :toDate and active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public double getSOSum(Date fromDate, Date toDate, String condition)
			throws Exception {
		double sum = 0;
		try {
			Object obj = getSession()
					.createQuery(
							"select sum(b.qunatity) from SalesOrderModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).uniqueResult();

			if (obj != null)
				sum = (Double) obj;

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return sum;
	}

	// Purchase

	@SuppressWarnings("unchecked")
	public List<Object> getItemWisePurchaseDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId, long contId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}
			
			if (contId != 0) {
				condition1 += " and b.container_no=" + contId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();

				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getPItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getPSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						ContainerModel cont=(ContainerModel)getSession().get(ContainerModel.class, rptObj.getContainer_no());
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						if(cont!=null)
							rptObj.setContainer(cont.getName());
						else
							rptObj.setContainer("");
						rptObj.setDescription(methodsDao.getItemBalanceAtDateNew(
								itemObj.getId(), toDate,itemObj.getOffice().getId())
								+ " "
								+ itemObj.getUnit().getSymbol());

						resultList.add(rptObj);
					}
				}
			} else {

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts")
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = getPItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getPSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							ContainerModel cont=(ContainerModel)getSession().get(ContainerModel.class, rptObj.getContainer_no());
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							if(cont!=null)
								rptObj.setContainer(cont.getName());
							else
								rptObj.setContainer("");
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDateNew(itemObj.getId(),
											toDate,itemObj.getOffice().getId())
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}
				}
			}
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
	
	@SuppressWarnings("unchecked")
	public List<Object> showItemWisePurchaseDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId, long contId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}
			
			if (contId != 0) {
				condition1 += " and b.container_no=" + contId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();

				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = showPItemWise(fromDate, toDate, condition1 + condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getPSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getTotal();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						rptObj.setDescription(methodsDao.getItemBalanceAtDateNew(
								itemObj.getId(), toDate,itemObj.getOffice().getId())
								+ " "
								+ itemObj.getUnit().getSymbol());
						resultList.add(rptObj);
					}
				}
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = showPItemWise(fromDate, toDate, condition1
							+ condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getPSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getTotal();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							rptObj.setDescription(methodsDao
									.getItemBalanceAtDateNew(itemObj.getId(),
											toDate,itemObj.getOffice().getId())
									+ " " + itemObj.getUnit().getSymbol());
							resultList.add(rptObj);
						}
					}
				}
			}
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> showItemWisePurchaseDetailsConsolidated(long itemID, long custId, Date fromDate, Date toDate, long officeId, long contId,long groupId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			String cdn="";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}
			
			if (itemID != 0) {
				cdn+=" and a.id="+itemID;
			}
			
			if (contId != 0) {
				condition1+=" and b.container_no="+contId;
			}
			
			if (groupId != 0) {
				cdn+=" and sub_group.group.id=" + groupId;
			}

				
			List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts"+cdn)
					.setParameter("ofc", officeId)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
			if(itemsList.size()>0){
				Iterator itr=itemsList.iterator();
				while (itr.hasNext()) {
					ItemModel itemObj = (ItemModel) itr.next();
					
					List list=getSession().createQuery("from PurchaseModel a join a.purchase_details_list b where date between :fromDate and :toDate and a.active=true and b.item.id=:item"
							+ condition1)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate)
							.setParameter("item", itemObj.getId()).list();
					
					if(list.size()>0)
						resultList.addAll(getSession().createQuery(
										"select new com.webspark.bean.ReportBean(b.item.id,b.item.name, coalesce(sum(b.qty_in_basic_unit),0)) " +
										"from PurchaseModel a join a.purchase_details_list b where date between :fromDate and :toDate and a.active=true and b.item.id=:item"
												+ condition1)
								.setParameter("fromDate", fromDate)
								.setParameter("toDate", toDate)
								.setParameter("item", itemObj.getId()).list());
					
				}
			}
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
	
	
	public List getPItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			// Constructor 26
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name, a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol, b.unit_price) from PurchaseModel a join a.purchase_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	public List showPItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
//			 Constructor 44 With Container
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(a.id,b.item.name, a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol, b.unit_price) from PurchaseModel a join a.purchase_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public double getPSum(Date fromDate, Date toDate, String condition)
			throws Exception {
		double sum = 0;
		try {
			Object obj = getSession()
					.createQuery(
							"select sum(b.qunatity) from PurchaseModel a join a.purchase_details_list b"
									+ " where date between :fromDate and :toDate  and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).uniqueResult();

			if (obj != null)
				sum = (Double) obj;

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return sum;
	}

	// Purchase Order

	public List<Object> getItemWisePurchaseOrderDetails(long itemID,
			long custId, Date fromDate, Date toDate, long officeId,long groupId)
			throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();

				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getPOItemWise(fromDate, toDate, condition1
						+ condition2);

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getPOSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getQuantity();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						resultList.add(rptObj);
					}
				}
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = getPOItemWise(fromDate, toDate, condition1
							+ condition2);

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getPOSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getQuantity();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							resultList.add(rptObj);
						}
					}
				}
			}
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
	
	public List<Object> showItemWisePurchaseOrderDetails(long itemID,
			long custId, Date fromDate, Date toDate, long officeId,long groupId)
			throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();

				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = showPOItemWise(fromDate, toDate, condition1
						+ condition2);

				Iterator itInr = tempList.iterator();
				if (itInr.hasNext()) {
					// double sum=getPOSum(fromDate, toDate,
					// condition1+condition2);
					double sum = 0;
					ReportBean rptObj;
					while (itInr.hasNext()) {
						rptObj = (ReportBean) itInr.next();
						sum += rptObj.getQuantity();
						rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
						rptObj.setTotal(sum);
						resultList.add(rptObj);
					}
				}
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = showPOItemWise(fromDate, toDate, condition1
							+ condition2);

					Iterator itInr = tempList.iterator();

					if (itInr.hasNext()) {
						// double sum=getPOSum(fromDate, toDate,
						// condition1+condition2);
						double sum = 0;
						ReportBean rptObj;
						while (itInr.hasNext()) {
							rptObj = (ReportBean) itInr.next();
							sum += rptObj.getQuantity();
							rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
							rptObj.setTotal(sum);
							resultList.add(rptObj);
						}
					}
				}
			}
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

	public List getPOItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name, a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol) from PurchaseOrderModel a join a.order_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	public List showPOItemWise(Date fromDate, Date toDate, String condition) /////////////////////////
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name,a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol,a.id) from PurchaseOrderModel a join a.order_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public double getPOSum(Date fromDate, Date toDate, String condition)
			throws Exception {
		double sum = 0;
		try {
			Object obj = getSession()
					.createQuery(
							"select sum(b.qunatity) from PurchaseOrderModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).uniqueResult();

			if (obj != null)
				sum = (Double) obj;

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return sum;
	}

	public List<Object> getSalesOrderDetails(long salesOrderId, long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {

		List<Object> list = null;

		try {

			begin();
			String condition = "";
			if (salesOrderId != 0) {
				condition += " and id=" + salesOrderId;
			}
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							"from SalesOrderModel where date>=:fromDate and date<=:toDate and active=true "// and (status=1 or status=2 or status=5)
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	public List<Object> getItemWiseSalesReturnDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId, int stockType,long groupId)
			throws Exception {
		List resultList = new ArrayList();

		try {
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}
			switch (stockType) {
//			case 1:
//				condition1 += " and b.good_stock!=0";
//				break;
//			case 2:
//				condition1 += " and b.stock_quantity!=0";
//				break;
			case 3:
				condition1 += " and b.returned_quantity!=0";
				break;
//			case 4:
//				condition1 += " and b.waste_quantity!=0";
//				break;

			default:
				break;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();
				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = getSRItemWise(fromDate, toDate, condition1
						+ condition2);
				ReportBean bean;
				double rate = 0;

				begin();
				Iterator iter = tempList.iterator();
				while (iter.hasNext()) {
					bean = (ReportBean) iter.next();
					Iterator itr4 = getSession()
							.createQuery(
									"select b.unit_price from SalesReturnModel a join a.inventory_details_list b where b.id = :ret and a.active=true and b.item.id=:itm")
							.setLong("ret", bean.getId())
							.setLong("itm", bean.getItem_id()).list()
							.iterator();
					if (itr4.hasNext()) {
						rate = (Double) itr4.next();
					}
					bean.setAmount(rate);
					bean.setType(stockType);
					resultList.add(bean);
				}
				
				commit();

				Collections.sort(resultList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});

			} else {
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				ReportBean bean;
				double rate = 0;

				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = getSRItemWise(fromDate, toDate, condition1
							+ condition2);

					
					begin();
					Iterator iter = tempList.iterator();
					while (iter.hasNext()) {
						bean = (ReportBean) iter.next();
						Iterator itr4 = getSession()
								.createQuery(
										"select b.unit_price from SalesReturnModel a join a.inventory_details_list b where b.id = :ret and a.active=true and b.item.id=:itm")
								.setLong("ret", bean.getId())
								.setLong("itm", bean.getItem_id()).list()
								.iterator();
						if (itr4.hasNext()) {
							rate = (Double) itr4.next();
						}
						bean.setAmount(rate);
						bean.setType(stockType);
						resultList.add(bean);
					}
					commit();

					Collections.sort(resultList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});
				}
			}

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
	
	public List<Object> showItemWisePurchaseReturnDetails(long itemID,
			long custId, Date fromDate, Date toDate, long officeId,long groupId)
			throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			String cdn="";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}
			if (itemID != 0) {
				cdn += " and a.id=" + itemID;
			}
			
			if (groupId != 0) {
				cdn+=" and sub_group.group.id=" + groupId;
			}
			
			List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts"+cdn)
					.setParameter("ofc", officeId)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
			if(itemsList.size()>0){
				Iterator itr=itemsList.iterator();
				while (itr.hasNext()) {
					ItemModel itemObj = (ItemModel) itr.next();
					
					List list=getSession().createQuery("from PurchaseReturnModel a join a.inventory_details_list b"
								+ " where date between :fromDate and :toDate and a.active=true and b.item.id=:item "
							+ condition1)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate)
							.setParameter("item", itemObj.getId()).list();
					
					if(list.size()>0)
						resultList.addAll(getSession().createQuery("select new com.webspark.bean.ReportBean(b.item.id, b.item.name, coalesce(sum(b.qty_in_basic_unit),0))"
								+ " from PurchaseReturnModel a join a.inventory_details_list b"
								+ " where date between :fromDate and :toDate and a.active=true and b.item.id=:item "
								+ condition1)
								.setParameter("fromDate", fromDate)
								.setParameter("item", itemObj.getId())
								.setParameter("toDate", toDate).list());
					
				}
			}
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
	
	
	

	@SuppressWarnings("unchecked")
	public List<Object> showItemWiseSalesReturnDetails(long itemID, long custId,
			Date fromDate, Date toDate, long officeId, int stockType,long groupId)
			throws Exception {
		List resultList = new ArrayList();

		try {
			
			begin();
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			String cdn="";
			if (custId != 0) {
				condition1 += " and a.customer.id=" + custId;
			}
			if (itemID != 0) {
				cdn += " and a.id=" + itemID;
			}
			if (groupId != 0) {
				cdn+=" and sub_group.group.id=" + groupId;
			}
			
			switch (stockType) {
//			case 1:
//				condition1 += " and b.good_stock!=0";
//				break;
//			case 2:
//				condition1 += " and b.stock_quantity!=0";
//				break;
			case 3:
				condition1 += " and b.returned_quantity!=0";
				break;
//			case 4:
//				condition1 += " and b.waste_quantity!=0";
//				break;

			default:
				break;
			}
				
			List itemsList = getSession().createQuery("from ItemModel a  where a.office.id=:ofc and a.status=:sts"+cdn)
					.setParameter("ofc", officeId)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
			if(itemsList.size()>0){
				Iterator itr=itemsList.iterator();
				while (itr.hasNext()) {
					ItemModel itemObj = (ItemModel) itr.next();
					
					List list=getSession().createQuery("from SalesReturnModel a join a.inventory_details_list b " +
							"where date between :fromDate and :toDate and a.active=true and b.item.id=:item"+ condition1)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate)
							.setParameter("item", itemObj.getId()).list();
					
					if(list.size()>0)
						resultList.addAll(getSession().createQuery(
										"select new com.webspark.bean.ReportBean("
												+ "b.item.id, b.item.name, " +
//												"coalesce(sum(b.quantity_in_basic_unit),0), " +
//												"coalesce(sum(b.quantity_in_basic_unit),0), " +
//												"coalesce(sum(b.quantity_in_basic_unit),0), " +
												"coalesce(sum(b.quantity_in_basic_unit),0)," +
												"b.unit.symbol)"
												+ " from SalesReturnModel a join a.inventory_details_list b"
												+ " where date between :fromDate and :toDate and a.active=true and b.item.id=:item"
												+ condition1)
								.setParameter("fromDate", fromDate)
								.setParameter("item", itemObj.getId())
								.setParameter("toDate", toDate).list());
					
				}
			}
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
	
	public List getSRItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			
			// Report Bean Constructor 61
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean("
									+ "b.item.name, a.customer.name, b.qunatity, b.quantity_in_basic_unit, b.qunatity,0.0,a.date,b.unit.symbol,b.unit_price," +
									"a.return_no,b.id,b.item.id,a.netCurrencyId.id)"
									+ " from SalesReturnModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition + " order by a.date")
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}

		return list;
	}

	public List<Object> getItemWisePurchaseReturnDetails(long itemID,
			long custId, Date fromDate, Date toDate, long officeId,long groupId)
			throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {

			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (custId != 0) {
				condition1 += " and a.supplier.id=" + custId;
			}

			if (itemID != 0) {
				condition2 = " and b.item.id=" + itemID;

				begin();

				ItemModel itemObj = (ItemModel) getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.id=:id")
						.setLong("id", itemID).uniqueResult();
				commit();

				List tempList = showPRetItemWise(fromDate, toDate, condition1
						+ condition2);

				Collections.sort(tempList, new Comparator<ReportBean>() {
					@Override
					public int compare(final ReportBean object1,
							final ReportBean object2) {

						int result = object1.getDt().compareTo(object2.getDt());
						if (result == 0) {
							result = object1
									.getItem_name()
									.toLowerCase()
									.compareTo(
											object2.getItem_name()
													.toLowerCase());
						}
						return result;
					}

				});
				resultList.addAll(tempList);
				Iterator itInr = tempList.iterator();
				// if(itInr.hasNext()) {
				// // double sum=getPSum(fromDate, toDate,
				// condition1+condition2);
				// double sum=0;
				// ReportBean rptObj;
				// while(itInr.hasNext()) {
				// rptObj=(ReportBean) itInr.next();
				// sum+=rptObj.getTotal();
				// rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
				// rptObj.setTotal(sum);
				// rptObj.setDescription(methodsDao.getItemBalanceAtDate(itemObj.getId(),
				// toDate)+" "+itemObj.getUnit().getSymbol());
				//
				// resultList.add(rptObj);
				// }
				// }
			} else {
				
				String con="";
				if (groupId != 0) 
					con = " and a.sub_group.group.id=" + groupId;

				begin();
				List itemsList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(a.id,a.unit) from ItemModel a  where a.office.id=:ofc and a.status=:sts"+con)
						.setParameter("ofc", officeId)
						.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
						.list();

				commit();

				Iterator it = itemsList.iterator();
				while (it.hasNext()) {
					ItemModel itemObj = (ItemModel) it.next();

					condition2 = " and b.item.id=" + itemObj.getId();
					List tempList = showPRetItemWise(fromDate, toDate,
							condition1 + condition2);

					Collections.sort(tempList, new Comparator<ReportBean>() {
						@Override
						public int compare(final ReportBean object1,
								final ReportBean object2) {

							int result = object1.getDt().compareTo(
									object2.getDt());
							if (result == 0) {
								result = object1
										.getItem_name()
										.toLowerCase()
										.compareTo(
												object2.getItem_name()
														.toLowerCase());
							}
							return result;
						}

					});

					resultList.addAll(tempList);
					// Iterator itInr=tempList.iterator();
					//
					// if(itInr.hasNext()) {
					// // double sum=getPSum(fromDate, toDate,
					// condition1+condition2);
					// double sum=0;
					// ReportBean rptObj;
					// while(itInr.hasNext()) {
					// rptObj=(ReportBean) itInr.next();
					// sum+=rptObj.getTotal();
					// rptObj.setBasic_unit(itemObj.getUnit().getSymbol());
					// rptObj.setTotal(sum);
					// rptObj.setDescription(methodsDao.getItemBalanceAtDate(itemObj.getId(),
					// toDate)+" "+itemObj.getUnit().getSymbol());
					// resultList.add(rptObj);
					// }
					// }
				}
			}
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

	public List getPRetItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean"
									+ "(b.item.name, a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol, b.unit_price,a.debit_note_no)"
									+ " from PurchaseReturnModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}
	
	public List showPRetItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			//Constructor 58
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean"
									+ "(a.id,b.item.name, a.supplier.name, b.qunatity, b.qty_in_basic_unit,a.date,b.unit.symbol, b.unit_price,a.return_no)"
									+ " from PurchaseReturnModel a join a.inventory_details_list b"
									+ " where date between :fromDate and :toDate and a.active=true "
									+ condition)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}

		return list;
	}

	public List getSalesNoFromPayment(Date from, Date toDate, long office,
			long custId) throws Exception {
		List list = null;
		String cond = "";
		if (custId != 0)
			cond = " and from_account_id= " + custId;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select sales_ids from PaymentModel where active=true and type="
									+ SConstants.CUSTOMER_PAYMENTS
									+ " and office.id=:ofc and date between :frm and :todate"
									+ cond).setParameter("ofc", office)
					.setParameter("frm", from).setParameter("todate", toDate)
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

		List longList = new ArrayList();
		if (list != null) {
			String[] str = null;
			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				str = iter.next().split(",");

				if (str != null && str.length > 0) {
					for (String st : str) {
						if (st != null && st.length() > 0)
							longList.add(Long.parseLong(st));
					}
				}

			}

		}

		return longList;
	}

	public String getPaymentDateOfSales(long salesId) throws Exception {
		String payDate = "";
		try {
			begin();
			List obj = getSession()
					.createQuery(
							"select a.date from PaymentModel a, PaymentInvoiceMapModel b where b.paymentId=a.id and b.invoiceId=:inv and b.type="
									+ SConstants.CUSTOMER_PAYMENTS)
					.setParameter("inv", salesId).list();
			commit();

			if (obj != null && obj.size() > 0)
				payDate = obj.get(0).toString();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return payDate;
	}
	
	
	public List<Object> getCustomerGroupWiseSalesDetails(long groupId,long orgId,long officeId,
			Date fromDate, Date toDate,  String condition1) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			if (groupId != 0) {
				List custList = getSession()
						.createQuery(
								"select a.ledger.id from CustomerModel a where  a.customerGroupId=:grp")
						.setParameter("grp", groupId).list();
						
				if(custList!=null&&custList.size()>0)
					condition += " and customer.id in(" + custList.toString().replace('[', ' ').replace(']', ' ')+")";
			}
			list = getSession()
					.createQuery(
							"from SalesModel where date>=:fromDate and date<=:toDate   and (type=0 or type=1)"
									+ condition
									+ " and office.organization.id=:orgId order by date desc")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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

	
	public List<Object> getCustomerGroupWiseConsolidatedSalesDetails(long groupId,long orgId,long officeId,
			Date fromDate, Date toDate,  String condition1) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			if (groupId != 0) {
					condition += " and b.id ="+groupId;
			}
			
			
			//Constructor #7
			list = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.SalesReportBean(b.id,b.name,coalesce(sum(a.amount),0)," +
							"coalesce(sum(a.payment_amount+a.paid_by_payment),0),coalesce(sum(a.amount-(a.payment_amount+a.paid_by_payment)),0)) " +
							"from SalesModel a,CustomerGroupModel b,CustomerModel c " +
							"where  a.customer.id=c.ledger.id and c.customerGroupId=b.id and a.date between :fromDate and :toDate  " +
							" and (a.type=0 or a.type=1)"
									+ condition
									+ " and a.office.organization.id=:orgId group by c.customerGroupId order by b.name")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
			
			
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
	
	public List<Object> getSalesManWiseConsolidatedSalesDetails( long custId,
			Date fromDate, Date toDate, long officeId, String condition1,
			long orgId, long salesManId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = condition1;
			if (custId != 0) {
				condition += " and a.customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			if (salesManId != 0) {
				condition += " and a.responsible_person=" + salesManId;
			}
			
			//Constructor #9
			list = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.SalesReportBean(b.first_name,a.responsible_employee,a.customer.id,a.customer.name,coalesce(sum(a.amount),0)," +
							"coalesce(sum(a.payment_amount+a.paid_by_payment),0),coalesce(sum(a.amount-(a.payment_amount+a.paid_by_payment)),0)) " +
							"from SalesModel a, UserModel b where a.responsible_employee=b.loginId.id  and a.date between :fromDate and :toDate   and (a.sales_type=0 or a.sales_type=1)"
									+ condition
									+ " and a.office.organization.id=:orgId group by a.customer.id order by b.first_name,a.customer.name")
					.setParameter("orgId", orgId)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate).list();
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
	
	
	
	public List<QuotationModel> getAllSalesQuotationForOffice(long office_id, Date fromDate,
			Date toDate) throws Exception{
		
		List resultList = null;
		try {
			begin();
		
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.QuotationModel(a.id,concat(a.quotation_no, '  - ', a.customer.name, ' - ',a.date) )"
									+ " from QuotationModel a join a.quotation_details_list b" +
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
	public List<QuotationModel> getAllSalesQuotationForCustomer(
			long supplier_id, long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.QuotationModel(a.id,concat(a.quotation_no, '  - ', a.customer.name, ' - ',a.date) )"
									+ " from QuotationModel a join a.quotation_details_list b" +
									" where a.office.id=:ofc and a.customer.id=:sup and a.active=true" +
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
	
	
	
	public List<Object> getSalesQuotationDetails(long id, long customerId,
			Date fromDate, Date toDate, long officeId)
					throws Exception {
		List<Object> list = null;
		
		try {
			begin();
			
			String condition = "";
			if (id != 0) {
				condition += " and id=" + id;
			}
			if (customerId != 0) {
				condition += " and customer.id=" + customerId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from QuotationModel where date between :fromDate and :toDate and active=true "
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
	
	
	public List<SalesInquiryModel> getAllSalesInquiryForOffice(long office_id, Date fromDate,
			Date toDate) throws Exception{
		
		List resultList = null;
		try {
			begin();
		
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesInquiryModel(a.id,concat(a.inquiry_no, '  - ', a.customer.name, ' - ',a.date) )"
									+ " from SalesInquiryModel a join a.sales_inquiry_details_list b" +
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
	
	public List<SalesInquiryModel> getAllSalesInquiryForCustomer(
			long customer_id, long office_id, Date fromDate,
			Date toDate) throws Exception{
		List resultList = null;
		try {
			begin();
			
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesInquiryModel(a.id,concat(a.inquiry_no, '  - ', a.customer.name, ' - ',a.date) )"
									+ " from SalesInquiryModel a join a.sales_inquiry_details_list b" +
									" where a.office.id=:ofc and a.customer.id=:sup and a.active=true" +
									" and a.date between :from_date and :to_date"+
									" group by a.id")
					//and b.balance>0 
									.setParameter("ofc", office_id)
									.setParameter("sup", customer_id)
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
	
	
	public List<Object> getSalesInquiryDetails(long id, long customerId,
			Date fromDate, Date toDate, long officeId)
					throws Exception {
		List<Object> list = null;
		
		try {
			begin();
			
			String condition = "";
			if (id != 0) {
				condition += " and id=" + id;
			}
			if (customerId != 0) {
				condition += " and customer.id=" + customerId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			list = getSession()
					.createQuery(
							" from SalesInquiryModel where date between :fromDate and :toDate and active=true "
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
	

	
}

