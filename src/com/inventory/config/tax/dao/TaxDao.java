package com.inventory.config.tax.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.tax.model.TaxModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @Author Jinshad P.T.
 */

public class TaxDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8821837188548492658L;
	List resultList = new ArrayList();

	public long save(TaxModel obj) throws Exception {

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
	
	
	public void update(TaxModel obj) throws Exception {

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
			getSession().delete(new TaxModel(id));
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
	
	
	public List getAllTaxes(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxModel(id, name)" +
					" from TaxModel where status!=:val and office.id=:ofc").setParameter("ofc", ofc_id)
					.setParameter("val", (long)0).list();
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
	
	
	public TaxModel getDefaultTax(long ofc_id) throws Exception {
		TaxModel taxObj=null;
		try {
			begin();
			Object obj = getSession().createQuery("select max(id)" +
					" from TaxModel where status!=:val and office.id=:ofc").setParameter("ofc", ofc_id)
					.setParameter("val", (long)0).uniqueResult();
			
			if(obj!=null){
				taxObj=(TaxModel) getSession().get(TaxModel.class, (Serializable) obj);
			}
			else {
				taxObj=new TaxModel(0, "No Tax");
				taxObj.setOffice(new S_OfficeModel(ofc_id));
				taxObj.setStatus(1);
				taxObj.setTax_type(1);
				taxObj.setValue_type(1);
				getSession().save(taxObj);
			}
			
			
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
			return taxObj;
		}
	}
	
	
	public List getAllActiveTaxes(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxModel(id, name)" +
					" from TaxModel where status=:val and office.id=:ofc").setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.TAX_ACTIVE).list();
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
			resultList = getSession().createQuery("select new com.inventory.config.tax.model.TaxModel(id, name)" +
					" from TaxModel where status=:val and office.id=:ofc and tax_type=:tp").setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.TAX_ACTIVE).setParameter("tp", type).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	public TaxModel getTax(long id) throws Exception {
		TaxModel mod=null;
		try {
			begin();
			mod=(TaxModel) getSession().get(TaxModel.class, id);
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
	
	
	public TaxModel saveDefault(long ofc_id) throws Exception {
		
		TaxModel obj=new TaxModel();
		try {
			
			obj.setName("Default");
			obj.setOffice(new S_OfficeModel(ofc_id));
			obj.setStatus(1);
			obj.setTax_type(1);
			obj.setValue(0);
			obj.setValue_type(1);

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
			return obj;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public TaxModel getDefaultTax(long office, long type) throws Exception {
		
		begin();
		TaxModel mdl=null;
		try {
			List list=new ArrayList();
			list = getSession().createQuery("from TaxModel where office.id=:office and tax_type=:type and value=0 and status=1 and value_type=2")
								.setParameter("office", office).setParameter("type", type).list();
			if(list.size()>0){
				mdl=(TaxModel)list.iterator().next();
			}
			else{
				mdl=new TaxModel();
				mdl.setName("No Tax");
				mdl.setOffice(new S_OfficeModel(office));
				mdl.setStatus(1);
				mdl.setTax_type(type);
				mdl.setValue(0);
				mdl.setValue_type(2);
				getSession().save(mdl);
				flush();
			}
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return mdl;
	}
	
	
	
	
}
