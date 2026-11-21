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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigBackupManager {
    private static final int               MAX_BACKUPS = 10;
    private static final DateTimeFormatter FORMATTER   = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ConfigBackupManager() { }

    public static File getBackupDir(File configParentDir, String configName) {
        return new File(configParentDir, "." + configName + File.separator + "backups");
    }

    @Nullable
    public static File createBackup(File configFile, String configName, int fromVersion, @Nullable MouseLogger logger) {
        if (!configFile.exists()) return null;

        File backupDir = getBackupDir(configFile.getParentFile(), configName);
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            if (logger != null) {
                logger.atError()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.RED_BG)
                        .log("Failed to create backup directory: {0}", backupDir.getAbsolutePath());
            }
            return null;
        }

        String timestamp      = LocalDateTime.now().format(FORMATTER);
        String backupFileName = configName + "_v" + fromVersion + "_" + timestamp + ".moc.bak";
        File   backupFile     = new File(backupDir, backupFileName);

        try {
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (logger != null) {
                logger.atInfo()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.GREEN_BG)
                        .log("Created config backup: {0}", backupFile.getName());
            }
            rotateBackups(backupDir, configName, logger);
            return backupFile;
        } catch (IOException e) {
            if (logger != null) {
                logger.atError()
                        .withException(e)
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.RED_BG)
                        .log("Failed to create backup of config file '{0}'", configFile.getName());
            }
            return null;
        }
    }

    private static void rotateBackups(File backupDir, String configName, @Nullable MouseLogger logger) {
        File[] files = backupDir.listFiles((dir, name) -> name.startsWith(configName) && name.endsWith(".bak"));
        if (files == null || files.length <= MAX_BACKUPS) return;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        int toDelete = files.length - MAX_BACKUPS;
        for (int i = 0; i < toDelete; i++) {
            try {
                Files.deleteIfExists(files[i].toPath());
            } catch (IOException e) {
                if (logger != null) logger.atDebug().log("Failed to delete old backup: {0}", files[i].getName());
            }
        }
    }
}