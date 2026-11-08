/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.values;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.ILocaleType;
import ru.mousecray.mousecore.api.config.IValType;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.ConfigListVal;
import ru.mousecray.mousecore.api.config.values.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.values.ConditionType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseReflection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
public class ConfigConditionVal<
        V extends CustomType<?>
        > extends ConfigOtherVal<ConditionType<ListType<?, V>, V>> {
    @Nullable protected final String                   pathToParList;
    @Nullable protected       Supplier<ListType<?, V>> listSupplier;
    @Nullable private         ListType<?, V>           cachedList;
    protected final           IValType                 listComponentType;
    @Nullable protected final String                   stringAny;
    @Nullable protected final String                   stringNone;
    @Nullable protected final String                   stringUseList;

    @SafeVarargs
    private ConfigConditionVal(
            IValType listComponentType,
            @Nullable String specificDataType, boolean enableThreshold,
            ConditionType<ListType<?, V>, V> defaultValue,
            ConditionType<ListType<?, V>, V> disabledValue,
            @Nullable String pathToParList, @Nullable Supplier<ListType<?, V>> listSupplier,
            @Nullable String stringAny, @Nullable String stringNone, @Nullable String stringUseList,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<String> additionalUseList,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionType<ListType<?, V>, V>>... configureValues
    ) {
        super(
                ConfigValType.CONDITION,
                Objects.requireNonNull(defaultValue),
                Objects.requireNonNull(disabledValue),
                specificDataType,
                constraints,
                genValues(
                        enableThreshold, pathToParList != null || listSupplier != null, stringAny,
                        stringNone, stringUseList, additionalThreshold, additionalUseList, configureValues
                )
        );
        this.pathToParList = pathToParList;
        this.listSupplier = listSupplier;
        this.listComponentType = Objects.requireNonNull(listComponentType);
        this.stringAny = stringAny;
        this.stringNone = stringNone;
        this.stringUseList = pathToParList != null || listSupplier != null ? stringUseList : null;
    }

    @SafeVarargs
    public ConfigConditionVal(
            IValType listComponentType,
            @Nullable String specificDataType, boolean enableThreshold,
            ConditionType<ListType<?, V>, V> defaultValue,
            ConditionType<ListType<?, V>, V> disabledValue,
            @Nullable Supplier<ListType<?, V>> listSupplier,
            @Nullable String stringAny, @Nullable String stringNone, @Nullable String stringUseList,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<String> additionalUseList,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionType<ListType<?, V>, V>>... configureValues
    ) {
        this(
                listComponentType, specificDataType, enableThreshold,
                defaultValue, disabledValue, null,
                listSupplier, stringAny, stringNone, stringUseList, additionalThreshold, additionalUseList,
                constraints, configureValues
        );
    }

    @SafeVarargs
    public ConfigConditionVal(
            IValType listComponentType,
            @Nullable String specificDataType, boolean enableThreshold,
            ConditionType<ListType<?, V>, V> defaultValue,
            ConditionType<ListType<?, V>, V> disabledValue,
            @Nullable String pathToParList,
            @Nullable String stringAny, @Nullable String stringNone, @Nullable String stringUseList,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<String> additionalUseList,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<ConditionType<ListType<?, V>, V>>... configureValues
    ) {
        this(
                listComponentType, specificDataType, enableThreshold,
                defaultValue, disabledValue, pathToParList,
                null, stringAny, stringNone, stringUseList, additionalThreshold, additionalUseList,
                constraints, configureValues
        );
    }

    @Nullable
    protected static <V extends CustomType<?>> PredefinedValue<ConditionType<ListType<?, V>, V>>[] genValues(
            boolean tv, boolean lv,
            @Nullable String stringAny, @Nullable String stringNone, @Nullable String stringUseList,
            @Nullable List<Pair<String, String>> additionalThreshold,
            @Nullable List<String> additionalUseList,
            @Nullable PredefinedValue<ConditionType<ListType<?, V>, V>>... predefinedValues
    ) {
        predefinedValues = MouseCollections.map(
                val -> val.getValue() != null ? val : new PredefinedValue<>(
                        val.getDisplayName(),
                        ConditionType.NONE(
                                stringNone != null ? stringNone : ConfigLocaleType.NONE_VARIANT.getDisplayName()
                        )

                ),
                true, predefinedValues
        );

        List<PredefinedValue<ConditionType<ListType<?, V>, V>>> list = new ArrayList<>();
        if (tv) {
            list.add(new PredefinedValue<>("+",
                    ConditionType.ANY(
                            stringAny != null ? stringAny : ConfigLocaleType.ANY_VARIANT.getDisplayName()
                    )
            ));
            list.add(new PredefinedValue<>("-",
                    ConditionType.NONE(
                            stringNone != null ? stringNone : ConfigLocaleType.NONE_VARIANT.getDisplayName()
                    )
            ));
        }
        if (additionalThreshold != null) {
            for (Pair<String, String> pair : additionalThreshold) {
                list.add(new PredefinedValue<>(pair.getLeft(),
                        ConditionType.ANY(
                                stringAny != null ? stringAny : ConfigLocaleType.ANY_VARIANT.getDisplayName()
                        )
                ));
                list.add(new PredefinedValue<>(pair.getRight(),
                        ConditionType.NONE(
                                stringNone != null ? stringNone : ConfigLocaleType.NONE_VARIANT.getDisplayName()
                        )
                ));
            }
        }
        if (lv) {
            list.add(
                    new PredefinedValue<>("*", ConditionType.create(
                            (l, val) -> l != null && l.containsOriginalValue(val),
                            stringUseList != null ? stringUseList : ConfigLocaleType.LIST_VARIANT.getDisplayName()
                    ))
            );
        }
        if (additionalUseList != null) {
            for (String s : additionalUseList) {
                list.add(
                        new PredefinedValue<>(s, ConditionType.create(
                                (l, val) -> l != null && l.containsOriginalValue(val),
                                stringUseList != null ? stringUseList : ConfigLocaleType.LIST_VARIANT.getDisplayName()
                        ))
                );
            }
        }
        return list.isEmpty() ? predefinedValues
                : MouseCollections.addAll(PredefinedValue.class, predefinedValues, list);
    }

    @Nullable public String getPathToParList()                     { return pathToParList; }
    public IValType getListComponentType()                         { return listComponentType; }

    @Nullable protected Supplier<ListType<?, V>> getListSupplier() { return listSupplier; }

    public boolean test(V value) {
        return getValue().test(listSupplier != null ? listSupplier.get() : null, value);
    }

    @Override
    protected VariableValue<ConditionType<ListType<?, V>, V>> parseValue(@Nullable String value) {
        return VariableValue.create();
    }

    @SuppressWarnings({ "DataFlowIssue", "unchecked" }) @Override
    protected void reloadCache() {
        super.reloadCache();

        if (cachedList != null) cachedList.clear();

        if (pathToParList != null) {
            ConfigListVal<?, ?, ?> list = getConfig().getListVal(pathToParList);
            if (list != null) {
                if (listComponentType.equals(list.getListComponentType())) {
                    VariableValue<ListType> var = MouseReflection.invokeMethod(
                            ConfigListVal.class, ListType.class,
                            "getList", list,
                            hasLogger() ? getLogger() : null
                    );
                    if (var.isPresent()) {
                        cachedList = (ListType<?, V>) var.getValue();
                        listSupplier = () -> cachedList;
                        return;
                    } else {
                        if (hasLogger()) {
                            getLogger().atWarn()
                                    .withPrefix(LogPrefixes.CONFIG)
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("'{0}' for ConfigConditionVal '{1}' found but that method 'getList' not present or broken",
                                            pathToParList, owner.getName());
                        }
                    }
                }
            }
            if (hasLogger()) {
                getLogger().atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("'{0}' is not found or incompatible type for ConfigConditionVal '{1}'",
                                pathToParList, owner.getName());
            }
        } else {
            ListType<?, V> list = listSupplier.get();
            if (list != null) cachedList = list;
            else if (hasLogger()) {
                getLogger().atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("listSupplier returns null for ConfigConditionVal '{0}'",
                                owner.getName());
            }
        }
    }

    @Override public ILocaleType getConstraintsLocaleType() { return ConfigLocaleType.VARIANTS; }
}