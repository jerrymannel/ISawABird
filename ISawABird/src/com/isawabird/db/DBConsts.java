package com.isawabird.db;

import com.isawabird.Utils;
import com.isawabird.parse.ParseUtils;

public class DBConsts {

	public static final String DATABASE_NAME = "BirdSightings";

	public static final int DATABASE_VERSION = 1;

	public static final String ID = "_id";

	public static final String TABLE_LIST = "list";
	public static final String LIST_NAME = "name";
	public static final String LIST_DATE = "ldate";
	public static final String LIST_USER = "user";
	public static final String LIST_NOTES = "lnotes";

	public static final String CREATE_LIST = "CREATE TABLE " + TABLE_LIST +
			"(" +
			ID 					+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
			LIST_NAME 			+ " TEXT UNIQUE NOT NULL," +
			LIST_USER 			+ " TEXT," + 
			LIST_NOTES 			+ " TEXT," +
			LIST_DATE 			+ " INTEGER" +
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
			SIGHTING_NOTES 				+ " TEXT" +
			")";
	
	public static final String TABLE_PARSE = "parse";
	public static final String PARSE_TYPE = "type";
	public static final String PARSE_TYPE_ID = "typeId";
	public static final String PARSE_OBJECT_ID = "objectId";
	public static final String PARSE_IS_UPLOAD_REQUIRED = "isUploadRequired";
	public static final String PARSE_IS_DELETE_MARKED = "isMarkedForDelete";
	public static final String PARSE_TYPE_BIRDLIST = "BIRDLIST";
	public static final String PARSE_TYPE_SIGHTING = "SIGHTING";
	
	
	public static final String CREATE_PARSE = "CREATE TABLE " + TABLE_PARSE +
			"(" +
			ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
			PARSE_TYPE 					+ " TEXT NOT NULL," +
			PARSE_TYPE_ID 				+ " INTEGER NOT NULL," +
			PARSE_OBJECT_ID				+ " TEXT," + 
			PARSE_IS_UPLOAD_REQUIRED	+ " INTEGER DEFAULT 0 NOT NULL," +
			PARSE_IS_DELETE_MARKED		+ " INTEGER DEFAULT 0 NOT NULL" + 
			")";

	/**
	 * DB QUERIES
	 */
	
	public static final String QUERY_SIGHTINGS_BY_LISTNAME = 
			"SELECT s." + ID + " as " + ID + ", " + SIGHTING_SPECIES + ", " + SIGHTING_NOTES +
			", " + SIGHTING_LATITUDE + ", " + SIGHTING_LONGITUDE + ", " +  SIGHTING_DATE +
			", " + LIST_DATE + ", " + LIST_NAME + ", " + LIST_NOTES + ", " + LIST_USER +
			", " + PARSE_IS_DELETE_MARKED + ", " + PARSE_IS_UPLOAD_REQUIRED +
			", " + PARSE_OBJECT_ID + ", " + PARSE_TYPE + ", " + PARSE_TYPE_ID +
			" FROM " + TABLE_SIGHTING + " as s LEFT OUTER JOIN " + TABLE_LIST + " as l" +
			" LEFT OUTER JOIN " + TABLE_PARSE + " as p" +
			" ON (s." + SIGHTING_LIST_ID + "=l." + ID + " AND s." + ID + "=p." + PARSE_TYPE_ID + 
			" AND p." + PARSE_TYPE + "='" + TABLE_SIGHTING + "' ) " +
			" WHERE " + LIST_NAME + "= ? COLLATE NOCASE" +
			" ORDER BY " + SIGHTING_DATE + " DESC";

	public static final String QUERY_IS_SIGHTINGS_EXIST = 
			"SELECT EXISTS(SELECT " + SIGHTING_LIST_ID + ", " + SIGHTING_SPECIES +
			", l." + ID + ", " + LIST_NAME + ", " + LIST_USER + 
			" FROM " + TABLE_SIGHTING + " as s LEFT OUTER JOIN " + TABLE_LIST + " as l" +
			" ON s." + SIGHTING_LIST_ID + "=l." + ID + 
			" WHERE l." + ID + "= ? " +
			" AND " + SIGHTING_SPECIES + "=? COLLATE NOCASE" +
			" AND " + LIST_NAME + "=? COLLATE NOCASE" +
			" LIMIT 1)";
	
	public static final String QUERY_LIST = 
			"SELECT l." + ID + " as " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED + 
			", " + PARSE_IS_UPLOAD_REQUIRED + ", " + PARSE_OBJECT_ID +
			", " + PARSE_TYPE + ", " + PARSE_TYPE_ID +
			" FROM " + TABLE_LIST + " as l LEFT OUTER JOIN " + TABLE_PARSE + " as p" +
			" ON (p." + PARSE_TYPE + "='" + TABLE_LIST + "'" +
			" AND p." + PARSE_TYPE_ID + "=l." + ID + ")" +
			" ORDER BY " + LIST_DATE + " DESC";

	public static final String QUERY_LIST_SYNC = 
			"SELECT l." + ID + " as " + ID + ", " + LIST_DATE + ", " + LIST_NAME +
			", " + LIST_NOTES + ", " + LIST_USER + ", " + PARSE_IS_DELETE_MARKED +
			" FROM " + TABLE_LIST + " as l LEFT OUTER JOIN " + TABLE_PARSE + " as p" +
			" ON (p." + PARSE_TYPE + "='" + TABLE_LIST + "'" +
			" AND p." + PARSE_TYPE_ID + "=l." + ID + ")" +
			" WHERE " + PARSE_IS_UPLOAD_REQUIRED + "=1" +
			" OR " + PARSE_IS_DELETE_MARKED + "=1";
	
	public static final String QUERY_COUNT_CURRENT_LIST = 
			"SELECT " + SIGHTING_SPECIES + " FROM " + TABLE_SIGHTING + " WHERE " + SIGHTING_LIST_ID + " = " + Utils.getCurrentListID() ; 
	
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
