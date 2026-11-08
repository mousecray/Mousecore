/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class StringType extends OtherType<String> {
    public static final StringType NULL = new StringType("");

    static {
        storage.put(StringType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            return str == null || str.isEmpty() ? NULL : create(str);
        });
    }

    private StringType(String value)              { super(Objects.requireNonNull(value)); }
    public static StringType create(String value) { return new StringType(value); }

    public String asString()                      { return value; }

    @SuppressWarnings("unchecked") @Override
    public StringType createType(Object value) {
        return create(value.toString());
    }
}