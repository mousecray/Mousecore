/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.collection.SimpleListType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;
import ru.mousecray.mousecore.core.config.val.base.list.ConfigValSimpleListLogicalImpl;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigLogicalListBuilder<T extends LogicalType<?>, BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListBuilder<BUILDER, T, ConfigLogicalListBuilder<T, BUILDER>> {

    private final IValType            valType;
    private final Function<String, T> valCreator;

    private ConfigLogicalListBuilder(BUILDER pb, IValType valType, Function<String, T> valCreator, Consumer<ConfigValSimpleList<?>> oc) {
        super(pb, oc);
        this.valType = valType;
        this.valCreator = valCreator;
    }

    public static <T extends LogicalType<?>, BUILDER extends AbstractConfigChildBuilder> ConfigLogicalListBuilder<T, BUILDER> create(
            BUILDER pb, IValType valType, Function<String, T> valCreator, Consumer<ConfigValSimpleList<?>> oc) {
        return new ConfigLogicalListBuilder<>(pb, valType, valCreator, oc);
    }

    @Override public IValType getComponentValType()      { return valType; }
    @Override public Function<String, T> getValCreator() { return valCreator; }

    public BUILDER buildLogicalList() {
        SimpleListType<T> def = resolveValue(rawDefaultEntries, "defaultValue");
        if (def == null) def = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        SimpleListType<T> dis = resolveValue(rawDisabledEntries, "disabledValue");
        if (dis == null) dis = SimpleListType.create(valType, conditional, unique, valCreator, Collections.emptyList());

        onComplete.accept(new ConfigValSimpleListLogicalImpl<>(
                conditional, unique, valCreator, def, dis,
                genConstraints(), genConfigure()
        ));
        return parBuilder;
    }
}