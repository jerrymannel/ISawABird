package com.isawabird;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Initialize Parse */
		ParseInit.init(this);
		
		try{
//			List<ParseObject> lists = ParseUtils.getLists();
//			Iterator<ParseObject> i = lists.iterator();
//			while(i.hasNext()){
//				String listName =  i.next().getString("ListName");
//				Log.d("ISawABird",listName);
//			}
			ParseUtils.login("sriniketana", "test123");
			List<ParseObject> lists = ParseUtils.getLists();
			ParseUtils.addSightingToList(lists.iterator().next(), "Scaly-breasted Munia");
			lists = ParseUtils.getLists();
			
			Iterator<ParseObject> iter = lists.iterator(); 
			while(iter.hasNext()){
				ParseObject temp = iter.next();
				Log.d(Consts.LOG_TAG, "List : " + temp.getString("Location"));
				List<ParseObject> sightings = ParseUtils.getSightingsForList(temp);
				Iterator<ParseObject> iter1 = sightings.iterator();
				while(iter1.hasNext()) { 
					ParseObject temp1 = iter1.next();
					Log.d(Consts.LOG_TAG, temp1.getString("Species"));
				}
				
			}
			
//			Utils.initializeChecklist(this, "Indonesia");
//			Vector<Species> results =  Utils.search("ow rum", null);
//			Log.d(Consts.LOG_TAG, "********************************");
//			Utils.search("owrump", results);
//			
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

}
