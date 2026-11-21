/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.par.group;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.DisplayName;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.group.ConfigParGroupPhase;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.core.config.par.ConfigParDisablerImpl;
import ru.mousecray.mousecore.core.config.val.ConfigValImpl;
import ru.mousecray.mousecore.core.config.val.number.ConfigValRangeImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigParGroupPhaseImpl<VAL extends CustomType<?>>
        extends ConfigParGroupImpl<VAL> implements ConfigParGroupPhase<VAL> {

    public record PhaseSlot<V extends CustomType<?>>(
            DisplayName valueName,
            DisplayName rangeName,
            ConfigValImpl<V> valueVal,
            ConfigValRangeImpl rangeVal
    ) { }

    private final List<PhaseSlot<VAL>> phaseSlots;

    public ConfigParGroupPhaseImpl(
            DisplayName name,
            @Nullable ConfigParDisablerImpl disablePar,
            Pair<DisplayName, ? extends ConfigValImpl<VAL>> fallbackVal,
            @Nullable String comment,
            List<PhaseSlot<VAL>> slots
    ) {
        super(name, disablePar, fallbackVal, comment, buildPhaseMap(slots));
        phaseSlots = List.copyOf(slots);
    }

    private static <V extends CustomType<?>> Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> buildPhaseMap(List<PhaseSlot<V>> slots) {
        Map<DisplayName, ConfigValImpl<? extends CustomType<?>>> map = new LinkedHashMap<>();
        for (PhaseSlot<V> slot : slots) {
            map.put(slot.valueName(), slot.valueVal());
            map.put(slot.rangeName(), slot.rangeVal());
        }
        return map;
    }

    @Override
    public VAL select(double number) {
        if (isDisabled()) return getFallbackValue();
        for (PhaseSlot<VAL> slot : phaseSlots) {
            if (slot.rangeVal().contains(number)) {
                return slot.valueVal().getValue();
            }
        }
        return getFallbackValue();
    }

    @Override
    public VAL select(@Nullable NumberType<?> number) {
        return number != null ? select(number.asDouble()) : getFallbackValue();
    }

    @Override public VAL getMainValue()     { return getFallbackValue(); }
    @Override public VAL getFallbackValue() { return getConfigVal().getValue(); }
}