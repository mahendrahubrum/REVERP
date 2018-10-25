package com.webspark.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.webspark.common.util.CommonUtil;
import com.webspark.model.AddressModel;

/**
 * @author Anil
 * 
 *         Jun 7, 2013
 */
public class AddressDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5123421376501088779L;
	List resultList = new ArrayList();

	public long saveAddress(AddressModel obj) throws Exception {

		try {

			begin();
			getSession().save(obj);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return obj.getId();
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
			resultList = new ArrayList();
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
			address = (AddressModel) getSession().get(AddressModel.class,
					addressId);
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

	public String getAddressString(long addressId) throws Exception {
		String address = "";
		AddressModel addressModel = null;
		try {
			begin();
			addressModel = (AddressModel) getSession().get(AddressModel.class,
					addressId);
			commit();
			if (addressModel != null) {
				if (addressModel.getAddress_area() != null&&!addressModel.getAddress_area().equals("")) {
					address += CommonUtil.removeHtml(addressModel.getAddress_area());
				}
				if (addressModel.getCountry() != null && addressModel.getCountry().getName() != null &&!addressModel.getCountry().getName().equals("")) {
					address += addressModel.getCountry().getName() + ", ";
				}
				if (addressModel.getEmail() != null&&!addressModel.getEmail().equals("")) {
					address += addressModel.getEmail() + ", ";
				}
				if (addressModel.getPhone() != null&&!addressModel.getPhone().equals("")) {
					address += addressModel.getPhone() + ", ";
				}
				if (addressModel.getMobile() != null&&!addressModel.getMobile().equals("")) {
					address += addressModel.getMobile() + ", ";
				}
			}

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return address;
	}
	
	public String getLocationAddressString(long addressId) throws Exception {
		String address = "";
		AddressModel addressModel = null;
		try {
			begin();
			addressModel = (AddressModel) getSession().get(AddressModel.class,addressId);
			commit();
			if (addressModel != null) {
				if (addressModel.getAddress_area() != null&&!addressModel.getAddress_area().equals("")) {
					address += addressModel.getAddress_area();
				}
				if (addressModel.getCountry() != null && addressModel.getCountry().getName() != null &&!addressModel.getCountry().getName().equals("")) {
					address += addressModel.getCountry().getName() + ", ";
				}
			}
		} 
		catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return address;
	}
}
