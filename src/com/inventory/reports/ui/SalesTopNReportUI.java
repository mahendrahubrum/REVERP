package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.reports.bean.TopNReportBean;
import com.inventory.reports.dao.TopNReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;

public class SalesTopNReportUI extends SparkLogic {

	private Report report;
	private SOfficeComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
/*	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;*/
	private SRadioButton reportTypeRadioButton;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private SButton showButton;
	private STable subTable;
	/*private ItemGroupDao itemGroupDao;
	private ItemSubGroupDao itemSubGroupDao;*/
	private STextField noOfRowsTextField;
	private TopNReportDao topNReportDao;
	private HashMap<Long, String> currencyHashMap;

	private static final String TBC_SN = "Sl No";
	private static final String TBC_ITEM_NAME = "Item Name";
	private static final String TBC_QUANTITY = "Quantity";
	private static final String TBC_AMOUNT = "Amount";
	
	public static final int AMOUNT_WISE = 0;
	public static final int QUANTITY_WISE = 1;

	@Override
	public SPanel getGUI() {
		setSize(750, 300);

		SPanel panel = new SPanel();
		panel.setSizeFull();
		/*itemGroupDao = new ItemGroupDao();
		itemSubGroupDao = new ItemSubGroupDao();*/
		topNReportDao = new TopNReportDao();
		report = new Report(getLoginID());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		/*List<ItemGroupModel> list = getItemGroupList(getOrganizationID());
		itemGroupComboField = new SComboField(getPropertyName("item_group"),
				125, list, "id", "name", false, getPropertyName("all"));

		List<ItemSubGroupModel> list_1 = getItemSubGroupList(toLong(itemGroupComboField
				.getValue()));
		itemSubGroupComboField = new SComboField(
				getPropertyName("item_sub_grp"), 200, list_1, "id", "name",
				false, getPropertyName("all"));*/

		reportTypeRadioButton = new SRadioButton(
				getPropertyName("report_type"), 250, Arrays.asList(
						new KeyValue(AMOUNT_WISE, getPropertyName("amount_wise")), new KeyValue(
								QUANTITY_WISE, getPropertyName("quantity_wise"))), "intKey", "value");
		reportTypeRadioButton.setStyleName("radio_horizontal");
		reportTypeRadioButton.setValue(0);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		fromDateField.setImmediate(true);

		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		toDateField.setImmediate(true);

		SHorizontalLayout dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// ===================================================================================
		noOfRowsTextField = new STextField(getPropertyName("no_of_items"));
		noOfRowsTextField.setStyleName("textfield_align_right");

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.SPACEBAR);

		showButton = new SButton(getPropertyName("show"));
		showButton.setClickShortcut(KeyCode.ENTER);

		SHorizontalLayout buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.addComponent(generateButton);
		buttonHorizontalLayout.addComponent(showButton);
		buttonHorizontalLayout.setSpacing(true);

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		mainFormLayout.addComponent(officeComboField);
		/*mainFormLayout.addComponent(itemGroupComboField);
		mainFormLayout.addComponent(itemSubGroupComboField);*/		
		mainFormLayout.addComponent(dateHorizontalLayout);
		mainFormLayout.addComponent(noOfRowsTextField);
		mainFormLayout.addComponent(reportTypeRadioButton);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(buttonHorizontalLayout);

		mainFormLayout.setComponentAlignment(buttonHorizontalLayout,
				Alignment.MIDDLE_CENTER);

		subTable = new STable(null, 350, 200);
		buildTable();

		subTable.setColumnExpandRatio(TBC_ITEM_NAME, (float) 2);
		subTable.setColumnExpandRatio(TBC_QUANTITY, (float) 0.9);
		subTable.setColumnExpandRatio(TBC_AMOUNT, 1f);

		// subTable.setVisibleColumns(visibleSubColumns);
		subTable.setSelectable(true);

