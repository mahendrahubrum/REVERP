package com.webspark.common.location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class LocationFinder {

	private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";
	private static final String navURL = "http://maps.google.com/maps/nav?q=";
	
	public GoogleResponse getLatLongFromAddress(String fullAddress) throws IOException {

		GoogleResponse response=null;
		try{
			URL url = new URL(URL + "?address="+ URLEncoder.encode(fullAddress, "UTF-8") + "&sensor=false");
			  URLConnection conn = url.openConnection();
			  InputStream in = conn.getInputStream() ;
			  ObjectMapper mapper = new ObjectMapper();
			  mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			  response = (GoogleResponse)mapper.readValue(in,GoogleResponse.class);
			  in.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return response;

	}
		 
	public GoogleResponse getAddressFromLatLong(String latlongString) throws IOException {

		GoogleResponse response =null;
		try{
		  URL url = new URL(URL + "?latlng="+ URLEncoder.encode(latlongString, "UTF-8") + "&sensor=false");
		  URLConnection conn = url.openConnection();
		  InputStream in = conn.getInputStream() ;
		  ObjectMapper mapper = new ObjectMapper();
		  response = (GoogleResponse)mapper.readValue(in,GoogleResponse.class);
		  in.close();
		}	  
		catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}
	
	
	
}
