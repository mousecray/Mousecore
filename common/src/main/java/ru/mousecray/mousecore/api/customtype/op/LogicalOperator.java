/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.op;

public interface LogicalOperator {
    enum Binary implements IBinaryOperator {
        LESS, MORE, EQUAL, LESS_OR_EQUAL, MORE_OR_EQUAL, AND, OR
    }

    enum Unary implements IUnaryOperator {
        NOT
    }
}