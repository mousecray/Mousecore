/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.vec3;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.vec3.ConfigValBlockPos;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.vec3.BlockPosType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigVector3Builder;
import ru.mousecray.mousecore.core.config.val.vec3.ConfigValBlockPosImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigBlockPosBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigVector3Builder<BUILDER, ConfigValBlockPos, IntegralType, BlockPosType, ConfigBlockPosBuilder<BUILDER>> {

    private ConfigBlockPosBuilder(BUILDER parBuilder, Consumer<ConfigValBlockPos> onComplete) {
        super(parBuilder, BlockPosType.ZERO, BlockPosType.ZERO, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigBlockPosBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValBlockPos> onComplete) {
        return new ConfigBlockPosBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public BUILDER buildBlockPos() {
        onComplete.accept(
                new ConfigValBlockPosImpl(
                        defaultValue, disabledValue,
                        genRangeContainerX(), genRangeContainerY(), genRangeContainerZ(),
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}