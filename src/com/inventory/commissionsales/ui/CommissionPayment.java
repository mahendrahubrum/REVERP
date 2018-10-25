/*package com.inventory.commissionsales.ui;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.commissionsales.dao.CommissionPaymentDao;
import com.inventory.commissionsales.dao.CommissionPurchaseDao;
import com.inventory.commissionsales.model.CommissionPaymentModel;
import com.inventory.commissionsales.model.CommissionPurchaseModel;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.model.S_OfficeModel;
*//**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 5, 2014
 *//*
public class CommissionPayment extends SparkLogic {
	
	private static final long serialVersionUID = -3702291973892269010L;

	long id;
	
	SPanel pannel;
	SVerticalLayout hLayout;
	SVerticalLayout form;
	HorizontalLayout buttonLayout;
	
	CollectionContainer bic;
	
	private SComboField commissionPurchaseCombo;
	private SComboField commissionPaymentCombo;
	private SComboField supplier;

	STextField supplierAmount,gross_sale,less_expense,net_sale,freight,airport_charges;
	STextField waste,dpa_charges,pickup_charge,unloading_charge,storage_charge,port;
	STextField auction,commission,details, commissionPercentage,purchaseAmountField,salesAmountField;
	SDateField dateField;
	
	SButton save;
    SButton delete;
    SButton update;
	
	CommissionPurchaseDao purchDao;
	CommissionPaymentDao objDao;
	
	SettingsValuePojo settings;
	
	SButton createNewButton;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;
	
	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		setSize(900, 600);
		objDao=new CommissionPaymentDao();
		purchDao=new CommissionPurchaseDao();
		
		
		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute("settings");
		
		try {
			dateField=new SDateField(null,100,getDateFormat(), getWorkingDate());
			
			supplier= new SComboField(null,250,new SupplierDao().getAllActiveSupplierNamesWithLedgerID(getOfficeID()),"id","name");
			supplier.setReadOnly(true);
			purchaseAmountField= new STextField(null,100,"0");
			purchaseAmountField.setReadOnly(true);
			salesAmountField= new STextField(null,100,"0");
			salesAmountField.setReadOnly(true);
			gross_sale= new STextField(getPropertyName("gross_sale_dhs"),"0");
			less_expense= new STextField(getPropertyName("less_expense"),"0");
			net_sale= new STextField(getPropertyName("net_sale"),"0");
			supplierAmount= new STextField(getPropertyName("supplier_amt"),"0");
			freight= new STextField(getPropertyName("frieght_do"),"0");
			airport_charges= new STextField(getPropertyName("airport_charge"),"0");
			waste= new STextField(getPropertyName("clear_waste"),"0");
			dpa_charges= new STextField(getPropertyName("dpa_charge"),"0");
			pickup_charge= new STextField(getPropertyName("pickup_charge"),"0");
			unloading_charge= new STextField(getPropertyName("unloading_charge"),"0");
			storage_charge= new STextField(getPropertyName("storage_charge"),"0");
			port= new STextField(getPropertyName("port"),"0");
			auction= new STextField(getPropertyName("auction_others"),"0");
			commission= new STextField(getPropertyName("commission"),"0");
			commissionPercentage= new STextField(getPropertyName("commission_perc"),"0");
			details= new STextField(getPropertyName("details"));
			
			less_expense.setReadOnly(true);
			net_sale.setReadOnly(true);
			supplierAmount.setReadOnly(true);
			commission.setReadOnly(true);
			
			createNewButton= new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription(getPropertyName("create_new"));

			pannel = new SPanel();
			hLayout = new SVerticalLayout();
//			vLayout=new SVerticalLayout();
			form = new SVerticalLayout();
			buttonLayout=new HorizontalLayout();

			pannel.setSizeFull();
			form.setSizeFull();
			
			save=new SButton(getPropertyName("save"), 70);
			save.setStyleName("savebtnStyle");
			save.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		    delete=new SButton(getPropertyName("Delete"), 78);
		    delete.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
		    delete.setStyleName("deletebtnStyle");
		    update=new SButton(getPropertyName("Update"),80);
		    update.setStyleName("updatebtnStyle");
		    update.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
		    
			
			buttonLayout.addComponent(save);
	        buttonLayout.addComponent(delete);
	        buttonLayout.addComponent(update);
	        
	        buttonLayout.setSpacing(true);
			
	        delete.setVisible(false);
	        update.setVisible(false);
	        
			CommissionPaymentModel og = new CommissionPaymentModel();
			og.setId(0);
			og.setDetails("--------- Create New ---------");
			List list = new ArrayList();
			list.add(0, og);
			list.addAll(objDao.getAllPaymentNos(getOfficeID()));
			commissionPaymentCombo = new SComboField(null, 160, list, "id", "details");
			commissionPaymentCombo.setInputPrompt("---------- Create New ------------");
			
			commissionPurchaseCombo = new SComboField(null, 160, purchDao.getAllActivePurchaseNos(getOfficeID()), "id", "comments");
			commissionPurchaseCombo.setInputPrompt("---------- Select ------------");
			
			loadOptions(0);
			
			SHorizontalLayout salLisrLay=new SHorizontalLayout();
			salLisrLay.addComponent(commissionPaymentCombo);
			salLisrLay.addComponent(createNewButton);
			
			SGridLayout grid=new SGridLayout(7,3);
			grid.setSpacing(true);
			grid.addComponent(new SLabel(getPropertyName("payment_no")),1,0);
			grid.addComponent(salLisrLay,2,0);
			grid.addComponent(new SLabel("Date"),3,0);
			grid.addComponent(dateField,4,0);
			grid.addComponent(new SLabel("Purchase No"),1,1);
			grid.addComponent(commissionPurchaseCombo,2,1);
			grid.addComponent(new SLabel(getPropertyName("Purchase Amount")),3,1);
			grid.addComponent(purchaseAmountField,4,1);
			grid.addComponent(new SLabel(getPropertyName("Net Sales")),5,1);
			grid.addComponent(salesAmountField,6,1);
			grid.addComponent(new SLabel("Supplier"),1,2);
			grid.addComponent(supplier,2,2);
			
			
			form.addComponent(grid);

			form.setMargin(true);
			form.setSpacing(true);
			
			form.addComponent(new SHTMLLabel(null, "<h2><u>"+getPropertyName("expenses")+"</u></h2>"));
			
			form.addComponent(new SHorizontalLayout(true, new SFormLayout(gross_sale,less_expense,net_sale,supplierAmount,details),new SFormLayout(freight,airport_charges,waste,dpa_charges,pickup_charge
					,unloading_charge,storage_charge,port,auction,new SHorizontalLayout(commissionPercentage,commission) )));

			form.addComponent(buttonLayout);
			form.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);

			hLayout.addComponent(popupLay);
			hLayout.addComponent(form);
			hLayout.setMargin(true);
			hLayout.setComponentAlignment(popupLay, Alignment.TOP_CENTER);
			windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
			      
			pannel.setContent(windowNotif);
			
			addShortcutListener(new ShortcutListener("Add New Purchase", ShortcutAction.KeyCode.N, new int[] {
                    ShortcutAction.ModifierKey.ALT}) {
		        @Override
		        public void handleAction(Object sender, Object target) {
		        	loadOptions(0);
		        }
		    });
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (save.isVisible())
						save.click();
					else
						update.click();
				}
			});
			
			commissionPurchaseCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(commissionPurchaseCombo.getValue()!=null&&!commissionPurchaseCombo.getValue().equals("")){
						CommissionPurchaseModel pmdl;
						try {
							pmdl = purchDao.getPurchase(toLong(commissionPurchaseCombo.getValue().toString()));
							supplier.setNewValue(pmdl.getSupplier().getId());
							purchaseAmountField.setNewValue(pmdl.getAmount()+"");
							
							salesAmountField.setNewValue(objDao.getTotalSalesAmountOfPurchase(pmdl.getId())+"");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						supplier.setNewValue(null);
						purchaseAmountField.setNewValue("0");
						salesAmountField.setNewValue("0");
					}
				}
			});
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)commissionPurchaseCombo.getValue(),confirmBox.getUserID());
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
				@Override
				public void buttonClick(ClickEvent event) {
					if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
						if(commissionPurchaseCombo.getValue()!=null && !commissionPurchaseCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)commissionPurchaseCombo.getValue(),
									"Commission Sales : No. "+commissionPurchaseCombo.getItemCaption(commissionPurchaseCombo.getValue()));
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
						if(commissionPurchaseCombo.getValue()!=null && !commissionPurchaseCombo.getValue().toString().equals("0")) {
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
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					commissionPaymentCombo.setValue(null);
				}
			});
			
			
			save.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
		        			if(isValid()){
		        				
		        				CommissionPaymentModel objModel=new CommissionPaymentModel();
		        				objModel.setNumber(getNextSequence("Commission Payment Number", getLoginID()));
			        			objModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			        			objModel.setGross_sale(toDouble(gross_sale.getValue()));
			        			objModel.setLess_expense(toDouble(less_expense.getValue()));
			        			objModel.setNet_sale(toDouble(net_sale.getValue()));
			        			objModel.setFreight(toDouble(freight.getValue()));
			        			objModel.setAirport_charges(toDouble(airport_charges.getValue()));
			        			objModel.setWaste(toDouble(waste.getValue()));
			        			objModel.setDpa_charges(toDouble(dpa_charges.getValue()));
			        			objModel.setPickup_charge(toDouble(pickup_charge.getValue()));
			        			objModel.setUnloading_charge(toDouble(unloading_charge.getValue()));
			        			objModel.setStorage_charge(toDouble(storage_charge.getValue()));
			        			objModel.setPort(toDouble(port.getValue()));
			        			objModel.setAuction(toDouble(auction.getValue()));
			        			objModel.setCommission(toDouble(commission.getValue()));
			        			objModel.setDetails(details.getValue());
			        			objModel.setPurchaseId(toLong(commissionPurchaseCombo.getValue().toString()));
			        			objModel.setSupplierId(toLong(supplier.getValue().toString()));
			        			objModel.setFromAccount(settings.getCASH_ACCOUNT());

			        			objModel.setOffice(new S_OfficeModel(getOfficeID()));
			        			
			        			FinTransaction trans = new FinTransaction();
								double supplierAmt=toDouble(supplierAmount.getValue());
								
								if (supplierAmt !=0) {
									trans.addTransactionWithNarration(SConstants.CR,settings.getCASH_ACCOUNT(),
											toLong(supplier.getValue().toString()),
											
											roundNumber(supplierAmt), "Supplier Amount To Purch. Acct.");
//									trans.addTransactionWithNarration(SConstants.CR,
//											settings.getCASH_ACCOUNT(),
//											objModel.getSupplier().getId(),
//											roundNumber(supplierAmt), "Supplier Amount");
								}
								if (objModel.getFreight()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getFreight()),"Freight Charge");
								}
								
								if (objModel.getAirport_charges()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getAirport_charges()),"Airport Charge");
								}
								
								if (objModel.getWaste()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getWaste()),"Waste");
								}
								
								if (objModel.getDpa_charges()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getDpa_charges()),"DPA Charge");
								}
								
								if (objModel.getPickup_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getPickup_charge()),"Pickup Charge");
								}
								
								if (objModel.getUnloading_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getUnloading_charge()),"Unloading Charge");
								}
								
								if (objModel.getStorage_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getStorage_charge()),"Storage Charge");
								}
								
								if (objModel.getPort()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getPort()),"Port");
								}
								
								if (objModel.getAuction()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getAuction()),"Auction");
								}
								
			        			
			                    try {
									id=objDao.save(objModel, trans.getTransaction(SConstants.COMMISSION_PAYMENTS, CommonUtil.getSQLDateFromUtilDate(dateField.getValue())));
									saveActivity(
											getOptionId(),
											"New Commission Payment. Payment No : "
													+ objModel.getNumber()
													+ ", Supplier : "
													+ supplier
															.getItemCaption(supplier
																	.getValue())
													+ ", Payment Amount : "
													+ roundNumber(toDouble(supplierAmount
															.getValue().toString())),objModel.getId());
									
									loadOptions(id);
									Notification.show(getPropertyName("Success"), getPropertyName("save_success"),
					                        Type.WARNING_MESSAGE);
									
								} catch (Exception e) {
									Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
					                        Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
		        		}
		        		
	        		} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
	        		
	        	}
	        	
	        });
			
			
			commissionPaymentCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
						
					try {
						removeErrors();
						
						if (commissionPaymentCombo.getValue() != null
								&&  !commissionPaymentCombo.getValue().toString().equals("0")) {
							
							save.setVisible(false);
							delete.setVisible(true);
							update.setVisible(true);

							CommissionPaymentModel objModel = objDao
									.getPaymentModel((Long) commissionPaymentCombo
											.getValue());
							
							CommissionPurchaseModel pmdl = purchDao.getPurchase(objModel.getPurchaseId());
							
							commissionPurchaseCombo.setValue(objModel.getPurchaseId());
							supplier.setNewValue(pmdl.getSupplier().getId());
							dateField.setValue(objModel.getDate());
							gross_sale.setValue(asString(objModel.getGross_sale()));
							less_expense.setNewValue(asString(objModel.getLess_expense()));
							net_sale.setNewValue(asString(objModel.getNet_sale()));
							freight.setValue(asString(objModel.getFreight()));
							airport_charges.setValue(asString(objModel.getAirport_charges()));
							waste.setValue(asString(objModel.getWaste()));
							dpa_charges.setValue(asString(objModel.getDpa_charges()));
							pickup_charge.setValue(asString(objModel.getPickup_charge()));
							unloading_charge.setValue(asString(objModel.getUnloading_charge()));
							storage_charge.setValue(asString(objModel.getStorage_charge()));
							port.setValue(asString(objModel.getPort()));
							auction.setValue(asString(objModel.getAuction()));
							commissionPercentage.setValue(asString(objModel.getCommission()*100/objModel.getGross_sale()));
							commission.setNewValue(asString(objModel.getCommission()));
							details.setValue(asString(objModel.getDetails()));
							

						} else {
							save.setVisible(true);
							delete.setVisible(false);
							update.setVisible(false);

							
							commissionPurchaseCombo.setValue(null);
							supplier.setNewValue(null);
							purchaseAmountField.setNewValue("0");
							salesAmountField.setNewValue("0");
							dateField.setValue(getWorkingDate());
							gross_sale.setValue("0");
							less_expense.setNewValue("0");
							net_sale.setNewValue("0");
							supplierAmount.setNewValue("0");
							freight.setValue("0");
							airport_charges.setValue("0");
							waste.setValue("0");
							dpa_charges.setValue("0");
							pickup_charge.setValue("0");
							unloading_charge.setValue("0");
							storage_charge.setValue("0");
							port.setValue("0");
							auction.setValue("0");
							commission.setNewValue("0");
							commissionPercentage.setValue("0");
							details.setValue("");

						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			ValueChangeListener listnr=new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						commission.setNewValue(""+roundNumber(toDouble(gross_sale.getValue())*toDouble(commissionPercentage.getValue())/100));
				
						net_sale.setNewValue(""+roundNumber(toDouble(gross_sale.getValue())-toDouble(less_expense.getValue())));
						
						supplierAmount.setNewValue(""+roundNumber(toDouble(gross_sale.getValue())-toDouble(less_expense.getValue())-toDouble(commission.getValue())));
					
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			};
			gross_sale.setImmediate(true);
			commissionPercentage.setImmediate(true);
			
			commissionPercentage.addValueChangeListener(listnr);
			gross_sale.addValueChangeListener(listnr);
			
			
			ValueChangeListener expnsCalculater=new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					try {
						less_expense.setNewValue(""+roundNumber(toDouble(freight.getValue())+toDouble(airport_charges.getValue())+toDouble(waste.getValue())+toDouble(dpa_charges.getValue())
								+toDouble(pickup_charge.getValue())+toDouble(unloading_charge.getValue())+toDouble(storage_charge.getValue())+toDouble(port.getValue())+toDouble(auction.getValue())));
					
						net_sale.setNewValue(""+roundNumber(toDouble(gross_sale.getValue())-toDouble(less_expense.getValue())));
						
						supplierAmount.setNewValue(""+roundNumber(toDouble(gross_sale.getValue())-toDouble(less_expense.getValue())-toDouble(commission.getValue())));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			};
			
			freight.setImmediate(true);
			airport_charges.setImmediate(true);
			waste.setImmediate(true);
			dpa_charges.setImmediate(true);
			pickup_charge.setImmediate(true);
			unloading_charge.setImmediate(true);
			storage_charge.setImmediate(true);
			port.setImmediate(true);
			auction.setImmediate(true);
			
			freight.addValueChangeListener(expnsCalculater);
			airport_charges.addValueChangeListener(expnsCalculater);
			waste.addValueChangeListener(expnsCalculater);
			dpa_charges.addValueChangeListener(expnsCalculater);
			pickup_charge.addValueChangeListener(expnsCalculater);
			unloading_charge.addValueChangeListener(expnsCalculater);
			storage_charge.addValueChangeListener(expnsCalculater);
			port.addValueChangeListener(expnsCalculater);
			auction.addValueChangeListener(expnsCalculater);
			
	        delete.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
	        			ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
	        			        new ConfirmDialog.Listener() {
	        			            public void onClose(ConfirmDialog dialog) {
	        			                if (dialog.isConfirmed()) {
	        			                	
	        			                	try {
	        			                		id=(Long)commissionPaymentCombo.getValue();
												objDao.delete(id);
												
												Notification.show(getPropertyName("Success"), getPropertyName("deleted_success"),
		        				                        Type.WARNING_MESSAGE);
												
												loadOptions(0);
												
											} catch (Exception e) {
												Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
								                        Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
	        			                } 
	        			            }
	        			        });
	        			
	        			
		        		
	        		} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        });
	        
	        
			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
							if (isValid()) {
								
								CommissionPaymentModel objModel = objDao
										.getPaymentModel((Long) commissionPaymentCombo.getValue());
								
			        			objModel.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
			        			objModel.setGross_sale(toDouble(gross_sale.getValue()));
			        			objModel.setLess_expense(toDouble(less_expense.getValue()));
			        			objModel.setNet_sale(toDouble(net_sale.getValue()));
			        			objModel.setFreight(toDouble(freight.getValue()));
			        			objModel.setAirport_charges(toDouble(airport_charges.getValue()));
			        			objModel.setWaste(toDouble(waste.getValue()));
			        			objModel.setDpa_charges(toDouble(dpa_charges.getValue()));
			        			objModel.setPickup_charge(toDouble(pickup_charge.getValue()));
			        			objModel.setUnloading_charge(toDouble(unloading_charge.getValue()));
			        			objModel.setStorage_charge(toDouble(storage_charge.getValue()));
			        			objModel.setPort(toDouble(port.getValue()));
			        			objModel.setAuction(toDouble(auction.getValue()));
			        			objModel.setCommission(toDouble(commission.getValue()));
			        			objModel.setDetails(details.getValue());
			        			objModel.setPurchaseId(toLong(commissionPurchaseCombo.getValue().toString()));
			        			objModel.setSupplierId(toLong(supplier.getValue().toString()));
			        			objModel.setFromAccount(settings.getCASH_ACCOUNT());
			        			
			        			FinTransaction trans = new FinTransaction();
								double supplierAmt=toDouble(supplierAmount.getValue());
								
								if (supplierAmt !=0) {
									trans.addTransactionWithNarration(SConstants.CR,settings.getCASH_ACCOUNT(),
											toLong(supplier.getValue().toString()),
											
											roundNumber(supplierAmt), "Supplier Amount To Purch. Acct.");
//									trans.addTransactionWithNarration(SConstants.CR,
//											settings.getCASH_ACCOUNT(),
//											objModel.getSupplier().getId(),
//											roundNumber(supplierAmt), "Supplier Amount");
								}
								if (objModel.getFreight()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getFreight()),"Freight Charge");
								}
								
								if (objModel.getAirport_charges()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getAirport_charges()),"Airport Charge");
								}
								
								if (objModel.getWaste()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getWaste()),"Waste");
								}
								
								if (objModel.getDpa_charges()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getDpa_charges()),"DPA Charge");
								}
								
								if (objModel.getPickup_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getPickup_charge()),"Pickup Charge");
								}
								
								if (objModel.getUnloading_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getUnloading_charge()),"Unloading Charge");
								}
								
								if (objModel.getStorage_charge()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getStorage_charge()),"Storage Charge");
								}
								
								if (objModel.getPort()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getPort()),"Port");
								}
								
								if (objModel.getAuction()!=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											settings.getCASH_ACCOUNT(),
											settings.getEXPENDITURE_ACCOUNT(),
											roundNumber(objModel.getAuction()),"Auction");
								}
								
								TransactionModel transObj=trans.getTransactionWithoutID(SConstants.COMMISSION_PAYMENTS, CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
								transObj.setTransaction_id(objModel.getTransaction_id());
								try {
									objDao.update(objModel, transObj);
									saveActivity(
											getOptionId(),
											"Commission Payment Updated. Payment No : "
													+ objModel.getNumber()
													+ ", Supplier : "
													+ supplier
															.getItemCaption(supplier
																	.getValue())
													+ ", Payment Amount : "
													+ roundNumber(toDouble(supplierAmount
															.getValue().toString())),objModel.getId());
									loadOptions(objModel.getId());
									Notification.show(getPropertyName("success"), getPropertyName("save_success"),
					                        Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
					                        Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
							}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});
			
			
			

		} catch (Exception e) {
		}

		return pannel;
	}



	public void loadOptions(long id){
		List testList;
		try {
			testList = objDao.getAllPaymentNos(getOfficeID());
			
			CommissionPaymentModel sop=new CommissionPaymentModel();
	        sop.setId(0);
	        sop.setDetails("----------- Create New ----------");
	        if(testList==null)
	        	testList=new ArrayList();
	        testList.add(0, sop);
	        
		    bic=CollectionContainer.fromBeans(testList, "id");
		    commissionPaymentCombo.setContainerDataSource(bic);
		    commissionPaymentCombo.setItemCaptionPropertyId("details");
		
		    commissionPaymentCombo.setValue(id);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public Boolean isValid() {

		boolean ret=true;
		
		if(gross_sale.getValue()==null || gross_sale.getValue().equals("")){
			setRequiredError(gross_sale, getPropertyName("invalid_data"),true);
			gross_sale.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(gross_sale.getValue())<0) {
					setRequiredError(gross_sale, getPropertyName("invalid_data"),true);
					gross_sale.focus();
					ret=false;
				}
				else
					setRequiredError(gross_sale, null,false);
			} catch (Exception e) {
				setRequiredError(gross_sale, getPropertyName("invalid_data"),true);
				gross_sale.focus();
				ret=false;
			}
		}

		if(less_expense.getValue()==null || less_expense.getValue().equals("")){
			setRequiredError(less_expense, getPropertyName("invalid_data"),true);
			less_expense.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(less_expense.getValue())<0) {
					setRequiredError(less_expense, getPropertyName("invalid_data"),true);
					less_expense.focus();
					ret=false;
				}
				else
					setRequiredError(less_expense, null,false);
			} catch (Exception e) {
				setRequiredError(less_expense, getPropertyName("invalid_data"),true);
				less_expense.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(net_sale.getValue()==null || net_sale.getValue().equals("")){
			setRequiredError(net_sale, getPropertyName("invalid_data"),true);
			net_sale.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(net_sale.getValue())<0) {
					setRequiredError(net_sale, getPropertyName("invalid_data"),true);
					net_sale.focus();
					ret=false;
				}
				else
					setRequiredError(net_sale, null,false);
			} catch (Exception e) {
				setRequiredError(net_sale, getPropertyName("invalid_data"),true);
				net_sale.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		
		if(freight.getValue()==null || freight.getValue().equals("")){
			setRequiredError(freight, getPropertyName("invalid_data"),true);
			freight.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(freight.getValue())<0) {
					setRequiredError(freight, getPropertyName("invalid_data"),true);
					freight.focus();
					ret=false;
				}
				else
					setRequiredError(freight, null,false);
			} catch (Exception e) {
				setRequiredError(freight, getPropertyName("invalid_data"),true);
				freight.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(airport_charges.getValue()==null || airport_charges.getValue().equals("")){
			setRequiredError(airport_charges, getPropertyName("invalid_data"),true);
			airport_charges.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(airport_charges.getValue())<0) {
					setRequiredError(airport_charges, getPropertyName("invalid_data"),true);
					airport_charges.focus();
					ret=false;
				}
				else
					setRequiredError(airport_charges, null,false);
			} catch (Exception e) {
				setRequiredError(airport_charges, getPropertyName("invalid_data"),true);
				airport_charges.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(waste.getValue()==null || waste.getValue().equals("")){
			setRequiredError(waste, getPropertyName("invalid_data"),true);
			waste.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(waste.getValue())<0) {
					setRequiredError(waste, getPropertyName("invalid_data"),true);
					waste.focus();
					ret=false;
				}
				else
					setRequiredError(waste, null,false);
			} catch (Exception e) {
				setRequiredError(waste, getPropertyName("invalid_data"),true);
				waste.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(dpa_charges.getValue()==null || dpa_charges.getValue().equals("")){
			setRequiredError(dpa_charges, getPropertyName("invalid_data"),true);
			dpa_charges.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(dpa_charges.getValue())<0) {
					setRequiredError(dpa_charges, getPropertyName("invalid_data"),true);
					dpa_charges.focus();
					ret=false;
				}
				else
					setRequiredError(dpa_charges, null,false);
			} catch (Exception e) {
				setRequiredError(dpa_charges, getPropertyName("invalid_data"),true);
				dpa_charges.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(pickup_charge.getValue()==null || pickup_charge.getValue().equals("")){
			setRequiredError(pickup_charge, getPropertyName("invalid_data"),true);
			pickup_charge.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(pickup_charge.getValue())<0) {
					setRequiredError(pickup_charge, getPropertyName("invalid_data"),true);
					pickup_charge.focus();
					ret=false;
				}
				else
					setRequiredError(pickup_charge, null,false);
			} catch (Exception e) {
				setRequiredError(pickup_charge, getPropertyName("invalid_data"),true);
				pickup_charge.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(unloading_charge.getValue()==null || unloading_charge.getValue().equals("")){
			setRequiredError(unloading_charge, getPropertyName("invalid_data"),true);
			unloading_charge.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(unloading_charge.getValue())<0) {
					setRequiredError(unloading_charge, getPropertyName("invalid_data"),true);
					unloading_charge.focus();
					ret=false;
				}
				else
					setRequiredError(unloading_charge, null,false);
			} catch (Exception e) {
				setRequiredError(unloading_charge, getPropertyName("invalid_data"),true);
				unloading_charge.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(storage_charge.getValue()==null || storage_charge.getValue().equals("")){
			setRequiredError(storage_charge, getPropertyName("invalid_data"),true);
			storage_charge.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(storage_charge.getValue())<0) {
					setRequiredError(storage_charge, getPropertyName("invalid_data"),true);
					storage_charge.focus();
					ret=false;
				}
				else
					setRequiredError(storage_charge, null,false);
			} catch (Exception e) {
				setRequiredError(storage_charge, getPropertyName("invalid_data"),true);
				storage_charge.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		
		
		if(port.getValue()==null || port.getValue().equals("")){
			setRequiredError(port, getPropertyName("invalid_data"),true);
			port.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(port.getValue())<0) {
					setRequiredError(port, getPropertyName("invalid_data"),true);
					port.focus();
					ret=false;
				}
				else
					setRequiredError(port, null,false);
			} catch (Exception e) {
				setRequiredError(port, getPropertyName("invalid_data"),true);
				port.focus();
				ret=false;
				// TODO: handle exception
			}
		}

		if(auction.getValue()==null || auction.getValue().equals("")){
			setRequiredError(auction, getPropertyName("invalid_data"),true);
			auction.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(auction.getValue())<0) {
					setRequiredError(auction, getPropertyName("invalid_data"),true);
					auction.focus();
					ret=false;
				}
				else
					setRequiredError(auction, null,false);
			} catch (Exception e) {
				setRequiredError(auction, getPropertyName("invalid_data"),true);
				auction.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		

		if(commission.getValue()==null || commission.getValue().equals("")) {
			setRequiredError(commission, getPropertyName("invalid_data"),true);
			commission.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(commission.getValue())<0) {
					setRequiredError(commission, getPropertyName("invalid_data"),true);
					commission.focus();
					ret=false;
				}
				else
					setRequiredError(commission, null,false);
			} catch (Exception e) {
				setRequiredError(commission, getPropertyName("invalid_data"),true);
				commission.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		
		if(commissionPurchaseCombo.getValue()==null||commissionPurchaseCombo.getValue().equals("")){
			setRequiredError(commissionPurchaseCombo, getPropertyName("invalid_selection"),true);
			commissionPurchaseCombo.focus();
			ret=false;
		}else{
			setRequiredError(commissionPurchaseCombo, null,false);
		}
		
		return ret;
	}
	
	public void removeErrors() {
		
		gross_sale.setComponentError(null);
		less_expense.setComponentError(null);
		net_sale.setComponentError(null);
		freight.setComponentError(null);
		airport_charges.setComponentError(null);
		waste.setComponentError(null);
		dpa_charges.setComponentError(null);
		pickup_charge.setComponentError(null);
		unloading_charge.setComponentError(null);
		storage_charge.setComponentError(null);
		port.setComponentError(null);
		auction.setComponentError(null);
		commission.setComponentError(null);
		details.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}




	public SComboField getCommissionSalesCombo() {
		return commissionPaymentCombo;
	}




	public void setCommissionSalesCombo(SComboField commissionSalesCombo) {
		this.commissionPaymentCombo = commissionSalesCombo;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return commissionPaymentCombo;
	}

}
*/