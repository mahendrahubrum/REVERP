package com.inventory.config.tax.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.tax.model.TaxGroupModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class TaxGroupDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6435371669920734444L;
	List resultList = new ArrayList();

	public long save(TaxGroupModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	
	public void update(TaxGroupModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
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
			getSession().delete(new TaxGroupModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} 
			flush();
			close();
	}
	
	
	public List getAllTaxess(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxGroupModel(id, name)" +
					" from TaxGroupModel where office.id=:ofc").setParameter("ofc", ofc_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getAllActiveTaxGroups() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxGroupModel(id, name)" +
					" from TaxGroupModel where status=:val").setParameter("val", SConstants.statuses.TAX_GROUP_ACTIVE).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getAllActiveTaxesFromType(long ofc_id, long type) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxGroupModel(id, name)" +
					" from TaxGroupModel where status=:val and office.id=:ofc and tax_type=:tp").setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.TAX_ACTIVE).setParameter("tp", type).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public TaxGroupModel getTax(long id) throws Exception {
		TaxGroupModel mod=null;
		try {
			begin();
			mod=(TaxGroupModel) getSession().get(TaxGroupModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}
	
	
}
