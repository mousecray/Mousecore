/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

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
import java.util.Map;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class DynamicConfigDictionary {
    private final             Map<ConfigLang, ConfigDictionary> dictionaries = new EnumMap<>(ConfigLang.class);
    @Nonnull private volatile ConfigLang                        activeLanguage;
    @Nonnull private final    ConfigLang                        fallbackLanguage;

    public DynamicConfigDictionary(
            Map<ConfigLang, ConfigDictionary> dictionaries,
            ConfigLang initialLanguage, ConfigLang fallbackLanguage
    ) {
        this.dictionaries.putAll(Objects.requireNonNull(dictionaries));
        this.fallbackLanguage = Objects.requireNonNull(fallbackLanguage);
        activeLanguage = Objects.requireNonNull(initialLanguage);
    }

    @Override public ConfigLang getActiveLanguage() {
        return activeLanguage;
    }

    @Override public void setActiveLanguage(ConfigLang language) {
        activeLanguage = Objects.requireNonNull(language);
    }

    public ConfigDictionary getEffectiveDictionary() {
        ConfigDictionary dict = dictionaries.get(activeLanguage);
        if (dict != null) return dict;

        dict = dictionaries.get(fallbackLanguage);
        if (dict != null) return dict;

        dict = dictionaries.get(ConfigLang.EN_US);
        if (dict != null) return dict;

        if (!dictionaries.isEmpty()) return dictionaries.values().iterator().next();

        return new ConfigDictionaryImpl(activeLanguage);
    }

    @Override public String getLocaleForType(IValType type)                              { return getEffectiveDictionary().getLocaleForType(type); }
    @Nullable @Override public String getRulesForType(IValType type)                     { return getEffectiveDictionary().getRulesForType(type); }
    @Override public String getLocaleForLocale(ILocaleType type)                         { return getEffectiveDictionary().getLocaleForLocale(type); }
    @Nullable @Override public ImmutableList<String> getDefUseListForType(IValType type) { return getEffectiveDictionary().getDefUseListForType(type); }

    @Nullable @Override
    public ImmutableList<Pair<String, String>> getDefThresholdForType(IValType type) { return getEffectiveDictionary().getDefThresholdForType(type); }

    @Nullable @Override
    public ImmutableList<Triple<String, String, String>> getDefStandardForType(IValType type) { return getEffectiveDictionary().getDefStandardForType(type); }
}