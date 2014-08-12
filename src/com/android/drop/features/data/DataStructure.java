package com.android.drop.features.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DataStructure {
	private HashMap<String, Method> methods = new  HashMap<String, Method>();

	public Method getMethod(String key) {
		return methods.get(key);
	}

	public void addMethod(Method method) {
		methods.put(method.getSigniture(), method);
	}
	
	public void dumpToFile(String filename) {
		try {
			File file = new File(filename);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			for (String m : methods.keySet()) {
				output.write(m + "|" + (methods.get(m).getCalled()?"1":"0") + System.lineSeparator());
			}
			output.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile(String filename) {	
		methods = new  HashMap<String, Method>();
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        while (line != null) {	          
	            String methodData[] = line.split("[|]");
	            if (methodData.length != 2) {
	            	System.err.println("Bad line in input: " + line);
	            	continue;
	            }	            
	            Method method = new Method(methodData[0]);
	            method.setCalled(methodData[1].equals("1")?true:false);
	            addMethod(method);
	            line = br.readLine();
	        }
	        
	        br.close();
	    } 
	    catch (IOException e) {
			e.printStackTrace();
		}	    	 
	}

	public void printUnusedMethods() {
		int i = 0;
		for (String m : methods.keySet()) {
			if (methods.get(m).getCalled() == false) {
				System.out.println(m);
				i++;
			}
		}
		System.out.println("Unused " + i + "/" + methods.size());
	}
	
	public void printUsedMethods() {
		int i = 0;
		for (String m : methods.keySet()) {
			if (methods.get(m).getCalled() == true) {
				System.out.println(m);
				i++;
			}
		}
		System.out.println("Used " + i + "/" + methods.size());
	}
	

}
