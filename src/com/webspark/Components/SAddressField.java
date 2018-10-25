package com.webspark.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.mail.internet.InternetAddress;

import com.vaadin.server.UserError;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.RichTextArea;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.AddressDao;
import com.webspark.model.AddressModel;
import com.webspark.uac.model.CountryModel;

public class SAddressField extends SFormLayout {

	private static final long serialVersionUID = 5641519629686725820L;
	
	private WrappedSession session;
	private RichTextArea addressArea;
	private SComboField countryComboField = null;
	private STextField phoneTextField = null;
	private STextField mobileTextField = null;
	private STextField emailTextField = null;
	
	ResourceBundle bundle;
	private SGridLayout vlayout = null;
	private SGridLayout layout = null;

	AddressDao dao = null;

	@SuppressWarnings("rawtypes")
	public SAddressField(int total_columns) {

		session=new SessionUtil().getHttpSession();
		dao = new AddressDao();
		
		if(session.getAttribute("property_file")!=null)
			bundle = ResourceBundle.getBundle(session.getAttribute("property_file").toString());
		
		addressArea=new RichTextArea("");
		addressArea.setWidth("300");
		addressArea.setHeight("150");
		phoneTextField = new STextField(getPropertyName("ad_phone"), 150, 15);
		mobileTextField = new STextField(getPropertyName("ad_mobile"), 150, 15);
		emailTextField = new STextField(getPropertyName("ad_email"), 150, 100);
		
		List countryList = null;
		try {
			countryList = dao.getCountry();
		} catch (Exception e) {
			countryList = new ArrayList();
			e.printStackTrace();
		}
		countryComboField = new SComboField(getPropertyName("ad_country"), 150, countryList, "id","name", true, "Select");
		setCaption("Address");
		
		layout = new SGridLayout();
		layout.setSpacing(true);
		
		/* 
		 * 
		 * Jinshad set this for arranging UI components as flexible
		 *  If we want to add another address components, need to change " layout.setRows(16/total_columns) "
		 *  16 is the number of components.
		 * 
		 * */
		vlayout=new SGridLayout();
		vlayout.setSpacing(true);
		
		layout.setColumns(2);
		layout.setRows(2);
		
		vlayout.setColumns(total_columns);
		vlayout.setRows(2/total_columns);

		vlayout.addComponent(addressArea);
		layout.addComponent(countryComboField);
		layout.addComponent(phoneTextField);
		layout.addComponent(mobileTextField);
		layout.addComponent(emailTextField);
		
//		layout.setStyleName("grid_address")
		
//		layout.setSpacing(true);
		vlayout.setMargin(true);
		vlayout.addComponent(layout);
		setSizeFull();
		addComponent(vlayout);
		setMargin(false);
	}

	public SComboField getCountryComboField() {
		return countryComboField;
	}

	public STextField getPhoneTextField() {
		return phoneTextField;
	}

	public STextField getMobileTextField() {
		return mobileTextField;
	}

	public STextField getEmailTextField() {
		return emailTextField;
	}

	public RichTextArea getAddressArea() {
		return addressArea;
	}

	public long saveAddress() {

		AddressModel addressModel = new AddressModel();

		if (countryComboField.getValue() != null) {
			addressModel.setCountry(new CountryModel(Long
					.parseLong(countryComboField.getValue().toString())));
		} else {
			addressModel.setCountry(new CountryModel());
		}

		if (phoneTextField.getValue() != null) {
			addressModel.setPhone(phoneTextField.getValue());
		} else {
			addressModel.setPhone("");
		}

		if (mobileTextField.getValue() != null) {
			addressModel.setMobile(mobileTextField.getValue());
		} else {
			addressModel.setMobile("");
		}

		if (emailTextField.getValue() != null) {
			addressModel.setEmail(emailTextField.getValue());
		} else {
			addressModel.setEmail("");
		}
		if (addressArea.getValue() != null) {
			addressModel.setAddress_area(addressArea.getValue());
		} else {
			addressModel.setAddress_area("");
		}
		
		try {
			return dao.saveAddress(addressModel);
		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}
	}

