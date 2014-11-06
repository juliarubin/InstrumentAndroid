package com.android.drop.features.asm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.DataStructure;

public class FilterCallsClassVisitor extends BasicClassVisitor {
	
	public static String EXCLUDED = "Excluded";
    
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
//	    	 //debug
//	    	 addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, "LEFT_CALL " + methodSigniture, 0);
//	    	 
//	    	 //debug
//		     if (this.name.equals("DrawerController") && name.contains("init")) {
//		    	 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
//		     }	    	 
	    	 return mv;
	     }
	     else {
	    	ClassHierarchy hierarchy = ClassHierarchy.getInstance();
	    	String printMarker =  Constants.FILTERED_MARKER;
	    			    
		    if (("onClick".equals(name) && "(Landroid/view/View;)V".equals(desc)) ||
		    	("onFocusChange".equals(name) && "(Landroid/view/View;Z)V".equals(desc)) ||
		    	("onEditorAction".equals(name) && "(Landroid/widget/TextView;ILandroid/view/KeyEvent;)Z".equals(desc))) {
		    	addExcludedPopupForView(mv);
		    	printMarker = "MODIFIED CALL ";
		    }
		    //FIXME 
//		    else if (("onMenuItemSelected".equals(name) && "(ILandroid/view/MenuItem;)Z".equals(desc))) {
//		    	addExcludedPopupForMenu(mv);
//		    	printMarker = "MODIFIED CALL ";
//		    }
		    else if (("onCreate".equals(name) || "onDestroy".equals(name) || "onPause".equals(name) || "onPostCreate".equals(name) ||
		    	"onPostResume".equals(name) || "onRestart".equals(name) || "onResule".equals(name) || "onStart".equals(name) ||
		    	"onStop".equals(name)) && 
		    	hierarchy.isAncestors(this.name, "android/app/Activity", 0)) {
		    		    	
		    	addCallToSuper(mv, name, desc, this.superName);
		    	printMarker = "MODIFIED CALL ";
		    }
		    	
		    addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, printMarker + methodSigniture, 0);
		    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
		    
			String returnType = desc.substring(desc.lastIndexOf(')')+1, desc.length());
		    addReturnStatement(mv, returnType);
		
		     //return super.visitMethod(access, name, desc, signature, exceptions);
		     return mv;
	    			 //FilterMethodAdapter(Opcodes.ASM5, new MethodWrite(Opcodes.ASM5, classWriterVisitor), this.name, access, name, desc);
	     }
        
    }
	
	private void addExcludedPopupForView(MethodVisitor mv) {
		// adds: android.widget.Toast.makeText(getApplicationContext(), "Excluded", Toast.LENGTH_LONG).show();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		addToast(mv);
	}
	
	private void addExcludedPopupForMenu(MethodVisitor mv) {
		// adds: android.widget.Toast.makeText(item.getActionView().getContext().getApplicationContext(), "Excluded", android.widget.Toast.LENGTH_LONG).show();
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "android/view/MenuItem", "getActionView", "()Landroid/view/View;", true);
		addToast(mv);
	}

	private void addToast(MethodVisitor mv) {
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/view/View", "getContext", "()Landroid/content/Context;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/content/Context", "getApplicationContext", "()Landroid/content/Context;", false);
		mv.visitLdcInsn(EXCLUDED);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false);
	}
	
	private void addCallToSuper(MethodVisitor mv, final String name, final String desc, final String superClass) {
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		
		//calculate the number of parameters
		Pattern pattern = Pattern.compile("\\[*L[^;]+;|\\[[ZBCSIFDJ]|[ZBCSIFDJ]"); //Regex for desc \[*L[^;]+;|\[[ZBCSIFDJ]|[ZBCSIFDJ]
		Matcher matcher = pattern.matcher(desc);
		int counter = 0;
		while(matcher.find()) {
			counter++;
		}
		
		for (int i = 1; i <= counter; i++) {
			mv.visitVarInsn(Opcodes.ALOAD, i);
		}
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, name, desc, false);
	}

	private void addReturnStatement(MethodVisitor mv, final String returnType) {
		
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
	}


}