		SHorizontalLayout mainHorizontalLayout = new SHorizontalLayout();
		mainHorizontalLayout.addComponent(mainFormLayout);
		mainHorizontalLayout.addComponent(subTable);

		panel.setContent(mainHorizontalLayout);

		officeComboField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});

	/*	itemSubGroupComboField
				.addValueChangeListener(new ValueChangeListener() {

					*//**
			 * 
			 *//*
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						subTable.removeAllItems();
					}
				});
*/
		fromDateField.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});
		toDateField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});
		reportTypeRadioButton
				.addValueChangeListener(new ValueChangeListener() {

					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						subTable.removeAllItems();
						buildTable();
					}
				});

		noOfRowsTextField.addValueChangeListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
			}
		});

	/*	itemGroupComboField.addValueChangeListener(new ValueChangeListener() {

			*//**
			 * 
			 *//*
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				subTable.removeAllItems();
				loadItemSubGroupComboField();
			}

			private void loadItemSubGroupComboField() {
				List<ItemSubGroupModel> list = getItemSubGroupList(toLong(itemGroupComboField
						.getValue()));
				itemSubGroupComboField
						.setContainerDataSource(SCollectionContainer.setList(
								list, "id"));
				itemSubGroupComboField.setItemCaptionPropertyId("name");
			}
		});*/

		showButton.addClickListener(new ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				subTable.removeAllItems();
				if (isValid()) {
					List<TopNReportBean> list = null;
					try {
						list = getSalesTopNReportList();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("============= "+list.size());
					int no_of_row = toInt(noOfRowsTextField.getValue());
					int reportType = toInt(reportTypeRadioButton.getValue().toString());
					no_of_row = (list.size() > no_of_row) ? no_of_row : list
							.size();
					boolean isDataExist = false;
					for (TopNReportBean bean : list) {
						if (no_of_row == 0) {
							break;
						}
						isDataExist = true;
						if(reportType == AMOUNT_WISE){
							bean.setUnit(getCurrencyDescription(getCurrencyID()));
						}
						
						subTable.addItem(
								new Object[] {
										subTable.getItemIds().size() + 1,
										bean.getItem(),
										bean.getAmountOrQuantity() + " "+bean.getUnit()},
										subTable.getItemIds().size() + 1);
						no_of_row--;
					}

					if (!isDataExist) {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				}
			}

			
		});

		generateButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// subTable.removeAllItems();
				if (isValid()) {
					try {
						List<TopNReportBean> list = null;
						try {
							list = getSalesTopNReportList();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boolean isDataExist = false;
						int reportType = toInt(reportTypeRadioButton.getValue().toString());
						int no_of_row = toInt(noOfRowsTextField.getValue());
						no_of_row = (list.size() > no_of_row) ? no_of_row
								: list.size();
						List reportList = new ArrayList<TopNReportBean>();
						for (TopNReportBean bean : list) {
							if (no_of_row == 0) {
								break;
							}
							isDataExist = true;
							if(reportType == AMOUNT_WISE){
								bean.setUnit(getCurrencyDescription(getCurrencyID()));
							}
							reportList.add(bean);
							no_of_row--;
						}

						if (isDataExist) {
							HashMap<String, Object> parameters = new HashMap<String, Object>();
							if(toInt(reportTypeRadioButton.getValue().toString()) == AMOUNT_WISE){
								parameters.put("measurement", getPropertyName("amount"));
							} else {
								parameters.put("measurement", getPropertyName("quantity"));
							}
							parameters.put("SL_NO_LABEL", getPropertyName("sl_no"));
							parameters.put("ITEM_LABEL", getPropertyName("item"));
							
							report.setJrxmlFileName("TopNReport");
							report.setReportFileName("SalesTopNReport");
							report.setReportTitle(getPropertyName("sales_top_n_report"));
							report.setReportSubTitle(getSubTitle());
							report.setReportType(toInt(reportChoiceField
									.getValue().toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, parameters);

							reportList.clear();
							list.clear();
						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			private String getSubTitle() {
				StringBuffer titleStringBuffer = new StringBuffer();
				/*titleStringBuffer
						.append("Item Group : ")
						.append((itemGroupComboField.getValue() != null) ? itemGroupComboField
								.getItemCaption(itemGroupComboField.getValue())
								: " All ")
						
						.append("\nItem Subgroup : ")
						.append((itemSubGroupComboField.getValue() != null) ? itemSubGroupComboField
								.getItemCaption(itemSubGroupComboField.getValue())
								: " All ")*/
				titleStringBuffer.append("\n"+getPropertyName("report_type")+" : "
						+ reportTypeRadioButton
								.getItemCaption(reportTypeRadioButton
										.getValue()))
				.append("\n"+getPropertyName("no_of_rows")+" : "
						+ noOfRowsTextField.getValue())
				.append("\n"+getPropertyName("from_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(fromDateField
								.getValue()))
				.append(" "+getPropertyName("to_date")+" : "
						+ CommonUtil.formatDateToDDMMYYYY(toDateField
								.getValue()));
				return titleStringBuffer.toString();
			}
		});

		return panel;
	}

	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}
	private void buildTable() {
		subTable.removeContainerProperty(TBC_SN);
		subTable.removeContainerProperty(TBC_ITEM_NAME);
		subTable.removeContainerProperty(TBC_QUANTITY);
		subTable.removeContainerProperty(TBC_AMOUNT);
		
		if(toInt(reportTypeRadioButton.getValue().toString()) == QUANTITY_WISE){
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			subTable.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_QUANTITY, String.class, null,
					getPropertyName("quantity"), null, Align.RIGHT);
		} else {
			subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			subTable.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			subTable.addContainerProperty(TBC_AMOUNT, String.class, null,
					getPropertyName("amount"), null, Align.RIGHT);
		}		
	}

	private List<TopNReportBean> getSalesTopNReportList()
			throws Exception {
		List<TopNReportBean> list = topNReportDao.getSalesTopNList(
				getOrganizationID(),
				toLong(officeComboField.getValue()),
				CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()),
				CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
				toInt(reportTypeRadioButton.getValue().toString()));
		return list;
	}

	private long toLong(Object obj) {
		if (obj == null) {
			return 0;
		} else {
			return toLong(obj.toString());
		}
	}

	/*
	 * private long toInt(Object obj) { if (obj == null) { return 0; } else {
	 * return toInt(obj.toString()); } }
	 */
