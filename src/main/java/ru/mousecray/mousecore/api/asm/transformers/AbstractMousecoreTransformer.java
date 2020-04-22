package ru.mousecray.mousecore.api.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class AbstractMousecoreTransformer implements IClassTransformer {
    AbstractMousecoreTransformer() {}

    /**
     * @return must be {@link org.objectweb.asm.Type#getInternalName Type#getInternalName()}
     */
    public abstract String getTransformClass();

    public int getHookPriority() {
        return 0;
    }
}
