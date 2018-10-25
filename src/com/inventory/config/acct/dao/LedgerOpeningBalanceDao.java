/**
 * 
 */
package com.inventory.config.acct.dao;

import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerOpeningBalanceModel;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author anil
 * @date 04-Sep-2015
 * @Project REVERP
 */
public class LedgerOpeningBalanceDao extends SHibernate{

	public List getOpeningBalanceList(long ofc) throws Exception {
		List ls=null;
		try {
			begin();
			ls=getSession().createQuery("from LedgerOpeningBalanceModel where ledger.office.id=:ofc order by date desc").setParameter("ofc", ofc).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		return ls;
	}

	public void save(List lst,long officeId) throws Exception {
		try {
			begin();
			List oldList=getSession().createQuery("from LedgerOpeningBalanceModel where ledger.office.id=:ofc").setParameter("ofc", officeId).list();
			if(oldList!=null&&oldList.size()>0){
				Iterator oldIter=oldList.iterator();
				while (oldIter.hasNext()) {
					getSession().delete((LedgerOpeningBalanceModel)oldIter.next());
				}
			}
			LedgerOpeningBalanceModel mdl;
			Iterator iter=lst.iterator();
			while (iter.hasNext()) {
				mdl = (LedgerOpeningBalanceModel) iter.next();
				getSession().save(mdl);
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
	}

}
