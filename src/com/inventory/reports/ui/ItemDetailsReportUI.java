package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.dao.ItemDetailsReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

public class ItemDetailsReportUI extends SparkLogic {

	private static final long serialVersionUID = 404962936827056872L;
	private static final String PROMPT_ALL = "-------------------All-----------------";

	private static final String TABLE_BALANCE = "Balance";
	private static final String TABLE_OUTWARDS = "Outwards";
	private static final String TABLE_INWARDS = "Inwards";
	private static final String TABLE_ITEM_NAME = "ItemName";
	private static final String TABLE_ITEM_CODE = "ItemCode";
	private static final String TABLE_ITEM_ID = "ItemID";
	private static final String TABLE_SL_NO = "SlNo";

	private SComboField itemComboField;
	private SDateField fromDateField;
	private SDateField toDateField;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private STable itemTable;
	private Object[] allHeaders;
	private Object[] visibleHeaders;

	private SButton generateButton;

	private Report report;

	private List itemList;
	private List itemReportList;

	private ItemDao itemDao;
	private ItemDetailsReportDao reportDao;

	private SCollectionContainer itemContainer;

	@Override
	public SPanel getGUI() {
		setSize(1200, 800);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		SGridLayout dateGridLayout = new SGridLayout();
		dateGridLayout.setRows(2);
		dateGridLayout.setColumns(2);
		dateGridLayout.setSpacing(true);

		itemDao = new ItemDao();

		try {
			itemList = itemDao.getAllActiveItems(getOfficeID());
		} catch (Exception e) {
			itemList = new ArrayList();
			e.printStackTrace();
		}
		ItemModel itemModel = new ItemModel();
		itemModel.setId(0);
		itemModel.setName(PROMPT_ALL);
		itemList.add(0, itemModel);
		itemComboField = new SComboField(getPropertyName("item"), 200,
				itemList, "id", "name");
		itemComboField.setInputPrompt(PROMPT_ALL);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateGridLayout.addComponent(fromDateField);
		dateGridLayout.addComponent(toDateField);

		formLayout.addComponent(itemComboField);
		formLayout.addComponent(dateGridLayout);

		allHeaders = new Object[] { TABLE_SL_NO, TABLE_ITEM_ID,
				TABLE_ITEM_CODE, TABLE_ITEM_NAME, TABLE_INWARDS,
				TABLE_OUTWARDS, TABLE_BALANCE };
		visibleHeaders = new Object[] { TABLE_SL_NO, TABLE_ITEM_CODE,
				TABLE_ITEM_NAME, TABLE_INWARDS, TABLE_OUTWARDS, TABLE_BALANCE };

		itemTable = new STable("", 700, 300);

		itemTable.addContainerProperty(TABLE_SL_NO, Integer.class, null, "#",
				null, Align.CENTER);
		itemTable.addContainerProperty(TABLE_ITEM_ID, Long.class, null,
				getPropertyName("item_id"), null, Align.CENTER);
		itemTable.addContainerProperty(TABLE_ITEM_CODE, String.class, null,
				getPropertyName("item_code"), null, Align.CENTER);
		itemTable.addContainerProperty(TABLE_ITEM_NAME, String.class, null,
				getPropertyName("item_name"), null, Align.LEFT);
		itemTable.addContainerProperty(TABLE_INWARDS, Double.class, null,
				getPropertyName("inwards"), null, Align.CENTER);
		itemTable.addContainerProperty(TABLE_OUTWARDS, Long.class, null,
				getPropertyName("outwards"), null, Align.CENTER);
		itemTable.addContainerProperty(TABLE_BALANCE, String.class, null,
				getPropertyName("balance"), null, Align.CENTER);

		itemTable.setSizeFull();
		itemTable.setSelectable(true);

		itemTable.setVisibleColumns(visibleHeaders);
		formLayout.addComponent(itemTable);

		generateButton = new SButton(getPropertyName("generate"));
		buttonLayout.addComponent(generateButton);
		formLayout.addComponent(buttonLayout);

		itemComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadItemDetails();
			}
		});

		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
				}
			}
		});

		mainPanel.setContent(formLayout);

		return mainPanel;
	}

	protected void loadItemDetails() {
		long itemId = 0;
		Object[] data;
		if (itemComboField.getValue() != null
				&& !itemComboField.getValue().equals("")) {
			itemId = Long.parseLong(itemComboField.getValue().toString());
		}

		try {
			List<Object> list = reportDao
					.getItemDetails(itemId, CommonUtil
							.getSQLDateFromUtilDate(fromDateField.getValue()),
							CommonUtil.getSQLDateFromUtilDate(toDateField
									.getValue()));

			data = new Object[] { "" };
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void generateReport() {

	}

	@Override
	public Boolean isValid() {
		return false;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
