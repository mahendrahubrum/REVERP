package com.inventory.reports.charts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inventory.reports.dao.ItemReportDao;
import com.inventory.sales.bean.SalesChartBean;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.GridTheme;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 21, 2014
 */
public class TopSalesItemChartUI extends SContainerLayout {

	
	private static final long serialVersionUID = -1817633881165793429L;

	private SFormLayout chartLayout;

	private Chart chart;
	private Configuration conf;
	private PlotOptionsPie plotOptions;
	
	private ItemReportDao  dao;

	@Override
	public void getChart(Date fromDate, Date toDate) {

		setSize(500, 400);
//		SPanel panel = new SPanel();
//		panel.setSizeFull();
//		SVerticalLayout layout = new SVerticalLayout();
//		layout.setMargin(true);
		
		removeAllComponents();
		
		dao=new ItemReportDao();

		chartLayout = new SFormLayout();
		chartLayout.setSizeFull();

		chart = new Chart(ChartType.PIE);
		chart.setSizeFull();
		chart.setHeight("300px");
		conf = chart.getConfiguration();
		conf.disableCredits();
		conf.setTitle((String)null);
		conf.setExporting(true);

//		conf.getxAxis().setType(AxisType.DATETIME);
//		conf.getxAxis().setDateTimeLabelFormats(
//                new DateTimeLabelFormats("%e. %b", "%b"));
//		conf.getxAxis().setMinRange(86400000);
//		conf.getxAxis().setTitle("Date");
//		conf.getyAxis().setTitle("Amount");
//		conf.getyAxis().setMin(0);
//		conf.getxAxis().setMin(fromDate);
//		conf.getxAxis().setMax(toDate);
		
		ChartOptions.get().setTheme(new GridTheme());

		chartLayout.addComponent(chart);
//		layout.addComponent(chartLayout);
//		panel.setContent(chart);
		
		plotOptions = new PlotOptionsPie();
		plotOptions.setCursor(Cursor.POINTER);
		plotOptions.setShowInLegend(true);
		Labels dataLabels = new Labels();
		dataLabels.setEnabled(true);
		dataLabels.setColor(SolidColor.BLACK);
		dataLabels.setConnectorColor(SolidColor.BLACK);
		dataLabels.setFormatter("''+ this.point.name +': '+ this.y +' '");
		plotOptions.setDataLabels(dataLabels);
		conf.setPlotOptions(plotOptions);
		conf.setSeries(getSalesDetails(fromDate,toDate));
		
		conf.setTitle(getPropertyName("top_sold_items"));
		if(conf.getSeries().isEmpty()){
			SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
		}
		chart.drawChart();
		
		addComponent(chart);
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
