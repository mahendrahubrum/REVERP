package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.model.S_RoleOptionMappingModel;
import com.webspark.uac.model.S_UserRoleModel;

/**
 * @Author Jinshad P.T.
 */

public class RoleDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5911325597334030271L;
	List resultList = null;

	public long saveRole(S_UserRoleModel obj) throws Exception {

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

	public void Update(S_UserRoleModel sts) throws Exception {

		try {

			begin();
			getSession().update(sts);
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
			getSession().delete(new S_UserRoleModel(id));
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

	public List getAllRoles() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(S_UserRoleModel.class)
					.add(Restrictions.eq("active", 'Y')).list();
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

	public List getAllRolesFromRole(boolean isSuperAdmin) throws Exception {
		try {
			begin();
			if (isSuperAdmin) {
				resultList = getSession().createCriteria(S_UserRoleModel.class)
						.list();
			} else {
				resultList = getSession().createCriteria(S_UserRoleModel.class)
						.add(Restrictions.eq("active", 'Y')).list();
			}

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

	public S_UserRoleModel getRole(long stsId) throws Exception {
		S_UserRoleModel mod = null;
		try {
			begin();
			mod = (S_UserRoleModel) getSession().get(S_UserRoleModel.class,
					stsId);
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

	public List<Long> selectOptionsToRole(long role_id) throws Exception {

		List<Long> objList = null;
		try {
			begin();

			objList = getSession()
					.createQuery(
							"select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
					.setParameter("RolId", role_id).list();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return objList;
		}
	}

	public void updateOptionsToRole(long role_id,
			List<S_RoleOptionMappingModel> rolOptList, boolean reset)
			throws Exception {
		try {
			begin();

			getSession()
					.createQuery(
							"delete from S_RoleOptionMappingModel where  role_id.id=:RolId")
					.setParameter("RolId", role_id).executeUpdate();

			for (S_RoleOptionMappingModel obj : rolOptList) {
				getSession().save(obj);
			}

			S_LoginModel loginModel = null;
			List userList = null;
			if (reset) {
				userList = getSession()
						.createQuery(
								"from S_LoginModel"
										+ " where userType.id=:role_id")
						.setParameter("role_id", role_id).list();

				getSession()
						.createQuery(
								"delete from S_LoginOptionMappingModel where login_id in (:list)")
						.setParameterList("list", userList).executeUpdate();

				List<Long> objList = getSession()
						.createQuery(
								"select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
						.setParameter("RolId", role_id).list();

				S_LoginOptionMappingModel lomm;
				for (int i = 0; i < userList.size(); i++) {
					loginModel=(S_LoginModel) userList.get(i);
					for (Long opt_id : objList) {
						lomm = new S_LoginOptionMappingModel();

						lomm.setLogin_id(new S_LoginModel(loginModel.getId()));
						lomm.setOption_id(new S_OptionModel(opt_id));
						lomm.setActive('Y');

						getSession().save(lomm);
					}
				}
			}

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

}
