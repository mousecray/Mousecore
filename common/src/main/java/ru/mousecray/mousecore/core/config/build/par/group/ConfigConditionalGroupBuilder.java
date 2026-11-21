/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigListBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.ConfigOtherListBuilder;
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupConditionalImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValSimpleListImpl;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValConditionImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigConditionalGroupBuilder<VAL extends CustomType<?>, TARGET extends CustomType<?>>
        extends AbstractConfigChildBuilder {

    private final ConfigParameterGroupBuilder<VAL> parentGroupBuilder;
    private final IValType                         targetValType;
    private final Class<TARGET>                    targetClass;
    private final Function<String, TARGET>         targetCreator;

    private           Pair<DisplayName, ConfigValImpl<VAL>>              mainPar;
    private           VAL                                                fallbackValue;
    private           Pair<DisplayName, ConfigValSimpleListImpl<TARGET>> listPar;
    @Nullable private DisplayName                                        listName;
    @Nullable private DisplayName                                        conditionName;

    public ConfigConditionalGroupBuilder(
            ConfigParameterGroupBuilder<VAL> parentGroupBuilder,
            IValType targetValType,
            Class<TARGET> targetClass,
            @Nullable Function<String, TARGET> targetCreator
    ) {
        super(parentGroupBuilder.configBuilder, parentGroupBuilder.path, parentGroupBuilder.name);
        this.parentGroupBuilder = Objects.requireNonNull(parentGroupBuilder);
        this.targetValType = Objects.requireNonNull(targetValType);
        this.targetClass = Objects.requireNonNull(targetClass);
        this.targetCreator = targetCreator != null ? targetCreator : str -> CustomType.parse(targetClass, str);
    }

    public Class<TARGET> getTargetClass()              { return targetClass; }
    public Function<String, TARGET> getTargetCreator() { return targetCreator; }

    public ConfigConditionalGroupBuilder<VAL, TARGET> setMainValue(String displayName, ConfigVal<VAL> value, VAL fallback) {
        mainPar = Pair.of(new DisplayName("internal$main", displayName), (ConfigValImpl<VAL>) Objects.requireNonNull(value));
        fallbackValue = Objects.requireNonNull(fallback);
        return this;
    }

    public ConfigConditionalGroupBuilder<VAL, TARGET> setMainValue(ConfigVal<VAL> value, VAL fallback) {
        return setMainValue(resolveLocale(ConfigLocaleType.GROUP_VALUE), value, fallback);
    }

    public ConfigConditionalGroupBuilder<VAL, TARGET> configureCondition(String displayName) {
        conditionName = new DisplayName("internal$condition", displayName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigConditionalGroupBuilder<VAL, TARGET>> configureTargetList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigOtherListBuilder.create(this, targetValType, (Function<String, O>) targetCreator, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<TARGET>) listVal);
        });
    }

    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigConditionalGroupBuilder<VAL, TARGET>> configureTargetList() {
        return configureTargetList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    @SuppressWarnings("unchecked")
    public ConfigListBuilder<ConfigConditionalGroupBuilder<VAL, TARGET>> configureCustomList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigListBuilder.create(this, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<TARGET>) listVal);
        });
    }

    public ConfigListBuilder<ConfigConditionalGroupBuilder<VAL, TARGET>> configureCustomList() {
        return configureCustomList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    private String resolveLocale(ConfigLocaleType localeType) {
        return hasDictionary() ? getDictionary().getLocaleForLocale(localeType) : localeType.getDisplayName();
    }

    public ConfigParameterGroupBuilder<VAL> buildConditionGroup() {
        if (mainPar == null) throw new IllegalStateException("Conditional group requires main value");

        String anyStr  = resolveLocale(ConfigLocaleType.ANY_VARIANT);
        String noneStr = resolveLocale(ConfigLocaleType.NONE_VARIANT);
        String listStr = resolveLocale(ConfigLocaleType.LIST_VARIANT);

        DisplayName condDisplayName = conditionName != null ? conditionName
                : new DisplayName("internal$condition", resolveLocale(ConfigLocaleType.GROUP_RULE));

        ConfigValConditionImpl<TARGET> condVal = new ConfigValConditionImpl<>(
                targetValType, null, true,
                ConditionType.ANY(anyStr),
                (ConditionType) ConditionType.NONE(noneStr),
                listPar != null ? listPar.getRight()::getValue : null,
                anyStr, noneStr, listStr,
                hasDictionary() ? getDictionary().getDefThresholdForType(ValType.SIMPLE_CONDITION) : null,
                hasDictionary() ? getDictionary().getDefUseListForType(ValType.SIMPLE_CONDITION) : null,
                null
        );

        parentGroupBuilder.setGroup(new ConfigParGroupConditionalImpl<>(
                parentGroupBuilder.name, parentGroupBuilder.getDisabler(), mainPar, fallbackValue, parentGroupBuilder.getComment(),
                Pair.of(condDisplayName, condVal), listPar
        ));
        return parentGroupBuilder;
    }
}