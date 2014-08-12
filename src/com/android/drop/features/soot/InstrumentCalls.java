
package com.android.drop.features.soot;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.options.Options;
import soot.util.Chain;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;
import com.android.drop.features.data.Method;


public class InstrumentCalls {
	
	public static DataStructure ds = new DataStructure();
	
	public static void main(String[] args) {
		
		//prefer Android APK files// -src-prec apk
		Options.v().set_src_prec(Options.src_prec_apk);
		
		//output as APK, too//-f J
		Options.v().set_output_format(Options.output_format_dex);		
		
		Options.v().set_output_dir(Constants.SOOT_OUTPUT_FILE);
        // resolve the PrintStream and System soot-classes
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new LogMethodsTransformer()));      
        try { 
        	soot.Main.main(args);
        }
		catch (Throwable t) {
			t.printStackTrace();
		}
        
		ds.dumpToFile(Constants.DATA_FILE);
	}

	    private static class LogMethodsTransformer extends BodyTransformer {

		@Override
		protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {				
			Method m = new Method(b.getMethod().getSignature());
			ds.addMethod(m);
			//System.out.println(m.getSigniture());
			
			Local tmpRef = addTmpRef(b);
			Local tmpString = addTmpString(b);
					
			final PatchingChain<Unit> units = b.getUnits();		
			Unit u = null;	
			Unit last = null;
			Iterator<Unit> iter = units.snapshotIterator();
			
			//skip all identity statements
			while (iter.hasNext()) {														
				u = iter.next();				
				if (u instanceof JIdentityStmt) {					
					continue; 
				}
				
				last = units.getPredOf(u);
				break;
			}

			//search for super ()
			iter = units.snapshotIterator();
			while (iter.hasNext()) {
				u = iter.next();		
				if (u instanceof JInvokeStmt && 
						((JInvokeStmt) u).getInvokeExprBox().getValue() instanceof JSpecialInvokeExpr) {					
					JSpecialInvokeExpr expr = (JSpecialInvokeExpr) (((JInvokeStmt) u).getInvokeExprBox().getValue());
					if (expr.getMethod().getName().equals("<init>")) {
						last = u;
					}
					break;
				}				
			}
			
			// "tmpRef = java.lang.System.out;" 
			Unit newUnit1 = Jimple.v().
					newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef( 
                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef()));					  

	        // "tmpLong = 'HELLO';" 
	        Unit newUnit2 = Jimple.v().newAssignStmt(tmpString, 
                    StringConstant.v(Constants.LOG_MARKER + b.getMethod().getSignature()));
	        	        
	        // "tmpRef.println(tmpString);" 	    
	        SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
	        Unit newUnit3 = Jimple.v().newInvokeStmt(
                    Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString));
	        
	        if (last != null) {	        
	        	units.insertAfter(newUnit1, last);
	        }
	        else {
	        	units.addFirst(newUnit1);
	        }
        	units.insertAfter(newUnit2, newUnit1); 	                    	    
        	units.insertAfter(newUnit3, newUnit2);
	        
	        //check that we did not mess up the Jimple
	        b.validate();					      
		}
	} //LogMethodsTransformer class
		
	private static Local addTmpRef(Body body) {
		Local tmpRef = Jimple.v().newLocal("tmpRef",
				RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		return tmpRef;
	}

	private static Local addTmpString(Body body) {
		Local tmpString = Jimple.v().newLocal("tmpString",
				RefType.v("java.lang.String"));
		body.getLocals().add(tmpString);
		return tmpString;
	} 
	    
}