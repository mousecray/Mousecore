/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.struct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.build.MouseConfigBuilder;
import ru.mousecray.mousecore.core.config.utils.ConfigDictionaryImpl;

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
                setLocaleForValType(ValType.PLUS_MINUS, "Логический");
                setLocaleForValType(ValType.DECIMAL, "Дробное число");
                setLocaleForValType(ValType.INTEGRAL, "Целое число");
                setLocaleForValType(ValType.SIMPLE_STRING, "Строка");
                setLocaleForValType(ValType.PERCENT, "Процент");
                setLocaleForValType(ValType.UNDEFINED, "Не определено");
                setLocaleForValType(ValType.RANDOM_QUANTITY, "Случайное кол-во",
                        "[мин]-[макс]:[шанс]%");
                setLocaleForValType(ValType.SIMPLE_CONDITION, "Условие");
                setLocaleForValType(ValType.SIMPLE_LIST, "Список",
                        "Разделитель значений - [,]. В начале новой строки - [│] как в секции");
                setLocaleForValType(ValType.CONDITIONAL_LIST, "Условный список",
                        "Разделитель значений - [,]. [!] перед значением - противоположное. " +
                                "В начале новой строки - [│] как в секции");
                return this;

            case EN_US:
            default:
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
                setLocaleForValType(ValType.PLUS_MINUS, "Logical");
                setLocaleForValType(ValType.DECIMAL, "Decimal Number");
                setLocaleForValType(ValType.INTEGRAL, "Integral Number");
                setLocaleForValType(ValType.SIMPLE_STRING, "String");
                setLocaleForValType(ValType.PERCENT, "Percent");
                setLocaleForValType(ValType.UNDEFINED, "Undefined");
                setLocaleForValType(ValType.RANDOM_QUANTITY, "Random Quantity",
                        "[min]-[max]:[chance]%");
                setLocaleForValType(ValType.SIMPLE_CONDITION, "Condition");
                setLocaleForValType(ValType.SIMPLE_LIST, "List",
                        "Value delimiter - [,]. Before start new line - [│] like a section");
                setLocaleForValType(ValType.CONDITIONAL_LIST, "Conditional List",
                        "Value delimiter - [,]. [!] before value - opposite. " +
                                "Before start new line - [│] like a section");
                return this;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
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
    public ConfigDictionaryBuilder setLocaleForValType(IValType type, @Nullable String displayName, @Nullable String ruleString) {
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

    @SuppressWarnings("UnusedReturnValue")
    public ConfigDictionaryBuilder setLocaleForValType(IValType type, @Nullable String displayName) {
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
        owner.dictionary = new ConfigDictionaryImpl(currentLanguage, localeTypes, defThreshold, defUseList, defStandard, localizedValTypes, rulesValTypes);
        return owner;
    }
}
