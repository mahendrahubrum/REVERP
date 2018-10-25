package com.webspark.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.charts.TopSalesItemChartUI;
import com.inventory.reports.ui.AlertPanel;
import com.inventory.reports.ui.LoginAlertPopup;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SDateField;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.DBOperations;
import com.webspark.dao.HomePageDao;
import com.webspark.dao.LanguageMappingDao;
import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_OptionModel;

/**
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Aug 19, 2014
 */
public class HomePageUI extends SContainerPanel{
	
	private static final long serialVersionUID = -5595029673624059987L;
	
	DBOperations dbopDao=new DBOperations();
	SGridLayout optionsGrid=new SGridLayout();
	WrappedSession session=new SessionUtil().getHttpSession();
	SettingsValuePojo settings;
	@SuppressWarnings("static-access")
	public HomePageUI(long login_id, final long officeId, LayoutClickListener clickListener) {
		SVerticalLayout verticalLayout=new SVerticalLayout();
		verticalLayout.setSpacing(true);
		
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		SHorizontalLayout chartLay=new SHorizontalLayout();
		chartLay.setSpacing(true);
		
//		java.util.Calendar cal = java.util.Calendar.getInstance();
//		cal.setTime(getWorkingDate());
//		cal.set(cal.DAY_OF_MONTH, 1);
//		Date fromDate=cal.getTime();
//		cal.set(cal.DAY_OF_MONTH,cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
//		Date toDate=cal.getTime();
		
		
		final	SDateField fromDateField;
		final	SDateField toDateField;
		
		fromDateField=new SDateField(null,100,getDateFormat(),getMonthStartDate());
		toDateField=new SDateField(null,100,getDateFormat(),getWorkingDate());
		
		SHorizontalLayout dateLay=new SHorizontalLayout();
		dateLay.setSpacing(true);
		dateLay.addComponent(new SLabel("From Date"));
		dateLay.addComponent(fromDateField);
		dateLay.addComponent(new SLabel("To Date"));
		dateLay.addComponent(toDateField);
		
		final TopSalesItemChartUI topChart=new TopSalesItemChartUI();
		final SVerticalLayout tileLay=new SVerticalLayout();
		
		fromDateField.setImmediate(true);
		fromDateField.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				topChart.getChart(fromDateField.getValue(), toDateField.getValue());
				tileLay.removeAllComponents();
				tileLay.addComponent(createTiles(fromDateField.getValue(), toDateField.getValue(),officeId));
			}
		});
		toDateField.setImmediate(true);
		toDateField.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				topChart.getChart(fromDateField.getValue(), toDateField.getValue());
				tileLay.removeAllComponents();
				tileLay.addComponent(createTiles(fromDateField.getValue(), toDateField.getValue(),officeId));
			}
		});
		
		topChart.getChart(toDateField.getValue(), fromDateField.getValue());
		tileLay.removeAllComponents();
		tileLay.addComponent(createTiles(fromDateField.getValue(), toDateField.getValue(),officeId));
		
//		ItemMovementChartUI chart=new ItemMovementChartUI(fromDate, toDate);
//		chart.getChart(fromDate, toDate);
		
//		TaskCalendarUI calendar=new TaskCalendarUI();
//		calendar.getChart(fromDate, toDate);
		
		SVerticalLayout verLay=new SVerticalLayout();
		verLay.addComponent(dateLay);
		verLay.addComponent(topChart);
		
		chartLay.addComponent(verLay);
//		chartLay.addComponent(chart);
		chartLay.addComponent(tileLay);
		
		verticalLayout.addComponent(chartLay);
		
		loadRecentlyUsed(login_id, officeId, clickListener);
		verticalLayout.addComponent(new SVerticalLayout(new SHTMLLabel(null, "<h2 style='padding-left:2em'>"+getPropertyName("recently_used_options")+"</h2>"),optionsGrid));
		verticalLayout.addComponent(new AlertPanel());
		setContent(verticalLayout);
		
		
	}
	
