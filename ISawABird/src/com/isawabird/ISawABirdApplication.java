package com.isawabird;

import android.app.Application;

import com.isawabird.parse.ParseConsts;
import com.parse.Parse;

public class ISawABirdApplication extends Application {

	@Override
	public void onCreate() {
		Parse.initialize(this, ParseConsts.APP_ID, ParseConsts.CLIENT_KEY);
		super.onCreate();
	}
}
