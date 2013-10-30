package com.isawabird;

import java.util.Date;

import com.isawabird.parse.ParseUtils;

import android.content.ContentValues;

public class Sighting {
	
	private Date date = new Date();
	private Species species = null;
	private String ListName = BirdList.getCurrentListName();
	private float latitude ;
	private float longitude; 
	private String notes;
	private int numberOfBirds = 1; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	private String parseObjectID = null; 
	private int isUploadRequired = 1; // Using int since SQLite doesn't support boolean directly.   
	

	public Sighting(String species ){
		this.species = new Species(species);
	}
	
	public Sighting(Species speciesName){
		this.species = speciesName;
	}
	
	public Species getSpecies(){
		return species;
	}

	public void setSpecies(Species species){
		this.species = species; 
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

	public int isUpdateRequired() {
		return isUploadRequired;
	}

	public void setUpdateRequired(int isUpdateRequired) {
		this.isUploadRequired = isUpdateRequired;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}	
}
