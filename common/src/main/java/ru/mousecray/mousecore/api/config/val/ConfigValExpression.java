/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.other.ExpressionType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public interface ConfigValExpression extends ConfigOtherVal<ExpressionType> {
    String getExpressionString();
    Set<String> getVariables();
    boolean hasVariables();

    double evaluateDouble(@Nullable Map<String, Double> varValues);
    double evaluateDouble();

    DecimalType evaluate(@Nullable Map<String, ? extends NumberType<?>> varValues);
    DecimalType evaluate();

    IntegralType evaluateIntegral(@Nullable Map<String, ? extends NumberType<?>> varValues);

    void setExpression(String expression);
}