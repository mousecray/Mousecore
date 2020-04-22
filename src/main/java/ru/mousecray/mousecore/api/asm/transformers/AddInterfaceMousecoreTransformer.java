package ru.mousecray.mousecore.api.asm.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public abstract class AddInterfaceMousecoreTransformer extends AbstractMousecoreTransformer {

    public abstract Collection<? extends String> getInterfaces();

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!getTransformClass().equals(name) || getInterfaces().isEmpty()) return basicClass;

//        if (!name.endsWith("BlockCrops")) return basicClass;

        ClassNode clazz = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(clazz, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

//        clazz.interfaces.forEach(System.out::println);
        getInterfaces().forEach(interfaze -> {
            String internalInterface = interfaze.replace(".", "/");
            System.out.println(internalInterface);
            if (!clazz.interfaces.contains(internalInterface)) clazz.interfaces.add(internalInterface);

        });

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        clazz.accept(writer);
        return writer.toByteArray();
    }
}