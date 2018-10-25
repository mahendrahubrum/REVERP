package com.webspark.dao;

import java.io.Serializable;

import org.hibernate.criterion.Restrictions;

import com.webspark.common.util.SConstants;
import com.webspark.model.S_IDGeneratorSettingsModel;
import com.webspark.model.S_IDValueModel;

/**
 * @Author Jinshad P.T.
 */

public class IDGeneratorDao extends SHibernate implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6223396734960632991L;

	public long generateID(String idName, long login_id, long office_id,
								long organization_id) throws Exception {

		S_IDValueModel val = null;

		try {

			begin();

			S_IDGeneratorSettingsModel idObj = (S_IDGeneratorSettingsModel) getSession()
					.createCriteria(S_IDGeneratorSettingsModel.class)
					.add(Restrictions.eq("id_name", idName)).uniqueResult();

			if (idObj != null) {

				switch (idObj.getScope()) {

				case SConstants.scopes.LOGIN_LEVEL:

					val = (S_IDValueModel) getSession().createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.login_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", login_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);
						
						if(val.getValue()<idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				case SConstants.scopes.OFFICE_LEVEL:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.office_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", office_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);
						
						if(val.getValue()<idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());
						
						getSession().update(val);
					}

					break;

				case SConstants.scopes.ORGANIZATION_LEVEL:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.organization_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", organization_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);
						
						if(val.getValue()<idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				default:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId")
							.setParameter("mastId", idObj.getId())
							.uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);
						
						if(val.getValue()<idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

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
			return val.getValue();
		}
	}
	
	
	
	
	public long generateIDWithOutBegin(String idName, long login_id, long office_id,
			long organization_id) throws Exception {

		S_IDValueModel val = null;

		try {


			S_IDGeneratorSettingsModel idObj = (S_IDGeneratorSettingsModel) getSession()
					.createCriteria(S_IDGeneratorSettingsModel.class)
					.add(Restrictions.eq("id_name", idName)).uniqueResult();

			if (idObj != null) {

				switch (idObj.getScope()) {

				case SConstants.scopes.LOGIN_LEVEL:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.login_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", login_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);

						if (val.getValue() < idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				case SConstants.scopes.OFFICE_LEVEL:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.office_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", office_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);

						if (val.getValue() < idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				case SConstants.scopes.ORGANIZATION_LEVEL:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId "
											+ "and id_val_comp_key.organization_id=:id")
							.setParameter("mastId", idObj.getId())
							.setLong("id", organization_id).uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);

						if (val.getValue() < idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				default:

					val = (S_IDValueModel) getSession()
							.createQuery(
									"from S_IDValueModel where id_val_comp_key.mast_id.id=:mastId")
							.setParameter("mastId", idObj.getId())
							.uniqueResult();

					if (val != null) {
						val.setValue(val.getValue() + 1);

						if (val.getValue() < idObj.getInitial_value())
							val.setValue(idObj.getInitial_value());

						getSession().update(val);
					}

					break;

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		return val.getValue();
	}
	
	
	
	
	
	

}
