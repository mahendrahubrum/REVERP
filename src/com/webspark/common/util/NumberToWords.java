package com.webspark.common.util;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.vaadin.server.WrappedSession;

public class NumberToWords {
	
	SettingsValuePojo settings;
	WrappedSession session;
	
	private double getPlace(String number) {
		switch (number.length()) {
		case 1:
			return DefinePlace.UNITS;
		case 2:
			return DefinePlace.TENS;
		case 3:
			return DefinePlace.HUNDREDS;
		case 4:
			return DefinePlace.THOUSANDS;
		case 5:
			return DefinePlace.TENTHOUSANDS;
		case 6:
			return DefinePlace.LAKHS;
		case 7:
			return DefinePlace.TENLAKHS;
		case 8:
			return DefinePlace.CRORES;
		case 9:
			return DefinePlace.TENCRORES;
		}// switch
		return 0.0;
	}// getPlace

	private String getWord(int number) {
		switch (number) {
		case 1:
			return "One";
		case 2:
			return "Two";
		case 3:
			return "Three";
		case 4:
			return "Four";
		case 5:
			return "Five";
		case 6:
			return "Six";
		case 7:
			return "Seven";
		case 8:
			return "Eight";
		case 9:
			return "Nine";
		case 0:
			return "Zero";
		case 10:
			return "Ten";
		case 11:
			return "Eleven";
		case 12:
			return "Tweleve";
		case 13:
			return "Thirteen";
		case 14:
			return "Forteen";
		case 15:
			return "Fifteen";
		case 16:
			return "Sixteen";
		case 17:
			return "Seventeen";
		case 18:
			return "Eighteen";
		case 19:
			return "Ninteen";
		case 20:
			return "Twenty";
		case 30:
			return "Thirty";
		case 40:
			return "Forty";
		case 50:
			return "Fifty";
		case 60:
			return "Sixty";
		case 70:
			return "Seventy";
		case 80:
			return "Eighty";
		case 90:
			return "Ninty";
		case 100:
			return "Hundred";
		} // switch
		return "";
	} // getWord

	// private String cleanNumber(String number) {
	//
	// System.out.println("Number-------------------------->"+number);
	//
	// String afterDecimal =number.replace(',',
	// ' ').substring(number.indexOf('.'),number.length() ).replace('.',
	// ' ').replaceAll(" ", "");
	//
	// System.out.println("After Decimal            "+afterDecimal);
	//
	// String beforeDecimal = "";
	//
	// beforeDecimal = number.replace(',', ' ').substring(0,
	// number.indexOf('.')).replace('.', ' ').replaceAll(" ", "");
	// beforeDecimal = beforeDecimal.replace(',', ' ').replaceAll(" ", "");
	// if (beforeDecimal.startsWith("0"))
	// beforeDecimal = beforeDecimal.replaceFirst("0", "");
	//
	// return beforeDecimal;
	// } // cleanNumber

	public String convertNumber(String number,String exponentCurrencyFormat,String decimalCurrencyFormat) {
		
		session = new SessionUtil().getHttpSession();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		String exponent = "";

		exponent = number.replace(',', ' ').substring(0, number.indexOf('.')).replace('.', ' ').replaceAll(" ", "");
		
		if (exponent.startsWith("0"))
			exponent = exponent.replaceFirst("0", "");
		
		
		String decimal =number.replace(',', ' ').substring(number.indexOf('.'),number.length() ).replace('.', ' ').replaceAll(" ", "");
		
		
		String convertedAmount="";
		
		if(settings!=null&&settings.getCURRENCY_FORMAT()==SConstants.currencyFormat.MILLIONS)
			convertedAmount=convertInMillions(Integer.parseInt(exponent));
		else
			convertedAmount=convertNumber(exponent);
		
		convertedAmount+=" "+exponentCurrencyFormat;
		
		if(!decimal.equals("000")&&!decimal.equals("00")&&!decimal.equals("0")){
			if(settings!=null&&settings.getCURRENCY_FORMAT()==SConstants.currencyFormat.MILLIONS)
				convertedAmount+=" "+convertInMillions(Integer.parseInt(decimal));
			else
				convertedAmount+=" "+convertNumber(decimal);
		
		convertedAmount+=" "+decimalCurrencyFormat;
		}
		convertedAmount+=" Only.";
		return convertedAmount.toUpperCase();
	}

