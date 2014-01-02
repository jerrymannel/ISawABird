package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;

public class SightingsActivity extends Activity {

	private SightingListAdapter mSightingListAdapter;
	private ListView mSightingListview;
	private int deleteSightingListPosition = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mysightings);

		mSightingListview = (ListView) findViewById(R.id.mysightingsView);

		Bundle b = getIntent().getExtras();
		final String listName = b.getString("listName");

		if(listName != null) {
			getActionBar().setTitle(listName);
		}

		new QuerySightingsAsyncTask().execute(listName, ParseUtils.getCurrentUsername());

		mSightingListAdapter = new SightingListAdapter(this, null, null, null);
		mSightingListview.setAdapter(mSightingListAdapter);

		registerForContextMenu(mSightingListview);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sighting_context_menu, menu);
		
		/* Check the heard only option if needed */ 
		long sightingID = mSightingListAdapter.idList.get(((AdapterContextMenuInfo)menuInfo).position);
		Log.i(Consts.TAG, "Sighiting id is  " + sightingID);
		DBHandler dh = DBHandler.getInstance(getApplicationContext());
		boolean heardOnly = dh.isSightingHeardOnly(sightingID); 
		menu.findItem(R.id.action_heardOnly).setChecked(heardOnly);
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				if(deleteSightingListPosition == -1) return;
				new DeleteSightingAsyncTask().execute(deleteSightingListPosition);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				deleteSightingListPosition = -1;
				break;
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.action_delete_list:
			deleteSightingListPosition  = info.position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
			.setNegativeButton("No", dialogClickListener).show();
			return true;
			
		case R.id.action_heardOnly:
			long sightingID = mSightingListAdapter.idList.get(info.position);
			Log.i(Consts.TAG, "Sighting id is " + sightingID); 
			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			dh.setHeardOnly(sightingID, !item.isChecked());	
			SyncUtils.triggerRefresh(false);
			//dh.dumpTable(DBConsts.TABLE_SIGHTING);
			return true; 
		default:
			return super.onContextItemSelected(item);
		}
	}

	private class SightingListAdapter extends BaseAdapter {
		public ArrayList<String> commonNameList;
		public ArrayList<String> scientificNameList;
		public ArrayList<Long> idList;

		public SightingListAdapter(Context context,
				ArrayList<String> commonNameList, ArrayList<String> scientificNameList, ArrayList<Long> idList) {
			this.commonNameList = commonNameList;
			this.scientificNameList = scientificNameList;
			this.idList = idList;
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
			scientificView.setTypeface(null, Typeface.ITALIC);
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

		public void remove(int position) {
			if(idList == null || position >= idList.size()) return;

			idList.remove(position);
			commonNameList.remove(position);
			scientificNameList.remove(position);
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
			mSightingListAdapter.idList = new ArrayList<Long>();

			for (Sighting sighting : result) {
				Log.i(Consts.TAG, "Sighting :: " + sighting.getSpecies().getFullName());
				Species species = sighting.getSpecies();
				mSightingListAdapter.commonNameList.add(species.commonName);
				mSightingListAdapter.scientificNameList.add(species.scientificName);
				mSightingListAdapter.idList.add(sighting.getId());
			}
			mSightingListAdapter.notifyDataSetChanged();
		}
	}

	private class DeleteSightingAsyncTask extends AsyncTask<Integer, String, Integer> {

		protected Integer doInBackground(Integer... params) {
			if(params[0] == -1) return -1;

			long sightingId = mSightingListAdapter.idList.get(params[0]);

			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			dh.deleteSighting(sightingId);
			return params[0];
		}

		@Override
		protected void onPostExecute(Integer position) {
			if(position != -1) {
				mSightingListAdapter.remove(position);
				mSightingListAdapter.notifyDataSetChanged();
				deleteSightingListPosition = -1;
				SyncUtils.triggerRefresh(false);
			}
		}
	}
}