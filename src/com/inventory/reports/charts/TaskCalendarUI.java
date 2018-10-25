package com.inventory.reports.charts;

import java.util.Date;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.InlineDateField;
import com.webspark.Components.SButton;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 22, 2014
 */
public class TaskCalendarUI extends SContainerLayout {

	private static final long serialVersionUID = 2010289560402788036L;
	
	private SDateField dateField;
	private Calendar styleCalendar;
	private InlineDateField yearField;
	
	SButton currentDateButton;


	@Override
	public void getChart(Date fromDate, Date toDate) {

		setSize(450, 310);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout formLayout = new SFormLayout();
		formLayout.setStyleName("layout_light_bordered");
		
		try {
			
			formLayout.setMargin(true);
			formLayout.setSpacing(true);
			
			currentDateButton=new SButton();
			currentDateButton.setPrimaryStyleName("current_date_btn");

			yearField=new InlineDateField();
			yearField.setValue(new Date());
			yearField.setResolution(Resolution.MONTH);
			yearField.setImmediate(true);

			dateField = new SDateField(getPropertyName("date"), 100);
			dateField.setValue(new Date());
			dateField.setReadOnly(true);
			
			styleCalendar = new Calendar();
			styleCalendar.setWidth("400px");
			styleCalendar.setHeight("280px");
			styleCalendar.setStyleName("calendar_day");
			styleCalendar.setStartDate(fromDate);
			styleCalendar.setEndDate(toDate);


			SHorizontalLayout hlay=new SHorizontalLayout();
			hlay.setSpacing(true);
			
			hlay.addComponent(yearField);
			hlay.addComponent(currentDateButton);

			formLayout.addComponent(hlay);
			formLayout.addComponent(styleCalendar);
			
			yearField.addListener(new Listener() {
				
				@Override
				public void componentEvent(Event event) {
					setCalDates(yearField.getValue());
				}
			});
			
			currentDateButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					yearField.setValue(getWorkingDate());
					setCalDates(getWorkingDate());
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addComponent(formLayout);
	}
	private void setCalDates(Date date) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);

		cal.set(java.util.Calendar.DATE, 1);
		styleCalendar.setStartDate(cal.getTime());
		cal.set(java.util.Calendar.DATE,
				cal.getMaximum(java.util.Calendar.DATE));
		styleCalendar.setEndDate(cal.getTime());
	}
}
