package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MainActivity extends Activity {

	static MainActivity act = null;

	TextView numberSpecies;
	TextView currentListName;
	TextView currentLocation;
	TextView total_sightings_title;
	TextView total_sightings;
	TextView btn_myLists;
	TextView btn_settings;
	Button mSawBirdButton;
	Typeface openSansLight;
	Typeface openSansBold;
	Typeface openSansBoldItalic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;
		
		openSansLight = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Light.ttf");
		openSansBold = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Bold.ttf");
		openSansBoldItalic = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-BoldItalic.ttf");
		
		try {
			// TODO: hide action bar before switching to login screen
			Utils.prefs = getSharedPreferences(Consts.PREF, Context.MODE_PRIVATE);
			Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
			ParseUtils.updateCurrentLocation();

			PushService.setDefaultPushCallback(this, MainActivity.class);
			ParseInstallation.getCurrentInstallation().saveInBackground();
			// ParseAnalytics.trackAppOpened(getIntent());
			if (Utils.isFirstTime()) {
				login();
				// exit this activity
				finish();
			} else {

				setContentView(R.layout.activity_main);
				mSawBirdButton = (Button) findViewById(R.id.btn_isawabird);
				numberSpecies = (TextView) findViewById(R.id.text_mode);
				currentListName = (TextView) findViewById(R.id.textView_currentList);
				total_sightings_title = (TextView) findViewById(R.id.textView_total_text);
				total_sightings = (TextView) findViewById(R.id.textView_total);
				btn_myLists = (TextView) findViewById(R.id.textView_myLists);
				btn_settings = (TextView) findViewById(R.id.textView_settings);
				
				mSawBirdButton.setTypeface(openSansLight);
				currentListName.setTypeface(openSansBold);
				numberSpecies.setTypeface(openSansBoldItalic);
				total_sightings_title.setTypeface(openSansBold);
				total_sightings.setTypeface(openSansLight);

				// move heavy work to asynctask
				new InitAsyncTask().execute();

				// FIXME : (jerry) commenting this. App is crashing. Fix.
				// ParseUtils.updateCurrentLocation();

				// FIXME : (jerry) commenting this. App is crashing. Fix.
				/* Set up the sync service */
				// SyncUtils.createSyncAccount(this);
				// SyncUtils.triggerRefresh();
				// ParseInstallation.getCurrentInstallation().saveInBackground();

				DBHandler mydbh = DBHandler.getInstance(MainActivity.getContext());
				numberSpecies.setText(Long.toString(mydbh.getBirdCountForCurrentList()));
				
				// TODO : there is no method to find total birds spotted till date.
				total_sightings.setText(Long.toString(mydbh.getBirdCountForCurrentList()));

				mSawBirdButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
						startActivity(searchIntent);
					}
				});
				
				btn_myLists.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						startActivity(new Intent(getApplicationContext(), MyListActivity.class));
					}
				});

				Log.i(Consts.TAG, "current List ID: " + Utils.getCurrentListID());
				Log.i(Consts.TAG, "current List Name: " + Utils.getCurrentListName());
				Log.i(Consts.TAG, "current Username: " + ParseUtils.getCurrentUsername());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	long lastPress;

	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastPress > 5000) {
			Toast.makeText(getBaseContext(), "Press Back again to exit.", Toast.LENGTH_SHORT).show();
			lastPress = currentTime;
		} else {
			finish();
		}
	}

	public static Context getContext() {
		return act.getApplicationContext();
	}

	private void login() {
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
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
				Utils.initializeChecklist(getApplicationContext(), Utils.getChecklistName());
				SyncUtils.createSyncAccount(MainActivity.getContext());
				SyncUtils.triggerRefresh();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i(Consts.TAG, "Checklist init complete");

			// TODO: remove below line after dev
			// put all the dump tables and testing in data loader
			// new
			// DataLoader(getApplicationContext()).load(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath());
			// new DataLoader(getApplicationContext())
			// .srihariTestFunction(getApplicationContext()
			// .getDatabasePath(DBConsts.DATABASE_NAME)
			// .getAbsolutePath());
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
