package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;

public class BirdListActivity extends Activity {

	private EditText mNewListNameText;
	private TextView mNewListCancelButton;
	private TextView mNewListSaveButton;
	private View mNewListView;
	private ListView mBirdListView;
	private ListAdapter mListAdapter;

	private long undoListId = -1;
	private int checkedRowPosition;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylists);

		mBirdListView = (ListView) findViewById(R.id.mylistView);
		mNewListSaveButton = (TextView) findViewById(R.id.btn_new_list_save);
		mNewListCancelButton = (TextView) findViewById(R.id.btn_new_list_cancel);
		mNewListNameText = (EditText) findViewById(R.id.editText_new_list_name);
		mNewListView = (View) findViewById(R.id.layout_new_list);

		final InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mNewListCancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mNewListNameText.setText("");
				mNewListView.setVisibility(View.GONE);
			}
		});

		// TODO: Read more fields from user to create a new list
		mNewListSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				// dh.addBirdList(mNewListNameText.getText(), true);
				BirdList list = new BirdList(mNewListNameText.getText().toString());
				new AddBirdListAsyncTask().execute(list);
			}
		});

		mListAdapter = new ListAdapter(this, null);
		mBirdListView.setAdapter(mListAdapter);
		mBirdListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mBirdListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private int count = 0;
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// Here you can do something when items are selected/de-selected,
				// such as update the title in the CAB
				if(checked) {
					++count;
				} else {
					--count;
				}
				mode.setTitle(count + " selected " + checked);
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
				case R.id.action_delete_list:
					//deleteSelectedItems();
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.list_context_menu, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are deselected/unchecked.
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		});
		
		mBirdListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Bundle b = new Bundle();
				b.putString("listName", mListAdapter.birdLists.get(position).getListName());

				Intent mySightingIntent = new Intent(getApplicationContext(), SightingsActivity.class);
				mySightingIntent.putExtras(b);
				startActivity(mySightingIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_list:
			// show 'add new list' view
			mNewListView.setVisibility(View.VISIBLE);
			mNewListNameText.requestFocus();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		new QueryBirdListAsyncTask().execute();
	}

	/*@Override
	public void onBackPressed() {
		if(undoListId != -1) {
			new DeleteListAsyncTask().execute(undoListId);
		}	    
		super.onBackPressed();
	}*/

	public class ListAdapter extends ArrayAdapter<BirdList> {

		private TextView mListNameText;
		private RadioButton mRadioButton;

		public ArrayList<BirdList> birdLists;

		public ListAdapter(Context context, ArrayList<BirdList> birdLists) {
			super(context, R.layout.mylists_row, birdLists);
			this.birdLists = birdLists;
		}

		@Override
		public int getCount() {
			if (birdLists == null)
				return 0;
			return birdLists.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View rowView = convertView;

			if (rowView == null) {
				LayoutInflater inflater = getLayoutInflater();
				rowView = inflater.inflate(R.layout.mylists_row, parent, false);
			}

			mListNameText = (TextView) rowView.findViewById(R.id.mylistsItem_name);
			mListNameText.setTypeface(Utils.getOpenSansLightTypeface(BirdListActivity.this));
			mRadioButton = (RadioButton) rowView.findViewById(R.id.radioButton_currList);

			mListNameText.setText(birdLists.get(position).getListName());

			if (birdLists.get(position).getId() == Utils.getCurrentListID()) {
				mRadioButton.setChecked(true);
				checkedRowPosition = position;
			} else {
				mRadioButton.setChecked(false);
			}

			mRadioButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.i(Consts.TAG, "Active Pos >>> " + position + ", previous checked: " + checkedRowPosition);
					if (checkedRowPosition == position)
						return;

					View vMain = ((View) v.getParent());
					View previousCheckedRow = ((ViewGroup) vMain.getParent()).getChildAt(checkedRowPosition);
					RadioButton previousCheckedRadio = (RadioButton) previousCheckedRow.findViewById(R.id.radioButton_currList);
					previousCheckedRadio.setChecked(false);

					checkedRowPosition = position;
					BirdList newList = birdLists.get(checkedRowPosition);
					Utils.setCurrentList(newList.getListName(), newList.getId());
				}
			});
			return rowView;
		}
	}

	private class AddBirdListAsyncTask extends AsyncTask<BirdList, String, BirdList> {

		@Override
		protected BirdList doInBackground(BirdList... params) {
			if(params == null || params.length == 0) return null;

			long result = -1;
			try {
				result = DBHandler.getInstance(getApplicationContext()).addBirdList(params[0], true);
			} catch (ISawABirdException e) {
				e.printStackTrace();
				if(e.getErrorCode() == ISawABirdException.ERR_LIST_ALREADY_EXISTS) {
					publishProgress("List already exists. Specify a different name");
				}
			}
			if(result == -1) return null;

			params[0].setId(result);
			return params[0];
		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(BirdList result) {
			if(result != null) {
				Toast.makeText(getBaseContext(), "Added new list :: " + mNewListNameText.getText(), Toast.LENGTH_SHORT).show();
				mNewListNameText.setText("");
				mNewListView.setVisibility(View.GONE);
				if(mListAdapter.birdLists == null) {
					mListAdapter.birdLists = new ArrayList<BirdList>();
				}
				mListAdapter.birdLists.add(0, result);
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

	private class QueryBirdListAsyncTask extends AsyncTask<Void, Void, ArrayList<BirdList>> {

		protected ArrayList<BirdList> doInBackground(Void... params) {

			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			dh = DBHandler.getInstance(getApplicationContext());
			return dh.getBirdLists(ParseUtils.getCurrentUsername());
		}

		protected void onPostExecute(ArrayList<BirdList> result) {

			if (result == null || result.size() == 0) {
				return;
			}
			Log.i(Consts.TAG, "List count: " + result.size());
			mListAdapter.birdLists = result;
			mListAdapter.notifyDataSetChanged();
		}
	}

	private class DeleteListAsyncTask extends AsyncTask<Long, String, Long> {

		protected Long doInBackground(Long... params) {
			if(params[0] == -1) return -1L;

			DBHandler dh = DBHandler.getInstance(getApplicationContext());
			dh.deleteList(params[0]);
			return params[0];
		}

		@Override
		protected void onPostExecute(Long result) {
			if(result != -1 && result == undoListId) {
				SyncUtils.triggerRefresh();
				undoListId = -1;
			}
		}
	}
}