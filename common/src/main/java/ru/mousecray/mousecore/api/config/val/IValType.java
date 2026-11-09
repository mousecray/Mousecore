/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.customtype.CustomValType;

import javax.annotation.Nonnull;

public interface IValType {
    @Nonnull String getDisplayName();
    @Nonnull CustomValType getValType();
}
