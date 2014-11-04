package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;
import com.android.drop.features.data.Method;

public class InstrumentCallsClassVisitor extends BasicClassVisitor {
    
	InstrumentCallsClassVisitor(ClassVisitor classvisitor, DataStructure ds, String instrumentationType) {
		super(classvisitor, ds, instrumentationType);
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
        
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature,
                exceptions);

        if (mv == null
                || (access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return mv;
        }

        return new InstrumentMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);
    }
	

	
	private class InstrumentMethodAdapter extends AdviceAdapter {
		
		protected String className;
		protected String name;
		protected String desc;
		protected String methodSigniture;
		
		protected InstrumentMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, access, name, desc);
	        this.className = className;
	        this.name = name;
	        this.desc = desc;
	        methodSigniture = className + "." + name +  desc;
		}
		
		@Override
        protected void onMethodEnter() {
		  Method m = new Method(methodSigniture);
		  ds.addMethod(m);
		  //System.out.println("Entering " + methodSigniture);
		  addPrintoutStatement(mv, Constants.INST_DEV_LOG_FILE, instrumentationType, Constants.LOG_MARKER + methodSigniture);

/*
 *   // insert GETSTATIC
		  super.visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC,
                                                           "java/lang/System", // field class
                                                           "out",              // field name
                                                           "Ljava/io/PrintStream;"); // field type
		  
          // insert ldc "Am in method xxx"
          final String str = Constants.LOG_MARKER + methodSigniture;
          super.visitLdcInsn(str);
          
          // insert invokevirtual
          super.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
                                                          "java/io/PrintStream", // class name
                                                          "println",             // method name
                                                           "(Ljava/lang/String;)V", false); // desc
        }
 */
        }
    };
	

    
}
