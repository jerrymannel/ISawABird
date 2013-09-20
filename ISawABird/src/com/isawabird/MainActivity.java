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
			Utils.initializeChecklist(this, "Indonesia");
			Vector<Species> results =  Utils.search("owrum", null);
			Log.d("ISawABird", "********************************");
			Utils.search("owrump", results);
			
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
