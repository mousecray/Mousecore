/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
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
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupTransformerImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValSimpleListImpl;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValConditionImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigTransformerGroupBuilder<T extends CustomType<?>, CONTEXT extends CustomType<?>>
        extends AbstractConfigChildBuilder {

    private final ConfigParameterGroupBuilder<T> parentGroupBuilder;
    private final IValType                       contextValType;
    private final Class<CONTEXT>                 contextClass;
    private final Function<String, CONTEXT>      contextCreator;

    private           Pair<DisplayName, ConfigValImpl<T>>                 replacementPar;
    private           Pair<DisplayName, ConfigValSimpleListImpl<CONTEXT>> listPar;
    @Nullable private DisplayName                                         listName;
    @Nullable private DisplayName                                         conditionName;

    public ConfigTransformerGroupBuilder(
            ConfigParameterGroupBuilder<T> parentGroupBuilder,
            IValType contextValType,
            Class<CONTEXT> contextClass,
            @Nullable Function<String, CONTEXT> contextCreator
    ) {
        super(parentGroupBuilder.configBuilder, parentGroupBuilder.path, parentGroupBuilder.name);
        this.parentGroupBuilder = Objects.requireNonNull(parentGroupBuilder);
        this.contextValType = Objects.requireNonNull(contextValType);
        this.contextClass = Objects.requireNonNull(contextClass);
        this.contextCreator = contextCreator != null ? contextCreator : str -> CustomType.parse(contextClass, str);
    }

    public Class<CONTEXT> getContextClass()              { return contextClass; }
    public Function<String, CONTEXT> getContextCreator() { return contextCreator; }

    public ConfigTransformerGroupBuilder<T, CONTEXT> setReplacement(String displayName, ConfigVal<T> replacementVal) {
        replacementPar = Pair.of(new DisplayName("internal$replacement", displayName), (ConfigValImpl<T>) Objects.requireNonNull(replacementVal));
        return this;
    }

    public ConfigTransformerGroupBuilder<T, CONTEXT> setReplacement(ConfigVal<T> replacementVal) {
        return setReplacement(resolveLocale(ConfigLocaleType.GROUP_REPLACEMENT), replacementVal);
    }

    public ConfigTransformerGroupBuilder<T, CONTEXT> configureCondition(String displayName) {
        conditionName = new DisplayName("internal$condition", displayName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigTransformerGroupBuilder<T, CONTEXT>> configureTargetList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigOtherListBuilder.create(this, contextValType, (Function<String, O>) contextCreator, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<CONTEXT>) listVal);
        });
    }

    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigTransformerGroupBuilder<T, CONTEXT>> configureTargetList() {
        return configureTargetList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    @SuppressWarnings("unchecked")
    public ConfigListBuilder<ConfigTransformerGroupBuilder<T, CONTEXT>> configureCustomList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigListBuilder.create(this, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<CONTEXT>) listVal);
        });
    }

    public ConfigListBuilder<ConfigTransformerGroupBuilder<T, CONTEXT>> configureCustomList() {
        return configureCustomList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    private String resolveLocale(ConfigLocaleType localeType) {
        return hasDictionary() ? getDictionary().getLocaleForLocale(localeType) : localeType.getDisplayName();
    }

    public ConfigParameterGroupBuilder<T> buildTransformerGroup() {
        if (replacementPar == null) throw new IllegalStateException("Transformer group requires replacement value");

        String anyStr  = resolveLocale(ConfigLocaleType.ANY_VARIANT);
        String noneStr = resolveLocale(ConfigLocaleType.NONE_VARIANT);
        String listStr = resolveLocale(ConfigLocaleType.LIST_VARIANT);

        DisplayName condDisplayName = conditionName != null ? conditionName
                : new DisplayName("internal$condition", resolveLocale(ConfigLocaleType.GROUP_RULE));

        ConfigValConditionImpl<CONTEXT> condVal = new ConfigValConditionImpl<>(
                contextValType, null, true,
                (ConditionType) ConditionType.ANY(anyStr),
                (ConditionType) ConditionType.NONE(noneStr),
                listPar != null ? listPar.getRight()::getValue : null,
                anyStr, noneStr, listStr,
                hasDictionary() ? getDictionary().getDefThresholdForType(ValType.SIMPLE_CONDITION) : null,
                hasDictionary() ? getDictionary().getDefUseListForType(ValType.SIMPLE_CONDITION) : null,
                null
        );

        parentGroupBuilder.setGroup(new ConfigParGroupTransformerImpl<>(
                parentGroupBuilder.name, parentGroupBuilder.getDisabler(), replacementPar, parentGroupBuilder.getComment(),
                Pair.of(condDisplayName, condVal), listPar
        ));
        return parentGroupBuilder;
    }
}