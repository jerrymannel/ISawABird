package com.isawabird;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends Activity {

	private ArrayList<Species> species = null;
	private ArrayList<Species> speciesSubset = new ArrayList<Species>();

	// listview section
	private static StandardArrayAdapter arrayAdapter;
	private SectionListAdapter sectionAdapter;
	private SectionListView listView;

	EditText search;

	Typeface openSansLight;
	Typeface openSansBold;
	Typeface arvo;

	// sideIndex
	LinearLayout sideIndex;
	// height of side index
	private int sideIndexHeight;

	// How many alphabets are there in the English language?
	private int sideIndexSize = 26;

	// list with items for side index
	private ArrayList<Object[]> sideIndexList = new ArrayList<Object[]>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		getActionBar().hide();

		openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
		openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
		arvo = Typeface.createFromAsset(getAssets(), "fonts/Arvo-Regular.ttf");

		search = (EditText) findViewById(R.id.search_query);
		search.addTextChangedListener(filterTextWatcher);
		listView = (SectionListView) findViewById(R.id.section_list_view);
		sideIndex = (LinearLayout) findViewById(R.id.list_index);
		sideIndex.setOnTouchListener(new Indextouch());

		search.setTypeface(arvo);
		search.requestFocus();
			
//		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(search, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		species = Utils.getAllSpecies();
		arrayAdapter = new StandardArrayAdapter(species);
		sectionAdapter = new SectionListAdapter(this.getLayoutInflater(), arrayAdapter);
		listView.setAdapter(sectionAdapter);
		//PoplulateSideview();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj;
				if(!((obj = parent.getItemAtPosition(position)) instanceof Species)) return;
				
				Species species = (Species) obj;

				// Jerry: Return to main intend after adding a bird
				//Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
				Intent mainIntent = getIntent();
				mainIntent.putExtra(Consts.SPECIES_NAME, species.getFullName());
				setResult(14, mainIntent);
				finish();
			}
		});
	}

	private class Indextouch implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
				// now you know coordinates of touch
				float sideIndexX = event.getX();
				float sideIndexY = event.getY();

				sideIndexHeight = sideIndex.getHeight();

				if (sideIndexX > 0 && sideIndexY > 0) {
					// and can display a proper item it country list
					displayListItem(sideIndexY);
				}
			}
			return true;
		}
	};

	private class StandardArrayAdapter extends BaseAdapter implements Filterable {

		private ArrayList<Species> items;
		private SpeciesFilter speciesFilter;

		public StandardArrayAdapter(ArrayList<Species> args) {
			this.items = args;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.search_row, null);
			}
			TextView textView = (TextView) view.findViewById(R.id.row_title);
			if (textView != null) {
				textView.setText(items.get(position).getFullName());
				textView.setTypeface(arvo);
			}
			return view;
		}

		public int getCount() {
			if(items == null) return 0;
			return items.size();
		}

		public Filter getFilter() {
			if(speciesFilter == null) {
				speciesFilter = new SpeciesFilter();
			}
			return speciesFilter;
		}

		public Object getItem(int position) {
			if(items == null) return null;
			return items.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}
	}

	public class SpeciesFilter extends Filter {

		private FilterResults result = new FilterResults();

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// NOTE: this function is *always* called from a background thread,
			// and not the UI thread.
			constraint = search.getText().toString();

			if (constraint != null && constraint.toString().length() >= 2) {
				// do not show side index while filter results
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((LinearLayout) findViewById(R.id.list_index)).setVisibility(View.INVISIBLE);
					}
				});
				//Log.i(Consts.TAG, "Searching from subset of size " + ((speciesSubset == null) ? 0 : speciesSubset.size())) ;
				ArrayList<Species> searchResult = Utils.search(constraint.toString(), speciesSubset);

				result.count = searchResult.size();
				result.values = searchResult;
				speciesSubset = searchResult; 
			} else {
				speciesSubset.clear();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((LinearLayout) findViewById(R.id.list_index)).setVisibility(View.VISIBLE);
					}
				});
				synchronized (this) {
					result.count = species.size();
					result.values = species;
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			arrayAdapter.items = (ArrayList<Species>) results.values;
			sectionAdapter = new SectionListAdapter(getLayoutInflater(), arrayAdapter);
			listView.setAdapter(sectionAdapter);
		}
	}

	private void displayListItem(float sideIndexY) {

		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / sideIndexSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		if (itemPosition < sideIndexList.size()) {
			// get the item (we can do it since we know item index)
			Object[] indexItem = sideIndexList.get(itemPosition);
			listView.setSelectionFromTop((Integer) indexItem[1], 0);
		}
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable searchText) {
			if(searchText.toString().length() < 2) return;
			SearchActivity.arrayAdapter.getFilter().filter(searchText.toString());
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// your search logic here
		}
	};
}