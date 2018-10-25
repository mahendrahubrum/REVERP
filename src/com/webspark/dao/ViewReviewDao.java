package com.webspark.dao;

import java.sql.Date;
import java.util.List;

import com.webspark.model.ReviewModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 10, 2014
 */
public class ViewReviewDao extends SHibernate{

	public List getAllReviewsOfLogin(long loginID, long officeID, Date fromDate, Date toDate) throws Exception {
		List resList=null;
		try {
			begin();
			resList=getSession().createQuery("from ReviewModel where login=:login and office_id=:ofc and date between :fromDt and :toDt order by date desc")
					.setParameter("ofc", officeID)
					.setParameter("login", loginID)
					.setParameter("fromDt", fromDate)
					.setParameter("toDt", toDate).list();
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		return resList;
	}

	public void delete(long id) throws Exception {
		try {
			begin();
			ReviewModel reviewModel=(ReviewModel) getSession().get(ReviewModel.class, id);
			getSession().delete(reviewModel);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}finally{
			flush();
			close();
		}
		
	}

	
}
