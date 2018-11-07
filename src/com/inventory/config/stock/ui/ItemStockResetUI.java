package com.inventory.config.stock.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.inventory.config.stock.bean.StockBean;
import com.inventory.config.stock.dao.ItemStockResetDao;
import com.inventory.config.stock.model.ItemDailyRateDetailModel;
import com.inventory.config.stock.model.ItemDailyRateModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.dao.UnitManagementDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 * @Date Jan 15, 2014
 */
public class ItemStockResetUI extends SparkLogic {

	private static final long serialVersionUID = 5863690371948089832L;

	private STable table;
	private SDateField dateField;

	private SButton saveButton;
	private SButton deleteButton;

	private static final String TBL_NO = "#";
	private static final String TBL_ITEM_ID = "Id";
	private static final String TBL_ITEM_NAME = "Name";
	private static final String TBL_UNIT_ID = "Unit Id";
	private static final String TBL_UNIT = "Unit";
	private static final String TBL_RATE = "Reset Qty";

	private Object[] allHeaders;
	private Object[] reqHeaders;

	private UnitManagementDao unitMgtDao;
	private UnitDao unitDao;
	private ItemStockResetDao dao;

	private SComboField organizationComboField;
	private SComboField officeComboField;

	STextField fillQtyTextField;
	SButton fillAllBtn;

