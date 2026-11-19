/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MouseResourceKey<T> implements Comparable<MouseResourceKey<T>> {
    private static final MouseUtils.Equator<MouseResourceKey<?>> EQUATOR = MouseUtils.Equator
            .<MouseResourceKey<?>>equaling(MouseResourceKey::getKey);

    private final ResourceKey<T> key;

    public MouseResourceKey(ResourceKey<T> key) {
        this.key = Objects.requireNonNull(key, "ResourceKey cannot be null");
    }

    public static <T> MouseResourceKey<T> create(ResourceKey<T> key) {
        return new MouseResourceKey<>(key);
    }

    public static <T> MouseResourceKey<T> create(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
        Objects.requireNonNull(registry, "Registry key cannot be null");
        Objects.requireNonNull(location, "ResourceLocation cannot be null");
        return new MouseResourceKey<>(ResourceKey.create(registry, location));
    }

    public static <T> MouseResourceKey<T> create(ResourceKey<? extends Registry<T>> registry, String namespace, String path) {
        ResourceLocation location = ResourceLocation.tryBuild(namespace, path);
        if (location == null) {
            throw new ValueFormatException("Invalid ResourceLocation: " + namespace + ":" + path);
        }
        return create(registry, location);
    }

    public ResourceKey<T> getKey()                                    { return key; }
    public ResourceLocation getLocation()                             { return key.location(); }
    public ResourceLocation getRegistry()                             { return key.registry(); }
    public String getNamespace()                                      { return key.location().getNamespace(); }
    public String getPath()                                           { return key.location().getPath(); }
    public boolean isFor(ResourceKey<? extends Registry<?>> registry) { return key.isFor(registry); }

    public boolean isEqual(@Nullable MouseResourceKey<?> other) {
        return other != null && (other == this || Objects.equals(key, other.key));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode()    { return Objects.hash(key); }
    @Override public String toString() { return key.location().toString(); }

    @Override
    public int compareTo(MouseResourceKey<T> o) {
        return Comparator
                .<MouseResourceKey<T>, ResourceLocation>comparing(MouseResourceKey::getRegistry)
                .thenComparing(MouseResourceKey::getLocation)
                .compare(this, o);
    }
}