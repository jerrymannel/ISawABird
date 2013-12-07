package com.isawabird;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DeveloperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	SharedPreferences prefs = null; 
	Preference masterChecklist = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); 
		
		masterChecklist = (Preference)findPreference("masterChecklist");
		Log.i(Consts.TAG, "Master checklist saved is " + prefs.getString("masterChecklist", "India"));
		masterChecklist.setSummary(prefs.getString("masterChecklist", "India")); 
		prefs.registerOnSharedPreferenceChangeListener(this); 
		
//		setContentView(R.layout.settings);
		
//		Button showLoginScreen = (Button) findViewById(R.id.button_dev_showloginscreen);
//		showLoginScreen.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Utils.setFirstTime(true);
//				Toast.makeText(getApplicationContext(), "Quit App and Restart.", Toast.LENGTH_SHORT).show();
//			}
//		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
		if (key.equals("masterChecklist")){
			masterChecklist.setSummary(sharedPrefs.getString("masterChecklist"	, "India"));
		}
		
	}

}
