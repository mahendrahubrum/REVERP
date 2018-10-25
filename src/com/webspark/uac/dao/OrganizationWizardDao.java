package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.model.SalesTypeModel;
import com.inventory.process.model.FinancialYearsModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;
import com.webspark.model.BillModel;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_LoginOptionMappingModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.model.DepartmentModel;
import com.webspark.uac.model.DesignationModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

public class OrganizationWizardDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6285710129153639681L;
	List resultList = new ArrayList();

	public UserModel save(S_OrganizationModel orgModel, DepartmentModel depModel, DesignationModel desModel,
			S_OfficeModel objOfcModel, UserModel userModel) throws Exception {
		
		try {
			

			begin();
			getSession().save(orgModel.getAddress());
			getSession().save(orgModel);
			
			depModel.setOrganization_id(orgModel.getId());
			desModel.setOrganization_id(orgModel.getId());
			
			getSession().save(depModel);
			getSession().save(desModel);
			
			
			
			// For Office
			
			objOfcModel.setOrganization(orgModel);
			
			getSession().save(objOfcModel.getAddress());
			getSession().save(objOfcModel);
			
			
			FinancialYearsModel fin= new FinancialYearsModel();
			fin.setEnd_date(objOfcModel.getFin_end_date());
			fin.setOffice_id(objOfcModel.getId());
			fin.setStart_date(objOfcModel.getFin_start_date());
			fin.setStatus(1);
			fin.setName(CommonUtil.formatSQLDateToDDMMMYYYY(fin.getStart_date())+" - "+
					CommonUtil.formatSQLDateToDDMMMYYYY(fin.getEnd_date()));
			
			getSession().save(fin);
			
			
			SalesTypeModel SAL_WHL=new SalesTypeModel();
			SAL_WHL.setStatus(1);
			SAL_WHL.setName("SAL_WHL");
			SAL_WHL.setOffice(objOfcModel);
			SalesTypeModel SAL_LOC=new SalesTypeModel();
			SAL_LOC.setStatus(1);
			SAL_LOC.setName("SAL_LOC");
			SAL_LOC.setOffice(objOfcModel);
			
			getSession().save(SAL_WHL);
			getSession().save(SAL_LOC);
			
			
			BillModel newModel=null;
			BillModel bill=null;
			List nameList=getSession().createQuery("from BillModel where office.id=(select max(office.id) from BillModel)").list();
			if(nameList!=null){
				Iterator itr=nameList.iterator();
				while (itr.hasNext()) {
					bill= (BillModel) itr.next();
					newModel=new BillModel();
					newModel.setOffice(objOfcModel);
					newModel.setType(bill.getType());
					newModel.setBill_name(bill.getBill_name());
					getSession().save(newModel);
				}
				
			}
			
			userModel.setDepartment(depModel);
			userModel.setDesignation(desModel);
			userModel.getLoginId().setOffice(objOfcModel);
			
			
			getSession().save(userModel.getLoginId());
			getSession().save(userModel.getAddress());
			getSession().save(userModel);
			
			depModel.setAdmin_user_id(userModel.getLoginId().getId());
			objOfcModel.setAdmin_user_id(userModel.getLoginId().getId());
			orgModel.setAdmin_user_id(userModel.getLoginId().getId());

			List<Long> objList = getSession()
					.createQuery(
							"select option_id.option_id from S_RoleOptionMappingModel where role_id.id=:RolId")
					.setParameter("RolId",
							userModel.getLoginId().getUserType().getId()).list();

			for (Long opt_id : objList) {
				S_LoginOptionMappingModel lomm = new S_LoginOptionMappingModel();

				lomm.setLogin_id(new S_LoginModel(userModel.getLoginId().getId()));
				lomm.setOption_id(new S_OptionModel(opt_id));
				lomm.setActive('Y');

				getSession().save(lomm);
			}
			
			
			
			
			
			
			
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return userModel;
		}
	}
	
	
	public S_OfficeModel saveOffice(S_OfficeModel obj) throws Exception {
		try {
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
			
			
			SalesTypeModel SAL_WHL=new SalesTypeModel();
			SAL_WHL.setStatus(1);
			SAL_WHL.setName("SAL_WHL");
			SAL_WHL.setOffice(obj);
			SalesTypeModel SAL_LOC=new SalesTypeModel();
			SAL_LOC.setStatus(1);
			SAL_LOC.setName("SAL_LOC");
			SAL_LOC.setOffice(obj);
			
			getSession().save(SAL_WHL);
			getSession().save(SAL_LOC);
			
			
			BillModel newModel=null;
			BillModel bill=null;
			List nameList=getSession().createQuery("from BillModel where office.id=(select max(office.id) from BillModel)").list();
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
			
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			throw e;
		} finally {
			flush();
			close();
			return obj;
		}
	}
	
	
}
