package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.sales.model.SalesModel;
import com.webspark.dao.SHibernate;

public class ExceptionReportDao extends SHibernate implements Serializable{
	List resultList = new ArrayList();
	
	@SuppressWarnings("unchecked")
	public List<Object> getSalesManWiseSalesDetails( long itemSubGrpId,
			Date fromDate, Date toDate, long officeId, long salesManId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			String condition = "";
			String condn="";
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
				condn += " and c.office.id=" + officeId;
			}
			if (salesManId != 0) {
				condition += " and responsible_person=" + salesManId;
				condn += " and c.responsible_person=" + salesManId;
			}
			
			
			list = getSession()
					.createQuery(
							"from SalesModel where date between :fromDate and :toDate "+condition	+ " ")
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
	public List<Object> getItemGrpSalesDetails( long itemSubGrpId,long salesId) throws Exception {
		List<Object> list = null;

		try {

			begin();

			list = getSession()
					.createQuery(
							"select b from SalesModel a join a.SalesInventoryDetailsModel b where b.item.sub_group.id=:subGrpid  and a.id=:salesId")
					.setParameter("subGrpid", itemSubGrpId)
					.setParameter("salesId", salesId).list();
			
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
