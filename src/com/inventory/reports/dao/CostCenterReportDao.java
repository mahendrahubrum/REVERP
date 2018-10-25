package com.inventory.reports.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import com.inventory.reports.bean.CostCenterBean;
import com.webspark.bean.SelectionFieldBean;
import com.webspark.dao.SHibernate;
import com.webspark.uac.dao.DivisionDao;
import com.webspark.uac.model.DivisionModel;

public class CostCenterReportDao extends SHibernate {
	private List<CostCenterBean> reportList;
	private HashMap<Long, Integer> idAndIndexMap;
	private int arrayIndex;
//	private int mapId;

	@SuppressWarnings("unchecked")
	public List<?> generateCostCenterReport(long orgId, long officeId, Date fromDate, Date toDate) throws Exception {
		reportList = new ArrayList<CostCenterBean>();
		idAndIndexMap = new HashMap<Long, Integer>();
		
		arrayIndex = 0;
//		mapId = 0;
		
		List<SelectionFieldBean> divisionList = new DivisionDao().getDivisionsHierarchy(orgId);
	//	long tableId = 0;
		Iterator<SelectionFieldBean> itr = divisionList.iterator();
		CostCenterBean bean = null;
		CostCenterBean prevBean = null;
		while(itr.hasNext()){
			SelectionFieldBean divModel = itr.next();
			bean = new CostCenterBean();
			bean.setId(divModel.getId());
			bean.setName(divModel.getValue());
			bean.setParentId(divModel.getParentId());
			bean.setCreditAmount(0);
			bean.setDebitAmount(0);
			bean.setBalance(0);
			bean.setTypeId(2);
			
			if(bean.getParentId() != 0 && prevBean != null) {
				if(prevBean.getParentId() == 0){
					prevBean.setTypeId(0);
				} else if(prevBean.getId() == bean.getParentId()){
					prevBean.setTypeId(1);
				} else {
					prevBean.setTypeId(2);
				}
			} else {
				bean.setTypeId(0);
			}
			prevBean = bean;	
			reportList.add(bean);
			idAndIndexMap.put(divModel.getId(), arrayIndex);
			arrayIndex++;			
		}
		
		Iterator<CostCenterBean> conItr = reportList.iterator();
		
		while(conItr.hasNext()){
			bean = conItr.next();
			loadCostCenterDetails(bean.getId(),bean.getId(), bean.getParentId(), officeId, fromDate, toDate);
		}
		return reportList;
	}

	
	private void loadCostCenterDetails(long id, long divisionId, long parentId,long officeId, Date fromDate, Date toDate) throws Exception{
			CostCenterBean bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean("
							+ "COALESCE(SUM(b.amount), 0),"
							+ "0.0)"+
							//"CAST("+parentId+" AS LONG))" +
									" FROM BankAccountDepositModel a JOIN a.bank_account_deposit_list b"
							+ " WHERE b.divisionId = :divisionId"
							+ " AND a.date BETWEEN :from_date AND :to_date" +
							" AND a.office_id = :office_id" +
							" GROUP BY b.divisionId")
//							.setString("div_name", divName)
//							.setLong("parent_id", parentId)
							.setLong("divisionId", divisionId)
							.setDate("from_date", fromDate)
							.setDate("to_date", toDate)
							.setLong("office_id", officeId).uniqueResult();		
				
				addDataToReportList(id, parentId, bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean("
						+ "0.0,"
						+ "COALESCE(SUM(b.amount), 0))"+
								" FROM BankAccountPaymentModel a JOIN a.bank_account_payment_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean("
						+ "0.0,"
						+ "COALESCE(SUM(a.amount - a.expenseCreditAmount), 0))"+
								" FROM SalesModel a"
						+ " WHERE a.division_id = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office.id = :office_id" +
						" GROUP BY a.division_id")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean("
						+ "COALESCE(SUM(a.amount - a.expenseCreditAmount), 0),"
						+ "0.0)"+
								" FROM PurchaseModel a"
						+ " WHERE a.division_id = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office.id = :office_id" +
						" GROUP BY a.division_id")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean("
						+ "COALESCE(SUM(b.amount), 0)," +
						"0.0)"+
								" FROM CashAccountDepositModel a JOIN a.cash_account_deposit_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean(" +
						"0.0,"
						+ "COALESCE(SUM(b.amount), 0))"+
								" FROM CashAccountPaymentModel a JOIN a.cash_account_payment_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean(" +
						"0.0,"
						+ "COALESCE(SUM(b.amount), 0))"+
								" FROM CreditNoteModel a JOIN a.credit_note_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean(" +
						"COALESCE(SUM(b.amount), 0)," +
						"0.0)"+
								" FROM DebitNoteModel a JOIN a.debit_note_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				bean = (CostCenterBean) getSession().createQuery("SELECT new com.inventory.reports.bean.CostCenterBean(" +
						"COALESCE(SUM(b.amount), 0)," +
						"0.0)"+
								" FROM PdcPaymentModel a JOIN a.pdc_payment_list b"
						+ " WHERE b.divisionId = :divisionId"
						+ " AND a.date BETWEEN :from_date AND :to_date" +
						" AND a.office_id = :office_id" +
						" GROUP BY b.divisionId")
						
						.setLong("divisionId", divisionId)
						.setDate("from_date", fromDate)
						.setDate("to_date", toDate)
						.setLong("office_id", officeId).uniqueResult();
				
				addDataToReportList(id, parentId,bean);
				//================================================================================================
				
				
				if(parentId > 0){
					DivisionModel subModel = (DivisionModel) getSession().createQuery(
							"FROM DivisionModel WHERE id = :div_id")
							.setLong("div_id", parentId).uniqueResult();
					loadCostCenterDetails(parentId, divisionId, subModel.getParent_id(), officeId, fromDate, toDate);
				}
				
	}


	private void addDataToReportList(long id, long parentId, CostCenterBean bean) throws Exception{		
		CostCenterBean tempBean;
		if(bean != null){
			tempBean = reportList.get(idAndIndexMap.get(id));
			tempBean.setCreditAmount(tempBean.getCreditAmount() + bean.getCreditAmount());
			tempBean.setDebitAmount(tempBean.getDebitAmount() + bean.getDebitAmount());				
		}
	}
}
