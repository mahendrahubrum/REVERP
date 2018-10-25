package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.STable;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Sep 18, 2013
 */
public class CopyCustomerUI extends SContainerPanel {

	private static final long serialVersionUID = 1058455381051459563L;

	private STable table;

	private SButton saveButton;

	private static final String TRANSFER = "Transfer";
	private static final String CUSTOMER_ID = "Customer ID";
	private static final String CUSTOMER_NAME = "Customer Name";
	private static final String CUSTOMER_CODE = "Customer Code";

	private String allHeaders[];
	private String requiredHeaders[];

	private SComboField fromOfficeComboField;
	private SComboField toOfficeComboField;

	private SCheckBox selectAllBox;

	private CustomerDao dao;
	
	private SComboField fromOrgField;
	private SComboField toOrgField;

	public CopyCustomerUI() {

		dao = new CustomerDao();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSizeFull();
		
		fromOrgField=new SComboField("From Organization",200);
		toOrgField=new SComboField("From Organization",200);
		loadOrganizations();
		
//		if(!isSystemAdmin()&&!isSuperAdmin()){
			fromOrgField.setVisible(false);
			toOrgField.setVisible(false);
//		}

		fromOfficeComboField = new SComboField("From Office");
		fromOfficeComboField.setInputPrompt("-------------Select-----------");
		toOfficeComboField = new SComboField("To Office");
		toOfficeComboField.setInputPrompt("-------------Select-----------");
		loadOffices(fromOfficeComboField,getOrganizationID());
		loadOffices(toOfficeComboField,getOrganizationID());
		

		selectAllBox = new SCheckBox("Select All");

		allHeaders = new String[] { TRANSFER, CUSTOMER_ID, CUSTOMER_NAME,
				CUSTOMER_CODE };
		requiredHeaders = new String[] { TRANSFER, CUSTOMER_NAME, CUSTOMER_CODE };

		table = new STable(null, 400, 300);
		table.addContainerProperty(TRANSFER, SCheckBox.class, null, TRANSFER,
				null, Align.CENTER);
		table.addContainerProperty(CUSTOMER_ID, Long.class, null, CUSTOMER_ID,
				null, Align.CENTER);
		table.addContainerProperty(CUSTOMER_NAME, String.class, null,
				CUSTOMER_NAME, null, Align.LEFT);
		table.addContainerProperty(CUSTOMER_CODE, String.class, null,
				CUSTOMER_CODE, null, Align.LEFT);

		table.setVisibleColumns(requiredHeaders);

		saveButton = new SButton("Save");

		mainFormLayout.addComponent(fromOrgField);
		mainFormLayout.addComponent(fromOfficeComboField);
		mainFormLayout.addComponent(toOrgField);
		mainFormLayout.addComponent(toOfficeComboField);
		mainFormLayout.addComponent(selectAllBox);
		mainFormLayout.addComponent(table);
		mainFormLayout.addComponent(saveButton);

		fromOrgField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
				loadOffices(fromOfficeComboField, (Long)fromOrgField.getValue());
			}
		});
		
		toOrgField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
				loadOffices(toOfficeComboField, (Long)toOrgField.getValue());
			}
		});
		toOfficeComboField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
			}
		});
		
		fromOfficeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					clearErrorMessages();

					table.setVisibleColumns(allHeaders);
					table.removeAllItems();
					if (fromOfficeComboField.getValue() != null
							&& !fromOfficeComboField.getValue().equals("")) {
						List list = dao
								.getAllCustomersNamesList(toLong(fromOfficeComboField
										.getValue().toString()));
						SCheckBox transferBox = null;
						CustomerModel model = null;
						for (int i = 0; i < list.size(); i++) {
							transferBox = new SCheckBox();
							model = (CustomerModel) list.get(i);
							Object items[] = new Object[] { transferBox,
									model.getId(), model.getName(),
									model.getCustomer_code() };
							table.addItem(items, i + 1);
						}
					}
					table.setVisibleColumns(requiredHeaders);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		selectAllBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
				if (table.getItemIds().size() > 0) {
					Iterator iter = table.getItemIds().iterator();
					if (selectAllBox.getValue()) {
						Item item;
						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());
							box.setValue(true);
						}
					} else {
						Item item;
						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());
							box.setValue(false);
						}
					}
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {
						CustomerModel newModel = null;
						CustomerModel oldModel = null;

						Vector modelVector = new Vector();
						Iterator iter = table.getItemIds().iterator();
						Item item;
						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());

							if (box.getValue()) {
								oldModel = dao.getCustomer(toLong(item
										.getItemProperty(CUSTOMER_ID)
										.getValue().toString()));

								if (!dao.isAlreadyExists(
										toLong(toOfficeComboField.getValue()
												.toString()), item
												.getItemProperty(CUSTOMER_NAME)
												.getValue().toString(), item
												.getItemProperty(CUSTOMER_CODE)
												.getValue().toString())) {

									LedgerModel objModel = new LedgerModel();
									objModel.setName(oldModel.getLedger()
											.getName());
									objModel.setGroup(new GroupModel(getSettings().getCUSTOMER_GROUP()));
									objModel.setCurrent_balance(0);
									objModel.setStatus(SConstants.statuses.LEDGER_ACTIVE);
									objModel.setOffice(new S_OfficeModel(
											toLong(toOfficeComboField
													.getValue().toString())));

									newModel = new CustomerModel();
									newModel.setAddress(oldModel.getAddress());
									newModel.setName(item
											.getItemProperty(CUSTOMER_NAME)
											.getValue().toString());
									newModel.setCustomer_code(item
											.getItemProperty(CUSTOMER_CODE)
											.getValue().toString());
//									newModel.setCredit_limit(oldModel
//											.getCredit_limit());
									newModel.setCustomer_currency(oldModel
											.getCustomer_currency());
									newModel.setDescription(oldModel
											.getDescription());
//									newModel.setDiscount_percentage(oldModel
//											.getDiscount_percentage());
//									newModel.setPayment_terms(oldModel
//											.getPayment_terms());
//									newModel.setPrompt_payment_discount_percent(oldModel
//											.getPrompt_payment_discount_percent());
//									newModel.setSales_type(oldModel
//											.getSales_type());
									newModel.setLedger(objModel);

									modelVector.add(newModel);
								} else {
									System.out.println("Skipped   "
											+ item.getItemProperty(
													CUSTOMER_NAME).getValue()
													.toString());
								}
							}
						}

						try {
							if (modelVector.size() > 0) {
								dao.save(modelVector);
								Notification.show("Success",
										"Saved Successfully",
										Type.WARNING_MESSAGE);
							} else {
								setRequiredError(
										table,
										"Select customers that are not present in "
												+ toOfficeComboField
														.getItemCaption(toOfficeComboField
																.getValue()),
										true);
							}
						} catch (Exception e) {
							Notification.show("Error", "Unable to save",
									Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		setContent(mainFormLayout);
		setSizeFull();
	}
	private void loadOrganizations() {
		try {
			List list = new ArrayList();

			list.addAll(new OrganizationDao()
					.getAllOrganizations());

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			fromOrgField.setContainerDataSource(bic);
			toOrgField.setContainerDataSource(bic);
			fromOrgField.setItemCaptionPropertyId("name");
			toOrgField.setItemCaptionPropertyId("name");
			fromOrgField.setValue(getOrganizationID());
			toOrgField.setValue(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadOffices(SComboField field,long orgId) {
		try {
			List list = new ArrayList();

			list.addAll(new OfficeDao()
					.getAllOfficeNamesUnderOrg(orgId));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			field.setContainerDataSource(bic);
			field.setItemCaptionPropertyId("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Boolean isValid() {
		boolean valid = true;

		clearErrorMessages();

		if (fromOfficeComboField.getValue() == null
				|| fromOfficeComboField.getValue().equals("")) {
			setRequiredError(fromOfficeComboField, "Select an office", true);
			valid = false;
		}
		if (toOfficeComboField.getValue() == null
				|| toOfficeComboField.getValue().equals("")) {
			setRequiredError(toOfficeComboField, "Select an office", true);
			valid = false;
		}
		if (valid) {
			if (toLong(toOfficeComboField.getValue().toString()) == toLong(fromOfficeComboField
					.getValue().toString())) {
				setRequiredError(toOfficeComboField,
						"Select different offices", true);
				valid = false;
			}
		}

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Cannot be empty", true);
			valid = false;
		}
		return valid;
	}

	private void clearErrorMessages() {
		fromOfficeComboField.setComponentError(null);
		toOfficeComboField.setComponentError(null);
		table.setComponentError(null);
	}
}
