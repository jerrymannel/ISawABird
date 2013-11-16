package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.isawabird.test.DataLoader;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class MainActivity extends Activity {

	static MainActivity act = null; 
	TextView numberSpecies ; 
	TextView currentListName ; 
	Button mSawBirdButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			//TODO: hide action bar before switching to login screen		
			Utils.prefs = getSharedPreferences(Consts.PREF, Context.MODE_PRIVATE);
			if(Utils.isFirstTime()) {
				login();
				// exit this activity
				finish();
			} else {

				setContentView(R.layout.activity_main);
				
				Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
				
				// TODO: move heavy work to asynctask
				ParseUtils.updateCurrentLocation(); 
				
				/* Set up the sync service */
				SyncUtils.createSyncAccount(this);
				SyncUtils.triggerRefresh();
				ParseInstallation.getCurrentInstallation().saveInBackground();

				/* Initialize the checklists */
				Log.i(Consts.TAG, "Starting checklist init...");
				Utils.initializeChecklist(this, Utils.getChecklistName());
				Log.i(Consts.TAG, "Checklist init complete");

				mSawBirdButton = (Button) findViewById(R.id.btn_isawabird);
				mSawBirdButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent loginIntent = new Intent(getApplicationContext(), SearchActivity.class);
						startActivity(loginIntent);					
					}
				});
				// TODO: fix inconsistency in listID and listName
				Log.i(Consts.TAG, "current List ID: " + Utils.getCurrentListID());
				Log.i(Consts.TAG, "current List Name: " + Utils.getChecklistName());
				Log.i(Consts.TAG, "current Username: " + ParseUtils.getCurrentUsername());
				
				numberSpecies = (TextView)findViewById(R.id.text_mode);
				currentListName = (TextView)findViewById(R.id.text_location);

				currentListName.setText(Utils.getCurrentListName());
				DBHandler dh = DBHandler.getInstance(this);
				// TODO: not happy with static access to Utils class in DBHandler
				numberSpecies.setText(String.valueOf(dh.getBirdCountForCurrentList()));

				// TODO: remove below line after dev
				// put all the dump tables and testing in data loader
				//new DataLoader(getApplicationContext()).load(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath());
				new DataLoader(getApplicationContext()).srihariTestFunction(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
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
}
