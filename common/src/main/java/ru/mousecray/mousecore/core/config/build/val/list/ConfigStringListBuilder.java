/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.other.StringType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValStringListImpl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigStringListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListBuilder<BUILDER, StringType, ConfigStringListBuilder<BUILDER>> {

    private ConfigStringListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) { super(parBuilder, onComplete); }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigStringListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigStringListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override public IValType getComponentValType()               { return ValType.SIMPLE_STRING; }
    @Override public Function<String, StringType> getValCreator() { return str -> CustomType.parse(StringType.class, str); }

    public BUILDER buildStringList() {
        onComplete.accept(
                new ConfigValStringListImpl(
                        conditional,
                        resolveValue(rawDefaultEntries, "defaultValue"),
                        resolveValue(rawDisabledEntries, "disabledValue"),
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}