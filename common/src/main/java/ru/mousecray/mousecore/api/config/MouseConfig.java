/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.pars.ConfigPar;
import ru.mousecray.mousecore.api.config.pars.ConfigParGroup;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.values.*;
import ru.mousecray.mousecore.api.config.values.base.ConfigListVal;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

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
public class MouseConfig {
    protected final          String                                      name;
    protected final          File                                        path;
    protected final          ImmutableDisplayNameMap.Mutable<ConfigSect> sections      = new ImmutableDisplayNameMap.Mutable<>();
    public final             ConfigParser                                parser;
    @Nullable                MouseLogger                                 logger;
    protected                boolean                                     loaded;
    protected final @Nonnull ConfigDictionary                            dictionary;
    protected final          int                                         syncSecInterval;
    protected final          int                                         saveSecInterval;
    protected                LocalTime                                   lastSaveTime;
    protected                LocalTime                                   lastSyncTime;
    volatile                 long                                        lastSyncSec;
    volatile                 long                                        lastSaveSec;
    protected final          List<ConfigParBase>                         dirtySections = new ArrayList<>();
    private                  boolean                                     built;

    public MouseConfig(String folderName, String name, @Nonnull ConfigDictionary dictionary) {
        this(folderName, name, null, dictionary, 0, 0);
    }

    public MouseConfig(String folderName, String name, @Nullable MouseLogger logger, @Nonnull ConfigDictionary dictionary,
                       int autoSyncSecInterval, int autoSaveSecInterval) {
        this.name = name;
        /*new File(FMLCommonHandler.instance().getSavesDirectory().getParentFile()*/
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
        synchronized (dirtySections) {
            for (ConfigParBase base : dirtySections) if (base.equals(par)) return true;
        }
        return false;
    }

    public final void markBuilt(boolean built) { this.built = built; }
    public boolean isBuilt()                   { return built; }

