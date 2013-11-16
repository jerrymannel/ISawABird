package com.isawabird.test;

import java.util.Vector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.Sighting;
import com.isawabird.Species;
import com.isawabird.Utils;
import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;
import com.isawabird.parse.extra.SyncUtils;

public class DataLoader {

	private Context context;
	public DataLoader(Context context) {
		this.context = context;
	}

	public void load(String dbPath) {

		try {

			// load test data
			SQLiteDatabase checkDB = null;
			try {
				checkDB = SQLiteDatabase.openDatabase(dbPath, null,
						SQLiteDatabase.OPEN_READONLY);
			} catch (SQLiteException e) {
				// database doesn't exist yet.
			} finally {
				if(checkDB != null) checkDB.close();
			}
			boolean isFirstTime = checkDB == null ? true : false;

			if(isFirstTime) {
				// Use below class to create test data for the first time
				String username = ParseUtils.getCurrentUsername();
				if(username == null) {
					throw new Exception("Parse username cannot be null");
				}
				DBHandler dh = DBHandler.getInstance(context);

				BirdList blist = new BirdList("Hebbal Nov 2013");
				blist.setNotes("Bird watch at hebbal");
				blist.setUsername(username);
				long listId = dh.addBirdList(blist, true);

				Species sighting = new Species("Brown Shrike");
				dh.addSightingToCurrentList(sighting);

				sighting = new Species("Purple-rumped Sunbird");
				dh.addSightingToCurrentList(sighting);

				sighting = new Species("Common Coot");
				dh.addSightingToCurrentList(sighting);
			}
			Log.e(Consts.TAG, "Querying ...");
			query();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void query() {

		try {
			String username = ParseUtils.getCurrentUsername();
			if(username == null) {
				throw new Exception("Parse username cannot be null");
			}

			DBHandler dh = DBHandler.getInstance(context);
			Vector<BirdList> birdList = dh.getBirdLists(username);

			for (BirdList list : birdList) {
				Log.i(Consts.TAG, list.getId() + ":" +  list.toString());
			}

			if (birdList.size() > 0){
				Utils.setCurrentList(birdList.elementAt(0).getListName(), birdList.elementAt(0).getId());
			}

			for (BirdList list : birdList) {
				Vector<Sighting> sightings = dh.getSightingsByListName(list.getListName(), username);
				for (Sighting sighting : sightings) {
					Log.i(Consts.TAG, sighting.toString());
				}
			}

			Log.i(Consts.TAG, " Current list ID is " + Utils.getCurrentListID());
			Log.i(Consts.TAG, " Current list name is " + Utils.getCurrentListName());
			Log.i(Consts.TAG, " Number of birds in current list is " + dh.getBirdCountByListId(Utils.getCurrentListID()));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void srihariTestFunction(String dbPath){
		try {
			Log.e(Consts.TAG, "Querying inside Srihari test function..");
			
			// load test data
			SQLiteDatabase checkDB = null;
			try {
				checkDB = SQLiteDatabase.openDatabase(dbPath, null,
						SQLiteDatabase.OPEN_READONLY);
			} catch (SQLiteException e) {
				// database doesn't exist yet.
			} finally {
				if(checkDB != null) checkDB.close();
			}
			boolean isFirstTime = checkDB == null ? true : false;

			if(isFirstTime) {
				// Use below class to create test data for the first time
				String username = ParseUtils.getCurrentUsername();
				if(username == null) {
					throw new Exception("Parse username cannot be null");
				}
			}
			DBHandler dh = DBHandler.getInstance(context);

			dh.clearTable(DBConsts.TABLE_LIST);
			dh.clearTable(DBConsts.TABLE_SIGHTING);
			
			// Test sync of sightings 
			BirdList list = new BirdList("Bangalore BirdRace 2014"); 
			dh.addBirdList(list, true); 
			Log.i(Consts.TAG, "Adding a sighting");
			dh.addSightingToCurrentList(new Species("Spotted Owlet"));
			dh.dumpTable(DBConsts.TABLE_LIST) ; 
			dh.dumpTable(DBConsts.TABLE_SIGHTING) ;
			
			SyncUtils.triggerRefresh(); 
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
