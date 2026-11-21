/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.sync;

import net.minecraft.network.FriendlyByteBuf;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigSyncSnapshot {
    private static final int MAX_ENTRIES = 65536;
    private static final int MAX_STRING  = 32767;

    public record SyncEntry(boolean disabled, @Nullable String rawValue) { }

    private final String                 configName;
    private final Map<String, SyncEntry> entries;

    public ConfigSyncSnapshot(String configName, Map<String, SyncEntry> entries) {
        this.configName = Objects.requireNonNull(configName);
        Objects.requireNonNull(entries);
        this.entries = entries.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    public String getConfigName()              { return configName; }
    public Map<String, SyncEntry> getEntries() { return entries; }
    public boolean isEmpty()                   { return entries.isEmpty(); }
    public int size()                          { return entries.size(); }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeUtf(configName, 256);
        buf.writeVarInt(entries.size());
        for (Map.Entry<String, SyncEntry> entry : entries.entrySet()) {
            buf.writeUtf(entry.getKey(), 1024);
            SyncEntry val = entry.getValue();
            buf.writeBoolean(val.disabled());
            boolean hasRaw = val.rawValue() != null;
            buf.writeBoolean(hasRaw);
            if (hasRaw) buf.writeUtf(val.rawValue(), MAX_STRING);
        }
    }

    public static ConfigSyncSnapshot readFromBuf(FriendlyByteBuf buf) {
        String configName = buf.readUtf(256);
        int    size       = buf.readVarInt();
        if (size < 0 || size > MAX_ENTRIES) {
            throw new IllegalArgumentException("Invalid ConfigSyncSnapshot entry count: " + size);
        }

        Map<String, SyncEntry> map = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            String  path     = buf.readUtf(1024);
            boolean disabled = buf.readBoolean();
            boolean hasRaw   = buf.readBoolean();
            String  rawValue = hasRaw ? buf.readUtf(MAX_STRING) : null;
            map.put(path, new SyncEntry(disabled, rawValue));
        }
        return new ConfigSyncSnapshot(configName, map);
    }

    @Override
    public String toString() {
        return "ConfigSyncSnapshot{configName='" + configName + "', entries=" + entries.size() + '}';
    }
}