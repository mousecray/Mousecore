/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.base;

import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.ISupportRange;
import ru.mousecray.mousecore.api.customtype.NumberType;

public interface ConfigNumberVal<T extends NumberType<?>> extends ConfigVal<T>, ISupportRange<T> {
    double getDouble();
    float getFloat();
    long getLong();
    int getInt();
    short getShort();
    byte getByte();
    void setDouble(double value);
    void setFloat(float value);
    void setLong(long value);
    void setInt(int value);
    void setShort(short value);
    void setByte(byte value);
}
