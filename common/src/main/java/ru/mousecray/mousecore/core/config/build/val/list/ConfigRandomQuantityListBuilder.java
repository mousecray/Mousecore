/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.list;

import ru.mousecray.mousecore.api.config.val.list.ConfigValRandomQuantityList;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigSimpleListNumberBuilder;
import ru.mousecray.mousecore.core.config.val.list.ConfigValRandomQuantityListImpl;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigRandomQuantityListBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigSimpleListNumberBuilder<BUILDER, ConfigValRandomQuantityList, RandomQuantityType, ConfigRandomQuantityListBuilder<BUILDER>> {

    private ConfigRandomQuantityListBuilder(BUILDER parBuilder, Consumer<ConfigValRandomQuantityList> onComplete) {
        super(parBuilder, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigRandomQuantityListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValRandomQuantityList> onComplete) {
        return new ConfigRandomQuantityListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildRandomQuantityList() {
        onComplete.accept(
                new ConfigValRandomQuantityListImpl(
                        conditional,
                        defaultValue, disabledValue,
                        genRangeContainer(), genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}