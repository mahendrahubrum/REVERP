package com.webspark.Components;

import java.util.ArrayList;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.layers.GoogleMapKmlLayer;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.Alignment;

@SuppressWarnings("serial")
public class LocationMap extends SVerticalLayout{
	SLabel label;
	public LocationMap(double latitude1,double longitude1,String name1,double latitude2,double longitude2,String name2) {
		setSizeFull();
		label=new SLabel();
		GoogleMap googleMap = new GoogleMap(new LatLon(latitude1, longitude1), 10.0, null);
		googleMap.setSizeFull();
		GoogleMapMarker marker1=new GoogleMapMarker(name1, new LatLon(latitude1, longitude1), true, null);
		GoogleMapMarker marker2=new GoogleMapMarker(name2, new LatLon(latitude2, longitude2), true, null);
		marker1.setAnimationEnabled(true);
		marker2.setAnimationEnabled(true);
		googleMap.setSizeFull();
		googleMap.addMarker(marker1);
		googleMap.addMarker(marker2);
		googleMap.setMinZoom(4.0);
		googleMap.setMaxZoom(20.0);
		googleMap.setZoom(8.0);
		
		ArrayList<LatLon> points = new ArrayList<LatLon>();
		points.add(new LatLon(latitude1, longitude1));
		points.add(new LatLon(latitude2, longitude2));
		
		double dist = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2)) + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(longitude1-longitude2));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		label.setValue(Math.round(dist)+" Kms");
		
		GoogleMapPolyline overlay = new GoogleMapPolyline(points, "#d31717", 0.8, 2);
//		googleMap.addPolyline(overlay);
		addComponent(googleMap);
		addComponent(label);
		setComponentAlignment(label, Alignment.BOTTOM_LEFT);
		setExpandRatio(googleMap, 1.5f);
		setExpandRatio(label, 0.1f);
	}
	
	public LocationMap(double latitude,double longitude,String name) {
		GoogleMap googleMap = new GoogleMap(new LatLon(latitude, longitude), 10.0, null);
		setSizeFull();
		googleMap.setSizeFull();
		googleMap.addMarker(name, new LatLon(latitude, longitude), true, null);
		googleMap.setMinZoom(4.0);
		googleMap.setMaxZoom(16.0);
		
//		googleMap.addKmlLayer(new GoogleMapKmlLayer("Source"));
		
		addComponent(googleMap);
	}
	
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	
	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

}
