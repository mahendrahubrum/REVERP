package com.webspark.uac.dao;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.inventory.model.DocumentAccessModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.LoanApprovalModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.vaadin.server.VaadinServlet;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.model.DesignationModel;
import com.webspark.uac.model.EmployeeDocumentModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
public class UserManagementDao extends SHibernate implements Serializable {

	@SuppressWarnings("rawtypes")
	private List resultList = new ArrayList();

	@SuppressWarnings("rawtypes")
	List empUnderList = new ArrayList();

	
	@SuppressWarnings("rawtypes")
	public List getUsers() throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(" from UserModel where user_role.active='Y' and status=:ustat")
									.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCode() throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' )')) " +
							" from UserModel where user_role.active='Y' and user_role.id!=1 and status=:ustat").setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeFromOrg(long org_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' )')) " +
							"from UserModel where office.organization.id=:org and user_role.id!=1 and status=:ustat order by first_name")
					.setLong("org", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeFromOrgExcept(long org_id, long id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
					"from UserModel where office.organization.id=:org and loginId.status=0 and status=:ustat and user_role.id!=1 and id!="+id+" order by first_name")
					.setLong("org", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeWithoutSuperAdminFromOrg(long org_id)
			throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' )')) " +
					"from UserModel where  office.organization.id=:org and status=:ustat and user_role.id!=1 order by first_name")
					.setLong("org", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeWithoutSuperAdminFromOrgExcept(long org_id,long id)
			throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' )')) " +
					"from UserModel where office.organization.id=:org and status=:ustat and user_role.id!=1 and id!="+id+" order by first_name")
					.setLong("org", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeUnderOffice(long officeId)
			throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
					"from UserModel where office.id=:ofc and status=:ustat and user_role.id!=1 order by first_name")
					.setLong("ofc", officeId).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeUnderAllOffice(long officeId,long organizationId)throws Exception {
		try {
			String condition = "";
			if (officeId > 0) {
				condition += " and office.id=" + officeId;
			}
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
					"from UserModel where office.organization.id=:org and loginId is not null and status=:ustat and loginId.status=0 and user_role.id!=1 "+condition+" order by first_name")
									.setLong("org", organizationId).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeUnderOfficeExcept(long officeId,long id)
			throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) '))" +
					" from UserModel where office.id=:ofc and status=:ustat and loginId is not null and loginId.status=0 and user_role.id!=1 and id!="+id+" order by first_name")
					.setLong("ofc", officeId).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(long officeId, long org_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(loginId.id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
					"from UserModel where ((user_role.id=:semiad and office.organization.id=:org) or office.id=:ofc) and loginId is not null" +
					" and loginId.status=0 and status=:ustat and user_role.id>2 and loginId.status!=1 order by first_name")
					.setLong("ofc", officeId).setLong("semiad", 3).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE)
					.setLong("ofc", officeId).setLong("org", org_id).list();
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

	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsFromRole(boolean isSuperAdmin, long org_id)
			throws Exception {
		try {
			begin();

			if (isSuperAdmin) {
				resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(id, login_name) from S_LoginModel").list();
			} else {
				resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(id, login_name) from S_LoginModel where "
										+ "userType.active='Y' and status=0 and office.organization.id=:org").setLong("org", org_id).list();
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


	
	@SuppressWarnings("rawtypes")
	public List getAllLoginNames() throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(id, login_name) from S_LoginModel where " +
					" userType.id!=:cust and status=0 and userType.id!=:super order by login_name")
					.setLong("cust", SConstants.ROLE_CUSTOMER)
					.setLong("super", SConstants.ROLE_SUPPLIER).list();

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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllLoginNamesWithoutSparkAdmin() throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(id, login_name) from S_LoginModel where " +
					"userType.id!=:cust and status=0 and userType.id!=:super and userType.id!=1 order by login_name")
					.setLong("cust", SConstants.ROLE_CUSTOMER)
					.setLong("super", SConstants.ROLE_SUPPLIER).list();

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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsForOrg(long org_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(id, login_name) from S_LoginModel where " +
					" office.organization.id=:org and status=0 and userType.id!=:cust and userType.id!=:sup and userType.id!=1 " +
					" order by login_name")
					.setLong("cust", SConstants.ROLE_CUSTOMER)
					.setParameter("sup", SConstants.ROLE_SUPPLIER)
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
		}
		return resultList;
	}

	
	
	public String getUserNameFromLoginID(long login_id) throws Exception {
		String name = "";
		try {
			begin();
			Object obj = getSession().createQuery("select first_name from UserModel where loginId.id=:login and loginId is not null and status=:ustat")
					.setLong("login", login_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).uniqueResult();
			commit();

			if (obj != null)
				name = (String) obj;

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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllDesignations() throws Exception {
		try {
			begin();
			resultList = getSession().createCriteria(DesignationModel.class).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllDesignationsUnderOrg(long org_id) throws Exception {
		try {
			begin();
			resultList = getSession().createCriteria(DesignationModel.class).add(Restrictions.eq("organization_id", org_id)).list();
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
	public UserModel save(UserModel obj)throws Exception {
		try {
			begin();
			if(obj.getLoginId()!=null){
				getSession().save(obj.getLoginId());
				flush();
				List<Long> objList = getSession().createQuery("select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
						.setParameter("RolId",obj.getUser_role().getId()).list();
				S_LoginOptionMappingModel lomm;
				for (Long opt_id : objList) {
					lomm = new S_LoginOptionMappingModel();
					lomm.setLogin_id(new S_LoginModel(obj.getLoginId().getId()));
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
		return obj;
	}

	
	
	public boolean isAlreadyExist(String login_name) throws Exception {
		int ct = 0;
		try {
			begin();
			ct = getSession().createCriteria(S_LoginModel.class).add(Restrictions.eq("login_name", login_name)).list().size();
			commit();
			if (ct > 0)
				return true;
			else
				return false;
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

	
	
	public boolean isCodeAlreadyExist(String compareString, long office_id)
			throws Exception {
		long ct = 0;
		try {
			begin();
			ct = (Long) getSession().createQuery("select count(id) from UserModel where employ_code=:cod and office.id=:ofc")
					.setString("cod", compareString).setLong("ofc", office_id).uniqueResult();

			commit();

			if (ct > 0)
				return true;
			else
				return false;

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

	
	
	public UserModel getUser(long userId) throws Exception {
		UserModel user = null;
		try {
			begin();
			user = (UserModel) getSession().get(UserModel.class, userId);
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
		return user;
	}

	
	
	public void delete(long id) throws Exception {
		try {
			begin();

			UserModel user = (UserModel) getSession().get(UserModel.class, id);

			getSession()
					.createQuery(
							"delete from S_LoginOptionMappingModel where login_id.id=:LogId and login_id.status=0")
					.setParameter("LogId", user.getLoginId().getId())
					.executeUpdate();

			getSession().delete(user.getLoginId());

			getSession().delete(user.getAddress());

			getSession().delete(user);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		flush();
		close();
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public void deleteUser(long id,long sub) throws Exception {
		try {
			begin();
			UserModel user = (UserModel) getSession().get(UserModel.class, id);
			UserModel subUser = (UserModel) getSession().get(UserModel.class, sub);
			
			List userList=new ArrayList();
			Iterator itr=null;
			long loginId=0, addr1=0, addr2=0, addr3=0;
			
			
			if(user!=null){
				
				// Purchase 
				
				getSession().createQuery("update PurchaseInquiryModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
	
				getSession().createQuery("update PurchaseQuotationModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
			
				getSession().createQuery("update PurchaseOrderModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update PurchaseGRNModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update PurchaseModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update ProformaPurchaseModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update PurchaseReturnModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				// Sales
				
				getSession().createQuery("update SalesInquiryModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update QuotationModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update SalesOrderModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
	
				getSession().createQuery("update DeliveryNoteModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
	
				getSession().createQuery("update SalesModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
	
				getSession().createQuery("update SalesReturnModel set responsible_employee=:newLogin where responsible_employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();

				// Laundry Sales
				getSession().createQuery("update LaundrySalesModel set responsible_person=:newLogin where responsible_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				// Tailoring Sales
				getSession().createQuery("update TailoringSalesModel set sales_person=:newLogin where sales_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				// Commission Sales
				getSession().createQuery("update CommissionSalesNewModel set responsible_person=:newLogin where responsible_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update CommissionSalesNewModel set sales_person=:newLogin where sales_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				// Customer
				getSession().createQuery("update CustomerModel set responsible_person=:newLogin where responsible_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				// Supplier
				getSession().createQuery("update SupplierModel set responsible_person=:newLogin where responsible_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update MaterialMappingModel set sales_person=:newLogin where sales_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update HotelSalesModel set sales_person=:newLogin where sales_person="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update CustomerBookingModel set employee=:newLogin where employee="+user.getId())
							.setParameter("newLogin", subUser.getId()).executeUpdate();
				
				getSession().createQuery("update TableModel set employee=:newLogin where employee.id="+user.getId())
							.setParameter("newLogin", new UserModel(subUser.getId())).executeUpdate();
				
				userList=getSession().createQuery("from EmployeeDocumentModel where employee_id="+user.getId()).list();
				itr=userList.iterator();
				while (itr.hasNext()) {
					EmployeeDocumentModel mdl = (EmployeeDocumentModel) itr.next();
					String DIR = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/EmployeeDocuments/".trim();
					if(mdl.getFilename().trim().length()>2){
						String[] arr=mdl.getFilename().trim().split(",");
						List fileNameList=Arrays.asList(arr);
						for (int i = 0; i < fileNameList.size(); i++) {
							try {
								File file=new File(DIR.trim()+fileNameList.get(i).toString().trim());
								if(file.exists() && !file.isDirectory())
									file.delete();
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
						}
					}
					getSession().delete(mdl);
				}
				flush();
				flush();

				// Payroll
				userList=getSession().createQuery("from SalaryDisbursalModel where user.id="+user.getId()).list();
				itr=userList.iterator();
				while (itr.hasNext()) {
					SalaryDisbursalModel mdl = (SalaryDisbursalModel) itr.next();
					getSession().delete(mdl);
				}
				flush();
				
				userList=getSession().createQuery("from DocumentAccessModel where creator.id="+user.getId()).list();
				itr=userList.iterator();
				while (itr.hasNext()) {
					DocumentAccessModel mdl = (DocumentAccessModel) itr.next();
					getSession().delete(mdl);
				}
				flush();
				
				userList=getSession().createQuery("from LoanApprovalModel where loanRequest.user.id="+user.getId()).list();
				itr=userList.iterator();
				while (itr.hasNext()) {
					LoanApprovalModel mdl = (LoanApprovalModel) itr.next();
					getSession().createQuery("delete from LoanDateModel where loan.id=:id").setParameter("id", mdl.getId()).executeUpdate();
					getSession().delete(mdl);
				}
				flush();
				
				userList=getSession().createQuery("from LeaveModel where user.id="+user.getId()).list();
				itr=userList.iterator();
				while (itr.hasNext()) {
					LeaveModel mdl = (LeaveModel) itr.next();
					getSession().createQuery("delete from LeaveHistoryModel where leave=:id").setParameter("id", mdl.getId()).executeUpdate();
					getSession().createQuery("delete from LeaveDateModel where leave.id=:id").setParameter("id", mdl.getId()).executeUpdate();
					getSession().delete(mdl);
				}
				flush();
				
				getSession().createQuery("delete from EmployeeStatusModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from SalesManCommissionMapModel where userId="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserLeaveAllocationModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserLeaveMapModel where userId="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from LoanRequestModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserQualificationModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserContactModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserFamilyContactModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from UserPreviousEmployerModel where user.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from CommissionSalaryModel where employee="+user.getId()).executeUpdate();

				getSession().createQuery("delete from SalaryBalanceMapModel where employee.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from MyMailsModel where user_id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from EmailConfigurationModel where user_id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from PayrollEmployeeMapModel where employee.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from AttendanceModel where userId="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from EmployeeWorkingTimeModel where employee.id="+user.getId()).executeUpdate();
				
				getSession().createQuery("delete from EmployeeAdvancePaymentModel where user.id="+user.getId()).executeUpdate();
				
				flush();
				
				if(user.getLoginId()!=null && subUser.getLoginId()!=null) {
					
					// Stock
					getSession().createQuery("update StockTransferModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					// Journal
					getSession().createQuery("update JournalModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Bank Account Payment
					getSession().createQuery("update BankAccountPaymentModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate(); 
					
					// Bank Account Deposit
					getSession().createQuery("update BankAccountDepositModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Cash Account Payment
					getSession().createQuery("update CashAccountPaymentModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Cash Account Payment
					getSession().createQuery("update CashAccountDepositModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Debit Note
					getSession().createQuery("update DebitNoteModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
					.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Credit Note
					getSession().createQuery("update CreditNoteModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Cheque Return
					getSession().createQuery("update ChequeReturnModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();

					// PDC
					getSession().createQuery("update PdcModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// PDC
					getSession().createQuery("update PdcPaymentModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Payment Deposit
					getSession().createQuery("update PaymentDepositModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					// Item Transfer
					getSession().createQuery("update ItemTransferModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					// Bank Reconsilation
					getSession().createQuery("update BankRecociliationModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					// Fixed Asset Sales
					getSession().createQuery("update FixedAssetSalesModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					// Fixed Asset Depreciation
					getSession().createQuery("update FixedAssetDepreciationMainModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update ManualTradingMasterModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update DailyQuotationModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update ItemDailyRateModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update SupplierQuotationModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update TasksModel set created_by=:newLogin where created_by.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update TasksAssignedUsersModel set user=:newLogin where user.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update ContactModel set added_by=:newLogin where added_by="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update MailModel set send_by=:newLogin where send_by="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update FinancePaymentModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update TailoringSalesModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update CustomerCommissionSalesModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update SupplierQuotationRequestModel set sendBy=:newLogin where sendBy.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update SupplierProposalReceiptionModel set sendBy=:newLogin where sendBy.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update ProposalsSentToCustomersModel set sendBy=:newLogin where sendBy.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("update SurveyModel set login_id=:newLogin where login_id="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update CommissionPurchaseModel set login=:newLogin where login="+user.getLoginId().getId())
								.setParameter("newLogin", subUser.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("update CommissionSalesNewModel set login=:newLogin where login.id="+user.getLoginId().getId())
								.setParameter("newLogin", new S_LoginModel(subUser.getLoginId().getId())).executeUpdate();
					
					getSession().createQuery("delete from SalesManMapModel where login_id="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from QuickMenuModel where login_id="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from SessionActivityModel where login="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from ReportIssueModel where login="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from ReviewModel where login="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from OfficeAllocationModel where login_id="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from PrivilageSetupModel where login_id="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from ActivityLogModel where login="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id="+user.getLoginId().getId()).executeUpdate();
					
					getSession().createQuery("delete from S_LoginHistoryModel where login_id="+user.getLoginId().getId()).executeUpdate();
					
					loginId=user.getLoginId().getId();
					flush();
				}
				if(user.getWork_address()!=null)
					addr1=user.getWork_address().getId();
					
				if(user.getLocal_address()!=null)
					addr2=user.getLocal_address().getId();
				
				if(user.getAddress()!=null)
					addr3=user.getAddress().getId();
				
				getSession().delete(user);
				flush();
				
				if(addr1!=0)
					getSession().createQuery("delete from AddressModel where id="+addr1).executeUpdate();
				
				if(addr2!=0)
					getSession().createQuery("delete from AddressModel where id="+addr2).executeUpdate();
				
				if(addr3!=0)
					getSession().createQuery("delete from AddressModel where id="+addr3).executeUpdate();
				
				if(loginId!=0)
					getSession().createQuery("delete from S_LoginModel where id="+loginId).executeUpdate();
				
				flush();
			}
			commit();
		} 
		catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}
	
	
	
	@SuppressWarnings("unchecked")
	public long update(UserModel obj) throws Exception {
		try {
			begin();
			if(obj.getLoginId()!=null){
				if(obj.getLoginId().getId()!=0)
					getSession().update(obj.getLoginId());
				else
					getSession().save(obj.getLoginId());
				flush();
				if(obj.getLoginId().getStatus()==1){
					getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:login")
								.setParameter("login", obj.getLoginId().getId()).executeUpdate();
					flush();
				}
				List<Long> objList = getSession().createQuery("select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
							.setParameter("RolId",obj.getUser_role().getId()).list();
				if(objList.size()>0){
					getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id=:login")
								.setParameter("login", obj.getLoginId().getId()).executeUpdate();
					flush();
					S_LoginOptionMappingModel lomm;
					for (Long opt_id : objList) {
						lomm = new S_LoginOptionMappingModel();
						lomm.setLogin_id(new S_LoginModel(obj.getLoginId().getId()));
						lomm.setOption_id(new S_OptionModel(opt_id));
						lomm.setActive('Y');
						getSession().save(lomm);
					}
				}
				flush();
			}
			getSession().update(obj);
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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsFromOffice(long office_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel "
							+ "where office.id=:ofc and loginId is not null and loginId.status=0 and status=:ustat and user_role.id!=1 order by first_name")
							.setLong("ofc", office_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsFromOrg(long org_id) throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel "
							+ "where office.organization.id=:ofc and status=:ustat and loginId is not null and loginId.status=0 and user_role.id!=1")
					.setLong("ofc", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();

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


	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsFromOfficeWithRole(long org_id, long roleId)
			throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel "
									+ "where office.organization.id=:org and loginId is not null and status=:ustat and loginId.status=0 " +
									"and user_role.id=:rl and user_role.id!=1")
					.setLong("org", org_id).setLong("rl", roleId).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();

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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllUsers() throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel " +
					"where user_role.active='Y' and loginId is not null and status=:ustat and loginId.status=0 and user_role.id!=1")
					.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();

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

	
	
	@SuppressWarnings("rawtypes")
	public UserModel getUserFromLogin(long login_id) throws Exception {
		UserModel user = null;
		try {
			begin();
			List list = getSession().createQuery("from UserModel where loginId.id=:login and loginId.status=0 and status=:ustat and loginId is not null")
					.setLong("login", login_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
			if (list.size() > 0)
				user = (UserModel) list.get(0);
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
		return user;
	}

	
	
	@SuppressWarnings("rawtypes")
	public List getAllLogins() throws Exception {
		try {
			begin();

			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from " +
													"UserModel where loginId.status=0 and status=:ustat and loginId is not null")
													.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithLedgerID(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(ledger.id, concat(first_name,'  ( ', employ_code ,' ) ')) from UserModel where "
									+ "loginId.office.id=:ofc and loginId.status=0 and status=:ustat")
					.setLong("ofc", ofc_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getNoPayedUsersWithLedgerID(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.uac.model.UserModel(ledger.id, concat(first_name,'  ( ', employ_code ,' ) ')) from UserModel where "
									+ "loginId.office.id=:ofc and loginId.status=0 and salary_type!=0 and status=:ustat")
					.setLong("ofc", ofc_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getUsersByCriteria(String criteria) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(loginId.id,first_name) from UserModel "
									+ "where user_role.id>2 and status=:ustat and loginId.status=0 "+ criteria+ " order by first_name")
									.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithLoginId(long officeID) throws Exception {
		try {
			String condition = "";
			if (officeID > 0) {
				condition += " and loginId.office.id=" + officeID;
			}
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(loginId.id,concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) '))"
									+ " from UserModel  where user_role.id!=1 and loginId is not null and status=:ustat and loginId.status=0 and user_role.active='Y' "
									+ condition + "order by first_name").setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getEmployeesUnderUser(long office_id, long login_id)
			throws Exception {
		try {
			begin();
			empUnderList = new ArrayList();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel "
							+ "where office.id=:ofc and loginId is not null and status=:ustat and loginId.status=0 and user_role.id!=1 and loginId.id in (:lgs)  order by first_name")
					.setLong("ofc", office_id).setParameterList("lgs",getEmployeesIDsUnderUser(office_id, login_id))
					.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();

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

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getEmployeesIDsUnderUser(long office_id, long login_id) throws Exception {
		try {
			List lst = getSession().createQuery("select loginId.id from UserModel where office.id=:ofc and loginId is not null and " +
					"loginId.status=0 and user_role.id!=1 and superior_id=:sup and status=:ustat")
					.setLong("sup", login_id).setLong("ofc", office_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
			empUnderList.add(login_id);
			if (lst != null && lst.size() > 0) {
				Iterator it1 = lst.iterator();
				while (it1.hasNext()) {
					getEmployeesIDsUnderUser(office_id, (Long) it1.next());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			
		}
		return empUnderList;
	}

	
	
	@SuppressWarnings("rawtypes")
	public List getAllSuperiorLogins(long officeID) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel "
									+ "where office.id=:ofc and loginId is not null and status=:ustat and loginId.status=0 and user_role.id!=1 and " +
									"user_role.id in (:supList) order by first_name")
					.setLong("ofc", officeID).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE)
					.setParameterList("supList", SConstants.superiorList).list();
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

	
	
	@SuppressWarnings("rawtypes")
	public List getAllSuperiorLoginsFromOrganization(long organizationID)throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel where " +
					"office.organization.id=:ofc and loginId is not null and loginId.status=0 and status=:ustat and user_role.id!=1 and user_role.id in (:supList) " +
					"order by first_name")
					.setLong("ofc", organizationID).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE)
					.setParameterList("supList", SConstants.superiorList).list();

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

	
	
	public String getUseEmailFromLogin(long login_id) throws Exception {
		String email = "";
		try {
			begin();
			email = (String) getSession().createQuery("select address.email from UserModel where loginId is not null and loginId.id=:login and loginId.status=0")
					.setLong("login", login_id).uniqueResult();
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

	
	
	public long getDepartment(long oId) throws Exception {
		long id = 0;
		try {
			begin();
			id = (Long) getSession().createQuery("select u.department.id from UserModel u where u.id=:id")
					.setParameter("id", oId).uniqueResult();
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
	
	
	
	@SuppressWarnings("rawtypes")
	public List getAllUsersUnderOffice(long officeId) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, first_name) from UserModel where " +
						"office.id=:ofc and loginId is not null and loginId.status=0 and status=:ustat and user_role.id!=1 order by first_name").setParameter("ofc", officeId)
					.setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();

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
	
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeFromOffice(long office, boolean isSuperAdmin) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			/*if(!isSuperAdmin){
				cdn+=" and user_role.id!=1 ";
			}*/
			list = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' ) ')) " +
					"from UserModel where office.id=:office and user_role.id!=1 and status=:ustat order by first_name")
					.setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
		return list;
	}

	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeFromOffice(long office) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			/*if(!isSuperAdmin){
				cdn+=" and user_role.id!=1 ";
			}*/
			list = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,' ( ', employ_code ,' ) ')) " +
					"from UserModel where office.id=:office and user_role.id!=1 order by first_name")
					.setLong("office", office).list();
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
		return list;
	}
	
	
	public void saveUserAddress(UserModel user) throws Exception{
		try {
			begin();
			if(user.getAddress().getId()!=0)
				getSession().update(user.getAddress());
			else
				getSession().save(user.getAddress());
			flush();
			if(user.getLocal_address().getId()!=0)
				getSession().update(user.getLocal_address());
			else
				getSession().save(user.getLocal_address());
			flush();
			if(user.getWork_address().getId()!=0)
				getSession().update(user.getWork_address());
			else
				getSession().save(user.getWork_address());
			flush();
			getSession().update(user);
			commit();
		}
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} 
		finally {
			flush();
			close();
		}
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public List getAllLoginsFromOfficeExcept(long office_id, List<Long> list) throws Exception {
		try {
			begin();
			String cdn="";
			if(list!=null && list.size()>0){
				cdn+=" and loginId.id not in"+list.toString().replace('[', '(').replace(']', ')');
			}
			resultList = getSession().createQuery("select new com.webspark.model.S_LoginModel(loginId.id, concat(first_name,' ', middle_name,' ', last_name,' [ ', employ_code ,' ]') ) from UserModel "
							+ "where office.id=:ofc and loginId is not null and loginId.status=0 and status=:ustat and user_role.id!=1" +cdn+" order by first_name")
							.setLong("ofc", office_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
	
	@SuppressWarnings("rawtypes")
    public List getUsersFromOffice(long office) throws Exception {
            List list=new ArrayList();
            try {
                    begin();
                    list = getSession().createQuery("from UserModel where office.id=:office and user_role.id!=1 and status=:ustat order by first_name")
                                    .setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
            return list;
    }
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List getUsersFromOffice(long office, Date start, Date end) throws Exception {
        List list=new ArrayList();
        try {
            begin();
            list = getSession().createQuery("from UserModel where office.id=:office and user_role.id!=1 and status=:ustat order by first_name")
                            .setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
            list.addAll(getSession().createQuery("select user from EmployeeStatusModel where date between :start and :end")
            			.setParameter("start", start).setParameter("end", end).list());
            list.addAll(getSession().createQuery("select user from EmployeeStatusModel where date > :end")
            			.setParameter("end", end).list());
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
        return list;
    }
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getUsersFromOffice(long office, long id,  Date start, Date end) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="",cdn1="";
			if(id!=0){
				cdn+=" and id="+id;
				cdn1+=" and user.id="+id;
			}
			list = getSession().createQuery("from UserModel where office.id=:office and user_role.id!=1 and status=:ustat "+cdn+" order by first_name")
								.setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
			 list.addAll(getSession().createQuery("select user from EmployeeStatusModel where date between :start and :end"+cdn1)
         							.setParameter("start", start).setParameter("end", end).list());
         list.addAll(getSession().createQuery("select user from EmployeeStatusModel where date > :end"+cdn1)
     							.setParameter("end", end).list());
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
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersFromOffice(long office, long id) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			String cdn="";
			if(id!=0){
				cdn+=" and id="+id;
			}
			list = getSession().createQuery("from UserModel where office.id=:office and user_role.id!=1 and status=:ustat "+cdn+" order by first_name")
					.setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersFromOrganization(long orgId) throws Exception {
		List list=new ArrayList();
		try {
			begin();
			list = getSession().createQuery("from UserModel where office.organization.id=:org and user_role.id!=1 and status=:ustat order by first_name")
					.setLong("org", orgId).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
		return list;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeFromOrgExcept(long org_id, UserModel usr) throws Exception {
		try {
			begin();
			if(usr.getLoginId()!=null){
				resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
						"from UserModel where office.organization.id=:org and loginId is not null and status=:ustat and user_role.id!=1 and " +
						" id!=:id order by first_name").setParameter("id", usr.getId())
						.setLong("org", org_id).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
			}
			else{
				resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
						"from UserModel where office.organization.id=:org and status=:ustat and user_role.id!=1 and id!=:id order by first_name")
						.setLong("org", org_id).setParameter("id", usr.getId()).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
	
	@SuppressWarnings("rawtypes")
	public List getUsersWithFullNameAndCodeUnderOfficeExcept(long office,  UserModel usr) throws Exception {
		try {
			begin();
			if(usr.getLoginId()!=null){
				resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
						"from UserModel where office.id=:office and loginId is not null and status=:ustat and user_role.id!=1 and " +
						" id!=:id order by first_name").setParameter("id", usr.getId())
						.setLong("office", office).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
			}
			else{
				resultList = getSession().createQuery("select new com.webspark.uac.model.UserModel(id, concat(first_name,' ', middle_name,' ', last_name,'  ( ', employ_code ,' ) ')) " +
						"from UserModel where office.id=:office and status=:ustat and user_role.id!=1 and id!=:id order by first_name")
						.setLong("office", office).setParameter("id", usr.getId()).setParameter("ustat", SConstants.EmployeeStatus.ACTIVE).list();
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
	
}
