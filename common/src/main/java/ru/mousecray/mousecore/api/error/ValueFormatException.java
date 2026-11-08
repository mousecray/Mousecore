/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.error;

import java.util.Arrays;

public class ValueFormatException extends NumberFormatException {
    public ValueFormatException(String message)                  { super(message); }
    public ValueFormatException()                                { super(); }
    public ValueFormatException(String message, Throwable cause) { this(message + "\n" + Arrays.toString(cause.getStackTrace())); }
}