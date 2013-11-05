package com.isawabird.db;

import java.util.Date;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
import com.isawabird.BirdList;
import com.isawabird.Sighting;
import com.isawabird.Species;
import com.isawabird.Utils;
import com.isawabird.parse.ParseUtils;

public class DBHandler extends SQLiteOpenHelper {

	private static DBHandler mInstance = null; 
	private SQLiteDatabase db;


	public static synchronized  DBHandler getInstance(Context ctx) {

		// Use the application context, which will ensure that you 
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (mInstance == null) {
			mInstance = new DBHandler(ctx.getApplicationContext());
		}
		return mInstance;
	}

	private DBHandler(Context ctx) {
		super(ctx, DBConsts.DATABASE_NAME, null, DBConsts.DATABASE_VERSION);

		if(db == null) db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		this.db = database;

		Log.i(Consts.TAG, "in onCreate db");
		Log.i(Consts.TAG, DBConsts.CREATE_LIST);
		Log.i(Consts.TAG, DBConsts.CREATE_SIGHTING);
		try {
			db.beginTransaction();
			db.execSQL(DBConsts.CREATE_LIST);
			db.execSQL(DBConsts.CREATE_SIGHTING);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(Consts.TAG, "exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/* Get all sightings for a given list */
	public Vector<Sighting> getSightingsByListName(String listName, String username) {

		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(
				DBConsts.QUERY_SIGHTINGS_BY_LISTNAME, 
				new String [] { BirdList.getCurrentListName(), username });

		if(result.getColumnCount() <= 0) return null;
		
		Vector<Sighting> sightings = new Vector<Sighting>();
		
		while(result.moveToNext()){
			Sighting s = new Sighting(result.getString(result.getColumnIndexOrThrow(DBConsts.SIGHTING_SPECIES)));
			s.setUsername(username);			
			s.setDate(new Date(result.getInt(result.getColumnIndexOrThrow(DBConsts.SIGHTING_DATE))));
			s.setListName(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			s.setLatitude(result.getFloat(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LATITUDE)));
			s.setLongitude(result.getFloat(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LONGITUDE)));
			s.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));

			sightings.add(s);
		}

		return sightings;
	}


	/* Add a sighting to a given list */
	public void addSighting(Sighting sighting, long listId, String username) throws ISawABirdException { 

		if(!db.isOpen()) db = getWritableDatabase();

		if(sighting == null) {
			throw new RuntimeException("Sighting = " + sighting + ", listId = " + listId);
		}

		if(!isSightingExist(sighting.getSpecies().getFullName(), listId, username)) {
			try {

				ContentValues values = new ContentValues();
				values.put(DBConsts.SIGHTING_SPECIES, sighting.getSpecies().getFullName());
				values.put(DBConsts.SIGHTING_LIST_ID, listId);
				values.put(DBConsts.SIGHTING_DATE, sighting.getDate().getTime());
				values.put(DBConsts.SIGHTING_LATITUDE, sighting.getLatitude());
				values.put(DBConsts.SIGHTING_LONGITUDE, sighting.getLongitude());								
				values.put(DBConsts.SIGHTING_NOTES, sighting.getNotes());

				long result = db.insertOrThrow(DBConsts.TABLE_SIGHTING, null, values);

				if(result != -1) {
					values.clear();
					values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, "1");
					values.put(DBConsts.PARSE_TYPE, DBConsts.TABLE_SIGHTING);
					values.put(DBConsts.PARSE_TYPE_ID, result);

					result = db.insertOrThrow(DBConsts.TABLE_PARSE, null, values);
				}
			} catch(SQLiteException ex) {
				throw new ISawABirdException(ex.getMessage());
			}
		} else{
			// TODO : Increment number of birds if this entry is already there. 
			Log.w(Consts.TAG, sighting.getSpecies() + " not added to list " + BirdList.getCurrentListName() ); 
		}
	}

	public boolean isSightingExist(String species, long listId,
			String username) {
		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(
				DBConsts.QUERY_IS_SIGHTINGS_EXIST, 
				new String [] { Long.toString(listId), species, username });
		return (result.getInt(0) == 1);
	}

	/* Create a new list for this user */
	public void  addBirdList(BirdList birdList, String username) throws ISawABirdException{
		if(!db.isOpen()) db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DBConsts.LIST_NAME, birdList.getListName()); 
		values.put(DBConsts.LIST_USER, username);
		values.put(DBConsts.LIST_DATE, birdList.getDate().getTime());
		values.put(DBConsts.LIST_NOTES, birdList.getNotes());
				
		try{
			long result = db.insertOrThrow(DBConsts.TABLE_LIST, null, values);

			if (result == -1){
				Log.e(Consts.TAG, "Error occurred"); 
			}
		}catch(SQLiteException ex){
			Log.e(Consts.TAG, "Error occurred adding a new table " + ex.getMessage());
			throw new ISawABirdException("Unable to create a new list. Perhaps, a list by the name already exists ?");
		}
	}


	/* Get the lists for the current user */
	public Vector<BirdList> getBirdLists(String username){

		if(!db.isOpen()) db = getWritableDatabase();

		// TODO: use username
		Cursor result = db.rawQuery(
				DBConsts.QUERY_LIST, null);
		
		if(result.getColumnCount() <= 0) return null;
		
		Vector<BirdList> birdList = new Vector<BirdList>();
		
		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			Log.v(Consts.TAG, "Found list " + result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			temp.setDate(new Date(result.getInt(result.getColumnIndexOrThrow(DBConsts.LIST_DATE))));
			temp.setNotes(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NOTES)));
			temp.setUsername(username);
			temp.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
			temp.setId(result.getInt(result.getColumnIndexOrThrow(DBConsts.ID)));
			birdList.add(temp);
		}
		
		return birdList;
	}

	public Vector<BirdList> getBirdListToSync(boolean toCreate, String username) {
		if(!db.isOpen()) db = getWritableDatabase();
		
		String query = null;
		if(toCreate) {
			query = DBConsts.QUERY_LIST_SYNC_CREATE;
		} else {
			query = DBConsts.QUERY_LIST_SYNC_UPDATE;
		}

		Cursor result = db.rawQuery(query, null);
		
		if(result.getColumnCount() <= 0) return null;
		
		Vector<BirdList> birdList = new Vector<BirdList>();
		
		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			temp.setDate(new Date(result.getInt(result.getColumnIndexOrThrow(DBConsts.LIST_DATE))));
			temp.setNotes(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NOTES)));
			temp.setUsername(username);
			temp.setId(result.getInt(result.getColumnIndexOrThrow(DBConsts.ID)));
			if(!toCreate) {
				temp.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
			}
			
			birdList.add(temp);
		}
		return birdList;
	}
}