package com.isawabird;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.isawabird.MyLocation.LocationResult;
import com.isawabird.parse.ParseConsts;
import com.isawabird.parse.ParseUtils;
import com.parse.Parse;
import com.parse.ParseGeoPoint;

public class ISawABirdApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(Consts.TAG, " >> onCreateApplication ");
		Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
		
		
	}
}
