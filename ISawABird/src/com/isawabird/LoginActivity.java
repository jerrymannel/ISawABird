package com.isawabird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button mLoginButton;
	private Button mSignupButton;
	private Button mSkipButton;
	private EditText mEmailText;
	private EditText mPassText;
	private EditText mPassConfirmText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getActionBar().hide();
		
		mLoginButton = (Button) findViewById(R.id.btn_login);
		mSignupButton = (Button) findViewById(R.id.btn_signup);
		mSkipButton = (Button) findViewById(R.id.btn_skip);
		mEmailText = (EditText) findViewById(R.id.text_email);
		mPassText = (EditText) findViewById(R.id.text_pass);
		mPassConfirmText = (EditText) findViewById(R.id.text_confirm);
	
		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: implement
				Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
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
				Utils.setFirstTime(false);
				Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
				// FLAG_ACTIVITY_CLEAR_TOP is required if we are coming from settings by clicking on 'Login'
				loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				startActivity(loginIntent);
				finish();
			}
		});
	}
}
