package com.android.drop.features.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockedStatementsManager {
	private static HashMap<String, List<String>> blockStatements = null;
	
	public static HashMap<String, List<String>> getBlockStatements(String filename) {
		if (blockStatements == null) {
			blockStatements = new  HashMap<String, List<String>>();
			readFromFile(filename);
		}
		return blockStatements;
	}
	
	//TODO add statement count for identical calls within the same method
	
	private static void readFromFile(String filename) {	
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        while (line != null) {	
	        	if (line.startsWith("%")) {
	        		line = br.readLine();
	        		continue;
	        	}
	            String methodData[] = line.split(" ");
	            if (methodData.length != 2) {
	            	System.err.println("Bad line in input: " + line);
	            	line = br.readLine();
	            	continue;
	            }	     
	            if (blockStatements.containsKey(methodData[0])) {
	            	blockStatements.get(methodData[0]).add(methodData[1]);
	            }
	            else {
	            	ArrayList<String> m = new ArrayList<String>();
	            	m.add(methodData[1]);
	            	blockStatements.put(methodData[0], m);
	            }
	            line = br.readLine();
	        }
	        
	        br.close();
	    } 
	    catch (IOException e) {
			e.printStackTrace();
		}	    	 
	}	

}
