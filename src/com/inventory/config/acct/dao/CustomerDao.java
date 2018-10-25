package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.model.CustomerGroupModel;
import com.inventory.config.acct.model.CustomerModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;

/**
 * @Author Jinshad P.T.
 */

public class CustomerDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7798611367868252125L;
	private List resultList = new ArrayList();

	public List getAllCustomersNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(id, concat(name, ' ( ' , customer_code,' ) '))"
									+ " from CustomerModel where ledger.office.id=:ofc order by name")
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

	public List getAllActiveCustomerNamesWithLedgerID(long ofc_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ " from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
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

	public List getAllActiveCustomerNamesWithOrgID(long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ " from CustomerModel where ledger.office.organization.id=:org and   ledger.status=:val")
					.setLong("org", org_id)
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

	@SuppressWarnings("unchecked")
	public long save(CustomerModel obj, S_LoginModel login) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj.getLedger());
			if (login != null) {
				getSession().save(login);

				List<Long> objList = getSession().createQuery("select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
						.setParameter("RolId", login.getUserType().getId())
						.list();

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
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}

	public CustomerModel saveAndGet(CustomerModel obj) throws Exception {
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
			return obj;
		}
	}

	@SuppressWarnings("unchecked")
	public void update(CustomerModel customer, S_LoginModel loginModel) throws Exception {
		try {
			begin();
			getSession().update(customer.getAddress());
			getSession().update(customer.getLedger());
			flush();
			if (loginModel != null) {
				if(loginModel.getId()!=0){
					getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:log")
								.setParameter("log", loginModel.getId()).executeUpdate();
					getSession().update(loginModel);
				}
				else{
					getSession().save(loginModel);
				}
				
				List<Long> objList = getSession().createQuery("select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
						.setParameter("RolId",loginModel.getUserType().getId()).list();

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

			CustomerModel cust = (CustomerModel) getSession().get(CustomerModel.class, id);
			S_LoginModel login=null;
			if(cust.getLogin()!=null){
				login = cust.getLogin();
				getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:log")
							.setParameter("log", cust.getLogin().getId()).executeUpdate();
			}
			getSession().delete(cust.getAddress());
			getSession().delete(cust.getLedger());
			getSession().delete(cust);
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

	public CustomerModel getCustomer(long id) throws Exception {
		CustomerModel cust = null;
		try {
			begin();
			cust = (CustomerModel) getSession().get(CustomerModel.class, id);
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

	public CustomerModel getCustomerFromLedger(long led_id) throws Exception {
		CustomerModel cust = null;
		try {
			begin();
			cust = (CustomerModel) getSession()
					.createQuery("from CustomerModel where ledger.id=:led")
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

	public int getCustomerMaxCRLimitFromLedger(long led_id) throws Exception {
		int max_cr_limit = 0;
		try {
			begin();
			Object obj = getSession()
					.createQuery(
							"select max_credit_period from CustomerModel where ledger.id=:led")
					.setLong("led", led_id).uniqueResult();
			commit();

			if (obj != null)
				max_cr_limit = (Integer) obj;

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return max_cr_limit;
		}
	}

	public double getLedgerCurrentBalance(long led_id) throws Exception {
		double current_balance = 0;
		try {
			begin();
			current_balance = (Double) getSession()
					.createQuery(
							"select coalesce(current_balance,0) from LedgerModel where id=:led")
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
			return current_balance;
		}
	}

	public List getAllActiveCustomers(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
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

	public boolean isCodeExists(long officeId, String code, long id)
			throws Exception {
		boolean flag = false;
		try {
			begin();

			if (getSession()
					.createQuery(
							"from CustomerModel where customer_code=:code and ledger.office.id=:ofc and id!=:id")
					.setParameter("code", code).setParameter("ofc", officeId)
					.setLong("id", id).list().size() > 0)
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

	// Added by Anil

	public List getAllCustomersNamesList(long ofc_id) throws Exception {
		try {
			begin();
			String condition = "";
			if (ofc_id != 0) {
				condition = " and ledger.office.id=" + ofc_id;
			}
			resultList = getSession()
					.createQuery(
							" from CustomerModel where ledger.status=:val"
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
							"from CustomerModel where name=:name and customer_code=:code and ledger.office.id=:ofc")
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
			CustomerModel model = null;

			for (int i = 0; i < modelVector.size(); i++) {

				model = (CustomerModel) modelVector.get(i);
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

	public List getCustomersCreditDetails(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from CustomerModel where ledger.office.id=:ofc and "
									+ "(ledger.current_balance+credit_limit)<0")
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

	public CustomerModel getCustomerFromLogin(long loginId) throws Exception {

		CustomerModel mdl = null;
		try {
			begin();
			mdl = (CustomerModel) getSession()
					.createQuery("from CustomerModel where login_id=:log")
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

	public List getAllActiveCustomerNamesunderanemployee(long employee_id,
			long org_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ "from CustomerModel where "
									+ "ledger.office.organization.id=:org and responsible_person=:resper and ledger.status=:val order by name")
					.setParameter("org", org_id)
					.setParameter("resper", employee_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					// "from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					// .setParameter("ofc", ofc_id)
					// .setParameter("val", SConstants.statuses.LEDGER_ACTIVE)

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

	public List getAllActiveCustomerNamesunderanemployeeandoffice(
			long employee_id, long off_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.acct.model.CustomerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
									+ "from CustomerModel where "
									+ "ledger.office.id=:ofc and responsible_person=:resper and ledger.status=:val order by name")
					.setParameter("ofc", off_id)
					.setParameter("resper", employee_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					// "from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					// .setParameter("ofc", ofc_id)
					// .setParameter("val", SConstants.statuses.LEDGER_ACTIVE)

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

	public String getCustomerGroupNameFromLedger(long ledgerId) throws Exception {
		String name="";
		try {
			begin();
			name =  (String) getSession()
					.createQuery("select a.name from CustomerGroupModel a, CustomerModel b where b.ledger.id=:ledg and b.customerGroupId=a.id")
					.setParameter("ledg", ledgerId).uniqueResult();
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
		return name;
	}
	
	public CustomerGroupModel getCustomerGroupModelFromLedger(long ledgerId) throws Exception {
		CustomerGroupModel name;
		try {
			begin();
			name =   (CustomerGroupModel) getSession()
					.createQuery("select a from CustomerGroupModel a, CustomerModel b where b.ledger.id=:ledg and b.customerGroupId=a.id")
					.setParameter("ledg", ledgerId).uniqueResult();
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
		return name;
	}
	
	
	public List<Object> getAllSalesDetailsForCustomer(long ledgerId,
			Date fromDate, Date last_payable_date) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.sales.model.SalesModel(sales_number,payment_amount+paid_by_payment,amount)"
									+ " from SalesModel where customer.id=:custId and date between :fromDate and :lastdate" +
									" and status=2 and active=true")
					.setParameter("custId", ledgerId)
					.setParameter("fromDate", fromDate)
					.setParameter("lastdate", last_payable_date).list();
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
