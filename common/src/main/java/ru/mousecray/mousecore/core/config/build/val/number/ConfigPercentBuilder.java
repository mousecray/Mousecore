/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValPercent;
import ru.mousecray.mousecore.api.customtype.values.PercentType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValPercentImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigPercentBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValPercent, PercentType, ConfigPercentBuilder<BUILDER>> {
    private ConfigPercentBuilder(BUILDER parBuilder, Consumer<ConfigValPercent> onComplete) {
        super(parBuilder, PercentType.NULL, PercentType.MIN, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigPercentBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValPercent> onComplete) {
        return new ConfigPercentBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildPercent() {
        onComplete.accept(
                new ConfigValPercentImpl(
                        defaultValue, disabledValue,
                        genRangeContainer(), enableThresholdConfiguration, enableStandardConfiguration,
                        genDictionaryThreshold(), genDictionaryStandard(),
                        actionIfNotEqualConstraint,
                        genConstraints(),
                        genConfigure()
                )
        );
        return parBuilder;
    }
}