package com.isawabird;

import java.util.Date;

public class Sighting {
	
	private long id;
	private Date date;
	private Species species = null;
	private String listName;
	private long listId;
	private float latitude ;
	private float longitude; 
	private String notes;
	/*private int numberOfBirds = 1; 
	private String username = ParseUtils.getCurrentUser().getUsername();*/
	private String parseObjectID = null; 

	public Sighting(String species ){
		this(new Species(species));
	}
	
	public Sighting(Species speciesName){
		this.species = speciesName;
		this.date = new Date();
	}
	
	public Species getSpecies(){
		return species;
	}

	public void setSpecies(Species species){
		this.species = species; 
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
}
