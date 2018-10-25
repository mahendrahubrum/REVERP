package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.SalesReportBean;
import com.inventory.reports.dao.CustomerProfitReportDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Feb 19 2014
 */
public class LaundryItemWiseProfitReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	CustomerProfitReportDao daoObj;

	@Override
	public SPanel getGUI() {

		ledDao = new LedgerDao();

		daoObj = new CustomerProfitReportDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(400, 300);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, "ALL");

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainPanel.setContent(mainFormLayout);

			organizationComboField
					.addListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										new OfficeDao()
												.getAllOfficeNamesUnderOrg((Long) organizationComboField
														.getValue()), "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			itemsComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					customerId = 0;
					if (itemsComboField.getValue() != null
							&& !itemsComboField.getValue().toString()
									.equals("0")) {
						customerId = toLong(itemsComboField.getValue()
								.toString());
					}
				}
			});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadItemsCombo(toLong(officeComboField.getValue()
							.toString()));
				}
			});

			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {
						boolean noData = true;
						SalesModel salesModel = null;
						SalesInventoryDetailsModel inventoryDetailsModel = null;
						SalesReportBean reportBean = null;
						String items = "";

						List<Object> reportList;

						long itmId = 0;

						if (itemsComboField.getValue() != null
								&& !itemsComboField.getValue().equals("")) {
							itmId = toLong(itemsComboField.getValue()
									.toString());
						}

						reportList = daoObj.getLaundryItemWiseProfitReport(
								itmId, CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
								toLong(officeComboField.getValue().toString()));

						if (reportList.size() > 0) {
							report.setJrxmlFileName("LaundryItemWiseProfit_Report");
							report.setReportFileName("Item Wise Profit Report");
							report.setReportTitle("Item Wise Profit Report");

							String subHeader = "";

							if (itemsComboField.getValue() != null)
								if (!itemsComboField.getValue().toString()
										.equals("0"))
									subHeader += "Item : "
											+ itemsComboField
													.getItemCaption(itemsComboField
															.getValue()) + "\t";

							subHeader += "\n From : "
									+ CommonUtil
											.formatDateToDDMMYYYY(fromDateField
													.getValue())
									+ "\t To : "
									+ CommonUtil
											.formatDateToDDMMYYYY(toDateField
													.getValue());

							report.setReportSubTitle(subHeader);

							report.setIncludeHeader(true);
							report.setIncludeFooter(false);
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, null);

							reportList.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}

	protected void loadItemsCombo(long officeId) {
		List itemsList = null;
		try {
			if (officeId != 0) {
				itemsList = new ItemDao()
						.getAllActiveItemsWithAppendingItemCode((Long) officeComboField
								.getValue());
			}

			ItemModel salesModel = new ItemModel(0,
					"---------------------ALL-------------------");
			if (itemsList == null) {
				itemsList = new ArrayList<Object>();
			}
			itemsList.add(0, salesModel);

			SCollectionContainer bic1 = SCollectionContainer.setList(itemsList,
					"id");
			itemsComboField.setContainerDataSource(bic1);
			itemsComboField.setItemCaptionPropertyId("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
