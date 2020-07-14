package ru.mousecray.mousecore.core;

import net.minecraft.launchwrapper.IClassTransformer;

import javax.annotation.Nullable;

public class MousecoreInnerTransformer implements IClassTransformer {

    @Nullable
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        else if (!Mousecore.events.isEmpty())
            Mousecore.events.forEach(event -> event.getHooks()
                    .forEach(hook -> hook.transform(name, transformedName, basicClass)));
        return basicClass;
    }
}
