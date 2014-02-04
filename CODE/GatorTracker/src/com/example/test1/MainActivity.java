package com.example.test1;

import java.io.IOException;
import android.widget.TextView;
import android.widget.EditText;
import java.io.InputStream;
import java.io.ObjectOutputStream.PutField;
import java.util.Currency;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	protected static final String EXTRA_MESSAGE = null;
	public static final String USERNAME = "username";
	
	private SharedPreferences user_name;
	public String uname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String uname;
		EditText et = (EditText)findViewById(R.id.editText1);
		//TextView tv = (TextView) findViewById(R.id.textView2);
		
		user_name = getSharedPreferences("my_uid", Context.MODE_PRIVATE);		//instantiate a preference
		boolean chk_uname = user_name.contains(USERNAME);
		if (chk_uname == false)
		{
			try {
				uname = new LongRunningGetIO().execute().get();			//gets the string (user_id) returned by doInBackground of AsyncTask
				SharedPreferences.Editor editor = user_name.edit();
				editor.putString(USERNAME, uname);
				editor.commit();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		else
			{
			uname = user_name.getString(USERNAME, "NULL");
			et.setText(uname);
			}
		sendMessage();
		
	}

	public void sendMessage() {
	    // Do something in response to button
		final Context context = this;
		Button button= (Button) findViewById(R.id.btncheckin);
		Button track= (Button)findViewById(R.id.track_act);
		
		button.setOnClickListener(new OnClickListener() {
		String message;	
			@Override
			public void onClick(View v) {

				LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
				Intent iScreen2 = new Intent(context, Screen2Activity.class);
				Intent iServ = new Intent(context, NotifyService.class);
				Spinner dropDown = (Spinner)findViewById(R.id.buslist);
				message = dropDown.getSelectedItem().toString();
				iScreen2.putExtra(EXTRA_MESSAGE, message);
				if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ))
				{
		        	Toast.makeText( MainActivity.this, "Please turn on GPS", Toast.LENGTH_SHORT ).show();
		        	Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
				    startActivity(myIntent);
				}
				else{
					startService(iServ);
					//showNotification();
					startActivity(iScreen2);
				}
							
			}
		});
		
		track.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message;
				
				Intent intent = new Intent(MainActivity.this, MapActivity.class);
				Spinner dropDown = (Spinner)findViewById(R.id.buslist);
				message = dropDown.getSelectedItem().toString();
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
				
			}
		});
		
	}
	
/*	private void showNotification() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(MainActivity.this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("service started")
		        .setContentText("Gator Tracker");
		mBuilder.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(MainActivity.this, Screen2Activity.class);
		
		PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, 0);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
		
	}*/
	
public class LongRunningGetIO extends AsyncTask <Void, Void, String> {
	
	private String user_id;
		
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
             HttpGet httpGet = new HttpGet("http://54.235.91.254:8080/startSession"); 
			
             String text = null;
            try {								
                   HttpResponse response = httpClient.execute(httpGet, localContext);
                   HttpEntity entity = response.getEntity();
                   text = getASCIIContentFromEntity(entity);

					JSONObject jObject = new JSONObject(text);
					user_id = jObject.getString("user_id");

             } 

             catch (Exception e) {
            	 return e.getLocalizedMessage();
             }
             return user_id;
		}	
		
		protected void onPostExecute(String user_id) {
			if (user_id!=null) {
					EditText et = (EditText)findViewById(R.id.editText1);
					et.setText(user_id);	
			}
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
