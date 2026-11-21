/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config.build.struct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.utils.MouseCollections;
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
    @Nonnull private final MouseConfigBuilder owner;

    @Nonnull private ConfigLang workingLanguage  = ConfigLang.EN_US;
    @Nonnull private ConfigLang fallbackLanguage = ConfigLang.EN_US;

    private final Map<ConfigLang, Map<ILocaleType, String>>                            localeTypes       = new EnumMap<>(ConfigLang.class);
    private final Map<ConfigLang, Map<IValType, List<Pair<String, String>>>>           defThreshold      = new EnumMap<>(ConfigLang.class);
    private final Map<ConfigLang, Map<IValType, List<String>>>                         defUseList        = new EnumMap<>(ConfigLang.class);
    private final Map<ConfigLang, Map<IValType, List<Triple<String, String, String>>>> defStandard       = new EnumMap<>(ConfigLang.class);
    private final Map<ConfigLang, Map<IValType, String>>                               localizedValTypes = new EnumMap<>(ConfigLang.class);
    private final Map<ConfigLang, Map<IValType, String>>                               rulesValTypes     = new EnumMap<>(ConfigLang.class);

    private ConfigDictionaryBuilder(MouseConfigBuilder owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    public static ConfigDictionaryBuilder create(MouseConfigBuilder owner) {
        return new ConfigDictionaryBuilder(owner);
    }

    public static ConfigDictionaryBuilder create(MouseConfigBuilder owner, ConfigLang defaultWorkingLang) {
        ConfigDictionaryBuilder builder = new ConfigDictionaryBuilder(owner);
        builder.workingLanguage = Objects.requireNonNull(defaultWorkingLang);
        return builder;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ConfigDictionaryBuilder setWorkingLanguage(ConfigLang language) {
        workingLanguage = Objects.requireNonNull(language);
        return this;
    }

    public ConfigDictionaryBuilder setFallbackLanguage(ConfigLang language) {
        fallbackLanguage = Objects.requireNonNull(language);
        return this;
    }

    public ConfigDictionaryBuilder setDefaults() {
        setDefaults(ConfigLang.EN_US, ConfigLang.RU_RU);
        return this;
    }

    @SuppressWarnings({ "ConstantValue", "UnusedReturnValue" })
    public ConfigDictionaryBuilder setDefaults(ConfigLang... langs) {
        ConfigLang[] res = MouseCollections.map(v -> {
            setDefaultForLanguage(v);
            return v;
        }, true, langs);
        if (res == null) {
            if (owner.logger != null) {
                owner.logger.atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("MouseConfig {0} get null defaults languages, that will be skipped");
            }
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ConfigDictionaryBuilder setDefaultForLanguage(ConfigLang language) {
        Objects.requireNonNull(language);
        switch (language) {
            case RU_RU -> {
                setLocaleForLocale(language, ConfigLocaleType.DEFAULT, "По-умолчанию");
                setLocaleForLocale(language, ConfigLocaleType.DISABLED, "Отключённое");
                setLocaleForLocale(language, ConfigLocaleType.PREDEFINED, "Предопределёно");
                setLocaleForLocale(language, ConfigLocaleType.RANGE, "Диапазон");
                setLocaleForLocale(language, ConfigLocaleType.TYPE, "Тип");
                setLocaleForLocale(language, ConfigLocaleType.DISABLE_PAR, "Отключить");
                setLocaleForLocale(language, ConfigLocaleType.DISABLED_STATE, "Отключено");
                setLocaleForLocale(language, ConfigLocaleType.CONSTRAINTS, "Ограничения");
                setLocaleForLocale(language, ConfigLocaleType.VARIANTS, "Варианты");
                setLocaleForLocale(language, ConfigLocaleType.RULES, "Правила");
                setLocaleForLocale(language, ConfigLocaleType.ANY_VARIANT, "Любой");
                setLocaleForLocale(language, ConfigLocaleType.NONE_VARIANT, "Никакой");
                setLocaleForLocale(language, ConfigLocaleType.LIST_VARIANT, "Использовать список");
                setLocaleForLocale(language, ConfigLocaleType.RULE_MODIFIER_CONDITIONAL, "[!] перед значением - противоположное");
                setLocaleForLocale(language, ConfigLocaleType.RULE_MODIFIER_UNIQUE, "Дубликаты запрещены");

                setLocaleForValType(language, ValType.PLUS_MINUS, "Логический");
                setLocaleForValType(language, ValType.DECIMAL, "Дробное число");
                setLocaleForValType(language, ValType.INTEGRAL, "Целое число");
                setLocaleForValType(language, ValType.SIMPLE_STRING, "Строка");
                setLocaleForValType(language, ValType.PERCENT, "Процент");
                setLocaleForValType(language, ValType.UNDEFINED, "Не определено");
                setLocaleForValType(language, ValType.RANDOM_QUANTITY, "Случайное кол-во", "[мин]-[макс]:[шанс]%");
                setLocaleForValType(language, ValType.SIMPLE_CONDITION, "Условие");
                setLocaleForValType(language, ValType.SIMPLE_LIST, "Список", "Разделитель значений - [,]. В начале новой строки - [│] как в секции");

                setLocaleForValType(language, ValType.RANGE, "Диапазон");
                setLocaleForValType(language, ValType.TIME, "Время");
                setLocaleForValType(language, ValType.VECTOR3D, "Вектор 3D");
                setLocaleForValType(language, ValType.BLOCK_POS, "Позиция блока");
                setLocaleForValType(language, ValType.ENUM, "Перечисление");
                setLocaleForValType(language, ValType.COLOR, "Цвет");
                setLocaleForValType(language, ValType.REGEX, "Регулярное выражение");
                setLocaleForValType(language, ValType.EXPRESSION, "Математическое выражение");
                setLocaleForValType(language, ValType.SIMPLE_TUPLE, "Кортеж");
                setLocaleForValType(language, ValType.SIMPLE_MAP, "Карта");
                setLocaleForValType(language, ValType.SIMPLE_TABLE, "Таблица");
                setLocaleForValType(language, ValType.SIMPLE_MATRIX, "Матрица");
                setLocaleForValType(language, ValType.NBT, "NBT");
                setLocaleForValType(language, ValType.MINECRAFT_BLOCK, "Блок Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_ITEM, "Предмет Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_ITEM_STACK, "Стак предметов Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_TAG, "Тег Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_ENTITY, "Сущность Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_DIMENSION, "Измерение Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_BIOME, "Биом Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_EFFECT, "Эффект Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_ENCHANTMENT, "Зачарование Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_PARTICLE, "Частица Minecraft");
                setLocaleForValType(language, ValType.MINECRAFT_SOUND, "Звук Minecraft");

                setLocaleForLocale(language, ConfigLocaleType.GROUP_RULE, "Правило");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_FILTER_LIST, "Список фильтрации");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_VALUE, "Значение");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_RANGE, "Диапазон");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_MATCH_VALUE, "Значение при совпадении");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_ELSE_VALUE, "Значение иначе");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_REPLACEMENT, "Замена");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_FALLBACK, "По-умолчанию");

                setLocaleForLocale(language, ConfigLocaleType.STATE, "Состояние");
                setLocaleForLocale(language, ConfigLocaleType.STATE_FROZEN, "Заморожено");
                setLocaleForLocale(language, ConfigLocaleType.STATE_NEW, "Новое");
                setLocaleForLocale(language, ConfigLocaleType.STATE_DEPRECATED, "Устарело");
                setLocaleForLocale(language, ConfigLocaleType.STATE_UNUSED, "Не используется");
                setLocaleForLocale(language, ConfigLocaleType.STATE_EXPERIMENTAL, "Экспериментально");
                setLocaleForLocale(language, ConfigLocaleType.SIDE, "Сторона");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_CLIENT, "Клиент");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_SERVER, "Сервер");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_SYNCED, "Синхронизируемый");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_COMMON, "Общий");
                setLocaleForLocale(language, ConfigLocaleType.SCHEMA_VERSION, "Версия схемы");
            }

            case EN_US, default -> {
                setLocaleForLocale(language, ConfigLocaleType.DEFAULT, "Default");
                setLocaleForLocale(language, ConfigLocaleType.DISABLED, "Disabled");
                setLocaleForLocale(language, ConfigLocaleType.PREDEFINED, "Predefined");
                setLocaleForLocale(language, ConfigLocaleType.RANGE, "Valid Range");
                setLocaleForLocale(language, ConfigLocaleType.TYPE, "Type");
                setLocaleForLocale(language, ConfigLocaleType.DISABLE_PAR, "Disable");
                setLocaleForLocale(language, ConfigLocaleType.DISABLED_STATE, "Disabled");
                setLocaleForLocale(language, ConfigLocaleType.CONSTRAINTS, "Constraints");
                setLocaleForLocale(language, ConfigLocaleType.VARIANTS, "Variants");
                setLocaleForLocale(language, ConfigLocaleType.RULES, "Rules");
                setLocaleForLocale(language, ConfigLocaleType.ANY_VARIANT, "Any");
                setLocaleForLocale(language, ConfigLocaleType.NONE_VARIANT, "None");
                setLocaleForLocale(language, ConfigLocaleType.LIST_VARIANT, "Use List");
                setLocaleForLocale(language, ConfigLocaleType.RULE_MODIFIER_CONDITIONAL, "[!] before value - opposite");
                setLocaleForLocale(language, ConfigLocaleType.RULE_MODIFIER_UNIQUE, "Duplicates are not allowed");

                setLocaleForValType(language, ValType.PLUS_MINUS, "Logical");
                setLocaleForValType(language, ValType.DECIMAL, "Decimal Number");
                setLocaleForValType(language, ValType.INTEGRAL, "Integral Number");
                setLocaleForValType(language, ValType.SIMPLE_STRING, "String");
                setLocaleForValType(language, ValType.PERCENT, "Percent");
                setLocaleForValType(language, ValType.UNDEFINED, "Undefined");
                setLocaleForValType(language, ValType.RANDOM_QUANTITY, "Random Quantity", "[min]-[max]:[chance]%");
                setLocaleForValType(language, ValType.SIMPLE_CONDITION, "Condition");
                setLocaleForValType(language, ValType.SIMPLE_LIST, "List", "Value delimiter - [,]. Before start new line - [│] like a section");

                setLocaleForValType(language, ValType.RANGE, "Range");
                setLocaleForValType(language, ValType.TIME, "Time");
                setLocaleForValType(language, ValType.VECTOR3D, "Vector 3D");
                setLocaleForValType(language, ValType.BLOCK_POS, "Block Position");
                setLocaleForValType(language, ValType.ENUM, "Enum");
                setLocaleForValType(language, ValType.COLOR, "Color");
                setLocaleForValType(language, ValType.REGEX, "Regex");
                setLocaleForValType(language, ValType.EXPRESSION, "Expression");
                setLocaleForValType(language, ValType.SIMPLE_TUPLE, "Tuple");
                setLocaleForValType(language, ValType.SIMPLE_MAP, "Map");
                setLocaleForValType(language, ValType.SIMPLE_TABLE, "Table");
                setLocaleForValType(language, ValType.SIMPLE_MATRIX, "Matrix");
                setLocaleForValType(language, ValType.NBT, "NBT");
                setLocaleForValType(language, ValType.MINECRAFT_BLOCK, "Minecraft Block");
                setLocaleForValType(language, ValType.MINECRAFT_ITEM, "Minecraft Item");
                setLocaleForValType(language, ValType.MINECRAFT_ITEM_STACK, "Minecraft Item Stack");
                setLocaleForValType(language, ValType.MINECRAFT_TAG, "Minecraft Tag");
                setLocaleForValType(language, ValType.MINECRAFT_ENTITY, "Minecraft Entity");
                setLocaleForValType(language, ValType.MINECRAFT_DIMENSION, "Minecraft Dimension");
                setLocaleForValType(language, ValType.MINECRAFT_BIOME, "Minecraft Biome");
                setLocaleForValType(language, ValType.MINECRAFT_EFFECT, "Minecraft Effect");
                setLocaleForValType(language, ValType.MINECRAFT_ENCHANTMENT, "Minecraft Enchantment");
                setLocaleForValType(language, ValType.MINECRAFT_PARTICLE, "Minecraft Particle");
                setLocaleForValType(language, ValType.MINECRAFT_SOUND, "Minecraft Sound");

                setLocaleForLocale(language, ConfigLocaleType.GROUP_RULE, "Rule");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_FILTER_LIST, "Filter List");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_VALUE, "Value");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_RANGE, "Range");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_MATCH_VALUE, "Match Value");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_ELSE_VALUE, "Else Value");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_REPLACEMENT, "Replacement");
                setLocaleForLocale(language, ConfigLocaleType.GROUP_FALLBACK, "Fallback");

                setLocaleForLocale(language, ConfigLocaleType.STATE, "State");
                setLocaleForLocale(language, ConfigLocaleType.STATE_FROZEN, "Frozen");
                setLocaleForLocale(language, ConfigLocaleType.STATE_NEW, "New");
                setLocaleForLocale(language, ConfigLocaleType.STATE_DEPRECATED, "Deprecated");
                setLocaleForLocale(language, ConfigLocaleType.STATE_UNUSED, "Unused");
                setLocaleForLocale(language, ConfigLocaleType.STATE_EXPERIMENTAL, "Experimental");
                setLocaleForLocale(language, ConfigLocaleType.SIDE, "Side");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_CLIENT, "Client");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_SERVER, "Server");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_SYNCED, "Synced");
                setLocaleForLocale(language, ConfigLocaleType.SIDE_COMMON, "Common");
                setLocaleForLocale(language, ConfigLocaleType.SCHEMA_VERSION, "Schema Version");
            }
        }
        return this;
    }

    public ConfigDictionaryBuilder setLocaleForLocale(ConfigLang lang, ILocaleType type, @Nullable String displayName) {
        Objects.requireNonNull(lang);
        Objects.requireNonNull(type);
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (displayName != null) {
            if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
            if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        }
        localeTypes.computeIfAbsent(lang, k -> new HashMap<>()).put(type, displayName);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public ConfigDictionaryBuilder setLocaleForValType(ConfigLang lang, IValType type, @Nullable String displayName, @Nullable String ruleString) {
        Objects.requireNonNull(lang);
        Objects.requireNonNull(type);
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (displayName != null) {
            if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
            if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        }
        localizedValTypes.computeIfAbsent(lang, k -> new HashMap<>()).put(type, displayName);

        if (ruleString != null) {
            ruleString = MouseStrings.trimWith(ruleString, true, '\t');
            if (ruleString.isEmpty()) throw new IllegalArgumentException("ruleString cannot be empty");
            if (ruleString.contains("\t")) throw new IllegalArgumentException("ruleString cannot contains tabs");
            rulesValTypes.computeIfAbsent(lang, k -> new HashMap<>()).put(type, ruleString);
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ConfigDictionaryBuilder setLocaleForValType(ConfigLang lang, IValType type, @Nullable String displayName) {
        return setLocaleForValType(lang, type, displayName, null);
    }

    public ConfigDictionaryBuilder addDefaultThresholdForType(ConfigLang lang, String maxDisplayName, String minDisplayName, IValType... types) {
        Objects.requireNonNull(lang);
        Objects.requireNonNull(types);
        minDisplayName = MouseStrings.trimWith(Objects.requireNonNull(minDisplayName), true, '\t');
        maxDisplayName = MouseStrings.trimWith(Objects.requireNonNull(maxDisplayName), true, '\t');

        Map<IValType, List<Pair<String, String>>> map = defThreshold.computeIfAbsent(lang, k -> new HashMap<>());
        for (IValType type : types) {
            map.computeIfAbsent(
                    Objects.requireNonNull(type), k -> new ArrayList<>()
            ).add(Pair.of(minDisplayName, maxDisplayName));
        }
        return this;
    }

    public ConfigDictionaryBuilder addDefaultUseListForType(ConfigLang lang, String listDisplayName, IValType... types) {
        Objects.requireNonNull(lang);
        Objects.requireNonNull(types);
        listDisplayName = MouseStrings.trimWith(Objects.requireNonNull(listDisplayName), true, '\t');

        Map<IValType, List<String>> map = defUseList.computeIfAbsent(lang, k -> new HashMap<>());
        for (IValType type : types) map.computeIfAbsent(Objects.requireNonNull(type), k -> new ArrayList<>()).add(listDisplayName);
        return this;
    }

    public ConfigDictionaryBuilder addDefaultStandardForType(
            ConfigLang lang, String lowDisplayName, String middleDisplayName, String highDisplayName, IValType... types
    ) {
        Objects.requireNonNull(lang);
        Objects.requireNonNull(types);
        lowDisplayName = MouseStrings.trimWith(Objects.requireNonNull(lowDisplayName), true, '\t');
        middleDisplayName = MouseStrings.trimWith(Objects.requireNonNull(middleDisplayName), true, '\t');
        highDisplayName = MouseStrings.trimWith(Objects.requireNonNull(highDisplayName), true, '\t');

        Map<IValType, List<Triple<String, String, String>>> map = defStandard.computeIfAbsent(lang, k -> new HashMap<>());
        for (IValType type : types) {
            map.computeIfAbsent(
                    Objects.requireNonNull(type),
                    k -> new ArrayList<>()).add(Triple.of(lowDisplayName, middleDisplayName, highDisplayName)
            );
        }
        return this;
    }

    public ConfigDictionaryBuilder setLocaleForLocale(ILocaleType type, @Nullable String displayName) {
        return setLocaleForLocale(workingLanguage, type, displayName);
    }

    public ConfigDictionaryBuilder setLocaleForValType(IValType type, @Nullable String displayName, @Nullable String ruleString) {
        return setLocaleForValType(workingLanguage, type, displayName, ruleString);
    }

    public ConfigDictionaryBuilder setLocaleForValType(IValType type, @Nullable String displayName) {
        return setLocaleForValType(workingLanguage, type, displayName, null);
    }

    public ConfigDictionaryBuilder addDefaultThresholdForType(String maxDisplayName, String minDisplayName, IValType... types) {
        return addDefaultThresholdForType(workingLanguage, maxDisplayName, minDisplayName, types);
    }

    public ConfigDictionaryBuilder addDefaultUseListForType(String listDisplayName, IValType... types) {
        return addDefaultUseListForType(workingLanguage, listDisplayName, types);
    }

    public ConfigDictionaryBuilder addDefaultStandardForType(String low, String mid, String high, IValType... types) {
        return addDefaultStandardForType(workingLanguage, low, mid, high, types);
    }

    public ConfigDictionary buildDictionary() {
        return new ConfigDictionaryImpl(
                workingLanguage, fallbackLanguage,
                localeTypes, defThreshold, defUseList, defStandard, localizedValTypes, rulesValTypes
        );
    }

    public MouseConfigBuilder finishDictionary() {
        owner.setDictionary(buildDictionary());
        return owner;
    }
}