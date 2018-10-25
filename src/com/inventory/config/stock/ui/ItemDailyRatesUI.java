package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.stock.dao.ItemDailyRateDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.SalesTypeDao;
import com.inventory.config.stock.model.ItemDailyRateDetailModel;
import com.inventory.config.stock.model.ItemDailyRateModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.dao.UnitManagementDao;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 27, 2013
 */
public class ItemDailyRatesUI extends SparkLogic {

	private static final long serialVersionUID = 5863690371948089832L;

	private STable table;
	public SComboField customerComboField;
	private SDateField dateField;

	private SButton saveButton;
	private SButton deleteButton;

	private static final String TBL_NO = "#";
	private static final String TBL_ITEM_ID = "Id";
	private static final String TBL_ITEM_NAME = "Name";
	private static final String TBL_UNIT_ID = "Unit Id";
	private static final String TBL_UNIT = "Unit";
	private static final String TBL_RATE = "Rate";

	private Object[] allHeaders;
	private Object[] reqHeaders;

	private UnitManagementDao unitMgtDao;
	private UnitDao unitDao;
	private ItemDailyRateDao dao;

	private SNativeSelect salesTypeSelect;

	@Override
	public SPanel getGUI() {

		setSize(800, 600);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SGridLayout dateLayout = new SGridLayout();
		dateLayout.setSpacing(true);
		dateLayout.setColumns(9);
		dateLayout.setRows(1);

		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		SHorizontalLayout lay = new SHorizontalLayout();
		lay.setSizeFull();

		try {
			dao = new ItemDailyRateDao();
			unitDao = new UnitDao();
			unitMgtDao = new UnitManagementDao();
			List<CustomerModel> list = new CustomerDao()
					.getAllActiveCustomerNamesWithLedgerID(getOfficeID());
			CustomerModel mdl = new CustomerModel();
			mdl.setId(0);
			mdl.setName("----------------------All----------------------");
			list.add(0, mdl);

			customerComboField = new SComboField(null, 200, list, "id", "name");
			customerComboField.setValue((long) 0);

			dateField = new SDateField(null, 100, getDateFormat(),
					getWorkingDate());

			salesTypeSelect = new SNativeSelect(null, 120,
					new SalesTypeDao()
							.getAllActiveSalesTypeNames(getOfficeID()), "id",
					"name");
			Iterator itt = salesTypeSelect.getItemIds().iterator();
			if (itt.hasNext())
				salesTypeSelect.setValue(itt.next());

			salesTypeSelect.setReadOnly(true);

			allHeaders = new Object[] { TBL_NO, TBL_ITEM_ID, TBL_ITEM_NAME,
					TBL_UNIT_ID, TBL_UNIT, TBL_RATE };
			reqHeaders = new Object[] { TBL_NO, TBL_ITEM_NAME, TBL_UNIT,
					TBL_RATE };

			table = new STable(null, 600, 400);
			table.setSelectable(false);
			table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO,
					null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null,
					TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null,
					getPropertyName("name"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT_ID, Long.class, null,
					TBL_UNIT_ID, null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT, String.class, null,
					getPropertyName("unit"), null, Align.LEFT);
			table.addContainerProperty(TBL_RATE, STextField.class, null,
					getPropertyName("rate"), null, Align.LEFT);

			loadItems();

			saveButton = new SButton(getPropertyName("Save"));
			deleteButton = new SButton(getPropertyName("clear"));

			lay.addComponent(saveButton);

			dateLayout.addComponent(new SLabel(getPropertyName("Customer")), 1,
					0);
			dateLayout.addComponent(customerComboField, 2, 0);
			dateLayout.addComponent(new SLabel(getPropertyName("date")), 4, 0);
			dateLayout.addComponent(dateField, 5, 0);
			dateLayout.addComponent(new SLabel(getPropertyName("sales_type")),
					7, 0);
			dateLayout.addComponent(salesTypeSelect, 8, 0);

			layout.addComponent(dateLayout);
			layout.addComponent(table);
			layout.addComponent(lay);

			pan.setContent(layout);

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.setComponentError(null);

						List list = new ArrayList();
						Item item = null;
						STextField field;

						ItemDailyRateDetailModel detail = null;

						Iterator itr = table.getItemIds().iterator();
						while (itr.hasNext()) {
							item = table.getItem(itr.next());
							field = (STextField) item.getItemProperty(TBL_RATE)
									.getValue();
							if (toDouble(field.getValue()) > 0) {
								detail = new ItemDailyRateDetailModel();
								detail.setItem(toLong(item
										.getItemProperty(TBL_ITEM_ID)
										.getValue().toString()));
								detail.setRate(toDouble(field.getValue()));
								detail.setUnit(toLong(item
										.getItemProperty(TBL_UNIT_ID)
										.getValue().toString()));
								list.add(detail);
							}
						}

						if (list.size() > 0) {
							ItemDailyRateModel mdl = new ItemDailyRateModel();
							mdl.setCustomer_id((Long) customerComboField
									.getValue());
							mdl.setDate(CommonUtil
									.getSQLDateFromUtilDate(dateField
											.getValue()));
							mdl.setSales_type((Long) salesTypeSelect.getValue());
							mdl.setLogin_id(getLoginID());
							mdl.setOffice_id(getOfficeID());
							mdl.setDaily_rate_list(list);

							dao.save(mdl);
							Notification.show(getPropertyName("Success"),
									getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

						} else {
							setRequiredError(table,
									getPropertyName("enter_rate"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("unable_to_save"),
								Type.ERROR_MESSAGE);
					}
				}
			});

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							loadDailyRates();
						}
					});

			dateField.setImmediate(true);
			dateField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadDailyRates();
				}
			});
			salesTypeSelect.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadDailyRates();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pan;
	}

	private void loadItems() {
		Object[] rows = null;
		ItemModel mdl = null;
		ItemUnitMangementModel mgtModel = null;
		STextField field = null;
		List unitList = null;
		int index = 1;
		double rate = 0;
		try {
			List list = new ItemDao().getAllActiveItemsFromOfc(getOfficeID());
			for (int i = 0; i < list.size(); i++) {
				mdl = (ItemModel) list.get(i);
				unitList = dao.getAllItemUnitDetails(mdl.getId(),
						(Long) salesTypeSelect.getValue());
				if (unitList != null && unitList.size() > 0) {
					for (int u = 0; u < unitList.size(); u++) {
						mgtModel = (ItemUnitMangementModel) unitList.get(u);
						rate = 0;

						field = new STextField();
						field.setValue(rate + "");
						rows = new Object[] {
								index,
								mdl.getId(),
								mdl.getName() + " (" + mdl.getItem_code() + ")",
								mgtModel.getAlternateUnit(),
								unitDao.getUnit(mgtModel.getAlternateUnit())
										.getSymbol(), field };
						table.addItem(rows, index);
						index++;
					}
				}
			}

			loadDailyRates();

			table.setVisibleColumns(reqHeaders);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDailyRates() {

		resetTable();

		Item item = null;
		STextField field;

		ItemDailyRateModel rateModel;
		ItemDailyRateDetailModel detailMdl;
		try {
			rateModel = dao.getItemDailyRate(
					(Long) customerComboField.getValue(),
					CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),
					(Long) salesTypeSelect.getValue());

			if (rateModel != null) {

				List detailList = rateModel.getDaily_rate_list();

				Iterator itr = table.getItemIds().iterator();
				while (itr.hasNext()) {
					item = table.getItem(itr.next());
					field = (STextField) item.getItemProperty(TBL_RATE)
							.getValue();

					for (int i = 0; i < detailList.size(); i++) {
						detailMdl = (ItemDailyRateDetailModel) detailList
								.get(i);
						if (toLong(item.getItemProperty(TBL_ITEM_ID).toString()) == detailMdl
								.getItem()
								&& toLong(item.getItemProperty(TBL_UNIT_ID)
										.toString()) == detailMdl.getUnit()) {
							field.setValue(asString(detailMdl.getRate()));
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resetTable() {

		Iterator tblItr = table.getItemIds().iterator();
		Item tblItem = null;
		STextField rate = null;
		try {
			while (tblItr.hasNext()) {
				tblItem = table.getItem(tblItr.next());

				rate = (STextField) (tblItem.getItemProperty(TBL_RATE)
						.getValue());

				rate.setValue("0");
			}
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

	
	public SComboField getCustomerComboField() {
		return customerComboField;
	}

	public void setCustomerComboField(SComboField customerComboField) {
		this.customerComboField = customerComboField;
	}

	
	
}
