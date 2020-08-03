package ru.mousecray.mousecore.core.find;

import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.core.visit.CheckMouseVisitor;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.mousecray.mousecore.core.Mousecore.LOGGER;

public class TransformersFinder {
    private final List<String> mouseContainers = new ArrayList<>();

    public TransformersFinder() {
        findMouseContainers();
    }

    private List<String> findMouseContainers() {
        Path developPath = Paths.get("../build/classes/main/");
        if (developPath.toFile().exists()) {
            try {
                Stream<Path> walk = Files.walk(developPath);
                List<File> files = walk
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(f -> f.getName().endsWith(".class"))
                        .collect(Collectors.toList());
                walk.close();
                for (File file : files) {
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        String candidatePath = file.getPath();
                        checkMouseContainer(candidatePath
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
        return mouseContainers;
    }

    private void checkMouseContainer(String name, byte[] bytes) {
        if (new CheckMouseVisitor().getResult(bytes)) mouseContainers.add(name);
    }

    @Nonnull
    public List<MouseLoadEvent> registerHooks() {
        List<MouseLoadEvent> loadEventList = new ArrayList<>();
        if (mouseContainers.isEmpty()) LOGGER.log(Level.WARN, "List of hooks is empty!");
        else {
            int containersCount = 0;
            for (String name : mouseContainers) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.ERROR, "Error while loading " + MouseContainer.class.getSimpleName() + " " + name + "!");
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (method.isAccessible() && method.isAnnotationPresent(Mod.EventHandler.class)) {
                            MouseLoadEvent event = new MouseLoadEvent();
                            try { method.invoke(clazz, event); } catch (IllegalAccessException | InvocationTargetException e) {
                                LOGGER.log(Level.ERROR, "Method annotated as " +
                                        Mod.EventHandler.class.getSimpleName() + " in " +
                                        MouseContainer.class.getSimpleName() + " \"" + clazz.getSimpleName() +
                                        "\" can't be invoked! This container will be skipped!");
                                e.printStackTrace();
                                event = null;
                            }
                            if (event != null && !event.isEmpty()) loadEventList.add(event);
                        }
                    }
                    ++containersCount;
                }
            }
            LOGGER.log(Level.INFO, "Coremod found " + containersCount + " HookContainers");
        }
        return loadEventList;
    }

//    /**
//     * Read, check and load transformers
//     */
//    private void findHooks() {
//        File mcModsDir = new File("./mods/");
//        File[] mods = mcModsDir.listFiles(filter -> {
//            String name = filter.getName();
//            return filter.isFile() && name.endsWith(".jar");
//        });
//        if (mods == null) throw new IllegalStateException("Minecraft mods package is have problems. Mousecore cannot loaded!");
//        for (File mod : mods)
//            try (ZipFile selectedMod = new ZipFile(mod)) {
//                Enumeration<? extends ZipEntry> modFiles = selectedMod.entries();
//
//                while (modFiles.hasMoreElements()) {
//                    ZipEntry entry = modFiles.nextElement();
//                    String name = entry.getName();
//                    if (!entry.isDirectory() && name.endsWith(".class")) {
//                        try (InputStream inputFile = selectedMod.getInputStream(entry)) {
//                            checkMouseClass(name
//                                    .substring(0, name.length() - 6)
//                                    .replace('/', '.'), IOUtils.toByteArray(inputFile));
//                        } catch (IOException e) {
//                            LOGGER.log(Level.FATAL, "Error while loading \"" + name + "\"! It will be skipped.");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                LOGGER.log(Level.FATAL, "Error while loading \"" + mod.getName() + "\"! It will be skipped.");
//                e.printStackTrace();
//            }
//
//        Path developPath = Paths.get("../build/classes/main/");
//        if (developPath.toFile().exists()) {
//            try (Stream<Path> walk = Files.walk(developPath)) {
//                List<File> files = walk
//                        .filter(Files::isRegularFile)
//                        .map(Path::toFile)
//                        .filter(f -> f.getName().endsWith(".class"))
//                        .collect(Collectors.toList());
//                for (File file : files) {
//                    try (FileInputStream inputStream = new FileInputStream(file)) {
//                        String candidatePath = file.getPath();
//                        checkMouseClass(candidatePath
//                                .substring(22, candidatePath.length() - 6)
//                                .replace("\\", "."), IOUtils.toByteArray(inputStream));
//                    } catch (IOException e) {
//                        LOGGER.log(Level.FATAL, "Error while loading \"" + file.getName() + "\"! It will be skipped.");
//                        e.printStackTrace();
//                    }
//                }
//            } catch (IOException e) {
//                LOGGER.log(Level.FATAL, "Error while loading develop files directory! It will be skipped.");
//                e.printStackTrace();
//            }
//        }
//    }
}