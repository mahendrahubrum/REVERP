package com.inventory.config.acct.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.exception.ConstraintViolationException;

import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * 
 * @author anil
 * @Project REVERP
 */

@SuppressWarnings("rawtypes")
public class LedgerDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8244909992097466922L;
	private List resultList = new ArrayList();

	public List getLedgers() throws Exception {

		try {
			begin();
			resultList = getSession().createCriteria(LedgerModel.class).list();
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

	public List getAllLedgerIdAndNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(id, name) from LedgerModel where office.id=:ofc")
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

	public List<String> getAllLedgerNames(long ofc_id) throws Exception {
		try {
			return (List<String>) getSession().createQuery("select name from LedgerModel where office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}

	public List getAllActiveLedgerNames(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
							+ " from LedgerModel where office.id=:ofc and status=:val order by name")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

	public List getAllActiveGeneralLedgerOnly(long ofc_id) throws Exception {
		resultList = null;
		try {
			begin();

			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(a.id, a.name)"
							+ " from LedgerModel a  where a.office.id=:ofc and a.status=:val and a.type=:type")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.setParameter("type", SConstants.LEDGER_ADDED_DIRECTLY).list();

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

	public List getAllActiveLedgerNames() throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
							+ " from LedgerModel where status=:val")
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

	public long save(List ledgerModels, long officeId) throws Exception {
//		long objId = 0;
		long saved = 0;
		try {
			begin();
			LedgerModel ledgerModel = null;
			Iterator ofcList = ledgerModels.iterator();
			while (ofcList.hasNext()) {
				boolean ledgerNamePresent = false;
				ledgerModel = (LedgerModel) ofcList.next();
				List<String> ledgerNames = getAllLedgerNames(ledgerModel.getOffice().getId());
				for (String ledgerName : ledgerNames) {
					if (ledgerModel.getName().equalsIgnoreCase(ledgerName)) {
						ledgerNamePresent = true;
						break;
					}
				}
				if (!ledgerNamePresent) {
					saved = (Long) getSession().save(ledgerModel);
					flush();
//					if (officeId == ledgerModel.getOffice().getId())
//						objId = ledgerModel.getId();
				}
			}
			if (ledgerModel != null && saved != 0) {
				getSession().createQuery("update GroupModel set ledgerParentId=ledgerParentId+1 where id=:grpId")
						.setParameter("grpId", ledgerModel.getGroup().getId()).executeUpdate();
				
				commit();
				
			}
		} catch (Exception e) {
			close();
			rollback();
			throw e;
		} finally {
			flush();
			close();
		}
		return saved;
	}

	public long update(List ledgList, long officeId) throws Exception {
		long objId = 0;
		try {
			begin();
			LedgerModel obj = null;
			Iterator iter = ledgList.iterator();
			while (iter.hasNext()) {
				obj = (LedgerModel) iter.next();
				getSession().update(obj);
				if (officeId == obj.getOffice().getId())
					objId = obj.getId();
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
		return objId;
	}

	public void delete(long id) throws Exception, ConstraintViolationException{

		try {
			begin();

			LedgerModel obj = (LedgerModel) getSession().get(LedgerModel.class, id);
			LedgerModel delMdl = null;

			Iterator resIter = getSession().createQuery(" from LedgerModel where group.id=:grp and parentId=:val")
					.setParameter("grp", obj.getGroup().getId()).setParameter("val", obj.getParentId()).list()
					.iterator();

			while (resIter.hasNext()) {
				delMdl = (LedgerModel) resIter.next();
				getSession().delete(delMdl);
			}

			commit();

		} catch(ConstraintViolationException ce) {
			rollback();
			close();
			throw ce;
		}catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();

	}

	public LedgerModel getLedgeer(long id) throws Exception {
		LedgerModel of = null;
		try {
			begin();
			of = (LedgerModel) getSession().get(LedgerModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return of;
	}

	public List getAllActiveLedgerNamesUnderGroup(long group_id, long ofc_id) throws Exception {
		try {
			begin();

			if (ofc_id != 0) {
				resultList = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
						+ " from LedgerModel where group.id=:grpid and status=:val and office.id=:ofc order by name")
						.setParameter("grpid", group_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
						.setLong("ofc", ofc_id).list();
			} else {
				resultList = getSession()
						.createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
								+ " from LedgerModel where group.id=:grpid and status=:val order by name")
						.setParameter("grpid", group_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
		return resultList;
	}

	/*
	 * public List getAllCustomers(long office_id) throws Exception { try { begin();
	 * resultList = getSession().createQuery(
	 * "select new com.inventory.config.acct.model.LedgerModel(id, name)" +
	 * " from LedgerModel where group.id=:grpid and status=:val and office.id=:ofc"
	 * ).setParameter("ofc", office_id) .setParameter("grpid",
	 * SConstants.CUSTOMER_GROUP_ID).setParameter("val",
	 * SConstants.statuses.LEDGER_ACTIVE).list(); commit(); } catch (Exception e) {
	 * rollback(); close(); // TODO Auto-generated catch block e.printStackTrace();
	 * throw e; } finally { flush(); close(); return resultList; } }
	 * 
	 * 
	 * public List getAllSuppliers(long office_id) throws Exception { try { begin();
	 * resultList = getSession().createQuery(
	 * "select new com.inventory.config.acct.model.LedgerModel(id, name)" +
	 * " from LedgerModel where group.id=:grpid and status=:val and office.id=:ofc"
	 * ).setParameter("ofc", office_id) .setParameter("grpid",
	 * SConstants.SUPPLIER_GROUP_ID).setParameter("val",
	 * SConstants.statuses.LEDGER_ACTIVE).list(); commit(); } catch (Exception e) {
	 * rollback(); close(); // TODO Auto-generated catch block e.printStackTrace();
	 * throw e; } finally { flush(); close(); return resultList; } }
	 */

	public List getAllLedgersUnderGroupAndSubGroups(long office_id, long org_id, long gpid) throws Exception {
		try {
			begin();

			gpList = new ArrayList();

			List lst = getAllGroupsUnderGroup(gpid, org_id);
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
							+ " from LedgerModel where group.id in (:grpids) and status=:val and office.id=:ofc")
					.setParameter("ofc", office_id).setParameterList("grpids", lst)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
		}
		return resultList;
	}

	public List getAllLedgersUnderGroupAndSubGroupsFromGroupList(long office_id, long org_id, List<Long> grpList)
			throws Exception {
		try {
			begin();

			List lst = new ArrayList();

			Iterator<Long> it = grpList.iterator();
			gpList = new ArrayList();
			while (it.hasNext()) {
				lst.addAll(getAllGroupsUnderGroup(it.next(), org_id));
			}
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
							+ " from LedgerModel where group.id in (:grpids) and status=:val and office.id=:ofc")
					.setParameter("ofc", office_id).setParameterList("grpids", lst)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
		}
		return resultList;
	}

	List gpList = new ArrayList();

	public List getAllGroupsUnderGroup(long grp_id, long org_id) throws Exception {
		try {
			gpList = new ArrayList();
			gpList.add(grp_id);
			gpList.addAll(getChilds(org_id, grp_id));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}

		return gpList;

	}

	public List getChilds(long org_id, long gpid) throws Exception {

		try {
			List list = getSession()
					.createQuery("select id " + "from GroupModel where parent_id=:grp and organization.id=:org")
					.setParameter("org", org_id).setLong("grp", gpid).list();

			Iterator it1 = list.iterator();
			while (it1.hasNext()) {

				long id = (Long) it1.next();
				gpList.add(id);
				gpList.addAll(getChilds(org_id, id));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return gpList;
	}

	public List getAllCustomers(long office_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
							+ " from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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
		}
		return resultList;
	}

	public List getAllCustomersUnderSalesMan(long office_id, long sales_man) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , customer_code,' ] '))"
							+ " from CustomerModel where ledger.office.id=:ofc and responsible_person=:sm and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("sm", sales_man)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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

	public List getAllSuppliersUnderSalesMan(long office_id, long sales_man) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.office.id=:ofc and responsible_person=:sm and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("sm", sales_man)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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
		}
		return resultList;
	}

	public List getAllSuppliers(long office_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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
		}
		return resultList;
	}

	public List getAllSuppliersWithLoginID(long office_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(login_id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.office.id=:ofc and ledger.status=:val and login_id!=0 order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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
		}
		return resultList;
	}

	public List getAllSuppliersWithLoginIDUnderCountry(long countryId, long office_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(login_id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.office.id=:ofc  and ledger.address.country.id=:ctry and ledger.status=:val and login_id!=0 order by name")
					.setParameter("ctry", countryId).setParameter("ofc", office_id)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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
		}
		return resultList;
	}

	public List getAllSuppliersFromOrgId(long org_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.office.organization.id=:org and ledger.status=:val")
					.setParameter("org", org_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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

	public List getAllCustomers() throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
							+ " from CustomerModel where ledger.status=:val order by name")
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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
		}
		return resultList;
	}

	public List getAllSuppliers() throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
							+ " from SupplierModel where ledger.status=:val order by name")
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

		}
		return resultList;
	}

	public boolean isAlreadyExists(long officeId, String name) throws Exception {

		boolean flag = false;
		List list = null;
		try {
			begin();
			list = getSession().createQuery("from LedgerModel where name=:name and office.id=:ofc")
					.setParameter("name", name).setParameter("ofc", officeId).list();
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
			LedgerModel model = null;

			for (int i = 0; i < modelVector.size(); i++) {

				model = (LedgerModel) modelVector.get(i);

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

	public long save(LedgerModel mdl) throws Exception {
		try {

			begin();

			getSession().save(mdl);

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
		return mdl.getId();
	}

	public GroupModel isGroupAlreadyExists(long orgId, GroupModel grp) throws Exception {

		GroupModel objModel = null;
		try {
			begin();
			Object obj = getSession().createQuery("from GroupModel where name=:name and organization.id=:org")
					.setParameter("name", grp.getName()).setParameter("org", orgId).uniqueResult();

			if (obj != null)
				objModel = (GroupModel) obj;
			else {
				grp.setId(0);
				grp.setOrganization(new S_OrganizationModel(orgId));
				getSession().save(grp);
				objModel = grp;
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

		return objModel;

	}

	public List getAllGeneralLedgers(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(" from LedgerModel where office.id=:ofc and type=:type order by name")
					.setParameter("ofc", ofc_id).setParameter("type", SConstants.LEDGER_ADDED_DIRECTLY).list();
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

	public List getAllLedgersFromIDList(List<Long> idList) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("from LedgerModel where id in (:ids)").setParameterList("ids", idList)
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

	public double getOpeningBalance(Date start_date, long ledger_id) throws Exception {
		double op_bal = 0;
		try {

			resultList = new ArrayList();

			begin();

			Object objDr = getSession().createQuery(
					"select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and "
							+ "b.toAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();
			Object objCr = getSession().createQuery(
					"select sum(b.amount) from TransactionModel a join a.transaction_details_list b where a.date <:stdt and "
							+ "b.fromAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date).uniqueResult();

			if (objDr != null)
				op_bal += (Double) objDr;

			if (objCr != null)
				op_bal -= (Double) objCr;

			commit();

			return op_bal;

		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}

	}

	public double getLedgerCurrentBalance(long led_id) throws Exception {
		double current_balance = 0;
		try {
			begin();
			current_balance = (Double) getSession()
					.createQuery("select coalesce(current_balance,0) from LedgerModel where id=:led")
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
		return current_balance;
	}

	public List getAllDirectAddedLedgersUnderType(long officeID, long expense) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					" from LedgerModel where group.account_class_id=:expense and office_id=:ofcid and type=:typ")
					.setLong("ofcid", officeID).setInteger("typ", SConstants.LEDGER_ADDED_DIRECTLY)
					.setParameter("expense", expense).list();

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

	public List getAllUserLedgers(long officeID) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select a.ledger from UserModel a where a.ledger.office.id=:ofcid")
					.setLong("ofcid", officeID).list();

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

	public String getEmailFromLedgerID(long led_id) throws Exception {
		String email = "";
		try {

			begin();

			email = (String) getSession().createQuery("select address.email from LedgerModel where id=:id")
					.setParameter("id", led_id).uniqueResult();

			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return email;
	}

	public List getAllSuppliersFromIDs(List IDs) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(
					"select new com.inventory.config.acct.model.LedgerModel(ledger.id, concat(name, ' [ ' , supplier_code,' ] '))"
							+ " from SupplierModel where ledger.id in(:ids) order by name")
					.setParameterList("ids", IDs).list();

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

	public String getLedgerNameFromID(long id) throws Exception {
		String name = "";
		try {

			begin();

			Object obj = getSession().createQuery("select name from LedgerModel where id=:id").setParameter("id", id)
					.uniqueResult();

			if (obj != null)
				name = (String) obj;

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

	public List getAllCustomersWithoutCode(long office_id) throws Exception {
		try {
			begin();

			resultList = getSession()
					.createQuery("select new com.inventory.config.acct.model.LedgerModel(ledger.id, name)"
							+ " from CustomerModel where ledger.office.id=:ofc and ledger.status=:val order by name")
					.setParameter("ofc", office_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();

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

	public long getParentUnderGroup(Long grpId) throws Exception {
		long id = 0;
		try {

			begin();

			Object obj = getSession().createQuery("select ledgerParentId from GroupModel where id=:id")
					.setParameter("id", grpId).uniqueResult();

			if (obj != null)
				id = (Long) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return id;
	}

	public List getAllLedgersUnderParentFromLedger(LedgerModel ledgModel) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery(" from LedgerModel where group.id=:grp and parentId=:val")
					.setParameter("grp", ledgModel.getGroup().getId()).setParameter("val", ledgModel.getParentId())
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

	public List getAllActiveLedgerNamesExcluding(long office, int type, long group) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
					+ " from LedgerModel where office.id=:office and type!=:type and group.id!=:group and status=:val")
					.setParameter("office", office).setParameter("type", type).setParameter("group", group)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

	public List getAllActiveLedgerNamesOfGroup(long office, int type, long group) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
					+ " from LedgerModel where office.id=:office and type=:type and group.id=:group and status=:val")
					.setParameter("office", office).setParameter("type", type).setParameter("group", group)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

	public List getAllActiveLedgerNamesOfAccountGroup(long office, int type, long account) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(id, name)"
					+ " from LedgerModel where office.id=:office and type=:type and group.account_class_id=:account and status=:val")
					.setParameter("office", office).setParameter("type", type).setParameter("account", account)
					.setParameter("val", SConstants.statuses.LEDGER_ACTIVE).list();
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

	public long getClassOfLedger(Long ledgerId) throws Exception {
		long id = 0;
		try {

			begin();
			Object obj = getSession().createQuery("select a.group.account_class_id from LedgerModel a where a.id=:id")
					.setParameter("id", ledgerId).uniqueResult();

			if (obj != null)
				id = (Long) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return id;
	}

	public double getLedgerBalanceBetweenDates(Date start_date, Date end_date, long ledger_id) throws Exception {
		double op_bal = 0;
		try {

			resultList = new ArrayList();

			begin();

			Object objDr = getSession()
					.createQuery("select sum(b.amount) from TransactionModel"
							+ " a join a.transaction_details_list b where a.date between :stdt and :enddt and "
							+ "b.toAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();
			Object objCr = getSession()
					.createQuery("select sum(b.amount) from TransactionModel"
							+ " a join a.transaction_details_list b where a.date between :stdt and :enddt and "
							+ "b.fromAcct.id =:led)")
					.setLong("led", ledger_id).setDate("stdt", start_date).setDate("enddt", end_date).uniqueResult();

			if (objDr != null)
				op_bal += (Double) objDr;

			if (objCr != null)
				op_bal -= (Double) objCr;

			commit();

			return op_bal;

		} catch (Exception e) {
			// TODO: handle exception
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}

	}

	public List getAllActiveLedgersUnderClasses(long ofc_id, List idClassList) throws Exception {
		resultList = null;
		try {
			begin();

			resultList = getSession().createQuery("select new com.inventory.config.acct.model.LedgerModel(a.id, a.name)"
					+ " from LedgerModel a  where a.office.id=:ofc and a.status=:val and a.group.account_class_id in (:list) and a.type=:type order by a.name")
					.setParameter("ofc", ofc_id).setParameter("val", SConstants.statuses.LEDGER_ACTIVE)
					.setParameterList("list", idClassList).setParameter("type", SConstants.LEDGER_ADDED_DIRECTLY)
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

	public void updateGroupModel(long id) throws Exception {
		try {
			begin();
			
			getSession().createQuery("update GroupModel set ledgerParentId=ledgerParentId+1 where id=:grpId")
			.setParameter("grpId", id).executeUpdate();
			
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
}
