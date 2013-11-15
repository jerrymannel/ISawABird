package com.isawabird.parse;


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
import com.parse.Parse;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {


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
							postEntries.add(birdList.getId());
							addCreateRequest(DBConsts.TABLE_LIST, body);
						} else {
							// UPDATE
							postEntries.add(birdList.getId());
							addUpdateRequest(birdList.getParseObjectID(), DBConsts.TABLE_LIST, body, requestArray);
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
									dh.updateParseObjectID(DBConsts.TABLE_LIST, postEntries.get(i), objID);
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
				// delete staleEntries from db
				for (Long id : staleEntries) {
					dh.deleteLocally(DBConsts.TABLE_LIST, id);
				}
			}
		} catch (JSONException e) {
			Log.e(Consts.TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public JSONArray postRequest(JSONObject batchRequest) {

		try {
			if(batchRequest == null) return null;
			HttpClient client = new DefaultHttpClient();
			HttpPost postReq = new HttpPost(ParseConsts.BATCH_URL);
			postReq.addHeader("X-Parse-Application-Id", ParseConsts.APP_ID);
			postReq.addHeader("X-Parse-REST-API-Key", ParseConsts.CLIENT_KEY);
			postReq.addHeader("Content-Type", "application/json");
			Log.i(Consts.TAG, "Request to be sent : " + batchRequest.toString());
			StringEntity entity = new StringEntity(batchRequest.toString());
			postReq.setEntity(entity);

			HttpResponse resp = client.execute(postReq);
			HttpEntity respEntity = resp.getEntity();
			String response = EntityUtils.toString(respEntity);
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
