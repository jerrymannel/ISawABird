package com.isawabird.parse;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
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
import com.parse.Parse;
import com.parse.ParseUser;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {

	private DBHandler dh;

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

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.w(Consts.TAG, "IN onPerformSync");
		try {
			if (isNetworkAvailable()) {

				Log.w(Consts.TAG, "SYNCING NOW");
				Parse.initialize(getContext(), ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
				// get bird list to sync create/update/delete
				Vector<BirdList> birdListToSync = dh.getBirdListToSync(ParseUtils.getCurrentUsername());

				ArrayList<Long> staleEntries = new ArrayList<Long>();
				JSONArray requestArray = new JSONArray();
				JSONObject body = null;
				for (BirdList birdList : birdListToSync) {

					if(birdList.isMarkedForDelete()) {
						// DELETE
						if(birdList.getParseObjectID() == null) {
							// exclude DELETE since object is not created at server yet
							staleEntries.add(birdList.getId());
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
							addCreateRequest(DBConsts.TABLE_LIST, body, requestArray);
						} else {
							// UPDATE
							addUpdateRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST, body, requestArray);
						}
					}
				}
				if(requestArray.length() > 0) {
					JSONObject batchRequest = buildRequest(requestArray);
					if(batchRequest != null) {
						// TODO post batchRequest to https://api.parse.com/1/batch
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

	public void postRequest(JSONObject batchRequest) {
		
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
		HttpResponse response;

		try {
			HttpPost post = new HttpPost(ParseConsts.BATCH_URL);
			
			StringEntity se = new StringEntity(batchRequest.toString());  
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
			response = client.execute(post);

			/*Checking response */
			if(response!=null){
				InputStream in = response.getEntity().getContent(); //Get the data in the entity
				Log.i(Consts.TAG, "Sync result: " + response.getStatusLine().getStatusCode());
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
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

	public void addCreateRequest(String objectName, JSONObject body, JSONArray requestArray) {
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