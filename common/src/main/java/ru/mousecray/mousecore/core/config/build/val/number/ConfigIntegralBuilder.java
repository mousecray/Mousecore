/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValIntegral;
import ru.mousecray.mousecore.api.customtype.values.IntegralType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValIntegralImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigIntegralBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValIntegral, IntegralType, ConfigIntegralBuilder<BUILDER>> {
    private ConfigIntegralBuilder(BUILDER parBuilder, Consumer<ConfigValIntegral> onComplete) {
        super(parBuilder, IntegralType.NULL, IntegralType.MIN, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigIntegralBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValIntegral> onComplete) {
        return new ConfigIntegralBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildInteger() {
        onComplete.accept(
                new ConfigValIntegralImpl(
                        defaultValue, disabledValue,
                        genRangeContainer(), enableThresholdConfiguration, enableStandardConfiguration,
                        genDictionaryThreshold(), genDictionaryStandard(),
                        actionIfNotEqualConstraint,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}