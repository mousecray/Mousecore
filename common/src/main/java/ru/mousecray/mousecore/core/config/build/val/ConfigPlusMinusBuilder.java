/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigValPlusMinus;
import ru.mousecray.mousecore.api.customtype.op.LogicalOperator;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.val.ConfigValPlusMinusImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigPlusMinusBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValPlusMinus, PlusMinusType, ConfigPlusMinusBuilder<BUILDER>> {
    ConfigPlusMinusBuilder(BUILDER parBuilder, Consumer<ConfigValPlusMinus> onComplete) {
        super(parBuilder, PlusMinusType.TRUE, PlusMinusType.FALSE, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigPlusMinusBuilder<BUILDER> create(
            BUILDER parBuilder, Consumer<ConfigValPlusMinus> onComplete
    ) {
        return new ConfigPlusMinusBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    @Override
    public ConfigPlusMinusBuilder<BUILDER> setDefaultValue(PlusMinusType defaultValue) {
        super.setDefaultValue(defaultValue);
        if (defaultValue.getLogicPipeline().isSupport(LogicalOperator.Unary.NOT)) {
            disabledValue = defaultValue.getLogicPipeline().not().getCastPipeline().asValue(PlusMinusType.class);
        }
        return this;
    }

    public ConfigPlusMinusBuilder<BUILDER> disableDictionaryThreshold() {
        disableDictionaryThreshold = true;
        return this;
    }

    public BUILDER buildPlusMinus() {
        onComplete.accept(
                new ConfigValPlusMinusImpl(
                        defaultValue, disabledValue,
                        genConstraints(), genDictionaryThreshold(), genConfigure()
                )
        );
        return parBuilder;
    }
}