package com.isawabird;

import java.util.Date;

import com.isawabird.parse.ParseUtils;
import com.parse.ParseGeoPoint;

public class Sighting {
	
	private long id;
	private Date date = new Date();
	private Species species = null;
	private String listName;
	private long listId;
	private double latitude ;
	private double longitude; 
	private String notes;
	/*private int numberOfBirds = 1; 
	private String username = ParseUtils.getCurrentUser().getUsername();*/
	private String parseObjectID = null; 
	private boolean isMarkedForDelete = false;
	private boolean isMarkedForUpload = true;

	public Sighting(String species ){
		this(new Species(species));
	}
	
	public Sighting(Species speciesName){
		this.species = speciesName;
		this.date = new Date();
		ParseGeoPoint myDot = ParseUtils.getLastKnownLocation(); 
		this.latitude = myDot.getLatitude() ; 
		this.longitude = myDot.getLongitude();
	}
	
	public Species getSpecies(){
		return species;
	}

	public void setSpecies(Species species){
		this.species = species; 
	}

	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getParseObjectID() {
		return parseObjectID;
	}

	public void setParseObjectID(String parseObjectID) {
		this.parseObjectID = parseObjectID;
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

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public long getListId() {
		return listId;
	}

	public void setListId(long listId) {
		this.listId = listId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {		
		return new StringBuffer().append(id).append(", ").append(species.toString()).append(", ")
				.append(listName).append(", ").append(date).append(", ").append(parseObjectID)
				.append(", ").append(String.valueOf(listId)).toString();
	}
	public boolean isMarkedForUpload() {
		return isMarkedForUpload;
	}

	public void setMarkedForUpload(boolean isMarkedForUpload) {
		this.isMarkedForUpload = isMarkedForUpload;
	}

	public boolean isMarkedForDelete() {
		return isMarkedForDelete;
	}

	public void setMarkedForDelete(boolean isMarkedForDelete) {
		this.isMarkedForDelete = isMarkedForDelete;
	}
}