	public String convertNumber(String number) {
		// number = cleanNumber(number);

		double num = 0.0;
		try {
			num = Double.parseDouble(number);
		} catch (Exception e) {
			return "0.0";
		} // catch

		String returnValue = "";
		while (num > 0) {
			number = "" + (int) num;
			double place = getPlace(number);
			if (place == DefinePlace.TENS || place == DefinePlace.TENTHOUSANDS
					|| place == DefinePlace.TENLAKHS
					|| place == DefinePlace.TENCRORES) {
				int subNum = Integer.parseInt(number.charAt(0) + ""
						+ number.charAt(1));

				if (subNum >= 21 && (subNum % 10) != 0) {
					returnValue += getWord(Integer.parseInt(""
							+ number.charAt(0)) * 10)
							+ " " + getWord(subNum % 10);
				} // if
				else {
					returnValue += getWord(subNum);
				}// else

				if (place == DefinePlace.TENS) {
					num = 0;
				}// if
				else if (place == DefinePlace.TENTHOUSANDS) {
					num -= subNum * DefinePlace.THOUSANDS;
					returnValue += " Thousands ";
				}// if
				else if (place == DefinePlace.TENLAKHS) {
					num -= subNum * DefinePlace.LAKHS;
					returnValue += " Lakhs ";
				}// if
				else if (place == DefinePlace.TENCRORES) {
					num -= subNum * DefinePlace.CRORES;
					returnValue += " Crores ";
				}// if
			}// if
			else {
				int subNum = Integer.parseInt("" + number.charAt(0));

				returnValue += getWord(subNum);
				if (place == DefinePlace.UNITS) {
					num = 0;
				}// if
				else if (place == DefinePlace.HUNDREDS) {
					num -= subNum * DefinePlace.HUNDREDS;
					returnValue += " Hundred ";
				}// if
				else if (place == DefinePlace.THOUSANDS) {
					num -= subNum * DefinePlace.THOUSANDS;
					returnValue += " Thousand ";
				}// if
				else if (place == DefinePlace.LAKHS) {
					num -= subNum * DefinePlace.LAKHS;
					returnValue += " Lakh ";
				}// if
				else if (place == DefinePlace.CRORES) {
					num -= subNum * DefinePlace.CRORES;
					returnValue += " Crore ";
				}// if
			}// else
		}// while
		return returnValue;
	}// convert number
	
	
	final private static String[] units = { "Zero", "One", "Two", "Three",
		"Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
		"Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
		"Seventeen", "Eighteen", "Nineteen" };
    final private static String[] tens = { "", "", "Twenty", "Thirty", "Forty",
		"Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };

	public static String convertInMillions(Integer i) {
		//
		if (i < 20)
			return units[i];
		if (i < 100)
			return tens[i / 10] + ((i % 10 > 0) ? " " + convertInMillions(i % 10) : "");
		if (i < 1000)
			return units[i / 100] + " Hundred"
					+ ((i % 100 > 0) ? " and " + convertInMillions(i % 100) : "");
		if (i < 1000000)
			return convertInMillions(i / 1000) + " Thousand "
					+ ((i % 1000 > 0) ? " " + convertInMillions(i % 1000) : "");
		return convertInMillions(i / 1000000) + " Million "
				+ ((i % 1000000 > 0) ? " " + convertInMillions(i % 1000000) : "");
	}
	
} // class

class DefinePlace {
	public static final double UNITS = 1;
	public static final double TENS = 10 * UNITS;
	public static final double HUNDREDS = 10 * TENS;
	public static final double THOUSANDS = 10 * HUNDREDS;
	public static final double TENTHOUSANDS = 10 * THOUSANDS;
	public static final double LAKHS = 10 * TENTHOUSANDS;
	public static final double TENLAKHS = 10 * LAKHS;
	public static final double CRORES = 10 * TENLAKHS;
	public static final double TENCRORES = 10 * CRORES;
} // class