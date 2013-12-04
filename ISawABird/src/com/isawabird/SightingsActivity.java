package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;

public class SightingsActivity extends Activity {

	private SightingListAdapter mSightingListAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysightings);

		final ListView listview = (ListView) findViewById(R.id.mysightingsView);

		Bundle b = getIntent().getExtras();
		final String listName = b.getString("listName");

		if(listName != null) {
			getActionBar().setTitle(listName);
		}

		new QuerySightingsAsyncTask().execute(listName, ParseUtils.getCurrentUsername());

		mSightingListAdapter = new SightingListAdapter(this, null, null);
		listview.setAdapter(mSightingListAdapter);

		//TODO: implement multiple selection based delete
	}

	private class SightingListAdapter extends BaseAdapter {
		public ArrayList<String> commonNameList;
		public ArrayList<String> scientificNameList;

		public SightingListAdapter(Context context,
				ArrayList<String> commonNameList, ArrayList<String> scientificNameList) {
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
			if(commonNameList == null) return 0;
			return commonNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	private class QuerySightingsAsyncTask extends AsyncTask<String, Void, ArrayList<Sighting>> {

		protected ArrayList<Sighting> doInBackground(String... params) {
			if(params == null || params.length != 2) 
				return null;

			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			return dh.getSightingsByListName(params[0], params[1]);
		}

		protected void onPostExecute(ArrayList<Sighting> result) {

			if (result == null || result.size() == 0) {
				return;
			}

			Log.i(Consts.TAG, "Sightings count: " + result.size());
			
			mSightingListAdapter.commonNameList = new ArrayList<String>();
			mSightingListAdapter.scientificNameList = new ArrayList<String>();

			for (Sighting sighting : result) {
				Log.i(Consts.TAG, "Sighting :: " + sighting.getSpecies().fullName);
				mSightingListAdapter.commonNameList.add(sighting.getSpecies().commonName);
				mSightingListAdapter.scientificNameList.add(sighting.getSpecies().scientificName);
			}

			mSightingListAdapter.notifyDataSetChanged();
		}
	}
}