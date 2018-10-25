package com.inventory.reports.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.inventory.purchase.ui.PurchaseOrderUI;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.inventory.subscription.dao.SubscriptionInDao;
import com.inventory.subscription.model.SubscriptionInModel;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.uac.dao.EmployeeDocumentDao;
import com.webspark.uac.dao.EmployeeDocumentMapDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.EmployeeDocumentCategoryModel;
import com.webspark.uac.model.EmployeeDocumentModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 19, 2014
 */
public class AlertPanel extends SContainerPanel{

	private static final long serialVersionUID = -2968338850905189644L;
	boolean popupEnable = false;

	public AlertPanel() {

		try {
			long office_id = getOfficeID();

			SGridLayout grid = new SGridLayout();
			grid.setColumns(2);
			grid.setRows(10);
			grid.setSpacing(true);
			grid.setMargin(true);
			/*Component cmp1 = getCustomerDetails(office_id);
			if (cmp1 != null){
//				if(popupEnable)
					grid.addComponent(cmp1);
			}

			Component cmp2 = getItemDetails(office_id);
			if (cmp2 != null){
//				if(popupEnable)
					grid.addComponent(cmp2);
			}
				

			Component cmp3 = getSalesDetailsDetails(office_id);
			if (cmp3 != null){
//				if(popupEnable)
					grid.addComponent(cmp3);
			}
				
			
			Component cmp4 = getClosedSubscriptions(office_id);
			if (cmp4 != null){
//				if(popupEnable)
					grid.addComponent(cmp4);
			}*/
				
			
			Component cmp5 = getValidityExpiryAlert(getOrganizationID());
			if (cmp5 != null){
					grid.addComponent(cmp5);
			}
			
			if(getSettings().isMANUFACTURING_DATES_ENABLE()){
				Component cmp6 = getItemExpiryDetails(getOrganizationID());
				if (cmp6 != null) {
					grid.addComponent(cmp6);
				}
			}
			
			if(getSettings().isPURCHSE_ORDER_EXPIRY_ENABLED()){
				Component cmp7 = getPurchaseOrderExpiryAlert(getOfficeID());
				if (cmp7 != null) {
					grid.addComponent(cmp7);
				}
			}
			
			if (popupEnable)
				setContent(grid);
			else
				setContent(null);

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}

	}

	
	public Component getItemDetails(long office_id) {
		STable table = new STable(getPropertyName("item_under_reorder_level"));
		try {

			table.addContainerProperty("#", Integer.class, null, "#", null,Align.CENTER);
			table.addContainerProperty("Item Name", String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty("Reorder_Level", Double.class, null,
					getPropertyName("reorder_level"), null, Align.CENTER);
			table.addContainerProperty("Current Balance", Double.class, null,
					getPropertyName("current_balance"), null, Align.RIGHT);
			table.setVisibleColumns(new String[] { "#", "Item Name",
					"Credit Limit", "Current Balance" });

			List list = new ItemDao().getItemsUnderReorderLevel(office_id);

			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;

			Iterator it = list.iterator();
			int i = 0;
			ItemModel objIn;
			while (it.hasNext()) {
				objIn = (ItemModel) it.next();
				i++;
				table.addItem(
						new Object[] { i, objIn.getName(),
								objIn.getReorder_level(),
								objIn.getCurrent_balalnce() }, i);
			}

			table.setColumnExpandRatio("Current Balance", (float) 0.3);

			table.setWidth("400");
			table.setHeight("200");

			table.setSelectable(true);
			table.setMultiSelect(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}

	
	public Component getCustomerDetails(long office_id) {
		STable table = new STable(getPropertyName("customers_under_credit_limit"));
		try {

			table.addContainerProperty("#", Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty("Customer Name", String.class, null,
					getPropertyName("customer_name"), null, Align.LEFT);
			table.addContainerProperty("Credit Limit", Double.class, null,
					getPropertyName("credit_limit"), null, Align.CENTER);
			table.addContainerProperty("Current Balance", Double.class, null,
					getPropertyName("current_balance"), null, Align.RIGHT);
			table.addContainerProperty("Contact", String.class, null,
					getPropertyName("contact"), null, Align.LEFT);

			table.setVisibleColumns(new String[] { "#", "Customer Name",
					"Credit Limit", "Current Balance", "Contact" });

			List list = new CustomerDao().getCustomersCreditDetails(office_id);

			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;

			Iterator it = list.iterator();
			int i = 0;
			CustomerModel obj;
			while (it.hasNext()) {
				obj = (CustomerModel) it.next();
				i++;
				table.addItem(
						new Object[] {
								i,
								obj.getName(),
								obj.getCredit_limit(),
								obj.getLedger().getCurrent_balance(),
								obj.getAddress().getPhone()
										+ "   "
										+ obj.getAddress()
												.getMobile() }, i);
			}

			table.setColumnExpandRatio("Current Balance", (float) 0.3);

			table.setWidth("400");
			table.setHeight("200");

			table.setSelectable(true);
			table.setMultiSelect(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}

	
	public Component getSalesDetailsDetails(long office_id) {
		STable table = new STable(getPropertyName("customers_exceedes_credit_period"));
		try {

			table.addContainerProperty("#", Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty("Customer Name", String.class, null,
					getPropertyName("customer_name"), null, Align.LEFT);
			table.addContainerProperty("Total Amount", Double.class, null,
					getPropertyName("total_amount"), null, Align.CENTER);
			table.addContainerProperty("Balance Amount", Double.class, null,
					getPropertyName("balance_amount"), null, Align.RIGHT);
			table.addContainerProperty("Sales No.", String.class, null,
					getPropertyName("sales_no"), null, Align.LEFT);

			table.setVisibleColumns(new String[] { "#", "Customer Name",
					"Total Amount", "Balance Amount", "Sales No." });

			List list = new CustomerDao().getAllActiveCustomers(office_id);

			if (list.size() <= 0)
				return null;
			 else
				 popupEnable=true;

			Iterator it = list.iterator();
			int i = 0;
			CustomerModel obj;
			Calendar cal;
			List list2;
			String salesNos = " ";
			double payed = 0, total = 0;
			Iterator it2;
			SalesModel salObj;
			while (it.hasNext()) {

				obj = (CustomerModel) it.next();

				cal = Calendar.getInstance();
				cal.setTime(new java.util.Date());

				cal.add(Calendar.DAY_OF_MONTH, -obj.getMax_credit_period());

				list2 = new SalesDao().getAllSalesDetailsForCustomer(obj
						.getLedger().getId(), getFinStartDate(), new Date(cal
						.getTime().getTime()));
				if (list2.size() > 0) {
					salesNos = " ";
					payed = 0;
					total = 0;
					it2 = list2.iterator();
					while (it2.hasNext()) {
						salObj = (SalesModel) it2.next();

						salesNos += " " + salObj.getSales_number() + " ,";
						payed += salObj.getPayment_amount();
						total += salObj.getAmount();

					}

					i++;
					table.addItem(
							new Object[] {
									i,
									obj.getName(),
									total,
									total - payed,
									salesNos.substring(0, salesNos.length() - 1) },
							i);

				}

			}

			table.setColumnExpandRatio("Current Balance", (float) 0.3);

			table.setWidth("400");
			table.setHeight("200");

			table.setSelectable(true);
			table.setMultiSelect(true);
		} 
		catch (Exception e) {
			// TODO: handle exception
		}

		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Component getClosedSubscriptions(long office_id) {
		STable table = new STable(getPropertyName("rentals_pending_return"));
		try {
			SubscriptionInModel mdl=null;
			table.addContainerProperty("#", Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty("Customer Name", String.class, null,
					"Customer Name", null, Align.LEFT);
			table.addContainerProperty("Rental Item", String.class, null,
					"Rental Item", null, Align.CENTER);
			table.addContainerProperty("Rental Date", String.class, null,
					"Rental Date", null, Align.RIGHT);
			table.addContainerProperty("Expected Return", String.class, null,
					"Expected Return", null, Align.LEFT);

			table.setVisibleColumns(new Object[] { "#", "Customer Name",
					"Rental Item", "Rental Date", "Expected Return" });

			List list = new SubscriptionInDao().getAllInSubscriptionsExceedingClosing(office_id, getWorkingDate());
			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;
			if(list.size()>0){
				Iterator it = list.iterator();
				while (it.hasNext()) {
					mdl = (SubscriptionInModel) it.next();
					table.addItem(
							new Object[] {
									table.getItemIds().size()+1,
									new LedgerDao().getLedgerNameFromID(mdl.getSubscriber()),
									mdl.getSubscription().getName(),
									CommonUtil.formatDateToDDMMYYYY(mdl.getSubscription_date()),
									CommonUtil.formatDateToDDMMYYYY(mdl.getClosing_date())}, table.getItemIds().size()+1);
				}
			}
			table.setWidth("400");
			table.setHeight("200");
			table.setSelectable(true);
			table.setMultiSelect(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Component getValidityExpiryAlert(long orgid) {
		STable table = new STable(getPropertyName("validity_expire"));
		List list=null;
		try {
			EmployeeDocumentCategoryModel edcmdl=null;
			EmployeeDocumentModel mdl;
			list=new ArrayList();
			table.addContainerProperty("#", Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty("Employee Name", String.class, null,
					"Employee Name", null, Align.LEFT);
			table.addContainerProperty("Document", String.class, null,
					"Document", null, Align.CENTER);
			table.addContainerProperty("Expiry Date", String.class, null,
					"Expiry Date", null, Align.RIGHT);
			table.setVisibleColumns(new Object[] { "#", "Employee Name", "Document", "Expiry Date"});
			List catList=new EmployeeDocumentMapDao().getDocumentCategoryList(orgid);
			Iterator catitr=catList.iterator();
			while(catitr.hasNext()){
				edcmdl=(EmployeeDocumentCategoryModel)catitr.next();
				list.addAll(new EmployeeDocumentMapDao().
						getExpiryList(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()), 
								CommonUtil.getSQLDateFromUtilDate(getAfterDate(edcmdl.getAlert_before())),edcmdl.getId()));
			}
			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;
			if(list.size()>0){
				Iterator it = list.iterator();
				while (it.hasNext()) {
					mdl = (EmployeeDocumentModel) it.next();
					UserModel user=new UserManagementDao().getUser(mdl.getEmployee_id());
					if(user.getOffice().getId()==getOfficeID()){
						table.addItem(
								new Object[] {
										table.getItemIds().size()+1,
										user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name(),
										new EmployeeDocumentDao().getDocumentName(mdl.getDocument().getId()),
										CommonUtil.formatDateToDDMMYYYY(mdl.getExpiry())}, table.getItemIds().size()+1);
					}
					else{
						continue;
					}
					
				}
			}
			table.setWidth("400");
			table.setHeight("200");
			table.setSelectable(true);
			table.setMultiSelect(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}
	
	
	@SuppressWarnings({ "rawtypes", "serial" })
	public Component getPurchaseOrderExpiryAlert(long officeId) {
		final STable table = new STable(getPropertyName("purchase_order_expiry"));
		try {
			List list=new ArrayList();
			table.addContainerProperty("#", Integer.class, null, "#", null, Align.CENTER);
			table.addContainerProperty("Id", Long.class, null, "Id", null, Align.CENTER);
			table.addContainerProperty("Supplier", String.class, null, getPropertyName("supplier"), null, Align.LEFT);
			table.addContainerProperty("Expiry Date", String.class, null, getPropertyName("expiry_date"), null, Align.LEFT);
			table.addContainerProperty("Amount", String.class, null, getPropertyName("amount"), null, Align.RIGHT);
			
			table.setVisibleColumns(new Object[] { "#", "Supplier", "Expiry Date", "Amount" });
			list=new PurchaseOrderDao().getPurchaseOrderExpiry(CommonUtil.getSQLDateFromUtilDate(getWorkingDate()),
																CommonUtil.getSQLDateFromUtilDate(getAfterDate(5)),
																getOfficeID());
			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;
			table.setVisibleColumns(new Object[] { "#", "Id", "Supplier", "Expiry Date", "Amount" });
			if(list.size()>0){
				Iterator it = list.iterator();
				while (it.hasNext()) {
					PurchaseOrderModel mdl = (PurchaseOrderModel) it.next();
					table.addItem(new Object[]{	table.getItemIds().size()+1,
												mdl.getId(),
												mdl.getSupplier().getName(),
												CommonUtil.formatDateToDDMMYYYY(mdl.getExpiryDate()),
												roundNumber(mdl.getAmount())+" "+new CurrencyManagementDao().getselecteditem(mdl.getCurrencyId()).getCode()},table.getItemIds().size()+1);
				}
			}
			table.setVisibleColumns(new Object[] { "#", "Supplier", "Expiry Date", "Amount" });
			table.setWidth("400");
			table.setHeight("200");
			table.setSelectable(true);
			table.setMultiSelect(false);
			
			final Action actionEdit = new Action(getPropertyName("edit"));
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					
				}
			};
			
			table.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							PurchaseOrderUI sales = new PurchaseOrderUI();
							sales.setCaption(getPropertyName("purchase_order"));
							sales.loadOptions((Long) item.getItemProperty("Id").getValue());
							sales.center();
							getUI().getCurrent().addWindow(sales);
							sales.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionEdit };
				}
			});
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}
	
	
	public Component getItemExpiryDetails(long office_id) {
		STable table = new STable(getPropertyName("expired_item"));
		try {

			table.addContainerProperty("#", Integer.class, null, "#", null,
					Align.CENTER);
			table.addContainerProperty("Item Name", String.class, null,
					getPropertyName("item_name"), null, Align.LEFT);
			table.addContainerProperty("Stock", String.class, null,
					getPropertyName("stock"), null, Align.CENTER);
			table.addContainerProperty("Quantity", Double.class, null,
					getPropertyName("balance"), null, Align.CENTER);
			table.addContainerProperty("Manufacture Date", String.class, null,
					getPropertyName("manuf_date"), null, Align.RIGHT);
			table.addContainerProperty("Expiry Date", String.class, null,
					getPropertyName("expiry_date"), null, Align.RIGHT);
			table.setVisibleColumns(new String[] { "#", "Item Name",
					"Stock", "Quantity","Manufacture Date" ,"Expiry Date" });

			List list = new ItemDao().getExpiredItems(office_id,getWorkingDate());

			if (list.size() <= 0)
				return null;
			else
				popupEnable = true;

			Iterator it = list.iterator();
			int i = 0;
			ItemStockModel objIn;
			while (it.hasNext()) {
				objIn = (ItemStockModel) it.next();
				i++;
				table.addItem(
						new Object[] { i, objIn.getItem().getName(),"ID: "+objIn.getId(),
								objIn.getBalance(),CommonUtil.formatDateToCommonDateTimeFormat(CommonUtil.getUtilFromSQLDate(objIn.getManufacturing_date())),
								CommonUtil.formatDateToCommonDateTimeFormat(CommonUtil.getUtilFromSQLDate(objIn.getExpiry_date())) }, i);
			}

			table.setColumnExpandRatio("Item Name", (float) 1);
			table.setColumnExpandRatio("Quantity", (float) 0.8);
			table.setColumnExpandRatio("Manufacture Date", (float) 1);
			table.setColumnExpandRatio("Expiry Date", (float) 1);

			table.setWidth("400");
			table.setHeight("200");

			table.setSelectable(true);
			table.setMultiSelect(true);
		} catch (Exception e) {
		}
		if(table.getItemIds().size()>0)
			return table;
		else
			return null;
	}
	
	
	public java.util.Date getAfterDate(int days){
		java.util.Calendar cal = null;
		cal=java.util.Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		return (java.util.Date) cal.getTime();
	}
	
}
