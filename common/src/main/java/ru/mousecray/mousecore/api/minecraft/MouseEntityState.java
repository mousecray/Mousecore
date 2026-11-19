/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import ru.mousecray.mousecore.api.container.SoftHashMap;
import ru.mousecray.mousecore.core.Mousecore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MouseEntityState implements Comparable<MouseEntityState> {
    private static final Map<EntityType<?>, MouseEntityState> cache = new SoftHashMap<>();

    @Nonnull private final EntityType<?> entityType;

    private ResourceLocation registryKey           = null;
    private MobCategory      category              = null;
    private Boolean          isFriendly            = null;
    private Boolean          isPersistent          = null;
    private Boolean          fireImmune            = null;
    private Boolean          canSummon             = null;
    private Boolean          canSerialize          = null;
    private Boolean          canSpawnFarFromPlayer = null;
    private EntityDimensions dimensions            = null;
    private Integer          clientTrackingRange   = null;
    private Integer          updateInterval        = null;
    private String           descriptionId         = null;

    public MouseEntityState(@Nonnull EntityType<?> entityType) {
        this.entityType = Objects.requireNonNull(entityType);
    }

    @Nullable
    public static MouseEntityState create(@Nullable EntityType<?> entityType) {
        if (entityType == null) return null;
        if (Mousecore.getInstance().getConfig().ENTITY_STATE_CACHE.getBooleanAnyway()) {
            return cache.computeIfAbsent(entityType, MouseEntityState::new);
        } else return new MouseEntityState(entityType);
    }

    @Nullable
    public static MouseEntityState create(String prefix, String name) {
        ResourceLocation loc = ResourceLocation.tryBuild(prefix, name);
        return loc != null ? create(loc) : null;
    }

    @SuppressWarnings("deprecation") @Nullable
    public static MouseEntityState create(@Nullable ResourceLocation location) {
        if (location == null) return null;
        return BuiltInRegistries.ENTITY_TYPE.getOptional(location)
                .map(MouseEntityState::create)
                .orElse(null);
    }

    public static void clearCache()               { cache.clear(); }

    @Nonnull public EntityType<?> getEntityType() { return entityType; }

    @SuppressWarnings("deprecation")
    public ResourceLocation getRegistryKey() {
        return checkCacheDisabled() || registryKey == null
                ? (registryKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType))
                : registryKey;
    }

    public MobCategory getCategory() {
        return checkCacheDisabled() || category == null
                ? (category = entityType.getCategory())
                : category;
    }

    public boolean isFriendly() {
        return checkCacheDisabled() || isFriendly == null
                ? (isFriendly = getCategory().isFriendly())
                : isFriendly;
    }

    public boolean isPersistent() {
        return checkCacheDisabled() || isPersistent == null
                ? (isPersistent = getCategory().isPersistent())
                : isPersistent;
    }

    public boolean isMonster() {
        return getCategory() == MobCategory.MONSTER;
    }

    public boolean isFireImmune() {
        return checkCacheDisabled() || fireImmune == null
                ? (fireImmune = entityType.fireImmune())
                : fireImmune;
    }

    public boolean canSummon() {
        return checkCacheDisabled() || canSummon == null
                ? (canSummon = entityType.canSummon())
                : canSummon;
    }

    public boolean canSerialize() {
        return checkCacheDisabled() || canSerialize == null
                ? (canSerialize = entityType.canSerialize())
                : canSerialize;
    }

    public boolean canSpawnFarFromPlayer() {
        return checkCacheDisabled() || canSpawnFarFromPlayer == null
                ? (canSpawnFarFromPlayer = entityType.canSpawnFarFromPlayer())
                : canSpawnFarFromPlayer;
    }

    public EntityDimensions getDimensions() {
        return checkCacheDisabled() || dimensions == null
                ? (dimensions = entityType.getDimensions())
                : dimensions;
    }

    public float getWidth()  { return getDimensions().width; }
    public float getHeight() { return getDimensions().height; }

    public int getClientTrackingRange() {
        return checkCacheDisabled() || clientTrackingRange == null
                ? (clientTrackingRange = entityType.clientTrackingRange())
                : clientTrackingRange;
    }

    public int getUpdateInterval() {
        return checkCacheDisabled() || updateInterval == null
                ? (updateInterval = entityType.updateInterval())
                : updateInterval;
    }

    public String getDescriptionId() {
        return checkCacheDisabled() || descriptionId == null
                ? (descriptionId = entityType.getDescriptionId())
                : descriptionId;
    }

    @SuppressWarnings("deprecation")
    public boolean is(TagKey<EntityType<?>> tag) {
        return entityType.builtInRegistryHolder().is(tag);
    }

    public boolean isEqual(@Nullable MouseEntityState other) {
        return other != null && (other == this || entityType == other.entityType);
    }

    private static boolean checkCacheDisabled() {
        return !Mousecore.getInstance().getConfig().ENTITY_STATE_CACHE_DATA.getBooleanAnyway();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseEntityState that)) return false;
        return Objects.equals(entityType, that.entityType);
    }

    @Override public int hashCode()    { return Objects.hash(entityType); }
    @Override public String toString() { return getRegistryKey().toString(); }

    @Override
    public int compareTo(@Nonnull MouseEntityState o) {
        return Comparator
                .comparing(MouseEntityState::getRegistryKey)
                .compare(this, o);
    }
}