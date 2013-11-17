package com.isawabird;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DeveloperSettings extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Button showLoginScreen = (Button) findViewById(R.id.button_dev_showloginscreen);
		showLoginScreen.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Utils.setFirstTime(true);
				Toast.makeText(getApplicationContext(), "Quit App and Restart.", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
