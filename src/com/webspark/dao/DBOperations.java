package com.webspark.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.process.model.FinancialYearsModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.ReportIssueModel;
import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_OptionModel;
import com.webspark.model.S_ProjectOptionMapModel;

/**
 * @Author Jinshad P.T.
 */

public class DBOperations extends SHibernate implements Serializable {
	
	
	private static final long serialVersionUID = 5430335431140902218L;
	List resultList = new ArrayList();

	public long saveOption(S_OptionModel md) throws Exception {

		try {

			begin();
			getSession().save(md);
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=new S_LanguageMappingModel();
					map.setType((long)3);
					map.setLanguage(new S_LanguageModel(lang.getId()));
					map.setOption(md.getOption_id());
					map.setName(md.getOption_name());
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
		return md.getOption_id();
	}
	
	
	@SuppressWarnings("rawtypes")
	public void Update(S_OptionModel md) throws Exception {

		try {

			begin();
			getSession().update(md);
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=3 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", md.getOption_id()).uniqueResult();
					if(map!=null){
						map.setType((long)3);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(md.getOption_id());
						map.setName(md.getOption_name());
						getSession().update(map);
					}
					else{
						map=new S_LanguageMappingModel();
						map.setType((long)3);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(md.getOption_id());
						map.setName(md.getOption_name());
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
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=3 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", id).uniqueResult();
					if(map!=null)
						getSession().delete(map);
					flush();
				}
			}
			getSession().delete(new S_OptionModel(id));
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
	
	
	public List getOptions() throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("FROM S_OptionModel").list();
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
	

	public List getStatuses(String modelName, String fieldName)
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from S_StatusModel where model_name=:mdl and field_name=:fld")
					.setParameter("mdl", modelName)
					.setParameter("fld", fieldName).list();
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
	
	
	public S_OptionModel getOptionForOpen(long opId, long project_type) throws Exception {
		S_OptionModel opt=null;
		try {
			begin();
			Object obj=getSession().createQuery("from S_ProjectOptionMapModel a where a.option.option_id=:opt and a.project_type.id=:prj")
					.setLong("opt", opId).setLong("prj", project_type).uniqueResult();
			
			if(obj==null) {
				opt=(S_OptionModel) getSession().get(S_OptionModel.class, opId);
			}
			else {
				S_ProjectOptionMapModel mdl=(S_ProjectOptionMapModel) obj;
				opt=mdl.getOption();
				getSession().evict(opt);
				opt.setClass_name(mdl.getClass_name());
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
		return opt;
		
	}
	
	
	public S_OptionModel getOptionModel(long opId) throws Exception {
		S_OptionModel opt=null;
		try {
			begin();
			
			opt=(S_OptionModel) getSession().get(S_OptionModel.class, opId);
				
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
		return opt;
		
	}
	
	
	public List getOptionsUnderGroup(long grpId) throws Exception {
		resultList=null;
		try {

			begin();
			resultList = getSession().createQuery("FROM S_OptionModel where group.id=:grp  order by priority_order")
					.setLong("grp", grpId).list();
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
	
	
	public List getOptionsUnderModule(long moduleId) throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("FROM S_OptionModel where group.module.id=:mdl")
					.setLong("mdl", moduleId).list();
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
	
	
	public List getFinancialYears(long ofc_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.process.model.FinancialYearsModel(id,name)" +
					" from FinancialYearsModel where office_id=:ofc")
							.setLong("ofc", ofc_id).list();
			
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
	
	
	public long getCurrentFinYear(long office_id, Date fin_st_dt,Date fin_end_dt ) throws Exception {
		long id=0;
		try {
			begin();
			
			Object obj=getSession().createQuery("select max(id) from FinancialYearsModel where start_date=:st and end_date=:end" +
					" and office_id=:ofc and status=1")
							.setParameter("st", fin_st_dt).setParameter("end", fin_end_dt)
							.setLong("ofc", office_id).uniqueResult();
			
			if(obj!=null)
				id=(Long) obj;
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
		return id;
	}
	
	
	public FinancialYearsModel getFinancialYear(long id) throws Exception {
		FinancialYearsModel fy=null;
		try {
			begin();
			fy=(FinancialYearsModel) getSession().get(FinancialYearsModel.class, id);
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
		return fy;
	}


	@SuppressWarnings("unchecked")
	public List getCreateMenuOfUser(long login_id, long officeId) throws Exception {
		
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
						"(a.option_id.option_id,a.option_id.option_name) from S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b where " +
						" a.login_id.id=:LogId and a.option_id.create=true and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.option_name")
						.setParameter("LogId", login_id).setLong("ofc", (Long)dep).list();
			}else{
				objList=getSession().createQuery("select new com.webspark.model.S_OptionModel" +
						"(a.option_id.option_id,a.option_id.option_name) from S_LoginOptionMappingModel a,OfficeOptionMappingModel b where " +
						" a.login_id.id=:LogId and a.option_id.create=true and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.option_name")
							.setParameter("LogId", login_id).setLong("ofc", officeId).list();
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
		return objList;
	}


	public List getRecentMenuOfUser(long login_id, long officeId) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery(" from ActivityLogModel where login=:LogId and billId!=0 and office_id=:ofc order by id desc").setMaxResults(10)
					.setLong("ofc", officeId).setParameter("LogId", login_id).list();
			
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
		return objList;
	}


	public S_ProjectOptionMapModel getProjectOptionMapping(long projectType,long optionId) throws Exception {
		S_ProjectOptionMapModel proj=null;
		try {
			begin();
			
			Object obj=getSession().createQuery(" from S_ProjectOptionMapModel where project_type.id=:proj and option.option_id=:opt")
							.setParameter("proj", projectType).setParameter("opt", optionId).uniqueResult();
			
			if(obj!=null)
				proj=(S_ProjectOptionMapModel) obj;
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
		return proj;
	}


	@SuppressWarnings("unchecked")
	public List getOptionsWithAnalytics(long moduleId,long loginId) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select distinct a.option_id from S_LoginOptionMappingModel a  " +
					"where  a.option_id.group.module.id =:module and a.login_id.id=:login  and a.option_id.analyticsClassName!=null and a.option_id.analyticsClassName!='' order by a.option_id.option_name ")
							.setParameter("module", moduleId).setParameter("login", loginId).list();
			
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
		return objList;
	}


	public List getOptionsUnderGroupAssignedToUser(long groupId,long loginId, long officeId) throws Exception {
		try {

			begin();
			
			List depOptList=null;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",loginId).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b " +
						"where a.option_id.group.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.priority_order")
						.setLong("grp", groupId).setLong("login", loginId).setLong("ofc", (Long)dep).list();
			
			}else{
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
					"where a.option_id.group.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.priority_order")
					.setLong("grp", groupId).setLong("login", loginId).setLong("ofc", officeId).list();
			
			}
//			resultList = getSession().createQuery("select distinct a.option_id.group FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
//					"where a.option_id.group.module.id=:grp  and a.login_id.id=:login  and b.option_id.id=a.option_id and b.officeId.id=:ofc order by a.option_id.group.priority_order")
//					.setLong("grp", moduleId).setLong("login", loginId).setLong("ofc", officeId).list();
			
			
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


	public List getOptionGroupsUnderModuleAssignedToUser(long moduleId,long loginId, long officeId) throws Exception {
		try {

			begin();
			List depOptList=null;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",loginId).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				resultList = getSession().createQuery("select distinct a.option_id.group FROM S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b " +
						"where a.option_id.group.module.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.group.priority_order")
						.setLong("grp", moduleId).setLong("login", loginId).setLong("ofc", (Long)dep).list();
			
			}else{
				resultList = getSession().createQuery("select distinct a.option_id.group FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
						"where a.option_id.group.module.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.group.priority_order")
						.setLong("grp", moduleId).setLong("login", loginId).setLong("ofc", officeId).list();
			
			}
			
//			resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
//					"where a.option_id.group.id=:grp  and a.login_id.id=:login and b.option_id.id=a.option_id and b.officeId.id=:ofc order by "+sort)
//					.setLong("grp", groupId).setLong("login", loginId).setLong("ofc", officeId).list();
			
			
			
			
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
	
	
	public List getOptionsLikeString(String text,long loginId, long officeId) throws Exception {
		try {
			begin();
			List depOptList=null;
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",loginId).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b " +
						"where a.option_id.option_name like :grp and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by a.option_id.option_name")
						.setString("grp", "%"+text+"%").setLong("login", loginId).setLong("ofc", (Long)dep).list();
			}else{
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
					"where a.option_id.option_name like :grp and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by a.option_id.option_name")
					.setString("grp", "%"+text+"%").setLong("login", loginId).setLong("ofc", officeId).list();
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
		return resultList;
	}
	
	
	public List getUnreadMailOfUser(long login_id) throws Exception {
			List lst=null;
			try {
				
				begin();
				
				lst= getSession().createQuery("from MyMailsModel where user_id=:usr and unreaded=true and folder_id=1"+
						"  order by date_time desc")
						.setLong("usr", login_id).list();
				
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


	@SuppressWarnings("unchecked")
	public List getUpdatesOfUser(long login_id) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			List userList=new ArrayList();
			userList.add(login_id);
			
			userList.addAll(getSession().createQuery("select loginId.id from UserModel where superior_id=:id").setParameter("id", login_id).list());
			
			
			objList=getSession().createQuery(" from ActivityLogModel where login in (:LogId) and billId!=0 order by id desc").setMaxResults(4)
							.setParameterList("LogId", userList).list();
			
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
		return objList;
	}


	public String getUserNameFromId(long loginID) throws Exception {
		String name="";
		try {
			begin();
			name=(String) getSession().createQuery("select concat(first_name,' ',middle_name,' ',last_name) from UserModel where loginId.id=:id")
					.setParameter("id", loginID).uniqueResult();
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
		return name;
	}
	
	public List getOptionsUnderGroupAssignedToUser(long groupId,long loginId,int sortType, long officeId) throws Exception {
		try {

			String sort=" a.option_id.priority_order";
			if(sortType==1)
				sort=" a.option_id.priority_order";
			else if(sortType==2)
				sort=" a.option_id.option_name";
			List depOptList=null;
			
			begin();
			
			
			
//			resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a " +
//					"where a.option_id.group.id=:grp  and a.login_id.id=:login order by "+sort)
//					.setLong("grp", groupId).setLong("login", loginId).list();
			Object dep=getSession().createQuery("select a.department.id from UserModel a where a.loginId.id=:log").setParameter("log",loginId).uniqueResult();
			if(dep!=null){
				depOptList=getSession().createQuery("select id from S_DepartmentOptionMappingModel  where departmentId.id=:dep").setParameter("dep",(Long)dep).list();
			}
			
			if(depOptList!=null&&depOptList.size()>0){
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,S_DepartmentOptionMappingModel b " +
						"where a.option_id.group.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.departmentId.id=:ofc order by "+sort)
						.setLong("grp", groupId).setLong("login", loginId).setLong("ofc", (Long)dep).list();
			}else{
				resultList = getSession().createQuery("select distinct a.option_id FROM S_LoginOptionMappingModel a,OfficeOptionMappingModel b " +
					"where a.option_id.group.id=:grp  and a.login_id.id=:login and b.option_id.option_id=a.option_id.option_id and b.officeId.id=:ofc order by "+sort)
					.setLong("grp", groupId).setLong("login", loginId).setLong("ofc", officeId).list();
			}
			
//			long siz=(Long) getSession().createQuery("select count(a.id) from S_LoginOptionMappingModel a,OfficeOptionMappingModel b  where " +
//					"a.login_id.id=:LogId and a.option_id.option_id=:optId and b.option_id.id=:optId and b.officeId.id=:ofc")
//						.setLong("LogId", login_id).setLong("optId", optionId)
//						.setLong("ofc", officeId).uniqueResult();
			
			
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


	public List getReportedIssues(long login_id) throws Exception {
		List list=null;
		try {

			begin();
			list = getSession().createQuery(" FROM ReportIssueModel  " +
					"where to_user=:to_user and status=1 order by date desc, id desc")
					.setLong("to_user", login_id).list();
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
		return list;
	}


	public ReportIssueModel getReportedIssueModel(long id) throws Exception {
		ReportIssueModel mdl=null;
		try {
			begin();
			mdl = (ReportIssueModel) getSession().get(ReportIssueModel.class, id);
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
		return mdl;
	}


	public void updateReportedIssue(long id) throws Exception {
		ReportIssueModel mdl=null;
		try {
			begin();
			mdl = (ReportIssueModel) getSession().get(ReportIssueModel.class, id);
			mdl.setStatus(2);
			getSession().update(mdl);
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
	
	public List getRecentlyAccessesOptions(long login_id, long officeId)
			throws Exception {
		List objList = new ArrayList();
		List tmpLst = null;
		try {
			begin();

			List optIDs = getSession()
					.createQuery(
							"select distinct option from ActivityLogModel where login=:LogId and office_id=:ofc order by id desc")
					.setMaxResults(SConstants.RECENTLY_USED_OPTIONS_COUNT)
					.setLong("ofc", officeId).setParameter("LogId", login_id)
					.list();
			if (optIDs != null && optIDs.size() > 0) {
				tmpLst = getSession()
						.createQuery(
								"from S_OptionModel where option_id in (:lst)")
						.setParameterList("lst", optIDs).list();

			}
			commit();

			long id = 0;
			S_OptionModel obj;
			Iterator itr1 = optIDs.iterator();
			while (itr1.hasNext()) {
				id = (Long) itr1.next();
				Iterator itr2 = tmpLst.iterator();
				while (itr2.hasNext()) {
					obj = (S_OptionModel) itr2.next();
					if (obj.getOption_id() == id)
						objList.add(obj);
				}
			}

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return objList;
	}

	

}
