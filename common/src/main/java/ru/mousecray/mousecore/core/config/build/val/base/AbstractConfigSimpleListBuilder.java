package ru.mousecray.mousecore.core.config.build.val.base;

import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.ConfigCollectionContainerBuilder;
import ru.mousecray.mousecore.core.config.utils.PredefinedValueImpl;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractConfigSimpleListBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        LIST_COMPONENT_TYPE extends CustomType<?>,
        SELF extends AbstractConfigSimpleListBuilder<BUILDER, LIST_COMPONENT_TYPE, SELF>
        > {
    protected final BUILDER                          parBuilder;
    protected final Consumer<ConfigValSimpleList<?>> onComplete;

    @Nullable protected List<ConditionalValType<LIST_COMPONENT_TYPE>> rawDefaultEntries;
    @Nullable protected List<ConditionalValType<LIST_COMPONENT_TYPE>> rawDisabledEntries;

    @Nullable protected ArrayList<Constraint<? extends CustomType<?>>>                  constraints;
    @Nullable protected ArrayList<PredefinedValue<SimpleListType<LIST_COMPONENT_TYPE>>> configureValues;
    protected           boolean                                                         conditional;
    protected           boolean                                                         unique;

    protected AbstractConfigSimpleListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        this.parBuilder = parBuilder;
        this.onComplete = onComplete;
    }

    public abstract IValType getComponentValType();
    @Nullable public Function<String, LIST_COMPONENT_TYPE> getValCreator() { return null; }

    public boolean isConditional()                                         { return conditional; }

    public ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, SELF> createDefaultValues() {
        return ConfigCollectionContainerBuilder.create(self(), entries -> rawDefaultEntries = entries);
    }

    public ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, SELF> createDisabledValues() {
        return ConfigCollectionContainerBuilder.create(self(), entries -> rawDisabledEntries = entries);
    }

    public SELF addConfigureValue(String name, SimpleListType<LIST_COMPONENT_TYPE> value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (configureValues == null) configureValues = new ArrayList<>();
        configureValues.add(new PredefinedValueImpl<>(name, value));
        return self();
    }

    public SELF addConstraint(Constraint<?> value) {
        Objects.requireNonNull(value);
        if (constraints == null) constraints = new ArrayList<>();
        constraints.add(value);
        return self();
    }

    public SELF setConditional() {
        conditional = true;
        return self();
    }

    public SELF setUnique() {
        unique = true;
        return self();
    }

    @Nullable
    protected SimpleListType<LIST_COMPONENT_TYPE> resolveValue(@Nullable List<ConditionalValType<LIST_COMPONENT_TYPE>> entries, String valueName) {
        if (entries == null) return null;
        validateEntries(entries, valueName);
        return SimpleListType.createWithEntries(getComponentValType(), conditional, unique, getValCreator(), entries);
    }

    protected void validateEntries(List<ConditionalValType<LIST_COMPONENT_TYPE>> entries, String valueName) { }
    @Nullable
    protected Constraint<? extends CustomType<?>>[] genConstraints() { return MouseCollections.toArray(constraints, Constraint.class); }

    @Nullable
    protected PredefinedValue<SimpleListType<LIST_COMPONENT_TYPE>>[] genConfigure() { return MouseCollections.toArray(configureValues, PredefinedValue.class); }

    @SuppressWarnings("unchecked") protected SELF self() { return (SELF) this; }
}