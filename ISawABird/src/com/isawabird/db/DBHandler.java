package com.isawabird.db;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
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
			db.execSQL(DBConsts.CREATE_FEEDBACK);
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
	public ArrayList<Sighting> getSightingsByListName(String listName, String username) {

		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(
				DBConsts.QUERY_SIGHTINGS_BY_LISTNAME, 
				new String [] { listName});

		if(result.getColumnCount() <= 0) return null;

		ArrayList<Sighting> sightings = new ArrayList<Sighting>();

		while(result.moveToNext()){
			Sighting s = new Sighting(result.getString(result.getColumnIndexOrThrow(DBConsts.SIGHTING_SPECIES)));
			s.setId(result.getLong(result.getColumnIndexOrThrow(DBConsts.ID)));
			s.setDate(new Date(result.getInt(result.getColumnIndexOrThrow(DBConsts.SIGHTING_DATE) ) * 1000));
			s.setListName(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			s.setLatitude(result.getFloat(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LATITUDE)));
			s.setLongitude(result.getFloat(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LONGITUDE)));
			s.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));

			sightings.add(s);
		}

		return sightings;
	}

	/* Get all sightings for current list */
	//TODO: do we really need this method?
	public ArrayList<Sighting> getSightingsForCurrentList(){
		return getSightingsByListName(Utils.getCurrentListName(), ParseUtils.getCurrentUsername());
	}

	/* Add a sighting to a given list */
	public long addSighting(Sighting sighting, long listId, String username) throws ISawABirdException { 
		if(!db.isOpen()) db = getWritableDatabase();

		if(sighting == null) {
			throw new RuntimeException("Sighting = " + sighting + ", listId = " + listId);
		}
		if (listId == -1){
			throw new ISawABirdException(ISawABirdException.ERR_NO_CURRENT_LIST);
		}
		
		long result = -1;
		if(!isSightingExist(sighting.getSpecies().getFullName(), listId, username)) {
			try {
				Log.i(Consts.TAG, "Adding new species to table: " + sighting.getSpecies().getFullName());

				ContentValues values = new ContentValues();
				values.put(DBConsts.SIGHTING_SPECIES, sighting.getSpecies().getFullName());
				values.put(DBConsts.SIGHTING_LIST_ID, listId);
				Log.i(Consts.TAG, "Sighting date is " + (int)(sighting.getDate().getTime()/1000));
				values.put(DBConsts.SIGHTING_DATE, (int)(sighting.getDate().getTime()/1000));
				values.put(DBConsts.SIGHTING_LATITUDE, sighting.getLatitude());
				values.put(DBConsts.SIGHTING_LONGITUDE, sighting.getLongitude());								
				values.put(DBConsts.SIGHTING_NOTES, sighting.getNotes());
				values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, DBConsts.TRUE);
				values.put(DBConsts.PARSE_IS_DELETE_MARKED, DBConsts.FALSE);

				result = db.insertOrThrow(DBConsts.TABLE_SIGHTING, null, values);
				// TODO Remove later
				dumpTable(DBConsts.TABLE_SIGHTING);

			} catch(SQLiteException ex) {
				throw new ISawABirdException(ex.getMessage());
			}
		} else{
			Log.w(Consts.TAG, sighting.getSpecies() + " not added to list with listID: " + listId + ", usrename: " + Utils.getCurrentListName());
			throw new ISawABirdException(ISawABirdException.ERR_SIGHTING_ALREADY_EXISTS); 
		}
		return result;
	}

	/* Add a sighting to the current active list */
	//TODO: do we really need this method?
	public long addSightingToCurrentList(String species) throws ISawABirdException{
		Sighting sighting = new Sighting(new Species(species));
		sighting.setDate(new Date());
		try{
			if (Utils.getCurrentListID() == -1){
				throw new ISawABirdException(ISawABirdException.ERR_NO_CURRENT_LIST);
			}
			return addSighting(sighting, Utils.getCurrentListID(), ParseUtils.getCurrentUsername());
		}catch(ISawABirdException ex){
			throw ex; 
		}
	}

	public boolean isSightingExist(String species, long listId,
			String username) {
		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(
				DBConsts.QUERY_IS_SIGHTINGS_EXIST, 
				new String [] { Long.toString(listId), species });
		Log.i(Consts.TAG, "isSightingExist: " + result.getCount());
		return (result.getCount() != 0);
	}
	
	public int updateBirdList(long listId, BirdList birdList) {
		if(!db.isOpen()) db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DBConsts.LIST_NAME, birdList.getListName()); 
		values.put(DBConsts.LIST_USER, birdList.getUsername());
		values.put(DBConsts.LIST_DATE, birdList.getDate().getTime());
		values.put(DBConsts.LIST_NOTES, birdList.getNotes());
		values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, (birdList.isMarkedForUpload())?1:0);
		values.put(DBConsts.PARSE_IS_DELETE_MARKED, (birdList.isMarkedForDelete())?1:0); 
		
		return db.update(DBConsts.TABLE_LIST, values, DBConsts.ID + "=?", new String[]{Long.toString(birdList.getId())});
	}

	/* Create a new list for this user */
	public long  addBirdList(BirdList birdList, boolean setCurrentList) throws ISawABirdException{
		Log.i(Consts.TAG, " >> addBirdList"); 
		if(!db.isOpen()) db = getWritableDatabase();
				
		BirdList oldBirdList = getBirdListByName(birdList.getListName());
		if(oldBirdList != null) {
			// old bird list with this name already exists
			// check if it is marked for delete
			if(oldBirdList.isMarkedForDelete()) {
				long oldId = oldBirdList.getId();
				// revert it back and mark for update, empty parse values
				oldBirdList.setMarkedForDelete(false);
				oldBirdList.setMarkedForUpload(true);
				//TODO: handle below case - instead of setting it null, we should update parseObjectId after every update sync
				oldBirdList.setParseObjectID(null);
				updateBirdList(oldId, oldBirdList);
				
				if (setCurrentList){
					Utils.setCurrentList(birdList.getListName(), oldId);
				}
				return oldId;
			} else {
				throw new ISawABirdException(ISawABirdException.ERR_LIST_ALREADY_EXISTS);
			}
		}

		ContentValues values = new ContentValues();
		values.put(DBConsts.LIST_NAME, birdList.getListName()); 
		values.put(DBConsts.LIST_USER, birdList.getUsername());
		values.put(DBConsts.LIST_DATE, (int)(birdList.getDate().getTime()/1000));
		values.put(DBConsts.LIST_NOTES, birdList.getNotes());
		values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, 1);
		values.put(DBConsts.PARSE_IS_DELETE_MARKED, 0);
		
		long result = -1;
		try{
			result = db.insertOrThrow(DBConsts.TABLE_LIST, null, values);

			if (result == -1){
				Log.e(Consts.TAG, "Error occurred");
				return result; 
			}
			
			if (setCurrentList){
				Utils.setCurrentList(birdList.getListName(), result);
			}
		} catch(SQLiteException ex) {
			Log.e(Consts.TAG, "Error occurred adding a new table " + ex.getMessage());
		}
		dumpTable(DBConsts.TABLE_LIST);
		
		return result;
	}

	public long getBirdCountByListId(long listId) {
		if(!db.isOpen()) db = getWritableDatabase();

		return DatabaseUtils.queryNumEntries(db, DBConsts.TABLE_SIGHTING,
				DBConsts.SIGHTING_LIST_ID + "=? AND " + DBConsts.PARSE_IS_DELETE_MARKED + "!= 1", new String[] {Long.toString(listId)});
	}

	public long getBirdCountForCurrentList() {
		return getBirdCountByListId(Utils.getCurrentListID());
	}
	
	public BirdList getBirdListByName(String listName) {
		if(!db.isOpen()) db = getWritableDatabase();
		
		Cursor result = db.rawQuery(DBConsts.QUERY_GET_LIST_BY_NAME, new String [] { listName , ParseUtils.getCurrentUsername() });
		if (result.getCount() == 0){
			return null; 
		}
		
		result.moveToNext();
		BirdList list = new BirdList(listName);
		list.setId(result.getLong(result.getColumnIndex(DBConsts.ID)));
		list.setDate( new Date(result.getLong(result.getColumnIndex(DBConsts.LIST_DATE)) * 1000));
		Log.i(Consts.TAG, "Getting list date as " +   (result.getLong(result.getColumnIndex(DBConsts.LIST_DATE)) * 1000)); 
		list.setNotes(result.getString(result.getColumnIndex(DBConsts.LIST_NOTES))); 
		list.setUsername(result.getString(result.getColumnIndex(DBConsts.LIST_USER))); 
		list.setMarkedForDelete((result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_DELETE_MARKED))) == 1);
		list.setMarkedForUpload((result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_UPLOAD_REQUIRED))) == 1);
		list.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
		
		return list;
	}
	
	public BirdList getBirdListById(long listId) throws ISawABirdException {
		if(!db.isOpen()) db = getWritableDatabase();
		
		dumpTable(DBConsts.TABLE_LIST);
		Log.i("", "List ID is " + listId);
		Cursor result = db.rawQuery(DBConsts.QUERY_GET_LIST_BY_ID, new String [] { String.valueOf(listId) });
		if (result.getCount() == 0){
			return null; 
		}
		result.moveToNext();
		BirdList list = new BirdList(result.getString(result.getColumnIndex(DBConsts.LIST_NAME)));
		list.setId(result.getLong(result.getColumnIndex(DBConsts.ID)));
		list.setDate( new Date(result.getLong(result.getColumnIndex(DBConsts.LIST_DATE)) * 1000));
		//Log.i(Consts.TAG, "Getting list date as " +   (result.getLong(result.getColumnIndex(DBConsts.LIST_DATE)) * 1000)); 
		list.setNotes(result.getString(result.getColumnIndex(DBConsts.LIST_NOTES))); 
		list.setUsername(result.getString(result.getColumnIndex(DBConsts.LIST_USER))); 
		list.setMarkedForDelete((result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_DELETE_MARKED))) == 1);
		list.setMarkedForUpload((result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_UPLOAD_REQUIRED))) == 1);
		list.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
		
		return list;
	}
	
	
	public long getTotalSpeciesCount(){
		if(!db.isOpen()) db = getWritableDatabase();
		
		Cursor result = db.rawQuery(DBConsts.QUERY_TOTAL_SPECIES_COUNT, null);
		result.moveToNext();
		Log.i(Consts.TAG, "Returning count of " + result.getLong(0));
		return result.getLong(0); 
	}
	
	/* Get the lists for the current user */
	public ArrayList<BirdList> getBirdLists(String username){

		if(!db.isOpen()) db = getWritableDatabase();

		// TODO: use username
		Cursor result = db.rawQuery(
				DBConsts.QUERY_LIST, null);

		if(result.getColumnCount() <= 0) return null;

		ArrayList<BirdList> birdList = new ArrayList<BirdList>();

		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			Log.v(Consts.TAG, "Found list " + result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			temp.setDate(new Date(result.getInt(result.getColumnIndexOrThrow(DBConsts.LIST_DATE) ) * 1000));
			temp.setNotes(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NOTES)));
			temp.setUsername(username);
			temp.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
			temp.setId(result.getLong(result.getColumnIndexOrThrow(DBConsts.ID)));
			birdList.add(temp);
		}

		return birdList;
	}

	public ArrayList<BirdList> getBirdListToSync(String username) {

		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(DBConsts.QUERY_LIST_SYNC, null);

		if(result.getColumnCount() <= 0) return null;

		ArrayList<BirdList> birdList = new ArrayList<BirdList>();

		while(result.moveToNext()) {
			BirdList temp = new BirdList(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NAME)));
			temp.setDate(new Date(result.getLong(result.getColumnIndexOrThrow(DBConsts.LIST_DATE) ) * 1000));
			Log.i(Consts.TAG, "List :: Date read from DB is " + temp.getDate().toString());
			temp.setNotes(result.getString(result.getColumnIndexOrThrow(DBConsts.LIST_NOTES)));
			temp.setUsername(username);
			temp.setId(result.getInt(result.getColumnIndexOrThrow(DBConsts.ID)));
			temp.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
			temp.setMarkedForDelete(result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_DELETE_MARKED)) == 1);
			temp.setMarkedForUpload(result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_UPLOAD_REQUIRED)) == 1);
			birdList.add(temp);
		}
		
		Log.i(Consts.TAG, "We have " + birdList.size() + " lists to sync");
		return birdList;
	}

	public JSONArray getFeedbackToSync() {

		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(DBConsts.QUERY_FEEDBACK_SYNC, null);

		if(result.getColumnCount() <= 0) return null;
		Log.i(Consts.TAG, "We have " + result.getCount() + " feedbacks to sync"); 
		JSONArray feedbackList = new JSONArray();

		while(result.moveToNext()) {
			try{
				JSONObject feedbackObj = new JSONObject();
				feedbackObj.put("feedbackText", result.getString(result.getColumnIndex(DBConsts.FEEDBACK_TEXT))) ; 
				feedbackObj.put("feedbackId", result.getInt(result.getColumnIndex(DBConsts.ID)));
				feedbackList.put(feedbackObj);
			}catch(Exception ex){
				// TODO Handle exception 
				ex.printStackTrace();
			}
		}
		
		return feedbackList;
	}



