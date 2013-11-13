package com.isawabird.parse;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Criteria;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
import com.isawabird.MainActivity;
import com.isawabird.parse.extra.GenericAccountService;
import com.parse.LocationCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ParseUtils {

	private static ParseUser currentUser = null;  
	private static ParseGeoPoint location = new ParseGeoPoint(0, 0);
	
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
	
	public static ParseGeoPoint getLastKnownLocation(){
		return location;
	}
	
	public static void updateCurrentLocation(){
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		Log.i(Consts.TAG, "Fetching location...");
		ParseGeoPoint.getCurrentLocationInBackground(50000, new LocationCallback() {
			
			@Override
			public void done(ParseGeoPoint point, ParseException ex) {
				if (ex == null){
					location = point ;
					Log.i(Consts.TAG,"Location acquired " + point.getLatitude());
				}else{
					ex.printStackTrace();
				}
			}
		});
	}
}
