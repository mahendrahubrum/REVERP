package com.inventory.payroll.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.AttendanceModel;
import com.inventory.payroll.model.LeaveDateModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * 
 * @author sangeeth
 * Automobile
 * 10-Jul-2015
 */

@SuppressWarnings("serial")
public class AttendanceDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	public void save(List list) throws Exception{
		try {
			begin();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				AttendanceModel mdl = (AttendanceModel) itr.next();
				if(mdl.getPresentLeave()!=SConstants.attendanceStatus.PRESENT){
					double days=0;
					if(mdl.getPresentLeave()==SConstants.attendanceStatus.LEAVE){
						days=1;
					}
					else if(mdl.getPresentLeave()==SConstants.attendanceStatus.HALF_DAY_LEAVE){
						days=0.5;
					}
					if(mdl.getLeaveId()==0 || days<1){
						LeaveDateModel leaveDate=null;
						try {
							leaveDate=(LeaveDateModel)getSession().createQuery("from LeaveDateModel where date=:date and " +
									" attendance=true and officeId=:officeId and userId=:userId and leave is null")
									.setParameter("date", mdl.getDate()).setParameter("userId", mdl.getUserId())
									.setParameter("officeId", mdl.getOfficeId()).uniqueResult();
							
							if(leaveDate==null){
								leaveDate=new LeaveDateModel();
								leaveDate.setUserId(mdl.getUserId());
								leaveDate.setLeave(null);
								leaveDate.setDate(CommonUtil.getSQLDateFromUtilDate(mdl.getDate()));
								leaveDate.setDays(CommonUtil.roundNumber(days));
								leaveDate.setOfficeId(mdl.getOfficeId());
								leaveDate.setAttendance(true);
								getSession().save(leaveDate);
								flush();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				
				flush();
			}
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
	
	
	public AttendanceModel getAttendanceModel(long id) throws Exception{
		AttendanceModel mdl=null;
		try {
			begin();
			mdl=(AttendanceModel)getSession().get(AttendanceModel.class, id);
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
	public List getAttendanceModel(Date date, long officeId) throws Exception{
		List list=new ArrayList();
		try {
			begin();
			list=getSession().createQuery("from AttendanceModel where officeId=:officeId and date=:date")
					.setParameter("officeId", officeId).setParameter("date", date).list();
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
	
	
	public AttendanceModel getAttendanceModel(Date date, long officeId, long user) throws Exception{
		AttendanceModel mdl;
		try {
			begin();
			mdl=(AttendanceModel)getSession().createQuery("select a from AttendanceModel a where a.officeId=:officeId " +
					"and a.date=:date and a.userId=:user")
					.setParameter("officeId", officeId)
					.setParameter("date", date)
					.setParameter("user", user).uniqueResult();
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
	public void delete(List list) throws Exception{
		try {
			begin();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				AttendanceModel mdl = (AttendanceModel) itr.next();
				
				if(mdl.getPresentLeave()!=SConstants.attendanceStatus.PRESENT){
					LeaveDateModel leaveDate=null;
					try {
						leaveDate=(LeaveDateModel)getSession().createQuery("from LeaveDateModel where date=:date and " +
								" attendance=true and officeId=:officeId and userId=:userId and leave is null")
								.setParameter("date", mdl.getDate()).setParameter("userId", mdl.getUserId())
								.setParameter("officeId", mdl.getOfficeId()).uniqueResult();
						if(leaveDate!=null){
							getSession().delete(leaveDate);
							flush();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				getSession().delete(mdl);
				flush();
			}
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
	
}