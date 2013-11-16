package com.isawabird;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Utils {

	private static ArrayList<String>  allSpecies = new ArrayList<String>(); 
	private static boolean checklistLoaded = false; 

	public static SharedPreferences prefs = null;

	public static void initializeChecklist(Context context, String checklist) throws IOException{
		try{
			DataInputStream fis = new DataInputStream((InputStream) context.getAssets().open("checklists/" + checklist));
			String fileText = "";
			Vector<String> speciesList = new Vector<String>(1000); 
			while ( (fileText = fis.readLine()) != null){
				speciesList.add(fileText);
			}
			
			for (String speciesName : speciesList){
				//Log.d("ISawABird", speciesList[i]);
				Species temp = new Species(speciesName);
				allSpecies.add(temp.getCommonName());
				//Log.d("ISawABird", temp.getUnPunctuatedName());
			}
			Log.d(Consts.TAG,  allSpecies.size() + " species added to checklist");
			checklistLoaded = true; 
		}catch(IOException ioex){
			throw ioex;
		}
	}

	public static ArrayList<String> getAllSpecies() {
		return allSpecies;
	}

	/** Function to search through the 'current' checklist for a given search term. 
	 * @param searchTerm : The search query. 
	 * @param subset : The subset of the checklist to search within. If null, search 
	 * is performed on the full checklist. This is useful to search as the user types.
	 * For each key press, the search function can be called with the subset being the 
	 * return value from the previous call. 
	 * 
	 * @returns A ArrayList of Species objects matching the search terms.
	 */
	public static ArrayList<String> search(String searchTerm, ArrayList<String> subset) {
		ArrayList<String> returnVal = new ArrayList<String> ();
		if (!checklistLoaded){
			return returnVal;
		}
		ArrayList<String> searchList = (subset == null || subset.size() == 0) ? allSpecies : subset ;

		Iterator<String> iter = searchList.iterator();
		while(iter.hasNext()){
			String temp = iter.next(); 
			if (unpunctuate(temp).indexOf(unpunctuate(searchTerm)) != -1) {
				Log.d(Consts.TAG, "Adding " + temp + " to search results.");
				returnVal.add(temp);
			}
		}
		return returnVal;
	}

	public static String unpunctuate(String string){
		return string.replaceAll("[-' ]", "").toLowerCase();
	}

	public static void setCurrentList(String listName, long listID){
		prefs.edit().putString(Consts.CURRENT_LIST_KEY, listName)
		.putLong(Consts.CURRENT_LIST_ID_KEY, listID).apply();
	}

	public static String setCurrentUsername(String username) {
		prefs.edit().putString(Consts.CURRENT_USER_ANONYMOUS, username).apply();
		return username;
	}

	public static boolean setFirstTime(boolean value) {
		prefs.edit().putBoolean(Consts.IS_FIRST_TIME, value).apply();
		return value;
	}

	public static String getCurrentListName(){
		return prefs.getString(Consts.CURRENT_LIST_KEY, "");
	}

	public static long getCurrentListID(){
		return prefs.getLong(Consts.CURRENT_LIST_ID_KEY, -1);
	}

	public static String getCurrentUsername() {
		return prefs.getString(Consts.CURRENT_USER_ANONYMOUS, null);
	}

	public static boolean isFirstTime() {
		return prefs.getBoolean(Consts.IS_FIRST_TIME, true);
	}
	public static String getChecklistName(){
		return prefs.getString(Consts.CHECKLIST, "India");
	}
	
	public static void setChecklistName(String checklistName){ 
		prefs.edit().putString(Consts.CHECKLIST, checklistName).apply();
	}
}