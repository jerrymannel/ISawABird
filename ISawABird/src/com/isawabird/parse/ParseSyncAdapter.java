package com.isawabird.parse;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.MainActivity;
import com.isawabird.Sighting;
import com.isawabird.Utils;
import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;
import com.parse.Parse;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {


	private DBHandler dh;
	private JSONArray requestArray = new JSONArray();
	private static final int MAX_REQUESTS_PER_MONTH = 1000000;
	private static final int NUM_ACTIVE_USERS = 5000 ; 
	private static final float QUOTA_PER_MONTH = MAX_REQUESTS_PER_MONTH/NUM_ACTIVE_USERS; // 200
	private static final String KEY_REQUESTS_THIS_MONTH = "RequestsThisMonth";
	private static final String KEY_LAST_SYNC_DATE = "LastSyncDate"; 
	
	public ParseSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		dh = DBHandler.getInstance(context);
	}

	private boolean isNetworkAvailable(){
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/* We have 1,000,000 requests per month available for free from Parse. 
	 * Assuming 5000 active users a month, it leaves 200 /user/month = 6.666 / day.
	 * Use this method to throttle the number of requests we send to Parse.  
	 */
	private boolean areSyncCreditsAvailable(){
		Calendar lastSyncDate = Calendar.getInstance();
		/* We can't use Utils.prefs because sync happens independent of the app */ 
		Preferences prefs = Preferences.userRoot(); 
		//SharedPreferences prefs = MainActivity.getContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
		lastSyncDate.setTimeInMillis(prefs.getLong(KEY_LAST_SYNC_DATE, new Date().getTime()));
		
		if (lastSyncDate.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH) || 
			lastSyncDate.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)){
			Log.i(Consts.TAG, "Syncing for the first time this month "); 
			/* We are syncing for the first time this month. Reset the request count and return true */ 
			prefs.putInt(KEY_REQUESTS_THIS_MONTH, 0);
			return true; 
		}
		
		int date= Calendar.getInstance().get(Calendar.DATE); 
		int requestsSpentThisMonth = prefs.getInt(KEY_REQUESTS_THIS_MONTH, 0);
		float quotaPerDay = QUOTA_PER_MONTH / Calendar.getInstance().getActualMaximum(Calendar.DATE); 
		
		float availableRequests =  ( date * quotaPerDay) - requestsSpentThisMonth;
		Log.i(Consts.TAG, " We have " + availableRequests + " requests remaining this month"); 
		return (availableRequests > 0); 
	}
	
	
	private void syncBirdLists(){
		try{
			// get bird list to sync create/update/delete
			Vector<BirdList> birdListToSync = dh.getBirdListToSync(ParseUtils.getCurrentUsername());
	
			ArrayList<Long> staleEntries = new ArrayList<Long>();
			ArrayList<Long> postEntries = new  ArrayList<Long>();
			JSONObject body = null;
			for (BirdList birdList : birdListToSync) {
	
				if(birdList.isMarkedForDelete()) {
					// DELETE
					if(birdList.getParseObjectID() == null) {
						// exclude DELETE since object is not created at server yet
						staleEntries.add(birdList.getId());
					} else {
						// include DELETE
						postEntries.add(birdList.getId());
						addDeleteRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST);
					}
				} else {
					// if not delete, then it is marked for upload
					body = new JSONObject();
					body.put(DBConsts.LIST_NAME, birdList.getListName());
					body.put(DBConsts.LIST_USER, birdList.getUsername());
					body.put(DBConsts.LIST_NOTES, birdList.getNotes());
					body.put(DBConsts.LIST_DATE, birdList.getDate());
	
					if(birdList.getParseObjectID() == null) {
						// CREATE
						postEntries.add(birdList.getId());
						addCreateRequest(DBConsts.TABLE_LIST, body);
					} else {
						// UPDATE
						postEntries.add(birdList.getId());
						addUpdateRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST, body);
					}
				}
			}
	
			if(requestArray.length() > 0) {
				JSONObject batchRequest = buildRequest(requestArray);
				JSONArray respArray = postRequest(batchRequest);
				/* Parse the response */ 
				if(respArray != null) {
					for(int i = 0 ; i < respArray.length() ; i++){
						JSONObject object = respArray.getJSONObject(i);
						if (object.has(ParseConsts.SUCCESS)){
							String method = requestArray.getJSONObject(i).getString("method");
							// update parseObjectId for POST requests
							if (method == "POST") {
								// We added a new entry to Parse  
								String objID = object.getJSONObject(ParseConsts.SUCCESS).getString(ParseConsts.OBJECTID);
								String table = requestArray.getJSONObject(i).getString("className");
								dh.updateParseObjectID(table, postEntries.get(i), objID);
								dh.dumpTable(DBConsts.TABLE_LIST);
							}else if (method == "PUT") {
								// We Updated Parse. So, just reset the upload required flag.
								dh.resetUploadRequiredFlag(DBConsts.TABLE_LIST, postEntries.get(i)); 
								dh.dumpTable(DBConsts.TABLE_LIST); 
							}else if (method == "DELETE") {
								// delete invalid rows for DELETE requests
								dh.deleteLocally(DBConsts.TABLE_LIST, postEntries.get(i));
								dh.dumpTable(DBConsts.TABLE_LIST); 
							}
	
						}else{
							// TODO : Handle failure
						}
					}
				}
			}
			requestArray = new JSONArray();
			// delete staleEntries from db
			for (Long id : staleEntries) {
				dh.deleteLocally(DBConsts.TABLE_LIST, id);
			}
		}catch(JSONException ex){
			Log.e(Consts.TAG, ex.getMessage());
			ex.printStackTrace();
		}
	}

	
	private void syncSightings(){
		try{
			// get bird list to sync create/update/delete
			Vector<Sighting> sightingsToSync = dh.getSightingsToSync(ParseUtils.getCurrentUsername());
	
			ArrayList<Long> staleEntries = new ArrayList<Long>();
			ArrayList<Long> postEntries = new  ArrayList<Long>();
			JSONObject body = null;
			for (Sighting  sighting : sightingsToSync) {
				if(sighting.isMarkedForDelete()) {
					// DELETE
					if(sighting.getParseObjectID() == null) {
						// exclude DELETE since object is not created at server yet
						staleEntries.add(sighting.getId());
					} else {
						// include DELETE
						postEntries.add(sighting.getId());
						addDeleteRequest(sighting.getParseObjectID(), DBConsts.TABLE_SIGHTING);
					}
				} else {
					// if not delete, then it is marked for upload
					body = new JSONObject();
					body.put(DBConsts.SIGHTING_SPECIES, sighting.getSpecies().getFullName());
					body.put(DBConsts.SIGHTING_NOTES, sighting.getNotes());
					body.put(DBConsts.SIGHTING_DATE, sighting.getDate().getTime());
					body.put(DBConsts.SIGHTING_LATITUDE, sighting.getLatitude());
					body.put(DBConsts.SIGHTING_LONGITUDE, sighting.getLongitude());
					// TODO: Add list name instead of list id
					body.put(DBConsts.SIGHTING_LIST_ID, sighting.getListId());
	
					if(sighting.getParseObjectID() == null) {
						// CREATE
						postEntries.add(sighting.getId());
						addCreateRequest(DBConsts.TABLE_SIGHTING, body);
					} else {
						// UPDATE
						postEntries.add(sighting.getId());
						addUpdateRequest(sighting.getParseObjectID(), DBConsts.TABLE_SIGHTING, body);
					}
				}
			}
			
			// TODO : Make a single batch call to sync both lists and sightings  
			if(requestArray.length() > 0) {
				JSONObject batchRequest = buildRequest(requestArray);
				JSONArray respArray = postRequest(batchRequest);
				/* Parse the response */ 
				if(respArray != null) {
					for(int i = 0 ; i < respArray.length() ; i++){
						JSONObject object = respArray.getJSONObject(i);
						if (object.has(ParseConsts.SUCCESS)){
							String method = requestArray.getJSONObject(i).getString("method");
							// update parseObjectId for POST requests
							if (method == "POST") {
								// We added a new entry to Parse  
								String objID = object.getJSONObject(ParseConsts.SUCCESS).getString(ParseConsts.OBJECTID);
								String table = requestArray.getJSONObject(i).getString("className");
								dh.updateParseObjectID(table, postEntries.get(i), objID);
								dh.dumpTable(DBConsts.TABLE_SIGHTING);
							}else if (method == "PUT") {
								// We Updated Parse. So, just reset the upload required flag.
								dh.resetUploadRequiredFlag(DBConsts.TABLE_SIGHTING, postEntries.get(i)); 
								dh.dumpTable(DBConsts.TABLE_SIGHTING); 
							}else if (method == "DELETE") {
								// delete invalid rows for DELETE requests
								dh.deleteLocally(DBConsts.TABLE_SIGHTING, postEntries.get(i));
								dh.dumpTable(DBConsts.TABLE_SIGHTING); 
							}
	
						}else{
							// TODO : Handle failure
						}
					}
				}
			}
			requestArray = new JSONArray();
	
			// delete staleEntries from db
			for (Long id : staleEntries) {
				dh.deleteLocally(DBConsts.TABLE_SIGHTING, id);
			}
		}catch(JSONException ex){
			Log.e(Consts.TAG, ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.w(Consts.TAG, "IN onPerformSync");

		try {
			if (isNetworkAvailable() && areSyncCreditsAvailable()) {
				Log.w(Consts.TAG, "SYNCING NOW");
				Parse.initialize(getContext(), ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
				syncBirdLists(); 
				syncSightings(); 
			}
		} catch (Exception e) {
			Log.e(Consts.TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public JSONArray postRequest(JSONObject batchRequest) {

		try {
			if(batchRequest == null) return null;
			HttpClient client = new DefaultHttpClient();
			HttpPost postReq = new HttpPost(ParseConsts.BATCH_URL);
			//postReq.addHeader("X-Parse-Application-Id", ParseConsts.APP_ID);
			postReq.addHeader("X-Parse-Application-Id", "bIUifzSsg8NsFXkZiy47tXP5dzP9v7rQ8vQGQECK"); // Srihari
			//postReq.addHeader("X-Parse-REST-API-Key", ParseConsts.CLIENT_KEY);
			postReq.addHeader("X-Parse-REST-API-Key", "ZTOXQtWbX3sCD9umliYbdymvNDPSvwLGa40LKWZR"); //Srihari
			postReq.addHeader("Content-Type", "application/json");
			Log.i(Consts.TAG, "Request to be sent : " + batchRequest.toString());
			StringEntity entity = new StringEntity(batchRequest.toString());
			postReq.setEntity(entity);

			HttpResponse resp = client.execute(postReq);
			HttpEntity respEntity = resp.getEntity();
			String response = EntityUtils.toString(respEntity);
			Preferences prefs = Preferences.userRoot() ; 
			prefs.putInt(KEY_REQUESTS_THIS_MONTH, prefs.getInt(KEY_REQUESTS_THIS_MONTH, 0) + 1);
			prefs.putLong(KEY_LAST_SYNC_DATE, new Date().getTime());
			Log.i(Consts.TAG, "Number of requests so far this month " + prefs.getInt(KEY_REQUESTS_THIS_MONTH, 0)); 
			return new JSONArray(response);
		} catch (Exception e) {
			Log.e(Consts.TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject buildRequest(JSONArray requestArray) {
		try {
			JSONObject request = new JSONObject();
			request.put("requests", requestArray);
			return request;
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in buildRequest: " + e.getMessage());
		}
		return null;
	}

	public void addCreateRequest(String objectName, JSONObject body) {
		Log.i(Consts.TAG, " >> addCreateRequest  for " + body.toString());
		JSONObject createRequest = new JSONObject();
		try {
			createRequest.put("method", "POST");
			createRequest.put("path", "/1/classes/" + objectName);
			createRequest.put("body", body);
			createRequest.put("className", objectName);
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addCreateRequest: " + e.getMessage());
			return;
		}
		requestArray.put(createRequest);
	}

	public void addUpdateRequest(String objectId, String objectName, JSONObject body) {
		JSONObject updateRequest = new JSONObject();
		try {
			updateRequest.put("method", "PUT");
			updateRequest.put("path", "/1/classes/" + objectName + "/" + objectId);
			updateRequest.put("body", body);
			updateRequest.put("className", objectName);
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addUpdateRequest: " + e.getMessage());
			return;
		}
		requestArray.put(updateRequest);
	}

	public void addDeleteRequest(String objectId, String objectName) {
		JSONObject deleteRequest = new JSONObject();
		try {
			deleteRequest.put("method", "DELETE");
			deleteRequest.put("path", "/1/classes/" + objectName + "/" + objectId);
			deleteRequest.put("className", objectName);
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addDeleteRequest: " + e.getMessage());
			return;
		}
		requestArray.put(deleteRequest);
	}
}
