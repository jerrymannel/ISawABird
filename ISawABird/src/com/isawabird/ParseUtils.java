package com.isawabird;

import java.util.List;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseUtils {

	private static ParseObject currentList = null; 
	
	/* Get the lists for the current user */
	public static List<ParseObject> getLists() throws ParseException{
		ParseUser currentUser = ParseUser.getCurrentUser(); 
		if (currentUser == null){
			/* Nobody is logged in*/
			return null; 
		}
		
		ParseRelation<ParseObject>list = currentUser.getRelation("Lists");
		List<ParseObject> lists = null;
		try{
			lists= list.getQuery().find(); 	
		}catch(ParseException pe){
			throw pe;
		}
		
		return lists; 
	}
	
	/* Get all sightings for a given list */
	public static List<ParseObject> getSightingsForList(ParseObject list) throws ParseException{
		List<ParseObject> returnVal = null;
		if (list == null){
			return null; 
		}
		
		ParseRelation<ParseObject> sightings = list.getRelation("Sightings"); 
		ParseQuery<ParseObject> query = sightings.getQuery(); 
		try{
			returnVal = query.find(); 
		}catch(ParseException ex){
			throw ex;
		}
		return returnVal;	
		
	}
	
	public static void login(String username, String password) throws ParseException, ISawABirdException{
		try{
			ParseUser.logIn(username, password);			
		}catch(ParseException ex){
			throw ex;
		}
	}
	
	public static void addSightingToList(final ParseObject list, String species)  { 
		final ParseObject sighting = new ParseObject("Sightings"); 
		sighting.put("Species", species);
			sighting.saveEventually(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				if (arg0 != null){
					Log.e(Consts.LOG_TAG, "Error saving " + arg0.getMessage());
				}
				ParseRelation<ParseObject> rel = list.getRelation("Sightings");
				rel.add(sighting);
				list.saveEventually();
			}
		});
	}
}
