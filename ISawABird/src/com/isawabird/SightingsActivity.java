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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
import com.isawabird.utilities.PostUndoAction;
import com.isawabird.utilities.SwipeDismissListViewTouchListener;
import com.isawabird.utilities.UndoBarController;
import com.isawabird.utilities.UndoBarController.UndoListener;

public class SightingsActivity extends Activity {

	private ArrayList<String> commonNameList;
	private ArrayList<String> scientificNameList;
	private TextView titleTextView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysightings);

		Bundle b = getIntent().getExtras();
		final String listName = b.getString("listName");

		DBHandler mydbh = DBHandler.getInstance(getApplicationContext());
		final ArrayList<Sighting> myBirdLists = mydbh.getSightingsByListName(listName, ParseUtils.getCurrentUsername());

		final ListView listview = (ListView) findViewById(R.id.mysightingsView);
		titleTextView = (TextView) findViewById(R.id.mysightings_title);

		titleTextView.setText(listName);

		commonNameList = new ArrayList<String>();
		scientificNameList = new ArrayList<String>();
		for (Sighting sighting : myBirdLists) {
			Log.i(Consts.TAG, "Sighting :: " + sighting.getSpecies().getFullName());
			commonNameList.add(sighting.getSpecies().getCommonName());
			scientificNameList.add(sighting.getSpecies().getScientificName());
		}
		final MyListAdapter listAdapter = new MyListAdapter(this, commonNameList, scientificNameList);
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
							final String commonName = commonNameList.get(position);
							final String scientificName = scientificNameList.get(position);
							PostUndoAction action = new PostUndoAction() {
								@Override
								public void action() {
									DBHandler dh = DBHandler.getInstance(getApplicationContext());
									dh.deleteSightingFromList(myBirdLists.get(position).getSpecies().getFullName(), listName);
								}
							};
							UndoBarController.show(SightingsActivity.this, commonNameList.get(position) + " is removed from the list",
									new UndoListener() {

										@Override
										public void onUndo(Parcelable token) {
											// listAdapter.insert(position);
											commonNameList.add(position, commonName);
											scientificNameList.add(position, scientificName);
											listAdapter.notifyDataSetChanged();
										}
									}, action);
							commonNameList.remove(position);
							scientificNameList.remove(position);
						}
						listAdapter.notifyDataSetChanged();
					}
				});
		listview.setOnTouchListener(touchListener);
		listview.setOnScrollListener(touchListener.makeScrollListener());
	}

	private class MyListAdapter extends BaseAdapter {
		private final Context context;
		private final ArrayList<String> commonNameList;
		private final ArrayList<String> scientificNameList;

		public MyListAdapter(Context context, ArrayList<String> commonNameList, ArrayList<String> scientificNameList) {
			this.context = context;
			this.commonNameList = commonNameList;
			this.scientificNameList = scientificNameList;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater inflater = getLayoutInflater();
				rowView = inflater.inflate(R.layout.mysightings_row, parent, false);
			}

			TextView commonView = (TextView) rowView.findViewById(R.id.mysightingsItem_name);
			TextView scientificView = (TextView) rowView.findViewById(R.id.mysightingsItem_scientific_name);
			commonView.setText(this.commonNameList.get(position));
			commonView.setTypeface(Utils.getOpenSansLightTypeface(SightingsActivity.this));
			scientificView.setText(this.scientificNameList.get(position));
			scientificView.setTypeface(Utils.getOpenSansLightTypeface(SightingsActivity.this));

			return rowView;
		}

		@Override
		public int getCount() {
			return this.commonNameList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

	}
}