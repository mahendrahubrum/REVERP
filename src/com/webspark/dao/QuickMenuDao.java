package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.QuickMenuModel;
import com.webspark.model.S_OptionModel;

/**
 * @Author Jinshad P.T.
 */

public class QuickMenuDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866874486369104513L;
	
	private List resultList=new ArrayList();
	
	public List<Long> getQuickMenuOptions(long login_id) throws Exception {
		
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select option_id.option_id from QuickMenuModel where login_id=:LogId")
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
	
	public List<S_OptionModel> getUserAllocatedOptions(long login_id) throws Exception {
		
		List<S_OptionModel> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(option_id.option_id,option_id.option_name) from S_LoginOptionMappingModel where login_id.id=:LogId order by option_name")
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
	
	public void updateQuickMenuToUser(long login_id, List<QuickMenuModel> rolOptList) throws Exception {
		try {
			begin();
			
			getSession().createQuery("delete from QuickMenuModel where login_id=:LogId")
							.setParameter("LogId", login_id).executeUpdate();
			
			for (QuickMenuModel obj: rolOptList) {
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
	
	
	public List<Long> getQuickMenuOfUser(long login_id, long officeId) throws Exception {
		
		List<Long> objList=null;
		try {
			begin();
			
			List depOptList=null;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",login_id).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				
				objList = getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(a.option_id.option_id,a.option_id.option_name) from QuickMenuModel a,S_DepartmentOptionMappingModel b" +
						" where a.login_id=:LogId and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.option_name")
						.setParameter("LogId", login_id).setLong("ofc", (Long)dep).list();
			}else{
				objList=getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(a.option_id.option_id,a.option_id.option_name) from QuickMenuModel a,OfficeOptionMappingModel b where a.login_id=:LogId and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.option_name")
							.setParameter("LogId", login_id).setLong("ofc", officeId).list();
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
			return objList;
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
	
}
