	package com.example.test1;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Bus_location {

	public double latitude, longitude;
	public String bus_id; 
	public Marker m;
	public int m_prop = 0, route;
	//public int detect = 0;
	
	public void set_info(double lat, double lng, int route_id) {
		longitude = lng;
		latitude = lat;
		route = route_id;
	}
	
	public void updateMarker() {
		m.setPosition(new LatLng(latitude, longitude));
		m.setTitle(bus_id);
		Log.d("In Markerupdate",bus_id);
	}
	
	public void createMarker(GoogleMap map) {
		//create marker with received location
		m = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(bus_id));	
	}
	
	public void clearMarker()
	{
		m.setVisible(false);
	}
	
	public void set_marker(GoogleMap map){
		if(m_prop == 0)
			clearMarker();
		else if(m_prop == 1)
			createMarker(map);
		else if(m_prop == 2)
			updateMarker();
	}
	
}


