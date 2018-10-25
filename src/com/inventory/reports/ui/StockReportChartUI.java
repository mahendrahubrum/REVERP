package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.purchase.model.StockRackMappingModel;
import com.inventory.reports.dao.StockReportDao;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 20, 2013
 */
public class StockReportChartUI extends SparkLogic {

	private static final long serialVersionUID = -2685949712478465772L;

	private SComboField itemComboField;
	private SFormLayout chartLayout;

	private Chart chart;
	private Configuration conf;
	private PlotOptionsPie plotOptions;

	@Override
	public SPanel getGUI() {
		setSize(800, 600);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);

		chartLayout = new SFormLayout();
		chartLayout.setSizeFull();

		List itemList = null;
		try {
			itemList = new ItemDao()
					.getAllActiveItemsWithAppendingItemCode(getOfficeID());
		} catch (Exception e) {
			itemList = new ArrayList();
			e.printStackTrace();
		}
		itemComboField = new SComboField(getPropertyName("item"), 200,
				itemList, "id", "name");
		itemComboField.setInputPrompt(getPropertyName("select"));
		layout.addComponent(itemComboField);

		chart = new Chart(ChartType.PIE);
		conf = chart.getConfiguration();
		conf.disableCredits();
		conf.setTitle((String) null);

		plotOptions = new PlotOptionsPie();
		plotOptions.setCursor(Cursor.NONE);
		plotOptions.setShowInLegend(true);
		Labels dataLabels = new Labels();
		dataLabels.setEnabled(true);
		dataLabels.setColor(SolidColor.BLACK);
		dataLabels.setConnectorColor(SolidColor.BLACK);
		dataLabels.setFormatter("''+ this.point.name +': '+ this.y +' '");
		plotOptions.setDataLabels(dataLabels);
		conf.setPlotOptions(plotOptions);

		chartLayout.addComponent(chart);
		layout.addComponent(chartLayout);
		panel.setContent(layout);

		itemComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// chartLayout.removeAllComponents();

				conf.setTitle("Stock Details Of "
						+ itemComboField.getItemCaption(itemComboField
								.getValue()));

				conf.setSeries(getStockDetails(toLong(itemComboField.getValue()
						.toString())));

				chart.drawChart();

				if (conf.getSeries().isEmpty()) {
					SNotification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}

			}
		});
		return panel;
	}

	protected DataSeries getStockDetails(long itemId) {
		DataSeries dataSeries = new DataSeries();
		dataSeries.setName("Count");
		List<Object> list = new ArrayList<Object>();
		StockRackMappingModel model = null;
		DataSeriesItem seriesItem;
		try {
			list.addAll(new StockReportDao().getRackDetails(itemId));

			for (int i = 0; i < list.size(); i++) {
				model = (StockRackMappingModel) list.get(i);
				seriesItem = new DataSeriesItem(model.getRack()
						.getRack_number(), model.getQuantity());
				// seriesItem.setColor(createRadialGradient(new SolidColor(
				// 1 + (int)(Math.random() * ((255 - 1) + 1)), 1 +
				// (int)(Math.random() * ((255 - 1) + 1)), 0),
				// new SolidColor(1 + (int)(Math.random() * ((255 - 1) + 1)), 1
				// + (int)(Math.random() * ((255 - 1) + 1)), 0)));
				dataSeries.add(seriesItem);
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
