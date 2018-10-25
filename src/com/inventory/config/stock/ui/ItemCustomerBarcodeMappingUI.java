package com.inventory.config.stock.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemCustomerBarcodeMappingDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemCustomerBarcodeMapModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.reports.bean.ItemReportBean;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;

/**
 * 
 * @author anil
 * @date 06-Nov-2015
 * @Project REVERP
 */
@SuppressWarnings("serial")
public class ItemCustomerBarcodeMappingUI extends SparkLogic {

	long id = 0;
	SFormLayout content;
	SComboField itemCombo;
	private STable table;
	SButton update;
	SHorizontalLayout buttonLayout ;

	private static final String TBL_ID = "Id";
	private static final String TBL_CUSTOMER = "Customer";
	private static final String TBL_BARCODE = "Barcode";
	private static final String TBL_PERCENTAGE = "Percentage";
	
	ItemCustomerBarcodeMappingDao dao;
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	
	STextField commonField;
	STextField commonPercField;
	SButton setButton;
	
	CustomerDao custDao;
	CurrencyManagementDao currDao;
	CommonMethodsDao comDao;
	ItemDao itmDao;
	
	ClickListener clickListener;
	
	STextField custField;
	STextField barcodeField;
	SButton printBtn;
	STextField printNoField;
	
	private SettingsValuePojo settings;
	WrappedSession session;
	
	@Override
	public SPanel getGUI() {
		
		SPanel pan=new SPanel();
		pan.setSizeFull();
		
		try {
			
			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");
		
		 dao=new ItemCustomerBarcodeMappingDao();
		 custDao=new CustomerDao();
		 currDao=new CurrencyManagementDao();
		 comDao=new CommonMethodsDao();
		 itmDao=new ItemDao(); 
				 
		setSize(730, 590);
		allHeaders=new Object[]{TBL_ID,TBL_CUSTOMER,TBL_BARCODE,TBL_PERCENTAGE};
		visibleHeaders=new Object[]{TBL_CUSTOMER,TBL_BARCODE,TBL_PERCENTAGE};
		
		table = new STable(null, 600, 360);
		table.setSelectable(false);
		table.addContainerProperty(TBL_ID, Long.class, null,TBL_ID, null, Align.CENTER);
		table.addContainerProperty(TBL_CUSTOMER, String.class, null,getPropertyName("customer"), null, Align.LEFT);
		table.addContainerProperty(TBL_BARCODE, STextField.class, null,getPropertyName("barcode"), null, Align.LEFT);
		table.addContainerProperty(TBL_PERCENTAGE, STextField.class, null,getPropertyName("percentage"),null, Align.LEFT);
		table.setColumnExpandRatio(TBL_CUSTOMER, (float)2);
		table.setColumnExpandRatio(TBL_BARCODE, (float)2.5);
		table.setVisibleColumns(visibleHeaders);
		table.setSelectable(true);
		
		update = new SButton(getPropertyName("update"));
		buttonLayout = new SHorizontalLayout();
		
		content = new SFormLayout();
		
		custField=new STextField("Customer");
		custField.setReadOnly(true);
		barcodeField=new STextField("Barcode");
		barcodeField.setReadOnly(true);
		printNoField=new STextField("No");
		printNoField.setValue("1");
		printBtn=new SButton("Print");
		final SHorizontalLayout contlay=new SHorizontalLayout();
		contlay.addComponent(custField);
		contlay.addComponent(barcodeField);
		contlay.addComponent(printNoField);
		contlay.addComponent(printBtn);
		contlay.setComponentAlignment(printBtn,Alignment.BOTTOM_CENTER);
		contlay.setVisible(false);
		
		SHorizontalLayout hlay=new SHorizontalLayout();
		hlay.setSpacing(true);
		commonField=new STextField("Barcode");
		commonPercField=new STextField("Amount Percentage");
		commonPercField.setValue("0");
		setButton=new SButton("Set");
		hlay.addComponent(commonField);
		hlay.addComponent(commonPercField);
		hlay.addComponent(setButton);
		hlay.setComponentAlignment(setButton, Alignment.BOTTOM_CENTER);
		
		itemCombo = new SComboField(null, 300,new ItemDao().getAllActiveItemsWithAppendingItemCode(getOfficeID()),"id","name",true,getPropertyName("select"));
		content.setMargin(true);
		SHorizontalLayout hrl=new SHorizontalLayout(getPropertyName("item"));
		hrl.addComponent(itemCombo);
		content.addComponent(hrl);
		content.addComponent(hlay);
		content.addComponent(table);
		content.addComponent(contlay);
		buttonLayout.addComponent(update);
		buttonLayout.setSpacing(true);
		content.addComponent(buttonLayout);
		content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		update.setVisible(true);
		content.setSizeUndefined();
		pan.setContent(content);
		
		
		printBtn.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				try {
					
			if(barcodeField.getValue().trim().length()>0){
				List reportList=new ArrayList();
				Report report = new Report(getLoginID());
				ItemReportBean bean = null;
			
				ItemModel itemMdl=itmDao.getItem((Long)itemCombo.getValue());
				
				for(int i=0;i<toInt(printNoField.getValue().toString());i++){
					bean = new ItemReportBean();
					bean.setCode(barcodeField.getValue().toString());
					bean.setCurrency(currDao.getCurrencySymbolName(getCountryID()));
					bean.setName(itemMdl.getName());
					bean.setRate(itemMdl.getSale_rate());
					reportList.add(bean);
				}
				report.setJrxmlFileName(getBillName(SConstants.bills.BARCODE));
				report.setReportFileName("PurchaseBarcode");
				report.setReportTitle("");
				report.setIncludeHeader(false);
				report.setReportType(Report.PDF);
				report.createReport(reportList, null);

				report.print();
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue()!=null){
					contlay.setVisible(true);
					
					Item item=table.getItem(table.getValue());
					STextField field=(STextField) item.getItemProperty(TBL_BARCODE).getValue();
					
					barcodeField.setNewValue(field.getValue());
					printNoField.setNewValue("1");
					custField.setNewValue( item.getItemProperty(TBL_CUSTOMER).getValue().toString());
				}else{
					contlay.setVisible(false);
					barcodeField.setNewValue("");
					printNoField.setNewValue("1");
					custField.setNewValue("");
				}
			}
		});

