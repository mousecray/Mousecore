/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import ru.mousecray.mousecore.api.container.SoftHashMap;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.core.Mousecore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MouseEnchantmentState implements Comparable<MouseEnchantmentState> {
    private static final Map<Enchantment, MouseEnchantmentState> cache = new SoftHashMap<>();

    @Nonnull private final Enchantment enchantment;

    private ResourceLocation    registryKey    = null;
    private Enchantment.Rarity  rarity         = null;
    private EnchantmentCategory category       = null;
    private Integer             minLevel       = null;
    private Integer             maxLevel       = null;
    private Boolean             isTreasureOnly = null;
    private Boolean             isCurse        = null;
    private Boolean             isTradeable    = null;
    private Boolean             isDiscoverable = null;
    private String              descriptionId  = null;

    public MouseEnchantmentState(@Nonnull Enchantment enchantment) {
        this.enchantment = Objects.requireNonNull(enchantment);
    }

    @Nullable
    public static MouseEnchantmentState create(@Nullable Enchantment enchantment) {
        if (enchantment == null) return null;
        if (Mousecore.getInstance().getConfig().ENCHANTMENT_STATE_CACHE.getBooleanAnyway()) {
            return cache.computeIfAbsent(enchantment, MouseEnchantmentState::new);
        } else return new MouseEnchantmentState(enchantment);
    }

    @Nullable
    public static MouseEnchantmentState create(String prefix, String name) {
        ResourceLocation loc = ResourceLocation.tryBuild(prefix, name);
        return loc != null ? create(loc) : null;
    }

    @SuppressWarnings("deprecation") @Nullable
    public static MouseEnchantmentState create(@Nullable ResourceLocation location) {
        if (location == null) return null;
        return BuiltInRegistries.ENCHANTMENT.getOptional(location)
                .map(MouseEnchantmentState::create)
                .orElse(null);
    }

    public static void clearCache() {
        cache.clear();
    }

    @Nonnull public Enchantment getEnchantment() { return enchantment; }

    @SuppressWarnings("deprecation")
    public ResourceLocation getRegistryKey() {
        if (checkCacheDisabled() || registryKey == null) {
            ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            registryKey = Objects.requireNonNull(key, "Unregistered Enchantment: " + enchantment);
        }
        return registryKey;
    }

    public Enchantment.Rarity getRarity() {
        return checkCacheDisabled() || rarity == null
                ? (rarity = enchantment.getRarity())
                : rarity;
    }

    public EnchantmentCategory getCategory() {
        return checkCacheDisabled() || category == null
                ? (category = enchantment.category)
                : category;
    }

    public int getMinLevel() {
        return checkCacheDisabled() || minLevel == null
                ? (minLevel = enchantment.getMinLevel())
                : minLevel;
    }

    public int getMaxLevel() {
        return checkCacheDisabled() || maxLevel == null
                ? (maxLevel = enchantment.getMaxLevel())
                : maxLevel;
    }

    public boolean isTreasureOnly() {
        return checkCacheDisabled() || isTreasureOnly == null
                ? (isTreasureOnly = enchantment.isTreasureOnly())
                : isTreasureOnly;
    }

    public boolean isCurse() {
        return checkCacheDisabled() || isCurse == null
                ? (isCurse = enchantment.isCurse())
                : isCurse;
    }

    public boolean isTradeable() {
        return checkCacheDisabled() || isTradeable == null
                ? (isTradeable = enchantment.isTradeable())
                : isTradeable;
    }

    public boolean isDiscoverable() {
        return checkCacheDisabled() || isDiscoverable == null
                ? (isDiscoverable = enchantment.isDiscoverable())
                : isDiscoverable;
    }

    public String getDescriptionId() {
        return checkCacheDisabled() || descriptionId == null
                ? (descriptionId = enchantment.getDescriptionId())
                : descriptionId;
    }

    public boolean canEnchant(@Nullable ItemStack stack) {
        return stack != null && !stack.isEmpty() && enchantment.canEnchant(stack);
    }

    public boolean isCompatibleWith(@Nullable MouseEnchantmentState other) {
        return other != null && enchantment.isCompatibleWith(other.enchantment);
    }

    public EnchantmentInstance createInstance(int level) {
        return new EnchantmentInstance(enchantment, MouseNumbers.clamp(level, 1, Integer.MAX_VALUE));
    }

    public EnchantmentInstance createClampedInstance(int level) {
        return new EnchantmentInstance(enchantment, MouseNumbers.clamp(level, getMinLevel(), getMaxLevel()));
    }

    public boolean isEqual(@Nullable MouseEnchantmentState other) {
        return other != null && (other == this || enchantment == other.enchantment);
    }

    private static boolean checkCacheDisabled() {
        return !Mousecore.getInstance().getConfig().ENCHANTMENT_STATE_CACHE_DATA.getBooleanAnyway();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseEnchantmentState that)) return false;
        return Objects.equals(enchantment, that.enchantment);
    }

    @Override public int hashCode()    { return Objects.hash(enchantment); }
    @Override public String toString() { return getRegistryKey().toString(); }

    @Override
    public int compareTo(@Nonnull MouseEnchantmentState o) {
        return Comparator
                .comparing(MouseEnchantmentState::getRegistryKey)
                .compare(this, o);
    }
}