package com.inventory.sales.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.sales.dao.SalesOrderDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesOrderModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
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
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Dec 26, 2013
 */
public class CustomerSOApproveUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

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
	SalesOrderDao soDao;
	CustomerDao custDao;

	@Override
	public SPanel getGUI() {

		setSize(340, 350);

		ofcDao = new OfficeDao();
		soDao = new SalesOrderDao();
		custDao = new CustomerDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		popupContainer = new SHorizontalLayout();

		formLayout = new SFormLayout();
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		try {
			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

			customerSelect = new SComboField(getPropertyName("customer"), 200,
					null, "id", "name");

			customerSOSelect = new SListSelect(getPropertyName("orders"), 200,
					null, "id", "ref_no");
			customerSOSelect.setHeight("100");
			customerSOSelect.setMultiSelect(true);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		processButton = new SButton(getPropertyName("approve"));
		buttonLayout.addComponent(processButton);
		formLayout.addComponent(popupContainer);
		formLayout.addComponent(organizationSelect);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(customerSelect);

		formLayout.addComponent(customerSOSelect);
		formLayout.addComponent(buttonLayout);

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
					SCollectionContainer bic = null;
					try {
						List lst = new ArrayList();
						lst.add(new S_OfficeModel(0, "ALL"));
						lst.addAll(custDao
								.getAllActiveCustomerNamesWithLedgerID((Long) officeSelect
										.getValue()));
						bic = SCollectionContainer.setList(lst, "id");

						customerSelect.setContainerDataSource(bic);
						customerSelect.setItemCaptionPropertyId("name");

						customerSelect.setValue((long) 0);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		customerSelect
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						reloadSO();

					}
				});

		customerSOSelect.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				try {

					if (customerSOSelect.getValue() != null) {

						Set<Long> lst = (Set<Long>) customerSOSelect.getValue();

						if (lst.size() == 1) {

							SalesOrderModel objModel = soDao.getSalesOrderModel(lst
									.iterator().next());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,
									"<h2><u>Sales Order</u></h2>"));
							form.addComponent(new SLabel(
									getPropertyName("sales_order_no"), objModel
											.getOrder_no() + ""));
							form.addComponent(new SLabel(
									getPropertyName("customer"), objModel
											.getCustomer().getName()));
							form.addComponent(new SLabel(
									getPropertyName("date"), CommonUtil
											.getUtilDateFromSQLDate(objModel
													.getDate())));

							form.addComponent(new SLabel(
									getPropertyName("total_amount"), objModel
											.getAmount() + ""));

							SGridLayout grid = new SGridLayout(
									getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(objModel.getOrder_details_list()
									.size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, "Item"), 1, 0);
							grid.addComponent(new SLabel(null, "Qty"), 2, 0);
							grid.addComponent(new SLabel(null, "Unit"), 3, 0);
							grid.addComponent(new SLabel(null, "Unit Price"),
									4, 0);
							grid.addComponent(new SLabel(null, "Discount"), 5,
									0);
							grid.addComponent(new SLabel(null, "Amount"), 6, 0);
							grid.setSpacing(true);
							int i = 1;
							SalesInventoryDetailsModel invObj;
							Iterator itmItr = objModel
									.getOrder_details_list().iterator();
							while (itmItr.hasNext()) {
								invObj = (SalesInventoryDetailsModel) itmItr
										.next();

								grid.addComponent(new SLabel(null, i + ""), 0,
										i);
								grid.addComponent(new SLabel(null, invObj
										.getItem().getName()), 1, i);
								grid.addComponent(
										new SLabel(null, invObj.getQunatity()
												+ ""), 2, i);
								grid.addComponent(new SLabel(null, invObj
										.getUnit().getSymbol()), 3, i);
								grid.addComponent(
										new SLabel(null, invObj.getUnit_price()
												+ ""), 4, i);
								grid.addComponent(
										new SLabel(null, invObj
												.getDiscount() + ""), 5,
										i);
								grid.addComponent(
										new SLabel(
												null,
												(invObj.getUnit_price()
														* invObj.getQunatity()
														- invObj.getDiscount() + invObj
															.getTaxAmount())
														+ ""), 6, i);
								i++;
							}

							form.addComponent(grid);
							form.addComponent(new SLabel(
									getPropertyName("comment"), objModel
											.getComments()));

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

		processButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {

						ConfirmDialog
								.show(getUI(),
										"This Order will convert to real SO and Customer can't edit this. Are you sure?",
										new ConfirmDialog.Listener() {
											public void onClose(
													ConfirmDialog dialog) {
												if (dialog.isConfirmed()) {
													try {
														soDao.changeCustomerSOtoRealSO(
																(Set<Long>) customerSOSelect
																		.getValue(),
																getLoginID(),
																(Long) officeSelect
																		.getValue(),
																(Long) organizationSelect
																		.getValue());
														Notification
																.show(getPropertyName("save_success"),
																		Type.WARNING_MESSAGE);
														reloadSO();

													} catch (Exception e) {
														// TODO
														// Auto-generated
														// catch block
														Notification
																.show(getPropertyName("error"),
																		Type.ERROR_MESSAGE);
														e.printStackTrace();
													}
												}
											}
										});

					} catch (Exception e1) {
						// TODO Auto-generated catch block
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

	public void reloadSO() {

		SCollectionContainer bic = null;
		try {
			if (customerSelect.getValue() != null
					&& officeSelect.getValue() != null)
//				bic = SCollectionContainer.setList(soDao
//						.getAllSalesCustomerCreatedOrderNumbersAsRefNo(
//								(Long) officeSelect.getValue(),
//								(Long) customerSelect.getValue()), "id");
			customerSOSelect.setContainerDataSource(bic);
			customerSOSelect.setItemCaptionPropertyId("ref_no");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		Set<Long> SOs = (Set<Long>) customerSOSelect.getValue();

		if (SOs.size() <= 0) {
			setRequiredError(customerSOSelect,
					getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(customerSOSelect, null, false);

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
