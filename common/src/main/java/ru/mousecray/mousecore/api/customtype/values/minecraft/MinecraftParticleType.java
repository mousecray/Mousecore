/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftParticleType extends OtherType<MouseRegistryEntry<ParticleType<?>>> {
    public static final MinecraftParticleType AMBIENT_ENTITY_EFFECT    = constant(ParticleTypes.AMBIENT_ENTITY_EFFECT);
    public static final MinecraftParticleType ANGRY_VILLAGER           = constant(ParticleTypes.ANGRY_VILLAGER);
    public static final MinecraftParticleType BLOCK                    = constant(ParticleTypes.BLOCK);
    public static final MinecraftParticleType BLOCK_MARKER             = constant(ParticleTypes.BLOCK_MARKER);
    public static final MinecraftParticleType BUBBLE                   = constant(ParticleTypes.BUBBLE);
    public static final MinecraftParticleType CLOUD                    = constant(ParticleTypes.CLOUD);
    public static final MinecraftParticleType CRIT                     = constant(ParticleTypes.CRIT);
    public static final MinecraftParticleType DAMAGE_INDICATOR         = constant(ParticleTypes.DAMAGE_INDICATOR);
    public static final MinecraftParticleType DRAGON_BREATH            = constant(ParticleTypes.DRAGON_BREATH);
    public static final MinecraftParticleType DRIPPING_LAVA            = constant(ParticleTypes.DRIPPING_LAVA);
    public static final MinecraftParticleType FALLING_LAVA             = constant(ParticleTypes.FALLING_LAVA);
    public static final MinecraftParticleType LANDING_LAVA             = constant(ParticleTypes.LANDING_LAVA);
    public static final MinecraftParticleType DRIPPING_WATER           = constant(ParticleTypes.DRIPPING_WATER);
    public static final MinecraftParticleType FALLING_WATER            = constant(ParticleTypes.FALLING_WATER);
    public static final MinecraftParticleType DUST                     = constant(ParticleTypes.DUST);
    public static final MinecraftParticleType DUST_COLOR_TRANSITION    = constant(ParticleTypes.DUST_COLOR_TRANSITION);
    public static final MinecraftParticleType EFFECT                   = constant(ParticleTypes.EFFECT);
    public static final MinecraftParticleType ELDER_GUARDIAN           = constant(ParticleTypes.ELDER_GUARDIAN);
    public static final MinecraftParticleType ENCHANTED_HIT            = constant(ParticleTypes.ENCHANTED_HIT);
    public static final MinecraftParticleType ENCHANT                  = constant(ParticleTypes.ENCHANT);
    public static final MinecraftParticleType END_ROD                  = constant(ParticleTypes.END_ROD);
    public static final MinecraftParticleType ENTITY_EFFECT            = constant(ParticleTypes.ENTITY_EFFECT);
    public static final MinecraftParticleType EXPLOSION_EMITTER        = constant(ParticleTypes.EXPLOSION_EMITTER);
    public static final MinecraftParticleType EXPLOSION                = constant(ParticleTypes.EXPLOSION);
    public static final MinecraftParticleType SONIC_BOOM               = constant(ParticleTypes.SONIC_BOOM);
    public static final MinecraftParticleType FALLING_DUST             = constant(ParticleTypes.FALLING_DUST);
    public static final MinecraftParticleType FIREWORK                 = constant(ParticleTypes.FIREWORK);
    public static final MinecraftParticleType FISHING                  = constant(ParticleTypes.FISHING);
    public static final MinecraftParticleType FLAME                    = constant(ParticleTypes.FLAME);
    public static final MinecraftParticleType CHERRY_LEAVES            = constant(ParticleTypes.CHERRY_LEAVES);
    public static final MinecraftParticleType SCULK_SOUL               = constant(ParticleTypes.SCULK_SOUL);
    public static final MinecraftParticleType SCULK_CHARGE             = constant(ParticleTypes.SCULK_CHARGE);
    public static final MinecraftParticleType SCULK_CHARGE_POP         = constant(ParticleTypes.SCULK_CHARGE_POP);
    public static final MinecraftParticleType SOUL_FIRE_FLAME          = constant(ParticleTypes.SOUL_FIRE_FLAME);
    public static final MinecraftParticleType SOUL                     = constant(ParticleTypes.SOUL);
    public static final MinecraftParticleType FLASH                    = constant(ParticleTypes.FLASH);
    public static final MinecraftParticleType HAPPY_VILLAGER           = constant(ParticleTypes.HAPPY_VILLAGER);
    public static final MinecraftParticleType COMPOSTER                = constant(ParticleTypes.COMPOSTER);
    public static final MinecraftParticleType HEART                    = constant(ParticleTypes.HEART);
    public static final MinecraftParticleType INSTANT_EFFECT           = constant(ParticleTypes.INSTANT_EFFECT);
    public static final MinecraftParticleType ITEM                     = constant(ParticleTypes.ITEM);
    public static final MinecraftParticleType VIBRATION                = constant(ParticleTypes.VIBRATION);
    public static final MinecraftParticleType ITEM_SLIME               = constant(ParticleTypes.ITEM_SLIME);
    public static final MinecraftParticleType ITEM_SNOWBALL            = constant(ParticleTypes.ITEM_SNOWBALL);
    public static final MinecraftParticleType LARGE_SMOKE              = constant(ParticleTypes.LARGE_SMOKE);
    public static final MinecraftParticleType LAVA                     = constant(ParticleTypes.LAVA);
    public static final MinecraftParticleType MYCELIUM                 = constant(ParticleTypes.MYCELIUM);
    public static final MinecraftParticleType NOTE                     = constant(ParticleTypes.NOTE);
    public static final MinecraftParticleType POOF                     = constant(ParticleTypes.POOF);
    public static final MinecraftParticleType PORTAL                   = constant(ParticleTypes.PORTAL);
    public static final MinecraftParticleType RAIN                     = constant(ParticleTypes.RAIN);
    public static final MinecraftParticleType SMOKE                    = constant(ParticleTypes.SMOKE);
    public static final MinecraftParticleType SNEEZE                   = constant(ParticleTypes.SNEEZE);
    public static final MinecraftParticleType SPIT                     = constant(ParticleTypes.SPIT);
    public static final MinecraftParticleType SQUID_INK                = constant(ParticleTypes.SQUID_INK);
    public static final MinecraftParticleType SWEEP_ATTACK             = constant(ParticleTypes.SWEEP_ATTACK);
    public static final MinecraftParticleType TOTEM_OF_UNDYING         = constant(ParticleTypes.TOTEM_OF_UNDYING);
    public static final MinecraftParticleType UNDERWATER               = constant(ParticleTypes.UNDERWATER);
    public static final MinecraftParticleType SPLASH                   = constant(ParticleTypes.SPLASH);
    public static final MinecraftParticleType WITCH                    = constant(ParticleTypes.WITCH);
    public static final MinecraftParticleType BUBBLE_POP               = constant(ParticleTypes.BUBBLE_POP);
    public static final MinecraftParticleType CURRENT_DOWN             = constant(ParticleTypes.CURRENT_DOWN);
    public static final MinecraftParticleType BUBBLE_COLUMN_UP         = constant(ParticleTypes.BUBBLE_COLUMN_UP);
    public static final MinecraftParticleType NAUTILUS                 = constant(ParticleTypes.NAUTILUS);
    public static final MinecraftParticleType DOLPHIN                  = constant(ParticleTypes.DOLPHIN);
    public static final MinecraftParticleType CAMPFIRE_COSY_SMOKE      = constant(ParticleTypes.CAMPFIRE_COSY_SMOKE);
    public static final MinecraftParticleType CAMPFIRE_SIGNAL_SMOKE    = constant(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
    public static final MinecraftParticleType DRIPPING_HONEY           = constant(ParticleTypes.DRIPPING_HONEY);
    public static final MinecraftParticleType FALLING_HONEY            = constant(ParticleTypes.FALLING_HONEY);
    public static final MinecraftParticleType LANDING_HONEY            = constant(ParticleTypes.LANDING_HONEY);
    public static final MinecraftParticleType FALLING_NECTAR           = constant(ParticleTypes.FALLING_NECTAR);
    public static final MinecraftParticleType FALLING_SPORE_BLOSSOM    = constant(ParticleTypes.FALLING_SPORE_BLOSSOM);
    public static final MinecraftParticleType ASH                      = constant(ParticleTypes.ASH);
    public static final MinecraftParticleType CRIMSON_SPORE            = constant(ParticleTypes.CRIMSON_SPORE);
    public static final MinecraftParticleType WARPED_SPORE             = constant(ParticleTypes.WARPED_SPORE);
    public static final MinecraftParticleType SPORE_BLOSSOM_AIR        = constant(ParticleTypes.SPORE_BLOSSOM_AIR);
    public static final MinecraftParticleType DRIPPING_OBSIDIAN_TEAR   = constant(ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
    public static final MinecraftParticleType FALLING_OBSIDIAN_TEAR    = constant(ParticleTypes.FALLING_OBSIDIAN_TEAR);
    public static final MinecraftParticleType LANDING_OBSIDIAN_TEAR    = constant(ParticleTypes.LANDING_OBSIDIAN_TEAR);
    public static final MinecraftParticleType REVERSE_PORTAL           = constant(ParticleTypes.REVERSE_PORTAL);
    public static final MinecraftParticleType WHITE_ASH                = constant(ParticleTypes.WHITE_ASH);
    public static final MinecraftParticleType SMALL_FLAME              = constant(ParticleTypes.SMALL_FLAME);
    public static final MinecraftParticleType SNOWFLAKE                = constant(ParticleTypes.SNOWFLAKE);
    public static final MinecraftParticleType DRIPPING_DRIPSTONE_LAVA  = constant(ParticleTypes.DRIPPING_DRIPSTONE_LAVA);
    public static final MinecraftParticleType FALLING_DRIPSTONE_LAVA   = constant(ParticleTypes.FALLING_DRIPSTONE_LAVA);
    public static final MinecraftParticleType DRIPPING_DRIPSTONE_WATER = constant(ParticleTypes.DRIPPING_DRIPSTONE_WATER);
    public static final MinecraftParticleType FALLING_DRIPSTONE_WATER  = constant(ParticleTypes.FALLING_DRIPSTONE_WATER);
    public static final MinecraftParticleType GLOW_SQUID_INK           = constant(ParticleTypes.GLOW_SQUID_INK);
    public static final MinecraftParticleType GLOW                     = constant(ParticleTypes.GLOW);
    public static final MinecraftParticleType WAX_ON                   = constant(ParticleTypes.WAX_ON);
    public static final MinecraftParticleType WAX_OFF                  = constant(ParticleTypes.WAX_OFF);
    public static final MinecraftParticleType ELECTRIC_SPARK           = constant(ParticleTypes.ELECTRIC_SPARK);
    public static final MinecraftParticleType SCRAPE                   = constant(ParticleTypes.SCRAPE);
    public static final MinecraftParticleType SHRIEK                   = constant(ParticleTypes.SHRIEK);
    public static final MinecraftParticleType EGG_CRACK                = constant(ParticleTypes.EGG_CRACK);

    @SuppressWarnings("deprecation")
    private static MinecraftParticleType constant(ParticleType<?> type) {
        return new MinecraftParticleType(MouseRegistryEntry.create(BuiltInRegistries.PARTICLE_TYPE, type));
    }

    static { storage.put(MinecraftParticleType.class, MinecraftParticleType::parse); }

    private MinecraftParticleType(MouseRegistryEntry<ParticleType<?>> entry) { super(Objects.requireNonNull(entry, "MouseRegistryEntry cannot be null")); }

    @SuppressWarnings({ "DataFlowIssue", "deprecation" })
    public static MinecraftParticleType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseRegistryEntry<ParticleType<?>> entry = MouseRegistryEntry.create(BuiltInRegistries.PARTICLE_TYPE, prefix, name);
        if (entry == null) throw new ValueFormatException("Unknown particle: " + prefix + ":" + name);
        return new MinecraftParticleType(entry);
    }

    public static MinecraftParticleType create(String name)                               { return create("minecraft", name); }
    public static MinecraftParticleType create(MouseRegistryEntry<ParticleType<?>> entry) { return new MinecraftParticleType(entry); }

    @Override public IValType getValType()                                                { return ValType.MINECRAFT_PARTICLE; }
    public MouseRegistryEntry<ParticleType<?>> asRegistryEntry()                          { return value; }
    public String getPrefix()                                                             { return value.getNamespace(); }
    public String getName()                                                               { return value.getPath(); }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static MinecraftParticleType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftParticleType particleType) return particleType;
        else if (value instanceof MouseRegistryEntry<?> entry && entry.getRegistry() == BuiltInRegistries.PARTICLE_TYPE) {
            return create((MouseRegistryEntry<ParticleType<?>>) entry);
        } else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftParticleType from " + value);
    }

    public static MinecraftParticleType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty particle string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Particle name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftParticleType fromString(@Nullable String value)                         { return parse((String) null, value); }
    @Override @SuppressWarnings("unchecked") public MinecraftParticleType createType(Object value) { return parse(value); }
}