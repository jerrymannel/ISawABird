package com.isawabird;

import java.util.Date;

import com.isawabird.parse.ParseUtils;

public class BirdList {

	private long id = -1;
	private static String currentListName;
	private static int currentListId;
	private Date date; 
	private String listName = null; 
	private String notes = null; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	private String parseObjectID = null;
	private boolean isMarkedForDelete = false;
	private boolean isMarkedForUpload = true;

	public BirdList(String listName){
		this.listName = listName;
		this.date = new Date();
		this.username = ParseUtils.getCurrentUser().getUsername();
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/** Function to return the current list to which sightings are added 
	 * @return A ParseObject for the current list
	 * @throws ISawABirdException
	 */
	/* Moved to Utils.java */
//	public static String getCurrentListName() {
//		// TODO: Fix this
//		/*String currentListName = prefs.getString(Consts.CURRENT_LIST_KEY, "Hebbal Oct 2013");
//		Log.d(Consts.TAG, "Current list name is " + currentListName);
//		return currentListName;*/
//
//		return "Hebbal Oct 2013";
//	}
//
//	public static void setCurrentList(String name, int id) {
//		currentListName = name;
//		currentListId = id;
//	}
	
	public boolean isMarkedForDelete() {
		return isMarkedForDelete;
	}

	public void setMarkedForDelete(boolean isMarkedForDelete) {
		this.isMarkedForDelete = isMarkedForDelete;
	}

	public boolean isMarkedForUpload() {
		return isMarkedForUpload;
	}

	public void setMarkedForUpload(boolean isMarkedForUpload) {
		this.isMarkedForUpload = isMarkedForUpload;
	}

	@Override
	public String toString() {
		
		return new StringBuffer().append(id).append(", ").append(listName).append(", ")
				.append(date).append(", ").append(username).append(", ").append(parseObjectID)
				.append(isMarkedForUpload).append(isMarkedForDelete).toString();
	}
}
