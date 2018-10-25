package com.webspark.uac.dao;

import java.io.Serializable;

import org.hibernate.criterion.Restrictions;

import com.webspark.dao.SHibernate;
import com.webspark.model.S_IDGeneratorSettingsModel;
import com.webspark.uac.model.InvoiceFormatModel;

@SuppressWarnings("serial")
public class InvoiceFormatMappingDao extends SHibernate implements Serializable {

	public void save(InvoiceFormatModel mdl) throws Exception{
		try{
			begin();
			getSession().saveOrUpdate(mdl);
			commit();
		}
		catch(Exception e){
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	
	public InvoiceFormatModel getInvoiceFormatModel(long office, long ids) throws Exception{
		InvoiceFormatModel mdl=null;
		try{
			begin();
			mdl=(InvoiceFormatModel) getSession().createQuery("from InvoiceFormatModel where office=:office and idFormat=:ids")
												.setParameter("office", office).setParameter("ids", ids).uniqueResult();
			commit();
		}
		catch(Exception e){
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	public InvoiceFormatModel getInvoiceFormatModel(long office, String keyIds) throws Exception{
		InvoiceFormatModel mdl=null;
		try{
			begin();
			S_IDGeneratorSettingsModel idMdl = (S_IDGeneratorSettingsModel) getSession().createCriteria(S_IDGeneratorSettingsModel.class)
																						.add(Restrictions.eq("id_name", keyIds)).uniqueResult();
			if(idMdl!=null){
				mdl=(InvoiceFormatModel) getSession().createQuery("from InvoiceFormatModel where office=:office and idFormat=:id")
						.setParameter("office", office).setParameter("id", idMdl.getId()).uniqueResult();
			}
			commit();
		}
		catch(Exception e){
			e.printStackTrace();
			rollback();
			close();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	
	
}
	
