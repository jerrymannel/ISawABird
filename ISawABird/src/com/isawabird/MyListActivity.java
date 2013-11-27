package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylists);

		DBHandler mydbh = DBHandler.getInstance(MainActivity.getContext());
		ArrayList<BirdList> myBirdLists = mydbh.getBirdLists(ParseUtils.getCurrentUsername());

		final ListView listview = (ListView) findViewById(R.id.mylistView);
		btn_add_new_list = (TextView) findViewById(R.id.btn_add_new_list);
		editText_new_list_name = (EditText) findViewById(R.id.editText_new_list_name);
		btn_new_list_cancel = (TextView) findViewById(R.id.btn_new_list_cancel);
		btn_new_list_save = (TextView) findViewById(R.id.btn_new_list_save);
		layout_new_list = (View) findViewById(R.id.layout_new_list);

		btn_add_new_list.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout_new_list.setVisibility(View.VISIBLE);
			}
		});

		btn_new_list_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editText_new_list_name.setText("");
				layout_new_list.setVisibility(View.INVISIBLE);
			}
		});

		// FIXME : how to create a new list
		btn_new_list_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				// mydbh.addBirdList(editText_new_list_name.getText(), true);
				DBHandler dh = DBHandler.getInstance(MainActivity.getContext());
				BirdList list = new BirdList(editText_new_list_name.getText().toString()); 
				try{
					dh.addBirdList(list, true);
				}catch(ISawABirdException ex){
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
					UndoBarController.show(MyListActivity.this, itemToRemove + " is removed from the list", new UndoListener() {

						@Override
						public void onUndo(Parcelable token) {
							listAdapter.insert(itemToRemove, position);
							listAdapter.notifyDataSetChanged();
						}
					});
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
			TextView textView2 = (TextView) rowView.findViewById(R.id.mylistItem_close);

			textView1.setText(values.get(position));
			textView2.setText(" ");

			return rowView;
		}
	}
}