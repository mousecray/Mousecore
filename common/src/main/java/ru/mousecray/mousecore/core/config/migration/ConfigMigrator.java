/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.migration;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.migration.ConfigMigrationStep;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigMigrator {
    public record MigrationRecord(int fromVersion, int toVersion, ConfigMigrationStep step) { }

    private final List<MigrationRecord> migrations = new ArrayList<>();

    public void addMigration(int fromVersion, int toVersion, ConfigMigrationStep step) {
        if (fromVersion >= toVersion) {
            throw new IllegalArgumentException("fromVersion (" + fromVersion + ") must be less than toVersion (" + toVersion + ")");
        }
        migrations.add(new MigrationRecord(fromVersion, toVersion, Objects.requireNonNull(step)));
    }

    public boolean canMigrate(int fromVersion, int targetVersion) {
        if (fromVersion == targetVersion) return true;
        if (fromVersion > targetVersion) return false;
        return findMigrationChain(fromVersion, targetVersion) != null;
    }

    @Nullable
    public List<MigrationRecord> findMigrationChain(int fromVersion, int targetVersion) {
        if (fromVersion == targetVersion) return Collections.emptyList();

        List<MigrationRecord> chain   = new ArrayList<>();
        int                   current = fromVersion;

        while (current < targetVersion) {
            int curr = current;
            Optional<MigrationRecord> step = migrations.stream()
                    .filter(m -> m.fromVersion() == curr && m.toVersion() <= targetVersion)
                    .max(Comparator.comparingInt(MigrationRecord::toVersion));

            if (step.isEmpty()) return null;

            MigrationRecord rec = step.get();
            chain.add(rec);
            current = rec.toVersion();
        }

        return chain;
    }

    public Map<String, String> migrate(
            int fileVersion, int targetVersion, Map<String, String> rawData,
            Set<String> outRenamedPaths, @Nullable MouseLogger logger
    ) {
        if (fileVersion == targetVersion) return rawData;

        List<MigrationRecord> chain = findMigrationChain(fileVersion, targetVersion);
        if (chain == null) {
            if (logger != null) {
                logger.atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("No migration path from schema v{0} to v{1}. Raw data will be used as is", fileVersion, targetVersion);
            }
            return rawData;
        }

        Map<String, String> currentData = new LinkedHashMap<>(rawData);
        for (MigrationRecord step : chain) {
            if (logger != null) {
                logger.atInfo()
                        .withPrefix(LogPrefixes.CONFIG)
                        .log("Applying config schema migration: v{0} -> v{1}", step.fromVersion(), step.toVersion());
            }
            MigrationContextImpl ctx = new MigrationContextImpl(step.fromVersion(), step.toVersion(), currentData);
            step.step().migrate(ctx);
            currentData = ctx.getRawData();
            outRenamedPaths.addAll(ctx.getRenames().values());
        }

        return currentData;
    }
}