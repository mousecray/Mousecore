/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseBlockState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftBlockType extends OtherType<MouseBlockState> {
    public static final MinecraftBlockType AIR = MinecraftBlockType.create("air");

    static { storage.put(MinecraftBlockType.class, MinecraftBlockType::parse); }

    @Nonnull private final String prefix;
    @Nonnull private final String name;

    private MinecraftBlockType(String prefix, String name) {
        super(MouseBlockState.create(prefix, name));
        this.prefix = prefix;
        this.name = name;
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
        return new MinecraftBlockType(prefix, name);
    }

    public static MinecraftBlockType create(String name) { return create("minecraft", name); }

    @Override public IValType getValType()               { return ValType.MINECRAFT_BLOCK; }

    @Nullable public MouseBlockState asBlockState()      { return value; }

    public String getPrefix()                            { return prefix; }
    public String getName()                              { return name; }

    public static MinecraftBlockType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftBlockType) return ((MinecraftBlockType) value);
        else if (value instanceof String) return parse((String) null, (String) value);
        throw new UnsupportedValException();
    }

    @SuppressWarnings({ "DataFlowIssue" })
    public static MinecraftBlockType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        if (value == null) throw new ValueFormatException();

        String[] split = value.split(":");
        String   name  = null;

        if (split.length > 0) {
            String prefixCandidate = MouseStrings.trimWith(split[0], true, '\t');
            if (!prefixCandidate.isEmpty() && !prefixCandidate.contains(" ") && !prefixCandidate.contains("\t")) {
                if (expectedPrefix != null) {
                    if (!prefixCandidate.equals(expectedPrefix)) {
                        name = prefixCandidate;
                        return create(expectedPrefix, name);
                    }
                } else expectedPrefix = prefixCandidate;
            }

            if (split.length > 1) {
                String rawName = MouseStrings.trimWith(split[1], true, '\t');
                if (!rawName.isEmpty() && !rawName.contains(" ") && !rawName.contains("\t")) name = rawName;
            }
        }

        if (name != null && expectedPrefix != null) return create(expectedPrefix, name);
        throw new ValueFormatException();
    }

    @SuppressWarnings("unchecked") @Override public MinecraftBlockType createType(Object value) { return create(value.toString()); }
}