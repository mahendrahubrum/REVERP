package com.webspark.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.CurrencyModel;
import com.webspark.model.CurrencyRateModel;

@SuppressWarnings("serial")
public class CurrencyRateDao extends SHibernate implements Serializable{

	@SuppressWarnings("rawtypes")
	List resultList = new ArrayList();

	public long addOption(CurrencyRateModel lm) throws Exception {
		try {
			begin();
			getSession().save(lm);
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
		return lm.getId();
	}

	public void resetRate(long id, Date dt, double rt) throws Exception {
		try {

			begin();
			getSession().createQuery(
							"update CurrencyRateModel set date=:dat and rate=:rat where currencyId.id=:eid")
					.setParameter("dat", dt).setParameter("eid", id)
					.setParameter("rat", rt).executeUpdate();
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

	public void Update(CurrencyRateModel md) throws Exception {

		try {

			begin();
			getSession().update(md);
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

	public void delete(long id) throws Exception {

		try {
			begin();
			getSession().delete(new CurrencyRateModel(id));
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

	public String getCurrency(long id) throws Exception {
		String nam = null;
		try {

			begin();
			nam = (String) getSession()
					.createQuery("select name from CurrencyModel where id=:cid")
					.setParameter("cid", id).uniqueResult();
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
		return nam;
	}

	public double getRateExists(long id) throws Exception {
		double md = 0;
		Object obj = null;
		try {

			begin();
			obj = getSession()
					.createQuery(
							"select rate from CurrencyRateModel where currencyId.id=:cid and date=(select max(date)"
									+ " FROM CurrencyRateModel WHERE currencyId.id=:cid)"
									+ " and id=(select max(id) from CurrencyRateModel where currencyId.id=:cid)")
					.setParameter("cid", id).uniqueResult();
			if(obj!=null){
				md=(Double) obj;
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
		return md;
	}

	public List getlabels() throws Exception {
		try {

			begin();
			resultList = getSession()
					.createQuery(
							"select new com.webspark.model.CurrencyModel(id,name) FROM CurrencyModel")
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

	public CurrencyRateModel getRateDetails(long id) throws Exception {
		CurrencyRateModel cm = null;
		try {

			begin();
			List list = getSession()
					.createQuery(
							"FROM CurrencyRateModel WHERE currencyId.id=:mdl and date=(select max(date)"
									+ " FROM CurrencyRateModel WHERE currencyId.id=:mdl)")
					.setParameter("mdl", id).list();

			if (list != null && list.size() > 0)
				cm = (CurrencyRateModel) list.get(list.size() - 1);

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
		return cm;
	}

	public double getConversionRate(long baseCurrId, long foreignCurrId) throws Exception {
		double rate=1;
		try {
			begin();
			List list = getSession()
					.createQuery(
							"select rate FROM CurrencyRateModel WHERE currencyId.id=:id and baseCurrency.id=:baseId order by id desc limit 1")
					.setParameter("id", foreignCurrId).setParameter("baseId", baseCurrId).list();

			if (list != null && list.size() > 0)
				rate = (Double) list.get(0);

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
		return rate;

	}
	
	public CurrencyModel getCurrencyModel(long id) throws Exception {
		CurrencyModel nam = null;
		try {
			begin();
			nam =  (CurrencyModel) getSession().get(CurrencyModel.class, id);
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
		return nam;
	}

	public CurrencyRateModel getCurrencyRateModel(Date date, long currency) throws Exception {
		CurrencyRateModel mdl = null;
		try {
			begin();
			mdl = (CurrencyRateModel) getSession().createQuery("from CurrencyRateModel where date=:date and currencyId.id=:currency")
													.setParameter("date", date).setParameter("currency", currency).uniqueResult();
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
		return mdl;
	}
	
	public Date saveCurrencyRateModel(CurrencyRateModel mdl) throws Exception {
		try {
			begin();
			if(mdl.getId()!=0)
				getSession().update(mdl);
			else
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
		return mdl.getDate();
	}


}
