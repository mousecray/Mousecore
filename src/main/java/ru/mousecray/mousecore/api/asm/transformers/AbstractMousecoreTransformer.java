package ru.mousecray.mousecore.api.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class AbstractMousecoreTransformer implements IClassTransformer {
    AbstractMousecoreTransformer() {}

    public abstract Class<?> getTransformClass();
}
