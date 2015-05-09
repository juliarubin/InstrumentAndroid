package com.android.drop.features.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import com.android.drop.features.runner.AppRecord.Dynamic;
import com.android.drop.features.runner.AppRecord.Static;

public class ResultsProcessorSuccess {
	
//	public static String[] apps = {
//		"com.facebook.katana", 
//		"com.pandora.android",
//		"com.king.candycrushsaga",
//		"com.grillgames.guitarrockhero",
//		"com.twitter.android",
//		"com.spotify.music",
//		"net.zedge.android",
//		"com.crimsonpine.stayinline",
//		"air.com.sgn.cookiejam.gp",
//		"com.walmart.android"
//	};

	
	public static String[] apps = {
	"air.com.sgn.cookiejam.gp",
	"com.crimsonpine.stayinline",
	"com.facebook.katana",
	"com.grillgames.guitarrockhero",
	"com.king.candycrushsaga",
	"com.pandora.android",
	"com.spotify.music",
	"com.twitter.android",
	"com.walmart.android",
	"net.zedge.android"
	};
	
	
//	public static String[] apps = {
//		"air.com.sgn.cookiejam.gp"
//	};
	
	public static String[] AALibraries = {
		"com/google/android/gms",
		"com/android/vending/billing",
//		"com/flurry/sdk", 
//		"com/millennialmedia/android",
//		"com/crashlytics/android/",
//		"com/comscore/metrics/",
//		"com/yume/android", 
//		"com/AdX", 
//		"com/tapjoy",
//		"com/squareup",
//		"com/mobileapptracker",
//		"com/mopub/mobileads",
//		"com/unity3d/ads",
//		"com/bugsense/trace",
//		"com/urbanairship"
		"org/apache" //technically, not A&A, but static analysis ignores it
	};
	
	public static String RES_DIR = "/Volumes/D/data/minst/conf/";
	public static boolean PRINTOUTS = true;
	public static int CONNECTION_TYPE = 0; //0 - external; 1 - internal; 2 - both
	public static String RES_VERSION = "connection-exception-analysis.20150508_2000/";
	

	
	public static HashSet<String> staticEssential;
	public static HashSet<String> staticOptional;
	public static HashSet<String> staticNonEssential;
	public static HashSet<String> staticUnknown;
	
	static float totalPrecisionNonEssention = 0, totalRecallNonEssention = 0;
	static float totalStaticFound = 0, totalCorrect =0, totalDynamic = 0; 
	
	public static void main(String[] args) {
		System.out.println("----------------------------------");
		System.out.println("----------- External --------------");
		System.out.println("----------------------------------");
		processForType(0);
//		
//		System.out.println("----------------------------------");
//		System.out.println("----------- Internal --------------");
//		System.out.println("----------------------------------");
//		processForType(1);
//		
//		System.out.println("----------------------------------");
//		System.out.println("----------- Both --------------");
//		System.out.println("----------------------------------");
//		processForType(2);
		
//		System.out.println("----------------------------------");
//		System.out.println("----------- No A&A --------------");
//		System.out.println("----------------------------------");
//		processForType(3);
	}
	
