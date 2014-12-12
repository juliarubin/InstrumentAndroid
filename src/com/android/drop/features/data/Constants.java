package com.android.drop.features.data;

public class Constants {

	
//top 20
//	public static String APP_NAME = "com.facebook.orca";
//	public static String APP_NAME = "com.facebook.katana";
//	public static String APP_NAME = "com.pandora.android";
//	public static String APP_NAME = "com.instagram.android";
//	public static String APP_NAME = "com.snapchat.android";
//	public static String APP_NAME = "com.surpax.ledflashlight.panel";
//	public static String APP_NAME = "com.netflix.mediaclient";
//	public static String APP_NAME = "com.skype.raider";
//	public static String APP_NAME = "com.king.candycrushsaga";

//	public static String APP_NAME = "com.grillgames.guitarrockhero";
//	public static String APP_NAME = "com.twitter.android";
//	public static String APP_NAME = "kik.android";
//	public static String APP_NAME = "com.whatsapp";
//	public static String APP_NAME = "com.spotify.music";
//	public static String APP_NAME = "net.zedge.android";
//	public static String APP_NAME = "com.crimsonpine.stayinline";
//	public static String APP_NAME = "air.com.sgn.cookiejam.gp";
//	public static String APP_NAME = "com.walmart.android";
//	public static String APP_NAME = "com.sega.cityrush";
//	public static String APP_NAME = "com.ebay.mobile";
	
	//tests
	//public static String APP_NAME = "com.walmart.android";
	//public static String APP_NAME = "com.google.android.apps.translate";
	public static String APP_NAME = "com.devuni.flashlight";
	//public static String APP_NAME = "com.example.jj";
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
	public static String STATEMENTS_TO_BLOCK_FILE = BASE_DIR + "conf/" + APP_NAME + "_toBlock.txt";
	
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
