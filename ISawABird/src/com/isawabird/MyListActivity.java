package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.isawabird.utilities.PostUndoAction;
import com.isawabird.utilities.SwipeDismissListViewTouchListener;
import com.isawabird.utilities.UndoBarController;
import com.isawabird.utilities.UndoBarController.UndoListener;

public class MyListActivity extends Activity {

	private ArrayList<String> myList;
	TextView btn_add_new_list;
	EditText editText_new_list_name;
	TextView btn_new_list_cancel;
	TextView btn_new_list_save;
	View layout_new_list;
	DBHandler mydbh;
	RadioButton listRadioButton = null;
	int active_list_position;
	int listIndex = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylists);

		mydbh = DBHandler.getInstance(MainActivity.getContext());
		ArrayList<BirdList> myBirdLists = mydbh.getBirdLists(ParseUtils.getCurrentUsername());

		final InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		final ListView listview = (ListView) findViewById(R.id.mylistView);
		btn_add_new_list = (TextView) findViewById(R.id.btn_add_new_list);
		editText_new_list_name = (EditText) findViewById(R.id.editText_new_list_name);
		btn_new_list_cancel = (TextView) findViewById(R.id.btn_new_list_cancel);
		btn_new_list_save = (TextView) findViewById(R.id.btn_new_list_save);
		layout_new_list = (View) findViewById(R.id.layout_new_list);

		btn_add_new_list.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout_new_list.setVisibility(View.VISIBLE);
				btn_add_new_list.setVisibility(View.INVISIBLE);
			}
		});

		btn_new_list_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				editText_new_list_name.setText("");
				layout_new_list.setVisibility(View.GONE);
				btn_add_new_list.setVisibility(View.VISIBLE);
			}
		});

		// TODO: Read more fields from user to create a new list
		btn_new_list_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				// mydbh.addBirdList(editText_new_list_name.getText(), true);
				BirdList list = new BirdList(editText_new_list_name.getText().toString());
				try {
					mydbh.addBirdList(list, true);
				} catch (ISawABirdException ex) {
					// TODO : Specify a proper error code if list already exists
					Toast.makeText(MainActivity.getContext(), "List already exists. Specify a different name", Toast.LENGTH_SHORT);
				}
				Toast.makeText(getBaseContext(), "Added new list :: " + editText_new_list_name.getText(), Toast.LENGTH_SHORT).show();
				editText_new_list_name.setText("");
				layout_new_list.setVisibility(View.INVISIBLE);
			}
		});

		/*
		 * (Jerry) Populate the list
		 */
		myList = new ArrayList<String>();
		for (BirdList bird : myBirdLists) {
			myList.add(bird.getListName());
		}

		final MyListAdapter listAdapter = new MyListAdapter(this, myList);
		listview.setAdapter(listAdapter);
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

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listview, new SwipeDismissListViewTouchListener.DismissCallbacks() {
			@Override
			public boolean canDismiss(int position) {
				return true;
			}

			@Override
			public void onDismiss(ListView listView, int[] reverseSortedPositions) {
				for (final int position : reverseSortedPositions) {
					final String itemToRemove = listAdapter.getItem(position);
					BirdList list = null;
					try {
						list = mydbh.getBirdListByName(itemToRemove);
					} catch (ISawABirdException ex) {
						Toast.makeText(getApplicationContext(), "Unable to delete list", Toast.LENGTH_SHORT).show();
					}
					PostUndoAction action = new PostUndoAction() {

						@Override
						public void action() {
							mydbh.deleteList(itemToRemove);
						}
					};
					UndoBarController.show(MyListActivity.this, itemToRemove + " removed from the list", new UndoListener() {

						@Override
						public void onUndo(Parcelable token) {
							listAdapter.insert(itemToRemove, position);
							listAdapter.notifyDataSetChanged();
							// TODO Handle case when data has been uploaded to
							// parse and when it has not been uploaded to parse.
						}
					}, action);
					listAdapter.remove(itemToRemove);

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
			super(context, R.layout.mylists_row, values);
			this.context = context;
			this.values = values;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.mylists_row, parent, false);

			TextView textView1 = (TextView) rowView.findViewById(R.id.mylistsItem_name);
			RadioButton rb = (RadioButton) rowView.findViewById(R.id.radioButton_currList);

			textView1.setText(values.get(position));
			if (values.get(position).equals(Utils.getCurrentListName())) {
				rb.setChecked(true);
				listRadioButton = rb;
				active_list_position = position;
				Log.i(Consts.TAG, values.get(position) + " >>> " + active_list_position);
			}

			rb.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					View vMain = ((View) v.getParent());
			        int newIndex = ((ViewGroup) vMain.getParent()).indexOfChild(vMain);
			        Log.i(Consts.TAG, "Active Pos >>> " + newIndex);
			        
			        if (active_list_position == newIndex) return;

	                listRadioButton.setChecked(false);
			        listRadioButton = (RadioButton) v;
			        active_list_position = newIndex;
				}
			});

			return rowView;
		}
	}
}