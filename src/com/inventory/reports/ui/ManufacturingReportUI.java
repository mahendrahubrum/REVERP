package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ManufacturingDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingDetailsModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.stock.ui.ItemManufacturingUI;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
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
 *         Mar 26, 2014
 */

public class ManufacturingReportUI extends SparkLogic {

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

	private SCollectionContainer custContainer;

	private Report report;

	private ManufacturingDao dao;
	private ItemDao itemDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_BILL = "Bill";
	static String TBC_DATE = "Date";
	static String TBC_QUANTITY = "Quantity";
	static String TBC_UNIT = "Unit";
	static String TBC_RAW = "Raw Materials";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_DATE,TBC_ITEM,TBC_BILL,TBC_QUANTITY,TBC_UNIT,TBC_RAW};
		visibleColumns = new Object[]{ TBC_SN, TBC_DATE,TBC_ITEM,TBC_BILL,TBC_QUANTITY,TBC_UNIT,TBC_RAW};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"), null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null, getPropertyName("item"), null,Align.CENTER);
		table.addContainerProperty(TBC_BILL, Long.class, null, getPropertyName("bill"), null,Align.CENTER);
		table.addContainerProperty(TBC_QUANTITY, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_RAW, String.class, null,getPropertyName("raw_material"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, (float) 2);
		table.setColumnExpandRatio(TBC_RAW, (float) 3);
		table.setColumnExpandRatio(TBC_QUANTITY, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

		dao = new ManufacturingDao();
		itemDao = new ItemDao();

		report = new Report(getLoginID());

		setSize(1050, 350);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

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

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, "ALL");
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			mainPanel.setContent(mainHorizontal);

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

								List itemsList = itemDao
										.getAllManufacturingItems((Long) officeComboField
												.getValue());
								ItemModel salesModel = new ItemModel(0,
										getPropertyName("all"));
								if (itemsList == null) {
									itemsList = new ArrayList<Object>();
								}
								itemsList.add(0, salesModel);

								SCollectionContainer bic1 = SCollectionContainer
										.setList(itemsList, "id");
								itemsComboField.setContainerDataSource(bic1);
								itemsComboField
										.setItemCaptionPropertyId("name");

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

			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));
			
			table.addActionHandler(new Handler() {
				
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							ItemManufacturingUI option=new ItemManufacturingUI();
							option.setCaption("Item Manufacturing");
							option.loadProductions(item.getItemProperty(TBC_BILL).getValue().toString());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionDelete };
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID).getValue();
							ManufacturingModel mdl=new ManufacturingDao().getManufacturingModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_manufacturing")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("bill"),mdl.getManufacturing_no()+""));
							form.addComponent(new SLabel(getPropertyName("item"),mdl.getItem().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),mdl.getDate().toString()));
							form.addComponent(new SLabel(getPropertyName("quantity"),item.getItemProperty(TBC_QUANTITY).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("unit"),item.getItemProperty(TBC_UNIT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("raw_material"),item.getItemProperty(TBC_RAW).getValue().toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {
							List reportList = new ArrayList();
							List list;
							long itemID = 0;
							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")) {
								itemID = (Long) itemsComboField.getValue();
							}
							list = dao.getManufacturingReport(
									itemID,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());
							if(list.size()>0){
								ReportBean bean = null;
								ManufacturingModel mdl = null;
								ManufacturingDetailsModel detMdl = null;
								String items = "";
								Iterator detIter;
								Iterator iter = list.iterator();
								while (iter.hasNext()) {
									mdl = (ManufacturingModel) iter.next();
									items = "";
									detIter = mdl.getManufacturing_details_list()
											.iterator();
									while (detIter.hasNext()) {
										detMdl = (ManufacturingDetailsModel) detIter
												.next();
										items += detMdl.getItem().getName() + "("
												+ detMdl.getQuantity() + " "
												+ detMdl.getUnit().getSymbol()
												+ ") ,";
									}
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											mdl.getId(),
											mdl.getDate().toString(),
											mdl.getItem().getName(),
											mdl.getManufacturing_no(),
											mdl.getQuantity(),
											mdl.getUnit().getSymbol(),
											items},table.getItemIds().size()+1);
								}
								setRequiredError(officeComboField, null, false);
							}
							else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
							
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}
						table.setVisibleColumns(visibleColumns);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List reportList = new ArrayList();
							List list;

							long itemID = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")) {
								itemID = (Long) itemsComboField.getValue();
							}

							list = dao.getManufacturingReport(
									itemID,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue());

							ReportBean bean = null;
							ManufacturingModel mdl = null;
							ManufacturingDetailsModel detMdl = null;
							String items = "";
							Iterator detIter;
							Iterator iter = list.iterator();
							while (iter.hasNext()) {
								mdl = (ManufacturingModel) iter.next();
								items = "";
								bean = new ReportBean();
								bean.setItem_name(mdl.getItem().getName());
								bean.setDate(CommonUtil
										.formatDateToCommonFormat(mdl.getDate()));
								bean.setQuantity(mdl.getQuantity());
								bean.setTitle(mdl.getManufacturing_no() + "");
								bean.setUnit(mdl.getUnit().getSymbol());

								detIter = mdl.getManufacturing_details_list()
										.iterator();
								while (detIter.hasNext()) {
									detMdl = (ManufacturingDetailsModel) detIter
											.next();
									items += detMdl.getItem().getName() + "("
											+ detMdl.getQuantity() + " "
											+ detMdl.getUnit().getSymbol()
											+ ") ,";
								}
								bean.setParticulars(items);
								reportList.add(bean);
							}

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("ManufacturingReport");
								report.setReportFileName("Manufacturing Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("manufacturing_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("unit_LABEL", getPropertyName("unit"));
								map.put("RAW_MATERIAL_LABEL", getPropertyName("raw_material"));
								
								String subHeader = "";
								if (itemID != 0) {
									subHeader += getPropertyName("item")+" : "
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
								report.createReport(reportList, map);

								reportList.clear();

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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
