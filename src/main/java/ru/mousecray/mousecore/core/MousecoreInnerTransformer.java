package ru.mousecray.mousecore.core;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.core.transformer.CoreHookAddInterface;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class MousecoreInnerTransformer implements IClassTransformer {

    @Nullable
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!Mousecore.INSTANCE.isEmpty()) {
            byte[] returnClass = basicClass;
            for (Map.Entry<String, List<String>> entry : Mousecore.INSTANCE.interfaceAdderHooks.entrySet()) {
                String key = entry.getKey();
                if (Mousecore.INSTANCE.visitedClasses.contains(key)) break;
                returnClass = new CoreHookAddInterface(key, entry.getValue()).transform(name, transformedName, returnClass);
                Mousecore.INSTANCE.visitedClasses.add(key);
            }
            for (MouseHookAdapter adapter : Mousecore.INSTANCE.rawHooks) returnClass = adapter.transform(name, transformedName, returnClass);
            return returnClass;
        }
        return basicClass;
    }
}
