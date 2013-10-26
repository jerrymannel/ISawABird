package com.isawabird;

import java.util.Iterator;
import java.util.Set;

import org.json.JSONObject;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class Sighting {
	
	private String Species = "";
	private String ListName = ""; // TODO Replace with getCurrentListName
	private float latitude ;
	private float longitude; 
	private int numberOfBirds = 1; 
	private String username = ParseUtils.getCurrentUser().getUsername();
	

	public Sighting(String species ){

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
	
}
