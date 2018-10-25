package com.inventory.config.stock.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.bean.ItemPhysicalStockBean;
import com.inventory.config.stock.bean.StockBean;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemPhysicalStockDao;
import com.inventory.config.stock.dao.ItemStockResetDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ItemPhysicalStockModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 * @Date Jan 15, 2014
 */
public class ItemPhysicalStockUI extends SparkLogic {

	private static final long serialVersionUID = 5863690371948089832L;

	private STable table;
	private SDateField dateField;

	private SButton saveButton;
	private SButton deleteButton;
	private SButton printButton;

	private static final String TBL_NO = "#";
	private static final String TBL_ITEM_ID = "Id";
	private static final String TBL_ITEM_NAME = "Name";
	private static final String TBL_UNIT_ID = "Unit Id";
	private static final String TBL_UNIT = "Unit";
	private static final String TBL_CURRENT = "Current Stock";
	private static final String TBL_PHYSICAL = "Physical Stock";
	private static final String TBL_DIFFERENCE = "Difference";
	private static final String TBL_VALUE = "Value";

	private Object[] allHeaders;
	private Object[] reqHeaders;


	private SComboField organizationComboField;
	private SComboField officeComboField;

	STextField fillQtyTextField;
	SButton fillAllBtn;
	ItemPhysicalStockDao dao;
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		allHeaders = new Object[] { TBL_NO, TBL_ITEM_ID, TBL_ITEM_NAME,TBL_UNIT_ID, TBL_UNIT,TBL_CURRENT, TBL_PHYSICAL,TBL_DIFFERENCE,TBL_VALUE };
		reqHeaders = new Object[] { TBL_NO,  TBL_ITEM_NAME, TBL_UNIT,TBL_CURRENT, TBL_PHYSICAL,TBL_DIFFERENCE,TBL_VALUE };
		setSize(820, 590);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SGridLayout dateLayout = new SGridLayout();
		dateLayout.setSpacing(true);
		dateLayout.setColumns(9);
		dateLayout.setRows(1);

		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		SHorizontalLayout lay = new SHorizontalLayout();
		lay.setSpacing(true);

