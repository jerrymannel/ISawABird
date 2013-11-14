package com.isawabird.db;


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

	/**
	 * DB QUERIES
	 */

	public static final String QUERY_SIGHTINGS_BY_LISTNAME = 
			"SELECT " +  ID + ", " + SIGHTING_SPECIES + ", " + SIGHTING_NOTES +
			", " + SIGHTING_LATITUDE + ", " + SIGHTING_LONGITUDE + ", " +  SIGHTING_DATE +
			", " + PARSE_OBJECT_ID + ", " + PARSE_IS_DELETE_MARKED + ", " + PARSE_IS_UPLOAD_REQUIRED +
			" FROM " + TABLE_SIGHTING + 
			" WHERE " + SIGHTING_LIST_ID  + "= ? COLLATE NOCASE" +
			" ORDER BY " + SIGHTING_DATE + " DESC";

	public static final String QUERY_IS_SIGHTINGS_EXIST = 
			"SELECT " + SIGHTING_LIST_ID + ", " + SIGHTING_SPECIES +
			" FROM " + TABLE_SIGHTING  +  
			" WHERE " + SIGHTING_LIST_ID + "= ? " +
			" AND " + SIGHTING_SPECIES + " = ? COLLATE NOCASE" ;

	public static final String QUERY_LIST = 
			"SELECT " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED + 
			", " + PARSE_IS_UPLOAD_REQUIRED + ", " + PARSE_OBJECT_ID +
			" FROM " + TABLE_LIST + 
			" ORDER BY " + LIST_DATE + " DESC";

	public static final String QUERY_LIST_SYNC = 
			"SELECT " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED +
			" FROM " + TABLE_LIST + 
			" WHERE " + PARSE_IS_UPLOAD_REQUIRED + "=1" +
			" OR " + PARSE_IS_DELETE_MARKED + "=1";

	/* We use SQLiteDatabase.delete() which doesn't require the SELECT statement until WHERE */
	public static final String QUERY_DELETE_SIGHTING = 
			DBConsts.SIGHTING_SPECIES + "= ? AND " + 
					DBConsts.SIGHTING_LIST_ID + "=?";
}