public ArrayList<Sighting> getSightingsToSync(String username) {

		if(!db.isOpen()) db = getWritableDatabase();

		Cursor result = db.rawQuery(DBConsts.QUERY_SIGHTINGS_SYNC, null);

		if(result.getColumnCount() <= 0) return null;

		ArrayList<Sighting> sightings = new ArrayList<Sighting>();

		while(result.moveToNext()) {
			Sighting temp = new Sighting(result.getString(result.getColumnIndexOrThrow(DBConsts.SIGHTING_SPECIES)));
			temp.setDate(new Date(result.getLong(result.getColumnIndexOrThrow(DBConsts.SIGHTING_DATE))  * 1000 ));
			Log.i(Consts.TAG, "Sighting::Date read from DB is " + temp.getDate().toString());
			
			temp.setNotes(result.getString(result.getColumnIndexOrThrow(DBConsts.SIGHTING_NOTES)));
			temp.setId(result.getInt(result.getColumnIndexOrThrow(DBConsts.ID)));
			// TODO : Add list name instead of list ID 
			temp.setListId(result.getLong(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LIST_ID))); 
			temp.setLatitude(result.getDouble(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LATITUDE)));
			temp.setLongitude(result.getDouble(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LONGITUDE)));
			temp.setParseObjectID(result.getString(result.getColumnIndexOrThrow(DBConsts.PARSE_OBJECT_ID)));
			temp.setMarkedForDelete(result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_DELETE_MARKED)) == 1);
			temp.setMarkedForUpload(result.getInt(result.getColumnIndexOrThrow(DBConsts.PARSE_IS_UPLOAD_REQUIRED)) == 1);
			temp.setListParseObjectId(result.getString(result.getColumnIndexOrThrow(DBConsts.SIGHTING_LIST_PARSE_OBJECT_ID)));
			sightings.add(temp);
		}
		
		Log.i(Consts.TAG, "We have " + sightings.size() + " sightings to sync");
		return sightings;
	}
	
	public void deleteList(long listId) {
		
		if(!db.isOpen()) db = getWritableDatabase();

		/* Do not actually delete. Just mark isMarkedDelete = 1(true) */
		ContentValues values = new ContentValues();
		values.put(DBConsts.PARSE_IS_DELETE_MARKED, 1); 
		db.update(DBConsts.TABLE_SIGHTING, values, DBConsts.SIGHTING_LIST_ID + "=" + listId , null); 

		/* Next delete the list from the LIST table */ 
		db.update(DBConsts.TABLE_LIST, values, DBConsts.ID + "=" + listId, null);

		if (listId == Utils.getCurrentListID()){
			Utils.setCurrentList("", -1);
		}
	}


	public void deleteList(String listName){
		
		try{
			long listId = getListIDByName(listName);
			deleteList(listId);
		}catch(ISawABirdException ex){
			// TODO Handle properly
			ex.printStackTrace(); 
		}
	}
	
	public void deleteSighting(long sightingId) {
		
		if(!db.isOpen()) db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBConsts.PARSE_IS_DELETE_MARKED, 1);
		db.update(DBConsts.TABLE_SIGHTING, values, DBConsts.ID + "=?", 
				new String[] {Long.toString(sightingId)});
	}
	
	public void deleteSightingFromCurrentList(String species){
		if(!db.isOpen()) db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBConsts.PARSE_IS_DELETE_MARKED, 1);
		db.update(DBConsts.TABLE_SIGHTING, values, DBConsts.QUERY_DELETE_SIGHTING, 
				new String[] {species, String.valueOf(Utils.getCurrentListID()) });
	}
	
	public void deleteSightingFromList(String species, String listName ){
		if(!db.isOpen()) db = getWritableDatabase();
		
		try{
			long listId = getListIDByName(listName);
			
			ContentValues values = new ContentValues();
			values.put(DBConsts.PARSE_IS_DELETE_MARKED, 1);
			db.update(DBConsts.TABLE_SIGHTING, values, DBConsts.QUERY_DELETE_SIGHTING, 
					new String[] {species, String.valueOf(listId) });
			
		}catch(ISawABirdException ex){
			// TODO : Handle properly. No list by the name is found 
			ex.printStackTrace(); 
		}
	}
	
	public long getListIDByName(String listName) throws ISawABirdException{
		if(!db.isOpen()) db = getWritableDatabase();
		
		String query = DBConsts.LIST_NAME + "=\"" + listName + "\""; 
		Cursor result = db.query(DBConsts.TABLE_LIST, new String[] { DBConsts.ID} , query , null,null, null, null); 
		/* List name is unique */ 
		if (result.moveToNext()){
			Log.i(Consts.TAG, "ID of list " + listName + " is " + result.getLong(0)); 
			return result.getLong(0); // hard code because we query for only one column
		}else{
			throw new ISawABirdException("No list found in the database"); 
		}
	}
	
	public boolean updateParseObjectID(String tableName, long id, String parseObjectId){
		if(!db.isOpen()) db = getWritableDatabase();
		
		try{
			ContentValues values = new ContentValues(); 
			values.put(DBConsts.PARSE_OBJECT_ID, parseObjectId); 
			values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, 0);
			Log.i(Consts.TAG, " Updating Parse object id for " + tableName + " id = " + id);
			db.update(tableName, values, DBConsts.ID + "=" + id, null);
			return true; 
		}catch(Exception ex){
			//TODO : Handle exception
			ex.printStackTrace();
		}
		return false;
	}

	
	public boolean resetUploadRequiredFlag(String tableName, long id){ 
		if(!db.isOpen()) db = getWritableDatabase();
		
		try{
			ContentValues values = new ContentValues(); 
			values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, 0);
			
			db.update(tableName, values, DBConsts.ID + "=" + id, null);
			return true; 
		}catch(Exception ex){
			//TODO : Handle exception
			ex.printStackTrace();
		}
		return false;
	}
	
	public boolean setParseFlag(String tableName, long id, String flag, int flagValue) {
		
		if(!db.isOpen()) db = getWritableDatabase();

		try{
			ContentValues values = new ContentValues(); 
			values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, (flagValue == 0)?0:1);

			return (1 == db.update(tableName, values, DBConsts.ID + "=" + id, null));
		}catch(Exception ex){
			//TODO : Handle exception
			ex.printStackTrace();
		}
		return false;		
	}

	public void addFeedback(String feedback){
		if(!db.isOpen()) db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DBConsts.FEEDBACK_USER, ParseUtils.getCurrentUsername()); 
		values.put(DBConsts.FEEDBACK_TEXT, feedback);
		values.put(DBConsts.PARSE_IS_UPLOAD_REQUIRED, 1);
		db.insertOrThrow(DBConsts.TABLE_FEEDBACK, null, values);
		dumpTable(DBConsts.TABLE_FEEDBACK);
	}
	
	public boolean deleteLocally(String tableName, long id){ 
		if(!db.isOpen()) db = getWritableDatabase();
		
		try{
			db.delete(tableName, DBConsts.ID + "=" + id, null);
			return true; 
		}catch(Exception ex){
			//TODO : Handle exception
			ex.printStackTrace();
		}
		return false;
	}
	
	/*public ArrayList<BirdList> getBirdListToSync(boolean toCreate, String username) {
		if(!db.isOpen()) db = getWritableDatabase();

		String query = null;
		if(toCreate) {
			query = DBConsts.QUERY_LIST_SYNC_CREATE;
		} else {
			query = DBConsts.QUERY_LIST_SYNC_UPDATE;
		}

		Cursor result = db.rawQuery(query, null);

		if(result.getColumnCount() <= 0) return null;

		ArrayList<BirdList> birdList = new ArrayList<BirdList>();

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
	}*/
	
	public void dumpTable(String tableName){
		if(!db.isOpen()) db = getWritableDatabase();
		Cursor res = db.query(tableName, null, null, null, null, null, null);
		
		String dumpString = ""; 
		for (int i = 0 ; i < res.getColumnCount(); i++){
			dumpString += res.getColumnName(i) + " | " ;  
		}
		Log.i(Consts.TAG, "Dumping contents of table " + tableName);
		Log.i(Consts.TAG, dumpString); 

		while (res.moveToNext()){
			dumpString = "" ;
			for (int i = 0 ; i < res.getColumnCount(); i++){
				int type = res.getType(i); 
				switch (type){
				case Cursor.FIELD_TYPE_STRING:
					dumpString += res.getString(i) + " | ";
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					dumpString += res.getInt(i)+ " | ";
					break; 
				case Cursor.FIELD_TYPE_FLOAT:
					dumpString += res.getFloat(i) + " | ";
					break;
				default:
					break;
				}
			}
			Log.i(Consts.TAG, dumpString); 
		}
	}
	
	public void clearTable(String  tableName){
		if(!db.isOpen()) db = getWritableDatabase();

		db.delete(tableName, null, null);
	}
}
