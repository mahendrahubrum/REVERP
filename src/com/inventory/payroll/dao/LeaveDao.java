package com.inventory.payroll.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.inventory.payroll.model.LeaveDateModel;
import com.inventory.payroll.model.LeaveHistoryModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.UserLeaveAllocationModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author sangeeth
 * @date 17-Nov-2015
 * @Project REVERP
 */

public class LeaveDao extends SHibernate {

	@SuppressWarnings({ "rawtypes"})
	public long save(LeaveModel leave, LeaveHistoryModel histMdl, long office)
			throws Exception {
		try {
			begin();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(leave.getFrom_date());
			getSession().save(leave);
			flush();
			
			if(leave.getDaysInYear().toString().trim().length()>0){
				List daysList=new ArrayList();
				String[] daysArr=leave.getDaysInYear().toString().trim().split(",");
				daysList=Arrays.asList(daysArr);
				long year=(long)calendar.get(Calendar.YEAR);
				if(daysList.size()>0){
					for(int i=0;i<daysList.size();i++){
						double daysInyear = Double.parseDouble(daysList.get(i).toString().trim());
						if(daysInyear>=0){
							UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
									" and leave_type.id=:type and year=:year and user.id=:user")
									.setParameter("year", (year+i))
									.setParameter("office", office).setParameter("user", leave.getUser().getId())
									.setParameter("type", leave.getLeave_type().getId()).uniqueResult();
							if(allMdl!=null){
								double taken=0;
								taken=CommonUtil.roundNumber(allMdl.getLeave_taken()+daysInyear);
								allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
								getSession().update(allMdl);
								flush();
							}
						}
					}
				}
			}
			
			Calendar diffCal=Calendar.getInstance();
			Calendar endCal=Calendar.getInstance();
			diffCal.setTime(leave.getFrom_date());
			endCal.setTime(leave.getTo_date());
			S_OfficeModel offc=(S_OfficeModel)getSession().get(S_OfficeModel.class, office);
			while (diffCal.getTime().compareTo(endCal.getTime())<=0) {
				List holidayList=new ArrayList();
				List weekOffList=new ArrayList();
				String[] holidays=offc.getHolidays().split(",");
				if(holidays.length>0)
					weekOffList=Arrays.asList(holidays);
				holidayList=getSession().createQuery("from HolidayModel where date between :date and :date  and office.id=:office")
						.setParameter("date", CommonUtil.getSQLDateFromUtilDate(diffCal.getTime())).setParameter("office", office).list();
				
				if(!(holidayList.size()>0) && !(weekOffList.contains(diffCal.get(Calendar.DAY_OF_WEEK)+"")) ){
					LeaveDateModel mdl=new LeaveDateModel();
					mdl.setLeave(new LeaveModel(leave.getId()));
					mdl.setUserId(leave.getUser().getId());
					mdl.setDate(CommonUtil.getSQLDateFromUtilDate(diffCal.getTime()));
					mdl.setAttendance(false);
					double days=0;
					if(leave.getNo_of_days()<1)
						days=0.5;
					else
						days=1;
					mdl.setDays(CommonUtil.roundNumber(days));
					mdl.setOfficeId(office);
					getSession().save(mdl);
					flush();
				}
				diffCal.add(Calendar.DAY_OF_MONTH, 1);
			}
			histMdl.setLeave(leave.getId());
			getSession().save(histMdl);
			flush();
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
		return leave.getId();
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List getAllLeaves(long empId) throws Exception {
		List list = new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from LeaveModel where user.id=:id order by id desc").setParameter("id", empId).list();
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

	
	
	public int getUserLeaveCount(long user, long office, long leaveType, long year) throws Exception {
		int count=0;
		try {
			begin();
			Object obj = getSession().createQuery("select count(id) from UserLeaveAllocationModel where user.office.id=:office " +
										" and leave_type.id=:type and year=:year and user.id=:user")
										.setParameter("year", year)
										.setParameter("office", office).setParameter("user", user)
										.setParameter("type", leaveType).uniqueResult();
			if(obj!=null)
				count=Integer.parseInt(""+(Long)obj);
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
		return count;
	}
	
	
	
	public double getLeaveOfEmployee(long user, long leaveType, Date toDate, long office) throws Exception {

		double count = 0;
		try {
			begin();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(toDate);
			Object ob = getSession().createQuery("select coalesce((leave_available-leave_taken),0) from UserLeaveAllocationModel where user.id=:user" +
					" and leave_type.id=:type and year=:year and user.office.id=:office")
					.setParameter("user", user).setParameter("type", leaveType).setParameter("office", office)
					.setParameter("year", (long)calendar.get(Calendar.YEAR)).uniqueResult();
			if (ob != null)
				count = (Double) ob;
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
		return count;

	}

	
	
	@SuppressWarnings("rawtypes")
	public void cancelLeave(long leaveId, Date date, long loginId) throws Exception {
		try {
			begin();
			
			LeaveModel leave = (LeaveModel) getSession().get(LeaveModel.class,leaveId);
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(leave.getFrom_date());
			getSession().createQuery("delete from LeaveDateModel where leave.id=:leave and officeId=:office and attendance=false and leave is not null")
						.setParameter("leave", leaveId).setParameter("office", leave.getUser().getOffice().getId()).executeUpdate();
			
			if(leave.getDaysInYear().toString().trim().length()>0){
				List daysList=new ArrayList();
				String[] daysArr=leave.getDaysInYear().toString().trim().split(",");
				daysList=Arrays.asList(daysArr);
				long year=(long)calendar.get(Calendar.YEAR);
				if(daysList.size()>0){
					for(int i=0;i<daysList.size();i++){
						double daysInyear = Double.parseDouble(daysList.get(i).toString().trim());
						if(daysInyear>=0){
							UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
									" and leave_type.id=:type and year=:year and user.id=:user")
									.setParameter("year", (year+i))
									.setParameter("office", leave.getUser().getOffice().getId()).setParameter("user", leave.getUser().getId())
									.setParameter("type", leave.getLeave_type().getId()).uniqueResult();
							if(allMdl!=null){
								double taken=0;
								taken=CommonUtil.roundNumber(allMdl.getLeave_taken()-daysInyear);
								allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
								getSession().update(allMdl);
								flush();
							}
						}
					}
				}
			}
			
			LeaveHistoryModel histMdl=new LeaveHistoryModel();
			histMdl.setComments(leave.getReason());
			histMdl.setDate(date);
			histMdl.setStatus(SConstants.leaveStatus.LEAVE_CANCELED);
			histMdl.setLogin(new S_LoginModel(loginId));
			
			getSession().createQuery("update LeaveModel set status=:status where id=:id")
						.setParameter("id", leaveId).setParameter("status", SConstants.leaveStatus.LEAVE_CANCELED).executeUpdate();
			flush();
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
	public List getAllAppliedLeaves(long loginId) throws Exception {
		
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("select distinct a from LeaveModel a, LeaveHistoryModel b where b.login.id=:id and b.leave=a.id order by a.id desc")
								.setParameter("id", loginId).list();
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
	
	
	
	public long getEligibleEmployee(long leaveId) throws Exception {
		
		long id=0;
		try {
			begin();
			Object obj=getSession().createQuery("select a.login.id from LeaveHistoryModel a where a.leave=:leave and a.id =" +
					" (select max(id) from LeaveHistoryModel c where c.leave=:leave)")
					.setParameter("leave", leaveId).uniqueResult();
			if(obj!=null)
				id=(Long)obj;
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
	
	
	
	public LeaveModel getLeaveModel(long leaveId) throws Exception {
		
		LeaveModel mdl=null;
		try {
			begin();
			mdl = (LeaveModel) getSession().get(LeaveModel.class, leaveId);
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

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Long> getAssignedUsers(long leaveId) throws Exception {
		List userList=new ArrayList();
		try {
			begin();
			LeaveModel leave=(LeaveModel)getSession().get(LeaveModel.class, leaveId);
			if(leave!=null){
				if(leave.getUser().getLoginId()!=null)
					userList.add(leave.getUser().getLoginId().getId());
			}
			userList.addAll(getSession().createQuery("select login.id from LeaveHistoryModel where leave=:leave")
								.setParameter("leave", leaveId).list());
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
		return userList;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public void doActionLeave(LeaveHistoryModel mdl, int status) throws Exception {
		
		try {
			begin();
			LeaveModel leave=(LeaveModel)getSession().get(LeaveModel.class, mdl.getLeave());
			getSession().createQuery("update LeaveModel set status=:status where id=:id")
						.setParameter("status", status).setParameter("id", mdl.getLeave()).executeUpdate();
			flush();
			if(status==SConstants.leaveStatus.LEAVE_REJECTED){
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(leave.getFrom_date());
				getSession().createQuery("delete from LeaveDateModel where leave.id=:leave and officeId=:office and attendance=false and leave is not null")
							.setParameter("leave", mdl.getLeave()).setParameter("office", leave.getUser().getOffice().getId()).executeUpdate();
				
				if(leave.getDaysInYear().toString().trim().length()>0){
					List daysList=new ArrayList();
					String[] daysArr=leave.getDaysInYear().toString().trim().split(",");
					daysList=Arrays.asList(daysArr);
					long year=(long)calendar.get(Calendar.YEAR);
					if(daysList.size()>0){
						for(int i=0;i<daysList.size();i++){
							double daysInyear = Double.parseDouble(daysList.get(i).toString().trim());
							if(daysInyear>=0){
								UserLeaveAllocationModel allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.office.id=:office " +
										" and leave_type.id=:type and year=:year and user.id=:user")
										.setParameter("year", (year+i))
										.setParameter("office", leave.getUser().getOffice().getId()).setParameter("user", leave.getUser().getId())
										.setParameter("type", leave.getLeave_type().getId()).uniqueResult();
								if(allMdl!=null){
									double taken=0;
									taken=CommonUtil.roundNumber(allMdl.getLeave_taken()-daysInyear);
									allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
									getSession().update(allMdl);
									flush();
								}
							}
						}
					}
				}
			}
			getSession().save(mdl);
			flush();
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
	public String getApproverComments(long leaveId) throws Exception {
		String comments="";
		List lst=new ArrayList();
		try {
			begin();
			lst = getSession().createQuery("select comments from LeaveHistoryModel where leave=:leaveId order by id desc").setParameter("leaveId", leaveId).list();
			commit();
			if(lst!=null&&lst.size()>0)
				comments=(String) lst.get(0);
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		
		return comments;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List getLeaveReport(java.sql.Date fromDate, java.sql.Date toDate, long empId) throws Exception {
		List lst=new ArrayList();
		String cond="";
		if(empId>0)
			cond+=" and employee.id="+empId;
		
		try {
			begin();
			lst = getSession().createQuery("select new com.intmark.report.bean.LeaveReportBean(cast(applied_date as string),cast(status as string),reason,employee.first_name,leave_type.name,no_of_days)" +
					" from LeaveModel where  applied_date between :frm and :to "+cond+" order by  employee.first_name")
					.setParameter("frm", fromDate).setParameter("to", toDate).list();
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
	
	
	
	public LeaveModel getLeaveModelFromDate(Date date,long userId) throws Exception {
		LeaveModel leave=null;
		try {
			begin();
			leave=(LeaveModel)getSession().createQuery("select a.leave from LeaveDateModel a where a.date=:date and a.userId=:userId " +
					" and a.attendance=false and a.leave is not null").setParameter("date", date).setParameter("userId", userId).uniqueResult();
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
		
		return leave;
	}
	
	
	
	public boolean isLeaveOnDate(Date date,long userId) throws Exception {
		boolean isLeave=false;
		try {
			begin();
			Object obj=getSession().createQuery("select count(a.id) from LeaveDateModel a where a.date=:date and a.userId=:userId ")
									.setParameter("date", date).setParameter("userId", userId).uniqueResult();
			if(obj!=null){
				if(((Long)obj)>0)
					isLeave=true;
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
		
		return isLeave;
	}
	
	
	
}
