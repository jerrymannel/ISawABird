package com.isawabird.parse;


import java.util.Vector;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.isawabird.Consts;
import com.isawabird.BirdList;
import com.isawabird.MainActivity;
import com.isawabird.db.DBHandler;

public class ParseSyncAdapter extends AbstractThreadedSyncAdapter {

	private DBHandler dh;
	
	public ParseSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		dh = DBHandler.getInstance(context);
	}

	private boolean isNetworkAvailable(){
		NetworkInfo nwInfo = MainActivity.getConnectivityManager().getActiveNetworkInfo(); 
		Log.v(Consts.TAG, "Network availability is " + (nwInfo != null && nwInfo.isConnected()));
		return (nwInfo != null && nwInfo.isConnected());
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		if (isNetworkAvailable()){
			
			String username =ParseUtils.getCurrentUser().getUsername();
			// get bird list to create
			Vector<BirdList> birdListToCreate = dh.getBirdListToSync(true, username);
			// TODO: check if isMarkedDelete is true, if so, skip sync and delete row in db
			// TODO: update the parseObjectId after syncing with parse (initially it will be null)

			// get bird list to update
			Vector<BirdList> birdListToUpdate = dh.getBirdListToSync(false, username);

		}

	}
}