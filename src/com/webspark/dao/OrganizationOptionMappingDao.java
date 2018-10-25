package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.OrganizationOptionMappingModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 12, 2014
 */
public class OrganizationOptionMappingDao extends SHibernate implements Serializable {
	
	private static final long serialVersionUID = -7537886232308530066L;
	
	private List resultList=new ArrayList();

	public List<Long> selectOptionsToOrganization(long organizationId) throws Exception {
		
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select option_id.option_id from OrganizationOptionMappingModel where organizationId.id=:LogId")
							.setParameter("LogId", organizationId).list();
			
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
	
	public void updateOptionsToOrganization(long orgId, List<OrganizationOptionMappingModel> rolOptList) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from OrganizationOptionMappingModel where organizationId.id=:LogId")
							.setParameter("LogId", orgId).executeUpdate();
			
			for (OrganizationOptionMappingModel obj: rolOptList) {
				getSession().save(obj);
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
