/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.core.config.val;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.ConfigValExpression;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.other.ExpressionType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.val.base.ConfigValOtherImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValExpressionImpl extends ConfigValOtherImpl<ExpressionType> implements ConfigValExpression {
    private final Set<String> allowedVariables;

    @SafeVarargs
    public ConfigValExpressionImpl(
            @Nullable Set<String> allowedVariables,
            ExpressionType defaultValue, ExpressionType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<ExpressionType>... configureValues
    ) {
        super(defaultValue, disabledValue, specificDataType, constraints, configureValues);
        this.allowedVariables = allowedVariables != null ? Collections.unmodifiableSet(allowedVariables) : Collections.emptySet();
    }

    @Override
    protected VariableValue<ExpressionType> parseValue(@Nullable String value) {
        try {
            ExpressionType parsed = CustomType.parse(ExpressionType.class, value);
            if (parsed != null) {
                try {
                    parsed.validateVariables(allowedVariables);
                    return VariableValue.create(parsed);
                } catch (ValueFormatException e) {
                    if (hasLogger()) {
                        getLogger().atWarn()
                                .withPrefix(LogPrefixes.CONFIG)
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("Invalid variables in Expression for '{0}': {1}",
                                        owner != null ? owner.getFullInternalName() : "unknown", e.getMessage());
                    }
                }
            }
            return VariableValue.create();
        } catch (ValueFormatException ignore) { return VariableValue.create(); }
    }

    @Override public String getExpressionString()                                                            { return getValue().asString(); }
    @Override public Set<String> getVariables()                                                              { return getValue().asVariables(); }
    @Override public boolean hasVariables()                                                                  { return getValue().hasVariables(); }

    @Override public double evaluateDouble(@Nullable Map<String, Double> varValues)                          { return getValue().evaluateDouble(varValues); }
    @Override public double evaluateDouble()                                                                 { return getValue().evaluateDouble(); }

    @Override public DecimalType evaluate(@Nullable Map<String, ? extends NumberType<?>> varValues)          { return getValue().evaluate(varValues); }
    @Override public DecimalType evaluate()                                                                  { return getValue().evaluate(); }

    @Override public IntegralType evaluateIntegral(@Nullable Map<String, ? extends NumberType<?>> varValues) { return getValue().evaluateIntegral(varValues); }

    @Override public void setExpression(String expression)                                                   { setValue(ExpressionType.create(expression)); }
}