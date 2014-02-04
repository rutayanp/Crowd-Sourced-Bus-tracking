package com.example.test1;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;

public class Screen2Activity extends Activity {
	
	public static final String USERNAME = "username";
	
	private SharedPreferences user_name;
	public String uname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen2);
		
		String display = "The selected bus is ";
		Intent intent= getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		final Context context = this;
		
		TextView textView = (TextView)findViewById(R.id.screen2);
	    textView.setTextSize(40);
	    textView.setGravity(Gravity.CENTER);
	    textView.setText(display+message);
	    
	    Button toScreen1 = (Button)findViewById(R.id.toScreen1);
	    toScreen1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(new Intent(Screen2Activity.this,NotifyService.class));
				Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		//to clear all the previously running screens
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen2, menu);
		return true;
	}

}
