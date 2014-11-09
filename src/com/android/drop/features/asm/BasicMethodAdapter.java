package com.android.drop.features.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class BasicMethodAdapter extends AdviceAdapter {
	
	protected String className;
	protected String name;
	protected String desc;
	protected String methodSigniture;
	protected String logFileName;
	protected String instrumentationType;
	
	protected BasicMethodAdapter(final int api, final MethodVisitor mv,
            String className, final int access, final String name, 
            final String desc, final String logFileName, final String instrumentationType) {
		super(api, mv, access, name, desc);
        this.className = className;
        this.name = name;
        this.desc = desc;
        methodSigniture = className + "." + name +  desc;
        this.logFileName = logFileName;
        this.instrumentationType = instrumentationType;
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc, boolean itf) {
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		
		if ((owner.equals("java/net/URL") && name.equals("openConnection")) ||
			(owner.equals("java/net/URL") && name.equals("openStream")) ||
			(owner.equals("org/apache/http/client/HttpClient") && name.equals("execute")) ||
			(owner.equals("org/apache/http/impl/client/AbstractHttpClient") && name.equals("execute")) ||
			(owner.equals("android/webkit/WebView") && name.equals("loadUrl")) ||		
			(owner.equals("java/net/DatagramSocket") && name.equals("send")) ||		
			(owner.equals("OpenSSLSocketImpl$SSLOutputStream") && name.equals("write")) ||	
			(owner.equals("libcore.io.Posix") && name.equals("sendto")) ||
			(owner.equals("libcore.io.Posix") && name.equals("sendtoBytes"))) {
			
			AsmUtils.addPrintoutStatement(mv, logFileName, instrumentationType, 
					"CONNECTION from " + methodSigniture + " via " + owner + "." + name, 0);
			}
	}

}
