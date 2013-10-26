package com.isawabird;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.SharedPreferences;
import android.os.storage.StorageManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseUtils {

	private static ParseObject currentList = null; 
	private static ParseUser currentUser = null;  
	private static List<ParseObject> lists = null; 
	static ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sightings");
	public static ParseGeoPoint location = null;
	
	
	public static void login(String username, String password) throws ParseException, ISawABirdException{
		try{
			if (username != null){
				currentUser = ParseUser.logIn(username, password);	
			}else{
				
				ParseAnonymousUtils.logIn(new LogInCallback() {
					
					@Override
					public void done(ParseUser user, ParseException ex) {
						if (ex == null){
							Log.d(Consts.LOG_TAG, user.getObjectId());
						}else{
							Log.e(Consts.LOG_TAG, ex.getMessage());
						}
					}
				});
			}
			
		}catch(Exception ex){
			//throw ex;
		}
	}

	public static ParseUser getCurrentUser(){
		currentUser = ParseUser.getCurrentUser();
		return currentUser;
	}
	
	/* Get the lists for the current user */
	public static List<BirdList> getLists() throws ParseException{
		/* Rewrite to query from SQLite DB */
		return null;
	}
	
	/* Create a new list for this user */
	public static ParseObject createList(String listName, String location, String notes){
		ParseObject obj = new ParseObject("Lists");
		obj.put("Username", currentUser.getUsername());
		obj.put("ListName"	,listName);
		obj.put("Location", location);
		obj.put("Date", new Date());
		obj.put("Notes", notes); 
		obj.saveEventually();
		return obj;
	}
	
	/* Add a sighting to a given list */
	public static void addSightingToList(final ParseObject list, String species)  { 
		final Sighting sighting = new Sighting(species); 
		sighting.put("Species", species);
		sighting.put("ListName"	, list.get("ListName"));
		sighting.put("Username", currentUser.getUsername());
		sighting.put("DateTime", new Date());
		if (location != null){
			sighting.put("Location", location);
		}
		sighting.saveEventually();
	}

	/* Get all sightings for a given list */
	public static List<ParseObject> getSightingsForList(ParseObject list) throws ParseException{
		List<ParseObject> returnVal = null;
		if (list == null){
			return null; 
		}
		
		
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.whereEqualTo("ListName", list.getString("ListName"));
		query.whereEqualTo("Username", currentUser.getUsername());
		try{
			returnVal = query.find(); 
		}catch(ParseException ex){
			ex.printStackTrace();
		}
		return returnVal;	
		
	}

}
