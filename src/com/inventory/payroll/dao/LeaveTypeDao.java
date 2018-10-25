package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.payroll.model.LeaveTypeModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class LeaveTypeDao extends SHibernate implements Serializable{

	public long save(LeaveTypeModel mdl) throws Exception{
		try {
			begin();
			getSession().save(mdl);
			commit();
		} catch (Exception e) {
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
	
	public LeaveTypeModel getLeaveTypeModel(long id) throws Exception{
		LeaveTypeModel mdl=null;
		try {
			begin();
			mdl=(LeaveTypeModel)getSession().get(LeaveTypeModel.class, id);
			commit();
		} catch (Exception e) {
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
	
	public void update(LeaveTypeModel mdl) throws Exception{
		try {
			begin();
			getSession().update(mdl);
			commit();
		} catch (Exception e) {
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
	
	public void delete(LeaveTypeModel mdl) throws Exception{
		try {
			begin();
			getSession().delete(mdl);
			commit();
		} catch (Exception e) {
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
	public List getLeaveTypeModelList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("select new com.inventory.payroll.model.LeaveTypeModel(id,name) " +
					"from LeaveTypeModel where office.id=:office order by id DESC").setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return resultList;
	}
	
	@SuppressWarnings("rawtypes")
	public List getLeaveTypeList(long office) throws Exception{
		List resultList=new ArrayList();
		try {
			begin();
			resultList=getSession().createQuery("from LeaveTypeModel where office.id=:office order by id DESC").setParameter("office", office).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return resultList;
	}
	
}