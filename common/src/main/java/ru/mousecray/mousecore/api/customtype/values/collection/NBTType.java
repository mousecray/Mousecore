/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.values.collection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class NBTType extends CustomType<NBTType> {
    private static final MouseUtils.Equator<NBTType> EQUATOR = MouseUtils.Equator
            .equaling(NBTType::getRawCompoundTag);

    public static final NBTType EMPTY = new NBTType(new CompoundTag());

    static {
        storage.put(NBTType.class, NBTType::parse);
    }

    private final CompoundTag tag;

    private NBTType(CompoundTag tag) {
        super();
        this.tag = Objects.requireNonNull(tag, "CompoundTag cannot be null");
    }

    public static NBTType create(CompoundTag tag) {
        return new NBTType(tag.copy());
    }

    public static NBTType create() {
        return EMPTY;
    }

    public static NBTType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) return EMPTY;

        try {
            CompoundTag parsed = TagParser.parseTag(clean);
            return new NBTType(parsed);
        } catch (Exception e) { throw new ValueFormatException("Invalid SNBT format in '" + raw + "': " + e.getMessage(), e); }
    }

    public static NBTType fromString(@Nullable String raw) {
        return parse(raw);
    }

    public CompoundTag asCompoundTag() {
        return tag.copy();
    }

    public CompoundTag getRawCompoundTag() {
        return tag;
    }

    public String asString() {
        return tag.toString();
    }

    public boolean contains(String key) {
        return tag.contains(key);
    }

    public boolean isEmpty() {
        return tag.isEmpty();
    }

    public int size() {
        return tag.size();
    }

    public Set<String> asKeySet() {
        return tag.getAllKeys();
    }

    public NBTType merge(NBTType other) {
        CompoundTag copy = tag.copy();
        copy.merge(other.getRawCompoundTag());
        return new NBTType(copy);
    }

    public NBTType remove(String key) {
        CompoundTag copy = tag.copy();
        copy.remove(key);
        return new NBTType(copy);
    }

    @Override public IValType getValType() { return ValType.NBT; }

    @Override
    public String toString() {
        return tag.toString();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return tag.hashCode(); }

    @Override
    public int compareTo(NBTType o) {
        return Comparator.comparing(NBTType::asString).compare(this, o);
    }
}