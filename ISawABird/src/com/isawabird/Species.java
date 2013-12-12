package com.isawabird;

public class Species {

	public String commonName ; 
	public String scientificName;
	public String unpunctuatedName;
	private String fullName;
	private static StringBuilder builder = new StringBuilder();
	enum TYPE {IS_CSV, IS_FULLNAME};

	public Species(String fullName) {
		this.fullName = fullName;
		parseFullName();
	}

	public Species(String name, TYPE type){
		if(type.equals(TYPE.IS_CSV)) {
			String[] list = name.split(Consts.CSV_DELIMITER);
			if(list.length != 3) {
				throw new RuntimeException("Invalid checklist file");
			}
			commonName = list[0];
			scientificName = list[1];
			unpunctuatedName = list[2];
		} else {
			this.fullName = name;
			parseFullName();
		}
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
		unpunctuatedName = Utils.unpunctuate(fullName);
	}

	public String getFullName() {
		if(fullName == null) {
			builder.setLength(0);
			fullName = builder.append(commonName).append(" (").append(scientificName).append(")").toString();
		}
		return fullName;
	}

	@Override
	public String toString(){
		return getFullName(); 
	}
}