	public void updateAddress(long addressId) {

		AddressModel addressModel;
		try {
			addressModel = dao.getAddressModel(addressId);
		} catch (Exception e) {
			addressModel = new AddressModel();
			e.printStackTrace();
		}

		if (countryComboField.getValue() != null) {
			addressModel.setCountry(new CountryModel(Long
					.parseLong(countryComboField.getValue().toString())));
		} else {
			addressModel.setCountry(new CountryModel());
		}

		if (phoneTextField.getValue() != null) {
			addressModel.setPhone(phoneTextField.getValue());
		} else {
			addressModel.setPhone("");
		}

		if (mobileTextField.getValue() != null) {
			addressModel.setMobile(mobileTextField.getValue());
		} else {
			addressModel.setMobile("");
		}

		if (emailTextField.getValue() != null) {
			addressModel.setEmail(emailTextField.getValue());
		} else {
			addressModel.setEmail("");
		}
		if (addressArea.getValue() != null) {
			addressModel.setAddress_area(addressArea.getValue());
		} else {
			addressModel.setAddress_area("");
		}
		try {
			dao.updateAddress(addressModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void loadAddress(long addressId){
		AddressModel addressModel;
		try {
			addressModel = dao.getAddressModel(addressId);
		} catch (Exception e) {
			addressModel = new AddressModel();
			e.printStackTrace();
		}
		countryComboField.setValue(addressModel.getCountry().getId());
		phoneTextField.setValue(addressModel.getPhone());
		mobileTextField.setValue(addressModel.getMobile());
		emailTextField.setValue(addressModel.getEmail());
		addressArea.setValue(addressModel.getAddress_area());
	}
	
	public void setAddress(AddressModel addressModel){
		countryComboField.setValue(addressModel.getCountry().getId());
		phoneTextField.setValue(addressModel.getPhone());
		mobileTextField.setValue(addressModel.getMobile());
		emailTextField.setValue(addressModel.getEmail());
		addressArea.setValue(addressModel.getAddress_area());
	}
	
	public AddressModel getAddress() {

		AddressModel addressModel = new AddressModel();


		if (countryComboField.getValue() != null) {
			addressModel.setCountry(new CountryModel(Long
					.parseLong(countryComboField.getValue().toString())));
		} else {
			addressModel.setCountry(new CountryModel());
		}

		if (phoneTextField.getValue() != null) {
			addressModel.setPhone(phoneTextField.getValue());
		} else {
			addressModel.setPhone("");
		}

		if (mobileTextField.getValue() != null) {
			addressModel.setMobile(mobileTextField.getValue());
		} else {
			addressModel.setMobile("");
		}

		if (emailTextField.getValue() != null) {
			addressModel.setEmail(emailTextField.getValue());
		} else {
			addressModel.setEmail("");
		}
		if (addressArea.getValue() != null) {
			addressModel.setAddress_area(addressArea.getValue());
		} else {
			addressModel.setAddress_area("");
		}
		return addressModel;
	}
	
	public void clearAll(){
		countryComboField.setValue(null);
		phoneTextField.setValue("");
		mobileTextField.setValue("");
		emailTextField.setValue("");
		addressArea.setValue("");
	}
	
	public boolean isValid(){
		
		boolean ret=true;
		
		if(emailTextField.getValue()!=null && !emailTextField.getValue().equals("")) {
			try {
			      InternetAddress emailAddr = new InternetAddress(emailTextField.getValue());
			      emailAddr.validate();
			      setRequiredError(emailTextField, null,false);
			   } catch (Exception ex) {
				   setRequiredError(emailTextField, "Enter a valid email",true);
					ret=false;
			   }
		}
		
//		if(mobileTextField.getValue()!=null && !mobileTextField.getValue().equals("")) {
//			try {
//			      Double.parseDouble(mobileTextField.getValue().toString());
//			      setRequiredError(mobileTextField, null,false);
//			   } catch (Exception ex) {
//				   setRequiredError(mobileTextField, "Enter a valid mobile",true);
//				   ret=false;
//			   }
//		}
//		
//		
//		if(phoneTextField.getValue()!=null && !phoneTextField.getValue().equals("")) {
//			try {
//			      Double.parseDouble(phoneTextField.getValue().toString());
//			      setRequiredError(phoneTextField, null,false);
//			   } catch (Exception ex) {
//				   setRequiredError(phoneTextField, "Enter a valid phone",true);
//				   ret=false;
//			   }
//		}
		
		if(countryComboField.getValue()==null || countryComboField.getValue().equals("")){
			setRequiredError(countryComboField, "Select a country",true);
			ret=false;
		}
		else
			setRequiredError(countryComboField, null,false);
		
		return ret;
		
	}
	
	public void setRequiredError(AbstractComponent component, String fieldNameToDisplay, boolean enable){
		if(enable)
			component.setComponentError(new UserError(fieldNameToDisplay));
		else
			component.setComponentError(null);
	}
	
	public String getPropertyName(String name) {
		try {
			if(bundle!=null)
				name=bundle.getString(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	

}
