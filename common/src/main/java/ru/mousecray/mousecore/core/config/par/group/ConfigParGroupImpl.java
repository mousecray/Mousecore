/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroup;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public abstract class ConfigParGroupImpl<T extends CustomType<?>> extends ConfigParBaseImpl<T> implements ConfigParGroup<T> {
    @Nullable public final ConfigParDisablerImpl disablePar;

    public ConfigParGroupImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<T>> valuePair,
            @Nullable String comment, @Nullable Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> values
    ) {
        this(name, disablePar != null, disablePar, valuePair, comment, values);
    }

    public ConfigParGroupImpl(
            DisplayName name,
            boolean canBeDisabled,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<T>> valuePair,
            @Nullable String comment, @Nullable Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> values
    ) {
        super(name, canBeDisabled || disablePar != null, valuePair, values, comment);
        this.disablePar = disablePar;
        if (disablePar != null) {
            disablePar.setParent(this);
            disablePar.setDeep(deep + 1);
            disablePar.setConfig(config);
            isDisabled = disablePar.isDisabled();
        }
    }

    public boolean hasDisabler() { return disablePar != null; }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void refreshThreadSafeFlag() {
        super.refreshThreadSafeFlag();
        if (hasDisabler()) disablePar.setThreadSafeByParent(isThreadSafe());
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void setDeep(int deep) {
        super.setDeep(deep);
        if (hasDisabler()) disablePar.setDeep(deep + 1);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void setConfig(@Nullable MouseConfigImpl config) {
        super.setConfig(config);
        if (hasDisabler()) disablePar.setConfig(config);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void setDisabled(boolean disabled) {
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigParGroup does not support disabling");
        if (config != null && config.isBuilt()) {
            ReentrantReadWriteLock                                     lock = config.getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch;
            if (lock != null) lock.writeLock().lock();
            try {
                if (hasDisabler()) {
                    disablePar.setDisabledDirect(disabled);
                    isDisabled = disablePar.isDisabled();
                } else {
                    isDisabled = disabled;
                }
                config.reloadCacheDirect();
                batch = config.collectChangesIfListening();
            } finally {
                if (lock != null) lock.writeLock().unlock();
            }
            config.dispatchChanges(batch);
        } else {
            ReentrantReadWriteLock lock = getSyncLock();
            if (lock != null) lock.writeLock().lock();
            try {
                if (hasDisabler()) {
                    disablePar.setDisabledDirect(disabled);
                    isDisabled = disablePar.isDisabled();
                } else {
                    isDisabled = disabled;
                }
                setDisabledStructurally(isStructurallyDisabled);
            } finally {
                if (lock != null) lock.writeLock().unlock();
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onReset() {
        super.onReset();
        if (hasDisabler()) {
            disablePar.onReset();
            isDisabled = disablePar.isDisabled();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onReloadCache() {
        if (hasDisabler()) {
            disablePar.onReloadCache();
            isDisabled = disablePar.isDisabled();
        }
        super.onReloadCache();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onSyncListeners() {
        if (hasDisabler()) disablePar.onSyncListeners();
        super.onSyncListeners();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onCollectChanges(List<ConfigListenerDispatcher.PendingValueNotification<?>> out) {
        if (hasDisabler()) disablePar.onCollectChanges(out);
        super.onCollectChanges(out);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void setDisabledStructurally(boolean parentDisabled) {
        isStructurallyDisabled = parentDisabled;
        if (hasDisabler()) {
            disablePar.setDisabledStructurally(parentDisabled);
            isDisabled = disablePar.isDisabled();
        }
    }

    @Override public boolean isDisabled() { return super.isDisabled() || getConfigVal().isConstrainedDisabled(); }
    @Override public boolean isFrozen()   { return super.isFrozen() || getConfigVal().isConstrainedDisabled(); }
}