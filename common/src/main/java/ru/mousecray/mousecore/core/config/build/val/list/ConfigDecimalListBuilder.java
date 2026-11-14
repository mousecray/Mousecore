/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValDecimalList;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValDecimalListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigDecimalListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, ConfigValDecimalList, DecimalType, ConfigDecimalListBuilder<BUILDER>> {

    private ConfigDecimalListBuilder(BUILDER parBuilder, Consumer<ConfigValDecimalList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigDecimalListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValDecimalList> onComplete) {
        return new ConfigDecimalListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildDecimalList() {
        onComplete.accept(
                new ConfigValDecimalListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}