package com.isawabird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isawabird.parse.ParseUtils;

public class LoginActivity extends Activity {

	private Button mLoginButton;
	private TextView mSignupButton;
	private TextView mSkipButton;
	private EditText mEmailText;
	private EditText mPassText;
	private EditText mPassConfirmText;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		getActionBar().hide();

		mLoginButton = (Button) findViewById(R.id.btn_login);
		mSignupButton = (TextView) findViewById(R.id.btn_signup);
		mSkipButton = (TextView) findViewById(R.id.btn_skip);
		mEmailText = (EditText) findViewById(R.id.text_email);
		mPassText = (EditText) findViewById(R.id.text_pass);
		mPassConfirmText = (EditText) findViewById(R.id.text_confirm);

		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(Consts.TAG, "Logging in...");
				try {
					String user = mLoginButton.getText().toString();
					String pass = mPassText.getText().toString();
					// TODO : (jerry) app crashing here. Fix it!
					ParseUtils.login(user, pass);
				} catch (Exception e) {
					// TODO handle login exception
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();					
				}
				Log.i(Consts.TAG, "Logged in");
				showHome();
			}
		});

		mSignupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: implement
				mLoginButton.setVisibility(View.INVISIBLE);
				mPassConfirmText.setVisibility(View.VISIBLE);
				Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
			}
		});

		mSkipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showHome();
			}
		});
		
		mEmailText.setText("sriniketana");
        mPassText.setText("test123");
	}
	
	private void showHome() {
		Log.i(Consts.TAG, "TO HOME");
		Utils.setFirstTime(false);
		Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
		// FLAG_ACTIVITY_CLEAR_TOP is required if we are coming from settings by clicking on 'Login'
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(homeIntent);
		finish();
	}
}
