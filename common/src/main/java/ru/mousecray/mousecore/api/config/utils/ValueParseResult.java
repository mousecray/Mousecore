/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.utils;

public enum ValueParseResult {
    SUCCESS, ADAPTED, ERROR;

    public boolean isSuccess()     { return this == SUCCESS; }
    public boolean isAdapted()     { return this == ADAPTED; }
    public boolean isError()       { return this == ERROR; }
    public boolean isCanBePassed() { return isSuccess() || isAdapted(); }
}