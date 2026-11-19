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
    public static final MinecraftBiomeType PLAINS = new MinecraftBiomeType(MouseResourceKey.create(Biomes.PLAINS));

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