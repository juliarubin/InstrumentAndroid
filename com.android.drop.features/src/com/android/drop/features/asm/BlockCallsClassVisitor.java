package com.android.drop.features.asm;

import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.runner.Instrumenter;

public class BlockCallsClassVisitor extends BasicClassVisitor {
	
	public BlockCallsClassVisitor(ClassVisitor classvisitor, String instrumentationType) {
		super(classvisitor, instrumentationType);
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
			
		if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return classWriterVisitor.visitMethod(access, name, desc, signature, exceptions);
        }
				
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature, exceptions);
		
//		//julia debug
//				if (
//						(this.name.startsWith("com/google/android/gms/internal/v") && name.startsWith("af")) 
//					) {
//					AsmUtils.addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, 
//							"HERE " + this.name + "." + name + desc + " from " + signature, 2);
//					AsmUtils.addPrintStackTrace(mv);
//					
//				}
		
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
			//super.visitMethodInsn(opcode, owner, name, desc, itf);
			
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
			
			
			
			HashMap<String, List<String>> statementsToBlock = Instrumenter.dm.getStatementsToBlock();
			if (!statementsToBlock.containsKey(methodSigniture)) {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
				
//				//julia debug
//				if (
//						(owner.startsWith("com/google/android/gms/internal/v") && name.startsWith("af")) 
//					) {
//					AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//							"HERE " + owner + "." + name + desc + " from " + methodSigniture, 2);
//					AsmUtils.addPrintStackTrace(mv);
//					
//				}
				
				return;
			}

			List<String> statementsToBlockInMethod = statementsToBlock.get(methodSigniture);
			
			boolean found = false;
			for (String s : statementsToBlockInMethod) {
				if (s.equals(owner + "." + name+desc)) {
					found = true;
					break;
				}
			}
			
			boolean toBlock = false;

			if (!found || !toBlock) {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
				return;
			}

			//now block
			
			AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
					"BLOCKED " + owner + "." + name + desc + " from " + methodSigniture, 2);
			AsmUtils.addPrintStackTrace(mv);
			
			
			//if the call knows to throw an exception --> do that
			//if the call is within a try block --> throw any catched exception
			//if method knows to throw exceptions --> throw any of these exceptions
			//otherwise, just exit the method
			
			if ((owner.equals("java/net/URL") && name.equals("openConnection")) ||
				(owner.equals("java/net/URL") && name.equals("openStream")) ||
			   (ClassHierarchy.getInstance().isAncestors(owner, "java/net/URLConnection") && name.equals("connect")) ||
		       (ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpURLConnection") && name.equals("connect")) ||
		    	(ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpsURLConnection") && name.equals("connect")) ||
				(ClassHierarchy.getInstance().isAncestors(owner, "java/net/JarURLConnection") && name.equals("connect")) ||
				(owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
			    (owner.equals("org/apache/http/impl/client/AbstractHttpClient") && name.equals("execute")) ||
			    (owner.equals("org/apache/http/impl/client/DefaultHttpClient") && name.equals("execute")) ||
			    (owner.equals("java/net/Socket") && name.equals("getOutputStream")) ||
				(owner.equals("javax/net/ssl/SSLSocket") && name.equals("getOutputStream")) ||
				(owner.equals("org/apache/harmony/xnet/provider/jsse/OpenSSLSocketImpl") && name.equals("getOutputStream")))
				{
					super.visitMethodInsn(opcode, owner, name, desc, itf);
					mv.visitTypeInsn(NEW, "java/io/IOException");
					mv.visitInsn(DUP);
					mv.visitLdcInsn("BLOCKED CONNECT");
					mv.visitMethodInsn(INVOKESPECIAL, "java/io/IOException", "<init>", "(Ljava/lang/String;)V", false);
					mv.visitInsn(ATHROW);
					return;
			}
//			if (owner.equals("android/os/Parcel") && name.equals("obtain")) {
//				mv.visitVarInsn(ASTORE, 1);
//				mv.visitVarInsn(ALOAD, 1);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "android/os/Parcel", "recycle", "()V", false);
//				
//				String returnType = methodSigniture.substring(methodSigniture.lastIndexOf(')')+1, methodSigniture.length());
//				AsmUtils.addReturnStatement(mv, returnType);	
//			    return;
//			}
			if (ClassHierarchy.getInstance().isAncestors(owner, "android/os/IBinder") && name.equals("transact")) {
				mv.visitTypeInsn(NEW, "android/os/RemoteException");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("BLOCKED TRANSACT");
				mv.visitMethodInsn(INVOKESPECIAL, "android/os/RemoteException", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitInsn(ATHROW);
				return;
			}
//			if (ClassHierarchy.getInstance().isAncestors(owner, "android/content/Context") && name.equals("startService")) {
//				mv.visitTypeInsn(NEW, "java/lang/SecurityException");
//				mv.visitInsn(DUP);
//				mv.visitLdcInsn("BLOCKED START SERVICE");
//				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/SecurityException", "<init>", "(Ljava/lang/String;)V", false);
//				mv.visitInsn(ATHROW);
//				return;
//			}
			
//			if (owner.equals("android/webkit/WebView") && name.equals("loadUrl")) {
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
