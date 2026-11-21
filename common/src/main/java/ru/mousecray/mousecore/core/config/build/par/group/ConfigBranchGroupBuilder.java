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
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupBranchImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigValSimpleListImpl;
import ru.mousecray.mousecore.core.config.val.logic.ConfigValConditionImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigBranchGroupBuilder<VAL extends CustomType<?>, TARGET extends CustomType<?>>
        extends AbstractConfigChildBuilder {

    private final ConfigParameterGroupBuilder<VAL> parentGroupBuilder;
    private final IValType                         targetValType;
    private final Class<TARGET>                    targetClass;
    private final Function<String, TARGET>         targetCreator;

    private           Pair<DisplayName, ConfigValImpl<VAL>>              matchPar;
    private           Pair<DisplayName, ConfigValImpl<VAL>>              elsePar;
    private           Pair<DisplayName, ConfigValSimpleListImpl<TARGET>> listPar;
    @Nullable private DisplayName                                        listName;
    @Nullable private DisplayName                                        conditionName;

    public ConfigBranchGroupBuilder(
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

    public ConfigBranchGroupBuilder<VAL, TARGET> setValues(
            String matchDisplayName, ConfigVal<VAL> matchVal,
            String elseDisplayName, ConfigVal<VAL> elseVal
    ) {
        matchPar = Pair.of(new DisplayName("internal$match", matchDisplayName), (ConfigValImpl<VAL>) Objects.requireNonNull(matchVal));
        elsePar = Pair.of(new DisplayName("internal$else", elseDisplayName), (ConfigValImpl<VAL>) Objects.requireNonNull(elseVal));
        return this;
    }

    public ConfigBranchGroupBuilder<VAL, TARGET> setValues(ConfigVal<VAL> matchVal, ConfigVal<VAL> elseVal) {
        return setValues(
                resolveLocale(ConfigLocaleType.GROUP_MATCH_VALUE), matchVal,
                resolveLocale(ConfigLocaleType.GROUP_ELSE_VALUE), elseVal
        );
    }

    public ConfigBranchGroupBuilder<VAL, TARGET> configureCondition(String displayName) {
        conditionName = new DisplayName("internal$condition", displayName);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigBranchGroupBuilder<VAL, TARGET>> configureTargetList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigOtherListBuilder.create(this, targetValType, (Function<String, O>) targetCreator, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<TARGET>) listVal);
        });
    }

    public <O extends OtherType<?>> ConfigOtherListBuilder<O, ConfigBranchGroupBuilder<VAL, TARGET>> configureTargetList() {
        return configureTargetList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    @SuppressWarnings("unchecked")
    public ConfigListBuilder<ConfigBranchGroupBuilder<VAL, TARGET>> configureCustomList(String displayName) {
        listName = new DisplayName("internal$list", displayName);
        return ConfigListBuilder.create(this, listVal -> {
            listPar = Pair.of(listName, (ConfigValSimpleListImpl<TARGET>) listVal);
        });
    }

    public ConfigListBuilder<ConfigBranchGroupBuilder<VAL, TARGET>> configureCustomList() {
        return configureCustomList(resolveLocale(ConfigLocaleType.GROUP_FILTER_LIST));
    }

    private String resolveLocale(ConfigLocaleType localeType) {
        return hasDictionary() ? getDictionary().getLocaleForLocale(localeType) : localeType.getDisplayName();
    }

    public ConfigParameterGroupBuilder<VAL> buildBranchGroup() {
        if (matchPar == null || elsePar == null) throw new IllegalStateException("Branch group requires both match and else values");

        String anyStr  = resolveLocale(ConfigLocaleType.ANY_VARIANT);
        String noneStr = resolveLocale(ConfigLocaleType.NONE_VARIANT);
        String listStr = resolveLocale(ConfigLocaleType.LIST_VARIANT);

        DisplayName condDisplayName = conditionName != null ? conditionName
                : new DisplayName("internal$condition", resolveLocale(ConfigLocaleType.GROUP_RULE));

        ConfigValConditionImpl<TARGET> condVal = new ConfigValConditionImpl<>(
                targetValType, null, true,
                ConditionType.ANY(anyStr),
                ConditionType.NONE(noneStr),
                listPar != null ? listPar.getRight()::getValue : null,
                anyStr, noneStr, listStr,
                hasDictionary() ? getDictionary().getDefThresholdForType(ValType.SIMPLE_CONDITION) : null,
                hasDictionary() ? getDictionary().getDefUseListForType(ValType.SIMPLE_CONDITION) : null,
                null
        );

        parentGroupBuilder.setGroup(new ConfigParGroupBranchImpl<>(
                parentGroupBuilder.name, parentGroupBuilder.getDisabler(), matchPar, elsePar, parentGroupBuilder.getComment(),
                Pair.of(condDisplayName, condVal), listPar
        ));
        return parentGroupBuilder;
    }
}