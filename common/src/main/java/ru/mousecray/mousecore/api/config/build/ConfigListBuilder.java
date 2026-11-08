/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.build;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.list.simple.ConfigSimpleListVal;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.ConditionalListType;
import ru.mousecray.mousecore.api.customtype.values.ConditionalValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigListBuilder<
        VAL extends Comparable<VAL>,
        BUILDER extends AbstractConfigParameterBuilder<BUILDER>
        > {
    private final          BUILDER                                              parBuilder;
    private                ConditionalListType<VAL>                             defaultValue;
    private                ConditionalListType<VAL>                             disabledValue;
    private                boolean                                              hasDisabledValue;
    @Nullable private      ArrayList<PredefinedValue<ConditionalListType<VAL>>> configureValues;
    @Nullable private      ArrayList<Constraint>                                constraints;
    @Nonnull private final Class<VAL>                                           listTypeClass;
    private final          ICustomType                                          listValType;
    private                Function<Object, VAL>                                valCreator;
    private final          List<Range<ConditionalValType<VAL>>>                 listValueRanges = new ArrayList<>();

    private ConfigListBuilder(BUILDER parBuilder, ICustomType listValType, Class<VAL> listTypeClass) {
        this.parBuilder = parBuilder;
        if (!listValType.canBeApplicableTo(Objects.requireNonNull(listTypeClass))) {
            throw new UnsupportedValException("CustomType is not support " + listTypeClass.getName());
        }
        this.listValType = Objects.requireNonNull(listValType);
        this.listTypeClass = Objects.requireNonNull(listTypeClass);
    }

    static <
            VAL extends Comparable<VAL>,
            BUILDER extends AbstractConfigParameterBuilder<BUILDER>
            > ConfigListBuilder<VAL, BUILDER>
    create(BUILDER parBuilder, ICustomType listValueType, Class<VAL> listTypeClass) {
        return new ConfigListBuilder<>(Objects.requireNonNull(parBuilder), listValueType, listTypeClass);
    }

    public ConfigListBuilder<VAL, BUILDER> setDefaultValue(ConditionalListType<VAL> list) {
        if (list.getValCreator() != valCreator) defaultValue = ConditionalListType.copyExcludeValCreator(list, valCreator);
        else defaultValue = list;
        if (!hasDisabledValue) disabledValue = list.invert();
        return this;
    }

    public ConfigListBuilder<VAL, BUILDER> setDisabledValue(ConditionalListType<VAL> list) {
        if (list.getValCreator() != valCreator) disabledValue = ConditionalListType.copyExcludeValCreator(list, valCreator);
        else disabledValue = list;
        hasDisabledValue = true;
        return this;
    }

    public ConfigListBuilder<VAL, BUILDER> addConfigureValue(String name, ConditionalListType<VAL> list) {
        Objects.requireNonNull(name);
        if (list.getValCreator() != valCreator) list = ConditionalListType.copyExcludeValCreator(list, valCreator);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValue<>(name, list));
        return this;
    }

    public ConfigListBuilder<VAL, BUILDER> addConstraint(Constraint value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return this;
    }

    public ConfigListBuilder<VAL, BUILDER> setValCreator(Function<Object, VAL> valCreator) {
        Objects.requireNonNull(valCreator);
        this.valCreator = valCreator;
        if (defaultValue.getValCreator() != valCreator)
            defaultValue = ConditionalListType.copyExcludeValCreator(defaultValue, valCreator);
        if (disabledValue.getValCreator() != valCreator)
            disabledValue = ConditionalListType.copyExcludeValCreator(disabledValue, valCreator);
        if (configureValues != null) {
            for (int i = 0; i < configureValues.size(); ++i) {
                PredefinedValue<ConditionalListType<VAL>> val = configureValues.get(i);
                if (val.getValue().getValCreator() != valCreator) {
                    val = new PredefinedValue<>(val.getDisplayName(), ConditionalListType.copyExcludeValCreator(val.getValue(), valCreator));
                    configureValues.set(i, val);
                }
            }
        }
        return this;
    }

    public ConfigListBuilder<VAL, BUILDER> addValidRange(ConditionalValType<VAL> min, ConditionalValType<VAL> max) {
        listValueRanges.add(Range.of(Objects.requireNonNull(min), Objects.requireNonNull(max)));
        return this;
    }

    public BUILDER buildList() {
        if (defaultValue == null) defaultValue = ConditionalListType.create(listTypeClass, valCreator);
        if (disabledValue == null) disabledValue = defaultValue.invert();
        parBuilder.setValue(new ConfigSimpleListVal<>(listValType, listTypeClass,
                new RangeContainer<>(listValueRanges),
                valCreator, defaultValue, disabledValue,
                MouseCollections.toArray(constraints, Constraint.class),
                MouseCollections.toArray(configureValues, PredefinedValue.class)));
        return parBuilder;
    }
}