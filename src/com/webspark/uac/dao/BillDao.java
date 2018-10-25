package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.model.BillModel;

public class BillDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2082448030287511511L;

	public List getAllBills() throws Exception {
		List list = null;
		try {
			begin();
			list = getSession().createQuery("from BillNameModel order by bill_name").list();
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

	public void save(BillModel model) throws Exception {
		try {
			begin();

			getSession()
					.createQuery(
							"delete from BillModel where office.id=:off and type=:typ")
					.setParameter("off", model.getOffice().getId())
					.setParameter("typ", model.getType()).executeUpdate();

			getSession().save(model);
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

	public long loadBill(Long office, Integer type) throws Exception {
		long id = 0;
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"from BillModel where office.id=:ofc and type=:typ ")
					.setParameter("ofc", office).setParameter("typ", type)
					.uniqueResult();
			commit();
			if(obj!=null)
				id = ((BillModel) obj).getBill_name().getId();
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

}
