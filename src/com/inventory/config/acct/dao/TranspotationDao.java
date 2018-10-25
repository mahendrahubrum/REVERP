package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.model.TranspotationModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class TranspotationDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -564432961389533160L;
	private List resultList = new ArrayList();

	public List getAllTranspotationNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.TranspotationModel(id, concat(name, ' ( ' , code,' ) '))"
									+ " from TranspotationModel where ledger.office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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
	
	
	public List getAllTranspotations(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from TranspotationModel where ledger.office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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
	
	

	public List getAllActiveTranspotationNamesWithLedgerID(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.TranspotationModel(ledger.id, concat(name, ' [ ' , code,' ] '))"
									+ " from TranspotationModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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
	
	
	
	public List getAllActiveTranspotationLedgerIDs(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select ledger.id from TranspotationModel where ledger.office.id=:ofc and ledger.status=:val")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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
	
	

	public List getAllActiveTranspotationNamesWithOrgID(long org_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.TranspotationModel(ledger.id, concat(name, ' [ ' , code,' ] '))"
									+ " from TranspotationModel where ledger.office.organization.id=:org and   ledger.status=:val")
									.setLong("org", org_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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

	public long save(TranspotationModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			throw e;
		} finally {
			flush();
			close();
			return obj.getId();
		}
	}
	
	public TranspotationModel saveAndGet(TranspotationModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}

	public void update(TranspotationModel customer)
			throws Exception {

		try {

			begin();

			getSession().update(customer.getAddress());

			getSession().update(customer.getLedger());

			flush();

			getSession().update(customer);

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

			TranspotationModel cust = (TranspotationModel) getSession().get(
					TranspotationModel.class, id);

			getSession().delete(cust.getAddress());

			getSession().delete(cust.getLedger());

			getSession().delete(cust);

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

	public TranspotationModel getTranspotation(long id) throws Exception {
		TranspotationModel cust = null;
		try {
			begin();
			cust = (TranspotationModel) getSession().get(TranspotationModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return cust;
		}
	}
	
	
	public TranspotationModel getTranspotationFromLedgerID(long led_id) throws Exception {
		TranspotationModel cust = null;
		try {
			begin();
			cust = (TranspotationModel) getSession().createQuery("from TranspotationModel where ledger.id=:led")
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
			return cust;
		}
	}
	
	
	public List getAllActiveTranspotation(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from TranspotationModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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
	
	
	public boolean isCodeExists(long officeId, String code, long id) throws Exception {
		boolean flag = false;
		try {
			begin();
			
			if(getSession().createQuery("from TranspotationModel where code=:code and ledger.office.id=:ofc and id!=:id")
					.setParameter("code", code).setParameter("ofc", officeId).setLong("id", id).list().size()>0)
			flag=true;
			
			commit();
		} catch (Exception e) {
			flag=false;
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
	

	// Added by Anil

	public List getAllTranspotationNamesList(long ofc_id) throws Exception {
		try {
			begin();
			String condition = "";
			if (ofc_id != 0) {
				condition = " and ledger.office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(" from TranspotationModel where ledger.status=:val"+condition)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
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

	public boolean isAlreadyExists(long officeId, String name, String code)
			throws Exception {
		boolean flag = false;
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"from TranspotationModel where name=:name and code=:code and ledger.office.id=:ofc")
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
			TranspotationModel model = null;

			for (int i = 0; i < modelVector.size(); i++) {

				model = (TranspotationModel) modelVector.get(i);
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
	
	
	
	public List getContractorsCreditDetails(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("from TranspotationModel where ledger.office.id=:ofc and "+
									"(ledger.current_balance+credit_limit)<0")
					.setParameter("ofc", ofc_id).list();
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
	
	
	
	
	

}
