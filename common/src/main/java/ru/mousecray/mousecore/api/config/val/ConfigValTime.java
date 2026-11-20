/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;

public interface ConfigValTime extends ConfigNumberVal<TimeType> {
    long getTicks();
    double getSeconds();
    double getMinutes();
    double getHours();
    double getDays();

    void setTicks(long ticks);
    void setTime(double value, TimeType.Unit unit);
    void setTime(long value, TimeType.Unit unit);
}