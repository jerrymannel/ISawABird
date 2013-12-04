package com.isawabird;

public class Species {

	public String commonName ; 
	public String scientificName;
	public String fullName; 
	public String unpunctuatedName; 
	
	public Species(String fullName){
		this.fullName = fullName;
		parseFullName(); 
	}
	
	
	/* A species name is stored in the text file as 
	 * Ashy-crowned Finch-lark (Eremopterix griseus)
	 * Separate out the common and the scientifc names
	 */
	private void parseFullName(){
		int index = fullName.indexOf("(");
		if (index != -1){
			commonName = fullName.substring(0, index - 1);
			int newIndex = fullName.indexOf(")", index) ; 
			if (newIndex != -1){
				scientificName = fullName.substring(index + 1, newIndex); 
			}else{
				scientificName = fullName.substring(index + 1);				
			}
		}else{
			commonName = fullName;
			scientificName = "";
		}
		
		/* Get the unpuntuated name */
		unpunctuatedName = Utils.unpunctuate(fullName);
		
	}
	
	@Override
	public String toString(){
		return fullName; 
	}
}
