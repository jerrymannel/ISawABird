package com.isawabird;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.isawabird.db.DBConsts;
import com.isawabird.parse.ParseInit;
import com.isawabird.parse.ParseUtils;
import com.isawabird.test.DataLoader;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static TextView helloworld = null; 
	static MainActivity act = null; 
	static Button click = null; 

	// Constants
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.isawabird.parse";
	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "2by0.com";
	// The account name
	public static final String ACCOUNT = "parse_dummy_account";
	// Instance fields
	Account mAccount;
	
	// Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the dummy account
		mAccount = createSyncAccount(this);

		ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

		act = this;
		helloworld = (TextView) findViewById(R.id.helloworld);
		click = (Button)findViewById(R.id.getLists);
		click.setOnClickListener(this); 

		try{
			/* Initialize Parse */
			ParseInit.init(this);
			MainActivity.updateLabel("Parse initialization complete");

			/* Initialize the checklists */
			Utils.initializeChecklist(this, "Indonesia");
			Utils.prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);

			/* Login to Parse */
			if (ParseUtils.getCurrentUser() == null){
				MainActivity.updateLabel("Logging in...");
				ParseUtils.login("sriniketana", "test123");
				MainActivity.updateLabel("Logged in");
			} else {
				MainActivity.updateLabel("Already logged in as " + ParseUtils.getCurrentUser().getUsername());
			}

			// load test data
			SQLiteDatabase checkDB = null;
			try {
				checkDB = SQLiteDatabase.openDatabase(this.getDatabasePath(DBConsts.DATABASE_NAME).getAbsolutePath(), null,
						SQLiteDatabase.OPEN_READONLY);
			} catch (SQLiteException e) {
				// database doesn't exist yet.
			} finally {
				if(checkDB != null) checkDB.close();
			}
			boolean isFirstTime = checkDB == null ? true : false;

			DataLoader loader = new DataLoader(this.getApplicationContext());
			if(isFirstTime) {
				// Use below class to create test data.
				// TODO: remove when not required 
				loader.load();
			}
			loader.query();

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//TODO complete this method
	public static Account createSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(
				ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager =
				(AccountManager) context.getSystemService(
						ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data
		 * If successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in
			 * in your <provider> element in the manifest,
			 * then call context.setIsSyncable(account, AUTHORITY, 1)
			 * here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report it,
			 * or handle it internally.
			 */
		}
		return null;
	}

	public static Context getContext(){
		return act;
	}

	public static ConnectivityManager getConnectivityManager(){
		return (ConnectivityManager)act.getSystemService(CONNECTIVITY_SERVICE);
	}

	public static void updateLabel(final String s){
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				helloworld.setText(helloworld.getText() + "\n" + s);			
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

}
