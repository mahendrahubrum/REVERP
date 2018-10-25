package com.inventory.reports.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.reports.dao.ItemReportDao;
import com.inventory.sales.bean.SalesChartBean;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.common.util.CommonUtil;

/**
 * 
 * @author anil
 * @date 03-Jun-2016
 * @Project REVERP
 */
public class ItemMovementChartUI extends SContainerLayout {
	private static final long serialVersionUID = 1440545198147025764L;

	private SFormLayout chartLayout;

	private Chart chart;
	private Configuration conf;
	private PlotOptionsPie plotOptions;
	
	SComboField itemBox;
	
	private ItemReportDao  dao;
	
	public ItemMovementChartUI(Date fromDate,Date toDate) {
		try {
			
			setSize(500, 350);
			SPanel panel = new SPanel();
			panel.setSizeFull();
			SFormLayout layout = new SFormLayout();
			layout.setMargin(true);
			SHorizontalLayout horLayout = new SHorizontalLayout();
			horLayout.setSpacing(true);
			
			dao=new ItemReportDao();
			
			itemBox=new SComboField("Item",200,new ItemDao().getAllActiveItems(getOfficeID()),"id","name");

			chartLayout = new SFormLayout();
			chartLayout.setSizeFull();

			chart = new Chart();
	        chart.setHeight("450px");
	        chart.setWidth("100%");

	        conf = chart.getConfiguration();
	        conf.getChart().setType(ChartType.LINE);
	        conf.getxAxis().setMin(fromDate);
			 conf.getxAxis().setMax(toDate);
	        conf.getTitle().setText("Monthly Average Temperature");

	       

	        YAxis yAxis = conf.getyAxis();
//	        yAxis.setTitle(new AxisTitle("Temperature (°C)"));

	        conf
	                .getTooltip()
	                .setFormatter(
	                        "'<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +'°C'");

	        PlotOptionsLine plotOptions = new PlotOptionsLine();
	        plotOptions.setDataLabels(new Labels(true));
	        plotOptions.setEnableMouseTracking(false);
	        conf.setPlotOptions(plotOptions);

	        ListSeries ls = new ListSeries();
	        ls.setName("Tokyo");
	        ls.setData(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3,
	                13.9, 9.6);
	        conf.addSeries(ls);

	        ls = new ListSeries();
	        ls.setName("London");
	        ls.setData(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6,
	                4.8);
	        conf.addSeries(ls);

	        chart.drawChart(conf);
			
			
			if(conf.getSeries().isEmpty()){
				SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
			}
			
			addComponent(chart);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	

	@Override
	public void getChart(Date fromDate, Date toDate) {

		
//		conf.setSeries(getSalesDetails(fromDate,toDate));
//		chart.drawChart();
		if(conf.getSeries().isEmpty()){
			SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
		}
		
	}
	

	protected DataSeries getSalesDetails(Date fromDate, Date toDate) {
		DataSeries dataSeries = new DataSeries();
		dataSeries.setName("Quantity");
		List<Object> list = new ArrayList<Object>();
		DataSeriesItem seriesItem;
		SalesChartBean bean;
		
		
		try {
			list.addAll(dao.getTopSaledItems(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),getOfficeID()));

			if(list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				bean = (SalesChartBean) list.get(i);
				
				
				bean = (SalesChartBean) list.get(i);
				seriesItem = new DataSeriesItem(bean.getName()
						, roundNumber(bean.getAmount()));
				if(i==0)
					seriesItem.setSelected(true);
//				seriesItem.setColor(ChartOptions.get().getTheme().getColors()[1]);
				dataSeries.add(seriesItem);
				
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSeries;
	}

	private GradientColor createRadialGradient(SolidColor start, SolidColor end) {
		GradientColor color = GradientColor.createRadial(0.5, 0.3, 0.7);
		color.addColorStop(0, start);
		color.addColorStop(1, end);
		return color;
	}
}
