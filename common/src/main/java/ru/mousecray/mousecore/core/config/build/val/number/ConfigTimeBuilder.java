/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.number.ConfigValTime;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigNumberValueBuilder;
import ru.mousecray.mousecore.core.config.val.number.ConfigValTimeImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigTimeBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigNumberValueBuilder<BUILDER, ConfigValTime, TimeType, ConfigTimeBuilder<BUILDER>> {

    private ConfigTimeBuilder(BUILDER parBuilder, Consumer<ConfigValTime> onComplete) {
        super(parBuilder, TimeType.ZERO, TimeType.ZERO, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigTimeBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValTime> onComplete) {
        return new ConfigTimeBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildTime() {
        onComplete.accept(
                new ConfigValTimeImpl(
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