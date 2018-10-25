package com.hotel.service.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.hotel.config.dao.TableDao;
import com.hotel.config.model.TableModel;
import com.hotel.service.dao.HotelSalesDao;
import com.hotel.service.model.CashPayDetailsModel;
import com.hotel.service.model.CustomerBookingModel;
import com.hotel.service.model.HotelSalesInventoryDetailsModel;
import com.hotel.service.model.HotelSalesModel;
import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.BankAccountDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.reports.bean.SalesPrintBean;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
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
import com.webspark.Components.SKeyPad;
import com.webspark.Components.SLabel;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.NumberToWords;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */
@Theme("testappstheme")
public class HotelSalesUI extends SparkLogic {

	private static final long serialVersionUID = -1512795482516148256L;
	static String TBC_SN = "SN";
	static String TBC_ITEM_ID = "item_id";
	static String TBC_ITEM_NAME = "Item Name";
	static String TBC_QTY = "Qty";
	static String TBC_PRICE = "Price";
	static String TBC_AMOUNT = "Amount";
	
	SGridLayout addingGrid;
	SGridLayout masterDetailsGrid;
	SGridLayout buttonsGrid;

	STextField quantityTextField,keyboardTextField;
	
	STextField totalTextField;
	STextField taxTextField;
	STextField chargesTextField;
	STextField discountTextField;
	STextField netTotalTextField;
	
	SPanel panel;
	
	SVerticalLayout mainVertLay;
	SGridLayout headLay1;
	SHorizontalLayout bodyLay1;
	SHorizontalLayout headLay2;

	SButton addItemButton;
	SButton updateItemButton;
	SButton saveSalesButton;
	SButton updateSalesButton;
	SButton deleteSalesButton;
	SButton printButton;
	
	SButton cancelBillButton, holdBillButton,recallBillButton, confirmButton;
	
	CommonMethodsDao comDao;
	
	ItemDao itemDao;
	
	SDateField date;
	
	SPopupView pop;
	SPopupView popKeyboad;
	
	UnitDao unitDao;
	
	SLabel itemName;
	
	SFormLayout popLay;
	SFormLayout keyboardLay;
	
	STable table;
	SListSelect categorySelectList;
	SGridLayout itemLay;
	
	SKeyPad popupKeyPad;
	SKeyPad keyBoard;
	
	SNativeSelect cardTypeSelect;
	SComboField  bankSelect;
	STextField ccNoTFld,cardAmtTFld,checkNoTFld,bankAmtTFld,creditAmtTFld, grantTotalTFld, balanceTFld;
	SDateField checkDate;
	SComboField salesNumberList;
	
	Component focusedComponent;
	int focusType=1;
	String[] allHeaders;
	String[] requiredHeaders;
	
	HotelSalesDao daoObj;
	
	SettingsValuePojo settings;
	
	SHorizontalLayout iconLink;
	
	long STS_HOLD=3, STS_CONFIRMED=2, STS_PAID=1, STS_CANCELED=4;
	
	List<Long> historyList;
	boolean isRecall=false;
	
	SLabel customerLabel;
	SComboField employeeField;
	SComboField tableField;
	
	LayoutClickListener itemLayoutrListener;
	
