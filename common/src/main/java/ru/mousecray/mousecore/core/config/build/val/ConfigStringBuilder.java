/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValString;
import ru.mousecray.mousecore.api.customtype.values.StringType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValStringImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigStringBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValString, StringType, ConfigStringBuilder<BUILDER>> {
    private String specificDataType;

    private ConfigStringBuilder(BUILDER parBuilder, Consumer<ConfigValString> onComplete) {
        super(parBuilder, StringType.NULL, StringType.NULL, onComplete);
    }
    public static <BUILDER extends AbstractConfigChildBuilder> ConfigStringBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValString> onComplete
    ) {
        return new ConfigStringBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public ConfigStringBuilder<BUILDER> setStringFormatHint(String formatHint) {
        Objects.requireNonNull(formatHint);
        specificDataType = formatHint;
        return this;
    }

    public BUILDER buildString() {
        onComplete.accept(
                new ConfigValStringImpl(
                        defaultValue, disabledValue,
                        specificDataType,
                        genConstraints(),
                        genConfigure()
                )
        );
        return parBuilder;
    }
}