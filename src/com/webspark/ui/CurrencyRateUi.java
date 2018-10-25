package com.webspark.ui;

import java.util.Date;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.CurrencyRateDao;
import com.webspark.model.CurrencyModel;
import com.webspark.model.CurrencyRateModel;

/**
 * 
 * @author sangeeth
 *
 */

@SuppressWarnings("serial")
public class CurrencyRateUi extends SparkLogic {

	SPanel mainPanel;
	SFormLayout mainLayout;
	
	STextField baseCurrencyField;
	public SComboField currencyCombo;
	public SDateField dateField;
	public STextField currencyRateField;
	SButton saveButton;
	CurrencyRateDao dao;
	
	CollectionContainer bic;
	WrappedSession session;

	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		mainLayout = new SFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setSize(375, 250);
		
		try {
			session = getHttpSession();
			dao = new CurrencyRateDao();
			dateField = new SDateField(getPropertyName("date"), 100, getDateFormat());
			dateField.setImmediate(true);
			baseCurrencyField = new STextField(getPropertyName("base_currency"), 200);
			baseCurrencyField.setNewValue(dao.getCurrency(getCurrencyID()));
			baseCurrencyField.setReadOnly(true);
			currencyCombo = new SComboField(getPropertyName("currency"), 200, dao.getlabels(), "id", "name",true,getPropertyName("select"));
			currencyRateField = new STextField(getPropertyName("rate"), 200);
			currencyRateField.setValue("1");
			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			
			mainLayout.addComponent(dateField);
			mainLayout.addComponent(baseCurrencyField);
			mainLayout.addComponent(currencyCombo);
			mainLayout.addComponent(currencyRateField);
			mainLayout.addComponent(saveButton);
			mainLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			dateField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(dateField.getValue()!=null){
							loadData();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			currencyCombo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(currencyCombo.getValue()!=null){
							loadData();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			dateField.setValue(getWorkingDate());
			
			saveButton.addClickListener(new ClickListener() {
				public void buttonClick(ClickEvent event) {

					try {
						if(isValid()) {
							CurrencyRateModel mdl;
							mdl=dao.getCurrencyRateModel(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), (Long)currencyCombo.getValue());
							if(mdl==null)
								mdl=new CurrencyRateModel();
							mdl.setBaseCurrency(new CurrencyModel((Long) session.getAttribute("currency_id")));
							mdl.setCurrencyId(new CurrencyModel((Long)currencyCombo.getValue()));
							mdl.setDate(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()));
							mdl.setRate(roundNumber(toDouble(currencyRateField.getValue().toString())));
							Date date = dao.saveCurrencyRateModel(mdl);
							dateField.setValue(null);
							if(date!=null)
								dateField.setValue(date);
							else
								dateField.setValue(getWorkingDate());
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}

			});
			
			mainPanel.addShortcutListener(new ShortcutListener("Save Item",
					ShortcutAction.KeyCode.ENTER, null) {

				@Override
				public void handleAction(Object sender, Object target) {
					saveButton.click();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	public void loadData(){
		try {
			baseCurrencyField.setNewValue(dao.getCurrency(getCurrencyID()));
			currencyRateField.setValue("1");
			if(dateField.getValue()!=null && currencyCombo.getValue()!=null){
				CurrencyRateModel mdl=dao.getCurrencyRateModel(CommonUtil.getSQLDateFromUtilDate(dateField.getValue()), (Long)currencyCombo.getValue());
				if(mdl!=null){
					baseCurrencyField.setNewValue(mdl.getBaseCurrency().getName());
					currencyCombo.setValue(mdl.getCurrencyId().getId());
					currencyRateField.setValue(roundNumber(mdl.getRate())+"");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		clearErrors();

		boolean valid = true;
		if (currencyRateField.getValue() == null || currencyRateField.getValue().equals("") || currencyRateField.getValue().equals("0")) {
			setRequiredError(currencyRateField, getPropertyName("invalid_data"), true);
			valid = false;
		} else {
			try {
				if (toDouble(currencyRateField.getValue().toString()) <= 0) {
					setRequiredError(currencyRateField, getPropertyName("invalid_data"),true);
					valid = false;
				}
			} catch (Exception e) {
				setRequiredError(currencyRateField, getPropertyName("invalid_data"), true);
				valid = false;
			}
		}

		if (dateField.getValue() == null || dateField.getValue().equals("")) {
			setRequiredError(dateField, getPropertyName("invalid_data"), true);
			valid = false;
		}

		if (currencyCombo.getValue() == null || currencyCombo.getValue().equals("")) {
			setRequiredError(currencyCombo,getPropertyName("invalid_selection"), true);
			valid = false;
		}

		return valid;
	}

	private void clearErrors() {
		currencyRateField.setComponentError(null);
		dateField.setComponentError(null);
		currencyCombo.setComponentError(null);
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
