/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.other.ColorType;

public interface ConfigValColor extends ConfigOtherVal<ColorType> {
    int getArgb();
    int getRgb();
    int getAlpha();
    int getRed();
    int getGreen();
    int getBlue();

    String getHex(boolean includeAlpha);
    String getHex();

    void setColor(int argb);
    void setColor(int r, int g, int b, int a);
    void setColor(int r, int g, int b);
}