private Component createTiles(Date fromDate,Date toDate,long officeId) {
	
	HomePageDao dao=new HomePageDao();
		
		SVerticalLayout verticalLayout=new SVerticalLayout();
		verticalLayout.setMargin(true);
		
		SGridLayout grid=new SGridLayout(2,3);
		grid.setSpacing(true);
		
		try {

			SVerticalLayout vLay = new SVerticalLayout();
			SLabel titleLabel = new SLabel(null, "Sales");
			titleLabel.setStyleName("dashboard_cell_small_label_style");
			SLabel numberLabel = new SLabel(null,dao.getTotalSalesCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),officeId)+"");
			numberLabel.setStyleName("dashboard_cell_big_label_style");
			vLay.addComponent(titleLabel);
			vLay.addComponent(numberLabel);
			vLay.setStyleName("dashboard_cell_orange_style");
			grid.addComponent(vLay);

			vLay = new SVerticalLayout();
			titleLabel = new SLabel(null, "Purchases");
			titleLabel.setStyleName("dashboard_cell_small_label_style");
			numberLabel = new SLabel(null, ""+dao.getTotalPurchaseCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),officeId));
			numberLabel.setStyleName("dashboard_cell_big_label_style");
			vLay.addComponent(titleLabel);
			vLay.addComponent(numberLabel);
			vLay.setStyleName("dashboard_cell_blue_style");
			grid.addComponent(vLay);

			vLay = new SVerticalLayout();
			titleLabel = new SLabel(null, "Sales Returns");
			titleLabel.setStyleName("dashboard_cell_small_label_style");
			numberLabel = new SLabel(null, ""+dao.getTotalSalesReturnCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),officeId));
			numberLabel.setStyleName("dashboard_cell_big_label_style");
			vLay.addComponent(titleLabel);
			vLay.addComponent(numberLabel);
			vLay.setStyleName("dashboard_cell_green_style");
			grid.addComponent(vLay);

			vLay = new SVerticalLayout();
			titleLabel = new SLabel(null, "Purchase Returns");
			titleLabel.setStyleName("dashboard_cell_small_label_style");
			numberLabel = new SLabel(null, ""+dao.getTotalPurchaseReturnCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),officeId));
			numberLabel.setStyleName("dashboard_cell_big_label_style");
			vLay.addComponent(titleLabel);
			vLay.addComponent(numberLabel);
			vLay.setStyleName("dashboard_cell_red_style");
			grid.addComponent(vLay);

			verticalLayout.addComponent(grid);

//			vLay = new SVerticalLayout();
			
//			numberLabel = new SLabel(null, ""+dao.getTotalSalesCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate)));
//			SLabel numberLabel2 = new SLabel(null, ""+dao.getTotalSalesCount(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate)));
//			numberLabel.setStyleName("dashboard_cell_big_label_style");
//			numberLabel2.setStyleName("dashboard_cell_big_label_style");
//			SGridLayout hl=new SGridLayout(2,2);
//			hl.setWidth("350px");
//			hl.setColumnExpandRatio(0, 10f);
//			hl.setColumnExpandRatio(1, 10f);
//			titleLabel = new SLabel(null, "Active Tasks");
//			titleLabel.setStyleName("dashboard_cell_small_label_style");
//			hl.addComponent(titleLabel);
//			titleLabel = new SLabel(null, "Finished Tasks");
//			titleLabel.setStyleName("dashboard_cell_small_label_style");
//			hl.addComponent(titleLabel);
//			hl.addComponent(numberLabel);
//			hl.addComponent(numberLabel2);
//			vLay.addComponent(hl);
//			vLay.setStyleName("dashboard_cell_grey_style");

//			verticalLayout.addComponent(vLay);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return verticalLayout;
	}

	
	protected void loadRecentlyUsed(long login_id, long officeId,
			LayoutClickListener clickListener) {
		try {
			optionsGrid.removeAllComponents();
			SHorizontalLayout menu = null;
			SHTMLLabel label = null;
			optionsGrid.setSizeFull();
			optionsGrid.setColumns(SConstants.RECENTLY_USED_OPTIONS_COUNT);
			optionsGrid.setStyleName("rec_acces_options_btn");
			optionsGrid.setHeight("70px");
			optionsGrid.setWidth("95%");
			S_OptionModel optionModel;
			List optionList = dbopDao.getRecentlyAccessesOptions(login_id,
					officeId);
			Iterator it = optionList.iterator();
			while (it.hasNext()) {
				optionModel = (S_OptionModel) it.next();
				S_LanguageMappingModel optmdl=new LanguageMappingDao()
														.getLanguageMappingModel((long)3, 
																				Long.parseLong(session.getAttribute("language_id")+""), 
																				optionModel.getOption_id());
				menu = new SHorizontalLayout();
				if(optmdl!=null)
					label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+ optmdl.getName() + "</b><center>");
				else
					label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+ optionModel.getOption_name() + "</b><center>");
				label.setWidth("100%");
				menu.setId("" + optionModel.getOption_id());
				menu.addComponent(label);
				menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
				menu.setStyleName("button outline-inward");
				menu.setWidth("90%");
				menu.setHeight("40px");
				optionsGrid.addComponent(menu);
				optionsGrid.setComponentAlignment(menu, Alignment.TOP_CENTER);
			}
			optionsGrid.addLayoutClickListener(clickListener);

		} catch (Exception e) {
		}

	}
	
	@SuppressWarnings("static-access")
	public void showAlert(){
		if(!settings.isHIDE_ALERTS()){
			SparkLogic pop = new LoginAlertPopup();
			if (pop.getContent() != null) {
				pop.center();
				pop.setModal(true);
				pop.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
				pop.setCaption(getPropertyName("alerts")+"...!");
				getUI().getCurrent().addWindow(pop);
				pop.focus();
			}
		}
	}
	
	public String getPropertyName(String name) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(new SessionUtil().getHttpSession().getAttribute("property_file").toString());
			if (bundle != null)
				name = bundle.getString(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;
	}
	
}
