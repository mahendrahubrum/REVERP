package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.model.AddressModel;


/**
 * @author Anil
 *
 * Jun 7, 2013
 */
public class SearchFieldDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6758801197081741262L;
	List resultList=new ArrayList();
	
	public long getDataSize(String query) throws Exception {
		long count=0;
		try {
			
			begin();
			count=(Long) getSession().createQuery(query).uniqueResult();
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return count;
		}
	}
	
	public List getData(String query) throws Exception {
		try {
			
			begin();
				resultList=getSession().createQuery(query).list();
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public void updateAddress(AddressModel obj) throws Exception {

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
	
	@SuppressWarnings("finally")
	public List getCountry() throws Exception {
		try {
			begin();
			resultList = getSession().createQuery(
					"from CountryModel order by name").list();
			commit();
		} catch (Exception e) {
			resultList=new ArrayList();
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


	public AddressModel getAddressModel(long addressId) throws Exception {
		AddressModel address = null;
			try {
				begin();
				address = (AddressModel) getSession().get(AddressModel.class, addressId);
				commit();
			} catch (Exception e) {
				rollback();
				close();
				e.printStackTrace();
				throw e;
			} finally {
				flush();
				close();
				return address;
		}
	}
}
