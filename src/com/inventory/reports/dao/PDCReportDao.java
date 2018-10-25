package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class PDCReportDao extends SHibernate implements Serializable {
	
	@SuppressWarnings("rawtypes")
	public List getPdcReport(long pdc, long office, Date start, Date end) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			String cdn="";
			if(pdc!=0){
				cdn+=" and a.id="+pdc;
			}
			resultList=getSession().createQuery("select new com.inventory.reports.bean.PdcReportBean(a.id, a.bill_no, a.bankAccount.name, a.chequeDate) " +
								" from PdcModel a where a.chequeDate between :start and :end and a.office_id=:office"+cdn)
									.setParameter("office", office).setParameter("start", start).setParameter("end", end).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getPdcChildReport(long pdc, long office, Date start, Date end) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			String cdn="";
			if(pdc!=0){
				cdn+=" and a.id="+pdc;
			}
			resultList=getSession().createQuery("select new com.inventory.reports.bean.PdcReportBean(a.bill_no, a.bankAccount.name, " +
					" b.chequeNo, b.currencyId.code , a.chequeDate, b.issueDate, b.status, b.amount, b.conversionRate) " +
								" from PdcModel a join a.pdc_list b where a.chequeDate between :start and :end and a.office_id=:office"+cdn)
									.setParameter("office", office).setParameter("start", start).setParameter("end", end).list();
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
	

}
