package ru.mousecray.mousecore.core.visitors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.MousecoreHook;

import java.util.ArrayList;
import java.util.List;

public class CheckHookClassVisitor extends ClassVisitor {

    List<String> obj = new ArrayList<>();

    public CheckHookClassVisitor() { super(Opcodes.ASM5); }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals(Type.getDescriptor(MousecoreHook.class))) return new GetAnnotationValueVisitor(() -> obj);
        return super.visitAnnotation(desc, visible);
    }

    public List<String> getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        reader.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return obj;
    }
}
