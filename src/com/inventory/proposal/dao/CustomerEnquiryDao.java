package com.inventory.proposal.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.inventory.proposal.model.CustomerEnquiryModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class CustomerEnquiryDao extends SHibernate implements Serializable {
	
	
	private static final long serialVersionUID = -3877907915523632470L;
	private List resultList = new ArrayList();

	public List getAllEnquiries(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(id, enquiry)"
									+ " from CustomerEnquiryModel where office.id=:ofc")
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
	
	
	public List getAllMasterEnquiries(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(a.id, concat(a.number,' : ',a.date,' , ', a.enquiry, ' ( ', a.customer.name,')'), a.number)"
									+ " from CustomerEnquiryModel a where a.office.id=:ofc and a.level=0 order by a.number")
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
	
	
	public long save(CustomerEnquiryModel obj) throws Exception {
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
	
	public void update(CustomerEnquiryModel objModel)
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

			getSession().delete(new CustomerEnquiryModel(id));

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

	public CustomerEnquiryModel getEnquiry(long id) throws Exception {
		CustomerEnquiryModel cust = null;
		try {
			begin();
			cust = (CustomerEnquiryModel) getSession().get(CustomerEnquiryModel.class, id);
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
	
	
	public int getEnquiryFromNumber(long id) throws Exception {
		int leval=0;
		try {
			begin();
			Object obj = getSession().createQuery("select max(level) from CustomerEnquiryModel where number=(select number from " +
								"CustomerEnquiryModel where id=:id) and id!=:id").setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				leval=(Integer) obj;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return leval;
		}
	}
	
	
	public int getMaxEnquiryLevel(long id) throws Exception {
		int leval=0;
		try {
			begin();
			Object obj = getSession().createQuery("select max(level) from CustomerEnquiryModel where number=(select number from " +
								"CustomerEnquiryModel where id=:id) and id!=:id").setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				leval=(Integer) obj;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return leval;
		}
	}
	
	public long getEnquiryNumberFromID(long id) throws Exception {
		long number=0;
		try {
			begin();
			Object obj = getSession().createQuery("select number from CustomerEnquiryModel where id=:id)").setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				number=(Long) obj;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return number;
		}
	}
	
	public long getEnquiryMinEnqIDFromNumber(long enq_no) throws Exception {
		long number=0;
		try {
			begin();
			Object obj = getSession().createQuery("select min(id) from CustomerEnquiryModel where number=:no)").setLong("no", enq_no).uniqueResult();
			commit();
			
			if(obj!=null)
				number=(Long) obj;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return number;
		}
	}
	
	
	public List getAllLatestEnquiriesUnderMaster(long id, long ofc_id) throws Exception {
		resultList=new ArrayList();
		long number=0;
		try {
			
			begin();
			
			Object obj = getSession().createQuery("select number from CustomerEnquiryModel where id=:id)").setLong("id", id).uniqueResult();
			
			if(obj!=null)
				number=(Long) obj;
			
			resultList = getSession().createQuery(
							"from CustomerEnquiryModel where office.id=:ofc and number=:no order by level")
					.setLong("ofc", ofc_id).setLong("no", number).list();
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
	
	public List getAllLatestEnquiryIDs(long id, long ofc_id) throws Exception {
		resultList=new ArrayList();
		long number=0;
		try {
			
			begin();
			
			Object obj = getSession().createQuery("select number from CustomerEnquiryModel where id=:id)").setLong("id", id).uniqueResult();
			
			if(obj!=null)
				number=(Long) obj;
			
			resultList = getSession().createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(id, concat(number,' : ',date,' , ', enquiry, ' ( ', customer.name,')'), number) from CustomerEnquiryModel where office.id=:ofc and number=:no order by level")
								.setLong("ofc", ofc_id).setLong("no", number).list();
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
	
	public long getParentID(long id, long ofc_id) throws Exception {
		long number=0, par_id=0;
		try {
			
			begin();
			
			Object obj = getSession().createQuery("select number from CustomerEnquiryModel where id=:id)").setLong("id", id).uniqueResult();
			
			if(obj!=null)
				number=(Long) obj;
			
			par_id = (Long) getSession().createQuery(
							"select id from CustomerEnquiryModel where office.id=:ofc and number=:no and level=0")
					.setLong("no", number).setLong("ofc", ofc_id).uniqueResult();
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
			return par_id;
		}
	}
	
	
	
	public List getEnquiriesUnderCustomer(long cust) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(id, enquiry)"
									+ " from CustomerEnquiryModel where customer.id=:cust")
					.setParameter("cust", cust).list();
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
	
	
	
	public List getAllLatestEnquiriesUnderCustomer(long cust, long cur_id) throws Exception {
		resultList=new ArrayList();
		try {
			
			String criteria="";
			if(cur_id!=0)
				criteria+=" and a.id !=(select b.id from CustomerEnquiryModel b where b.id="+cur_id+")";
			
			begin();
			resultList = getSession().createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(a.id, concat(a.number,' : ',a.date,' , ', a.enquiry, ' ( ', a.customer.name,')'), a.number)"
									+ " from CustomerEnquiryModel a where a.customer.id=:cust"+criteria+" order by a.number")
					.setParameter("cust", cust).list();
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
	
	
	public List getAllFirstEnquiriesUnderCustomer(long cust, long cur_id) throws Exception {
		resultList=new ArrayList();
		try {
			
			String criteria="";
//			if(cur_id!=0)
//				criteria+=" and a.number !=(select b.number from CustomerEnquiryModel b where b.id="+cur_id+")";
			
			begin();
			List lst = getSession().createQuery(
							"select new com.inventory.proposal.model.CustomerEnquiryModel(a.id, concat(a.number,' : ',a.date,' , ', a.enquiry, ' ( ', a.customer.name,')'), a.number)"
									+ " from CustomerEnquiryModel a where a.customer.id=:cust and a.level=0 order by a.number")
					.setParameter("cust", cust).list();
			commit();
			
			long number=0;
			CustomerEnquiryModel obj;
			
			for(int i=0; i<lst.size(); i++) {
				obj=(CustomerEnquiryModel) lst.get(i);
				
				if(number!=obj.getNumber()) {
					resultList.add(obj);
				}
				
				number=obj.getNumber();
			}
			
			
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
	
	
	
	
	
	public boolean isParent(long id) throws Exception {
		boolean isParent=false;
		try {
			
			begin();
			
			Object obj = getSession().createQuery("select level from CustomerEnquiryModel where id=:id")
					.setLong("id", id).uniqueResult();
			commit();
			
			if(obj!=null)
				if(((Integer)obj)==0)
					isParent=true;
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return isParent;
		}
	}
	
	
	
	
	
}
