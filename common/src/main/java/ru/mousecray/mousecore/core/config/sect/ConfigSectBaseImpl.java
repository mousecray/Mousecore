/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.sect;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigState;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.utils.ConfigEnvironmentController;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public abstract class ConfigSectBaseImpl implements ConfigSectBase {
    @Nonnull protected final  DisplayName                                         name;
    @Nullable protected       ConfigSectBaseImpl                                  parent;
    @Nullable protected final String                                              comment;
    protected                 ImmutableDisplayNameMap.Mutable<ConfigSectBaseImpl> children;
    protected final           boolean                                             canBeDisabled;
    protected                 boolean                                             canBeDisabledByParent;
    protected                 int                                                 deep;
    protected                 MouseConfigImpl                                     config;

    @Nullable protected ConfigSide           explicitSide;
    protected final     EnumSet<ConfigState> manualStates = EnumSet.noneOf(ConfigState.class);
    protected volatile  boolean              isNewState;

    protected volatile boolean setByConfig;
    protected volatile boolean isDisabled;
    protected volatile boolean isStructurallyDisabled;

    protected                  boolean                threadSafe;
    protected                  boolean                threadSafeByParent;
    private volatile           boolean                effectiveThreadSafe;
    @Nullable private volatile ReentrantReadWriteLock standaloneLock;

    public ConfigSectBaseImpl(DisplayName name, boolean canBeDisabled, @Nullable String comment) {
        this.name = Objects.requireNonNull(name);
        this.canBeDisabled = canBeDisabled;

        comment = MouseStrings.trimWith(comment, true, '\t');
        if (comment != null && comment.isEmpty()) comment = null;
        this.comment = comment;
    }

    public void addManualStates(@Nullable Collection<ConfigState> states) {
        if (states == null) return;
        for (ConfigState st : states) {
            if (st == null) continue;
            if (!st.isManual()) throw new IllegalArgumentException("ConfigState." + st.name() + " cannot be assigned manually");
            manualStates.add(st);
        }
    }

    @Nonnull @Override
    public ConfigSide getSide() {
        if (explicitSide != null) return explicitSide;
        if (parent != null) return parent.getSide();
        if (config != null) return config.getDefaultSide();
        return ConfigSide.COMMON;
    }

    @Nonnull @Override
    public EnumSet<ConfigState> getStates() {
        EnumSet<ConfigState> result = getManualStates();
        if (isNew()) result.add(ConfigState.NEW);
        if (isFrozen()) result.add(ConfigState.FROZEN);
        return result;
    }

    @Nonnull
    protected EnumSet<ConfigState> getManualStates() {
        return manualStates.isEmpty() ? EnumSet.noneOf(ConfigState.class) : EnumSet.copyOf(manualStates);
    }

    @Override public boolean isNew()                       { return isNewState; }
    @Override public boolean isFrozen()                    { return isStructurallyDisabled || getSide().isFrozenIn(ConfigEnvironmentController.getCurrent()); }
    @Override public boolean hasState(ConfigState state)   { return getStates().contains(state); }
    public void markNew(boolean isNew)                     { isNewState = isNew; }
    public void setExplicitSide(@Nullable ConfigSide side) { explicitSide = side; }
    public boolean hasExplicitSide()                       { return explicitSide != null; }

    public void setThreadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
        refreshThreadSafeFlag();
    }

    protected void setThreadSafeByParent(boolean value) {
        threadSafeByParent = value;
        refreshThreadSafeFlag();
    }

    protected void refreshThreadSafeFlag() {
        boolean effective = threadSafe || threadSafeByParent || (config != null && config.isThreadSafe());
        effectiveThreadSafe = effective;
        if (effective && config != null) config.getOrCreateSyncLock();
        if (hasChildren()) children.values().forEach(c -> c.setThreadSafeByParent(effective));
    }

    @Override
    public boolean isThreadSafe() {
        return effectiveThreadSafe;
    }

    @Nullable
    public ReentrantReadWriteLock getSyncLock() {
        if (!effectiveThreadSafe) return null;
        if (config != null) return config.getOrCreateSyncLock();
        ReentrantReadWriteLock lock = standaloneLock;
        if (lock == null) {
            synchronized (this) {
                lock = standaloneLock;
                if (lock == null) {
                    lock = new ReentrantReadWriteLock();
                    standaloneLock = lock;
                }
            }
        }
        return lock;
    }

    protected <R> R readLocked(Supplier<R> action) {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) return action.get();
        lock.readLock().lock();
        try { return action.get(); } finally { lock.readLock().unlock(); }
    }

    @Override public boolean hasChildren()                            { return isSupportChildren() && children != null && !children.isEmpty(); }
    @Override public boolean canBeDisabled()                          { return canBeDisabled || canBeDisabledByParent; }
    public boolean isStructurallyDisabled()                           { return isStructurallyDisabled; }
    @Override public boolean isDisabled()                             { return isDisabled || isStructurallyDisabled; }

    public <T extends ConfigSectBase> boolean isSupportChild(T child) { return true; }

    private boolean checkChildren() {
        if (!isSupportChildren()) throw new UnsupportedOperationException("This ConfigSection cannot contains children");
        if (children == null) {
            children = new ImmutableDisplayNameMap.Mutable<>();
            return true;
        } else return false;
    }

    @Override
    public boolean hasChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByDisplayName(name);
    }

    @Nullable @Override
    public ConfigSectBaseImpl getChildByDisplayName(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByDisplayName(name);
    }

    @Override
    public boolean hasChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return false;
        else return children.containsByInternalName(name);
    }

    @Nullable @Override
    public ConfigSectBaseImpl getChild(String name) {
        Objects.requireNonNull(name);
        if (checkChildren()) return null;
        else return children.getByInternalName(name);
    }

    public void addChild(ConfigSectBase section) {
        ConfigSectBaseImpl sectionImpl = (ConfigSectBaseImpl) Objects.requireNonNull(section);
        checkChildren();
        if (!isSupportChild(section)) {
            throw new UnsupportedOperationException("This ConfigSection cannot support child '" + section.getName().getInternalName() + "'");
        }
        ImmutableDisplayNameMap.Entry<ConfigSectBaseImpl> prev = children.put(section.getName(), sectionImpl);
        if (prev != null && hasLogger()) {
            assert getLogger() != null;
            getLogger().atDebug().log("ConfigSectBaseImpl '{0}' was overwritten by ConfigSectBaseImpl '{1}'",
                    prev.getValue().getFullInternalName(), section.getName().getInternalName());
        }
        sectionImpl.parent = this;
        sectionImpl.setDeep(deep + 1);
        sectionImpl.setConfig(config);
        sectionImpl.setCanBeDisabledByParent(canBeDisabled());
        sectionImpl.setThreadSafeByParent(effectiveThreadSafe);

        if (config != null && config.isBuilt()) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(section);
            List<ConfigParBase<? extends CustomType<?>>> parChildren = config.getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBaseImpl)
                    .map(val -> (ConfigParBase<? extends CustomType<?>>) val)
                    .collect(Collectors.toList());
            if (section instanceof ConfigParBaseImpl) {
                parChildren.add((ConfigParBase<? extends CustomType<?>>) section);
            }
            synchronized (config.dirtySections) { config.dirtySections.addAll(parChildren); }
        }
    }

    public void setDeep(int deep) {
        this.deep = deep;
        if (hasChildren()) children.values().forEach(c -> c.setDeep(deep + 1));
    }

    public void setConfig(@Nullable MouseConfigImpl config) {
        this.config = config;
        refreshThreadSafeFlag();
        if (hasChildren()) children.values().forEach(c -> c.setConfig(config));
    }

    protected void setCanBeDisabledByParent(boolean value) {
        canBeDisabledByParent = value;
        boolean effective = canBeDisabled();
        if (hasChildren()) children.values().forEach(c -> c.setCanBeDisabledByParent(effective));
    }

    public void removeChild(DisplayName name) {
        Objects.requireNonNull(name);
        if (!checkChildren()) {
            ImmutableDisplayNameMap.Entry<ConfigSectBaseImpl> remove = children.remove(name);
            if (remove != null && remove.getValue() != null) {
                ConfigSectBaseImpl value = remove.getValue();
                value.parent = null;
                value.setCanBeDisabledByParent(false);
                if (config != null && config.isBuilt()) {
                    ArrayList<ConfigSectBase> list = new ArrayList<>();
                    list.add(value);
                    List<ConfigParBase<? extends CustomType<?>>> parChildren = config.getChildrenRecursivelyInternal(list)
                            .stream()
                            .filter(val -> val instanceof ConfigParBaseImpl)
                            .map(val -> (ConfigParBase<? extends CustomType<?>>) val)
                            .collect(Collectors.toList());
                    if (value instanceof ConfigParBaseImpl) {
                        parChildren.add((ConfigParBase<? extends CustomType<?>>) value);
                    }
                    synchronized (config.dirtySections) { config.dirtySections.removeAll(parChildren); }
                }
            }
        }
    }

    @Override
    public ImmutableDisplayNameMap<ConfigSectBaseImpl> getChildren() {
        checkChildren();
        return children;
    }

    public void write(Consumer<ConfigSectBaseImpl> startAction, Consumer<ConfigSectBaseImpl> endAction) {
        startAction.accept(this);
        if (hasChildren()) children.forEach((key, section) -> section.write(startAction, endAction));
        endAction.accept(this);
    }

    @Nonnull @Override
    public String getFullInternalName() {
        StringBuilder      builder    = new StringBuilder(name.getInternalName());
        ConfigSectBaseImpl parentBase = parent;
        while (parentBase != null) {
            builder.insert(0, parentBase.getName().getInternalName() + ".");
            parentBase = parentBase.parent;
        }
        return builder.toString();
    }

    @Override public final void reset() {
        if (config != null && config.isBuilt()) {
            ReentrantReadWriteLock                                     lock = config.getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch;
            if (lock != null) lock.writeLock().lock();
            try {
                onReset();
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
                onReset();
                onReloadCache();
            } finally {
                if (lock != null) lock.writeLock().unlock();
            }
        }
    }

    public final void reloadCache() {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock != null) lock.writeLock().lock();
        try {
            onReloadCache();
        } finally {
            if (lock != null) lock.writeLock().unlock();
        }
    }

    public void onReset() {
        isDisabled = false;
        if (hasChildren()) children.forEach((key, value) -> value.onReset());
    }

    public void onReloadCache() {
        if (hasChildren()) children.forEach((key, value) -> value.onReloadCache());
    }

    public void onSyncListeners() {
        if (hasChildren()) children.forEach((key, value) -> value.onSyncListeners());
    }

    public void onCollectChanges(List<ConfigListenerDispatcher.PendingValueNotification<?>> out) {
        if (hasChildren()) children.forEach((key, value) -> value.onCollectChanges(out));
    }

    @Nonnull @Override public DisplayName getName()                             { return name; }
    @Nullable @Override public ConfigSectBase getParent()                       { return parent; }
    @Nullable @Override public String getComment()                              { return comment; }
    @Override public int getDeep()                                              { return deep; }

    @Nullable @Override public MouseConfigImpl getConfig()                      { return config; }
    @Override public boolean hasConfig()                                        { return config != null; }

    @SuppressWarnings("DataFlowIssue") @Nullable public MouseLogger getLogger() { return getConfig().getLogger(); }
    public boolean hasLogger()                                                  { return config != null && config.getLogger() != null; }
    public void setParent(@Nullable ConfigSectBaseImpl parent)                  { this.parent = parent; }

    @Override
    public boolean isLoaded() {
        return setByConfig && config != null && config.isLoaded();
    }

    public void setDisabledStructurally(boolean parentDisabled) {
        isStructurallyDisabled = parentDisabled;
        boolean effective = isDisabled();
        if (hasChildren()) children.forEach((key, value) -> value.setDisabledStructurally(effective));
    }

    @Override
    public void setDisabled(boolean disabled) {
        if (!canBeDisabled()) throw new UnsupportedOperationException("This ConfigSection does not support disabling");
        if (config != null && config.isBuilt()) {
            ReentrantReadWriteLock                                     lock = config.getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch;
            if (lock != null) lock.writeLock().lock();
            try {
                isDisabled = disabled;
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
                isDisabled = disabled;
                setDisabledStructurally(isStructurallyDisabled);
            } finally {
                if (lock != null) lock.writeLock().unlock();
            }
        }
    }
}