/*
	private List<ItemSubGroupModel> getItemSubGroupList(long item_group_id) {
		List<ItemSubGroupModel> list = new ArrayList<ItemSubGroupModel>();
		list.add(new ItemSubGroupModel(0, "---- All -----"));
		try {
			if (item_group_id == 0) {
				list.addAll(itemSubGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID()));
			} else {
				list.addAll(itemSubGroupDao
						.getAllActiveItemSubGroups(item_group_id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<ItemGroupModel> getItemGroupList(long organization_id) {
		List<ItemGroupModel> list = new ArrayList<ItemGroupModel>();
		list.add(new ItemGroupModel(0, "---- All -----"));
		try {
			list.addAll(itemGroupDao.getAllItemGroupsNames(organization_id));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
*/
	@Override
	public Boolean isValid() {
		try {
			if (noOfRowsTextField.getValue() == null
					|| toInt(noOfRowsTextField.getValue().toString()) == 0) {
				setRequiredError(noOfRowsTextField,
						getPropertyName("invalid_data"), true);
				return false;
			} else {
				setRequiredError(noOfRowsTextField, null, false);
			}
		} catch (NumberFormatException e) {
			setRequiredError(noOfRowsTextField,
					getPropertyName("invalid_data"), true);
			return false;
		}

		return true;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
