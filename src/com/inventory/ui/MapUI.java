package com.inventory.ui;

import java.util.ArrayList;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.webspark.Components.SPanel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 20, 2015
 */
public class MapUI extends SparkLogic {

	private static final long serialVersionUID = 3986462936874666917L;

	@Override
	public SPanel getGUI() {

		setSize(900, 700);
		SPanel pan = new SPanel();
		pan.setSizeFull();

		SVerticalLayout main = new SVerticalLayout();
		main.setSizeFull();
		
		try {
			

		GoogleMap googleMap = new GoogleMap(new LatLon(60.440963, 22.25122), 10.0, null);
		googleMap.setSizeFull();
		googleMap.addMarker("NOT DRAGGABLE: Iso-Heikkilä", new LatLon(
		        60.450403, 22.230399), true, null);
		googleMap.setMinZoom(4.0);
		googleMap.setMaxZoom(16.0);
		
		
		ArrayList<LatLon> points = new ArrayList<LatLon>();
		points.add(new LatLon(60.448118, 22.253738));
		points.add(new LatLon(60.455144, 22.24198));
		points.add(new LatLon(60.460222, 22.211939));
		points.add(new LatLon(60.488224, 22.174602));
		points.add(new LatLon(60.486025, 22.169195));

		GoogleMapPolyline overlay = new GoogleMapPolyline(
		        points, "#d31717", 0.8, 10);
		googleMap.addPolyline(overlay);
		
		main.addComponent(googleMap);
		pan.setContent(main);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pan;
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
