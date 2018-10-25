package com.webspark.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.webspark.model.S_LanguageMappingModel;

@SuppressWarnings("serial")
public class LanguageMappingDao extends SHibernate implements Serializable{
	
	public S_LanguageMappingModel getLanguageMappingModel(long typ,long language,long option) throws Exception {
		S_LanguageMappingModel mdl=null;
		try{
			begin();
			int type=Integer.parseInt(typ+"");
			switch (type){
				case 1 :mdl=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=1 and language.id=:language and option=:option")
							.setParameter("language", language).setParameter("option", option).uniqueResult();
						break;
				case 2 :mdl=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=2 and language.id=:language and option=:option")
						.setParameter("language", language).setParameter("option", option).uniqueResult();
						break;
				case 3 :mdl=(S_LanguageMappingModel)getSession().createQuery("from S_LanguageMappingModel where type=3 and language.id=:language and option=:option")
						.setParameter("language", language).setParameter("option", option).uniqueResult();
						break;
				default:mdl=null;
						break;
			}
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
	
	public void save(S_LanguageMappingModel mdl) throws Exception{
		try{
			begin();
			getSession().save(mdl);
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
	public void update(List list) throws Exception{
		try{
			begin();
			S_LanguageMappingModel map=null;
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					map=(S_LanguageMappingModel)itr.next();
					if(map.getId()!=0){
						getSession().update(map);
					}
					else{
						getSession().save(map);
					}
					flush();
				}
			}
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
	
	public void delete(S_LanguageMappingModel mdl) throws Exception{
		try{
			begin();
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
	
}
