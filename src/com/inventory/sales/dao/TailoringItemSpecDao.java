package com.inventory.sales.dao;

import java.util.List;
import java.util.Set;

import com.inventory.sales.model.TailoringItemSpecModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Feb 14, 2014
 */
public class TailoringItemSpecDao extends SHibernate{

	private static final long serialVersionUID = 8467319984312173924L;

	public List getAllSpec(long orgId) throws Exception {
		
		List lst=null;
		try {
			begin();
			lst =  getSession().createQuery(" from TailoringItemSpecModel where organization=:id").setParameter("id", orgId).list();
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
		return lst;
	}
	
	public long save(TailoringItemSpecModel obj) throws Exception {

		try {
	
			begin();
			getSession().save(obj);
			commit();
	
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return obj.getId();		
	}
	
	
	public void Update(TailoringItemSpecModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new TailoringItemSpecModel(id));
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

	public TailoringItemSpecModel getItemSpecModel(long specId) throws Exception {
		TailoringItemSpecModel mdl=null;
		try {
			begin();
			mdl=(TailoringItemSpecModel) getSession().get(TailoringItemSpecModel.class, specId);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return mdl;
	}

	public double getSpecPrice(Set ids) throws Exception {
		double value=0;
		try {
			begin();
			if(!ids.isEmpty())
			value=(Double) getSession().createQuery("select sum(price) from TailoringItemSpecModel where id in (:ids)").setParameterList("ids", ids).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return value;
	}
	
	public List getSpecOfType(long orgId,long type) throws Exception {
		
		List lst=null;
		try {
			begin();
			lst =  getSession().createQuery(" from TailoringItemSpecModel where organization=:id and type=:typ").setParameter("id", orgId).setParameter("typ", type).list();
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
		return lst;
	}

}
