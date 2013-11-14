package com.isawabird;

import android.accounts.Account;
import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseInit;
import com.isawabird.parse.ParseSyncAdapter;
import com.isawabird.parse.ParseSyncService;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.isawabird.test.DataLoader;
import com.isawabird.test.DummyAsyncTask;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static TextView helloworld = null; 
	static MainActivity act = null; 
	static Button click = null; 
	static Button isawabird = null; 

	// Constants
	
	// Instance fields
	Account mAccount;
	
	// Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Initialize Parse */
		ParseInit.init(this.getApplicationContext());
		
		SyncUtils.createSyncAccount(this);

		act = this;
		helloworld = (TextView) findViewById(R.id.helloworld);
		click = (Button)findViewById(R.id.getLists);
		click.setOnClickListener(this); 
		isawabird = (Button)findViewById(R.id.addSighting); 
		isawabird.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});	

		try{
			
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
			//dh.deleteList("Hebbal Nov 2013"); 
			//dh.deleteList("Hesaraghatta Nov 2013");
			
			//if(isFirstTime) {
				// Use below class to create test data.
				// TODO: remove when not required 
				// loader.load();
			//}
			Log.e(Consts.TAG, "Querying ...");
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
		try{
			Log.i(Consts.TAG, "Clicked");
			DBHandler dh = DBHandler.getInstance(this) ; 
			
			dh.clearTable(DBConsts.TABLE_LIST);
			dh.clearTable(DBConsts.TABLE_SIGHTING);

			dh.dumpTable(DBConsts.TABLE_LIST);
			dh.dumpTable(DBConsts.TABLE_SIGHTING);
			
			BirdList birdList = new BirdList("Hebbal Nov 2013");
			long id = dh.addBirdList(birdList,true );
			birdList = new BirdList("Hebbal Nov 2013");
			try{
				dh.addBirdList(birdList, true);
			}catch (Exception ex){
				ex.printStackTrace();
			}
			
			birdList = new BirdList("Hesaraghatta Nov 2013");
			dh.addBirdList(birdList, false);
			
			dh.addSightingToCurrentList(new Species("Blue Rock Thrush"));
			dh.addSightingToCurrentList(new Species("Blue Rock Thrush")); // this should fail
			dh.addSightingToCurrentList(new Species("Blue-capped Rock Thrush"));
			dh.addSightingToCurrentList(new Species("Indian Pitta"));
			
			dh.deleteList("Hesaraghatta Nov 2013");
			dh.deleteSightingFromList("Indian Pitta", "Hebbal Nov 2013");
			
//			dh.clearTable(DBConsts.TABLE_LIST);
//			dh.clearTable(DBConsts.TABLE_SIGHTING);
			
			//dh.deleteList("Hebbal Nov 2013");
			dh.dumpTable(DBConsts.TABLE_LIST);
			dh.dumpTable(DBConsts.TABLE_SIGHTING);
			
			/* Start the Parse sync service */ 
			Log.i(Consts.TAG, "Syncing now...");
			DummyAsyncTask dat = new DummyAsyncTask(this); 
			dat.execute("hello world");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
