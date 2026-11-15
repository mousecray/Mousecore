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
import ru.mousecray.mousecore.api.customtype.values.PercentType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValPercentListImpl;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigPercentListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, PercentType, ConfigPercentListBuilder<BUILDER>> {

    private ConfigPercentListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) { super(parBuilder, onComplete); }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigPercentListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigPercentListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override public IValType getComponentValType()                { return ValType.PERCENT; }
    @Override public Function<String, PercentType> getValCreator() { return str -> CustomType.parse(PercentType.class, str); }

    public BUILDER buildPercentList() {
        onComplete.accept(
                new ConfigValPercentListImpl(
                        conditional,
                        resolveValue(rawDefaultEntries, "defaultValue"),
                        resolveValue(rawDisabledEntries, "disabledValue"),
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}
