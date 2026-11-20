/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.number.DecimalType;
import ru.mousecray.mousecore.api.customtype.values.number.RangeType;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import java.util.Random;

public interface ConfigValRange extends ConfigNumberVal<RangeType> {
    DecimalType getMin();
    DecimalType getMax();
    double getMinDouble();
    double getMaxDouble();
    long getMinLong();
    long getMaxLong();
    boolean isIncludeMin();
    boolean isIncludeMax();
    double getSpan();

    RangeContainer<DecimalType> getBoundaryRange();

    boolean contains(double val);
    boolean contains(Number val);
    boolean contains(NumberType<?> val);

    double rollDouble();
    double rollDouble(QuantityDistribution distribution);
    double rollDouble(Random random, QuantityDistribution distribution);

    long rollLong();
    long rollLong(QuantityDistribution distribution);
    long rollLong(Random random, QuantityDistribution distribution);

    void setRange(double min, double max);
    void setRange(double min, double max, boolean includeMin, boolean includeMax);
}