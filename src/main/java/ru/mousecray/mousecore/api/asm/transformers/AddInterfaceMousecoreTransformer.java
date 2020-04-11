package ru.mousecray.mousecore.api.asm.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public abstract class AddInterfaceMousecoreTransformer extends AbstractMousecoreTransformer {

    public abstract Collection<? extends Class<?>> getInterfaces();

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!getTransformClass().getCanonicalName().equals(transformedName) || getInterfaces().isEmpty()) return basicClass;

        ClassNode clazz = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(clazz, 0);

        getInterfaces()
                .stream()
                .filter(Class::isInterface)
                .forEach(interfaze -> {
                    String interfaceName = Type.getInternalName(interfaze);
                    if (!clazz.interfaces.contains(interfaceName)) clazz.interfaces.add(interfaceName);
                });

        ClassWriter writer = new ClassWriter(3);
        clazz.accept(writer);
        return writer.toByteArray();
    }
}