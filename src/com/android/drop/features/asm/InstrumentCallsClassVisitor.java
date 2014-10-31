package com.android.drop.features.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentCallsClassVisitor extends ClassNode {

	private final ClassVisitor classvisitor;

	InstrumentCallsClassVisitor(ClassVisitor classvisitor) {
        super(Opcodes.ASM5);
        this.classvisitor = classvisitor;
    }

    @Override
    public void visitEnd() {
        for (Object methodNodeObject : this.methods) {
            final MethodNode methodNode = (MethodNode) methodNodeObject;
            final String methodname = methodNode.name;
      
            boolean isAbstract = ( methodNode.access & Opcodes.ACC_ABSTRACT) != 0;
            boolean isNative = (methodNode.access & Opcodes.ACC_NATIVE) != 0;
            
            if (isAbstract || isNative) {
            	continue;
            }
            
           AbstractInsnNode firstInst = methodNode.instructions.getFirst();

            // Insert the three opcodes that will output the
            // name of the routine

            // ***
            // insert GETSTATIC
            final FieldInsnNode fieldnode = new FieldInsnNode(org.objectweb.asm.Opcodes.GETSTATIC,
                                                             "java/lang/System", // field class
                                                             "out",              // field name
                                                             "Ljava/io/PrintStream;"); // field type
            methodNode.instructions.insertBefore(firstInst, fieldnode);

            // ***
            // insert ldc "Am in method xxx"
            final String str = "AM IN METHOD:" + this.name + "." + methodname;
            final LdcInsnNode ldcnode = new LdcInsnNode(str);
            methodNode.instructions.insertBefore(firstInst, ldcnode);

            // ***
            // insert invokevirtual
            final AbstractInsnNode newInsnNode = new MethodInsnNode(org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
                                                                   "java/io/PrintStream", // class name
                                                                   "println",             // method name
                                                                   "(Ljava/lang/String;)V", false); // desc
            methodNode.instructions.insertBefore(firstInst, newInsnNode);
        }

   
        // Without this, get
        // "UnsupportedClassVersionError: Test : Unsupoorted majorminor version 0.0"
        // Where Test was the application class
        accept(classvisitor);
    }
    
}
