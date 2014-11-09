package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
		
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature,
                exceptions);
		
		if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) > 0) {
            return mv;
        }
		
		//this part instruments all connection requests
		FilterMethodAdapter methodAdapter = new FilterMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);
        
        String methodSigniture = this.name + "." + name +  desc;
        
        if (ds.getMethod(methodSigniture) == null) {
			System.err.println("The method from an app is not found in data structure: " + methodSigniture);	
			//return mv;
			return methodAdapter;
	     }
	     else if (ds.getMethod(methodSigniture).getCalled() || "<init>".equals(name) || "uncaughtException".equals(name)) {
//	    	 //debug
//	    	 AsmUtils.addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, "LEFT_CALL " + methodSigniture, 0);
//	    	 
//	    	 //debug
//		     if (this.name.equals("DrawerController") && name.contains("init")) {
//		    	 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
//		     }	    	 
	    	//return mv;
			return methodAdapter;
	     }
	     else {
	    	ClassHierarchy hierarchy = ClassHierarchy.getInstance();
	    	String printMarker =  Constants.FILTERED_MARKER;
	    			    
		    if (("onClick".equals(name) && "(Landroid/view/View;)V".equals(desc)) ||
		    	("onFocusChange".equals(name) && "(Landroid/view/View;Z)V".equals(desc)) ||
		    	("onEditorAction".equals(name) && "(Landroid/widget/TextView;ILandroid/view/KeyEvent;)Z".equals(desc))) {
		    	AsmUtils.addExcludedPopupForView(mv);
		    	printMarker = "MODIFIED CALL ";
		    }
		    //FIXME walmart search menu
//		    else if (("onMenuItemSelected".equals(name) && "(ILandroid/view/MenuItem;)Z".equals(desc))) {
//		    	addExcludedPopupForMenu(mv);
//		    	printMarker = "MODIFIED CALL ";
//		    }
		    else if (("onCreate".equals(name) || "onDestroy".equals(name) || "onPause".equals(name) || "onPostCreate".equals(name) ||
		    	"onPostResume".equals(name) || "onRestart".equals(name) || "onResule".equals(name) || "onStart".equals(name) ||
		    	"onStop".equals(name) || "onAttach".equals(name)) && 
		    	hierarchy.isAncestors(this.name, "android/app/Activity", 0)) {
		    		    	
		    	AsmUtils.addCallToSuper(mv, name, desc, this.superName);
		    	printMarker = "MODIFIED CALL ";
		    }
		    	
		    AsmUtils.addPrintoutStatement(mv, Constants.FILTER_DEV_LOG_FILE, instrumentationType, printMarker + methodSigniture, 0);
		    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "dumpStack", "()V", false);
		    
			String returnType = desc.substring(desc.lastIndexOf(')')+1, desc.length());
			AsmUtils.addReturnStatement(mv, returnType);
		
		    //return mv;
			return methodAdapter;
		    // return new FilterMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);
	     }
        
    }
	
	private class FilterMethodAdapter extends BasicMethodAdapter {
		public FilterMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, className, access, name, desc, Constants.FILTER_DEV_LOG_FILE, FilterCallsClassVisitor.this.instrumentationType);
		}
	}
}
