/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

import ru.mousecray.mousecore.api.customtype.CustomType;

public interface CustomArithmetic<T extends CustomType<?>> {
    T invert();
    T increment();
    T decrement();

    T plus(CustomType<?> other);
    T minus(CustomType<?> other);
    T divide(CustomType<?> other);
    T multiply(CustomType<?> other);
    T modulo(CustomType<?> other);

    boolean isSupport(ArithmeticOperator.Unary op);
    boolean isSupport(ArithmeticOperator.Binary op, CustomType<?> other);
    boolean isSupport(ArithmeticOperator.Binary op, Class<? extends CustomType<?>> otherClass);
}