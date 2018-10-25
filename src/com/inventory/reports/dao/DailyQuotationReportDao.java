package com.inventory.reports.dao;

import java.sql.Date;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 18, 2013
 */
public class DailyQuotationReportDao extends SHibernate {

	private static final long serialVersionUID = -4196914803510839554L;
	private List resultList;

	public List getUsersWithFullNameAndCode(long officeId) throws Exception {
		try {
			String condition = "";
			if (officeId > 0) {
				condition = " and loginId.office.id=" + officeId;
			}
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(loginId.id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) from UserModel "
									+ "where 1=1"
									+ condition
									+ " order by first_name").list();
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
		return resultList;
	}

	public List getAllActiveSuppliers(long officeId) throws Exception {
		try {

			String condition = "";
			if (officeId > 0) {
				condition = " and ledger.office.id=" + officeId;
			}

			begin();
			resultList = getSession()
					.createQuery(
							"from SupplierModel where  ledger.status=:val "
									+ condition)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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
		}
		return resultList;
	}

	public List getAllActiveItemsWithAppendingItemCode(long officeId)
			throws Exception {

		try {
			String condition = "";
			if (officeId > 0) {
				condition = " and office.id=" + officeId;
			}

			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' ) '))"
									+ " from ItemModel  where status=:sts "
									+ condition + " order by name")
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
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
		}
		return resultList;
	}

	public List getAllQuotations(long orgId,long office,Long user, Long supplier, Long item,
			Date fromDate, Date toDate,long countryId) throws Exception {
		try {

			String condition = "";
			if (user > 0) {
				condition += " and a.login.id=" + user;
			}
			if (supplier > 0) {
				condition += " and b.supplier.id=" + supplier;
			}
			if (item > 0) {
				condition += " and b.item.id=" + item;
			}
			if (office > 0) {
				condition += " and a.login.office.id=" + office;
			}
			if (countryId > 0) {
				condition += " and b.countryId=" + countryId;
			}

			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.DailyQuotationBean(cast (a.date as string),"
									+ "(select first_name from UserModel where loginId.id=a.login.id), b.supplier.name, b.item.name,b.unit.symbol," +
									" b.rate,(select ct.name from CountryModel ct where ct.id=b.countryId),a.login.id) "
									+ " from DailyQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate " +
									" and  a.login.office.organization.id=:org"
									+ condition + " order by a.date desc")
					.setParameter("fromDate", fromDate).setParameter("org", orgId)
					.setParameter("toDate", toDate).list();
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
	
	public List showAllQuotations(long orgId,long office,Long user, Long supplier, Long item,
			Date fromDate, Date toDate,long countryId,int sort) throws Exception {
		try {

			String condition = "";
			if (user > 0) {
				condition += " and a.login.id=" + user;
			}
			if (supplier > 0) {
				condition += " and b.supplier.id=" + supplier;
			}
			if (item > 0) {
				condition += " and b.item.id=" + item;
			}
			if (office > 0) {
				condition += " and a.login.office.id=" + office;
			}
			if (countryId > 0) {
				condition += " and b.countryId=" + countryId;
			}
			if(sort==1){
				condition+=" order by b.item.name";
			}
			else{
				condition+=" order by a.login.login_name";
			}
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.DailyQuotationBean(cast (a.date as string),"
									+ "(select first_name from UserModel where loginId.id=a.login.id), b.supplier.name, b.item.name,b.unit.symbol," +
									" b.rate,(select ct.name from CountryModel ct where ct.id=b.countryId),a.login.id) "
									+ " from DailyQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate " +
									" and  a.login.office.organization.id=:org"
									+ condition + " ,a.date desc")
					.setParameter("fromDate", fromDate).setParameter("org", orgId)
					.setParameter("toDate", toDate).list();
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
}
