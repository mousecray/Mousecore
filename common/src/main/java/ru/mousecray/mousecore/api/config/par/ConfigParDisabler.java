/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.par;

import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.core.config.val.ConfigValDisablerImpl;

public interface ConfigParDisabler extends ConfigPar<PlusMinusType> {
    ConfigValDisablerImpl getValue();
}
