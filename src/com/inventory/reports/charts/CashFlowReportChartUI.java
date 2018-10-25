package com.inventory.reports.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.reports.bean.AcctReportMainBean;
import com.inventory.reports.dao.CashFlowReportDao;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.DateTimeLabelFormats;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.themes.GridTheme;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.common.util.CommonUtil;

public class CashFlowReportChartUI extends SContainerLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SFormLayout chartLayout;
	private Chart chart;
	private Configuration conf;
	private CashFlowReportDao daoObj;
	private SettingsValuePojo settings;

	@Override
	public void getChart(Date fromDate, Date toDate) {

		setSize(550, 450);
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		SHorizontalLayout horLayout = new SHorizontalLayout();
		horLayout.setSpacing(true);

		daoObj = new CashFlowReportDao();
		settings = (SettingsValuePojo) getHttpSession()
				.getAttribute("settings");
		
		chartLayout = new SFormLayout();
		chartLayout.setSizeFull();

		chart = new Chart(ChartType.LINE);
		chart.setSizeFull();
		
		conf = chart.getConfiguration();
		conf.disableCredits();
		conf.setTitle((String) null);
		conf.setExporting(true);

		conf.getxAxis().setType(AxisType.DATETIME);
		conf.getxAxis().setDateTimeLabelFormats(
                new DateTimeLabelFormats("%e.%b.%y", "%Y"));
		conf.getxAxis().setTitle("Date");
		conf.getyAxis().setTitle("Amount");
	//	conf.getyAxis().setMin(-5);
		conf.getxAxis().setMin(fromDate);
		conf.getxAxis().setMax(toDate);
		
		conf.getTooltip().setFormatter(
				" this.y+' ' +this.point.name+'('+new Date(this.x).toUTCString() +')' ");
		
		ChartOptions.get().setTheme(new GridTheme());

		chartLayout.addComponent(chart);
		layout.addComponent(horLayout);
		layout.addComponent(chartLayout);
		panel.setContent(layout);


		conf.setTitle("");

		conf.setSeries(getCashFlowDetails(fromDate,toDate));
		
//		Legend legend = new Legend();
//        legend.setEnabled(true);
//        conf.setLegend(legend);
        
		chart.drawChart(conf);
		
		
		addComponent(chart);

	}

	@SuppressWarnings("unchecked")
	private List<Series> getCashFlowDetails(Date fromDate, Date toDate) {
		List<Series> dataSeriesList = new ArrayList<Series>();
		
		List<AcctReportMainBean> resultList = new ArrayList<AcctReportMainBean>();
		DataSeriesItem seriesItem;
		AcctReportMainBean bean = null;
		DataSeries creditListSeries = new DataSeries();
		creditListSeries.setName("Cr");

		DataSeries debitListSeries = new DataSeries();
		debitListSeries.setName("Dr");
		
		DataSeries balanceListSeries = new DataSeries();
		balanceListSeries.setName("Balance");
		
	

		try {
			resultList = daoObj.getCashFlowReportChart(getOfficeID(),
					CommonUtil.getSQLDateFromUtilDate(fromDate),
					CommonUtil.getSQLDateFromUtilDate(toDate), settings);

			if (resultList.size() > 0) {

				Collections.sort(resultList,
						new Comparator<AcctReportMainBean>() {
							@Override
							public int compare(
									final AcctReportMainBean object1,
									final AcctReportMainBean object2) {
								return object1.getDate().compareTo(
										object2.getDate());
							}
						});
//				double amount = daoObj.getCashFlowOpeningBalance(getOfficeID(),
//								(long)0,
//								CommonUtil.getSQLDateFromUtilDate(fromDate),
//								settings);
				double amount = 0;
				Date prevDate = fromDate;
				Date currDate = fromDate;
				boolean isFirst = true;
				for (int i = 0; i < resultList.size(); i++) {
					
					bean = (AcctReportMainBean) resultList.get(i);		
					
					if(isFirst){
						prevDate = bean.getDate();
						isFirst = false;
					} else {
						prevDate = currDate;
					}
					
					currDate = bean.getDate();
					
					if(prevDate.compareTo(currDate) != 0){
						seriesItem = new DataSeriesItem(currDate, roundNumber((amount)));
						if(amount < 0){
							seriesItem.setName("Dr (Balance)");
						}else {
							seriesItem.setName("Cr (Balance)");
						}
						seriesItem.setColor(ChartOptions.get().getTheme().getColors()[1]);
						
						balanceListSeries.add(seriesItem);
						amount = 0;
					}
					
					
					seriesItem = new DataSeriesItem(bean.getDate(), roundNumber(bean.getAmount()));
			//		seriesItem.setName(bean.getName());
					seriesItem.setColor(ChartOptions.get().getTheme().getColors()[1]);
					
					if (bean.getAmount_type().trim().equals("Dr")) {
						amount -= roundNumber(bean.getAmount());
						
						seriesItem.setName("Dr ("+bean.getName()+")");
						debitListSeries.add(seriesItem);
					} else {
						amount += roundNumber(bean.getAmount());
						
						seriesItem.setName("Cr ("+bean.getName()+")");
						creditListSeries.add(seriesItem);
					}
					
					
				}			
				
				dataSeriesList.add(debitListSeries);
				dataSeriesList.add(creditListSeries);
				dataSeriesList.add(balanceListSeries);
				
				/*conf.addSeries(debitListSeries);
				conf.addSeries(creditListSeries);*/
				/* dataSeries.addData(creditList); */
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dataSeriesList;
	}

}
