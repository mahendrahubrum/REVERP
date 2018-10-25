package com.inventory.budget.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.budget.dao.BudgetDao;
import com.inventory.budget.dao.BudgetLVDao;
import com.inventory.budget.model.BudgetLVChildModel;
import com.inventory.budget.model.BudgetLVMasterModel;
import com.inventory.budget.model.BudgetModel;
import com.inventory.config.acct.dao.LedgerDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 25, 2014
 */
public class BudgetLVUI extends SparkLogic {

	private static final long serialVersionUID = 529190348093896182L;
	UserManagementDao umDao;
	SHorizontalLayout horizontalLayout;
	SHorizontalLayout horLayout;
	SHorizontalLayout hLayout;
	SVerticalLayout verticalLayout;
	SHorizontalLayout buttonLayout;
	SComboBox budgetCombo;
	STextField budgetAmount;
	SDateField date;
	STable entryTable;
	SDateField from_date;
	SDateField to_date;
	STextField reference_no;
	STextField amount;
	STextField totalAmount;
	STextField variationAmount;
	SButton addIcon;
	SButton updateIcon;
	SButton save;
	SButton update;
	SButton delete;
	BudgetDao budgetDao;
	BudgetLVDao LVDao;
	STextField personName;
	SCheckBox bank;
	SComboField bankCombo;
	long depId = 0;

