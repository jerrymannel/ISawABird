package com.isawabird.parse;

import java.util.UUID;

import android.util.Log;

import com.isawabird.Consts;
import com.isawabird.ISawABirdException;
import com.isawabird.Utils;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class ParseUtils {

	private static ParseUser currentUser = null;  
	public  static ParseGeoPoint location = new ParseGeoPoint(0, 0);

	public static String getCurrentUsername(){
		currentUser = ParseUser.getCurrentUser();
		if(currentUser != null) {
			return currentUser.getUsername();
		}
		String username = Utils.getCurrentUsername();
		if(username == null) {
			username = Utils.setCurrentUsername(generateUsername());
		}
		
		return username;
	}
	
	private static String generateUsername() {
		return "b_i_r_d" + UUID.randomUUID().toString();
	}

	public static ParseGeoPoint getLastKnownLocation(){
		return location;
	}

//	public static void updateCurrentLocation(){
//		Criteria criteria = new Criteria();
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		Log.i(Consts.TAG, "Fetching location...");
//		ParseGeoPoint.getCurrentLocationInBackground(120000, new LocationCallback() {
//
//			@Override
//			public void done(ParseGeoPoint point, ParseException ex) {
//				if (ex == null){
//					location = point ;
//					Log.i(Consts.TAG,"Location acquired " + point.getLatitude());
//				}else{
//					ex.printStackTrace();
//				}
//			}
//		});
//	}

	public static boolean isLoggedIn() {
		return ParseUser.getCurrentUser() != null;
	}
}
