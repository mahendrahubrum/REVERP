package com.webspark.uac.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.dao.SHibernate;
import com.webspark.uac.model.CountryModel;

public class CountryDao extends SHibernate implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4688712183042341184L;
	private List resultList = new ArrayList();

	@SuppressWarnings("finally")
	public List getCountry() throws Exception {

		try {
			begin();
			resultList = getSession().createQuery(
					"from CountryModel order by name").list();
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

	public CountryModel getCountryModel(long countryId) throws Exception {
		CountryModel model = null;

		try {
			begin();
			model = (CountryModel) getSession().get(CountryModel.class,
					countryId);
			commit();
		} catch (Exception e) {
			rollback();
			close();
		} finally {
			flush();
			close();
			return model;
		}
	}
	
	
	public long save(CountryModel md) throws Exception {

		try {

			begin();
			getSession().save(md);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return md.getId();
		}
	}
	
	public void delete(long id) throws Exception {

		try {

			begin();
			getSession().delete(new CountryModel(id));
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
	
	public long update(CountryModel md) throws Exception {

		try {

			begin();
			getSession().update(md);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return md.getId();
		}
	}

	public String getCountryName(long countryId) throws Exception {
		String country="";
		try {
			begin();
			Object ob = getSession().createQuery(
					"select name from CountryModel where id=:id").setParameter("id", countryId).uniqueResult();
			if(ob!=null)
				country=(String) ob;
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
		return country;
	}

}