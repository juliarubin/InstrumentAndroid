package com.android.drop.features.data;

public class Constants {

	
//top 20
//	public static String APP_NAME = "com.facebook.orca";  //INSTRUMENTATION FAILS. TOO LARGE?
//	public static String APP_NAME = "com.facebook.katana"; //DONE. dynamically loads classes from resources that do teh connection
//	public static String APP_NAME = "com.pandora.android"; //DONE 
//	public static String APP_NAME = "com.instagram.android"; //INSTRUMENTED VERSION DOES NOT WORK. Something with annotations.
//	public static String APP_NAME = "com.snapchat.android"; //CHAT excluded
//	public static String APP_NAME = "com.surpax.ledflashlight.panel"; // INSTRUMENTATION FAILS. Jar2Dex fails 
//	public static String APP_NAME = "com.netflix.mediaclient"; //WANTS money
//	public static String APP_NAME = "com.skype.raider"; //CHAT excluded
//	public static String APP_NAME = "com.king.candycrushsaga"; //DONE
//	public static String APP_NAME = "com.grillgames.guitarrockhero";
	
//	public static String APP_NAME = "com.twitter.android"; //DONE
//	public static String APP_NAME = "kik.android"; //CHAT - excluded
//	public static String APP_NAME = "com.whatsapp";   //CHAT - excluded
//	public static String APP_NAME = "com.spotify.music"; //DONE
//	public static String APP_NAME = "net.zedge.android"; //DONE
//	public static String APP_NAME = "com.crimsonpine.stayinline"; //DONE
//	public static String APP_NAME = "air.com.sgn.cookiejam.gp"; //DONE
	public static String APP_NAME = "com.walmart.android"; //DONE
//	public static String APP_NAME = "com.sega.cityrush"; //FOR PAYMENT
//	public static String APP_NAME = "com.ebay.mobile"; //INSTUENTED VERSION CRASHES
	
	//tests
	//public static String APP_NAME = "com.g6677.android.princesshs";
	//public static String APP_NAME = "com.walmart.android";
	//public static String APP_NAME = "com.google.android.apps.translate";
	//public static String APP_NAME = "com.devuni.flashlight";
	//public static String APP_NAME = "com.example.jj";
	//public static String APP_NAME = "com.example.testreflectionapp";
	//public static String APP_NAME = "com.cleanmaster.mguard";
	//public static String APP_NAME = "com.facebook.katana";
	//public static String APP_NAME = "com.appershopper.ios7lockscreen";
	
	public static String BASE_DIR = "/Volumes/D/data/minst/";
	
	public static String SOOT_ANDROID_PLATFORM = BASE_DIR + "android_platforms";
	//public static String SOOT_CALL_GRAPH_DIR = BASE_DIR + "callgraphs/";
	//public static String SOOT_INPUT_DIR = BASE_DIR + "apks/orig/";
	//public static String SOOT_OUTPUT_DIR = BASE_DIR + "apks/sootOutput/";

	public static String DEX2JAR_PATH = BASE_DIR + "../android_tools/dex2jar-0.0.9.15/";
	public static String INPUT_DIR = BASE_DIR + "apks/orig/";
	public static String DECOMPILED_INPUT_DIR = BASE_DIR + "apks/decompiled/orig/";
	public static String ASM_DECOMPILED_OUTPUT_DIR = BASE_DIR + "apks/decompiled/asmOutput/";
	public static String ASM_OUTPUT_DIR = BASE_DIR + "apks/asmOutput/";
	
	public static String METHOD_DATA_FILE = BASE_DIR + "logs/" + APP_NAME + ".methods.log";
	public static String STATEMENT_DATA_FILE = BASE_DIR + "logs/" + APP_NAME + ".statements.log";
	//public static String FILTER_METHOD_DATA_FILE = BASE_DIR + "logs/" + APP_NAME + ".methods_filter.log";
	public static String STATEMENTS_TO_BLOCK_FILE = BASE_DIR + "conf/" + APP_NAME + "_toBlock1.txt";
	
	public static String EXECUTED_LOG_FILE = BASE_DIR + "logs/" + APP_NAME + "_instrument_dev.log";
	
	public static final String INST_DEV_LOG_FILE = "/data/data/" + APP_NAME + "/instrument.log";
	public static final String FILTER_DEV_LOG_FILE = "/data/data/" + APP_NAME + "/filter.log";
	
	//for flowdroid
	public static final String SOURCES_AND_SINKS_FILE = BASE_DIR + "conf/" + APP_NAME + "_SourcesAndSinks" + ".txt";
	public static final String ENTRYPOINTS_FILE = BASE_DIR + "conf/" + APP_NAME + "_Entrypoints_" + ".txt";
	
	public static String LOG_MARKER = "INST_CALL ";
	public static String FILTERED_MARKER = "FILTERED_CALL ";
	public static String SOURCE_MARKER = "_SOURCE_ ";
	public static String SINK_MARKER = "_SINK_ ";
	public static String EXCLUDED = "Excluded";
}
