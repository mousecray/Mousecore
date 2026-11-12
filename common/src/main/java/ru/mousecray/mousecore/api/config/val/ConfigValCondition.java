/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config.val;

import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.values.ConditionType;

import javax.annotation.Nullable;

public interface ConfigValCondition<V extends CustomType<?>> extends ConfigOtherVal<ConditionType<ListType<?, V>, V>> {
    @Nullable String getPathToParList();
    IValType getListComponentType();
    boolean test(V value);
}