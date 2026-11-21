/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.utils;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigDictionaryImpl implements ConfigDictionary {
    @Nonnull private volatile ConfigLang activeLanguage;
    @Nonnull private final    ConfigLang fallbackLanguage;

    private final Map<ConfigLang, Map<ILocaleType, String>>                            localeTypes;
    private final Map<ConfigLang, Map<IValType, List<Pair<String, String>>>>           defThreshold;
    private final Map<ConfigLang, Map<IValType, List<String>>>                         defUseList;
    private final Map<ConfigLang, Map<IValType, List<Triple<String, String, String>>>> defStandard;
    private final Map<ConfigLang, Map<IValType, String>>                               localizedValTypes;
    private final Map<ConfigLang, Map<IValType, String>>                               rulesValTypes;

    public ConfigDictionaryImpl(ConfigLang activeLanguage) {
        this(activeLanguage, ConfigLang.EN_US,
                new EnumMap<>(ConfigLang.class), new EnumMap<>(ConfigLang.class),
                new EnumMap<>(ConfigLang.class), new EnumMap<>(ConfigLang.class),
                new EnumMap<>(ConfigLang.class), new EnumMap<>(ConfigLang.class));
    }

    public ConfigDictionaryImpl(
            ConfigLang activeLanguage,
            ConfigLang fallbackLanguage,
            Map<ConfigLang, Map<ILocaleType, String>> localeTypes,
            Map<ConfigLang, Map<IValType, List<Pair<String, String>>>> defThreshold,
            Map<ConfigLang, Map<IValType, List<String>>> defUseList,
            Map<ConfigLang, Map<IValType, List<Triple<String, String, String>>>> defStandard,
            Map<ConfigLang, Map<IValType, String>> localizedValTypes,
            Map<ConfigLang, Map<IValType, String>> rulesValTypes
    ) {
        this.activeLanguage = Objects.requireNonNull(activeLanguage);
        this.fallbackLanguage = Objects.requireNonNull(fallbackLanguage);
        this.localeTypes = localeTypes;
        this.defThreshold = defThreshold;
        this.defUseList = defUseList;
        this.defStandard = defStandard;
        this.localizedValTypes = localizedValTypes;
        this.rulesValTypes = rulesValTypes;
    }

    @Override public ConfigLang getActiveLanguage()          { return activeLanguage; }
    @Override public void setActiveLanguage(ConfigLang lang) { activeLanguage = Objects.requireNonNull(lang); }
    @Override public ConfigLang getFallbackLanguage()        { return fallbackLanguage; }

    @Nullable
    private <K, V> V lookup(Map<ConfigLang, Map<K, V>> table, K key) {
        Map<K, V> activeMap = table.get(activeLanguage);
        if (activeMap != null) {
            V val = activeMap.get(key);
            if (val != null) return val;
        }

        Map<K, V> fallbackMap = table.get(fallbackLanguage);
        if (fallbackMap != null) {
            V val = fallbackMap.get(key);
            if (val != null) return val;
        }

        if (fallbackLanguage != ConfigLang.EN_US) {
            Map<K, V> enMap = table.get(ConfigLang.EN_US);
            if (enMap != null) return enMap.get(key);
        }

        return null;
    }

    @Override
    public String getLocaleForType(IValType type) {
        Objects.requireNonNull(type);
        String result = lookup(localizedValTypes, type);
        return result != null ? result : type.getDisplayName();
    }

    @Nullable @Override
    public String getRulesForType(IValType type) {
        Objects.requireNonNull(type);
        return lookup(rulesValTypes, type);
    }

    @Nullable @Override
    public ImmutableList<Pair<String, String>> getDefThresholdForType(IValType type) {
        Objects.requireNonNull(type);
        List<Pair<String, String>> list = lookup(defThreshold, type);
        return list != null ? ImmutableList.copyOf(list) : null;
    }

    @Nullable @Override
    public ImmutableList<String> getDefUseListForType(IValType type) {
        Objects.requireNonNull(type);
        List<String> list = lookup(defUseList, type);
        return list != null ? ImmutableList.copyOf(list) : null;
    }

    @Nullable @Override
    public ImmutableList<Triple<String, String, String>> getDefStandardForType(IValType type) {
        Objects.requireNonNull(type);
        List<Triple<String, String, String>> list = lookup(defStandard, type);
        return list != null ? ImmutableList.copyOf(list) : null;
    }

    @Override
    public String getLocaleForLocale(ILocaleType type) {
        Objects.requireNonNull(type);
        String result = lookup(localeTypes, type);
        return result != null ? result : type.getDisplayName();
    }
}