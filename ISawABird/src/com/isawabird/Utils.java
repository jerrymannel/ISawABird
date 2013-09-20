package com.isawabird;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class Utils {

	private static Vector<Species>  allSpecies = new Vector<Species>(); 
	private static boolean checklistLoaded = false; 
	
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
			Log.d("ISawABird",  allSpecies.size() + " species added to checklist");
			checklistLoaded = true; 
		}catch(IOException ioex){
			throw ioex;
		}
	}
	
	public static Vector<Species> search(String searchTerm, Vector<Species> subset) throws ISawABirdException{
		if (!checklistLoaded){
			throw new ISawABirdException("Checklist not loaded. Use Utils.initializeChecklist() first.");
		}
		Vector<Species> searchList = (subset == null) ? allSpecies : subset ;
		Vector <Species> returnVal = new Vector<Species> (); 
		Iterator<Species> iter = searchList.iterator(); 
		searchTerm = searchTerm.toLowerCase(); 
		while(iter.hasNext()){
			Species temp = iter.next(); 
			if (temp.getCommonName().toLowerCase(). indexOf(searchTerm) != -1 || 
					temp.getScientificName().toLowerCase().indexOf(searchTerm) != -1 || 
					temp.getUnPunctuatedName().toLowerCase().indexOf(searchTerm) != -1){
				Log.d("ISawABird", "Adding " + temp.getFullName() + " to search results.");
				returnVal.add(temp);
			}
		}
		return returnVal;
	}
	
}
