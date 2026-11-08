/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

public interface BitwiseOperator {
    enum Binary implements IBinaryOperator {
        AND, OR, XOR
    }

    enum Unary implements IUnaryOperator {
        NOT
    }

    enum Shift implements IShiftOperator {
        LEFT_SHIFT, RIGHT_SHIFT, U_RIGHT_SHIFT
    }
}