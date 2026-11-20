/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.config;

import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.config.io.IOFuture;
import ru.mousecray.mousecore.api.config.par.ConfigParGroup;
import ru.mousecray.mousecore.api.config.sect.ConfigSect;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.base.ConfigValSimpleList;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListLogical;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListNumber;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListOther;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListVector3;
import ru.mousecray.mousecore.api.config.val.logic.ConfigValCondition;
import ru.mousecray.mousecore.api.config.val.logic.ConfigValPlusMinus;
import ru.mousecray.mousecore.api.config.val.number.ConfigValDecimal;
import ru.mousecray.mousecore.api.config.val.number.ConfigValIntegral;
import ru.mousecray.mousecore.api.config.val.number.ConfigValPercent;
import ru.mousecray.mousecore.api.config.val.number.ConfigValRandomQuantity;
import ru.mousecray.mousecore.api.config.val.other.ConfigValString;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.log.MouseLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalTime;
import java.util.List;

public interface MouseConfig {
    @Nullable <T extends CustomType<?>, D extends ConfigVal<T>> D getValue(@Nullable String fullName);
    @Nullable ConfigValPlusMinus getBooleanVal(@Nullable String fullName);
    @Nullable ConfigValDecimal getDecimalVal(@Nullable String fullName);
    @Nullable ConfigValIntegral getIntegerVal(@Nullable String fullName);
    @Nullable ConfigValString getStringVal(@Nullable String fullName);
    @Nullable ConfigValRandomQuantity getRandomQuantityVal(@Nullable String fullName);
    @Nullable ConfigValPercent getPercentVal(@Nullable String fullName);

    @Nullable <TYPE extends CustomType<?>> ConfigValCondition<TYPE> getConditionVal(@Nullable String fullName);

    @Nullable <T extends CustomType<?>> ConfigValSimpleList<T> getListVal(@Nullable String fullName);
    @Nullable <T extends NumberType<?>> ConfigValSimpleListNumber<T> getNumberListVal(@Nullable String fullName);
    @Nullable <T extends LogicalType<?>> ConfigValSimpleListLogical<T> getLogicalListVal(@Nullable String fullName);
    @Nullable <T extends OtherType<?>> ConfigValSimpleListOther<T> getOtherListVal(@Nullable String fullName);
    @Nullable <N extends NumberType<?>, T extends Vector3Type<N, T>> ConfigValSimpleListVector3<N, T> getVectorListVal(@Nullable String fullName);

    <T extends CustomType<?>, D extends ConfigParGroup<T>> D getGroup(@Nullable String fullName);

    ConfigSect getSection(DisplayName name);
    @Nullable ConfigSectBase getSectionBase(@Nullable String fullName);
    boolean hasSection(DisplayName name);

    List<ConfigSectBase> getByDeep(int deep);
    List<ConfigSectBase> getChildrenRecursively();

    boolean isBuilt();
    boolean isLoaded();
    boolean isEnableAutoSync();
    boolean isEnableAutoSave();
    boolean hasUnsavedChanges();

    int getAutoSyncSecInterval();
    int getAutoSaveSecInterval();
    LocalTime getLastSyncTime();
    LocalTime getLastSaveTime();

    @Nonnull ConfigDictionary getDictionary();
    String getName();
    File getPath();
    @Nullable MouseLogger getLogger();

    void reset();
    IOFuture load();
    IOFuture save(boolean markUnloaded);
}
