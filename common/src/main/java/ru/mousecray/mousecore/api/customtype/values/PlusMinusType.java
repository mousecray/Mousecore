/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.customtype.LogicalType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;

public final class PlusMinusType extends LogicalType<String> {
    public static final PlusMinusType TRUE  = new PlusMinusType(Boolean.TRUE);
    public static final PlusMinusType FALSE = new PlusMinusType(Boolean.FALSE);
    public static final PlusMinusType NULL  = new PlusMinusType(Boolean.FALSE);

    static {
        storage.put(PlusMinusType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) return NULL;
            return switch (str.charAt(0)) {
                case '+' -> TRUE;
                case '-' -> FALSE;
                default -> NULL;
            };
        });
    }

    private PlusMinusType(boolean isYes)              { super("+", "-", isYes); }
    public static PlusMinusType create(boolean isYes) { return isYes ? TRUE : FALSE; }

    @Nonnull @Override public IValType getValType()   { return ValType.PLUS_MINUS; }

    @SuppressWarnings("unchecked") @Nonnull @Override
    public PlusMinusType createType(boolean isYes) {
        return create(isYes);
    }
}