package com.isawabird;

import android.app.Activity;
import android.content.Context;
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
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseSyncAdapter;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.isawabird.test.DataLoader;
import com.parse.Parse;
import com.parse.ParseUser;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static TextView helloworld = null; 
	static MainActivity act = null; 
	static Button click = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Initialize Parse */
		Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
		Utils.prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
		SyncUtils.createSyncAccount(this);

		act = this;
		helloworld = (TextView) findViewById(R.id.helloworld);
		click = (Button)findViewById(R.id.getLists);
		click.setOnClickListener(this); 

		try{
			MainActivity.updateLabel("Parse initialization complete");

			/* Initialize the checklists */
			Utils.initializeChecklist(this, "Indonesia");

			/* Login to Parse */
			if (ParseUser.getCurrentUser() == null){
				MainActivity.updateLabel("Logging in...");
				// TODO: handle signup
				ParseUtils.login("sriniketana", "test123");
				MainActivity.updateLabel("Logged in");
			} else {
				MainActivity.updateLabel("Already logged in as " + ParseUtils.getCurrentUsername());
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
				// Use below class to create test data for the first time
				// TODO: remove when not required 
				loader.load();
			}
			Log.e(Consts.TAG, "Querying ...");
			loader.query();
			
			DBHandler dh = DBHandler.getInstance(this);
			dh.clearTable(DBConsts.TABLE_LIST);
			dh.clearTable(DBConsts.TABLE_SIGHTING);
//		
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
			dh.addSightingToCurrentList(new Species("Blue Rock Thrush"));
			dh.addSightingToCurrentList(new Species("Blue-capped Rock Thrush"));
			dh.addSightingToCurrentList(new Species("Indian Pitta"));
			
			dh.deleteList("Hesaraghatta Nov 2013");
			dh.deleteSightingFromCurrentList("Indian Pitta");
			
//			dh.clearTable(DBConsts.TABLE_LIST);
//			dh.clearTable(DBConsts.TABLE_SIGHTING);
			
			//dh.deleteList("Hebbal Nov 2013");
			dh.dumpTable(DBConsts.TABLE_LIST);
			dh.dumpTable(DBConsts.TABLE_SIGHTING);
			
			SyncUtils.triggerRefresh();
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

	}
}
