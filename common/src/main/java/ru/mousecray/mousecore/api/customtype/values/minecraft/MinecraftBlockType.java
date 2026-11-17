/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseBlockState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftBlockType extends OtherType<MouseBlockState> {
    public static final MinecraftBlockType AIR = MinecraftBlockType.create("air");

    static { storage.put(MinecraftBlockType.class, MinecraftBlockType::parse); }

    private MinecraftBlockType(MouseBlockState state) {
        super(Objects.requireNonNull(state, "MouseBlockState cannot be null"));
    }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftBlockType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseBlockState state = MouseBlockState.create(prefix, name);
        if (state == null) throw new ValueFormatException("Unknown block: " + prefix + ":" + name);
        return new MinecraftBlockType(state);
    }

    public static MinecraftBlockType create(String name)           { return create("minecraft", name); }
    public static MinecraftBlockType create(MouseBlockState state) { return new MinecraftBlockType(state); }

    @Override public IValType getValType()                         { return ValType.MINECRAFT_BLOCK; }
    public MouseBlockState asBlockState()                          { return value; }

    @SuppressWarnings("deprecation") public String getPrefix()     { return BuiltInRegistries.BLOCK.getKey(value.getBlock()).getNamespace(); }

    @SuppressWarnings("deprecation") public String getName()       { return BuiltInRegistries.BLOCK.getKey(value.getBlock()).getPath(); }

    public static MinecraftBlockType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftBlockType blockType) return blockType;
        else if (value instanceof MouseBlockState state) return create(state);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftBlockType from " + value);
    }

    public static MinecraftBlockType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty block string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) {
            return create(defaultNamespace, clean);
        } else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Block name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftBlockType fromString(@Nullable String value)                         { return parse((String) null, value); }

    @Override @SuppressWarnings("unchecked") public MinecraftBlockType createType(Object value) { return parse(value); }

    @Override @SuppressWarnings("deprecation")
    public String toString() { return BuiltInRegistries.BLOCK.getKey(value.getBlock()).toString(); }
}