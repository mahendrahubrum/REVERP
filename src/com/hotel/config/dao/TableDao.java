package com.hotel.config.dao;

import java.util.List;

import com.hotel.config.model.TableModel;
import com.hotel.service.model.CustomerBookingModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class TableDao extends SHibernate{

	public List getAllTables(long officeID) throws Exception {
		List resultList;
			try {

				begin();
				resultList = getSession()
						.createQuery(
								"from TableModel  where office.id=:ofc order by id")
						.setParameter("ofc", officeID).list();
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
			return resultList;
		}

	public void save(TableModel objModel) throws Exception {
		try {

			begin();
			getSession().save(objModel);
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

	public TableModel getTable(Long TableId) throws Exception {
		TableModel gm;
		try {

			begin();
			gm=(TableModel) getSession().get(TableModel.class, TableId);
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
		return gm;
	}

	public void delete(Long TableId) throws Exception {
		try {
			TableModel gm;
			begin();
			gm=(TableModel) getSession().get(TableModel.class, TableId);
			getSession().delete(gm);
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

	public void update(TableModel objModel) throws Exception {
		try {
			begin();
			getSession().update(objModel);
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

	public void saveCustomerInfo(CustomerBookingModel model) throws Exception {
		try {
			begin();
			getSession().save(model);
			
			flush();
			
			getSession().createQuery("update TableModel set status=:sts where id=:id")
				.setParameter("id", model.getTableNo().getId()).setParameter("sts",SConstants.tableStatus.BUSY).executeUpdate();
			
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

	public void cleanTable(long tableId) throws Exception {
		try {
			begin();
			getSession().createQuery("update TableModel set status=:sts where id=:id")
				.setParameter("id", tableId).setParameter("sts",SConstants.tableStatus.AVAILABLE).executeUpdate();
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
