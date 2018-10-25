package com.inventory.transaction.biz;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.IDGeneratorDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 *
 * Jul 24, 2013
 */

public class FinTransaction {
	
	WrappedSession session= new SessionUtil().getHttpSession();
	
	public List<TransactionDetailsModel> detailsList=new ArrayList<TransactionDetailsModel>();
	
	public void addTransaction(int cr_or_dr, long fromAcct, long toAcct, double amount){
		
		TransactionDetailsModel detailsObj=new TransactionDetailsModel();
		detailsObj.setAmount(amount);
		detailsObj.setFromAcct(new LedgerModel(fromAcct));
		detailsObj.setToAcct(new LedgerModel(toAcct));
		detailsObj.setSi_no(detailsList.size()+1);
		detailsObj.setType(cr_or_dr);
		detailsObj.setCurrencyId(Long.parseLong(session.getAttribute("currency_id").toString()));
		detailsObj.setConversionRate(1);
		detailsObj.setDepartmentId(0);
		detailsObj.setDivisionId(0);
		
		detailsList.add(detailsObj);
	}
	
	public void addTransactionWithNarration(int cr_or_dr, long fromAcct, long toAcct, double amount, String narration){
		
		TransactionDetailsModel detailsObj=new TransactionDetailsModel();
		detailsObj.setAmount(amount);
		detailsObj.setFromAcct(new LedgerModel(fromAcct));
		detailsObj.setToAcct(new LedgerModel(toAcct));
		detailsObj.setSi_no(detailsList.size()+1);
		detailsObj.setType(cr_or_dr);
		detailsObj.setNarration(narration);
		detailsObj.setCurrencyId(Long.parseLong(session.getAttribute("currency_id").toString()));
		detailsObj.setConversionRate(1);
		detailsObj.setDepartmentId(0);
		detailsObj.setDivisionId(0);
		
		detailsList.add(detailsObj);
	}
	
	public void addTransaction(int cr_or_dr, long fromAcct, long toAcct, double amount, String narration, long currencyId, double conversionRate){
		
		TransactionDetailsModel detailsObj=new TransactionDetailsModel();
		detailsObj.setAmount(amount);
		detailsObj.setFromAcct(new LedgerModel(fromAcct));
		detailsObj.setToAcct(new LedgerModel(toAcct));
		detailsObj.setSi_no(detailsList.size()+1);
		detailsObj.setType(cr_or_dr);
		detailsObj.setCurrencyId(currencyId);
		detailsObj.setConversionRate(conversionRate);
		detailsObj.setDepartmentId(0);
		detailsObj.setDivisionId(0);
		
		detailsList.add(detailsObj);
	}
	public void addTransaction(int cr_or_dr, long fromAcct, long toAcct, double amount, String narration, long currencyId, double conversionRate,long departmentId,long divisionId){
		
		TransactionDetailsModel detailsObj=new TransactionDetailsModel();
		detailsObj.setAmount(amount);
		detailsObj.setFromAcct(new LedgerModel(fromAcct));
		detailsObj.setToAcct(new LedgerModel(toAcct));
		detailsObj.setSi_no(detailsList.size()+1);
		detailsObj.setType(cr_or_dr);
		detailsObj.setCurrencyId(currencyId);
		detailsObj.setConversionRate(conversionRate);
		detailsObj.setDepartmentId(departmentId);
		detailsObj.setDivisionId(divisionId);
		
		detailsList.add(detailsObj);
	}
	
	
	public TransactionModel getTransaction(int transaction_type, Date date) throws Exception {
		long loginID=(Long) session.getAttribute("login_id");
		
		TransactionModel transObj=new TransactionModel();
		transObj.setDate(date);
		transObj.setLogin_id(loginID);
		transObj.setOffice(new S_OfficeModel((Long) session.getAttribute("office_id")));
		transObj.setStatus(1);
		transObj.setTransaction_id(new IDGeneratorDao().generateID("transaction_id", loginID, transObj.getOffice().getId(), 
				(Long) session.getAttribute("organization_id")));
		
		transObj.setTransaction_type(transaction_type);
		
		transObj.setTransaction_details_list(detailsList);
		
		return transObj;
	}
	
	
	public TransactionModel getTransactionWithoutID(int transaction_type, Date date) throws Exception {
		long loginID=(Long) session.getAttribute("login_id");
		
		TransactionModel transObj=new TransactionModel();
		transObj.setDate(date);
		transObj.setLogin_id(loginID);
		transObj.setOffice(new S_OfficeModel((Long) session.getAttribute("office_id")));
		transObj.setStatus(1);
		
		transObj.setTransaction_type(transaction_type);
		
		transObj.setTransaction_details_list(detailsList);
		
		return transObj;
	}
	
	
	public List getChildList() {
		return detailsList;
	}

}
