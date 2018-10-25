package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.google.gwt.dom.client.SpanElement;
import com.inventory.config.acct.ui.BankAccountDepositUI;
import com.inventory.config.acct.ui.BankAccountPaymentUI;
import com.inventory.config.acct.ui.CashAccountDepositUI;
import com.inventory.config.acct.ui.CashAccountPaymentUI;
import com.inventory.journal.ui.JournalUI;
import com.inventory.purchase.ui.PurchaseGRNUI;
import com.inventory.purchase.ui.PurchaseInquiryUI;
import com.inventory.purchase.ui.PurchaseQuotationUI;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.purchase.ui.PurchaseUI;
import com.inventory.reports.dao.BillViewDao;
import com.inventory.sales.ui.DeliveryNoteUI;
import com.inventory.sales.ui.QuotationUI;
import com.inventory.sales.ui.SalesInquiryUI;
import com.inventory.sales.ui.SalesOrderUI;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboBox;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.ui.MainLayout;

public class BillViewUI extends SparkLogic {

	SPanel panel;
	SDateField dateFeild;
	SComboBox billnoCombo;
	SRadioButton billButtons;
	SButton view;
	SDateField dateFieldFrom;
	SDateField dateFieldTo;
	SFormLayout MainLayout;
	SHorizontalLayout dateLayout;
	BillViewDao daoBill;
    CollectionContainer bic;
    SparkLogic window;
	@Override
	public SPanel getGUI() {

		setSize(600, 600);
		daoBill=new BillViewDao();
		

		billButtons = new SRadioButton("", 300, SConstants.BillViewDetails.billViewDetails, "intKey", "value");
		/* billButtons.setHorizontal(true); */

		dateFieldFrom = new SDateField("From", 120, getDateFormat(), getFinStartDate());

		dateFieldTo = new SDateField("To", 100, getDateFormat(), getWorkingDate());

		billnoCombo = new SComboField("Bill No", 300);
		billnoCombo.setInputPrompt("Select");

		view = new SButton(getPropertyName("View"));

		panel = new SPanel();
		panel.setSizeFull();

		dateLayout = new SHorizontalLayout();
		dateLayout.setSpacing(true);
		dateLayout.addComponent(dateFieldFrom);
		dateLayout.addComponent(dateFieldTo);

		MainLayout = new SFormLayout();
		MainLayout.setMargin(true);
		MainLayout.setSpacing(true);
		MainLayout.addComponent(billButtons);
		MainLayout.addComponent(dateLayout);
		MainLayout.addComponent(billnoCombo);
		MainLayout.addComponent(view);

		panel.setContent(MainLayout);

		billButtons.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
			loadBillDetails();	
			}
		});
		
		view.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(window!=null&&billnoCombo.getValue()!=null){
					window.getBillNoFiled().setValue(billnoCombo.getValue());
					getUI().getCurrent().addWindow(window);
					window.setCaption(billButtons.getItemCaption(billButtons.getValue()));
					window.center();
				}
			}
		});
		
		
		dateFieldFrom.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				// TODO Auto-generated method stub
				loadBillDetails();
			}
		});

		
		dateFieldTo.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				// TODO Auto-generated method stub
				loadBillDetails();
			}
		});
		return panel;
		
		
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void loadBillDetails(){
		try {
			List billList = daoBill.getBillNo((Integer)billButtons.getValue(),
					CommonUtil.getSQLDateFromUtilDate(dateFieldFrom.getValue()),
					CommonUtil.getSQLDateFromUtilDate(dateFieldTo.getValue()), getOfficeID());
			bic=CollectionContainer.fromBeans(billList,"id");
			billnoCombo.setContainerDataSource(bic);
			switch ((Integer)billButtons.getValue()) {
			case SConstants.BillViewDetails.PURCHASE_ENQUIRY:
				billnoCombo.setItemCaptionPropertyId("inquiry_no");
				window=new PurchaseInquiryUI();
				break;
			case SConstants.BillViewDetails.PURCHASE_QUOTATION:
				billnoCombo.setItemCaptionPropertyId("quotation_no");
				window=new PurchaseQuotationUI();
				break;
			case SConstants.BillViewDetails.GRN:
				billnoCombo.setItemCaptionPropertyId("grn_no");
				window=new PurchaseGRNUI();
				break;
			case SConstants.BillViewDetails.PURCHASE:
				billnoCombo.setItemCaptionPropertyId("purchase_no");
				window=new PurchaseUI();
				break;
		
			case SConstants.BillViewDetails.PURCHASE_RETURN:
				billnoCombo.setItemCaptionPropertyId("return_no");
				window=new PurchaseReturnUI();
				break;
			case SConstants.BillViewDetails.SALES_ENQIRY:
				billnoCombo.setItemCaptionPropertyId("inquiry_no");
				window=new SalesInquiryUI();
				break;
			case SConstants.BillViewDetails.SALES_QUOTATION:
				billnoCombo.setItemCaptionPropertyId("quotation_no");
			     window=new QuotationUI();
				break;
			case SConstants.BillViewDetails.SALES_ORDER:
				billnoCombo.setItemCaptionPropertyId("order_no");
				window=new SalesOrderUI();
				break;
			case SConstants.BillViewDetails.DELIVERY_NOTE:
				billnoCombo.setItemCaptionPropertyId("deliveryNo");
				window=new DeliveryNoteUI();
				break;
				
			case SConstants.BillViewDetails.CASH_DEPOSIT:
				billnoCombo.setItemCaptionPropertyId("bill_no");
				window=new CashAccountDepositUI();
				break;
				
			case SConstants.BillViewDetails.CASH_PAYMENT:
				billnoCombo.setItemCaptionPropertyId("bill_no");
				window=new CashAccountPaymentUI();
				break;
				
			case SConstants.BillViewDetails.BANK_DEPOSIT:
				billnoCombo.setItemCaptionPropertyId("bill_no");
				window=new BankAccountDepositUI();
				break;
				
				
			case SConstants.BillViewDetails.BANK_PAYMENT:
				billnoCombo.setItemCaptionPropertyId("bill_no");
				window=new BankAccountPaymentUI();
				break;
				
			case SConstants.BillViewDetails.JOURNEL:
				billnoCombo.setItemCaptionPropertyId("bill_no");
				window=new JournalUI();
				break;
				

			default:
				break;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
