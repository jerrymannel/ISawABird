package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static MainActivity act = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static Context getContext(){
		return act;
	}

	public static ConnectivityManager getConnectivityManager(){
		return (ConnectivityManager)act.getSystemService(CONNECTIVITY_SERVICE);
	}

	@Override
	public void onClick(View v) {

	}

}
