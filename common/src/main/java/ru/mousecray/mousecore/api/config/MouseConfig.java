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
import ru.mousecray.mousecore.api.config.val.container.*;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListLogical;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListNumber;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListOther;
import ru.mousecray.mousecore.api.config.val.container.list.ConfigValSimpleListVector3;
import ru.mousecray.mousecore.api.config.val.logic.ConfigValCondition;
import ru.mousecray.mousecore.api.config.val.logic.ConfigValPlusMinus;
import ru.mousecray.mousecore.api.config.val.minecraft.*;
import ru.mousecray.mousecore.api.config.val.number.*;
import ru.mousecray.mousecore.api.config.val.other.*;
import ru.mousecray.mousecore.api.config.val.vec3.ConfigValBlockPos;
import ru.mousecray.mousecore.api.config.val.vec3.ConfigValVector3d;
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

    @Nullable ConfigValRange getRangeVal(@Nullable String fullName);
    @Nullable ConfigValTime getTimeVal(@Nullable String fullName);

    @Nullable ConfigValVector3d getVector3dVal(@Nullable String fullName);
    @Nullable ConfigValBlockPos getBlockPosVal(@Nullable String fullName);

    @Nullable <E extends Enum<E>> ConfigValEnum<E> getEnumVal(@Nullable String fullName);
    @Nullable ConfigValColor getColorVal(@Nullable String fullName);
    @Nullable ConfigValRegex getRegexVal(@Nullable String fullName);
    @Nullable ConfigValExpression getExpressionVal(@Nullable String fullName);

    @Nullable ConfigValBlock getBlockVal(@Nullable String fullName);
    @Nullable ConfigValItem getItemVal(@Nullable String fullName);
    @Nullable ConfigValItemStack getItemStackVal(@Nullable String fullName);
    @Nullable ConfigValTag getTagVal(@Nullable String fullName);
    @Nullable ConfigValEntity getEntityVal(@Nullable String fullName);
    @Nullable ConfigValDimension getDimensionVal(@Nullable String fullName);
    @Nullable ConfigValBiome getBiomeVal(@Nullable String fullName);
    @Nullable ConfigValEffect getEffectVal(@Nullable String fullName);
    @Nullable ConfigValEnchantment getEnchantmentVal(@Nullable String fullName);
    @Nullable ConfigValParticle getParticleVal(@Nullable String fullName);
    @Nullable ConfigValSound getSoundVal(@Nullable String fullName);

    @Nullable ConfigValNBT getNBTVal(@Nullable String fullName);
    @Nullable ConfigValTuple getTupleVal(@Nullable String fullName);
    @Nullable <K extends CustomType<?>, V extends CustomType<?>> ConfigValMap<K, V> getMapVal(@Nullable String fullName);
    @Nullable ConfigValTable getTableVal(@Nullable String fullName);
    @Nullable ConfigValMatrix getMatrixVal(@Nullable String fullName);

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
