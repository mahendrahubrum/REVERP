package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.OfficeOptionMappingModel;

/**
 * @Author Jinshad P.T.
 */

public class OfficeOptionMappingDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866874486369104513L;
	
	private List resultList=new ArrayList();

	
	
	public List<Long> selectOptionsToOffice(long officeId) throws Exception {
		
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select option_id.option_id from OfficeOptionMappingModel where officeId.id=:LogId")
							.setParameter("LogId", officeId).list();
			
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
	
	
	
	public void updateOptionsToOffice(long officeId, List<OfficeOptionMappingModel> rolOptList) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from OfficeOptionMappingModel where officeId.id=:LogId")
							.setParameter("LogId", officeId).executeUpdate();
			
			for (OfficeOptionMappingModel obj: rolOptList) {
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
	
	


	public List getAllOptionsUnderAllOrganization() throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select a.option_id " +
					"from OrganizationOptionMappingModel a order by a.option_id.option_name").list();
			
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
	
	
	public List getAllOptionsUnderOrganization(long orgId) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select a.option_id  " +
					"from OrganizationOptionMappingModel a where a.organizationId.id=:LogId order by a.option_id.option_name")
					.setParameter("LogId", orgId).list();
			
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

public List getAllOptionsUnderOrganization(long orgId,long langId) throws Exception {
	List<Long> objList=null;
	try {
		begin();
		String orgCon="";
		if(orgId!=0)
			orgCon= " and a.organizationId.id="+orgId;
		
		objList=getSession().createQuery("select  new com.webspark.model.S_OptionModel(a.option_id.option_id,b.name) " +
				" from OrganizationOptionMappingModel a ,S_LanguageMappingModel b where a.option_id.option_id=b.option " +orgCon+
				" and b.language.id=:lang order by a.option_id.option_name")
				.setParameter("lang", langId).list();
		
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
}