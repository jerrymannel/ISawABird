package com.isawabird;

import java.util.ListIterator;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseInit;
import com.isawabird.parse.ParseUtils;

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
			BirdList list1 = new BirdList("Hebbal Nov 2013");
			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			try {
				dh.addBirdList(list1, ParseUtils.getCurrentUser().getUsername()); 
			} catch(ISawABirdException ex){
				ex.printStackTrace();
			}
			
			/* Next query the lists */
			Vector<BirdList> lists ; 
			lists = dh.getBirdLists(ParseUtils.getCurrentUser().getUsername()); 
			ListIterator<BirdList> iter = lists.listIterator();
			while(iter.hasNext()){
				BirdList temp = iter.next(); 
				Log.v(Consts.TAG, temp.getId() + ":" + temp.getListName());
			}
			
			
			/* Add a sighting */ 
			Log.v(Consts.TAG, "Setting current list to " + lists.get(0).getListName());
			BirdList.setCurrentList(lists.get(0).getListName(), lists.get(0).getId());
			Sighting sighting = new Sighting("common crow");
			dh.addSighting(sighting, lists.get(0).getId(), ParseUtils.getCurrentUser().getUsername());
		
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
