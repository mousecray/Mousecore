package ru.mousecray.mousecore.api.asm.adapter;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import ru.mousecray.mousecore.api.asm.MinecraftClass;

public abstract class MouseHookAdapter implements IClassTransformer, Opcodes {
    private static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
    protected static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
    protected static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;

    /**
     * targetClass must be {@link org.objectweb.asm.Type#getInternalName Type#getInternalName()}
     */
    private final MinecraftClass targetClass;

    /**
     * @param targetClass - must be {@link org.objectweb.asm.Type#getInternalName Type#getInternalName()}
     */
    public MouseHookAdapter(MinecraftClass targetClass) {
        this.targetClass = targetClass;
    }

    public MinecraftClass getTargetClass() {
        return targetClass;
    }

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!canTransform(name)) return basicClass;
        else return transformClass(name, transformedName, basicClass);
    }

    protected abstract byte[] transformClass(String name, String transformedName, byte[] basicClass);

    protected boolean canTransform(String name) {
        return targetClass.getCanonicalName().equals(name);
    }
}