package com.inventory.commissionsales.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.commissionsales.dao.CommissionSalesDao;
import com.inventory.commissionsales.dao.CustomerCommissionSalesDao;
import com.inventory.commissionsales.model.CommissionSalesCustomerDetailsModel;
import com.inventory.commissionsales.model.CustomerCommissionSalesModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
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
import com.webspark.Components.SComboSearchField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 25, 2013
 */
public class CustomerCommissionSalesUI extends SparkLogic {

	static String TBC_SN = "SN";
	static String TBC_CUSTOMER_ID = "customer_id";
	static String TBC_CUSTOMER_CODE = "Customer Code";
	static String TBC_CUSTOMER_NAME = "Customer Name";
	static String TBC_QTY = "Qty";
	static String TBC_UNIT_ID = "unit_id";
	static String TBC_UNIT = "Unit";
	static String TBC_UNIT_PRICE = "Unit Price";
	static String TBC_TAX_ID = "TaxID";
	static String TBC_TAX_PERC = "TaxPerc";
	static String TBC_TAX_AMT = "TaxAmt";
	static String TBC_NET_PRICE = "Net Price";

	CustomerCommissionSalesDao daoObj;

	private SComboField commissionSaleNumberList;

	SPanel pannel;
	SVerticalLayout hLayout;
	SFormLayout form;
	SComboSearchField a;

	STable table;

	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout bottomGrid;
	SGridLayout buttonsGrid;

	SComboField customerCompo;
	STextField quantityTextField;
	SNativeSelect unitSelect;
	STextField unitPriceTextField;
	SNativeSelect taxSelect;
	// STextField discount;
	STextField netPriceTextField;

	SButton addItemButton;
	SButton updateItemButton;
	SButton savePOButton;
	SButton updatePOButton;
	SButton deletePOButton;
	SButton printButton;

	SettingsValuePojo settings;

	ItemDao itemDao = new ItemDao();

	SLabel qtyTotal;
	SLabel taxTotal;
	SLabel netTotal;

	STextField referenceNoTextField;
	SComboField employSelect;
	SComboField caontainerSalesSelect;
	SDateField date;

	STextField approximateAmtTextField;
	STextArea comment;

	private String[] allHeaders;
	private String[] requiredHeaders;

	boolean taxEnable = isTaxEnable();

	CommonMethodsDao comDao;
	CustomerDao customerDao;
	TaxDao taxDao;
	UnitDao untDao;

	SButton newSaleButton;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		taxEnable = isTaxEnable();

		comDao = new CommonMethodsDao();
		customerDao = new CustomerDao();
		taxDao = new TaxDao();
		untDao = new UnitDao();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("Add new Purchase Order");

		allHeaders = new String[] { TBC_SN, TBC_CUSTOMER_ID, TBC_CUSTOMER_CODE,
				TBC_CUSTOMER_NAME, TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
				TBC_UNIT_PRICE, TBC_TAX_ID, TBC_TAX_AMT, TBC_TAX_PERC,
				TBC_NET_PRICE };

		if (taxEnable) {
			requiredHeaders = new String[] { TBC_SN, TBC_CUSTOMER_NAME,
					TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE, TBC_TAX_AMT,
					TBC_NET_PRICE };
		} else {
			requiredHeaders = new String[] { TBC_SN, TBC_CUSTOMER_NAME,
					TBC_QTY, TBC_UNIT, TBC_UNIT_PRICE, TBC_NET_PRICE };
		}

		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute(
					"settings");

		setSize(1200, 600);

		daoObj = new CustomerCommissionSalesDao();

		pannel = new SPanel();
		hLayout = new SVerticalLayout();
		form = new SFormLayout();

		addingGrid = new SGridLayout();
		addingGrid.setSizeFull();
		addingGrid.setColumns(8);
		addingGrid.setRows(2);

		masterDetailsGrid = new SGridLayout();
		masterDetailsGrid.setSizeFull();
		masterDetailsGrid.setColumns(9);
		masterDetailsGrid.setRows(2);

		bottomGrid = new SGridLayout();
		bottomGrid.setSizeFull();
		bottomGrid.setColumns(8);
		bottomGrid.setRows(2);

		buttonsGrid = new SGridLayout();
		buttonsGrid.setSizeFull();
		buttonsGrid.setColumns(8);
		buttonsGrid.setRows(1);

		qtyTotal = new SLabel(null);
		taxTotal = new SLabel(null);
		netTotal = new SLabel(null);
		qtyTotal.setValue("0.0");
		taxTotal.setValue("0.0");
		netTotal.setValue("0.0");

		pannel.setSizeFull();
		form.setSizeFull();

