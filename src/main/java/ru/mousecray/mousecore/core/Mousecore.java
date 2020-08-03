package ru.mousecray.mousecore.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.core.find.TransformersFinder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("Mousecore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"ru.mousecray.mousecore"})
@IFMLLoadingPlugin.SortingIndex(1001)
public class Mousecore implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("mousecore");
    public static final List<MouseLoadEvent> events = new ArrayList<>();
    private static boolean deobfEnvironment;

    public Mousecore() {
        LOGGER.log(Level.INFO, "Coremod initialized");

        TransformersFinder finder = new TransformersFinder();
        events.addAll(finder.registerHooks());
    }

    public static boolean isDeobf() {
        return deobfEnvironment;
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