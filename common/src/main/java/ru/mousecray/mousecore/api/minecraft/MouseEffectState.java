/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import ru.mousecray.mousecore.api.container.SoftHashMap;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.core.Mousecore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MouseEffectState implements Comparable<MouseEffectState> {
    private static final Map<MobEffect, MouseEffectState> cache = new SoftHashMap<>();

    @Nonnull private final MobEffect effect;

    private ResourceLocation  registryKey    = null;
    private MobEffectCategory category       = null;
    private Boolean           isBeneficial   = null;
    private Boolean           isInstantenous = null;
    private Integer           color          = null;
    private String            descriptionId  = null;

    public MouseEffectState(@Nonnull MobEffect effect) {
        this.effect = Objects.requireNonNull(effect);
    }

    @Nullable
    public static MouseEffectState create(@Nullable MobEffect effect) {
        if (effect == null) return null;
        if (Mousecore.getInstance().getConfig().EFFECT_STATE_CACHE.getBooleanAnyway()) {
            return cache.computeIfAbsent(effect, MouseEffectState::new);
        } else return new MouseEffectState(effect);
    }

    @Nullable
    public static MouseEffectState create(String prefix, String name) {
        ResourceLocation loc = ResourceLocation.tryBuild(prefix, name);
        return loc != null ? create(loc) : null;
    }

    @SuppressWarnings("deprecation") @Nullable
    public static MouseEffectState create(@Nullable ResourceLocation location) {
        if (location == null) return null;
        return BuiltInRegistries.MOB_EFFECT.getOptional(location)
                .map(MouseEffectState::create)
                .orElse(null);
    }

    public static void clearCache() {
        cache.clear();
    }

    @Nonnull public MobEffect getEffect() { return effect; }

    @SuppressWarnings("deprecation")
    public ResourceLocation getRegistryKey() {
        if (checkCacheDisabled() || registryKey == null) {
            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            registryKey = Objects.requireNonNull(key, "Unregistered MobEffect: " + effect);
        }
        return registryKey;
    }

    public MobEffectCategory getCategory() {
        return checkCacheDisabled() || category == null
                ? (category = effect.getCategory())
                : category;
    }

    public boolean isBeneficial() {
        return checkCacheDisabled() || isBeneficial == null
                ? (isBeneficial = effect.isBeneficial())
                : isBeneficial;
    }

    public boolean isHarmful() {
        return getCategory() == MobEffectCategory.HARMFUL;
    }

    public boolean isNeutral() {
        return getCategory() == MobEffectCategory.NEUTRAL;
    }

    public boolean isInstantenous() {
        return checkCacheDisabled() || isInstantenous == null
                ? (isInstantenous = effect.isInstantenous())
                : isInstantenous;
    }

    public int getColor() {
        return checkCacheDisabled() || color == null
                ? (color = effect.getColor())
                : color;
    }

    public String getDescriptionId() {
        return checkCacheDisabled() || descriptionId == null
                ? (descriptionId = effect.getDescriptionId())
                : descriptionId;
    }

    public MobEffectInstance createInstance(int durationTicks, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        int safeDuration  = MouseNumbers.clamp(durationTicks, 0, Integer.MAX_VALUE);
        int safeAmplifier = MouseNumbers.clamp(amplifier, 0, 255);
        return new MobEffectInstance(effect, safeDuration, safeAmplifier, ambient, visible, showIcon);
    }

    public MobEffectInstance createInstance(int durationTicks, int amplifier) {
        return createInstance(durationTicks, amplifier, false, true, true);
    }

    public MobEffectInstance createInstance(int durationTicks) {
        return createInstance(durationTicks, 0);
    }

    public boolean isEqual(@Nullable MouseEffectState other) {
        return other != null && (other == this || effect == other.effect);
    }

    private static boolean checkCacheDisabled() {
        return !Mousecore.getInstance().getConfig().EFFECT_STATE_CACHE_DATA.getBooleanAnyway();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseEffectState that)) return false;
        return Objects.equals(effect, that.effect);
    }

    @Override public int hashCode()    { return Objects.hash(effect); }
    @Override public String toString() { return getRegistryKey().toString(); }

    @Override
    public int compareTo(@Nonnull MouseEffectState o) {
        return Comparator
                .comparing(MouseEffectState::getRegistryKey)
                .compare(this, o);
    }
}