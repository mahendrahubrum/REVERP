package com.inventory.proposal.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.proposal.model.SupplierProposalReceiptionModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class SupplierProposalReceiptionDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3877907915523632470L;
	private List resultList = new ArrayList();

	public List getAllSupplierProposals(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.SupplierProposalReceiptionModel(id, concat(number, ' : ',head))"
									+ " from SupplierProposalReceiptionModel where office.id=:ofc")
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
	
	public List getAllSupplierProposalsFromRequest(long rqst_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.SupplierProposalReceiptionModel(id, concat(number, ' : ',head))"
									+ " from SupplierProposalReceiptionModel where request.id=:rqt")
					.setParameter("rqt", rqst_id).list();
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
	
	public List getAllSupplierProposalsDetailsFromRequest(long rqst_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from SupplierProposalReceiptionModel where request.id=:rqt")
					.setParameter("rqt", rqst_id).list();
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
	
	
	public long save(SupplierProposalReceiptionModel obj) throws Exception {
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
	
	public void update(SupplierProposalReceiptionModel objModel)
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

			getSession().delete(new SupplierProposalReceiptionModel(id));

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

	public SupplierProposalReceiptionModel getSupplierProposal(long id) throws Exception {
		SupplierProposalReceiptionModel cust = null;
		try {
			begin();
			cust = (SupplierProposalReceiptionModel) getSession().get(SupplierProposalReceiptionModel.class, id);
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
