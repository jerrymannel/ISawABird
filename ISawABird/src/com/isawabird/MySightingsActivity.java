package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
import com.isawabird.utilities.SwipeDismissListViewTouchListener;
import com.isawabird.utilities.UndoBarController;
import com.isawabird.utilities.UndoBarController.UndoListener;

public class MySightingsActivity extends Activity {

	private ArrayList<String> myList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysightings);

		Bundle b = getIntent().getExtras();
		final String listName = b.getString("listName");

		DBHandler mydbh = DBHandler.getInstance(MainActivity.getContext());
		ArrayList<Sighting> myBirdLists = mydbh.getSightingsByListName(listName, ParseUtils.getCurrentUsername());

		final ListView listview = (ListView) findViewById(R.id.mysightingsView);

		myList = new ArrayList<String>();
		for (Sighting sighting : myBirdLists) {
			Log.i(Consts.TAG, "Sighting :: " + sighting.getSpecies().getFullName());
			myList.add(sighting.getSpecies().getFullName());
		}
		final MyListAdapter listAdapter = new MyListAdapter(this, myList);
		listview.setAdapter(listAdapter);

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listview,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView, int[] reverseSortedPositions) {
						for (final int position : reverseSortedPositions) {
							final String itemToRemove = listAdapter.getItem(position);
							UndoBarController.show(MySightingsActivity.this, itemToRemove + " is removed from the list",
									new UndoListener() {

										@Override
										public void onUndo(Parcelable token) {
											listAdapter.insert(itemToRemove, position);
											listAdapter.notifyDataSetChanged();
										}
									});
							listAdapter.remove(itemToRemove);
							DBHandler dh = DBHandler.getInstance(getApplicationContext());
							dh.deleteSightingFromList(itemToRemove, listName);
						}
						listAdapter.notifyDataSetChanged();
					}
				});
		listview.setOnTouchListener(touchListener);
		listview.setOnScrollListener(touchListener.makeScrollListener());
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