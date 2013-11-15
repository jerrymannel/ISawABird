package com.isawabird;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Utils {

	private static Vector<Species>  allSpecies = new Vector<Species>(); 
	private static boolean checklistLoaded = false; 
	
	public static SharedPreferences prefs = null;
	
	public static void initializeChecklist(Context context, String checklist) throws IOException{
		try{
			allSpecies = new Vector<Species>(); 
			InputStream fis = (InputStream) context.getAssets().open("checklists/" + checklist);
			byte[] buffer = new byte[4096];
			String fileText = "";
			int count ; 
			while( (count = fis.read(buffer)) != -1 ){
				fileText += new String(buffer, 0 , count);
			}
			String [] speciesList = fileText.split("\n"); 
			for (int i = 0 ; i < speciesList.length ; i++){
				//Log.d("ISawABird", speciesList[i]);
				Species temp = new Species(speciesList[i]);
				allSpecies.add(temp);
				//Log.d("ISawABird", temp.getUnPunctuatedName());
			}
			Log.d(Consts.TAG,  allSpecies.size() + " species added to checklist");
			checklistLoaded = true; 
		}catch(IOException ioex){
			throw ioex;
		}
	}
	
	/** Function to search through the 'current' checklist for a given search term. 
	 * @param searchTerm : The search query. 
	 * @param subset : The subset of the checklist to search within. If null, search 
	 * is performed on the full checklist. This is useful to search as the user types.
	 * For each key press, the search function can be called with the subset being the 
	 * return value from the previous call. 
	 * 
	 * @returns A Vector of Species objects matching the search terms.
	 */
	public static Vector<Species> search(String searchTerm, Vector<Species> subset) throws ISawABirdException{
		if (!checklistLoaded){
			throw new ISawABirdException("Checklist not loaded. Use Utils.initializeChecklist() first.");
		}
		Vector<Species> searchList = (subset == null) ? allSpecies : subset ;
		Vector <Species> returnVal = new Vector<Species> (); 
		Iterator<Species> iter = searchList.iterator(); 
		while(iter.hasNext()){
			Species temp = iter.next(); 
			if (temp.getUnpunctuatedName().indexOf(Utils.unpunctuate(searchTerm)) != -1){
				
				Log.d(Consts.TAG, "Adding " + temp.getFullName() + " to search results.");
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
		return prefs.getString(Consts.CURRENT_LIST_KEY, null);
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
}