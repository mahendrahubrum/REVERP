package com.webspark.Components;

import java.util.Date;

import com.inventory.common.dao.CommonMethodsDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.ui.CurrencyRateUi;

/**
 * @author anil
 *
 */

/**
 * 
 * @author sangeeth
 *
 */

public class SCurrencyField extends SHorizontalLayout{

	private static final long serialVersionUID = -659096858801953769L;

	public STextField amountField;
	public SNativeSelect currencySelect;
	public SButton rateButton;
	public STextField conversionField;
	WrappedSession session;
	Date currencyDate;
	double previousRate=0;
	
	@SuppressWarnings("serial")
	public SCurrencyField(Date date) {
		try {
			session = new SessionUtil().getHttpSession();
			currencySelect=new SNativeSelect(null,60,new CurrencyManagementDao().getCurrencySymbol(),"id","name",false);
			currencySelect.setImmediate(true);
			currencySelect.setValue(Long.parseLong(session.getAttribute("currency_id").toString()));
			amountField=new STextField();
			amountField.setStyleName("textfield_align_right");
			amountField.setImmediate(true);
			rateButton=new SButton();
			rateButton.setVisible(false);
			rateButton.setPrimaryStyleName("currencyRateBtnStyles");
			conversionField=new STextField(null,25);
			conversionField.setVisible(false);
			conversionField.setValue("1");
			conversionField.setImmediate(true);
			currencyDate=new Date();
			setCurrencyDate(date);
			
			addComponent(currencySelect);
			addComponent(amountField);
			addComponent(rateButton);
			addComponent(conversionField);
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					setRequiredError(amountField, null, false);
					double newRate=0;
					if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
						if(isFieldValid(currencyDate))
							getCurrencyRate(currencyDate);
						newRate=getConversionRate();
						if(previousRate!=newRate)
							session.setAttribute("currency_rate_changed", true);
						else
							session.setAttribute("currency_rate_changed", false);
					}
				}
			};
			
			rateButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						previousRate=getConversionRate();
						CurrencyRateUi rate = new CurrencyRateUi();
						rate.setCaption("Currency Rate");
						rate.center();
						getUI().getCurrent().addWindow(rate);
						Date date=null;
						rate.currencyCombo.setValue(currencySelect.getValue());
						rate.currencyRateField.setValue(CommonUtil.roundNumber(getConversionRate())+"");
						Object obj=new CommonMethodsDao().getCurrencyDate(CommonUtil.getSQLDateFromUtilDate(currencyDate), (Long)currencySelect.getValue());
						if(obj!=null)
							date=(Date)obj;
						if(date!=null)
							rate.dateField.setValue(date);
						rate.addCloseListener(closeListener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						rateButton.setVisible(false);
						setRequiredError(amountField, null, false);
						
						if(currencySelect.getValue()!=null) {
							if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
								rateButton.setVisible(true);	
								isFieldValid(currencyDate);
								getCurrencyRate(currencyDate);
							}
							else
								conversionField.setValue("1");
								
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			/*amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						setRequiredError(amountField, null, false);
						if(amountField.getValue()!=null && !amountField.getValue().toString().equals("")){
							isFieldValid(currencyDate);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});*/
			amountField.setNewValue("0");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("serial")
	public SCurrencyField(String caption ,int width, Date date) {
		try {
			session = new SessionUtil().getHttpSession();
			currencySelect=new SNativeSelect(null,60,new CurrencyManagementDao().getCurrencySymbol(),"id","name",false);
			currencySelect.setImmediate(true);
			currencySelect.setValue(Long.parseLong(session.getAttribute("currency_id").toString()));
			amountField=new STextField(null,width);
			amountField.setStyleName("textfield_align_right");
			amountField.setImmediate(true);
			rateButton=new SButton();
			rateButton.setVisible(false);
			rateButton.setPrimaryStyleName("currencyRateBtnStyles");
			conversionField=new STextField(null,25);
			conversionField.setVisible(false);
			conversionField.setValue("1");
			conversionField.setImmediate(true);
			currencyDate=new Date();
			setCurrencyDate(date);
			
			setCaption(caption);
			addComponent(currencySelect);
			addComponent(amountField);
			addComponent(rateButton);
			addComponent(conversionField);
			
			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					setRequiredError(amountField, null, false);
					double newRate=0;
					if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
						if(isFieldValid(currencyDate)){
							getCurrencyRate(currencyDate);
							newRate=getConversionRate();
							if(previousRate!=newRate)
								session.setAttribute("currency_rate_changed", true);
							else
								session.setAttribute("currency_rate_changed", false);
						}
					}
				}
			};
			
			rateButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						previousRate=getConversionRate();
						CurrencyRateUi rate = new CurrencyRateUi();
						rate.setCaption("Currency Rate");
						rate.center();
						Date date=null;
						rate.currencyCombo.setValue(currencySelect.getValue());
						rate.currencyRateField.setValue(CommonUtil.roundNumber(getConversionRate())+"");
						Object obj=new CommonMethodsDao().getCurrencyDate(CommonUtil.getSQLDateFromUtilDate(currencyDate), (Long)currencySelect.getValue());
						if(obj!=null)
							date=(Date)obj;
						if(date!=null)
							rate.dateField.setValue(date);
						getUI().getCurrent().addWindow(rate);
						rate.addCloseListener(closeListener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						rateButton.setVisible(false);
						setRequiredError(amountField, null, false);
						if(currencySelect.getValue()!=null){
							if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
								rateButton.setVisible(true);	
								isFieldValid(currencyDate);
								getCurrencyRate(currencyDate);
							}
							else
								conversionField.setValue("1");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			/*amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						setRequiredError(amountField, null, false);
						if(amountField.getValue()!=null && !amountField.getValue().toString().equals("")){
							isFieldValid(currencyDate);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});*/
			amountField.setNewValue("0");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("serial")
	public SCurrencyField(String caption ,int width,boolean symbolVisible,boolean changeRateVisible, Date date) {
		try {
			session = new SessionUtil().getHttpSession();
			currencySelect=new SNativeSelect(null,60,new CurrencyManagementDao().getCurrencySymbol(),"id","name",false);
			currencySelect.setImmediate(true);
			currencySelect.setValue(Long.parseLong(session.getAttribute("currency_id").toString()));
			amountField=new STextField(null,width);
			amountField.setStyleName("textfield_align_right");
			amountField.setImmediate(true);
			rateButton=new SButton();
			rateButton.setVisible(false);
			rateButton.setPrimaryStyleName("currencyRateBtnStyles");
			conversionField=new STextField(null,25);
			conversionField.setVisible(false);
			conversionField.setValue("1");
			conversionField.setImmediate(true);
			currencyDate=new Date();
			setCurrencyDate(date);
			
			setCaption(caption);
			if(symbolVisible){
				
				addComponent(currencySelect);
				
				currencySelect.addValueChangeListener(new ValueChangeListener() {
					
					@Override
					public void valueChange(ValueChangeEvent event) {
						try {
							rateButton.setVisible(false);
							setRequiredError(amountField, null, false);
							if(currencySelect.getValue()!=null){
								if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
									rateButton.setVisible(true);	
									isFieldValid(currencyDate);
								}
								else
									conversionField.setValue("1");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			addComponent(amountField);
			
			/*amountField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						setRequiredError(amountField, null, false);
						if(amountField.getValue()!=null && !amountField.getValue().toString().equals("")){
							isFieldValid(currencyDate);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});*/
			amountField.setNewValue("0");
			
			if(changeRateVisible){
				
				addComponent(rateButton);
				
				final CloseListener closeListener = new CloseListener() {

					@Override
					public void windowClose(CloseEvent e) {
						setRequiredError(amountField, null, false);
						double newRate=0;
						if((Long)currencySelect.getValue()!=Long.parseLong(session.getAttribute("currency_id").toString())){
							if(isFieldValid(currencyDate))
								getCurrencyRate(currencyDate);
							newRate=getConversionRate();
							if(previousRate!=newRate)
								session.setAttribute("currency_rate_changed", true);
							else
								session.setAttribute("currency_rate_changed", false);
						}
					}
				};

				
				rateButton.addClickListener(new ClickListener() {
					
					@SuppressWarnings("static-access")
					@Override
					public void buttonClick(ClickEvent event) {
						try {
							previousRate=getConversionRate();
							CurrencyRateUi rate = new CurrencyRateUi();
							rate.setCaption("Currency Rate");
							rate.center();
							Date date=null;
							rate.currencyCombo.setValue(currencySelect.getValue());
							rate.currencyRateField.setValue(CommonUtil.roundNumber(getConversionRate())+"");
							Object obj=new CommonMethodsDao().getCurrencyDate(CommonUtil.getSQLDateFromUtilDate(currencyDate), (Long)currencySelect.getValue());
							if(obj!=null)
								date=(Date)obj;
							if(date!=null)
								rate.dateField.setValue(date);
							getUI().getCurrent().addWindow(rate);
							rate.addCloseListener(closeListener);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			addComponent(conversionField);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public double getValue() {
		double value=0;
		try {
			value=Double.parseDouble(amountField.getValue().toString());
			getCurrencyRate(currencyDate);
		} catch (Exception e) {
			value=0;
			amountField.setValue("0");
		}
		return value;
	}
	
	
	public long getCurrency() {
		return Long.parseLong(currencySelect.getValue().toString());
	}
	
	
	public double getCurrencyRate(Date currencyDate){
		double value=0;
		try {
			value=new CommonMethodsDao().getCurrencyRate(CommonUtil.getSQLDateFromUtilDate(currencyDate), (Long)currencySelect.getValue());
			amountField.setRequiredError(null);
			if(currencySelect.getValue()!=null && !currencySelect.getValue().toString().equals("")){
				long cid=Long.parseLong(currencySelect.getValue().toString());
				if(cid!=Long.parseLong(session.getAttribute("currency_id").toString())){
					if(value==0){
						amountField.setRequiredError("Set Currency Rate");
					}
//					else
//						conversionField.setNewValue(CommonUtil.roundNumber(value)+"");
				}
				else{
					value=1;
				}
			}
			conversionField.setNewValue(CommonUtil.roundNumberToString(value));
		} catch (Exception e) {
			e.printStackTrace();
			value=0;
		}
		conversionField.setNewValue(CommonUtil.roundNumberToString(value));
		return value;
	}
	
	
	public double getConversionRate() {
		double rate=0;
		try {
			rate=Double.parseDouble(conversionField.getValue().toString());
		} catch (Exception e) {
			rate=0;
			conversionField.setValue("0");
		}
		return rate;
	}
	
	
	public void setCurrency(long currency){
		currencySelect.setValue(currency);
	}
	
	
	public void setNewCurrency(long currency){
		if(currencySelect.isReadOnly()){
			currencySelect.setReadOnly(false);
			currencySelect.setValue(currency);
			currencySelect.setReadOnly(true);
		}
		else
			currencySelect.setValue(currency);
	}
	
	
	public void setValue(double amount){
		amountField.setValue(CommonUtil.roundNumberToString(amount));
		getCurrencyRate(currencyDate);
	}
	
	
	public void setValue(long currencyId, double amount){
		currencySelect.setValue(currencyId);
		amountField.setValue(CommonUtil.roundNumberToString(amount));
		getCurrencyRate(currencyDate);
	}
	
	
	public boolean isFieldValid(Date currencyDate){
		boolean valid=true;
		
		if(getConversionRate()<=0){
			valid=false;
			setRequiredError(rateButton, "Set Currency Rate", true);
		}
		else
			setRequiredError(rateButton, null, false);
				
		if(getCurrency()==0){
			valid=false;
			setRequiredError(currencySelect, "Select Currency", true);
		}
		else
			setRequiredError(currencySelect, null, false);
		
		if(getCurrency()!=0){
			long cid=Long.parseLong(currencySelect.getValue().toString());
			if(cid!=Long.parseLong(session.getAttribute("currency_id").toString())){
				
				if(getCurrencyRate(currencyDate)<=0){
					valid=false;
					setRequiredError(rateButton, "Set Currency Rate", true);
				}
				else
					setRequiredError(rateButton, null, false);
			}
			else
				setRequiredError(rateButton, null, false);
		}
		
		if(getValue()<0){
			valid=false;
			setRequiredError(amountField, "Enter Valid Amount", true);
		}
		else
			setRequiredError(amountField, null, false);
		
		return valid;
	}
	
	
	public Date getCurrencyDate() {
		return currencyDate;
	}

	
	public void setCurrencyDate(Date currencyDate) {
		this.currencyDate = currencyDate;
		getCurrencyRate(this.currencyDate);
	}

	
	public void setRequiredError(AbstractComponent component,String fieldNameToDisplay, boolean enable) {
		if (enable) {
			component.setComponentError(new SUserError("<i style='font-size: 13px;'>" + fieldNameToDisplay,
					ContentMode.HTML, ErrorLevel.CRITICAL));
		} else
			component.setComponentError(null);
	}

	
	@Override
	public void setReadOnly(boolean readOnly) {
		amountField.setReadOnly(readOnly);
		currencySelect.setReadOnly(readOnly);
	}
	
	
	public void setNewValue(double amount){
		
		if(amountField.isReadOnly()){
			amountField.setReadOnly(false);
			amountField.setValue(CommonUtil.roundNumberToString(amount));
			amountField.setReadOnly(true);
		}
		else{
			amountField.setValue(CommonUtil.roundNumberToString(amount));
		}
		getCurrencyRate(currencyDate);
	}
	
	
	public void setNewValue(long currencyId, double amount){
		
		if(currencySelect.isReadOnly()){
			currencySelect.setReadOnly(false);
			currencySelect.setValue(currencyId);
			currencySelect.setReadOnly(true);
		}
		else{
			currencySelect.setValue(currencyId);
		}
		
		if(amountField.isReadOnly()){
			amountField.setReadOnly(false);
			amountField.setValue(CommonUtil.roundNumberToString(amount));
			amountField.setReadOnly(true);
		}
		else{
			amountField.setValue(CommonUtil.roundNumberToString(amount));
		}
		getCurrencyRate(currencyDate);
	}

	
}
