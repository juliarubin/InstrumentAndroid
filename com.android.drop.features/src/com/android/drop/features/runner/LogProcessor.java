package com.android.drop.features.runner;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataManager;

public class LogProcessor {
	
	public static DataManager dm = new DataManager();


	public static void main(String[] args) {
		dm.readMethodsFromFile(Constants.METHOD_DATA_FILE);
		dm.readStatementsFromFile(Constants.STATEMENT_DATA_FILE);
		
		dm.processExecutionLog(Constants.EXECUTED_LOG_FILE);
		
		dm.dumpMethodsToFile(Constants.METHOD_DATA_FILE); 
		dm.dumpStatementsToFile(Constants.STATEMENT_DATA_FILE);
		
		dm.dumpStatementsToBlockToFile(Constants.STATEMENTS_TO_BLOCK_FILE);
		
		//for flowdroid
		//dm.dumpSourceSinkEntryPoints(Constants.SOURCES_AND_SINKS_FILE, Constants.ENTRYPOINTS_FILE);
	
	}

}
