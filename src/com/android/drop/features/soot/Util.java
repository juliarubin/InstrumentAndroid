package com.android.drop.features.soot;

import java.util.ArrayList;
import java.util.List;

/*
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
*/

public class Util {
	/*
	public static List<Unit> generatePrintout(final Body b, String stringToPrint) {
		
		ArrayList<Unit> result = new ArrayList<Unit>();
		Local tmpRef = Util.addTmpRef(b);
		Local tmpString = Util.addTmpString(b);
		
		// "tmpRef = java.lang.System.out;" 
		result.add(0, 
				Jimple.v().
				newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef( 
                Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));					  

        // "tmpString = stringToPrint;" 
		result.add(1,
				Jimple.v().newAssignStmt(tmpString, 
                StringConstant.v(stringToPrint + b.getMethod().getSignature())));
        	        
        // "tmpRef.println(tmpString);" 	    
        SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
        result.add(2, 
        		Jimple.v().newInvokeStmt(
                Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)));
        
        return result;
	}
	
	public static Local addTmpRef(Body body) {
		Local tmpRef = Jimple.v().newLocal("tmpRef",
				RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		return tmpRef;
	}

	public static Local addTmpString(Body body) {
		Local tmpString = Jimple.v().newLocal("tmpString",
				RefType.v("java.lang.String"));
		body.getLocals().add(tmpString);
		return tmpString;
	} 
	  */  

}