	@Override
	public SPanel getGUI() {
		
		historyList=new ArrayList<Long>();
		historyList.add((long)0);
		// TODO Auto-generated method stub
		
		setSize(1180, 640);
		setCaption("Sales");
		
		cancelBillButton=new SButton();
		holdBillButton=new SButton();
		recallBillButton=new SButton();
		confirmButton=new SButton();
		
		iconLink = new SHorizontalLayout();
		
		final SFormLayout payBtn=new SFormLayout();
		payBtn.addComponent(new SLabel(null,"Pay","btn_caption"));
		payBtn.setWidth("60");
		payBtn.setHeight("50");
		payBtn.setStyleName("testBtn");
		payBtn.setId("Pay");
		iconLink.addComponent(payBtn);
		
		final SFormLayout confirmBtn=new SFormLayout();
		confirmBtn.addComponent(new SLabel(null,"Confirm","btn_caption"));
		confirmBtn.setWidth("70");
		confirmBtn.setHeight("50");
		confirmBtn.setStyleName("testBtn");
		confirmBtn.setId("Confirm");
		iconLink.addComponent(confirmBtn);
		
		final SFormLayout deleteBtn=new SFormLayout();
		deleteBtn.addComponent(new SLabel(null, "Delete","btn_caption"));
		deleteBtn.setWidth("70");
		deleteBtn.setHeight("50");
		deleteBtn.setStyleName("testBtn");
		deleteBtn.setId("Delete");
		iconLink.addComponent(deleteBtn);
		
		deleteBtn.setVisible(false);
		
		final SFormLayout holdBtn=new SFormLayout();
		holdBtn.addComponent(new SLabel(null, "Hold","btn_caption"));
		holdBtn.setWidth("60");
		holdBtn.setHeight("50");
		holdBtn.setStyleName("testBtn");
		holdBtn.setId("Hold");
		iconLink.addComponent(holdBtn);
		
		final SFormLayout printBtn=new SFormLayout();
		printBtn.addComponent(new SLabel(null, "Print","btn_caption"));
		printBtn.setWidth("60");
		printBtn.setHeight("50");
		printBtn.setStyleName("testBtn");
		printBtn.setId("Print");
		iconLink.addComponent(printBtn);
		
		final SFormLayout printKitchen=new SFormLayout();
		printKitchen.addComponent(new SLabel(null, "Print Kitchen","btn_caption"));
		printKitchen.setWidth("70");
		printKitchen.setHeight("50");
		printKitchen.setStyleName("testBtn");
		printKitchen.setId("Print Kitchen");
		iconLink.addComponent(printKitchen);
		
		final SFormLayout recall=new SFormLayout();
		recall.addComponent(new SLabel(null, "Recall","btn_caption"));
		recall.setWidth("60");
		recall.setHeight("50");
		recall.setStyleName("testBtn");
		recall.setId("Recall");
		iconLink.addComponent(recall);
		
		final SFormLayout cancelBill=new SFormLayout();
		cancelBill.addComponent(new SLabel(null, "Cancel Bill","btn_caption"));
		cancelBill.setWidth("70");
		cancelBill.setHeight("50");
		cancelBill.setStyleName("testBtn");
		cancelBill.setId("Cancel Bill");
		iconLink.addComponent(cancelBill);
		cancelBill.setVisible(false);
		
		final SFormLayout addNewBill=new SFormLayout();
		addNewBill.addComponent(new SLabel(null, "Add New","btn_caption"));
		addNewBill.setWidth("70");
		addNewBill.setHeight("50");
		addNewBill.setStyleName("testBtn");
		addNewBill.setId("Add New Bill");
		iconLink.addComponent(addNewBill);
		
		
		allHeaders=new String[] { TBC_SN,TBC_ITEM_ID,TBC_ITEM_NAME, TBC_QTY , TBC_PRICE, TBC_AMOUNT};
		requiredHeaders=new String[] { TBC_SN, TBC_ITEM_NAME, TBC_QTY , TBC_PRICE, TBC_AMOUNT};
		
		comDao=new CommonMethodsDao();
		itemDao = new ItemDao();
		unitDao=new UnitDao();
		daoObj=new HotelSalesDao();
		panel=new SPanel();
		
		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute("settings");
		
		itemName=new SLabel("Item Name :");
		itemName.setContentMode(ContentMode.HTML);
		
		quantityTextField=new STextField("Quantity", 330);
		quantityTextField.setHeight("40");
		
		quantityTextField.setStyleName("qty_textfield_style");
		
		
		keyboardTextField=new STextField("Value", 330);
		keyboardTextField.setHeight("40");
		keyboardTextField.setStyleName("qty_textfield_style");
		
		taxTextField=new STextField(null, 70,"0");
		taxTextField.setStyleName("textfield_align_right_color_red");
		discountTextField=new STextField(null, 80,"0");
		discountTextField.setStyleName("textfield_align_right_color_red");
		
		
		
		totalTextField=new STextField(null, 100,"0");
		totalTextField.setStyleName("textfield_align_right_color_red");
		chargesTextField=new STextField(null, 80,"0");
		chargesTextField.setStyleName("textfield_align_right_color_red");
		netTotalTextField=new STextField(null, 100,"0");
		netTotalTextField.setStyleName("textfield_align_right_color_red");
		
		taxTextField.setReadOnly(true);
		totalTextField.setReadOnly(true);
		netTotalTextField.setReadOnly(true);
		
		
		popupKeyPad=new SKeyPad(null,80,80);
		popLay=new SFormLayout(null, 490, 500);
		popLay.setMargin(true);
		popLay.addComponent(itemName);
		popLay.addComponent(quantityTextField);
		popLay.addComponent(popupKeyPad);
		popLay.setStyleName("hotel_keypad_style");
		pop=new SPopupView("", popLay);
		pop.setHideOnMouseOut(false);
		
		keyBoard=new SKeyPad(null,80,80);
		keyboardLay=new SFormLayout(null, 490, 500);
		keyboardLay.setMargin(true);
		keyboardLay.addComponent(keyboardTextField);
		keyboardLay.addComponent(keyBoard);
		popKeyboad=new SPopupView("", keyboardLay);
		popKeyboad.setHideOnMouseOut(false);
		
		try {
			
			
			cardTypeSelect=new SNativeSelect(null, 70, Arrays.asList( new KeyValue((long) 0, "NONE"),new KeyValue((long) 1, "VISA"), new KeyValue((long) 2, "MASTRO" )),"key", "value");
			cardTypeSelect.setValue((long)0);
			bankSelect=new SComboField(null, 70, new BankAccountDao().getAllActiveBankAccountNames(getOfficeID()),"id", "name",true,"Select");
			
			ccNoTFld=new STextField(null, 150);
			cardAmtTFld=new STextField(null, 100,"0");
			cardAmtTFld.setStyleName("textfield_align_right_color_red");
			checkNoTFld=new STextField(null, 100);
			bankAmtTFld=new STextField(null, 100,"0");
			bankAmtTFld.setStyleName("textfield_align_right_color_red");
			creditAmtTFld=new STextField(null, 100,"0");
			creditAmtTFld.setStyleName("textfield_align_right_color_red");
			checkDate=new SDateField(null, 90,getDateFormat(),getWorkingDate());
			
			grantTotalTFld=new STextField(null, 100,"0.0");
			grantTotalTFld.setStyleName("textfield_align_right_color_red");
			balanceTFld=new STextField(null, 100,"0");
			balanceTFld.setStyleName("textfield_align_right_color_red");
			grantTotalTFld.setReadOnly(true);
			balanceTFld.setReadOnly(true);
			
			List users=new UserManagementDao().getUsersWithFullNameAndCodeUnderOfficeIncludingSemiAdmin(
	        		getOfficeID(), getOrganizationID());
			
			itemLay=new SGridLayout();
			itemLay.setSpacing(true);
			itemLay.setColumns(4);
			itemLay.setWidth("470px");
			itemLay.setHeight("550px");
			itemLay.setStyleName("hotel_item_panel_style");
			
//			itemSelectList=new SListSelect(null, 347, 350);
//			itemSelectList.setNullSelectionAllowed(false);
			
			categorySelectList=new SListSelect("Categories", 220, 530,daoObj.
					getAllItemSubGroupsNamesWithSalesOnly(getOrganizationID(),getOfficeID()),"id","name");
			categorySelectList.setNullSelectionAllowed(false);
			
			salesNumberList=new SComboField(null, 80);
			loadSale(0);
			
			categorySelectList.setStyleName("select_list_style");
//			itemSelectList.setStyleName("select_list_style");
			
			table = new STable(null);
			
			mainVertLay=new SVerticalLayout();
			headLay1=new SGridLayout(10,1);
			bodyLay1=new SHorizontalLayout();
			headLay2=new SHorizontalLayout();
			
//			bodyLay2.addComponent();
			
			mainVertLay.setStyleName("htl_main_verti_lay");
			headLay1.setStyleName("htl_head_lay1");
			bodyLay1.setStyleName("htl_body_lay1");
			headLay2.setStyleName("htl_head_lay2");
			
			mainVertLay.addComponent(headLay1);
			mainVertLay.addComponent(bodyLay1);
			mainVertLay.addComponent(headLay2);
			
			List tabList=new TableDao().getAllTables(getOfficeID());
			tabList.add(0,new TableModel((long)0, "Take Away"));
			
			tableField=new SComboField(null,150,tabList,"id","tableNo");
			tableField.setValue((long)0);
			employeeField=new SComboField(null,200,new UserManagementDao()
							.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name");
			employeeField.setInputPrompt("------------------------- Select --------------------------");
			employeeField.setValue(getUserID());
			customerLabel=new SLabel(null,150);
			
			headLay1.setSizeFull();
			headLay1.addComponent(new SLabel("Bill No"));
			headLay1.addComponent(salesNumberList);
			headLay1.addComponent(new SLabel("Table"));
			headLay1.addComponent(tableField);
			headLay1.addComponent(new SLabel("Customer"));
			headLay1.addComponent(customerLabel);
			headLay1.addComponent(new SLabel("Employee"));
			headLay1.addComponent(employeeField);
			// Expand Ratio
			headLay1.setColumnExpandRatio(0, 1f);
			headLay1.setColumnExpandRatio(1, 1);
			headLay1.setColumnExpandRatio(2, .5f);
			headLay1.setColumnExpandRatio(3, 1);
			headLay1.setColumnExpandRatio(4, 1);
			headLay1.setColumnExpandRatio(5, 1);
			headLay1.setColumnExpandRatio(6, 1);
			headLay1.setColumnExpandRatio(7, 1);
			// Alignment
			headLay1.setSpacing(true);
//			headLay1.setComponentAlignment(salesNumberList, Alignment.MIDDLE_CENTER);
//			headLay1.setComponentAlignment(tableField, Alignment.MIDDLE_CENTER);
//			headLay1.setComponentAlignment(customerLabel, Alignment.MIDDLE_CENTER);
//			headLay1.setComponentAlignment(employeeField, Alignment.MIDDLE_CENTER);
//			headLay1.setComponentAlignment(selectCateg, Alignment.MIDDLE_CENTER);
			
		
			
			date = new SDateField(null, 120, "dd/MMM/yyyy", new Date());
			
			table.setMultiSelect(false);

			table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty(TBC_ITEM_ID, Long.class, null,
					TBC_ITEM_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_ITEM_NAME, String.class, null,
					TBC_ITEM_NAME, null, Align.LEFT);
			table.addContainerProperty(TBC_QTY, Double.class, null, TBC_QTY,
					null, Align.CENTER);
			table.addContainerProperty(TBC_PRICE, Double.class, null, TBC_PRICE,
					null, Align.RIGHT);
			table.addContainerProperty(TBC_AMOUNT, Double.class, null, TBC_AMOUNT,
					null, Align.RIGHT);
			
			table.setColumnExpandRatio(TBC_SN, (float) 0.3);
			table.setColumnExpandRatio(TBC_ITEM_NAME, 2);
			table.setColumnExpandRatio(TBC_QTY, (float).6);
			table.setColumnExpandRatio(TBC_PRICE, (float).8);
			table.setColumnExpandRatio(TBC_AMOUNT, 1);
			
			
			table.setVisibleColumns(new String[] { TBC_SN, TBC_ITEM_NAME, TBC_QTY,TBC_PRICE,TBC_AMOUNT});
			
			table.setColumnFooter(TBC_AMOUNT, "0");
			
			table.setFooterVisible(true);
			table.setColumnFooter(TBC_ITEM_NAME, "Total :");
			table.setColumnFooter(TBC_QTY, asString(0.0));

			table.setStyleName("htl_tbl_style");
			table.setSizeFull();
			table.setSelectable(true);
			table.setNullSelectionAllowed(true);
			table.setWidth("492");
			table.setHeight("270");
			

			table.setColumnReorderingAllowed(true);
			table.setColumnCollapsingAllowed(true);

			saveSalesButton = new SButton("Save", 70);
			saveSalesButton.setStyleName("savebtnStyle");
			saveSalesButton.setIcon(new ThemeResource(
					"icons/saveSideIcon.png"));

			updateSalesButton = new SButton("Update", 80);
			updateSalesButton.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
			updateSalesButton.setStyleName("updatebtnStyle");

			deleteSalesButton = new SButton("Delete", 78);
			deleteSalesButton.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
			deleteSalesButton.setStyleName("deletebtnStyle");

			printButton = new SButton("Print");
			printButton.setIcon(new ThemeResource(
					"icons/print.png"));
			
			SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
			mainButtonLayout.addComponent(saveSalesButton);
			mainButtonLayout.addComponent(updateSalesButton);
			mainButtonLayout.addComponent(deleteSalesButton);
			mainButtonLayout.addComponent(printButton);
			updateSalesButton.setVisible(false);
			deleteSalesButton.setVisible(false);
			printButton.setVisible(false);
			
			SVerticalLayout detailsVetLay=new SVerticalLayout();
			SGridLayout horz=new SGridLayout(8,1);
			horz.addComponent(new SLabel("Total"));
			horz.addComponent(totalTextField);
			if(isTaxEnable()){
			horz.addComponent(new SLabel("Tax"));
			horz.addComponent(taxTextField);
			}
			if(isShippingChargeEnable()){
			horz.addComponent(new SLabel("Charges"));
			horz.addComponent(chargesTextField);
			}
			horz.addComponent(new SLabel("Discount"));
			horz.addComponent(discountTextField);
			horz.addComponent(new SLabel("Net Total"));
			horz.addComponent(netTotalTextField);
			
//			horz.setMargin(true);
			horz.setSpacing(true);
			horz.setStyleName("htl_body_lay3");
			detailsVetLay.addComponent(table);
			detailsVetLay.addComponent(horz);
			detailsVetLay.setStyleName("htl_body_lay2");
			
			bodyLay1.addComponent(detailsVetLay);
			bodyLay1.addComponent(pop);
			bodyLay1.addComponent(popKeyboad);
			
			bodyLay1.addComponent(itemLay);
			bodyLay1.addComponent(categorySelectList);
			
			bodyLay1.setExpandRatio(detailsVetLay, 2);
			bodyLay1.setExpandRatio(categorySelectList, 1);
			
			
			bodyLay1.setComponentAlignment(detailsVetLay, Alignment.TOP_RIGHT);
			bodyLay1.setComponentAlignment(categorySelectList, Alignment.TOP_RIGHT);
			
			SVerticalLayout vertiLay1=new SVerticalLayout();
			vertiLay1.setStyleName("htl_sal_btm_left_lay");
			
			SHorizontalLayout lay1=new SHorizontalLayout();
			lay1.addComponent(new SLabel("Card"));
			lay1.addComponent(cardTypeSelect);
			lay1.addComponent(new SLabel("C.C.No"));
			lay1.addComponent(ccNoTFld);
			lay1.addComponent(cardAmtTFld);
			lay1.setSpacing(true);
			vertiLay1.addComponent(lay1);
			
			SHorizontalLayout lay2=new SHorizontalLayout();
			lay2.addComponent(new SLabel("Bank"));
			lay2.addComponent(bankSelect);
			lay2.addComponent(new SLabel("Chq.No"));
			lay2.addComponent(checkNoTFld);
			lay2.addComponent(new SLabel("Date"));
			lay2.addComponent(checkDate);
			lay2.addComponent(bankAmtTFld);
			lay2.setSpacing(true);
			vertiLay1.addComponent(lay2);
			
			SHorizontalLayout lay3=new SHorizontalLayout();
			lay3.addComponent(new SLabel("Cash",null, 200));
			lay3.addComponent(creditAmtTFld);
			lay3.setExpandRatio(creditAmtTFld, 4);
			vertiLay1.addComponent(lay3);
			
			SHorizontalLayout lay4=new SHorizontalLayout();
			lay4.addComponent(new SLabel("Total Col",null, 200));
			lay4.addComponent(grantTotalTFld);
			lay4.setExpandRatio(grantTotalTFld, 4);
			vertiLay1.addComponent(lay4);
			
			SHorizontalLayout lay5=new SHorizontalLayout();
			lay5.addComponent(new SLabel("Balance",null, 200));
			lay5.addComponent(balanceTFld);
			lay5.setExpandRatio(balanceTFld, 4);
			vertiLay1.addComponent(lay5);
			
			detailsVetLay.addComponent(vertiLay1);
			
			SVerticalLayout lay6=new SVerticalLayout();
//			lay6.setWidth("550");
//			lay6.setHeight("196");
//			lay6.addComponent(mainButtonLayout);
			lay6.addComponent(iconLink);
			lay6.setSpacing(true);
			lay6.setMargin(true);
			lay6.setComponentAlignment(iconLink, Alignment.TOP_LEFT);
			vertiLay1.addComponent(lay6);
			
			
//			mainVertLay.addComponent(iconLink);
			
			panel.setContent(mainVertLay);
			
			popupKeyPad.setImmediate(true);
			
			taxTextField.setImmediate(true);
			discountTextField.setImmediate(true);
			
			bankAmtTFld.setReadOnly(true);
			cardAmtTFld.setReadOnly(true);
			
			cardTypeSelect.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						
						if(!cardTypeSelect.getValue().toString().equals("0")) {
							cardAmtTFld.setNewValue("0");
							cardAmtTFld.setReadOnly(false);
						}
						else {
							cardAmtTFld.setNewValue("0");
							cardAmtTFld.setReadOnly(true);
						}
						calculateSubNetPrice();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			bankSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(bankSelect.getValue()!=null&&!bankSelect.getValue().equals("")) {
							bankAmtTFld.setNewValue("0");
							bankAmtTFld.setReadOnly(false);
						}
						else {
							bankAmtTFld.setNewValue("0");
							bankAmtTFld.setReadOnly(true);
						}
						calculateSubNetPrice();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			itemLayoutrListener=new LayoutClickListener() {
				
				@Override
				public void layoutClick(LayoutClickEvent event) {
					if(event.getComponent().getId()!=null){
						long itemId=toLong(event.getComponent().getId());
						table.setVisibleColumns(allHeaders);
						boolean isExist=false;
						int id = 0, ct = 0;
						Iterator it = table.getItemIds().iterator();
						while (it.hasNext()) {
							id = (Integer) it.next();
							Item itm=table.getItem(id);
							
							if(itm.getItemProperty(TBC_ITEM_ID).getValue().toString().equals(event.getComponent().getId())) {
								itm.getItemProperty(TBC_QTY).setValue((Double)itm.getItemProperty(TBC_QTY).getValue()
										+1);
								
								itm.getItemProperty(TBC_AMOUNT).setValue((Double)itm.getItemProperty(TBC_QTY).getValue()
										*(Double)itm.getItemProperty(TBC_PRICE).getValue());
								isExist=true;
							}
							
						}
						id++;
						
						if(!isExist) {

							ItemModel itm=null;
							try {
								itm = itemDao.getItem(itemId);
						
							table.addItem(
									new Object[] {
											table.getItemIds().size() + 1,
											itemId,
											itm.getName(),
											1.0,
											itm.getRate(),
											itm.getRate()*1
											}, id);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						else {
							
							Iterator iter=table.iterator();
							Item oldItm;
							Item itm=null;
							while (iter.hasNext()) {
								oldItm = table.getItem((Object) iter.next());
								if(oldItm.getItemProperty(TBC_ITEM_ID).getValue().equals(event.getComponent().getId())){
									itm=oldItm;
									break;
								}
							}
							
							if (itm != null) {
								double qty = toDouble(itm
										.getItemProperty(TBC_QTY).getValue()
										.toString());

								itm.getItemProperty(TBC_QTY).setValue(qty + 1);

								itm.getItemProperty(TBC_AMOUNT).setValue(
										(Double) itm.getItemProperty(TBC_QTY)
												.getValue()
												* (Double) itm.getItemProperty(
														TBC_PRICE).getValue());
							}
							
						}
						table.setVisibleColumns(requiredHeaders);

						quantityTextField.setValue("");
						
						calculateTotals();
						table.setVisibleColumns(requiredHeaders);
					}
				}
			};
					
			
			
			table.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if(table.getValue()!=null) {
							focusType=2;
							pop.setPopupVisible(true);
							quantityTextField.setValue(table.getItem(table.getValue()).getItemProperty(TBC_QTY).toString());
							quantityTextField.setComponentError(null);
							itemName.setValue(table.getItem(table.getValue()).getItemProperty(TBC_ITEM_NAME).toString());
						}else{
							itemName.setValue("");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			iconLink.addLayoutClickListener(new LayoutClickListener() {
				@Override
				public void layoutClick(LayoutClickEvent event) {
					// TODO Auto-generated method stub
					
					if(event.getChildComponent().getId().equals("Pay")) {
						if(salesNumberList.getValue()==null || salesNumberList.getValue().toString().equals("0")) {
							boolean valid=false;
							try {
								if(toDouble(grantTotalTFld.getValue())==toDouble(netTotalTextField.getValue()))
									valid=true;
							} catch (Exception e) {
								// TODO: handle exception
							}
							if(valid)
								saveAsBill();
							else {
								Notification.show("Invalid Paying Amount..!",
										"Paying amount and Total amount must be Same.",
										Type.ERROR_MESSAGE);
							}
						}
						else {
							boolean valid=false;
							try {
								if(toDouble(grantTotalTFld.getValue())==toDouble(netTotalTextField.getValue()))
									valid=true;
							} catch (Exception e) {
								// TODO: handle exception
							}
							if(valid)
								updateAsBill();
							else {
								Notification.show("Invalid Paying Amount..!",
										"Paying amount and Total amount must be Same.",
										Type.ERROR_MESSAGE);
							}
						}
					}
					else if(event.getChildComponent().getId().equals("Confirm")) {
						if(salesNumberList.getValue()==null || salesNumberList.getValue().toString().equals("0")) {
							saveAsConfirm();
						}
						else {
							updateAsConfirm();
						}
					}
					else if(event.getChildComponent().getId().equals("Delete")) {
						deleteSalesButton.click();
					}
					else if(event.getChildComponent().getId().equals("Hold")) {
						if(salesNumberList.getValue()==null || salesNumberList.getValue().toString().equals("0")) {
							saveAsHold();
						}
						else {
							updateAsHold();
						}
					}
					else if(event.getChildComponent().getId().equals("Print")) {
						printButton.click();
					}
					else if(event.getChildComponent().getId().equals("Print Kitchen")) {
						printButton.click();
					}
					else if(event.getChildComponent().getId().equals("Recall")) {
						isRecall=true;
						salesNumberList.setValue(historyList.get(0));
						historyList.add(historyList.get(0));
						historyList.remove(0);
						
					}
					else if(event.getChildComponent().getId().equals("Add New Bill")) {
						salesNumberList.setValue((long)0);
					}
					else if(event.getChildComponent().getId().equals("Cancel Bill")) {
						if (salesNumberList.getValue() != null
								&& !salesNumberList.getValue().toString()
										.equals("0")) {

							ConfirmDialog.show(getUI(), "Are you sure?",
									new ConfirmDialog.Listener() {
										public void onClose(ConfirmDialog dialog) {
											if (dialog.isConfirmed()) {
												try {
													daoObj.cancelOrder(toLong(salesNumberList
															.getValue().toString()));
													Notification
															.show("Success",
																	"Deleted Successfully..!",
																	Type.WARNING_MESSAGE);
													loadSale(0);
													
													categorySelectList.setValue(null);

												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
									});
						}
					}
					
				}
			});
			
			
			
			
			categorySelectList.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					if(categorySelectList.getValue()!=null)
						loadItems((Long)categorySelectList.getValue());
				}
			});
			
			
			
			saveSalesButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {
						if (isValid()) {
							
							long customer_id = 1;

							HotelSalesModel salObj = new HotelSalesModel();

							List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

							HotelSalesInventoryDetailsModel invObj;
							ItemModel itemObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new HotelSalesInventoryDetailsModel();
								item = table.getItem(it.next());
								
								itemObj=itemDao.getItem((Long) item
										.getItemProperty(TBC_ITEM_ID).getValue());

								invObj.setItem(itemObj);
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());

								invObj.setTax(new TaxModel(1));
								invObj.setTax_amount(0);
								invObj.setTax_percentage(0);

								invObj.setUnit(itemObj.getUnit());
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_PRICE)
										.getValue());

								if (isDiscountEnable()) {
									invObj.setDiscount_amount(0);
								}

								invObj.setOrder_id(0);
								invObj.setCess_amount(0);


								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY)
										.getValue());

								itemsList.add(invObj);


							}
							
							if(((Long)cardTypeSelect.getValue())!=0 || (bankSelect.getValue())!=null) {
								
								CashPayDetailsModel payModel=new CashPayDetailsModel();
								payModel.setBank_amount(toDouble(bankAmtTFld.getValue()));
								payModel.setBank_type((Long) bankSelect.getValue());
								payModel.setCard_amount(toDouble(cardAmtTFld.getValue()));
								payModel.setCard_type((Long) cardTypeSelect.getValue());
								payModel.setCcn_no(ccNoTFld.getValue());
								payModel.setCheque_date(CommonUtil.getSQLDateFromUtilDate(checkDate.getValue()));
								payModel.setCheque_no(checkNoTFld.getValue());
								
								salObj.setCash_pay_id(payModel);
							}

							salObj.setShipping_charge(toDouble(chargesTextField
									.getValue()));
							salObj.setExcise_duty(toDouble(taxTextField
									.getValue()));
							
							salObj.setDiscount(toDouble(discountTextField.getValue()));
							salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
							salObj.setAmount(toDouble(netTotalTextField.getValue()));

							salObj.setComments("");
							salObj.setDate(CommonUtil.getCurrentSQLDate());
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							
							salObj.setCustomer(customerLabel.getValue());
							salObj.setSales_person((Long)employeeField.getValue());
							salObj.setInventory_details_list(itemsList);

							salObj.setSales_number(getNextSequence(
									"Sales Number", getLoginID()));
							
							salObj.setStatus(1);
							
							FinTransaction trans = new FinTransaction();
							double totalAmt = salObj.getAmount();
							double netAmt = totalAmt;

							double amt = 0;
							
							int status=0;
							double payingAmt = salObj.getPayment_amount();

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								
								salObj.setStatus(1);
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								salObj.setStatus(2);
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								status = 3;
								salObj.setStatus(3);
							}

//							if (isTaxEnable()) {
//								if (settings.getSALES_TAX_ACCOUNT() != 0) {
//									amt = toDouble(taxTextField.getValue());
//									if (amt != 0) {
//										trans.addTransaction(
//												SConstants.CR,
//												settings.getSALES_TAX_ACCOUNT(),
//												settings.getCGS_ACCOUNT(),
//												roundNumber(amt));
//										totalAmt -= amt;
//									}
//								}
//
//							}
//
//
//							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
//								amt = toDouble(chargesTextField
//										.getValue());
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}

//							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
//								amt = toDouble(discountTextField
//										.getValue());
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_DESCOUNT_ACCOUNT(),
//											settings.getCASH_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}

//							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_REVENUE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(totalAmt));
//								}
//							}
							
