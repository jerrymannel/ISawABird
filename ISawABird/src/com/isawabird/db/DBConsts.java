package com.isawabird.db;

import com.isawabird.Utils;

public class DBConsts {

	public static final String DATABASE_NAME = "BirdSightings";

	public static final int DATABASE_VERSION = 1;
	
	public static final int TRUE = 1;
	public static final int FALSE = 0;

	public static final String ID = "_id";

	public static final String TABLE_LIST = "list";
	public static final String LIST_NAME = "name";
	public static final String LIST_DATE = "ldate";
	public static final String LIST_USER = "user";
	public static final String LIST_NOTES = "lnotes";

	/* Fields used to sync with Parse */ 
	public static final String PARSE_OBJECT_ID = "objectId";
	public static final String PARSE_IS_UPLOAD_REQUIRED = "isUploadRequired";
	public static final String PARSE_IS_DELETE_MARKED = "isMarkedForDelete";
	

	public static final String CREATE_LIST = "CREATE TABLE " + TABLE_LIST +
			"(" +
			ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
			LIST_NAME 					+ " TEXT UNIQUE NOT NULL," +
			LIST_USER 					+ " TEXT," + 
			LIST_NOTES 					+ " TEXT," +
			LIST_DATE 					+ " INTEGER," +
			PARSE_OBJECT_ID    			+ " TEXT," + 
			PARSE_IS_UPLOAD_REQUIRED 	+ " INTEGER DEFAULT 0 NOT NULL," + 
			PARSE_IS_DELETE_MARKED		+ " INTEGER DEFAULT 0 NOT NULL" + 
			")";
	
	public static final String TABLE_FEEDBACK = "feedback"; 
	public static final String FEEDBACK_USER = "user";
	public static final String FEEDBACK_TEXT = "feedback";
	public static final String FEEDBACK_DATE = "date";
	
	
	public static final String CREATE_FEEDBACK = "CREATE TABLE " + TABLE_FEEDBACK +
			"(" +
			ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
			FEEDBACK_USER 				+ " TEXT," + 
			FEEDBACK_DATE				+ " INTEGER," +
			FEEDBACK_TEXT 				+ " TEXT NOT NULL," +
			PARSE_OBJECT_ID    			+ " TEXT," + 
			PARSE_IS_UPLOAD_REQUIRED 	+ " INTEGER DEFAULT 1 NOT NULL" +   
			")";

	public static final String TABLE_SIGHTING = "sighting";
	public static final String SIGHTING_SPECIES = "species";
	public static final String SIGHTING_LIST_ID = "listId";
	public static final String SIGHTING_NOTES = "snotes";
	public static final String SIGHTING_LATITUDE = "latitude";
	public static final String SIGHTING_LONGITUDE = "longitude";
	public static final String SIGHTING_DATE = "sdate";

	public static final String CREATE_SIGHTING = "CREATE TABLE " + TABLE_SIGHTING +
			"(" +
			ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
			SIGHTING_SPECIES 			+ " TEXT," +
			SIGHTING_LIST_ID 			+ " INTEGER NOT NULL," +
			SIGHTING_LATITUDE			+ " REAL," + 
			SIGHTING_LONGITUDE			+ " REAL," +
			SIGHTING_DATE				+ " INTEGER," + 
			SIGHTING_NOTES 				+ " TEXT," +
			PARSE_OBJECT_ID    			+ " TEXT," + 
			PARSE_IS_UPLOAD_REQUIRED 	+ " INTEGER DEFAULT 0 NOT NULL," + 
			PARSE_IS_DELETE_MARKED		+ " INTEGER DEFAULT 0 NOT NULL" +
			")";

	/* Separate Parse table not required. */
//	public static final String TABLE_PARSE = "parse";
//	public static final String PARSE_TYPE = "type";
//	public static final String PARSE_TYPE_ID = "typeId";
//	public static final String PARSE_TYPE_BIRDLIST = "BIRDLIST";
//	public static final String PARSE_TYPE_SIGHTING = "SIGHTING";
//	
//	
//	public static final String CREATE_PARSE = "CREATE TABLE " + TABLE_PARSE +
//			"(" +
//			ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
//			PARSE_TYPE 					+ " TEXT NOT NULL," +
//			PARSE_TYPE_ID 				+ " INTEGER NOT NULL," +
//			PARSE_OBJECT_ID				+ " TEXT," + 
//			PARSE_IS_UPLOAD_REQUIRED	+ " INTEGER DEFAULT 0 NOT NULL," +
//			PARSE_IS_DELETE_MARKED		+ " INTEGER DEFAULT 0 NOT NULL" + 
//			")";

	/**
	 * DB QUERIES
	 */

	public static final String QUERY_SIGHTINGS_BY_LISTNAME = 
			"SELECT s." +  ID + ", " + SIGHTING_SPECIES + ", " + SIGHTING_NOTES +
			", " + SIGHTING_LATITUDE + ", " + SIGHTING_LONGITUDE + ", " +  SIGHTING_DATE +
			", s." + PARSE_OBJECT_ID + ", s." + PARSE_IS_DELETE_MARKED + ", s." + PARSE_IS_UPLOAD_REQUIRED +
			", l." + LIST_NAME + 
			" FROM " + TABLE_SIGHTING + " as s LEFT OUTER JOIN " + TABLE_LIST + " as l" +
			" ON (s." + SIGHTING_LIST_ID + "= l." + ID + " ) " + 
			" WHERE s." + PARSE_IS_DELETE_MARKED + "!=1" +
			" AND l." + LIST_NAME + "= ? " +
			" ORDER BY " + SIGHTING_DATE + " DESC";
	
