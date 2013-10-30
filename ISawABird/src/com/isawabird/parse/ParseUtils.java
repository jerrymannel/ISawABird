package com.isawabird.parse;

import java.util.List;

import android.util.Log;

import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

}