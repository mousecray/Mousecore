/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.utils;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ConfigDictionary {
    @Nonnull String getLocaleForType(IValType type);
    @Nullable String getRulesForType(IValType type);
    @Nullable ImmutableList<Pair<String, String>> getDefThresholdForType(IValType type);
    @Nullable ImmutableList<String> getDefUseListForType(IValType type);
    @Nullable ImmutableList<Triple<String, String, String>> getDefStandardForType(IValType type);
    @Nonnull String getLocaleForLocale(ILocaleType type);
}
