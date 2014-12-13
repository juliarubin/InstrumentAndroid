package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.android.drop.features.data.ClassHierarchy;
import com.android.drop.features.data.Constants;
import com.android.drop.features.data.Method;
import com.android.drop.features.data.Statement;

public class InstrumentCallsClassVisitor extends BasicClassVisitor {
    
	public InstrumentCallsClassVisitor(ClassVisitor classvisitor, String instrumentationType) {
		super(classvisitor, instrumentationType);
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
		  dm.addMethod(m);
		  //System.out.println("Entering " + methodSigniture);
		  //julia - enable here to perform filtering
		  AsmUtils.addPrintoutStatement(mv, Constants.INST_DEV_LOG_FILE, instrumentationType, Constants.LOG_MARKER + methodSigniture, 2);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
			String desc, boolean itf) {
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		
//			if (isConnection(owner, name)) { 
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
//				AsmUtils.addPrintStackTrace(mv);
//			}
			
//			if ((owner.equals("android/net/NetworkInfo") && name.equals("isConnected"))) {
//				AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
//						"BLOCK CHECK CONNECT from " + methodSigniture + " via " + owner + "." + name, 2);
//				AsmUtils.addPrintStackTrace(mv);
//				//mv.visitInsn(ICONST_0);
//			}
			
			
			//sources
			if (
					(owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/impl/CloseableHttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/client/AbstractHttpClient") && name.equals("execute")) ||
					(owner.equals("org/apache/http/client/DefaultHttpClient") && name.equals("execute")) ||
					(owner.equals("java/net/URL") && name.equals("openConnection")) ||
					(owner.equals("java/net/URL") && name.equals("openStream")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/URLConnection", 0) && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpURLConnection", 0) && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/HttpsURLConnection", 0) && name.equals("connect")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "java/net/JarURLConnection", 0) && name.equals("connect")) ||
					
					(owner.equals("java/net/Socket") && name.equals("<init>")) ||
					(owner.equals("javax.net.ssl.SSLSocket") && name.equals("<init>")) ||
					(owner.equals("org/apache/harmony/xnet/provider/jsse/OpenSSLSocketImpl$SSLOutputStream") && name.equals("<init>")) ||
					(owner.equals("libcore/io/Posix") && name.equals("<init>")) ||
					
					(owner.equals("android/os/Parcel") && name.equals("obtain")) 
			 ) {
						AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
						Constants.SOURCE_MARKER + owner + "." + name + desc +
						" from " + methodSigniture, 2);
						AsmUtils.addPrintStackTrace(mv);
						Statement statement = new Statement(methodSigniture, owner + "." + name + desc, 0);
						dm.addStatement(statement);
			}
			
			//sinks
			if (
					(owner.equals("android/view/ViewGroup") && name.equals("addView")) ||
					(owner.equals("android/view/ViewGroup") && name.equals("addFocusables")) ||
					(owner.equals("android/view/ViewGroup") && name.equals("addTouchables")) ||
					(owner.equals("android/view/ViewGroup") && name.equals("addChildrenForAccessibility")) ||
					(owner.equals("android/widget/TextSwitcher") && name.equals("addView")) ||
					(owner.equals("android/widget/ViewSwitcher") && name.equals("addView")) ||
					(owner.equals("android/view/WindowManagerImpl") && name.equals("addView")) ||
					(owner.equals("android/view/WindowManagerImpl$CompatModeWrapper") && name.equals("addView")) ||
					
					(ClassHierarchy.getInstance().isAncestors(owner, "android/view/ViewManager", 0) && name.equals("addView")) ||
					(ClassHierarchy.getInstance().isAncestors(owner, "android/view/ViewManager", 0) && name.equals("updateViewLayout")) ||
									
					(owner.equals("android/app/Dialog") && name.equals("setContentView")) ||
					(owner.equals("android/support/v7/app/ActionBarActivityDelegate") && name.equals("setContentView")) ||				
					(owner.equals("android/support/v7/app/ActionBarActivityDelegateBase") && name.equals("setContentView")) ||
					(owner.equals("android/support/v7/app/ActionBarActivityDelegateICS") && name.equals("setContentView")) ||
					
					(owner.equals("android/webkit/WebView") && name.equals("loadData")) ||
					(owner.equals("android/webkit/WebView") && name.equals("loadDataWithBaseURL")) ||
					(owner.equals("android/webkit/WebView") && name.equals("loadUrl")) ||
					
					(owner.equals("android/view/View") && name.equals("onLayout")) ||
					(owner.equals("android/view/View") && name.equals("layout"))  ||
					(owner.equals("android/view/View") && name.equals("onDraw"))  ||
					(owner.equals("android/view/View") && name.equals("onAttachedToWindow"))  ||
					
					(owner.equals("android/view/ImageView") && name.equals("onLayout")) ||
					(owner.equals("android/view/ImageView") && name.equals("layout"))  ||
					(owner.equals("android/view/ImageView") && name.equals("onDraw"))  ||
					(owner.equals("android/view/ImageView") && name.equals("onAttachedToWindow"))  ||
					
					(owner.equals("android/view/KeyboardView") && name.equals("onLayout")) ||
					(owner.equals("android/view/KeyboardView") && name.equals("layout"))  ||
					(owner.equals("android/view/KeyboardView") && name.equals("onDraw"))  ||
					(owner.equals("android/view/KeyboardView") && name.equals("onAttachedToWindow"))  ||
					
					(owner.equals("android/widget/AnalogClock") && name.equals("onLayout")) ||
					(owner.equals("android/widget/AnalogClock") && name.equals("layout"))  ||
					(owner.equals("android/widget/AnalogClock") && name.equals("onDraw"))  ||
					(owner.equals("android/widget/AnalogClock") && name.equals("onAttachedToWindow"))  ||
					
					(owner.equals("android/widget/TextView") && name.equals("onLayout")) ||
					(owner.equals("android/widget/TextView") && name.equals("layout"))  ||
					(owner.equals("android/widget/TextView") && name.equals("onDraw"))  ||
					(owner.equals("android/widget/TextView") && name.equals("onAttachedToWindow"))  ||
					(owner.equals("android/widget/TextView") && name.equals("append"))  ||
					(owner.equals("android/widget/TextView") && name.equals("setText")) 
			) {
						AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
								Constants.SINK_MARKER + owner + "." + name + desc +
								" from " + methodSigniture, 2);
						AsmUtils.addPrintStackTrace(mv);
						Statement statement = new Statement(methodSigniture, owner + "." + name + desc, 1);
						dm.addStatement(statement);
			}
			
		}
    };
	

    
}
