/**
 * 
 */
package com.inventory.management.ui;


import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartClickEvent;
import com.vaadin.addon.charts.ChartClickListener;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.util.Util;
import com.vaadin.ui.Notification;
import com.webspark.Components.SPanel;

/**
 * @author Anil K.P
 *
 */
public class TaskChartUI extends SPanel{

	private static final long serialVersionUID = -2313292851110291235L;

	public TaskChartUI() {
		 
	}

	public void drawChart(Date stDt, Date endDt, Date actEndDt) {
		final  Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");

        Configuration configuration = new Configuration();
        configuration.getChart().setType(ChartType.SPLINE);

        configuration.getxAxis().setType(AxisType.DATETIME);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(2013, 2, 11);

        DataSeries dataSeries = new DataSeries();
        Number[] values = new Number[] { 71.5, 29.9, 106.4 };
        Random r = new Random(0);
        for (Number number : values) {
            c.add(Calendar.MINUTE, r.nextInt(5));
            DataSeriesItem item = new DataSeriesItem(c.getTime(), number);
            dataSeries.add(item);
        }
        configuration.addSeries(dataSeries);
        chart.drawChart(configuration);

        chart.addChartClickListener(new ChartClickListener() {

            @Override
            public void onClick(ChartClickEvent event) {
                /*
                 * The axis value is in client side library's raw format: unix
                 * timestamp, "shifted" to UTC time zone
                 */;
                double timeStampShiftedToUc = event.getxAxisValue();
                /*
                 * When working with Date objects, developers probably want to
                 * convert it to Date object at their local time zone.
                 */
                Notification.show("Clicked @ "
                        + Util.toServerDate(timeStampShiftedToUc).toString());
            }
        });
        
        chart.addPointClickListener(new PointClickListener() {
			
			@Override
			public void onClick(PointClickEvent event) {
				/*
                 * Same with point clicks...
                 */;
                double timeStampShiftedToUc = event.getX();
                Notification.show("Clicked Point with Date value "
                        + Util.toServerDate(timeStampShiftedToUc).toString());
            }
        });

        setContent(chart);
		
	}
}
