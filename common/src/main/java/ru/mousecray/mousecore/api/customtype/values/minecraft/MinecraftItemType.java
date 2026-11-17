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
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftItemType extends OtherType<MouseItemState> {
    public static final MinecraftItemType AIR = MinecraftItemType.create("air");

    static { storage.put(MinecraftItemType.class, MinecraftItemType::parse); }

    private MinecraftItemType(MouseItemState state) {
        super(Objects.requireNonNull(state, "MouseItemState cannot be null"));
    }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftItemType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseItemState state = MouseItemState.create(prefix, name);
        if (state == null) throw new ValueFormatException("Unknown item: " + prefix + ":" + name);
        return new MinecraftItemType(state);
    }

    public static MinecraftItemType create(String name)          { return create("minecraft", name); }
    public static MinecraftItemType create(MouseItemState state) { return new MinecraftItemType(state); }

    @Override public IValType getValType()                       { return ValType.MINECRAFT_ITEM; }
    public MouseItemState asItemState()                          { return value; }

    @SuppressWarnings("deprecation") public String getPrefix()   { return BuiltInRegistries.ITEM.getKey(value.getItem()).getNamespace(); }
    @SuppressWarnings("deprecation") public String getName()     { return BuiltInRegistries.ITEM.getKey(value.getItem()).getPath(); }

    public static MinecraftItemType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftItemType itemType) return itemType;
        else if (value instanceof MouseItemState state) return create(state);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftItemType from " + value);
    }

    public static MinecraftItemType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty item string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) {
            return create(defaultNamespace, clean);
        } else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Item name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftItemType fromString(@Nullable String value)                         { return parse((String) null, value); }

    @Override @SuppressWarnings("unchecked") public MinecraftItemType createType(Object value) { return parse(value); }

    @Override @SuppressWarnings("deprecation")
    public String toString() { return BuiltInRegistries.ITEM.getKey(value.getItem()).toString(); }
}