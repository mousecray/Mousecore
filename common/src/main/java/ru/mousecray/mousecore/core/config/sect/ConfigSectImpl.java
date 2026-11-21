/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ParametersAreNonnullByDefault
public class ConfigSectImpl extends ConfigSectBaseImpl implements ConfigSect {
    @Nullable public final ConfigParDisablerImpl disablePar;

    public ConfigSectImpl(DisplayName name)                                             { this(name, false, null, null); }
    public ConfigSectImpl(DisplayName name, @Nullable String comment)                   { this(name, false, null, comment); }
    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar) { this(name, disablePar != null, disablePar, null); }
    public ConfigSectImpl(DisplayName name, @Nullable ConfigParDisablerImpl disablePar, @Nullable String comment) {
        this(name, disablePar != null, disablePar, comment);
    }

    public ConfigSectImpl(
            DisplayName name, boolean canBeDisabled,
            @Nullable ConfigParDisablerImpl disablePar, @Nullable String comment
    ) {
        super(name, canBeDisabled || disablePar != null, comment);
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
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigSection does not support disabling");
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
        boolean effective = isDisabled();
        if (hasChildren()) children.forEach((key, value) -> value.setDisabledStructurally(effective));
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
        boolean effective = isDisabled();
        if (hasChildren()) children.forEach((key, value) -> value.setDisabledStructurally(effective));
    }

    @Override public boolean isSupportChildren() { return true; }

    @Override
    public <T extends ConfigSectBase> boolean isSupportChild(T child) {
        return child instanceof ConfigSectImpl || child instanceof ConfigParBaseImpl;
    }
}