		try {
			List list = new ArrayList();
			list.add(new CustomerCommissionSalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllPurchaseOrderNumbersAsRefNo(getOfficeID()));
			commissionSaleNumberList = new SComboField(null, 125, list, "id",
					"ref_no", false, getPropertyName("create_new"));

			referenceNoTextField = new STextField(null, 120);
			date = new SDateField(null, 120, getDateFormat(), new Date(
					getWorkingDate().getTime()));

			employSelect = new SComboField(
					null,
					160,
					new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
									getOfficeID(), getOrganizationID()), "id",
					"first_name", true, getPropertyName("select"));
			caontainerSalesSelect = new SComboField(null, 120,
					new CommissionSalesDao()
							.getAllActiveSalesNames(getOfficeID()), "id",
					"contr_no", true, getPropertyName("select"));

			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(commissionSaleNumberList);
			salLisrLay.addComponent(newSaleButton);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("comn_sales_no"), 40), 1, 0);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			masterDetailsGrid.addComponent(
					new SLabel(getPropertyName("ref_no")), 3, 0);
			masterDetailsGrid.addComponent(referenceNoTextField, 4, 0);
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")),
					6, 0);
			masterDetailsGrid.addComponent(date, 8, 0);
			masterDetailsGrid.setSpacing(true);
			masterDetailsGrid.setComponentAlignment(referenceNoTextField,
					Alignment.MIDDLE_LEFT);
			masterDetailsGrid
					.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
			// masterDetailsGrid.setComponentAlignment(netTotal,
			// Alignment.MIDDLE_RIGHT);

			masterDetailsGrid.setColumnExpandRatio(1, 1);
			masterDetailsGrid.setColumnExpandRatio(2, 2);
			masterDetailsGrid.setColumnExpandRatio(3, 1);
			masterDetailsGrid.setColumnExpandRatio(4, 2);
			masterDetailsGrid.setColumnExpandRatio(5, 1);
			masterDetailsGrid.setColumnExpandRatio(6, 2);

			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("container_sales_no")), 1, 1);
			masterDetailsGrid.addComponent(caontainerSalesSelect, 2, 1);
			masterDetailsGrid.addComponent(new SLabel(
					getPropertyName("employee")), 3, 1);
			masterDetailsGrid.addComponent(employSelect, 4, 1);

			masterDetailsGrid.setStyleName("master_border");

			customerCompo = new SComboField(
					getPropertyName("Customer"),
					250,
					new CustomerDao()
							.getAllActiveCustomerNamesWithLedgerID(getOfficeID()),
					"id", "name");

			quantityTextField = new STextField(getPropertyName("qty"), 60);
			quantityTextField.setStyleName("textfield_align_right");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 60,
					new UnitDao().getAllActiveUnits(getOrganizationID()), "id",
					"symbol");

			try {
				unitSelect.setValue(unitSelect.getItemIds().iterator().next());
			} catch (Exception e) {
				// TODO: handle exception
			}

			unitPriceTextField = new STextField(getPropertyName("unit_price"),
					100);
			unitPriceTextField.setValue("0.00");
			unitPriceTextField.setStyleName("textfield_align_right");

			if (taxEnable) {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80,
						taxDao.getAllActiveTaxesFromType(getOfficeID(),
								SConstants.tax.PURCHASE_TAX), "id", "name");
				taxSelect.setVisible(true);
			} else {
				taxSelect = new SNativeSelect(getPropertyName("tax"), 80, null,
						"id", "name");
				taxSelect.setVisible(false);
			}

			// discount=new STextField("Discount");
			netPriceTextField = new STextField(getPropertyName("net_price"),
					100);
			netPriceTextField.setValue("0.00");
			netPriceTextField.setStyleName("textfield_align_right");

			netPriceTextField.setReadOnly(true);
			addItemButton = new SButton(null, getPropertyName("add_item"));
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, getPropertyName("Update"));
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);

			SFormLayout buttonLay = new SFormLayout();
			buttonLay.addComponent(addItemButton);
			buttonLay.addComponent(updateItemButton);

			addingGrid.addComponent(customerCompo);
			addingGrid.addComponent(quantityTextField);
			addingGrid.addComponent(unitSelect);
			addingGrid.addComponent(unitPriceTextField);
			addingGrid.addComponent(taxSelect);
			addingGrid.addComponent(netPriceTextField);
			addingGrid.addComponent(buttonLay);

			addingGrid.setSpacing(true);

			addingGrid.setStyleName("po_border");

			form.setStyleName("po_style");

			table = new STable(null, 1000, 200);

			table.setMultiSelect(true);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER_ID, Long.class, null,
					TBC_CUSTOMER_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER_CODE, String.class, null,
					TBC_CUSTOMER_CODE, null, Align.CENTER);
			table.addContainerProperty(TBC_CUSTOMER_NAME, String.class, null,
					getPropertyName("customer_name"), null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null,
					getPropertyName("qty"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_ID, Long.class, null,
					TBC_UNIT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.CENTER);
			table.addContainerProperty(TBC_UNIT_PRICE, Double.class, null,
					getPropertyName("unit_price"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_ID, Long.class, null,
					TBC_TAX_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TAX_PERC, Double.class, null,
					getPropertyName("tax_perc"), null, Align.RIGHT);
			table.addContainerProperty(TBC_TAX_AMT, Double.class, null,
					getPropertyName("tax_amt"), null, Align.RIGHT);
			table.addContainerProperty(TBC_NET_PRICE, Double.class, null,
					getPropertyName("net_price"), null, Align.RIGHT);

			table.setColumnExpandRatio(TBC_SN, 1);
			table.setColumnExpandRatio(TBC_CUSTOMER_ID, 1);
			table.setColumnExpandRatio(TBC_CUSTOMER_CODE, 2);
			table.setColumnExpandRatio(TBC_CUSTOMER_NAME, 4);
			table.setColumnExpandRatio(TBC_QTY, 2);
			table.setColumnExpandRatio(TBC_UNIT_ID, 1);
			table.setColumnExpandRatio(TBC_UNIT, 1);
			table.setColumnExpandRatio(TBC_UNIT_PRICE, 2);
			table.setColumnExpandRatio(TBC_TAX_AMT, 1);
			table.setColumnExpandRatio(TBC_TAX_PERC, 1);
			table.setColumnExpandRatio(TBC_NET_PRICE, 3);

			table.setVisibleColumns(requiredHeaders);

			table.setSizeFull();
			table.setSelectable(true);
			// table.setEditable(true);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_CUSTOMER_NAME, getPropertyName("total"));
			table.setColumnFooter(TBC_QTY, asString(0.0));
			table.setColumnFooter(TBC_TAX_AMT, asString(0.0));
			table.setColumnFooter(TBC_NET_PRICE, asString(0.0));

			// Adjust the table height a bit
			table.setPageLength(table.size());

			table.setWidth("1130");
			table.setHeight("200");

			approximateAmtTextField = new STextField(null, 200, "0.0");
			approximateAmtTextField.setReadOnly(true);
			approximateAmtTextField.setStyleName("textfield_align_right");
			comment = new STextArea(null, 400, 70);

			bottomGrid.addComponent(new SLabel(""), 6, 0);
			bottomGrid.addComponent(
					new SLabel(getPropertyName("total_amount")), 6, 1);
			bottomGrid.addComponent(approximateAmtTextField, 7, 1);
			bottomGrid.addComponent(new SLabel(getPropertyName("comment")), 0,
					1);
			bottomGrid.addComponent(comment, 1, 1);

			bottomGrid.setComponentAlignment(approximateAmtTextField,
					Alignment.TOP_RIGHT);

			savePOButton = new SButton(getPropertyName("save"), 70);
			savePOButton.setStyleName("savebtnStyle");
			savePOButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			updatePOButton = new SButton(getPropertyName("update"), 80);
			updatePOButton
					.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updatePOButton.setStyleName("updatebtnStyle");

			deletePOButton = new SButton(getPropertyName("delete"), 78);
			deletePOButton
					.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deletePOButton.setStyleName("deletebtnStyle");

			printButton = new SButton(getPropertyName("print"), 78);

			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(savePOButton);
			mainButtonLayout.addComponent(updatePOButton);
			mainButtonLayout.addComponent(deletePOButton);
			mainButtonLayout.addComponent(printButton);

			updatePOButton.setVisible(false);
			deletePOButton.setVisible(false);
			printButton.setVisible(false);
			buttonsGrid.addComponent(mainButtonLayout, 4, 0);
			mainButtonLayout.setSpacing(true);

			form.addComponent(masterDetailsGrid);
			form.addComponent(table);
			form.addComponent(addingGrid);
			form.addComponent(bottomGrid);
			form.addComponent(buttonsGrid);

			form.setWidth("700");

			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			  
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			      
			pannel.setContent(windowNotif);

			caontainerSalesSelect.focus();


			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)commissionSaleNumberList.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					confirmBox.close();
				}
			};
			
			confirmBox.setClickListener(confirmListener);
			
			ClickListener clickListnr=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
						if(commissionSaleNumberList.getValue()!=null && !commissionSaleNumberList.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)commissionSaleNumberList.getValue(),
									"Customer Commission Sales : No. "+commissionSaleNumberList.getItemCaption(commissionSaleNumberList.getValue()));
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						}
						else
							Notification.show("Select an Invoice..!",
									"Select an Invoice for save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
						if(commissionSaleNumberList.getValue()!=null && !commissionSaleNumberList.getValue().toString().equals("0")) {
							confirmBox.open();
						}
						else
							Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else {
						try {
							helpPopup=new SHelpPopupView(getOptionId());
							popupLay.removeAllComponents();
							popupLay.addComponent(helpPopup);
							helpPopup.setPopupVisible(true);
							helpPopup.setHideOnMouseOut(false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			};
			
			windowNotif.setClickListener(clickListnr);
			
			caontainerSalesSelect
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			newSaleButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					commissionSaleNumberList.setValue((long) 0);
				}
			});

			savePOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							FinTransaction trans = new FinTransaction();

							CustomerCommissionSalesModel poObj = new CustomerCommissionSalesModel();

							List<CommissionSalesCustomerDetailsModel> itemsList = new ArrayList<CommissionSalesCustomerDetailsModel>();

							CommissionSalesCustomerDetailsModel invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new CommissionSalesCustomerDetailsModel();

								item = table.getItem(it.next());

								invObj.setCustomer(new LedgerModel((Long) item
										.getItemProperty(TBC_CUSTOMER_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());

								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTax_amount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTax_percentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTax_amount(0);
									invObj.setTax_percentage(0);
								}

								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

								itemsList.add(invObj);

								// trans.addTransaction(SConstants.CR,
								// invObj.getCustomer().getId(),
								// settings.getCASH_ACCOUNT(),
								// roundNumber((Double)
								// item.getItemProperty(TBC_NET_PRICE).getValue()));
								trans.addTransaction(SConstants.CR, settings
										.getSALES_ACCOUNT(), invObj
										.getCustomer().getId(),
										roundNumber((Double) item
												.getItemProperty(TBC_NET_PRICE)
												.getValue()));

							}

							poObj.setAmount(Double
									.parseDouble(approximateAmtTextField
											.getValue()));
							poObj.setComments(comment.getValue());
							poObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							poObj.setLogin_id((Long) employSelect.getValue());
							poObj.setOffice(new S_OfficeModel(getOfficeID()));
							poObj.setRef_no(referenceNoTextField.getValue());
							poObj.setStatus(1);
							poObj.setContainerId((Long) caontainerSalesSelect
									.getValue());
							poObj.setDetails_list(itemsList);

							poObj.setSales_no(getNextSequence(
									"Customer Commission Sales Id",
									getLoginID()));

							long id = daoObj.save(poObj, trans.getTransaction(
									SConstants.COMMISSION_SALES,
									CommonUtil.getSQLDateFromUtilDate(date
											.getValue())));

							saveActivity(
									getOptionId(),
									"New Purchase Order Created. Bill No : "
											+ poObj.getSales_no()
											+ ", Customer : "
											+ caontainerSalesSelect
													.getItemCaption(caontainerSalesSelect
															.getValue())
											+ ", Approximate Amount : "
											+ poObj.getAmount(),poObj.getId());

							loadPO(id);

							Notification.show(getPropertyName("success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification
								.show(getPropertyName("error"),
										getPropertyName("issue_occured")
												+ e.getCause(),
										Type.ERROR_MESSAGE);
					}

				}
			});

			commissionSaleNumberList
					.addListener(new Property.ValueChangeListener() {

						public void valueChange(ValueChangeEvent event) {

							try {

								removeAllErrors();

								if (commissionSaleNumberList.getValue() != null
										&& !commissionSaleNumberList.getValue()
												.toString().equals("0")) {

									CustomerCommissionSalesModel poObj = daoObj
											.getSale((Long) commissionSaleNumberList
													.getValue());

									table.setVisibleColumns(allHeaders);

									table.removeAllItems();

									double netTotal;
									CommissionSalesCustomerDetailsModel invObj;
									Iterator it = poObj.getDetails_list()
											.iterator();
									while (it.hasNext()) {
										invObj = (CommissionSalesCustomerDetailsModel) it
												.next();

										netTotal = (invObj.getUnit_price() * invObj
												.getQunatity())
												+ invObj.getTax_amount()
												- invObj.getDiscount_amount();

										table.addItem(
												new Object[] {
														table.getItemIds()
																.size() + 1,
														invObj.getCustomer()
																.getId(),
														"",
														invObj.getCustomer()
																.getName(),
														invObj.getQunatity(),
														invObj.getUnit()
																.getId(),
														invObj.getUnit()
																.getSymbol(),
														invObj.getUnit_price(),
														invObj.getTax().getId(),
														invObj.getTax_amount(),
														invObj.getTax_percentage(),
														netTotal },
												table.getItemIds().size() + 1);

									}

									table.setVisibleColumns(requiredHeaders);

									approximateAmtTextField
											.setNewValue(asString(poObj
													.getAmount()));
									employSelect.setValue(poObj.getLogin_id());
									comment.setValue(poObj.getComments());
									date.setValue(poObj.getDate());
									referenceNoTextField.setValue(poObj
											.getRef_no());
									caontainerSalesSelect.setValue(poObj
											.getContainerId());

									isValid();
									updatePOButton.setVisible(true);
									deletePOButton.setVisible(true);
									printButton.setVisible(true);
									savePOButton.setVisible(false);
								} else {
									table.removeAllItems();

									approximateAmtTextField.setNewValue("0.0");
									employSelect.setValue(null);
									comment.setValue("");
									date.setValue(new Date(getWorkingDate()
											.getTime()));
									referenceNoTextField.setValue("");
									caontainerSalesSelect.setValue(null);

									savePOButton.setVisible(true);
									updatePOButton.setVisible(false);
									deletePOButton.setVisible(false);
									printButton.setVisible(false);
								}

								calculateTotals();

								customerCompo.setValue(null);
								customerCompo.focus();
								quantityTextField.setValue("0.0");
								unitPriceTextField.setValue("0.0");
								netPriceTextField.setNewValue("0.0");

								caontainerSalesSelect.focus();

								if (!isFinYearBackEntry()) {
									savePOButton.setVisible(false);
									updatePOButton.setVisible(false);
									deletePOButton.setVisible(false);
									printButton.setVisible(false);
									if (commissionSaleNumberList.getValue() == null
											|| commissionSaleNumberList
													.getValue().toString()
													.equals("0")) {
										Notification
												.show("Warning..!",
														"You can't Add or edit transaction. For add or edit "
																+ "change your financial year or change settings.",
														Type.WARNING_MESSAGE);
									}
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});

			updatePOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							FinTransaction trans = new FinTransaction();
							CustomerCommissionSalesModel poObj = daoObj
									.getSale((Long) commissionSaleNumberList
											.getValue());

							List<CommissionSalesCustomerDetailsModel> itemsList = new ArrayList<CommissionSalesCustomerDetailsModel>();

							CommissionSalesCustomerDetailsModel invObj;
							Item item;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new CommissionSalesCustomerDetailsModel();

								item = table.getItem(it.next());

								invObj.setCustomer(new LedgerModel((Long) item
										.getItemProperty(TBC_CUSTOMER_ID)
										.getValue()));
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());

								if (taxEnable) {
									invObj.setTax(new TaxModel((Long) item
											.getItemProperty(TBC_TAX_ID)
											.getValue()));
									invObj.setTax_amount((Double) item
											.getItemProperty(TBC_TAX_AMT)
											.getValue());
									invObj.setTax_percentage((Double) item
											.getItemProperty(TBC_TAX_PERC)
											.getValue());
								} else {
									invObj.setTax(new TaxModel(1));
									invObj.setTax_amount(0);
									invObj.setTax_percentage(0);
								}

								invObj.setUnit(new UnitModel((Long) item
										.getItemProperty(TBC_UNIT_ID)
										.getValue()));
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

								itemsList.add(invObj);

								// trans.addTransaction(SConstants.CR,
								// invObj.getCustomer().getId(),
								// settings.getCASH_ACCOUNT(),
								// roundNumber((Double)
								// item.getItemProperty(TBC_NET_PRICE).getValue()));
								trans.addTransaction(SConstants.CR, settings
										.getSALES_ACCOUNT(), invObj
										.getCustomer().getId(),
										roundNumber((Double) item
												.getItemProperty(TBC_NET_PRICE)
												.getValue()));

							}

							poObj.setAmount(Double
									.parseDouble(approximateAmtTextField
											.getValue()));
							poObj.setComments(comment.getValue());
							poObj.setDate(CommonUtil
									.getSQLDateFromUtilDate(date.getValue()));
							poObj.setLogin_id((Long) employSelect.getValue());
							poObj.setOffice(new S_OfficeModel(getOfficeID()));
							poObj.setRef_no(referenceNoTextField.getValue());
							poObj.setContainerId((Long) caontainerSalesSelect
									.getValue());
							poObj.setDetails_list(itemsList);

							TransactionModel transObj = trans
									.getTransactionWithoutID(
											SConstants.COMMISSION_SALES,
											CommonUtil
													.getSQLDateFromUtilDate(date
															.getValue()));
							transObj.setTransaction_id(poObj
									.getTransaction_id());

							daoObj.update(poObj, transObj);

							saveActivity(
									getOptionId(),
									"Purchase Order Updated. Bill No : "
											+ poObj.getSales_no()
											+ ", Customer : "
											+ caontainerSalesSelect
													.getItemCaption(caontainerSalesSelect
															.getValue())
											+ ", Approximate Amount : "
											+ poObj.getAmount(),poObj.getId());

							loadPO(poObj.getId());

							Notification.show(getPropertyName("success"),
									getPropertyName("update_success"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification
								.show(getPropertyName("error"),
										getPropertyName("issue_occured")
												+ e.getCause(),
										Type.ERROR_MESSAGE);
					}

				}
			});

			deletePOButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (commissionSaleNumberList.getValue() != null
							&& !commissionSaleNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete((Long) commissionSaleNumberList
														.getValue());
												Notification
														.show(getPropertyName("success"),
																getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												saveActivity(
														getOptionId(),
														"New Purchase Order Created. Bill No : "
																+ commissionSaleNumberList
																		.getItemCaption(commissionSaleNumberList
																				.getValue())
																+ ", Customer : "
																+ caontainerSalesSelect
																		.getItemCaption(caontainerSalesSelect
																				.getValue()),(Long) commissionSaleNumberList
																				.getValue());

												loadPO(0);

											} catch (Exception e) {
												e.printStackTrace();
												Notification
														.show(getPropertyName("error"),
																getPropertyName("issue_occured")
																		+ e.getCause(),
																Type.ERROR_MESSAGE);
											}
										}
									}
								});
					}

				}
			});

			table.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					removeAllErrors();

					Collection selectedItems = null;

					if (table.getValue() != null) {
						selectedItems = (Collection) table.getValue();
					}

					if (selectedItems != null && selectedItems.size() == 1) {

						Item item = table.getItem(selectedItems.iterator()
								.next());

						// item.getItemProperty(
						// TBC_CUSTOMER_NAME).setValue("JPTTTTTT");

						customerCompo.setValue(item.getItemProperty(
								TBC_CUSTOMER_ID).getValue());
						quantityTextField.setValue(""
								+ item.getItemProperty(TBC_QTY).getValue());
						unitSelect.setValue(item.getItemProperty(TBC_UNIT_ID)
								.getValue());
						unitPriceTextField.setValue(""
								+ item.getItemProperty(TBC_UNIT_PRICE)
										.getValue());

						if (taxEnable) {
							taxSelect.setValue(item.getItemProperty(TBC_TAX_ID)
									.getValue());
						}

						netPriceTextField.setNewValue(""
								+ item.getItemProperty(TBC_NET_PRICE)
										.getValue());

						visibleAddupdatePOButton(false, true);

						customerCompo.focus();

						// item.getItemProperty(
						// TBC_CUSTOMER_NAME).setValue("JPTTTTTT");

					} else {
						customerCompo.setValue(null);
						customerCompo.focus();
						quantityTextField.setValue("0.0");
						unitPriceTextField.setValue("0.0");
						netPriceTextField.setNewValue("0.0");

						visibleAddupdatePOButton(true, false);

						customerCompo.focus();
					}

				}

			});

			addItemButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (table.getComponentError() != null)
							setRequiredError(table, null, false);

						if (isAddingValid()) {

							double price = 0, qty = 0, totalAmt = 0;

							price = Double.parseDouble(unitPriceTextField
									.getValue());
							qty = Double.parseDouble(quantityTextField
									.getValue());

							netPriceTextField
									.setNewValue(asString(price * qty));

							table.setVisibleColumns(allHeaders);

							CustomerModel custObj = customerDao
									.getCustomerFromLedger((Long) customerCompo
											.getValue());

							UnitModel objUnit = untDao
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							totalAmt = price * qty + tax_amt;

							int id = 0, ct = 0;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								id = (Integer) it.next();
							}
							id++;

							table.addItem(
									new Object[] {
											table.getItemIds().size() + 1,
											custObj.getLedger().getId(),
											custObj.getCustomer_code(),
											custObj.getName(),
											qty,
											objUnit.getId(),
											objUnit.getSymbol(),
											Double.parseDouble(unitPriceTextField
													.getValue()),
											objTax.getId(), tax_amt, tax_perc,
											totalAmt }, id);

							table.setVisibleColumns(requiredHeaders);

							customerCompo.setValue(null);
							customerCompo.focus();
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");

							calculateTotals();

							customerCompo.focus();
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			updateItemButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings("unchecked")
				public void buttonClick(ClickEvent event) {
					try {

						if (isAddingValid()) {

							Collection selectedItems = (Collection) table
									.getValue();

							Item item = table.getItem(selectedItems.iterator()
									.next());

							double price = 0, qty = 0, totalAmt = 0;

							price = Double.parseDouble(unitPriceTextField
									.getValue());
							qty = Double.parseDouble(quantityTextField
									.getValue());

							netPriceTextField
									.setNewValue(asString(price * qty));

							// table.setVisibleColumns(new String[] {TBC_SN,
							// TBC_CUSTOMER_ID,TBC_CUSTOMER_CODE,
							// TBC_CUSTOMER_NAME,TBC_QTY, TBC_UNIT_ID, TBC_UNIT,
							// TBC_UNIT_PRICE,TBC_TAX_ID, TBC_TAX_AMT,
							// TBC_TAX_PERC , TBC_NET_PRICE});

							CustomerModel custObj = customerDao
									.getCustomerFromLedger((Long) customerCompo
											.getValue());
							UnitModel objUnit = untDao
									.getUnit((Long) unitSelect.getValue());

							double tax_amt = 0, tax_perc = 0;

							TaxModel objTax = null;
							if (taxEnable) {
								objTax = taxDao.getTax((Long) taxSelect
										.getValue());

								if (objTax.getValue_type() == 1) {
									tax_perc = objTax.getValue();
									tax_amt = price * qty * objTax.getValue()
											/ 100;
								} else {
									tax_perc = 0;
									tax_amt = objTax.getValue();
								}
							} else {
								objTax = new TaxModel(1);
							}

							totalAmt = price * qty + tax_amt;

							// int id=(Integer) table.getValue();
							// table.removeItem(table.getValue());
							// table.addItem(new Object[] {id, custObj.getId(),
							// custObj.getItem_code(),
							// custObj.getName(), qty , objUnit.getId() ,
							// objUnit.getSymbol(),
							// Double.parseDouble(unitPriceTextField.getValue()),
							// objTax.getId(), tax_amt, tax_perc, totalAmt},
							// id);

							item.getItemProperty(TBC_CUSTOMER_ID).setValue(
									custObj.getLedger().getId());
							item.getItemProperty(TBC_CUSTOMER_CODE).setValue(
									custObj.getCustomer_code());
							item.getItemProperty(TBC_CUSTOMER_NAME).setValue(
									custObj.getName());
							item.getItemProperty(TBC_QTY).setValue(qty);
							item.getItemProperty(TBC_UNIT_ID).setValue(
									objUnit.getId());
							item.getItemProperty(TBC_UNIT).setValue(
									objUnit.getSymbol());
							item.getItemProperty(TBC_UNIT_PRICE).setValue(
									Double.parseDouble(unitPriceTextField
											.getValue()));
							item.getItemProperty(TBC_TAX_ID).setValue(
									objTax.getId());
							item.getItemProperty(TBC_TAX_AMT).setValue(tax_amt);
							item.getItemProperty(TBC_TAX_PERC).setValue(
									tax_perc);
							item.getItemProperty(TBC_NET_PRICE).setValue(
									totalAmt);

							table.setVisibleColumns(requiredHeaders);

							customerCompo.setValue(null);
							customerCompo.focus();
							quantityTextField.setValue("0.0");
							unitPriceTextField.setValue("0.0");
							netPriceTextField.setNewValue("0.0");

							visibleAddupdatePOButton(true, false);

							customerCompo.focus();

							table.setValue(null);

							calculateTotals();

						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			customerCompo.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
						if (customerCompo.getValue() != null) {
							quantityTextField.selectAll();
							quantityTextField.focus();

						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			unitSelect.addListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					try {
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});

			table.addShortcutListener(new ShortcutListener("Submit Item",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (addItemButton.isVisible())
						addItemButton.click();
					else
						updateItemButton.click();
				}
			});

			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});

			table.addShortcutListener(new ShortcutListener("Delete Item",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadPO(0);
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

			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});

			unitPriceTextField.setImmediate(true);

			unitPriceTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});

			quantityTextField.setImmediate(true);

			quantityTextField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}

				}
			});

			if (!isFinYearBackEntry()) {
				savePOButton.setVisible(false);
				updatePOButton.setVisible(false);
				deletePOButton.setVisible(false);
				printButton.setVisible(false);
				Notification
						.show("Warning..!!",
								"You can't Add or edit transaction. For add or edit change your financial year or change settings.",
								Type.WARNING_MESSAGE);
			}

			printButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					List<Object> reportList = new ArrayList<Object>();
					SalesPrintBean bean = null;
					NumberToWords numberToWords = new NumberToWords();
					double total = 0;
					try {

						// SupplierModel customerModel = new SupplierDao()
						// .getSupplierFromLedger(toLong(caontainerSalesSelect.getValue()
						// .toString()));
						// String address = "";
						// if (customerModel != null) {
						// address = new AddressDao()
						// .getAddressString(customerModel.getLedger()
						// .getAddress().getId());
						// }

						map.put("CUSTOMER_NAME", caontainerSalesSelect
								.getItemCaption(caontainerSalesSelect
										.getValue()));
						map.put("CUSTOMER_ADDRESS", "");
						map.put("SALES_BILL_NO",
								toLong(commissionSaleNumberList
										.getItemCaption(commissionSaleNumberList
												.getValue())));
						map.put("BILL_DATE", CommonUtil
								.formatDateToDDMMMYYYY(date.getValue()));
						map.put("SALES_MAN", employSelect
								.getItemCaption(employSelect.getValue()));

						map.put("SALES_TYPE", "Commission Sale");
						map.put("OFFICE_NAME", getOfficeName());

						Item item;
						Iterator itr1 = table.getItemIds().iterator();
						while (itr1.hasNext()) {
							item = table.getItem(itr1.next());

							bean = new SalesPrintBean(item
									.getItemProperty(TBC_CUSTOMER_NAME)
									.getValue().toString(), toDouble(item
									.getItemProperty(TBC_QTY).getValue()
									.toString()), toDouble(item
									.getItemProperty(TBC_UNIT_PRICE).getValue()
									.toString()), toDouble(item
									.getItemProperty(TBC_NET_PRICE).getValue()
									.toString()), item
									.getItemProperty(TBC_UNIT).getValue()
									.toString(), item
									.getItemProperty(TBC_CUSTOMER_CODE)
									.getValue().toString(), toDouble(item
									.getItemProperty(TBC_QTY).getValue()
									.toString()));

							total += toDouble(item
									.getItemProperty(TBC_NET_PRICE).getValue()
									.toString());

							reportList.add(bean);
						}

						S_OfficeModel officeModel = new OfficeDao()
								.getOffice(getOfficeID());
						map.put("AMOUNT_IN_WORDS", numberToWords.convertNumber(
								roundNumber(total) + "", officeModel
										.getCurrency().getInteger_part(),
								officeModel.getCurrency().getFractional_part()));

						Report report = new Report(getLoginID());
						report.setJrxmlFileName("CustomerCommissionSale_A4_Print");
						// report.setReportFileName("PurchaseOrderPrint");
						report.setReportTitle("Purchase Order");
						// report.setIncludeHeader(true);
						report.setReportType(Report.PDF);
						report.createReport(reportList, map);

						report.print();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return pannel;
	}

	public void calculateNetPrice() {
		double unitPrc = 0, qty = 0;

		try {
			unitPrc = roundNumber(Double.parseDouble(unitPriceTextField
					.getValue()));
			qty = roundNumber(Double.parseDouble(quantityTextField.getValue()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		netPriceTextField.setNewValue(asString(roundNumber((unitPrc * qty))));
	}

	public void calculateTotals() {
		try {

			double qty_ttl = 0, tax_ttl = 0, net_ttl = 0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());

				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();

				if (taxEnable) {
					tax_ttl += (Double) item.getItemProperty(TBC_TAX_AMT)
							.getValue();
				}

				net_ttl += (Double) item.getItemProperty(TBC_NET_PRICE)
						.getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_TAX_AMT, asString(roundNumber(tax_ttl)));
			table.setColumnFooter(TBC_NET_PRICE, asString(roundNumber(net_ttl)));

			approximateAmtTextField.setNewValue(asString(roundNumber(net_ttl)));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (taxEnable) {
				if (taxSelect.getValue() == null
						|| taxSelect.getValue().equals("")) {
					setRequiredError(taxSelect, "Select a Tax", true);
					taxSelect.focus();
					ret = false;
				} else
					setRequiredError(taxSelect, null, false);
			}

			if (unitPriceTextField.getValue() == null
					|| unitPriceTextField.getValue().equals("")) {
				setRequiredError(unitPriceTextField, "Enter Unit Price", true);
				unitPriceTextField.focus();
				unitPriceTextField.selectAll();
				ret = false;
			} else {
				try {
					if (Double.parseDouble(unitPriceTextField.getValue()) < 0) {
						setRequiredError(unitPriceTextField,
								"Enter a valid Price", true);
						unitPriceTextField.focus();
						unitPriceTextField.selectAll();
						ret = false;
					} else
						setRequiredError(unitPriceTextField, null, false);
				} catch (Exception e) {
					setRequiredError(unitPriceTextField, "Enter a valid Price",
							true);
					unitPriceTextField.focus();
					unitPriceTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField, "Enter a Quantity", true);
				quantityTextField.focus();
				quantityTextField.selectAll();
				ret = false;
			} else {
				try {
					if (Double.parseDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								"Quantity must be greater than Zero", true);
						quantityTextField.focus();
						quantityTextField.selectAll();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							"Enter a valid Quantity", true);
					quantityTextField.focus();
					quantityTextField.selectAll();
					ret = false;
					// TODO: handle exception
				}
			}

			if (unitSelect.getValue() == null
					|| unitSelect.getValue().equals("")) {
				setRequiredError(unitSelect, "Select a Unit", true);
				unitSelect.focus();
				ret = false;
			} else
				setRequiredError(unitSelect, null, false);

			if (customerCompo.getValue() == null
					|| customerCompo.getValue().equals("")) {
				setRequiredError(customerCompo, "Select an Item", true);
				customerCompo.focus();
				ret = false;
			} else
				setRequiredError(customerCompo, null, false);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}

	public void visibleAddupdatePOButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				Collection selectedItems = (Collection) table.getValue();
				Iterator it1 = selectedItems.iterator();
				while (it1.hasNext()) {
					// Item item=table.getItem(selectedItems.iterator().next());
					table.removeItem(it1.next());
				}

				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = table.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}
			customerCompo.focus();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadPO(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new CustomerCommissionSalesModel(0, "----Create New-----"));
			list.addAll(daoObj.getAllPurchaseOrderNumbersAsRefNo(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			commissionSaleNumberList.setContainerDataSource(bic);
			commissionSaleNumberList.setItemCaptionPropertyId("ref_no");

			commissionSaleNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		/*
		 * if(.getValue()==null || .getValue().equals("")){ setRequiredError( ,
		 * "Select a Date",true); .focus(); ret=false; } else setRequiredError(
		 * , null,false);
		 */

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, getPropertyName("add_some_items"), true);
			customerCompo.focus();
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (employSelect.getValue() == null
				|| employSelect.getValue().equals("")) {
			setRequiredError(employSelect,
					getPropertyName("invalid_selection"), true);
			employSelect.focus();
			ret = false;
		} else
			setRequiredError(employSelect, null, false);

		if (caontainerSalesSelect.getValue() == null
				|| caontainerSalesSelect.getValue().equals("")) {
			setRequiredError(caontainerSalesSelect,
					getPropertyName("invalid_selection"), true);
			caontainerSalesSelect.focus();
			ret = false;
		} else
			setRequiredError(caontainerSalesSelect, null, false);

		if (date.getValue() == null || date.getValue().equals("")) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			date.focus();
			ret = false;
		} else
			setRequiredError(date, null, false);

		// TODO Auto-generated method stub
		return ret;
	}

	public void removeAllErrors() {
		if (taxSelect.getComponentError() != null)
			setRequiredError(taxSelect, null, false);
		if (unitPriceTextField.getComponentError() != null)
			setRequiredError(unitPriceTextField, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		if (customerCompo.getComponentError() != null)
			setRequiredError(customerCompo, null, false);
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	public SComboField getCommissionSaleNumberList() {
		return commissionSaleNumberList;
	}

	public void setCommissionSaleNumberList(SComboField commissionSaleNumberList) {
		this.commissionSaleNumberList = commissionSaleNumberList;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return commissionSaleNumberList;
	}

}
