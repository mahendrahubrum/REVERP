package com.webspark.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;


public class TEst {

	public static void main(String[] args) {
		
		
		
		
		
		
		
		System.out.println(new TEst().ConvertSecondToHHMMString(8000));
		
		
		
		double val=155.5545;
//		System.out.println(new TEst().roundNumber(val));
		
		String str="samosa";
		
		char[] arr=str.toCharArray();
		
//		Character[] chr=new Character[str.length()];
		
		Character[] chr=ArrayUtils.toObject(arr);
		
		ArrayList al=new ArrayList();
		Collections.addAll(al, chr);
		Character c;
		
		Iterator iter=al.iterator();
		while (iter.hasNext()) {
			c = (Character) iter.next();
			int occ=Collections.frequency(al,c);
			
			if(occ==1){
				System.out.println(c);
				break;
			}
			
			
		}
		
		
	}
	
	private String ConvertSecondToHHMMString(int secondtTime)
	{
	  TimeZone tz = TimeZone.getTimeZone("UTC");
	  SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	  df.setTimeZone(tz);
	  String time = df.format(new Date(secondtTime*1000L));

	  return time;

	}
	
	
	public  double roundNumber(double val) {
		
		DecimalFormat df = new DecimalFormat("#.###");
		
	
		
//		int Rpl = 2;
//		double p = (double) Math.pow(10, Rpl);
//		val *= p;
//		double tmp = Math.round(val);
//		return (double) tmp / p;
		return Double.parseDouble(new BigDecimal(df.format(val)).setScale(3, BigDecimal.ROUND_HALF_UP).toString());
	}
}