		itemCombo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if(itemCombo.getValue()!=null&&!itemCombo.getValue().equals("")){
						if(settings.isBARCODE_ENABLED()&&settings.getBARCODE_TYPE()==SConstants.barcode_types.ITEM_SPECIFIC){
							commonField.setValue(itmDao.getItemCode((Long)itemCombo.getValue()));
						}
					}
						loadTable();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		setButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
//				if(commonField.getValue().toString().trim().length()>0){
					
					if(setValid()){
					
					Iterator itr=table.getItemIds().iterator();
					STextField field,percField;
					ItemCustomerBarcodeMapModel mapModel=null;
					while (itr.hasNext()) {
						Item item=table.getItem(itr.next());
						field = (STextField) item.getItemProperty(TBL_BARCODE).getValue();
						field.setValue(commonField.getValue());
						percField = (STextField) item.getItemProperty(TBL_PERCENTAGE).getValue();
						percField.setValue(commonPercField.getValue());
					}
//					}
				}
			}
		});
		
		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if(isValid()){
						if(table.getItemIds().size()>0){
							Iterator itr=table.getItemIds().iterator();
							List<ItemCustomerBarcodeMapModel> list=new ArrayList<ItemCustomerBarcodeMapModel>();
							STextField field;
							STextField percField;
							ItemCustomerBarcodeMapModel mapModel=null;
							while (itr.hasNext()) {
								Item item=table.getItem(itr.next());
								
								field = (STextField) item.getItemProperty(TBL_BARCODE).getValue();
								percField = (STextField) item.getItemProperty(TBL_PERCENTAGE).getValue();
							if(field.getValue().trim().length()>0){
									long custId=toLong(item.getItemProperty(TBL_ID).getValue().toString());
								mapModel=dao.getMappingModel((Long)itemCombo.getValue(), custId);
								if(mapModel!=null){
									mapModel.setItemId((Long)itemCombo.getValue());
									mapModel.setCustomerId(custId);
									mapModel.setBarcode(field.getValue());
									mapModel.setPercentage(toDouble(percField.getValue().toString()));
								}
								else{
									mapModel=new ItemCustomerBarcodeMapModel();
									mapModel.setItemId((Long)itemCombo.getValue());
									mapModel.setCustomerId(custId);
									mapModel.setBarcode(field.getValue());
									mapModel.setPercentage(toDouble(percField.getValue().toString()));
								}
								list.add(mapModel);
								}
							}
							if(list.size()>0)
								dao.update(list);
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							loadTable();
						}
					}
				} 
				catch (Exception e) {
					Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}

		});
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}

	protected boolean setValid() {
		boolean flag=true;
		commonPercField.setRequiredError(null);
		try {
			if(toDouble(commonPercField.getValue().toString())<0||toDouble(commonPercField.getValue().toString())>100){
				setRequiredError(commonPercField, "Invalid Percentage", true);
				flag=false;
			}
		} catch (Exception e) {
			setRequiredError(commonPercField, "Invalid Percentage", true);
			flag=false;
		}
		return flag;
	}

	@SuppressWarnings("rawtypes")
	public void loadTable(){
		try{
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			
			if(itemCombo.getValue()!=null&&!itemCombo.getValue().equals("")){
			CustomerModel cust;
			List custList=custDao.getAllActiveCustomerNamesWithLedgerID(getOfficeID());
			Iterator iter=custList.iterator();
			STextField field;
			STextField percField;
			while (iter.hasNext()) {
				cust = (CustomerModel) iter.next();
				field = new STextField(null,265);
				percField = new STextField(null,100);
				
				ItemCustomerBarcodeMapModel module=dao.getMappingModel((Long)itemCombo.getValue(), cust.getId());
				if(module!=null)
					field.setValue(module.getBarcode());
				if(module!=null)
					percField.setValue(module.getPercentage()+"");
				
				table.addItem(new Object[]{cust.getId(),
						cust.getName(), field,percField},table.getItemIds().size()+1);
			}
			}
						
			table.setVisibleColumns(visibleHeaders);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		boolean flag=true;
		itemCombo.setComponentError(null);
		table.setComponentError(null);
		if(itemCombo.getValue()==null || itemCombo.getValue().equals("")){
			flag=false;
			setRequiredError(itemCombo, "Invalid Data", true);
		}
		if(flag){
			if(table.getItemIds().size()>0){
				Iterator itr=table.getItemIds().iterator();
				STextField percField;
				while (itr.hasNext()) {
					Item item=table.getItem(itr.next());
					percField = (STextField) item.getItemProperty(TBL_PERCENTAGE).getValue();
					try {
						if(toDouble(percField.getValue().toString())<0||toDouble(percField.getValue().toString())>100){
							flag=false;
							setRequiredError(table, "Invalid Percentage", true);
							break;
						}else{
							flag=true;
						}
							
					} catch (Exception e) {
						flag=false;
						break;
						
					}
				}
			}
		}
		return flag;
	}
	
	@Override
	public Boolean getHelp() {
		return null;
	}
}
