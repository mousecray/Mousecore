package ru.mousecray.mousecore.core;

import net.minecraft.launchwrapper.IClassTransformer;

public class MousecoreTransformer implements IClassTransformer {
    private final TransformersManager manager = new TransformersManager();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return manager.apply(name, transformedName, basicClass);
    }
}
