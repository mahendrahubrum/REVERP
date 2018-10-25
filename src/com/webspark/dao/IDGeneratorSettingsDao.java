package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_IDGeneratorSettingsModel;
import com.webspark.model.S_IDValueCompoundKey;
import com.webspark.model.S_IDValueModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

public class IDGeneratorSettingsDao extends SHibernate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4632077476267235772L;
	List resultList = new ArrayList();
	List orgnzns=new ArrayList();
	List offices=new ArrayList();
	S_IDValueModel idValue;
	S_IDValueCompoundKey compKey;

	public long saveIDGeneratorSettings(S_IDGeneratorSettingsModel obj) throws Exception {
		try {

			begin();
			getSession().save(obj);
			
			
			
			// Following for Creating Value
			
			switch(obj.getScope()){
				
				case SConstants.scopes.LOGIN_LEVEL:
					
					orgnzns = getSession()
							.createQuery(
									"select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel")
							.list();
	
					if (orgnzns.size() > 0) {
	
						S_OrganizationModel org;
						S_OfficeModel ofc;
						List logins;
						for (Object orgObj : orgnzns) {
	
							org = (S_OrganizationModel) orgObj;
	
							offices = getSession().createQuery(
											"select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
											.setParameter("org", org).list();
	
							if (offices.size() > 0) {
	
								for (Object ofcObj : offices) {
	
									ofc = (S_OfficeModel) ofcObj;
	
									logins = getSession().createQuery(
													"select new com.webspark.model.S_LoginModel(id) from S_LoginModel where office=:ofc")
													.setParameter("ofc", ofc).list();
	
									if (logins.size() > 0) {
	
										for (Object logObj : logins) {
	
											idValue = new S_IDValueModel();
											compKey = new S_IDValueCompoundKey();
	
											compKey.setMast_id(obj);
											compKey.setOrganization_id(org.getId());
											compKey.setOffice_id(ofc.getId());
											compKey.setLogin_id(((S_LoginModel) logObj).getId());
											idValue.setValue(obj.getInitial_value()-1);
	
											idValue.setId_val_comp_key(compKey);
	
											getSession().save(idValue);
	
										}
	
									}
	
								}
	
							}
	
						}
					}
					
					break;
					
				case SConstants.scopes.OFFICE_LEVEL:
					
					orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
					
					if(orgnzns.size()>0){
						
						S_OrganizationModel org;
						
						for (Object orgObj : orgnzns) {
							
							org=(S_OrganizationModel) orgObj;
							
							offices=getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
									.setParameter("org", org).list();
							
							if(offices.size()>0){
								
								for (Object ofcObj : offices) {
									
									idValue=new S_IDValueModel();
									compKey=new S_IDValueCompoundKey();
									
									compKey.setMast_id(obj);
									compKey.setOrganization_id(org.getId());
									compKey.setOffice_id(((S_OfficeModel)ofcObj).getId());
									idValue.setValue(obj.getInitial_value()-1);
									
									idValue.setId_val_comp_key(compKey);
									
									getSession().save(idValue);
									
								}
							
							}
							
						}
						
						
					}
					break;
					
				case SConstants.scopes.ORGANIZATION_LEVEL:
					
					orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
					
					if(orgnzns.size()>0){
						
						for (Object orgObj : orgnzns) {
							idValue=new S_IDValueModel();
							compKey=new S_IDValueCompoundKey();
							
							compKey.setMast_id(obj);
							compKey.setOrganization_id(((S_OrganizationModel)orgObj).getId());
							idValue.setValue(obj.getInitial_value()-1);
							
							idValue.setId_val_comp_key(compKey);
							
							getSession().save(idValue);
							
						}
						
						
					}
					
					break;
					
				default:
					
					idValue=new S_IDValueModel();
					compKey=new S_IDValueCompoundKey();
					compKey.setMast_id(obj);
					
					compKey.setOrganization_id(0);
					compKey.setOffice_id(0);
					compKey.setLogin_id(0);
					
					idValue.setValue(obj.getInitial_value()-1);
					
					idValue.setId_val_comp_key(compKey);
					
					getSession().save(idValue);
					
					break;
					
			
			}
			
			
			/*if(obj.getScope()==Constants.scopes.LOGIN_LEVEL){
				
				
				
				
				
			}
			else if(obj.getScope()==Constants.scopes.OFFICE_LEVEL){
				
				
				
				
				
				
			}
			else if(obj.getScope()==Constants.scopes.ORGANIZATION_LEVEL){
				
				List orgnzns=getSession().createQuery("from S_OrganizationModel").list();
				
				if(orgnzns.size()>0){
					
				}
				
				
				
			}
			else {   // System Level
				idValue=new S_IDValueModel();
				compKey=new S_IDValueCompoundKey();
				compKey.setMast_id(obj);
				compKey.setValue(obj.getInitial_value());
				
				idValue.setId_val_comp_key(compKey);
				
				getSession().save(idValue);
			}*/
			
			
			
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
			return obj.getId();
		}
	}
	
	
	public void Update(S_IDGeneratorSettingsModel obj) throws Exception {

		try {

			begin();
			getSession().update(obj);
			
			
			getSession().createQuery("delete from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId")
							.setParameter("mastId", obj.getId()).executeUpdate();
			flush();
			
			switch(obj.getScope()){
			
			case SConstants.scopes.LOGIN_LEVEL:
				
				orgnzns = getSession()
						.createQuery(
								"select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel")
						.list();

				if (orgnzns.size() > 0) {
					
					S_OrganizationModel org;
					S_OfficeModel ofc;
					List logins;
					for (Object orgObj : orgnzns) {

						org = (S_OrganizationModel) orgObj;

						offices = getSession().createQuery(
										"select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
										.setParameter("org", org).list();

						if (offices.size() > 0) {

							for (Object ofcObj : offices) {

								ofc = (S_OfficeModel) ofcObj;

								logins = getSession().createQuery(
												"select new com.webspark.model.S_LoginModel(id) from S_LoginModel where office=:ofc")
												.setParameter("ofc", ofc).list();

								if (logins.size() > 0) {

									for (Object logObj : logins) {

										idValue = new S_IDValueModel();
										compKey = new S_IDValueCompoundKey();

										compKey.setMast_id(obj);
										compKey.setOrganization_id(org.getId());
										compKey.setOffice_id(ofc.getId());
										compKey.setLogin_id(((S_LoginModel) logObj).getId());
										idValue.setValue(obj.getInitial_value()-1);

										idValue.setId_val_comp_key(compKey);

										getSession().save(idValue);

									}

								}

							}

						}

					}
				}
				
				break;
				
			case SConstants.scopes.OFFICE_LEVEL:
				
				orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
				
				if(orgnzns.size()>0){
					S_OrganizationModel org;
					
					for (Object orgObj : orgnzns) {
						
						org=(S_OrganizationModel) orgObj;
						
						offices=getSession().createQuery("select new com.webspark.uac.model.S_OfficeModel(id) from S_OfficeModel where organization=:org")
								.setParameter("org", org).list();
						
						if(offices.size()>0){
							
							for (Object ofcObj : offices) {
								
								idValue=new S_IDValueModel();
								compKey=new S_IDValueCompoundKey();
								
								compKey.setMast_id(obj);
								compKey.setOrganization_id(org.getId());
								compKey.setOffice_id(((S_OfficeModel)ofcObj).getId());
								idValue.setValue(obj.getInitial_value()-1);
								
								idValue.setId_val_comp_key(compKey);
								
								getSession().save(idValue);
								
							}
							flush();
						}
						
					}
					
					
				}
				break;
				
			case SConstants.scopes.ORGANIZATION_LEVEL:
				
				orgnzns=getSession().createQuery("select new com.webspark.uac.model.S_OrganizationModel(id) from S_OrganizationModel").list();
				
				if(orgnzns.size()>0){
					
					for (Object orgObj : orgnzns) {
						idValue=new S_IDValueModel();
						compKey=new S_IDValueCompoundKey();
						
						compKey.setMast_id(obj);
						compKey.setOrganization_id(((S_OrganizationModel)orgObj).getId());
						idValue.setValue(obj.getInitial_value()-1);
						
						idValue.setId_val_comp_key(compKey);
						
						getSession().save(idValue);
						
					}
					
					
				}
				
				break;
				
			default:
				
				idValue=new S_IDValueModel();
				compKey=new S_IDValueCompoundKey();
				compKey.setMast_id(obj);
				
				compKey.setOrganization_id(0);
				compKey.setOffice_id(0);
				compKey.setLogin_id(0);
				
				idValue.setValue(obj.getInitial_value()-1);
				
				idValue.setId_val_comp_key(compKey);
				
				getSession().save(idValue);
				
				break;
				
		
		}
			
			
			
			
			
			
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
	}
	
	
	
	public void createIDGenerators(int type, long org_id, long office_id, 
													long login_id) throws Exception {
		try {
			

			begin();
			
				
			resultList = getSession()
					.createQuery(
							"FROM S_IDGeneratorSettingsModel where scope=:scp")
							.setParameter("scp", type).list();
			
			
			Iterator it=resultList.iterator();
			
			
			while (it.hasNext()) {
				S_IDGeneratorSettingsModel obj= (S_IDGeneratorSettingsModel) it.next();
				
				// Following for Creating Value
			
				switch(type){
					
					case SConstants.scopes.LOGIN_LEVEL:
						
					
						idValue = new S_IDValueModel();
						compKey = new S_IDValueCompoundKey();
	
						compKey.setMast_id(obj);
						compKey.setOrganization_id(org_id);
						compKey.setOffice_id(office_id);
						compKey.setLogin_id(login_id);
						idValue.setValue(obj.getInitial_value()-1);
	
						idValue.setId_val_comp_key(compKey);
	
						getSession().save(idValue);
						
						break;
						
					case SConstants.scopes.OFFICE_LEVEL:
						
						idValue=new S_IDValueModel();
						compKey=new S_IDValueCompoundKey();
						
						compKey.setMast_id(obj);
						compKey.setOrganization_id(org_id);
						compKey.setOffice_id(office_id);
						idValue.setValue(obj.getInitial_value()-1);
						
						idValue.setId_val_comp_key(compKey);
						
						getSession().save(idValue);
										
						break;
						
					case SConstants.scopes.ORGANIZATION_LEVEL:
						
						idValue=new S_IDValueModel();
						compKey=new S_IDValueCompoundKey();
						
						compKey.setMast_id(obj);
						compKey.setOrganization_id(org_id);
						idValue.setValue(obj.getInitial_value()-1);
						
						idValue.setId_val_comp_key(compKey);
						
						getSession().save(idValue);
							
						break;
						
					default:
						
						idValue=new S_IDValueModel();
						compKey=new S_IDValueCompoundKey();
						compKey.setMast_id(obj);
						
						compKey.setOrganization_id(org_id);
						compKey.setOffice_id(office_id);
						compKey.setLogin_id(login_id);
						
						idValue.setValue(obj.getInitial_value()-1);
						
						idValue.setId_val_comp_key(compKey);
						
						getSession().save(idValue);
						
						break;
						
				}
			}
			
			
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
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void delete(long id) throws Exception {

		try {
			begin();
			
			getSession().createQuery("delete from S_IDValueModel where id_val_comp_key.mast_id.id=:id")
			.setLong("id", id).executeUpdate();
			
			getSession().delete(new S_IDGeneratorSettingsModel(id));
			
			
			
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
	
	
	public List getAllIDGenerators()
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_IDGeneratorSettingsModel").list();
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
	

	public List getStatuses(String modelName, String fieldName)
			throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"FROM S_StatusModel WHERE model_name=:mdl and field_name=:fld")
					.setParameter("mdl", modelName)
					.setParameter("fld", fieldName).list();
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
	
	
	public S_IDGeneratorSettingsModel getIDSettings(long sId) throws Exception {
		S_IDGeneratorSettingsModel opt=null;
		try {
			begin();
			opt=(S_IDGeneratorSettingsModel) getSession().get(S_IDGeneratorSettingsModel.class, sId);
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
			return opt;
		}
	}
	
	
	
	
	
}
