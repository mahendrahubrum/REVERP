package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.LoanApprovalModel;
import com.inventory.payroll.model.LoanDateModel;
import com.inventory.payroll.model.LoanRequestModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.CurrencyModel;

@SuppressWarnings("serial")
public class LoanApprovalDao extends SHibernate implements Serializable {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public long save(LoanApprovalModel mdl, LoanRequestModel reqMdl, TransactionModel trans) throws Exception {
		try {
			begin();
			List oldTransList=new ArrayList();
			List oldTransIdList=new ArrayList();
			
			if(mdl.getTransactionId()!=0){
				oldTransList=getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
						.setParameter("id", mdl.getTransactionId()).list();
				
				Iterator transItr=oldTransList.iterator();
				while (transItr.hasNext()) {
					TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

					oldTransIdList.add(tdm.getId());
					flush();
				}
			}
			long id=0;
			if(trans!=null) {
				if(mdl.getTransactionId()!=0)
					getSession().update(trans);
				else
					getSession().save(trans);
				
				flush();
				
				Iterator transItr=trans.getTransaction_details_list().iterator();
				while (transItr.hasNext()) {
					TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

					flush();
				}
				id=trans.getTransaction_id();
			}
			else{
				if(mdl.getTransactionId()!=0){
					trans=(TransactionModel)getSession().get(TransactionModel.class, mdl.getTransactionId());
					getSession().delete(trans);
					flush();
				}
				id=0;
			}
			
			mdl.setTransactionId(id);
			
			if(mdl.getId()!=0)
				getSession().update(mdl);
			else
				getSession().save(mdl);
			flush();
			
			if(mdl.getStatus()==SConstants.statuses.LOAN_APPROVED){
				Calendar startCalendar=Calendar.getInstance();
				startCalendar.setTime(mdl.getPaymentStartDate());
				startCalendar.set(Calendar.DAY_OF_MONTH, 1);
				for(int i=0;i<(int)mdl.getNoOfInstallment();i++){
					LoanDateModel dateModel=new LoanDateModel();
					dateModel.setLoan(new LoanApprovalModel(mdl.getId()));
					dateModel.setDate(CommonUtil.getSQLDateFromUtilDate(startCalendar.getTime()));
					double amount=((mdl.getLoanAmount()/(int)mdl.getNoOfInstallment())+mdl.getMonthlycharge());
					dateModel.setAmount(CommonUtil.roundNumber(amount));
					dateModel.setCurrency(new CurrencyModel(mdl.getCurrency().getId()));
					dateModel.setConversionRate(CommonUtil.roundNumber(mdl.getConversionRate()));
					dateModel.setOfficeId(mdl.getLoanRequest().getUser().getOffice().getId());
					dateModel.setLoanStatus(SConstants.loanPaymentStatus.PAYMENT_PENDING);
					getSession().save(dateModel);
					startCalendar.add(Calendar.MONTH, 1);
				}
				flush();
			}
			else if(mdl.getStatus()==SConstants.statuses.LOAN_REJECTED){
				getSession().createQuery("delete from LoanDateModel where loan.id=:id").setParameter("id", mdl.getId()).executeUpdate();
				flush();
			}
			
			getSession().update(reqMdl);
			flush();
			
			if(oldTransIdList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) oldTransIdList).executeUpdate();
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
		}
		return mdl.getId();
	}

	public void update(LoanApprovalModel model) throws Exception {
		try {
			begin();
			getSession().update(model);
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

	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new LoanApprovalModel(id));
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
	
	
	@SuppressWarnings("rawtypes")
	public List getLoanApprovalModelList(long officeId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery( " FROM LoanApprovalModel WHERE loanRequest.user.office.id = :officeid order by loanRequest.requestNo")
					.setParameter("officeid", officeId).list();
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
		return list;
	}
	
	
	public LoanApprovalModel getLoanApprovalModelById(long id) throws Exception {
		LoanApprovalModel model = null;
		try {
			begin();
			model = (LoanApprovalModel) getSession().get(LoanApprovalModel.class,id);
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
		return model;
	}
	
	
	public LoanApprovalModel getLoanApprovalModelByRequestId(long requestId) throws Exception {
		LoanApprovalModel model = null;
		try {
			begin();
			model = (LoanApprovalModel) getSession().createQuery("from LoanApprovalModel where loanRequest.id = :id")
													.setParameter("id", requestId).uniqueResult();
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
		return model;
	}


}
