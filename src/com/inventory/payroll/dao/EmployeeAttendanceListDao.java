package com.inventory.payroll.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.AttendanceModel;
import com.inventory.payroll.model.LeaveDateModel;
import com.inventory.payroll.model.UserLeaveAllocationModel;
import com.inventory.payroll.model.UserLeaveMapModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

/**
 * @author sangeeth
 * @date 19-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class EmployeeAttendanceListDao extends SHibernate implements Serializable{

	
	public int getHolidaysInMonth(Date start, Date end, long office)throws Exception{
		int days=0;
		try {
			begin();
			Object obj = getSession().createQuery("select count(id) from HolidayModel where date between :start and :end and office.id=:office")
						.setParameter("start", start).setParameter("end", end).setParameter("office", office).uniqueResult();
			if(obj!=null)
				days=Integer.parseInt((Long)obj+"");
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
		return days;
	}
	
	
	public double getLeavesInMonth(Date start, Date end, long office, long user)throws Exception{
		double days=0;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(sum(days),0) from LeaveDateModel where date between :start and :end and officeId=:office " +
					" and userId=:user")
						.setParameter("start", start).setParameter("end", end)
						.setParameter("office", office).setParameter("user", user).uniqueResult();
			if(obj!=null)
				days=(Double)obj;
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
		return days;
	}

	
	public UserLeaveMapModel getUserLeaveMapModel(Date date, long userId, long office) throws Exception{
		UserLeaveMapModel mdl=null;
		try {
			begin();
			mdl=(UserLeaveMapModel)getSession().createQuery("from UserLeaveMapModel where date=:date and userId=:userId and officeId=:office")
					.setParameter("date", date).setParameter("userId", userId).setParameter("office", office).uniqueResult();
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
	
	
	public UserLeaveMapModel getUserLeaveMapModel(long id) throws Exception{
		UserLeaveMapModel mdl=null;
		try {
			begin();
			mdl=(UserLeaveMapModel)getSession().get(UserLeaveMapModel.class, id);
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
	public void save(List list, Date date, Date startDate, Date endDate, long office, long user) throws Exception{
		try {
			begin();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			
			List leaveList=new ArrayList();
			leaveList=getSession().createQuery("from UserLeaveMapModel where date between :start and :end and userId=:user and officeId=:office")
									.setParameter("start", startDate).setParameter("end", endDate)
									.setParameter("office", office).setParameter("user", user).list();
			Iterator itr=null;
			if(leaveList.size()>0){
				itr=leaveList.iterator();
				while (itr.hasNext()) {
					UserLeaveMapModel mdl = (UserLeaveMapModel) itr.next();
					if(mdl!=null){
						UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
								" and leave_type.id=:type and year=:year and user.id=:user")
								.setParameter("year", (long)calendar.get(Calendar.YEAR))
								.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId())
								.setParameter("type", mdl.getLeave_type().getId()).uniqueResult();

						LeaveDateModel leaveDate =(LeaveDateModel)getSession().createQuery("from LeaveDateModel where officeId=:office and userId=:user" +
								" and date=:date and leave is null")
								.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId()).setParameter("date", mdl.getDate()).uniqueResult();
						double days=0;
						if(leaveDate!=null)
							days=leaveDate.getDays();
						if(allMdl!=null){
							double taken=0;
							taken=CommonUtil.roundNumber(allMdl.getLeave_taken()-days);
							allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
							getSession().update(allMdl);
							flush();
						}
					}
				}
			}
			getSession().clear();
			itr=list.iterator();
			while (itr.hasNext()) {
				UserLeaveMapModel mdl = (UserLeaveMapModel) itr.next();
				
				UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
						" and leave_type.id=:type and year=:year and user.id=:user")
						.setParameter("year", (long)calendar.get(Calendar.YEAR))
						.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId())
						.setParameter("type", mdl.getLeave_type().getId()).uniqueResult();
				
				LeaveDateModel leaveDate =(LeaveDateModel)getSession().createQuery("from LeaveDateModel where officeId=:office and userId=:user" +
						" and date=:date and leave is null")
						.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId()).setParameter("date", mdl.getDate()).uniqueResult();
				double days=0;
				if(leaveDate!=null)
					days=leaveDate.getDays();
				if(allMdl!=null){
					double taken=0;
					taken=CommonUtil.roundNumber(allMdl.getLeave_taken()+days);
					
					if(allMdl.getLeave_available()!=0){
						if(allMdl.getLeave_available()<taken){
							if(allMdl.getLeave_type().isLop())
								mdl.setLossOfPay(true);
						}
						else
							mdl.setLossOfPay(false);
					}
					else
						mdl.setLossOfPay(true);
					
					mdl.setNoOfDays(CommonUtil.roundNumber(days));
					
					allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
					getSession().update(allMdl);
					flush();
				}
				
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				
			}
			getSession().createQuery("update AttendanceModel set blocked=true where date between :start and :end and officeId=:office and userId=:user")
						.setParameter("start", startDate).setParameter("end", endDate).setParameter("office", office).setParameter("user", user).executeUpdate();
			flush();
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
	public void delete(List list, Date date, Date startDate, Date endDate, long office, long user) throws Exception{
		try {
			begin();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				UserLeaveMapModel mdl = (UserLeaveMapModel) itr.next();
				
				UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
						" and leave_type.id=:type and year=:year and user.id=:user")
						.setParameter("year", (long)calendar.get(Calendar.YEAR))
						.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId())
						.setParameter("type", mdl.getLeave_type().getId()).uniqueResult();
				
				LeaveDateModel leaveDate =(LeaveDateModel)getSession().createQuery("from LeaveDateModel where officeId=:office and userId=:user" +
						" and date=:date and leave is null")
						.setParameter("office", mdl.getOfficeId()).setParameter("user", mdl.getUserId()).setParameter("date", mdl.getDate()).uniqueResult();
				double days=0;
				if(leaveDate!=null)
					days=leaveDate.getDays();
				if(allMdl!=null){
					double taken=0;
					taken=CommonUtil.roundNumber(allMdl.getLeave_taken()-days);
					allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
					getSession().update(allMdl);
					flush();
				}
				getSession().createQuery("update AttendanceModel set blocked=false where date between :start and :end and officeId=:office and userId=:user")
							.setParameter("start", startDate).setParameter("end", endDate).setParameter("office", office).setParameter("user", user).executeUpdate();
							flush();
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
	
	
}