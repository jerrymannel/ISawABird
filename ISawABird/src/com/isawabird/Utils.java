package com.isawabird;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {

	private static ArrayList<Species> allSpecies = new ArrayList<Species>();
	private static boolean checklistLoaded = false;

	static Pattern pattern = Pattern.compile("[-\\s']*");
	public static SharedPreferences prefs = null;

	public static void initializeChecklist(Context context, String checklist) throws IOException {
		if (!checklist.equals(Utils.getChecklistName()) || allSpecies.size() == 0) {
			/* Load the checklist only if (a) the default checklist has changed (b) It has not been initialized */
			try {
				Log.i(Consts.TAG, "Initializing checklist for " + checklist);
				DataInputStream fis = new DataInputStream((InputStream) context.getAssets().open("checklists/" + checklist));
				String fileText = "";
				allSpecies = new ArrayList<Species>();
				ArrayList<String> speciesList = new ArrayList<String>(1000);
				while ((fileText = fis.readLine()) != null) {
					speciesList.add(fileText);
					allSpecies.add(new Species(fileText));
				}
				Log.d(Consts.TAG, allSpecies.size() + " species added to checklist");
				checklistLoaded = true;
				Utils.setChecklistName(checklist);
			} catch (IOException ioex) {
				throw ioex;
			}
		}
	}

	public static ArrayList<Species> getAllSpecies() {
		return allSpecies;
	}

	/**
	 * Function to search through the 'current' checklist for a given search term.
	 * 
	 * @param searchTerm
	 *            : The search query.
	 * @param subset
	 *            : The subset of the checklist to search within. If null, search is performed on the full checklist. This is useful to
	 *            search as the user types. For each key press, the search function can be called with the subset being the return value
	 *            from the previous call.
	 * 
	 * @returns A ArrayList of Species objects matching the search terms.
	 */
	public static ArrayList<Species> search(String searchTerm, ArrayList<Species> subset) {
		ArrayList<Species> returnVal = new ArrayList<Species>();
		if (!checklistLoaded) {
			return returnVal;
		}
		ArrayList<Species> searchList = (subset == null || subset.size() == 0) ? allSpecies : subset;

		Iterator<Species> iter = searchList.iterator();
		while (iter.hasNext()) {
			Species temp = iter.next();
			if (unpunctuate(temp.fullName).indexOf(unpunctuate(searchTerm)) != -1) {
				// Log.d(Consts.TAG, "Adding " + temp + " to search results.");
				returnVal.add(temp);
			}
		}
		return returnVal;
	}

	public static String unpunctuate(String string) {
		return pattern.matcher(string).replaceAll("").toLowerCase();
	}

	public static void setCurrentList(String listName, long listID) {
		prefs.edit().putString(Consts.CURRENT_LIST_KEY, listName).putLong(Consts.CURRENT_LIST_ID_KEY, listID).apply();
	}

	public static String setCurrentUsername(String username) {
		prefs.edit().putString(Consts.CURRENT_USER_ANONYMOUS, username).apply();
		return username;
	}

	public static boolean setFirstTime(boolean value) {
		prefs.edit().putBoolean(Consts.IS_FIRST_TIME, value).apply();
		return value;
	}

	public static String getCurrentListName() {
		return prefs.getString(Consts.CURRENT_LIST_KEY, "");
	}

	public static long getCurrentListID() {
		return prefs.getLong(Consts.CURRENT_LIST_ID_KEY, -1);
	}

	public static String getCurrentUsername() {
		return prefs.getString(Consts.CURRENT_USER_ANONYMOUS, null);
	}

	public static boolean isFirstTime() {
		return prefs.getBoolean(Consts.IS_FIRST_TIME, true);
	}

	public static String getChecklistName() {
		return prefs.getString(Consts.CHECKLIST, "India");
	}

	public static void setChecklistName(String checklistName) {
		prefs.edit().putString(Consts.CHECKLIST, checklistName).apply();
	}

	public static int getNumberOfRequestsThisMonth() {
		return prefs.getInt(Consts.NUMBER_REQUESTS_THIS_MONTH, 0);
	}

	public static void incrementNumberRequestsThisMonth() {
		prefs.edit().putInt(Consts.NUMBER_REQUESTS_THIS_MONTH, getNumberOfRequestsThisMonth() + 1).apply();
	}

	public static void resetNumberRequestsThisMonth() {
		prefs.edit().putInt(Consts.NUMBER_REQUESTS_THIS_MONTH, 0).apply();
	}

	public static long getLastSyncDate() {
		return prefs.getLong(Consts.LAST_SYNC_DATE, new Date().getTime());
	}

	public static void setLastSyncDate(long time) {
		prefs.edit().putLong(Consts.LAST_SYNC_DATE, time).apply();
	}

	public static void initializePrefs(Context context) {
		if (prefs == null) {
			prefs = context.getSharedPreferences(Consts.PREF, Context.MODE_PRIVATE);
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static Typeface getOpenSansLightTypeface(Activity activity) {
		return Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Light.ttf");
	}
}