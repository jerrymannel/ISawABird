package com.isawabird.db;

import java.util.Date;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
import com.isawabird.Sighting;
import com.isawabird.Utils;
import com.isawabird.parse.ParseUtils;
import com.parse.ParseException;

public class DBUtils {

	/* Get all sightings for a given list */
	public static Vector<Sighting> getSightingsForList(String listName) {
		SQLiteDatabase db = MySQLiteHelper.getDB(); 
		Vector<Sighting> returnVal = new Vector<Sighting>();
		
		String existingSightingSQL = "ListName = ? AND Username = ?" ; 
		Cursor result = db.query(MySQLiteHelper.SIGHTING, null, existingSightingSQL,
				new String [] {Utils.getCurrentListName(), ParseUtils.getCurrentUser().getUsername() } , null,null, null );
		
		while(result.moveToNext()){
			Sighting s = new Sighting(result.getString(5));
			s.setDate(new Date(result.getInt(0)));
			s.setListName(result.getString(1));
			s.setLatitude(result.getFloat(2));
			s.setLongitude(result.getFloat(3));
			s.setNumberOfBirds(result.getInt(4));
			s.setUsername(result.getString(6));
			s.setParseObjectID(result.getString(7));
			s.setUpdateRequired(result.getInt(8));
			returnVal.add(s);
		}
		
		return returnVal;
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

	
	/* Create a new list for this user */
	public static void  createBirdList(BirdList birdList) throws ISawABirdException{
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

	
	/* Get the lists for the current user */
	public static Vector<BirdList> getBirdLists(){
		Log.v(Consts.LOG_TAG, ">> getLists()");
		/* Rewrite to query from SQLite DB */
		SQLiteDatabase db = MySQLiteHelper.getDB();
		Cursor result = db.query(MySQLiteHelper.BIRDLIST, MySQLiteHelper.BIRDLIST_COLS, null,null, null, null, null);
		Vector<BirdList> returnVal = new Vector<BirdList>();
		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(2));
			Log.v(Consts.LOG_TAG, "Found list " + result.getString(2));
			temp.setDate(new Date(result.getInt(1)));
			temp.setLocation(result.getString(3));
			temp.setNotes(result.getString(4));
			temp.setUsername(result.getString(5));
			temp.setParseObjectID(result.getString(6));
			temp.setListID(result.getInt(0));
			returnVal.add(temp);
		}		
		Log.v(Consts.LOG_TAG, "<< getLists()");
		return returnVal;
	}

}
