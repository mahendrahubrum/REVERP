package com.inventory.config.stock.dao;

import java.sql.Date;

import com.inventory.config.stock.model.DailyQuotationModel;
import com.webspark.dao.SHibernate;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Dec 17, 2013
 */
public class DailyQuotationDao extends SHibernate {

	private static final long serialVersionUID = 3362574346164877360L;

	public void save(DailyQuotationModel quotationModel,
			DailyQuotationModel oldModl) throws Exception {
		try {
			begin();
			if (oldModl != null) {
				getSession().delete(oldModl);
				flush();
			}
			getSession().save(quotationModel);

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
	}

	public DailyQuotationModel getQuotationModel(long login, Date date)
			throws Exception {

		DailyQuotationModel dailyQuotationModel = null;
		try {
			begin();

			dailyQuotationModel = (DailyQuotationModel) getSession()
					.createQuery(
							"from DailyQuotationModel where date=:dat and login.id=:login")
					.setParameter("dat", date).setParameter("login", login)
					.uniqueResult();

			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return dailyQuotationModel;
	}

	public void delete(DailyQuotationModel dailyQuotationModel)
			throws Exception {

		try {
			begin();
			if (dailyQuotationModel != null)
				getSession().delete(dailyQuotationModel);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
	}

}