							long id = daoObj.save(salObj,
									trans.getTransaction(SConstants.SALES,CommonUtil.getSQLDateFromUtilDate(date.getValue())),payingAmt);
							
							
							saveActivity(getOptionId(), "New Sales Created. Bill No : "+salObj.getSales_number()+
									", Amount : "+salObj.getAmount());

							loadSale(id);

							Notification.show("Success",
									"Saved Successfully..!",
									Type.WARNING_MESSAGE);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			
			salesNumberList.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						removeAllErrors();
						
						deleteBtn.setVisible(true);

						updateSalesButton.setVisible(true);
						deleteSalesButton.setVisible(true);
						printButton.setVisible(true);
						saveSalesButton.setVisible(false);
						
						holdBtn.setVisible(true);
						confirmBtn.setVisible(true);
						cancelBill.setVisible(false);
						
						if (salesNumberList.getValue() != null && !salesNumberList.getValue().toString().equals("0")) {
							
							HotelSalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());
							
							if(salObj.getStatus()==STS_PAID) {
								holdBtn.setVisible(false);
								confirmBtn.setVisible(false);
								cancelBill.setVisible(false);
							}
							
							
							if(salObj.getStatus()==STS_CONFIRMED) {
								holdBtn.setVisible(false);
								cancelBill.setVisible(false);
							}