	public static void processForType(int type) {
		totalStaticFound = 0;
		totalCorrect = 0;
		totalDynamic = 0;
		
		totalPrecisionNonEssention = 0;
		totalRecallNonEssention = 0;
		
		for (String app : apps) {
			System.out.println("\n**** " + app + " ****");
			
			AppRecord appRecord = new AppRecord(app);
			readDynamicResults(appRecord, new File(RES_DIR + app + "_toBlock1_final.txt"), type);
			readStaticResults(appRecord, new File(RES_DIR + RES_VERSION + app + "/droidsafe-gen/connection-error-analysis.txt"), type);
	
//			if (PRINTOUTS) {
//				readSanity(appRecord);
//			}
//
//			initiateStaticList(appRecord);
//			
//			compare(appRecord);
//			
//			if (PRINTOUTS) {
//				printStaticButNotDynamic(appRecord.dynamicResults);
//			}
			
			int totalCalled = appRecord.dynamicResults.unneeded.size() + appRecord.dynamicResults.essential.size() + appRecord.dynamicResults.optional.size();
			
			System.out.println("Non-essential: " + appRecord.dynamicResults.unneeded.size() + " out of " + totalCalled + " (" + (1.0*appRecord.dynamicResults.unneeded.size()/totalCalled) + "%)");
			System.out.println("Non Called: " + appRecord.dynamicResults.notCalled.size() + " out of " + (totalCalled+appRecord.dynamicResults.notCalled.size()) + " (" + (1.0*appRecord.dynamicResults.notCalled.size()/(totalCalled +appRecord.dynamicResults.notCalled.size())) + "%)");
			
			
		}
		
		System.out.println("=====");
		System.out.println("Precision =  " + (totalCorrect / totalStaticFound) + "  Recall = " + (totalCorrect / totalDynamic) );
		System.out.println("Ave Precision =  " + (totalPrecisionNonEssention / apps.length) + "  Recall = " + (totalRecallNonEssention / apps.length) );
	}

	
    private static void printStaticButNotDynamic(Dynamic dynamicResults) {
    	System.out.println("Static Non-Essential But Dynamic:");
 		for (String m : staticNonEssential) {
 			if (dynamicResults.unneeded.contains(m)) {
 				continue;
 			}
 			
 			System.out.println(" " + m);
 			
 			if (dynamicResults.essential.contains(m)) {
 				System.out.println("   -> dynamic essential");
 			}
 			if (dynamicResults.optional.contains(m)) {
 				System.out.println("   -> dynamic optional");
 			}
 			
 		}
		
	}

	private static void compare(AppRecord appRecord) {
//    	System.out.println("Essential: ");
//    	calculateStat(appRecord.dynamicResults.essential, staticEssential);
//    	
//    	System.out.println("Optional: ");
//    	calculateStat(appRecord.dynamicResults.optional, staticOptional);
    	
    	System.out.println("Non-Essential: ");
    	calculateStat(appRecord.dynamicResults.unneeded, staticNonEssential);
    	
//    	System.out.println("Non-Esentional and Optional: ");
//    	HashSet<String> dynamicSet = new HashSet<String>();
//    	dynamicSet.addAll(appRecord.dynamicResults.optional);
//    	dynamicSet.addAll(appRecord.dynamicResults.unneeded);
//    	HashSet<String> staticSet = new HashSet<String>();
//    	staticSet.addAll(staticOptional);
//    	staticSet.addAll(staticNonEssential);
//    	calculateStat(dynamicSet, staticSet);

	}
    
    private static void calculateStat(HashSet<String> dynamicSet, HashSet<String> staticSet) {
    	if (PRINTOUTS) {   		
    		printBreakdown(dynamicSet);
    	}
    	
    	HashSet<String> correct = new HashSet<String>();
    	correct.addAll(dynamicSet);
    	correct.retainAll(staticSet);
    	
    	double p = 1;
    	if (staticSet.size() != 0) {
    		p = correct.size()*1.0/staticSet.size();
    	}
		double r = 1;
		if (dynamicSet.size() != 0) {
			r = correct.size()*1.0/dynamicSet.size();
		}
	
		System.out.println("precision " + correct.size() + "/" + staticSet.size() +
				" (" + p + ")" +
				" recall " + correct.size() + "/" + dynamicSet.size() +
				" (" + r + ")" 
		);   
		
		totalCorrect += correct.size();
		totalStaticFound += staticSet.size();
		totalDynamic += dynamicSet.size();
		
		totalPrecisionNonEssention += p;
		totalRecallNonEssention += r;
    	
	}


	private static void printBreakdown(HashSet<String> dynamicSet) { 
    	int i = 0;
    	System.out.println("Dynamic:");
 		for (String m : dynamicSet) {
 			System.out.println(" " + m);
 			if (staticEssential.contains(m)) {
 				System.out.println("   -> static essential");
 			}
 			if (staticOptional.contains(m)) {
 				System.out.println("   -> static optional");
 			}
 			if (staticNonEssential.contains(m)) {
 				System.out.println("   -> static non-essential");
 			}
 			if (staticUnknown.contains(m)) {
 				System.out.println("   -> static unknown");
 				i++;
 			}
 		}
 		
 		System.out.println("Unknows: " + i);
 	}


