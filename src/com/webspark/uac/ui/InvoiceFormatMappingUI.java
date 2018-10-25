package com.webspark.uac.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.Reindeer;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.uac.dao.InvoiceFormatMappingDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.InvoiceFormatModel;

/**
 * @author Anil K P
 *         WebSpark.
 *         Nov 13, 2013
 */

/**
 * @author sangeeth
 * @date 06-Jan-2016
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class InvoiceFormatMappingUI extends SparkLogic {

	private SComboField officeCombo;
	private SComboField idCombo;
	private STextField formatField;
	private STextField exampleField;
	private SHTMLLabel noteLabel;
	
	private SButton saveButton;

	SPanel mainPanel;
	SFormLayout mainLayout;
	
	private InvoiceFormatMappingDao dao;
	
	public static String DEFAULT_NUM = "$No$";
	
	boolean isSavable=true;
	
	@Override
	public SPanel getGUI() {

		mainPanel = new SPanel();
		mainPanel.setSizeFull();
		mainLayout = new SFormLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setSize(500, 400);		
		dao = new InvoiceFormatMappingDao();
		isSavable=true;

		try {
			officeCombo = new SComboField(getPropertyName("office"), 300, new OfficeDao().getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name", true, getPropertyName("select"));
			
			idCombo = new SComboField(getPropertyName("type"), 300, new IDGeneratorSettingsDao().getAllIDGenerators(),
									"id", "id_name", true, getPropertyName("select"));
			formatField=new STextField(getPropertyName("format"), 300, true);
			exampleField=new STextField(getPropertyName("example_format"), 300, true);
			formatField.setImmediate(true);
			exampleField.setImmediate(true);
			exampleField.setReadOnly(true);
			
			noteLabel=new SHTMLLabel(getPropertyName("note"));
			noteLabel.setStyleName(Reindeer.LAYOUT_BLUE);
			
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

			mainLayout.addComponent(officeCombo);
			mainLayout.addComponent(idCombo);
			mainLayout.addComponent(formatField);
			mainLayout.addComponent(exampleField);
			mainLayout.addComponent(noteLabel);
			mainLayout.addComponent(saveButton);
			
			mainPanel.setContent(mainLayout);
			
			String noteString = "<font color='red'>Do Not Change '$No$'.</font><br>" +
								"Specify Date Format in between '$' & '$'. &nbsp <font color='red'>Eg. : $MM- dd- yyyy$</font><br>" +
								"MM- Month No.<br>" +
								"dd- Day Of Month.<br>" +
								"yy- Year in 2 Digit.<br>" +
								"yyyy- Year in 4 Digit.<br>"; 
			
			noteLabel.setValue(noteString);

			
			formatField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						isSavable=true;
						formatField.setComponentError(null);
						if (formatField.getValue() != null && !formatField.getValue().trim().equals("")) {
							String finalString=getStringFormat(formatField.getValue().trim(), getWorkingDate());
							exampleField.setNewValue(finalString);
						}
						if(!isSavable)
							setRequiredError(formatField, getPropertyName("invalid_date"), true);
					} catch (Exception e) {
						e.printStackTrace();
					} 					
				}
			});
			formatField.setValue(DEFAULT_NUM);
			
			
			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (isValid()) {

							InvoiceFormatModel mdl=null;
							mdl=dao.getInvoiceFormatModel((Long)officeCombo.getValue(), (Long)idCombo.getValue());
							if(mdl==null)
								mdl=new InvoiceFormatModel();
							mdl.setOffice((Long)officeCombo.getValue());
							mdl.setIdFormat((Long)idCombo.getValue());
							mdl.setInvocieFormat(formatField.getValue().trim());
							dao.save(mdl);
							Object ofcObject=officeCombo.getValue();
							Object idObject=idCombo.getValue();
							resetAll();
							officeCombo.setValue(ofcObject);
							idCombo.setValue(idObject);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});

			
			officeCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					resetAll();
					try {
						if (officeCombo.getValue() != null && idCombo.getValue()!=null) {
							InvoiceFormatModel mdl=dao.getInvoiceFormatModel((Long)officeCombo.getValue(), (Long)idCombo.getValue());
							formatField.setValue(mdl.getInvocieFormat().trim());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			officeCombo.setValue(getOfficeID());
			
			
			idCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (officeCombo.getValue() != null && idCombo.getValue()!=null) {
							InvoiceFormatModel mdl=dao.getInvoiceFormatModel((Long)officeCombo.getValue(), (Long)idCombo.getValue());
							if(mdl!=null)
								formatField.setValue(mdl.getInvocieFormat().trim());
							else
								formatField.setValue(DEFAULT_NUM);
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

	
	protected void resetAll() {
		officeCombo.setComponentError(null);
		officeCombo.setValue(getOfficeID());
		idCombo.setValue(null);
		formatField.setValue(DEFAULT_NUM);
	}

	
	@Override
	public Boolean isValid() {

		officeCombo.setComponentError(null);
		idCombo.setComponentError(null);

		boolean flag = true;

		if (officeCombo.getValue() == null || officeCombo.getValue().equals("")) {
			flag = false;
			setRequiredError(officeCombo, getPropertyName("invalid_selection"),true);
		}
		
		if (idCombo.getValue() == null || idCombo.getValue().equals("")) {
			flag = false;
			setRequiredError(idCombo, getPropertyName("invalid_selection"),true);
		}
		
		if (formatField.getValue() == null || formatField.getValue().equals("")) {
			setRequiredError(formatField,getPropertyName("invalid_data"), true);
			flag = false;
		} else
			setRequiredError(formatField, null, false);
		
		if(!isSavable){
			setRequiredError(formatField,getPropertyName("invalid_data"), true);
			flag = false;
		}
		else
			setRequiredError(formatField, null, false);
			
		
		return flag;
	}

	
	public String getStringFormat(String inputFormat, Date date){
		String finalString="";
		try {
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			String format=inputFormat;
			if(format.contains("$No$")){
				int noIndex=format.indexOf("$No$");
				String subString=format.substring(0,noIndex);
				
				if(subString.contains("$")){
					int count=0;
					for (int i = 0; i < subString.length(); i++) {
						if(subString.charAt(i)=='$')
							count++;
					}
					if(count%2!=0)
						isSavable=false;
					count/=2;
					int firstIndex=0;
					int secondIndex=0;
					int referIndex=0;
					finalString+=" ";
					for (int j = 1; j <= count; j++) {
						firstIndex=subString.indexOf("$", firstIndex);
						secondIndex=subString.indexOf("$", firstIndex+1);
						if(secondIndex<0){
							isSavable=false;
							break;
						}
						String subStringRemaining=subString.substring(referIndex, firstIndex);
						finalString+=subStringRemaining;
						
						String dateString=subString.substring(firstIndex+1, secondIndex);
						SimpleDateFormat formatter=new SimpleDateFormat(dateString);
						try {
							finalString+=formatter.format(calendar.getTime());
						} catch (Exception e) {
							isSavable=false;
							break;
						}
						secondIndex++;
						firstIndex=secondIndex;
						referIndex=secondIndex;
					}
					String subStringRemaining=subString.substring(referIndex, noIndex);
					finalString+=subStringRemaining;
				}
				else{
					finalString+=subString;
				}
				finalString+="$No$";
				
				subString=format.substring(noIndex+DEFAULT_NUM.length(), format.length());
				if(subString.contains("$")){
					int count=0;
					for (int i = 0; i < subString.length(); i++) {
						if(subString.charAt(i)=='$')
							count++;
					}
					if(count%2!=0)
						isSavable=false;
					count/=2;
					int firstIndex=0;
					int secondIndex=0;
					int referIndex=0;
					finalString+=" ";
					for (int j = 1; j <= count; j++) {
						firstIndex=subString.indexOf("$", firstIndex);
						secondIndex=subString.indexOf("$", firstIndex+1);
						if(secondIndex<0){
							isSavable=false;
							break;
						}
						String subStringRemaining=subString.substring(referIndex, firstIndex);
						finalString+=subStringRemaining;
						
						String dateString=subString.substring(firstIndex+1, secondIndex);
						SimpleDateFormat formatter=new SimpleDateFormat(dateString);
						try {
							finalString+=formatter.format(calendar.getTime());
						} catch (Exception e) {
							isSavable=false;
							break;
						}
						secondIndex++;
						firstIndex=secondIndex;
						referIndex=secondIndex;
					}
					String subStringRemaining=subString.substring(referIndex, subString.length());
					finalString+=subStringRemaining;
				}
				else{
					finalString+=subString;
				}
			}
			else{
				isSavable=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalString;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
