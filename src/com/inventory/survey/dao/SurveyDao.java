package com.inventory.survey.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.inventory.survey.model.SurveyModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class SurveyDao extends SHibernate implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7798611367868252125L;
	private List resultList = new ArrayList();

	public List getAllSurveys(long ofc_id) throws Exception {
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.survey.model.SurveyModel(id, cast(id as string))"
									+ " from SurveyModel where office.id=:ofc")
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
	
	
	public long save(SurveyModel obj) throws Exception {
		try {
			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return obj.getId();
	}

	public void update(SurveyModel obj) throws Exception {
		try {
			begin();
			
			getSession().update(obj);
			
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

			getSession().delete(getSession().get(
					SurveyModel.class, id));

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block

		} finally {
			flush();
			close();
		}

	}

	public SurveyModel getSurvey(long id) throws Exception {
		SurveyModel cust = null;
		try {
			begin();
			cust = (SurveyModel) getSession().get(SurveyModel.class, id);
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
	
	
	
	public List getSurveyReport(long ofc_id, Date stdt, Date enddt, long userId) throws Exception {
		try {
			resultList=null;
			String condn="";
			
			if(userId!=0)
				condn+=" and login_id="+userId;
			
			begin();
			resultList = getSession()
					.createQuery("from SurveyModel where office.id=:ofc and date between :stdt and :enddt"+condn)
					.setParameter("ofc", ofc_id).setParameter("stdt", stdt).setParameter("enddt", enddt).list();
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
	

}
