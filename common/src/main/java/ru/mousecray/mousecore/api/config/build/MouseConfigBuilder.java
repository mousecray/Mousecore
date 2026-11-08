/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.*;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class MouseConfigBuilder {
    private final   String      name;
    private final   String      folderName;
    @Nullable final MouseLogger logger;
    ConfigDictionary                        dictionary;
    SortedMap<String, List<ConfigSectBase>> sections    = new TreeMap<>(
            Comparator
                    .<String, Integer>comparing(k -> StringUtils.countMatches(k, "."))
                    .thenComparing(k -> k)
    );
    boolean                                 autoDisable = false;
    private int syncSecInterval = 0;
    private int saveSecInterval = 0;

    private MouseConfigBuilder(String name, String folderName, @Nullable MouseLogger logger) {
        this.name = name;
        this.folderName = folderName;
        this.logger = logger;
    }

    public static MouseConfigBuilder create(String name, String folderName) { return create(name, folderName, null); }
    public static MouseConfigBuilder create(String name, String folderName, @Nullable MouseLogger logger) {
        return new MouseConfigBuilder(Objects.requireNonNull(name), Objects.requireNonNull(folderName), logger);
    }

    public ConfigDictionaryBuilder createDictionary(ConfigLang currentLanguage) {
        return ConfigDictionaryBuilder.create(this, currentLanguage);
    }

    /**
     * All disabling state while config building will be applied to children automatically without console warning
     */
    public MouseConfigBuilder setAutoDisable() {
        autoDisable = true;
        return this;
    }

    public MouseConfigBuilder enableConfigAutoSynchronization(int secInterval) {
        syncSecInterval = secInterval;
        return this;
    }

    public MouseConfigBuilder enableConfigAutoSaving(int secInterval) {
        saveSecInterval = secInterval;
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigSectionBuilder createSection(@Nullable String path, String name, String displayName) {
        if (path == null) path = ".";
        else {
            String trim = MouseStrings.trimWith(path, true, '\t');
            if (!trim.startsWith(".")) trim = "." + trim;
            if (!trim.endsWith(".")) trim += ".";
            path = trim;
        }
        return ConfigSectionBuilder.create(this, new DisplayName(name, displayName), path);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T extends Comparable<T>> ConfigParameterBuilder<T> createParameter(@Nullable String path, String name, String displayName) {
        if (path == null) path = ".";
        else {
            String trim = MouseStrings.trimWith(path, true, '\t');
            if (!trim.startsWith(".")) trim = "." + trim;
            if (!trim.endsWith(".")) trim += ".";
            path = trim;
        }
        if (StringUtils.countMatches(path, ".") < 2) throw new IllegalArgumentException("MouseConfig cannot contains " +
                "Parameter without a section. " +
                "Wrong Parameter name: \"" + name + "\"");
        return ConfigParameterBuilder.create(this, new DisplayName(name, displayName), path);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T extends Comparable<T>> ConfigParameterGroupBuilder<T> createParameterGroup(
            @Nullable String path, String name, String displayName, IValType valType, Class<T> typeClass
    ) {
        if (path == null) path = ".";
        else {
            String trim = MouseStrings.trimWith(path, true, '\t');
            if (!trim.startsWith(".")) trim = "." + trim;
            if (!trim.endsWith(".")) trim += ".";
            path = trim;
        }
        if (StringUtils.countMatches(path, ".") < 2) throw new IllegalArgumentException("MouseConfig cannot contains " +
                "Parameter without a section. " +
                "Wrong Parameter name: \"" + name + "\"");
        return ConfigParameterGroupBuilder.create(this, path, new DisplayName(name, displayName), valType, typeClass);
    }

    void putSection(String path, ConfigSect section) {
        sections.computeIfAbsent(path, k -> new ArrayList<>()).add(section);
    }

    <T extends ConfigParBase<?>> void putParameter(String path, T par) {
        sections.computeIfAbsent(path, k -> new ArrayList<>()).add(par);
    }

    public MouseConfig buildConfig() {
        MouseConfig config = new MouseConfig(folderName, name, logger,
                dictionary == null ? new ConfigDictionary(ConfigLang.EN_US) : dictionary, syncSecInterval, saveSecInterval);
        sections.forEach((path, list) -> list.stream().filter(base -> !config.putForPath(path, base)).forEach(base -> {
            throw new IllegalStateException("MouseConfigBuilder got value with non-existent path (Value: \"" +
                    base.getName().getInternalName() + "\", Path: \"" + path + "\"");
        }));
        config.markBuilt(true);
        config.reloadCache();
        return config;
    }
}