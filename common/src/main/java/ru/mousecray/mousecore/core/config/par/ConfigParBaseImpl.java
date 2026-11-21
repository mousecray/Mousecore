/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.par;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectBaseImpl;
import ru.mousecray.mousecore.core.config.sync.ConfigSyncSnapshot;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class ConfigParBaseImpl<T extends CustomType<?>> extends ConfigSectBaseImpl implements ConfigParBase<T> {
    @Nonnull protected final ImmutableDisplayNameMap<ConfigVal<?>> values = new ImmutableDisplayNameMap.Mutable<>();
    @Nonnull protected final DisplayName                           firstValName;

    public ConfigParBaseImpl(
            DisplayName name, boolean canBeDisabled,
            Pair<DisplayName, ? extends ConfigValImpl<T>> valuePair,
            @Nullable Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> values, @Nullable String comment
    ) {
        super(name, canBeDisabled, comment);
        firstValName = Objects.requireNonNull(valuePair.getKey());
        this.values.put(firstValName, Objects.requireNonNull(valuePair.getValue()));
        valuePair.getValue().setOwner(this);
        if (values != null) {
            values.forEach((displayName, configVal) -> {
                if (isSupportAdditionalValue(Objects.requireNonNull(configVal))) {
                    this.values.put(Objects.requireNonNull(displayName), configVal);
                    configVal.setOwner(this);
                }
            });
        }
    }

    @Override
    public void setConfig(@Nullable MouseConfigImpl config) {
        MouseConfigImpl prev = this.config;
        super.setConfig(config);
        if (prev != config && config != null) {
            values.values().forEach(val -> {
                if (val instanceof ConfigValImpl<?> impl) impl.onConfigAttached(config);
            });
        }
    }

    @Nullable @Override
    public ConfigVal<? extends CustomType<?>> getValByDisplayName(String name) {
        return values.getByDisplayName(name);
    }

    @Override
    public void onReset() {
        super.onReset();
        values.values().forEach(val -> {
            if (val instanceof ConfigValImpl<?> impl) impl.onReset();
            else val.reset();
        });
    }

    @Override
    public void onReloadCache() {
        super.onReloadCache();
        values.values().forEach(val -> {
            if (val instanceof ConfigValImpl<?> impl) impl.onReloadCache();
        });
    }

    @Override
    public void onSyncListeners() {
        super.onSyncListeners();
        values.values().forEach(val -> {
            if (val instanceof ConfigValImpl<?> impl) impl.onSyncListeners();
        });
    }

    @Override
    public void onCollectChanges(List<ConfigListenerDispatcher.PendingValueNotification<?>> out) {
        super.onCollectChanges(out);
        values.values().forEach(val -> {
            if (val instanceof ConfigValImpl<?> impl) impl.onCollectChanges(out);
        });
    }

    @Override
    public void onClearNetworkOverride() {
        super.onClearNetworkOverride();
        values.values().forEach(val -> {
            if (val instanceof ConfigValImpl<?> impl) impl.onClearNetworkOverride();
        });
    }

    @Override
    public void onCollectSyncedValues(Map<String, ConfigSyncSnapshot.SyncEntry> out) {
        super.onCollectSyncedValues(out);
        ConfigValImpl<T> impl = getConfigVal();
        if (impl.getSide().isSynced()) {
            out.put(getFullInternalName(), impl.createSyncEntry());
        }
    }

    @Nonnull @Override public ImmutableDisplayNameMap<ConfigVal<?>> getValues()     { return new ImmutableDisplayNameMap<>(values); }

    @Override public boolean isSupportChildren()                                    { return false; }
    @Override public boolean isSupportChild(ConfigSectBase child)                   { return false; }
    public boolean isSupportAdditionalValue(ConfigVal<? extends CustomType<?>> val) { return true; }

    @Nullable @Override
    public final ConfigVal<? extends CustomType<?>> getConfigVal(String internalName) {
        return getCapacity() > 0 ? values.getByInternalName(internalName) : null;
    }

    @Nullable @Override
    public final ConfigVal<? extends CustomType<?>> getConfigVal(DisplayName name) {
        return getCapacity() > 0 ? values.get(name) : null;
    }

    @SuppressWarnings({ "DataFlowIssue", "unchecked" }) @Nonnull @Override
    public final ConfigValImpl<T> getConfigVal() { return (ConfigValImpl<T>) getConfigVal(firstValName); }

    @Override public int getCapacity() { return values.getSize(); }

    public void markDirty() {
        if (config != null && config.isBuilt()) config.markDirty(this);
    }

    @Override public IValType getType() { return getConfigVal().getType(); }
}