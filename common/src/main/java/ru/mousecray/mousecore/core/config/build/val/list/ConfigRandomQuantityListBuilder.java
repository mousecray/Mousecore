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
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValRandomQuantityListImpl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigRandomQuantityListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, RandomQuantityType, ConfigRandomQuantityListBuilder<BUILDER>> {

    private ConfigRandomQuantityListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) { super(parBuilder, onComplete); }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigRandomQuantityListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigRandomQuantityListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override public IValType getComponentValType()                       { return ValType.RANDOM_QUANTITY; }
    @Override public Function<String, RandomQuantityType> getValCreator() { return str -> CustomType.parse(RandomQuantityType.class, str); }

    public BUILDER buildRandomQuantityList() {
        onComplete.accept(
                new ConfigValRandomQuantityListImpl(
                        conditional,
                        resolveValue(rawDefaultEntries, "defaultValue"),
                        resolveValue(rawDisabledEntries, "disabledValue"),
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}