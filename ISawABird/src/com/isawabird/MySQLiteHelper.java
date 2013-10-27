package com.isawabird;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "BirdSightings.db";
	private static final int DB_VER = 1;
	
	public static final String BIRDLIST = "BIRDLIST"; 
	public static final String SIGHITNG = "SIGHTING";
	
	public static final String [] BIRDLIST_COLS = {"Date","ListName","Location","Notes","CreatedByUser", "ParseObjectID","isUploadRequired"};
	
	private static MySQLiteHelper myHelper = null; 
	
	/* SQL statements */ 
	private static final String BIRDLIST_TABLE = "CREATE TABLE BIRDLIST (" + 
	"Date integer, ListName text unique not null, Location text, Notes text,  CreatedByUser text not null, " + 
			"ParseObjectID text, isUploadRequired integer);" ; 
	
	private static final String SIGHTING_TABLE = "CREATE TABLE SIGHTING (" + 
			"Date integer, ListName text not null, Latitude real, Longitude real,  NumberOfBirds integer not null, " + 
			"Species text not null, Username text not null, " + 
					"ParseObjectID text, isUploadRequired integer);" ; 
	
	private MySQLiteHelper(Context context){
		super(context, DB_NAME, null, DB_VER);
	}
	
	public static MySQLiteHelper getSQLiteHelper(){
		if (myHelper == null){
			myHelper = new MySQLiteHelper(MainActivity.getContext());
		}
		return myHelper;
	}
	
	public static SQLiteDatabase getDB(){
		return getSQLiteHelper().getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BIRDLIST_TABLE);
		db.execSQL(SIGHTING_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
