/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.config.val.base.list.ConfigValSimpleList;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.list.*;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigListBuilder<BUILDER extends AbstractConfigChildBuilder> {
    private final BUILDER                          parBuilder;
    private final Consumer<ConfigValSimpleList<?>> onComplete;

    private ConfigListBuilder(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        this.parBuilder = parBuilder;
        this.onComplete = onComplete;
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigListBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValSimpleList<?>> onComplete) {
        return new ConfigListBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public ConfigDecimalListBuilder<BUILDER> setDecimalType()               { return ConfigDecimalListBuilder.create(parBuilder, onComplete); }
    public ConfigIntegralListBuilder<BUILDER> setIntegralType()             { return ConfigIntegralListBuilder.create(parBuilder, onComplete); }
    public ConfigPercentListBuilder<BUILDER> setPercentType()               { return ConfigPercentListBuilder.create(parBuilder, onComplete); }
    public ConfigPlusMinusListBuilder<BUILDER> setPlusMinusType()           { return ConfigPlusMinusListBuilder.create(parBuilder, onComplete); }
    public ConfigRandomQuantityListBuilder<BUILDER> setRandomQuantityType() { return ConfigRandomQuantityListBuilder.create(parBuilder, onComplete); }
    public ConfigStringListBuilder<BUILDER> setStringType()                 { return ConfigStringListBuilder.create(parBuilder, onComplete); }
}