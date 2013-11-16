package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.isawabird.db.DBConsts;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.isawabird.test.DataLoader;
import com.parse.Parse;

public class MainActivity extends Activity {

	private Button mSawBirdButton ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//TODO: hide action bar before switching to login screen
		Utils.prefs = getSharedPreferences(Consts.PREF, Context.MODE_PRIVATE);
		if(Utils.isFirstTime()) {
			login();
			// exit this activity
			finish();
		} else {

			setContentView(R.layout.activity_main);
			
			mSawBirdButton = (Button) findViewById(R.id.btn_isawabird);
			mSawBirdButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent loginIntent = new Intent(getApplicationContext(), SearchActivity.class);
					startActivity(loginIntent);					
				}
			});
			/* Initialize Parse and preferences */
			Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);

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

	/*public static ConnectivityManager getConnectivityManager(){
		return (ConnectivityManager)act.getSystemService(CONNECTIVITY_SERVICE);
	}*/
}
