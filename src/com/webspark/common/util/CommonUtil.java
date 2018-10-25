package com.webspark.common.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import org.jsoup.Jsoup;

import com.vaadin.server.WrappedSession;

/**
 * @Author Jinshad P.T.
 */

public class CommonUtil {
	
	public static Date getSQLDateFromUtilDate(java.util.Date date){
		Date sql=null;
		try {
			sql=new Date(date.getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sql;
	}
	
	
	public static java.util.Date getUtilFromSQLDate(Date date){
		java.util.Date utDt=null;
		try {
			utDt=new java.util.Date(date.getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return utDt;
	}
	
	
	public static Timestamp getCurrentDateTime(){
		Timestamp ts=null;
		try {
			java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf =
			new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(getFormattedTime(dt));
			ts = Timestamp.valueOf(currentTime); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ts;
	}
	
	public static Timestamp getTimestampFromUtilDate(java.util.Date dt){
		Timestamp ts=null;
		try {
			ts = new Timestamp(dt.getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ts;
	}
	
	
	
	public static String getCurrentTime(){
		String currentTime="";
		try {
			java.util.Date dt = new java.util.Date();
			
			java.text.SimpleDateFormat sdf =
					new java.text.SimpleDateFormat("HH:mm:ss");
			
			currentTime = sdf.format(getFormattedTime(dt));
		} catch (Exception e) {
		}
		return currentTime;
	}
	
	public static Date getCurrentSQLDate(){
		Date sql=null;
		try {
			sql=new Date(getFormattedTime(new java.util.Date()).getTime());
		} catch (Exception e) {
		}
		return sql;
	}
	
	
	public static String formatDateToDDMMYYYY(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd-MM-yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	public static String formatDateToDDMMMYYYY(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd-MMM-yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	public static String formatSQLDateToDDMMMYYYY(Date sqlDate){
		String dateString ="";
		try {
			
			java.util.Date date=new java.util.Date(sqlDate.getTime());
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd-MMM-yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	
	public static String formatDateToMMDDYYYY(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("MM-dd-yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	public static String formatDateToYYYY(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	public static String getUtilDateFromSQLDate(Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd/MM/yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	public java.sql.Date getSQLDate(String pickDate) throws Exception {
		java.sql.Date sqlDate = null;
		try {
			String subDate = pickDate.substring(0, 10);
		} catch (Exception e) {
			throw new Exception("Exception in Date.getSQLDate."
					+ e.getMessage());
		}
		return sqlDate;
	}
	
	public static String formatDateToCommonFormat(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd/MM/yyyy");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	public static String formatDateToCommonDateTimeFormat(java.util.Date date){
		String dateString ="";
		try {
			
			SimpleDateFormat sdf =
					new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			dateString= sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}
	
	
	
	public static double roundNumber(double val) {
		int Rpl = 2;
		WrappedSession session=new SessionUtil().getHttpSession();
		if ( session!= null) {
			Rpl = (Integer) session.getAttribute("no_of_precisions");
		}
		double p = (double) Math.pow(10, Rpl);
		val *= p;
		double tmp = Math.round(val);
		return (double) tmp / p;
	}
	
	public static String roundNumberToString(double val) {
		int Rpl = 2;
		WrappedSession session=new SessionUtil().getHttpSession();
		if (session.getAttribute("no_of_precisions") != null) {
			Rpl = (Integer) session.getAttribute("no_of_precisions");
		}
//		double p = (double) Math.pow(10, Rpl);
//		val *= p;
//		double tmp = Math.round(val);
		return new BigDecimal(String.valueOf(val)).setScale(Rpl,BigDecimal.ROUND_CEILING).toString().toString();
	}
	
	public static double roundNumberTwoDigit(double val) {
		int Rpl = 2;
		double p = (double) Math.pow(10, Rpl);
		val *= p;
		double tmp = Math.round(val);
		return (double) tmp / p;
	}
	
	
	public static String removeHtml(String html) {
	    return Jsoup.parse(html).text();
	}
	
	public static java.util.Date getFormattedTime(java.util.Date date){
		DateFormat format = DateFormat.getDateTimeInstance();
		WrappedSession session=new SessionUtil().getHttpSession();
		if (session.getAttribute("time_zone") != null) 
			format.setTimeZone(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		
		return new java.util.Date(format.format(date));
	}
	
	public static java.util.Date getFormattedCurrentTime(){
		DateFormat format = DateFormat.getDateTimeInstance();
		WrappedSession session=new SessionUtil().getHttpSession();
		if (session.getAttribute("time_zone") != null) 
			format.setTimeZone(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		
		return new java.util.Date(format.format(new java.util.Date()));
	}
	
	
	public static String getMonthName(int monthId) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthId >= 0 && monthId <= 11 ) {
            month = months[monthId];
        }
        return month;
    }
	
	public static Vector getStartAndEndDate(java.util.Date currentDate){
		WrappedSession session=new SessionUtil().getHttpSession();
		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
		cal.setTime(currentDate);
		cal.set(cal.DAY_OF_MONTH, 1);
		java.util.Date fromDate=cal.getTime();
		cal.set(cal.DAY_OF_MONTH,cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		java.util.Date toDate=cal.getTime();
		Vector vec=new Vector();
		vec.add(fromDate);
		vec.add(toDate);
		return vec;
	}
	
}
