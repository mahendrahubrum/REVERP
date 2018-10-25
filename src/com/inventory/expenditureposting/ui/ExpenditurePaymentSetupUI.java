package com.inventory.expenditureposting.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.expenditureposting.dao.ExpenditurePaymentSetupDao;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupDetailsModel;
import com.inventory.expenditureposting.model.ExpenditurePaymentSetupModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;

/**
 * @author Jinshad P.T. WebSpark. Apr 7 2014
 */

public class ExpenditurePaymentSetupUI extends SparkLogic {

	private static final long serialVersionUID = 2561215323382808817L;

	SPanel panel = null;

	static String TSR_SN = "SN";

	static String TSJ_TYPE_ID = "Type ID";
	static String TSJ_TYPE_NAME = "Type Name";
	static String TSJ_FROM_LEDGER_ID = "From Account ID";
	static String TSJ_FROM_LEDGER_NAME = "From Account";
	static String TSJ_TO_LEDGER_ID = "To Account ID";
	static String TSJ_TO_LEDGER_NAME = "To Account";
	static String TSJ_AMOUNT = "Amount";
	static String TSJ_NARRATION = "Narration";
	static final long EXPENSE = 4;

	STable accountDepositEntryTable;
	ExpenditurePaymentSetupDao daoObj;

	SGridLayout masterDetailsGrid;
	SGridLayout accountDepositAddGrid;
	SVerticalLayout stkrkVLay;

	SComboField accountDepositNumberList;

	SComboField accountHeadFilter;

	SComboField fromAcctCombo;

	SNativeSelect accountTypeSelect;

	SComboField expendetureAccSelect;

	SNativeButton addItemButton;
	SNativeButton updateItemButton;
	STextField amountTextField, narration, groupName;
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;

	LedgerDao ledgerDao;

	SButton createNewButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub

		List lst = new ArrayList();
		lst.add(new KeyValue(1, "Expenditure"));
		lst.add(new KeyValue(2, "Salary Advance Payment"));
		accountTypeSelect = new SNativeSelect(getPropertyName("type"), 100,
				lst, "intKey", "value");

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		daoObj = new ExpenditurePaymentSetupDao();
		ledgerDao = new LedgerDao();

		addItemButton = new SNativeButton(getPropertyName("add"));
		updateItemButton = new SNativeButton(getPropertyName("Update"));

