package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.vaadin.haijian.ExcelExporter;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.DormantStockReportDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Mar 27, 2014
 */
public class DormantStockReportUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton showButton;

	private Report report;

	private DormantStockReportDao daoObj;

	SDateField fromDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;

	STextField intervalDays, no_ofIntervals;

	private SNativeSelect reportType;

	static String TBC_SN = "SN";
	static String TBC_ITEM = "Item";

	SHorizontalLayout mainLay;

	STable table;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	OfficeDao ofcDao;
	ItemDao ledDao;

	ArrayList<String> visibleColumnsList;

	ExcelExporter excelExporter;

	private SComboField itemComboField;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		try {

			intervalDays = new STextField(getPropertyName("interval_days"),
					150, "15");
			no_ofIntervals = new STextField(getPropertyName("no_intervals"),
					150, "5");

			ofcDao = new OfficeDao();
			ledDao = new ItemDao();

			allColumns = new String[] { TBC_SN, TBC_ITEM };
			visibleColumns = new String[] { TBC_SN, TBC_ITEM };

			visibleColumnsList = new ArrayList<String>(Arrays.asList(TBC_SN,
					TBC_ITEM));

			itemComboField = new SComboField(getPropertyName("item"), 200);
			reloadItemCombo(getOfficeID());

			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(1200, 500);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			table = new STable(null, 1000, 200);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM, String.class, null,
					getPropertyName("item"), null, Align.LEFT);

			table.setColumnWidth(TBC_SN, 30);

			table.setVisibleColumns(visibleColumns);
			table.setSizeFull();
			table.setSelectable(true);
			table.setWidth("820");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new DormantStockReportDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), getMonthStartDate());

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(itemComboField);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(intervalDays);
			formLayout.addComponent(no_ofIntervals);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			excelExporter = new ExcelExporter(table);
			excelExporter.setCaption(getPropertyName("export_excel"));

			showButton = new SButton(getPropertyName("show"));
			buttonLayout.addComponent(showButton);
			buttonLayout.addComponent(excelExporter);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(table);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			showButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						showReport();
					}
				}
			});

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

			officeSelect
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							reloadItemCombo((Long) officeSelect.getValue());
						}
					});
			itemComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							table.removeAllItems();
							removeContainerProperties();
						}
					});

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void showReport() {
		try {

			table.removeAllItems();

			if (isValid()) {

				List lst = null;

				removeContainerProperties();

				long itemId = 0;
				if (itemComboField.getValue() != null
						&& !itemComboField.getValue().equals(""))
					itemId = (Long) itemComboField.getValue();
				lst = daoObj
						.getDormantStockReport(CommonUtil
								.getSQLDateFromUtilDate(fromDate.getValue()),
								toInt(intervalDays.getValue()),
								toInt(no_ofIntervals.getValue()),
								getOfficeID(), itemId);

				table.setVisibleColumns(allColumns);

				int ct = 0;
				double bal = 0;
				AcctReportMainBean obj;
				Iterator it = lst.iterator();
				while (it.hasNext()) {

					if (ct == 0) {
						int val = 0;
						for (int i = 0; i < toInt(no_ofIntervals.getValue()); i++) {
							val += toInt(intervalDays.getValue());
							table.addContainerProperty("Upto " + val
									+ " Days Old", Double.class, 0, "Upto "
									+ val + " Days Old", null, Align.RIGHT);
							visibleColumnsList.add("Upto " + val + " Days Old");
						}

						table.setVisibleColumns((String[]) visibleColumnsList
								.toArray(new String[visibleColumnsList.size()]));

					}

					obj = (AcctReportMainBean) it.next();

					Object[] objs = new Object[visibleColumnsList.size()];

					objs[0] = ct + 1;
					objs[1] = obj.getName();

					for (int i = 2; i < visibleColumnsList.size(); i++) {
						objs[i] = obj.getSubList().get(i - 2);
					}

					table.addItem(objs, ct);

					ct++;
				}

				table.setVisibleColumns((String[]) visibleColumnsList
						.toArray(new String[visibleColumnsList.size()]));

				buttonLayout.removeComponent(excelExporter);
				excelExporter = new ExcelExporter(table);
				buttonLayout.addComponent(excelExporter);
				excelExporter.setCaption(getPropertyName("export_excel"));

				lst.clear();

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		try {
			if (toInt(intervalDays.getValue()) < 0) {
				setRequiredError(intervalDays, getPropertyName("invalid_data"),
						true);
				intervalDays.focus();
				ret = false;
			} else
				setRequiredError(intervalDays, null, false);
		} catch (Exception e) {
			setRequiredError(intervalDays, getPropertyName("invalid_data"),
					true);
			intervalDays.focus();
			ret = false;
		}

		try {
			if (toInt(no_ofIntervals.getValue()) < 0
					|| toInt(no_ofIntervals.getValue()) > 10) {
				setRequiredError(no_ofIntervals,
						getPropertyName("invalid_data"), true);
				no_ofIntervals.focus();
				ret = false;
			} else
				setRequiredError(no_ofIntervals, null, false);
		} catch (Exception e) {
			setRequiredError(no_ofIntervals, getPropertyName("invalid_data"),
					true);
			no_ofIntervals.focus();
			ret = false;
		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;
	}

	public void removeContainerProperties() {

		String refId;
		for (int i = 2; i < visibleColumnsList.size(); i++) {
			refId = visibleColumnsList.get(i);
			table.removeContainerProperty(refId);
			visibleColumnsList.remove(i);
			i--;
		}

	}

	private void reloadItemCombo(long office) {
		try {

			List itemList = ledDao.getAllActiveItems(office);

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName("-----------------ALL------------------");
			if (itemList == null)
				itemList = new ArrayList();

			itemList.add(0, itemModel);

			itemComboField
					.setInputPrompt("-----------------ALL------------------");

			SCollectionContainer itemContainer = SCollectionContainer.setList(
					itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
