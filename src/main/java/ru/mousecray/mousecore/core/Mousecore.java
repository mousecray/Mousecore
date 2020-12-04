package ru.mousecray.mousecore.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.adapter.MouseHookAdapter;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;
import ru.mousecray.mousecore.core.transformer.TransformersFinder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.mousecray.mousecore.api.asm.event.MouseLoadEvent.LOGGER;

@IFMLLoadingPlugin.Name("Mousecore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"ru.mousecray.mousecore"})
@IFMLLoadingPlugin.SortingIndex(1001)
public class Mousecore implements IFMLLoadingPlugin {
    public static Mousecore INSTANCE;
    private static boolean deobfEnvironment;
    public final Map<MinecraftClass, List<MinecraftClass>> interfaceAdderHooks;
    public final List<MouseHookAdapter> rawHooks;
    public final Map<MinecraftClass, Map<String, MouseMethod>> methodAdderHooks;
    public final Map<MinecraftClass, Map<String, MouseMethod>> methodRefractorHooks;

    public Mousecore() {
        INSTANCE = this;
        MousecoreConfig.setupAllValues();

        TransformersFinder finder = new TransformersFinder();
        LOGGER.log(Level.INFO, "Coremod began to search...");
        List<MouseLoadEvent> events = finder.registerHooks();

        Map<MinecraftClass, List<MinecraftClass>> interfaceAdderHooks = new HashMap<>();
        List<MouseHookAdapter> rawHooks = new ArrayList<>();
        Map<MinecraftClass, Map<String, MouseMethod>> methodRefractorHooks = new HashMap<>();
        Map<MinecraftClass, Map<String, MouseMethod>> methodAdderHooks = new HashMap<>();
        for (MouseLoadEvent event : events) {
            if (!event.isEmpty()) {
                interfaceAdderHooks.putAll(event.getInterfaceAdders());
                rawHooks.addAll(event.getAdapters());
                event.getMethodRefactors().forEach((key, value) -> methodRefractorHooks.put(key, ImmutableMap.copyOf(value)));
                event.getMethodAdders().forEach((key, value) -> methodRefractorHooks.put(key, ImmutableMap.copyOf(value)));
            }
        }

        this.interfaceAdderHooks = ImmutableMap.copyOf(interfaceAdderHooks);
        this.rawHooks = ImmutableList.copyOf(rawHooks);
        this.methodAdderHooks = ImmutableMap.copyOf(methodAdderHooks);
        this.methodRefractorHooks = ImmutableMap.copyOf(methodRefractorHooks);
    }

    public static boolean isDeobf() {
        return deobfEnvironment;
    }

    public boolean isEmpty() {
        return interfaceAdderHooks.isEmpty() && rawHooks.isEmpty() && methodAdderHooks.isEmpty() && methodRefractorHooks.isEmpty();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"ru.mousecray.mousecore.core.MousecoreInnerTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "ru.mousecray.mousecore.core.MousecoreContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(@Nonnull Map<String, Object> data) {
        deobfEnvironment = !((Boolean) data.get("runtimeDeobfuscationEnabled"));
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}