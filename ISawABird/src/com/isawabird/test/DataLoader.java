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
}
