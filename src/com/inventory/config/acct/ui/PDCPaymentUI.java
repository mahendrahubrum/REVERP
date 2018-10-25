package com.inventory.config.acct.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.dao.PDCDao;
import com.inventory.config.acct.dao.PDCPaymentDao;
import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.PdcDetailsModel;
import com.inventory.config.acct.model.PdcModel;
import com.inventory.config.acct.model.PdcPaymentDetailsModel;
import com.inventory.config.acct.model.PdcPaymentModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.sales.bean.PaymentInvoiceBean;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.PaymentInvoicePanel;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextArea;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.dao.PaymentInvoiceDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.DepartmentDao;
import com.webspark.uac.dao.DivisionDao;

/**
 * 
 * @author anil
 * @date 12-Aug-2015
 * @Project REVERP
 * 
 */

/**
 * @author sangeeth
 * @date 15-Oct-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class PDCPaymentUI extends SparkLogic {

	SPanel panel = null;
	
	static String TBC_SELECT = "#";
	static String TBC_ID = "Id";
	static String TBC_TID = "TID";
	static String TBC_PDC_ID = "PDC Id";
	static String TBC_PDC_NO = "PDC No";
	static String TBC_PDC_CHILD_ID = "PDC Child Id";
	static String TBC_BILL_NO = "Bill No";
	static String TBC_TYPE = "Type";
	static String TBC_CHEQUE_NO= "Cheque No";
	static String TBC_ISSUE_DATE_ID = "Issue Date Id";
	static String TBC_ISSUE_DATE = "Issue Date";
	static String TBC_CHEQUE_DATE_ID = "Cheque Date Id";
	static String TBC_CHEQUE_DATE = "Cheque Date";
	static String TBC_DEPARTMENT_ID = "Department ID";
	static String TBC_DEPARTMENT = "Department";
	static String TBC_DIVISION_ID = "Division ID";
	static String TBC_DIVISION = "Division";
	static String TBC_AMOUNT = "Amount";
	static String TBC_CURRENCY_ID = "Currency ID";
	static String TBC_CURRENCY = "Currency";
	static String TBC_CONVERSION_RATE = "Conversion Rate";
	static String TBC_STATUS_ID = "Status Id";
	static String TBC_STATUS = "Status";
	static String TBC_FROM_LEDGER_ID = "From Ledger";
	static String TBC_TO_LEDGER_ID = "To Ledger";
	

	STable table;
	PaymentInvoicePanel invoicePanel;
	PDCPaymentDao dao;
	PaymentInvoiceDao paymentDao;

	SGridLayout masterDetailsGrid;
	SVerticalLayout stkrkVLay;

	SComboField paymentCombo;
	SDateField dateField;
	SDateField paymentDateField;
//	SDateField toDateField;
	
	SButton saveButton;
	SButton updateButton;
	SButton deleteButton;
	SButton cancelButton;

	STextArea memoTextArea;

	LedgerDao ledgerDao = new LedgerDao();

	SButton createNewButton;

	SettingsValuePojo settings;
	WrappedSession session;

	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	
	
	@Override
	public SPanel getGUI() {
		paymentDao=new PaymentInvoiceDao();
		invoicePanel=new PaymentInvoicePanel(null);
		
		paymentDateField=new SDateField(null, 100, getDateFormat());
//		toDateField=new SDateField(null, 100, getDateFormat());

		paymentDateField.setImmediate(true);
//		toDateField.setImmediate(true);
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());

		session = getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		allHeaders=new Object[]{ TBC_SELECT, TBC_ID, TBC_TID, TBC_PDC_ID, TBC_PDC_NO, TBC_PDC_CHILD_ID, TBC_TYPE, TBC_BILL_NO, TBC_CHEQUE_NO, TBC_ISSUE_DATE_ID, 
								TBC_ISSUE_DATE, TBC_CHEQUE_DATE_ID, TBC_CHEQUE_DATE, TBC_DEPARTMENT_ID, TBC_DEPARTMENT, TBC_DIVISION_ID, 
								TBC_DIVISION, TBC_AMOUNT, TBC_CURRENCY_ID, TBC_CURRENCY, TBC_CONVERSION_RATE, TBC_STATUS_ID, TBC_STATUS, 
								TBC_FROM_LEDGER_ID, TBC_TO_LEDGER_ID };
		
		requiredHeaders=new Object[]{TBC_SELECT, TBC_PDC_NO, TBC_CHEQUE_NO, TBC_ISSUE_DATE, TBC_CHEQUE_DATE, TBC_DEPARTMENT, TBC_DIVISION, 
										TBC_AMOUNT, TBC_CURRENCY, TBC_STATUS };
		
		List<Object> templist = new ArrayList<Object>();
		Collections.addAll(templist, requiredHeaders);
		
		if(!settings.isDEPARTMENT_ENABLED()){
			templist.remove(TBC_DEPARTMENT);
		}
		if(!settings.isDIVISION_ENABLED()){
			templist.remove(TBC_DIVISION);
		}
					
		requiredHeaders = templist.toArray(new String[templist.size()]);
		
//		docAttach = new DocumentAttach(SConstants.documentAttach.CHEQUE);
		
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));

		dao = new PDCPaymentDao();
		ledgerDao = new LedgerDao();

		saveButton = new SButton(getPropertyName("pay"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("Update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("Delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");

		cancelButton = new SButton(getPropertyName("Cancel"), 78);
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");

		SHorizontalLayout mainButtonLayout = new SHorizontalLayout();
		mainButtonLayout.addComponent(saveButton);
//		mainButtonLayout.addComponent(updateButton);

//		if (settings.isKEEP_DELETED_DATA())
		mainButtonLayout.addComponent(cancelButton);
//		else
		mainButtonLayout.addComponent(deleteButton);

		updateButton.setVisible(false);
//		deleteButton.setVisible(false);
//		cancelButton.setVisible(false);

		dateField = new SDateField(null, 100, getDateFormat(), getWorkingDate());
		memoTextArea = new STextArea(getPropertyName("comments"), 810, 40);

		panel = new SPanel();
		panel.setSizeFull();

		setSize(1000, 525);
		try {
			table = new STable(null, 800, 200);

			paymentCombo = new SComboField(null, 200, null, "id","comments", true, getPropertyName("create_new"));

			stkrkVLay = new SVerticalLayout();
			
			masterDetailsGrid = new SGridLayout();
			masterDetailsGrid.setSizeFull();
			masterDetailsGrid.setColumns(9);
			masterDetailsGrid.setRows(2);

			table.setSizeFull();
			table.setSelectable(true);
			table.setMultiSelect(false);
			table.setWidth("900px");
			table.setHeight("250px");
			
			table.addContainerProperty(TBC_SELECT,SCheckBox.class, null, TBC_SELECT, null, Align.CENTER);
			table.addContainerProperty(TBC_ID,Long.class, null, TBC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TID,Long.class, null, TBC_TID, null, Align.CENTER);
			table.addContainerProperty(TBC_PDC_ID,Long.class, null, TBC_PDC_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_PDC_NO,String.class, null, TBC_PDC_NO, null, Align.CENTER);
			table.addContainerProperty(TBC_PDC_CHILD_ID,Long.class, null, TBC_PDC_CHILD_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_TYPE,Integer.class, null, TBC_TYPE, null, Align.CENTER);
			table.addContainerProperty(TBC_BILL_NO,String.class, null, TBC_BILL_NO, null, Align.CENTER);
			table.addContainerProperty(TBC_CHEQUE_NO,String.class, null, getPropertyName("cheque_no"), null,Align.LEFT);
			table.addContainerProperty(TBC_ISSUE_DATE_ID,Date.class, null, TBC_ISSUE_DATE_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_ISSUE_DATE,String.class, null, getPropertyName("issue_date"), null,Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE_ID,Date.class, null, TBC_CHEQUE_DATE_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_CHEQUE_DATE,String.class, null, getPropertyName("cheque_date"), null,Align.LEFT);
			table.addContainerProperty(TBC_DEPARTMENT_ID,Long.class, null, TBC_DEPARTMENT_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DEPARTMENT,String.class, null, getPropertyName("department"), null,Align.LEFT);
			table.addContainerProperty(TBC_DIVISION_ID,Long.class, null, TBC_DIVISION_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_DIVISION,String.class, null, getPropertyName("division"), null,Align.LEFT);
			table.addContainerProperty(TBC_AMOUNT,Double.class, null, getPropertyName("amount"), null,Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY_ID,Long.class, null, TBC_CURRENCY_ID, null, Align.CENTER);
			table.addContainerProperty(TBC_CURRENCY,String.class, null, getPropertyName("currency"), null,Align.LEFT);
			table.addContainerProperty(TBC_CONVERSION_RATE,Double.class, null, getPropertyName("conversion_rate"), null,Align.CENTER);
			table.addContainerProperty(TBC_STATUS_ID,Integer.class, null, TBC_STATUS_ID, null,Align.CENTER);
			table.addContainerProperty(TBC_STATUS,String.class, null, getPropertyName("status"), null,Align.LEFT);
			table.addContainerProperty(TBC_FROM_LEDGER_ID,Long.class, null, TBC_FROM_LEDGER_ID, null,Align.LEFT);
			table.addContainerProperty(TBC_TO_LEDGER_ID,Long.class, null, TBC_TO_LEDGER_ID, null,Align.LEFT);
			
			table.setColumnExpandRatio(TBC_PDC_NO,1f);
			table.setColumnExpandRatio(TBC_CHEQUE_NO,1f);
			table.setColumnExpandRatio(TBC_CHEQUE_DATE,1f);
			table.setColumnExpandRatio(TBC_ISSUE_DATE,1f);
			table.setColumnExpandRatio(TBC_AMOUNT, 1f);
			table.setColumnExpandRatio(TBC_CURRENCY, 1f);
			table.setColumnExpandRatio(TBC_STATUS, 1.5f);
			
			table.setVisibleColumns(requiredHeaders);

			masterDetailsGrid.addComponent(new SLabel(getPropertyName("payment_id"), 30), 1, 0);
			SHorizontalLayout salLisrLay = new SHorizontalLayout();
			salLisrLay.addComponent(paymentCombo);
			salLisrLay.addComponent(createNewButton);
			masterDetailsGrid.addComponent(salLisrLay, 2, 0);
			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("payment_date")), 3, 1);
			masterDetailsGrid.addComponent(paymentDateField, 4, 1);
			
//			masterDetailsGrid.addComponent(new SLabel(getPropertyName("to_date")), 5, 1);
//			masterDetailsGrid.addComponent(toDateField, 6, 1);
			
			masterDetailsGrid.addComponent(new SLabel(getPropertyName("date")), 1, 1);
			masterDetailsGrid.addComponent(dateField, 2, 1);

			masterDetailsGrid.setColumnExpandRatio(1, 1f);
			masterDetailsGrid.setColumnExpandRatio(2, 1f);
			masterDetailsGrid.setColumnExpandRatio(3, 1f);
			masterDetailsGrid.setColumnExpandRatio(4, 1f);
			masterDetailsGrid.setColumnExpandRatio(5, 1f);
			masterDetailsGrid.setColumnExpandRatio(6, 1f);


			stkrkVLay.addComponent(masterDetailsGrid);

			stkrkVLay.setMargin(true);
			stkrkVLay.setSpacing(true);

			stkrkVLay.addComponent(table);

			SFormLayout fm = new SFormLayout();
			fm.addComponent(memoTextArea);
			stkrkVLay.addComponent(fm);

			stkrkVLay.addComponent(mainButtonLayout);
			mainButtonLayout.setSpacing(true);
			stkrkVLay.setComponentAlignment(mainButtonLayout,Alignment.BOTTOM_CENTER);

			table.setFooterVisible(true);
			table.setColumnFooter(TBC_AMOUNT, asString(roundNumber(0)));
			table.setColumnFooter(TBC_CHEQUE_NO, getPropertyName("total"));

			SVerticalLayout hLayout=new SVerticalLayout();
			hLayout.addComponent(popupLay);
			hLayout.addComponent(stkrkVLay);

			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			panel.setContent(windowNotif);

			loadData(0);
			
			
			/*addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadData(0);
				}
			});

			addShortcutListener(new ShortcutListener("Add New Purchase",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					deleteItem();
				}
			});*/

			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)paymentCombo.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					confirmBox.close();
				}
			};
			
			
			confirmBox.setClickListener(confirmListener);
			
			
			ClickListener clickListnr=new ClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
						if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)paymentCombo.getValue(),
									"Bank Account Deposit : No. "+paymentCombo.getItemCaption(paymentCombo.getValue()));
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						}
						else
							Notification.show("Select an Invoice..!",
									"Select an Invoice for save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
						if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0")) {
							confirmBox.open();
						}
						else
							Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
									Type.HUMANIZED_MESSAGE);
					}
					else {
						try {
							helpPopup=new SHelpPopupView(getOptionId());
							popupLay.removeAllComponents();
							popupLay.addComponent(helpPopup);
							helpPopup.setPopupVisible(true);
							helpPopup.setHideOnMouseOut(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			};
			
			
			windowNotif.setClickListener(clickListnr);
			
			
			createNewButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					paymentCombo.setValue((long) 0);
				}
			});
			
			
			paymentDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isDatesValid(paymentDateField)){
							boolean isCreateNew=true;
							if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0"))
								isCreateNew=false;
							loadTable(isCreateNew);
						}
						else
							table.removeAllItems();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			paymentDateField.setValue(getWorkingDate());
			
			
			/*toDateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(isDatesValid(paymentDateField) && isDatesValid(toDateField)){
							boolean isCreateNew=true;
							if(paymentCombo.getValue()!=null && !paymentCombo.getValue().toString().equals("0"))
								isCreateNew=false;
							loadTable(isCreateNew);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			toDateField.setValue(getWorkingDate());*/

	
			table.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {

					if (table.getValue() != null) {
						
					}
				}
			});
			
			
			saveButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							PdcPaymentModel mdl=null;
								if (paymentCombo.getValue() == null || paymentCombo.getValue().toString().equals("0")) {
									mdl = new PdcPaymentModel();
								}
								else
									mdl = dao.getPdcPaymentModel((Long)paymentCombo.getValue());
								
								List<PdcPaymentDetailsModel> childList=new ArrayList<PdcPaymentDetailsModel>();
								List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
								
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setPaymentDate(CommonUtil.getSQLDateFromUtilDate(paymentDateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setBill_no(getNextSequence("PDC Payment No", getLoginID())+"");
								mdl.setBill_no(getNextSequence("PDC Payment No", getLoginID(), getOfficeID(), CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
								mdl.setMemo(memoTextArea.getValue());
								mdl.setActive(true);
								
								FinTransaction tran = new FinTransaction();
								Iterator itr = table.getItemIds().iterator();							

								while (itr.hasNext()) {
									boolean isBaseCurrency=true;
									boolean isCheque=true;
									SCheckBox check;
									Item item = table.getItem(itr.next());
									check=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
									if((Integer) item.getItemProperty(TBC_STATUS_ID).getValue()!=SConstants.PDCStatus.APPROVED){
										if(!check.getValue()){
											continue;
										}
									}
									PdcPaymentDetailsModel det=new PdcPaymentDetailsModel();
									det.setType((Integer) item.getItemProperty(TBC_TYPE).getValue());
									det.setPdc_id((Long) item.getItemProperty(TBC_PDC_ID).getValue());
									det.setPdc_child_id((Long) item.getItemProperty(TBC_PDC_CHILD_ID).getValue());
									det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
									det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
									det.setCurrencyId(new CurrencyModel((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()));
									det.setConversionRate((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
									det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
									det.setChequeDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()));
									det.setIssueDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue()));
									det.setStatus(SConstants.PDCStatus.APPROVED);
									det.setFrom_id((Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue());
									det.setTo_id((Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue());
									if((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()!=getCurrencyID())
										isBaseCurrency=false;
									childList.add(det);
									
									if((Integer) item.getItemProperty(TBC_TYPE).getValue()==SConstants.SALES){
										if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
											double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
											double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
											List billList=new ArrayList();
											billList=Arrays.asList(det.getBill_no().split(","));
											Iterator it=billList.iterator();
											
											while (it.hasNext()) {
												
												long pid = Long.parseLong(it.next().toString().trim());
												double actualPaidAmount=0;
												double actualAmount=0;
												double actual_balance_to_pay=0;
												double actualPayingAmount=0;
												
												double totalPayed=0;
												double totalAmount=0;
												double payingAmount=0;
												double balance_to_pay=0;
												
												if(paymentAmount<=0)
													break;
												if(pid!=0) {
													SalesModel pmdl=new SalesDao().getSale(pid);
													List paymentList=new ArrayList();
													
													paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.SALES, pid);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalPayed+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualPaidAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.CUSTOMER);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalAmount+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.CUSTOMER);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalPayed+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualPaidAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													if(isBaseCurrency){
														totalAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
														totalPayed+=roundNumber(pmdl.getPayment_amount());
													}
													else{
														totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
														actualAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
														totalPayed+=roundNumber(pmdl.getPayment_amount()/pmdl.getConversionRate());
														actualPaidAmount+=roundNumber(pmdl.getPayment_amount());
													}
													
													balance_to_pay=totalAmount-totalPayed;
													actual_balance_to_pay=actualAmount-actualPaidAmount;
													
													PaymentInvoiceBean bean;
													if(isBaseCurrency) {
														if(paymentAmount>=balance_to_pay){
															payingAmount=balance_to_pay;
															paymentAmount-=balance_to_pay;
														}
														else{
															payingAmount=paymentAmount;
															paymentAmount=0;
														}
														bean=new PaymentInvoiceBean(
																			SConstants.SALES,
																			pid,
																			mdl.getId(),
																			getOfficeID(),
																			(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																			roundNumber(payingAmount),
																			roundNumber(paymentConversionRate),
																			isCheque);
														invoiceMapList.add(bean);
														tran.addTransaction(SConstants.CR, 
																			(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
																			(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
																			roundNumber(payingAmount),
																			"",
																			(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																			(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																			,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																			(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
														
													}
													else{
														boolean isSavable=false;
														if(paymentAmount>=actual_balance_to_pay){
															isSavable=true;
															actualPayingAmount=actual_balance_to_pay;
															paymentAmount-=actual_balance_to_pay;
														}
														else{
															actualPayingAmount=paymentAmount;
															paymentAmount=0;
														}
														
														double actualBaseCurrency=0;
														actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
														double differenceAmount=0;
														differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
														
														bean=new PaymentInvoiceBean(
																			SConstants.SALES,
																			pid,
																			mdl.getId(),
																			getOfficeID(),
																			(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																			roundNumber(actualPayingAmount),
																			roundNumber(paymentConversionRate),
																			isCheque);
														if(isSavable){
															if(differenceAmount>0){
																tran.addTransaction(SConstants.CR,
																					settings.getFOREX_DIFFERENCE_ACCOUNT(),
																					(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																					roundNumber(differenceAmount),
																					"",
																					(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																					(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																					,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																					(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
																actualBaseCurrency-=differenceAmount;
															}
														}
														
														tran.addTransaction(SConstants.CR, 
																	(Long)item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
																	(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
																	roundNumber(actualBaseCurrency),
																	"",
																	(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																	(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																	,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																	(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
														invoiceMapList.add(bean);
													}
												}
											}
											if(paymentAmount>0){
												tran.addTransaction(SConstants.CR, 
														(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
														(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
														roundNumber(paymentAmount/paymentConversionRate),
														"",
														(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
														(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
														,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
														(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											}
										}
										else{
											tran.addTransaction(SConstants.CR, 
													(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
													(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
													roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue() /
																(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
													"",
													(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
													(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
													,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
													(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										}
									}
									else if((Integer) item.getItemProperty(TBC_TYPE).getValue()==SConstants.PURCHASE){
										if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
											double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
											double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
											List billList=new ArrayList();
											billList=Arrays.asList(det.getBill_no().split(","));
											Iterator it=billList.iterator();
											while (it.hasNext()) {
												
												long pid = Long.parseLong(it.next().toString().trim());
												
												double actualPaidAmount=0;
												double actualAmount=0;
												double actual_balance_to_pay=0;
												double actualPayingAmount=0;
												
												double totalPayed=0;
												double totalAmount=0;
												double payingAmount=0;
												double balance_to_pay=0;
												
												if(paymentAmount<=0)
													break;
												if(pid!=0) {
													PurchaseModel pmdl=new PurchaseDao().getPurchaseModel(pid);
													List paymentList=new ArrayList();
													
													paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.PURCHASE, pid);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalPayed+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualPaidAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.SUPPLIER);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalAmount+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.SUPPLIER);
													if(paymentList.size()>0){
														Iterator payItr=paymentList.iterator();
														while (payItr.hasNext()) {
															DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
															if(mapMdl.getPaymentId()!=mdl.getId()){
																if(isBaseCurrency){
																	totalPayed+=roundNumber(mapMdl.getAmount());
																}
																else{
																	totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																	actualPaidAmount+=mapMdl.getAmount();
																}
															}
														}
													}
													
													if(isBaseCurrency){
														totalAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
														totalPayed+=roundNumber(pmdl.getPaymentAmount());
													}
													else{
														totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate())-(pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
														actualAmount=roundNumber(pmdl.getAmount()-pmdl.getExpenseCreditAmount());
														totalPayed+=roundNumber(pmdl.getPaymentAmount()/pmdl.getConversionRate());
														actualPaidAmount+=roundNumber(pmdl.getPaymentAmount());
													}
													
													balance_to_pay=totalAmount-totalPayed;
													actual_balance_to_pay=actualAmount-actualPaidAmount;
													
													PaymentInvoiceBean bean;
													if(isBaseCurrency) {
														if(paymentAmount>=balance_to_pay){
															payingAmount=balance_to_pay;
															paymentAmount-=balance_to_pay;
														}
														else{
															payingAmount=paymentAmount;
															paymentAmount=0;
														}
														bean=new PaymentInvoiceBean(SConstants.PURCHASE,
																					pid,
																					mdl.getId(),
																					getOfficeID(),
																					(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																					roundNumber(payingAmount),
																					roundNumber(paymentConversionRate),
																					isCheque);
														invoiceMapList.add(bean);
														tran.addTransaction(SConstants.CR, 
																			(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																			(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																			roundNumber(payingAmount),
																			"",
																			(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																			(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																			,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																			(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
														
													}
													else{
														boolean isSavable=false;
														if(paymentAmount>=actual_balance_to_pay){
															isSavable=true;
															actualPayingAmount=actual_balance_to_pay;
															paymentAmount-=actual_balance_to_pay;
														}
														else{
															actualPayingAmount=paymentAmount;
															paymentAmount=0;
														}
														double actualBaseCurrency=0;
														actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
														double differenceAmount=0;
														differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
														
														bean=new PaymentInvoiceBean(SConstants.PURCHASE,
																					pid,
																					mdl.getId(),
																					getOfficeID(),
																					(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																					roundNumber(actualPayingAmount),
																					roundNumber(paymentConversionRate),
																					isCheque);
														if(isSavable){
															if(differenceAmount>0){
																tran.addTransaction(SConstants.CR, 
																					(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																					settings.getFOREX_DIFFERENCE_ACCOUNT(),  
																					roundNumber(differenceAmount),
																					"",
																					(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																					(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																					,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																					(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
																actualBaseCurrency-=differenceAmount;
															}
														}
														
														tran.addTransaction(SConstants.CR, 
																	(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																	(Long)item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																	roundNumber(actualBaseCurrency),
																	"",
																	(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																	(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																	,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																	(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
														invoiceMapList.add(bean);
													}
												}
											}
											if(paymentAmount>0){
												tran.addTransaction(SConstants.CR, 
														(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
														(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
														roundNumber(paymentAmount/paymentConversionRate),
														"",
														(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
														(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
														,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
														(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											}
										}
										else{
											tran.addTransaction(SConstants.CR, 
													(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
													(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
													roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()/
																(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
													"",
													(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
													(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
													,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
													(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										}
									}
								}
								mdl.setPdc_payment_list(childList);
								TransactionModel transaction=null;
								
								if(mdl.getTransactionId()!=0)
									transaction=dao.getTransactionModel(mdl.getTransactionId());
								
								if(transaction!=null){
									if(tran.getChildList().size()>0)
										transaction.setTransaction_details_list(tran.getChildList());
									else
										transaction.setTransaction_details_list(null);
									transaction.setDate(mdl.getDate());
									transaction.setLogin_id(getLoginID());
								}
								else
									transaction=tran.getTransaction(SConstants.PDC_PAYMENT,CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));

								long id=dao.update(	mdl, transaction , invoiceMapList, true);
								
//								saveActivity(getOptionId(), "PDC Payment Saved. Bill No : "
//												+ id+ ",  : , Payment Amount : "
//												+ table.getColumnFooter(TBC_AMOUNT).toString(),id);
								Notification.show("Updated Successfully..!",Type.WARNING_MESSAGE);
								loadData(id);
//							}
						}
					} catch (Exception e) {
						Notification.show("Error",Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});

			
			paymentCombo.addValueChangeListener(new Property.ValueChangeListener() {

				@SuppressWarnings("rawtypes")
				public void valueChange(ValueChangeEvent event) {

					try {
						table.setVisibleColumns(allHeaders);
						table.removeAllItems();
						dateField.setValue(getWorkingDate());
						paymentDateField.setValue(null);
						paymentDateField.setValue(getWorkingDate());
						memoTextArea.setValue("");
						saveButton.setVisible(true);
						cancelButton.setVisible(true);
						deleteButton.setVisible(true);
//						cancelButton.setVisible(false);
						updateButton.setVisible(false);
//						deleteButton.setVisible(false);
						if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {

							PdcPaymentModel objModel = dao.getPdcPaymentModel((Long) paymentCombo.getValue());
							paymentDateField.setValue(objModel.getPaymentDate());
							table.removeAllItems();
							dateField.setValue(objModel.getDate());
							memoTextArea.setValue(objModel.getMemo());
							table.setVisibleColumns(allHeaders);
							Iterator it = objModel.getPdc_payment_list().iterator();
							while (it.hasNext()) {
								PdcPaymentDetailsModel det = (PdcPaymentDetailsModel) it.next();

								String department="None";
								String division="None";
								
								if(det.getDepartmentId()!=0)
									department=new DepartmentDao().getDepartmentName(det.getDepartmentId());
								if(det.getDivisionId()!=0)
									division=new DivisionDao().getDivisionName(det.getDivisionId());
								
								table.addItem(new Object[] {new SCheckBox(null, false),
															det.getId(),
															objModel.getTransactionId(),
															det.getPdc_id(),
															new PDCDao().getPdcModel(det.getPdc_id()).getBill_no(),
															det.getPdc_child_id(),
															det.getType(),
															det.getBill_no(),
															det.getChequeNo(),
															det.getIssueDate(),
															CommonUtil.formatDateToDDMMYYYY(det.getIssueDate()),
															det.getChequeDate(),
															CommonUtil.formatDateToDDMMYYYY(det.getChequeDate()),
															det.getDepartmentId(),
															department,
															det.getDivisionId(),
															division,
															roundNumber(det.getAmount()),
															det.getCurrencyId().getId(),
															det.getCurrencyId().getCode(),
															roundNumber(det.getConversionRate()),
															det.getStatus(),
															getStatus(det.getStatus()),
															det.getFrom_id(),
															det.getTo_id()}, table.getItemIds().size()+1);
								
							}
							calculateTotals();
							saveButton.setVisible(true);
							cancelButton.setVisible(true);
							deleteButton.setVisible(true);
						}
						if (!isFinYearBackEntry()) {
							saveButton.setVisible(false);
							updateButton.setVisible(false);
							deleteButton.setVisible(false);
							
							if (paymentCombo.getValue() == null || paymentCombo.getValue().toString().equals("0")) {
								Notification.show(getPropertyName("warning_financial_year"),Type.WARNING_MESSAGE);
							}
						}
						table.setVisibleColumns(requiredHeaders);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			
			
			cancelButton.addClickListener(new Button.ClickListener() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {
							if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {
								PdcPaymentModel mdl = dao.getPdcPaymentModel((Long)paymentCombo.getValue());
								List<PdcPaymentDetailsModel> childList=new ArrayList<PdcPaymentDetailsModel>();
								List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setPaymentDate(CommonUtil.getSQLDateFromUtilDate(paymentDateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								mdl.setActive(true);
								
								FinTransaction tran = new FinTransaction();
								Iterator itr = table.getItemIds().iterator();							

								while (itr.hasNext()) {
									boolean isBaseCurrency=true;
									boolean isCheque=true;
									
									Item item = table.getItem(itr.next());
									SCheckBox check;
									check=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
									if((Long) item.getItemProperty(TBC_TID).getValue()!=0){
										PdcPaymentDetailsModel det=null;
										if((Long) item.getItemProperty(TBC_ID).getValue()!=0)
											det=dao.getPdcPaymentDetailsModel((Long) item.getItemProperty(TBC_ID).getValue());
										if(det==null)
											det=new PdcPaymentDetailsModel();
										det.setType((Integer) item.getItemProperty(TBC_TYPE).getValue());
										det.setPdc_id((Long) item.getItemProperty(TBC_PDC_ID).getValue());
										det.setPdc_child_id((Long) item.getItemProperty(TBC_PDC_CHILD_ID).getValue());
										det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
										det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
										det.setCurrencyId(new CurrencyModel((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()));
										det.setConversionRate((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
										det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
										det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
										det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
										det.setChequeDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()));
										det.setIssueDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_ISSUE_DATE_ID).getValue()));
										det.setStatus(SConstants.PDCStatus.CANCELLED);
										det.setFrom_id((Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue());
										det.setTo_id((Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue());
										if((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()!=getCurrencyID())
											isBaseCurrency=false;
										childList.add(det);
										
										if(check.getValue()){
											continue;
										}
										
										if((Integer) item.getItemProperty(TBC_TYPE).getValue()==SConstants.SALES){
											if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
												double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
												double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
												List billList=new ArrayList();
												billList=Arrays.asList(det.getBill_no().split(","));
												Iterator it=billList.iterator();
												
												while (it.hasNext()) {
													
													long pid = Long.parseLong(it.next().toString().trim());
													double actualPaidAmount=0;
													double actualAmount=0;
													double actual_balance_to_pay=0;
													double actualPayingAmount=0;
													
													double totalPayed=0;
													double totalAmount=0;
													double payingAmount=0;
													double balance_to_pay=0;
													
													if(paymentAmount<=0)
														break;
													if(pid!=0) {
														SalesModel pmdl=new SalesDao().getSale(pid);
														List paymentList=new ArrayList();
														
														paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.SALES, pid);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalPayed+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualPaidAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.CUSTOMER);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalAmount+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.CUSTOMER);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalPayed+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualPaidAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														if(isBaseCurrency){
															totalAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																					(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
															totalPayed+=roundNumber(pmdl.getPayment_amount());
														}
														else{
															totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate()-pmdl.getExpenseAmount()/pmdl.getConversionRate())+
																	(pmdl.getExpenseAmount()/pmdl.getConversionRate()-pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
															actualAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																					(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
															totalPayed+=roundNumber(pmdl.getPayment_amount()/pmdl.getConversionRate());
															actualPaidAmount+=roundNumber(pmdl.getPayment_amount());
														}
														
														balance_to_pay=totalAmount-totalPayed;
														actual_balance_to_pay=actualAmount-actualPaidAmount;
														
														PaymentInvoiceBean bean;
														if(isBaseCurrency) {
															if(paymentAmount>=balance_to_pay){
																payingAmount=balance_to_pay;
																paymentAmount-=balance_to_pay;
															}
															else{
																payingAmount=paymentAmount;
																paymentAmount=0;
															}
															bean=new PaymentInvoiceBean(
																				SConstants.SALES,
																				pid,
																				mdl.getId(),
																				getOfficeID(),
																				(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				roundNumber(payingAmount),
																				roundNumber(paymentConversionRate),
																				isCheque);
															invoiceMapList.add(bean);
															tran.addTransaction(SConstants.CR, 
																				(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
																				(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
																				roundNumber(payingAmount),
																				"",
																				(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																				,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																				(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
															
														}
														else{
															boolean isSavable=false;
															if(paymentAmount>=actual_balance_to_pay){
																isSavable=true;
																actualPayingAmount=actual_balance_to_pay;
																paymentAmount-=actual_balance_to_pay;
															}
															else{
																actualPayingAmount=paymentAmount;
																paymentAmount=0;
															}
															
															double actualBaseCurrency=0;
															actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
															double differenceAmount=0;
															differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
															
															bean=new PaymentInvoiceBean(
																				SConstants.SALES,
																				pid,
																				mdl.getId(),
																				getOfficeID(),
																				(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				roundNumber(actualPayingAmount),
																				roundNumber(paymentConversionRate),
																				isCheque);
															if(isSavable){
																if(differenceAmount>0){
																	tran.addTransaction(SConstants.CR,
																						settings.getFOREX_DIFFERENCE_ACCOUNT(),
																						(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																						roundNumber(differenceAmount),
																						"",
																						(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																						(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																						,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																						(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
																	actualBaseCurrency-=differenceAmount;
																}
															}
															
															tran.addTransaction(SConstants.CR, 
																		(Long)item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
																		(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
																		roundNumber(actualBaseCurrency),
																		"",
																		(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																		,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																		(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
															invoiceMapList.add(bean);
														}
													}
												}
												if(paymentAmount>0){
													tran.addTransaction(SConstants.CR, 
															(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
															(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
															roundNumber(paymentAmount/paymentConversionRate),
															"",
															(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
															(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
															,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
															(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
												}
											}
											else{
												tran.addTransaction(SConstants.CR, 
														(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),
														(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(),
														roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue() /
																	(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
														"",
														(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
														(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
														,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
														(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											}
										}
										else if((Integer) item.getItemProperty(TBC_TYPE).getValue()==SConstants.PURCHASE){
											if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
												double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
												double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
												List billList=new ArrayList();
												billList=Arrays.asList(det.getBill_no().split(","));
												Iterator it=billList.iterator();
												while (it.hasNext()) {
													
													long pid = Long.parseLong(it.next().toString().trim());
													
													double actualPaidAmount=0;
													double actualAmount=0;
													double actual_balance_to_pay=0;
													double actualPayingAmount=0;
													
													double totalPayed=0;
													double totalAmount=0;
													double payingAmount=0;
													double balance_to_pay=0;
													
													if(paymentAmount<=0)
														break;
													if(pid!=0) {
														PurchaseModel pmdl=new PurchaseDao().getPurchaseModel(pid);
														List paymentList=new ArrayList();
														
														paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.PURCHASE, pid);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalPayed+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualPaidAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.SUPPLIER);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalAmount+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.SUPPLIER);
														if(paymentList.size()>0){
															Iterator payItr=paymentList.iterator();
															while (payItr.hasNext()) {
																DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
																if(mapMdl.getPaymentId()!=mdl.getId()){
																	if(isBaseCurrency){
																		totalPayed+=roundNumber(mapMdl.getAmount());
																	}
																	else{
																		totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																		actualPaidAmount+=mapMdl.getAmount();
																	}
																}
															}
														}
														
														if(isBaseCurrency){
															totalAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																					(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
															totalPayed+=roundNumber(pmdl.getPaymentAmount());
														}
														else{
															totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate()-pmdl.getExpenseAmount()/pmdl.getConversionRate())+
																	(pmdl.getExpenseAmount()/pmdl.getConversionRate()-pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
															actualAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																					(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
															totalPayed+=roundNumber(pmdl.getPaymentAmount()/pmdl.getConversionRate());
															actualPaidAmount+=roundNumber(pmdl.getPaymentAmount());
														}
														
														balance_to_pay=totalAmount-totalPayed;
														actual_balance_to_pay=actualAmount-actualPaidAmount;
														
														PaymentInvoiceBean bean;
														if(isBaseCurrency) {
															if(paymentAmount>=balance_to_pay){
																payingAmount=balance_to_pay;
																paymentAmount-=balance_to_pay;
															}
															else{
																payingAmount=paymentAmount;
																paymentAmount=0;
															}
															bean=new PaymentInvoiceBean(SConstants.PURCHASE,
																						pid,
																						mdl.getId(),
																						getOfficeID(),
																						(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																						roundNumber(payingAmount),
																						roundNumber(paymentConversionRate),
																						isCheque);
															invoiceMapList.add(bean);
															tran.addTransaction(SConstants.CR, 
																				(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																				(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																				roundNumber(payingAmount),
																				"",
																				(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																				,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																				(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
															
														}
														else{
															boolean isSavable=false;
															if(paymentAmount>=actual_balance_to_pay){
																isSavable=true;
																actualPayingAmount=actual_balance_to_pay;
																paymentAmount-=actual_balance_to_pay;
															}
															else{
																actualPayingAmount=paymentAmount;
																paymentAmount=0;
															}
															double actualBaseCurrency=0;
															actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
															double differenceAmount=0;
															differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
															
															bean=new PaymentInvoiceBean(SConstants.PURCHASE,
																						pid,
																						mdl.getId(),
																						getOfficeID(),
																						(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																						roundNumber(actualPayingAmount),
																						roundNumber(paymentConversionRate),
																						isCheque);
															if(isSavable){
																if(differenceAmount>0){
																	tran.addTransaction(SConstants.CR, 
																						(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																						settings.getFOREX_DIFFERENCE_ACCOUNT(),  
																						roundNumber(differenceAmount),
																						"",
																						(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																						(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																						,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																						(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
																	actualBaseCurrency-=differenceAmount;
																}
															}
															
															tran.addTransaction(SConstants.CR, 
																		(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																		(Long)item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																		roundNumber(actualBaseCurrency),
																		"",
																		(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
																		,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
																		(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
															invoiceMapList.add(bean);
														}
													}
												}
												if(paymentAmount>0){
													tran.addTransaction(SConstants.CR, 
															(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
															(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
															roundNumber(paymentAmount/paymentConversionRate),
															"",
															(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
															(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
															,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
															(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
												}
											}
											else{
												tran.addTransaction(SConstants.CR, 
														(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
														(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
														roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()/
																	(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
														"",
														(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
														(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()
														,(Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue(),
														(Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
											}
										}
									}
								}
								mdl.setPdc_payment_list(childList);

								TransactionModel transaction=null;
								
								if(mdl.getTransactionId()!=0)
									transaction=dao.getTransactionModel(mdl.getTransactionId());
								
								if(transaction!=null){
									if(tran.getChildList().size()>0)
										transaction.setTransaction_details_list(tran.getChildList());
									else
										transaction.setTransaction_details_list(null);
									transaction.setDate(mdl.getDate());
									transaction.setLogin_id(getLoginID());
								}
								else
									transaction=tran.getTransaction(SConstants.PDC_PAYMENT,CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));

								long id=dao.update(	mdl, transaction, invoiceMapList, false);
								
//								saveActivity(getOptionId(), "PDC Payment Saved. Bill No : "
//												+ id+ ",  : , Payment Amount : "
//												+ table.getColumnFooter(TBC_AMOUNT).toString(),id);
								Notification.show("Updated Successfully..!",Type.WARNING_MESSAGE);
								loadData(id);
							}
							else{
								Notification.show("No Payment Done",Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show("Error",Type.ERROR_MESSAGE);
					}
				}
			});
			
			
			updateButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					/*try {
						if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {
							if (isValid()) {
								PdcPaymentModel mdl = dao.getPdcPaymentModel((Long) paymentCombo.getValue());
								List<PdcPaymentDetailsModel> childList=new ArrayList<PdcPaymentDetailsModel>();
								List<PaymentInvoiceBean> invoiceMapList=new ArrayList<PaymentInvoiceBean>();
								
								mdl.setBankAccount(new LedgerModel((Long)bankAccountSelect.getValue()));
								mdl.setRef_no(refNoTextField.getValue());
								mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								mdl.setOffice_id(getOfficeID());
								mdl.setLogin_id(getLoginID());
								mdl.setMemo(memoTextArea.getValue());
								
								FinTransaction tran = new FinTransaction();
								Iterator itr = table.getItemIds().iterator();							

								while (itr.hasNext()) {
									boolean isBaseCurrency=true;
									boolean isCheque=false;
									Item item = table.getItem(itr.next());
									PdcPaymentDetailsModel det=null;
									
									if((Long) item.getItemProperty(TBC_PDC_ID).getValue()!=0)
										det=dao.getPdcPaymentDetailsModel((Long) item.getItemProperty(TBC_PDC_ID).getValue());
									
									if(det==null)
										det=new PdcPaymentDetailsModel();
									
									det.setCash_or_check((Integer) item.getItemProperty(TBC_TYPE).getValue());
									det.setCash_or_check((Integer) item.getItemProperty(TBC_TYPE).getValue());
									if((Integer) item.getItemProperty(TBC_TYPE).getValue() == SConstants.bank_account.CHEQUE)
										isCheque=true;
									det.setAccount(new LedgerModel((Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue()));
									det.setButtonVisible((Boolean) item.getItemProperty(TBC_BUTTON_VISIBLE).getValue());
									det.setBill_no(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim());
									det.setAmount(roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()));
									det.setCurrencyId(new CurrencyModel((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()));
									if((Long) item.getItemProperty(TBC_CURRENCY_ID).getValue()!=getCurrencyID())
										isBaseCurrency=false;
									det.setConversionRate((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
									det.setDepartmentId((Long) item.getItemProperty(TBC_DEPARTMENT_ID).getValue());
									det.setDivisionId((Long) item.getItemProperty(TBC_DIVISION_ID).getValue());
									det.setChequeNo(item.getItemProperty(TBC_CHEQUE_NO).getValue().toString());
									if((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()!=null)
										det.setChequeDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_CHEQUE_DATE_ID).getValue()));
									else
										det.setChequeDate(null);
									det.setFromDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_FROM_DATE).getValue()));
									det.setToDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_TO_DATE).getValue()));
									childList.add(det);
									
									if(item.getItemProperty(TBC_BILL_NO).getValue().toString().trim().length()>0){
										double paymentAmount = roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue());
										double paymentConversionRate = roundNumber((Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
										List billList=new ArrayList();
										billList=Arrays.asList(det.getBill_no().split(","));
										Iterator it=billList.iterator();
										while (it.hasNext()) {
											long pid = Long.parseLong(it.next().toString().trim());
											double actualPaidAmount=0;
											double actualAmount=0;
											double actual_balance_to_pay=0;
											double actualPayingAmount=0;
											
											double totalPayed=0;
											double totalAmount=0;
											double payingAmount=0;
											double balance_to_pay=0;
											
											if(paymentAmount<=0)
												break;
											if(pid!=0) {
												PurchaseModel pmdl=new PurchaseDao().getPurchaseModel(pid);
												List paymentList=new ArrayList();
												
												paymentList=dao.getAllPaymentList(getOfficeID(), SConstants.PURCHASE, pid);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														PaymentInvoiceMapModel mapMdl = (PaymentInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalPayed+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualPaidAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.DEBIT, pid, SConstants.creditDebitNote.SUPPLIER);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalAmount+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalAmount+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												paymentList=dao.getAllCreditDebitList(getOfficeID(), SConstants.creditDebitNote.CREDIT, pid, SConstants.creditDebitNote.SUPPLIER);
												if(paymentList.size()>0){
													Iterator payItr=paymentList.iterator();
													while (payItr.hasNext()) {
														DebitCreditInvoiceMapModel mapMdl = (DebitCreditInvoiceMapModel) payItr.next();
														if(mapMdl.getPaymentId()!=mdl.getId()){
															if(isBaseCurrency){
																totalPayed+=roundNumber(mapMdl.getAmount());
															}
															else{
																totalPayed+=roundNumber(mapMdl.getAmount()/mapMdl.getConversionRate());
																actualPaidAmount+=mapMdl.getAmount();
															}
														}
													}
												}
												
												if(isBaseCurrency){
													totalAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																			(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
													totalPayed+=roundNumber(pmdl.getPaymentAmount());
												}
												else{
													totalAmount=roundNumber((pmdl.getAmount()/pmdl.getConversionRate()-pmdl.getExpenseAmount()/pmdl.getConversionRate())+
															(pmdl.getExpenseAmount()/pmdl.getConversionRate()-pmdl.getExpenseCreditAmount()/pmdl.getConversionRate()));
													actualAmount=roundNumber((pmdl.getAmount()-pmdl.getExpenseAmount())+
																			(pmdl.getExpenseAmount()-pmdl.getExpenseCreditAmount()));
													totalPayed+=roundNumber(pmdl.getPaymentAmount()/pmdl.getConversionRate());
													actualPaidAmount+=roundNumber(pmdl.getPaymentAmount());
												}
												
												balance_to_pay=totalAmount-totalPayed;
												actual_balance_to_pay=actualAmount-actualPaidAmount;
												
												PaymentInvoiceBean bean;
												if(isBaseCurrency) {
													if(paymentAmount>=balance_to_pay){
														payingAmount=balance_to_pay;
														paymentAmount-=balance_to_pay;
													}
													else{
														payingAmount=paymentAmount;
														paymentAmount=0;
													}
													bean=new PaymentInvoiceBean(
																		SConstants.PURCHASE,
																		pid,
																		mdl.getId(),
																		getOfficeID(),
																		(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		roundNumber(payingAmount),
																		roundNumber(paymentConversionRate),
																		isCheque);
													invoiceMapList.add(bean);
													tran.addTransaction(SConstants.CR, 
																		(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																		(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																		roundNumber(payingAmount),
																		"",
																		(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue());
													
												}
												else{
													boolean isSavable=false;
													if(paymentAmount>=actual_balance_to_pay){
														isSavable=true;
														actualPayingAmount=actual_balance_to_pay;
														paymentAmount-=actual_balance_to_pay;
													}
													else{
														actualPayingAmount=paymentAmount;
														paymentAmount=0;
													}
													
													double actualBaseCurrency=0;
													actualBaseCurrency=roundNumber(actualPayingAmount/(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
													double differenceAmount=0;
													differenceAmount=(actualBaseCurrency+totalPayed)-totalAmount;
													
													bean=new PaymentInvoiceBean(
																		SConstants.PURCHASE,
																		pid,
																		mdl.getId(),
																		getOfficeID(),
																		(Long) item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																		roundNumber(actualPayingAmount),
																		roundNumber(paymentConversionRate),
																		isCheque);
													if(isSavable){
														if(differenceAmount>0){
															tran.addTransaction(SConstants.CR, 
																				(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																				settings.getFOREX_DIFFERENCE_ACCOUNT(),  
																				roundNumber(differenceAmount),
																				"",
																				(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																				(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue());
															actualBaseCurrency-=differenceAmount;
														}
													}
													
													tran.addTransaction(SConstants.CR, 
																(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
																(Long)item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
																roundNumber(actualBaseCurrency),
																"",
																(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
																(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue());
													invoiceMapList.add(bean);
												}
											}
										}
										if(paymentAmount>0){
											tran.addTransaction(SConstants.CR, 
													(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
													(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
													roundNumber(paymentAmount/paymentConversionRate),
													"",
													(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
													(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue());
										}
									}
									else{
										tran.addTransaction(SConstants.CR, 
												(Long) item.getItemProperty(TBC_FROM_LEDGER_ID).getValue(), 
												(Long) item.getItemProperty(TBC_TO_LEDGER_ID).getValue(),  
												roundNumber((Double)item.getItemProperty(TBC_AMOUNT).getValue()/
															(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue()),
												"",
												(Long)item.getItemProperty(TBC_CURRENCY_ID).getValue(),
												(Double)item.getItemProperty(TBC_CONVERSION_RATE).getValue());
									}
								}
								mdl.setBank_account_payment_list(childList);
								
								TransactionModel transaction = new PurchaseDao().getTransactionModel(mdl.getTransactionId());
								transaction.setTransaction_details_list(tran.getChildList());
								transaction.setDate(mdl.getDate());
								transaction.setLogin_id(getLoginID());

								dao.update(mdl, transaction, invoiceMapList);
								saveActivity(getOptionId(),"Bank Account Payment Updated. Bill No : "
										+ paymentCombo.getItemCaption(paymentCombo.getValue())
										+ ", Bank Acct. : "+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())
										+ ", Payment Amount : "+ table.getColumnFooter(TBC_AMOUNT).toString(),(Long)paymentCombo.getValue());
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								loadData(mdl.getId());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}*/
				}
			});

			
			deleteButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString() .equals("0")) {
						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.delete((Long) paymentCombo.getValue());
										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadData(0);
									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									}
								}
							}
						});
					}
				}
			});

			
			cancelButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					/*if (paymentCombo.getValue() != null && !paymentCombo.getValue().toString().equals("0")) {

						ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										dao.cancel((Long) paymentCombo.getValue());
										saveActivity(getOptionId(),"Bank Account Payment Deleted. Bill No : "
													+ paymentCombo.getItemCaption(paymentCombo.getValue())+ ", Bank Acct. : "
													+ bankAccountSelect.getItemCaption(bankAccountSelect.getValue())+ ", Payment Amount : "+ 
													table.getColumnFooter(TBC_AMOUNT).toString(),
													(Long)paymentCombo.getValue());

										Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
										loadData(0);
									} catch (Exception e) {
										e.printStackTrace();
										Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
									}
								}
							}
						});
					}*/
				}
			});
			
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					Object obj=paymentDateField.getValue();
					paymentDateField.setValue(null);
					if(obj!=null)
						paymentDateField.setValue((Date)obj);
				}
			};
			
			
			final Action actionDeleteStock = new Action("Edit");

			
			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					return new Action[] { actionDeleteStock };
				}

				@SuppressWarnings("static-access")
				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
					if(table.getValue()!=null){
						PDCUI pdc=new PDCUI();
						Item item = table.getItem(table.getValue());	
						pdc.setCaption(getPropertyName("pdc"));
						pdc.loadData((Long) item.getItemProperty(TBC_PDC_ID).getValue());
						pdc.center();
						getUI().getCurrent().addWindow(pdc);
						pdc.addCloseListener(closeListener);
					}
				}

			});

			
			addShortcutListener(new ShortcutListener("Add", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			if (!isFinYearBackEntry()) {
				saveButton.setVisible(false);
				updateButton.setVisible(false);
				deleteButton.setVisible(false);
				
				Notification.show(getPropertyName("warning_financial_year"),
						Type.WARNING_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	
	
	@SuppressWarnings("rawtypes")
	public void loadTable(boolean isCreateNew){
		table.setVisibleColumns(allHeaders);
		table.removeAllItems();
		try {
			List resultList=new ArrayList();
			resultList=dao.getPdcList(CommonUtil.getSQLDateFromUtilDate(paymentDateField.getValue()), 
										getOfficeID(), isCreateNew);
			
			if(resultList.size()>0){
				Iterator itr=resultList.iterator();
				while (itr.hasNext()) {
					PdcModel mdl = (PdcModel) itr.next();
					Iterator it=mdl.getPdc_list().iterator();
					while (it.hasNext()) {
						PdcDetailsModel det = (PdcDetailsModel) it.next();
						String department="None";
						String division="None";
						
						if(det.getDepartmentId()!=0)
							department=new DepartmentDao().getDepartmentName(det.getDepartmentId());
						if(det.getDivisionId()!=0)
							division=new DivisionDao().getDivisionName(det.getDivisionId());
						
						if(isCreateNew){
							if(det.getStatus()!=SConstants.PDCStatus.ISSUED)
								continue;
						}
						
						table.addItem(new Object[]{new SCheckBox(null, false),
												(long)0,
												(long)0,
												mdl.getId(),
												mdl.getBill_no(),
												det.getId(),
												det.getType(),
												det.getBill_no(),
												det.getChequeNo(),
												det.getIssueDate(),
												CommonUtil.formatDateToDDMMYYYY(det.getIssueDate()),
												mdl.getChequeDate(),
												CommonUtil.formatDateToDDMMYYYY(mdl.getChequeDate()),
												det.getDepartmentId(),
												department,
												det.getDivisionId(),
												division,
												roundNumber(det.getAmount()),
												det.getCurrencyId().getId(),
												det.getCurrencyId().getCode(),
												roundNumber(det.getConversionRate()),
												det.getStatus(),
												getStatus(det.getStatus()),
												mdl.getBankAccount().getId(),
												det.getAccount().getId()},table.getItemIds().size()+1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		calculateTotals();
		table.setVisibleColumns(requiredHeaders);
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadData(long id) {
		List list= new ArrayList();
		try {
			list.add(new PdcPaymentModel(0, "----Create New-----"));
			list.addAll(dao.getPdcPaymentModelList(getOfficeID()));
			SCollectionContainer bic = SCollectionContainer.setList(list, "id");
			paymentCombo.setContainerDataSource(bic);
			paymentCombo.setItemCaptionPropertyId("bill_no");
			paymentCombo.setValue(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	
	@SuppressWarnings("rawtypes")
	public void calculateTotals() {
		try {
			double total=0;
			Iterator it = table.getItemIds().iterator();
			while (it.hasNext()) {
				Item item = table.getItem(it.next());
				total += roundNumber((Double) item.getItemProperty(TBC_AMOUNT).getValue()/ 
										(Double) item.getItemProperty(TBC_CONVERSION_RATE).getValue());
			}
			table.setColumnFooter(TBC_AMOUNT,roundNumber(total)+new CurrencyManagementDao().getselecteditem(getCurrencyID()).getCode());
		} catch (Exception e) {
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
	

	
	@SuppressWarnings("rawtypes")
	@Override
	public Boolean isValid() {
		boolean ret = true;
		int count=0;
		Iterator itr=table.getItemIds().iterator();
		while (itr.hasNext()) {
			Item item = table.getItem(itr.next());
			SCheckBox box=(SCheckBox)item.getItemProperty(TBC_SELECT).getValue();
			if(box.getValue())
				count++;
		}
		
		if (count <= 0) {
			setRequiredError(table,getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(table, null, false);

		if (dateField.getValue() == null) {
			setRequiredError(dateField, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(dateField, null, false);
		return ret;
	}
	
	

	
	public boolean isDatesValid(SDateField date){
		boolean valid=true;
		
			
		if (date.getValue() == null) {
			setRequiredError(date, getPropertyName("invalid_selection"), true);
			valid = false;
		} else
			setRequiredError(date, null, false);
		
		if(date.getValue()!=null){
			if(date.getValue().compareTo(getWorkingDate())>0){
				setRequiredError(date, getPropertyName("invalid_selection"), true);
				valid = false;
			}
			else
				setRequiredError(date, null, false);
		}
		return valid;
	}
	
	
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}
	
	
	
	
	public SComboField getAccountDepositNumberList() {
		return paymentCombo;
	}
	
	

	
	public void setAccountDepositNumberList(SComboField paymentCombo) {
		this.paymentCombo = paymentCombo;
	}
	
	

	
	@Override
	public SComboField getBillNoFiled() {
		return paymentCombo;
	}
	
	
	
	
	public void setPaymentCombo(long id) {
		paymentCombo.setValue(id);
	}
	
	

	
	public String getStatus(int stat){
		String status="";
		switch (stat) {
		
			case 1:
					status="Issued";
					break;
					
			case 2:
					status="Approved";
					break;
				
			case 3:
					status="Cancelled";
					break;

			default:
					break;
		}
		return status;
	}
	
}

 