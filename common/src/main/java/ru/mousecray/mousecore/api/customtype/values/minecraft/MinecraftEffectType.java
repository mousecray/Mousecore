/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.number.TimeType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseEffectState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftEffectType extends OtherType<MouseEffectState> {
    public static final MinecraftEffectType SPEED             = constant(MobEffects.MOVEMENT_SPEED);
    public static final MinecraftEffectType MOVEMENT_SPEED    = constant(MobEffects.MOVEMENT_SPEED);
    public static final MinecraftEffectType MOVEMENT_SLOWDOWN = constant(MobEffects.MOVEMENT_SLOWDOWN);
    public static final MinecraftEffectType DIG_SPEED         = constant(MobEffects.DIG_SPEED);
    public static final MinecraftEffectType DIG_SLOWDOWN      = constant(MobEffects.DIG_SLOWDOWN);
    public static final MinecraftEffectType DAMAGE_BOOST      = constant(MobEffects.DAMAGE_BOOST);
    public static final MinecraftEffectType HEAL              = constant(MobEffects.HEAL);
    public static final MinecraftEffectType HARM              = constant(MobEffects.HARM);
    public static final MinecraftEffectType JUMP              = constant(MobEffects.JUMP);
    public static final MinecraftEffectType CONFUSION         = constant(MobEffects.CONFUSION);
    public static final MinecraftEffectType REGENERATION      = constant(MobEffects.REGENERATION);
    public static final MinecraftEffectType DAMAGE_RESISTANCE = constant(MobEffects.DAMAGE_RESISTANCE);
    public static final MinecraftEffectType FIRE_RESISTANCE   = constant(MobEffects.FIRE_RESISTANCE);
    public static final MinecraftEffectType WATER_BREATHING   = constant(MobEffects.WATER_BREATHING);
    public static final MinecraftEffectType INVISIBILITY      = constant(MobEffects.INVISIBILITY);
    public static final MinecraftEffectType BLINDNESS         = constant(MobEffects.BLINDNESS);
    public static final MinecraftEffectType NIGHT_VISION      = constant(MobEffects.NIGHT_VISION);
    public static final MinecraftEffectType HUNGER            = constant(MobEffects.HUNGER);
    public static final MinecraftEffectType WEAKNESS          = constant(MobEffects.WEAKNESS);
    public static final MinecraftEffectType POISON            = constant(MobEffects.POISON);
    public static final MinecraftEffectType WITHER            = constant(MobEffects.WITHER);
    public static final MinecraftEffectType HEALTH_BOOST      = constant(MobEffects.HEALTH_BOOST);
    public static final MinecraftEffectType ABSORPTION        = constant(MobEffects.ABSORPTION);
    public static final MinecraftEffectType SATURATION        = constant(MobEffects.SATURATION);
    public static final MinecraftEffectType GLOWING           = constant(MobEffects.GLOWING);
    public static final MinecraftEffectType LEVITATION        = constant(MobEffects.LEVITATION);
    public static final MinecraftEffectType LUCK              = constant(MobEffects.LUCK);
    public static final MinecraftEffectType UNLUCK            = constant(MobEffects.UNLUCK);
    public static final MinecraftEffectType SLOW_FALLING      = constant(MobEffects.SLOW_FALLING);
    public static final MinecraftEffectType CONDUIT_POWER     = constant(MobEffects.CONDUIT_POWER);
    public static final MinecraftEffectType DOLPHINS_GRACE    = constant(MobEffects.DOLPHINS_GRACE);
    public static final MinecraftEffectType BAD_OMEN          = constant(MobEffects.BAD_OMEN);

    private static MinecraftEffectType constant(MobEffect effect) { return new MinecraftEffectType(new MouseEffectState(effect)); }

    static { storage.put(MinecraftEffectType.class, MinecraftEffectType::parse); }

    private MinecraftEffectType(MouseEffectState state) { super(Objects.requireNonNull(state, "MouseEffectState cannot be null")); }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftEffectType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseEffectState state = MouseEffectState.create(prefix, name);
        if (state == null) throw new ValueFormatException("Unknown mob effect: " + prefix + ":" + name);
        return new MinecraftEffectType(state);
    }

    public static MinecraftEffectType create(String name)            { return create("minecraft", name); }
    public static MinecraftEffectType create(MouseEffectState state) { return new MinecraftEffectType(state); }

    @Override public IValType getValType()                           { return ValType.MINECRAFT_EFFECT; }
    public MouseEffectState asEffectState()                          { return value; }
    public String getPrefix()                                        { return value.getRegistryKey().getNamespace(); }
    public String getName()                                          { return value.getRegistryKey().getPath(); }

    public MobEffectInstance createInstance(TimeType duration, int amplifier) {
        return value.createInstance((int) duration.asTicks(), amplifier);
    }

    public MobEffectInstance createInstance(TimeType duration) {
        return value.createInstance((int) duration.asTicks());
    }

    public static MinecraftEffectType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftEffectType effectType) return effectType;
        else if (value instanceof MouseEffectState state) return create(state);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftEffectType from " + value);
    }

    public static MinecraftEffectType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty mob effect string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Mob effect name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftEffectType fromString(@Nullable String value)                         { return parse((String) null, value); }

    @Override @SuppressWarnings("unchecked") public MinecraftEffectType createType(Object value) { return parse(value); }
}