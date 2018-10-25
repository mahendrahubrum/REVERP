package com.inventory.purchase.ui;

import java.util.Iterator;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.purchase.dao.StockRateUpdateDao;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Jinshad P.T.
 * 
 *         WebSpark.
 * 
 *         Jun 17, 2014
 */
public class StockRateUpdateUI extends SparkLogic {

	private static final long serialVersionUID = 4371648459776840153L;

	SComboField itemSelect, stockSelect;
	SNativeSelect unitSelect;

	SCollectionContainer bic;
	WrappedSession session;
	SettingsValuePojo settings;

	STextField txtRate;

	@Override
	public SPanel getGUI() {

		setSize(400, 300);

		List testList = null;
		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		txtRate = new STextField(getPropertyName("rate"), 250);
		txtRate.setValue("0");

		final SButton save = new SButton(getPropertyName("save_new_rate"));

		try {

			itemSelect = new SComboField(getPropertyName("item"), 250,
					new ItemDao().getAllActiveItems(getOfficeID()), "id",
					"name", false, "Select");
			unitSelect = new SNativeSelect(getPropertyName("unit"), 80,
					new UnitDao().getAllActiveUnits(getOrganizationID()), "id",
					"symbol");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		stockSelect = new SComboField(getPropertyName("stock"), 250);
		stockSelect.setInputPrompt("---------Select---------");
		SPanel pan = new SPanel();
		pan.setSizeFull();

		SFormLayout lay = new SFormLayout();
		lay.setMargin(true);

		lay.addComponent(itemSelect);
		lay.addComponent(stockSelect);
		lay.addComponent(unitSelect);
		lay.addComponent(txtRate);
		lay.addComponent(save);

		pan.setContent(lay);

		pan.addShortcutListener(new ShortcutListener("Save Item",
				ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				save.click();
			}
		});

		save.addClickListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {

//				if (isValid()) {
					try {
//						StockRateModel obj = new StockRateModel();
//						obj.setStock_id((Long) stockSelect.getValue());
//						obj.setUnit_id((Long) unitSelect.getValue());
//						obj.setRate(toDouble(txtRate.getValue()));
//						new StockRateUpdateDao().updateStockSalesRate(obj);
//						Notification.show(getPropertyName("save_success"),
//								Type.WARNING_MESSAGE);
						
						SalesDao saleDao=new SalesDao();
					List salesList=	saleDao.getAllSalesModelUnderOffice(getOfficeID());
					
					Iterator iter=salesList.iterator();
					while (iter.hasNext()) {
						SalesModel mdl= (SalesModel) iter.next();
						double netDiscount=0;
						Iterator it = mdl.getInventory_details_list().iterator();
						while (it.hasNext()) {
							SalesInventoryDetailsModel det=(SalesInventoryDetailsModel) it.next();
							
							if (isDiscountEnable()) {
								double amount=roundNumber(det.getUnit_price());
								double	discPer = roundNumber(det.getDiscountPercentage());
								double	discount_amt = det.getDiscount();
								double discountPrice=0;
								double quantity=det.getQunatity();
								
								if(det.getDiscount_type()==1){
									discountPrice=((amount*discPer/100)*quantity);
								}else{
									discountPrice=(discount_amt*quantity);
								}
								netDiscount=netDiscount+discountPrice;
								
							}
							
							
						}
							
							if((Integer) mdl.getDiscount_type()==1){
								netDiscount+=(mdl.getAmount()*mdl.getDiscountPercentage()/100);
							}else{
								netDiscount+=mdl.getDiscountAmount();
							}
							
							FinTransaction transaction=new FinTransaction();
							
							transaction.addTransaction(SConstants.DR, 
													mdl.getSales_account(),
													mdl.getCustomer().getId(),
													mdl.getAmount(),
													"",
													mdl.getCurrency_id(),
													mdl.getConversionRate());
							
							
							if(netDiscount>0)
								transaction.addTransaction(SConstants.DR, 
										mdl.getSales_account(),
										settings.getSALES_DESCOUNT_ACCOUNT(), 
										roundNumber(netDiscount),
										"",
										mdl.getCurrency_id(),
										mdl.getConversionRate());
			
							
							TransactionModel trans = saleDao.getTransactionModel(mdl.getTransaction_id());
							trans.setTransaction_details_list(transaction.getChildList());
							trans.setDate(mdl.getDate());
							trans.setLogin_id(getLoginID());
							
							new StockRateUpdateDao().updateSalesTransaction(mdl, trans);
						
					}

					} catch (Exception e) {

						Notification.show(getPropertyName("Error"),

						Type.WARNING_MESSAGE);
//					}
				}
			}
		});

		itemSelect.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5188369735622627751L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (itemSelect.getValue() != null) {
					try {

						txtRate.setValue("0");

						bic = SCollectionContainer.setList(
								new StockRateUpdateDao()
										.getAllStocksWithBalance((Long) itemSelect
												.getValue()), "id");
						stockSelect.setContainerDataSource(bic);
						stockSelect.setItemCaptionPropertyId("stock_details");

						unitSelect.setValue(new ItemDao()
								.getItem((Long) itemSelect.getValue())
								.getUnit().getId());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		stockSelect.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5188369735622627751L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (stockSelect.getValue() != null) {
					try {
						txtRate.setValue(asString(new StockRateUpdateDao()
								.getStockSalesRate(
										(Long) stockSelect.getValue(),
										(Long) unitSelect.getValue())));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		unitSelect.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -5188369735622627751L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (stockSelect.getValue() != null
						&& unitSelect.getValue() != null) {
					try {
						txtRate.setValue(asString(new StockRateUpdateDao()
								.getStockSalesRate(
										(Long) stockSelect.getValue(),
										(Long) unitSelect.getValue())));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return pan;
	}

	@Override
	public Boolean isValid() {

		clearErrors();

		boolean valid = true;
		if (txtRate.getValue() == null || txtRate.getValue().equals("")) {
			setRequiredError(txtRate, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			try {
				if (toDouble(txtRate.getValue().toString()) <= 0) {
					setRequiredError(txtRate, getPropertyName("invalid_data"),
							true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(txtRate, getPropertyName("invalid_data"), true);
				valid = false;
			}
		}

		if (itemSelect.getValue() == null || itemSelect.getValue().equals("")
				|| itemSelect.getValue().toString().equals("0")) {
			setRequiredError(itemSelect, getPropertyName("invalid_selection"),
					true);
			valid = false;
		} else
			itemSelect.setComponentError(null);

		if (unitSelect.getValue() == null || unitSelect.getValue().equals("")
				|| unitSelect.getValue().toString().equals("0")) {
			setRequiredError(unitSelect, getPropertyName("invalid_selection"),
					true);
			valid = false;
		} else
			unitSelect.setComponentError(null);

		if (stockSelect.getValue() == null) {
			setRequiredError(stockSelect, getPropertyName("invalid_selection"),
					true);
			valid = false;
		} else
			stockSelect.setComponentError(null);

		return valid;
	}

	private void clearErrors() {
		txtRate.setComponentError(null);
		stockSelect.setComponentError(null);
		itemSelect.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
