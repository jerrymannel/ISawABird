package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import com.isawabird.db.DBConsts;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.isawabird.test.DataLoader;
import com.parse.Parse;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static MainActivity act = null; 
	TextView numberSpecies ; 
	TextView currentListName ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//TODO: hide action bar before switching to login screen
		Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
		Utils.prefs = getSharedPreferences(Consts.PREF, Context.MODE_PRIVATE);
		ParseUtils.updateCurrentLocation(); 
		/* Set up the sync service */
		SyncUtils.createSyncAccount(this);
		SyncUtils.triggerRefresh();
		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		try{
			/* Initialize the checklists */
			Log.i(Consts.TAG, "Starting checklist init...");
			Utils.initializeChecklist(this, Utils.getChecklistName());
//			Utils.initializeChecklist(this, "World");
			Log.i(Consts.TAG, "Checklist init complete");
			
			/* Login to Parse */
			if (ParseUser.getCurrentUser() == null){
				// TODO : Create a new Login Activity and show it 
				Log.i(Consts.TAG, "Logging in...");
				// TODO: handle signup
				ParseUtils.login("sriniketana", "test123");
				Log.i(Consts.TAG, "Logged in");
			} else {
				Log.i(Consts.TAG, "Already logged in as " + ParseUtils.getCurrentUsername());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		/* End of initialization */ 

		if(Utils.isFirstTime()) {
			login();
			// exit this activity
			finish();
		} else {

			setContentView(R.layout.activity_main);
			numberSpecies = (TextView)findViewById(R.id.textView_main_mode);
			currentListName = (TextView)findViewById(R.id.textView_main_location);
				
			currentListName.setText(Utils.getCurrentListName());
			DBHandler dh = DBHandler.getInstance(this);
			numberSpecies.setText(String.valueOf(dh.getBirdCountForCurrentList()));
			
			// TODO: remove below line after dev
			// put all the dump tables and testing in data loader
			new DataLoader(getApplicationContext()).load(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		if(ParseUtils.isLoggedIn()) {
			menu.removeItem(R.id.action_login);
		} else {
			menu.removeItem(R.id.action_logout);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			//TODO implement
			return true;
		case R.id.action_login:
			login();
			// don't exit this activity
			return true;
		case R.id.action_logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void login() {
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(loginIntent);
	}
	
	private void logout() {
		// TODO implement
	}
	public static Context getContext(){
		return act;
	}

	/*public static ConnectivityManager getConnectivityManager(){
		return (ConnectivityManager)act.getSystemService(CONNECTIVITY_SERVICE);
	}*/

	@Override
	public void onClick(View v) {

	}
}
