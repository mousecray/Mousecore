/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.ConfigChangeListener;
import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.io.ConfigIOResultType;
import ru.mousecray.mousecore.api.config.io.IOFuture;
import ru.mousecray.mousecore.api.config.par.ConfigPar;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.par.group.*;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.utils.ConfigSide;
import ru.mousecray.mousecore.api.config.utils.ConfigThreadPolicy;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListLogical;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListNumber;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListOther;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListVector3;
import ru.mousecray.mousecore.api.config.val.number.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.config.val.other.ConfigValString;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.platform.PlatformHelper;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.io.ConfigIOThread;
import ru.mousecray.mousecore.core.config.io.IOResultImpl;
import ru.mousecray.mousecore.core.config.migration.ConfigMigrator;
import ru.mousecray.mousecore.core.config.migration.ConfigStructureMeta;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParImpl;
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectBaseImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectImpl;
import ru.mousecray.mousecore.core.config.sync.ConfigSyncManager;
import ru.mousecray.mousecore.core.config.sync.ConfigSyncSnapshot;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.container.*;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValConditionImpl;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValPlusMinusImpl;
import ru.mousecray.mousecore.core.config.val.minecraft.*;
import ru.mousecray.mousecore.core.config.val.number.*;
import ru.mousecray.mousecore.core.config.val.other.ConfigValColorImpl;
import ru.mousecray.mousecore.core.config.val.other.ConfigValEnumImpl;
import ru.mousecray.mousecore.core.config.val.other.ConfigValExpressionImpl;
import ru.mousecray.mousecore.core.config.val.other.ConfigValRegexImpl;
import ru.mousecray.mousecore.core.config.val.vec3.ConfigValBlockPosImpl;
import ru.mousecray.mousecore.core.config.val.vec3.ConfigValVector3dImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class MouseConfigImpl implements MouseConfig {
    protected final          String                                          name;
    protected final          File                                            path;
    protected final          ImmutableDisplayNameMap.Mutable<ConfigSectImpl> sections      = new ImmutableDisplayNameMap.Mutable<>();
    public final             ConfigParser                                    parser;
    @Nullable public         MouseLogger                                     logger;
    @Nonnull protected final ConfigDictionary                                dictionary;
    protected final          boolean                                         dynamicLanguage;
    protected final          int                                             syncSecInterval;
    protected final          int                                             saveSecInterval;
    protected                LocalTime                                       lastSaveTime;
    protected                LocalTime                                       lastSyncTime;
    public volatile          long                                            lastSyncSec;
    public volatile          long                                            lastSaveSec;
    public final             List<ConfigParBase>                             dirtySections = new ArrayList<>();

    @Nonnull protected final ConfigSide defaultSide;

    protected volatile         boolean                loaded;
    private volatile           boolean                built;
    protected final            boolean                threadSafe;
    @Nullable private volatile ReentrantReadWriteLock syncLock;

    private final CopyOnWriteArrayList<ConfigListenerDispatcher.GlobalListenerEntry> globalListeners = new CopyOnWriteArrayList<>();
    private final AtomicInteger                                                      totalListeners  = new AtomicInteger(0);

    protected final            int                 schemaVersion;
    protected final            ConfigMigrator      migrator;
    @Nullable private volatile ConfigStructureMeta structureMeta;

    protected final String modId;

    public MouseConfigImpl(String modId, String folderName, String name, ConfigDictionary dictionary) {
        this(modId, folderName, name, null, dictionary, 0, 0);
    }

    public MouseConfigImpl(
            String modId, String folderName, String name, @Nullable MouseLogger logger, ConfigDictionary dictionary,
            int autoSyncSecInterval, int autoSaveSecInterval
    ) {
        this(modId, folderName, name, logger, dictionary, autoSyncSecInterval, autoSaveSecInterval, ConfigSide.COMMON);
    }

    public MouseConfigImpl(
            String modId, String folderName, String name, @Nullable MouseLogger logger, ConfigDictionary dictionary,
            int autoSyncSecInterval, int autoSaveSecInterval, ConfigSide defaultSide
    ) {
        this(
                modId, folderName, name, logger, dictionary, autoSyncSecInterval, autoSaveSecInterval, defaultSide,
                false, false, 1, new ConfigMigrator()
        );
    }

    public MouseConfigImpl(
            String modId, String folderName, String name, @Nullable MouseLogger logger, ConfigDictionary dictionary,
            int autoSyncSecInterval, int autoSaveSecInterval, ConfigSide defaultSide, boolean threadSafe,
            boolean dynamicLanguage,
            int schemaVersion, ConfigMigrator migrator
    ) {
        this.modId = modId;
        this.name = name;
        path = new File(".", folderName);
        parser = new ConfigParser(this, logger);
        this.logger = logger;
        this.dictionary = Objects.requireNonNull(dictionary);
        this.defaultSide = Objects.requireNonNull(defaultSide);
        this.threadSafe = threadSafe;
        this.dynamicLanguage = dynamicLanguage;
        this.schemaVersion = Math.max(1, schemaVersion);
        this.migrator = Objects.requireNonNull(migrator);
        if (threadSafe) syncLock = new ReentrantReadWriteLock();
        syncSecInterval = Math.max(autoSyncSecInterval, 0);
        saveSecInterval = Math.max(autoSaveSecInterval, 0);
        if (isEnableAutoSync() || isEnableAutoSave()) ConfigIOThread.getInstance().registerConfig(this);
    }

    @Override public String getModId()                    { return modId; }
    @Nonnull @Override public ConfigSide getDefaultSide() { return defaultSide; }
    @Override public boolean isThreadSafe()               { return threadSafe; }
    @Nullable public ReentrantReadWriteLock getSyncLock() { return syncLock; }

    @Nonnull
    public ReentrantReadWriteLock getOrCreateSyncLock() {
        ReentrantReadWriteLock lock = syncLock;
        if (lock == null) {
            synchronized (this) {
                lock = syncLock;
                if (lock == null) {
                    lock = new ReentrantReadWriteLock();
                    syncLock = lock;
                }
            }
        }
        return lock;
    }

    public boolean isDirty(@Nullable ConfigParBase par) {
        if (par == null || !built) return false;
        synchronized (dirtySections) { for (ConfigParBase base : dirtySections) if (base.equals(par)) return true; }
        return false;
    }

    public final void markBuilt(boolean built) { this.built = built; }
    @Override public boolean isBuilt()         { return built; }

    public void addSection(ConfigSectImpl section) {
        if (built) {
            ReentrantReadWriteLock                                     lock = getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch;
            if (lock != null) lock.writeLock().lock();
            try {
                putSectionDirect(section);
                reloadCacheDirect();
                batch = collectChangesIfListening();
            } finally { if (lock != null) lock.writeLock().unlock(); }
            dispatchChanges(batch);
            broadcastSyncedIfNeeded();
        } else putSectionDirect(section);
    }

    private void putSectionDirect(ConfigSectImpl section) {
        ImmutableDisplayNameMap.Entry<ConfigSectImpl> prev = sections.put(section.getName(), Objects.requireNonNull(section));
        if (prev != null) {
            if (logger != null) {
                logger.atDebug().log("ConfigSectBaseImpl '{0}' was overwritten by ConfigSectBaseImpl '{1}'",
                        prev.getValue().getFullInternalName(), section.getName().getInternalName());
            }
            if (built) {
                ArrayList<ConfigSectBase> list = new ArrayList<>();
                list.add(prev.getValue());
                List<ConfigParBase<? extends CustomType<?>>> children = getChildrenRecursivelyInternal(list)
                        .stream()
                        .filter(val -> val instanceof ConfigParBaseImpl)
                        .map(val -> (ConfigParBase<? extends CustomType<?>>) val)
                        .collect(Collectors.toList());
                synchronized (dirtySections) { dirtySections.removeAll(children); }
            }
        }
        section.setDeep(0);
        section.setConfig(this);

        if (built) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(section);
            List<ConfigParBase<? extends CustomType<?>>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBaseImpl)
                    .map(val -> (ConfigParBase<? extends CustomType<?>>) val)
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.addAll(children); }
        }
    }

    @Override public ConfigSect getSection(DisplayName name) { return sections.get(Objects.requireNonNull(name)); }
    @Override public boolean hasSection(DisplayName name)    { return sections.contains(Objects.requireNonNull(name)); }

    public void removeSection(DisplayName name) {
        if (built) {
            ReentrantReadWriteLock                                     lock  = getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
            if (lock != null) lock.writeLock().lock();
            try {
                if (removeSectionDirect(name)) {
                    reloadCacheDirect();
                    batch = collectChangesIfListening();
                }
            } finally { if (lock != null) lock.writeLock().unlock(); }
            dispatchChanges(batch);
            broadcastSyncedIfNeeded();
        } else removeSectionDirect(name);
    }

    private boolean removeSectionDirect(DisplayName name) {
        ImmutableDisplayNameMap.Entry<ConfigSectImpl> remove = sections.remove(Objects.requireNonNull(name));
        if (remove != null && built) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(remove.getValue());
            List<ConfigParBase<? extends CustomType<?>>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBaseImpl)
                    .map(val -> (ConfigParBase<? extends CustomType<?>>) val)
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.removeAll(children); }
            return true;
        }
        return false;
    }

    @SuppressWarnings("DataFlowIssue") @Nullable @Override
    public ConfigSectBase getSectionBase(@Nullable String fullName) {
        if (fullName == null) return null;
        fullName = MouseStrings.trimWith(fullName, true, '.');
        if (fullName.isEmpty()) return null;

        String[] splitted = Objects.requireNonNull(fullName).split("\\.");

        ConfigSectBase section = null;
        for (int i = 0, splittedLength = splitted.length; i < splittedLength; ++i) {
            if (i > 0 && section == null) break;
            String s = MouseStrings.trimWith(splitted[i], true, '\t');
            section = section == null ? sections.getByInternalName(s) : section.getChild(s);
        }

        return section;
    }

    @SuppressWarnings({ "DataFlowIssue" }) @Nullable
    protected ConfigVal<? extends CustomType<?>> getVal(@Nullable String fullName) {
        if (fullName == null) return null;
        fullName = MouseStrings.trimWith(fullName, true, '.');
        if (fullName.isEmpty()) return null;

        String[] splitted = Objects.requireNonNull(fullName).split("\\.");

        int            stopped = -1;
        ConfigSectBase section = null;
        for (int i = 0, splittedLength = splitted.length; i < splittedLength; ++i) {
            if (i > 0 && section == null) break;
            String s = MouseStrings.trimWith(splitted[i], true, '\t');
            if (section == null) section = sections.getByInternalName(s);
            else {
                if (section instanceof ConfigSectImpl sect) {
                    if (sect.hasDisabler() && sect.disablePar.getName().getInternalName().equals(s)) section = sect.disablePar;
                    else section = section.getChild(s);
                } else if (section instanceof ConfigParGroupImpl<?> sect) {
                    if (sect.hasDisabler() && sect.disablePar.getName().getInternalName().equals(s)) section = sect.disablePar;
                    else {
                        stopped = i;
                        break;
                    }
                }
            }
        }

        if (stopped >= 0) {
            return splitted.length - stopped > 1 ? null : ((ConfigParGroup<? extends CustomType<?>>) section).getConfigVal(splitted[stopped]);
        }

        if (section instanceof ConfigParImpl) return ((ConfigPar) section).getConfigVal();
        return null;
    }

    @Nullable
    private ConfigParGroup<? extends CustomType<?>> getGroup0(@Nullable String fullName) {
        ConfigSectBase section = getSectionBase(fullName);
        return section instanceof ConfigParGroup<?> group ? group : null;
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <T extends CustomType<?>, D extends ConfigParGroup<T>> D getGroup(@Nullable String fullName) {
        try { return (D) getGroup0(fullName); } catch (ClassCastException ignore) { return null; }
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <VAL extends CustomType<?>, TARGET extends CustomType<?>> ConfigParGroupConditional<VAL, TARGET> getConditionalGroup(@Nullable String fullName) {
        ConfigParGroup<?> group = getGroup0(fullName);
        return group instanceof ConfigParGroupConditional<?, ?> g ? (ConfigParGroupConditional<VAL, TARGET>) g : null;
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <VAL extends CustomType<?>, TARGET extends CustomType<?>> ConfigParGroupBranch<VAL, TARGET> getBranchGroup(@Nullable String fullName) {
        ConfigParGroup<?> group = getGroup0(fullName);
        return group instanceof ConfigParGroupBranch<?, ?> g ? (ConfigParGroupBranch<VAL, TARGET>) g : null;
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <VAL extends CustomType<?>> ConfigParGroupPhase<VAL> getPhaseGroup(@Nullable String fullName) {
        ConfigParGroup<?> group = getGroup0(fullName);
        return group instanceof ConfigParGroupPhase<?> g ? (ConfigParGroupPhase<VAL>) g : null;
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <T extends CustomType<?>, CONTEXT extends CustomType<?>> ConfigParGroupTransformer<T, CONTEXT> getTransformerGroup(@Nullable String fullName) {
        ConfigParGroup<?> group = getGroup0(fullName);
        return group instanceof ConfigParGroupTransformer<?, ?> g ? (ConfigParGroupTransformer<T, CONTEXT>) g : null;
    }

    @Nullable @Override public ConfigValPlusMinusImpl getBooleanVal(@Nullable String fullName)                { return getValue(fullName); }
    @Nullable @Override public ConfigValDecimalImpl getDecimalVal(@Nullable String fullName)                  { return getValue(fullName); }
    @Nullable @Override public ConfigValIntegralImpl getIntegerVal(@Nullable String fullName)                 { return getValue(fullName); }
    @Nullable @Override public ConfigValString getStringVal(@Nullable String fullName)                        { return getValue(fullName); }
    @Nullable @Override public ConfigValRandomQuantity getRandomQuantityVal(@Nullable String fullName)        { return getValue(fullName); }
    @Nullable @Override public ConfigValPercentImpl getPercentVal(@Nullable String fullName)                  { return getValue(fullName); }

    @Nullable @Override public ConfigValRangeImpl getRangeVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValTimeImpl getTimeVal(@Nullable String fullName)                        { return getValue(fullName); }

    @Nullable @Override public ConfigValVector3dImpl getVector3dVal(@Nullable String fullName)                { return getValue(fullName); }
    @Nullable @Override public ConfigValBlockPosImpl getBlockPosVal(@Nullable String fullName)                { return getValue(fullName); }

    @Nullable @Override public <E extends Enum<E>> ConfigValEnumImpl<E> getEnumVal(@Nullable String fullName) { return getValue(fullName); }
    @Nullable @Override public ConfigValColorImpl getColorVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValRegexImpl getRegexVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValExpressionImpl getExpressionVal(@Nullable String fullName)            { return getValue(fullName); }

    @Nullable @Override public ConfigValBlockImpl getBlockVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValItemImpl getItemVal(@Nullable String fullName)                        { return getValue(fullName); }
    @Nullable @Override public ConfigValItemStackImpl getItemStackVal(@Nullable String fullName)              { return getValue(fullName); }
    @Nullable @Override public ConfigValTagImpl getTagVal(@Nullable String fullName)                          { return getValue(fullName); }
    @Nullable @Override public ConfigValEntityImpl getEntityVal(@Nullable String fullName)                    { return getValue(fullName); }
    @Nullable @Override public ConfigValDimensionImpl getDimensionVal(@Nullable String fullName)              { return getValue(fullName); }
    @Nullable @Override public ConfigValBiomeImpl getBiomeVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValEffectImpl getEffectVal(@Nullable String fullName)                    { return getValue(fullName); }
    @Nullable @Override public ConfigValEnchantmentImpl getEnchantmentVal(@Nullable String fullName)          { return getValue(fullName); }
    @Nullable @Override public ConfigValParticleImpl getParticleVal(@Nullable String fullName)                { return getValue(fullName); }
    @Nullable @Override public ConfigValSoundImpl getSoundVal(@Nullable String fullName)                      { return getValue(fullName); }

    @Nullable @Override public ConfigValNBTImpl getNBTVal(@Nullable String fullName)                          { return getValue(fullName); }
    @Nullable @Override public ConfigValTupleImpl getTupleVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValTableImpl getTableVal(@Nullable String fullName)                      { return getValue(fullName); }
    @Nullable @Override public ConfigValMatrixImpl getMatrixVal(@Nullable String fullName)                    { return getValue(fullName); }

    @Nullable @Override
    public <K extends CustomType<?>, V extends CustomType<?>> ConfigValMapImpl<K, V> getMapVal(@Nullable String fullName) {
        return getValue(fullName);
    }

    @Nullable @Override
    public <TYPE extends CustomType<?>> ConfigValConditionImpl<TYPE> getConditionVal(@Nullable String fullName) {
        return getValue(fullName);
    }

    @SuppressWarnings("unchecked")
    @Nullable @Override
    public <T extends CustomType<?>> ConfigValSimpleList<T> getListVal(@Nullable String fullName) {
        ConfigVal<? extends CustomType<?>> val = getVal(fullName);
        return val instanceof ConfigValSimpleList<?> list ? (ConfigValSimpleList<T>) list : null;
    }

    @SuppressWarnings("unchecked")
    @Nullable @Override
    public <T extends NumberType<?>> ConfigValSimpleListNumber<T> getNumberListVal(@Nullable String fullName) {
        ConfigVal<? extends CustomType<?>> val = getVal(fullName);
        return val instanceof ConfigValSimpleListNumber<?> list ? (ConfigValSimpleListNumber<T>) list : null;
    }

    @SuppressWarnings("unchecked")
    @Nullable @Override
    public <T extends LogicalType<?>> ConfigValSimpleListLogical<T> getLogicalListVal(@Nullable String fullName) {
        ConfigVal<? extends CustomType<?>> val = getVal(fullName);
        return val instanceof ConfigValSimpleListLogical<?> list ? (ConfigValSimpleListLogical<T>) list : null;
    }

    @SuppressWarnings("unchecked")
    @Nullable @Override
    public <T extends OtherType<?>> ConfigValSimpleListOther<T> getOtherListVal(@Nullable String fullName) {
        ConfigVal<? extends CustomType<?>> val = getVal(fullName);
        return val instanceof ConfigValSimpleListOther<?> list ? (ConfigValSimpleListOther<T>) list : null;
    }

    @SuppressWarnings("unchecked")
    @Nullable @Override
    public <N extends NumberType<?>, T extends Vector3Type<N, T>> ConfigValSimpleListVector3<N, T> getVectorListVal(@Nullable String fullName) {
        ConfigVal<? extends CustomType<?>> val = getVal(fullName);
        return val instanceof ConfigValSimpleListVector3<?, ?> list ? (ConfigValSimpleListVector3<N, T>) list : null;
    }

    @SuppressWarnings("unchecked") @Nullable @Override
    public <T extends CustomType<?>, D extends ConfigVal<T>> D getValue(@Nullable String fullName) {
        try { return (D) getVal(fullName); } catch (ClassCastException ignore) { return null; }
    }

    @Override
    public List<ConfigSectBase> getByDeep(int deep) {
        List<ConfigSectBase> map = sections.values().toList();
        if (deep > 0) {
            for (int i = 1; i <= deep; ++i) {
                map = map.stream()
                        .flatMap(s -> s.hasChildren() ? s.getChildren().values().stream() : Stream.empty())
                        .collect(Collectors.toList());
            }
        }
        return map;
    }

    @Override public List<ConfigSectBase> getChildrenRecursively() { return getChildrenRecursivelyInternal(null); }

    @Nonnull
    public List<ConfigSectBase> getChildrenRecursivelyInternal(@Nullable List<ConfigSectBase> sections) {
        if (sections == null) sections = this.sections.values().toList();
        List<ConfigSectBase> output = new ArrayList<>(sections);

        List<ConfigSectBase> list = sections.stream()
                .flatMap(s -> s.hasChildren() ? s.getChildren().values().stream() : Stream.empty())
                .collect(Collectors.toList());
        if (!list.isEmpty()) output.addAll(getChildrenRecursivelyInternal(list));

        return output;
    }

    public void reloadCacheDirect() {
        sections.forEach((key, value) -> value.setDisabledStructurally(false));
        sections.forEach((key, value) -> value.onReloadCache());
    }

    public void reloadCache() {
        if (!built) return;
        ReentrantReadWriteLock                                     lock  = getSyncLock();
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
        if (lock != null) lock.writeLock().lock();
        try {
            reloadCacheDirect();
            batch = collectChangesIfListening();
        } finally { if (lock != null) lock.writeLock().unlock(); }
        dispatchChanges(batch);
    }

    public void syncListeners() {
        sections.forEach((key, value) -> value.onSyncListeners());
    }

    @Nullable
    public List<ConfigListenerDispatcher.PendingValueNotification<?>> collectChangesIfListening() {
        if (!hasAnyListeners()) return null;
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = new ArrayList<>();
        sections.forEach((key, value) -> value.onCollectChanges(batch));
        return batch;
    }

    public void dispatchChanges(@Nullable List<ConfigListenerDispatcher.PendingValueNotification<?>> batch) {
        if (batch != null && !batch.isEmpty()) ConfigListenerDispatcher.dispatch(this, batch, List.copyOf(globalListeners), logger);
    }

    @Override
    public void reset() {
        ReentrantReadWriteLock                                     lock  = getSyncLock();
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
        if (lock != null) lock.writeLock().lock();
        try {
            sections.forEach((key, value) -> value.onReset());
            reloadCacheDirect();
            batch = collectChangesIfListening();
        } finally { if (lock != null) lock.writeLock().unlock(); }
        dispatchChanges(batch);
        broadcastSyncedIfNeeded();
    }

    @Override public IOFuture load() { return ConfigIOThread.getInstance().loadIntermediately(this); }

    public IOResultImpl loadInternal() {
        ReentrantReadWriteLock                                     lock  = getSyncLock();
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
        IOResultImpl                                               result;
        if (lock != null) lock.writeLock().lock();
        try {
            result = parser.readFile();
            if (result.getType() == ConfigIOResultType.SUCCESS) {
                loaded = true;
                lastSyncTime = LocalTime.now();
                reloadCacheDirect();
                batch = collectChangesIfListening();
            }
        } finally {
            if (lock != null) lock.writeLock().unlock();
        }
        dispatchChanges(batch);
        broadcastSyncedIfNeeded();
        return result;
    }

    @Override
    public IOFuture save(boolean markUnloaded) {
        return ConfigIOThread.getInstance().saveIntermediately(this, markUnloaded);
    }

    public IOResultImpl saveInternal(boolean markUnloaded) {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock != null) lock.readLock().lock();
        try {
            IOResultImpl result = parser.writeFile();
            if (result.getType() == ConfigIOResultType.SUCCESS) {
                synchronized (dirtySections) { dirtySections.clear(); }
                lastSaveTime = LocalTime.now();
            }
            loaded = !markUnloaded;
            return result;
        } finally {
            if (lock != null) lock.readLock().unlock();
        }
    }

    public ConfigSyncSnapshot collectNetworkSnapshot() {
        Map<String, ConfigSyncSnapshot.SyncEntry> map  = new java.util.LinkedHashMap<>();
        ReentrantReadWriteLock                    lock = getSyncLock();
        if (lock != null) lock.readLock().lock();
        try {
            sections.forEach((key, value) -> value.onCollectSyncedValues(map));
        } finally {
            if (lock != null) lock.readLock().unlock();
        }
        return new ConfigSyncSnapshot(name, map);
    }

    public void applyNetworkSnapshot(ConfigSyncSnapshot snapshot) {
        ReentrantReadWriteLock                                     lock  = getSyncLock();
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
        if (lock != null) lock.writeLock().lock();
        try {
            for (Map.Entry<String, ConfigSyncSnapshot.SyncEntry> entry : snapshot.getEntries().entrySet()) {
                ConfigVal<?> val = getVal(entry.getKey());
                if (val instanceof ConfigValImpl<?> impl && impl.getSide().isSynced()) {
                    impl.applySyncEntry(entry.getValue());
                }
            }
            reloadCacheDirect();
            batch = collectChangesIfListening();
        } finally {
            if (lock != null) lock.writeLock().unlock();
        }
        dispatchChanges(batch);
    }

    public void revertNetworkOverrides() {
        ReentrantReadWriteLock                                     lock  = getSyncLock();
        List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
        if (lock != null) lock.writeLock().lock();
        try {
            sections.forEach((key, value) -> value.onClearNetworkOverride());
            reloadCacheDirect();
            batch = collectChangesIfListening();
        } finally {
            if (lock != null) lock.writeLock().unlock();
        }
        dispatchChanges(batch);
    }

    public void broadcastSyncedIfNeeded() {
        ConfigSyncManager.broadcastIfServer(this);
    }


    void write(Consumer<ConfigSectBaseImpl> startAction, Consumer<ConfigSectBaseImpl> endAction) {
        sections.forEach((key, section) -> section.write(startAction, endAction));
    }

    @Nonnull @Override public ConfigDictionary getDictionary() { return dictionary; }
    @Override public String getName()                          { return name; }
    @Override public File getPath()                            { return path; }
    @Override public boolean isLoaded()                        { return loaded; }
    @Override public boolean isEnableAutoSync()                { return syncSecInterval > 0; }
    @Override public boolean isEnableAutoSave()                { return saveSecInterval > 0; }
    @Override public int getAutoSyncSecInterval()              { return syncSecInterval; }
    @Override public int getAutoSaveSecInterval()              { return saveSecInterval; }
    @Override public LocalTime getLastSyncTime()               { return lastSyncTime; }
    @Override public LocalTime getLastSaveTime()               { return lastSaveTime; }
    @Nullable @Override public MouseLogger getLogger()         { return logger; }

    public void markDirty(ConfigParBase par) {
        if (par.getConfig() == this && built) {
            synchronized (dirtySections) { if (!dirtySections.contains(par)) dirtySections.add(par); }
        }
    }

    @Override
    public boolean hasUnsavedChanges() {
        synchronized (dirtySections) { return !dirtySections.isEmpty(); }
    }

    public void update(long sec) {
        if (dynamicLanguage && sec % 10 == 0) refreshLanguage();
        if (isEnableAutoSync() && (sec - lastSyncSec) % syncSecInterval == 0) ConfigIOThread.getInstance().addLoadAction(this);
        if (isEnableAutoSave() && (sec - lastSaveSec) % saveSecInterval == 0) ConfigIOThread.getInstance().addSaveAction(this);
    }

    public void adjustListenerCount(int delta) { totalListeners.addAndGet(delta); }
    public boolean hasAnyListeners()           { return totalListeners.get() > 0; }
    public boolean hasGlobalListeners()        { return !globalListeners.isEmpty(); }

    @Override
    public void addChangeListener(ConfigThreadPolicy policy, ConfigChangeListener listener) {
        Objects.requireNonNull(policy);
        Objects.requireNonNull(listener);
        synchronized (globalListeners) {
            globalListeners.add(new ConfigListenerDispatcher.GlobalListenerEntry(policy, listener));
            adjustListenerCount(1);
        }
    }

    @Override
    public boolean removeChangeListener(ConfigChangeListener listener) {
        Objects.requireNonNull(listener);
        synchronized (globalListeners) {
            int     before  = globalListeners.size();
            boolean removed = globalListeners.removeIf(e -> e.listener().equals(listener));
            int     delta   = globalListeners.size() - before;
            if (delta != 0) adjustListenerCount(delta);
            return removed;
        }
    }

    public String getSyncKey() { return name; }

    @SuppressWarnings("DataFlowIssue")
    public boolean putForPath(@Nullable String path, ConfigSectBase base) {
        Objects.requireNonNull(base);
        if (path == null) path = "";
        else path = MouseStrings.trimWith(path, true, '.');
        if (path.isEmpty()) {
            if (!(base instanceof ConfigSectImpl)) return false;
            else {
                addSection((ConfigSectImpl) base);
                return true;
            }
        }

        String[] splitted = path.split("\\.");

        ConfigSectBaseImpl section = null;
        for (int i = 0, splittedLength = splitted.length; i < splittedLength; ++i) {
            if (i > 0 && section == null) break;
            String s = MouseStrings.trimWith(splitted[i], true, '\t');
            section = section == null ? sections.getByInternalName(s) : section.getChild(s);
        }

        if (section != null) {
            section.addChild(base);
            reloadCache();
            return true;
        } else return false;
    }

    @Nonnull @Override public ConfigLang getLanguage() { return dictionary.getActiveLanguage(); }
    @Override public boolean isDynamicLanguage()       { return dynamicLanguage; }

    @Override
    public void setLanguage(ConfigLang language) {
        Objects.requireNonNull(language);
        if (dictionary.getActiveLanguage() == language) return;

        ReentrantReadWriteLock lock = getSyncLock();
        if (lock != null) lock.writeLock().lock();
        try {
            dictionary.setActiveLanguage(language);
            reloadCacheDirect();
        } finally { if (lock != null) lock.writeLock().unlock(); }

        if (built && fileExists()) save(false);
    }

    @Override
    public boolean refreshLanguage() {
        if (!dynamicLanguage) return false;
        ConfigLang gameLang = PlatformHelper.getInstance().getGameLanguage();
        if (gameLang != getLanguage()) {
            setLanguage(gameLang);
            return true;
        }
        return false;
    }

    public boolean fileExists()                             { return new File(path, name + ".moc").exists(); }

    @Override public int getSchemaVersion()                 { return schemaVersion; }
    public ConfigMigrator getMigrator()                     { return migrator; }
    @Nullable public ConfigStructureMeta getStructureMeta() { return structureMeta; }
    public void setStructureMeta(ConfigStructureMeta meta)  { structureMeta = meta; }

    public Set<String> collectAllStructuralPaths() {
        Set<String> paths = new TreeSet<>();
        for (ConfigSectBase s : getChildrenRecursively()) {
            paths.add(s.getFullInternalName());
            if (s instanceof ConfigParBase<?> par) {
                par.getValues().forEach((dn, v) -> paths.add(s.getFullInternalName() + "." + dn.getInternalName()));
            }
        }
        return paths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseConfigImpl that)) return false;
        return syncSecInterval == that.syncSecInterval
                && saveSecInterval == that.saveSecInterval
                && Objects.equals(name, that.name) && Objects.equals(path, that.path)
                && Objects.equals(sections, that.sections) && Objects.equals(parser, that.parser)
                && Objects.equals(logger, that.logger) && Objects.equals(dictionary, that.dictionary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, sections, parser, logger, dictionary, syncSecInterval, saveSecInterval);
    }
}