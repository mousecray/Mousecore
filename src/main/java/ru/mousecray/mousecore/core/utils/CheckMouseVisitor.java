package ru.mousecray.mousecore.core.utils;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.MouseContainer;

import javax.annotation.Nonnull;

public class CheckMouseVisitor extends ClassVisitor {

    private boolean result;

    public CheckMouseVisitor() { super(Opcodes.ASM5); }

    @Override
    public AnnotationVisitor visitAnnotation(@Nonnull String desc, boolean visible) {
        if (desc.equals(Type.getDescriptor(MouseContainer.class))) result = true;
        return super.visitAnnotation(desc, visible);
    }

    public boolean getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        reader.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return result;
    }
}
