package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.model.ProductLicenseModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

public class OrganizationDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1148424557438639100L;
	List resultList = new ArrayList();

	public long save(S_OrganizationModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	
	public void update(S_OrganizationModel sts) throws Exception {

		try {

			begin();
			getSession().createQuery("delete from ProductLicenseModel where organizationId=:org").setParameter("org", sts.getId()).executeUpdate();
			getSession().save(sts.getAddress());
			getSession().update(sts);
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
			getSession().createQuery("delete from ProductLicenseModel where organizationId=:org").setParameter("org", id).executeUpdate();
			getSession().delete(new S_OrganizationModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			
		} 
			flush();
			close();
		
	}
	
	
	public List getAllOrganizations()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.S_OrganizationModel(id,name) FROM S_OrganizationModel" +
							" where active='Y'").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

	
	public S_OrganizationModel getOrganization(long id) throws Exception {
		S_OrganizationModel mod=null;
		try {
			begin();
			mod=(S_OrganizationModel) getSession().get(S_OrganizationModel.class, id);
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


	public void setExpiryDate(ProductLicenseModel licenseModel) throws Exception {
		try {
			begin();
			getSession().save(licenseModel);
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


	public ProductLicenseModel getProductLicense(long orgId) throws Exception {
		
		ProductLicenseModel list=null;
		
		try {
			begin();
			Object obj=getSession().createQuery("from ProductLicenseModel where organizationId=:org").setParameter("org", orgId).uniqueResult();
			commit();
			if(obj!=null)
				list=(ProductLicenseModel) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		} 
		return list;
	}
	
	
}
