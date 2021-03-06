package com.isawabird;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.AutoText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isawabird.db.DBHandler;
import com.isawabird.parse.ParseConsts;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private TextView tv_title;
	// private TextView tv_forgot;

	private Button mLoginButton;
	private TextView mSignupButton;
	private TextView mSkipButton;

	private TextView mShowSignupButton;
	private TextView mShowLoginButton;

	private EditText mUsernameText;
	private EditText mPassText;
	private EditText mPassConfirmText;

	Typeface openSansLight;
	Typeface openSansBold;
	Typeface openSansBoldItalic;
	Typeface sonsie;
	Typeface tangerine;

	ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// hide action bar before switching to login screen
		getActionBar().hide();

		openSansLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
		openSansBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
		openSansBoldItalic = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-BoldItalic.ttf");
		sonsie = Typeface.createFromAsset(getAssets(), "fonts/SonsieOne-Regular.ttf");
		tangerine = Typeface.createFromAsset(getAssets(), "fonts/Tangerine_Bold.ttf");

		tv_title = (TextView) findViewById(R.id.textView_title);
		// tv_forgot = (TextView) findViewById(R.id.btn_forgot_password);

		mLoginButton = (Button) findViewById(R.id.btn_login);
		mSignupButton = (TextView) findViewById(R.id.btn_signup);
		mSkipButton = (TextView) findViewById(R.id.btn_skip);

		mShowSignupButton = (TextView) findViewById(R.id.btn_showsignup);
		mShowLoginButton = (TextView) findViewById(R.id.btn_showlogin);

		mUsernameText = (EditText) findViewById(R.id.text_email);
		mPassText = (EditText) findViewById(R.id.text_pass);
		mPassConfirmText = (EditText) findViewById(R.id.text_confirm);

		tv_title.setTypeface(tangerine);
		// tv_forgot.setTypeface(openSansLight);

		mLoginButton.setTypeface(openSansLight);
		mSignupButton.setTypeface(openSansLight);
		mSkipButton.setTypeface(openSansLight);
		mShowSignupButton.setTypeface(openSansLight);
		mShowLoginButton.setTypeface(openSansLight);

		mUsernameText.setTypeface(openSansLight);
		mPassText.setTypeface(openSansLight);
		mPassConfirmText.setTypeface(openSansLight);

		mSkipButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showHome();
			}
		});
		if(mDialog == null) {
			mDialog = new ProgressDialog(this);
			mDialog.setMessage("Logging in...");
			mDialog.setCancelable(false);
		}

		mUsernameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if(hasFocus && mUsernameText.getText().toString().equalsIgnoreCase(getString(R.string.email))){
					mUsernameText.setText("");
				}
			}
		});
		mUsernameText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mUsernameText.getText().toString().equalsIgnoreCase(getString(R.string.email))) {
					mUsernameText.setText("");
				}
			}
		});

		mPassText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus && mPassText.getText().toString().equalsIgnoreCase(getString(R.string.password))) {
					mPassText.setText("");
				}
			}
		});

		mPassText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPassText.getText().toString().equalsIgnoreCase(getString(R.string.password))) {
					mPassText.setText("");
				}
			}
		});
	}

	long lastPress;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastPress > 5000) {
				Toast.makeText(getBaseContext(), "Press Back again to exit.", Toast.LENGTH_SHORT).show();
				lastPress = currentTime;
			} else {
				moveTaskToBack(true);
				return true;
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showLogin(View view) {
		mShowLoginButton.setVisibility(View.GONE);
		mSignupButton.setVisibility(View.GONE);
		mPassConfirmText.setVisibility(View.GONE);
		// tv_forgot.setVisibility(View.VISIBLE);
		mShowSignupButton.setVisibility(View.VISIBLE);
		mLoginButton.setVisibility(View.VISIBLE);
	}

	public void showSignup(View view) {
		mShowSignupButton.setVisibility(View.GONE);
		mLoginButton.setVisibility(View.GONE);
		// tv_forgot.setVisibility(View.GONE);
		mShowLoginButton.setVisibility(View.VISIBLE);
		mPassConfirmText.setVisibility(View.VISIBLE);
		mSignupButton.setVisibility(View.VISIBLE);
	}

	public void login(View view) {

		try {
			showActivityIndicator();

			String user = mUsernameText.getText().toString();
			String pass = mPassText.getText().toString();
			ParseUser.logInInBackground(user, pass, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					hideActivityIndicator();
					if (user == null) {
						Toast.makeText(getApplicationContext(), "Not able to login. Please try again later", Toast.LENGTH_SHORT).show();
					} else {
						saveInstallationID();

						String city = user.getString(Consts.BIRDRACE_CITY);
						/* Bird Race specific code */
						if (city != null) {
							Log.i(Consts.TAG, "Bird Race user ");
							/* Create a new list for BirdRace */
							try {
								DBHandler dh = DBHandler.getInstance(getApplicationContext());
								BirdList birdRaceList = new BirdList(city + " BirdRace " + user.getInt(Consts.BIRDRACE_YEAR));
								Log.i(Consts.TAG, "Current user is " + ParseUser.getCurrentUser().getUsername());
								birdRaceList.setUsername(user.getUsername());
								dh.addBirdList(birdRaceList, true);
							} catch (ISawABirdException ex) {
								/* DO nothing. The list already exists */
							}
							PushService.subscribe(getApplicationContext(), "BirdRace", MainActivity.class );
							PushService.subscribe(getApplicationContext(), city, MainActivity.class );
							PushService.subscribe(getApplicationContext(), "Y" + String.valueOf(user.getInt(Consts.BIRDRACE_YEAR)), MainActivity.class );
						}
						showHome();
					}
				}
			});
		} catch (Exception e) {
			hideActivityIndicator();
			Toast.makeText(getApplicationContext(), "Not able to login. Please try again later", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	private void showActivityIndicator() {
		if(mDialog.isShowing()) return;
		mDialog.show();
	}

	private void hideActivityIndicator() {
		if(!mDialog.isShowing()) return;
		mDialog.hide();
	}

	public void loginTwitter(View view) {

		showActivityIndicator();

		// network not available
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
			hideActivityIndicator();
			return;
		}

		ParseTwitterUtils.initialize(ParseConsts.TWITTER_CONSUMER_KEY, ParseConsts.TWITTER_CONSUMER_SECRET);
		ParseTwitterUtils.logIn(this, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException ex) {
				hideActivityIndicator();
				if (user == null) {
					String errMsg ; 
					if (ex == null){
						errMsg = "Sorry, but looks like you cancelled the Twitter login";
					}else {
						errMsg = "Unable to login using Twitter " + ex.getMessage(); 
					}
					Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
				} else {
					Utils.setCurrentUsername(user.getUsername());
					saveInstallationID();
					showHome();
				}
			}
		});
	}

	public void loginFacebook(View view) {
		showActivityIndicator();

		// network not available
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
			hideActivityIndicator();
			return;
		}

		ParseFacebookUtils.initialize(ParseConsts.FACEBOOK_APP_ID);
		ParseFacebookUtils.logIn(this, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException err) {
				hideActivityIndicator();
				if (user == null) {
					Toast.makeText(getApplicationContext(), "Unable to login to facebook : " + err.getMessage(), Toast.LENGTH_SHORT).show();
				} else {
					Utils.setCurrentUsername(user.getUsername());
					saveInstallationID();
					showHome();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		showHome();
	}

	public void loginGoogle(View view) {
		showActivityIndicator();

		// network not available
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
			hideActivityIndicator();
		}
	}

	public void signup(View view) {
		showActivityIndicator();
		// network not available
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
			hideActivityIndicator();
			return;
		}

		final String email = mUsernameText.getText().toString();
		final String pass = mPassText.getText().toString();
		final String passConfirm = mPassConfirmText.getText().toString();

		if (!pass.equals(passConfirm)) {
			Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
			return;
		}

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", email);
		// TODO: add busy indicator
		query.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> objects, ParseException e) {
				hideActivityIndicator();
				if (e == null) {
					if (objects != null && objects.size() > 0) {
						Toast.makeText(getApplicationContext(), "User with email '" + email + "' already exists", Toast.LENGTH_SHORT)
						.show();
						return;
					} else {
						ParseUser user = new ParseUser();
						user.setUsername(email);
						user.setPassword(pass);
						user.setEmail(email);

						user.signUpInBackground(new SignUpCallback() {
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if (e != null) {
									Toast.makeText(getApplicationContext(), "Not able to signup. Please try again later",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getApplicationContext(), "Successfully signed up", Toast.LENGTH_SHORT).show();
									ParseUser.logInInBackground(email, pass, new LogInCallback() {
										@Override
										public void done(ParseUser user, ParseException e) {
											if (user == null) {
												Toast.makeText(getApplicationContext(), "Not able to login. Please try again later",
														Toast.LENGTH_SHORT).show();
											} else {
												showHome();
											}
										}
									});
								}
							}
						});
					}
				} else {
					Toast.makeText(getApplicationContext(), "Not able to signup. Please try again later", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mDialog != null) {
			mDialog.dismiss();
		}
	}

	private void showHome() {
		
		Utils.setFirstTime(false);
		Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
		// FLAG_ACTIVITY_CLEAR_TOP is required if we are coming from settings by clicking on 'Login'
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

	private void saveInstallationID(){
		ParseUser currentUser =ParseUser.getCurrentUser();  
		if (currentUser.getString("InstallationID") != null){
			return ;
		}
		currentUser .add("InstallationID", ParseInstallation.getCurrentInstallation().getInstallationId()); 
		currentUser .saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException ex) {
				if (ex == null){
					Log.i(Consts.TAG, "User saved !");
				}else{
					ex.printStackTrace();
				}								
			}
		}) ;

	}
}
