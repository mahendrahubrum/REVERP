package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.model.ClearingAgentModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;

/**
 * @author anil
 * @date 03-Sep-2015
 * @Project REVERP
 */
public class ClearingAgentDao extends SHibernate implements Serializable {

	private static final long serialVersionUID = 2151148223678937042L;
	
	private List resultList = new ArrayList();

	public List getAllClearingAgentsNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new  com.inventory.config.acct.model.ClearingAgentModel(id, concat(name, ' ( ' , agent_code,' ) '))"
									+ " from ClearingAgentModel where ledger.office.id=:ofc order by name")
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

	public List getAllActiveClearingAgentNamesWithLedgerID(long ofc_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.ClearingAgentModel(ledger.id, concat(name, ' [ ' , agent_code,' ] '))"
									+ " from ClearingAgentModel where ledger.office.id=:ofc order by name")
					.setParameter("ofc", ofc_id)
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
	public long save(ClearingAgentModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			getSession().save(obj);
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
		return obj.getId();
	}

	@SuppressWarnings("unchecked")
	public void update(ClearingAgentModel ClearingAgent) throws Exception {
		try {
			begin();
			getSession().update(ClearingAgent.getAddress());
			getSession().update(ClearingAgent.getLedger());
			getSession().update(ClearingAgent);

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

			ClearingAgentModel cust = (ClearingAgentModel) getSession().get(ClearingAgentModel.class, id);
			getSession().delete(cust.getAddress());
			getSession().delete(cust.getLedger());
			getSession().delete(cust);
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

	public ClearingAgentModel getClearingAgent(long id) throws Exception {
		ClearingAgentModel cust = null;
		try {
			begin();
			cust = (ClearingAgentModel) getSession().get(ClearingAgentModel.class, id);
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

	public ClearingAgentModel getClearingAgentFromLedger(long led_id) throws Exception {
		ClearingAgentModel cust = null;
		try {
			begin();
			cust = (ClearingAgentModel) getSession()
					.createQuery("from ClearingAgentModel where ledger.id=:led")
					.setLong("led", led_id).uniqueResult();
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


}
