/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValIntegralList;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValIntegralListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigIntegralListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, ConfigValIntegralList, IntegralType, ConfigIntegralListBuilder<BUILDER>> {

    private ConfigIntegralListBuilder(BUILDER parBuilder, Consumer<ConfigValIntegralList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigIntegralListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValIntegralList> onComplete) {
        return new ConfigIntegralListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildIntegralList() {
        onComplete.accept(
                new ConfigValIntegralListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}
