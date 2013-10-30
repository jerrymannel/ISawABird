package com.isawabird;

import java.util.Date;

import android.content.ContentValues;

import com.isawabird.parse.ParseUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;

public class BirdList {
	
	private Date date = new Date(); 
	private int ListID = -1;
	private String listName = null; 
	private String location = null;
	private String notes = null; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	private String parseObjectID = null; 
	
	public BirdList(String listName){
		this.listName = listName;
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put("ListName", listName); 
		values.put("CreatedByUser", username);
		values.put("Date", date.getTime()); 
		if (location != null){
			values.put("Location", location);
		}
		
		if (notes != null){
			values.put("Notes", notes);
		}
		return values;
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

	public int getListID() {
		return ListID;
	}

	public void setListID(int listID) {
		ListID = listID;
	}
	

}