		try {

			fillQtyTextField = new STextField();
			fillAllBtn = new SButton(getPropertyName("fill_all"));

			organizationComboField = new SComboField(null, 150,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(null, 150);

			dao = new ItemPhysicalStockDao();

			dateField = new SDateField(null, 100, getDateFormat(),getWorkingDate());


			table = new STable(null, 750, 400);
			table.setSelectable(false);
			table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO,null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_ID, Long.class, null,TBL_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_ITEM_NAME, String.class, null,getPropertyName("name"), null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT_ID, Long.class, null,TBL_UNIT_ID, null, Align.LEFT);
			table.addContainerProperty(TBL_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
			table.addContainerProperty(TBL_CURRENT, Double.class, null,getPropertyName("current_stock"), null, Align.LEFT);
			table.addContainerProperty(TBL_PHYSICAL, STextField.class, null,getPropertyName("physical_stock"), null, Align.LEFT);
			table.addContainerProperty(TBL_DIFFERENCE, STextField.class, null,getPropertyName("difference"), null, Align.LEFT);
			table.addContainerProperty(TBL_VALUE, STextField.class, null,getPropertyName("value"), null, Align.LEFT);
			
			table.setColumnExpandRatio(TBL_NO, (float)0.5);
			table.setColumnExpandRatio(TBL_ITEM_NAME, (float)2);
			table.setColumnExpandRatio(TBL_UNIT, (float)0.8);
			table.setColumnExpandRatio(TBL_CURRENT, (float)1);
			table.setColumnExpandRatio(TBL_PHYSICAL, (float)1);
			table.setColumnExpandRatio(TBL_DIFFERENCE, (float)1);
			table.setColumnExpandRatio(TBL_VALUE, (float)1);
			
			
			saveButton = new SButton(getPropertyName("save"));
			deleteButton = new SButton(getPropertyName("delete"));
			printButton = new SButton(getPropertyName("print"));

			table.setVisibleColumns(reqHeaders);
			lay.addComponent(saveButton);
			lay.addComponent(deleteButton);
			lay.addComponent(printButton);

			dateLayout.addComponent(
					new SLabel(getPropertyName("organization")), 1, 0);
			dateLayout.addComponent(organizationComboField, 2, 0);

			dateLayout
					.addComponent(new SLabel(getPropertyName("office")), 3, 0);
			dateLayout.addComponent(officeComboField, 4, 0);

			dateLayout.addComponent(new SLabel(getPropertyName("date")), 5, 0);
			dateLayout.addComponent(dateField, 6, 0);

			layout.addComponent(dateLayout);
			layout.addComponent(table);
//			layout.addComponent(new SHorizontalLayout(true, fillQtyTextField,fillAllBtn));

			layout.addComponent(lay);

			pan.setContent(layout);

			organizationComboField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					try {

						SCollectionContainer bic = SCollectionContainer.setList(new OfficeDao()
														.getAllOfficeNamesUnderOrg((Long) organizationComboField.getValue()), "id");
						officeComboField.setContainerDataSource(bic);
						officeComboField.setItemCaptionPropertyId("name");
						Iterator it = officeComboField.getItemIds().iterator();
						if (it.hasNext())
							officeComboField.setValue(it.next());
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			officeComboField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isValid()){
							loadItems((Long)officeComboField.getValue());
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			dateField.setImmediate(true);
			
			dateField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isValid()){
							loadItems((Long)officeComboField.getValue());
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			organizationComboField.setValue(getOrganizationID());

			officeComboField.setValue(getOfficeID());

			saveButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							List list=(List)table.getItemIds();
							Iterator itr=list.iterator();
							ItemPhysicalStockModel psm=null;
							STextField physical=null,diff=null,val=null;
							List<ItemPhysicalStockModel> itemList=new ArrayList<ItemPhysicalStockModel>();
							while (itr.hasNext()) {
								Item item=table.getItem(itr.next());
								ItemModel mdl=new ItemDao().getItem(toLong(item.getItemProperty(TBL_ITEM_ID).getValue().toString()));
								psm=dao.getItemPhysicalStockModel(	(Long)officeComboField.getValue(),
																	CommonUtil.getSQLDateFromUtilDate(dateField.getValue()),
																	mdl.getId());
								physical = (STextField) item.getItemProperty(TBL_PHYSICAL).getValue();
								diff = (STextField) item.getItemProperty(TBL_DIFFERENCE).getValue();
								val = (STextField) item.getItemProperty(TBL_VALUE).getValue();
								if(psm!=null){
									psm.setItem(new ItemModel(mdl.getId()));
									psm.setOffice((Long)officeComboField.getValue());
									psm.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									psm.setCurrent_stock(toDouble(item.getItemProperty(TBL_CURRENT).getValue().toString()));
									psm.setPhysical_stock(toDouble(physical.getValue().toString()));
									psm.setDifference(toDouble(diff.getValue().toString()));
									psm.setValue_difference(toDouble(val.getValue().toString()));
								}
								else{
									psm=new ItemPhysicalStockModel();
									psm.setItem(new ItemModel(mdl.getId()));
									psm.setOffice((Long)officeComboField.getValue());
									psm.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
									psm.setCurrent_stock(toDouble(item.getItemProperty(TBL_CURRENT).getValue().toString()));
									psm.setPhysical_stock(toDouble(physical.getValue().toString()));
									psm.setDifference(toDouble(diff.getValue().toString()));
									psm.setValue_difference(toDouble(val.getValue().toString()));
								}
								itemList.add(psm);
							}
							dao.update(itemList);
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							loadItems((Long)officeComboField.getValue());
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											dao.delete((Long)officeComboField.getValue(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
											SNotification.show(getPropertyName("delete_success"),Type.WARNING_MESSAGE);
											loadItems((Long)officeComboField.getValue());
										} 
										catch (Exception e) {
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
		        			        } 
		        			    }
		        			});
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});

			printButton.addClickListener(new ClickListener() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							List reportList=new ArrayList();
							List list=(List)table.getItemIds();
							Iterator itr=list.iterator();
							STextField physical=null,diff=null,val=null;
							ItemPhysicalStockBean bean=null;
							Report report= new Report(getLoginID());
							while (itr.hasNext()) {
								Item item=table.getItem(itr.next());
								ItemModel mdl=new ItemDao().getItem(toLong(item.getItemProperty(TBL_ITEM_ID).getValue().toString()));
								physical = (STextField) item.getItemProperty(TBL_PHYSICAL).getValue();
								diff = (STextField) item.getItemProperty(TBL_DIFFERENCE).getValue();
								val = (STextField) item.getItemProperty(TBL_VALUE).getValue();
								bean=new ItemPhysicalStockBean(	mdl.getName(),
																mdl.getUnit().getSymbol(),
																toDouble(item.getItemProperty(TBL_CURRENT).getValue().toString()),
																toDouble(physical.getValue().toString()),
																toDouble(diff.getValue().toString()),
																toDouble(val.getValue().toString()));
								reportList.add(bean);
							}
							
							if(reportList.size()>0){
								report.setJrxmlFileName("ItemPhysicalStock");
								report.setReportFileName("Item Physical Stock");
								report.setReportTitle("Item Physical Stock");
								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(Report.PDF);
								report.setOfficeName(officeComboField.getItemCaption(officeComboField.getValue()));
								report.createReport(reportList, null);
								report.print();
							}
							else{
								Notification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
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

	private void loadItems(long office) {
		Object[] rows = null;
		ItemModel mdl = null;
		STextField physical = null,diff = null,val = null;
		int index = 1;
		try {
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list=dao.getAllItems(office);
			for (int i = 0; i < list.size(); i++) {
				mdl = (ItemModel) list.get(i);
				ItemPhysicalStockModel psm=dao.getItemPhysicalStockModel(office, 
														CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), 
														mdl.getId());
				physical = new STextField();
				diff = new STextField();
				val = new STextField();
				if(psm!=null){
					physical.setValue(psm.getPhysical_stock()+"");
					diff.setValue(psm.getDifference()+"");
					val.setValue(psm.getValue_difference()+"");
				}
				else{
					physical.setValue("0");
					diff.setValue("0");
					val.setValue("0");
				}
				rows = new Object[] { 	index, 
										mdl.getId(),
										mdl.getName(),
										mdl.getUnit().getId(), 
										mdl.getUnit().getSymbol(), 
										mdl.getCurrent_balalnce(),
										physical,
										diff,
										val };
				table.addItem(rows, index);
				index++;
			}
			table.setVisibleColumns(reqHeaders);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		boolean valid=true;
		if(organizationComboField.getValue()==null || organizationComboField.getValue().toString().equals("")){
			valid=false;
		}
		if(officeComboField.getValue()==null || officeComboField.getValue().toString().equals("")){
			valid=false;
		}
		if(dateField.getValue()==null){
			valid=false;
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
