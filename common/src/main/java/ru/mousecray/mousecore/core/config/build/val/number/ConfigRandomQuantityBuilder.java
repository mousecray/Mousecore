/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.PercentType;
import ru.mousecray.mousecore.api.customtype.values.RandomQuantityType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigNumberValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValRandomQuantityImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigRandomQuantityBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValRandomQuantity, RandomQuantityType, ConfigRandomQuantityBuilder<BUILDER>> {
    private ConfigRandomQuantityBuilder(BUILDER parBuilder, Consumer<ConfigValRandomQuantity> onComplete) {
        super(parBuilder, RandomQuantityType.NULL, RandomQuantityType.MIN, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigRandomQuantityBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValRandomQuantity> onComplete
    ) {
        return new ConfigRandomQuantityBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildRandomQuantity() {
        onComplete.accept(
                new ConfigValRandomQuantityImpl(
                        defaultValue, disabledValue,
                        genRangeContainer(
                                RandomQuantityType.MIN,
                                RandomQuantityType.create(PercentType.MAX, IntegralType.create(1000), IntegralType.create(1000))
                        ),
                        enableThresholdConfiguration, enableStandardConfiguration,
                        genDictionaryThreshold(), genDictionaryStandard(),
                        actionIfNotEqualConstraint, genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}