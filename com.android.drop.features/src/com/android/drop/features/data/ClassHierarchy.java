package com.android.drop.features.data;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class ClassHierarchy {
	private static ClassHierarchy instance = null;
	private Map<String, Ancestors> hierarchy;
	
	
	private ClassHierarchy() {
		hierarchy = new TreeMap<String, Ancestors>();
	}
	
	public static ClassHierarchy getInstance() {
		if (instance == null) {
			instance = new ClassHierarchy();
		}
		return instance;
	}
	
	public void reset() {
		hierarchy = new TreeMap<String, Ancestors>();
	}
	
	/* flag==0 --> super class
	 * flag==1 --> interface
	 */
	public void add(String className, String superName, int flag) {
		Ancestors a; 
		if (hierarchy.containsKey(className)) {
			a = hierarchy.get(className);
		}
		else {
			a = new Ancestors();
			hierarchy.put(className, a);
		}
		if (flag == 0) {
			a.superClass = superName;
		}
		else {
			a.interfaces.add(superName);
		}
	}
	
	/* flag==0 --> super class
	 * flag==1 --> interface
	 */
	public boolean isAncestors(String className, String superName, int flag) {
		if (flag == 0) {
			if (!hierarchy.containsKey(className) || hierarchy.get(className).superClass == null) {
				return false;
			}
			if (hierarchy.get(className).superClass.equals(superName)) {
				return true;
			}
			else {
				return isAncestors(hierarchy.get(className).superClass, superName, flag);
			}
		}
		else {
			if (!hierarchy.containsKey(className) || hierarchy.get(className).interfaces.isEmpty()) {
				return false;
			}
			else {
				for (String s: hierarchy.get(className).interfaces) {
					if (s.equals(superName)) {
						return true;
					}
					else if (isAncestors(s, superName, flag)) {
						return true;
					}
				}
				return false;
			}
		}
	}
	
	private class Ancestors {
		public String superClass;
		public HashSet<String> interfaces = new HashSet<String>();
	}

	

}
