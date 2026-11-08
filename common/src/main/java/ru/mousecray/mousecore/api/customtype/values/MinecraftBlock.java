/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseIBlockState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MinecraftBlock extends OtherType<MouseIBlockState> {
    public static final MinecraftBlock AIR = MinecraftBlock.create("air");

    static { storage.put(MinecraftBlock.class, MinecraftBlock::parse); }

    @Nonnull protected final String prefix;
    @Nonnull protected final String name;

    protected MinecraftBlock(String prefix, String name) {
        super(MouseIBlockState.create(prefix, name));
        this.prefix = prefix;
        this.name = name;
    }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftBlock create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");
        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");
        return new MinecraftBlock(prefix, name);
    }

    public static MinecraftBlock create(String name) { return create("minecraft", name); }

    @Nullable public MouseIBlockState asBlockState() { return value; }

    public String getPrefix()                        { return prefix; }
    public String getName()                          { return name; }

    public static MinecraftBlock parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftBlock) return ((MinecraftBlock) value);
        else if (value instanceof String) return parse((String) null, (String) value);
        throw new UnsupportedValException();
    }

    @SuppressWarnings({ "DataFlowIssue" })
    public static MinecraftBlock parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
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

    @SuppressWarnings("unchecked") @Override public MinecraftBlock createType(Object value) { return create(value.toString()); }
}