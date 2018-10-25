package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.BankAccountDepositDetailsModel;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentDetailsModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.ChequeReturnDetailsModel;
import com.inventory.config.acct.model.ChequeReturnModel;
import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.SHibernate;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author sangeeth
 * @date 28-Sep-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class ChequeReturnDao extends SHibernate implements Serializable {
	
	
	WrappedSession session=new SessionUtil().getHttpSession();
	SettingsValuePojo settings = (SettingsValuePojo) session.getAttribute("settings");
	
	@SuppressWarnings("rawtypes")
	public long save(ChequeReturnModel mdl) throws Exception {
		try {
			begin();
			List<ChequeReturnDetailsModel> childList =new ArrayList<ChequeReturnDetailsModel>();
			Iterator itr=mdl.getCheque_return_list().iterator();
			while (itr.hasNext()) {
				ChequeReturnDetailsModel det = (ChequeReturnDetailsModel) itr.next();
				if(!det.isReturned()){
					if(det.getType()==SConstants.SALES){
						loadData(SConstants.SALES, det);
					}
					else if(det.getType()==SConstants.PURCHASE){
						loadData(SConstants.PURCHASE, det);
					}
					flush();
				}
				det.setReturned(true);
				childList.add(det);
			}
			mdl.setCheque_return_list(childList);
			getSession().save(mdl);
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
	
	
	@SuppressWarnings("rawtypes")
	public List getChequeReturnModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.ChequeReturnModel(id,cast(id as string))" +
					" from ChequeReturnModel where office_id=:office order by id DESC")
						.setLong("office", office).list();
			
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
	
	
	public ChequeReturnModel getChequeReturnModel(long id) throws Exception {
		ChequeReturnModel mdl=null;
		try {
			begin();
			mdl=(ChequeReturnModel) getSession().get(ChequeReturnModel.class, id);
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
		return mdl;
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	public void update(ChequeReturnModel mdl) throws Exception {
		try {
			begin();
			
			List oldChildList=new ArrayList();
			List<ChequeReturnDetailsModel> childList =new ArrayList<ChequeReturnDetailsModel>();
			oldChildList=getSession().createQuery("select b.id from ChequeReturnModel a join a.cheque_return_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			Iterator itr=mdl.getCheque_return_list().iterator();
			while (itr.hasNext()) {
				ChequeReturnDetailsModel det = (ChequeReturnDetailsModel) itr.next();
				if(!det.isReturned()){
					if(det.getType()==SConstants.SALES){
						loadData(SConstants.SALES, det);
					}
					else if(det.getType()==SConstants.PURCHASE){
						loadData(SConstants.PURCHASE, det);
					}
					flush();
				}
				det.setReturned(true);
				childList.add(det);
			}
			mdl.setCheque_return_list(childList);
			getSession().update(mdl);
			flush();
			if(oldChildList.size()>0){
				getSession().createQuery("delete from ChequeReturnDetailsModel where id in (:list)")
							.setParameterList("list", oldChildList).executeUpdate();
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
	}
	
	
	public void delete(long id) throws Exception {
		try {
			begin();
			ChequeReturnModel mdl=(ChequeReturnModel) getSession().get(ChequeReturnModel.class, id);
			getSession().delete(mdl);
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
	
	
	public void cancel(long id) throws Exception {
		try {
			begin();
			ChequeReturnModel mdl=(ChequeReturnModel) getSession().get(ChequeReturnModel.class, id);
			getSession().createQuery("update ChequeReturnModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
	public List getAllPaymentList(long office, int type, long invoice) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and invoiceId=:invoice")
						.setParameter("office", office).setParameter("type", type).setParameter("invoice", invoice).list();
			
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
	public List getAllBankTransactionList(long office, int type, long invoice) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from BankDetailsInvoiceMapModel where office_id=:office and type=:type " +
					" and invoice_id=:invoice order by paymentId")
						.setParameter("office", office).setParameter("type", type).setParameter("invoice", invoice).list();
			
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadData(int type, ChequeReturnDetailsModel crdet) throws Exception {
		try {
			if(type==SConstants.SALES){
				
				BankAccountDepositModel dmdl=(BankAccountDepositModel)getSession().get(BankAccountDepositModel.class, crdet.getPay_id());
				List<BankAccountDepositDetailsModel> childList=new ArrayList<BankAccountDepositDetailsModel>();
				List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
				
				S_OfficeModel office=(S_OfficeModel)getSession().get(S_OfficeModel.class, dmdl.getOffice_id());
				
				dmdl.setBankAccount(new LedgerModel(dmdl.getBankAccount().getId()));
				dmdl.setRef_no(dmdl.getRef_no());
				dmdl.setDate(CommonUtil.getSQLDateFromUtilDate(dmdl.getDate()));
				dmdl.setOffice_id(dmdl.getOffice_id());
				dmdl.setLogin_id(dmdl.getLogin_id());
				dmdl.setMemo(dmdl.getMemo());
				
				FinTransaction tran = new FinTransaction();
				Iterator itr = dmdl.getBank_account_deposit_list().iterator();
				while (itr.hasNext()) {
					BankAccountDepositDetailsModel det = (BankAccountDepositDetailsModel) itr.next();
					if(crdet.getPay_child_id()==det.getId())
						continue;
					
					boolean isBaseCurrency=true;
					boolean isCheque=false;
					
					det.setCash_or_check(det.getCash_or_check());
					det.setAccount(new LedgerModel(det.getAccount().getId()));
					det.setButtonVisible(det.isButtonVisible());
					det.setBill_no(det.getBill_no());
					det.setAmount(CommonUtil.roundNumber(det.getAmount()));
					det.setCurrencyId(new CurrencyModel(det.getCurrencyId().getId()));
					if(det.getCurrencyId().getId()!=office.getCurrency().getId())
						isBaseCurrency=false;
					det.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
					det.setDepartmentId(det.getDepartmentId());
					det.setDivisionId(det.getDivisionId());
					det.setChequeNo(det.getChequeNo());
					if(det.getChequeDate()!=null)
						det.setChequeDate(CommonUtil.getSQLDateFromUtilDate(det.getChequeDate()));
					else
						det.setChequeDate(null);
					det.setFromDate(CommonUtil.getSQLDateFromUtilDate(det.getFromDate()));
					det.setToDate(CommonUtil.getSQLDateFromUtilDate(det.getToDate()));
					childList.add(det);
				
					if(det.getBill_no().trim().length()>0){
						double paymentAmount = CommonUtil.roundNumber(det.getAmount());
						double paymentConversionRate = CommonUtil.roundNumber(det.getConversionRate());
						List billList=new ArrayList();
						billList=Arrays.asList(det.getBill_no().split(","));
						Iterator it=billList.iterator();
						while (it.hasNext()) {
							long pid = Long.parseLong(it.next().toString().trim());
							double actualPaidAmount=0;
							double actualAmount=0;
							double actual_balance_to_pay=0;
							double actualPayingAmount=0;
							
							double totalPayed=0;
							double totalAmount=0;
							double payingAmount=0;
							double balance_to_pay=0;
							
							if(paymentAmount<=0)
								break;
							if(pid!=0) {
								SalesModel pmdl=(SalesModel)getSession().get(SalesModel.class,pid);
								List paymentList=new ArrayList();
								
								paymentList=getSession().createQuery("from PaymentInvoiceMapModel where office_id=:office and payment_type=:payment_type and type=:type and " +
												" invoiceId=:invoice").setParameter("office", office.getId())
												.setParameter("type", SConstants.SALES)
												.setParameter("payment_type", SConstants.BANK_ACCOUNT_DEPOSITS)
																		.setParameter("invoice", pid).list();
								if(paymentList.size()>0){
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualPaidAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								paymentList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and supplier_customer=:cusSup " +
														"and invoiceId=:invoice").setParameter("office", office.getId()).setParameter("type", SConstants.creditDebitNote.DEBIT)
														.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER).setParameter("invoice", pid).list();
								if(paymentList.size()>0){
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalAmount+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalAmount+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								paymentList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and supplier_customer=:cusSup " +
										"and invoiceId=:invoice").setParameter("office", office.getId()).setParameter("type", SConstants.creditDebitNote.CREDIT)
										.setParameter("cusSup", SConstants.creditDebitNote.CUSTOMER).setParameter("invoice", pid).list();
								if(paymentList.size()>0){
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualPaidAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								if(isBaseCurrency){
									totalAmount=CommonUtil.roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
									totalPayed+=CommonUtil.roundNumber(pmdl.getPayment_amount());
								}
								else{
									totalAmount=CommonUtil.roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
									actualAmount=CommonUtil.roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
									totalPayed+=CommonUtil.roundNumber(pmdl.getPayment_amount()/pmdl.getConversionRate());
									actualPaidAmount+=CommonUtil.roundNumber(pmdl.getPayment_amount());
								}
								
								balance_to_pay=totalAmount-totalPayed;
								actual_balance_to_pay=actualAmount-actualPaidAmount;
								
								PaymentInvoiceBean bean;
								if(isBaseCurrency) {
									if(paymentAmount>=balance_to_pay){
										payingAmount=balance_to_pay;
										paymentAmount-=balance_to_pay;
									}
									else{
										payingAmount=paymentAmount;
										paymentAmount=0;
									}
									bean=new PaymentInvoiceBean(
														SConstants.SALES,
														pid,
														dmdl.getId(),
														office.getId(),
														office.getCurrency().getId(),
														CommonUtil.roundNumber(payingAmount),
														CommonUtil.roundNumber(paymentConversionRate),
														isCheque);
									invoiceMapList.add(bean);
									tran.addTransaction(SConstants.CR, 
														det.getAccount().getId(),
														dmdl.getBankAccount().getId(),
														CommonUtil.roundNumber(payingAmount),
														"",
														office.getCurrency().getId(),
														CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
									
								}
								else{
									boolean isSavable=false;
									if(paymentAmount>=actual_balance_to_pay){
										isSavable=true;
										actualPayingAmount=actual_balance_to_pay;
										paymentAmount-=actual_balance_to_pay;
									}
									else{
										actualPayingAmount=paymentAmount;
										paymentAmount=0;
									}
									
									double actualBaseCurrency=0;
									actualBaseCurrency=CommonUtil.roundNumber(actualPayingAmount/det.getConversionRate());
									double differenceAmount=0;
									differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
									
									bean=new PaymentInvoiceBean(
														SConstants.SALES,
														pid,
														dmdl.getId(),
														office.getId(),
														office.getCurrency().getId(),
														CommonUtil.roundNumber(actualPayingAmount),
														CommonUtil.roundNumber(paymentConversionRate),
														isCheque);
									if(isSavable){
										if(differenceAmount>0){
											tran.addTransaction(SConstants.CR,
																settings.getFOREX_DIFFERENCE_ACCOUNT(),
																dmdl.getBankAccount().getId(), 
																CommonUtil.roundNumber(differenceAmount),
																"",
																office.getCurrency().getId(),
																CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
											actualBaseCurrency-=differenceAmount;
										}
									}
									
									tran.addTransaction(SConstants.CR, 
												det.getAccount().getId(),
												dmdl.getBankAccount().getId(),
												CommonUtil.roundNumber(actualBaseCurrency),
												"",
												office.getCurrency().getId(),
												CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
									invoiceMapList.add(bean);
								}
							}
						}
						if(paymentAmount>0){
							tran.addTransaction(SConstants.CR, 
									det.getAccount().getId(),
									dmdl.getBankAccount().getId(),
									CommonUtil.roundNumber(paymentAmount/paymentConversionRate),
									"",
									office.getCurrency().getId(),
									CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
						}
					}
					else{
						tran.addTransaction(SConstants.CR, 
								det.getAccount().getId(),
								dmdl.getBankAccount().getId(),
								CommonUtil.roundNumber(det.getAmount() /det.getConversionRate()),
								"",
								office.getCurrency().getId(),
								CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
					}
					
				}
				dmdl.setBank_account_deposit_list(childList);
				TransactionModel transaction = (TransactionModel)getSession().get(TransactionModel.class, dmdl.getTransactionId());
				transaction.setTransaction_details_list(tran.getChildList());
				transaction.setDate(dmdl.getDate());
				transaction.setLogin_id(transaction.getLogin_id());
				new BankAccountDepositDao().updateReturn(dmdl, transaction, invoiceMapList);
			}
			else if(type==SConstants.PURCHASE){
				BankAccountPaymentModel dmdl=(BankAccountPaymentModel)getSession().get(BankAccountPaymentModel.class, crdet.getPay_id());
				List<BankAccountPaymentDetailsModel> childList=new ArrayList<BankAccountPaymentDetailsModel>();
				List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
				
				S_OfficeModel office=(S_OfficeModel)getSession().get(S_OfficeModel.class, dmdl.getOffice_id());
				
				dmdl.setBankAccount(new LedgerModel(dmdl.getBankAccount().getId()));
				dmdl.setRef_no(dmdl.getRef_no());
				dmdl.setDate(CommonUtil.getSQLDateFromUtilDate(dmdl.getDate()));
				dmdl.setOffice_id(dmdl.getOffice_id());
				dmdl.setLogin_id(dmdl.getLogin_id());
				dmdl.setMemo(dmdl.getMemo());
				
				FinTransaction tran = new FinTransaction();
				Iterator itr = dmdl.getBank_account_payment_list().iterator();
				while (itr.hasNext()) {
					BankAccountPaymentDetailsModel det = (BankAccountPaymentDetailsModel) itr.next();
					if(crdet.getPay_child_id()==det.getId())
						continue;
					boolean isBaseCurrency=true;
					boolean isCheque=false;
					
					det.setCash_or_check(det.getCash_or_check());
					det.setAccount(new LedgerModel(det.getAccount().getId()));
					det.setButtonVisible(det.isButtonVisible());
					det.setBill_no(det.getBill_no());
					det.setAmount(CommonUtil.roundNumber(det.getAmount()));
					det.setCurrencyId(new CurrencyModel(det.getCurrencyId().getId()));
					if(det.getCurrencyId().getId()!=office.getCurrency().getId())
						isBaseCurrency=false;
					det.setConversionRate(CommonUtil.roundNumber(det.getConversionRate()));
					det.setDepartmentId(det.getDepartmentId());
					det.setDivisionId(det.getDivisionId());
					det.setChequeNo(det.getChequeNo());
					if(det.getChequeDate()!=null)
						det.setChequeDate(CommonUtil.getSQLDateFromUtilDate(det.getChequeDate()));
					else
						det.setChequeDate(null);
					det.setFromDate(CommonUtil.getSQLDateFromUtilDate(det.getFromDate()));
					det.setToDate(CommonUtil.getSQLDateFromUtilDate(det.getToDate()));
					childList.add(det);
					
					if(det.getBill_no().trim().length()>0){
						double paymentAmount = CommonUtil.roundNumber(det.getAmount());
						double paymentConversionRate = CommonUtil.roundNumber(det.getConversionRate());
						List billList=new ArrayList();
						billList=Arrays.asList(det.getBill_no().split(","));
						Iterator it=billList.iterator();
						while (it.hasNext()) {
							long pid = Long.parseLong(it.next().toString().trim());
							double actualPaidAmount=0;
							double actualAmount=0;
							double actual_balance_to_pay=0;
							double actualPayingAmount=0;
							
							double totalPayed=0;
							double totalAmount=0;
							double payingAmount=0;
							double balance_to_pay=0;
							
							if(paymentAmount<=0)
								break;
							if(pid!=0) {
								PurchaseModel pmdl=(PurchaseModel)getSession().get(PurchaseModel.class, pid);
								List paymentList=new ArrayList();
								
								paymentList=getSession().createQuery("from PaymentInvoiceMapModel where office_id=:office and payment_type=:payment_type and type=:type " +
																	" and invoiceId=:invoice").setParameter("office", office.getId())
																	.setParameter("payment_type", SConstants.BANK_ACCOUNT_PAYMENTS)
																	.setParameter("type", SConstants.PURCHASE).setParameter("invoice", pid).list();
								if(paymentList.size()>0) {
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualPaidAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								paymentList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and " +
											"supplier_customer=:cusSup and invoiceId=:invoice").setParameter("office", office.getId())
											.setParameter("type", SConstants.creditDebitNote.DEBIT).setParameter("cusSup", SConstants.creditDebitNote.SUPPLIER)
											.setParameter("invoice", pid).list();
								if(paymentList.size()>0){
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalAmount+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalAmount+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								paymentList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and " +
										"supplier_customer=:cusSup and invoiceId=:invoice").setParameter("office", office.getId())
										.setParameter("type", SConstants.creditDebitNote.CREDIT).setParameter("cusSup", SConstants.creditDebitNote.SUPPLIER)
										.setParameter("invoice", pid).list();
								if(paymentList.size()>0){
									Iterator payItr=paymentList.iterator();
									while (payItr.hasNext()) {
										DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
										if(mapMdl.getPaymentId()!=dmdl.getId()){
											if(isBaseCurrency){
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount());
											}
											else{
												totalPayed+=CommonUtil.roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
												actualPaidAmount+=mapMdl.getAmount();
											}
										}
									}
								}
								
								if(isBaseCurrency){
									totalAmount=CommonUtil.roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
									totalPayed+=CommonUtil.roundNumber(pmdl.getPaymentAmount());
								}
								else{
									totalAmount=CommonUtil.roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
									actualAmount=CommonUtil.roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
									totalPayed+=CommonUtil.roundNumber(pmdl.getPaymentAmount()/pmdl.getConversionRate());
									actualPaidAmount+=CommonUtil.roundNumber(pmdl.getPaymentAmount());
								}
								
								balance_to_pay=totalAmount-totalPayed;
								actual_balance_to_pay=actualAmount-actualPaidAmount;
								
								PaymentInvoiceBean bean;
								if(isBaseCurrency) {
									if(paymentAmount>=balance_to_pay){
										payingAmount=balance_to_pay;
										paymentAmount-=balance_to_pay;
									}
									else{
										payingAmount=paymentAmount;
										paymentAmount=0;
									}
									bean=new PaymentInvoiceBean(
														SConstants.PURCHASE,
														pid,
														dmdl.getId(),
														office.getId(),
														det.getCurrencyId().getId(),
														CommonUtil.roundNumber(payingAmount),
														CommonUtil.roundNumber(paymentConversionRate),
														isCheque);
									invoiceMapList.add(bean);
									tran.addTransaction(SConstants.CR, 
														dmdl.getBankAccount().getId(), 
														det.getAccount().getId(),  
														CommonUtil.roundNumber(payingAmount),
														"",
														det.getCurrencyId().getId(),
														CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
									
								}
								else{
									boolean isSavable=false;
									if(paymentAmount>=actual_balance_to_pay){
										isSavable=true;
										actualPayingAmount=actual_balance_to_pay;
										paymentAmount-=actual_balance_to_pay;
									}
									else{
										actualPayingAmount=paymentAmount;
										paymentAmount=0;
									}
									
									double actualBaseCurrency=0;
									actualBaseCurrency=CommonUtil.roundNumber(actualPayingAmount/det.getConversionRate());
									double differenceAmount=0;
									differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
									
									bean=new PaymentInvoiceBean(
														SConstants.PURCHASE,
														pid,
														dmdl.getId(),
														office.getId(),
														det.getCurrencyId().getId(),
														CommonUtil.roundNumber(actualPayingAmount),
														CommonUtil.roundNumber(paymentConversionRate),
														isCheque);
									if(isSavable){
										if(differenceAmount>0){
											tran.addTransaction(SConstants.CR, 
																dmdl.getBankAccount().getId(), 
																settings.getFOREX_DIFFERENCE_ACCOUNT(),  
																CommonUtil.roundNumber(differenceAmount),
																"",
																det.getCurrencyId().getId(),
																CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
											actualBaseCurrency-=differenceAmount;
										}
									}
									
									tran.addTransaction(SConstants.CR, 
												dmdl.getBankAccount().getId(), 
												det.getAccount().getId(),  
												CommonUtil.roundNumber(actualBaseCurrency),
												"",
												det.getCurrencyId().getId(),
												CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
									invoiceMapList.add(bean);
								}
							}
						}
						if(paymentAmount>0){
							tran.addTransaction(SConstants.CR, 
									dmdl.getBankAccount().getId(), 
									det.getAccount().getId(),  
									CommonUtil.roundNumber(paymentAmount/paymentConversionRate),
									"",
									det.getCurrencyId().getId(),
									CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
						}
					}
					else{
						tran.addTransaction(SConstants.CR, 
								dmdl.getBankAccount().getId(), 
								det.getAccount().getId(),  
								CommonUtil.roundNumber(det.getAmount()/ det.getConversionRate()),
								"",
								det.getCurrencyId().getId(),
								CommonUtil.roundNumber(det.getConversionRate()),det.getDepartmentId(),det.getDivisionId());
					}
				}
				dmdl.setBank_account_payment_list(childList);
				
				TransactionModel transaction = (TransactionModel)getSession().get(TransactionModel.class, dmdl.getTransactionId());
				transaction.setTransaction_details_list(tran.getChildList());
				transaction.setDate(dmdl.getDate());
				transaction.setLogin_id(transaction.getLogin_id());
				new BankAccountPaymentDao().updateReturn(dmdl, transaction, invoiceMapList);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
}
