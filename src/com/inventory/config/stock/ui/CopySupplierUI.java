package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.GroupModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.SupplierModel;
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
public class CopySupplierUI extends SContainerPanel {

	private static final long serialVersionUID = 1058455381051459563L;

	private STable table;

	private SButton saveButton;

	private static final String TRANSFER = "Transfer";
	private static final String SUPPLIER_ID = "Supplier ID";
	private static final String SUPPLIER_NAME = "Supplier Name";
	private static final String SUPPLIER_CODE = "Supplier Code";

	private String allHeaders[];
	private String requiredHeaders[];

	private SComboField fromOfficeComboField;
	private SComboField toOfficeComboField;

	private SCheckBox selectAllBox;

	private SupplierDao dao;

	private SComboField fromOrgField;
	private SComboField toOrgField;

	public CopySupplierUI() {

		dao = new SupplierDao();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSizeFull();

		fromOrgField = new SComboField(getPropertyName("from_org"), 200);
		toOrgField = new SComboField(getPropertyName("to_org"), 200);
		loadOrganizations();

		// if(!isSystemAdmin()&&!isSuperAdmin()){
		fromOrgField.setVisible(false);
		toOrgField.setVisible(false);
		// }

		fromOfficeComboField = new SComboField(getPropertyName("from_offc"));
		fromOfficeComboField.setInputPrompt(getPropertyName("select"));
		toOfficeComboField = new SComboField(getPropertyName("to_offc"));
		toOfficeComboField.setInputPrompt(getPropertyName("select"));
		loadOffices(fromOfficeComboField, getOrganizationID());
		loadOffices(toOfficeComboField, getOrganizationID());

		selectAllBox = new SCheckBox(getPropertyName("select_all"));

		allHeaders = new String[] { TRANSFER, SUPPLIER_ID, SUPPLIER_NAME,
				SUPPLIER_CODE };
		requiredHeaders = new String[] { TRANSFER, SUPPLIER_NAME, SUPPLIER_CODE };

		table = new STable(null, 400, 300);
		table.addContainerProperty(TRANSFER, SCheckBox.class, null,
				getPropertyName("transfer"), null, Align.CENTER);
		table.addContainerProperty(SUPPLIER_ID, Long.class, null, SUPPLIER_ID,
				null, Align.CENTER);
		table.addContainerProperty(SUPPLIER_NAME, String.class, null,
				getPropertyName("supplier_name"), null, Align.LEFT);
		table.addContainerProperty(SUPPLIER_CODE, String.class, null,
				getPropertyName("supplier_code"), null, Align.LEFT);

		table.setVisibleColumns(requiredHeaders);

		saveButton = new SButton(getPropertyName("save"));

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
				loadOffices(fromOfficeComboField,
						(Long) fromOrgField.getValue());
			}
		});

		toOrgField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
				loadOffices(toOfficeComboField, (Long) toOrgField.getValue());
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
								.getAllSupplierNamesList(toLong(fromOfficeComboField
										.getValue().toString()));
						SCheckBox transferBox = null;
						SupplierModel model = null;
						for (int i = 0; i < list.size(); i++) {
							transferBox = new SCheckBox();
							model = (SupplierModel) list.get(i);
							Object items[] = new Object[] { transferBox,
									model.getId(), model.getName(),
									model.getSupplier_code() };
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
					Item item;
					if (selectAllBox.getValue()) {

						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());
							box.setValue(true);
						}
					} else {
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
						SupplierModel newModel = null;
						SupplierModel oldModel = null;

						Vector modelVector = new Vector();
						Iterator iter = table.getItemIds().iterator();
						Item item;
						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());

							if (box.getValue()) {
								oldModel = dao.getSupplier(toLong(item
										.getItemProperty(SUPPLIER_ID)
										.getValue().toString()));

								if (!dao.isAlreadyExists(
										toLong(toOfficeComboField.getValue()
												.toString()), item
												.getItemProperty(SUPPLIER_NAME)
												.getValue().toString(), item
												.getItemProperty(SUPPLIER_CODE)
												.getValue().toString())) {

									LedgerModel objModel = new LedgerModel();
									objModel.setName(oldModel.getLedger()
											.getName());
									objModel.setGroup(new GroupModel(getSettings().getSUPPLIER_GROUP()));
									objModel.setCurrent_balance(0);
									objModel.setStatus(SConstants.statuses.LEDGER_ACTIVE);
									objModel.setOffice(new S_OfficeModel(
											toLong(toOfficeComboField
													.getValue().toString())));

									newModel = new SupplierModel();
									newModel.setAddress(oldModel.getAddress());
									newModel.setName(item
											.getItemProperty(SUPPLIER_NAME)
											.getValue().toString());
									newModel.setSupplier_code(item
											.getItemProperty(SUPPLIER_CODE)
											.getValue().toString());
									newModel.setCredit_limit(oldModel
											.getCredit_limit());
									newModel.setSupplier_currency(oldModel
											.getSupplier_currency());
									newModel.setDescription(oldModel
											.getDescription());
									newModel.setPayment_terms(oldModel
											.getPayment_terms());
									newModel.setBank_name(oldModel
											.getBank_name());
									newModel.setWebsite(oldModel.getWebsite());
									// newModel.setTax_group(oldModel
									// .getTax_group());
									newModel.setContact_person(oldModel
											.getContact_person());
									newModel.setContact_person_fax(oldModel
											.getContact_person_fax());
									newModel.setContact_person_email(oldModel
											.getContact_person_email());

									newModel.setLedger(objModel);

									modelVector.add(newModel);
								} else {
									System.out.println("Skipped   "
											+ item.getItemProperty(
													SUPPLIER_NAME).getValue()
													.toString());
								}
							}
						}

						try {
							if (modelVector.size() > 0) {
								dao.save(modelVector);
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);
							} else {
								setRequiredError(
										table,
										getPropertyName("select_supplier")
												+ toOfficeComboField
														.getItemCaption(toOfficeComboField
																.getValue()),
										true);
							}
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"),
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

			list.addAll(new OrganizationDao().getAllOrganizations());

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

	private void loadOffices(SComboField field, long orgId) {
		try {
			List list = new ArrayList();

			list.addAll(new OfficeDao().getAllOfficeNamesUnderOrg(orgId));

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
			setRequiredError(fromOfficeComboField,
					getPropertyName("invalid_selection"), true);
			valid = false;
		}
		if (toOfficeComboField.getValue() == null
				|| toOfficeComboField.getValue().equals("")) {
			setRequiredError(toOfficeComboField,
					getPropertyName("invalid_selection"), true);
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
			setRequiredError(table, getPropertyName("invalid_data"), true);
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
