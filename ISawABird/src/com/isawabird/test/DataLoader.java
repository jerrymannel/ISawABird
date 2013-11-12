package com.isawabird.test;

import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.Sighting;
import com.isawabird.Utils;
import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseUtils;

public class DataLoader {

	private Context context;
	public DataLoader(Context context) {
		this.context = context;
	}
	public void load() {

		try {

			String username = ParseUtils.getCurrentUser().getUsername();
			if(username == null) {
				throw new Exception("Parse username cannot be null");
			}
			DBHandler dh = DBHandler.getInstance(context);

			BirdList blist = new BirdList("Hesaraghatta Nov 2013");
			blist.setNotes("Bird watch at hebbal");
			blist.setUsername(username);
			long listId = dh.addBirdList(blist);

			Sighting sighting = new Sighting("Common Crow");
			dh.addSighting(sighting, listId, username);

			sighting = new Sighting("Black-breasted Sunbird");
			dh.addSighting(sighting, listId, username);

			sighting = new Sighting("Eurasian Eagle-owl");
			dh.addSighting(sighting, listId, username);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void query() {

		try {
			String username = ParseUtils.getCurrentUser().getUsername();
			if(username == null) {
				throw new Exception("Parse username cannot be null");
			}

			DBHandler dh = DBHandler.getInstance(context);
			Vector<BirdList> birdList = dh.getBirdLists(username);

			for (BirdList list : birdList) {
				Log.i(Consts.TAG, list.getId() + ":" +  list.toString());
			}
			
			Utils.setCurrentList(birdList.elementAt(0).getListName(), birdList.elementAt(0).getId());
			
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
