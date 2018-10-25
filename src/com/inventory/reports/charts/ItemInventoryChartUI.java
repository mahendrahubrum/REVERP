package com.inventory.reports.charts;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.reports.dao.ItemReportDao;
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
import com.vaadin.addon.charts.themes.GrayTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 20, 2013
 */
public class ItemInventoryChartUI extends SparkLogic {

	private static final long serialVersionUID = 7807356878666068650L;
	
	private SComboField itemComboField;
	private SDateField toDateField;
	private SFormLayout chartLayout;

	private Chart chart;
	private Configuration conf;
	private PlotOptionsPie plotOptions;
	
	private ItemReportDao dao;

	@Override
	public SPanel getGUI() {
		setSize(800, 600);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		SHorizontalLayout horLayout = new SHorizontalLayout();
		horLayout.setSpacing(true);
		
		dao=new ItemReportDao();

		chartLayout = new SFormLayout();
		chartLayout.setSizeFull();

		List itemList = null;
		try {
			itemList = new ItemDao().getAllActiveItemsWithAppendingItemCode(getOfficeID());
		} catch (Exception e) {
			itemList = new ArrayList();
			e.printStackTrace();
		}
		itemComboField = new SComboField(null, 200, itemList, "id", "name");
		itemComboField.setInputPrompt(getPropertyName("select"));
		horLayout.addComponent(new SLabel(getPropertyName("item")));
		horLayout.addComponent(itemComboField);
		
		toDateField=new SDateField(null,100,getDateFormat(),getWorkingDate());
		toDateField.setImmediate(true);
		horLayout.addComponent(new SLabel(getPropertyName("date")));
		horLayout.addComponent(toDateField);

		chart = new Chart(ChartType.PIE);
		chart.setSizeFull();
		conf = chart.getConfiguration();
		conf.disableCredits();
		conf.setTitle((String)null);
		conf.setExporting(true);
		
		ChartOptions.get().setTheme(new GrayTheme());

		plotOptions = new PlotOptionsPie();
		plotOptions.setCursor(Cursor.POINTER);
		plotOptions.setShowInLegend(true);
		Labels dataLabels = new Labels();
		dataLabels.setEnabled(true);
		dataLabels.setColor(SolidColor.WHITESMOKE);
		dataLabels.setConnectorColor(SolidColor.WHITESMOKE);
		dataLabels.setFormatter("''+ this.point.name +': '+ this.y +' '");
		plotOptions.setDataLabels(dataLabels);
		conf.setPlotOptions(plotOptions);

		chartLayout.addComponent(chart);
		layout.addComponent(horLayout);
		layout.addComponent(chartLayout);
		panel.setContent(layout);

		itemComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

				redrawChart();

			}
		});
		
		toDateField.addListener(new Listener() {
			
			@Override
			public void componentEvent(Event event) {
				redrawChart();
			}
		});
		
		return panel;
	}

	protected void redrawChart() {
		conf.setTitle("Inventory Details Of "
				+ itemComboField.getItemCaption(itemComboField
						.getValue()));

		conf.setSeries(getStockDetails(toLong(itemComboField.getValue()
				.toString())));

		chart.drawChart();
		
		if(conf.getSeries().isEmpty()){
			SNotification.show("No data available",Type.WARNING_MESSAGE);
		}
		
	}

	protected DataSeries getStockDetails(long itemId) {
		DataSeries dataSeries = new DataSeries();
		dataSeries.setName("Count");
		List<Object> list = new ArrayList<Object>();
		DataSeriesItem seriesItem;
		ItemReportBean bean;
		try {
			list.addAll(dao.getItemInventoryDetails((Long)itemComboField.getValue(),CommonUtil.getSQLDateFromUtilDate(toDateField.getValue())));

			if(list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				bean = (ItemReportBean) list.get(i);
				seriesItem = new DataSeriesItem(bean.getName()
						, roundNumber(bean.getRate()));
//				seriesItem.setSliced(true);
				seriesItem.setSelected(true);
				dataSeries.add(seriesItem);
			}
			}else{
				SNotification.show("No data available",Type.WARNING_MESSAGE);
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

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}
}
