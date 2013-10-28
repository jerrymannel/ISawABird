package com.isawabird;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	static TextView helloworld = null; 
	static MainActivity act = null; 
	static Button click = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
			try{
				if (ParseUtils.getCurrentUser() == null){
					MainActivity.updateLabel("Logging in...");
					ParseUtils.login("sriniketana", "test123");
					MainActivity.updateLabel("Logged in");
				}else{
					MainActivity.updateLabel("Already logged in as " + ParseUtils.getCurrentUser().getUsername());
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			

			/* Create a list */
			BirdList list1 = new BirdList("Hebbal Oct 2013");
			try{
				ParseUtils.createList(list1); 
			}catch(ISawABirdException ex){
				ex.printStackTrace();
			}
			
			/* Next query the lists */
			Vector<BirdList> lists ; 
			lists = ParseUtils.getLists(); 
			
			/* Add a sighting */ 
			Log.v(Consts.LOG_TAG, "Setting current list to " + lists.get(0).getListName());
			Utils.setCurrentList(lists.get(0).getListName());
			ParseUtils.addSightingToCurrentList("Common Crow");
			
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
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
