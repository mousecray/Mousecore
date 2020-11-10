package ru.mousecray.mousecore.api.asm.event;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;
import ru.mousecray.mousecore.core.utils.InterfaceHookException;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.mousecray.mousecore.core.Mousecore.LOGGER;

public class MouseLoadEvent {
    private final Map<String, List<String>> interfaceAdderHooks = new HashMap<>();
    private final List<MouseHookAdapter> rawHooks = new ArrayList<>();
    private final Map<String, Map<String, MouseMethod>> methodRefactorHooks = new HashMap<>();
    private final Map<String, Map<String, MouseMethod>> methodAdderHooks = new HashMap<>();

    private int interfaceSize;
    private int rawSize;
    private int refactorSize;
    private int adderSize;

    public void registerInterfaceCreator(String targetClass, @Nonnull Class<?> interfaze, MouseMethod... hooks) {
        if (!interfaze.isInterface()) throw new InterfaceHookException("Interface " + interfaze.getName() + "isn't interface");
        if (interfaceAdderHooks.containsKey(targetClass)) interfaceAdderHooks.get(targetClass).add(Type.getInternalName(interfaze));
        else {
            List<String> list = new ArrayList<>();
            list.add(Type.getInternalName(interfaze));
            interfaceAdderHooks.put(targetClass, list);
        }

        List<String> allMethod = new ArrayList<>();
        List<String> defaultMethod = new ArrayList<>();

        Arrays.stream(interfaze.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .forEach(method -> {
                    String desc = Type.getMethodDescriptor(method);
                    if (method.isDefault()) {
                        defaultMethod.add(desc);
                        allMethod.add(desc);
                    } else allMethod.add(desc);
                });
        Arrays.stream(hooks).forEach(method -> {
            if (!allMethod.remove(method.getDescriptor())) LOGGER.log(Level.WARN,
                    "Registered method with descriptor \"" + method.getDescriptor() +
                            "\" is missing in the interface \"" + interfaze.getName() +
                            "\". Please, use \"MouseLoadEvent#registerMethodCreator\" " +
                            "to register unrelated methods!");
            registerMethodCreator(targetClass, method);
        });
        allMethod.removeAll(defaultMethod);

        if (!allMethod.isEmpty()) {
            throw new InterfaceHookException("You must implement all interface methods, " +
                    "which nonstatic and nondefault. Interface: " + interfaze.getName() +
                    ", Not implemented methods descriptors: " + allMethod.stream().reduce((str1, str2) -> str1 + ", " + str2).get());
        }
        ++interfaceSize;
    }

    public void registerMethodRefactor(String targetClass, @Nonnull MouseMethod hook) {
        if (methodRefactorHooks.containsKey(targetClass)) methodRefactorHooks.get(targetClass).put(hook.getDescriptor(), hook);
        else {
            Map<String, MouseMethod> map = new HashMap<>();
            map.put(hook.getDescriptor(), hook);
            methodRefactorHooks.put(targetClass, map);
        }
        ++refactorSize;
    }

    public void registerMethodCreator(String targetClass, @Nonnull MouseMethod hook) {
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

    public List<MouseHookAdapter> getAdapters() {
        return rawHooks;
    }

    public Map<String, List<String>> getInterfaceAdders() {
        return interfaceAdderHooks;
    }

    public Map<String, Map<String, MouseMethod>> getMethodAdders() {
        return methodAdderHooks;
    }

    public Map<String, Map<String, MouseMethod>> getMethodRefactors() {
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