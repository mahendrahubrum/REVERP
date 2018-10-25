package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.LedgerOpeningBalanceDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.LedgerOpeningBalanceModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author anil
 * @date 04-Sep-2015
 * @Project REVERP
 */
public class LedgerOpeningBalanceUI extends SparkLogic {

	private static final long serialVersionUID = 378956692489948736L;

	SPanel panel = null;

	static String TSJ_LEDGER_ID = "Account ID";
	static String TSJ_LEDGER_NAME = "Account";
	static String TSJ_AMOUNT = "Amount";
	static String TSJ_TYPE_ID = "Type Id";
	static String TSJ_TYPE = "Type";
	static String TSJ_DATE = "Date";
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;

	STable table;
	LedgerDao daoObj;

	SHorizontalLayout addGrid;
	SVerticalLayout stkrkVLay;

	SComboField ledgerSelect;

	 SNativeSelect accountTypseSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField amountTextField;
	SDateField date;

	SettingsValuePojo settings;

	SButton saveButton;
	LedgerOpeningBalanceDao dao;
	boolean update;

	@SuppressWarnings({ "deprecation", "serial", "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {
		allHeaders=new Object[]{  TSJ_LEDGER_ID, TSJ_LEDGER_NAME, TSJ_AMOUNT ,TSJ_TYPE_ID,TSJ_TYPE,TSJ_DATE};
		requiredHeaders=new Object[]{  TSJ_LEDGER_NAME, TSJ_AMOUNT ,TSJ_TYPE,TSJ_DATE};
		
		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");

		daoObj = new LedgerDao();

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("Update"));

		saveButton = new SButton(getPropertyName("save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));


		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
		date = new SDateField(null, 120, getDateFormat(), getWorkingDate());

		panel = new SPanel();
		panel.setSizeFull();

		setSize(700, 580);
		try {

			dao=new LedgerOpeningBalanceDao();
			updateItemButton.setVisible(false);

			table = new STable(null, 500, 300);

			ledgerSelect = new SComboField(null, 150,
					daoObj.getAllActiveGeneralLedgerOnly(getOfficeID()), "id", "name", true, getPropertyName("select"));
			
			addGrid = new SHorizontalLayout();
			stkrkVLay = new SVerticalLayout();

			table.setSizeFull();
			table.setSelectable(true);
			table.setMultiSelect(false);


			table.addContainerProperty(TSJ_LEDGER_ID,Long.class, null, TSJ_LEDGER_ID, null, Align.CENTER);
			table.addContainerProperty(TSJ_TYPE_ID,Integer.class, null, TSJ_TYPE_ID, null, Align.CENTER);
			table.addContainerProperty(TSJ_LEDGER_NAME,String.class, null, getPropertyName("account"), null,Align.LEFT);
			table.addContainerProperty(TSJ_TYPE,String.class, null,TSJ_TYPE, null,Align.LEFT);
			table.addContainerProperty(TSJ_DATE,Date.class, null,TSJ_DATE, null,Align.LEFT);
			table.addContainerProperty(TSJ_AMOUNT,Double.class, null, getPropertyName("amount"), null,
					Align.CENTER);
			table.setColumnExpandRatio(TSJ_LEDGER_NAME, 2f);
			
			amountTextField = new STextField(null, 80);
			amountTextField.setValue("0");
			
			accountTypseSelect=new SNativeSelect(null,80,SConstants.amountTypesWithId,
					"intKey", "value");
			accountTypseSelect.setValue(1);
			

			addGrid.setSpacing(true);
			addGrid.addComponent(new SLabel(getPropertyName("ledger")));
			addGrid.addComponent(ledgerSelect);
			addGrid.addComponent(new SLabel(getPropertyName("type")));
			addGrid.addComponent(accountTypseSelect);
			addGrid.addComponent(new SLabel(getPropertyName("amount")));
			addGrid.addComponent(amountTextField);
			addGrid.addComponent(new SLabel(getPropertyName("date")));
			addGrid.addComponent(date);
			addGrid.addComponent(addItemButton);
			addGrid.addComponent(updateItemButton);

			amountTextField.setStyleName("textfield_align_right");

			addGrid.setComponentAlignment(addItemButton,
					Alignment.BOTTOM_RIGHT);
			addGrid.setComponentAlignment(updateItemButton,
					Alignment.BOTTOM_RIGHT);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(table);

			stkrkVLay.addComponent(addGrid);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(addGrid,
					Alignment.BOTTOM_CENTER);

			table.setVisibleColumns(requiredHeaders);

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(stkrkVLay);

			panel.setContent(hLayout);
			
			loadTableData();

			addShortcutListener(new ShortcutListener("Delete",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});
			
			table.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
				}
			});


			table.addShortcutListener(new ShortcutListener(
					"Submit Item", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			final Action actionDeleteStock = new Action(
					getPropertyName("Delete"));

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});
			
			ledgerSelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					amountTextField.selectAll();
					amountTextField.focus();
					if(ledgerSelect.getValue()!=null){
						long classId;
						try {
							classId = daoObj.getClassOfLedger((Long)ledgerSelect.getValue());
						if(classId==SConstants.account_parent_groups.ASSET||classId==SConstants.account_parent_groups.EXPENSE)
							accountTypseSelect.setValue(2);
						else
							accountTypseSelect.setValue(1);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					}
				}
			});

			table.addValueChangeListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							if (table.getValue() != null) {

								Item item = table
										.getItem(table.getValue());

								ledgerSelect.setValue(item.getItemProperty(TSJ_LEDGER_ID).getValue());
								amountTextField.setValue(asString(item.getItemProperty(TSJ_AMOUNT).getValue()));
								accountTypseSelect.setValue(asString(item.getItemProperty(TSJ_TYPE_ID).getValue()));
								date.setValue((Date)item.getItemProperty(TSJ_DATE).getValue());
								updateItemButton.setVisible(true);
								addItemButton.setVisible(false);
								
							} else {
								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);
								accountTypseSelect.setValue(1);
								ledgerSelect.setValue(null);
								amountTextField.setValue("0");
								date.setValue(getWorkingDate());
							}
						}
					});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(table, null, false);
						update=true;
						if (isAddingValid()) {
						
							table.setVisibleColumns(allHeaders);
							if (table.getValue() != null) {
							Item item = table
									.getItem(table.getValue());
							item.getItemProperty(TSJ_TYPE_ID).setValue(accountTypseSelect.getValue());
							item.getItemProperty(TSJ_TYPE).setValue(accountTypseSelect.getItemCaption(accountTypseSelect.getValue()));
							item.getItemProperty(TSJ_LEDGER_ID).setValue(ledgerSelect.getValue());
							item.getItemProperty(TSJ_LEDGER_NAME).setValue(ledgerSelect.getItemCaption(ledgerSelect.getValue()));
							item.getItemProperty(TSJ_AMOUNT).setValue(toDouble(amountTextField.getValue()));
							item.getItemProperty(TSJ_DATE).setValue(date.getValue());

							table.setVisibleColumns(requiredHeaders);

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);
							table.setValue(null);
						}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(table, null, false);
						update=false;
						if (isAddingValid()) {

							table.setVisibleColumns(allHeaders);

							double amt = toDouble(amountTextField.getValue());
							table.addItem(
									new Object[] {
											(Long) ledgerSelect.getValue(),
											ledgerSelect.getItemCaption(ledgerSelect.getValue()), 
											amt ,accountTypseSelect.getValue(),
											 accountTypseSelect.getItemCaption(accountTypseSelect.getValue()),date.getValue()},
											 table
												.getItemIds().size()+1);

							table.setVisibleColumns(requiredHeaders);
							ledgerSelect.setValue(null);
							amountTextField.setValue("0");
							ledgerSelect.focus();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							LedgerOpeningBalanceModel mdl;
							List lst=new ArrayList();
							Iterator iter=table.getItemIds().iterator();
							Item item;
							while (iter.hasNext()) {
								item = table.getItem((Object) iter.next());
								mdl=new LedgerOpeningBalanceModel();
								if((Integer)item.getItemProperty(TSJ_TYPE_ID).getValue()==1)
									mdl.setAmount(-toDouble(item.getItemProperty(TSJ_AMOUNT).getValue().toString()));
								else
									mdl.setAmount(toDouble(item.getItemProperty(TSJ_AMOUNT).getValue().toString()));
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TSJ_DATE).getValue()));
								mdl.setLedger(new LedgerModel((Long)item.getItemProperty(TSJ_LEDGER_ID).getValue()));
								mdl.setType((Integer)item.getItemProperty(TSJ_TYPE_ID).getValue());
								lst.add(mdl);
							}

							dao.save(lst,getOfficeID());
							loadTableData();
							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}


	private void loadTableData() {
	try {
		
		table.removeAllItems();
		
		table.setVisibleColumns(allHeaders);
		List list=dao.getOpeningBalanceList(getOfficeID());
		LedgerOpeningBalanceModel mdl;
		Iterator iter=list.iterator();
		while (iter.hasNext()) {
			mdl = (LedgerOpeningBalanceModel) iter.next();
			String type="CR";
			if(mdl.getType()==2)
				type="DR";
			table.addItem(
						new Object[] { mdl.getLedger().getId(),
								mdl.getLedger().getName(), Math.abs(mdl.getAmount()),
								mdl.getType(), type,mdl.getDate() }, table.getItemIds()
								.size() + 1);
		}
		table.setVisibleColumns(requiredHeaders);
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteItem() {
		try {

			if (table.getValue() != null) {

					table.removeItem(table.getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isAddingValid() {
		boolean ret = true;

		if (amountTextField.getValue() == null
				|| amountTextField.getValue().equals("")) {
			setRequiredError(amountTextField, getPropertyName("invalid_data"),
					true);
			amountTextField.focus();
			ret = false;
		} else {
			try {
				if (toDouble(amountTextField.getValue()) < 0) {
					setRequiredError(amountTextField,
							getPropertyName("invalid_data"), true);
					amountTextField.focus();
					ret = false;
				} else
					setRequiredError(amountTextField, null, false);
			} catch (Exception e) {
				setRequiredError(amountTextField,
						getPropertyName("invalid_data"), true);
				amountTextField.focus();
				ret = false;
			}
		}

		if (ledgerSelect.getValue() == null
				|| ledgerSelect.getValue().equals("")) {
			setRequiredError(ledgerSelect,
					getPropertyName("invalid_selection"), true);
			ledgerSelect.focus();
			ret = false;
		} else
			setRequiredError(ledgerSelect, null, false);
		
		if(ret&&!update){
			
			Iterator iter=table.getItemIds().iterator();
			Item item;
			boolean flag=true;
			while (iter.hasNext()) {
				
				item = table.getItem((Object) iter.next());
				if((Long)item.getItemProperty(TSJ_LEDGER_ID).getValue()==(Long)ledgerSelect.getValue()&&
						(item.getItemProperty(TSJ_DATE).getValue().equals(date.getValue()))){
					flag=false;
					break;
				}
			}
			if(!flag){
				setRequiredError(ledgerSelect, "Opening balance already added for this date", true);
				ret=false;
			}else
				ledgerSelect.setComponentError(null);
			
		}
		
		return ret;
	}


	@Override
	public Boolean isValid() {
		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table,
					getPropertyName("add_some_items"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
}
