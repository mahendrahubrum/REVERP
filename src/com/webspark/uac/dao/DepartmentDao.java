package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.model.S_RoleOptionMappingModel;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.S_DepartmentOptionMappingModel;

public class DepartmentDao extends SHibernate implements Serializable{

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -5954159944339495111L;
	List resultList = new ArrayList();
	 
	@SuppressWarnings("finally")
	public long save(DepartmentModel obj) throws Exception {

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
	
	
	public void Update(DepartmentModel obj) throws Exception {

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
			getSession().delete(new DepartmentModel(id));
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
	
	
	public List getDepartments(long org_id) throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.DepartmentModel(id,name) from DepartmentModel where organization_id="+org_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getDepartmentsUnderOrg(long org_id) throws Exception {
		try {

			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.DepartmentModel(id,name) from DepartmentModel where organization_id=:org")
							.setLong("org", org_id).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public DepartmentModel getDepartment(long Id) throws Exception {
		DepartmentModel lm=null;
		try {
			

			begin();
			lm = (DepartmentModel)getSession().get(DepartmentModel.class,Id);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return lm;
		}
	}
	
	public void updateOptionsToDepartment(long depId,
			List<S_DepartmentOptionMappingModel> rolOptList, boolean reset)
			throws Exception {
		try {
			begin();

			getSession()
					.createQuery(
							"delete from S_DepartmentOptionMappingModel where  departmentId.id=:RolId")
					.setParameter("RolId", depId).executeUpdate();

			for (S_DepartmentOptionMappingModel obj : rolOptList) {
				getSession().save(obj);
			}

			S_LoginModel loginModel = null;
			List userList = null;
			if (reset) {
				userList = getSession()
						.createQuery(
								"from S_LoginModel"
										+ " where userType.id=:role_id")
						.setParameter("role_id", depId).list();

				getSession()
						.createQuery(
								"delete from S_LoginOptionMappingModel where login_id in (:list)")
						.setParameterList("list", userList).executeUpdate();

				List<Long> objList = getSession()
						.createQuery(
								"select option_id.option_id from S_DepartmentOptionMappingModel where departmentId.id=:RolId")
						.setParameter("RolId", depId).list();

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
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
	}

	public List<Long> selectOptionsToDepartment(long depId) throws Exception {

		List<Long> objList = null;
		try {
			begin();

			objList = getSession()
					.createQuery(
							"select option_id.option_id from S_DepartmentOptionMappingModel where departmentId.id=:RolId")
					.setParameter("RolId", depId).list();

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return objList;
		}
	}
	
	public List getAllOptionsUnderOffice(long officeId) throws Exception {
		List<Long> objList=null;
		try {
			begin();
			
			objList=getSession().createQuery("select a.option_id  " +
					"from OfficeOptionMappingModel a where a.officeId.id=:LogId order by a.option_id.option_name")
					.setParameter("LogId", officeId).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return objList;
	}


	public String getDepartmentName(long departmentId) throws Exception {
		String name="";
		try {

			begin();
			name = (String) getSession().createQuery("select name from DepartmentModel where id=:id").setLong("id", departmentId).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return name;
	}
}
