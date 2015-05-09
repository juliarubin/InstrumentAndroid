package com.android.drop.features.runner;
import java.util.HashSet;


public class AppRecord {

	private String name;
	public Static staticResults;
	public Dynamic dynamicResults;
	
	public AppRecord(String name) {
		this.name = name;
		staticResults = new Static();
		dynamicResults = new Dynamic();
	}
	
	public String getName() {
		return name;
	}
	
	public class Static {
		
		public HashSet<String> noUIonFailure = new HashSet<String>();
		public HashSet<String> unknownOnFailure = new HashSet<String>();
		public HashSet<String> UIonFailure = new HashSet<String>();
		
		public HashSet<String> UIonSuccessForward = new HashSet<String>();
		public HashSet<String> noUIonSuccessForward = new HashSet<String>();
		
		public HashSet<String> UIonSuccessBackward = new HashSet<String>();
		public HashSet<String> noUIonSuccessBackward = new HashSet<String>();
		
	}
	
	public class Dynamic {
		public HashSet<String> essential = new HashSet<String>();
		public HashSet<String> optional = new HashSet<String>();
		public HashSet<String> unneeded = new HashSet<String>();
		public HashSet<String> notCalled = new HashSet<String>();
	}
				
		
		

}
