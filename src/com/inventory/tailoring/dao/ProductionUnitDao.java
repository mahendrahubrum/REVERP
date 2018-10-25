package com.inventory.tailoring.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.tailoring.model.ProductionUnitModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 18, 2014
 */

public class ProductionUnitDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = -7529300785840375776L;
	List resultList = new ArrayList();

	public List getAllProductionUnits(long officeId) throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from ProductionUnitModel  where office.id=:org order by name")
					.setParameter("org", officeId).list();
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
	
	public ProductionUnitModel getBrand(long brandId) throws Exception {
		
		ProductionUnitModel bm=null;
		try {
			
			begin();
			bm = (ProductionUnitModel) getSession()
					.get(ProductionUnitModel.class, brandId);
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
	
	public void save(ProductionUnitModel bm) throws Exception {
		
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
	
	public void update(ProductionUnitModel bm) throws Exception {
		
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
	
	public void delete(long prodId) throws Exception {
		
		
		try {
			ProductionUnitModel bm=null;
			begin();
			bm = (ProductionUnitModel) getSession().get(ProductionUnitModel.class, prodId);
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
	
	public List getAllProductionUnitsInPriorityOrder(long officeId) throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from ProductionUnitModel  where office.id=:org order by priorityOrder")
					.setParameter("org", officeId).list();
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
