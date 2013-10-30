package com.isawabird;

import java.util.Date;

import android.content.ContentValues;
import android.util.Log;

import com.isawabird.parse.ParseUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;

public class BirdList {
	
	private static String currentListName;
	private static int currentListId;
	private Date date = new Date(); 
	private int id = -1;
	private String listName = null; 
	private String location = null;
	private String notes = null; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	private String parseObjectID = null; 
	
	public BirdList(String listName){
		this.listName = listName;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getParseObjectID() {
		return parseObjectID;
	}

	public void setParseObjectID(String parseObjectID) {
		this.parseObjectID = parseObjectID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/** Function to return the current list to which sightings are added 
	 * @return A ParseObject for the current list
	 * @throws ISawABirdException
	 */
	public static String getCurrentListName() {
		// TODO: Fix this
		String currentListName = prefs.getString(Consts.CURRENT_LIST_KEY, "Hebbal Oct 2013");
		Log.d(Consts.TAG, "Current list name is " + currentListName);
		return currentListName;
	}

	public static void setCurrentList(String name, int id) {
		currentListName = name;
		currentListId = id;
	}
}
