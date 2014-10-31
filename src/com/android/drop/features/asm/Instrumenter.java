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

import com.android.drop.features.data.Constants;

public class Instrumenter {
	
	private static final String CLASS_FILE_REGEX = "[^\\s]+\\.class$";
		
	public static void main(String[] args) {
		
		
		//sUtils.runSystemCommand("/usr/local/bin/asmPrepare " + Constants.APP_NAME);
		
		if (args.length < 1) {
			System.err.println("Provide instrumentation type");
			return;
		}
		
		String instrumentationType = args[0];
		
		File classFolder = new File(Constants.ASM_DECOMPILED_INPUT_DIR + Constants.APP_NAME);
		File instrumentedClassFolder = new File(Constants.ASM_DECOMPILED_OUTPUT_DIR + Constants.APP_NAME);
			
		Collection<File> classFiles = FileUtils.listFiles(classFolder, new RegexFileFilter(CLASS_FILE_REGEX), DirectoryFileFilter.DIRECTORY);

		for (File classFile : classFiles) {
			System.out.println("Transforming " + classFile);
			try {
								
				byte[] inBytes = FileUtils.readFileToByteArray(classFile);
				
				final ClassReader classReader = new ClassReader(inBytes);
	            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

	            ClassVisitor cv = null;
	            
	            if (instrumentationType.equals("instrumented")) {
	            	cv = new InstrumentCallsClassVisitor(classWriter);
	            }
	            else if (instrumentationType.equals("filtered")) {
	            	//cv = 
	            }
	            else {
	            	System.err.println("Instrumentation type should be either instrumented or filtered");
	    			return;
	            }
	            
	            classReader.accept(cv, 0);
	                        
	            final byte[] outBytes = classWriter.toByteArray();
	            
				File instrumentedClassFile = new File(classFile.getPath().replace(classFolder.getPath(), instrumentedClassFolder.getPath()));
				FileUtils.writeByteArrayToFile(instrumentedClassFile, outBytes);								
			} 
			catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		Utils.runSystemCommand("/usr/local/bin/asmPack " + Constants.APP_NAME + " instrumented");
		Utils.runSystemCommand("/usr/local/bin/deploy " + Constants.APP_NAME + " instrumented");
		
	}
	
}
