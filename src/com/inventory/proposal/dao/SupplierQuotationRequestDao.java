package com.inventory.proposal.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.proposal.model.SupplierQuotationRequestModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class SupplierQuotationRequestDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3877907915523632470L;
	private List resultList = new ArrayList();

	public List getAllSupplierQutationRequests(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.SupplierQuotationRequestModel(id, head)"
									+ " from SupplierQuotationRequestModel where office.id=:ofc")
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
	
	
	public List getAllSupplierQutationRequestsWithEnquiryID(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.SupplierQuotationRequestModel(a.id, concat(a.head, ' ( Enq : ',a.enquiry.number,' )'))"
									+ " from SupplierQuotationRequestModel a where a.office.id=:ofc")
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
	
	
	public List getAllSupplierQutationFromCustomerEnquiry(long cust_enq_id) throws Exception {
		resultList=null;
		try {
			begin();
			resultList = getSession().createQuery(
							"from SupplierQuotationRequestModel where enquiry.id=:enq")
					.setParameter("enq", cust_enq_id).list();
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
	
	
	public List getAllSupplierQutationFromMasterEnquiry(long id, long ofc_id) throws Exception {
		resultList=null;
		long number=0;
		try {
			
			begin();
			
			Object obj = getSession().createQuery("select number from CustomerEnquiryModel where id=:id)").setLong("id", id).uniqueResult();
			
			if(obj!=null)
				number=(Long) obj;
			
			List ids = getSession().createQuery(
							"select id from CustomerEnquiryModel where office.id=:ofc and number=:no order by level")
					.setLong("ofc", ofc_id).setLong("no", number).list();
			
			resultList = getSession().createQuery(
							"from SupplierQuotationRequestModel where enquiry.id in (:ids)")
					.setParameterList("ids", ids).list();
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
	
	
	public long save(SupplierQuotationRequestModel obj) throws Exception {
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
	
	public void update(SupplierQuotationRequestModel objModel)
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

			getSession().delete(new SupplierQuotationRequestModel(id));

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

	public SupplierQuotationRequestModel getSupplierQuotationRequest(long id) throws Exception {
		SupplierQuotationRequestModel cust = null;
		try {
			begin();
			cust = (SupplierQuotationRequestModel) getSession().get(SupplierQuotationRequestModel.class, id);
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
