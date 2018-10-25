/*package com.inventory.commissionsales.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.commissionsales.dao.CommissionSalesDao;
import com.inventory.commissionsales.model.CommissionSalesModel;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.LedgerModel;
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
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.StatusDao;
import com.webspark.uac.model.S_OfficeModel;

public class AddCommissionSale extends SparkLogic {
	
	long id;
	
	SPanel pannel;
	SVerticalLayout hLayout;
//	SVerticalLayout vLayout;
	SVerticalLayout form;
	HorizontalLayout buttonLayout;
	
	CollectionContainer bic;
	
	private SComboField commissionSalesCombo;

	SComboField supplier;
	SDateField received_date, issue_date;
	STextField vesel, contr_no, consignment_mark, quantity, ss_cc, packages, quality, received_sound, supplierAmount;
	STextField damage,empty,shorte,gross_sale,less_expense,net_sale,freight,airport_charges;
	STextField waste,dpa_charges,pickup_charge,unloading_charge,storage_charge,port;
	STextField auction,commission,details, commissionPercentage;
	SComboField statusCombo;
	
	SButton save;
    SButton edit;
    SButton delete;
    SButton update;
    SButton cancel, printButton;
	
	List list;
	CommissionSalesDao objDao;
	
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
		
		setSize(1000, 600);
		objDao=new CommissionSalesDao();
		
		
		printButton = new SButton(getPropertyName("print"));
		printButton.setIcon(new ThemeResource(
				"icons/print.png"));
		printButton.setVisible(false);
		
		if (getHttpSession().getAttribute("settings") != null)
			settings = (SettingsValuePojo) getHttpSession().getAttribute("settings");
		
		try {
			
			supplier= new SComboField(null,160,new SupplierDao().getAllActiveSupplierNamesWithLedgerID(getOfficeID()),
					"id", "name", true, getPropertyName("select"));
			
			received_date= new SDateField(null, 100, getDateFormat(), getWorkingDate());
			issue_date= new SDateField(null, 100, getDateFormat(), getWorkingDate());
			vesel= new STextField();
			contr_no= new STextField();
			consignment_mark= new STextField();
			quantity= new STextField(null,"0");
			ss_cc= new STextField();
			packages= new STextField();
			quality= new STextField();
			received_sound= new STextField();
			damage= new STextField();
			empty= new STextField();
			shorte= new STextField();
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
		    edit=new SButton(getPropertyName("Edit"));
		    delete=new SButton(getPropertyName("Delete"), 78);
		    delete.setIcon(new ThemeResource(
					"icons/deleteSideIcon.png"));
		    update=new SButton(getPropertyName("Update"),80);
		    update.setIcon(new ThemeResource(
					"icons/updateSideIcon.png"));
		    cancel=new SButton(getPropertyName("Cancel"));
		    
			
			buttonLayout.addComponent(save);
	        buttonLayout.addComponent(edit);
	        buttonLayout.addComponent(delete);
	        buttonLayout.addComponent(update);
	        buttonLayout.addComponent(cancel);
	        buttonLayout.addComponent(printButton);
	        
	        buttonLayout.setSpacing(true);
			
	        printButton.setVisible(false);
	        edit.setVisible(false);
	        delete.setVisible(false);
	        update.setVisible(false);
	        cancel.setVisible(false);
	        
			CommissionSalesModel og = new CommissionSalesModel();
			og.setId(0);
			og.setContr_no("--------- Create New ---------");
			if (list == null)
				list = new ArrayList();
			list.add(0, og);
			
			commissionSalesCombo = new SComboField(null, 160, list, "id", "name");
			commissionSalesCombo.setInputPrompt(getPropertyName("create_new"));
			
			loadOptions(0);
			
			statusCombo=new SComboField(null, 160, new StatusDao().getStatuses("SalesTypeModel", "status"), "value","name");
			statusCombo.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long)1);
			vesel=new STextField(null,442);
			
			SHorizontalLayout salLisrLay=new SHorizontalLayout(new SLabel(getPropertyName("comn_sales_no"), 126),
					commissionSalesCombo,createNewButton);
			form.addComponent(salLisrLay);

//			form.addComponent(supplier);
			form.setMargin(true);
			form.setSpacing(true);
			
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("supplier"), 120), supplier,new SLabel(null, 180),new SLabel(getPropertyName("received_date")),received_date,new SLabel(getPropertyName("sissue_date")),issue_date));
//			form.addComponent(issue_date);
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("vesel_no"), 120),vesel,new SLabel(getPropertyName("contr_no"), 120),contr_no));
//			form.addComponent(contr_no);
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("consignment_mark"), 120),consignment_mark,new SLabel(getPropertyName("quantity"), 120),quantity,new SLabel(getPropertyName("ss/cc"), 120),ss_cc));
//			form.addComponent(quantity);
//			form.addComponent(ss_cc);
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("packages"), 120),packages,new SLabel(getPropertyName("quality"), 120),quality,new SLabel(getPropertyName("received_sound"), 120),received_sound));
//			form.addComponent(quality);
//			form.addComponent(received_sound);
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("damage"), 120), damage, new SLabel(getPropertyName("empty"), 120),empty, new SLabel(getPropertyName("short"), 120),shorte));
			form.addComponent(new SHorizontalLayout(true, new SLabel(getPropertyName("status"), 120),statusCombo));
			form.addComponent(new SHTMLLabel(null, "<h2><u>"+getPropertyName("expenses")+"</u></h2>"));
//			form.addComponent(empty);
//			form.addComponent(shorte);
			
			form.addComponent(new SHorizontalLayout(true, new SFormLayout(gross_sale,less_expense,net_sale,supplierAmount,details),new SFormLayout(freight,airport_charges,waste,dpa_charges,pickup_charge
					,unloading_charge,storage_charge,port,auction,new SHorizontalLayout(commissionPercentage,commission) )));
//			form.addComponent(less_expense);
//			form.addComponent(net_sale);
//			form.addComponent(airport_charges);
//			form.addComponent(waste);
//			form.addComponent(dpa_charges);
//			form.addComponent(pickup_charge);
//			form.addComponent(unloading_charge);
//			form.addComponent(storage_charge);
//			form.addComponent(port);
//			form.addComponent(auction);
//			form.addComponent(commission);
//			form.addComponent(details);

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
			
			
			ClickListener confirmListener=new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					if(event.getButton().getId().equals("1")) {
						try {
							saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)commissionSalesCombo.getValue(),confirmBox.getUserID());
							Notification.show("Success",
									"Session Saved Successfully..!",
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
						if(commissionSalesCombo.getValue()!=null && !commissionSalesCombo.getValue().toString().equals("0")) {
							saveSessionActivity(getOptionId(), (Long)commissionSalesCombo.getValue(),
									"Commission Sales : No. "+commissionSalesCombo.getItemCaption(commissionSalesCombo.getValue()));
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
						if(commissionSalesCombo.getValue()!=null && !commissionSalesCombo.getValue().toString().equals("0")) {
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			};
			
			windowNotif.setClickListener(clickListnr);
			
			createNewButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					commissionSalesCombo.setValue((long)0);
				}
			});
			
			
			save.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
		        		if(commissionSalesCombo.getValue()==null || commissionSalesCombo.getValue().toString().equals("0")){
		        			
		        			if(isValid()){
		        				
		        				CommissionSalesModel objModel=new CommissionSalesModel();
		        				objModel.setNumber(getNextSequence("Commission Sales Number", getLoginID()));
		        				objModel.setVesel(vesel.getValue());
			        			objModel.setSupplier(new LedgerModel((Long) supplier.getValue()));
			        			objModel.setReceived_date(CommonUtil.getSQLDateFromUtilDate(received_date.getValue()));
			        			objModel.setIssue_date(CommonUtil.getSQLDateFromUtilDate(issue_date.getValue()));
			        			objModel.setContr_no(contr_no.getValue());
			        			objModel.setConsignment_mark(consignment_mark.getValue());
			        			objModel.setQuantity(quantity.getValue());
			        			objModel.setSs_cc(ss_cc.getValue());
			        			objModel.setPackages(packages.getValue());
			        			objModel.setQuality(quality.getValue());
			        			objModel.setReceived_sound(received_sound.getValue());
			        			objModel.setDamage(damage.getValue());
			        			objModel.setEmpty(empty.getValue());
			        			objModel.setShorte(shorte.getValue());
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

			        			
			        			objModel.setOffice(new S_OfficeModel(getOfficeID()));
			        			objModel.setStatus((Long) statusCombo.getValue());
			        			
			        			FinTransaction trans = new FinTransaction();
								double supplierAmt=toDouble(supplierAmount.getValue());
								
								if (supplierAmt !=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											objModel.getSupplier().getId(),
											settings.getPURCHASE_ACCOUNT(),
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
									id=objDao.save(objModel, trans.getTransaction(SConstants.COMMISSION_PURCHASE, CommonUtil.getSQLDateFromUtilDate(received_date.getValue())));
									loadOptions(id);
									Notification.show(getPropertyName("Success"), getPropertyName("save_success"),
					                        Type.WARNING_MESSAGE);
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
					                        Type.ERROR_MESSAGE);
									e.printStackTrace();
								}
		        			}
		        		}
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
	        	}
	        	
	        });
			
			
			commissionSalesCombo.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
						
					try {
						removeErrors();
						
						if (commissionSalesCombo.getValue() != null
								&&  !commissionSalesCombo.getValue().toString().equals("0")) {
							
							save.setVisible(false);
							edit.setVisible(true);
							printButton.setVisible(true);
							delete.setVisible(true);
							update.setVisible(false);
							cancel.setVisible(false);

							CommissionSalesModel objModel = objDao
									.getCommissionSales((Long) commissionSalesCombo
											.getValue());
							setWritableAll();
							
							supplier.setValue(objModel.getSupplier().getId());
							received_date.setValue(objModel.getReceived_date());
							issue_date.setValue(objModel.getIssue_date());
							vesel.setValue(objModel.getVesel());
							contr_no.setValue(objModel.getContr_no());
							consignment_mark.setValue(objModel.getConsignment_mark());
							quantity.setValue(objModel.getQuantity());
							ss_cc.setValue(objModel.getSs_cc());
							packages.setValue(objModel.getPackages());
							quality.setValue(objModel.getQuality());
							received_sound.setValue(objModel.getReceived_sound());
							damage.setValue(objModel.getDamage());
							empty.setValue(objModel.getEmpty());
							shorte.setValue(objModel.getShorte());
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
							
							vesel.setValue(objModel.getVesel());
							statusCombo.setValue(objModel.getStatus());
							setReadOnlyAll();

						} else {
							save.setVisible(true);
							edit.setVisible(false);
							printButton.setVisible(false);
							delete.setVisible(false);
							update.setVisible(false);
							cancel.setVisible(false);

							setWritableAll();
							
							supplier.setValue(null);
							received_date.setValue(getWorkingDate());
							issue_date.setValue(getWorkingDate());
							vesel.setValue("");
							contr_no.setValue("");
							consignment_mark.setValue("");
							quantity.setValue("0");
							ss_cc.setValue("");
							packages.setValue("");
							quality.setValue("");
							received_sound.setValue("");
							damage.setValue("");
							empty.setValue("");
							shorte.setValue("");
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
							vesel.setValue("");
							statusCombo.setValue((long)1);

						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			ValueChangeListener listnr=new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
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
			
	        edit.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			edit.setVisible(false);
	        			printButton.setVisible(false);
	        			delete.setVisible(false);
	        			update.setVisible(true);
	        			cancel.setVisible(true);
	        			setWritableAll();
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
	        
	        cancel.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			edit.setVisible(false);
	        			printButton.setVisible(false);
	        			delete.setVisible(false);
	        			update.setVisible(false);
	        			cancel.setVisible(false);
	        			loadOptions(Long.parseLong(commissionSalesCombo.getValue().toString()));
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
	        
	        
	        delete.addClickListener(new Button.ClickListener(){
	        	public void buttonClick(ClickEvent event){
	        		try {
	        			
	        			ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
	        			        new ConfirmDialog.Listener() {
	        			            public void onClose(ConfirmDialog dialog) {
	        			                if (dialog.isConfirmed()) {
	        			                	
	        			                	try {
	        			                		id=(Long)commissionSalesCombo.getValue();
												objDao.delete(id);
												
												Notification.show(getPropertyName("Success"), getPropertyName("deleted_success"),
		        				                        Type.WARNING_MESSAGE);
												
												loadOptions(0);
												
											} catch (Exception e) {
												// TODO Auto-generated catch block
												Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
								                        Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
	        			                } 
	        			            }
	        			        });
	        			
	        			
		        		
	        		} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        });
	        
	        
			update.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						if (commissionSalesCombo.getValue() != null) {

							if (isValid()) {
								
								CommissionSalesModel objModel = objDao
										.getCommissionSales((Long) commissionSalesCombo.getValue());
								
								objModel.setVesel(vesel.getValue());
			        			objModel.setSupplier(new LedgerModel((Long) supplier.getValue()));
			        			objModel.setReceived_date(CommonUtil.getSQLDateFromUtilDate(received_date.getValue()));
			        			objModel.setIssue_date(CommonUtil.getSQLDateFromUtilDate(issue_date.getValue()));
			        			objModel.setContr_no(contr_no.getValue());
			        			objModel.setConsignment_mark(consignment_mark.getValue());
			        			objModel.setQuantity(quantity.getValue());
			        			objModel.setSs_cc(ss_cc.getValue());
			        			objModel.setPackages(packages.getValue());
			        			objModel.setQuality(quality.getValue());
			        			objModel.setReceived_sound(received_sound.getValue());
			        			objModel.setDamage(damage.getValue());
			        			objModel.setEmpty(empty.getValue());
			        			objModel.setShorte(shorte.getValue());
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
			        			
			        			
			        			FinTransaction trans = new FinTransaction();
								double supplierAmt=toDouble(supplierAmount.getValue());
								
								if (supplierAmt !=0) {
									trans.addTransactionWithNarration(SConstants.CR,
											objModel.getSupplier().getId(),
											settings.getPURCHASE_ACCOUNT(),
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
								
								TransactionModel transObj=trans.getTransactionWithoutID(SConstants.COMMISSION_PURCHASE, CommonUtil.getSQLDateFromUtilDate(received_date.getValue()));
								transObj.setTransaction_id(objModel.getTransaction_id());
								try {
									objDao.update(objModel, transObj);
									loadOptions(objModel.getId());
									Notification.show(getPropertyName("success"), getPropertyName("save_success"),
					                        Type.WARNING_MESSAGE);
								} catch (Exception e) {
									Notification.show(getPropertyName("error"), getPropertyName("issue_occured")+e.getCause(),
					                        Type.ERROR_MESSAGE);
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
			
			
			printButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {
						System.out.println("Option :"
								+ commissionSalesCombo.getValue());
						if (commissionSalesCombo.getValue() != null) {

							if (isValid()) {

								CommissionSalesModel objModel = objDao
										.getCommissionSales((Long) commissionSalesCombo
												.getValue());

								HashMap<String, Object> map = new HashMap<String, Object>();
								
								map.put("supplier", objModel.getSupplier().getName());
								map.put("received_date", CommonUtil.getUtilDateFromSQLDate(objModel.getReceived_date()));
								map.put("issue_date", CommonUtil.getUtilDateFromSQLDate(objModel.getIssue_date()));
								map.put("vessel", objModel.getVesel());
								map.put("contr_no", objModel.getContr_no());
								map.put("consignment_mark", objModel.getConsignment_mark());
								map.put("quantity", objModel.getQuantity());
								map.put("ss_cc", objModel.getSs_cc());
								map.put("packages", objModel.getPackages());
								map.put("quality", objModel.getQuality());
								map.put("received_sound", objModel.getReceived_sound());
								map.put("damage", objModel.getDamage());
								map.put("empty", objModel.getEmpty());
								map.put("shorte", objModel.getShorte());
								map.put("gross_sale", objModel.getGross_sale());
								map.put("less_expense", objModel.getLess_expense());
								map.put("net_sale", objModel.getNet_sale());
								map.put("freight", objModel.getFreight());
								map.put("airport_charges", objModel.getAirport_charges());
								map.put("waste", objModel.getWaste());
								map.put("dpa_charges", objModel.getDpa_charges());
								map.put("pickup_charge", objModel.getPickup_charge());
								map.put("unloading_charge", objModel.getUnloading_charge());
								map.put("storage_charge", objModel.getStorage_charge());
								map.put("port", objModel.getPort());
								map.put("auction", objModel.getAuction());
								map.put("commission", objModel.getCommission());
								map.put("details", objModel.getDetails());
								map.put("NUMBER", objModel.getNumber());
								
								map.put("OFFICE_NAME", getOfficeName());
								
								
								Report report = new Report(getLoginID());
								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setJrxmlFileName(getBillName(SConstants.bills.COMMISSION_SALES));
								report.setReportFileName("CommissionSales_Print");
								// report.setReportTitle("Sales Invoice");
								report.setOfficeName(getOfficeName());
								report.setReportType(Report.PDF);
								List lst=new ArrayList();
								lst.add(new ReportBean());
								report.createReport(lst, map);

								report.print();
//								map.clear();
							}
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			});
			
			

		} catch (Exception e) {
		}

		return pannel;
	}




	public void setReadOnlyAll(){
		vesel.setReadOnly(true);
	    statusCombo.setReadOnly(true);
		supplier.setReadOnly(true);
		received_date.setReadOnly(true);
		issue_date.setReadOnly(true);
		vesel.setReadOnly(true);
		contr_no.setReadOnly(true);
		consignment_mark.setReadOnly(true);
		quantity.setReadOnly(true);
		ss_cc.setReadOnly(true);
		packages.setReadOnly(true);
		quality.setReadOnly(true);
		received_sound.setReadOnly(true);
		damage.setReadOnly(true);
		empty.setReadOnly(true);
		shorte.setReadOnly(true);
		gross_sale.setReadOnly(true);
		less_expense.setReadOnly(true);
		net_sale.setReadOnly(true);
		freight.setReadOnly(true);
		airport_charges.setReadOnly(true);
		waste.setReadOnly(true);
		dpa_charges.setReadOnly(true);
		pickup_charge.setReadOnly(true);
		unloading_charge.setReadOnly(true);
		storage_charge.setReadOnly(true);
		port.setReadOnly(true);
		auction.setReadOnly(true);
		commission.setReadOnly(true);
		details.setReadOnly(true);
	    vesel.focus();
	    commissionPercentage.setReadOnly(true);
	    supplierAmount.setReadOnly(true);
	}
	
	public void setWritableAll(){
		vesel.setReadOnly(false);
		statusCombo.setReadOnly(false);
		supplier.setReadOnly(false);
		received_date.setReadOnly(false);
		issue_date.setReadOnly(false);
		vesel.setReadOnly(false);
		contr_no.setReadOnly(false);
		consignment_mark.setReadOnly(false);
		quantity.setReadOnly(false);
		ss_cc.setReadOnly(false);
		packages.setReadOnly(false);
		quality.setReadOnly(false);
		received_sound.setReadOnly(false);
		damage.setReadOnly(false);
		empty.setReadOnly(false);
		shorte.setReadOnly(false);
		gross_sale.setReadOnly(false);
		freight.setReadOnly(false);
		airport_charges.setReadOnly(false);
		waste.setReadOnly(false);
		dpa_charges.setReadOnly(false);
		pickup_charge.setReadOnly(false);
		unloading_charge.setReadOnly(false);
		storage_charge.setReadOnly(false);
		port.setReadOnly(false);
		auction.setReadOnly(false);
		details.setReadOnly(false);
		commissionPercentage.setReadOnly(false);
	}
	
	
	public void loadOptions(long id){
		List testList;
		try {
			list = objDao.getAllActiveSalesNames(getOfficeID());
			
			CommissionSalesModel sop=new CommissionSalesModel();
	        sop.setId(0);
	        sop.setContr_no("----------- Create New ----------");
	        if(list==null)
	        	list=new ArrayList();
	        list.add(0, sop);
	        
		    bic=CollectionContainer.fromBeans(list, "id");
		    commissionSalesCombo.setContainerDataSource(bic);
		    commissionSalesCombo.setItemCaptionPropertyId("contr_no");
		
		    commissionSalesCombo.setValue(id);
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}






	@Override
	public Boolean isValid() {

		boolean ret=true;
		
		if(statusCombo.getValue()==null || statusCombo.getValue().equals("")){
			setRequiredError(statusCombo, getPropertyName("invalid_selection"),true);
			statusCombo.focus();
			ret=false;
		}
		else
			setRequiredError(statusCombo, null,false);

		if(received_date.getValue()==null || received_date.getValue().equals("")){
			setRequiredError(received_date, getPropertyName("invalid_selection"),true);
			received_date.focus();
			ret=false;
		}
		else
			setRequiredError(received_date, null,false);

		if(issue_date.getValue()==null || issue_date.getValue().equals("")){
			setRequiredError(issue_date, getPropertyName("invalid_selection"),true);
			issue_date.focus();
			ret=false;
		}
		else
			setRequiredError(issue_date, null,false);
		
		if(supplier.getValue()==null || supplier.getValue().equals("")){
			setRequiredError(supplier, getPropertyName("invalid_selection"),true);
			supplier.focus();
			ret=false;
		}
		else
			setRequiredError(supplier, null,false);
		
		if(quantity.getValue()==null || quantity.getValue().equals("")){
			setRequiredError(quantity, getPropertyName("invalid_data"),true);
			quantity.focus();
			ret=false;
		}
		else {
			try {
				if(toDouble(quantity.getValue())<0) {
					setRequiredError(quantity, getPropertyName("invalid_data"),true);
					quantity.focus();
					ret=false;
				}
				else
					setRequiredError(quantity, null,false);
			} catch (Exception e) {
				setRequiredError(quantity, getPropertyName("invalid_data"),true);
				quantity.focus();
				ret=false;
				// TODO: handle exception
			}
		}
		
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
				// TODO: handle exception
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
		
		return ret;
	}
	
	public void removeErrors() {
		
		
		supplier.setComponentError(null);
		received_date.setComponentError(null);
		issue_date.setComponentError(null);
		vesel.setComponentError(null);
		contr_no.setComponentError(null);
		consignment_mark.setComponentError(null);
		quantity.setComponentError(null);
		ss_cc.setComponentError(null);
		packages.setComponentError(null);
		quality.setComponentError(null);
		received_sound.setComponentError(null);
		damage.setComponentError(null);
		empty.setComponentError(null);
		shorte.setComponentError(null);
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
		statusCombo.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}




	public SComboField getCommissionSalesCombo() {
		return commissionSalesCombo;
	}




	public void setCommissionSalesCombo(SComboField commissionSalesCombo) {
		this.commissionSalesCombo = commissionSalesCombo;
	}
	
	@Override
	public SComboField getBillNoFiled() {
		return commissionSalesCombo;
	}

}
*/