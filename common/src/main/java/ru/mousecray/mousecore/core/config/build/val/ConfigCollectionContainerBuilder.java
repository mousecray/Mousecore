package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigCollectionContainerBuilder<
        BUILDER extends AbstractConfigChildBuilder,
        LIST_COMPONENT_TYPE extends CustomType<?>,
        LIST_BUILDER extends AbstractConfigSimpleListBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER>
        > {
    private final LIST_BUILDER                                            builder;
    private final Consumer<List<ConditionalValType<LIST_COMPONENT_TYPE>>> onComplete;
    private final List<ConditionalValType<LIST_COMPONENT_TYPE>>           entries = new ArrayList<>();

    private ConfigCollectionContainerBuilder(LIST_BUILDER builder, Consumer<List<ConditionalValType<LIST_COMPONENT_TYPE>>> onComplete) {
        this.builder = Objects.requireNonNull(builder);
        this.onComplete = Objects.requireNonNull(onComplete);
    }

    public static <
            BUILDER extends AbstractConfigChildBuilder,
            LIST_COMPONENT_TYPE extends CustomType<?>,
            LIST_BUILDER extends AbstractConfigSimpleListBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER>
            > ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER> create(
            LIST_BUILDER parBuilder, Consumer<List<ConditionalValType<LIST_COMPONENT_TYPE>>> onComplete
    ) {
        return new ConfigCollectionContainerBuilder<>(parBuilder, onComplete);
    }

    public ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER> putValue(LIST_COMPONENT_TYPE value) { return putValue(value, false); }

    public ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER> putValue(LIST_COMPONENT_TYPE value, boolean isAnti) {
        Objects.requireNonNull(value);
        entries.add(ConditionalValType.create(value, PlusMinusType.create(isAnti)));
        return this;
    }

    @SafeVarargs
    public final ConfigCollectionContainerBuilder<BUILDER, LIST_COMPONENT_TYPE, LIST_BUILDER> putValues(LIST_COMPONENT_TYPE... values) {
        for (LIST_COMPONENT_TYPE val : values) putValue(val);
        return this;
    }

    public LIST_BUILDER buildContainer() {
        onComplete.accept(entries);
        return builder;
    }
}