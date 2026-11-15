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
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValIntegralListImpl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigIntegralListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, IntegralType, ConfigIntegralListBuilder<BUILDER>> {

    private ConfigIntegralListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) { super(parBuilder, onComplete); }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigIntegralListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigIntegralListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override public IValType getComponentValType()                 { return ValType.INTEGRAL; }
    @Override public Function<String, IntegralType> getValCreator() { return str -> CustomType.parse(IntegralType.class, str); }

    public BUILDER buildIntegralList() {
        onComplete.accept(
                new ConfigValIntegralListImpl(
                        conditional,
                        resolveValue(rawDefaultEntries, "defaultValue"),
                        resolveValue(rawDisabledEntries, "disabledValue"),
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}