	@Override
	public SPanel getGUI() {
		budgetDao = new BudgetDao();
		LVDao = new BudgetLVDao();
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("1000");
		setHeight("700");
		layout.setSpacing(true);
		panel.setContent(layout);
		LedgerDao ledgerDao = new LedgerDao();
		// budgetCombo = new SComboField("Budget :", 200);
		budgetCombo = new SComboField(getPropertyName("budget"), 200);
		umDao = new UserManagementDao();

		List list;
		try {
			depId = umDao.getDepartment(getUserID());
			list = budgetDao.getAllActiveBudgetsBudgetDefinitionAndDepartment(
					getOfficeID(), depId);
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			budgetCombo.setContainerDataSource(bic);
			budgetCombo.setItemCaptionPropertyId("jobName");
			budgetCombo.setInputPrompt(getPropertyName("select"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for (int i=0; i<entryTable.getItemIds().size(); i++)
		// totlAmnt += toDouble(amount.getValue()) ;
		// System.out.println("Total Amount : " + totlAmnt);

		budgetAmount = new STextField(getPropertyName("amount"), 200);
		date = new SDateField(getPropertyName("date"), 200);
		date.setValue(CommonUtil.getCurrentSQLDate());
		entryTable = new STable();
		entryTable.setWidth("800");
		entryTable.addContainerProperty("From Date", java.util.Date.class,
				null, getPropertyName("from_date"), null, Align.CENTER);
		entryTable.addContainerProperty("Person's name", String.class, null,
				getPropertyName("person_name"), null, Align.CENTER);
		entryTable.addContainerProperty("Ref. No", String.class, null,
				getPropertyName("ref_no"), null, Align.CENTER);
		entryTable.addContainerProperty("Amount", Double.class, null,
				getPropertyName("amount"), null, Align.CENTER);
		entryTable.setFooterVisible(true);
		entryTable.setColumnFooter("Ref. No", getPropertyName("total"));
		entryTable.setSelectable(true);
		from_date = new SDateField(getPropertyName("from_date"), 200);
		from_date.setValue(CommonUtil.getCurrentSQLDate());
		to_date = new SDateField(getPropertyName("to_date"), 200);
		to_date.setValue(CommonUtil.getCurrentSQLDate());
		reference_no = new STextField(getPropertyName("ref_no"), 200);
		amount = new STextField(getPropertyName("amount"), 200);
		addIcon = new SButton();
		addIcon.setStyleName("addItemBtnStyle");
		addIcon.setDescription(getPropertyName("enter"));
		updateIcon = new SButton();
		updateIcon.setStyleName("updateItemBtnStyle");
		updateIcon.setDescription(getPropertyName("update"));
		// addIcon.setIcon(new ThemeResource("icons/add_item.png"));
		totalAmount = new STextField(getPropertyName("total"), 200);
		variationAmount = new STextField(getPropertyName("variation"), 200);
		personName = new STextField(getPropertyName("person_name"), 200);
		bank = new SCheckBox(getPropertyName("activate_bank"));
		bank.setImmediate(true);
		bankCombo = new SComboField(null, 200);
		try {
			List lt = ledgerDao.getAllActiveGeneralLedgerOnly(getOfficeID());
			SCollectionContainer bic = SCollectionContainer.setList(lt, "id");
			bankCombo.setContainerDataSource(bic);
			bankCombo.setItemCaptionPropertyId("name");
			bankCombo.setInputPrompt(getPropertyName("select"));
			// bankCombo =new SComboField(null, 200, ledgerDao.
			// getAllLedgersUnderGroupAndSubGroups(getOfficeID(),
			// getOrganizationID(), SConstants.BANK_ACCOUNT_GROUP_ID), "id",
			// "name",true, "Select");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		save = new SButton(getPropertyName("save"));
		update = new SButton(getPropertyName("update"));
		delete = new SButton(getPropertyName("delete"));

		horizontalLayout = new SHorizontalLayout();
		horizontalLayout.setSpacing(true);
		horLayout = new SHorizontalLayout();
		horLayout.setSpacing(true);
		hLayout = new SHorizontalLayout();
		hLayout.setSpacing(true);
		verticalLayout = new SVerticalLayout();
		verticalLayout.setSpacing(true);
		verticalLayout.setWidth("600");
		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("800");

		hLayout.addComponent(bank);
		hLayout.addComponent(bankCombo);
		bankCombo.setVisible(false);
		hLayout.addComponent(verticalLayout);
		verticalLayout.addComponent(totalAmount);
		verticalLayout.setComponentAlignment(totalAmount,
				Alignment.MIDDLE_CENTER);
		verticalLayout.addComponent(variationAmount);
		verticalLayout.setComponentAlignment(variationAmount,
				Alignment.MIDDLE_CENTER);

		horizontalLayout.addComponent(budgetCombo);
		horizontalLayout.addComponent(budgetAmount);
		horizontalLayout.addComponent(date);

		horLayout.addComponent(from_date);
		horLayout.addComponent(personName);
		horLayout.addComponent(reference_no);
		horLayout.addComponent(amount);
		horLayout.addComponent(addIcon);
		horLayout.addComponent(updateIcon);
		updateIcon.setVisible(false);

		// buttonLayout.addComponent(save);
		// buttonLayout.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
		buttonLayout.addComponent(update);
		buttonLayout.addComponent(delete);
		buttonLayout.setComponentAlignment(update, Alignment.MIDDLE_CENTER);
		// update.setVisible(false);
		delete.setVisible(false);
		layout.addComponent(horizontalLayout);
		layout.addComponent(entryTable);
		layout.addComponent(horLayout);
		layout.addComponent(hLayout);
		layout.addComponent(buttonLayout);

		budgetCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				BudgetModel budgetmdl;
				BudgetLVMasterModel mastermdl = null;
				BudgetLVChildModel childmdl;

				if (budgetCombo.getValue() != null
						&& !budgetCombo.getValue().equals("")
						&& !budgetCombo.getValue().toString().equals("0")) {

					try {

						addIcon.setVisible(true);
						from_date.setValue(CommonUtil.getCurrentSQLDate());
						// to_date.setValue(CommonUtil.getCurrentSQLDate());
						personName.setValue("");
						reference_no.setValue("");
						amount.setValue("");
						updateIcon.setVisible(false);

						budgetmdl = budgetDao.getBudgetModel((Long) budgetCombo
								.getValue());

						if (budgetmdl != null) {

							bank.setValue(false);
							bankCombo.setValue(null);
							entryTable.removeAllItems();
							budgetAmount.setReadOnly(false);
							budgetAmount.setValue(Double.toString(budgetmdl
									.getAmount()));
							budgetAmount.setReadOnly(true);

							List lis = LVDao
									.getbudgetMasterList((Long) budgetCombo
											.getValue());
							for (int m = 0; m < lis.size(); m++) {
								mastermdl = (BudgetLVMasterModel) lis.get(m);
								for (int n = 0; n < mastermdl
										.getInventory_details_list().size(); n++) {
									childmdl = mastermdl
											.getInventory_details_list().get(n);
									// String fromDate =
									// CommonUtil.getUtilDateFromSQLDate(childmdl.getFrom_date());
									// String toDate =
									// CommonUtil.getUtilDateFromSQLDate(childmdl.getTo_date());
									entryTable.addItem(
											new Object[] {
													CommonUtil
															.getUtilFromSQLDate(childmdl
																	.getFrom_date()),

													// CommonUtil.getUtilFromSQLDate(childmdl.getTo_date()),
													// childmdl.getTo_date().getDate(),
													childmdl.getPerson_name(),
													childmdl.getRef_no(),
													childmdl.getAmount(),

											},
											entryTable.getItemIds().size() + 1);
									System.out.println("from date : "
											+ childmdl.getFrom_date());

								}

								// }

								// }

							}

							double totalAnt = 0;

							List liss = (List) entryTable.getItemIds();
							for (int i = 0; i < liss.size(); i++) {
								Item item;
								item = entryTable.getItem(liss.get(i));
								totalAnt += toDouble(item
										.getItemProperty("Amount").getValue()
										.toString());
								// totlAmnt +=
								// toDouble(entryTable.getItem(it.next()).getItemProperty("Amount").getValue().toString())
								// ;
								System.out.println(item.getItemProperty(
										"Amount").getValue());
							}
							entryTable.setColumnFooter("Amount",
									Double.toString(totalAnt));
							System.out.println("updated Amount : " + totalAnt);
							totalAmount.setReadOnly(false);
							totalAmount.setValue(Double.toString(totalAnt));
							totalAmount.setReadOnly(true);

							double variationAmt = 0;
							variationAmount.setReadOnly(false);
							variationAmt = toDouble(budgetAmount.getValue())
									- toDouble(totalAmount.getValue());
							variationAmount
									.setValue(asString(roundNumber(variationAmt)));
							// totalAmnt +=
							// toDouble(item.getItemProperty("Amount").getValue().toString());
							variationAmount.setReadOnly(true);
							if (mastermdl.getBank() != 0) {
								bank.setValue(true);
								bankCombo.setVisible(true);
								bankCombo.setValue(mastermdl.getBank());
								System.out.println(mastermdl.getBank());
							}

						}
						// if( entryTable.getValue() != null){
						//
						// // save.setVisible(false);
						//
						//
						// addIcon.setVisible(true);
						// updateIcon.setVisible(false);
						// }

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				else {

					budgetCombo.setValue(null);

					amount.setValue("");
					date.setValue(CommonUtil.getCurrentSQLDate());

				}
			}

		});

		addIcon.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					try {

						entryTable.addItem(new Object[] { from_date.getValue(),
								personName.getValue(), reference_no.getValue(),
								toDouble(amount.getValue()) }, entryTable
								.getItemIds().size() + 1);
						double totlAmnt = 0;
						// Iterator it=entryTable.getItemIds().iterator();
						// while(it.hasNext()) {
						// Item itm=entryTable.getItem(it.next());
						// totlAmnt +=
						// toDouble(itm.getItemProperty("Amount").getValue().toString())
						// ;
						//
						// }
						List lis = (List) entryTable.getItemIds();
						for (int i = 0; i < lis.size(); i++) {
							Item itm;
							itm = entryTable.getItem(lis.get(i));
							totlAmnt += toDouble(itm.getItemProperty("Amount")
									.getValue().toString());
							// totlAmnt +=
							// toDouble(entryTable.getItem(it.next()).getItemProperty("Amount").getValue().toString())
							// ;
						}
						entryTable.setColumnFooter("Amount",
								Double.toString(totlAmnt));
						System.out.println("Total Amount : " + totlAmnt);
						totalAmount.setReadOnly(false);
						totalAmount.setValue(Double.toString(totlAmnt));

						double variationAmt = 0;
						variationAmount.setReadOnly(false);
						variationAmt = toDouble(budgetAmount.getValue())
								- toDouble(totalAmount.getValue());
						variationAmount
								.setValue(asString(roundNumber(variationAmt)));
						// totalAmnt +=
						// toDouble(item.getItemProperty("Amount").getValue().toString());
						variationAmount.setReadOnly(true);

						totalAmount.setReadOnly(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				from_date.setValue(CommonUtil.getCurrentSQLDate());
				to_date.setValue(CommonUtil.getCurrentSQLDate());
				reference_no.setValue("");
				personName.setValue("");
				amount.setValue("");
			}

		});

		entryTable.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (entryTable.getValue() != null) {
					Item itm = entryTable.getItem(entryTable.getValue());

					String amountcalc = itm.getItemProperty("Amount")
							.getValue().toString(); // TODO: Do something with
													// this value.
					BudgetLVUI.this.amount.setValue(amountcalc);

					String ref_n = itm.getItemProperty("Ref. No").getValue()
							.toString();
					reference_no.setValue(ref_n);

					Date f_dat = (Date) itm.getItemProperty("From Date")
							.getValue();
					from_date.setValue(f_dat);

					String p_name = itm.getItemProperty("Person's name")
							.getValue().toString();
					personName.setValue(p_name);
					// Date t_dat = (Date)
					// itm.getItemProperty("To Date").getValue();
					// to_date.setValue(t_dat);
					addIcon.setVisible(false);
					updateIcon.setVisible(true);

				} else {
					addIcon.setVisible(true);
					updateIcon.setVisible(false);
					from_date.setValue(CommonUtil.getCurrentSQLDate());
					to_date.setValue(CommonUtil.getCurrentSQLDate());
					reference_no.setValue("");
					personName.setValue("");
					amount.setValue("");
				}

			}
		});

