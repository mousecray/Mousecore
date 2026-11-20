/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.other.ConfigValColor;
import ru.mousecray.mousecore.api.customtype.values.other.ColorType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.other.ConfigValColorImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigColorBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValColor, ColorType, ConfigColorBuilder<BUILDER>> {

    private ConfigColorBuilder(BUILDER parBuilder, Consumer<ConfigValColor> onComplete) {
        super(parBuilder, ColorType.WHITE, ColorType.TRANSPARENT, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigColorBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValColor> onComplete) {
        return new ConfigColorBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildColor() {
        onComplete.accept(new ConfigValColorImpl(defaultValue, disabledValue, null, genConstraints(), genConfigure()));
        return parBuilder;
    }
}