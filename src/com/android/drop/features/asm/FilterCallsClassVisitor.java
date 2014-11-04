package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;

public class FilterCallsClassVisitor extends BasicClassVisitor {
    
	FilterCallsClassVisitor(ClassVisitor classvisitor, DataStructure ds, String instrumentationType) {
		super(classvisitor, ds, instrumentationType);
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
		
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature,
                exceptions);
        
        if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return mv;
        }

        String methodSigniture = this.name + "." + name +  desc;
        
        if (ds.getMethod(methodSigniture) == null) {
			System.err.println("The method from an app is not found in data structure: " + methodSigniture);	
			return mv;
	     }
	     else if (ds.getMethod(methodSigniture).getCalled() || "<init>".equals(name) || "uncaughtException".equals(name)) {
	    	 //debug
	    	 addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, "LEFT_CALL " + methodSigniture, 0);
	    	 
	    	 //debug
		     if (this.name.equals("DrawerController") && name.contains("init")) {
		    	 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
		     }
	    	 
	    	 return mv;
	     }
	     else {
	    	addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, Constants.FILTERED_MARKER + methodSigniture, 0);
		    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
		     
		    int i =  desc.lastIndexOf(')');
		    String returnType = desc.substring(i+1, desc.length());
		    if ("V".equals(returnType)) {
		    	mv.visitInsn(Opcodes.RETURN);
		    }
		    else if ("I".equals(returnType) || "Z".equals(returnType) || "B".equals(returnType) || 
		    		 "C".equals(returnType) || "S".equals(returnType)) {
		    	mv.visitInsn(Opcodes.ICONST_0);
		    	mv.visitInsn(Opcodes.IRETURN);
		    }
		    else if ("L".equals(returnType) || "J".equals(returnType)) {
		    	mv.visitInsn(Opcodes.LCONST_0);
		    	mv.visitInsn(Opcodes.LRETURN);
		    }
		    else if ("F".equals(returnType)) {
		    	mv.visitInsn(Opcodes.FCONST_0);
		    	mv.visitInsn(Opcodes.FRETURN);
		    }
		    else if ("D".equals(returnType)) {
		    	mv.visitInsn(Opcodes.DCONST_0);
		    	mv.visitInsn(Opcodes.DRETURN);
		    }
		    else {
		    	mv.visitInsn(Opcodes.ACONST_NULL);
		    	mv.visitInsn(Opcodes.ARETURN);
		    }
		
		     //return super.visitMethod(access, name, desc, signature, exceptions);
		     return mv;
	    			 //FilterMethodAdapter(Opcodes.ASM5, new MethodWrite(Opcodes.ASM5, classWriterVisitor), this.name, access, name, desc);
	     }
        
    }
}
