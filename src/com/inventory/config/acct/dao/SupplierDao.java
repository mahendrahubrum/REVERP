package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.model.SupplierModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;

/**
 * @Author Jinshad P.T.
 */

public class SupplierDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5384436636251835676L;
	private List resultList = new ArrayList();

	public List getAllSuppliersNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.SupplierModel(id, concat(name, ' [ ' , supplier_code,' ] '))"
									+ " from SupplierModel where ledger.office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list();
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

	public List getAllActiveSupplierNamesWithLedgerID(long ofc_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.SupplierModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
									+ " from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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

	public List getAllActiveSupplierNamesWithLedgerIDFromOrg(long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.SupplierModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
									+ " from SupplierModel where ledger.office.organization.id=:org and ledger.status=:val order by name")
					.setLong("org", org_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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

	@SuppressWarnings("unchecked")
	public long save(SupplierModel obj, S_LoginModel login) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			if (login != null) {
				getSession().save(login);
				List<Long> objList = getSession().createQuery( "select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
						.setParameter("RolId", login.getUserType().getId()).list();
				S_LoginOptionMappingModel lomm;
				for (Long opt_id : objList) {
					lomm = new S_LoginOptionMappingModel();

					lomm.setLogin_id(new S_LoginModel(login.getId()));
					lomm.setOption_id(new S_OptionModel(opt_id));
					lomm.setActive('Y');
					getSession().save(lomm);
				}
				flush();
			}
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}

	public SupplierModel saveSupplier(SupplierModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public void update(SupplierModel supplier, S_LoginModel loginModel) throws Exception {

		try {

			begin();
			getSession().update(supplier.getAddress());
			getSession().update(supplier.getLedger());
			flush();
			if (loginModel != null) {
				if(loginModel.getId()!=0){
					getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:log")
								.setParameter("log", loginModel.getId()).executeUpdate();
					getSession().update(loginModel);
				}
				else
					getSession().save(loginModel);
				
				flush();
				List<Long> objList = getSession().createQuery( "select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
												.setParameter("RolId", loginModel.getUserType().getId()).list();
				S_LoginOptionMappingModel lomm;
				for (Long opt_id : objList) {
					lomm = new S_LoginOptionMappingModel();
					lomm.setLogin_id(new S_LoginModel(loginModel.getId()));
					lomm.setOption_id(new S_OptionModel(opt_id));
					lomm.setActive('Y');
					getSession().save(lomm);
				}
				flush();
			}
			getSession().update(supplier);
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

	public void delete(long id) throws Exception {

		try {
			begin();

			SupplierModel supp = (SupplierModel) getSession().get(SupplierModel.class, id);
			S_LoginModel login=null;
			if(supp.getLogin()!=null){
				login = supp.getLogin();
				getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:log")
							.setParameter("log", supp.getLogin().getId()).executeUpdate();
			}
			getSession().delete(supp.getAddress());
			getSession().delete(supp.getLedger());
			getSession().delete(supp);
			flush();
			if(login!=null){
				getSession().delete(login);
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
	}

	public SupplierModel getSupplier(long id) throws Exception {
		SupplierModel cust = null;
		try {
			begin();
			cust = (SupplierModel) getSession().get(SupplierModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return cust;
	}

	public List getAllActiveSuppliers(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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

	public SupplierModel getSupplierFromLedger(long led_id) throws Exception {
		SupplierModel cust = null;
		try {
			begin();
			cust = (SupplierModel) getSession()
					.createQuery("from SupplierModel where ledger.id=:led")
					.setLong("led", led_id).uniqueResult();
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
		return cust;
	}

	// Added by Anil

	public List getAllSupplierNamesList(long ofc_id) throws Exception {
		try {
			begin();

			String condition = "";
			if (ofc_id != 0) {
				condition = " and ledger.office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(
							" from SupplierModel where ledger.status=:val"
									+ condition)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.list();
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

	public boolean isCodeExists(long officeId, String code, long id)
			throws Exception {
		boolean flag = false;
		try {
			begin();

			if (getSession().createQuery("from SupplierModel where supplier_code=:code and ledger.office.id=:ofc and id!=:id")
					.setParameter("code", code).setLong("id", id)
					.setParameter("ofc", officeId).list().size() > 0)
				flag = true;

			commit();
		} catch (Exception e) {
			flag = false;
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return flag;
	}

	public boolean isAlreadyExists(long officeId, String name, String code)
			throws Exception {
		boolean flag = false;
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"from SupplierModel where name=:name and supplier_code=:code and ledger.office.id=:ofc")
					.setParameter("name", name).setParameter("code", code)
					.setParameter("ofc", officeId).list();
			commit();

			if (list.size() > 0)
				flag = true;

		} catch (Exception e) {
			flag = false;
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}

		return flag;
	}

	public void save(Vector modelVector) throws Exception {
		try {

			begin();
			SupplierModel model = null;

			for (int i = 0; i < modelVector.size(); i++) {

				model = (SupplierModel) modelVector.get(i);
				getSession().save(model.getAddress());
				getSession().save(model.getLedger());
				getSession().save(model);

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

	public int getSupplierCreditPeriodFromLedger(long led_id) throws Exception {
		int max_cr_limit = 0;
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select credit_period from SupplierModel where ledger.id=:led")
					.setLong("led", led_id).uniqueResult();
			commit();

			if (obj != null)
				max_cr_limit = (Integer) obj;

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return max_cr_limit;
	}

	public SupplierModel getSupplierFromLogin(long loginId) throws Exception {
			
		SupplierModel mdl=null;
			try {
				begin();
				mdl = (SupplierModel) getSession()
						.createQuery(
								"from SupplierModel where login_id=:log")
						.setParameter("log", loginId).uniqueResult();
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


}
