package ru.mousecray.mousecore.api.asm.event;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;
import ru.mousecray.mousecore.core.Mousecore;
import ru.mousecray.mousecore.core.utils.CheckInterfaceVisitor;
import ru.mousecray.mousecore.core.utils.InterfaceHookException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MouseLoadEvent {
    public static final Logger LOGGER = LogManager.getLogger("mousecore");
    private static LaunchClassLoader loader;
    private final Map<MinecraftClass, List<MinecraftClass>> interfaceAdderHooks = new HashMap<>();
    private final List<MouseHookAdapter> rawHooks = new ArrayList<>();
    private final Map<MinecraftClass, Map<String, MouseMethod>> methodRefactorHooks = new HashMap<>();
    private final Map<MinecraftClass, Map<String, MouseMethod>> methodAdderHooks = new HashMap<>();
    private int interfaceSize;
    private int rawSize;
    private int refactorSize;
    private int adderSize;

    public static LaunchClassLoader getClassLoader() {
        if (loader != null) return loader;
        ClassLoader loader = Mousecore.class.getClassLoader();
        if (loader instanceof LaunchClassLoader) return MouseLoadEvent.loader = (LaunchClassLoader) loader;
        else throw new RuntimeException("Mousecore loaded with incompatible ClassLoader");
    }

    public void registerInterfaceCreator(MinecraftClass targetClass, @Nonnull MinecraftClass interfaze, MouseMethod... hooks) {
        String canonicalName = interfaze.getCanonicalName();
        Triple<Boolean, Set<String>, Set<String>> triple;
        try {
            triple = new CheckInterfaceVisitor().getResult(getClassLoader().getClassBytes(canonicalName));
        } catch (IOException e) {
            throw new InterfaceHookException("Interface " + canonicalName + "can't be read");
        }
        if (!triple.getLeft()) throw new InterfaceHookException("Interface " + canonicalName + " isn't interface");
        if (interfaceAdderHooks.containsKey(targetClass)) interfaceAdderHooks.get(targetClass).add(interfaze);
        else {
            List<MinecraftClass> list = new ArrayList<>();
            list.add(interfaze);
            interfaceAdderHooks.put(targetClass, list);
        }


        Set<String> defaultMethod = triple.getMiddle();
        Set<String> allMethod = triple.getRight();

        Arrays.stream(hooks).forEach(method -> {
            if (!allMethod.remove(method.getDescriptor())) LOGGER.log(Level.WARN,
                    "Registered method with descriptor \"" + method.getDescriptor() +
                            "\" is missing in the interface \"" + canonicalName +
                            "\". Please, use \"MouseLoadEvent#registerMethodCreator\" " +
                            "to register unrelated methods!");
            registerMethodCreator(targetClass, method);
        });
        allMethod.removeAll(defaultMethod);

        if (!allMethod.isEmpty()) {
            throw new InterfaceHookException("You must implement all interface methods, " +
                    "which nonstatic and nondefault. Interface: " + interfaze.getCanonicalName() +
                    ", Not implemented methods descriptors: " + allMethod.stream().reduce((str1, str2) -> str1 + ", " + str2).orElse(""));
        }
        ++interfaceSize;
    }

    public void registerMethodRefactor(MinecraftClass targetClass, @Nonnull MouseMethod hook) {
        if (methodRefactorHooks.containsKey(targetClass)) methodRefactorHooks.get(targetClass).put(hook.getDescriptor(), hook);
        else {
            Map<String, MouseMethod> map = new HashMap<>();
            map.put(hook.getDescriptor(), hook);
            methodRefactorHooks.put(targetClass, map);
        }
        ++refactorSize;
    }

    public void registerMethodCreator(MinecraftClass targetClass, @Nonnull MouseMethod hook) {
        if (methodAdderHooks.containsKey(targetClass)) methodAdderHooks.get(targetClass).put(hook.getDescriptor(), hook);
        else {
            Map<String, MouseMethod> map = new HashMap<>();
            map.put(hook.getDescriptor(), hook);
            methodAdderHooks.put(targetClass, map);
        }
        ++adderSize;
    }

    public void registerHookAdapter(@Nonnull MouseHookAdapter hook) {
        rawHooks.add(hook);
        ++rawSize;
    }

    public boolean isEmpty() {
        return interfaceAdderHooks.isEmpty() && rawHooks.isEmpty() && methodRefactorHooks.isEmpty() && methodAdderHooks.isEmpty();
    }

    public Map<MinecraftClass, List<MinecraftClass>> getInterfaceAdders() {
        return interfaceAdderHooks;
    }

    public List<MouseHookAdapter> getAdapters() {
        return rawHooks;
    }

    public Map<MinecraftClass, Map<String, MouseMethod>> getMethodAdders() {
        return methodAdderHooks;
    }

    public Map<MinecraftClass, Map<String, MouseMethod>> getMethodRefactors() {
        return methodRefactorHooks;
    }

    public int size(int type) {
        switch (type) {
            case 1:
                return interfaceSize;
            case 2:
                return refactorSize;
            case 3:
                return adderSize;
            default:
                return rawSize;
        }
    }
}