package com.android.drop.features.data;

public class Constants {

	public static String APP_NAME = "com.walmart.android";
	//public static String APP_NAME = "com.devuni.flashlight";
	
	public static String BASE_DIR = "/Volumes/D/data/minst/";
	
	public static String SOOT_ANDROID_PLATFORM = BASE_DIR + "android-platforms";
	public static String SOOT_INPUT_DIR = BASE_DIR + "apks/orig/";
	public static String SOOT_OUTPUT_DIR = BASE_DIR + "apks/sootOutput/";

	public static String DEX2JAR_PATH = BASE_DIR + "../android_tools/dex2jar-0.0.9.15/";
	public static String ASM_INPUT_DIR = BASE_DIR + "apks/orig/";
	public static String ASM_DECOMPILED_INPUT_DIR = BASE_DIR + "apks/decompiled/orig/";
	public static String ASM_DECOMPILED_OUTPUT_DIR = BASE_DIR + "apks/decompiled/asmOutput/";
	public static String ASM_OUTPUT_DIR = BASE_DIR + "apks/asmOutput/";
	
	public static String DATA_FILE = BASE_DIR + "logs/" + APP_NAME + ".methods.log";
	public static String LOGCAT_FILE = BASE_DIR + "logs/" + APP_NAME + "_instrumented.logcat.log";

	public static String LOG_MARKER = "INST_CALL ";
	public static String FILTERED_MARKER = "FILTERED_CALL ";
}
