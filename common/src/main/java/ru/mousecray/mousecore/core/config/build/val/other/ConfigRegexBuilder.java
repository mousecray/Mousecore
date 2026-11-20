/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValRegex;
import ru.mousecray.mousecore.api.customtype.values.other.RegexType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValRegexImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigRegexBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValRegex, RegexType, ConfigRegexBuilder<BUILDER>> {

    private ConfigRegexBuilder(BUILDER parBuilder, Consumer<ConfigValRegex> onComplete) {
        super(parBuilder, RegexType.MATCH_ALL, RegexType.MATCH_ALL, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigRegexBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValRegex> onComplete) {
        return new ConfigRegexBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildRegex() {
        onComplete.accept(new ConfigValRegexImpl(defaultValue, disabledValue, null, genConstraints(), genConfigure()));
        return parBuilder;
    }
}