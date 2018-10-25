package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManufacturingDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Mar 31, 2014
 */

public class RawmaterialConsumptionReportUI extends SparkLogic {

	private static final long serialVersionUID = 4018773221080484618L;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField itemsComboField;
	private SComboField rawMaterialComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private Report report;

	private ManufacturingDao dao;
	private ItemDao itemDao;

	private List rawList;

	@Override
	public SPanel getGUI() {

		dao = new ManufacturingDao();
		itemDao = new ItemDao();

		rawList = new ArrayList();

		report = new Report(getLoginID());

		setSize(360, 320);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			itemsComboField = new SComboField(getPropertyName("product"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);

			rawMaterialComboField = new SComboField(
					getPropertyName("raw_material"), 200, null, "id", "name",
					false, getPropertyName("all"));
			mainFormLayout.addComponent(rawMaterialComboField);

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
					.addValueChangeListener(new Property.ValueChangeListener() {
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
								e.printStackTrace();
							}
						}
					});

			officeComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								List prodList = itemDao
										.getAllManufacturingItems((Long) officeComboField
												.getValue());
								ItemModel salesModel = new ItemModel(0,getPropertyName("all"));
								if (prodList == null) {
									prodList = new ArrayList<Object>();
								}
								prodList.add(0, salesModel);

								SCollectionContainer bic1 = SCollectionContainer
										.setList(prodList, "id");
								itemsComboField.setContainerDataSource(bic1);
								itemsComboField
										.setItemCaptionPropertyId("name");

								List materialList = new ArrayList();
								materialList.addAll(prodList);

								materialList.addAll(itemDao
										.getAllPurchaseOnlyItems((Long) officeComboField
												.getValue()));

								Collections.sort(materialList,
										new Comparator<ItemModel>() {
											@Override
											public int compare(
													final ItemModel object1,
													final ItemModel object2) {
												int result = object1
														.getName()
														.compareTo(
																object2.getName());
												return result;
											}
										});

								SCollectionContainer bic2 = SCollectionContainer
										.setList(materialList, "id");
								rawMaterialComboField
										.setContainerDataSource(bic2);
								rawMaterialComboField
										.setItemCaptionPropertyId("name");

								rawList.clear();
								rawList.addAll(materialList);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			itemsComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {

								LoadMaterials();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});

			rawMaterialComboField
					.addValueChangeListener(new Property.ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {
							try {
								if (rawMaterialComboField.getValue() != null
										&& !rawMaterialComboField.getValue()
												.equals("")
										&& !rawMaterialComboField.getValue()
												.toString().equals("0")) {
									rawList.clear();
									rawList.add(new ItemModel(
											(Long) rawMaterialComboField
													.getValue()));
								} else {
									LoadMaterials();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
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

						if (officeComboField.getValue() != null) {

							List list;

							long itemID = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")) {
								itemID = (Long) itemsComboField.getValue();
							}

							list = dao.getRawMaterialConsumptionReport(
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue(), rawList);

							if (list != null && list.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("RawMaterialConsumptionReport");
								report.setReportFileName("RawMaterialConsumptionReport");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("raw_material_consumption_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("RAW_MATERIAL_LABEL", getPropertyName("raw_material"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("UNIT_LABEL", getPropertyName("unit"));
								
								String subHeader = "";
								if (itemID != 0) {
									subHeader += getPropertyName("product")+" : "
											+ itemsComboField
													.getItemCaption(itemsComboField
															.getValue());
								}

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(list, map);

								list.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
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

	private void LoadMaterials() throws Exception {
		List itemsList = new ArrayList();

		if (itemsComboField.getValue() != null
				&& !itemsComboField.getValue().equals("")
				&& !itemsComboField.getValue().toString().equals("0")) {
			List mapList = dao.getItemForManufacturing((Long) itemsComboField
					.getValue());

			Iterator mapIter = mapList.iterator();
			ManufacturingMapModel mapMdl;

			while (mapIter.hasNext()) {
				mapMdl = (ManufacturingMapModel) mapIter.next();
				itemsList.add(mapMdl.getSubItem());
			}

		} else {
			itemsList.addAll(itemDao
					.getAllManufacturingItems((Long) officeComboField
							.getValue()));

			itemsList
					.addAll(itemDao
							.getAllPurchaseOnlyItems((Long) officeComboField
									.getValue()));

		}

		Collections.sort(itemsList, new Comparator<ItemModel>() {
			@Override
			public int compare(final ItemModel object1, final ItemModel object2) {
				int result = object1.getName().compareTo(object2.getName());
				return result;
			}
		});

		ItemModel salesModel = new ItemModel(0,getPropertyName("all"));
		itemsList.add(0, salesModel);

		SCollectionContainer bic2 = SCollectionContainer.setList(itemsList,
				"id");
		rawMaterialComboField.setContainerDataSource(bic2);
		rawMaterialComboField.setItemCaptionPropertyId("name");

		rawList.clear();
		rawList.addAll(itemsList);
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
