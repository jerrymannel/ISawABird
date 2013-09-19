package com.isawabird;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class ParseUtils {

	private static ParseObject currentList = null; 
	
	public static List<ParseObject> getLists() throws ParseException{
		try{
			ParseUser currentUser = ParseUser.getCurrentUser(); 
			ParseRelation<ParseObject> relation = currentUser.getRelation("Lists");
			List<ParseObject>lists = relation.getQuery().find();
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Lists");
			
			
			return lists; 
		}catch(ParseException ex){
			throw ex;
		}
		 
	}
	
	public static void login(String username, String password) throws ParseException{
		try{
			ParseUser.logIn(username, password);			
		}catch(ParseException ex){
			throw ex;
		}
	}
	
}
