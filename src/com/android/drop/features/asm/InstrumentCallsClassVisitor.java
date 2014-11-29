package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
	

	
	private class InstrumentMethodAdapter extends BasicMethodAdapter {
		
		protected InstrumentMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, className, access, name, desc, Constants.INST_DEV_LOG_FILE, InstrumentCallsClassVisitor.this.instrumentationType);
		}
		
		@Override
        protected void onMethodEnter() {
		  Method m = new Method(methodSigniture);
		  ds.addMethod(m);
		  //System.out.println("Entering " + methodSigniture);
		  AsmUtils.addPrintoutStatement(mv, Constants.INST_DEV_LOG_FILE, instrumentationType, Constants.LOG_MARKER + methodSigniture, 2);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
			String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		
			if (isConnection(owner, name)) { 
				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
						"CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
				AsmUtils.addPrintStackTrace(mv);
			}
		}
    };
	

    
}
