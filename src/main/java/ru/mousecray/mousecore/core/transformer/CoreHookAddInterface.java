package ru.mousecray.mousecore.core.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

public class CoreHookAddInterface extends MouseHookAdapter {

    private final List<String> interfaces;

    public CoreHookAddInterface(String targetClass, @Nonnull List<String> interfaces) {
        super(targetClass);
        this.interfaces = interfaces;
    }

    @Override
    protected final byte[] transformClass(String name, String transformedName, byte[] basicClass) {

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//        ClassVisitor visitor = new AddInterfaceClassVisitor(writer, interfaces,
//                transformedName.replace(".", "/"), defaultMethod, normalMethod);
//        ClassReader reader = new ClassReader(basicClass);
//        reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        return writer.toByteArray();
    }

    //TODO
    private static class AddInterfaceClassVisitor extends ClassVisitor {
        private final List<String> interfaze;
        private final List<Method> defaultMethod;
        private final List<Method> normalMethod;

        public AddInterfaceClassVisitor(ClassVisitor visitor, List<String> input, List<Method> defaultMethod, List<Method> normalMethod) {
            super(ASM5, visitor);
            interfaze = input;
            this.defaultMethod = defaultMethod;
            this.normalMethod = normalMethod;
        }

//        @Override
//        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//            super.visit(version, access, name, signature, superName, ArrayUtils.addAll(interfaces, interfaze));
//        }

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

//            for (int count = 0; count < methods.size(); ++count) {
//                Method method = methods.get(count);

//                mv = super.visitMethod(ACC_PUBLIC, method.getName(),
//                        Type.getMethodDescriptor(method.getReturnType(), method.getParameterTypes()), null, null);
//                mv.visitCode();
//                mv.visitVarInsn(ALOAD, 0);
            //TODO:
//                mv.visitFieldInsn(GETFIELD, "ru/mousecray/mousecore/Get", "transformer",
//                        "Lru/mousecray/mousecore/api/asm/transformers/CoreHookAddInterface;");
//            }

//            super.visitEnd();
        }
    }
}