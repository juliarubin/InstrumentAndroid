package com.android.drop.features.runner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.android.drop.features.data.Method;

public class ResultsProcessor {
	
	public static String[] apps = {
		//"com.facebook.katana", 
		"com.pandora.android",
		"com.king.candycrushsaga",
		"com.grillgames.guitarrockhero",
		"com.twitter.android",
		"com.spotify.music",
		"net.zedge.android",
		"com.crimsonpine.stayinline",
		"air.com.sgn.cookiejam.gp",
		"com.walmart.android"
	};
	
//	public static String[] apps = {
//		"cair.com.sgn.cookiejam.gp"
//	};
	
	public static String RES_DIR = "/Volumes/D/data/minst/conf/";
	
	public static HashSet<String> dynamicNeeded ;
	public static HashSet<String> dynamicUnneeded;
	public static HashSet<String> dynamicNotCalled;
	
	public static HashSet<String> staticHandled;
	public static HashSet<String> staticUnhandled;
	public static HashSet<String> staticUnknown;
	
	public static void main(String[] args) {
	
		for (String app : apps) {
			System.out.println("\n**** " + app + " ****");
			
			dynamicNeeded = new HashSet<String>();
			dynamicUnneeded = new HashSet<String>();
			dynamicNotCalled = new HashSet<String>();
			
			staticHandled = new HashSet<String>();
			staticUnhandled = new HashSet<String>();
			staticUnknown = new HashSet<String>();
			
			readDynamicResults(RES_DIR + app + "_toBlock1_final.txt");
			readStaticResults(RES_DIR + "connection-exception-analysis.20150119_1530/" + app + "/droidsafe-gen/connection-error-analysis.txt");
			//compare();
			compare1();
		}
	
	}

	private static void compare1() {
		float incorrect =0, correct = 0;
		for (String s : staticUnhandled) {
			if (containsDynamicNeeded(s)) {
				System.out.println("Dynamic needed/Static unhandled: " + s);
				incorrect++;
			}
			else {
				correct ++;
			}
		}
		
		for (String s : dynamicUnneeded) {
			if (!staticUnhandled.contains(s)) {
				if (staticHandled.contains(s)) {
					System.out.println("Dynamic unneeded/Static handled: " + s);
				}
				if (staticUnknown.contains(s)) {
					System.out.println("Dynamic unneeded/Static unknown: " + s);
				}
			}
		}
		
		System.out.println("Static unhandled = " + staticUnhandled.size() + "; dynamic unneeded = " + dynamicUnneeded.size() + 
		"; overlap = " + correct + "; static unhandled but dymanic needed = " + incorrect);
		float precision = correct/staticUnhandled.size();
		float recall = correct/dynamicUnneeded.size();
		System.out.println(" | Precision =  " + precision + "  Recall = " + recall + " F-measure = " + 2*precision*recall  / (precision + recall));
	
//		float n = 0, u = 0, all = 0;
//		for (String s : dynamicNeeded) {
//			if (!s.contains("transact")) {
//				n++;
//			}
//			all++;
//		}
//		
//		//System.out.println("Needed real =  " + n/all);
//		
//		all = 0;
//		for (String s : dynamicUnneeded) {
//			if (!s.contains("transact")) {
//				u++;
//			}
//			all++;
//		}
//		
//		//System.out.println("Unneeded real =  " + u/all);
	}

	private static void compare() {
		//Precision: correct / reported
		//Recall: correct / expected
				
		//needed
		float neededExpected = dynamicNeeded.size();
		float neededReported = staticHandled.size();
		float neededCorrect = 0;
		for (String s : staticHandled) {
			//check if it is also needed in the dynamic analysis
			if (containsDynamicNeeded(s)) {
				neededCorrect++;
			}
			else {
				//sanity check
        		//System.out.println("Dynamic unneded/Static handled: " + s);
				if (!dynamicNotCalled.contains(s) && !dynamicUnneeded.contains(s)) {
					System.out.println("Line found statically but not dynamically: " + s);
				}
			}
		}
		
		System.out.print("Needed: expected = " + neededExpected + " reported = " + neededReported + " correct = " + neededCorrect);
		float precision = neededCorrect/neededReported;
		float recall = neededCorrect/neededExpected;
		System.out.println(" | Precision =  " + precision + "  Recall = " + recall + " F-measure = " + 2*precision*recall  / (precision + recall));
		
		//unneeded
		float unneededExpected = dynamicUnneeded.size();
		float unneededReported = staticUnhandled.size();
		float unneededCorrect = 0;
		for (String s : staticUnhandled) {
			//check if it is also unneeded in the dynamic analysis
			if (dynamicUnneeded.contains(s)) {
				unneededCorrect++;
			}
			else {
				//sanity check
				//System.out.println("Dynamic needed/Static unhandled: " + s);
				if (!dynamicNotCalled.contains(s) && !containsDynamicNeeded(s)) {
					System.out.println("Line found statically but not dynamically: " + s);
				}
			}
		}
		
		System.out.print("Unneeded: expected = " + unneededExpected + " reported = " + unneededReported + " correct = " + unneededCorrect);
		precision = unneededCorrect/unneededReported;
		recall = unneededCorrect/unneededExpected;
		System.out.println(" | Precision =  " + precision + "  Recall = " + recall + " F-measure = " + 2*precision*recall  / (precision + recall));
		
		//unknown
		System.out.println("Unknown: " + staticUnknown.size());
	}
	
	private static boolean containsDynamicNeeded(String s) {
		for (String s1 : dynamicNeeded) {
			if (s1.contains(s)) {
				return true;
			}
		}
		return false;
	}

	private static void readDynamicResults(String filename) {
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        boolean called = false;
	        
	        while (line != null) {	          
	        	if (line.startsWith("%---")) {
	        		called = true;
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (line.equals("")) {
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (line.startsWith("%-")) {
	        		line = br.readLine();
	        		continue;
	        	}
	            if (!called) {
	            	dynamicNotCalled.add(line);
	            }
	            else {
	            	if (line.startsWith("%")) {
	            		dynamicNeeded.add(line);
	            	}
	            	else {
	            		dynamicUnneeded.add(line);
	            	}
	            }
	            line = br.readLine();
	        }
	        
	        br.close();
	    } 
	    catch (IOException e) {
			e.printStackTrace();
		}	  
		
	}

	private static void readStaticResults(String filename) {
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        int status = 0; //unhandled, unknown, handled
	                
	        while (line != null) {	          
	        	if (line.startsWith("Connect calls unhandled")) {
	        		status = 0;
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (line.startsWith("Connect calls with handling unknown")) {
	        		status = 1;
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (line.startsWith("Connect calls handled")) {
	        		status = 2;
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (line.equals("")) {
	        		line = br.readLine();
	        		continue;
	        	}
	        	
//	        	line = line.replace(".", "/");
//	        	line = line.replace(": ", ".");
	        	//if (dynamicNotCalled.contains(line)) {
	        	if (!containsDynamicNeeded(line) && !dynamicUnneeded.contains(line)) {
	        		line = br.readLine();
	        		continue;
	        	}

	        	if (status == 0) {
	        		staticUnhandled.add(line);
	        	}
	        	else if (status == 1) {
	        		staticUnknown.add(line);
	        	}
	        	else {
	        		staticHandled.add(line);
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
