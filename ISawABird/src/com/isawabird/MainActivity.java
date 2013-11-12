package com.isawabird;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.isawabird.db.DBConsts;
import com.isawabird.parse.ParseInit;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;
import com.isawabird.test.DataLoader;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static TextView helloworld = null; 
	static MainActivity act = null; 
	static Button click = null;

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
			if(isFirstTime) {
				// Use below class to create test data when app is installed for first time
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
