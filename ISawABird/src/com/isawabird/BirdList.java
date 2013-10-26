package com.isawabird;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

public class BirdList {
	
	private Date date = new Date(); 
	private String listName = null; 
	private String location = null;
	private String notes = null; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	
	public BirdList(String listName){
		
	}

	public void save(){
		/* Write to SQLite DB */
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
	

}
