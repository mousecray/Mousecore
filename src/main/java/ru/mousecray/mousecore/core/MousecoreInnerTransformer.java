package ru.mousecray.mousecore.core;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookParent;

import javax.annotation.Nullable;

public class MousecoreInnerTransformer implements IClassTransformer {

    @Nullable
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        else if (!Mousecore.events.isEmpty()) {
            byte[] returnClass = basicClass;

            for (MouseLoadEvent event : Mousecore.events) {
                for (MouseHookParent hook : event.getHooks()) {
                    returnClass = hook.transform(name, transformedName, basicClass);
                }
            }
            return returnClass;
        }
        return basicClass;
    }
}
