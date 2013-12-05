package com.isawabird;

import com.isawabird.parse.ParseConsts;
import com.parse.Parse;

import android.app.Application;

public class ISawABirdApplication extends Application {

	@Override
	public void onCreate() {
		Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
		super.onCreate();
	}
}
