package ru.mousecray.mousecore.core;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.core.transformer.CoreClassHookWithAddInterface;

import javax.annotation.Nullable;
import java.util.List;

public class MousecoreInnerTransformer implements IClassTransformer {

    @Nullable
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!Mousecore.INSTANCE.isEmpty()) {
            byte[] returnClass = basicClass;
            MinecraftClass clazz = MinecraftClass.fromCanonicalName(name);
            List<MinecraftClass> interfaces = Mousecore.INSTANCE.interfaceAdderHooks.get(clazz);
            if (interfaces != null) {
                returnClass = new CoreClassHookWithAddInterface(clazz, interfaces,
                        Mousecore.INSTANCE.methodAdderHooks.get(clazz),
                        Mousecore.INSTANCE.methodRefractorHooks.get(clazz))
                        .transform(name, transformedName, returnClass);
            } else {
                //TODO: Adder and refactor methods
            }
            for (MouseHookAdapter adapter : Mousecore.INSTANCE.rawHooks) returnClass = adapter.transform(name, transformedName, returnClass);
            return returnClass;
        }
        return basicClass;
    }
}
