package ru.mousecray.mousecore.core.classvisitors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;

public class AnnotationClassVisitor extends ClassVisitor {

    private final Class<? extends Annotation> annotation;
    private boolean bool = false;

    public AnnotationClassVisitor(Class<? extends Annotation> annotation) {
        super(Opcodes.ASM5);
        this.annotation = annotation;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        bool = desc.equals(Type.getDescriptor(annotation));
        return super.visitAnnotation(desc, visible);
    }

    public boolean getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        reader.accept(this, 0);
        return bool;
    }
}
