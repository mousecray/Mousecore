/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseResourceKey;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftBiomeType extends OtherType<MouseResourceKey<Biome>> {
    public static final MinecraftBiomeType THE_VOID                 = constant(Biomes.THE_VOID);
    public static final MinecraftBiomeType PLAINS                   = constant(Biomes.PLAINS);
    public static final MinecraftBiomeType SUNFLOWER_PLAINS         = constant(Biomes.SUNFLOWER_PLAINS);
    public static final MinecraftBiomeType SNOWY_PLAINS             = constant(Biomes.SNOWY_PLAINS);
    public static final MinecraftBiomeType ICE_SPIKES               = constant(Biomes.ICE_SPIKES);
    public static final MinecraftBiomeType DESERT                   = constant(Biomes.DESERT);
    public static final MinecraftBiomeType SWAMP                    = constant(Biomes.SWAMP);
    public static final MinecraftBiomeType MANGROVE_SWAMP           = constant(Biomes.MANGROVE_SWAMP);
    public static final MinecraftBiomeType FOREST                   = constant(Biomes.FOREST);
    public static final MinecraftBiomeType FLOWER_FOREST            = constant(Biomes.FLOWER_FOREST);
    public static final MinecraftBiomeType BIRCH_FOREST             = constant(Biomes.BIRCH_FOREST);
    public static final MinecraftBiomeType DARK_FOREST              = constant(Biomes.DARK_FOREST);
    public static final MinecraftBiomeType OLD_GROWTH_BIRCH_FOREST  = constant(Biomes.OLD_GROWTH_BIRCH_FOREST);
    public static final MinecraftBiomeType OLD_GROWTH_PINE_TAIGA    = constant(Biomes.OLD_GROWTH_PINE_TAIGA);
    public static final MinecraftBiomeType OLD_GROWTH_SPRUCE_TAIGA  = constant(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
    public static final MinecraftBiomeType TAIGA                    = constant(Biomes.TAIGA);
    public static final MinecraftBiomeType SNOWY_TAIGA              = constant(Biomes.SNOWY_TAIGA);
    public static final MinecraftBiomeType SAVANNA                  = constant(Biomes.SAVANNA);
    public static final MinecraftBiomeType SAVANNA_PLATEAU          = constant(Biomes.SAVANNA_PLATEAU);
    public static final MinecraftBiomeType WINDSWEPT_HILLS          = constant(Biomes.WINDSWEPT_HILLS);
    public static final MinecraftBiomeType WINDSWEPT_GRAVELLY_HILLS = constant(Biomes.WINDSWEPT_GRAVELLY_HILLS);
    public static final MinecraftBiomeType WINDSWEPT_FOREST         = constant(Biomes.WINDSWEPT_FOREST);
    public static final MinecraftBiomeType WINDSWEPT_SAVANNA        = constant(Biomes.WINDSWEPT_SAVANNA);
    public static final MinecraftBiomeType JUNGLE                   = constant(Biomes.JUNGLE);
    public static final MinecraftBiomeType SPARSE_JUNGLE            = constant(Biomes.SPARSE_JUNGLE);
    public static final MinecraftBiomeType BAMBOO_JUNGLE            = constant(Biomes.BAMBOO_JUNGLE);
    public static final MinecraftBiomeType BADLANDS                 = constant(Biomes.BADLANDS);
    public static final MinecraftBiomeType ERODED_BADLANDS          = constant(Biomes.ERODED_BADLANDS);
    public static final MinecraftBiomeType WOODED_BADLANDS          = constant(Biomes.WOODED_BADLANDS);
    public static final MinecraftBiomeType MEADOW                   = constant(Biomes.MEADOW);
    public static final MinecraftBiomeType CHERRY_GROVE             = constant(Biomes.CHERRY_GROVE);
    public static final MinecraftBiomeType GROVE                    = constant(Biomes.GROVE);
    public static final MinecraftBiomeType SNOWY_SLOPES             = constant(Biomes.SNOWY_SLOPES);
    public static final MinecraftBiomeType FROZEN_PEAKS             = constant(Biomes.FROZEN_PEAKS);
    public static final MinecraftBiomeType JAGGED_PEAKS             = constant(Biomes.JAGGED_PEAKS);
    public static final MinecraftBiomeType STONY_PEAKS              = constant(Biomes.STONY_PEAKS);
    public static final MinecraftBiomeType RIVER                    = constant(Biomes.RIVER);
    public static final MinecraftBiomeType FROZEN_RIVER             = constant(Biomes.FROZEN_RIVER);
    public static final MinecraftBiomeType BEACH                    = constant(Biomes.BEACH);
    public static final MinecraftBiomeType SNOWY_BEACH              = constant(Biomes.SNOWY_BEACH);
    public static final MinecraftBiomeType STONY_SHORE              = constant(Biomes.STONY_SHORE);
    public static final MinecraftBiomeType WARM_OCEAN               = constant(Biomes.WARM_OCEAN);
    public static final MinecraftBiomeType LUKEWARM_OCEAN           = constant(Biomes.LUKEWARM_OCEAN);
    public static final MinecraftBiomeType DEEP_LUKEWARM_OCEAN      = constant(Biomes.DEEP_LUKEWARM_OCEAN);
    public static final MinecraftBiomeType OCEAN                    = constant(Biomes.OCEAN);
    public static final MinecraftBiomeType DEEP_OCEAN               = constant(Biomes.DEEP_OCEAN);
    public static final MinecraftBiomeType COLD_OCEAN               = constant(Biomes.COLD_OCEAN);
    public static final MinecraftBiomeType DEEP_COLD_OCEAN          = constant(Biomes.DEEP_COLD_OCEAN);
    public static final MinecraftBiomeType FROZEN_OCEAN             = constant(Biomes.FROZEN_OCEAN);
    public static final MinecraftBiomeType DEEP_FROZEN_OCEAN        = constant(Biomes.DEEP_FROZEN_OCEAN);
    public static final MinecraftBiomeType MUSHROOM_FIELDS          = constant(Biomes.MUSHROOM_FIELDS);
    public static final MinecraftBiomeType DRIPSTONE_CAVES          = constant(Biomes.DRIPSTONE_CAVES);
    public static final MinecraftBiomeType LUSH_CAVES               = constant(Biomes.LUSH_CAVES);
    public static final MinecraftBiomeType DEEP_DARK                = constant(Biomes.DEEP_DARK);
    public static final MinecraftBiomeType NETHER_WASTES            = constant(Biomes.NETHER_WASTES);
    public static final MinecraftBiomeType WARPED_FOREST            = constant(Biomes.WARPED_FOREST);
    public static final MinecraftBiomeType CRIMSON_FOREST           = constant(Biomes.CRIMSON_FOREST);
    public static final MinecraftBiomeType SOUL_SAND_VALLEY         = constant(Biomes.SOUL_SAND_VALLEY);
    public static final MinecraftBiomeType BASALT_DELTAS            = constant(Biomes.BASALT_DELTAS);
    public static final MinecraftBiomeType THE_END                  = constant(Biomes.THE_END);
    public static final MinecraftBiomeType END_HIGHLANDS            = constant(Biomes.END_HIGHLANDS);
    public static final MinecraftBiomeType END_MIDLANDS             = constant(Biomes.END_MIDLANDS);
    public static final MinecraftBiomeType SMALL_END_ISLANDS        = constant(Biomes.SMALL_END_ISLANDS);
    public static final MinecraftBiomeType END_BARRENS              = constant(Biomes.END_BARRENS);

    private static MinecraftBiomeType constant(ResourceKey<Biome> biome) { return new MinecraftBiomeType(MouseResourceKey.create(biome)); }

    static { storage.put(MinecraftBiomeType.class, MinecraftBiomeType::parse); }

    private MinecraftBiomeType(MouseResourceKey<Biome> key) { super(Objects.requireNonNull(key, "MouseResourceKey cannot be null")); }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftBiomeType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        return new MinecraftBiomeType(MouseResourceKey.create(Registries.BIOME, prefix, name));
    }

    public static MinecraftBiomeType create(String name)                 { return create("minecraft", name); }
    public static MinecraftBiomeType create(MouseResourceKey<Biome> key) { return new MinecraftBiomeType(key); }
    public static MinecraftBiomeType create(ResourceKey<Biome> key)      { return new MinecraftBiomeType(MouseResourceKey.create(key)); }

    @Override public IValType getValType()                               { return ValType.MINECRAFT_BIOME; }
    public MouseResourceKey<Biome> asResourceKey()                       { return value; }
    public String getPrefix()                                            { return value.getNamespace(); }
    public String getName()                                              { return value.getPath(); }

    @SuppressWarnings("unchecked")
    public static MinecraftBiomeType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftBiomeType biomeType) return biomeType;
        else if (value instanceof MouseResourceKey<?> key && key.isFor(Registries.BIOME)) return create((MouseResourceKey<Biome>) key);
        else if (value instanceof ResourceKey<?> key && key.isFor(Registries.BIOME)) return create((ResourceKey<Biome>) key);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftBiomeType from " + value);
    }

    public static MinecraftBiomeType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty biome string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Biome name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftBiomeType fromString(@Nullable String value)                         { return parse((String) null, value); }
    @Override @SuppressWarnings("unchecked") public MinecraftBiomeType createType(Object value) { return parse(value); }
}