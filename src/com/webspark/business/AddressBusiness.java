package com.webspark.business;

import com.webspark.common.util.CommonUtil;
import com.webspark.dao.AddressDao;
import com.webspark.model.AddressModel;

public class AddressBusiness {
	public AddressBusiness() {
		// TODO Auto-generated constructor stub
	}
	
	public String getAddressString(long addressId) throws Exception {
		AddressDao addressDao = new AddressDao();
		AddressModel addressModel = addressDao.getAddressModel(addressId);
		if(addressModel!=null) {
			return createAddressAsString(addressModel);
		}
		else
			return "";
			
	}

	public String createAddressAsString(AddressModel addressModel) {
		StringBuffer sb = new StringBuffer();
		if (addressModel.getAddress_area() != null && !addressModel.getAddress_area().equals("")) {
			sb.append(CommonUtil.removeHtml(addressModel.getAddress_area()));
		}
		if (addressModel.getCountry() != null && addressModel.getCountry().getName() != null
				&& !addressModel.getCountry().getName().equals("")) {
			sb.append("\n");
			sb.append(addressModel.getCountry().getName() + ", ");
		}
		if (addressModel.getEmail() != null && !addressModel.getEmail().equals("")) {
			sb.append("\n");
			sb.append(addressModel.getEmail() + ", ");
		}
		if (addressModel.getPhone() != null && !addressModel.getPhone().equals("")) {
			sb.append("\n");
			sb.append(addressModel.getPhone() + ", ");
		}
		if (addressModel.getMobile() != null && !addressModel.getMobile().equals("")) {
			sb.append("\n");
			sb.append(addressModel.getMobile() + ", ");
		}
		return sb.toString();
	}

}
