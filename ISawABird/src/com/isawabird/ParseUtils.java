package com.isawabird;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

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
	
	
}
