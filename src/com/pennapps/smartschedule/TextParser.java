package com.pennapps.smartschedule;

import java.util.ArrayList;

import android.util.Log;

	
public class TextParser {
	
	String speech;
	String[] value;
	
	private ArrayList<String> list;	
	TextParser(ArrayList<String> l){
		list = l;
	}
	public enum HW{
		DUE, TOMORROW, TIME			
	}
	public void speech(){
		
		speech = list.get(0);
		value = speech.split(" ");
		//String first = value[0];
		
		for(int i = 0; i <= value.length-1; i++){
			if(value[i].equals("exit")){
				// create Intent to take a picture and return control to the calling application
			    System.exit(0);
			    
			    
				Log.wtf("OOOO YEA", "I found do!");
			}
		}
		
		Log.wtf("What they said", speech);
	}
	
	
}
