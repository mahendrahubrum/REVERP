package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.uac.model.S_ModuleModel;

public class ModuleDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8101243619593185989L;
	@SuppressWarnings("rawtypes")
	List resultList = new ArrayList();

	@SuppressWarnings("rawtypes")
	public long saveModule(S_ModuleModel obj) throws Exception {

		try {
			begin();
			getSession().save(obj);
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=new S_LanguageMappingModel();
					map.setType((long)1);
					map.setLanguage(new S_LanguageModel(lang.getId()));
					map.setOption(obj.getId());
					map.setName(obj.getModule_name());
					getSession().save(map);
					flush();
				}
			}
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
		return obj.getId();
	}
	
	
	@SuppressWarnings("rawtypes")
	public void Update(S_ModuleModel sts) throws Exception {

		try {

			begin();
			getSession().update(sts);
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=1 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", sts.getId()).uniqueResult();
					if(map!=null){
						map.setType((long)1);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(sts.getId());
						map.setName(sts.getModule_name());
						getSession().update(map);
					}
					else{
						map=new S_LanguageMappingModel();
						map.setType((long)1);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(sts.getId());
						map.setName(sts.getModule_name());
						getSession().save(map);
					}
					flush();
				}
			}
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
	
	
	@SuppressWarnings("rawtypes")
	public void delete(long id) throws Exception {

		try {
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=1 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", id).uniqueResult();
					if(map!=null)
						getSession().delete(map);
					flush();
				}
			}
			getSession().delete(new S_ModuleModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
			flush();
			close();
		
	}
	
	
	public List getAllModules()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_ModuleModel").list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	

	
	
	public S_ModuleModel getModule(long stsId) throws Exception {
		S_ModuleModel mod=null;
		try {
			begin();
			mod=(S_ModuleModel) getSession().get(S_ModuleModel.class, stsId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}


	public List getAllAssignedModules(long loginId, long officeId) throws Exception {
		List list=null;
		try {
			begin();
			
			List depOptList=null;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",loginId).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				list = getSession().createQuery("select distinct a.option_id.group.module FROM S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b " +
						"where a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.group.module.priority_order ")
					.setParameter("login", loginId).setLong("ofc", (Long)dep).list();
			}else{
				list = getSession()
					.createQuery(
							"select distinct a.option_id.group.module from S_LoginOptionMappingModel a,OfficeOptionMappingModel b where " +
							"a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.group.module.priority_order ")
					.setParameter("login", loginId).setLong("ofc", officeId).list();
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
		return list;
	}
	
}
