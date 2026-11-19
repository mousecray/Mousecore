/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MouseRegistryEntry<T> implements Comparable<MouseRegistryEntry<T>> {
    private static final MouseUtils.Equator<MouseRegistryEntry<?>> EQUATOR = MouseUtils.Equator
            .<MouseRegistryEntry<?>>equaling(e -> e.getRegistry().key())
            .thenEqualing(MouseRegistryEntry::getLocation);

    private final Registry<T>      registry;
    private final T                value;
    private final ResourceLocation location;

    public MouseRegistryEntry(Registry<T> registry, T value, ResourceLocation location) {
        this.registry = Objects.requireNonNull(registry, "Registry cannot be null");
        this.value = Objects.requireNonNull(value, "Registry value cannot be null");
        this.location = Objects.requireNonNull(location, "ResourceLocation cannot be null");
    }

    public static <T> MouseRegistryEntry<T> create(Registry<T> registry, T value) {
        Objects.requireNonNull(registry, "Registry cannot be null");
        Objects.requireNonNull(value, "Registry value cannot be null");
        ResourceLocation key = registry.getKey(value);
        if (key == null) {
            throw new ValueFormatException("Value " + value + " is not registered in " + registry.key().location());
        }
        return new MouseRegistryEntry<>(registry, value, key);
    }

    @Nullable
    public static <T> MouseRegistryEntry<T> create(Registry<T> registry, @Nullable ResourceLocation location) {
        Objects.requireNonNull(registry, "Registry cannot be null");
        if (location == null) return null;
        return registry.getOptional(location)
                .map(val -> new MouseRegistryEntry<>(registry, val, location))
                .orElse(null);
    }

    @Nullable
    public static <T> MouseRegistryEntry<T> create(Registry<T> registry, String prefix, String name) {
        ResourceLocation loc = ResourceLocation.tryBuild(prefix, name);
        return loc != null ? create(registry, loc) : null;
    }

    public Registry<T> getRegistry()      { return registry; }
    public T getValue()                   { return value; }
    public ResourceLocation getLocation() { return location; }
    public String getNamespace()          { return location.getNamespace(); }
    public String getPath()               { return location.getPath(); }

    public boolean is(TagKey<T> tag) {
        return registry.wrapAsHolder(value).is(tag);
    }

    public boolean isEqual(@Nullable MouseRegistryEntry<?> other) {
        return other != null && (other == this || (registry == other.registry && value == other.value));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode()    { return Objects.hash(registry.key(), location); }
    @Override public String toString() { return location.toString(); }

    @Override
    public int compareTo(MouseRegistryEntry<T> o) {
        return Comparator
                .<MouseRegistryEntry<T>, ResourceLocation>comparing(e -> e.getRegistry().key().location())
                .thenComparing(MouseRegistryEntry::getLocation)
                .compare(this, o);
    }
}