	private static void initiateStaticList(AppRecord appRecord) {
    	staticEssential = new HashSet<String>();			
    	staticOptional = new HashSet<String>();
    	staticNonEssential = new HashSet<String>();
    	staticUnknown = new HashSet<String>();
    	
    	HashSet<String> allStatic = new HashSet<String>();
    	allStatic.addAll(appRecord.staticResults.UIonSuccessForward);
    	allStatic.addAll(appRecord.staticResults.noUIonSuccessForward);
    	
    	for (String m : allStatic) {
    		//not executed dynamically - no information
//    		if (appRecord.dynamicResults.notCalled.contains(m)) {
//    			continue;
//    		}
    		if (!appRecord.dynamicResults.essential.contains(m) && !appRecord.dynamicResults.optional.contains(m) && !appRecord.dynamicResults.unneeded.contains(m)) {
    			continue;
    		}
    		
    		
    		//essential -- UI on failure
    		if (appRecord.staticResults.UIonFailure.contains(m))  {
    			staticEssential.add(m);
    		}
    		//optional -- no UI on failure but UI on success
    		else if (appRecord.staticResults.noUIonFailure.contains(m) && isUIOnSuccess(appRecord.staticResults, m)) {
    			staticOptional.add(m);
    		}
    		//non-essential -- no UI neither on failure nor on success
    		else if (appRecord.staticResults.noUIonFailure.contains(m) && !isUIOnSuccess(appRecord.staticResults, m)) {
    			staticNonEssential.add(m);
    		}
    		else if (appRecord.staticResults.unknownOnFailure.contains(m)) {
    			staticUnknown.add(m);
    		}
    		else {
    			System.err.println("Static result can't be classified " + m);
    		}
    	}
	}
    
	static boolean isUIOnSuccess(Static staticResults, String m) {
    	return staticResults.UIonSuccessForward.contains(m) || 
    		staticResults.UIonSuccessBackward.contains(m);
    }



	
	public static void readDynamicResults(AppRecord appRecord, File file, int connectionType) {
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(file));	       
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
	        	if (connectionType == 0 && line.contains("android/os/IBinder.transact(")) {
	        		line = br.readLine();
	        		continue;
	        	}
	        	if (connectionType == 1 && !line.contains("android/os/IBinder.transact(")) {
	        		line = br.readLine();
	        		continue;
	        	} 
	        	if (connectionType == 3 && isFromAALibrary(line)) {
        			line = br.readLine();
        			continue;
	        	} 
	        	
