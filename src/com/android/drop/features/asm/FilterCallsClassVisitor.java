package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import com.android.drop.features.data.ClassHierarchy;
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
			
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature, exceptions);
		FilterMethodAdapter methodAdapter = new FilterMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);
		if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return methodAdapter;
        }
		
		String methodSigniture = this.name + "." + name +  desc;
		
		if (ds.getMethod(methodSigniture) == null) {
			System.err.println("The method from an app is not found in data structure: " + methodSigniture);
			//leave the method as is
			return methodAdapter;
	     }
		
		if (ds.getMethod(methodSigniture).getCalled() || "<init>".equals(name) || "uncaughtException".equals(name)) {
	    	 //also leave the method as is
	    	 return methodAdapter;
	     }
		
		//now filter
		mv.visitCode();
		
	    ClassHierarchy hierarchy = ClassHierarchy.getInstance();
	    String printMarker =  Constants.FILTERED_MARKER;
	    			    
		if (("onClick".equals(name) && "(Landroid/view/View;)V".equals(desc)) ||
		  	("onFocusChange".equals(name) && "(Landroid/view/View;Z)V".equals(desc)) ||
		   	("onEditorAction".equals(name) && "(Landroid/widget/TextView;ILandroid/view/KeyEvent;)Z".equals(desc))) {
		   	AsmUtils.addExcludedPopupForView(mv);
		   	printMarker = "MODIFIED CALL ";
	    }
		 //FIXME walmart search menu
//		 else if (("onMenuItemSelected".equals(name) && "(ILandroid/view/MenuItem;)Z".equals(desc))) {
//		    	addExcludedPopupForMenu(mv);
//		    	printMarker = "MODIFIED CALL ";
//		 }
		 else if (("onCreate".equals(name) || "onDestroy".equals(name) || "onPause".equals(name) || "onPostCreate".equals(name) ||
		  	"onPostResume".equals(name) || "onRestart".equals(name) || "onResume".equals(name) || "onStart".equals(name) ||
	    	"onStop".equals(name) || "onAttach".equals(name)) && 
	    	hierarchy.isAncestors(this.name, "android/app/Activity", 0)) {		    		    	
			 AsmUtils.addCallToSuper(mv, name, desc, this.superName);
			 printMarker = "MODIFIED CALL ";
		 }
		    	
		AsmUtils.addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, printMarker + methodSigniture, 0);
		    
	    String returnType = desc.substring(desc.lastIndexOf(')')+1, desc.length());
		AsmUtils.addReturnStatement(mv, returnType);			
		
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	        
		return null;
        
    }
	
	private class FilterMethodAdapter extends BasicMethodAdapter {
		public FilterMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, className, access, name, desc, Constants.FILTER_DEV_LOG_FILE, FilterCallsClassVisitor.this.instrumentationType);
		}
		
		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
				String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			
//			if ((owner.equals("android/net/NetworkInfo") && name.equals("isConnected"))) {
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"BLOCK CHECK CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
//				AsmUtils.addPrintStackTrace(mv);
//				//mv.visitInsn(ICONST_0);
//				return;
//			}
//			
			//if (methodSigniture.startsWith("com/millennialmedia/")) {
			//	AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
			//			"exec " + owner + "." + name + " from " + methodSigniture, 2);
			//	return;
			//}
			if (!isConnection(owner, name)) {
				return;
			}
			
			String stat = "STILL";
//			
//			if ((owner.equals("java/net/URL") && name.equals("openConnection")) ||
//			    (owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
//			    (owner.equals("org/apache/http/impl/client/AbstractHttpClient") && name.equals("execute")) ||
//			    (owner.equals("org/apache/http/impl/client/DefaultHttpClient") && name.equals("execute"))) {
//				mv.visitTypeInsn(NEW, "java/io/IOException");
//				mv.visitInsn(DUP);
//				mv.visitLdcInsn("BLOCKED CONNECT");
//				mv.visitMethodInsn(INVOKESPECIAL, "java/io/IOException", "<init>", "(Ljava/lang/String;)V", false);
//				mv.visitInsn(ATHROW);
//				stat = "BLOCK";
//			}
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
			AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
					stat + " CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
			AsmUtils.addPrintStackTrace(mv);
		}
	}
}
