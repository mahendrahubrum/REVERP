package com.inventory.onlineSales.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.BuildingDao;
import com.inventory.onlineSales.dao.ImportOnlineSalesOrderDao;
import com.inventory.onlineSales.model.OnlineCustomerModel;
import com.inventory.onlineSales.model.OnlineSalesOrderDetailsModel;
import com.inventory.onlineSales.model.OnlineSalesOrderModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDialogBox;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 24, 2014
 */
public class ImportOnlineSalesOrder extends SparkLogic {

	private static final long serialVersionUID = 6404324575929336791L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton processButton;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField customerSelect;
	private SListSelect customerSOSelect;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	ImportOnlineSalesOrderDao soDao;
	CustomerDao custDao;
	UnitDao unitDao;
	ItemDao itemDao;

	private SButton mapCustomer;
	SComboField buildingSelect;

	SDialogBox dialg;
	private WrappedSession session;

	@Override
	public SPanel getGUI() {

		setSize(420, 520);

		ofcDao = new OfficeDao();
		soDao = new ImportOnlineSalesOrderDao();
		custDao = new CustomerDao();
		unitDao = new UnitDao();
		itemDao = new ItemDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		popupContainer = new SHorizontalLayout();

		formLayout = new SFormLayout();
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		session = getHttpSession();

		try {
			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

			customerSelect = new SComboField(null, 200, null, "id", "name",
					true, "Select");

			customerSOSelect = new SListSelect(getPropertyName("orders"), 250,
					null, "id", "ref_no");
			customerSOSelect.setHeight("300");
			customerSOSelect.setMultiSelect(true);

			buildingSelect = new SComboField(
					"Building",
					200,
					new BuildingDao()
							.getAllActiveBuildingNamesUnderOffice(getOfficeID()),
					"id", "name", true, "Select");
			if (buildingSelect.getItemIds() != null
					&& buildingSelect.getItemIds().size() > 0) {
				buildingSelect.setValue(buildingSelect.getItemIds().iterator()
						.next());
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		mapCustomer = new SButton("Map");

		SHorizontalLayout hor = new SHorizontalLayout("Customer");
		hor.addComponent(customerSelect);
		hor.addComponent(mapCustomer);

		processButton = new SButton(getPropertyName("approve"));
		buttonLayout.addComponent(processButton);
		formLayout.addComponent(popupContainer);
		// formLayout.addComponent(organizationSelect);
		// formLayout.addComponent(officeSelect);
		formLayout.addComponent(hor);
		formLayout.addComponent(customerSOSelect);
		// formLayout.addComponent(buildingSelect);
		formLayout.addComponent(buttonLayout);

		dialg = new SDialogBox(getPropertyName("map_customer"), 800, 600);

		organizationSelect
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {

						SCollectionContainer bic = null;
						try {

							bic = SCollectionContainer.setList(
									ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
											.getValue()), "id");

						} catch (Exception e) {
							e.printStackTrace();
						}
						officeSelect.setContainerDataSource(bic);
						officeSelect.setItemCaptionPropertyId("name");

					}
				});

		officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if (officeSelect.getValue() != null) {
					reloadCustomer();
				}
			}
		});

		customerSelect
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {

						if (customerSelect.getValue() != null
								&& officeSelect.getValue() != null) {
							reloadSO();
							long custId;
							try {
								session.removeAttribute("new_online_cust_id");
								custId = soDao
										.getCustomerIdOfOnlineCustomer((Long) customerSelect
												.getValue());

								if (custId != 0) {
									CustomerModel customer = custDao
											.getCustomer(custId);
									session.setAttribute("new_online_cust_id",
											customer.getLedger().getId());
								} else {
									dialg.addComponent(new OnlineCustomerMapPanel(
											(Long) customerSelect.getValue()));
									getUI().getCurrent().addWindow(dialg);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});

		customerSOSelect.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				try {

					if (customerSOSelect.getValue() != null) {

						Set<Long> lst = (Set<Long>) customerSOSelect.getValue();

						if (lst.size() == 1) {

							OnlineSalesOrderDetailsModel invObj;
							OnlineSalesOrderModel objModel = soDao
									.getOnlineOrder(lst.iterator().next());
							OnlineCustomerModel cust = soDao
									.getOnlineCustomer(objModel
											.getOnlineCustomer());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Sales Order</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("online_customer"), cust
											.getFirstName()
											+ " "
											+ cust.getLastName()));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(objModel
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("total_amount"), objModel
											.getTotalAmount() + ""));

							SGridLayout grid = new SGridLayout(
									getPropertyName("item_details"));
							grid.setColumns(12);

							List list = soDao
									.getOnlineSalesOrderDetails(objModel
											.getId());

							grid.setRows(list.size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "Item"), 1, 0);
							grid.addComponent(new SLabel(null, "Qty"), 2, 0);
							grid.addComponent(new SLabel(null, "Unit"), 3, 0);
							grid.addComponent(new SLabel(null, "Unit Price"),
									4, 0);
							grid.addComponent(new SLabel(null, "Amount"), 5, 0);
							grid.setSpacing(true);
							int i = 1;
							ItemModel item;
							Iterator itmItr = list.iterator();
							while (itmItr.hasNext()) {
								invObj = (OnlineSalesOrderDetailsModel) itmItr
										.next();
								item = itemDao.getItem(invObj.getItem());
								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(
										new SLabel(null, item.getName()), 1, i);
								grid.addComponent(
										new SLabel(null, invObj.getQunatity()
												+ ""), 2, i);
								grid.addComponent(new SLabel(null, item
										.getUnit().getSymbol()), 3, i);
								grid.addComponent(
										new SLabel(null, invObj.getUnit_price()
												+ ""), 4, i);
								grid.addComponent(
										new SLabel(null, (invObj
												.getUnit_price()
												* invObj.getQunatity() - invObj
													.getDiscount_amount())
												+ ""), 5, i);
								i++;
							}

							form.addComponent(grid);

							form.setStyleName("grid_max_limit");

							popupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		mapCustomer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (customerSelect.getValue() != null
						&& !customerSelect.getValue().equals("")) {
					customerSelect.setComponentError(null);
					dialg.addComponent(new OnlineCustomerMapPanel(
							(Long) customerSelect.getValue()));
					getUI().getCurrent().addWindow(dialg);
				} else {
					setRequiredError(customerSelect,
							getPropertyName("select_customer"), true);
				}
			}
		});

		processButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {

						ConfirmDialog.show(getUI(),
								"Sales Order will be created. Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {

												Set<Long> lst = (Set<Long>) customerSOSelect
														.getValue();
												Iterator iter = lst.iterator();
												List saveList = new ArrayList();
												List updateList = new ArrayList();
												List itemsList = new ArrayList();
												OnlineSalesOrderDetailsModel detailMdl;
												OnlineSalesOrderModel objModel;
												SalesOrderModel soMdl;
												SalesInventoryDetailsModel invObj;
												CommonMethodsDao comDao = new CommonMethodsDao();
												CustomerModel cust = null;

												double conv_rat = 0;

												while (iter.hasNext()) {

													itemsList = new ArrayList();

													objModel = soDao
															.getOnlineOrder((Long) iter
																	.next());

													cust = custDao
															.getCustomerFromLedger(toLong(session
																	.getAttribute(
																			"new_online_cust_id")
																	.toString()));

													soMdl = new SalesOrderModel();
													soMdl.setActive(true);
													soMdl.setAmount(objModel
															.getTotalAmount());
													soMdl.setLocationId(0);
													soMdl.setComments("");
													soMdl.setDate(objModel
															.getDate());
													soMdl.setOffice(new S_OfficeModel(
															getOfficeID()));
													soMdl.setRef_no("");
													soMdl.setCustomer(new LedgerModel(
															cust.getLedger()
																	.getId()));
													soMdl.setOrder_no(getNextSequence(
															"Sales Order Id",
															getLoginID())+"");


													List list = soDao
															.getOnlineSalesOrderDetails(objModel
																	.getId());
													ItemModel item;
													Iterator itmItr = list
															.iterator();
													while (itmItr.hasNext()) {

														detailMdl = (OnlineSalesOrderDetailsModel) itmItr
																.next();
														item = itemDao
																.getItem(detailMdl
																		.getItem());
														invObj = new SalesInventoryDetailsModel();

														invObj.setItem(new ItemModel(
																detailMdl
																		.getItem()));
														invObj.setQunatity(detailMdl
																.getQunatity());

														invObj.setTax(new TaxModel(
																1));
														invObj.setUnit(new UnitModel(
																item.getUnit()
																		.getId()));
														invObj.setUnit_price(detailMdl
																.getUnit_price());

														conv_rat = comDao
																.getConvertionRate(
																		detailMdl
																				.getItem(),
																		detailMdl
																				.getUnit(),
																		toInt(cust
																				.getSales_type()
																				+ ""));

														invObj.setQuantity_in_basic_unit(conv_rat
																* detailMdl
																		.getQunatity());


														// invObj.setManufacturing_date(CommonUtil.getCurrentSQLDate());
														// invObj.setExpiry_date(CommonUtil.getCurrentSQLDate());

														itemsList.add(invObj);
													}


													saveList.add(soMdl);
													updateList.add(objModel);
												}

												soDao.saveSalesOrder(saveList,
														updateList);
												Notification
														.show(getPropertyName("success"),
																Type.WARNING_MESSAGE);
												reloadSO();

											} catch (Exception e) {
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		mainPanel.setContent(formLayout);

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());

		return mainPanel;
	}

	protected void reloadCustomer() {
		SCollectionContainer bic = null;
		try {
			bic = SCollectionContainer.setList(
					soDao.getAllOnlineCustomersWithSO(), "id");

			customerSelect.setContainerDataSource(bic);
			customerSelect.setItemCaptionPropertyId("firstName");

			customerSelect.setValue((long) 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reloadSO() {

		SCollectionContainer bic = null;
		try {

			bic = SCollectionContainer.setList(soDao.getAllOnlineSalesOrders(
					(Long) officeSelect.getValue(),
					(Long) customerSelect.getValue()), "id");
			customerSOSelect.setContainerDataSource(bic);
			customerSOSelect.setItemCaptionPropertyId("comments");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		Set<Long> SOs = (Set<Long>) customerSOSelect.getValue();

		if (SOs.size() <= 0) {
			setRequiredError(customerSOSelect, "Select SO", true);
			ret = false;
		} else
			setRequiredError(customerSOSelect, null, false);

		// if(buildingSelect.getValue()==null ||
		// buildingSelect.getValue().equals("")){
		// setRequiredError( buildingSelect, "Select a building",true);
		// buildingSelect.focus();
		// ret=false;
		// }
		// else
		// setRequiredError(buildingSelect , null,false);

		if (session.getAttribute("new_online_cust_id") == null) {
			setRequiredError(customerSelect, "Customer not mapped", true);
			customerSelect.focus();
			ret = false;
		} else
			setRequiredError(customerSelect, null, false);
		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
