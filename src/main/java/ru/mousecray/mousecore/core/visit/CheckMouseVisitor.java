package ru.mousecray.mousecore.core.visit;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.MouseContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CheckMouseVisitor extends ClassVisitor {

    private String result;

    public CheckMouseVisitor() { super(Opcodes.ASM5); }

    @Override
    public AnnotationVisitor visitAnnotation(@Nonnull String desc, boolean visible) {
        if (desc.equals(Type.getDescriptor(MouseContainer.class))) return new GetAnnotationValueVisitor(value -> result = value);
//        else if (desc.equals(Type.getDescriptor(MouseClass.class))) result = TransformersFinder.mouseClassDescriptor;
        return super.visitAnnotation(desc, visible);
    }

    @Nullable
    public String getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        reader.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return result;
    }
}