	public static final String QUERY_GET_LIST_BY_NAME = 
			"SELECT * FROM " + TABLE_LIST + " WHERE " + 
			LIST_NAME + " = ? AND " + LIST_USER + " = ? " ; 

	public static final String QUERY_GET_LIST_BY_ID = 
			"SELECT * FROM " + TABLE_LIST + " WHERE " + 
			ID + " = ? ";
	
	public static final String QUERY_IS_SIGHTINGS_EXIST = 
			"SELECT " + SIGHTING_LIST_ID + ", " + SIGHTING_SPECIES +
			" FROM " + TABLE_SIGHTING  +  
			" WHERE " + SIGHTING_LIST_ID + "= ? " +
			" AND " + SIGHTING_SPECIES + " = ? AND " + 
			PARSE_IS_DELETE_MARKED + "!= 1  COLLATE NOCASE" ;
	
	public static final String QUERY_LIST = 
			"SELECT " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED + 
			", " + PARSE_IS_UPLOAD_REQUIRED + ", " + PARSE_OBJECT_ID +
			" FROM " + TABLE_LIST + 
			" WHERE " + PARSE_IS_DELETE_MARKED + "!= 1" + 
			" ORDER BY " + LIST_DATE + " DESC";

	public static final String QUERY_LIST_SYNC = 
			"SELECT " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED +
			", " + PARSE_OBJECT_ID + 
			", " + PARSE_IS_UPLOAD_REQUIRED + 
			" FROM " + TABLE_LIST + 
			" WHERE " + PARSE_IS_UPLOAD_REQUIRED + "=1" +
			" OR " + PARSE_IS_DELETE_MARKED + "=1";
	
	public static final String QUERY_FEEDBACK_SYNC = 
			"SELECT " + ID + ", " + FEEDBACK_DATE + ", " + FEEDBACK_USER +
			", " + FEEDBACK_TEXT + 
			", " + PARSE_OBJECT_ID + 
			" FROM " + TABLE_FEEDBACK + 
			" WHERE " + PARSE_IS_UPLOAD_REQUIRED + "=1";
	
	
	public static final String QUERY_SIGHTINGS_SYNC = 
			"SELECT " + ID + ", " + SIGHTING_DATE + ", " + SIGHTING_SPECIES +
			", " + SIGHTING_LIST_ID + ", " + SIGHTING_LATITUDE +  
			", " + SIGHTING_LONGITUDE + ", " + SIGHTING_NOTES + 
			", " + PARSE_IS_DELETE_MARKED +
			", " + PARSE_OBJECT_ID + 
			", " + PARSE_IS_UPLOAD_REQUIRED + 
			" FROM " + TABLE_SIGHTING + 
			" WHERE " + PARSE_IS_UPLOAD_REQUIRED + "=1" +
			" OR " + PARSE_IS_DELETE_MARKED + "=1";
	// i think it is not a good idea to call static method in consts
	public static final String QUERY_COUNT_CURRENT_LIST = 
			"SELECT " + SIGHTING_SPECIES + " FROM " + TABLE_SIGHTING + " WHERE " + 
			SIGHTING_LIST_ID + " = " + Utils.getCurrentListID() + " AND " + 
			PARSE_IS_DELETE_MARKED + "!=1"; 
	
	/* We use SQLiteDatabase.delete() which doesn't require the SELECT statement until WHERE */
	public static final String QUERY_DELETE_SIGHTING = 
			DBConsts.SIGHTING_SPECIES + "= ? AND " + 
			DBConsts.SIGHTING_LIST_ID + "=?"; 
	
	// TODO: Include username 
	public static final String QUERY_TOTAL_SPECIES_COUNT = 
			"SELECT  DISTINCT COUNT(" + SIGHTING_SPECIES + ") FROM " + TABLE_SIGHTING +  
			" WHERE " + PARSE_IS_DELETE_MARKED + " != 1";
	
	/*public static final String QUERY_LIST_SYNC_CREATE = 
			"SELECT l." + ID + " as " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED +
			" FROM " + TABLE_LIST + " as l LEFT OUTER JOIN " + TABLE_PARSE + " as p" +
			" ON (p." + PARSE_TYPE + "='" + TABLE_LIST + "'" +
			" AND p." + PARSE_TYPE_ID + "=l." + ID + ")" +
			" WHERE " + PARSE_OBJECT_ID + " IS NULL" +
			" AND " + PARSE_IS_UPLOAD_REQUIRED + "=1";
	
	public static final String QUERY_LIST_SYNC_UPDATE = 
			"SELECT l." + ID + " as " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED +
			" FROM " + TABLE_LIST + " as l LEFT OUTER JOIN " + TABLE_PARSE + " as p" +
			" ON (p." + PARSE_TYPE + "='" + TABLE_LIST + "'" +
			" AND p." + PARSE_TYPE_ID + "=l." + ID + ")" +
			" WHERE " + PARSE_OBJECT_ID + " IS NOT NULL" +
			" AND " + PARSE_IS_UPLOAD_REQUIRED + "=1";*/
}
