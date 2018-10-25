package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 10, 2015
 */

@SuppressWarnings("serial")
public class LedgerBalanceResetMappingDao extends SHibernate implements Serializable {

	
	@SuppressWarnings("unchecked")
	public List<Long> selectLedgersofGroup(long group,long office) throws Exception {
		
		List<Long> objList=new ArrayList<Long>();
		try {
			begin();
			objList=getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id,name) from LedgerModel where group.id=:group and office.id=:office")
					.setParameter("group", group).setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return objList;
	}

	
	
	@SuppressWarnings("rawtypes")
	public void updateLedger(List<LedgerModel> ledgerList) throws Exception {
		try {
			begin();
			
			Iterator itr=ledgerList.iterator();
			while (itr.hasNext()) {
				LedgerModel ledger = (LedgerModel) itr.next();
				getSession().update(ledger);
				flush();
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	
	
}
