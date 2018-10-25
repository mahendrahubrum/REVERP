package com.inventory.payroll.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.model.LeaveTypeModel;
import com.inventory.payroll.model.RoleLeaveMapModel;
import com.inventory.payroll.model.UserLeaveAllocationModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.UserModel;

/**
 * @author sangeeth
 * @date 11-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class RoleLeaveMapDao extends SHibernate implements Serializable {
	
	
	public RoleLeaveMapModel getRoleLeaveMapModel(long id) throws Exception {
		RoleLeaveMapModel mdl=null;
		try {
			begin();
			mdl=(RoleLeaveMapModel)getSession().get(RoleLeaveMapModel.class, id);
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
	public void save(List list, long role, long year, long office) throws Exception {
		try {
			begin();
			List userList=new ArrayList();
			List oldMapList=new ArrayList();
			List oldMapIdList=new ArrayList();
			List oldLeaveTypeList=new ArrayList();
			List presentYearList=new ArrayList();
			Iterator itr=null;
			
			userList=getSession().createQuery("select a from UserModel a where a.user_role.id=:role and a.office.id=:office")
					.setParameter("role", role).setParameter("office", office).list();
			
			oldLeaveTypeList=getSession().createQuery("select distinct leave_type.id from UserLeaveAllocationModel where year=:year and user.office.id=:office")
					.setParameter("year", year).setParameter("office", office).list();
			
			// For Roll Backing Carry Forward and Present Leave Balance If Any
			
			oldMapList = getSession().createQuery("from RoleLeaveMapModel where role.id=:role and year=:year")
									.setParameter("year", year).setParameter("role", role).list();
			
			if(oldMapList.size()>0){
				itr=oldMapList.iterator();
				
				while (itr.hasNext()) {
					
					RoleLeaveMapModel map = (RoleLeaveMapModel) itr.next();
					
					oldMapIdList.add(map.getId());
					
					presentYearList = getSession().createQuery("from UserLeaveAllocationModel where user.user_role.id=:role " +
							" and leave_type.id=:type and year=:year and user.office.id=:office")
							.setParameter("year", year).setParameter("role", role).setParameter("office", office)
							.setParameter("type", map.getLeave_type().getId()).list();
					
					if(presentYearList.size()>0){
						
						Iterator it=presentYearList.iterator();
						while (it.hasNext()) {
							UserLeaveAllocationModel allMdl = (UserLeaveAllocationModel) it.next();
							double balance=0;
							if(map.getLeave_type().isCarry_forward()){
								Object obj=getSession().createQuery("select coalesce((leave_available-leave_taken),0) from UserLeaveAllocationModel where " +
										" user.user_role.id=:role and leave_type.id=:type and year=:year and user.office.id=:office and user.id=:user")
										.setParameter("year", (year-(long)1)).setParameter("role", role).setParameter("office", office)
										.setParameter("type", map.getLeave_type().getId()).setParameter("user", allMdl.getUser().getId()).uniqueResult();
								if(obj!=null)
									balance=(Double)obj;
								if(balance<0)
									balance=0;
							}
							getSession().createQuery("update UserLeaveAllocationModel set leave_available=leave_available-:value, carry_forward=0 where id="+allMdl.getId())
										.setParameter("value", (map.getValue()+balance)).executeUpdate();
						}
					}
					else
						continue;
				}
			}
			
			// Saving or Updating New Data
			itr = list.iterator();
			while (itr.hasNext()) {
				RoleLeaveMapModel mdl = (RoleLeaveMapModel) itr.next();
				
				getSession().clear();
				if(mdl.getId()!=0)
					getSession().update(mdl);
				else
					getSession().save(mdl);
				flush();
				
				if(oldMapIdList.contains(mdl.getId()))
					oldMapIdList.remove(mdl.getId());
				
				if(oldLeaveTypeList.contains(mdl.getLeave_type().getId()))
					oldLeaveTypeList.remove(mdl.getLeave_type().getId());
				
				Iterator it=userList.iterator();
				while (it.hasNext()) {
					UserModel user = (UserModel) it.next();
					UserLeaveAllocationModel allMdl=null;
					double balance=0,taken=0;
					allMdl=(UserLeaveAllocationModel)getSession().createQuery("from UserLeaveAllocationModel where user.user_role.id=:role and leave_type.id=:type and " +
							" year=:year and user.office.id=:office and user.id=:user")
							.setParameter("year", year).setParameter("role", role).setParameter("office", office)
							.setParameter("user", user.getId()).setParameter("type", mdl.getLeave_type().getId()).uniqueResult();
					
					if(allMdl==null){
						allMdl=new UserLeaveAllocationModel();
					}
						
					else{
						taken=allMdl.getLeave_taken();
//						available=allMdl.getLeave_available()-allMdl.getCarry_forward();
					}
					LeaveTypeModel leaveType=(LeaveTypeModel)getSession().get(LeaveTypeModel.class, mdl.getLeave_type().getId());
					if(leaveType.isCarry_forward()){
						Object obj=getSession().createQuery("select coalesce((leave_available-leave_taken),0) from UserLeaveAllocationModel where " +
								" user.user_role.id=:role and leave_type.id=:type and year=:year and user.office.id=:office and user.id=:user")
								.setParameter("year", (year-(long)1)).setParameter("role", role).setParameter("office", office)
								.setParameter("user", user.getId()).setParameter("type", leaveType.getId()).uniqueResult();
						if(obj!=null)
							balance=(Double)obj;
						if(balance<0)
							balance=0;
					}
					allMdl.setUser(new UserModel(user.getId()));
					allMdl.setLeave_type(new LeaveTypeModel(mdl.getLeave_type().getId()));
					allMdl.setLeave_available(CommonUtil.roundNumber(balance+mdl.getValue()));
					allMdl.setCarry_forward(CommonUtil.roundNumber(balance));
					allMdl.setLeave_taken(CommonUtil.roundNumber(taken));
					allMdl.setYear(year);
					
					if(allMdl.getId()!=0)
						getSession().update(allMdl);
					else
						getSession().save(allMdl);
					
					flush();
				}
			}
			
			if(oldLeaveTypeList.size()>0){
				List lst=new ArrayList();
				lst= getSession().createQuery("from UserLeaveAllocationModel where year=:year and user.office.id=:office and leave_type.id in (:list)")
						.setParameter("year", year).setParameter("office", office).setParameterList("list", oldLeaveTypeList).list();
				if(lst.size()>0){
					Iterator itr1=lst.iterator();
					while (itr1.hasNext()) {
						UserLeaveAllocationModel obj = (UserLeaveAllocationModel) itr1.next();
						getSession().delete(obj);
					}
				}
				flush();
			}
			
			if(oldMapIdList.size()>0){
				getSession().createQuery("delete from RoleLeaveMapModel where id in (:list)")
							.setParameterList("list", oldMapIdList).executeUpdate();
				flush();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getRoleMap(long role, long year, long office) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery("from RoleLeaveMapModel where role.id=:role and year=:year and officeId=:office")
								.setParameter("role", role).setParameter("year", year).setParameter("office", office).list();
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
	

	@SuppressWarnings("rawtypes")
	public void delete(long role, long year, long office) throws Exception {
		try {
			begin();
			List list=new ArrayList();
			list= getSession().createQuery("from UserLeaveAllocationModel where user.user_role.id=:role and year=:year and user.office.id=:office")
								.setParameter("year", year).setParameter("role", role)
								.setParameter("office", office).list();
			if(list.size()>0){
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					UserLeaveAllocationModel obj = (UserLeaveAllocationModel) itr.next();
					getSession().delete(obj);
				}
			}
			flush();
			
			getSession().createQuery("delete from RoleLeaveMapModel where role.id=:role and year=:year and officeId=:office")
						.setParameter("year", year).setParameter("office", office).setParameter("role", role).executeUpdate();
			
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

}