							table.setVisibleColumns(allHeaders);

							table.removeAllItems();

							HotelSalesInventoryDetailsModel invObj;
							double netTotal=0;
							Iterator it = salObj.getInventory_details_list()
									.iterator();
							int id=1;
							while (it.hasNext()) {
								invObj = (HotelSalesInventoryDetailsModel) it
										.next();

								table.addItem(
										new Object[] {
												table.getItemIds().size() + 1,
												invObj.getItem().getId(),
												invObj.getItem().getName(),
												invObj.getQunatity(),
												invObj.getUnit_price(),
												invObj.getQunatity()*invObj.getUnit_price()
												}, id);
								
								id++;
								netTotal+=invObj.getQunatity()*invObj.getUnit_price();
								
							}
							
							creditAmtTFld.setNewValue("0");

							table.setVisibleColumns(requiredHeaders);

							date.setValue(salObj.getDate());
							
							
							chargesTextField.setValue(asString(salObj
									.getShipping_charge()));
							taxTextField.setNewValue(asString(salObj
									.getExcise_duty()));
							
							totalTextField.setNewValue(asString(netTotal));
							discountTextField.setValue(asString(salObj
									.getDiscount()));
							netTotalTextField.setNewValue(asString(netTotal-salObj.getDiscount()));

							double ttlPaid=0;
							if(salObj.getCash_pay_id()!=null) {
								bankAmtTFld.setNewValue(asString(salObj.getCash_pay_id().getBank_amount()));
								bankSelect.setValue(salObj.getCash_pay_id().getBank_type());
								cardAmtTFld.setNewValue(asString(salObj.getCash_pay_id().getCard_amount()));
								cardTypeSelect.setValue(salObj.getCash_pay_id().getCard_type());
								ccNoTFld.setValue(salObj.getCash_pay_id().getCcn_no());
								checkDate.setValue(salObj.getCash_pay_id().getCheque_date());
								checkNoTFld.setValue(salObj.getCash_pay_id().getCheque_no());
								ttlPaid+=salObj.getCash_pay_id().getBank_amount()+salObj.getCash_pay_id().getCard_amount();
								
							}
							else {
								bankAmtTFld.setNewValue("0");
								bankSelect.setValue(null);
								cardAmtTFld.setNewValue("0");
								cardTypeSelect.setValue((long)0);
								ccNoTFld.setValue("");
								checkDate.setValue(getWorkingDate());
								checkNoTFld.setValue("");
							}
							
							if((salObj.getPayment_amount()-ttlPaid)>0)
								creditAmtTFld.setNewValue(asString(salObj.getPayment_amount()-ttlPaid));
							
							tableField.setValue(salObj.getTableId());
							employeeField.setValue(salObj.getSales_person());
							
