package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.webspark.model.S_LoginModel;

/**
 * @Author Jinshad P.T.
 */

public class LoginCreationDao extends SHibernate implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1118859097378332049L;
	private List resultList = new ArrayList();

	public List getUsers() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(S_LoginModel.class).list();
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

	public S_LoginModel save(S_LoginModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj);
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
			return obj;
		}
	}

	public boolean isAlreadyExist(String login_name) throws Exception {
		int ct = 0;
		try {
			begin();
			ct = getSession().createCriteria(S_LoginModel.class)
					.add(Restrictions.eq("login_name", login_name)).list()
					.size();
			commit();

			if (ct > 0)
				return true;
			else
				return false;

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();

		}
	}

	public S_LoginModel getUser(long loginId) throws Exception {
		S_LoginModel user = null;
		try {
			begin();
			user = (S_LoginModel) getSession().get(S_LoginModel.class, loginId);
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
			return user;
		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();

			getSession()
					.createQuery(
							"delete from S_LoginOptionMappingModel where login_id.id=:LogId")
					.setParameter("LogId", id).executeUpdate();
			flush();
			getSession().delete(new S_LoginModel(id));
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();

	}

	public long update(S_LoginModel obj) throws Exception {

		try {
			begin();
			getSession().update(obj);
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
			return obj.getId();
		}
	}

	public List getActiveUsers(long organizationId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from S_LoginModel where office.organization.id=:org and userType.active='Y'")
					.setParameter("org", organizationId).list();
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
