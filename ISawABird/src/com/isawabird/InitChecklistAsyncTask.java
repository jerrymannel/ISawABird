package com.isawabird;

import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class InitChecklistAsyncTask extends AsyncTask<Void, Void, Void> {

	Context context = null; 
	public InitChecklistAsyncTask(Context context){
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			/* Initialize the checklists */
			String checklistName = PreferenceManager.getDefaultSharedPreferences(context)
					.getString("masterChecklist", "India"); 
			Log.i(Consts.TAG, "Starting checklist init for " + checklistName + " at " + new Date().toString());
			Utils.initializeChecklist(context, checklistName);
			Log.i(Consts.TAG, "Initializing checklist complete at " + new Date().toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
