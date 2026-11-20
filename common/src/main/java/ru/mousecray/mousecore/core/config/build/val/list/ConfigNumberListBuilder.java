package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.container.list.ConfigValSimpleListNumberImpl;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigNumberListBuilder<T extends NumberType<?>, BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, T, ConfigNumberListBuilder<T, BUILDER>> {

    private final IValType            valType;
    private final Function<String, T> valCreator;

    private ConfigNumberListBuilder(BUILDER pb, IValType valType, Function<String, T> valCreator, Consumer<ConfigValSimpleList<?>> oc) {
        super(pb, oc);
        this.valType = valType;
        this.valCreator = valCreator;
    }

    public static <T extends NumberType<?>, BUILDER extends AbstractConfigChildBuilder> ConfigNumberListBuilder<T, BUILDER> create(
            BUILDER pb, IValType valType, Function<String, T> valCreator, Consumer<ConfigValSimpleList<?>> oc) {
        return new ConfigNumberListBuilder<>(pb, valType, valCreator, oc);
    }

    @Override public IValType getComponentValType()      { return valType; }
    @Override public Function<String, T> getValCreator() { return valCreator; }

    public BUILDER buildNumberList() {
        SimpleListType<T> def = resolveValue(rawDefaultEntries, "defaultValue");
        if (def == null) def = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        SimpleListType<T> dis = resolveValue(rawDisabledEntries, "disabledValue");
        if (dis == null) dis = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        RangeContainer<T> rc = genRangeContainer();
        if (rc == null) rc = new RangeContainer<>();

        onComplete.accept(new ConfigValSimpleListNumberImpl<>(
                conditional, unique, valCreator, def, dis,
                rc, genConstraints(), genConfigure()
        ));
        return parBuilder;
    }
}