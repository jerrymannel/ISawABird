package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

public class MainActivity extends Activity {

	static MainActivity act = null; 
	
	TextView numberSpecies;
	TextView currentListName;
	TextView currentLocation;
	Button mSawBirdButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this; 
		try {
			// TODO: hide action bar before switching to login screen
			Utils.prefs = getSharedPreferences(Consts.PREF,
					Context.MODE_PRIVATE);
			Parse.initialize(this, ParseConsts.APP_ID,ParseConsts.REST_CLIENT_KEY);
			ParseUtils.updateCurrentLocation();

			if (Utils.isFirstTime()) {
				login();
				// exit this activity
				finish();
			} else {

				setContentView(R.layout.activity_main);
				mSawBirdButton = (Button) findViewById(R.id.btn_isawabird);
				numberSpecies = (TextView) findViewById(R.id.text_mode);
				currentListName = (TextView) findViewById(R.id.textView_currentList);

				
				// FIXME : (jerry) commenting this. App is crashing. Fix.
				// move heavy work to asynctask
				new InitAsyncTask().execute();

				// FIXME : (jerry) commenting this. App is crashing. Fix.
				// ParseUtils.updateCurrentLocation();

				// FIXME : (jerry) commenting this. App is crashing. Fix.
				/* Set up the sync service */
				// SyncUtils.createSyncAccount(this);
				// SyncUtils.triggerRefresh();
				// ParseInstallation.getCurrentInstallation().saveInBackground();

				mSawBirdButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent searchIntent = new Intent(
								getApplicationContext(), SearchActivity.class);
						startActivity(searchIntent);
					}
				});

				Log.i(Consts.TAG,
						"current List ID: " + Utils.getCurrentListID());
				Log.i(Consts.TAG,
						"current List Name: " + Utils.getCurrentListName());
				Log.i(Consts.TAG,
						"current Username: " + ParseUtils.getCurrentUsername());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Context getContext(){
		return act.getApplicationContext();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// FIXME : (jerry) commenting this. App is crashing. Issue with
		// ParseUtils.
		// if(ParseUtils.isLoggedIn()) {
		// menu.removeItem(R.id.action_login);
		// } else {
		// menu.removeItem(R.id.action_logout);
		// }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// TODO implement
			return true;
		case R.id.action_login:
			login();
			// don't exit this activity
			return true;
		case R.id.action_logout:
			logout();
			return true;
			// TODO :: (jerry) remove this
		case R.id.action_developerSettings:
			startActivity(new Intent(getApplicationContext(),
					DeveloperSettings.class));
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void login() {
		Intent loginIntent = new Intent(getApplicationContext(),
				LoginActivity.class);
		startActivity(loginIntent);
	}

	private void logout() {
		// TODO implement
	}

	/*
	 * public static ConnectivityManager getConnectivityManager(){ return
	 * (ConnectivityManager)act.getSystemService(CONNECTIVITY_SERVICE); }
	 */

	private class InitAsyncTask extends AsyncTask<Void, Void, Long> {

		protected Long doInBackground(Void... params) {

			/* Initialize the checklists */
			Log.i(Consts.TAG, "Starting checklist init...");
			try {
				Utils.initializeChecklist(getApplicationContext(),
						Utils.getChecklistName());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i(Consts.TAG, "Checklist init complete");

			// TODO: remove below line after dev
			// put all the dump tables and testing in data loader
			// new
			// DataLoader(getApplicationContext()).load(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath());
//			new DataLoader(getApplicationContext())
//					.srihariTestFunction(getApplicationContext()
//							.getDatabasePath(DBConsts.DATABASE_NAME)
//							.getAbsolutePath());
//
			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			// TODO: not happy with static access to Utils class in DBHandler
			return dh.getBirdCountForCurrentList();
		}

		protected void onPostExecute(Long param) {
			numberSpecies.setText(String.valueOf(param));
			currentListName.setText(Utils.getCurrentListName());
		}
	}
}
