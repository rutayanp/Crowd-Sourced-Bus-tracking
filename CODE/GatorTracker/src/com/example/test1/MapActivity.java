package com.example.test1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.ViewDebug.IntToString;
import android.widget.EditText;
import android.widget.TextView;

public class MapActivity extends Activity {
	
	private GoogleMap myMap;
	protected int prev_buses = 0;
	Bus_location[] BusArray = new Bus_location[5];
	private Timer myTimer;
	private Marker m;
	LongRunningGetIO track;
	
	public static final String USERNAME = "username";
	
	private SharedPreferences user_name;
	public String uname;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		user_name = getSharedPreferences("my_uid", Context.MODE_PRIVATE);
	    uname = user_name.getString(USERNAME, "NULL");
		
		for(int i=0; i<BusArray.length; i++)
			BusArray[i] = null;
		
		Intent intent = getIntent();
		String bus_no = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		Log.d("busNo",bus_no);

		
		myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		
		myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.6324, -82.3617), 13.0f));
		myMap.setMyLocationEnabled(true);
		
		PolylineOptions rectOptions = new PolylineOptions()
		.add(new LatLng(29.62532028,-82.37863156))
		.add(new LatLng(29.62506939,-82.37811707))
		.add(new LatLng(29.62476339,-82.37767182))
		.add(new LatLng(29.62425905,-82.37716645))
		.add(new LatLng(29.62356798,-82.37633432))
		.add(new LatLng(29.62273809,-82.37705615))
		.add(new LatLng(29.62211511,-82.37780999))
		.add(new LatLng(29.62055,-82.38021))
		.add(new LatLng(29.61989,-82.3797))
		.add(new LatLng(29.61784,-82.378))
		.add(new LatLng(29.61761,-82.3777))
		.add(new LatLng(29.61747,-82.37731))
		.add(new LatLng(29.61738,-82.37705))
		.add(new LatLng(29.61732905,-82.37667739))
		.add(new LatLng(29.6173146,-82.37653102))
		.add(new LatLng(29.61743,-82.37526))
		.add(new LatLng(29.61779,-82.37418))
		.add(new LatLng(29.61803,-82.37238))
		.add(new LatLng(29.62176,-82.3727))
		.add(new LatLng(29.62403,-82.3724))
		.add(new LatLng(29.62472,-82.3723))
		.add(new LatLng(29.62684,-82.3724))
		.add(new LatLng(29.62722,-82.3719))
		.add(new LatLng(29.62764,-82.37125))
		.add(new LatLng(29.62798,-82.37066))
		.add(new LatLng(29.62843,-82.36981))
		.add(new LatLng(29.62884,-82.36890))
		.add(new LatLng(29.62914,-82.36817))
		.add(new LatLng(29.62928,-82.36785))
		.add(new LatLng(29.63058,-82.36507))
		.add(new LatLng(29.63147,-82.36318))
		.add(new LatLng(29.63222,-82.36158))
		.add(new LatLng(29.63358,-82.35869))
		.add(new LatLng(29.63379,-82.35820))
		.add(new LatLng(29.63494,-82.35504))
		.add(new LatLng(29.63490,-82.35491))
		.add(new LatLng(29.63502,-82.35445))
		.add(new LatLng(29.63507,-82.35407))
		.add(new LatLng(29.63510,-82.35341))
		.add(new LatLng(29.63489,-82.35130))
		.add(new LatLng(29.63502,-82.35130))
		.add(new LatLng(29.63544,-82.35133))
		.add(new LatLng(29.63580,-82.35022))
		.add(new LatLng(29.63586,-82.35012))
		.add(new LatLng(29.63597,-82.35009))
		.add(new LatLng(29.63671,-82.35018))
		.add(new LatLng(29.63689,-82.35020))
		.add(new LatLng(29.63862,-82.35046))
		.add(new LatLng(29.63916,-82.35046))
		.add(new LatLng(29.64487,-82.35040))
		.add(new LatLng(29.64487,-82.34927))
		.add(new LatLng(29.64484,-82.34914))
		.add(new LatLng(29.64484,-82.34859))
		.add(new LatLng(29.64482,-82.34818))
		.add(new LatLng(29.64480,-82.34691))
		.add(new LatLng(29.64482,-82.34665))
		.add(new LatLng(29.64484,-82.34655))
		.add(new LatLng(29.64483,-82.34552))
		.add(new LatLng(29.64484,-82.34434))
		.add(new LatLng(29.64484,-82.34351))
		.add(new LatLng(29.64654,-82.34351))
		.add(new LatLng(29.64653,-82.34360))
		.add(new LatLng(29.64579,-82.34525))
		.add(new LatLng(29.64573,-82.34546))
		.add(new LatLng(29.64571,-82.34566))
		.add(new LatLng(29.64568,-82.34632))
		.add(new LatLng(29.64565,-82.34650))
		.add(new LatLng(29.64560,-82.34663))
		.add(new LatLng(29.64552,-82.34675))
		.add(new LatLng(29.64547,-82.34679))
		.add(new LatLng(29.64533,-82.34688))
		.add(new LatLng(29.64522,-82.34691))
		.add(new LatLng(29.64513,-82.34691))
		.add(new LatLng(29.64489,-82.34691))
		.add(new LatLng(29.64488,-82.34779))
		.add(new LatLng(29.64490,-82.34860))
		.add(new LatLng(29.64491,-82.34911))
		.add(new LatLng(29.64488,-82.34926))
		.add(new LatLng(29.64489,-82.35014))
		.add(new LatLng(29.64488,-82.35040))
		.add(new LatLng(29.64458,-82.35041))
		.add(new LatLng(29.64214,-82.35046))
		.add(new LatLng(29.64008,-82.35048))
		.add(new LatLng(29.63863,-82.35048))
		.add(new LatLng(29.63684,-82.35021))
		.add(new LatLng(29.63662,-82.35020))
		.add(new LatLng(29.63631,-82.35016))
		.add(new LatLng(29.63596,-82.35010))
		.add(new LatLng(29.63590,-82.35011))
		.add(new LatLng(29.63585,-82.35015))
		.add(new LatLng(29.63581,-82.35023))
		.add(new LatLng(29.63552,-82.35111))
		.add(new LatLng(29.63545,-82.35134))
		.add(new LatLng(29.63535,-82.35132))
		.add(new LatLng(29.63500,-82.35132))
		.add(new LatLng(29.63520,-82.35302))
		.add(new LatLng(29.63526,-82.35355))
		.add(new LatLng(29.63531,-82.35400))
		.add(new LatLng(29.63536,-82.35438))
		.add(new LatLng(29.63423,-82.35760))
		.add(new LatLng(29.63356,-82.35918))
		.add(new LatLng(29.63238,-82.36167))
		.add(new LatLng(29.62975,-82.36727))
		.add(new LatLng(29.62933,-82.36827))
		.add(new LatLng(29.62901,-82.36898))
		.add(new LatLng(29.62846,-82.37018))
		.add(new LatLng(29.62785,-82.37124))
		.add(new LatLng(29.62737,-82.37200))
		.add(new LatLng(29.62680,-82.37260))
		.add(new LatLng(29.62566,-82.37256))
		.add(new LatLng(29.62504,-82.37254))
		.add(new LatLng(29.62394,-82.37262))
		.add(new LatLng(29.62224,-82.37291))
		.add(new LatLng(29.62202,-82.37292))
		.add(new LatLng(29.62145,-82.37292))
		.add(new LatLng(29.62018,-82.37283))
		.add(new LatLng(29.61802,-82.37258))
		.add(new LatLng(29.61788,-82.37383))
		.add(new LatLng(29.61783,-82.37413))
		.add(new LatLng(29.61746,-82.37527))
		.add(new LatLng(29.61742,-82.37552))
		.add(new LatLng(29.61735,-82.37644))
		.add(new LatLng(29.61740,-82.37699))
		.add(new LatLng(29.61746,-82.37723))
		.add(new LatLng(29.61752,-82.37740))
		.add(new LatLng(29.61763,-82.37764))
		.add(new LatLng(29.61790,-82.37803))
		.add(new LatLng(29.61939,-82.37921))
		.add(new LatLng(29.61988,-82.37967))
		.add(new LatLng(29.62041,-82.38007))
		.add(new LatLng(29.62055,-82.38021))
		.add(new LatLng(29.62241,-82.38157))
		.add(new LatLng(29.62274,-82.38195))
		.add(new LatLng(29.62302,-82.38172))
		.add(new LatLng(29.62345,-82.38099))
		.add(new LatLng(29.62536,-82.37969))
		.add(new LatLng(29.62566,-82.37944))
		.add(new LatLng(29.62532028,-82.37863156))
		.color(Color.BLUE)
        .width(5);


		if(bus_no.equals("Bus 12 Reitz Union - Hunters Run")){
			Polyline polyline = myMap.addPolyline(rectOptions);
			polyline.setGeodesic(true);}
	}
	
	protected void onStart() {
		super.onStart();
		final Handler handler = new Handler();
			
		   myTimer = new Timer();
		    TimerTask doAsynchronousTask = new TimerTask() {       
		        @Override
		        public void run() {
		            handler.post(new Runnable() {
		                public void run() {       
		                    try {
		                    	track = (LongRunningGetIO) new LongRunningGetIO(prev_buses).execute();
		                    } catch (Exception e) {
		                        // TODO Auto-generated catch block
		                    }
		                }
		            });
		        }
		    };
		    myTimer.schedule(doAsynchronousTask, 0, 3000); //execute in every 50000 ms 	
	}
	
	
	public class LongRunningGetIO extends AsyncTask <Void, Void, String> {
		
		int prev_buses;
		
		public LongRunningGetIO (int buses)
		{
			prev_buses = buses;
		}
				
			protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		       InputStream in = entity.getContent();
		         StringBuffer out = new StringBuffer();
		         int n = 1;
		         while (n>0) {
		             byte[] b = new byte[4096];
		             n =  in.read(b);
		             if (n>0) out.append(new String(b, 0, n));
		         }
		         return out.toString();
		    }
			
			@Override
			protected String doInBackground(Void... params) {
				 HttpClient httpClient = new DefaultHttpClient();
				 HttpContext localContext = new BasicHttpContext();
	             HttpPost httpPost = new HttpPost("http://54.235.91.254:8080/get_bus_location");
	             String text = null;
	             
	             try{									//for get_bus_location
	            	 JSONObject json = new JSONObject();
	            	 httpPost.setHeader("Content-Type", "application/json");
	            	    json.put("user_id",uname);			//change to uname
	            	    json.put("route_id","12");
	            	    httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes()));
	             }
	             catch (Exception e) {
	            	 return e.getLocalizedMessage();
	             }
	             
	             try {								//to capture response into String
	                 HttpResponse response = httpClient.execute(httpPost, localContext);
	                 HttpEntity entity = response.getEntity();
	                 text = getASCIIContentFromEntity(entity);
	             } 
	             catch (Exception e) {
	            	 return e.getLocalizedMessage();
	             }
	             
	             try {
					set_locations(text);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	           
	             return text;
			}	
			
			protected void onPostExecute(String user_id) {
				for(int i=0; i<BusArray.length; i++)
				{
					if(BusArray[i] != null)
					{
						BusArray[i].set_marker(myMap);
					}
				}

			}
	    }
		
	public void set_locations(String json) throws JSONException {	
		int buses = 0;
		int new_buses = 0;
		
		for(int i=0; i<BusArray.length; i++)
		{
			if(BusArray[i] != null)
				BusArray[i].m_prop = 0;
		}
		
		Log.d("InSet_locations", " entered set_locations");
		JSONObject response = new JSONObject(json);
		JSONArray valuesArray = response.getJSONArray("bus_info");
		buses = valuesArray.length();
		Log.d("JSONHandled", " obtained #buses");
		
		if(buses != 0)
		{
			Log.d("buses!=0", " buses present");
			for(int i=0; i<buses; i++)
			{
				JSONObject bus_info = valuesArray.getJSONObject(i);
				String bus_id = bus_info.getString("bus_id"); 
				JSONObject loc = bus_info.getJSONObject("loc");
				if(prev_buses == 0)
				{
					Log.d("prev_buses==0", " inside no prev buses");
					BusArray[i] = new Bus_location();
					BusArray[i].bus_id = bus_id;
					BusArray[i].set_info(loc.getDouble("lat"), loc.getDouble("lng"), bus_info.getInt("route_id"));
					BusArray[i].m_prop = 1;
					Log.d("End_prev_bus==0", "ending prev_buses==0");
				}
				else
				{
					Log.d("prev_buses!=0", " inside prev buses exist");
					int detected = 0;
					for(int j=0; j<BusArray.length; j++)
					{
						if(BusArray[j] != null)
						{
							Log.d("recv bus_id", bus_id);
							Log.d("prev bus_id", BusArray[j].bus_id);
							if(bus_id.equals(BusArray[j].bus_id))
							{
								Log.d("BusIDMatch", bus_id);
								BusArray[j].set_info(loc.getDouble("lat"), loc.getDouble("lng"), bus_info.getInt("route_id"));
								BusArray[j].m_prop = 2;
								detected = 1;
								Log.d("Detect=1", "Match detected");
								break;
							}
						}
					}
					if(detected==0)
					{
						Log.d("InsideDetect==0", "entered detect==0");
						BusArray[prev_buses+new_buses] = new Bus_location();
						BusArray[prev_buses+new_buses].bus_id = bus_id;
						BusArray[prev_buses+new_buses].set_info(loc.getDouble("lat"), loc.getDouble("lng"), bus_info.getInt("route_id"));
						BusArray[prev_buses+new_buses].m_prop = 1;
						new_buses++;
					}
					Log.d("Detect==0Checked", "ending prev_buses!=0");
				}
			}
			
		}
		else
		{
			Log.d("Buses=0", "No Bus");
			for(int i=0; i<BusArray.length; i++)
			{
				BusArray[i] = null;
			}
		}
		
		prev_buses = buses;
	}
	
	protected void onStop() {
		super.onStop();
		myTimer.cancel();
		
	}
	
	protected void onPause (){
		super.onPause();
		myTimer.cancel();
	}
	

}


