/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValStringList;
import ru.mousecray.mousecore.api.customtype.values.StringType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValStringListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigStringListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListBuilder<BUILDER, ConfigValStringList, StringType, ConfigStringListBuilder<BUILDER>> {

    private ConfigStringListBuilder(BUILDER parBuilder, Consumer<ConfigValStringList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigStringListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValStringList> onComplete) {
        return new ConfigStringListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildStringList() {
        onComplete.accept(
                new ConfigValStringListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}