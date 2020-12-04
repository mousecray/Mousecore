package ru.mousecray.mousecore.core.transformer;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.event.MouseLoadEvent;
import ru.mousecray.mousecore.core.MousecoreConfig;
import ru.mousecray.mousecore.core.utils.CheckMouseVisitor;
import ru.mousecray.mousecore.core.utils.InterfaceHookException;
import ru.mousecray.mousecore.core.utils.MouseMethodException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.mousecray.mousecore.api.asm.event.MouseLoadEvent.LOGGER;

public class TransformersFinder {
    private final List<String> mouseContainers = new ArrayList<>();
    private final List<Object> mouseContainerObjs = new ArrayList<>();
    private final int[] returnData = new int[6];

    public TransformersFinder() {
        findMouseContainers();
    }

    private void findMouseContainers() {
        Path developPath = Paths.get(MousecoreConfig.devPath);
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
                    try {
                        String name = file.getPath().substring(MousecoreConfig.devPath.length(), file.getPath().length() - 6).replace("\\", ".");
                        checkMouseContainer(name, MouseLoadEvent.getClassLoader().getClassBytes(name));
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
    }

    private void checkMouseContainer(String name, byte[] bytes) {
        if (new CheckMouseVisitor().getResult(bytes)) mouseContainers.add(name);
    }

    private void initializeContainers() {
        int containersCount = 0;
        if (mouseContainers.isEmpty()) LOGGER.log(Level.WARN, "List of hooks is empty!");
        else {
            for (String name : mouseContainers) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.ERROR, "Error while loading " + MouseContainer.class.getSimpleName() + " " + name + "!");
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Object initClazz = null;
                    try {
                        initClazz = clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.log(Level.ERROR, "Error while initialize " + MouseContainer.class.getSimpleName() + " " + name + "!");
                        e.printStackTrace();
                    }
                    if (initClazz != null) mouseContainerObjs.add(initClazz);
                    ++containersCount;
                }
            }
            mouseContainers.clear();
        }
        returnData[0] = containersCount;
        returnData[1] = mouseContainerObjs.size();
    }

    @Nonnull
    public List<MouseLoadEvent> registerHooks() {
        initializeContainers();

        List<MouseLoadEvent> loadEventList = new ArrayList<>();
        int adaptersCount = 0;
        int interfaceAdderCount = 0;
        int methodCount = 0;
        int methodAdderCount = 0;
        if (mouseContainerObjs.isEmpty()) LOGGER.log(Level.WARN, "List of loaded hooks is empty!");
        else {
            for (Object obj : mouseContainerObjs) {
                Method[] methods = obj.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Mod.EventHandler.class)) {
                        if (Modifier.isPublic(method.getModifiers())
                                && !Modifier.isStatic(method.getModifiers())
                                && method.getReturnType() == void.class) {
                            MouseLoadEvent event = new MouseLoadEvent();
                            try {
                                method.invoke(obj, event);
                                adaptersCount += event.size(0);
                                interfaceAdderCount += event.size(1);
                                methodCount += event.size(2);
                                methodAdderCount += event.size(3);
                                loadEventList.add(event);
                                continue;
                            } catch (IllegalAccessException ignore) {} catch (InvocationTargetException err) {
                                Throwable target = err.getTargetException();
                                if (target instanceof InterfaceHookException || target instanceof MouseMethodException)
                                    throw (RuntimeException) target;
                                target.printStackTrace();
                            }
                        }
                    } else continue;
                    LOGGER.log(Level.ERROR, "Method \"" + method.getName() + "\" annotated as @" +
                            Mod.EventHandler.class.getSimpleName() + " in " +
                            MouseContainer.class.getSimpleName() + " \"" + obj.getClass().getSimpleName() +
                            "\" can't be invoked! Please make sure that the method matches the \"public void\" " +
                            "signature and that only MouseLoadEvent is specified in its arguments.");
                }
            }
        }
        returnData[2] = adaptersCount;
        returnData[3] = interfaceAdderCount;
        returnData[4] = methodCount;
        returnData[5] = methodAdderCount;

        printStatus();

        return loadEventList;
    }

    private void printStatus() {
        LOGGER.log(Level.INFO, "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        LOGGER.log(Level.INFO, " " + correct("Coremod found " + returnData[0] + " HookContainer(s)"));
        LOGGER.log(Level.INFO, " " + correct("Coremod initialized " + returnData[1] + " HookContainer(s)"));
        LOGGER.log(Level.INFO, " " + correct("Coremod registered " + returnData[2] + " hook adapters(s)"));
        LOGGER.log(Level.INFO, " " + correct("Coremod registered " + returnData[5] + " method creator(s)"));
        LOGGER.log(Level.INFO, " " + correct("Coremod registered " + returnData[4] + " method refactor(s)"));
        LOGGER.log(Level.INFO, " " + correct("Coremod registered " + returnData[3] + " interface creator(s)"));
        LOGGER.log(Level.INFO, "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
    }

    private String correct(String s) {
        StringBuilder builder = new StringBuilder(s);
        int factor = 44 - s.length();
        for (int i = 0; i < factor; ++i) {
            if (i < factor / 2) builder.insert(0, " ");
            else builder.append(" ");
        }

        return builder.toString();
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