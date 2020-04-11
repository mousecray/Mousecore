package ru.mousecray.mousecore.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mousecray.mousecore.MousecoreContainer;

import javax.annotation.Nullable;
import javax.annotation.Tainted;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@IFMLLoadingPlugin.Name("Mousecore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"ru.mousecray.mousecore.core"})
@IFMLLoadingPlugin.SortingIndex(1001)
public class Mousecore implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("mousecore");
    private static boolean deobfEnvironment;
    private static Method findLoadedClass = null;

    public Mousecore() {
        LOGGER.log(Level.INFO, "Coremod initialized");
    }

    static boolean isDeobf() {
        return !deobfEnvironment;
    }

    /**
     * This method based of method in opensource HookLib by @GloomyFolken. Thank you very much!
     */
    @Nullable
    @Tainted
    public static Class getLoadedClass(String className) {
        if (findLoadedClass == null) try {
            findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            findLoadedClass.setAccessible(true);
        } catch (NoSuchMethodException e) {e.printStackTrace();}
        else try {
            return (Class) findLoadedClass.invoke(Mousecore.class.getClassLoader(), className);
        } catch (IllegalAccessException | InvocationTargetException e) {e.printStackTrace();}
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{MousecoreTransformer.class.getCanonicalName()};
    }

    @Override
    public String getModContainerClass() {
        return MousecoreContainer.class.getCanonicalName();
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        deobfEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}