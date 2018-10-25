package com.inventory.tailoring.report.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.stock.model.ItemModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T. 
 * 
 *  Dec 19, 2014
 */
public class OrderwiseMaterialReportDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6241049407965920098L;

	CommonMethodsDao methodsDao = new CommonMethodsDao();


	// Added By Jinshad On 20 Nov 2013

	@SuppressWarnings("unchecked")
	public List<Object> getItemWiseSalesDetails(long itemID, long orderId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			
			String condition1 = " and a.office.id=" + officeId, condition2 = "";
			if (orderId != 0) {
				condition1 += " and a.orderId=" + orderId;
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
						rptObj.setDescription(methodsDao.getItemBalanceAtDate(
								itemObj.getId(), toDate)
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
	
	

	public List getSItemWise(Date fromDate, Date toDate, String condition)
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(b.item.name, c.customer.name, b.qunatity, b.quantity_in_basic_unit,a.date," +
							"b.unit.symbol,b.unit_price,c.sales_number) from MaterialMappingModel a,TailoringSalesModel c join a.inventory_details_list b"
									+ " where c.id=a.orderId and a.date between :fromDate and :toDate "
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
	

}
