package com.isawabird;

import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseUtils {

	private static ParseObject currentList = null; 
	
	public static List<ParseObject> getLists() throws ParseException{
		try{
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Lists");
			List<ParseObject> lists = query.find();
			
			return lists; 
		}catch(ParseException ex){
			throw ex;
		}
		 
	}
	
	public static ParseObject getCurrentList() throws ParseException{
		try{
			if (currentList == null){
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Lists");
				query.find()
			}
			
		}catch(ParseException ex){
			throw ex; 
		}
	}
}
