
package com.android.drop.features.soot;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.Unit;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.options.Options;
*/
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.ExecutedMethodsManager;
import com.android.drop.features.data.Method;


public class InstrumentCalls {
	/*
	public static DataStructure ds = new DataStructure();
	
	public static void main(String[] args) {
		
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_process_dir(Collections.singletonList(Constants.SOOT_INPUT_DIR + Constants.APP_NAME + ".apk"));
		Options.v().set_output_dir(Constants.SOOT_OUTPUT_DIR);
		
//		Options.v().set_src_prec(Options.src_prec_class);				
//		Options.v().set_output_format(Options.output_format_class);
//		Options.v().set_process_dir(Collections.singletonList(Constants.SOOT_INPUT_DIR + "../decompiled/orig/" + Constants.APP_NAME + "_dex2jar.jar"));
//		Options.v().set_output_dir(Constants.SOOT_OUTPUT_DIR + Constants.APP_NAME + ".jar");
//		Options.v().set_output_jar(true);
		
		Options.v().set_allow_phantom_refs(true);
		//Options.v().set_android_jars(Constants.SOOT_ANDROID_PLATFORM); soot needs at least one command line parameter, otherwise does not work
		
		
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
			
//			//debug
			if (b.getMethod().getDeclaringClass().getName().equals("com.walmart.android.analytics.GoogleAnalytics") &&
					b.getMethod().getName().equals("start")) {
				System.out.println("JULIA HERE");
//				//look for $r1 = virtualinvoke $r4.<java.lang.Class: java.lang.String getSimpleName()>();
//				//and print it
//				while (iter.hasNext()) {														
//					u = iter.next();	
//					if (u instanceof JAssignStmt && 
//							((JAssignStmt) u).rightBox.getValue() instanceof JVirtualInvokeExpr) {	
//						JVirtualInvokeExpr expr = (JVirtualInvokeExpr) (((JAssignStmt) u).rightBox.getValue());
//						if (expr.getMethod().getName().equals("getSimpleName")) { 
//					        Unit u2 = Jimple.v().newAssignStmt(tmpString, ((JAssignStmt) u).leftBox.getValue());				                    
//					        	        
//					        // "tmpRef.println(tmpString);" 	    
//					        SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
//					        Unit u3 = Jimple.v().newInvokeStmt(
//				                    Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString));
//
//					        units.insertAfter(u2, u);
//					        units.insertAfter(u3, u2);
//							System.out.println("JULIA HERE");
//						}
//					}
//				}
			}

			//if constructor, search for super ()
			if (b.getMethod().isConstructor()) {
				iter = units.snapshotIterator();
				while (iter.hasNext()) {
					u = iter.next();		
					if (u instanceof JInvokeStmt && 
							((JInvokeStmt) u).getInvokeExprBox().getValue() instanceof JSpecialInvokeExpr) {					
							JSpecialInvokeExpr expr = (JSpecialInvokeExpr) (((JInvokeStmt) u).getInvokeExprBox().getValue());
						if (expr.getMethod().getName().equals("<init>") && expr.getBaseBox().getValue().equals(b.getLocals().getFirst())) {
							last = u;
							break;
						}						
					}
				}
			}
			
			List<Unit> printUnits = Util.generatePrintout(b, Constants.LOG_MARKER);
				      
	        if (last != null) {	        
	        	units.insertAfter(printUnits.get(0), last);
	        }
	        else {
	        	units.addFirst(printUnits.get(0));
	        }
	        
	        last = printUnits.get(0);
	        for (int i = 1; i < printUnits.size();i++) {
	        	units.insertAfter(printUnits.get(i), last);
	        	last = printUnits.get(i);
	        }
        		                    	    
	       // check that we did not mess up the Jimple
	        b.validate();

		}				
	} //LogMethodsTransformer class
		
*/
}