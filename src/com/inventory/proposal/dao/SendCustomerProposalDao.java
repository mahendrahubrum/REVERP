package com.inventory.proposal.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.proposal.model.ProposalsSentToCustomersModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class SendCustomerProposalDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3877907915523632470L;
	private List resultList = new ArrayList();

	public List getAllCustomersSentProposals(long ofc_id) throws Exception {
		try {
			begin();
			
			resultList = getSession()
					.createQuery("select new com.inventory.proposal.model.ProposalsSentToCustomersModel(id, concat(number, ' : ',head))"
									+ " from ProposalsSentToCustomersModel where office.id=:ofc")
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
	
	public List getAllCustomersSentProposalsUnderReception(long rec_id) throws Exception {
		try {
			begin();
			
			resultList = getSession()
					.createQuery("from ProposalsSentToCustomersModel where supplier_proposal.id=:rec")
									.setParameter("rec", rec_id).list();
			
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
	
	public long save(ProposalsSentToCustomersModel obj) throws Exception {
		try {
			begin();
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
	
	public void update(ProposalsSentToCustomersModel objModel)
			throws Exception {

		try {

			begin();
			
			getSession().update(objModel);

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

			getSession().delete(new ProposalsSentToCustomersModel(id));

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

	public ProposalsSentToCustomersModel getCustomerProposalSent(long id) throws Exception {
		ProposalsSentToCustomersModel cust = null;
		try {
			begin();
			cust = (ProposalsSentToCustomersModel) getSession().get(ProposalsSentToCustomersModel.class, id);
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
	
}
