package com.isawabird;

public class ISawABirdException extends Exception {

	private int errorCode = 0;  
	
	public static final int ERR_SIGHTING_ALREADY_EXISTS = 1; 
	
	private static String [] errorMsgs = {"",
										"Sighting already exists"}; 
	
	public ISawABirdException(String message){
		super(message);
	}
	
	public ISawABirdException(int errorCode){
		this(errorMsgs[errorCode]);
		this.errorCode = errorCode;		
	}
	
	public int getErrorCode(){
		return errorCode;
	}
}
