/**
 * 
 */
package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.HolidayModel;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author sangeeth
 * @date 02-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class HolidayDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	public void save(List list)throws Exception {
		try{
			begin();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				HolidayModel mdl = (HolidayModel) itr.next();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
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
	
	public void delete(HolidayModel mdl)throws Exception {
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
	
	public HolidayModel getHolidayModel(long id) throws Exception{
		HolidayModel mdl=null;
		try {
			begin();
			mdl=(HolidayModel)getSession().get(HolidayModel.class, id);
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
	
	@SuppressWarnings("rawtypes")
	public List getHolidayModelList(long office, Date date) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("from HolidayModel where date between :date and :date  and office.id=:office")
					.setParameter("date", date).setParameter("office", office).list();
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
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public List getHolidayModel(long office, Date start, Date end) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("from HolidayModel where date between :start and :end and office.id=:office order by date ASC")
					.setParameter("start", start).setParameter("end", end).setParameter("office", office).list();
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
		return list;
	}
	
}
