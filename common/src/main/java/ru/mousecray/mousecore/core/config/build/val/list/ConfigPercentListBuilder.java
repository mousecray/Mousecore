/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValPercentList;
import ru.mousecray.mousecore.api.customtype.values.PercentType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValPercentListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigPercentListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, ConfigValPercentList, PercentType, ConfigPercentListBuilder<BUILDER>> {

    private ConfigPercentListBuilder(BUILDER parBuilder, Consumer<ConfigValPercentList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigPercentListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValPercentList> onComplete) {
        return new ConfigPercentListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildPercentList() {
        onComplete.accept(
                new ConfigValPercentListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}