							updateSalesButton.setVisible(true);
							printButton.setVisible(true);
							deleteSalesButton.setVisible(true);
							saveSalesButton.setVisible(false);
							
//							if(!isRecall) {
							setRecall();
//								isRecall=false;
//							}
							
							
						} else {
							table.removeAllItems();
							
							chargesTextField.setNewValue("0");
							totalTextField.setNewValue("0");
							grantTotalTFld.setNewValue("0");
							netTotalTextField.setNewValue("0");
							taxTextField.setNewValue("0");
							checkDate.setValue(getWorkingDate());
							
							discountTextField.setValue("0");
							creditAmtTFld.setNewValue("0");
							
							creditAmtTFld.setNewValue("0");
							tableField.setValue(null);
							employeeField.setValue(null);
							
							saveSalesButton.setVisible(true);
							updateSalesButton.setVisible(false);
							printButton.setVisible(false);
							deleteSalesButton.setVisible(false);
							
							deleteBtn.setVisible(false);
						}

						calculateTotals();
						calculateSubNetPrice();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Notification.show("Error..!!",
								"Error Message :" + e.getCause(),
								Type.ERROR_MESSAGE);
					}
				}

			});
			
			
			
			

			updateSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (isValid()) {

							long customer_id = 1;

							HotelSalesModel salObj = daoObj
									.getSale((Long) salesNumberList.getValue());

							List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

							HotelSalesInventoryDetailsModel invObj;
							ItemModel itemObj;
							Item item;
							double std_cost;
							Iterator it = table.getItemIds().iterator();
							while (it.hasNext()) {
								invObj = new HotelSalesInventoryDetailsModel();
								item = table.getItem(it.next());
								
								itemObj=itemDao.getItem((Long) item
										.getItemProperty(TBC_ITEM_ID).getValue());

								invObj.setItem(itemObj);
								invObj.setQunatity((Double) item
										.getItemProperty(TBC_QTY).getValue());
								invObj.setBalance((Double) item
										.getItemProperty(TBC_QTY).getValue());

								invObj.setTax(new TaxModel(1));
								invObj.setTax_amount(0);
								invObj.setTax_percentage(0);

								invObj.setUnit(itemObj.getUnit());
								invObj.setUnit_price((Double) item
										.getItemProperty(TBC_PRICE)
										.getValue());

								if (isDiscountEnable()) {
									invObj.setDiscount_amount(0);
								}

								invObj.setOrder_id(0);
								invObj.setCess_amount(0);


								invObj.setQuantity_in_basic_unit((Double) item
										.getItemProperty(TBC_QTY)
										.getValue());

								itemsList.add(invObj);


							}
							
							if(((Long)cardTypeSelect.getValue())!=0 || (bankSelect.getValue())!=null) {
								
								CashPayDetailsModel payModel;
								
								if(salObj.getCash_pay_id()!=null)
									payModel=salObj.getCash_pay_id();
								else
									payModel=new CashPayDetailsModel();
								
								payModel.setBank_amount(toDouble(bankAmtTFld.getValue()));
								payModel.setBank_type((Long) bankSelect.getValue());
								payModel.setCard_amount(toDouble(cardAmtTFld.getValue()));
								payModel.setCard_type((Long) cardTypeSelect.getValue());
								payModel.setCcn_no(ccNoTFld.getValue());
								payModel.setCheque_date(CommonUtil.getSQLDateFromUtilDate(checkDate.getValue()));
								payModel.setCheque_no(checkNoTFld.getValue());
								
								salObj.setCash_pay_id(payModel);
							}
							else
								salObj.setCash_pay_id(null);

							salObj.setShipping_charge(toDouble(chargesTextField
									.getValue()));
							salObj.setExcise_duty(toDouble(taxTextField
									.getValue()));
							
							salObj.setDiscount(toDouble(discountTextField.getValue()));
							salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
							salObj.setAmount(toDouble(netTotalTextField.getValue()));

							salObj.setComments("");
							salObj.setDate(CommonUtil.getCurrentSQLDate());
							// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
							salObj.setLogin(new S_LoginModel(getLoginID()));
							salObj.setOffice(new S_OfficeModel(getOfficeID()));
							
							salObj.setCustomer(customerLabel.getValue());
							salObj.setSales_person((Long)employeeField.getValue());
							salObj.setInventory_details_list(itemsList);

							salObj.setStatus(1);
							
							FinTransaction trans = new FinTransaction();
							double totalAmt = salObj.getAmount();
							double netAmt = totalAmt;

							double amt = 0;
							
							int status=0;
							double payingAmt = salObj.getPayment_amount();

							if (payingAmt == netAmt) {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								
								salObj.setStatus(1);
								status = 1;
							} else if (payingAmt == 0) {
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								salObj.setStatus(2);
								status = 2;
							} else {
								trans.addTransaction(SConstants.CR,
										customer_id,
										settings.getCASH_ACCOUNT(),
										roundNumber(payingAmt));
								trans.addTransaction(SConstants.CR,
										settings.getSALES_ACCOUNT(),
										customer_id,
										roundNumber(netAmt));
								status = 3;
								salObj.setStatus(3);
							}

//							if (isTaxEnable()) {
//								if (settings.getSALES_TAX_ACCOUNT() != 0) {
//									amt = toDouble(taxTextField.getValue());
//									if (amt != 0) {
//										trans.addTransaction(
//												SConstants.CR,
//												settings.getSALES_TAX_ACCOUNT(),
//												settings.getCGS_ACCOUNT(),
//												roundNumber(amt));
//										totalAmt -= amt;
//									}
//								}
//
//							}
//
//
//							if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
//								amt = toDouble(chargesTextField
//										.getValue());
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
//								amt = toDouble(discountTextField
//										.getValue());
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(amt));
//									totalAmt -= amt;
//								}
//							}
//
//							if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
//								if (amt != 0) {
//									trans.addTransaction(
//											SConstants.CR,
//											settings.getSALES_REVENUE_ACCOUNT(),
//											settings.getCGS_ACCOUNT(),
//											roundNumber(totalAmt));
//								}
//							}
							
							TransactionModel tran = daoObj
									.getTransaction(salObj.getTransaction_id());
							tran.setTransaction_details_list(trans
									.getChildList());
							tran.setDate(salObj.getDate());
							tran.setLogin_id(getLoginID());
							
							daoObj.update(salObj, tran, payingAmt, true);
							
							saveActivity(getOptionId(), "Sales Updated. Bill No : "+salObj.getSales_number()+
									", Amount : "+salObj.getAmount());

							loadSale(salObj.getId());

							Notification.show("Success",
									"Saved Successfully..!",
									Type.WARNING_MESSAGE);
							
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			
			deleteSalesButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (salesNumberList.getValue() != null
							&& !salesNumberList.getValue().toString()
									.equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											try {
												daoObj.delete(toLong(salesNumberList
														.getValue().toString()));
												Notification
														.show("Success",
																"Deleted Successfully..!",
																Type.WARNING_MESSAGE);
												loadSale(0);
												
												categorySelectList.setValue(null);

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
					}

				}
			});

			quantityTextField.setImmediate(true);
			
			quantityTextField.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						calculateNetPrice();
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error..!!",	"Error Message :" + e.getCause(),Type.ERROR_MESSAGE);
					}

				}
			});
			

			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					deleteItem();
				}

			});
			
			addShortcutListener(new ShortcutListener(
					"Clear entereded and edited data and Add new",
					ShortcutAction.KeyCode.ESCAPE, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					table.setValue(null);
					pop.setPopupVisible(false);
				}
			});
			
			
			
			FocusListener focusListener=new FocusListener() {
				@Override
				public void focus(FocusEvent event) {
					// TODO Auto-generated method stub
					if(event.getComponent()!=null) {
						focusedComponent=event.getComponent();
						focusType=1;
						popKeyboad.setPopupVisible(true);
						if(!((STextField)focusedComponent).getValue().equals("0"))
							keyboardTextField.setValue(((STextField)focusedComponent).getValue());
						keyboardTextField.setComponentError(null);
					}
				}
			};
			
			
			/*ItemClickListener itemClickListener=new ItemClickListener() {
				
				@Override
				public void itemClick(ItemClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getItem()!=null) {
						focusedComponent=event.getComponent();
						focusType=2;
						popKeyboad.setPopupVisible(true);
						if(!((STextField)focusedComponent).getValue().equals("0"))
							keyboardTextField.setValue(((STextField)focusedComponent).getValue());
						keyboardTextField.setComponentError(null);
					}
					System.out.println("Haiiiiiiii");
				}
			};*/
			
			
			
