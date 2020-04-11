package ru.mousecray.mousecore.core;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MousecoreHook;
import ru.mousecray.mousecore.core.classvisitors.AnnotationClassVisitor;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class TransformerBytesManager {
    private Map<String, byte[]> transformerBytes = new HashMap<>();

    public TransformerBytesManager() throws IOException {
        File mcModsDir = new File("./mods/");
        File[] mods = mcModsDir.listFiles(filter -> {
            String name = filter.getName();
            return filter.isFile() && name.endsWith(".jar");
        });
        if (mods == null) {
            Mousecore.LOGGER.log(Level.ERROR, "Minecraft mods package is have problems. Coremod is not loaded correctly.");
            throw new IllegalStateException();
        }
        for (File mod : mods) {
            ZipFile selectedMod = new ZipFile(mod);
            Enumeration<? extends ZipEntry> modFiles = selectedMod.entries();

            while (modFiles.hasMoreElements()) {
                ZipEntry entry = modFiles.nextElement();
                String name = entry.getName();

                if (!entry.isDirectory() && name.endsWith(".class")) {
                    byte[] bytes = IOUtils.toByteArray(selectedMod.getInputStream(entry));

                    if (new AnnotationClassVisitor(MousecoreHook.class).getResult(bytes))
                        transformerBytes.put(
                                name.substring(0, name.length() - 6).replace('/', '.'), bytes);
                }
            }
        }
    }

    public Map<String, byte[]> getTransformerBytes() {
        return transformerBytes;
    }
}