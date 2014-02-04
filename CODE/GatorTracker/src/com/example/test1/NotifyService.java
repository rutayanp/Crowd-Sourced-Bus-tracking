package com.example.test1;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotifyService extends Service {
	
	public double lat;
	public double lng;
	private Timer myTimer;
	
	public static final String USERNAME = "username";
	
	private SharedPreferences user_name;
	public String uname;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.d("my service","in on create");
		
		user_name = getSharedPreferences("my_uid", Context.MODE_PRIVATE);
	    uname = user_name.getString(USERNAME, "NULL");
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.d("location_debug", "Latitude before change:"+lat);
        Log.d("location_debug", "Longitude before change"+lng);
        }
        
        final LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				lat = loc.getLatitude();
				lng = loc.getLongitude();
		        Log.d("location_debug", "Latitude after change:"+lat);
		        Log.d("location_debug", "Longitude after change"+lng);
				
			}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		        1000,          // 1-second interval.
		        1,             // 1 meters.
		        listener);

		
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		//super.onStart(intent, startId);
		Log.d("my service","service startd");
		Toast.makeText(this, "Location Feed Started", Toast.LENGTH_LONG).show();
		
		long delay = 1000;
		long period = 5000;
		
	    myTimer = new Timer();
	    myTimer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	new LongRunningGetIO().execute();
	        }

	    }, delay, period );
		
	}

	@Override
	public void onDestroy(){
		Log.d("my service","service stopped");
		myTimer.cancel();
		Toast.makeText(this, "Location Feed Stopped", Toast.LENGTH_SHORT).show();
		}
	
private class LongRunningGetIO extends AsyncTask <Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			 HttpClient httpClient = new DefaultHttpClient();
			 HttpContext localContext = new BasicHttpContext();
			HttpPut httpPut = new HttpPut("http://54.235.91.254:8080/set_bus_location");
			
             String text = null;

             try{									//for set_bus_location
            	 JSONObject json = new JSONObject();
            	 JSONObject jsonloc = new JSONObject();
            	 if(lat != 0 && lng != 0)
            	 {
            	 httpPut.setHeader("Content-Type", "application/json");
            	    jsonloc.put("lng", lng);
            	    jsonloc.put("lat", lat);
            	    json.put("loc", jsonloc);
            	    json.put("user_id",uname);
            	    json.put("route_id","12");
            	    httpPut.setEntity(new ByteArrayEntity(json.toString().getBytes()));
            	    HttpResponse response = httpClient.execute(httpPut, localContext);
            	    text = response.toString();
            	    Log.d("Server_Response", text);
            	 }
             }
             catch (Exception e) {
            	 return e.getLocalizedMessage();
             }
             return text;
		}	
		
		protected void onPostExecute(String results) {

		}
    }
}
