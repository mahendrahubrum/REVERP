package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.model.BrandModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Apr 24, 2014
 */

public class BrandDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -7247708772825492603L;
	
	List resultList = new ArrayList();

	public List getAllBrands(long orgId) throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from BrandModel  where organization.id=:org order by name")
					.setParameter("org", orgId).list();
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
	
	public BrandModel getBrand(long brandId) throws Exception {
		
		BrandModel bm=null;
		try {
			
			begin();
			bm = (BrandModel) getSession()
					.get(BrandModel.class, brandId);
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
		return bm;
	}
	
	public void save(BrandModel bm) throws Exception {
		
		try {
			
			begin();
			getSession().save(bm);
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
	
	public void update(BrandModel bm) throws Exception {
		
		try {
			
			begin();
			getSession().update(bm);
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
	
	public void delete(long brandId) throws Exception {
		
		
		try {
			BrandModel bm=null;
			begin();
			bm = (BrandModel) getSession().get(BrandModel.class, brandId);
			getSession().delete(bm);
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

	public BrandModel getBrandFromName(String name) throws Exception {
		
		BrandModel mdl=null;
		try {

			begin();
			resultList = getSession()
					.createQuery(
							"select distinct a from BrandModel a where name=:name")
					.setParameter("name",name).list();
			commit();
			if(resultList!=null&&resultList.size()>0)
				mdl=(BrandModel) resultList.get(0);
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

}
