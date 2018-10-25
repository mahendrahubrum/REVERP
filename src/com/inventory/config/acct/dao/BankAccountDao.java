package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.model.BankAccountModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
public class BankAccountDao extends SHibernate implements Serializable {
	
	
	@SuppressWarnings("rawtypes")
	public List getAllBankAccountNames(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.BankAccountModel(id, name)" +
					" from BankAccountModel where ledger.office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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
	public List getAllActiveBankAccountNames(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.BankAccountModel(id, name)" +
					" from BankAccountModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
	public List getAllActiveBankAccountNamesWithLedgerID(long ofc_id) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.BankAccountModel(ledger.id, concat(name,'(',account_no,')'))" +
					" from BankAccountModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
	public List getAllActiveBankAccountNames() throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.BankAccountModel(id, name)" +
					" from BankAccountModel where ledger.status=:val")
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
	
	
	public long save(BankAccountModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj.getLedger());
			getSession().save(obj);
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}
	
	
	public void update(BankAccountModel bankAcct) throws Exception {
		try {

			begin();
			
			getSession().update(bankAcct.getLedger());
			
			flush();
			
			getSession().update(bankAcct);
			
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
			
			BankAccountModel cust= (BankAccountModel) getSession().get(BankAccountModel.class, id);
			
			getSession().delete(cust.getLedger());
			
			getSession().delete(cust);
			
			
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
	
	
	public BankAccountModel getBankAccount(long id) throws Exception {
		BankAccountModel cust=null;
		try {
			begin();
			cust=(BankAccountModel) getSession().get(BankAccountModel.class, id);
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
		return cust;
	}
	
	
}
