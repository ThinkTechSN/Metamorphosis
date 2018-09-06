package org.metamorphosis.core;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassVisitor implements org.objectweb.asm.ClassVisitor {

	@Override
	public void visit(int version, int access, String name,String signature, String superName, String[] interfaces) {
		System.out.println(name + " extends " + superName + " {");
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc,boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
	}

	@Override
	public void visitEnd() {	
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,String signature, Object value) {
		return null;
	}

	@Override
	public void visitInnerClass(String name, String outerName,String innerName, int access) {
	}

	@Override
	public MethodVisitor visitMethod(int access, String name,String desc, String signature, String[] exceptions) {
		return null;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}

	@Override
	public void visitSource(String source, String debug) {
	}
}