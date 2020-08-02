package ru.mousecray.mousecore.core.visit;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.MouseContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class CheckMouseVisitor extends ClassVisitor {

    private String result;
    private Set<String> depend = new HashSet<>();

    public CheckMouseVisitor() { super(Opcodes.ASM5); }

    @Override
    public AnnotationVisitor visitAnnotation(@Nonnull String desc, boolean visible) {
        if (desc.equals(Type.getDescriptor(MouseContainer.class))) return new GetAnnotationValueVisitor(value -> result = value);
//        else addName(desc);
//        else if (desc.equals(Type.getDescriptor(MouseClass.class))) result = TransformersFinder.mouseClassDescriptor;
        return super.visitAnnotation(desc, visible);
    }


//    @Override
//    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        if (signature == null) {
//            addName(superName);
//            addNames(interfaces);
//        }
//        else {
//            addSignature(signature);
//        }
//    }
//
//    private void addSignature(String sign) {
//        SignatureVisitor
//        if (sign != null) new SignatureReader(sign).accept(this);
//    }
//
//    private void addTypeSignature(String sign) {
//        if (sign != null) new SignatureReader(sign).acceptType(this);
//    }
//
//    private void addName(String desc) {
//        if (desc == null) return;
//        String name = Type.getType(desc).getClassName();
//        if (name != null) depend.add(name);
//    }

    @Nullable
    public String getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        reader.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return result;
    }
}
