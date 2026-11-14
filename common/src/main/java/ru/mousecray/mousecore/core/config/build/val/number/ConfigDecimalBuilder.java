/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValDecimal;
import ru.mousecray.mousecore.api.customtype.values.DecimalType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigNumberValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValDecimalImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigDecimalBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValDecimal, DecimalType, ConfigDecimalBuilder<BUILDER>> {
    private ConfigDecimalBuilder(BUILDER parBuilder, Consumer<ConfigValDecimal> onComplete) {
        super(parBuilder, DecimalType.NULL, DecimalType.MIN, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigDecimalBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValDecimal> onComplete) {
        return new ConfigDecimalBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildDecimal() {
        onComplete.accept(
                new ConfigValDecimalImpl(
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