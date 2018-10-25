package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.PdcDetailsModel;
import com.inventory.config.acct.model.PdcModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */

/**
 * 
 * @author sangeeth
 *
 */
@SuppressWarnings("serial")
public class PDCDao extends SHibernate implements Serializable {
	
	
	public long save(PdcModel mdl) throws Exception {
		try {
			begin();
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
	public List getPdcModelList(long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.config.acct.model.PdcModel(id,cast(id as string))" +
					" from PdcModel where office_id=:office order by id DESC")
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
	
	
	public PdcModel getPdcModel(long id) throws Exception {
		PdcModel mdl=null;
		try {
			begin();
			mdl=(PdcModel) getSession().get(PdcModel.class, id);
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
	
	
	public PdcDetailsModel getPdcDetailsModel(long id) throws Exception {
		PdcDetailsModel mdl=null;
		try {
			begin();
			mdl=(PdcDetailsModel) getSession().get(PdcDetailsModel.class, id);
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
	
	
	@SuppressWarnings("rawtypes")
	public void update(PdcModel mdl) throws Exception {
		try {
			begin();
			List oldChildIdList=new ArrayList();
			oldChildIdList=getSession().createQuery("select b.id from PdcModel a join a.pdc_list b where a.id=:id")
									.setParameter("id", mdl.getId()).list();
			
			Iterator itr=mdl.getPdc_list().iterator();
			while (itr.hasNext()) {
				PdcDetailsModel payDet = (PdcDetailsModel) itr.next();
				if(payDet.getId()!=0){
					if(oldChildIdList.contains(payDet.getId())){
						oldChildIdList.remove(payDet.getId());
					}
				}
			}
			getSession().clear();
			getSession().update(mdl);
			flush();
			if(oldChildIdList.size()>0){
				getSession().createQuery("delete from PdcDetailsModel where id in (:list)")
							.setParameterList("list", oldChildIdList).executeUpdate();
				flush();
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
			PdcModel mdl=(PdcModel) getSession().get(PdcModel.class, id);
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
			PdcModel mdl=(PdcModel) getSession().get(PdcModel.class, id);
			getSession().createQuery("update PdcModel set active=false where id=:id").setParameter("id", mdl.getId()).executeUpdate();
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
			resultList=getSession().createQuery("from PaymentInvoiceMapModel where office_id=:office and type=:type and invoiceId=:invoice")
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
	public List getAllCreditDebitList(long office, int type, long invoice, int cusSup) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from DebitCreditInvoiceMapModel where office_id=:office and type=:type and supplier_customer=:cusSup and invoiceId=:invoice")
						.setParameter("office", office).setParameter("type", type).setParameter("cusSup", cusSup).setParameter("invoice", invoice).list();
			
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
