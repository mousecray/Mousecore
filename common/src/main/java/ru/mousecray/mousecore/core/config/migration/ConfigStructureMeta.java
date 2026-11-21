/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.migration;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigStructureMeta {
    private static final int MAGIC = 0x4D4F434D; //"MOCM"

    private final int         schemaVersion;
    private final int         structureHash;
    private final Set<String> allPaths;
    private final Set<String> pendingNewPaths;
    private       boolean     dirty = false;

    public ConfigStructureMeta(int schemaVersion, Set<String> allPaths, Set<String> pendingNewPaths) {
        this.schemaVersion = schemaVersion;
        this.allPaths = new TreeSet<>(Objects.requireNonNull(allPaths));
        this.pendingNewPaths = new TreeSet<>(Objects.requireNonNull(pendingNewPaths));
        structureHash = computeStructureHash(this.allPaths);
    }

    public int getSchemaVersion()           { return schemaVersion; }
    public int getStructureHash()           { return structureHash; }
    public Set<String> getAllPaths()        { return Collections.unmodifiableSet(allPaths); }
    public Set<String> getPendingNewPaths() { return Collections.unmodifiableSet(pendingNewPaths); }

    public boolean isPendingNew(String path) {
        return pendingNewPaths.contains(path);
    }

    /**
     * Remove New State on editing by user
     */
    public boolean dismissNew(String path) {
        if (pendingNewPaths.remove(path)) {
            dirty = true;
            return true;
        }
        return false;
    }

    public static int computeStructureHash(Set<String> sortedPaths) {
        int hash = 1;
        for (String p : sortedPaths) {
            hash = 31 * hash + p.hashCode();
        }
        return hash;
    }

    public static File getMetaFile(File configParentDir, String configName) {
        File dir = new File(configParentDir, "." + configName + File.separator + "binary");
        return new File(dir, "meta.bin");
    }

    @Nullable
    public static ConfigStructureMeta load(File metaFile, @Nullable MouseLogger logger) {
        if (!metaFile.exists()) return null;

        try (DataInputStream in = new DataInputStream(new GZIPInputStream(Files.newInputStream(metaFile.toPath())))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                if (logger != null) {
                    logger.atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("Corrupted binary metadata in '{0}'. Ignored", metaFile.getName());
                }
                return null;
            }

            int version   = in.readInt();
            int savedHash = in.readInt();

            int         allCount = in.readInt();
            Set<String> allPaths = new TreeSet<>();
            for (int i = 0; i < allCount; i++) allPaths.add(in.readUTF());

            int         newCount = in.readInt();
            Set<String> newPaths = new TreeSet<>();
            for (int i = 0; i < newCount; i++) newPaths.add(in.readUTF());

            return new ConfigStructureMeta(version, allPaths, newPaths);
        } catch (Exception e) {
            if (logger != null) {
                logger.atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("Failed to read config binary metadata: {0}", e.getMessage());
            }
            return null;
        }
    }

    public boolean save(File metaFile, @Nullable MouseLogger logger) {
        if (metaFile.exists() && !dirty) {
            ConfigStructureMeta existing = load(metaFile, null);
            if (existing != null && existing.schemaVersion == schemaVersion
                    && existing.structureHash == structureHash
                    && existing.pendingNewPaths.equals(pendingNewPaths)) {
                return false;
            }
        }

        File parentDir = metaFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) return false;

        File tempFile = new File(parentDir, metaFile.getName() + "~");
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(tempFile.toPath())))) {
            out.writeInt(MAGIC);
            out.writeInt(schemaVersion);
            out.writeInt(structureHash);

            out.writeInt(allPaths.size());
            for (String p : allPaths) out.writeUTF(p);

            out.writeInt(pendingNewPaths.size());
            for (String p : pendingNewPaths) out.writeUTF(p);

            out.flush();
        } catch (IOException e) {
            if (logger != null) {
                logger.atError()
                        .withException(e)
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.RED_BG)
                        .log("Failed to save binary config metadata: {0}", metaFile.getName());
            }
            return false;
        }

        try {
            Files.move(tempFile.toPath(), metaFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            dirty = false;
            return true;
        } catch (IOException e) {
            try {
                Files.move(tempFile.toPath(), metaFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dirty = false;
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    /**
     * Creates structure metadata when transitioning to a new schema version.
     *
     * <p>Parameters that appeared in {@code currentPaths} relative to {@code oldMeta}
     * (excluding renamed paths) are marked as {@code pendingNew}. Any paths that were
     * already pending in {@code oldMeta} are carried over as long as they are still
     * present in {@code currentPaths}.</p>
     *
     * <p>On a fresh install (or when no previous metadata is available), the set of
     * pending-new paths is left empty.</p>
     *
     * @param currentSchemaVersion the schema version being transitioned to
     * @param currentPaths         all paths present in the current config structure
     * @param oldMeta              metadata from the previous schema version, or
     *                             {@code null} if none is available (e.g. first launch)
     * @param renamedPaths         paths that were explicitly renamed and should not
     *                             be treated as newly added
     * @param isFreshInstall       {@code true} if this is a fresh installation,
     *                             in which case no paths are marked as pending-new
     * @return newly constructed structure metadata for the current schema version
     */
    public static ConfigStructureMeta createForVersionTransition(
            int currentSchemaVersion,
            Set<String> currentPaths,
            @Nullable ConfigStructureMeta oldMeta,
            Set<String> renamedPaths,
            boolean isFreshInstall
    ) {
        Set<String> newPaths = new TreeSet<>();

        if (!isFreshInstall && oldMeta != null) {
            for (String p : currentPaths) if (!oldMeta.allPaths.contains(p) && !renamedPaths.contains(p)) newPaths.add(p);
            for (String pending : oldMeta.pendingNewPaths) if (currentPaths.contains(pending)) newPaths.add(pending);
        }

        return new ConfigStructureMeta(currentSchemaVersion, currentPaths, newPaths);
    }
}