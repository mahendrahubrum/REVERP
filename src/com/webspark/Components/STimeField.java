package com.webspark.Components;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.InlineDateField;
import com.webspark.common.util.SessionUtil;


/**
 * @author anil
 *
 */

/**
 * @author sangeeth
 * @date 17-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
public class STimeField extends SHorizontalLayout{

	public InlineDateField startTime;
	public InlineDateField endTime;
//	SHorizontalLayout mainLayout;
	Calendar mainCalendar;
	Calendar timeCalendar;
	Calendar startCalendar;
	Calendar endCalendar;
	WrappedSession session;
	
	public STimeField(String caption) {
		session = new SessionUtil().getHttpSession();
		setCaption(caption);
//		mainLayout=new SHorizontalLayout(caption);
//		mainLayout.setSpacing(true);
		setSpacing(true);
		
		startTime=new InlineDateField("In");
		startTime.setResolution(Resolution.MINUTE);
		startTime.setWidth("75");
		startTime.addStyleName("time-only");
		startTime.setImmediate(true);
		
		endTime=new InlineDateField("Out");
		endTime.setResolution(Resolution.MINUTE);
		endTime.setWidth("75");
		endTime.addStyleName("time-only");
		endTime.setImmediate(true);
		
		mainCalendar=Calendar.getInstance();
		timeCalendar=Calendar.getInstance();
		startCalendar=Calendar.getInstance();
		endCalendar=Calendar.getInstance();
		
		mainCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mainCalendar.set(Calendar.MINUTE, 0);
		mainCalendar.set(Calendar.SECOND, 0);
		mainCalendar.set(Calendar.MILLISECOND, 0);
		
		timeCalendar.setTime(mainCalendar.getTime());
		startCalendar.setTime(mainCalendar.getTime());
		endCalendar.setTime(mainCalendar.getTime());
		
//		mainLayout.addComponent(startTime);
//		mainLayout.addComponent(endTime);
//		addComponent(mainLayout);
		addComponent(startTime);
		addComponent(endTime);
//		addComponent(mainLayout);
		
		startTime.setValue(mainCalendar.getTime());
		endTime.setValue(mainCalendar.getTime());

		
		
		startTime.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(startTime.getValue()!= null)
					startCalendar.setTime(startTime.getValue());
				if(endTime.isVisible())
					isValid();
			}
		});
		
		
		
		endTime.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(endTime.getValue()!= null)
					endCalendar.setTime(endTime.getValue());
				if(endTime.isVisible())
					isValid();
				
			}
		});
		
		
		
	}
	
	
	public STimeField(String caption, String caption1, String caption2) {
		session = new SessionUtil().getHttpSession();
		setCaption(caption);
		setSpacing(true);
//		mainLayout=new SHorizontalLayout(caption);
//		mainLayout.setSpacing(true);
		
		startTime=new InlineDateField(caption1);
		startTime.setResolution(Resolution.MINUTE);
		startTime.setWidth("75");
		startTime.addStyleName("time-only");
		startTime.setImmediate(true);
		
		endTime=new InlineDateField(caption2);
		endTime.setResolution(Resolution.MINUTE);
		endTime.setWidth("75");
		endTime.addStyleName("time-only");
		endTime.setImmediate(true);
		
		mainCalendar=Calendar.getInstance();
		timeCalendar=Calendar.getInstance();
		startCalendar=Calendar.getInstance();
		endCalendar=Calendar.getInstance();
		
		mainCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mainCalendar.set(Calendar.MINUTE, 0);
		mainCalendar.set(Calendar.SECOND, 0);
		mainCalendar.set(Calendar.MILLISECOND, 0);
		
		timeCalendar.setTime(mainCalendar.getTime());
		startCalendar.setTime(mainCalendar.getTime());
		endCalendar.setTime(mainCalendar.getTime());
		
//		mainLayout.addComponent(startTime);
//		mainLayout.addComponent(endTime);
//		addComponent(mainLayout);
		addComponent(startTime);
		addComponent(endTime);
		
		startTime.setValue(mainCalendar.getTime());
		endTime.setValue(mainCalendar.getTime());
		
		
		startTime.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(startTime.getValue()!= null)
					startCalendar.setTime(startTime.getValue());
				if(endTime.isVisible())
					isValid();
			}
		});
		
		
		
		endTime.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(endTime.getValue()!= null)
					endCalendar.setTime(endTime.getValue());
				if(endTime.isVisible())
					isValid();
			}
		});
		
		
		
	}
	
	
	public boolean isValid(){
		boolean valid=true;
		
		if(startTime.getValue()==null){
			setRequiredError(startTime, "Invalid Selection", true);
			valid=false;
		}
		else
			setRequiredError(startTime, null, false);
		
		if(endTime.getValue()==null){
			setRequiredError(endTime, "Invalid Selection", true);
			valid=false;
		}
		else
			setRequiredError(startTime, null, false);
		
		if(startTime.getValue()!=null && endTime.getValue()!=null){
			if(startCalendar.get(Calendar.AM_PM)==Calendar.PM) {
				if(endCalendar.get(Calendar.AM_PM)==Calendar.AM) {
					if(startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)){
						endCalendar.add(Calendar.DAY_OF_MONTH, 1);
					}
				}
				else {
					endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
				}
			}
			else{
				endCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
			}
			endTime.setValue(endCalendar.getTime());
		}
		
		if(startTime.getValue()!=null && endTime.getValue()!=null){
			if(startCalendar.getTime().getTime()>endCalendar.getTime().getTime()){
				setRequiredError(startTime, "Invalid Selection", true);
				valid=false;
			}
			else
				setRequiredError(startTime, null, false);
		}
		return valid;
	}
	
	
	public void setReadOnly(boolean isReadOnly){
		startTime.setReadOnly(isReadOnly);
		endTime.setReadOnly(isReadOnly);
	}

	
	public boolean isReadOnly(){
		boolean isReadOnly=false;
		isReadOnly=startTime.isReadOnly();
		isReadOnly=endTime.isReadOnly();
		return isReadOnly;
	}
	
	
	public void setValue(Date start, Date end){
		startTime.setValue(start);
		endTime.setValue(end);
	}
	
	
	
	public void setTime(Date time){
		startTime.setValue(time);
	}
	
	
	
	public void setNewValue(Date start, Date end){
		boolean isStartReadOnly=false;
		boolean isEndReadOnly=false;
		isStartReadOnly=startTime.isReadOnly();
		isEndReadOnly=endTime.isReadOnly();
				
		if(isStartReadOnly)
			startTime.setReadOnly(false);
		if(isEndReadOnly)
			endTime.setReadOnly(false);
		
		startTime.setValue(start);
		endTime.setValue(end);
		
		startTime.setReadOnly(isStartReadOnly);
		endTime.setReadOnly(isEndReadOnly);
		
	}

	
	
	public void setNewTime(Date time){
		boolean isTimeReadOnly=false;
		isTimeReadOnly=startTime.isReadOnly();
				
		if(isTimeReadOnly)
			startTime.setReadOnly(false);
		
		startTime.setValue(time);
		
		startTime.setReadOnly(isTimeReadOnly);
		
	}

	
	
	public int getMinutes(){
		int minutes=0;
		 try {
			if(isValid()){
				minutes=(int) ((endCalendar.getTime().getTime()-startCalendar.getTime().getTime())/(1000*60));
			}
		} catch (Exception e) {
			minutes=0;
		}
		return minutes;
	}
	
	
	
	public void resetAll(){
		boolean isStartReadOnly=false;
		boolean isEndReadOnly=false;
		
		isStartReadOnly=startTime.isReadOnly();
		isEndReadOnly=endTime.isReadOnly();
				
		if(isStartReadOnly)
			startTime.setReadOnly(false);
		if(isEndReadOnly)
			endTime.setReadOnly(false);
		
		startTime.setValue(mainCalendar.getTime());
		endTime.setValue(mainCalendar.getTime());
		
		startTime.setReadOnly(isStartReadOnly);
		endTime.setReadOnly(isEndReadOnly);
	}
	
	
	
	public void setRequiredError(AbstractComponent component,String fieldNameToDisplay, boolean enable) {
		if (enable) {
			component.setComponentError(new SUserError("<i style='font-size: 13px;'>" + fieldNameToDisplay,
					ContentMode.HTML, ErrorLevel.CRITICAL));
		} else
			component.setComponentError(null);
	}
	
}
