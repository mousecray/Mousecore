/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.build.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.number.RangeType;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;
import ru.mousecray.mousecore.core.config.par.group.ConfigParGroupPhaseImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.number.ConfigValRangeImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class ConfigPhaseGroupBuilder<VAL extends CustomType<?>> extends AbstractConfigChildBuilder {
    private final ConfigParameterGroupBuilder<VAL>             parentGroupBuilder;
    private       Pair<DisplayName, ConfigValImpl<VAL>>        fallbackPar;
    private final List<ConfigParGroupPhaseImpl.PhaseSlot<VAL>> slots        = new ArrayList<>();
    private       int                                          phaseCounter = 1;

    public ConfigPhaseGroupBuilder(ConfigParameterGroupBuilder<VAL> parentGroupBuilder) {
        super(parentGroupBuilder.configBuilder, parentGroupBuilder.path, parentGroupBuilder.name);
        this.parentGroupBuilder = Objects.requireNonNull(parentGroupBuilder);
    }

    public ConfigPhaseGroupBuilder<VAL> setFallback(String displayName, ConfigVal<VAL> fallbackVal) {
        fallbackPar = Pair.of(new DisplayName("internal$fallback", displayName), (ConfigValImpl<VAL>) Objects.requireNonNull(fallbackVal));
        return this;
    }

    public ConfigPhaseGroupBuilder<VAL> setFallback(ConfigVal<VAL> fallbackVal) {
        return setFallback(resolveLocale(ConfigLocaleType.GROUP_FALLBACK), fallbackVal);
    }

    public ConfigPhaseGroupBuilder<VAL> addPhase(
            String phaseName,
            ConfigVal<VAL> phaseValue,
            double minRange,
            double maxRange
    ) {
        Objects.requireNonNull(phaseName);
        Objects.requireNonNull(phaseValue);

        String valWord = resolveLocale(ConfigLocaleType.GROUP_VALUE);
        String rngWord = resolveLocale(ConfigLocaleType.GROUP_RANGE);

        DisplayName vName = new DisplayName("internal$phase_val$" + phaseCounter, phaseName + " (" + valWord + ")");
        DisplayName rName = new DisplayName("internal$phase_rng$" + phaseCounter, phaseName + " (" + rngWord + ")");
        phaseCounter++;

        RangeType rangeDef = RangeType.create(minRange, maxRange);
        ConfigValRangeImpl rangeVal = new ConfigValRangeImpl(
                rangeDef, rangeDef, null, null, false, null, null, null, null
        );

        slots.add(new ConfigParGroupPhaseImpl.PhaseSlot<>(
                vName, rName, (ConfigValImpl<VAL>) phaseValue, rangeVal
        ));
        return this;
    }

    private String resolveLocale(ConfigLocaleType localeType) {
        return hasDictionary() ? getDictionary().getLocaleForLocale(localeType) : localeType.getDisplayName();
    }

    public ConfigParameterGroupBuilder<VAL> buildPhaseGroup() {
        if (fallbackPar == null) throw new IllegalStateException("Phase group requires fallback value");
        if (slots.isEmpty()) throw new IllegalStateException("Phase group requires at least one phase slot");

        parentGroupBuilder.setGroup(new ConfigParGroupPhaseImpl<>(
                parentGroupBuilder.name, parentGroupBuilder.getDisabler(), fallbackPar, parentGroupBuilder.getComment(), slots
        ));
        return parentGroupBuilder;
    }
}