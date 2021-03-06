package com.isawabird;

public abstract class Consts {

	public static final String TAG = "Lyre";

	// Shared Preferences
	public static final String PREF = "Lorikeet";
	public static final String CURRENT_LIST_KEY = "CurrentList";
	public static final String CURRENT_LIST_ID_KEY = "CurrentListID";
	public static final String CURRENT_USER_ANONYMOUS = "DummyUser";
	public static final String IS_FIRST_TIME = "IsFirstTime";
	public static final String CHECKLIST = "Checklist";
	public static final String BIRDRACE_CITY = "BirdRaceCity"; 
	public static final String BIRDRACE_YEAR = "BirdRaceYear";

	public static final String CSV_DELIMITER = ",";
	
	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.isawabird.parse";

	// An account type, in the form of a domain name
	public static final String ACCOUNT_TYPE = "2by0.com";

	// The account name
	public static final String ACCOUNT = "birdr";

	public static final CharSequence LOGIN = "Login";
	public static final CharSequence LOGOUT = "Logout";

	public static final String NUMBER_REQUESTS_THIS_MONTH = "NumberRequestThisMonth";

	public static final String LAST_SYNC_DATE = "LastSyncDate";

	public static final String SPECIES_NAME = "SpeciesName";

	public static final String OVERRIDE_THROTTLE = "OverrideThrottle";
	
	public static final int ONE_MINUTE = 60 * 1000; 

}
