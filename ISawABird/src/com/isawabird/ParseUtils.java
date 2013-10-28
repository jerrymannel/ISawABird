package com.isawabird;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.storage.StorageManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseUtils {

	private static ParseObject currentList = null; 
	private static ParseUser currentUser = null;  
	private static List<ParseObject> lists = null; 
	static ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sightings");
	public static ParseGeoPoint location = null;
	
	
	public static void login(String username, String password) throws ParseException, ISawABirdException{
		try{
			if (username != null){
				currentUser = ParseUser.logIn(username, password);	
			}else{
				
				ParseAnonymousUtils.logIn(new LogInCallback() {
					
					@Override
					public void done(ParseUser user, ParseException ex) {
						if (ex == null){
							Log.d(Consts.LOG_TAG, user.getObjectId());
						}else{
							Log.e(Consts.LOG_TAG, ex.getMessage());
						}
					}
				});
			}
			
		}catch(Exception ex){
			//throw ex;
		}
	}

	public static ParseUser getCurrentUser(){
		currentUser = ParseUser.getCurrentUser();
		return currentUser;
	}
	
	/* Get the lists for the current user */
	public static Vector<BirdList> getLists() throws ParseException{
		Log.v(Consts.LOG_TAG, ">> getLists()");
		/* Rewrite to query from SQLite DB */
		SQLiteDatabase db = MySQLiteHelper.getDB();
		Cursor result = db.query(MySQLiteHelper.BIRDLIST, MySQLiteHelper.BIRDLIST_COLS, null,null, null, null, null);
		Vector<BirdList> returnVal = new Vector<BirdList>();
		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(1));
			Log.v(Consts.LOG_TAG, "Found list " + result.getString(1));
			temp.setDate(new Date(result.getInt(0)));
			temp.setLocation(result.getString(2));
			temp.setNotes(result.getString(3));
			temp.setUsername(result.getString(4));
			temp.setParseObjectID(result.getString(5));
			returnVal.add(temp);
		}		
		Log.v(Consts.LOG_TAG, "<< getLists()");
		return returnVal;
	}
	
	/* Create a new list for this user */
	public static void  createList(BirdList birdList) throws ISawABirdException{
		SQLiteDatabase db = MySQLiteHelper.getDB();
		ContentValues values = birdList.getContentValues();
		values.put("isUploadRequired", true); 
		try{
			long  result = db.insertOrThrow("BirdList", null, values);
		
			if (result == -1){
				Log.e(Consts.LOG_TAG, "Error occurred"); 
			}
		}catch(SQLiteException ex){
			Log.e(Consts.LOG_TAG, "Error occurred adding a new table " + ex.getMessage());
			throw new ISawABirdException("Unable to create a new list. Perhaps, a list by the name already exists ?");
		}
	}
	
	/* Add a sighting to a given list */
	public static void addSightingToCurrentList(String species) throws ISawABirdException { 
		Sighting s = new Sighting(species);
		SQLiteDatabase db = MySQLiteHelper.getDB(); 
		String existingSightingSQL = "ListName = ? AND Username = ? AND Species = ?" ; 
		Cursor existingSightings = db.query(MySQLiteHelper.SIGHTING, new String[] {"ListName" ,  "Species",  "Username"}, existingSightingSQL,
				new String [] {Utils.getCurrentListName(), ParseUtils.getCurrentUser().getUsername(), species } , null,null, null );
		if (existingSightings.getCount() <= 0){
			try{
				long ret = db.insertOrThrow(MySQLiteHelper.SIGHTING, null, s.getContentValues()); 
			}catch(SQLiteException ex){
				throw new ISawABirdException(ex.getMessage());
			}
		}else{
			// TODO : Increment number of birds if this entry is already there. 
			Log.w(Consts.LOG_TAG, species + " not added to list " + Utils.getCurrentListName() ); 
		}
	}

	/* Get all sightings for a given list */
	public static Vector<Sighting> getSightingsForList(String listName) {
		SQLiteDatabase db = MySQLiteHelper.getDB(); 
		Vector<Sighting> returnVal = new Vector<Sighting>();
		
		String existingSightingSQL = "ListName = ? AND Username = ?" ; 
		Cursor result = db.query(MySQLiteHelper.SIGHTING, null, existingSightingSQL,
				new String [] {Utils.getCurrentListName(), ParseUtils.getCurrentUser().getUsername() } , null,null, null );
		
		while(result.moveToNext()){
			Sighting s = new Sighting(result.getString(5));
			
		}
		
		return null;
	}

}
