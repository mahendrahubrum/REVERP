package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.RoomModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class RoomDao extends SHibernate  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2299656564012089L;
	List resultList = new ArrayList();

	public long save(RoomModel obj) throws Exception {

		try {
			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}

	public void update(RoomModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
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

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new RoomModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		}
		flush();
		close();
	}

	public List getAllRoomNames() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
					"select new com.inventory.model.RoomModel(id, room_number)"
							+ " from RoomModel").list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

	public List getAllRoomNamesFromBuilding(long building_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.model.RoomModel(id, room_number)"
									+ " from RoomModel where building.id=:bid and status=:sts")
					.setLong("sts", SConstants.statuses.ROOM_ACTIVE)
					.setLong("bid", building_id).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

	public List getAllActiveRoomNames() throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.RoomModel(id, room_number)"
									+ " from RoomModel where status=:val")
					.setParameter("val", SConstants.statuses.ROOM_ACTIVE)
					.list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}

	public RoomModel getRoom(long id) throws Exception {
		RoomModel mod = null;
		try {
			begin();
			mod = (RoomModel) getSession().get(RoomModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}

	public List getAllRoomNamesUnderOrganization(long orgId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.RoomModel(id, room_number)"
									+ " from RoomModel where building.office.organization.id=:org")
					.setParameter("org", orgId).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public List getAllRoomNamesUnderOffice(long orgId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.model.RoomModel(id, room_number)"
									+ " from RoomModel where building.office.id=:org")
					.setParameter("org", orgId).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
}
