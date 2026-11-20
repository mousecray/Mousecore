/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.other.RegexType;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public interface ConfigValRegex extends ConfigOtherVal<RegexType> {
    String getPatternString();
    Pattern getPattern();
    int getFlags();

    boolean matches(@Nullable String input);
    boolean find(@Nullable String input);

    void setPattern(String pattern);
    void setPattern(String pattern, int flags);
}