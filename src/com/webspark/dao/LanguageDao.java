package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.model.S_ModuleModel;

@SuppressWarnings("serial")
public class LanguageDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public long save(S_LanguageModel mdl) throws Exception{
		try{
			begin();
			getSession().save(mdl);
			
			List moduleList=getSession().createQuery("from S_ModuleModel").list();
			if(moduleList.size()>0){
				S_ModuleModel module=null;
				for(int i=0;i<moduleList.size();i++){
					module=(S_ModuleModel)moduleList.get(i);
					S_LanguageMappingModel lmm=new S_LanguageMappingModel();
					lmm.setType((long)1);
					lmm.setLanguage(new S_LanguageModel(mdl.getId()));
					lmm.setOption(module.getId());
					lmm.setName(module.getModule_name());
					getSession().save(lmm);
				}
			}
			flush();
			
			List optionGroupList=getSession().createQuery("from S_OptionGroupModel").list();
			if(optionGroupList.size()>0){
				S_OptionGroupModel optionGroup=null;
				for(int i=0;i<optionGroupList.size();i++){
					optionGroup=(S_OptionGroupModel)optionGroupList.get(i);
					S_LanguageMappingModel lmm=new S_LanguageMappingModel();
					lmm.setType((long)2);
					lmm.setLanguage(new S_LanguageModel(mdl.getId()));
					lmm.setOption(optionGroup.getId());
					lmm.setName(optionGroup.getOption_group_name());
					getSession().save(lmm);
				}
			}
			flush();
			
			List optionList=getSession().createQuery("from S_OptionModel").list();
			if(optionList.size()>0){
				S_OptionModel option=null;
				for(int i=0;i<optionList.size();i++){
					option=(S_OptionModel)optionList.get(i);
					S_LanguageMappingModel lmm=new S_LanguageMappingModel();
					lmm.setType((long)3);
					lmm.setLanguage(new S_LanguageModel(mdl.getId()));
					lmm.setOption(option.getOption_id());
					lmm.setName(option.getOption_name());
					getSession().save(lmm);
				}
			}
			flush();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl.getId();
	}
	
	@SuppressWarnings("rawtypes")
	public void update(S_LanguageModel mdl) throws Exception{
		try{
			begin();
			getSession().update(mdl);
			
			List moduleList=getSession().createQuery("from S_ModuleModel").list();
			if(moduleList.size()>0){
				S_ModuleModel module=null;
				for(int i=0;i<moduleList.size();i++){
					module=(S_ModuleModel)moduleList.get(i);
					S_LanguageMappingModel lmm=null;
					
					lmm=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=1 and language.id=:language and option=:option")
							.setParameter("language", mdl.getId()).setParameter("option", module.getId()).uniqueResult();
					if(lmm!=null){

					}
					else{
						lmm=new S_LanguageMappingModel();
						lmm.setType((long)1);
						lmm.setLanguage(new S_LanguageModel(mdl.getId()));
						lmm.setOption(module.getId());
						lmm.setName(module.getModule_name());
						getSession().save(lmm);
					}
				}
			}
			flush();
			
			List optionGroupList=getSession().createQuery("from S_OptionGroupModel").list();
			if(optionGroupList.size()>0){
				S_OptionGroupModel optionGroup=null;
				for(int i=0;i<optionGroupList.size();i++){
					optionGroup=(S_OptionGroupModel)optionGroupList.get(i);
					S_LanguageMappingModel lmm=null;
					
					lmm=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=2 and language.id=:language and option=:option")
							.setParameter("language", mdl.getId()).setParameter("option", optionGroup.getId()).uniqueResult();
					if(lmm!=null){

					}
					else{
						lmm=new S_LanguageMappingModel();
						lmm.setType((long)2);
						lmm.setLanguage(new S_LanguageModel(mdl.getId()));
						lmm.setOption(optionGroup.getId());
						lmm.setName(optionGroup.getOption_group_name());
						getSession().save(lmm);
					}
				}
			}
			flush();
			
			List optionList=getSession().createQuery("from S_OptionModel").list();
			if(optionList.size()>0){
				S_OptionModel option=null;
				for(int i=0;i<optionList.size();i++){
					option=(S_OptionModel)optionList.get(i);
					S_LanguageMappingModel lmm=null;
					
					lmm=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=3 and language.id=:language and option=:option")
							.setParameter("language", mdl.getId()).setParameter("option", option.getOption_id()).uniqueResult();
					if(lmm!=null){
						
					}
					else{
						lmm=new S_LanguageMappingModel();
						lmm.setType((long)3);
						lmm.setLanguage(new S_LanguageModel(mdl.getId()));
						lmm.setOption(option.getOption_id());
						lmm.setName(option.getOption_name());
						getSession().save(lmm);
					}
				}
			}
			flush();
			
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	public void delete(long id) throws Exception{
		try{
			begin();
			S_LanguageModel mdl=(S_LanguageModel)getSession().get(S_LanguageModel.class, id);
			getSession().createQuery("delete from S_LanguageMappingModel where language.id=:language")
						.setParameter("language", mdl.getId()).executeUpdate();
			flush();
			getSession().delete(mdl);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getAllLanguages() throws Exception{
		List list=new ArrayList();
		try{
			begin();
			list=getSession().createQuery("select new com.webspark.model.S_LanguageModel(id,name) from S_LanguageModel order by id").list();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return list;
	}
	
	public S_LanguageModel getLanguage(long lang_id) throws Exception{
		S_LanguageModel mdl=null;
		try{
			begin();
			mdl=(S_LanguageModel)getSession().get(S_LanguageModel.class, lang_id);
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
	public S_LanguageModel getDefaultLanguage() throws Exception{
		S_LanguageModel mdl=null;
		try{
			begin();
			mdl=(S_LanguageModel)getSession().createQuery(" from S_LanguageModel where id=( select min(id) from S_LanguageModel)").uniqueResult();
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return mdl;
	}
	
}
