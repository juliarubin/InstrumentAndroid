package com.android.detect.unhandled;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.asm.AsmUtils;
import com.android.drop.features.asm.BasicClassVisitor;
import com.android.drop.features.asm.BasicMethodAdapter;
import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.Method;
import com.android.drop.features.data.Statement;

public class InstrumentSensitiveCallsClassVisitor extends BasicClassVisitor {
    
	public InstrumentSensitiveCallsClassVisitor(ClassVisitor classvisitor, String instrumentationType) {
		super(classvisitor, instrumentationType);
	}

	@Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
        
		MethodVisitor mv = classWriterVisitor.visitMethod(access, name, desc, signature,
                exceptions);

        if (mv == null
                || (access & Opcodes.ACC_ABSTRACT) > 0) {
            return mv;
        }
        
//        if ((access & Opcodes.ACC_NATIVE) > 0) {
//        	
//        }

        return new InstrumentSensitiveMethodAdapter(Opcodes.ASM5, mv, this.name, access, name, desc);
    }
	

	
	private class InstrumentSensitiveMethodAdapter extends BasicMethodAdapter {
		
		protected InstrumentSensitiveMethodAdapter(final int api, final MethodVisitor mv,
	            String className, final int access, final String name, final String desc) {
			super(api, mv, className, access, name, desc, Constants.INST_DEV_LOG_FILE, InstrumentSensitiveCallsClassVisitor.this.instrumentationType);
		}
		
		@Override
        protected void onMethodEnter() {
//		  Method m = new Method(methodSigniture);
//		  dm.addMethod(m);
//		  //System.out.println("Entering " + methodSigniture);
//		  //julia - enable here to perform filtering
//		  AsmUtils.addPrintoutStatement(mv, Constants.INST_DEV_LOG_FILE, instrumentationType, Constants.LOG_MARKER + methodSigniture, 1);
			
			//debug
			if ((ClassHierarchy.getInstance().isAncestors(className,"com/facebook/nodex/startup/splashscreen/NodexInitializer$EnsureDexExceptionHandler") && name.equals("a"))) {
				//AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
				//		"HERE " + methodSigniture + " from " + className + "." + name, 1);
				AsmUtils.addPrintStackTrace(mv);
				//mv.visitInsn(ICONST_0);
			}
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
			String desc, boolean itf) {
//			super.visitMethodInsn(opcode, owner, name, desc, itf);
		
//			if (isConnection(owner, name)) { 
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
//				AsmUtils.addPrintStackTrace(mv);
//			}
			
	
			
			
			//sources
			if (
					(owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/impl/CloseableHttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/client/AbstractHttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/client/DefaultHttpClient") && name.equals("execute")) ||
					(owner.equals("java/net/URL") && name.equals("openConnection")) ||
					(owner.equals("java/net/URL") && name.equals("openStream")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/URLConnection") && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpURLConnection") && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpsURLConnection") && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/JarURLConnection") && name.equals("connect")) ||
					
					(owner.equals("java/net/Socket") && name.equals("getOutputStream")) ||
					(owner.equals("javax/net/ssl/SSLSocket") && name.equals("getOutputStream")) ||
					(owner.equals("org/apache/harmony/xnet/provider/jsse/OpenSSLSocketImpl") && name.equals("getOutputStream")) ||
					(owner.equals("libcore/io/Posix") && name.equals("<init>")) ||
					
					//(ClassHierarchy.getInstance().isAncestors(owner, "android/content/Context") && name.equals("startService")) ||
//					(ClassHierarchy.getInstance().isAncestors(owner, "android/content/Context") && name.equals("bindService")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "android/os/IBinder") && name.equals("transact")) 
					//(owner.equals("android/os/Parcel") && name.equals("obtain")) ||
					//(owner.equals("android/os/Parcel") && name.equals("obtain")) 
					//(owner.equals("android/os/Parcel") && name.equals("obtain")) 
			 ){
						AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
						Constants.SOURCE_MARKER + owner + "." + name + desc +
						" from " + methodSigniture, 1);
						AsmUtils.addPrintStackTrace(mv);
						//Statement statement = new Statement(methodSigniture, owner + "." + name + desc, 0);
						//dm.addStatement(statement);
						return;
			}
			
			else if ((access & Opcodes.ACC_NATIVE) > 0) {
	        	AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
						"SOURCE_NATIVE " + owner + "." + name + desc +
						" from " + methodSigniture, 1);
						AsmUtils.addPrintStackTrace(mv);
						return;
	        }
			
			else if (
					(ClassHierarchy.getInstance().isAncestors(owner, "java/lang/ClassLoader") && name.equals("loadClass")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "dalvik/system/BaseDexClassLoader") && name.equals("loadClass")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "dalvik/system/DexClassLoader") && name.equals("loadClass")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "dalvik/system/PathClassLoader") && name.equals("loadClass"))
					) {
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"SOURCE_LOAD " + owner + "." + name + desc +
//						" from " + methodSigniture, 1);
				AsmUtils.addPrintStackTrace(mv);
				AsmUtils.addThrow(mv, "java/lang/LinkageError", "BLOCKED_LOAD_CLASS");
				return;
			}
			
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			
//			//sinks
//			if (					
//					(owner.equals("android/view/ViewGroup") && name.equals("addView")) ||
//					(owner.equals("android/view/ViewGroup") && name.equals("addFocusables")) ||
//					(owner.equals("android/view/ViewGroup") && name.equals("addTouchables")) ||
//			        (owner.equals("android/view/ViewGroup") && name.equals("addChildrenForAccessibility")) ||
//				    (owner.equals("android/widget/TextSwitcher") && name.equals("addView")) ||
//					(owner.equals("android/widget/ViewSwitcher") && name.equals("addView")) ||
//			        (owner.equals("android/view/WindowManagerImpl") && name.equals("addView")) ||
//			        (owner.equals("android/view/WindowManagerImpl$CompatModeWrapper") && name.equals("addView")) ||
//				
//			        (ClassHierarchy.getInstance().isAncestors(owner, "android/view/ViewManager") && name.equals("addView")) ||
//				    (ClassHierarchy.getInstance().isAncestors(owner, "android/view/ViewManager") && name.equals("updateViewLayout")) ||
//				    
//				    (owner.equals("android/app/Dialog") && name.equals("setContentView")) ||
//					(owner.equals("android/support/v7/app/ActionBarActivityDelegate") && name.equals("setContentView")) ||
//					(owner.equals("android/support/v7/app/ActionBarActivityDelegateBase") && name.equals("setContentView")) ||
//					(owner.equals("android/support/v7/app/ActionBarActivityDelegateICS") && name.equals("setContentView")) ||
//
//					(owner.equals("android/webkit/WebView") && name.equals("loadData")) ||
//					(owner.equals("android/webkit/WebView") && name.equals("loadDataWithBaseURL")) ||
//					(owner.equals("android/webkit/WebView") && name.equals("loadUrl")) ||
//					
//					(owner.equals("android/view/View") && name.equals("onLayout")) ||
//					(owner.equals("android/view/View") && name.equals("layout"))  ||
//					(owner.equals("android/view/View") && name.equals("onDraw"))  ||
//					(owner.equals("android/view/View") && name.equals("onAttachedToWindow"))  ||
//
//					(owner.equals("android/widget/ImageView") && name.equals("onLayout")) ||
//					(owner.equals("android/widget/ImageView") && name.equals("layout"))  ||
//					(owner.equals("android/widget/ImageView") && name.equals("onDraw"))  ||
//					(owner.equals("android/widget/ImageView") && name.equals("onAttachedToWindow"))  ||
//
//					(owner.equals("android/inputmethodservice/KeyboardView") && name.equals("onLayout")) ||
//					(owner.equals("android/inputmethodservice/KeyboardView") && name.equals("layout"))  ||
//					(owner.equals("android/inputmethodservice/KeyboardView") && name.equals("onDraw"))  ||
//					(owner.equals("android/inputmethodservice/KeyboardView") && name.equals("onAttachedToWindow"))  ||
//
//					(owner.equals("android/widget/AnalogClock") && name.equals("onLayout")) ||
//					(owner.equals("android/widget/AnalogClock") && name.equals("layout"))  ||
//					(owner.equals("android/widget/AnalogClock") &&name.equals("onDraw"))  ||
//					(owner.equals("android/widget/AnalogClock") && name.equals("onAttachedToWindow"))  ||
//
//					(owner.equals("android/widget/TextView") && name.equals("onLayout")) ||
//					(owner.equals("android/widget/TextView") && name.equals("layout"))  ||
//					(owner.equals("android/widget/TextView") && name.equals("onDraw"))  ||
//					(owner.equals("android/widget/TextView") && name.equals("onAttachedToWindow"))  ||
//					(owner.equals("android/widget/TextView") && name.equals("append"))  ||
//					(owner.equals("android/widget/TextView") && name.equals("setText"))
//			) {
//						AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//								Constants.SINK_MARKER + owner + "." + name + desc +
//								" from " + methodSigniture, 2);
//						AsmUtils.addPrintStackTrace(mv);
//						Statement statement = new Statement(methodSigniture, owner + "." + name + desc, 1);
//						dm.addStatement(statement);
//			}
			
		}


    };
	

    
}
