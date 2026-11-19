/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftTagType extends OtherType<ResourceLocation> {
    private static final MouseUtils.Equator<MinecraftTagType> EQUATOR = MouseUtils.Equator
            .<MinecraftTagType>equaling(MinecraftTagType::asLocation)
            .thenEqualing(MinecraftTagType::getTargetValType);

    static { storage.put(MinecraftTagType.class, MinecraftTagType::parse); }

    private final IValType targetValType;

    private MinecraftTagType(ResourceLocation location, IValType targetValType) {
        super(Objects.requireNonNull(location, "ResourceLocation cannot be null"));
        this.targetValType = Objects.requireNonNull(targetValType, "TargetValType cannot be null");
    }

    public static MinecraftTagType create(ResourceLocation location, IValType targetValType) { return new MinecraftTagType(location, targetValType); }
    public static MinecraftTagType create(ResourceLocation location)                         { return create(location, ValType.UNDEFINED); }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftTagType create(String namespace, String path, IValType targetValType) {
        namespace = MouseStrings.trimWith(Objects.requireNonNull(namespace), true, '\t');
        if (namespace.isEmpty()) throw new ValueFormatException("Tag namespace cannot be empty");
        if (namespace.contains("\t")) throw new ValueFormatException("Tag namespace cannot contains tabs");
        if (namespace.contains(" ")) throw new ValueFormatException("Tag namespace cannot contains space");

        path = MouseStrings.trimWith(Objects.requireNonNull(path), true, '\t');
        if (path.isEmpty()) throw new ValueFormatException("Tag path cannot be empty");
        if (path.contains("\t")) throw new ValueFormatException("Tag path cannot contains tabs");
        if (path.contains(" ")) throw new ValueFormatException("Tag path cannot contains space");

        ResourceLocation loc = ResourceLocation.tryBuild(namespace, path);
        if (loc == null) throw new ValueFormatException("Invalid ResourceLocation characters in: #" + namespace + ":" + path);

        return create(loc, targetValType);
    }

    public static MinecraftTagType create(String namespace, String path) { return create(namespace, path, ValType.UNDEFINED); }
    public static MinecraftTagType create(String path)                   { return create("minecraft", path, ValType.UNDEFINED); }

    public static <T> MinecraftTagType create(TagKey<T> tagKey, IValType targetValType) {
        Objects.requireNonNull(tagKey, "TagKey cannot be null");
        return create(tagKey.location(), targetValType);
    }

    public ResourceLocation asLocation() { return value; }
    public IValType getTargetValType()   { return targetValType; }
    public String getNamespace()         { return value.getNamespace(); }
    public String getPath()              { return value.getPath(); }

    public <T> TagKey<T> asTagKey(ResourceKey<? extends Registry<T>> registryKey) {
        return TagKey.create(registryKey, value);
    }

    public TagKey<Item> asItemTag()            { return asTagKey(Registries.ITEM); }
    public TagKey<Block> asBlockTag()          { return asTagKey(Registries.BLOCK); }
    public TagKey<EntityType<?>> asEntityTag() { return asTagKey(Registries.ENTITY_TYPE); }
    public TagKey<Biome> asBiomeTag()          { return asTagKey(Registries.BIOME); }

    @SuppressWarnings("deprecation")
    public boolean contains(@Nullable MinecraftItemType item) {
        if (item == null) return false;
        return item.asItemState().getItem().builtInRegistryHolder().is(asItemTag());
    }

    public boolean contains(@Nullable MinecraftBlockType block) {
        if (block == null) return false;
        return block.asBlockState().getState().is(asBlockTag());
    }

    public boolean contains(@Nullable MinecraftItemStackType stack) { return stack != null && contains(stack.getItem()); }
    public boolean contains(@Nullable MinecraftEntityType entity)   { return entity != null && entity.asEntityState().is(asEntityTag()); }

    public static MinecraftTagType parse(@Nullable String raw, IValType targetValType) throws ValueFormatException {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty tag string");

        if (clean.startsWith("#")) clean = clean.substring(1).trim();

        int    colonIdx  = clean.indexOf(':');
        String namespace = "minecraft";
        String path;

        if (colonIdx == -1) path = clean;
        else {
            String ns = clean.substring(0, colonIdx).trim();
            path = clean.substring(colonIdx + 1).trim();
            if (!ns.isEmpty()) namespace = ns;
        }

        if (path.isEmpty()) throw new ValueFormatException("Tag path cannot be empty in: '" + raw + "'");
        return create(namespace, path, targetValType);
    }

    public static MinecraftTagType parse(@Nullable String raw) throws ValueFormatException { return parse(raw, ValType.UNDEFINED); }
    public static MinecraftTagType fromString(@Nullable String raw)                        { return parse(raw); }

    @Override public IValType getValType()                                                 { return ValType.MINECRAFT_TAG; }

    @Override @SuppressWarnings("unchecked")
    public MinecraftTagType createType(Object val) {
        if (val instanceof MinecraftTagType tagType) return tagType;
        else if (val instanceof TagKey<?> tagKey) return create(tagKey, targetValType);
        else if (val instanceof ResourceLocation loc) return create(loc, targetValType);
        else if (val instanceof String str) return parse(str, targetValType);
        throw new UnsupportedValException("Cannot create MinecraftTagType from " + val);
    }

    @Override public String toString() { return "#" + value.toString(); }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(value, targetValType); }

    @Override
    public int compareTo(OtherType<?> o) {
        if (o instanceof MinecraftTagType other) {
            return Comparator
                    .comparing(MinecraftTagType::asLocation)
                    .thenComparing(t -> t.getTargetValType().getDisplayName())
                    .compare(this, other);
        }
        return super.compareTo(o);
    }
}