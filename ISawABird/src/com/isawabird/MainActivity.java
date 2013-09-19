package com.isawabird;

import java.util.Iterator;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Initialize Parse */
		ParseLogin.login(this);
		
		try{
			List<ParseObject> lists = ParseUtils.getLists();
			Iterator<ParseObject> i = lists.iterator();
			while(i.hasNext()){
				Log.d("ISawABird", i.next().getString("ListName"));
			}
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
