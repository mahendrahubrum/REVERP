package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.bean.SelectionFieldBean;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.DivisionModel;

public class DivisionDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 3400264992616027878L;
	
	List resultList = new ArrayList();
	 
	@SuppressWarnings("finally")
	public long save(DivisionModel obj) throws Exception {

		try {
	
			begin();
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
	
	
	public void Update(DivisionModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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
			getSession().delete(new DivisionModel(id));
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
	
	
	public List getDivisions(long org_id) throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.DivisionModel(id,name) from DivisionModel where organization_id="+org_id).list();
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
	
	
	public List getDivisionsUnderOrg(long org_id) throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.DivisionModel(id,name) from DivisionModel where organization_id=:org")
							.setLong("org", org_id).list();
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
	
	
	public DivisionModel getDivision(long Id) throws Exception {
		DivisionModel lm=null;
		try {
			

			begin();
			lm = (DivisionModel)getSession().get(DivisionModel.class,Id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return lm;
		}
	}
	
	public String getDivisionName(long divId) throws Exception {
		String name="";
		try {

			begin();
			name = (String) getSession().createQuery("select name from DivisionModel where id=:id").setLong("id", divId).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return name;
	}
	

	public int getLevel(Long grpId) throws Exception {
		int level=0;
		try {
			begin();
			level = (Integer) getSession().createQuery("select level " +
					"from DivisionModel where id=:id").setLong("id", grpId).uniqueResult();
			
				
			commit();			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return level;
	}


	public boolean isChildExists(long parentId) throws Exception {
		boolean flag=false;
		try {
			begin();
			List lis =  getSession().createQuery("from DivisionModel where parent_id=:id").setLong("id", parentId).list();
			
			if(lis!=null&&lis.size()>0){
				flag=true;
			}
			commit();			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		flush();
		close();
		return flag;
	}
	
	
	public List getDivisionsHierarchy(long orgId) throws Exception {
		
		List divList=new ArrayList();
		try {
			
			begin();
			divList.addAll(getSession().createQuery(" select new com.webspark.bean.SelectionFieldBean(id,parent_id,name) from DivisionModel where parent_id=0 and organization_id=:org")
							.setLong("org", orgId).list());
			
			if(divList.size()>0)
				divList.addAll(getDivisionsUnderParent(divList));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return divList;
	}
	public List getDivisionsUnderParent(List parentIds) throws Exception {
		
		List divList=new ArrayList();
		if(parentIds.size()>0){
			Iterator iter=parentIds.iterator();
			SelectionFieldBean div;
			while (iter.hasNext()) {
				div = (SelectionFieldBean) iter.next();
				if(div!=null)
					divList.addAll(getSession().createQuery("select new com.webspark.bean.SelectionFieldBean(id,parent_id,name) from DivisionModel where parent_id =:org)")
						.setParameter("org", div.getId()).list());
			}
			
		}
		if(divList.size()>0)
			divList.addAll(getDivisionsUnderParent(divList));
			

		return divList;
	}
	
}
