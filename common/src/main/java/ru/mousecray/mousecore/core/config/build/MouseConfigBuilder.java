/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.build.par.ConfigParameterBuilder;
import ru.mousecray.mousecore.core.config.build.par.group.ConfigParameterGroupBuilder;
import ru.mousecray.mousecore.core.config.build.struct.ConfigDictionaryBuilder;
import ru.mousecray.mousecore.core.config.build.struct.ConfigSectionBuilder;
import ru.mousecray.mousecore.core.config.utils.ConfigDictionaryImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class MouseConfigBuilder {
    private final          String                                  name;
    private final          String                                  folderName;
    @Nullable public final MouseLogger                             logger;
    public                 ConfigDictionary                        dictionary;
    public                 SortedMap<String, List<ConfigSectBase>> sections        = new TreeMap<>(
            Comparator
                    .<String, Integer>comparing(k -> StringUtils.countMatches(k, "."))
                    .thenComparing(k -> k)
    );
    public                 boolean                                 autoDisable     = false;
    private                int                                     syncSecInterval = 0;
    private                int                                     saveSecInterval = 0;

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
    public ConfigParameterBuilder createParameter(@Nullable String path, String name, String displayName) {
        if (path == null) path = ".";
        else {
            String trim = MouseStrings.trimWith(path, true, '\t');
            if (!trim.startsWith(".")) trim = "." + trim;
            if (!trim.endsWith(".")) trim += ".";
            path = trim;
        }
        if (StringUtils.countMatches(path, ".") < 2) throw new IllegalArgumentException("MouseConfigImpl cannot contains " +
                "Parameter without a section. " +
                "Wrong Parameter name: \"" + name + "\"");
        return ConfigParameterBuilder.create(this, new DisplayName(name, displayName), path);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T extends CustomType<?>> ConfigParameterGroupBuilder<T> createParameterGroup(
            @Nullable String path, String name, String displayName, IValType valType, Class<T> typeClass
    ) {
        if (path == null) path = ".";
        else {
            String trim = MouseStrings.trimWith(path, true, '\t');
            if (!trim.startsWith(".")) trim = "." + trim;
            if (!trim.endsWith(".")) trim += ".";
            path = trim;
        }
        if (StringUtils.countMatches(path, ".") < 2) throw new IllegalArgumentException("MouseConfigImpl cannot contains " +
                "Parameter without a section. " +
                "Wrong Parameter name: \"" + name + "\"");
        return ConfigParameterGroupBuilder.create(this, path, new DisplayName(name, displayName), valType, typeClass);
    }

    public void putSection(String path, ConfigSect section) {
        sections.computeIfAbsent(path, k -> new ArrayList<>()).add(section);
    }

    public void putParameter(String path, ConfigParBase<?> par) {
        sections.computeIfAbsent(path, k -> new ArrayList<>()).add(par);
    }

    public MouseConfigImpl buildConfig() {
        MouseConfigImpl config = new MouseConfigImpl(folderName, name, logger,
                dictionary == null ? new ConfigDictionaryImpl(ConfigLang.EN_US) : dictionary, syncSecInterval, saveSecInterval);
        sections.forEach((path, list) -> list.stream().filter(base -> !config.putForPath(path, base)).forEach(base -> {
            throw new IllegalStateException("MouseConfigBuilder got value with non-existent path (Value: \"" +
                    base.getName().getInternalName() + "\", Path: \"" + path + "\"");
        }));
        config.markBuilt(true);
        config.reloadCache();
        return config;
    }
}