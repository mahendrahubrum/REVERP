package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.HolidayDao;
import com.inventory.payroll.model.HolidayModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.model.S_OfficeModel;

/**
 * 
 * @author sangeeth
 * @date 02-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class AddHolidaysUI extends SparkLogic {

	SPanel mainPanel;
	SVerticalLayout mainLayout;
	
	private InlineDateField yearField;
	private STable table;
	
	SButton saveButton;
	SButton deleteButton;
	
	SButton addItemButton;
	SButton updateItemButton;
	
	STextField titleField;
	STextField descriptionField;
	SDateField dateField;
	
	private static final String TBL_ID="ID";
	private static final String TBL_TITLE="Title";
	private static final String TBL_DESCRIPTION="Description";
	private static final String TBL_DATE_ID="Date Id";
	private static final String TBL_DATE="Date";
	
	Object[] allHeaders, requiredHeaders;
	HolidayDao dao;
	
	@SuppressWarnings("static-access")
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		allHeaders=new Object[] {TBL_ID, TBL_TITLE, TBL_DESCRIPTION, TBL_DATE_ID, TBL_DATE};
		requiredHeaders=new String[] {TBL_TITLE, TBL_DESCRIPTION, TBL_DATE};
		setSize(650, 550);
		dao=new HolidayDao();
		try {
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			yearField=new InlineDateField("Year");
			yearField.setResolution(Resolution.YEAR);
			yearField.setImmediate(true);
			
			table=new STable(null,600,250);
			table.addContainerProperty(TBL_ID, Long.class, null, TBL_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_TITLE, String.class, null, TBL_TITLE, null, Align.CENTER);
			table.addContainerProperty(TBL_DESCRIPTION, String.class, null, TBL_DESCRIPTION, null, Align.CENTER);
			table.addContainerProperty(TBL_DATE_ID, Date.class, null, TBL_DATE_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_DATE, String.class, null, TBL_DATE, null, Align.CENTER);
			
			table.setColumnExpandRatio(TBL_TITLE, 2f);
			table.setColumnExpandRatio(TBL_DESCRIPTION, 2f);
			table.setColumnExpandRatio(TBL_DATE, 1f);
			table.setSelectable(true);
			table.setVisibleColumns(requiredHeaders);
			
			titleField=new STextField("Title", 200, true);
			titleField.setInputPrompt("Title");
			descriptionField=new STextField("Description", 200, true);
			descriptionField.setInputPrompt("Description");
			dateField=new SDateField("Date", 100, getDateFormat());
			
			SHorizontalLayout itemLayout=new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			
			addItemButton = new SButton(null, "Add");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			itemLayout.addComponent(titleField);
			itemLayout.addComponent(descriptionField);
			itemLayout.addComponent(dateField);
			itemLayout.addComponent(addItemButton);
			itemLayout.addComponent(updateItemButton);
			
			itemLayout.setComponentAlignment(addItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setStyleName("po_border");
			
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/save.png"));
			saveButton.setStyleName("saveButtonStyle");
			deleteButton = new SButton(getPropertyName("delete"), 100, 25);
			deleteButton.setIcon(new ThemeResource("icons/delete.png"));
			deleteButton.setStyleName("deleteButtonStyle");
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(deleteButton);
			
			deleteButton.setVisible(false);
			
			mainLayout.addComponent(new SFormLayout(yearField));
			mainLayout.addComponent(table);
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainPanel.setContent(mainLayout);
			
			yearField.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(yearField.getValue()!=null)
							loadTable();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			Calendar yearCal=getCalendar();
			yearCal.set(yearCal.DATE, 1);
			yearCal.set(yearCal.MONTH, 0);
			yearField.setValue(yearCal.getTime());
			
			addItemButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							table.setVisibleColumns(allHeaders);
							table.addItem(new Object[]{
								(long)0,
								titleField.getValue(),
								descriptionField.getValue(),
								dateField.getValue(),
								CommonUtil.formatDateToDDMMYYYY(dateField.getValue())},table.getItemIds().size()+1);
							table.setVisibleColumns(requiredHeaders);
							titleField.setValue("");
							descriptionField.setValue("");
							dateField.setValue(null);
							addItemButton.setVisible(true);
							updateItemButton.setVisible(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							titleField.setValue(item.getItemProperty(TBL_TITLE).getValue().toString());
							descriptionField.setValue(item.getItemProperty(TBL_DESCRIPTION).getValue().toString());
							dateField.setValue((Date)item.getItemProperty(TBL_DATE_ID).getValue());
							addItemButton.setVisible(false);
							updateItemButton.setVisible(true);
							deleteButton.setVisible(true);
						}
						else{
							titleField.setValue("");
							descriptionField.setValue("");
							dateField.setValue(null);
							addItemButton.setVisible(true);
							updateItemButton.setVisible(false);
							deleteButton.setVisible(false);
						}
					} catch (ConversionException e) {
						e.printStackTrace();
					}
						
				}
			});
			
			updateItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							Item item=table.getItem(table.getValue());
							item.getItemProperty(TBL_TITLE).setValue(titleField.getValue());
							item.getItemProperty(TBL_DESCRIPTION).setValue(descriptionField.getValue());
							item.getItemProperty(TBL_DATE_ID).setValue(dateField.getValue());
							item.getItemProperty(TBL_DATE).setValue(CommonUtil.formatDateToDDMMYYYY(dateField.getValue()));
							table.setValue(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						Iterator itr=table.getItemIds().iterator();
						List<HolidayModel> list=new ArrayList<HolidayModel>();
						HolidayModel mdl=null;
						while (itr.hasNext()) {
							Item item = table.getItem(itr.next());
							long id=(Long)item.getItemProperty(TBL_ID).getValue();
							mdl=dao.getHolidayModel(id);
							if(mdl==null)
								mdl=new HolidayModel();
							mdl.setTitle(item.getItemProperty(TBL_TITLE).getValue().toString());
							mdl.setDescription(item.getItemProperty(TBL_DESCRIPTION).getValue().toString());
							mdl.setDate(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBL_DATE_ID).getValue()));
							mdl.setOffice(new S_OfficeModel(getOfficeID()));
							list.add(mdl);
						}
						if(list.size()>0){
							dao.save(list);
							Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							Date date=yearField.getValue();
							yearField.setValue(null);
							yearField.setValue(date);
						}
						else{
							Notification.show("No Change",Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			deleteButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(table.getValue()!=null){
							Item item=table.getItem(table.getValue());
							final long id=(Long)item.getItemProperty(TBL_ID).getValue();
							ConfirmDialog.show(getUI(), "Are you sure?", new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {
										try {
											if(id!=0){
												HolidayModel mdl=dao.getHolidayModel(id);
												dao.delete(mdl);
											}
											else{
												table.removeItem(table.getValue());
											}
											Notification.show(getPropertyName("deleted_success"),Type.WARNING_MESSAGE);
											Date date=yearField.getValue();
											yearField.setValue(null);
											yearField.setValue(date);
										} 
										catch (Exception e) {
											Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
											e.printStackTrace();
										}
									}
								}
							});
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

	@SuppressWarnings("rawtypes")
	public void loadTable(){
		try {
			table.removeAllItems();
			Calendar startcal=getCalendar();
			Calendar endcal=getCalendar();
			startcal.setTime(yearField.getValue());
			startcal.set(Calendar.DAY_OF_MONTH, 1);
			startcal.set(Calendar.MONTH, 0);
//			System.out.println("Start "+startcal.getTime());
			endcal.setTime(yearField.getValue());
			endcal.set(Calendar.DAY_OF_MONTH, 31);
			endcal.set(Calendar.MONTH, 11);
//			System.out.println("End "+endcal.getTime());
			dateField.setRangeStart(null);
			dateField.setRangeEnd(null);
			dateField.setRangeStart(startcal.getTime());
			dateField.setRangeEnd(endcal.getTime());
			List list=new ArrayList();
			list=dao.getHolidayModel(getOfficeID(), 
										CommonUtil.getSQLDateFromUtilDate(startcal.getTime()), 
										CommonUtil.getSQLDateFromUtilDate(endcal.getTime()));
			if(list.size()>0){
				table.setVisibleColumns(allHeaders);
				Iterator itr=list.iterator();
				while (itr.hasNext()) {
					HolidayModel mdl = (HolidayModel) itr.next();
					table.addItem(new Object[]{
							mdl.getId(),
							mdl.getTitle(),
							mdl.getDescription(),
							mdl.getDate(),
							CommonUtil.formatDateToDDMMYYYY(mdl.getDate())},table.getItemIds().size()+1);
				}
				table.setVisibleColumns(requiredHeaders);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Boolean isValid() {
		return null;
	}
	
	public Boolean isAddingValid() {
		boolean valid=true;
		
		if(titleField.getValue()==null || titleField.getValue().toString().length()<=0){
			setRequiredError(titleField, "Invalid selection", true);
			valid=false;
		}
		else
			setRequiredError(titleField, null, false);
		
		if(dateField.getValue()==null){
			setRequiredError(dateField, "Invalid selection", true);
			valid=false;
		}
		else
			setRequiredError(dateField, null, false);
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
	
	

}
