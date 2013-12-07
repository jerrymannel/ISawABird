package com.isawabird;

import com.isawabird.parse.ParseUtils;
import com.parse.ParseGeoPoint;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSLocation implements LocationListener {

	private LocationManager locationManager = null; 
	private int MIN_TIME_BW_UPDATES = 2 * 60 * 60 * 1000; 
	private int MIN_DIST_BW_UPDATES = 100; //metres 
	
	private Location location = null; 
	
	@Override
	public void onLocationChanged(Location arg0) {
		this.location = arg0; 
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
	
	public void getLocation(Context context){
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		
		boolean isNetworkAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ; 
		boolean isGPSAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
		
		if (isNetworkAvailable){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DIST_BW_UPDATES, this); 
			if (locationManager != null){
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
				if (location!= null){
					Log.i(Consts.TAG, "Location obtained from network " + location.getLatitude() + "," + location.getLongitude()); 
					ParseUtils.location = new ParseGeoPoint(location.getLatitude(), location.getLongitude()); 
				}
			}
			return ; 
		}
		
		if (isGPSAvailable){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DIST_BW_UPDATES, this); 
			if (locationManager != null){
				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
				if (location!= null){
					Log.i(Consts.TAG, "Location obtained from GPS " + location.getLatitude() + "," + location.getLongitude()); 
					ParseUtils.location = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
				}
			}
		}
	}

}