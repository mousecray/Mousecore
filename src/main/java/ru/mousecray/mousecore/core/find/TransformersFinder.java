package ru.mousecray.mousecore.core.find;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.core.load.ByteClassLoader;
import ru.mousecray.mousecore.core.visit.CheckMouseVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static ru.mousecray.mousecore.core.Mousecore.LOGGER;

public class TransformersFinder {
    public static final String mouseClassDescriptor = "\\\\mouseClass";
    private Map<String, Class<?>> mouseContainers = new HashMap<>();
    private Map<String, Class<?>> mouseClasses = new HashMap<>();
    private ByteClassLoader loader = new ByteClassLoader(getClass().getClassLoader());

    public TransformersFinder() {
        findHooks();
        putClassesToFML();
    }

    public Map<String, Object> findHookContainers() {
        Map<String, Object> hookContainers = new HashMap<>();
        mouseContainers.forEach((id, clazz) -> {
            Object obj;
            try {
                obj = clazz.newInstance();
                Field[] fields = clazz.getFields();
                for (Field field : fields) {
                    if (field.isAccessible() && field.isAnnotationPresent(Mod.Instance.class)) {
                        field.set(obj, obj);
                    }
                }
                hookContainers.put(id, obj);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.log(Level.ERROR, MouseContainer.class.getSimpleName() + " \"" + id + "\" can't created. It will be skipped!");
                e.printStackTrace();
            }
        });
        return hookContainers;
    }

    @SuppressWarnings("unchecked")
    private void putClassesToFML() {
        LaunchClassLoader launchCL;
        if (getClass().getClassLoader() instanceof LaunchClassLoader) {
            launchCL = (LaunchClassLoader) getClass().getClassLoader();
        }
        else throw new IllegalStateException("Mousecore loaded with incompatible class loader");

        try {
            Field f = launchCL.getClass().getDeclaredField("cachedClasses");
            f.setAccessible(true);
            ConcurrentHashMap<String, Class<?>> map = (ConcurrentHashMap<String, Class<?>>) f.get(launchCL);
            mouseClasses.forEach(map::put);
        } catch (Exception e) { e.printStackTrace(); } finally {
            mouseClasses.clear();
            mouseClasses = null;
        }
    }

    /**
     * Read, check and load transformers
     */
    private void findHooks() {
        File mcModsDir = new File("./mods/");
        File[] mods = mcModsDir.listFiles(filter -> {
            String name = filter.getName();
            return filter.isFile() && name.endsWith(".jar");
        });
        if (mods == null) throw new IllegalStateException("Minecraft mods package is have problems. Mousecore can't loaded!");
        for (File mod : mods)
            try (ZipFile selectedMod = new ZipFile(mod)) {
                Enumeration<? extends ZipEntry> modFiles = selectedMod.entries();

                while (modFiles.hasMoreElements()) {
                    ZipEntry entry = modFiles.nextElement();
                    String name = entry.getName();
                    if (!entry.isDirectory() && name.endsWith(".class")) {
                        try (InputStream inputFile = selectedMod.getInputStream(entry)) {
                            checkMouseClass(name
                                    .substring(0, name.length() - 6)
                                    .replace('/', '.'), IOUtils.toByteArray(inputFile));
                        } catch (IOException e) {
                            LOGGER.log(Level.FATAL, "Error while loading \"" + name + "\"! It will be skipped.");
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.FATAL, "Error while loading \"" + mod.getName() + "\"! It will be skipped.");
                e.printStackTrace();
            }

        Path developPath = Paths.get("../build/classes/main/");
        if (developPath.toFile().exists())
            try (Stream<Path> walk = Files.walk(developPath)) {
                List<File> files = walk
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().endsWith(".class"))
                        .collect(Collectors.toList());
                for (File file : files) {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        String candidatePath = file.getPath();
                        checkMouseClass(candidatePath
                                .substring(22, candidatePath.length() - 6)
                                .replace("\\", "."), IOUtils.toByteArray(inputStream));
                    } catch (IOException e) {
                        LOGGER.log(Level.FATAL, "Error while loading \"" + file.getName() + "\"! It will be skipped.");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.FATAL, "Error while loading develop files directory! It will be skipped.");
                e.printStackTrace();
            }
    }

    private void checkMouseClass(String name, byte[] bytes) {
        String hookid = new CheckMouseVisitor().getResult(bytes);
        if (hookid != null) {
            if (hookid == mouseClassDescriptor) mouseClasses.put(name, loader.defineClass(name, bytes));
            else mouseContainers.put(hookid, loader.defineClass(name, bytes));
        }
    }
}