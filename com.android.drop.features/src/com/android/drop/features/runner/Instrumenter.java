package com.android.drop.features.runner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.android.drop.features.asm.BlockCallsClassVisitor;
import com.android.drop.features.asm.FilterCallsClassVisitor;
import com.android.drop.features.asm.InstrumentCallsClassVisitor;
import com.android.drop.features.asm.PreprocessClassVisitor;
import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataManager;

public class Instrumenter {
	
	private static final String CLASS_FILE_REGEX = "[^\\s]+\\.class$";
	private static final String INSTRUMENT = "instrument";
	private static final String FILTER = "filter";
	private static final String BLOCK = "block";
	private static final String PREPROCESS = "preprocess";
		
	public static DataManager dm = new DataManager();
	
	public static void main(String[] args) {
		
		
//		Utils.runSystemCommand(Constants.SCRIPTS_DIR + "asmPrepare " + Constants.APP_NAME);
		
		long start = System.currentTimeMillis();
		
		if (args.length < 1) {
			System.err.println("Provide instrumentation type");
			return;
		}
			
		String instrumentationType = args[0];
		if (! (INSTRUMENT.equals(instrumentationType) || FILTER.equals(instrumentationType) || BLOCK.equals(instrumentationType)) ) {        	
        	System.err.println("Instrumentation type should be either " + INSTRUMENT + ", " + FILTER + " or " + BLOCK);
			return;
        }
		
		//build class hierarchy
		ClassHierarchy.getInstance().reset();
		loopOverFiles(PREPROCESS);
		
		//build call graph
		//CallGraph cg = CallGraphWrapper.getInstance().getSootCallGraph();
		
		if (instrumentationType.equals(FILTER)) {
			dm.readMethodsFromFile(Constants.METHOD_DATA_FILE);
		}
		
		if (instrumentationType.equals(BLOCK)) {
			dm.readStatementsToBlockFromFile(Constants.STATEMENTS_TO_BLOCK_FILE);
		}
		
		loopOverFiles(instrumentationType);
		
		
		if (instrumentationType.equals(INSTRUMENT) || instrumentationType.equals(FILTER)) {
			dm.dumpMethodsToFile(Constants.METHOD_DATA_FILE); 
			dm.dumpStatementsToFile(Constants.STATEMENT_DATA_FILE);
		}

		long end = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
		System.out.println("Execution time is " + formatter.format(end - start) + " m:ss:SSS");
		System.out.println(instrumentationType + " execution time is " + (end - start) + " ms");
		
		Utils.runSystemCommand(Constants.SCRIPTS_DIR + "asmPack " + Constants.APP_NAME + " " + instrumentationType);
	    Utils.runSystemCommand(Constants.SCRIPTS_DIR + "deploy " + Constants.APP_NAME + " " + instrumentationType);
	}

	private static void loopOverFiles(String instrumentationType) {
		File inputClassFolder = new File(Constants.DECOMPILED_INPUT_DIR + Constants.APP_NAME);
		File instrumentedClassFolder = new File(Constants.ASM_DECOMPILED_OUTPUT_DIR + Constants.APP_NAME + "_" + instrumentationType);
			
		Collection<File> classFiles = FileUtils.listFiles(inputClassFolder, new RegexFileFilter(CLASS_FILE_REGEX), DirectoryFileFilter.DIRECTORY);

		try {
			for (File classFile : classFiles) {
				//System.out.println(instrumentationType + " " + classFile);
							
				byte[] inBytes = FileUtils.readFileToByteArray(classFile);
				
				final ClassReader classReader = new ClassReader(inBytes);
	            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

	            ClassVisitor cv = null;
	            
	            if (INSTRUMENT.equals(instrumentationType)) {
	            	cv = new InstrumentCallsClassVisitor(classWriter, instrumentationType);	            	
	            }
	            else if (FILTER.equals(instrumentationType)) {
	            	cv = new FilterCallsClassVisitor(classWriter, instrumentationType);
	            }
	            else if (BLOCK.equals(instrumentationType)) {
	            	cv = new BlockCallsClassVisitor(classWriter, instrumentationType);
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
