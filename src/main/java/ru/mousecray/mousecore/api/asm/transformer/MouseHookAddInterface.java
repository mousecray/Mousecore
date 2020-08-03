package ru.mousecray.mousecore.api.asm.transformer;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;
import ru.mousecray.mousecore.api.asm.method.MouseConsumer;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MouseHookAddInterface<T> extends MouseHookParent implements Opcodes {

    private final Class<T> interfaze;

    public MouseHookAddInterface(String targetClass, @Nonnull Class<T> interfaze) {
        super(targetClass);
        this.interfaze = interfaze;
    }

    @Override
    protected byte[] transformClass(String name, String transformedName, byte[] basicClass) {
        if (interfaze == null) {
            error("The interface cannot be null! It will be skipped");
            return basicClass;
        }
        else if (!interfaze.isInterface()) {
            error("The target class is not an interface! It will be skipped");
            return basicClass;
        }
        List<Method> defaultMethod = new ArrayList<>();
        List<Method> normalMethod = new ArrayList<>();
        Arrays.stream(interfaze.getMethods()).forEach(m -> {
            if (!Modifier.isStatic(m.getModifiers())) {
                if (m.isDefault()) defaultMethod.add(m);
                else normalMethod.add(m);
            }
        });

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new AddInterfaceClassVisitor(writer, Type.getInternalName(interfaze),
                transformedName.replace(".", "/"), defaultMethod, normalMethod);
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        return writer.toByteArray();
    }

    public MouseConsumer onMethodExecuted(MouseMethod method) {
        return (pars -> null);
    }

    //TODO
    private static class AddInterfaceClassVisitor extends ClassVisitor {
        private final String interfaze;
        private final List<Method> defaultMethod;
        private final List<Method> normalMethod;
        private final String className;

        public AddInterfaceClassVisitor(ClassVisitor visitor, String input, String className, List<Method> defaultMethod, List<Method> normalMethod) {
            super(ASM5, visitor);
            interfaze = input;
            this.defaultMethod = defaultMethod;
            this.normalMethod = normalMethod;
            this.className = className;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, ArrayUtils.addAll(interfaces, interfaze));
        }

        @Override
        public void visitEnd() {
            FieldVisitor fv;
            fv = super.visitField(ACC_PRIVATE, "voidMethods", "Ljava/util/Map;",
                    "Ljava/util/Map<Ljava/lang/String;Lru/mousecray/mousecore/api/asm/methods/MouseConsumerVoid;>;", null);
            fv.visitEnd();
            fv = super.visitField(ACC_PRIVATE, "returnMethods", "Ljava/util/Map;",
                    "Ljava/util/Map<Ljava/lang/String;Lru/mousecray/mousecore/api/asm/methods/MouseConsumer;>;", null);
            fv.visitEnd();
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD, "ru/mousecray/mousecore/Get", "voidMethods", "Ljava/util/Map;");


            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD, "ru/mousecray/mousecore/Get", "returnMethods", "Ljava/util/Map;");

            mv.visitEnd();

//            for (int count = 0; count < methods.size(); ++count) {
//                Method method = methods.get(count);
//
//                mv = super.visitMethod(ACC_PUBLIC, method.getName(),
//                        Type.getMethodDescriptor(method.getReturnType(), method.getParameterTypes()), null, null);
//                mv.visitCode();
//                mv.visitVarInsn(ALOAD, 0);
//                //TODO:
//                mv.visitFieldInsn(GETFIELD, "ru/mousecray/mousecore/Get", "transformer",
//                        "Lru/mousecray/mousecore/api/asm/transformers/MouseHookAddInterface;");
//            }

            super.visitEnd();
        }
    }
}