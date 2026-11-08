/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import ru.mousecray.mousecore.api.container.SoftHashMap;
import ru.mousecray.mousecore.core.Mousecore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MouseItemState implements Comparable<MouseItemState> {
    private static final Map<Item, MouseItemState> cache = new SoftHashMap<>();
    @Nonnull private final Item item;

    private Boolean    hasBlock        = null;
    private Block      block           = null;
    private BlockState defaultState    = null;
    private Integer    maxStackSize    = null;
    private Integer    maxDamage       = null;
    private Boolean    isDamageable    = null;
    private Boolean    isEdible        = null;
    private Boolean    isFireResistant = null;
    private String     descriptionId   = null;

    public MouseItemState(@Nonnull Item item) { this.item = Objects.requireNonNull(item); }

    public static MouseItemState create(Item item) {
        if (item == null) return null;
        if (Mousecore.getInstance().getConfig().ITEM_STATE_CACHE.getBooleanAnyway()) {
            return cache.computeIfAbsent(item, MouseItemState::new);
        } else return new MouseItemState(item);
    }

    @SuppressWarnings("deprecation")
    public static MouseItemState create(String prefix, String name) {
        return create(BuiltInRegistries.ITEM.get(ResourceLocation.tryBuild(prefix, name)));
    }

    @SuppressWarnings("deprecation")
    public static MouseItemState create(ResourceLocation location) { return create(BuiltInRegistries.ITEM.get(location)); }

    @Nonnull public Item getItem() { return item; }

    public boolean hasBlock() {
        return checkCacheDisabled() || hasBlock == null
                ? (hasBlock = (item instanceof BlockItem))
                : hasBlock;
    }

    @Nullable
    public Block getBlock() {
        return checkCacheDisabled() || block == null
                ? (block = (item instanceof BlockItem blockItem ? blockItem.getBlock() : null))
                : block;
    }

    @Nullable
    public BlockState getDefaultBlockState() {
        if (checkCacheDisabled()) {
            Block b = getBlock();
            return (b != null && b != Blocks.AIR) ? b.defaultBlockState() : null;
        }
        if (defaultState == null) {
            Block b = getBlock();
            defaultState = (b != null && b != Blocks.AIR) ? b.defaultBlockState() : null;
        }
        return defaultState;
    }

    @SuppressWarnings("deprecation")
    public int getMaxStackSize(@Nullable ItemStack stack) {
        if (stack != null) return stack.getMaxStackSize();
        return checkCacheDisabled() || maxStackSize == null
                ? (maxStackSize = item.getMaxStackSize())
                : maxStackSize;
    }

    public int getMaxStackSize() { return getMaxStackSize(null); }

    @SuppressWarnings("deprecation")
    public int getMaxDamage(@Nullable ItemStack stack) {
        if (stack != null) return stack.getMaxDamage();
        return checkCacheDisabled() || maxDamage == null
                ? (maxDamage = item.getMaxDamage())
                : maxDamage;
    }

    public int getMaxDamage() { return getMaxDamage(null); }

    public boolean isDamageable() {
        return checkCacheDisabled() || isDamageable == null
                ? (isDamageable = item.canBeDepleted())
                : isDamageable;
    }

    public boolean isEdible() {
        return checkCacheDisabled() || isEdible == null
                ? (isEdible = item.isEdible())
                : isEdible;
    }

    public boolean isFireResistant() {
        return checkCacheDisabled() || isFireResistant == null
                ? (isFireResistant = item.isFireResistant())
                : isFireResistant;
    }

    public String getDescriptionId() {
        return checkCacheDisabled() || descriptionId == null
                ? (descriptionId = item.getDescriptionId())
                : descriptionId;
    }

    public Rarity getRarity()                    { return item.getRarity(ItemStack.EMPTY); }
    public ItemStack getItemStack(int stackSize) { return new ItemStack(item, Math.min(stackSize, getMaxStackSize())); }
    public ItemStack getItemStack()              { return new ItemStack(item, 1); }
    public boolean isEqual(MouseItemState other) { return other != null && (other == this || item == other.item); }
    private static boolean checkCacheDisabled()  { return !Mousecore.getInstance().getConfig().ITEM_STATE_CACHE_DATA.getBooleanAnyway(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseItemState that)) return false;
        return Objects.equals(item, that.item);
    }

    @Override public int hashCode()                                     { return Objects.hash(item); }
    @SuppressWarnings("deprecation") @Override public String toString() { return BuiltInRegistries.ITEM.getKey(item).toString(); }

    @SuppressWarnings("deprecation") @Override
    public int compareTo(@Nonnull MouseItemState o) {
        return Comparator
                .comparing((MouseItemState t) -> BuiltInRegistries.ITEM.getKey(t.getItem()))
                .compare(this, o);
    }
}