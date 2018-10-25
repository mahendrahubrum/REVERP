package com.inventory.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.model.RackModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class RackDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8944862441025183605L;
	List resultList = new ArrayList();

	public long save(RackModel obj) throws Exception {

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
	
	
	public void update(RackModel obj) throws Exception {

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
			getSession().delete(new RackModel(id));
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
	
	
	public List getAllRacks() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.RackModel(id, rack_number)" +
					" from RackModel").list();
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
	
	
	public List getAllActiveRacks() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.RackModel(id, rack_number)" +
					" from RackModel where status=:val")
					.setParameter("val", SConstants.statuses.RACK_ACTIVE).list();
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
	
	
	public List getAllActiveRacksUnderRoom(long room_id) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.RackModel(id, rack_number)" +
					" from RackModel where status=:val and room.id=:room")
					.setLong("room", room_id).setParameter("val", SConstants.statuses.RACK_ACTIVE).list();
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
	
	
	public RackModel getRack(long id) throws Exception {
		RackModel mod=null;
		try {
			begin();
			mod=(RackModel) getSession().get(RackModel.class, id);
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
	
	
	public List getAllRacksUnderOrganization(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.RackModel(id, rack_number)" +
					" from RackModel where room.building.office.organization.id=:org")
					.setParameter("org", organizationId).list();
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
	
	
	public List getAllRacksUnderOffice(long ofcId) throws Exception {

		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.model.RackModel(id, rack_number)" +
					" from RackModel where room.building.office.id=:ofc")
					.setParameter("ofc", ofcId).list();
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
	
	
	public String getRackName(long rack_id) throws Exception {
		String name="";
		try {
			begin();
			Object obj = getSession().createQuery("select rack_number from RackModel where id=:id")
					.setLong("id", rack_id).uniqueResult();
			commit();
			if(obj!=null)
				name=(String) obj;
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return name;
		}
	}
	

	public List getItemsInRack(long rackId) throws Exception {
		List list=null;
		try {
			begin();
			list = getSession().createQuery("from StockRackMappingModel where rack.id=:id")
					.setLong("id", rackId).list();
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


	public double getTotalQuantityFromRack(long rackId) throws Exception {
		double qty=0;
		try {
			begin();
		Object	obj = getSession().createQuery("select sum(quantity) from StockRackMappingModel where rack.id=:id")
					.setLong("id", rackId).uniqueResult();
			commit();
			if(obj!=null)
				qty=(Double) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return qty;
	}
	
}
