package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;

/**
 * @Author Jinshad P.T.
 */

public class LoginOptionMappingDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866874486369104513L;
	
	private List resultList=new ArrayList();

	
	
	public List<Long> selectOptionsToUser(long login_id) throws Exception {
		
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select option_id.option_id from S_LoginOptionMappingModel where login_id.id=:LogId")
							.setParameter("LogId", login_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return objList;
		}
	}
	
	public List getAllOptionsByRole(boolean isSuperAdmin) throws Exception {
		try {

			begin();
			
			if(isSuperAdmin){
				resultList = getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(option_id,concat(option_name,'(',group.module.module_name,')')) FROM S_OptionModel order by group.module.module_name").list();
			}
			else {
				resultList = getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(option_id,concat(option_name,'(',group.module.module_name,')')) FROM S_OptionModel where active='Y' order by  group.module.module_name").list();
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
		return resultList;
	}
	
	public List getAllOptionsByRole(boolean isSuperAdmin,long laguageId) throws Exception {
		try {

			begin();
			
			if(isSuperAdmin){
				resultList = getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(option,name) FROM  S_LanguageMappingModel where language.id=:lang " +
						" order by name").setParameter("lang", laguageId).list();
			}
			else {
				resultList = getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(a.option_id,b.name) FROM S_OptionModel a ,S_LanguageMappingModel b where a.active='Y' and b.language.id=:lang " +
						" and a.option_id=b.option order by b.name").setParameter("lang", laguageId).list();
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
		return resultList;
	}
	
	
	public void updateOptionsToUser(long login_id, List<S_LoginOptionMappingModel> rolOptList) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:LogId")
							.setParameter("LogId", login_id).executeUpdate();
			
			for (S_LoginOptionMappingModel obj: rolOptList) {
				getSession().save(obj);
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}
	
	
	/*public boolean isOptionsAvailToUser(long login_id, long optionId) throws Exception {
		boolean avail=false;
		try {
			begin();
			
			int siz=getSession().createQuery("select option_id.option_id from S_LoginOptionMappingModel where " +
					"login_id.id=:LogId and option_id.option_id=:optId")
						.setParameter("LogId", login_id).setParameter("optId", optionId).list().size();
			
			commit();
			
			if(siz>0)
				avail= true;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			flush();
			close();
			return avail;
		}
	}*/
	
	
	public boolean isOptionsAvailToUser(long login_id, long optionId, long officeId) throws Exception {
		boolean avail=false;
		try {
			begin();
			List depOptList=null;
			long siz=0;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",login_id).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				siz=(Long)getSession().createQuery("select count(a.id) from S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b,OfficeOptionMappingModel c  where " +
					"a.login_id.id=:LogId and a.option_id.option_id=:optId and b.option_id.id=:optId and c.option_id.id=:optId and b.departmentId.id=:dep and c.officeId.id=:ofc ")
						.setLong("LogId", login_id).setLong("optId", optionId)
						.setLong("dep", (Long)dep).setLong("ofc", officeId).uniqueResult();
			}else{
				siz=(Long) getSession().createQuery("select count(a.id) from S_LoginOptionMappingModel a,OfficeOptionMappingModel b  where " +
					"a.login_id.id=:LogId and a.option_id.option_id=:optId and b.option_id.id=:optId and b.officeId.id=:ofc")
						.setLong("LogId", login_id).setLong("optId", optionId)
						.setLong("ofc", officeId).uniqueResult();
			}
			commit();
			
			if(siz>0)
				avail= true;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			return false;
		} finally {
			flush();
			close();
			return avail;
		}
	}
	
	
	public void updateOptionsToUserFromRole(long login_id, long roleId) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:LogId")
				.setParameter("LogId", login_id).executeUpdate();
			
			
			List<Long> objList=getSession().createQuery("select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
							.setParameter("RolId", roleId).list();
		
			S_LoginOptionMappingModel lomm;
			for (Long opt_id : objList) {
				lomm=new S_LoginOptionMappingModel();
				
				lomm.setLogin_id(new S_LoginModel(login_id));
				lomm.setOption_id(new S_OptionModel(opt_id));
				lomm.setActive('Y');
				
				getSession().save(lomm);
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

	public List getAllOptionsUnderOffice(long officeID) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select a.option_id from OfficeOptionMappingModel a where a.officeId.id=:LogId")
							.setParameter("LogId", officeID).list();
			
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
