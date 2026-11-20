/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.build.val.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.other.ConfigValExpression;
import ru.mousecray.mousecore.api.customtype.values.other.ExpressionType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.build.val.base.AbstractConfigValueBuilder;
import ru.mousecray.mousecore.core.config.val.other.ConfigValExpressionImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigExpressionBuilder<BUILDER extends AbstractConfigChildBuilder>
        extends AbstractConfigValueBuilder<BUILDER, ConfigValExpression, ExpressionType, ConfigExpressionBuilder<BUILDER>> {

    private final Set<String> allowedVariables = new HashSet<>();

    private ConfigExpressionBuilder(BUILDER parBuilder, Consumer<ConfigValExpression> onComplete) {
        super(parBuilder, ExpressionType.ZERO, ExpressionType.ZERO, onComplete);
    }

    public static <BUILDER extends AbstractConfigChildBuilder> ConfigExpressionBuilder<BUILDER> create(BUILDER parBuilder, Consumer<ConfigValExpression> onComplete) {
        return new ConfigExpressionBuilder<>(Objects.requireNonNull(parBuilder), onComplete);
    }

    public ConfigExpressionBuilder<BUILDER> addAllowedVariable(String varName) {
        allowedVariables.add(Objects.requireNonNull(varName));
        return this;
    }

    public BUILDER buildExpression() {
        onComplete.accept(
                new ConfigValExpressionImpl(
                        allowedVariables, defaultValue, disabledValue, null,
                        genConstraints(), genConfigure()
                )
        );
        return parBuilder;
    }
}