		updateIcon.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (isValid()) {
					try {

						// entryTable.addItem(new Object[] {
						// from_date.getValue(),to_date.getValue(),reference_no.getValue(),
						// toDouble(amount.getValue())},
						// entryTable.getItemIds().size()+1);
						Item itm;
						itm = entryTable.getItem(entryTable.getValue());

						itm.getItemProperty("From Date").setValue(
								from_date.getValue());
						itm.getItemProperty("Person's name").setValue(
								personName.getValue());
						itm.getItemProperty("Ref. No").setValue(
								reference_no.getValue());

						itm.getItemProperty("Amount").setValue(
								toDouble(amount.getValue()));

						// Iterator it=entryTable.getItemIds().iterator();
						// while(it.hasNext()) {
						// Item itm=entryTable.getItem(it.next());
						// totlAmnt +=
						// toDouble(itm.getItemProperty("Amount").getValue().toString())
						// ;
						//
						// }

						double totalAmnt = 0;

						List lis = (List) entryTable.getItemIds();
						for (int i = 0; i < lis.size(); i++) {
							Item item;
							item = entryTable.getItem(lis.get(i));
							totalAmnt += toDouble(item
									.getItemProperty("Amount").getValue()
									.toString());
							// totlAmnt +=
							// toDouble(entryTable.getItem(it.next()).getItemProperty("Amount").getValue().toString())
							// ;
							System.out.println(item.getItemProperty("Amount")
									.getValue());
						}
						entryTable.setColumnFooter("Amount",
								Double.toString(totalAmnt));
						System.out.println("updated Amount : " + totalAmnt);
						totalAmount.setReadOnly(false);
						totalAmount.setValue(Double.toString(totalAmnt));
						totalAmount.setReadOnly(true);

						double variationAt = 0;
						variationAmount.setReadOnly(false);
						variationAt = toDouble(budgetAmount.getValue())
								- toDouble(totalAmount.getValue());
						variationAmount
								.setValue(asString(roundNumber(variationAt)));
						// totalAmnt +=
						// toDouble(item.getItemProperty("Amount").getValue().toString());
						variationAmount.setReadOnly(true);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				from_date.setValue(CommonUtil.getCurrentSQLDate());
				to_date.setValue(CommonUtil.getCurrentSQLDate());
				reference_no.setValue("");
				amount.setValue("");

				personName.setValue("");
				addIcon.setVisible(true);
				updateIcon.setVisible(false);
				entryTable.setValue(null);
			}
		});

		save.addClickListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				try {

					// budgetmdl = budgetDao
					// .getBudgetModel((Long) budgetCombo
					// .getValue());
					BudgetLVMasterModel mastermdl = new BudgetLVMasterModel();
					BudgetLVChildModel childmdl = new BudgetLVChildModel();
					mastermdl.setBudget_id(new BudgetModel((Long) budgetCombo
							.getValue()));
					mastermdl.setTotal_amt(Double.parseDouble(totalAmount
							.getValue()));
					mastermdl.setDate(CommonUtil.getSQLDateFromUtilDate(date
							.getValue()));

					List<BudgetLVChildModel> childList = new ArrayList<BudgetLVChildModel>();
					List lis = (List) entryTable.getItemIds();
					Iterator listit = entryTable.getItemIds().iterator();
					while (listit.hasNext()) {
						childmdl = new BudgetLVChildModel();
						Item itmm = entryTable.getItem(listit.next());
						childmdl.setFrom_date(CommonUtil
								.getSQLDateFromUtilDate((Date) itmm
										.getItemProperty("From Date")
										.getValue()));
						childmdl.setTo_date(CommonUtil
								.getSQLDateFromUtilDate((Date) itmm
										.getItemProperty("From Date")
										.getValue()));
						childmdl.setPerson_name(itmm
								.getItemProperty("Person's name").getValue()
								.toString());
						childmdl.setRef_no(itmm.getItemProperty("Ref. No")
								.getValue().toString());
						childmdl.setAmount(((Double) itmm.getItemProperty(
								"Amount").getValue()));
						childList.add(childmdl);
					}
					mastermdl.setInventory_details_list(childList);

					// childmdl.setFrom_date(CommonUtil.getSQLDateFromUtilDate(from_date.getValue()));
					// childmdl.setTo_date(CommonUtil.getSQLDateFromUtilDate(to_date.getValue()));
					// childmdl.setRef_no(reference_no.getValue());
					// childmdl.setAmount(Double.parseDouble(amount.getValue()));

					LVDao.save(mastermdl);
					// loadBudget(mdl.getId());
					// Iterator it=entryTable.getItemIds().iterator();
					// while(it.hasNext()) {
					// Item itm=entryTable.getItem(it.next());
					// totlAmnt +=
					// toDouble(itm.getItemProperty("Amount").getValue().toString())
					// ;
					//
					// }
					SNotification.show(getPropertyName("save_success"),
							Type.TRAY_NOTIFICATION);

					double variationAmunt = 0;
					variationAmount.setReadOnly(false);
					variationAmunt = toDouble(budgetAmount.getValue())
							- toDouble(totalAmount.getValue());
					variationAmount
							.setValue(asString(roundNumber(variationAmunt)));
					// totalAmnt +=
					// toDouble(item.getItemProperty("Amount").getValue().toString());
					variationAmount.setReadOnly(true);

					// save.setVisible(false);
					update.setVisible(true);
					// delete.setVisible(true);
				} catch (Exception e) {
					SNotification.show(getPropertyName("unable_to_save"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}

		});

		update.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {

					BudgetLVMasterModel mastermdl = new BudgetLVMasterModel();
					BudgetLVChildModel childmdl;
					if (mastermdl != null) {
						mastermdl.setBudget_id(new BudgetModel(
								(Long) budgetCombo.getValue()));
						mastermdl.setTotal_amt(Double.parseDouble(totalAmount
								.getValue()));
						mastermdl.setDate(CommonUtil
								.getSQLDateFromUtilDate(date.getValue()));
						if (bankCombo.getValue() != null) {
							mastermdl.setBank((Long) bankCombo.getValue());
						}

						mastermdl
								.setOffice_id(new S_OfficeModel(getOfficeID()));
						List<BudgetLVChildModel> childList = new ArrayList<BudgetLVChildModel>();
						List lis = (List) entryTable.getItemIds();
						Iterator listit = entryTable.getItemIds().iterator();
						while (listit.hasNext()) {
							childmdl = new BudgetLVChildModel();
							Item itmm = entryTable.getItem(listit.next());
							childmdl.setFrom_date(CommonUtil
									.getSQLDateFromUtilDate((Date) itmm
											.getItemProperty("From Date")
											.getValue()));
							childmdl.setTo_date(CommonUtil
									.getSQLDateFromUtilDate((Date) itmm
											.getItemProperty("From Date")
											.getValue()));
							childmdl.setPerson_name(itmm
									.getItemProperty("Person's name")
									.getValue().toString());
							childmdl.setRef_no(itmm.getItemProperty("Ref. No")
									.getValue().toString());
							childmdl.setAmount(((Double) itmm.getItemProperty(
									"Amount").getValue()));
							childList.add(childmdl);
						}
						mastermdl.setInventory_details_list(childList);

						LVDao.update(mastermdl);

						SNotification.show(getPropertyName("update_success"),
								Type.TRAY_NOTIFICATION);

						double variationAt = 0;
						BudgetLVUI.this.variationAmount.setReadOnly(false);
						variationAt = toDouble(budgetAmount.getValue())
								- toDouble(totalAmount.getValue());
						variationAmount
								.setValue(asString(roundNumber(variationAt)));
						// totalAmnt +=
						// toDouble(item.getItemProperty("Amount").getValue().toString());
						variationAmount.setReadOnly(true);

						// save.setVisible(false);
						update.setVisible(true);
						// delete.setVisible(true);
					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("update_unable"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});

		// delete.addClickListener(new ClickListener() {
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		// ConfirmDialog.show(getUI().getCurrent(), "Are you sure?",
		// new ConfirmDialog.Listener() {
		//
		// @Override
		// public void onClose(ConfirmDialog arg0) {
		// if (arg0.isConfirmed()) {
		// BudgetLVChildModel childmdl;
		// BudgetLVMasterModel mastermdl;
		//
		//
		// try {
		//
		// mastermdl = LVDao
		// .getBudgetLVMasterModel((Long) budgetCombo
		// .getValue());
		// LVDao.delete(mastermdl);
		//
		// SNotification.show(
		// "Deleted Successfull",
		// Type.TRAY_NOTIFICATION);
		//
		//
		// }
		//
		// catch (Exception e) {
		// SNotification.show("Unable to Delete",
		// Type.ERROR_MESSAGE);
		// e.printStackTrace();
		// }
		// }
		// }
		//
		// });
		//
		// }
		// });

		final Action actionDelete = new Action("Delete");

		entryTable.addActionHandler(new Action.Handler() {
			@Override
			public Action[] getActions(final Object target, final Object sender) {
				// if(deleteItemButton.isVisible())
				// deleteItemButton.click();
				return new Action[] { actionDelete };
			}

			@Override
			public void handleAction(final Action action, final Object sender,
					final Object target) {
				deleteItem();
			}

		});

		bank.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean value = (Boolean) event.getProperty().getValue();

				if (value == true) {
					bankCombo.setVisible(true);
				} else {
					bankCombo.setValue(null);
					bankCombo.setVisible(false);
				}
				System.out.println("Hai" + value);

			}
		});

		return panel;
	}

	public void deleteItem() {
		try {

			Item itm;
			itm = entryTable.getItem(entryTable.getValue());
			if (entryTable.getValue() != null) {

				entryTable.removeItem(entryTable.getValue());
				double totalAmnt = 0;

				List lis = (List) entryTable.getItemIds();
				for (int i = 0; i < lis.size(); i++) {
					Item item;
					item = entryTable.getItem(lis.get(i));
					totalAmnt += toDouble(item.getItemProperty("Amount")
							.getValue().toString());
					// totlAmnt +=
					// toDouble(entryTable.getItem(it.next()).getItemProperty("Amount").getValue().toString())
					// ;
					System.out.println(item.getItemProperty("Amount")
							.getValue());
				}
				entryTable
						.setColumnFooter("Amount", Double.toString(totalAmnt));
				System.out.println("updated Amount : " + totalAmnt);
				totalAmount.setReadOnly(false);
				totalAmount.setValue(Double.toString(totalAmnt));
				totalAmount.setReadOnly(true);
				addIcon.setVisible(true);
				updateIcon.setVisible(false);
				from_date.setValue(CommonUtil.getCurrentSQLDate());
				to_date.setValue(CommonUtil.getCurrentSQLDate());
				personName.setValue("");
				reference_no.setValue("");
				amount.setValue("");

				// int SN = 0;
				// Item newitem;
				// Iterator it = table.getItemIds().iterator();
				// while (it.hasNext()) {
				// SN++;
				//
				// newitem = table.getItem((Integer) it.next());
				//
				// newitem.getItemProperty(TBC_SN).setValue(SN);

			}

			// }
			// itemSelectCombo.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show(getPropertyName("delete_unable"),
					Type.ERROR_MESSAGE);
		}
	}

	@Override
	public Boolean isValid() {
		reference_no.setComponentError(null);
		amount.setComponentError(null);

		personName.setComponentError(null);
		budgetCombo.setComponentError(null);

		boolean flag = true;
		if (reference_no.getValue() == null
				|| reference_no.getValue().equals("")) {
			setRequiredError(reference_no, getPropertyName("enter_valid_ref"),
					true);
			flag = false;
		}
		if (personName.getValue() == null || personName.getValue().equals("")) {
			setRequiredError(personName, getPropertyName("enter_valid_ref"),
					true);
			flag = false;
		}
		if (amount.getValue() == null || amount.getValue().equals("")) {
			setRequiredError(amount, getPropertyName("enter_valid_amount"),
					true);
			flag = false;
		} else {
			try {
				if (toDouble(amount.getValue()) <= 0) {
					setRequiredError(amount,
							getPropertyName("enter_valid_amount"), true);
					flag = false;
				}
			} catch (Exception e) {
				setRequiredError(amount, getPropertyName("enter_valid_amount"),
						true);
				flag = false;
			}
		}

		if (budgetCombo.getValue() == null || budgetCombo.getValue().equals("")) {
			setRequiredError(budgetCombo, getPropertyName("select_budget"),
					true);
			flag = false;
		}

		return flag;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
