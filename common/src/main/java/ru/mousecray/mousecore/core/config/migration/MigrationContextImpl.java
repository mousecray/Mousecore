/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.migration;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.migration.MigrationContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class MigrationContextImpl implements MigrationContext {
    private final int                 fromVersion;
    private final int                 toVersion;
    private final Map<String, String> rawData;
    private final Map<String, String> renames = new LinkedHashMap<>();

    public MigrationContextImpl(int fromVersion, int toVersion, Map<String, String> rawData) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.rawData = new LinkedHashMap<>(Objects.requireNonNull(rawData));
    }

    @Override public int getFromVersion()                 { return fromVersion; }
    @Override public int getToVersion()                   { return toVersion; }

    @Override public boolean has(String path)             { return rawData.containsKey(path); }
    @Nullable @Override public String getRaw(String path) { return rawData.get(path); }

    @Override
    public void rename(String oldPath, String newPath) {
        Objects.requireNonNull(oldPath);
        Objects.requireNonNull(newPath);
        if (rawData.containsKey(oldPath)) {
            String value = rawData.remove(oldPath);
            rawData.put(newPath, value);
            renames.put(oldPath, newPath);
        }
    }

    @Override
    public void transform(String path, Function<String, String> transformer) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(transformer);
        if (rawData.containsKey(path)) {
            String current     = rawData.get(path);
            String transformed = transformer.apply(current);
            if (transformed != null) rawData.put(path, transformed);
            else rawData.remove(path);
        }
    }

    @Override
    public void remove(String path) {
        Objects.requireNonNull(path);
        rawData.remove(path);
    }

    @Override
    public void putRaw(String path, String rawValue) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(rawValue);
        rawData.put(path, rawValue);
    }

    @Override public Map<String, String> getRawData() { return rawData; }
    public Map<String, String> getRenames()           { return renames; }
}