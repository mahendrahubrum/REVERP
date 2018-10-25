package com.inventory.reports.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inventory.sales.bean.SalesChartBean;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.dao.SalesNewDao;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.GridTheme;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.common.util.CommonUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 20, 2014
 */
public class BalanceSheetChartUI extends SContainerLayout {

	private static final long serialVersionUID = 7807356878666068650L;
	
	private SFormLayout chartLayout;

	private Chart chart;
	private Configuration conf;
	private PlotOptionsPie plotOptions;
	
	private SalesDao  dao;
	

	@Override
	public void getChart(Date fromDate, Date toDate) {

		setSize(550, 450);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		SHorizontalLayout horLayout = new SHorizontalLayout();
		horLayout.setSpacing(true);
		
		dao=new SalesDao();

		chartLayout = new SFormLayout();
		chartLayout.setSizeFull();

		chart = new Chart(ChartType.COLUMN);
		chart.setSizeFull();
		conf = chart.getConfiguration();
		conf.disableCredits();
		conf.setTitle((String)null);
		conf.setExporting(true);

		conf.getxAxis().setType(AxisType.DATETIME);
		conf.getxAxis().setDateTimeLabelFormats(
                new DateTimeLabelFormats("%e. %b", "%b"));
		conf.getxAxis().setMinRange(86400000);
		conf.getxAxis().setTitle("Date");
		conf.getyAxis().setTitle("Amount");
		conf.getyAxis().setMin(0);
		conf.getxAxis().setMin(fromDate);
		conf.getxAxis().setMax(toDate);
		
		conf.disableCredits();
		
		ChartOptions.get().setTheme(new GridTheme());

		chartLayout.addComponent(chart);
		layout.addComponent(horLayout);
		layout.addComponent(chartLayout);
		panel.setContent(layout);


		conf.setTitle("");

		conf.setSeries(getSalesDetails(fromDate,toDate));
		
		Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);
        
		chart.drawChart();
		
		if(conf.getSeries().isEmpty()){
			SNotification.show("No data available",Type.WARNING_MESSAGE);
		}
		
		addComponent(chart);
	}
	

	protected DataSeries getSalesDetails(Date fromDate, Date toDate) {
		DataSeries dataSeries = new DataSeries();
		dataSeries.setName("Amount");
		List<Object> list = new ArrayList<Object>();
		DataSeriesItem seriesItem;
		SalesChartBean bean;
		
		
		try {
			list.addAll(dao.getSalesChartDetails(CommonUtil.getSQLDateFromUtilDate(fromDate),CommonUtil.getSQLDateFromUtilDate(toDate),getOfficeID()));

			if(list.size()>0){
				int color=0;
			for (int i = 0; i < list.size(); i++) {
				bean = (SalesChartBean) list.get(i);
				
				
				seriesItem = new DataSeriesItem(bean.getDate(),
						roundNumber(bean.getAmount()));
				seriesItem.setSliced(true);
//				seriesItem.setSelected(true);
				seriesItem.setColor(ChartOptions.get().getTheme().getColors()[1]);
				dataSeries.add(seriesItem);
				
				color++;
				
				if(color>8)
					color=0;
				
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