	            if (!called) {
	            	appRecord.dynamicResults.notCalled.add(line);
	            }
	            else {
	            	if (line.startsWith("% ")) {
	            		String[] data = line.split(" ");
	            		if (data[1].equals("optional")) {
	            			appRecord.dynamicResults.optional.add(data[2] + " " + data[3] + " " + data[4]);
	            		}
	            		else if (data[1].equals("mandatory")) {
	            			appRecord.dynamicResults.essential.add(data[2] + " " + data[3] + " " + data[4]);
	            		}
	            		else {
	            			System.err.println("Dynamic unused is neither mandatoty not optional: " + line);	
	            		}
	            	}
	            	else {
	            		appRecord.dynamicResults.unneeded.add(line);
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

	private static void readStaticResults(AppRecord appRecord, File file, int connectionType) {
		int type = 0;
		
		//String[] data = file.getAbsolutePath().split("/");
		//String appName = data[data.length-1].split("-")[0];
		
		 try {
		    	BufferedReader br = new BufferedReader(new FileReader(file));	       
		        String line = br.readLine();

		        while (line != null) {	          
		        	if (line.startsWith("Call with errors unhandled")) {
		        		type = 1;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with error handling unknown")) {
		        		type = 2;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with errors handled")) {
		        		type = 3;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with UI effect (UI call) on success forward path")) {
		        		type = 4;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with NO UI effect (UI call) on success forward path")) {
		        		type = 5;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with UI effect (UI call) on success backward path")) {
		        		type = 6;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("Calls with NO UI effect (UI call) on success backward path")) {
		        		type = 7;
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.equals("")) {
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (line.startsWith("java/lang/reflect/Method.invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")) {
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (connectionType == 0 && line.startsWith("android/os/IBinder.transact(")) {
		        		line = br.readLine();
		        		continue;
		        	}
		        	if (connectionType == 1 && !line.startsWith("android/os/IBinder.transact(")) {
		        		line = br.readLine();
		        		continue;
		        	} 
		        	if (connectionType == 3 && isFromAALibrary(line)) {
		        		line = br.readLine();
			        	continue;
		        	} 
		        	
		        	if (type == 1) { //Call with errors unhandled
		        		appRecord.staticResults.noUIonFailure.add(line);
		        	}
		        	else if (type == 2) {
		        		appRecord.staticResults.unknownOnFailure.add(line); //Calls with error handling unknown
		        	}
		        	else if (type == 3) { //Calls with errors handled
		        		appRecord.staticResults.UIonFailure.add(line);
		        	}
		        	else if (type == 4) { //Calls with UI effect (UI call) on success forward path
		        		appRecord.staticResults.UIonSuccessForward.add(line);
		        	}
		        	else if (type == 5) { //Calls with NO UI effect (UI call) on success forward path
		        		appRecord.staticResults.noUIonSuccessForward.add(line);
		        	}
		        	else if (type == 6) { //Calls with UI effect (UI call) on success backward path
		        		appRecord.staticResults.UIonSuccessBackward.add(line);
		        	}
		        	else if (type == 7) { //Calls with NO UI effect (UI call) on success backward path
		        		appRecord.staticResults.noUIonSuccessBackward.add(line); 
		        	}
		                                
		            line = br.readLine();
		        }
		        
		        br.close();
		        
		    } 
		    catch (IOException e) {
				e.printStackTrace();
				return; 
			}	    	
		
	}

	private static boolean isFromAALibrary(String line) {
		String[] data = line.split(" from ");
		for (String aal: AALibraries) {
			if (data[1].contains(aal)) {
				return true;
			}
		}
		return false;
	}
	
	private static void readSanity(AppRecord appRecord) {
		//check static results
		System.out.println(" ----- Sanity ------- ");
		
		System.out.println("Dynamic all: " + (appRecord.dynamicResults.essential.size()+appRecord.dynamicResults.optional.size()+appRecord.dynamicResults.unneeded.size()+appRecord.dynamicResults.notCalled.size()));
		
		System.out.println(" essential=" + appRecord.dynamicResults.essential.size() +
				           " optional=" + appRecord.dynamicResults.optional.size() +
				           " unneeded=" + appRecord.dynamicResults.unneeded.size() +
				           " notCalled=" + appRecord.dynamicResults.notCalled.size());
		
		System.out.println("Static all: " + (appRecord.staticResults.noUIonFailure.size()+appRecord.staticResults.UIonFailure.size()+appRecord.staticResults.unknownOnFailure.size()) + 
					" == " + 
				(appRecord.staticResults.UIonSuccessBackward.size() + appRecord.staticResults.noUIonSuccessBackward.size()) +
					" == " + 
				(appRecord.staticResults.UIonSuccessForward.size() + appRecord.staticResults.noUIonSuccessForward.size()));
		
		System.out.println("staticResults.UIonSuccessForward " + appRecord.staticResults.UIonSuccessForward.size());
		System.out.println("staticResults.UIonSuccessBackwards " + appRecord.staticResults.UIonSuccessBackward.size());
		System.out.println("staticResults.noUIonSuccessForward " + appRecord.staticResults.noUIonSuccessForward.size());
		System.out.println("staticResults.noUIonSuccessBackwards " + appRecord.staticResults.noUIonSuccessBackward.size());
		
		HashSet<String> allDynamic = new HashSet<String>();
		allDynamic.addAll(appRecord.dynamicResults.essential);
		allDynamic.addAll(appRecord.dynamicResults.optional);
		allDynamic.addAll(appRecord.dynamicResults.unneeded);
		allDynamic.addAll(appRecord.dynamicResults.notCalled);
		
		HashSet<String> allStatic = new HashSet<String>();
		allStatic.addAll(appRecord.staticResults.UIonSuccessForward);
		allStatic.addAll(appRecord.staticResults.noUIonSuccessForward);
		
		//why more static then dymanic?
		for (String r: allStatic) {
			if (!allDynamic.contains(r)) {
				System.out.println("Exists statically but not dynamically: " + r);
			}
		}
		
		//why more dynamic then static?
		for (String r: allDynamic) {
			if (!allStatic.contains(r)) {
				System.out.println("Exists dynamically but not statically: " + r);
			}
		}
		
//		//are there any elements with UIonSuccessForward but not UIonSuccessBackward
//		for (String r: appRecord.staticResults.UIonSuccessForward) {
//			if (!appRecord.staticResults.UIonSuccessBackward.contains(r)) {
//				System.out.println("HERE UIonSuccessForward but not UIonSuccessBackward: " + r);
//			}
//		}
		
		System.out.println(" ----- End Sanity ------- ");
	}


	
}