    public void addSection(ConfigSect section) {
        ImmutableDisplayNameMap.Entry<ConfigSect> prev = sections.put(section.getName(), Objects.requireNonNull(section));
        if (prev != null) {
            if (logger != null) {
                logger.atDebug().log("ConfigSectBase '{0}' was overwritten by ConfigSectBase '{1}'",
                        prev.getValue().getFullInternalName(), section.getName().getInternalName());
            }
            if (built) {
                ArrayList<ConfigSectBase> list = new ArrayList<>();
                list.add(prev.getValue());
                List<ConfigParBase<?>> children = getChildrenRecursivelyInternal(list)
                        .stream()
                        .filter(val -> val instanceof ConfigParBase)
                        .map(val -> ((ConfigParBase<?>) val))
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
            List<ConfigParBase<?>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBase)
                    .map(val -> ((ConfigParBase<?>) val))
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.addAll(children); }
        }
    }

    public ConfigSect getSection(DisplayName name) { return sections.get(Objects.requireNonNull(name)); }
    public boolean hasSection(DisplayName name)    { return sections.contains(Objects.requireNonNull(name)); }

    public void removeSection(DisplayName name) {
        ImmutableDisplayNameMap.Entry<ConfigSect> remove = sections.remove(Objects.requireNonNull(name));
        if (remove != null && built) {
            ArrayList<ConfigSectBase> list = new ArrayList<>();
            list.add(remove.getValue());
            List<ConfigParBase<?>> children = getChildrenRecursivelyInternal(list)
                    .stream()
                    .filter(val -> val instanceof ConfigParBase)
                    .map(val -> ((ConfigParBase<?>) val))
                    .collect(Collectors.toList());
            synchronized (dirtySections) { dirtySections.removeAll(children); }
            reloadCache();
        }
    }

    @SuppressWarnings("DataFlowIssue") @Nullable
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
    protected ConfigVal<?> getVal(@Nullable String fullName) {
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
                if (section instanceof ConfigSect) {
                    ConfigSect sect = (ConfigSect) section;
                    if (sect.canBeDisabled() && sect.disablePar.getName().getInternalName().equals(s)) section = sect.disablePar;
                    else section = section.getChild(s);
                } else if (section instanceof ConfigParGroup) {
                    ConfigParGroup<?> sect = (ConfigParGroup<?>) section;
                    if (sect.canBeDisabled() && sect.disablePar.getName().getInternalName().equals(s)) section = sect.disablePar;
                    else {
                        stopped = i;
                        break;
                    }
                }
            }
        }

        if (stopped >= 0) return splitted.length - stopped > 1 ? null
                : ((ConfigParGroup<?>) section).getConfigVal(splitted[stopped]);

        if (section instanceof ConfigPar) return ((ConfigPar) section).getConfigVal();
        return null;
    }

    @SuppressWarnings({ "DataFlowIssue", "SpellCheckingInspection" }) @Nullable
    private ConfigParGroup<?> getGroup0(@Nullable String fullName) {
        if (fullName == null) return null;
        fullName = MouseStrings.trimWith(fullName, true, '.');
        if (fullName.isEmpty()) return null;

        String[]          splitted = Objects.requireNonNull(fullName).split("\\.");
        ConfigSectBase    section  = null;
        ConfigParGroup<?> group    = null;
        for (int i = 0, splittedLength = splitted.length; i < splittedLength; ++i) {
            group = null;
            if (i > 0 && section == null) break;
            String s = MouseStrings.trimWith(splitted[i], true, '\t');
            if (section == null) section = sections.getByInternalName(s);
            else {
                if (section instanceof ConfigSect) section = section.getChild(s);
                else if (section instanceof ConfigParGroup) group = (ConfigParGroup<?>) section;
            }
        }
        return group;
    }

    @SuppressWarnings("unchecked")
    public <T extends CustomType<T>, D extends ConfigParGroup<T>> D getGroup(@Nullable String fullName) {
        try { return (D) getGroup0(fullName); } catch (ClassCastException ignore) { return null; }
    }

    @Nullable public ConfigValPlusMinus getBooleanVal(@Nullable String fullName)             { return getValue(fullName); }
    @Nullable public ConfigValDecimal getDecimalVal(@Nullable String fullName)               { return getValue(fullName); }
    @Nullable public ConfigValIntegral getIntegerVal(@Nullable String fullName)              { return getValue(fullName); }
    @Nullable public ConfigValString getStringVal(@Nullable String fullName)                 { return getValue(fullName); }
    @Nullable public ConfigValRandomQuantity getRandomQuantityVal(@Nullable String fullName) { return getValue(fullName); }
    @Nullable public ConfigValPercent getPercentVal(@Nullable String fullName)               { return getValue(fullName); }

    @Nullable
    public <TYPE extends CustomType<TYPE>> ConfigConditionVal<TYPE> getConditionVal(@Nullable String fullName) {
        return getValue(fullName);
    }

    @Nullable
    public <LIST_VAL extends CustomType<?>,
            VAL extends CustomType<?>,
            TYPE extends ListType<LIST_VAL, VAL>
            > ConfigListVal<LIST_VAL, VAL, TYPE> getListVal(@Nullable String fullName) {
        return getValue(fullName);
    }

    @SuppressWarnings("unchecked") @Nullable
    public <T extends CustomType<?>, D extends ConfigVal<T>> D getValue(@Nullable String fullName) {
        try { return (D) getVal(fullName); } catch (ClassCastException ignore) { return null; }
    }

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

    public List<ConfigSectBase> getChildrenRecursively() { return getChildrenRecursivelyInternal(null); }

    @Nonnull List<ConfigSectBase> getChildrenRecursivelyInternal(@Nullable List<ConfigSectBase> sections) {
        if (sections == null) sections = this.sections.values().toList();
        List<ConfigSectBase> output = new ArrayList<>(sections);

        List<ConfigSectBase> list = sections.stream()
                .flatMap(s -> s.hasChildren() ? s.getChildren().values().stream() : Stream.empty())
                .collect(Collectors.toList());
        if (!list.isEmpty()) output.addAll(getChildrenRecursivelyInternal(list));

        return output;
    }

    public void reset()       { sections.forEach((key, value) -> value.reset()); }
    public void reloadCache() { if (built) sections.forEach((key, value) -> value.reloadCache()); }

    public ConfigIOThread.IOFuture load() {
        return ConfigIOThread.getInstance().loadIntermediately(this);
    }

    ConfigIOThread.IOResult loadInternal() {
        ConfigIOThread.IOResult result = parser.readFile();
        if (result.getType() == ConfigIOThread.IOResult.ConfigIOResultType.SUCCESS) {
            loaded = true;
            lastSyncTime = LocalTime.now();
        }
        return result;
    }

    public ConfigIOThread.IOFuture save(boolean markUnloaded) {
        return ConfigIOThread.getInstance().saveIntermediately(this, markUnloaded);
    }

    ConfigIOThread.IOResult saveInternal(boolean markUnloaded) {
        ConfigIOThread.IOResult result = parser.writeFile();
        if (result.getType() == ConfigIOThread.IOResult.ConfigIOResultType.SUCCESS) {
            synchronized (dirtySections) { dirtySections.clear(); }
            lastSaveTime = LocalTime.now();
        }
        loaded = !markUnloaded;
        return result;
    }

    void write(Consumer<ConfigSectBase> startAction, Consumer<ConfigSectBase> endAction) {
        sections.forEach((key, section) -> section.write(startAction, endAction));
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean putForPath(@Nullable String path, ConfigSectBase base) {
        Objects.requireNonNull(base);
        if (path == null) path = "";
        else path = MouseStrings.trimWith(path, true, '.');
        if (path.isEmpty()) {
            if (!(base instanceof ConfigSect)) return false;
            else {
                addSection((ConfigSect) base);
                return true;
            }
        }

        String[] splitted = path.split("\\.");

        ConfigSectBase section = null;
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

    @Nonnull public ConfigDictionary getDictionary() { return dictionary; }
    public String getName()                          { return name; }
    public File getPath()                            { return path; }
    public boolean isLoaded()                        { return loaded; }
    public boolean isEnableAutoSync()                { return syncSecInterval > 0; }
    public boolean isEnableAutoSave()                { return saveSecInterval > 0; }
    public int getAutoSyncSecInterval()              { return syncSecInterval; }
    public int getAutoSaveSecInterval()              { return saveSecInterval; }
    public LocalTime getLastSyncTime()               { return lastSyncTime; }
    public LocalTime getLastSaveTime()               { return lastSaveTime; }
    @Nullable public MouseLogger getLogger()         { return logger; }

    void markDirty(ConfigParBase par) {
        if (par.getConfig() == this && built) {
            synchronized (dirtySections) { dirtySections.add(par); }
        }
    }

    public boolean hasUnsavedChanges() {
        synchronized (dirtySections) {
            return !dirtySections.isEmpty();
        }
    }

    public void update(long sec) {
        if (isEnableAutoSync()) {
            if ((sec - lastSyncSec) % syncSecInterval == 0) {
                ConfigIOThread.getInstance().addLoadAction(this);
            }
        }

        if (isEnableAutoSave()) {
            if ((sec - lastSaveSec) % saveSecInterval == 0) {
                ConfigIOThread.getInstance().addSaveAction(this);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseConfig)) return false;
        MouseConfig that = (MouseConfig) o;
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