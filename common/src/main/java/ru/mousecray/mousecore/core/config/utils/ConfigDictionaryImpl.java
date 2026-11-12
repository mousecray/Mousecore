/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.utils;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ConfigDictionaryImpl implements ru.mousecray.mousecore.api.config.utils.ConfigDictionary {
    @Nonnull protected final  ConfigLang                                          currentLanguage;
    @Nullable protected final Map<ILocaleType, String>                            localeTypes;
    @Nullable protected final Map<IValType, List<Pair<String, String>>>           defThreshold;
    @Nullable protected final Map<IValType, List<String>>                         defUseList;
    @Nullable protected final Map<IValType, List<Triple<String, String, String>>> defStandard;
    @Nullable protected final Map<IValType, String>                               localizedValTypes;
    @Nullable protected final Map<IValType, String>                               rulesValTypes;

    public ConfigDictionaryImpl(ConfigLang currentLanguage) {
        this(currentLanguage, null, null, null, null, null, null);
    }

    public ConfigDictionaryImpl(
            ConfigLang currentLanguage, @Nullable Map<ILocaleType, String> localeTypes,
            @Nullable Map<IValType, List<Pair<String, String>>> defThreshold,
            @Nullable Map<IValType, List<String>> defUseList,
            @Nullable Map<IValType, List<Triple<String, String, String>>> defStandard,
            @Nullable Map<IValType, String> localizedValTypes,
            @Nullable Map<IValType, String> rulesValTypes
    ) {
        this.currentLanguage = Objects.requireNonNull(currentLanguage);
        if (localeTypes != null) {
            localeTypes.forEach((val, displayName) -> {
                String tempName = MouseStrings.trimWith(displayName, true, '\t');
                if (tempName != null) {
                    if (tempName.isEmpty()) throw new IllegalArgumentException("localeForValName cannot be empty");
                    if (tempName.contains("\t")) throw new IllegalArgumentException("localeForValName cannot contains tabs");
                }
            });
        }
        this.localeTypes = localeTypes;

        if (defUseList != null) {
            defUseList.forEach((val, list) -> list.forEach(s -> {
                String tempName = MouseStrings.trimWith(s, true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defUseList value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defUseList value cannot contains tabs");
            }));
        }
        this.defUseList = defUseList;

        if (defThreshold != null) {
            defThreshold.forEach((val, list) -> list.forEach(pair -> {
                String tempName = MouseStrings.trimWith(pair.getLeft(), true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defThreshold value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defThreshold value cannot contains tabs");
                tempName = MouseStrings.trimWith(pair.getRight(), true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defThreshold value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defThreshold value cannot contains tabs");
            }));
        }
        this.defThreshold = defThreshold;

        if (defStandard != null) {
            defStandard.forEach((val, list) -> list.forEach(triple -> {
                String tempName = MouseStrings.trimWith(triple.getLeft(), true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defConfigure value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defConfigure value cannot contains tabs");
                tempName = MouseStrings.trimWith(triple.getMiddle(), true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defConfigure value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defConfigure value cannot contains tabs");
                tempName = MouseStrings.trimWith(triple.getRight(), true, '\t');
                if (Objects.requireNonNull(tempName).isEmpty())
                    throw new IllegalArgumentException("defConfigure value cannot be empty");
                if (tempName.contains("\t")) throw new IllegalArgumentException("defConfigure value cannot contains tabs");
            }));
        }
        this.defStandard = defStandard;

        if (localizedValTypes != null) {
            localizedValTypes.forEach((val, displayName) -> {
                String tempName = MouseStrings.trimWith(displayName, true, '\t');
                if (tempName != null) {
                    if (tempName.isEmpty()) throw new IllegalArgumentException("localeForValName cannot be empty");
                    if (tempName.contains("\t")) throw new IllegalArgumentException("localeForValName cannot contains tabs");
                }
            });
        }
        this.localizedValTypes = localizedValTypes;

        if (rulesValTypes != null) {
            rulesValTypes.forEach((val, text) -> {
                String tempName = MouseStrings.trimWith(text, true, '\t');
                if (tempName != null) {
                    if (tempName.isEmpty()) throw new IllegalArgumentException("rulesForVal cannot be empty");
                    if (tempName.contains("\t")) throw new IllegalArgumentException("rulesForVal cannot contains tabs");
                }
            });
        }
        this.rulesValTypes = rulesValTypes;
    }

    @Nonnull @Override public String getLocaleForType(IValType type) {
        Objects.requireNonNull(type);
        if (localizedValTypes == null) return type.getDisplayName();
        else {
            String s = localizedValTypes.get(type);
            if (s == null) s = type.getDisplayName();
            return s;
        }
    }

    @Nullable @Override public String getRulesForType(IValType type) {
        Objects.requireNonNull(type);
        return rulesValTypes == null ? null : rulesValTypes.get(type);
    }

    @Nullable @Override public ImmutableList<Pair<String, String>> getDefThresholdForType(IValType type) {
        Objects.requireNonNull(type);
        return defThreshold == null ? null : ImmutableList.copyOf(defThreshold.get(type));
    }

    @Nullable @Override public ImmutableList<String> getDefUseListForType(IValType type) {
        Objects.requireNonNull(type);
        return defUseList == null ? null : ImmutableList.copyOf(defUseList.get(type));
    }

    @Nullable @Override public ImmutableList<Triple<String, String, String>> getDefStandardForType(IValType type) {
        Objects.requireNonNull(type);
        return defStandard == null ? null : ImmutableList.copyOf(defStandard.get(type));
    }

    @Nonnull @Override public String getLocaleForLocale(ILocaleType type) {
        Objects.requireNonNull(type);
        if (localeTypes == null) return type.getDisplayName();
        else {
            String s = localeTypes.get(type);
            if (s == null) s = type.getDisplayName();
            return s;
        }
    }
}