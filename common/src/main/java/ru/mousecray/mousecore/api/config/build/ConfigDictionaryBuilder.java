/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ConfigLang;
import ru.mousecray.mousecore.api.config.ILocaleType;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDictionaryBuilder {
    @Nonnull private final MouseConfigBuilder                                  owner;
    @Nonnull private final ConfigLang                                          currentLanguage;
    @Nullable private      Map<ILocaleType, String>                            localeTypes;
    @Nullable private      Map<IValType, List<Pair<String, String>>>           defThreshold;
    @Nullable private      Map<IValType, List<String>>                         defUseList;
    @Nullable private      Map<IValType, List<Triple<String, String, String>>> defStandard;
    @Nullable private      Map<IValType, String>                               localizedValTypes;
    @Nullable private      Map<IValType, String>                               rulesValTypes;

    private ConfigDictionaryBuilder(MouseConfigBuilder owner, ConfigLang currentLanguage) {
        this.owner = owner;
        this.currentLanguage = currentLanguage;
    }

    public static ConfigDictionaryBuilder create(MouseConfigBuilder owner, ConfigLang currentLanguage) {
        return new ConfigDictionaryBuilder(Objects.requireNonNull(owner), Objects.requireNonNull(currentLanguage));
    }

    public ConfigDictionaryBuilder setDefaultForLanguage() {
        switch (currentLanguage) {
            default:
            case EN_US:
                if (localeTypes != null) localeTypes.clear();
                if (localizedValTypes != null) localizedValTypes.clear();
                if (rulesValTypes != null) rulesValTypes.clear();
                setLocaleForLocale(ConfigLocaleType.DEFAULT, "Default");
                setLocaleForLocale(ConfigLocaleType.DISABLED, "Disabled");
                setLocaleForLocale(ConfigLocaleType.PREDEFINED, "Predefined");
                setLocaleForLocale(ConfigLocaleType.RANGE, "Valid Range");
                setLocaleForLocale(ConfigLocaleType.TYPE, "Type");
                setLocaleForLocale(ConfigLocaleType.DISABLE_PAR, "Disable");
                setLocaleForLocale(ConfigLocaleType.DISABLED_STATE, "Disabled");
                setLocaleForLocale(ConfigLocaleType.CONSTRAINTS, "Constraints");
                setLocaleForLocale(ConfigLocaleType.VARIANTS, "Variants");
                setLocaleForLocale(ConfigLocaleType.RULES, "Rules");
                setLocaleForLocale(ConfigLocaleType.ANY_VARIANT, "Any");
                setLocaleForLocale(ConfigLocaleType.NONE_VARIANT, "None");
                setLocaleForLocale(ConfigLocaleType.LIST_VARIANT, "Use List");
                setLocaleForValType(ConfigValType.LOGICAL, "Logical");
                setLocaleForValType(ConfigValType.DECIMAL, "Decimal Number");
                setLocaleForValType(ConfigValType.INTEGRAL, "Integral Number");
                setLocaleForValType(ConfigValType.STRING, "String");
                setLocaleForValType(ConfigValType.PERCENT, "Percent");
                setLocaleForValType(ConfigValType.UNDEFINED, "Undefined");
                setLocaleForValType(ConfigValType.RANDOM_QUANTITY, "Random Quantity",
                        "[min]-[max]:[chance]%");
                setLocaleForValType(ConfigValType.CONDITION, "Condition");
                setLocaleForValType(ConfigValType.LIST, "List",
                        "Value delimiter - [,]. Before start new line - [│] like a section");
                setLocaleForValType(ConfigValType.CONDITIONAL_LIST, "Conditional List",
                        "Value delimiter - [,]. [!] before value - opposite. " +
                                "Before start new line - [│] like a section");
                return this;
            case RU_RU:
                if (localeTypes != null) localeTypes.clear();
                if (localizedValTypes != null) localizedValTypes.clear();
                if (rulesValTypes != null) rulesValTypes.clear();
                setLocaleForLocale(ConfigLocaleType.DEFAULT, "По-умолчанию");
                setLocaleForLocale(ConfigLocaleType.DISABLED, "Отключённое");
                setLocaleForLocale(ConfigLocaleType.PREDEFINED, "Предопределёно");
                setLocaleForLocale(ConfigLocaleType.RANGE, "Диапазон");
                setLocaleForLocale(ConfigLocaleType.TYPE, "Тип");
                setLocaleForLocale(ConfigLocaleType.DISABLE_PAR, "Отключить");
                setLocaleForLocale(ConfigLocaleType.DISABLED_STATE, "Отключено");
                setLocaleForLocale(ConfigLocaleType.CONSTRAINTS, "Ограничения");
                setLocaleForLocale(ConfigLocaleType.VARIANTS, "Варианты");
                setLocaleForLocale(ConfigLocaleType.RULES, "Правила");
                setLocaleForLocale(ConfigLocaleType.ANY_VARIANT, "Любой");
                setLocaleForLocale(ConfigLocaleType.NONE_VARIANT, "Никакой");
                setLocaleForLocale(ConfigLocaleType.LIST_VARIANT, "Использовать список");
                setLocaleForValType(ConfigValType.LOGICAL, "Логический");
                setLocaleForValType(ConfigValType.DECIMAL, "Дробное число");
                setLocaleForValType(ConfigValType.INTEGRAL, "Целое число");
                setLocaleForValType(ConfigValType.STRING, "Строка");
                setLocaleForValType(ConfigValType.PERCENT, "Процент");
                setLocaleForValType(ConfigValType.UNDEFINED, "Не определено");
                setLocaleForValType(ConfigValType.RANDOM_QUANTITY, "Случайное кол-во",
                        "[мин]-[макс]:[шанс]%");
                setLocaleForValType(ConfigValType.CONDITION, "Условие");
                setLocaleForValType(ConfigValType.LIST, "Список",
                        "Разделитель значений - [,]. В начале новой строки - [│] как в секции");
                setLocaleForValType(ConfigValType.CONDITIONAL_LIST, "Условный список",
                        "Разделитель значений - [,]. [!] перед значением - противоположное. " +
                                "В начале новой строки - [│] как в секции");
                return this;
        }
    }

    public ConfigDictionaryBuilder setLocaleForLocale(ILocaleType type, @Nullable String displayName) {
        Objects.requireNonNull(type);
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (displayName != null) {
            if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
            if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        }
        if (localeTypes == null) localeTypes = new HashMap<>();
        localeTypes.put(type, displayName);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigDictionaryBuilder setLocaleForValType(@Nonnull IValType type, @Nullable String displayName, @Nullable String ruleString) {
        Objects.requireNonNull(type);
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (displayName != null) {
            if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
            if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        }
        if (localizedValTypes == null) localizedValTypes = new HashMap<>();
        localizedValTypes.put(type, displayName);
        if (ruleString != null) {
            ruleString = MouseStrings.trimWith(ruleString, true, '\t');
            if (ruleString.isEmpty()) throw new IllegalArgumentException("ruleString cannot be empty");
            if (ruleString.contains("\t")) throw new IllegalArgumentException("ruleString cannot contains tabs");
            if (rulesValTypes == null) rulesValTypes = new HashMap<>();
            rulesValTypes.put(type, ruleString);
        }
        return this;
    }

    public ConfigDictionaryBuilder setLocaleForValType(@Nonnull IValType type, @Nullable String displayName) {
        return setLocaleForValType(type, displayName, null);
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigDictionaryBuilder addDefaultThresholdForType(String maxDisplayName, String minDisplayName, IValType... types) {
        Objects.requireNonNull(types);
        minDisplayName = MouseStrings.trimWith(Objects.requireNonNull(minDisplayName), true, '\t');
        if (minDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (minDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        maxDisplayName = MouseStrings.trimWith(Objects.requireNonNull(maxDisplayName), true, '\t');
        if (maxDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (maxDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");

        if (defThreshold == null) defThreshold = new HashMap<>();
        for (IValType type : types) {
            List<Pair<String, String>> values = defThreshold.computeIfAbsent(Objects.requireNonNull(type),
                    key -> new ArrayList<>());
            values.add(Pair.of(minDisplayName, maxDisplayName));
        }
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigDictionaryBuilder addDefaultUseListForType(String listDisplayName, IValType... types) {
        Objects.requireNonNull(types);
        listDisplayName = MouseStrings.trimWith(Objects.requireNonNull(listDisplayName), true, '\t');
        if (listDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (listDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");

        if (defUseList == null) defUseList = new HashMap<>();
        for (IValType type : types) {
            List<String> values = defUseList.computeIfAbsent(Objects.requireNonNull(type),
                    key -> new ArrayList<>());
            values.add(listDisplayName);
        }
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigDictionaryBuilder addDefaultStandardForType(
            String lowDisplayName, String middleDisplayName, String highDisplayName,
            IValType... types
    ) {
        Objects.requireNonNull(types);
        lowDisplayName = MouseStrings.trimWith(Objects.requireNonNull(lowDisplayName), true, '\t');
        if (lowDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (lowDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        middleDisplayName = MouseStrings.trimWith(Objects.requireNonNull(middleDisplayName), true, '\t');
        if (middleDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (middleDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        highDisplayName = MouseStrings.trimWith(Objects.requireNonNull(highDisplayName), true, '\t');
        if (highDisplayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (highDisplayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");

        if (defStandard == null) defStandard = new HashMap<>();
        for (IValType type : types) {
            List<Triple<String, String, String>> values = defStandard.computeIfAbsent(Objects.requireNonNull(type),
                    key -> new ArrayList<>());
            values.add(Triple.of(lowDisplayName, middleDisplayName, highDisplayName));
        }
        return this;
    }

    public MouseConfigBuilder buildDictionary() {
        owner.dictionary = new ConfigDictionary(currentLanguage, localeTypes, defThreshold, defUseList, defStandard, localizedValTypes, rulesValTypes);
        return owner;
    }
}
