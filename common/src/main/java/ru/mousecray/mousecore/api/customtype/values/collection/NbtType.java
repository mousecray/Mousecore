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
public final class NbtType extends CustomType<NbtType> {
    private static final MouseUtils.Equator<NbtType> EQUATOR = MouseUtils.Equator
            .equaling(NbtType::getRawCompoundTag);

    public static final NbtType EMPTY = new NbtType(new CompoundTag());

    static {
        storage.put(NbtType.class, NbtType::parse);
    }

    private final CompoundTag tag;

    private NbtType(CompoundTag tag) {
        super();
        this.tag = Objects.requireNonNull(tag, "CompoundTag cannot be null");
    }

    public static NbtType create(CompoundTag tag) {
        return new NbtType(tag.copy());
    }

    public static NbtType create() {
        return EMPTY;
    }

    public static NbtType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) return EMPTY;

        try {
            CompoundTag parsed = TagParser.parseTag(clean);
            return new NbtType(parsed);
        } catch (Exception e) { throw new ValueFormatException("Invalid SNBT format in '" + raw + "': " + e.getMessage(), e); }
    }

    public static NbtType fromString(@Nullable String raw) {
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

    public NbtType merge(NbtType other) {
        CompoundTag copy = tag.copy();
        copy.merge(other.getRawCompoundTag());
        return new NbtType(copy);
    }

    public NbtType remove(String key) {
        CompoundTag copy = tag.copy();
        copy.remove(key);
        return new NbtType(copy);
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
    public int compareTo(NbtType o) {
        return Comparator.comparing(NbtType::asString).compare(this, o);
    }
}