//			table.addListener(itemClickListener);
			
			discountTextField.addListener(focusListener);
			chargesTextField.addListener(focusListener);
			ccNoTFld.addListener(focusListener);
			cardAmtTFld.addListener(focusListener);
			checkNoTFld.addListener(focusListener);
			bankAmtTFld.addListener(focusListener);
			creditAmtTFld.addListener(focusListener);
			
			
			
			ClickListener listener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					
					if(event.getComponent().getId().equals("1")) {
						quantityTextField.setValue(quantityTextField.getValue()+"1");
					}
					else if(event.getComponent().getId().equals("2")) {
						quantityTextField.setValue(quantityTextField.getValue()+"2");
					}
					else if(event.getComponent().getId().equals("3")) {
						quantityTextField.setValue(quantityTextField.getValue()+"3");
					}
					else if(event.getComponent().getId().equals("4")) {
						quantityTextField.setValue(quantityTextField.getValue()+"4");
					}
					else if(event.getComponent().getId().equals("5")) {
						quantityTextField.setValue(quantityTextField.getValue()+"5");
					}
					else if(event.getComponent().getId().equals("6")) {
						quantityTextField.setValue(quantityTextField.getValue()+"6");
					}
					else if(event.getComponent().getId().equals("7")) {
						quantityTextField.setValue(quantityTextField.getValue()+"7");
					}
					else if(event.getComponent().getId().equals("8")) {
						quantityTextField.setValue(quantityTextField.getValue()+"8");
					}
					else if(event.getComponent().getId().equals("9")) {
						quantityTextField.setValue(quantityTextField.getValue()+"9");
					}
					else if(event.getComponent().getId().equals("0")) {
						quantityTextField.setValue(quantityTextField.getValue()+"0");
					}
					else if(event.getComponent().getId().equals("00")) {
						quantityTextField.setValue(quantityTextField.getValue()+"00");
					}
					else if(event.getComponent().getId().equals(".")) {
						if(!quantityTextField.getValue().contains("."))
							quantityTextField.setValue(quantityTextField.getValue()+".");
					}
					else if(event.getComponent().getId().equals("Clr")) {
						quantityTextField.setValue("");
					}
					else if(event.getComponent().getId().equals("Close")) {
						quantityTextField.setValue("");
						pop.setPopupVisible(false);
					}
					else if(event.getComponent().getId().equals("Del")) {
						if (quantityTextField.getValue().length()>0) {
							quantityTextField.setValue(quantityTextField.getValue().substring(0, quantityTextField.getValue().length()-1));
						 }
					}
					else if(event.getComponent().getId().equals("Enter")) {
						
						if(isAddingValid()) {
						
							table.setVisibleColumns(allHeaders);
							
//							if(focusType==1) {
//							
//								boolean isExist=false;
//								int id = 0, ct = 0;
//								Iterator it = table.getItemIds().iterator();
//								while (it.hasNext()) {
//									id = (Integer) it.next();
//									Item itm=table.getItem(id);
//									
//									if(itm.getItemProperty(TBC_ITEM_ID).getValue().toString().equals(itemSelectList.getValue().toString())) {
//										itm.getItemProperty(TBC_QTY).setValue((Double)itm.getItemProperty(TBC_QTY).getValue()
//												+toDouble(quantityTextField.getValue()));
//										
//										itm.getItemProperty(TBC_AMOUNT).setValue((Double)itm.getItemProperty(TBC_QTY).getValue()
//												*(Double)itm.getItemProperty(TBC_PRICE).getValue());
//										isExist=true;
//									}
//									
//								}
//								id++;
//								
//								if(!isExist) {
//		
//									ItemModel itm=null;
//									try {
//										itm = itemDao.getItem((Long)itemSelectList.getValue());
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//									table.addItem(
//											new Object[] {
//													table.getItemIds().size() + 1,
//													itemSelectList.getValue(),
//													itm.getName(),
//													toDouble(quantityTextField.getValue()),
//													itm.getRate(),
//													itm.getRate()*toDouble(quantityTextField.getValue())
//													}, id);
//									
//								}
//							}
//							else {
								
								Item itm=table.getItem(table.getValue());
								
								itm.getItemProperty(TBC_QTY).setValue(toDouble(quantityTextField.getValue()));
								
								itm.getItemProperty(TBC_AMOUNT).setValue((Double)itm.getItemProperty(TBC_QTY).getValue()
										*(Double)itm.getItemProperty(TBC_PRICE).getValue());
								
								focusType=1;
							}
							table.setVisibleColumns(requiredHeaders);
	
							quantityTextField.setValue("");
							
							calculateTotals();
							
							pop.setPopupVisible(false);
//						}
					}
				}
			};
			
			
			ClickListener keyBoardListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					
					if(event.getComponent().getId().equals("1")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"1");
					}
					else if(event.getComponent().getId().equals("2")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"2");
					}
					else if(event.getComponent().getId().equals("3")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"3");
					}
					else if(event.getComponent().getId().equals("4")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"4");
					}
					else if(event.getComponent().getId().equals("5")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"5");
					}
					else if(event.getComponent().getId().equals("6")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"6");
					}
					else if(event.getComponent().getId().equals("7")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"7");
					}
					else if(event.getComponent().getId().equals("8")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"8");
					}
					else if(event.getComponent().getId().equals("9")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"9");
					}
					else if(event.getComponent().getId().equals("0")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"0");
					}
					else if(event.getComponent().getId().equals("00")) {
						keyboardTextField.setValue(keyboardTextField.getValue()+"00");
					}
					else if(event.getComponent().getId().equals(".")) {
						if(!keyboardTextField.getValue().contains("."))
							keyboardTextField.setValue(keyboardTextField.getValue()+".");
					}
					else if(event.getComponent().getId().equals("Clr")) {
						keyboardTextField.setValue("");
					}
					else if(event.getComponent().getId().equals("Close")) {
						keyboardTextField.setValue("");
						popKeyboad.setPopupVisible(false);
					}
					else if(event.getComponent().getId().equals("Del")) {
						if (keyboardTextField.getValue().length()>0) {
							keyboardTextField.setValue(keyboardTextField.getValue().substring(0, keyboardTextField.getValue().length()-1));
						 }
					}
					else if(event.getComponent().getId().equals("Enter")) {
						
						if(isAddingValidIncludingZero(keyboardTextField)) {
						
							((STextField)focusedComponent).setValue(keyboardTextField.getValue());
							
							keyboardTextField.setValue("");
							popKeyboad.setPopupVisible(false);
							
							calculateSubNetPrice();
						}
					}
					
				}
			};
			
			keyBoard.setListener(keyBoardListener);
			popupKeyPad.setListener(listener);
			
			
		
		printButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				List<Object> reportList = new ArrayList<Object>();
				SalesPrintBean bean = null;
				NumberToWords numberToWords = new NumberToWords();
				double total = 0;
				try {


					map.put("CUSTOMER_NAME", tableField.getItemCaption(tableField.getValue()));
					map.put("CUSTOMER_ADDRESS", "");
					map.put("SALES_BILL_NO", toLong(salesNumberList
							.getItemCaption(salesNumberList.getValue())));
					map.put("BILL_DATE", CommonUtil
							.formatDateToDDMMMYYYY(date.getValue()));
					map.put("SALES_MAN", employeeField.getItemCaption(employeeField.getValue()));

					String type = "";
					if (toDouble(balanceTFld.getValue())== 0 && toDouble(creditAmtTFld.getValue())== 0) {
						type = "Cash Sale";
					} else {
						type = "Credit Sale";
					}
					map.put("SALES_TYPE", type);
					map.put("OFFICE_NAME", getOfficeName());

					Item item;
					Iterator itr1 = table.getItemIds().iterator();
					while (itr1.hasNext()) {
						item = table.getItem(itr1.next());

						bean = new SalesPrintBean(item
								.getItemProperty(TBC_ITEM_NAME).getValue()
								.toString(), toDouble(item
								.getItemProperty(TBC_QTY).getValue()
								.toString()), toDouble(item
								.getItemProperty(TBC_PRICE).getValue()
								.toString()), toDouble(item
								.getItemProperty(TBC_AMOUNT).getValue()
								.toString()), "", "", toDouble(item
								.getItemProperty(TBC_QTY).getValue()
								.toString()));

						total += toDouble(item
								.getItemProperty(TBC_AMOUNT).getValue()
								.toString());

						reportList.add(bean);
					}

					S_OfficeModel officeModel = new OfficeDao()
							.getOffice(getOfficeID());
					map.put("AMOUNT_IN_WORDS", getAmountInWords(total));
					map.put("CURRENCY", officeModel.getCurrency().getCode());
					map.put("ORGANIZATION",  officeModel.getOrganization().getName());
					String orgLogo=VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")+ "VAADIN/themes/testappstheme/OrganizationLogos/"+officeModel.getOrganization().getLogoName();
					File file=new File(orgLogo);
					if(file.exists())
						map.put("LOGO_PATH",orgLogo);
					else
						map.put("LOGO_PATH",VaadinServlet.getCurrent().getServletContext()
								.getRealPath("/")+ "VAADIN/themes/testappstheme/OrganizationLogos/BaseLogo.png");

					Report report = new Report(getLoginID());
					report.setJrxmlFileName(getBillName(SConstants.bills.SALES));
					report.setReportFileName("SalesPrint");
					// report.setReportTitle("Sales Invoice");
					// report.setIncludeHeader(true);
					report.setReportType(Report.PDF);
					report.createReport(reportList, map);

					report.printReport();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		return panel;
	}
	
	
	
	public void setRecall() {
		
		Object id=salesNumberList.getValue();
		if(((Long)id)!=0) {
			historyList.remove(id);
			historyList.add(0,(Long) id);
		}
	}
	
	public void setRecall(long id) {
		
		if(id!=0) {
			historyList.remove(id);
			historyList.add(0,(Long) id);
		}
	}
	
	public void loadSale(long id) {
		List list;
		try {
			list = new ArrayList();
			list.add(new HotelSalesModel(0, "New"));
			list.addAll(daoObj.getAllSalesNumbersAsComment(getOfficeID()));

			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			salesNumberList.setContainerDataSource(bic);
			salesNumberList.setItemCaptionPropertyId("comments");

			categorySelectList.setValue(null);

			salesNumberList.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Error..!!",
					"Error Message from method loadSale() :" + e.getCause(),
					Type.ERROR_MESSAGE);
		}
	}


	public void calculateNetPrice(){
		
		try {
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
//	double tax_perc=(Double) getHttpSession().getAttribute("tax_percentage");
	public void calculateSubNetPrice(){
		try {
			try {
				toDouble(discountTextField.getValue());
			} catch (Exception e) {
				discountTextField.setValue("0");
				// TODO: handle exception
			}
			try {
				toDouble(chargesTextField.getValue());
			} catch (Exception e) {
				chargesTextField.setValue("0");
				// TODO: handle exception
			}
			
			
			
			totalTextField.setNewValue(table.getColumnFooter(TBC_AMOUNT));
			
			if(isTaxEnable())
				taxTextField.setNewValue(asString(toDouble(table.getColumnFooter(TBC_AMOUNT))));
//			taxTextField.setNewValue(asString(toDouble(table.getColumnFooter(TBC_AMOUNT))*tax_perc/100));
			
			netTotalTextField.setNewValue(asString(toDouble(table.getColumnFooter(TBC_AMOUNT))+toDouble(taxTextField.getValue())+toDouble(chargesTextField.getValue())-
					toDouble(discountTextField.getValue())));
			
			
			try {
				grantTotalTFld.setNewValue(asString(toDouble(cardAmtTFld.getValue())+toDouble(bankAmtTFld.getValue())+toDouble(creditAmtTFld.getValue())));
				balanceTFld.setNewValue(asString(toDouble(netTotalTextField.getValue())-toDouble(grantTotalTFld.getValue())));
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	

	public void calculateTotals() {
		try {

			double qty_ttl = 0, amt_ttl=0;

			Item item;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				item = table.getItem(it.next());
				amt_ttl+= (Double) item.getItemProperty(TBC_AMOUNT).getValue();
				qty_ttl += (Double) item.getItemProperty(TBC_QTY).getValue();
			}

			table.setColumnFooter(TBC_QTY, asString(roundNumber(qty_ttl)));
			table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(amt_ttl)));
			
			calculateSubNetPrice();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean isAddingValid() {
		boolean ret = true;
		try {

			if (quantityTextField.getValue() == null
					|| quantityTextField.getValue().equals("")) {
				setRequiredError(quantityTextField, "Enter a Quantity", true);
				quantityTextField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(quantityTextField.getValue()) <= 0) {
						setRequiredError(quantityTextField,
								"Quantity must be greater than Zero", true);
						quantityTextField.focus();
						ret = false;
					} else
						setRequiredError(quantityTextField, null, false);
				} catch (Exception e) {
					setRequiredError(quantityTextField,
							"Enter a valid Quantity", true);
					quantityTextField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}
	
	
	public boolean isAddingValid(STextField valueField) {
		boolean ret = true;
		try {

			if (valueField.getValue() == null
					|| valueField.getValue().equals("")) {
				setRequiredError(valueField, "Enter a Quantity", true);
				valueField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(valueField.getValue()) <= 0) {
						setRequiredError(valueField,
								"Quantity must be greater than Zero", true);
						valueField.focus();
						ret = false;
					} else
						setRequiredError(valueField, null, false);
				} catch (Exception e) {
					setRequiredError(valueField,
							"Enter a valid Quantity", true);
					valueField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;
	}
	
	public boolean isAddingValidIncludingZero(STextField valueField) {
		boolean ret = true;
		try {

			if (valueField.getValue() == null
					|| valueField.getValue().equals("")) {
				setRequiredError(valueField, "Enter a Quantity", true);
				valueField.focus();
				ret = false;
			} else {
				try {
					if (toDouble(valueField.getValue()) < 0) {
						setRequiredError(valueField,
								"Quantity must be greater than Zero", true);
						valueField.focus();
						ret = false;
					} else
						setRequiredError(valueField, null, false);
				} catch (Exception e) {
					setRequiredError(valueField,
							"Enter a valid Quantity", true);
					valueField.focus();
					ret = false;
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return ret;

	}
	
	

	public void visibleAddupdateSalesButton(boolean AddVisible,
			boolean UpdateVisible) {
		addItemButton.setVisible(AddVisible);
		updateItemButton.setVisible(UpdateVisible);
	}

	public void deleteItem() {
		try {

			if (table.getValue() != null) {

				table.removeItem(table.getValue());

				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;

					newitem = table.getItem((Integer) it.next());

					newitem.getItemProperty(TBC_SN).setValue(SN);

				}

				calculateTotals();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void loadProductions(String id) {
		try {
			salesNumberList.removeAllItems();
			salesNumberList.addItem("New");
			
			salesNumberList.setValue(id);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	

	public Boolean isValid() {

		boolean ret = true;

		if (table.getItemIds().size() <= 0) {
			setRequiredError(table, "Add some items", true);
			ret = false;
		} else
			setRequiredError(table, null, false);
		
		
		if(((Long)cardTypeSelect.getValue())!=0) {
			try {
				if(toDouble(cardAmtTFld.getValue())<=0) {
					setRequiredError(cardAmtTFld, "Enter a valid amount", true);
					cardAmtTFld.focus();
					ret = false;
				}
				else
					cardAmtTFld.setComponentError(null);
				
			} catch (Exception e) {
				setRequiredError(cardAmtTFld, "Enter a valid amount", true);
				cardAmtTFld.focus();
				ret = false;
				// TODO: handle exception
			}
		}
		
		if((bankSelect.getValue())!=null) {
			try {
				if(checkDate.getValue()==null) {
					setRequiredError(checkDate, "Select a date", true);
					checkDate.focus();
					ret = false;
				}
				else
					checkDate.setComponentError(null);
				
				if(toDouble(bankAmtTFld.getValue())<=0) {
					setRequiredError(bankAmtTFld, "Enter a valid amount", true);
					bankAmtTFld.focus();
					ret = false;
				}
				else
					bankAmtTFld.setComponentError(null);
				
			} catch (Exception e) {
				
				setRequiredError(cardAmtTFld, "Enter a valid amount", true);
				cardAmtTFld.focus();
				ret = false;
			}
			
		}
		
		if(employeeField.getValue()==null) {
			setRequiredError(employeeField, "Select an Employee", true);
			employeeField.focus();
			ret = false;
		}
		else
			employeeField.setComponentError(null);
		

		if(tableField.getValue()==null) {
			setRequiredError(tableField, "Select a table", true);
			tableField.focus();
			ret = false;
		}
		else
			tableField.setComponentError(null);
		
		return ret;
	}

	public void removeAllErrors() {
		if (table.getComponentError() != null)
			setRequiredError(table, null, false);
		if (quantityTextField.getComponentError() != null)
			setRequiredError(quantityTextField, null, false);
		
		cardAmtTFld.setComponentError(null);
		bankAmtTFld.setComponentError(null);
		checkDate.setComponentError(null);
	}

	public Boolean getHelp() {
		return null;
	}
	
	
	

	public void updateAsHold() {
		try {
			if (isValid()) {
				
				long customer_id = 1;

				HotelSalesModel salObj = daoObj.getSale((Long) salesNumberList.getValue());

				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());

					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());
					
					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);

					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());

					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}

					invObj.setOrder_id(0);
					invObj.setCess_amount(0);


					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());

					itemsList.add(invObj);
					
					
				}
				
				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));

				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				
				salObj.setCustomer(customerLabel.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setInventory_details_list(itemsList);

				
				salObj.setStatus(STS_HOLD);
				
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;

				double amt = 0;
				
				int status=0;
				double payingAmt = salObj.getPayment_amount();


				long id = daoObj.updateHold(salObj);
				
				setRecall(id);
				saveActivity(getOptionId(), "New Sales Created. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());

				loadSale(0);
				
				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void saveAsHold() {
		try {
			if (isValid()) {
				
				long customer_id = 1;

				HotelSalesModel salObj = new HotelSalesModel();

				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());

					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());

					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);

					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());

					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}

					invObj.setOrder_id(0);
					invObj.setCess_amount(0);


					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());

					itemsList.add(invObj);


				}
				
				/*if(((Long)cardTypeSelect.getValue())!=0 || ((Long)bankSelect.getValue())!=0) {
					
					CashPayDetailsModel payModel=new CashPayDetailsModel();
					payModel.setBank_amount(toDouble(bankAmtTFld.getValue()));
					payModel.setBank_type((Long) bankSelect.getValue());
					payModel.setCard_amount(toDouble(cardAmtTFld.getValue()));
					payModel.setCard_type((Long) cardTypeSelect.getValue());
					payModel.setCcn_no(ccNoTFld.getValue());
					payModel.setCheque_date(CommonUtil.getSQLDateFromUtilDate(checkDate.getValue()));
					payModel.setCheque_no(checkNoTFld.getValue());
					
					salObj.setCash_pay_id(payModel);
				}*/

				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));

				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				
				salObj.setCustomer(customerLabel.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setInventory_details_list(itemsList);

				salObj.setSales_number(getNextSequence(
						"Sales Number", getLoginID()));
				
				
				salObj.setStatus(STS_HOLD);
				
//				FinTransaction trans = new FinTransaction();
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;

				double amt = 0;
				
				int status=0;
				double payingAmt = salObj.getPayment_amount();


				long id = daoObj.saveHold(salObj);
				
				setRecall(id);
				
				saveActivity(getOptionId(), "New Sales Created. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());

				loadSale(0);
				
				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateAsConfirm() {
		
		if (isValid()) {
			
			try {
				long customer_id = 1;
				
				HotelSalesModel salObj = daoObj
						.getSale((Long) salesNumberList.getValue());
				
				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();
	
				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());
	
					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());
	
					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);
	
					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());
	
					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}
	
					invObj.setOrder_id(0);
					invObj.setCess_amount(0);
	
	
					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());
	
					itemsList.add(invObj);
	
	
				}
				
				salObj.setCash_pay_id(null);
	
				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));
	
				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				
				salObj.setCustomer(customerLabel.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setInventory_details_list(itemsList);
	
				salObj.setStatus(STS_CONFIRMED);
				
				FinTransaction trans = new FinTransaction();
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;
				
				double amt = 0;
				
				daoObj.updateAsConfirm(salObj);
				
				setRecall(salObj.getId());
				
				saveActivity(getOptionId(), "Sales Updated. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());
	
				loadSale(salObj.getId());
	
				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void saveAsConfirm() {
		
		if (isValid()) {
			
			try {
				
				long customer_id = 1;
				
				HotelSalesModel salObj = new HotelSalesModel();
				
				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();
	
				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());
	
					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());
	
					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);
	
					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());
	
					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}
	
					invObj.setOrder_id(0);
					invObj.setCess_amount(0);
	
	
					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());
	
					itemsList.add(invObj);
	
	
				}
				
				salObj.setCash_pay_id(null);
	
				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble(netTotalTextField.getValue())-toDouble(creditAmtTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));
	
				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				
				salObj.setCustomer(customerLabel.getValue());
				salObj.setInventory_details_list(itemsList);
				salObj.setTableId((Long)tableField.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setSales_number(getNextSequence(
						"Sales Number", getLoginID()));
				
				salObj.setStatus(STS_CONFIRMED);
				
				FinTransaction trans = new FinTransaction();
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;
				
				double amt = 0;
				
				long id=daoObj.saveAsConfirm(salObj);
				
				setRecall(id);
				
				saveActivity(getOptionId(), "Sales Updated. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());
	
				loadSale(salObj.getId());
	
				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public void saveAsBill() {
		
		if (isValid()) {
			
			try {
				
				long customer_id = 1;

				HotelSalesModel salObj = new HotelSalesModel();

				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());

					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());

					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);

					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());

					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}
					
					invObj.setOrder_id(0);
					invObj.setCess_amount(0);
					
					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());

					itemsList.add(invObj);

				}
				
				if(((Long)cardTypeSelect.getValue())!=0 || (bankSelect.getValue())!=null) {
					
					CashPayDetailsModel payModel=new CashPayDetailsModel();
					payModel.setBank_amount(toDouble(bankAmtTFld.getValue()));
					payModel.setBank_type((Long) bankSelect.getValue());
					payModel.setCard_amount(toDouble(cardAmtTFld.getValue()));
					payModel.setCard_type((Long) cardTypeSelect.getValue());
					payModel.setCcn_no(ccNoTFld.getValue());
					payModel.setCheque_date(CommonUtil.getSQLDateFromUtilDate(checkDate.getValue()));
					payModel.setCheque_no(checkNoTFld.getValue());
					
					salObj.setCash_pay_id(payModel);
				}

				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble( grantTotalTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));

				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				salObj.setTableId((Long)tableField.getValue());
				salObj.setCustomer(customerLabel.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setInventory_details_list(itemsList);

				salObj.setSales_number(getNextSequence(
						"Sales Number", getLoginID()));
				
				salObj.setStatus(STS_PAID);
				
				FinTransaction trans = new FinTransaction();
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;

				double amt = 0;
				
				int status=0;
				double payingAmt = salObj.getPayment_amount();

				if (payingAmt == netAmt) {
					trans.addTransaction(SConstants.CR,
							customer_id,
							settings.getCASH_ACCOUNT(),
							roundNumber(payingAmt));
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					
					salObj.setStatus(1);
					status = 1;
				} else if (payingAmt == 0) {
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					salObj.setStatus(2);
					status = 2;
				} else {
					trans.addTransaction(SConstants.CR,
							customer_id,
							settings.getCASH_ACCOUNT(),
							roundNumber(payingAmt));
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					status = 3;
					salObj.setStatus(3);
				}

				if (isTaxEnable()) {
					if (settings.getSALES_TAX_ACCOUNT() != 0) {
						amt = toDouble(taxTextField.getValue());
						if (amt != 0) {
							trans.addTransaction(
									SConstants.CR,
									settings.getSALES_TAX_ACCOUNT(),
									settings.getCGS_ACCOUNT(),
									roundNumber(amt));
							totalAmt -= amt;
						}
					}

				}
				
				
				if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
					amt = toDouble(chargesTextField
							.getValue());
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(amt));
						totalAmt -= amt;
					}
				}

				if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
					amt = toDouble(discountTextField
							.getValue());
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(amt));
						totalAmt -= amt;
					}
				}

				if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_REVENUE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(totalAmt));
					}
				}
				
				long id = daoObj.save(salObj,
						trans.getTransaction(SConstants.SALES,CommonUtil.getSQLDateFromUtilDate(date.getValue())),payingAmt);
				
				setRecall(id);
				
				
				saveActivity(getOptionId(), "New Sales Created. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());
				
				loadSale(0);

				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void updateAsBill() {
		
		try {

			if (isValid()) {

				long customer_id = 1;

				HotelSalesModel salObj = daoObj
						.getSale((Long) salesNumberList.getValue());

				List<HotelSalesInventoryDetailsModel> itemsList = new ArrayList<HotelSalesInventoryDetailsModel>();

				HotelSalesInventoryDetailsModel invObj;
				ItemModel itemObj;
				Item item;
				double std_cost;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					invObj = new HotelSalesInventoryDetailsModel();
					item = table.getItem(it.next());
					
					itemObj=itemDao.getItem((Long) item
							.getItemProperty(TBC_ITEM_ID).getValue());

					invObj.setItem(itemObj);
					invObj.setQunatity((Double) item
							.getItemProperty(TBC_QTY).getValue());
					invObj.setBalance((Double) item
							.getItemProperty(TBC_QTY).getValue());

					invObj.setTax(new TaxModel(1));
					invObj.setTax_amount(0);
					invObj.setTax_percentage(0);

					invObj.setUnit(itemObj.getUnit());
					invObj.setUnit_price((Double) item
							.getItemProperty(TBC_PRICE)
							.getValue());

					if (isDiscountEnable()) {
						invObj.setDiscount_amount(0);
					}

					invObj.setOrder_id(0);
					invObj.setCess_amount(0);


					invObj.setQuantity_in_basic_unit((Double) item
							.getItemProperty(TBC_QTY)
							.getValue());

					itemsList.add(invObj);


				}
				
				if(((Long)cardTypeSelect.getValue())!=0 || (bankSelect.getValue())!=null) {
					
					CashPayDetailsModel payModel;
					
					if(salObj.getCash_pay_id()!=null)
						payModel=salObj.getCash_pay_id();
					else
						payModel=new CashPayDetailsModel();
					
					payModel.setBank_amount(toDouble(bankAmtTFld.getValue()));
					payModel.setBank_type((Long) bankSelect.getValue());
					payModel.setCard_amount(toDouble(cardAmtTFld.getValue()));
					payModel.setCard_type((Long) cardTypeSelect.getValue());
					payModel.setCcn_no(ccNoTFld.getValue());
					payModel.setCheque_date(CommonUtil.getSQLDateFromUtilDate(checkDate.getValue()));
					payModel.setCheque_no(checkNoTFld.getValue());
					
					salObj.setCash_pay_id(payModel);
				}
				else
					salObj.setCash_pay_id(null);
				
				salObj.setShipping_charge(toDouble(chargesTextField
						.getValue()));
				salObj.setExcise_duty(toDouble(taxTextField
						.getValue()));
				
				salObj.setDiscount(toDouble(discountTextField.getValue()));
				salObj.setPayment_amount(toDouble( grantTotalTFld.getValue()));
				salObj.setAmount(toDouble(netTotalTextField.getValue()));

				salObj.setComments("");
				salObj.setDate(CommonUtil.getCurrentSQLDate());
				// salObj.setExpected_delivery_date(CommonUtil.getSQLDateFromUtilDate(expected_delivery_date.getValue()));
				salObj.setLogin(new S_LoginModel(getLoginID()));
				salObj.setOffice(new S_OfficeModel(getOfficeID()));
				
				salObj.setCustomer(customerLabel.getValue());
				salObj.setSales_person((Long)employeeField.getValue());
				salObj.setInventory_details_list(itemsList);
				salObj.setTableId((Long)tableField.getValue());
				salObj.setStatus(STS_PAID);
				
				FinTransaction trans = new FinTransaction();
				double totalAmt = salObj.getAmount();
				double netAmt = totalAmt;

				double amt = 0;
				
				int status=0;
				double payingAmt = salObj.getPayment_amount();

				if (payingAmt == netAmt) {
					trans.addTransaction(SConstants.CR,
							customer_id,
							settings.getCASH_ACCOUNT(),
							roundNumber(payingAmt));
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					
					salObj.setStatus(1);
					status = 1;
				} else if (payingAmt == 0) {
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					salObj.setStatus(2);
					status = 2;
				} else {
					trans.addTransaction(SConstants.CR,
							customer_id,
							settings.getCASH_ACCOUNT(),
							roundNumber(payingAmt));
					trans.addTransaction(SConstants.CR,
							settings.getSALES_ACCOUNT(),
							customer_id,
							roundNumber(netAmt));
					status = 3;
					salObj.setStatus(3);
				}

				if (isTaxEnable()) {
					if (settings.getSALES_TAX_ACCOUNT() != 0) {
						amt = toDouble(taxTextField.getValue());
						if (amt != 0) {
							trans.addTransaction(
									SConstants.CR,
									settings.getSALES_TAX_ACCOUNT(),
									settings.getCGS_ACCOUNT(),
									roundNumber(amt));
							totalAmt -= amt;
						}
					}

				}


				if (settings.getSALES_SHIPPING_CHARGE_ACCOUNT() != 0) {
					amt = toDouble(chargesTextField
							.getValue());
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(amt));
						totalAmt -= amt;
					}
				}

				if (settings.getSALES_DESCOUNT_ACCOUNT() != 0) {
					amt = toDouble(discountTextField
							.getValue());
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_SHIPPING_CHARGE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(amt));
						totalAmt -= amt;
					}
				}

				if (settings.getSALES_REVENUE_ACCOUNT() != 0) {
					if (amt != 0) {
						trans.addTransaction(
								SConstants.CR,
								settings.getSALES_REVENUE_ACCOUNT(),
								settings.getCGS_ACCOUNT(),
								roundNumber(totalAmt));
					}
				}
				boolean transExist=false;
				TransactionModel tran=null;
				if(salObj.getTransaction_id()!=0) {
					tran = daoObj.getTransaction(salObj.getTransaction_id());
					tran.setTransaction_details_list(trans
							.getChildList());
					transExist=true;
				}
				else
					tran=trans.getTransaction(SConstants.SALES,CommonUtil.getSQLDateFromUtilDate(date.getValue()));
				
				tran.setDate(salObj.getDate());
				tran.setLogin_id(getLoginID());
				
				daoObj.update(salObj, tran, payingAmt, transExist);
				
				setRecall(salObj.getId());
				
				saveActivity(getOptionId(), "Sales Updated. Bill No : "+salObj.getSales_number()+
						", Amount : "+salObj.getAmount());

				loadSale(0);

				Notification.show("Success",
						"Saved Successfully..!",
						Type.WARNING_MESSAGE);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadItems(long subgroupId) {
		try {
			itemLay.removeAllComponents();
			List consList=new ArrayList();
			consList.add(SConstants.affect_type.AFFECT_ALL);
			consList.add(SConstants.affect_type.MANUFACTURING);
			List list=daoObj.getAllItemNamesUnderType(getOfficeID(), (Long) categorySelectList.getValue(),consList);
			ItemModel mdl=null;
			Iterator iter=list.iterator();
			SVerticalLayout items;
			SLabel nameLab;
//			SImage itemImage;
			while (iter.hasNext()) {
				mdl= (ItemModel) iter.next();
				items=new SVerticalLayout();
				items.setStyleName("hotel_item_style");
//				if(mdl.getIcon()!=null&&mdl.getIcon().trim().length()>0){
//					itemImage=new SImage(null,new ThemeResource("ItemImages/"+mdl.getIcon().substring(0,mdl.getIcon().indexOf(",", 0))));
//					System.out.println("ItemImages/"+mdl.getIcon().substring(0,mdl.getIcon().indexOf(",", 0)));
//				}else{
//					itemImage=new SImage(null,new ThemeResource("Images/no_image.png"));
//				}
				
//				itemImage.setWidth("50px");
//				itemImage.setHeight("50px");
//				items.addComponent(itemImage);
				nameLab=new SLabel(null,mdl.getName());
//				nameLab.setWidth("10px");
				nameLab.setStyleName("hotel_item_name_style");
				items.addComponent(nameLab);
				items.setId(mdl.getId()+"");
				items.addLayoutClickListener(itemLayoutrListener);
			
				itemLay.addComponent(items);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void setFields(CustomerBookingModel bookingMdl){
		customerLabel.setValue(bookingMdl.getCustomer_name());
		tableField.setValue(bookingMdl.getTableNo().getId());
		employeeField.setValue(bookingMdl.getEmployee());
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return salesNumberList;
	}
	
}
