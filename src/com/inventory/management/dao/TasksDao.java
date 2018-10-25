package com.inventory.management.dao;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.management.model.TaskComponentDetailsModel;
import com.inventory.management.model.TasksModel;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 * 
 */
public class TasksDao extends SHibernate {

	public TasksModel getTasksModel(long id) throws Exception {

		TasksModel model = null;
		try {
			begin();

			model = (TasksModel) getSession().get(TasksModel.class,
					id);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return model;
	}

	public void update(TasksModel objModel)
			throws Exception {
		try {
			begin();
			
			List old_notDeletedUsrLst = getSession().createQuery(
					"select b.id from TasksModel a join a.assignedList b "
							+ "where a.id=" + objModel.getId()).list();
			List old_notDeletedComponentLst = getSession().createQuery(
					"select b.id from TasksModel a join a.componentDetailsList b "
							+ "where a.id=" + objModel.getId()).list();
			
			getSession().update(objModel);
			
			flush();
			
			getSession().createQuery("delete from TasksAssignedUsersModel where id in (:ids)")
							.setParameterList("ids", old_notDeletedUsrLst).executeUpdate();
			getSession().createQuery("delete from TaskComponentDetailsModel where id in (:ids)")
							.setParameterList("ids", old_notDeletedComponentLst).executeUpdate();
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		
	}

	public long save(TasksModel objModel) throws Exception {
		long id = 0;
		try {
			begin();
			id = (Long) getSession().save(objModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return id;
	}

	public void delete(long id) throws Exception {
		try {
			begin();
			getSession().delete(new TasksModel(id));

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
	}

	public List getAllTasksListCreatedByUser(long userID) throws Exception {

		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(id, title)from TasksModel where created_by.id=:usrId")
					.setParameter("usrId", userID).list();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	public List getAllTasksListAssignedToUser(long userID) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(a.id, a.title)from TasksModel a join a.assignedList b where b.user.id=:usrId group by a.id")
					.setParameter("usrId", userID).list();

			commit();	
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllNewTasksOfUser(long userID) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(a.id, a.title)from TasksModel a join a.assignedList b where a.status=1 and b.user.id=:usrId group by a.id")
					.setParameter("usrId", userID).list();

			commit();	
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllTasksListAssignedToUser(Date frmDt, Date toDt,long crtdBy, long asgndTo,
			long compnt_id, long sts) throws Exception {
		List list = null;
		try {
			
			String criteria="";
			
			if(crtdBy!=0)
				criteria+=" and a.created_by.id="+crtdBy;
			if(asgndTo!=0)
				criteria+=" and b.user.id="+asgndTo;
			if(compnt_id!=0)
				criteria+=" and c.task_component.id="+compnt_id;
			if(sts!=0)
				criteria+=" and a.status="+sts;
			
			begin();
			list = getSession().createQuery("select a from TasksModel a join a.assignedList b join a.componentDetailsList c where a.start_time between :stdt and :enddt "+criteria+" group by a.id order by a.date")
					.setParameter("stdt", frmDt).setParameter("enddt", toDt).list();

			commit();	
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public String getComponentDetails(long id) throws Exception {
		String componentDetails="";
		try {
			
			String criteria="";
			begin();
			Iterator it = getSession().createQuery("select c from TasksModel a join a.componentDetailsList c where a.id="+id)
					.list().iterator();
			
			commit();
			
			while (it.hasNext()) {
				TaskComponentDetailsModel obj=(TaskComponentDetailsModel) it.next();
				if(obj.getStatus()==1) 
					componentDetails+=""+obj.getTask_component().getName()+" - Status : Created - Description :"+obj.getDescription()+" <br> ";
				else
					componentDetails+=""+obj.getTask_component().getName()+" - Status : Completed - Description :"+obj.getDescription()+" <br> ";
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return componentDetails;
	}
	
	
	
	
	public List getAssigedComponentsForTask(long taskID) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select b from TasksModel a join a.componentDetailsList b where a.id=:taskID")
					.setParameter("taskID", taskID).list();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllTasksListAssignedToUserByStatus(long userID, long status) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(a.id, a.title)from TasksModel a join a.assignedList b where b.user.id=:usrId and status=:sts group by a.id")
					.setParameter("usrId", userID).setParameter("sts", status).list();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllTasksListForVerification(long office_id) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(id, title)from TasksModel where assigned_to.office.id=:ofc and status=3")
					.setParameter("ofc", office_id).list();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	
	public List getAllTasksReport(long userID) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery(
							"select new com.inventory.management.model.TasksModel(a.id, a.title)from TasksModel a join a.assignedList b where b.user.id=:usrId group by a.id")
					.setParameter("usrId", userID).list();

			commit();	
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return list;
	}
	
	

}
