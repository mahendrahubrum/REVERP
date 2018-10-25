package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
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
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad
 * 
 *         WebSpark.
 * 
 *         Sep 18, 2013
 */
public class CopyItemsUI extends SparkLogic {

	private static final long serialVersionUID = -7848426914622351124L;

	private STable table;

	private SButton saveButton;

	private static final String TRANSFER = "Transfer";
	private static final String ITEM_ID = "Item ID";
	private static final String ITEM_NAME = "Item Name";
	private static final String ITEM_CODE = "Item Code";

	private String allHeaders[];
	private String requiredHeaders[];

	private SComboField fromOfficeComboField;
	private SComboField toOfficeComboField;

	private SCheckBox selectAllBox;

	private ItemDao dao;
	private STabSheet tab;
	
	private SComboField fromOrgField;
	private SComboField toOrgField;

	@Override
	public SPanel getGUI() {
		setSize(600, 600);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		
		SPanel itemPanel = new SPanel();
		itemPanel.setSizeFull();
		SPanel ledgerPanel = new SPanel();
		ledgerPanel.setSizeFull();
		SPanel customerPanel = new SPanel();
		customerPanel.setSizeFull();
		SPanel supplierPanel = new SPanel();
		supplierPanel.setSizeFull();

		dao = new ItemDao();
		tab=new STabSheet(null);
		tab.setSizeFull();
		tab.addTab(itemPanel,"Item");
		tab.addTab(ledgerPanel,"Ledger");
		tab.addTab(customerPanel,"Customer");
		tab.addTab(supplierPanel,"Supplier");

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

		allHeaders = new String[] { TRANSFER, ITEM_ID, ITEM_NAME, ITEM_CODE };
		requiredHeaders = new String[] { TRANSFER, ITEM_NAME, ITEM_CODE };

		table = new STable(null, 400, 300);
		table.addContainerProperty(TRANSFER, SCheckBox.class, null, TRANSFER,
				null, Align.CENTER);
		table.addContainerProperty(ITEM_ID, Long.class, null, ITEM_ID, null,
				Align.CENTER);
		table.addContainerProperty(ITEM_NAME, String.class, null, ITEM_NAME,
				null, Align.LEFT);
		table.addContainerProperty(ITEM_CODE, String.class, null, ITEM_CODE,
				null, Align.LEFT);

		table.setVisibleColumns(requiredHeaders);

		saveButton = new SButton("Save");

		mainFormLayout.addComponent(fromOrgField);
		mainFormLayout.addComponent(fromOfficeComboField);
		mainFormLayout.addComponent(toOrgField);
		mainFormLayout.addComponent(toOfficeComboField);
		mainFormLayout.addComponent(selectAllBox);
		mainFormLayout.addComponent(table);
		mainFormLayout.addComponent(saveButton);

		itemPanel.setContent(mainFormLayout);
		
		ledgerPanel.setContent(new CopyLedgerUI());
		customerPanel.setContent(new CopyCustomerUI());
		supplierPanel.setContent(new CopySupplierUI());
		
		panel.setContent(tab);
		
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
								.getAllActiveItemsList(toLong(fromOfficeComboField
										.getValue().toString()));
						SCheckBox transferBox = null;
						ItemModel model = null;
						for (int i = 0; i < list.size(); i++) {
							transferBox = new SCheckBox();
							model = (ItemModel) list.get(i);
							Object items[] = new Object[] { transferBox,
									model.getId(), model.getName(),
									model.getItem_code() };
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
						ItemModel newModel = null;
						ItemModel oldModel = null;

						Vector modelVector = new Vector();
						Iterator iter = table.getItemIds().iterator();
						Item item;
						while (iter.hasNext()) {
							item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());

							if (box.getValue()) {
								oldModel = dao.getItem(toLong(item
										.getItemProperty(ITEM_ID).getValue()
										.toString()));

								if (!dao.isAlreadyExists(
										toLong(toOfficeComboField.getValue()
												.toString()), item
												.getItemProperty(ITEM_NAME)
												.getValue().toString(), item
												.getItemProperty(ITEM_CODE)
												.getValue().toString())) {

									newModel = new ItemModel();
									newModel.setName(item
											.getItemProperty(ITEM_NAME)
											.getValue().toString());
									newModel.setItem_code(item
											.getItemProperty(ITEM_CODE)
											.getValue().toString());
									newModel.setCess_enabled(oldModel
											.getCess_enabled());
									newModel.setCurrent_balalnce(0);
									newModel.setOffice(new S_OfficeModel(
											toLong(toOfficeComboField
													.getValue().toString())));
									newModel.setOpening_balance(0);
									newModel.setPurchaseTax(oldModel
											.getPurchaseTax());
									newModel.setReorder_level(oldModel
											.getReorder_level());
									newModel.setSalesTax(oldModel.getSalesTax());
									newModel.setStatus(SConstants.statuses.ITEM_ACTIVE);
									newModel.setSub_group(oldModel
											.getSub_group());
									newModel.setUnit(oldModel.getUnit());

									modelVector.add(newModel);
								} else {
									System.out.println("Skipped   "
											+ item.getItemProperty(ITEM_NAME)
													.getValue().toString());
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
										"Select items that are not present in "
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
//		
//		tab.addSelectedTabChangeListener(new SelectedTabChangeListener() {
//			@Override
//			public void selectedTabChange(SelectedTabChangeEvent event) {
//				
//				if(tab.getSelectedTab().getId().equals("Item")){
//				}
//				else if(tab.getSelectedTab().getId().equals("Customer")) {
//				}
//				else if(tab.getSelectedTab().getId().equals("Supplier")) {
//				}
//			}
//		});
		
		
		return panel;
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

	@Override
	public Boolean isValid() {
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

	@Override
	public Boolean getHelp() {
		return null;
	}

}
