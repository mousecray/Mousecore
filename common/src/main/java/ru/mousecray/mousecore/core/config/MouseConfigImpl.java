/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.MouseConfig;
import ru.mousecray.mousecore.api.config.io.ConfigIOResultType;
import ru.mousecray.mousecore.api.config.io.IOFuture;
import ru.mousecray.mousecore.api.config.par.ConfigPar;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
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
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.io.ConfigIOThread;
import ru.mousecray.mousecore.core.config.io.IOResultImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParImpl;
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectBaseImpl;
import ru.mousecray.mousecore.core.config.sect.ConfigSectImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class MouseConfigImpl implements MouseConfig {
    protected final          String                                          name;
    protected final          File                                            path;
    protected final          ImmutableDisplayNameMap.Mutable<ConfigSectImpl> sections      = new ImmutableDisplayNameMap.Mutable<>();
    public final             ConfigParser                                    parser;
    public @Nullable         MouseLogger                                     logger;
    protected                boolean                                         loaded;
    protected final @Nonnull ConfigDictionary                                dictionary;
    protected final          int                                             syncSecInterval;
    protected final          int                                             saveSecInterval;
    protected                LocalTime                                       lastSaveTime;
    protected                LocalTime                                       lastSyncTime;
    public volatile          long                                            lastSyncSec;
    public volatile          long                                            lastSaveSec;
    public final             List<ConfigParBase>                             dirtySections = new ArrayList<>();
    private                  boolean                                         built;

    public MouseConfigImpl(String folderName, String name, ConfigDictionary dictionary) {
        this(folderName, name, null, dictionary, 0, 0);
    }

    public MouseConfigImpl(
            String folderName, String name, @Nullable MouseLogger logger, ConfigDictionary dictionary,
            int autoSyncSecInterval, int autoSaveSecInterval
    ) {
        this.name = name;
        path = new File(".", folderName);
        parser = new ConfigParser(this, logger);
        this.logger = logger;
        this.dictionary = Objects.requireNonNull(dictionary);
        syncSecInterval = Math.max(autoSyncSecInterval, 0);
        saveSecInterval = Math.max(autoSaveSecInterval, 0);
        if (isEnableAutoSync() || isEnableAutoSave()) ConfigIOThread.getInstance().registerConfig(this);
    }

    public boolean isDirty(@Nullable ConfigParBase par) {
        if (par == null || !built) return false;
        synchronized (dirtySections) { for (ConfigParBase base : dirtySections) if (base.equals(par)) return true; }
        return false;
    }

    public final void markBuilt(boolean built) { this.built = built; }
    @Override public boolean isBuilt()         { return built; }

    public void addSection(ConfigSectImpl section) {
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
                        .map(val -> ((ConfigParBase<? extends CustomType<?>>) val))
                        .collect(Collectors.toList());
                synchronized (dirtySections) { dirtySections.removeAll(children); }
            }
        }
        section.setDeep(0);
        section.setConfig(this);
        reloadCache();

        if (built) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(section);
            List<ConfigParBase<? extends CustomType<?>>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBaseImpl)
                    .map(val -> ((ConfigParBase<? extends CustomType<?>>) val))
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.addAll(children); }
        }
    }

    @Override public ConfigSect getSection(DisplayName name) { return sections.get(Objects.requireNonNull(name)); }
    @Override public boolean hasSection(DisplayName name)    { return sections.contains(Objects.requireNonNull(name)); }

    public void removeSection(DisplayName name) {
        ImmutableDisplayNameMap.Entry<ConfigSectImpl> remove = sections.remove(Objects.requireNonNull(name));
        if (remove != null && built) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(remove.getValue());
            List<ConfigParBase<? extends CustomType<?>>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBaseImpl)
                    .map(val -> ((ConfigParBase<? extends CustomType<?>>) val))
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.removeAll(children); }
            reloadCache();
        }
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

    @SuppressWarnings({ "DataFlowIssue", "SpellCheckingInspection" }) @Nullable
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

    @SuppressWarnings({ "DataFlowIssue", "SpellCheckingInspection" }) @Nullable
    private ConfigParGroup<? extends CustomType<?>> getGroup0(@Nullable String fullName) {
        if (fullName == null) return null;
        fullName = MouseStrings.trimWith(fullName, true, '.');
        if (fullName.isEmpty()) return null;

        String[]                                splitted = Objects.requireNonNull(fullName).split("\\.");
        ConfigSectBase                          section  = null;
        ConfigParGroup<? extends CustomType<?>> group    = null;
        for (int i = 0, splittedLength = splitted.length; i < splittedLength; ++i) {
            group = null;
            if (i > 0 && section == null) break;
            String s = MouseStrings.trimWith(splitted[i], true, '\t');
            if (section == null) section = sections.getByInternalName(s);
            else {
                if (section instanceof ConfigSectImpl) section = section.getChild(s);
                else if (section instanceof ConfigParGroupImpl) group = (ConfigParGroup<? extends CustomType<?>>) section;
            }
        }
        return group;
    }

    @SuppressWarnings("unchecked") @Override
    public <T extends CustomType<?>, D extends ConfigParGroup<T>> D getGroup(@Nullable String fullName) {
        try { return (D) getGroup0(fullName); } catch (ClassCastException ignore) { return null; }
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

    @Override
    public void reset() {
        sections.forEach((key, value) -> value.reset());
        reloadCache();
    }

    public void reloadCache() {
        if (!built) return;
        sections.forEach((key, value) -> value.setDisabledStructurally(false));
        sections.forEach((key, value) -> value.reloadCache());
    }

    @Override
    public IOFuture load() {
        return ConfigIOThread.getInstance().loadIntermediately(this);
    }

    public IOResultImpl loadInternal() {
        IOResultImpl result = parser.readFile();
        if (result.getType() == ConfigIOResultType.SUCCESS) {
            loaded = true;
            lastSyncTime = LocalTime.now();
            reloadCache();
        }
        return result;
    }

    @Override
    public IOFuture save(boolean markUnloaded) {
        return ConfigIOThread.getInstance().saveIntermediately(this, markUnloaded);
    }

    public IOResultImpl saveInternal(boolean markUnloaded) {
        IOResultImpl result = parser.writeFile();
        if (result.getType() == ConfigIOResultType.SUCCESS) {
            synchronized (dirtySections) { dirtySections.clear(); }
            lastSaveTime = LocalTime.now();
        }
        loaded = !markUnloaded;
        return result;
    }

    void write(Consumer<ConfigSectBaseImpl> startAction, Consumer<ConfigSectBaseImpl> endAction) {
        sections.forEach((key, section) -> section.write(startAction, endAction));
    }

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
        if (par.getConfig() == this && built) synchronized (dirtySections) { dirtySections.add(par); }
    }

    @Override
    public boolean hasUnsavedChanges() {
        synchronized (dirtySections) { return !dirtySections.isEmpty(); }
    }

    public void update(long sec) {
        if (isEnableAutoSync() && (sec - lastSyncSec) % syncSecInterval == 0) ConfigIOThread.getInstance().addLoadAction(this);
        if (isEnableAutoSave() && (sec - lastSaveSec) % saveSecInterval == 0) ConfigIOThread.getInstance().addSaveAction(this);
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