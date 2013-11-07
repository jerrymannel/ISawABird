package com.isawabird.parse;

import android.content.Context;
import android.location.Criteria;

import com.isawabird.MainActivity;
import com.parse.LocationCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

public class ParseInit {
	
	private static boolean isInitialized = false ;

	public static void init(Context context){
		if (!isInitialized){ 
			Parse.initialize(context, 
				 "bIUifzSsg8NsFXkZiy47tXP5dzP9v7rQ8vQGQECK", "KRw1j22gGpmfSoRC2UE7YnevufQCV4oz1mny5Eum");
		}
		ParseUtils.getCurrentUser();
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		ParseGeoPoint.getCurrentLocationInBackground(50000, new LocationCallback() {
			
			@Override
			public void done(ParseGeoPoint point, ParseException ex) {
				if (ex == null){
					ParseUtils.location = point ;
					MainActivity.updateLabel("Location acquired " + point.getLatitude());
				}else{
					ex.printStackTrace();
				}
				
			}
		});
		isInitialized = true; 
	}
}