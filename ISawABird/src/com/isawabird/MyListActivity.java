package com.isawabird;

import java.util.ArrayList;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyListActivity extends Activity {

	private ArrayList<String> myList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylists);
		
		DBHandler mydbh = DBHandler.getInstance(MainActivity.getContext());
		ArrayList<BirdList> myBirdLists = mydbh.getBirdLists(ParseUtils.getCurrentUsername());

		final ListView listview = (ListView) findViewById(R.id.mylistView);

		myList = new ArrayList<String>();
		for (BirdList bird : myBirdLists) {
			myList.add(bird.getListName());
		}
		
		listview.setAdapter(new MyListAdapter(this, myList));
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Log.i(Consts.TAG, "Clicked list :: " + parent.getItemAtPosition(position));
				
				Bundle b = new Bundle();
				b.putString("listName", parent.getItemAtPosition(position).toString());
				
				Intent mySightingIntent = new Intent(getApplicationContext(), MySightingsActivity.class);
				mySightingIntent.putExtras(b);
				startActivity(mySightingIntent);
			}
		});
	}

	private class MyListAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> values;

		public MyListAdapter(Context context, ArrayList<String> values) {
			super(context, R.layout.mylists_row, values);
			this.context = context;
			this.values = values;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.mylists_row, parent, false);
			
			TextView textView1 = (TextView) rowView.findViewById(R.id.mylistsItem_name);
			TextView textView2 = (TextView) rowView.findViewById(R.id.mylistItem_close);
			
			textView1.setText(values.get(position));
			textView2.setText(" ");
			
			return rowView;
		}
	}
}