package com.android.drop.features.soot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.DoubleType;
import soot.FloatType;
import soot.LongType;
import soot.PackManager;
import soot.PatchingChain;
import soot.PrimType;
import soot.RefLikeType;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.options.Options;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;
import com.android.drop.features.data.Method;

public class FilterUnused {

	public static DataStructure ds = new DataStructure();
	
	
	public static void main(String[] args) {
		ds.readFromFile(Constants.DATA_FILE);
		processExecutionLog(Constants.LOGCAT_FILE);
		// ds.printUnusedMethods();
		// ds.printUsedMethods();

		// prefer Android APK files// -src-prec apk
		Options.v().set_src_prec(Options.src_prec_apk);

		// output as APK, too//-f J
		Options.v().set_output_format(Options.output_format_dex);
		
		//Options.v().set_android_jars(Constants.SOOT_ANDROID_PLATFORM); soot needs at least one command line parameter, otherwise does not work
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(Arrays.asList(Constants.SOOT_INPUT_DIR + Constants.APP_NAME + ".apk"));
		Options.v().set_output_dir(Constants.SOOT_OUTPUT_DIR);

		// resolve the PrintStream and System soot-classes
		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);

	    PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter",
						new FilterMethodsTransformer()));
		try {
			soot.Main.main(args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	private static void processExecutionLog(String filename) {
		try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));	       
	        String line = br.readLine();

	        while (line != null) {	          
	            String methodData[] = line.split(Constants.LOG_MARKER);
	            if (methodData.length != 2) {
	            	line = br.readLine();
	            	continue;
	            }	            
	            String signiture = methodData[1];	         
	            Method method = ds.getMethod(signiture);
	            if (method == null) {
	            	System.err.println("Method executed but not in the data structure: " + signiture);
	            }
	            else {
	            	method.setCalled(true);
	            }	            
	            line = br.readLine();
	        }
	        
	        br.close();
	    } 
	    catch (IOException e) {
			e.printStackTrace();
		}	    	 
		
	}
	
	
	 private static class FilterMethodsTransformer extends BodyTransformer {

			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {				

				String signiture = b.getMethod().getSignature();
				if (ds.getMethod(signiture) == null) {
					System.err.println("The method from an app is not found in data structure: " + signiture);				
				}
				else if (!ds.getMethod(signiture).getCalled() && !b.getMethod().isConstructor()) {
					//System.out.println(m.getSigniture());
							
							
					final PatchingChain<Unit> units = b.getUnits();
				
					/*
					JReturnStmt returnStatement = null; 					
					
					Unit u = null;
					Iterator<Unit> iter = units.snapshotIterator();
					while (iter.hasNext()) {														
						u = iter.next();						
						if (u instanceof JReturnStmt) { 
							returnStatement = (JReturnStmt) u;							
						}
						units.remove(u);
					}
					*/
					
					units.clear();
			        b.getTraps().clear();
			        
					Type returnType = b.getMethod().getReturnType();
					
					if (returnType instanceof VoidType)  {
						units.addLast(Jimple.v().newReturnVoidStmt());
  		            }
					else if (returnType instanceof RefLikeType)  {
						units.addLast(Jimple.v().newReturnStmt(NullConstant.v()));
  		            }
					else if (returnType instanceof DoubleType) {
		            	units.addLast(Jimple.v().newReturnStmt(DoubleConstant.v(0)));
		            }
					else if (returnType instanceof FloatType) {
		            	units.addLast(Jimple.v().newReturnStmt(FloatConstant.v(0)));
		            }
					else if (returnType instanceof LongType) {
		            	units.addLast(Jimple.v().newReturnStmt(LongConstant.v(0)));
		            }
  		            else if (returnType instanceof PrimType) {
  		            	units.addLast(Jimple.v().newReturnStmt(IntConstant.v(0)));
  		            }  		          
  		            else {
  		              System.err.println("Unknown return type " + returnType);
  		            }
  		            	
  		            
			        ;
/*			        
			        if (returnStatement != null) {					
						Type type = returnStatement.getOp().getType();						
						System.out.println(returnStatement.getOp().getType().getClass());
						System.out.println();
						NullConstant.
						((Type) returnStatement.getOp().getType().getClass()).
						

						//units.addLast(returnStatement);						
						//units.addLast(new JReturnStmt(NullConstant.v()));
					}
					else {
						units.addLast(Jimple.v().newReturnVoidStmt());
					}
					
*/							        
				}
				/*
				else {
					//do nothing, but need it to create previously "empty" methods
					final PatchingChain<Unit> units = b.getUnits();
					for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {														
						Unit u = iter.next();
						u.apply(new AbstractStmtSwitch() {							
							public void caseInvokeStmt(InvokeStmt stmt) {
							}								
						});				
					}	
				}
					*/
				
				//check that we did not mess up the Jimple	
				b.validate();
								
		        
			}
		} //FilterMethodsTransformer class
			


}
