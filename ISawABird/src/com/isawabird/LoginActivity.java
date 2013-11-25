package com.isawabird;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private Button mLoginButton;
	private TextView tv_title;
	private TextView mSignupButton;
	private TextView mSkipButton;
	private TextView tv_forgot;
	private TextView tv_or;
	private EditText mUsernameText;
	private EditText mPassText;
	
	Typeface openSansLight;
	Typeface openSansBold;
	Typeface openSansBoldItalic;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
		openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
		openSansBoldItalic = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-BoldItalic.ttf");

		tv_title = (TextView)findViewById(R.id.textView_title);
		mLoginButton = (Button) findViewById(R.id.btn_login);
		mSignupButton = (TextView) findViewById(R.id.btn_signup);
		mSkipButton = (TextView) findViewById(R.id.btn_skip);
		tv_forgot = (TextView) findViewById(R.id.btn_forgot_password);
		tv_or = (TextView) findViewById(R.id.textView_or);
		mUsernameText = (EditText) findViewById(R.id.text_email);
		mPassText = (EditText) findViewById(R.id.text_pass);
		
		tv_title.setTypeface(openSansBold);
		mLoginButton.setTypeface(openSansBold);
		mUsernameText.setTypeface(openSansLight);
		mPassText.setTypeface(openSansLight);
		tv_forgot.setTypeface(openSansLight);
		tv_or.setTypeface(openSansBold);
		mSignupButton.setTypeface(openSansBold);
		mSkipButton.setTypeface(openSansBold);

		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					String user = mLoginButton.getText().toString();
					String pass = mPassText.getText().toString();
					ParseUser.logInInBackground(user, pass, new LogInCallback() {
						public void done(ParseUser user, ParseException e) {
							if(user == null) {
								Toast.makeText(getApplicationContext(), "Not able to login. Please try again later", Toast.LENGTH_SHORT).show();
							} else {
								showHome();
							}
						}
					});
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Not able to login. Please try again later", Toast.LENGTH_SHORT).show();
					e.printStackTrace();					
				}
			}
		});

		mSignupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// network not available
				if(!Utils.isNetworkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
					return;
				}
				
				final String username = mUsernameText.getText().toString();
				final String email = mUsernameText.getText().toString();
				final String pass = mPassText.getText().toString();
				
				ParseQuery<ParseUser> query = ParseUser.getQuery();
				query.whereEqualTo("username", username);
				// TODO: add busy indicator
				query.findInBackground(new FindCallback<ParseUser>() {
				  public void done(List<ParseUser> objects, ParseException e) {
				    if (e == null) {
				        if(objects != null && objects.size() > 0) {
				        	
				        }
				    } else {
				        // Something went wrong.
				    }
				  }
				});

				// TODO: show busy indicator
				ParseUser user = new ParseUser();
				user.setUsername(email);
				user.setPassword(pass);
				user.setEmail(email);

				user.signUpInBackground(new SignUpCallback() {
					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						if (e != null) {
							Toast.makeText(getApplicationContext(), "Not able to signup. Please try again later", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "Successfully signed up", Toast.LENGTH_SHORT).show();
							ParseUser.logInInBackground(email, pass, new LogInCallback() {
								@Override
								public void done(ParseUser user,
										ParseException e) {
									if(user == null) {
										Toast.makeText(getApplicationContext(), "Not able to login. Please try again later", Toast.LENGTH_SHORT).show();
									} else {
										showHome();
									}
								}
							});
						}
					}
				});
			}
		});

		mSkipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showHome();
			}
		});

		//mEmailText.setText("sriniketana");
		//mPassText.setText("test123");
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