	@Override
	public SPanel getGUI() {

		setSize(680, 590);

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

			fillQtyTextField = new STextField();
			fillAllBtn = new SButton(getPropertyName("fill_all"));

			organizationComboField = new SComboField(null, 150, new OrganizationDao().getAllOrganizations(), "id",
					"name");
			officeComboField = new SComboField(null, 150);

			dao = new ItemStockResetDao();
			unitDao = new UnitDao();
			unitMgtDao = new UnitManagementDao();

			dateField = new SDateField(null, 100, getDateFormat(), getWorkingDate());

			allHeaders = new Object[] { TBL_NO, TBL_ITEM_ID, TBL_ITEM_NAME, TBL_UNIT_ID, TBL_UNIT, TBL_RATE };
			reqHeaders = new Object[] { TBL_NO, TBL_ITEM_NAME, TBL_UNIT, TBL_RATE };

			table = new STable(null, 600, 400);
			table.setSelectable(false);
			table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null, TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null, getPropertyName("name"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT_ID, Long.class, null, TBL_UNIT_ID, null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT, String.class, null, getPropertyName("unit"), null, Align.LEFT);
			table.addContainerProperty(TBL_RATE, STextField.class, null, getPropertyName("reset_qty"), null,
					Align.LEFT);

			saveButton = new SButton(getPropertyName("reset"));
			deleteButton = new SButton(getPropertyName("clear"));

			lay.addComponent(saveButton);

			dateLayout.addComponent(new SLabel(getPropertyName("organization")), 1, 0);
			dateLayout.addComponent(organizationComboField, 2, 0);

			dateLayout.addComponent(new SLabel(getPropertyName("office")), 3, 0);
			dateLayout.addComponent(officeComboField, 4, 0);

			dateLayout.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			dateLayout.addComponent(dateField, 6, 0);

			layout.addComponent(dateLayout);
			layout.addComponent(table);
			layout.addComponent(new SHorizontalLayout(true, fillQtyTextField, fillAllBtn));

			layout.addComponent(lay);

			pan.setContent(layout);

			organizationComboField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						SCollectionContainer bic = SCollectionContainer.setList(
								new OfficeDao().getAllOfficeNamesUnderOrg((Long) organizationComboField.getValue()),
								"id");
						officeComboField.setContainerDataSource(bic);
						officeComboField.setItemCaptionPropertyId("name");

						Iterator it = officeComboField.getItemIds().iterator();
						if (it.hasNext())
							officeComboField.setValue(it.next());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			officeComboField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {
						loadItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			fillAllBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (!fillQtyTextField.getValue().equals("")) {
						try {
							double val = toDouble(fillQtyTextField.getValue());
							Item item;
							Iterator itr = table.getItemIds().iterator();
							while (itr.hasNext()) {
								item = table.getItem(itr.next());
								((STextField) item.getItemProperty(TBL_RATE).getValue()).setValue(asString(val));
							}

							setRequiredError(fillQtyTextField, null, false);
						} catch (Exception e) {
							setRequiredError(fillQtyTextField, getPropertyName("invalid_data"), true);
						}
					}

				}
			});

			organizationComboField.setValue(getOrganizationID());

			officeComboField.setValue(getOfficeID());

			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.setComponentError(null);
						Date date = CommonUtil.getSQLDateFromUtilDate(dateField.getValue());
						List list = new ArrayList();
						Item item = null;
						STextField field;
						StockBean stkObj;

						Iterator itr = table.getItemIds().iterator();
						while (itr.hasNext()) {
							item = table.getItem(itr.next());
							field = (STextField) item.getItemProperty(TBL_RATE).getValue();
							stkObj = new StockBean();
							stkObj.setItem_id((Long) item.getItemProperty(TBL_ITEM_ID).getValue());
							stkObj.setQuantity(toDouble(field.getValue()));
							list.add(stkObj);

						}

						if (list.size() > 0) {

//							takeBackUP();

							dao.save(list, date);
							Notification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);

						} else {
							setRequiredError(table, "Enter Rate", true);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
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

			// loadItems();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}

	protected void takeBackUP() {

		FileResource fileResource;
		Properties properties = new Properties();
		InputStream in = getClass().getResourceAsStream("/settings.properties");

		try {
			properties.load(in);
			in.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		SimpleDateFormat frmt = new SimpleDateFormat("ddMMyyyyHHmmss");
		String realName = ("BackUPBeforeReset" + frmt.format(CommonUtil.getCurrentDateTime())).replace('.', ' ')
				.replace(',', ' ').replace(':', ' ').replace('/', ' ').replace('-', ' ').replace('~', ' ')
				.replace('!', ' ').replace('\\', ' ').replace(';', ' ').trim();

		String fileName = "";

		File dir = new File(VaadinServlet.getCurrent().getServletContext().getRealPath("/") + "Backup");

		if (dir != null && dir.isDirectory()) {
			try {

				for (File file : dir.listFiles()) {
					file.delete();
				}
			} catch (Exception e) {
				Notification.show(getPropertyName("no_files_found"), Type.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}

		fileName = VaadinServlet.getCurrent().getServletContext().getRealPath("\\") + "Backup\\" + realName + ".sql";
		File sqlFile = new File(fileName);
		try {
			sqlFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean flag = takeBackup(properties.getProperty("database"), properties.getProperty("username"),
				properties.getProperty("password"), fileName, properties.getProperty("backupscripttype"));
		if (flag) {
			// Notification.show("Backup created Successfully",
			// Type.WARNING_MESSAGE);

			fileResource = new FileResource(new File(fileName));

		} else {
			Notification.show(getPropertyName("backup_failed"), Type.ERROR_MESSAGE);
		}

		// TODO Auto-generated method stub

	}

	public boolean takeBackup(String dbName, String dbUserName, String dbPassword, String path, String dbType) {

		String executeCmd = dbType + " -u " + dbUserName + " -p" + dbPassword + " " + dbName + " -r " + path;
		Process runtimeProcess;
		try {
			System.out.println(executeCmd);// this out put works in mysql shell
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			int processComplete = runtimeProcess.waitFor();

			if (processComplete == 0) {
				System.out.println("Backup created successfully");
				return true;
			} else {
				System.out.println("Could not create the backup");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void loadItems() {
		Object[] rows = null;
		ItemModel mdl = null;
		STextField field = null;
		List unitList = null;
		int index = 1;
		double rate = 0;

		try {
			table.removeAllItems();

			table.setVisibleColumns(allHeaders);

			List list = dao.getAllItems((Long) officeComboField.getValue());
			for (int i = 0; i < list.size(); i++) {
				mdl = (ItemModel) list.get(i);

				field = new STextField();
				field.setValue(mdl.getCurrent_balalnce() + "");
				rows = new Object[] { index, mdl.getId(), mdl.getName() + " (" + mdl.getItem_code() + ")",
						mdl.getUnit().getId(), mdl.getUnit().getSymbol(), field };
				table.addItem(rows, index);
				index++;
			}

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

			rateModel = dao.getItemDailyRate(1, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 1);

			if (rateModel != null) {

				List detailList = rateModel.getDaily_rate_list();

				Iterator itr = table.getItemIds().iterator();
				while (itr.hasNext()) {
					item = table.getItem(itr.next());
					field = (STextField) item.getItemProperty(TBL_RATE).getValue();

					for (int i = 0; i < detailList.size(); i++) {
						detailMdl = (ItemDailyRateDetailModel) detailList.get(i);
						if (toLong(item.getItemProperty(TBL_ITEM_ID).toString()) == detailMdl.getItem()
								&& toLong(item.getItemProperty(TBL_UNIT_ID).toString()) == detailMdl.getUnit()) {
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

				rate = (STextField) (tblItem.getItemProperty(TBL_RATE).getValue());

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

}
