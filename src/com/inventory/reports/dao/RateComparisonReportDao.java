package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 2, 2014
 */
public class RateComparisonReportDao extends SHibernate {

	private static final long serialVersionUID = 6511427674844080060L;

	public List getComparisonReport(ItemUnitMangementModel unitMangementModel,
			long employee, long supplier, Date fromDt, Date toDt)
			throws Exception {
		List<Object> list = new ArrayList();
		try {
			begin();

			String condition1 = "";
			String condition2 = "";

			if (supplier != 0) {
				condition1 += " and a.login_id= (select login_id from SupplierModel where id="
						+ supplier + ")";
				condition2 += "and b.supplier.id=" + supplier;
			}

			if (employee != 0) {
				condition2 += " and a.login.id= (select loginId.id from UserModel where id="
						+ employee + ")";
			}

			// query1 =
			// " from SupplierQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate and b.item.id=:itm and b.unit.id=:unit"
			// + condition1;

			list.addAll(getSession()
					.createQuery(
							" select new com.inventory.reports.bean.RateComparisonReportBean(b.item.name,b.unit.symbol,b.rate,"
									+ "(select name from SupplierModel where login_id=a.login_id),cast(a.date as string))"
									+ " from SupplierQuotationModel a join a.quotation_details_list b"
									+ " where a.date between :fromDate and :toDate and b.item.id=:itm and b.unit.id=:unit"
									+ condition1)
					.setParameter("fromDate", fromDt)
					.setParameter("toDate", toDt)
					.setParameter("unit", unitMangementModel.getAlternateUnit())
					.setParameter("itm", unitMangementModel.getItem().getId())
					.list());

			list.addAll(getSession()
					.createQuery(
							"select new com.inventory.reports.bean.RateComparisonReportBean(b.item.name,b.unit.symbol,0.0,'',b.rate,"
									+ "(select first_name from UserModel where loginId.id=a.login.id),cast(a.date as string))"
									+ " from DailyQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate and b.item.id=:itm and b.unit.id=:unit"
									+ condition2)
					.setParameter("fromDate", fromDt)
					.setParameter("toDate", toDt)
					.setParameter("unit", unitMangementModel.getAlternateUnit())
					.setParameter("itm", unitMangementModel.getItem().getId())
					.list());

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

	public List getItems(long office,Date fromDt, Date toDt) throws Exception {
		List<Object> list = null;
		try {
			begin();

			String condition1 = "";
			String query = "";

			if (office != 0) {
				condition1 += " and item.office.id=" + office;
			}

			list = getSession()
					.createQuery(
							"from ItemUnitMangementModel where item.id in " +
							"(select b.item.id from SupplierQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate) " +
							" or item.id in (select b.item.id from DailyQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate)"
									+ condition1 + " group by item.id,alternateUnit order by item.name").setParameter("fromDate", fromDt)
									.setParameter("toDate", toDt)
					.list();

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

	public List getComparisonSupplierReport(long officeId,
			ItemUnitMangementModel unitMangementModel, long supplier,
			Date fromDt, Date toDt) throws Exception {
		List<Object> list = new ArrayList();
		try {
			begin();

			String condition1 = "";

			if (supplier != 0) {
				condition1 += " and a.login_id= (select login_id from SupplierModel where id="
						+ supplier + ")";
			}
			if (officeId != 0) {
				condition1 += " and b.item.office.id="+officeId;
			}

			list.addAll(getSession()
					.createQuery(
							" select new com.inventory.reports.bean.RateComparisonReportBean(b.item.name,b.unit.symbol,b.rate,"
									+ "(select name from SupplierModel where login_id=a.login_id),0.0,'',cast(a.date as string))"
									+ " from SupplierQuotationModel a join a.quotation_details_list b"
									+ " where a.date between :fromDate and :toDate and b.item.id=:itm and b.unit.id=:unit"
									+ condition1
									+ " group by a.date,b.item.name,b.unit.symbol,a.login_id order by a.date,b.item.name")
					.setParameter("fromDate", fromDt)
					.setParameter("toDate", toDt)
					.setParameter("unit", unitMangementModel.getAlternateUnit())
					.setParameter("itm", unitMangementModel.getItem().getId())
					.list());

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

	public List getComparisonEmplReport(long officeId,
			ItemUnitMangementModel unitMangementModel, long employee,
			long supplier, Date fromDt, Date toDt) throws Exception {
		List<Object> list = new ArrayList();
		try {
			begin();

			String condition2 = "";

			if (supplier != 0) {
				condition2 += " and b.supplier.id=" + supplier;
			}

			if (employee != 0) {
				condition2 += " and a.login.id= (select loginId.id from UserModel where id="
						+ employee + ")";
			}
			if (officeId != 0) {
				condition2 += " and b.item.office.id="+officeId;
			}

			list.addAll(getSession()
					.createQuery(
							"select new com.inventory.reports.bean.RateComparisonReportBean(b.item.name,b.unit.symbol,b.rate,"
									+ "(select first_name from UserModel where loginId.id=a.login.id),cast(a.date as string))"
									+ " from DailyQuotationModel a join a.quotation_details_list b where a.date between :fromDate and :toDate and b.item.id=:itm and b.unit.id=:unit"
									+ condition2
									+ " group by a.date,b.item.name,b.unit.symbol,b.supplier.id order by a.date,b.item.name ")
					.setParameter("fromDate", fromDt)
					.setParameter("toDate", toDt)
					.setParameter("unit", unitMangementModel.getAlternateUnit())
					.setParameter("itm", unitMangementModel.getItem().getId())
					.list());

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

}
