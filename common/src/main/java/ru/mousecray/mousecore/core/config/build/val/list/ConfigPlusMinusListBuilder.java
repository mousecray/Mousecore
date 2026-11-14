/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValPlusMinusList;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValPlusMinusListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigPlusMinusListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListBuilder<BUILDER, ConfigValPlusMinusList, PlusMinusType, ConfigPlusMinusListBuilder<BUILDER>> {


    private ConfigPlusMinusListBuilder(BUILDER parBuilder, Consumer<ConfigValPlusMinusList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigPlusMinusListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValPlusMinusList> onComplete) {
        return new ConfigPlusMinusListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildPlusMinusList() {
        onComplete.accept(
                new ConfigValPlusMinusListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}