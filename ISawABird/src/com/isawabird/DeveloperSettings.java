package com.isawabird;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DeveloperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	SharedPreferences prefs = null;
	Preference masterChecklist = null;
	Preference feedback = null;
	Preference about = null;
	Preference version = null;
	Toast prefToast = null;
	Handler handler = null;

	int counter = 1;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		masterChecklist = (Preference) findPreference("masterChecklist");
		feedback = (Preference) findPreference("feedback");
		about = (Preference) findPreference("about");
		version = (Preference) findPreference("version");

		feedback.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
				return false;
			}
		});

		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), AboutActivity.class));
				return false;
			}
		});

		version.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if (counter == 5) {
					counter = 1;
					showToast("Chirp! Chirp!");
				} else {
					showToast("Tap count :: " + counter);
					counter++;
				}
				return false;
			}
		});

		Log.i(Consts.TAG, "Master checklist saved is " + prefs.getString("masterChecklist", "India"));
		masterChecklist.setSummary(prefs.getString("masterChecklist", "India"));
		prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
		if (key.equals("masterChecklist")) {
			masterChecklist.setSummary(sharedPrefs.getString("masterChecklist", "India"));
		}
		try {
			InitChecklistAsyncTask asyncTask = new InitChecklistAsyncTask(getApplicationContext());
			asyncTask.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void showToast(String s){
		prefToast = Toast.makeText(DeveloperSettings.this, s, Toast.LENGTH_SHORT);
		prefToast.show();

		handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				prefToast.cancel();
			}
		}, 500);
	}

}
