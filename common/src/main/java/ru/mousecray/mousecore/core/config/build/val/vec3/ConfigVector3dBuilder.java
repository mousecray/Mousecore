/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.vec3;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.vec3.ConfigValVector3d;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.vec3.Vector3dType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigVector3Builder;
import ru.mousecray.mousecore.core.config.val.vec3.ConfigValVector3dImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigVector3dBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigVector3Builder<BUILDER, ConfigValVector3d, DecimalType, Vector3dType, ConfigVector3dBuilder<BUILDER>> {

    private ConfigVector3dBuilder(BUILDER parBuilder, Consumer<ConfigValVector3d> onComplete) {
        super(parBuilder, Vector3dType.ZERO, Vector3dType.ZERO, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigVector3dBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValVector3d> onComplete) {
        return new ConfigVector3dBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildVector3d() {
        onComplete.accept(
                new ConfigValVector3dImpl(
                        defaultValue, disabledValue,
                        genRangeContainerX(), genRangeContainerY(), genRangeContainerZ(),
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}