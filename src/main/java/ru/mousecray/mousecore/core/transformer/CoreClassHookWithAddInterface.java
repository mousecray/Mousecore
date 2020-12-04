package ru.mousecray.mousecore.core.transformer;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CoreClassHookWithAddInterface extends MouseHookAdapter {

    private final List<MinecraftClass> interfaces;
    private final Map<String, MouseMethod> methods;
    private final Map<String, MouseMethod> methodRefactor;

    public CoreClassHookWithAddInterface(MinecraftClass targetClass, @Nonnull List<MinecraftClass> interfaces, @Nonnull Map<String, MouseMethod> methods, Map<String, MouseMethod> methodRefactor) {
        super(targetClass);
        this.interfaces = interfaces;
        this.methods = methods;
        this.methodRefactor = methodRefactor;
    }

    @Override
    protected final byte[] transformClass(String name, String transformedName, byte[] basicClass) {
        ClassWriter writer = new ClassWriter(WRITER_FLAGS);
        ClassVisitor visitor = new AddInterfaceClassVisitor(writer);
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(visitor, READER_FLAGS);

        return writer.toByteArray();
    }

    //TODO
    private class AddInterfaceClassVisitor extends ClassVisitor {

        public AddInterfaceClassVisitor(ClassVisitor visitor) {
            super(ASM5, visitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            String[] addInterfaces = CoreClassHookWithAddInterface.this.interfaces.stream()
                    .map(MinecraftClass::getInternalName)
                    .toArray(String[]::new);
            super.visit(version, access, name, signature, superName, ArrayUtils.addAll(interfaces, addInterfaces));
        }

        @Override
        public void visitEnd() {
//            FieldVisitor fv;
//            fv = super.visitField(ACC_PRIVATE, "voidMethods", "Ljava/util/Map;",
//                    "Ljava/util/Map<Ljava/lang/String;Lru/mousecray/mousecore/api/asm/methods/MouseConsumerVoid;>;", null);
//            fv.visitEnd();
//            fv = super.visitField(ACC_PRIVATE, "returnMethods", "Ljava/util/Map;",
//                    "Ljava/util/Map<Ljava/lang/String;Lru/mousecray/mousecore/api/asm/methods/MouseExecutor;>;", null);
//            fv.visitEnd();
            MethodVisitor mv;
//            = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitTypeInsn(NEW, "java/util/HashMap");
//            mv.visitInsn(DUP);
//            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
//            mv.visitFieldInsn(PUTFIELD, "ru/mousecray/mousecore/Get", "voidMethods", "Ljava/util/Map;");
//
//
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitTypeInsn(NEW, "java/util/HashMap");
//            mv.visitInsn(DUP);
//            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
//            mv.visitFieldInsn(PUTFIELD, "ru/mousecray/mousecore/Get", "returnMethods", "Ljava/util/Map;");
//
//            mv.visitEnd();

            for (Map.Entry<String, MouseMethod> entry : methods.entrySet()) {
                MouseMethod method = entry.getValue();
                mv = super.visitMethod(ACC_PUBLIC, method.);
            }
//            for (int count = 0; count < methods.size(); ++count) {
//                Method method = methods.get(count);


                        Type.getMethodDescriptor(method.getReturnType(), method.getParameterTypes()), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
            //TODO:
//                mv.visitFieldInsn(GETFIELD, "ru/mousecray/mousecore/Get", "transformer",
//                        "Lru/mousecray/mousecore/api/asm/transformers/CoreClassHookWithAddInterface;");
//            }

//            super.visitEnd();
        }
    }
}