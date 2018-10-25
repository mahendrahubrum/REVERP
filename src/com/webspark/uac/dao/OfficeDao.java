package com.webspark.uac.dao;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.hotel.service.model.HotelSalesModel;
import com.hotel.service.model.ProductionModel;
import com.inventory.budget.model.BudgetLVMasterModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.commissionsales.model.CommissionSalesNewModel;
import com.inventory.commissionsales.model.CustomerCommissionSalesModel;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.CashAccountDepositModel;
import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.inventory.config.acct.model.ChequeReturnModel;
import com.inventory.config.acct.model.CreditNoteModel;
import com.inventory.config.acct.model.DebitNoteModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PdcModel;
import com.inventory.config.acct.model.PdcPaymentModel;
import com.inventory.config.settings.model.AccountSettingsModel;
import com.inventory.config.settings.model.SettingsModel;
import com.inventory.config.stock.model.DailyQuotationModel;
import com.inventory.config.stock.model.ItemComboModel;
import com.inventory.config.stock.model.ItemDailyRateModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ItemReceiveModel;
import com.inventory.config.stock.model.ItemTransferModel;
import com.inventory.config.stock.model.ManualTradingMasterModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.stock.model.SalesTypeModel;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.config.stock.model.SupplierQuotationModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.expenditureposting.model.BatchExpenditurePaymentMasterModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel;
import com.inventory.finance.model.FinancePaymentModel;
import com.inventory.fixedasset.model.FixedAssetDepreciationMainModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.fixedasset.model.FixedAssetSalesModel;
import com.inventory.journal.model.JournalModel;
import com.inventory.management.model.TasksModel;
import com.inventory.model.DocumentAccessModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.process.model.FinancialYearsModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.ProformaPurchaseModel;
import com.inventory.purchase.model.PurchaseGRNModel;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.inventory.purchase.model.PurchaseQuotationModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.model.StockCreateModel;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.sales.model.QuotationModel;
import com.inventory.sales.model.SalesInquiryModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.sales.model.TailoringSalesModel;
import com.inventory.tailoring.model.MaterialMappingModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.VaadinServlet;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.BillModel;
import com.webspark.uac.model.EmployeeDocumentModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @Author Jinshad P.T.
 */
