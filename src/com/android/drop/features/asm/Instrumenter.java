package com.android.drop.features.asm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import soot.jimple.toolkits.callgraph.CallGraph;

import com.android.drop.features.data.CallGraphWrapper;
import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;

public class Instrumenter {
	
	private static final String CLASS_FILE_REGEX = "[^\\s]+\\.class$";
	private static final String INSTRUMENT = "instrument";
	private static final String FILTER = "filter";
	private static final String PREPROCESS = "preprocess";
	
	public static DataStructure ds = new DataStructure();
		
	public static void main(String[] args) {
		
		
		//Utils.runSystemCommand("/usr/local/bin/asmPrepare " + Constants.APP_NAME);
		
		if (args.length < 1) {
			System.err.println("Provide instrumentation type");
			return;
		}
			
		String instrumentationType = args[0];
		if (!(INSTRUMENT.equals(instrumentationType) || FILTER.equals(instrumentationType))) {        	
        	System.err.println("Instrumentation type should be either " + INSTRUMENT + " or " + FILTER);
			return;
        }
		
		if (FILTER.equals(instrumentationType)) {
			ds.readFromFile(Constants.DATA_FILE);
			ds.processExecutionLog(Constants.EXECUTED_LOG_FILE);
			
			//build class hierarchy
			ClassHierarchy.getInstance().reset();
			loopOverFiles(PREPROCESS);
			
			//build call graph
			//CallGraph cg = CallGraphWrapper.getInstance().getSootCallGraph();
		}
		
		loopOverFiles(instrumentationType);
		
		
		if (INSTRUMENT.equals(instrumentationType)) {
			ds.dumpToFile(Constants.DATA_FILE);             		    
		}
		else {
			ds.dumpToFile(Constants.FILTER_DATA_FILE);           
		}
		Utils.runSystemCommand("/usr/local/bin/asmPack " + Constants.APP_NAME + " " + instrumentationType);
	    //Utils.runSystemCommand("/usr/local/bin/deploy " + Constants.APP_NAME + " " + instrumentationType);
	}

	private static void loopOverFiles(String instrumentationType) {
		File inputClassFolder = new File(Constants.DECOMPILED_INPUT_DIR + Constants.APP_NAME);
		File instrumentedClassFolder = new File(Constants.ASM_DECOMPILED_OUTPUT_DIR + Constants.APP_NAME + "_" + instrumentationType);
			
		Collection<File> classFiles = FileUtils.listFiles(inputClassFolder, new RegexFileFilter(CLASS_FILE_REGEX), DirectoryFileFilter.DIRECTORY);

		try {
			for (File classFile : classFiles) {
				System.out.println(instrumentationType + " " + classFile);
							
				byte[] inBytes = FileUtils.readFileToByteArray(classFile);
				
				final ClassReader classReader = new ClassReader(inBytes);
	            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

	            ClassVisitor cv = null;
	            
	            if (INSTRUMENT.equals(instrumentationType)) {
	            	cv = new InstrumentCallsClassVisitor(classWriter, ds, instrumentationType);
	            }
	            else if (FILTER.equals(instrumentationType)) {
	            	cv = new FilterCallsClassVisitor(classWriter, ds, instrumentationType);
	            }
	            else if (PREPROCESS.equals(instrumentationType)) {
	            	cv = new PreprocessClassVisitor(); 
	            }
	           
	            classReader.accept(cv, 0);
	            
	            if (!PREPROCESS.equals(instrumentationType)) {
	            	final byte[] outBytes = classWriter.toByteArray();
		            
					File instrumentedClassFile = new File(classFile.getPath().replace(inputClassFolder.getPath(), instrumentedClassFolder.getPath()));
					FileUtils.writeByteArrayToFile(instrumentedClassFile, outBytes);
	            }	            
			} 
			
			if (!PREPROCESS.equals(instrumentationType)) {
				FileUtils.copyFileToDirectory(new File(Constants.BASE_DIR + "XXX_Utils.class"), new File(instrumentedClassFolder.getPath() + "/julia/"));
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
