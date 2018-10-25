package com.migration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.unit.dao.UnitDao;
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
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Anil K P.
 * 
 *         WebSpark.
 * 
 *         Nov 19, 2013
 */
public class MigrateLedgerUI extends SparkLogic {

	private static final long serialVersionUID = 1421097679696133680L;

	private STable table;

	private SButton saveButton;

	private static final String TRANSFER = "Transfer";
	private static final String LEDGER_ID = "Ledger ID";
	private static final String LEDGER_NAME = "Ledger Name";
	private static final String LEDGER_GROUP = "Ledger Group";

	private String allHeaders[];
	private String requiredHeaders[];

	private SComboField fromOfficeComboField;

	private SCheckBox selectAllBox;
	
	STextField officeId;
	

	private LedgerDao dao;

	private SComboField fromOrgField;

	public MigrateLedgerUI() {

		dao = new LedgerDao();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SHorizontalLayout buttonLayout = new SHorizontalLayout();
		buttonLayout.setSizeFull();

		fromOrgField = new SComboField("From Organization", 200);
		loadOrganizations();
		
		
		
		officeId=new STextField("Office ID", 200);
		
		
		
		

//		if (!isSystemAdmin() && !isSuperAdmin()) {
//			fromOrgField.setVisible(false);
//			toOrgField.setVisible(false);
//		}

		fromOfficeComboField = new SComboField("From Office");
		fromOfficeComboField.setInputPrompt("-------------Select-----------");
		loadOffices(fromOfficeComboField, getOrganizationID());

		selectAllBox = new SCheckBox("Select All");

		allHeaders = new String[] { TRANSFER, LEDGER_ID, LEDGER_NAME,
				LEDGER_GROUP };
		requiredHeaders = new String[] { TRANSFER, LEDGER_NAME, LEDGER_GROUP };

		table = new STable(null, 400, 300);

		table.addContainerProperty(TRANSFER, SCheckBox.class, null, TRANSFER,
				null, Align.CENTER);
		table.addContainerProperty(LEDGER_ID, Long.class, null, LEDGER_ID,
				null, Align.CENTER);
		table.addContainerProperty(LEDGER_NAME, String.class, null,
				LEDGER_NAME, null, Align.LEFT);
		table.addContainerProperty(LEDGER_GROUP, String.class, null,
				LEDGER_GROUP, null, Align.LEFT);

		table.setVisibleColumns(requiredHeaders);

		saveButton = new SButton("Copy");

		mainFormLayout.addComponent(fromOrgField);
		mainFormLayout.addComponent(fromOfficeComboField);
		mainFormLayout.addComponent(selectAllBox);
		mainFormLayout.addComponent(table);
		mainFormLayout.addComponent(officeId);
		
		mainFormLayout.addComponent(saveButton);
		
		fromOrgField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				clearErrorMessages();
				loadOffices(fromOfficeComboField,
						(Long) fromOrgField.getValue());
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
								.getAllActiveGeneralLedgerOnly(toLong(fromOfficeComboField
										.getValue().toString()));
						SCheckBox transferBox = null;
						LedgerModel model = null;
						for (int i = 0; i < list.size(); i++) {
							transferBox = new SCheckBox();
							model = (LedgerModel) list.get(i);
							Object items[] = new Object[] { transferBox,
									model.getId(), model.getName(),
									model.getGroup().getName() };
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

						while (iter.hasNext()) {
							Item item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());
							box.setValue(true);
						}
					} else {
						while (iter.hasNext()) {
							Item item = table.getItem(iter.next());
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
						
						saveButton.setEnabled(false);
						
						LedgerModel objModel = null;
						
						long ofc_id=toLong(officeId.getValue());
						long org_id=(Long) fromOrgField.getValue();
						
						long to_ofc_id=toLong(officeId.getValue());

						Vector modelVector = new Vector();
						Iterator iter = table.getItemIds().iterator();
						List idsList=new ArrayList();
						while (iter.hasNext()) {
							Item item = table.getItem(iter.next());
							SCheckBox box = ((SCheckBox) item.getItemProperty(
									TRANSFER).getValue());
							
							idsList.add(toLong(item.getItemProperty(LEDGER_ID).getValue()
									.toString()));
							
						}
						JDBCSave daoObj=new JDBCSave();
						
						
						List list=dao.getAllLedgersFromIDList(idsList);
						List grps=new GroupDao().getAllActiveGroups(org_id);
						
						daoObj.saveLedgers(list, to_ofc_id, grps, 1);
						
						List customerList=new CustomerDao().getAllActiveCustomers(ofc_id);
						
						daoObj.saveCustomer(customerList, to_ofc_id);
						
						
						List supplierList=new SupplierDao().getAllActiveSuppliers(ofc_id);
						
						
						daoObj.saveSupplier(supplierList, to_ofc_id);
						
						
						List itnGPs=new ItemGroupDao().getAllItemGroups(org_id);
						List itnSGPs=new ItemSubGroupDao().getAllActiveItemSubGroupsUnderOrg(org_id);
						List itemsList=new ItemDao().getAllActiveItemsFromOfc(ofc_id);
						
						List unitList=new UnitDao().getAllActiveUnitsFromOrg(org_id);
						
						daoObj.saveItems(itnGPs, itnSGPs, itemsList, unitList, to_ofc_id, 1);
						
						Notification.show("Success",
								"Migrated Successfully..!",
								Type.WARNING_MESSAGE);
						
					}	

							
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				saveButton.setEnabled(true);
			}

		});
		setContent(mainFormLayout);
		setSizeFull();
	}

	private void clearErrorMessages() {
		fromOfficeComboField.setComponentError(null);
		table.setComponentError(null);
	}


	private void loadOrganizations() {
		try {
			List list = new ArrayList();

			list.addAll(new OrganizationDao().getAllOrganizations());

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			fromOrgField.setContainerDataSource(bic);
			fromOrgField.setItemCaptionPropertyId("name");
			fromOrgField.setValue(getOrganizationID());
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

	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		return null;
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

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Cannot be empty", true);
			valid = false;
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
}