@SuppressWarnings("rawtypes")
public class OfficeDao extends SHibernate implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4512208559869363319L;
	
	private List resultList=new ArrayList();

	
	public List getOffices() throws Exception {
		resultList=null;
		try {
			begin();
			resultList = getSession().createCriteria(S_OfficeModel.class)
					.add(Restrictions.eq("active", 'Y')).list();
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
	
	
	public List getAllOfficeNames() throws Exception {
		try {
			resultList=null;
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, name)" +
					" from S_OfficeModel where active='Y'").list();
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
	
	
	public List getAllOfficeNamesUnderOrg(long organization_id) throws Exception {
		try {
			resultList=null;
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, name)" +
					" from S_OfficeModel where active='Y' and organization.id=:org  order by name")
					.setLong("org", organization_id).list();
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
	
	
	public List getAllOfficeName() throws Exception {
		try {
			resultList=null;
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, concat(name,' ( ',organization.name,' )'))" +
					" from S_OfficeModel where active='Y'")
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
	
	
	public List getAllOfficeNamesOfUserMaped(long loginId, long ofc_id, long org_id) throws Exception {
		try {
			resultList=null;
			begin();
			
			List<Long> lst=new ArrayList();
			lst.add(ofc_id);
			lst.addAll(getSession().createQuery("select office_id from OfficeAllocationModel where login_id=:log")
					.setLong("log", loginId).list());
			
			
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, name)" +
					" from S_OfficeModel where active='Y' and id in (:ids) and organization.id=:org")
					.setLong("org", org_id).setParameterList("ids", lst).list();
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
	
	
	public List getAllOrganizationNamesOfUserMaped(long loginId, long ofc_id) throws Exception {
		try {
			begin();
			
			List<Long> lst=new ArrayList();
			lst.add(ofc_id);
			lst.addAll(getSession().createQuery("select office_id from OfficeAllocationModel where login_id=:log")
					.setLong("log", loginId).list());
			
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id, name) from S_OrganizationModel where id in (select distinct organization.id " +
					" from S_OfficeModel where active='Y' and id in (:ids))")
					.setParameterList("ids", lst).list();
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
	
	
	public List getAllOfficesUnderOrg(long organization_id) throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("from S_OfficeModel where active='Y' and organization.id=:org")
					.setLong("org", organization_id).list();
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
	
	
	public List getAllOfficeNamesUnderAllOrg() throws Exception {
		try {
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, name)" +
					" from S_OfficeModel where active='Y'")
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
			
		}
		return resultList;
	}
	
	
	public S_OfficeModel save(S_OfficeModel obj,long currentOffice) throws Exception {
		try {
			begin();
			getSession().save(obj.getAddress());
			getSession().save(obj);
			
			
			FinancialYearsModel fin= new FinancialYearsModel();
			fin.setEnd_date(obj.getFin_end_date());
			fin.setOffice_id(obj.getId());
			fin.setStart_date(obj.getFin_start_date());
			fin.setStatus(1);
			fin.setName(CommonUtil.formatSQLDateToDDMMMYYYY(fin.getStart_date())+" - "+
					CommonUtil.formatSQLDateToDDMMMYYYY(fin.getEnd_date()));
			
			getSession().save(fin);
			
			List saleTypelist=getSession().createQuery("from SalesTypeModel where office.id=:ofc").setParameter("ofc", currentOffice).list();
			Iterator saleTypeIter=saleTypelist.iterator();
			SalesTypeModel saleTypeMdl;
			SalesTypeModel oldSaleTypeMdl;
			while (saleTypeIter.hasNext()) {
				oldSaleTypeMdl= (SalesTypeModel) saleTypeIter.next();
				saleTypeMdl=new SalesTypeModel();
				saleTypeMdl.setStatus(oldSaleTypeMdl.getStatus());
				saleTypeMdl.setName(oldSaleTypeMdl.getName());
				saleTypeMdl.setOffice(obj);
				getSession().save(saleTypeMdl);
				flush();
			}
			
			BillModel newModel=null;
			BillModel bill=null;
			List nameList=getSession().createQuery("from BillModel where office.id=:ofc").setParameter("ofc", currentOffice).list();
			if(nameList!=null){
				Iterator itr=nameList.iterator();
				while (itr.hasNext()) {
					bill= (BillModel) itr.next();
					newModel=new BillModel();
					newModel.setOffice(obj);
					newModel.setType(bill.getType());
					newModel.setBill_name(bill.getBill_name());
					getSession().save(newModel);
				}
			}
			
			List taxList=getSession().createQuery("from TaxModel where office.id=:ofc order by tax_type").setParameter("ofc", currentOffice).list();
			Iterator taxIter=taxList.iterator();
			TaxModel taxMdl;
			TaxModel oldTaxMdl;
			long oldType=0;
			while (taxIter.hasNext()) {
				oldTaxMdl=(TaxModel) taxIter.next();
				if(oldType!=oldTaxMdl.getTax_type()){
					taxMdl=new TaxModel();
					taxMdl.setName(oldTaxMdl.getName());
					taxMdl.setOffice(obj);
					taxMdl.setStatus(oldTaxMdl.getStatus());
					taxMdl.setTax_type(oldTaxMdl.getTax_type());
					taxMdl.setValue(1);
					taxMdl.setValue_type(oldTaxMdl.getValue_type());
					oldType=oldTaxMdl.getTax_type();
					getSession().save(taxMdl);
				}
			}
			
			
			flush();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj;
	}
	
	
	public void update(S_OfficeModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj.getAddress());
			getSession().update(obj);
			
			getSession().createQuery("delete from FinancialYearsModel where start_date=:strt and end_date=:end and office_id=:ofc")
							.setLong("ofc", obj.getId()).setParameter("strt", obj.getFin_start_date()).setParameter("end",  obj.getFin_end_date()).executeUpdate();
			
			FinancialYearsModel fin=new FinancialYearsModel();
//			if(objId!=null) {
//				fin=(FinancialYearsModel) getSession().get(FinancialYearsModel.class, (Long) objId);
//			}
			
			fin.setEnd_date(obj.getFin_end_date());
			fin.setOffice_id(obj.getId());
			fin.setStart_date(obj.getFin_start_date());
			fin.setStatus(1);
			fin.setName(CommonUtil.formatSQLDateToDDMMMYYYY(fin.getStart_date())+" - "+
					CommonUtil.formatSQLDateToDDMMMYYYY(fin.getEnd_date()));
			
			getSession().saveOrUpdate(fin);
			
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void delete(long id, boolean isDeletable) throws Exception {
		try {
			
			begin();
			S_OfficeModel office=(S_OfficeModel) getSession().get(S_OfficeModel.class, id);
			List deleteList=new ArrayList();
			List addressList=new ArrayList();
			Iterator itr=null;
			
			// Sales Return Model
			deleteList=getSession().createQuery("from SalesReturnModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SalesReturnModel mdl = (SalesReturnModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Sales Model
			deleteList=getSession().createQuery("from SalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SalesModel mdl = (SalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Delivery Note Model
			deleteList=getSession().createQuery("from DeliveryNoteModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				DeliveryNoteModel mdl = (DeliveryNoteModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Sales Order Model
			deleteList=getSession().createQuery("from SalesOrderModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SalesOrderModel mdl = (SalesOrderModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Sales Quotaion Model
			deleteList=getSession().createQuery("from QuotationModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				QuotationModel mdl = (QuotationModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Sales Inquiry Model
			deleteList=getSession().createQuery("from SalesInquiryModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SalesInquiryModel mdl = (SalesInquiryModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Laundry Sales Model
			deleteList=getSession().createQuery("from LaundrySalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				LaundrySalesModel mdl = (LaundrySalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Tailoring Sales Model
			deleteList=getSession().createQuery("from TailoringSalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				TailoringSalesModel mdl = (TailoringSalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Customer Commission Sales Model
			deleteList=getSession().createQuery("from CustomerCommissionSalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CustomerCommissionSalesModel mdl = (CustomerCommissionSalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Commission Sales New Model
			deleteList=getSession().createQuery("from CommissionSalesNewModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CommissionSalesNewModel mdl = (CommissionSalesNewModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Hotel Sales Model
			deleteList=getSession().createQuery("from HotelSalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				HotelSalesModel mdl = (HotelSalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Purchase Return Model
			deleteList=getSession().createQuery("from PurchaseReturnModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseReturnModel mdl = (PurchaseReturnModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Proforma Purchase Model
			deleteList=getSession().createQuery("from ProformaPurchaseModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ProformaPurchaseModel mdl = (ProformaPurchaseModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Purchase Model
			deleteList=getSession().createQuery("from PurchaseModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseModel mdl = (PurchaseModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Purchase GRN Model
			deleteList=getSession().createQuery("from PurchaseGRNModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseGRNModel mdl = (PurchaseGRNModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Purchase Order Model
			deleteList=getSession().createQuery("from PurchaseOrderModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseOrderModel mdl = (PurchaseOrderModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Purchase Quotaion Model
			deleteList=getSession().createQuery("from PurchaseQuotationModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseQuotationModel mdl = (PurchaseQuotationModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Purchase Inquiry Model
			deleteList=getSession().createQuery("from PurchaseInquiryModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PurchaseInquiryModel mdl = (PurchaseInquiryModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Expenditure Payment Setup Model
			deleteList=getSession().createQuery("from ExpenditurePaymentSetupModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ExpenditurePaymentSetupModel mdl = (ExpenditurePaymentSetupModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Batch Expenditure Payment Model
			deleteList=getSession().createQuery("from BatchExpenditurePaymentMasterModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				BatchExpenditurePaymentMasterModel mdl = (BatchExpenditurePaymentMasterModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Commission Purchase Model
			deleteList=getSession().createQuery("from CommissionPurchaseModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CommissionPurchaseModel mdl = (CommissionPurchaseModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Fixed Asset Depreciation Model
			deleteList=getSession().createQuery("from FixedAssetDepreciationMainModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				FixedAssetDepreciationMainModel mdl = (FixedAssetDepreciationMainModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Fixed Asset Sales Model
			deleteList=getSession().createQuery("from FixedAssetSalesModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				FixedAssetSalesModel mdl = (FixedAssetSalesModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Fixed Asset Purchase Model
			deleteList=getSession().createQuery("from FixedAssetPurchaseModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				FixedAssetPurchaseModel mdl = (FixedAssetPurchaseModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Production Model
			deleteList=getSession().createQuery("from ProductionModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ProductionModel mdl = (ProductionModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Customer Booking Model
			deleteList=getSession().createQuery("select id from CustomerBookingModel where tableNo.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from CustomerBookingModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Table Model
			getSession().createQuery("delete from TableModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Sales Man Commisison Map Model
			getSession().createQuery("delete from SalesManCommissionMapModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Fixed Asset Model
			getSession().createQuery("delete from FixedAssetModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Leave Map Model
			getSession().createQuery("delete from UserLeaveMapModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Leave Date Model
			getSession().createQuery("delete from LeaveDateModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Leave History Model
			deleteList=getSession().createQuery("select id from LeaveHistoryModel where login.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from LeaveHistoryModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Loan Date Model
			getSession().createQuery("delete from LoanDateModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Loan Approval Model
			deleteList=getSession().createQuery("from LoanApprovalModel where loanRequest.user.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from LoanApprovalModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Loan Request Model
			deleteList=getSession().createQuery("from LoanRequestModel where user.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from LoanRequestModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Leave Model
			deleteList=getSession().createQuery("select id from LeaveModel where user.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from LeaveModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// User Leave Allocation Model
			deleteList=getSession().createQuery("select id from UserLeaveAllocationModel where user.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from UserLeaveAllocationModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Role Leave Map Model
			getSession().createQuery("delete from RoleLeaveMapModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Leave Type Model
			getSession().createQuery("delete from LeaveTypeModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Holiday Model
			getSession().createQuery("delete from HolidayModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Bank Reconsilation Model
			deleteList=getSession().createQuery("select id from BankRecociliationModel where login.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from BankRecociliationModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// PDC Payment Model
			deleteList=getSession().createQuery("from PdcPaymentModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PdcPaymentModel mdl = (PdcPaymentModel) itr.next();
				getSession().delete(mdl);
			}
			
			// PDC Model
			deleteList=getSession().createQuery("from PdcModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				PdcModel mdl = (PdcModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Cheque Return Model
			deleteList=getSession().createQuery("from ChequeReturnModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ChequeReturnModel mdl = (ChequeReturnModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Debit Note Model
			deleteList=getSession().createQuery("from DebitNoteModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				DebitNoteModel mdl = (DebitNoteModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Credit Note Model
			deleteList=getSession().createQuery("from CreditNoteModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CreditNoteModel mdl = (CreditNoteModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Debit Credit invoice Map Model
			getSession().createQuery("delete from DebitCreditInvoiceMapModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// bank Details Invoice Map Model
			getSession().createQuery("delete from BankDetailsInvoiceMapModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Batch Model
			getSession().createQuery("delete from BatchModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Clearing Agent Model
			addressList=getSession().createQuery("select address.id from ClearingAgentModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			deleteList=getSession().createQuery("select id from ClearingAgentModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from ClearingAgentModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();			
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();
			
			// Document Access Model
			deleteList=getSession().createQuery("from DocumentAccessModel where creator.office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				DocumentAccessModel mdl = (DocumentAccessModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Location Model
			getSession().createQuery("delete from LocationModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Contact Model
			getSession().createQuery("delete from UserContactModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Family Contact Model
			getSession().createQuery("delete from UserFamilyContactModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Previous Employer Model
			getSession().createQuery("delete from UserPreviousEmployerModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Visa Type Model
			getSession().createQuery("delete from VisaTypeModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Qualification Model
			getSession().createQuery("delete from UserQualificationModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Qualification Model
			getSession().createQuery("delete from QualificationModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Size Model
			getSession().createQuery("delete from SizeModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Colour Model
			getSession().createQuery("delete from ColourModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Item Model Model
			getSession().createQuery("delete from ItemModelModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Style Model
			getSession().createQuery("delete from StyleModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Container Model
			getSession().createQuery("delete from ContainerModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Commission Salary Model
			getSession().createQuery("delete from CommissionSalaryModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Material Mapping Model
			deleteList=getSession().createQuery("from MaterialMappingModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				MaterialMappingModel mdl = (MaterialMappingModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Office Option Mapping Model
			getSession().createQuery("delete from OfficeOptionMappingModel where officeId.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Item Physical Stock Model
			getSession().createQuery("delete from ItemPhysicalStockModel where office=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Production Unit Model
			getSession().createQuery("delete from ProductionUnitModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Employee Document Model
			deleteList=getSession().createQuery("from EmployeeDocumentModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
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
			
			// Commission Stock Model
			deleteList=getSession().createQuery("select id from CommissionStockModel where item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from CommissionStockModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Commission Payment Model
			getSession().createQuery("delete from CommissionPaymentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Survey Model
			getSession().createQuery("delete from SurveyModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Session Activity Model
			getSession().createQuery("delete from SessionActivityModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Report Issue Model
			getSession().createQuery("delete from ReportIssueModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Review Model
			getSession().createQuery("delete from ReviewModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Payment Invoice Map Model
			getSession().createQuery("delete from PaymentInvoiceMapModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Stock Create Model
			deleteList=getSession().createQuery("from StockCreateModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				StockCreateModel mdl = (StockCreateModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Item Combo Model
			deleteList=getSession().createQuery("from ItemComboModel where item.office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ItemComboModel mdl = (ItemComboModel) itr.next();
				getSession().delete(mdl);
			}

			// Budget Master Model
			deleteList=getSession().createQuery("from BudgetLVMasterModel where office_id.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				BudgetLVMasterModel mdl = (BudgetLVMasterModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Budget Model
			getSession().createQuery("delete from BudgetModel where office_id.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Budget Definition Model
			getSession().createQuery("delete from BudgetDefinitionModel where office_id.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Grade Model
			getSession().createQuery("delete from GradeModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Proposal Sent To Customer Model
			getSession().createQuery("delete from ProposalsSentToCustomersModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Supplier Proposal Reception Model
			getSession().createQuery("delete from SupplierProposalReceiptionModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Supplier Quotation Request Model
			getSession().createQuery("delete from SupplierQuotationRequestModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Customer Enquiry Model
			getSession().createQuery("delete from CustomerEnquiryModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
						
			// Item Daily Rate Model
			deleteList=getSession().createQuery("from ManufacturingModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ManufacturingModel mdl = (ManufacturingModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Manufacturing Map Model
			deleteList=getSession().createQuery("select id from ManufacturingMapModel where item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from ManufacturingMapModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Commission Sales Model
			getSession().createQuery("delete from CommissionSalesModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Sales Man Map Model
			getSession().createQuery("delete from SalesManMapModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();			
			
			// Stock Reset Model
			deleteList=getSession().createQuery("select id from StockResetDetailsModel where item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from StockResetDetailsModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Cash Investment Model
			getSession().createQuery("delete from CashInvestmentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Contact Model
			deleteList=getSession().createQuery("select id from ContactModel where login.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from ContactModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Item Daily Rate Model
			deleteList=getSession().createQuery("from ItemDailyRateModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ItemDailyRateModel mdl = (ItemDailyRateModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Supplier Quotation Model
			deleteList=getSession().createQuery("from SupplierQuotationModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SupplierQuotationModel mdl = (SupplierQuotationModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Office Allocation Model
			getSession().createQuery("delete from OfficeAllocationModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Manual Trading Model
			deleteList=getSession().createQuery("from ManualTradingMasterModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ManualTradingMasterModel mdl = (ManualTradingMasterModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Employee Advance Model
			getSession().createQuery("delete from EmployeeAdvancePaymentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Privilage Setup Model
			getSession().createQuery("delete from PrivilageSetupModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Transportation Payment Model
			getSession().createQuery("delete from TransportationPaymentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Transportation Model
			addressList=getSession().createQuery("select address.id from TranspotationModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			deleteList=getSession().createQuery("select id from TranspotationModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from TranspotationModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();			
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();
			
			// Office Bill Mapping Model
			getSession().createQuery("delete from BillModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Activity Log Model
			getSession().createQuery("delete from ActivityLogModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Employee Working Time Model
			deleteList=getSession().createQuery("select id from EmployeeWorkingTimeModel where employee.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from EmployeeWorkingTimeModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Contractor Model
			addressList=getSession().createQuery("select address.id from ContractorModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			deleteList=getSession().createQuery("select id from ContractorModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from ContractorModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();			
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();
			
			flush();
			
			// Item Transfer Model
			deleteList=getSession().createQuery("from ItemTransferModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ItemTransferModel mdl = (ItemTransferModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Item Receive Model
			deleteList=getSession().createQuery("from ItemReceiveModel where item.office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				ItemReceiveModel mdl = (ItemReceiveModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Payment Deposit Model
			getSession().createQuery("delete from PaymentDepositModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Financial Year Model
			getSession().createQuery("delete from FinancialYearsModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// OverTime Model
			getSession().createQuery("delete from ItemClosingStockModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Item Unit Management Model
			deleteList=getSession().createQuery("select id from ItemUnitMangementModel where item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from ItemUnitMangementModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// OverTime Model
			getSession().createQuery("delete from OverTimeModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Attendance Model
			getSession().createQuery("delete from AttendanceModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Salary Disbursal Model
			deleteList=getSession().createQuery("from SalaryDisbursalModel where officeId=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				SalaryDisbursalModel mdl = (SalaryDisbursalModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Payroll Employee Map Model
			deleteList=getSession().createQuery("select id from PayrollEmployeeMapModel where employee.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from PayrollEmployeeMapModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Payroll Component Model
			getSession().createQuery("delete from PayrollComponentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Bank Account Payment Model
			deleteList=getSession().createQuery("from BankAccountPaymentModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				BankAccountPaymentModel mdl = (BankAccountPaymentModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Bank Account Deposit Model
			deleteList=getSession().createQuery("from BankAccountDepositModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				BankAccountDepositModel mdl = (BankAccountDepositModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Cash Account Payment Model
			deleteList=getSession().createQuery("from CashAccountPaymentModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CashAccountPaymentModel mdl = (CashAccountPaymentModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Cash Account Deposit Model
			deleteList=getSession().createQuery("from CashAccountDepositModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				CashAccountDepositModel mdl = (CashAccountDepositModel) itr.next();
				getSession().delete(mdl);
			}
			
			flush();
			
			// Work Order Model
			getSession().createQuery("delete from WorkOrderModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Bank Account Model
			deleteList=getSession().createQuery("select id from BankAccountModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from BankAccountModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();			
			
			// Customer Model
			addressList=getSession().createQuery("select address.id from CustomerModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			deleteList=getSession().createQuery("select id from CustomerModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0){
				getSession().createQuery("delete from CustomerModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			}
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();			

			// Supplier Model
			addressList=getSession().createQuery("select address.id from SupplierModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			deleteList=getSession().createQuery("select id from SupplierModel where ledger.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from SupplierModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();			
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();			
			
			flush();
			
			// Payment Model
			getSession().createQuery("delete from PaymentModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Customer Group Model
			getSession().createQuery("delete from CustomerGroupModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Journal Model
			deleteList=getSession().createQuery("from JournalModel where office_id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				JournalModel mdl = (JournalModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Account Settings Model
			getSession().createQuery("delete from AccountSettingsModel where office_id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Transaction Model
			deleteList=getSession().createQuery("from TransactionModel where office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				TransactionModel mdl = (TransactionModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Stock Rack Mapping Model
			deleteList=getSession().createQuery("select id from TransferStockMap where stock.item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from TransferStockMap where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Stock Transfer Model
			deleteList=getSession().createQuery("from StockTransferModel where from_office.id=:office or to_office.id=:office").setParameter("office", office.getId()).list();
			itr=deleteList.iterator();
			while (itr.hasNext()) {
				StockTransferModel mdl = (StockTransferModel) itr.next();
				getSession().delete(mdl);
			}
			
			// Stock Rack Mapping Model
			deleteList=getSession().createQuery("select id from StockRackMappingModel where stock.item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from StockRackMappingModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Item Stock Model
			deleteList=getSession().createQuery("select id from ItemStockModel where item.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0){
				getSession().createQuery("delete from SalesStockMapModel where stockId in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from StockRateModel where stock_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from ManufacturingStockMap where stock_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from ItemStockModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			}
				
			// Item Model
			getSession().createQuery("delete from ItemModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Sales Type Model
			getSession().createQuery("delete from SalesTypeModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Tax Model
			getSession().createQuery("delete from TaxModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Rack Model
			deleteList=getSession().createQuery("select id from RackModel where room.building.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from RackModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();

			// Finance Component Model
			getSession().createQuery("delete from FinanceComponentModel where officeId=:office").setParameter("office", office.getId()).executeUpdate();
			
			// Room Model
			deleteList=getSession().createQuery("select id from RoomModel where building.office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from RoomModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();
			
			// Building Model
			deleteList=getSession().createQuery("select id from BuildingModel where office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0)
				getSession().createQuery("delete from BuildingModel where id in (:list)").setParameterList("list", deleteList).executeUpdate();

			flush();
			
			// Ledger Model
			deleteList=getSession().createQuery("select id from LedgerModel where office.id=:office").setParameter("office", office.getId()).list();
			if(deleteList.size()>0){
				getSession().createQuery("delete from ItemCustomerBarcodeMapModel where customerId in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from LedgerOpeningBalanceModel where ledger.id in (:list)").setParameterList("list", deleteList).executeUpdate();
			}
			getSession().createQuery("delete from LedgerModel where office.id=:office").setParameter("office", office.getId()).executeUpdate();
			
			// User Model
			deleteList=getSession().createQuery("select id from UserModel where office.id=:office and user_role.id!=1").setParameter("office", office.getId()).list();
			addressList=getSession().createQuery("select address.id from UserModel where office.id=:office and user_role.id!=1 and address is not null").setParameter("office", office.getId()).list();
			addressList.addAll(getSession().createQuery("select work_address.id from UserModel where office.id=:office and user_role.id!=1 and work_address is not null").setParameter("office", office.getId()).list());
			addressList.addAll(getSession().createQuery("select local_address.id from UserModel where office.id=:office and user_role.id!=1 and local_address is not null").setParameter("office", office.getId()).list());
			if(deleteList.size()>0){
				getSession().createQuery("delete from EmailConfigurationModel where user_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from SalaryBalanceMapModel where used_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from MyMailsModel where user_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from EmployeeStatusModel where user.id in (:list)").setParameterList("list", deleteList).executeUpdate();
			}
			getSession().createQuery("delete from UserModel where office.id=:office and user_role.id!=1").setParameter("office", office.getId()).executeUpdate();
			if(addressList.size()>0)
				getSession().createQuery("delete from AddressModel where id in (:list)").setParameterList("list", addressList).executeUpdate();
			
			// Login Model
			deleteList=getSession().createQuery("select id from S_LoginModel where office.id=:office and userType.id!=1").setLong("office", office.getId()).list();
			if(deleteList.size()>0){
				getSession().createQuery("delete from S_LoginHistoryModel where login_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from MailModel where send_by in (:list)").setParameterList("list", deleteList).executeUpdate();
				getSession().createQuery("delete from QuickMenuModel where login_id in (:list)").setParameterList("list", deleteList).executeUpdate();
				
				addressList=getSession().createQuery("from DailyQuotationModel where login.id in (:list)").setParameterList("list", deleteList).list();
				itr=addressList.iterator();
				while (itr.hasNext()) {
					DailyQuotationModel object = (DailyQuotationModel) itr.next();
					getSession().delete(object);
				}
				
				addressList=getSession().createQuery("from TasksModel where created_by.id in (:list)").setParameterList("list", deleteList).list();
				itr=addressList.iterator();
				while (itr.hasNext()) {
					TasksModel object = (TasksModel) itr.next();
					getSession().delete(object);
				}
				
				addressList=getSession().createQuery("from FinancePaymentModel where login_id in (:list)").setParameterList("list", deleteList).list();
				itr=addressList.iterator();
				while (itr.hasNext()) {
					FinancePaymentModel object = (FinancePaymentModel) itr.next();
					getSession().delete(object);
				}
				
			}
			getSession().createQuery("delete from S_LoginModel where office.id=:office and userType.id!=1").setParameter("office", office.getId()).executeUpdate();
			
			if(isDeletable)
				getSession().delete(office);
			flush();
			commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} 
		finally{
			flush();
			close();
		}
	}
	
	
	
	public long getFirstOffice(long id)throws Exception{
		long oid=0;
		try{
			begin();
			Object obj=getSession().createQuery("select min(id) from S_OfficeModel where organization.id="+id).uniqueResult();
			if(obj!=null)
				oid=(Long)obj;
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
			
		}
		return oid;
	}
	
	
	
	public S_OfficeModel getOffice(long id) throws Exception {
		S_OfficeModel of=null;
		try {
			begin();
			of=(S_OfficeModel) getSession().get(S_OfficeModel.class, id);
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
	
	
	
	public String getOfficeName(long id) throws Exception {
		String name="";
		try {
			begin();
			Object obj= getSession().createQuery("select name from S_OfficeModel where id=:id")
					.setLong("id", id).uniqueResult();
			if(obj!=null)
				name=(String) obj;
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
	
	
	
	public void updateWorkingDate(long id, Date date) throws Exception {
		try {
			begin();
			getSession().createQuery("update S_OfficeModel set workingDate=:dt where id=:id")
					.setLong("id", id).setParameter("dt", date).executeUpdate();
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
	
	
	
	public void updateFinancialYear(long id, Date st_dt, Date enddt) throws Exception {
		try {
			begin();
			Object obj= getSession().createQuery("update S_OfficeModel set fin_start_date=:stdt, " +
					"fin_end_date=:enddt where id=:id").setLong("id", id)
					.setParameter("stdt", st_dt).setParameter("enddt", enddt).executeUpdate();
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
	
	
	
	public void createItems(long officeId, long newOfficeId) throws Exception {
		try {
			begin();
			List itemList=new ArrayList();
			itemList = getSession().createQuery("from ItemModel where office.id=:ofc").setParameter("ofc", officeId).list();		
			Iterator itmIter=itemList.iterator();
			ItemModel item;
			ItemModel oldtem;
			while (itmIter.hasNext()) {
				oldtem= (ItemModel) itmIter.next();
				item=new ItemModel();
				item.setItem_code(oldtem.getItem_code());
				item.setSupplier_code(oldtem.getSupplier_code());
				item.setName(oldtem.getName());
				item.setSub_group(oldtem.getSub_group());
				item.setOpening_balance(oldtem.getOpening_balance());
				item.setCurrent_balalnce(0);
				item.setReorder_level(oldtem.getReorder_level());
				item.setMinimum_level(oldtem.getMinimum_level());
				item.setMaximum_level(oldtem.getMaximum_level());
				
				item.setSalesTax((TaxModel) getSession().createQuery("from TaxModel where tax_type=:typ and office.id=:ofc")
						.setParameter("typ", SConstants.tax.SALES_TAX)
						.setParameter("ofc", newOfficeId).list().get(0));
				item.setPurchaseTax((TaxModel) getSession().createQuery("from TaxModel where tax_type=:typ and office.id=:ofc")
						.setParameter("typ", SConstants.tax.PURCHASE_TAX)
						.setParameter("ofc", newOfficeId).list().get(0));
					
				item.setUnit(oldtem.getUnit());
				item.setStatus(oldtem.getStatus());
				item.setOpening_stock_date(oldtem.getOpening_stock_date());
				item.setAffect_type(oldtem.getAffect_type());
				item.setRate(oldtem.getRate());
				item.setSale_rate(oldtem.getSale_rate());
				item.setDiscount(oldtem.getDiscount());
				item.setMax_discount(oldtem.getMax_discount());
				item.setItem_model(oldtem.getItem_model());
				item.setColour(oldtem.getColour());
				item.setSize(oldtem.getSize());
				item.setStyle(oldtem.getStyle());
				item.setBrand(oldtem.getBrand());
				item.setPreferred_vendor("");
				item.setSpecification(oldtem.getSpecification());
				item.setDesciption(oldtem.getDesciption());
				item.setIcon(oldtem.getIcon());
				item.setOffice(new S_OfficeModel(newOfficeId));
				item.setCess_enabled('N');
				item.setReservedQuantity(0);
				item.setParentId(oldtem.getParentId());
				getSession().save(item);
				flush();
				
				ItemStockModel stk=new ItemStockModel();
				stk.setBalance(item.getOpening_balance());
				stk.setExpiry_date(item.getOpening_stock_date());
				stk.setItem(item);
				stk.setManufacturing_date(item.getOpening_stock_date());
				stk.setRate(item.getRate());
				stk.setPurchase_id(0);
				stk.setQuantity(item.getOpening_balance());
				stk.setStatus(2);
				stk.setDate_time(CommonUtil.getCurrentDateTime());
				stk.setBarcode("");
				stk.setItem_tag("");
				stk.setInv_det_id(0);
				stk.setGradeId(0);

				getSession().save(stk);
				flush();

				List lstST = getSession().createQuery("select id from SalesTypeModel where office.id=:ofc")
						.setLong("ofc", newOfficeId).list();
				
				ItemUnitMangementModel objIUM;
				Iterator it = lstST.iterator();
				while (it.hasNext()) {

					objIUM = new ItemUnitMangementModel(0,
							item, item.getUnit().getId(),
							item.getUnit().getId(), (Long) it.next(),
							1,
							item.getRate(),
							2);

					getSession().save(objIUM);
				}
				
				flush();
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
	
	
	
	public void createLedgers(long officeId, long newOfficeId) throws Exception {
		try {
			begin();
			List ledgerList=new ArrayList();
			ledgerList=getSession().createQuery("from LedgerModel where office.id =:ofc").setParameter("ofc", officeId).list();
			Iterator ledgItr=ledgerList.iterator();
			LedgerModel ledg;
			LedgerModel newLedg;
			while (ledgItr.hasNext()) {
				ledg = (LedgerModel) ledgItr.next();
				newLedg=new LedgerModel();
				newLedg.setCurrent_balance(0);
				newLedg.setGroup(ledg.getGroup());
				newLedg.setName(ledg.getName());
				newLedg.setOffice(new S_OfficeModel(newOfficeId));
				newLedg.setParentId(ledg.getParentId());
				newLedg.setStatus(ledg.getStatus());
				newLedg.setType(ledg.getType());
				getSession().save(newLedg);
				flush();
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
	
	
	
	public void saveSettings(long officeId,long newOfficeId) throws Exception{
		try {
			begin();
			
			List genList = getSession()
					.createQuery(
							"from SettingsModel where level=:level and level_id=:level_id")
					.setParameter("level_id", officeId)
					.setParameter("level",
							SConstants.scopes.OFFICE_LEVEL_GENERAL).list();			
			
			SettingsModel settMdl;
			SettingsModel newSettMdl;
			Iterator genIter=genList.iterator();
			while (genIter.hasNext()) {
				settMdl = (SettingsModel) genIter.next();
				newSettMdl=new SettingsModel();
				newSettMdl.setLevel(settMdl.getLevel());
				newSettMdl.setLevel_id(newOfficeId);
				newSettMdl.setSettings_name(settMdl.getSettings_name());
				newSettMdl.setValue(settMdl.getValue());
				getSession().save(newSettMdl);
				flush();
			}
			
			List list=getSession().createQuery("from AccountSettingsModel where office_id=:ofc").setParameter("ofc", officeId).list();
			if(list!=null&&list.size()>0){
				AccountSettingsModel mdl;
				AccountSettingsModel newMdl;
				Iterator iter=list.iterator();
				while (iter.hasNext()) {
					
					mdl = (AccountSettingsModel) iter.next();
					newMdl=new AccountSettingsModel();
					newMdl.setOffice_id(newOfficeId);
					newMdl.setSettings_name(mdl.getSettings_name());
					
					if(mdl.getType()==1){
						LedgerModel ledg=(LedgerModel) getSession().get(LedgerModel.class,Long.parseLong(mdl.getValue()));
						List listOb = getSession()
								.createQuery(
										"select id from LedgerModel where office.id=:ofc and parentId=:parent and group.id=:grp")
								.setParameter("ofc", newOfficeId).setParameter("grp", ledg.getGroup().getId())
								.setParameter("parent",ledg.getParentId()).list();
						
						if (listOb != null&&listOb.size()>0) {
							newMdl.setValue(listOb.get(0)+"");
							getSession().save(newMdl);
						}
					}else{
						if (mdl.getValue() != null) {
							newMdl.setValue(mdl.getValue());
							getSession().save(newMdl);
						}
					}
					
					flush();
				}
			}
		
		commit();
		}catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		
	}
	
	
	
	public void saveAccountSettings(long ofcID) throws Exception {
		try {
			begin();
			LedgerModel ledgerModel=new LedgerModel();
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(30));
			ledgerModel.setName("Inventory Account");
			
			getSession().save(ledgerModel);

			AccountSettingsModel objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.INVENTORY_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
//			ledgerModel = new LedgerModel();
//			ledgerModel.setCurrent_balance(0);
//			ledgerModel.setOffice(new S_OfficeModel(ofcID));
//			ledgerModel.setStatus(1);
//			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
//			ledgerModel.setGroup(new GroupModel(SConstants.CASH_GROUP));
//			ledgerModel.setName("Cash Book");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.CASH_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(3));
			ledgerModel.setName("Liability Account");
			getSession().save(ledgerModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(3));
			ledgerModel.setName("Employee Payment Account");

			getSession().save(ledgerModel);
			
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(10));
			ledgerModel.setName("Sales Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(10));
			ledgerModel.setName("Sales Return Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_RETURN_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
		
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(29));
			ledgerModel.setName("C.G.S Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.CGS_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(23));
			ledgerModel.setName("Sales Tax Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_TAX_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(15));
			ledgerModel.setName("Shipping Charge Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_SHIPPING_CHARGE_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.PURCHASE_SHIPPING_CHARGE_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(23));
			ledgerModel.setName("Cess Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.CESS_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(15));
			ledgerModel.setName("Discount Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_DESCOUNT_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			getSession().save(objModel);
			
//			objModel=new AccountSettingsModel();
//			objModel.setOffice_id(ofcID);
//			objModel.setSettings_name(SConstants.settings.PURCHASE_DESCOUNT_ACCOUNT);
//			objModel.setValue(ledgerModel.getId()+"");
//			getSession().save(objModel);
			
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(12));
			ledgerModel.setName("Sales Revenue Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.SALES_REVENUE_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(11));
			ledgerModel.setName("Purchase Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.PURCHASE_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(11));
			ledgerModel.setName("Purchase Return Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.PURCHASE_RETURN_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(23));
			ledgerModel.setName("Purchase Tax Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.PURCHASE_TAX_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
		
			getSession().save(objModel);
			
			ledgerModel = new LedgerModel();
			
			ledgerModel.setCurrent_balance(0);
			ledgerModel.setOffice(new S_OfficeModel(ofcID));
			
			ledgerModel.setStatus(1);
			ledgerModel.setType(SConstants.LEDGER_ADDED_DIRECTLY);
			ledgerModel.setGroup(new GroupModel(3));
			ledgerModel.setName("Cash Payable Account");
			
			getSession().save(ledgerModel);
			
			objModel=new AccountSettingsModel();
			objModel.setOffice_id(ofcID);
			objModel.setSettings_name(SConstants.settings.CASH_PAYABLE_ACCOUNT);
			objModel.setValue(ledgerModel.getId()+"");
			
			getSession().save(objModel);
			
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

	
	public boolean isDeletable(long officeId)  throws Exception {
		boolean deletable=true;
		try {
			begin();
			List list=new ArrayList();
			list=getSession().createQuery("select id from UserModel where user_role.id=1 and office.id="+officeId).list();
			if(list.size()>0)
				deletable=false;
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			deletable=false;
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return deletable;
	}
	
	public List getAllOfficeNamesUnderOrgExceptCurrent(long organization_id,long officeId) throws Exception {
		try {
			resultList=null;
			begin();
			resultList = getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id, name)" +
					" from S_OfficeModel where active='Y' and organization.id=:org and id!=:ofc order by name")
					.setLong("org", organization_id).setLong("ofc", officeId).list();
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
