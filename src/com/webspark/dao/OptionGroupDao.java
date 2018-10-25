package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_OptionGroupModel;

/**
 * @Author Jinshad P.T.
 */

public class OptionGroupDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6915101401905952362L;
	List resultList = new ArrayList();

	public long save(S_OptionGroupModel obj) throws Exception {

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
					map.setType((long)2);
					map.setLanguage(new S_LanguageModel(lang.getId()));
					map.setOption(obj.getId());
					map.setName(obj.getOption_group_name());
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
	
	
	public void update(S_OptionGroupModel sts) throws Exception {

		try {

			begin();
			getSession().update(sts);
			List list=new ArrayList();
			list=getSession().createQuery("from S_LanguageModel order by id").list();
			if(list!=null && list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					S_LanguageModel lang = (S_LanguageModel) itr.next();
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=2 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", sts.getId()).uniqueResult();
					if(map!=null){
						map.setType((long)2);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(sts.getId());
						map.setName(sts.getOption_group_name());
						getSession().update(map);
					}
					else{
						map=new S_LanguageMappingModel();
						map.setType((long)2);
						map.setLanguage(new S_LanguageModel(lang.getId()));
						map.setOption(sts.getId());
						map.setName(sts.getOption_group_name());
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
					S_LanguageMappingModel map=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=2 and language.id=:language and option=:option")
							.setParameter("language", lang.getId()).setParameter("option", id).uniqueResult();
					if(map!=null)
						getSession().delete(map);
					flush();
				}
			}
			getSession().delete(new S_OptionGroupModel(id));
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
	
	
	public List getAllOptionGroups()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_OptionGroupModel order by priority_order").list();
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
	

	
	public S_OptionGroupModel getOptionGroup(long stsId) throws Exception {
		S_OptionGroupModel mod=null;
		try {
			begin();
			mod=(S_OptionGroupModel) getSession().get(S_OptionGroupModel.class, stsId);
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
		return mod;
	}
	
	
	public List getOptionGroupsUnderModule(long mod_id)
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_OptionGroupModel where module.id=:mod")
							.setLong("mod", mod_id).list();
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
	
	
}
