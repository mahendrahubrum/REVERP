package com.inventory.fixedasset.dao;

import java.io.Serializable;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class FixedAssetDao extends SHibernate implements Serializable{
	public long save(FixedAssetModel assetModel, LedgerModel ledgerModel) throws Exception {
		try {
			begin();
			if(ledgerModel != null){
				getSession().save(ledgerModel);
				assetModel.setAccount(new LedgerModel(ledgerModel.getId()));
			}
			getSession().save(assetModel);
			
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
		return assetModel.getId();
	}
	public FixedAssetModel getFixedAssetModel(long id) throws Exception {
		FixedAssetModel model = null;
		try {
			begin();
			model = (FixedAssetModel) getSession().get(
					FixedAssetModel.class, id);
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
		return model;
	}
	public void update(FixedAssetModel model) throws Exception {
		try {
			begin();
			getSession().update(model);
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
	}
	public void delete(long id) throws Exception {

		try {
			begin();
			FixedAssetModel model = (FixedAssetModel) getSession().get(
					FixedAssetModel.class, id);
			getSession().delete(model);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FixedAssetModel> getAllFixedAssetList(long officeId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetModel(id, name)"
									+ " FROM FixedAssetModel WHERE office.id = :officeid" +
										" ORDER BY name")				
					.setParameter("officeid", officeId).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FixedAssetModel> getAllFixedAssetList(long officeId, long groupId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetModel(id, name)"
									+ " FROM FixedAssetModel" +
									" WHERE office.id = :officeid" +
									(groupId != 0 ? " AND assetTypeGroup.id = "+groupId : " ")+
										" ORDER BY name")				
					.setParameter("officeid", officeId).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isAlreadyExistFixedAsset(long officeId, long id, String name) throws Exception{
		List list = null;
		try {
			begin();
			String query = "SELECT m"
							+ " FROM FixedAssetModel m" +
							" WHERE m.office.id = :officeid" +
							" AND UPPER(m.name) = UPPER( :name ) ";
			if(id != 0){
				query += " AND m.id != "+id;
			}							
			list = getSession().createQuery(query)				
					.setParameter("officeid", officeId)
					.setParameter("name", name).list();
			commit();
			

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		if(list == null || list.size() == 0){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<UnitModel> getFixedAssetUnitList(long officeId, long id) throws Exception{
		List list = null;
		try {
			begin();
			String query = "SELECT new com.inventory.config.unit.model.UnitModel(m.unit.id, m.unit.symbol)"
							+ " FROM FixedAssetModel m" +
							" WHERE m.office.id = :officeid" ;
			if(id != 0){
				query += " AND m.id = "+id;
			}							
			list = getSession().createQuery(query)				
					.setParameter("officeid", officeId).list();
			commit();
			

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
	
		return list;
	}
		
	
	public FixedAssetModel getFixedAssetConfiguration(long officeId, long asset_type_group_id) throws Exception {
		FixedAssetModel model = null;
		try {
			begin();
			model = (FixedAssetModel) getSession()
					.createQuery( "SELECT m FROM FixedAssetModel m WHERE m.office.id = :officeid" +
							" AND m.assetTypeGroup.id = :asset_type_group_id" +
										" AND m.id = (SELECT MAX(m1.id) FROM FixedAssetModel m1 WHERE m1.office.id = m.office.id" +
										" AND m1.assetTypeGroup.id = m.assetTypeGroup.id)"+
										" ORDER BY m.name")				
					.setParameter("officeid", officeId)
					.setParameter("asset_type_group_id", asset_type_group_id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return model;
	}
}
