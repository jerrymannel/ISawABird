package com.isawabird.parse;

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

	private static ParseUser currentUser = null;  
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
							Log.d(Consts.TAG, user.getObjectId());
						}else{
							Log.e(Consts.TAG, ex.getMessage());
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
	
	public static String getCurrentUserName(){
		return getCurrentUser().getUsername();
	}
	
}
