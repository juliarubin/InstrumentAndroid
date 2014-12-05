package com.android.drop.features.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExecutedMethodsManager {
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

	public void processExecutionLog(String filename) {
		HashSet<String> sources = new HashSet<String>();
		HashSet<String> sinks = new HashSet<String>();
		HashSet<String> entrypoints = new HashSet<String>();
		
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        while (line != null) {	          
	            //methodCall
	        	String methodData[] = line.split(Constants.LOG_MARKER);
	            if (methodData.length == 2) {
	            	String signiture = methodData[1];	         
	            	Method method = getMethod(signiture);
	            	if (method == null) {
	            		System.err.println("Method executed but not in the data structure: " + signiture);
	            	}
	            	else {
	            		method.setCalled(true);
	            	}
	            	line = br.readLine();
	            	continue;
	            }
	            
	            //source
	           String data[] = line.split(Constants.SOURCE_MARKER);
	           if (data.length == 2) {
	        	   methodData = data[1].split(" from ");
	        	   sources.add(methodData[0]);
	        	   entrypoints.add(methodData[1]);
	        	   
	        	   line = br.readLine();
	               continue;
	        	   
	           }
	           
	        	//sink
	           data = line.split(Constants.SINK_MARKER);
	           if (data.length == 2) {
	        	   methodData = data[1].split(" from ");
	        	   sinks.add(methodData[0]);
	        	   entrypoints.add(methodData[1]);
	        	   
	        	   line = br.readLine();
	               continue;  
	           }
	            
	           //wrong line
	           System.err.println("Bad execution log entry: " + line);
	           line = br.readLine();
	        }
	        
	        br.close();
	        
	        dumpSourceSinkEntryPoints(sources, sinks, entrypoints);
	    } 
	    catch (IOException e) {
			e.printStackTrace();
		}	    	 
		
	}

	private void dumpSourceSinkEntryPoints(HashSet<String> sources,
			HashSet<String> sinks, HashSet<String> entrypoints) {
		org.objectweb.asm.Type type;
		
		try {
			File file = new File(Constants.SOURCES_AND_SINKS_FILE);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
//			for (String m : sources) {
//				//type = org.objectweb.asm.Type.getMethodType(m);
//				output.write("<" + m + "> -> _SOURCE_" + System.lineSeparator());
//			}
//			for (String m : sinks) {
//				output.write("<" + m + "> -> _SINK_" + System.lineSeparator());
//			}
//			output.close();
			
			file = new File(Constants.ENTRYPOINTS_FILE);
			output = new BufferedWriter(new FileWriter(file));
			for (String m : entrypoints) {
				output.write("<" + m + ">" + System.lineSeparator());
			}
			output.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
