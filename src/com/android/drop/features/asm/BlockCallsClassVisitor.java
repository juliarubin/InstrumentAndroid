package com.android.drop.features.asm;

import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.data.Constants;
import com.android.drop.features.data.ExecutedMethodsManager;

public class BlockCallsClassVisitor extends BasicClassVisitor {
	
	BlockCallsClassVisitor(ClassVisitor classvisitor, ExecutedMethodsManager ds, String instrumentationType) {
		super(classvisitor, ds, instrumentationType);
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
			
		if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return classWriterVisitor.visitMethod(access, name, desc, signature, exceptions);
        }
				
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature, exceptions);
		BlockMethodAdapter methodAdapter = new BlockMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);

		return methodAdapter;      
    }
	
	private class BlockMethodAdapter extends BasicMethodAdapter {
		public BlockMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, className, access, name, desc, Constants.FILTER_DEV_LOG_FILE, BlockCallsClassVisitor.this.instrumentationType);
		}
		
		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			
			//TODO add statement count for identical calls within the same method
		
			
//			if ((owner.equals("android/net/NetworkInfo") && name.equals("isConnected"))) {
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"BLOCK CHECK CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
//				AsmUtils.addPrintStackTrace(mv);
//				//mv.visitInsn(ICONST_0);
//				return;
//			}
//			
//			String stat = "STILL";
			
//			//julia debug
//			if (
//					(owner.startsWith("android/view/ViewGroup") && name.startsWith("addView")) ||
//					(owner.startsWith("android/os/Parcel") && (name.startsWith("obtain") || name.startsWith("readStrongBinder")))
//				) {
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"exec " + owner + "." + name + desc + " from " + methodSigniture, 2);
//				AsmUtils.addPrintStackTrace(mv);
//				
//			}
			
			if (!Instrumenter.statementsToBlock.containsKey(methodSigniture)) {
				return;
			}

			List<String> statementsToBlockInMethod = Instrumenter.statementsToBlock.get(methodSigniture);
			
			boolean found = false;
			for (String s : statementsToBlockInMethod) {
				if (s.equals(owner + "." + name+desc)) {
					found = true;
					break;
				}
			}

			if (!found) {
				return;
			}
			
			//now block
			
			AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
					"BLOCKED " + owner + "." + name + desc + " from " + methodSigniture, 2);
			//AsmUtils.addPrintStackTrace(mv);
			
			
			//if the call knows to throw an exception --> do that
			//if the call is within a try block --> throw any catched exception
			//if method knows to throw exceptions --> throw any of these exceptions
			//otherwise, just exit the method
			
			if ((owner.equals("java/net/URL") && name.equals("openConnection")) ||
			    (owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
			    (owner.equals("org/apache/http/impl/client/AbstractHttpClient") && name.equals("execute")) ||
			    (owner.equals("org/apache/http/impl/client/DefaultHttpClient") && name.equals("execute"))) {
				mv.visitTypeInsn(NEW, "java/io/IOException");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("BLOCKED CONNECT");
				mv.visitMethodInsn(INVOKESPECIAL, "java/io/IOException", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitInsn(ATHROW);
			}
			else if (owner.equals("android/os/Parcel") && name.equals("obtain")) {
				mv.visitVarInsn(ASTORE, 1);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, "android/os/Parcel", "recycle", "()V", false);
				
				String returnType = methodSigniture.substring(methodSigniture.lastIndexOf(')')+1, methodSigniture.length());
				AsmUtils.addReturnStatement(mv, returnType);	
			}
//			else if (owner.equals("java/net/URL") && name.equals("openStream")) {
//				
//			}
//			else if (owner.equals("android/webkit/WebView") && name.equals("loadUrl")) {
//				
//			}
//			else if (owner.equals("java/net/DatagramSocket") && name.equals("send")) {
//				
//			}
//			else if (owner.equals("OpenSSLSocketImpl$SSLOutputStream") && name.equals("write")) {
//				
//			}
//			else if (owner.equals("java.net.Socket") && name.equals("connect")) {
//				
//			}
//			else if (owner.equals("libcore.io.Posix") && name.equals("sendto")) {
//				
//			}
//			else if (owner.equals("libcore.io.Posix") && name.equals("sendtoBytes")) {
//				
//			}
//			
//			
			
		}
	}
}
