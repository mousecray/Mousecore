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
import ru.mousecray.mousecore.api.config.val.*;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
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

    @Nullable <TYPE extends CustomType<TYPE>> ConfigValCondition<TYPE> getConditionVal(@Nullable String fullName);
    @Nullable <
            LIST_VAL extends CustomType<?>,
            VAL extends CustomType<?>,
            TYPE extends ListType<LIST_VAL, VAL>
            >
    ConfigListVal<LIST_VAL, VAL, TYPE> getListVal(@Nullable String fullName);

    <T extends CustomType<T>, D extends ConfigParGroup<T>> D getGroup(@Nullable String fullName);

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
