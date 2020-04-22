package ru.mousecray.mousecore.core;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class TransformerBytesManager {
    private Map<String, byte[]> transformerBytes = new HashMap<>();

    public TransformerBytesManager(Consumer<Triple<byte[], String, Map<String, byte[]>>> consumer) throws IOException {
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

                if (!entry.isDirectory() && name.endsWith(".class"))
                    consumer.accept(Triple.of(IOUtils.toByteArray(selectedMod.getInputStream(entry)), name, transformerBytes));
            }
        }
    }

    public Map<String, byte[]> getTransformerBytes() {
        return transformerBytes;
    }
}