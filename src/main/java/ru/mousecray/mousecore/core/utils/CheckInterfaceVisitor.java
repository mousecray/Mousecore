package ru.mousecray.mousecore.core.utils;

import org.apache.commons.lang3.tuple.Triple;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

public class CheckInterfaceVisitor extends ClassVisitor {

    private final Set<String> defaultDescriptors = new HashSet<>();
    private final Set<String> allDescriptors = new HashSet<>();

    public CheckInterfaceVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & Opcodes.ACC_STATIC) == 0) allDescriptors.add(desc);
        if ((access & Opcodes.ACC_ABSTRACT) == 0) defaultDescriptors.add(desc);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public Triple<Boolean, Set<String>, Set<String>> getResult(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        boolean flag = (reader.getAccess() & Opcodes.ACC_INTERFACE) != 0;
        reader.accept(this, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return Triple.of(flag, defaultDescriptors, allDescriptors);
    }
}
