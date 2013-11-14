package com.isawabird.parse;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

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
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.isawabird.BirdList;
import com.isawabird.Consts;
import com.isawabird.db.DBConsts;
import com.isawabird.db.DBHandler;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {
	
	private static final String PARSE_BATCH_URL = "https://api.parse.com/1/batch";

	private DBHandler dh;
	private JSONArray requestArray = new JSONArray();
	
	
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

	public void doSync(){
		try {
			if (isNetworkAvailable()) {

				Log.w(Consts.TAG, "SYNCING NOW");

				String username = ParseUtils.getCurrentUser().getUsername();
				// get bird list to sync create/update/delete
				Vector<BirdList> birdListToSync = dh.getBirdListToSync(username);

				ArrayList<Long> staleEntries = new ArrayList<Long>();
				JSONObject body = null;
				for (BirdList birdList : birdListToSync) {

					if(birdList.isMarkedForDelete()) {
						// DELETE
						if(birdList.getParseObjectID() == null) {
							// exclude DELETE since object is not created at server yet
							staleEntries.add(birdList.getId());
							// TODO : Delete in the local DB 
						} else {
							// include DELETE
							addDeleteRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST, requestArray);
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
							addCreateRequest(DBConsts.TABLE_LIST, body);
						} else {
							// UPDATE
							addUpdateRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST, body, requestArray);
						}
					}
				}
				if(requestArray.length() > 0) {
					JSONObject batchRequest = buildRequest(requestArray);
					if(batchRequest != null) {
						try{
							HttpClient client = new DefaultHttpClient();
							HttpPost postReq = new HttpPost(PARSE_BATCH_URL);
							postReq.addHeader("X-Parse-Application-Id", "bIUifzSsg8NsFXkZiy47tXP5dzP9v7rQ8vQGQECK");
							postReq.addHeader("X-Parse-REST-API-Key", "ZTOXQtWbX3sCD9umliYbdymvNDPSvwLGa40LKWZR");
							postReq.addHeader("Content-Type", "application/json");
							Log.i(Consts.TAG, "Request to be sent : " + batchRequest.toString());
							StringEntity entity = new StringEntity(batchRequest.toString());
							postReq.setEntity(entity);
							
							HttpResponse resp = client.execute(postReq);
							HttpEntity respEntity = resp.getEntity();
							String response = EntityUtils.toString(respEntity);
							
							System.out.println("Response is " + response);
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
						// TODO after response update parseObjectId for POST requests
						// TODO after response delete invalid rows for DELETE requests
					}
				}

				// TODO: delete staleEntries from db

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.w(Consts.TAG, "IN onPerformSync");
		doSync();
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
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addCreateRequest: " + e.getMessage());
			return;
		}
		requestArray.put(createRequest);
	}

	public void addUpdateRequest(String objectId, String objectName, JSONObject body, JSONArray requestArray) {
		JSONObject updateRequest = new JSONObject();
		try {
			updateRequest.put("method", "PUT");
			updateRequest.put("path", "/1/classes/" + objectName + "/" + objectId);
			updateRequest.put("body", body);
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addUpdateRequest: " + e.getMessage());
			return;
		}
		requestArray.put(updateRequest);
	}

	public void addDeleteRequest(String objectId, String objectName, JSONArray requestArray) {
		JSONObject deleteRequest = new JSONObject();
		try {
			deleteRequest.put("method", "DELETE");
			deleteRequest.put("path", "/1/classes/" + objectName + "/" + objectId);
		} catch (JSONException e) {
			Log.e(Consts.TAG, "error in addDeleteRequest: " + e.getMessage());
			return;
		}
		requestArray.put(deleteRequest);
	}
}