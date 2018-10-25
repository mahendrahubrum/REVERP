package com.webspark.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.test.dao.SalesStockUpdateDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Jan 23 2014
 */
public class PurchaseStockUpdateUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField customerComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;
	
	LedgerDao ledDao;
	
	SalesStockUpdateDao daoObj;

	@Override
	public SPanel getGUI() {
		
		ledDao=new LedgerDao();
		
		daoObj=new SalesStockUpdateDao();
		
		customerId = 0;
		report = new Report(getLoginID());

		setSize(400, 300);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);
		mainFormLayout.setMargin(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);
		
		fromDateField = new SDateField("From Date");
		fromDateField.setValue(getWorkingDate());
		toDateField = new SDateField("To Date");
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		
		try {
			
			organizationComboField=new SComboField("Organization :",200, new OrganizationDao().getAllOrganizations(),"id", "name");
			officeComboField = new SComboField("Office", 200);
			
			customerComboField = new SComboField("Customer", 200, null,
					"id", "name", false, "ALL");
			
			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);
			
			mainFormLayout.addComponent(dateHorizontalLayout);
			
			mainFormLayout.addComponent(customerComboField);

			reportChoiceField = new SReportChoiceField("Export To");
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton("Update");
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);

			mainPanel.setContent(mainFormLayout);
			
			
			
			
			
			organizationComboField.addValueChangeListener(new Property.ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) {
					
	            	try {
	            		
	            		SCollectionContainer bic=SCollectionContainer.setList(new OfficeDao().
	            				getAllOfficeNamesUnderOrg((Long) organizationComboField.getValue()) , "id");
	            		officeComboField.setContainerDataSource(bic);
	            		officeComboField.setItemCaptionPropertyId("name");
	            		
	            		
	            		Iterator it=officeComboField.getItemIds().iterator();
	        		    if(it.hasNext())
	        		    	officeComboField.setValue(it.next());
	            	} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
			

			customerComboField
					.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							customerId = 0;
							if (customerComboField.getValue() != null
									&& !customerComboField.getValue()
											.toString().equals("0")) {
								customerId = toLong(customerComboField
										.getValue().toString());
							}
						}
					});

			officeComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCustomerCombo(toLong(officeComboField.getValue()
							.toString()));
				}
			});
			
			
			
			if(isSystemAdmin() || isSuperAdmin()) {
	        	organizationComboField.setEnabled(true);
	        	officeComboField.setEnabled(true);
	        }
	        else {
	        	organizationComboField.setEnabled(false);
	        	if(isOrganizationAdmin()) {
	        		officeComboField.setEnabled(true);
	        	}
	        	else {
	        		officeComboField.setEnabled(false);
	        	}
	        }
	        
	        organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			
			
			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					try {

						long custId = 0;

						if (customerComboField.getValue() != null
								&& !customerComboField.getValue().equals("")) {
							custId = toLong(customerComboField.getValue()
									.toString());
						}
						
						List<Long> purchIds = daoObj
								.getAllPurchases(CommonUtil
										.getSQLDateFromUtilDate(fromDateField
												.getValue()), CommonUtil
										.getSQLDateFromUtilDate(toDateField
												.getValue()),
										toLong(officeComboField.getValue()
												.toString()));

						if (purchIds!=null&&purchIds.size()>0) {
							for (Long salesId : purchIds) {
								daoObj.programeticalPurchaseUpdate(salesId);
							}
							SNotification.show("Operation Completed",
									Type.WARNING_MESSAGE);
						} else {
							SNotification.show("No data available",
									Type.WARNING_MESSAGE);
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

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomers(officeId);
			} else {
				custList = ledDao.getAllCustomers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName("---------------------ALL-------------------");
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			customerComboField.setContainerDataSource(custContainer);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);
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