		saveButton = new SButton(getPropertyName("Save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("Update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("Delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
		mainButtonLayout.addComponent(updateButton);
		mainButtonLayout.addComponent(deleteButton);

		updateButton.setVisible(false);
		deleteButton.setVisible(false);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(1000, 550);
		try {

			updateItemButton.setVisible(false);

			accountDepositEntryTable = new STable(null, 800, 160);

			accountDepositNumberList = new SComboField(null, 200);

			loadData(0);

			expendetureAccSelect = new SComboField(
					getPropertyName("expenditure_account"), 160);

			List actList = null;
			if ((Boolean) getHttpSession().getAttribute(
					"expendeture_acct_cash_only") == true) {
				actList = ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						getSettings().getCASH_GROUP());
			} else {
				actList = new ArrayList();
				actList.addAll(ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						getSettings().getCASH_GROUP()));
				actList.addAll(ledgerDao.getAllLedgersUnderGroupAndSubGroups(
						getOfficeID(), getOrganizationID(),
						getSettings().getCASH_GROUP()));
			}

			fromAcctCombo = new SComboField(getPropertyName("from_account"),
					160, actList, "id", "name", true, getPropertyName("select"));
			groupName = new STextField(null, 200);

			accountDepositAddGrid = new SGridLayout();
			stkrkVLay = new SVerticalLayout();

			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			accountDepositEntryTable.setSizeFull();
			accountDepositEntryTable.setSelectable(true);
			accountDepositEntryTable.setMultiSelect(true);

			accountDepositEntryTable.setWidth("900px");
			accountDepositEntryTable.setHeight("200px");

			accountDepositEntryTable.addContainerProperty(TSR_SN,
					Integer.class, null, "#", null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_ID,
					Long.class, null, TSJ_FROM_LEDGER_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_FROM_LEDGER_NAME,
					String.class, null, getPropertyName("from_account"), null,
					Align.LEFT);
			accountDepositEntryTable.addContainerProperty(TSJ_TO_LEDGER_ID,
					Long.class, null, TSJ_TO_LEDGER_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_TO_LEDGER_NAME,
					String.class, null, getPropertyName("to_account"), null,
					Align.LEFT);

			accountDepositEntryTable.addContainerProperty(TSJ_TYPE_ID,
					Integer.class, null, TSJ_TYPE_ID, null, Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_TYPE_NAME,
					String.class, null, getPropertyName("type_name"), null,
					Align.LEFT);
			accountDepositEntryTable.addContainerProperty(TSJ_AMOUNT,
					Double.class, null, getPropertyName("amount"), null,
					Align.CENTER);
			accountDepositEntryTable.addContainerProperty(TSJ_NARRATION,
					String.class, null, getPropertyName("narration"), null,
					Align.CENTER);

			accountDepositEntryTable.setColumnExpandRatio(TSR_SN, (float) .5);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_FROM_LEDGER_NAME,
					2);
			accountDepositEntryTable.setColumnExpandRatio(TSJ_AMOUNT,
					(float) 1.5);

			accountDepositAddGrid.setColumns(8);
			accountDepositAddGrid.setRows(2);

			amountTextField = new STextField(getPropertyName("amount"), 80);
			narration = new STextField(getPropertyName("narration"), 80);

			accountDepositAddGrid.addComponent(accountTypeSelect, 1, 1);
			accountDepositAddGrid.addComponent(fromAcctCombo, 2, 1);
			accountDepositAddGrid.addComponent(expendetureAccSelect, 3, 1);
			accountDepositAddGrid.addComponent(amountTextField, 4, 1);
			accountDepositAddGrid.addComponent(narration, 5, 1);
			accountDepositAddGrid.addComponent(addItemButton, 6, 1);
			accountDepositAddGrid.addComponent(updateItemButton, 7, 1);

			accountDepositAddGrid.setStyleName("journal_adding_grid");
			amountTextField.setStyleName("textfield_align_right");

			accountDepositAddGrid.setComponentAlignment(addItemButton,
					Alignment.BOTTOM_RIGHT);
			accountDepositAddGrid.setComponentAlignment(updateItemButton,
					Alignment.BOTTOM_RIGHT);

			accountDepositAddGrid.setSizeFull();

			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("group")), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(accountDepositNumberList);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("group_name")), 3, 0);
			masterDetailsGrid.addComponent(groupName, 4, 0);

			// masterDetailsGrid.addComponent(new SLabel("Pay To :"), 3, 1);
			// masterDetailsGrid.addComponent(, 4, 1);
			// masterDetailsGrid.addComponent(new
			// SLabel(getPropertyName("exp_account")), 1, 1);
			// masterDetailsGrid.addComponent(expendetureAccSelect, 2, 1);

			masterDetailsGrid.setSpacing(true);

			masterDetailsGrid.setColumnExpandRatio(1, 3);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 2);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			accountDepositAddGrid.setSpacing(true);

			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(accountDepositEntryTable);

			stkrkVLay.addComponent(accountDepositAddGrid);
			stkrkVLay.setComponentAlignment(accountDepositEntryTable,
					Alignment.MIDDLE_CENTER);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,
					Alignment.BOTTOM_CENTER);
			stkrkVLay.setComponentAlignment(accountDepositAddGrid,
					Alignment.BOTTOM_CENTER);

			accountDepositEntryTable.setVisibleColumns(new String[] { TSR_SN,
					TSJ_TYPE_NAME, TSJ_FROM_LEDGER_NAME, TSJ_TO_LEDGER_NAME,
					TSJ_AMOUNT, TSJ_NARRATION });

			accountDepositEntryTable.setFooterVisible(true);
			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(0)));
			accountDepositEntryTable.setColumnFooter(TSJ_FROM_LEDGER_NAME,
					getPropertyName("total"));

			panel.setContent(stkrkVLay);

			expendetureAccSelect.focus();

			accountTypeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								if (accountTypeSelect.getValue() != null) {
									List lst;
									if ((Integer) accountTypeSelect.getValue() == 1) {
										lst = ledgerDao
												.getAllDirectAddedLedgersUnderType(
														getOfficeID(), EXPENSE);

									} else {
										lst = ledgerDao
												.getAllUserLedgers(getOfficeID());
									}

									SCollectionContainer bic = SCollectionContainer
											.setList(lst, "id");
									expendetureAccSelect
											.setContainerDataSource(bic);
									expendetureAccSelect
											.setItemCaptionPropertyId("name");

								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			expendetureAccSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (expendetureAccSelect.getValue() != null) {
									expendetureAccSelect.setDescription("<i class='ledger_bal_style'>"
											+ getPropertyName("current_balance")
											+ " : "
											+ ledgerDao
													.getLedgerCurrentBalance((Long) expendetureAccSelect
															.getValue())
											+ "</i>");
								} else
									expendetureAccSelect.setDescription(null);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			fromAcctCombo
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (fromAcctCombo.getValue() != null) {
									fromAcctCombo.setDescription("<i class='ledger_bal_style'>"
											+ getPropertyName("current_balance")
											+ " : "
											+ ledgerDao
													.getLedgerCurrentBalance((Long) fromAcctCombo
															.getValue())
											+ "</i>");
								} else
									fromAcctCombo.setDescription(null);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					accountDepositNumberList.setValue((long) 0);
				}
			});

			accountDepositNumberList
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {
								accountDepositEntryTable.removeAllItems();
								if (accountDepositNumberList.getValue() != null
										&& !accountDepositNumberList.getValue()
												.toString().equals("0")) {

									updateButton.setVisible(true);
									deleteButton.setVisible(true);
									saveButton.setVisible(false);

									ExpenditurePaymentSetupModel objModel = daoObj
											.getSetup((Long) accountDepositNumberList
													.getValue());

									groupName.setValue(objModel.getGroup_name());

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_ID,
													TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_ID,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_ID,
													TSJ_TO_LEDGER_NAME,
													TSJ_AMOUNT, TSJ_NARRATION });

									String type = "";
									int ct = 0;
									Iterator it = objModel.getDetails_list()
											.iterator();
									while (it.hasNext()) {
										ExpenditurePaymentSetupDetailsModel det = (ExpenditurePaymentSetupDetailsModel) it
												.next();
										ct++;
										if (det.getType() == 1)
											type = "Expenditure";
										else
											type = "Salary Payment";

										accountDepositEntryTable.addItem(
												new Object[] {
														ct,
														det.getType(),
														type,
														det.getFrom_account()
																.getId(),
														det.getFrom_account()
																.getName(),
														det.getTo_account()
																.getId(),
														det.getTo_account()
																.getName(),
														det.getAmount(),
														det.getComments() }, ct);

									}

									accountDepositEntryTable
											.setVisibleColumns(new String[] {
													TSR_SN, TSJ_TYPE_NAME,
													TSJ_FROM_LEDGER_NAME,
													TSJ_TO_LEDGER_NAME,
													TSJ_AMOUNT, TSJ_NARRATION });

									updateItemButton.setVisible(false);
									addItemButton.setVisible(true);

								} else {
									groupName.setValue("");
									expendetureAccSelect.setValue(null);
									fromAcctCombo.setValue(null);

									updateButton.setVisible(false);
									deleteButton.setVisible(false);
									saveButton.setVisible(true);

									expendetureAccSelect.focus();

								}
								calculateTotals();

							} catch (Exception e) {
							}

						}
					});

			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							ExpenditurePaymentSetupModel objMdl = daoObj
									.getSetup((Long) accountDepositNumberList
											.getValue());

							objMdl.setGroup_name(groupName.getValue());
							objMdl.setOffice_id(getOfficeID());
							List<ExpenditurePaymentSetupDetailsModel> detList = new ArrayList<ExpenditurePaymentSetupDetailsModel>();
							ExpenditurePaymentSetupDetailsModel detObj = null;
							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());
								detObj = new ExpenditurePaymentSetupDetailsModel();
								int amtType = 1;
								detObj.setAmount((Double) item.getItemProperty(
										TSJ_AMOUNT).getValue());
								detObj.setComments((String) item
										.getItemProperty(TSJ_NARRATION)
										.getValue());
								detObj.setFrom_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_FROM_LEDGER_ID).getValue()));
								detObj.setTo_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_TO_LEDGER_ID).getValue()));
								detObj.setType((Integer) item.getItemProperty(
										TSJ_TYPE_ID).getValue());
								detList.add(detObj);
							}
							objMdl.setDetails_list(detList);
							daoObj.update(objMdl);

							saveActivity(
									getOptionId(),
									"Expenditure Transaction Setup Updated. No : "
											+ accountDepositNumberList
													.getItemCaption(accountDepositNumberList
															.getValue()));

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							loadData(objMdl.getId());

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (accountDepositNumberList.getValue() != null
							&& !accountDepositNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) accountDepositNumberList
														.getValue());

												saveActivity(
														getOptionId(),
														"Expenditure Transaction Deleted. Bill No : "
																+ accountDepositNumberList
																		.getItemCaption(accountDepositNumberList
																				.getValue())
																+ ", Expenditure Acct. : "
																+ expendetureAccSelect
																		.getItemCaption(expendetureAccSelect
																				.getValue())
																+ ", Payment Amount : "
																+ roundNumber(toDouble(accountDepositEntryTable
																		.getColumnFooter(
																				TSJ_AMOUNT)
																		.toString())));

												Notification
														.show(getPropertyName("Success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadData(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			accountDepositEntryTable
					.addListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							Collection selectedItems = null;

							if (accountDepositEntryTable.getValue() != null) {
								selectedItems = (Collection) accountDepositEntryTable
										.getValue();
							}

							if (selectedItems != null
									&& selectedItems.size() == 1) {

								Item item = accountDepositEntryTable
										.getItem(selectedItems.iterator()
												.next());

								accountTypeSelect.setValue(item
										.getItemProperty(TSJ_TYPE_ID)
										.getValue());

								fromAcctCombo.setValue(item.getItemProperty(
										TSJ_FROM_LEDGER_ID).getValue());
								expendetureAccSelect.setValue(item
										.getItemProperty(TSJ_TO_LEDGER_ID)
										.getValue());
								amountTextField
										.setValue(asString(item
												.getItemProperty(TSJ_AMOUNT)
												.getValue()));
								narration.setValue(asString(item
										.getItemProperty(TSJ_NARRATION)
										.getValue()));

								updateItemButton.setVisible(true);
								addItemButton.setVisible(false);

								fromAcctCombo.focus();
							} else {
								updateItemButton.setVisible(false);
								addItemButton.setVisible(true);
								narration.setValue("");
								fromAcctCombo.setValue(null);
								amountTextField.setValue("");
								fromAcctCombo.focus();
							}
						}
					});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(accountDepositEntryTable, null, false);
						if (isAddingValid()) {

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_TYPE_ID, TSJ_TYPE_NAME,
											TSJ_FROM_LEDGER_ID,
											TSJ_FROM_LEDGER_NAME,
											TSJ_TO_LEDGER_ID,
											TSJ_TO_LEDGER_NAME, TSJ_AMOUNT,
											TSJ_NARRATION });

							Collection selectedItems = null;

							if (accountDepositEntryTable.getValue() != null) {
								selectedItems = (Collection) accountDepositEntryTable
										.getValue();
							}

							int sel_id = (Integer) selectedItems.iterator()
									.next();
							Item item = accountDepositEntryTable
									.getItem(sel_id);

							item.getItemProperty(TSJ_FROM_LEDGER_ID).setValue(
									fromAcctCombo.getValue());
							item.getItemProperty(TSJ_FROM_LEDGER_NAME)
									.setValue(
											fromAcctCombo
													.getItemCaption(fromAcctCombo
															.getValue()));
							item.getItemProperty(TSJ_TO_LEDGER_ID).setValue(
									expendetureAccSelect.getValue());
							item.getItemProperty(TSJ_TO_LEDGER_NAME)
									.setValue(
											expendetureAccSelect
													.getItemCaption(expendetureAccSelect
															.getValue()));
							item.getItemProperty(TSJ_TYPE_ID).setValue(
									accountTypeSelect.getValue());
							item.getItemProperty(TSJ_TYPE_NAME).setValue(
									accountTypeSelect
											.getItemCaption(accountTypeSelect
													.getValue()));

							double amt = toDouble(amountTextField.getValue());

							item.getItemProperty(TSJ_AMOUNT).setValue(amt);
							item.getItemProperty(TSJ_NARRATION).setValue(
									narration.getValue());

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_TYPE_NAME,
											TSJ_FROM_LEDGER_NAME,
											TSJ_TO_LEDGER_NAME, TSJ_AMOUNT,
											TSJ_NARRATION });

							updateItemButton.setVisible(false);
							addItemButton.setVisible(true);
							narration.setValue("");
							accountDepositEntryTable.setValue(null);

							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			addItemButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						setRequiredError(accountDepositEntryTable, null, false);
						if (isAddingValid()) {

							boolean exist = false;
							int exist_id = 0;
							double total_qty = 0;
							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_TYPE_ID, TSJ_TYPE_NAME,
											TSJ_FROM_LEDGER_ID,
											TSJ_FROM_LEDGER_NAME,
											TSJ_TO_LEDGER_ID,
											TSJ_TO_LEDGER_NAME, TSJ_AMOUNT,
											TSJ_NARRATION });

							int id = 0, ct = accountDepositEntryTable
									.getItemIds().size();
							Iterator it1 = accountDepositEntryTable
									.getItemIds().iterator();
							while (it1.hasNext()) {
								id = (Integer) it1.next();
							}

							id++;
							ct++;

							double amt = toDouble(amountTextField.getValue());

							accountDepositEntryTable.addItem(
									new Object[] {
											ct,
											(Integer) accountTypeSelect
													.getValue(),
											accountTypeSelect
													.getItemCaption(accountTypeSelect
															.getValue()),
											(Long) fromAcctCombo.getValue(),
											fromAcctCombo
													.getItemCaption(fromAcctCombo
															.getValue()),
											(Long) expendetureAccSelect
													.getValue(),
											expendetureAccSelect
													.getItemCaption(expendetureAccSelect
															.getValue()), amt,
											narration.getValue() }, id);

							accountDepositEntryTable
									.setVisibleColumns(new String[] { TSR_SN,
											TSJ_TYPE_NAME,
											TSJ_FROM_LEDGER_NAME,
											TSJ_TO_LEDGER_NAME, TSJ_AMOUNT,
											TSJ_NARRATION });

							fromAcctCombo.setValue(null);
							amountTextField.setValue("");
							fromAcctCombo.focus();
							narration.setValue("");
							calculateTotals();

						}
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {

						if (isValid()) {

							ExpenditurePaymentSetupModel objMdl = new ExpenditurePaymentSetupModel();

							objMdl.setGroup_name(groupName.getValue());
							objMdl.setOffice_id(getOfficeID());
							objMdl.setStatus(1);
							List<ExpenditurePaymentSetupDetailsModel> detList = new ArrayList<ExpenditurePaymentSetupDetailsModel>();
							ExpenditurePaymentSetupDetailsModel detObj = null;
							Iterator it = accountDepositEntryTable.getItemIds()
									.iterator();
							while (it.hasNext()) {
								Item item = accountDepositEntryTable.getItem(it
										.next());
								detObj = new ExpenditurePaymentSetupDetailsModel();
								int amtType = 1;
								detObj.setAmount((Double) item.getItemProperty(
										TSJ_AMOUNT).getValue());
								detObj.setComments((String) item
										.getItemProperty(TSJ_NARRATION)
										.getValue());
								detObj.setFrom_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_FROM_LEDGER_ID).getValue()));
								detObj.setTo_account(new LedgerModel(
										(Long) item.getItemProperty(
												TSJ_TO_LEDGER_ID).getValue()));
								detObj.setType((Integer) item.getItemProperty(
										TSJ_TYPE_ID).getValue());
								detList.add(detObj);
							}
							objMdl.setDetails_list(detList);

							long id = daoObj.save(objMdl);

							saveActivity(getOptionId(),
									"Expenditure Settings Saved. ID : "
											+ objMdl.getId());

							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							loadData(id);

						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			accountDepositEntryTable.addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					accountDepositEntryTable.setValue(null);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadData(0);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			accountDepositEntryTable.addShortcutListener(new ShortcutListener(
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

			accountDepositEntryTable.addActionHandler(new Action.Handler() {
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

			accountTypeSelect.setValue((int) 1);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return panel;
	}

	public void loadData(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new ExpenditurePaymentSetupModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllSetups(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			accountDepositNumberList.setContainerDataSource(bic);
			accountDepositNumberList.setItemCaptionPropertyId("group_name");

			accountDepositNumberList.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	public void deleteItem() {
		try {

			if (accountDepositEntryTable.getValue() != null) {

				Collection selectedItems = (Collection) accountDepositEntryTable
						.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					accountDepositEntryTable.removeItem(it1.next());
				}

				int SN = 0;
				Iterator it = accountDepositEntryTable.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					Item newitem = accountDepositEntryTable
							.getItem((Integer) it.next());

					newitem.getItemProperty(TSR_SN).setValue(SN);

				}
				calculateTotals();
			}
			accountDepositEntryTable.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isAddingValid() {
		// TODO Auto-generated method stub
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
				// TODO: handle exception
			}
		}

		if (fromAcctCombo.getValue() == null
				|| fromAcctCombo.getValue().equals("")) {
			setRequiredError(fromAcctCombo,
					getPropertyName("invalid_selection"), true);
			fromAcctCombo.focus();
			ret = false;
		} else
			setRequiredError(fromAcctCombo, null, false);

		return ret;
	}

	public void calculateTotals() {
		try {

			double amtttl = 0;

			Iterator it = accountDepositEntryTable.getItemIds().iterator();
			while (it.hasNext()) {
				Item item = accountDepositEntryTable.getItem(it.next());

				amtttl += (Double) item.getItemProperty(TSJ_AMOUNT).getValue();
			}

			accountDepositEntryTable.setColumnFooter(TSJ_AMOUNT,
					asString(roundNumber(amtttl)));

		} catch (Exception e) {
			// TODO: handle exception
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		boolean ret = true;

		if (accountDepositEntryTable.getItemIds().size() <= 0) {
			setRequiredError(accountDepositEntryTable,
					getPropertyName("add_some_items"), true);
			expendetureAccSelect.focus();
			ret = false;
		} else
			setRequiredError(accountDepositEntryTable, null, false);

		if (groupName.getValue() == null || groupName.getValue().equals("")) {
			setRequiredError(groupName, getPropertyName("invalid_data"), true);
			groupName.focus();
			ret = false;
		} else
			setRequiredError(groupName, null, false);

		boolean g = true || false;

		return ret;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
