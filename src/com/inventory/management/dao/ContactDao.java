package com.inventory.management.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.management.model.ContactModel;
import com.inventory.management.model.MailModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class ContactDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4045660356158761284L;
	List resultList = new ArrayList();

	public List getAllContacts(long login_id, int type) throws Exception {
		resultList = null;
		try {
			begin();

			if (type == 0) {
				resultList = getSession()
						.createQuery(
								"select new com.inventory.management.model.ContactModel(id, name)"
										+ " from ContactModel where (login.id=:log or added_by=:log)")
						.setLong("log", login_id).list();
			} else {
				resultList = getSession()
						.createQuery(
								"select new com.inventory.management.model.ContactModel(id, name)"
										+ " from ContactModel where (login.id=:log or added_by=:log) and type=:tp")
						.setLong("log", login_id).setInteger("tp", type).list();
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

	public List getCategories(int type, long org_id) throws Exception {
		try {
			resultList = null;
			begin();
			if (type != 0)
				resultList = getSession()
						.createQuery(
								"select new com.inventory.management.model.ContactCategoryModel(id, name)"
										+ " from ContactCategoryModel where type=:tp and organization_id=:org")
						.setLong("org", org_id).setInteger("tp", type).list();
			else
				resultList = getSession()
						.createQuery(
								"select new com.inventory.management.model.ContactCategoryModel(id, name)"
										+ " from ContactCategoryModel where organization_id=:org")
						.setLong("org", org_id).list();
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

	public ContactModel getContact(long id) throws Exception {
		ContactModel mod = null;
		try {
			begin();
			mod = (ContactModel) getSession().get(ContactModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}

	public long save(ContactModel unitModel) throws Exception {
		long model;
		try {

			begin();
			model = (Long) getSession().save(unitModel);
			commit();

		} catch (Exception e) {
			model = 0;
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return model;
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession()
					.createQuery("delete from MailModel where contact_id=:cnt")
					.setLong("cnt", id).executeUpdate();
			getSession().delete(new ContactModel(id));
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

	public void update(ContactModel unitModel) throws Exception {
		try {

			begin();
			getSession().update(unitModel);
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

	public List getContactReport(long categId, Date frmDt, Date toDt,
			long ofc_id, long org_id, long createdBy, int cat_type,long login_id)
			throws Exception {
		try {
			resultList = null;
			begin();
			String criteria = "";
			if (categId != 0) {
				criteria += " and category.id=" + categId;
			}

			if (cat_type != 0) {
				criteria += " and category.type=" + cat_type;
			}

			if (createdBy != 0) {
				criteria += " and login.id=" + createdBy;
			} 
//			else {
//				if (ofc_id != 0) {
//					criteria += " and login.office.id=" + ofc_id;
//				} else {
//					criteria += " and login.office.organization.id=" + org_id;
//				}
//			}

			resultList = getSession()
					.createQuery(
							"select new com.webspark.bean.ReportBean(login.office.name," +
																		"address," +
																		"login.login_name," +
																		"name," +
																		"category.name," +
																		"mobile," +
																		"location," +
																		"date," +
																		"contact_person,id)"
									+ " from ContactModel where date between :frm and :to  and added_by=:logId "
									+ criteria).setParameter("frm", frmDt)
					.setParameter("to", toDt).setParameter("logId",login_id).list();

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

	public List getSentMails(long contactId) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from MailModel where contact_id=:cnt order by date desc")
					.setLong("cnt", contactId).list();

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

	public void saveMail(List mailList) throws Exception {
		try {
			begin();
			
			Iterator itr=mailList.iterator();
			while (itr.hasNext()) {
				getSession().save((MailModel)itr.next());
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

	public List getSentMailReport(int type, long contact, long category,
			Date fromDate, Date toDate) throws Exception {
		List resList = null;
		try {
			String condition = "";
			begin();

			if (category != 0) {
				condition += " and contact_id in (select id from ContactModel where category.id="+category+")";
			}

			if (type != 0) {
				condition+=" and contact_id in (select id from ContactModel where type="+type+")";
			}

			if (contact != 0) {
				condition+=" and contact_id="+contact;
			}

			resList = getSession()
					.createQuery(
							"select new com.inventory.reports.bean.SentMailReportBean( cast(date as string), " +
															"(select name from ContactModel where id=contact_id) ,subject ,content)"
									+ " from MailModel where date(date) between :frm and :to "+condition+" order by date").setParameter("frm", fromDate).setParameter("to", toDate)
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
		return resList;
	}
	
	public String getEmailId(long contactId) throws Exception {
		String email="";
		try {
			begin();
			email = (String) getSession()
					.createQuery(
							"select email from ContactModel where id=:cont order by name")
					.setLong("cont", contactId).uniqueResult();

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
		return email;
	}
	
	public List getAllEmailIds(long loginID) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.management.model.ContactModel(id,concat(name,' <',email,'>'))" +
							" from ContactModel where login.id=:login order by email")
					.setLong("login", loginID).list();

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

	public String getEmailAttachment(long mailId) throws Exception {
		String att="";
		try {
			begin();
			att = (String) getSession()
					.createQuery("select attachment from MailModel where id=:id")
					.setLong("id",mailId).uniqueResult();

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
		return att;
	}

}