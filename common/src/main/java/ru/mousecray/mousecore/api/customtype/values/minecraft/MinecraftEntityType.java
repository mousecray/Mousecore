/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.world.entity.EntityType;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseEntityState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftEntityType extends OtherType<MouseEntityState> {
    public static final MinecraftEntityType ALLAY                  = constant(EntityType.ALLAY);
    public static final MinecraftEntityType AREA_EFFECT_CLOUD      = constant(EntityType.AREA_EFFECT_CLOUD);
    public static final MinecraftEntityType ARMOR_STAND            = constant(EntityType.ARMOR_STAND);
    public static final MinecraftEntityType ARROW                  = constant(EntityType.ARROW);
    public static final MinecraftEntityType AXOLOTL                = constant(EntityType.AXOLOTL);
    public static final MinecraftEntityType BAT                    = constant(EntityType.BAT);
    public static final MinecraftEntityType BEE                    = constant(EntityType.BEE);
    public static final MinecraftEntityType BLAZE                  = constant(EntityType.BLAZE);
    public static final MinecraftEntityType BLOCK_DISPLAY          = constant(EntityType.BLOCK_DISPLAY);
    public static final MinecraftEntityType BOAT                   = constant(EntityType.BOAT);
    public static final MinecraftEntityType CAMEL                  = constant(EntityType.CAMEL);
    public static final MinecraftEntityType CAT                    = constant(EntityType.CAT);
    public static final MinecraftEntityType CAVE_SPIDER            = constant(EntityType.CAVE_SPIDER);
    public static final MinecraftEntityType CHEST_BOAT             = constant(EntityType.CHEST_BOAT);
    public static final MinecraftEntityType CHEST_MINECART         = constant(EntityType.CHEST_MINECART);
    public static final MinecraftEntityType CHICKEN                = constant(EntityType.CHICKEN);
    public static final MinecraftEntityType COD                    = constant(EntityType.COD);
    public static final MinecraftEntityType COMMAND_BLOCK_MINECART = constant(EntityType.COMMAND_BLOCK_MINECART);
    public static final MinecraftEntityType COW                    = constant(EntityType.COW);
    public static final MinecraftEntityType CREEPER                = constant(EntityType.CREEPER);
    public static final MinecraftEntityType DOLPHIN                = constant(EntityType.DOLPHIN);
    public static final MinecraftEntityType DONKEY                 = constant(EntityType.DONKEY);
    public static final MinecraftEntityType DRAGON_FIREBALL        = constant(EntityType.DRAGON_FIREBALL);
    public static final MinecraftEntityType DROWNED                = constant(EntityType.DROWNED);
    public static final MinecraftEntityType EGG                    = constant(EntityType.EGG);
    public static final MinecraftEntityType ELDER_GUARDIAN         = constant(EntityType.ELDER_GUARDIAN);
    public static final MinecraftEntityType END_CRYSTAL            = constant(EntityType.END_CRYSTAL);
    public static final MinecraftEntityType ENDER_DRAGON           = constant(EntityType.ENDER_DRAGON);
    public static final MinecraftEntityType ENDER_PEARL            = constant(EntityType.ENDER_PEARL);
    public static final MinecraftEntityType ENDERMAN               = constant(EntityType.ENDERMAN);
    public static final MinecraftEntityType ENDERMITE              = constant(EntityType.ENDERMITE);
    public static final MinecraftEntityType EVOKER                 = constant(EntityType.EVOKER);
    public static final MinecraftEntityType EVOKER_FANGS           = constant(EntityType.EVOKER_FANGS);
    public static final MinecraftEntityType EXPERIENCE_BOTTLE      = constant(EntityType.EXPERIENCE_BOTTLE);
    public static final MinecraftEntityType EXPERIENCE_ORB         = constant(EntityType.EXPERIENCE_ORB);
    public static final MinecraftEntityType EYE_OF_ENDER           = constant(EntityType.EYE_OF_ENDER);
    public static final MinecraftEntityType FALLING_BLOCK          = constant(EntityType.FALLING_BLOCK);
    public static final MinecraftEntityType FIREWORK_ROCKET        = constant(EntityType.FIREWORK_ROCKET);
    public static final MinecraftEntityType FOX                    = constant(EntityType.FOX);
    public static final MinecraftEntityType FROG                   = constant(EntityType.FROG);
    public static final MinecraftEntityType FURNACE_MINECART       = constant(EntityType.FURNACE_MINECART);
    public static final MinecraftEntityType GHAST                  = constant(EntityType.GHAST);
    public static final MinecraftEntityType GIANT                  = constant(EntityType.GIANT);
    public static final MinecraftEntityType GLOW_ITEM_FRAME        = constant(EntityType.GLOW_ITEM_FRAME);
    public static final MinecraftEntityType GLOW_SQUID             = constant(EntityType.GLOW_SQUID);
    public static final MinecraftEntityType GOAT                   = constant(EntityType.GOAT);
    public static final MinecraftEntityType GUARDIAN               = constant(EntityType.GUARDIAN);
    public static final MinecraftEntityType HOGLIN                 = constant(EntityType.HOGLIN);
    public static final MinecraftEntityType HOPPER_MINECART        = constant(EntityType.HOPPER_MINECART);
    public static final MinecraftEntityType HORSE                  = constant(EntityType.HORSE);
    public static final MinecraftEntityType HUSK                   = constant(EntityType.HUSK);
    public static final MinecraftEntityType ILLUSIONER             = constant(EntityType.ILLUSIONER);
    public static final MinecraftEntityType INTERACTION            = constant(EntityType.INTERACTION);
    public static final MinecraftEntityType IRON_GOLEM             = constant(EntityType.IRON_GOLEM);
    public static final MinecraftEntityType ITEM                   = constant(EntityType.ITEM);
    public static final MinecraftEntityType ITEM_DISPLAY           = constant(EntityType.ITEM_DISPLAY);
    public static final MinecraftEntityType ITEM_FRAME             = constant(EntityType.ITEM_FRAME);
    public static final MinecraftEntityType FIREBALL               = constant(EntityType.FIREBALL);
    public static final MinecraftEntityType LEASH_KNOT             = constant(EntityType.LEASH_KNOT);
    public static final MinecraftEntityType LIGHTNING_BOLT         = constant(EntityType.LIGHTNING_BOLT);
    public static final MinecraftEntityType LLAMA                  = constant(EntityType.LLAMA);
    public static final MinecraftEntityType LLAMA_SPIT             = constant(EntityType.LLAMA_SPIT);
    public static final MinecraftEntityType MAGMA_CUBE             = constant(EntityType.MAGMA_CUBE);
    public static final MinecraftEntityType MARKER                 = constant(EntityType.MARKER);
    public static final MinecraftEntityType MINECART               = constant(EntityType.MINECART);
    public static final MinecraftEntityType MOOSHROOM              = constant(EntityType.MOOSHROOM);
    public static final MinecraftEntityType MULE                   = constant(EntityType.MULE);
    public static final MinecraftEntityType OCELOT                 = constant(EntityType.OCELOT);
    public static final MinecraftEntityType PAINTING               = constant(EntityType.PAINTING);
    public static final MinecraftEntityType PANDA                  = constant(EntityType.PANDA);
    public static final MinecraftEntityType PARROT                 = constant(EntityType.PARROT);
    public static final MinecraftEntityType PHANTOM                = constant(EntityType.PHANTOM);
    public static final MinecraftEntityType PIG                    = constant(EntityType.PIG);
    public static final MinecraftEntityType PIGLIN                 = constant(EntityType.PIGLIN);
    public static final MinecraftEntityType PIGLIN_BRUTE           = constant(EntityType.PIGLIN_BRUTE);
    public static final MinecraftEntityType PILLAGER               = constant(EntityType.PILLAGER);
    public static final MinecraftEntityType POLAR_BEAR             = constant(EntityType.POLAR_BEAR);
    public static final MinecraftEntityType POTION                 = constant(EntityType.POTION);
    public static final MinecraftEntityType PUFFERFISH             = constant(EntityType.PUFFERFISH);
    public static final MinecraftEntityType RABBIT                 = constant(EntityType.RABBIT);
    public static final MinecraftEntityType RAVAGER                = constant(EntityType.RAVAGER);
    public static final MinecraftEntityType SALMON                 = constant(EntityType.SALMON);
    public static final MinecraftEntityType SHEEP                  = constant(EntityType.SHEEP);
    public static final MinecraftEntityType SHULKER                = constant(EntityType.SHULKER);
    public static final MinecraftEntityType SHULKER_BULLET         = constant(EntityType.SHULKER_BULLET);
    public static final MinecraftEntityType SILVERFISH             = constant(EntityType.SILVERFISH);
    public static final MinecraftEntityType SKELETON               = constant(EntityType.SKELETON);
    public static final MinecraftEntityType SKELETON_HORSE         = constant(EntityType.SKELETON_HORSE);
    public static final MinecraftEntityType SLIME                  = constant(EntityType.SLIME);
    public static final MinecraftEntityType SMALL_FIREBALL         = constant(EntityType.SMALL_FIREBALL);
    public static final MinecraftEntityType SNIFFER                = constant(EntityType.SNIFFER);
    public static final MinecraftEntityType SNOW_GOLEM             = constant(EntityType.SNOW_GOLEM);
    public static final MinecraftEntityType SNOWBALL               = constant(EntityType.SNOWBALL);
    public static final MinecraftEntityType SPAWNER_MINECART       = constant(EntityType.SPAWNER_MINECART);
    public static final MinecraftEntityType SPECTRAL_ARROW         = constant(EntityType.SPECTRAL_ARROW);
    public static final MinecraftEntityType SPIDER                 = constant(EntityType.SPIDER);
    public static final MinecraftEntityType SQUID                  = constant(EntityType.SQUID);
    public static final MinecraftEntityType STRAY                  = constant(EntityType.STRAY);
    public static final MinecraftEntityType STRIDER                = constant(EntityType.STRIDER);
    public static final MinecraftEntityType TADPOLE                = constant(EntityType.TADPOLE);
    public static final MinecraftEntityType TEXT_DISPLAY           = constant(EntityType.TEXT_DISPLAY);
    public static final MinecraftEntityType TNT                    = constant(EntityType.TNT);
    public static final MinecraftEntityType TNT_MINECART           = constant(EntityType.TNT_MINECART);
    public static final MinecraftEntityType TRADER_LLAMA           = constant(EntityType.TRADER_LLAMA);
    public static final MinecraftEntityType TRIDENT                = constant(EntityType.TRIDENT);
    public static final MinecraftEntityType TROPICAL_FISH          = constant(EntityType.TROPICAL_FISH);
    public static final MinecraftEntityType TURTLE                 = constant(EntityType.TURTLE);
    public static final MinecraftEntityType VEX                    = constant(EntityType.VEX);
    public static final MinecraftEntityType VILLAGER               = constant(EntityType.VILLAGER);
    public static final MinecraftEntityType VINDICATOR             = constant(EntityType.VINDICATOR);
    public static final MinecraftEntityType WANDERING_TRADER       = constant(EntityType.WANDERING_TRADER);
    public static final MinecraftEntityType WARDEN                 = constant(EntityType.WARDEN);
    public static final MinecraftEntityType WITCH                  = constant(EntityType.WITCH);
    public static final MinecraftEntityType WITHER                 = constant(EntityType.WITHER);
    public static final MinecraftEntityType WITHER_SKELETON        = constant(EntityType.WITHER_SKELETON);
    public static final MinecraftEntityType WITHER_SKULL           = constant(EntityType.WITHER_SKULL);
    public static final MinecraftEntityType WOLF                   = constant(EntityType.WOLF);
    public static final MinecraftEntityType ZOGLIN                 = constant(EntityType.ZOGLIN);
    public static final MinecraftEntityType ZOMBIE                 = constant(EntityType.ZOMBIE);
    public static final MinecraftEntityType ZOMBIE_HORSE           = constant(EntityType.ZOMBIE_HORSE);
    public static final MinecraftEntityType ZOMBIE_VILLAGER        = constant(EntityType.ZOMBIE_VILLAGER);
    public static final MinecraftEntityType ZOMBIFIED_PIGLIN       = constant(EntityType.ZOMBIFIED_PIGLIN);
    public static final MinecraftEntityType PLAYER                 = constant(EntityType.PLAYER);
    public static final MinecraftEntityType FISHING_BOBBER         = constant(EntityType.FISHING_BOBBER);

    private static MinecraftEntityType constant(EntityType<?> type) { return new MinecraftEntityType(new MouseEntityState(type)); }

    static { storage.put(MinecraftEntityType.class, MinecraftEntityType::parse); }

    private MinecraftEntityType(MouseEntityState state) { super(Objects.requireNonNull(state, "MouseEntityState cannot be null")); }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftEntityType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseEntityState state = MouseEntityState.create(prefix, name);
        if (state == null) throw new ValueFormatException("Unknown entity: " + prefix + ":" + name);
        return new MinecraftEntityType(state);
    }

    public static MinecraftEntityType create(String name)            { return create("minecraft", name); }
    public static MinecraftEntityType create(MouseEntityState state) { return new MinecraftEntityType(state); }

    @Override public IValType getValType()                           { return ValType.MINECRAFT_ENTITY; }
    public MouseEntityState asEntityState()                          { return value; }
    public String getPrefix()                                        { return value.getRegistryKey().getNamespace(); }
    public String getName()                                          { return value.getRegistryKey().getPath(); }

    public static MinecraftEntityType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftEntityType entityType) return entityType;
        else if (value instanceof MouseEntityState state) return create(state);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftEntityType from " + value);
    }

    public static MinecraftEntityType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty entity string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Entity name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftEntityType fromString(@Nullable String value)                         { return parse((String) null, value); }
    @Override @SuppressWarnings("unchecked") public MinecraftEntityType createType(Object value) { return parse(value); }
}