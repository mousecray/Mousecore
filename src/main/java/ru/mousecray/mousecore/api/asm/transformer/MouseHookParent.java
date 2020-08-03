package ru.mousecray.mousecore.api.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MouseHookParent implements IClassTransformer {
    /**
     * targetClass must be {@link org.objectweb.asm.Type#getInternalName Type#getInternalName()}
     */
    private final String targetClass;
    private Logger logger;

    /**
     * @param targetClass - must be {@link org.objectweb.asm.Type#getInternalName Type#getInternalName()}
     */
    public MouseHookParent(String targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!canTransform(name)) return basicClass;
        else return transformClass(name, transformedName, basicClass);
    }

    protected abstract byte[] transformClass(String name, String transformedName, byte[] basicClass);

    protected boolean canTransform(String name) {
        return targetClass.equals(name);
    }

    protected void error(String message) {
        (logger == null ? (logger = LogManager.getLogger("mousecore=>" + getClass().getSimpleName())) : logger).log(Level.ERROR,
                "Error while loading " + getClass().getSimpleName() + " transformer: " + message);
    }
}