/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

public interface ArithmeticOperator {
    enum Binary implements IBinaryOperator {
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO
    }

    enum Unary implements IUnaryOperator {
        INVERT, INCREMENT, DECREMENT
    }
}