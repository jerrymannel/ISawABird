package com.isawabird;

import java.util.ArrayList;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MySightingsActivity extends Activity {

	private ArrayList<String> myList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysightings);
		
		Bundle b = getIntent().getExtras();
		String listName = b.getString("listName");
		
		DBHandler mydbh = DBHandler.getInstance(MainActivity.getContext());
		ArrayList<Sighting> myBirdLists = mydbh.getSightingsByListName(listName, ParseUtils.getCurrentUsername());

		final ListView listview = (ListView) findViewById(R.id.mysightingsView);

		myList = new ArrayList<String>();
		for (Sighting sighting : myBirdLists) {
			Log.i(Consts.TAG, "Sighting :: " + sighting.getListName());
			myList.add(sighting.getListName());
		}
		
		listview.setAdapter(new MyListAdapter(this, myList));
	}

	private class MyListAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> values;

		public MyListAdapter(Context context, ArrayList<String> values) {
			super(context, R.layout.mysightings_row, values);
			this.context = context;
			this.values = values;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.mysightings_row, parent, false);
			
			TextView textView1 = (TextView) rowView.findViewById(R.id.mysightingsItem_name);
			TextView textView2 = (TextView) rowView.findViewById(R.id.mysightingsItem_close);
			
			textView1.setText(values.get(position));
			textView2.setText(" ");
			
			return rowView;
		}
	}
}