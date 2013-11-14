package com.isawabird.test;

import com.isawabird.Consts;
import com.isawabird.parse.ParseSyncAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DummyAsyncTask extends AsyncTask {

	private Context context = null; 
	
	public DummyAsyncTask(Context context){
		this.context = context;
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		Log.i(Consts.TAG, "Inside doInBackground");
		ParseSyncAdapter adap = new ParseSyncAdapter(context, true); 
		adap.doSync();
		return null;
	}

}
