package com.isawabird.db;

import java.util.TimerTask;

import com.isawabird.Consts;
import com.isawabird.MainActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.util.Log;

public class ParseSyncThread extends TimerTask {

	private static String BASE_URL = "https://api.parse.com/";
	private static String CREATE_PATH = "1/classes/"; 
	private static String PARSE_BIRDLIST_CLASS = "BirdList"; 
	private static String PARSE_SIGHTINGS_CLASS = "Sightings";

	@Override
	public void run() {
		if (isNetworkAvailable()){
			SQLiteDatabase db = MySQLiteHelper.getDB(); 
			
			/* Query all lists */
			String query = "isUploadRequired > 0";
			Cursor listResult = db.query("BIRDLIST", null, query, null, null, null, null);
			while(listResult.moveToNext()){
				
			}
			
		}
	}
	
	private boolean isNetworkAvailable(){
		NetworkInfo nwInfo = MainActivity.getConnectivityManager().getActiveNetworkInfo(); 
		Log.v(Consts.LOG_TAG, "Network availability is " + (nwInfo != null && nwInfo.isConnected()));
		return (nwInfo != null && nwInfo.isConnected());
	}

}
