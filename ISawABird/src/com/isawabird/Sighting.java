package com.isawabird;

import java.util.Date;

import android.content.ContentValues;

public class Sighting {
	
	private Date date = new Date();
	private String Species = "";
	private String ListName = Utils.getCurrentListName();
	private float latitude ;
	private float longitude; 
	private int numberOfBirds = 1; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	private String parseObjectID = null; 
	private boolean isUploadRequired = true; 
	

	public Sighting(String species ){
		this.Species = species;
	}
	
	public ContentValues getContentValues(){
		ContentValues ret = new ContentValues();
		ret.put("Date", date.getTime());
		ret.put("ListName", ListName);
		ret.put("Species", Species);
		ret.put("NumberOfBirds", numberOfBirds);
		ret.put("Latitude", latitude);
		ret.put("Longitude", longitude);
		ret.put("Username", username);
		ret.put("ParseObjectID", parseObjectID);
		ret.put("isUploadRequired"	, isUploadRequired);
		
		return ret;
	}
	
	public String getSpecies() {
		return Species;
	}

	public void setSpecies(String species) {
		Species = species;
	}


	public String getListName() {
		return ListName;
	}


	public void setListName(String listName) {
		ListName = listName;
	}


	public float getLatitude() {
		return latitude;
	}


	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}


	public float getLongitude() {
		return longitude;
	}


	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}


	public int getNumberOfBirds() {
		return numberOfBirds;
	}


	public void setNumberOfBirds(int numberOfBirds) {
		this.numberOfBirds = numberOfBirds;
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

	public boolean isUpdateRequired() {
		return isUploadRequired;
	}

	public void setUpdateRequired(boolean isUpdateRequired) {
		this.isUploadRequired = isUpdateRequired;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
