/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.CustomType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class ConfigParBase<T extends CustomType<?>> extends ConfigSectBase {
    @Nonnull protected final ImmutableDisplayNameMap<ConfigVal<?>> values = new ImmutableDisplayNameMap.Mutable<>();
    @Nonnull protected final DisplayName                           firstValName;

    public ConfigParBase(
            DisplayName name, boolean canBeDisabled,
            Pair<DisplayName, ? extends ConfigVal<T>> valuePair,
            @Nullable Map<DisplayName, ConfigVal<?>> values, @Nullable String comment
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

    @Nullable public ConfigVal getValByDisplayName(String name) { return values.getByDisplayName(name); }

    @Override
    public void reset() {
        super.reset();
        values.values().forEach(ConfigVal::reset);
        reloadCache();
    }

    @Override
    protected void reloadCache() {
        super.reloadCache();
        values.values().forEach(ConfigVal::reloadCache);
    }

    @Nonnull public ImmutableDisplayNameMap<ConfigVal<?>> getValues() { return new ImmutableDisplayNameMap<>(values); }

    @Override public boolean isSupportChildren()                      { return false; }
    @Override public boolean isSupportChild(ConfigSectBase child)     { return false; }
    public boolean isSupportAdditionalValue(ConfigVal<?> val)         { return true; }

    @Nullable
    public final ConfigVal<?> getConfigVal(String internalName) { return getCapacity() > 0 ? values.getByInternalName(internalName) : null; }

    @Nullable public final ConfigVal<?> getConfigVal(DisplayName name) { return getCapacity() > 0 ? values.get(name) : null; }

    @SuppressWarnings({ "DataFlowIssue", "unchecked" }) @Nonnull
    public final ConfigVal<T> getConfigVal() { return (ConfigVal<T>) getConfigVal(firstValName); }

    public int getCapacity()  { return values.getSize(); }
    public void markDirty()   { if (config != null && config.isBuilt()) config.markDirty(this); }

    public IValType getType() { return getConfigVal().getType(); }
}