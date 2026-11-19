/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseUtils;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MouseItemStack implements Comparable<MouseItemStack> {
    private static final MouseUtils.Equator<MouseItemStack> EQUATOR = MouseUtils.Equator
            .<MouseItemStack>equaling(MouseItemStack::getItemState)
            .thenEqualing(MouseItemStack::getMinCount)
            .thenEqualing(MouseItemStack::getMaxCount)
            .thenEqualing(MouseItemStack::getChance)
            .thenEqualing(MouseItemStack::getRawNbt);

    public static final MouseItemStack EMPTY = new MouseItemStack(new MouseItemState(Items.AIR), 0, 0, 0.0D, null);

    private final           MouseItemState itemState;
    private final           int            minCount;
    private final           int            maxCount;
    private final           double         chance;
    @Nullable private final CompoundTag    nbt;

    private MouseItemStack(MouseItemState itemState, int minCount, int maxCount, double chance, @Nullable CompoundTag nbt) {
        this.itemState = Objects.requireNonNull(itemState, "MouseItemState cannot be null");
        int clampedMin = MouseNumbers.clamp(minCount, 0, Integer.MAX_VALUE);
        int clampedMax = MouseNumbers.clamp(maxCount, 0, Integer.MAX_VALUE);
        this.minCount = Math.min(clampedMin, clampedMax);
        this.maxCount = Math.max(clampedMin, clampedMax);
        this.chance = MouseNumbers.clamp(chance, 0.0D, 100.0D);
        this.nbt = (nbt != null && !nbt.isEmpty()) ? nbt.copy() : null;
    }

    public static MouseItemStack create(MouseItemState itemState, int minCount, int maxCount, double chance, @Nullable CompoundTag nbt) {
        return new MouseItemStack(itemState, minCount, maxCount, chance, nbt);
    }

    public static MouseItemStack create(MouseItemState itemState, int minCount, int maxCount, double chance) {
        return create(itemState, minCount, maxCount, chance, null);
    }

    public static MouseItemStack create(MouseItemState itemState, int count, @Nullable CompoundTag nbt) {
        return create(itemState, count, count, 100.0D, nbt);
    }

    public static MouseItemStack create(MouseItemState itemState, int count) {
        return create(itemState, count, count, 100.0D, null);
    }

    public static MouseItemStack create(MouseItemState itemState) {
        return create(itemState, 1);
    }

    public static MouseItemStack create(ItemStack vanillaStack) {
        Objects.requireNonNull(vanillaStack, "ItemStack cannot be null");
        if (vanillaStack.isEmpty()) return EMPTY;
        MouseItemState state = Objects.requireNonNull(MouseItemState.create(vanillaStack.getItem()));
        return create(state, vanillaStack.getCount(), vanillaStack.getTag());
    }

    public MouseItemState getItemState()     { return itemState; }
    public int getMinCount()                 { return minCount; }
    public int getMaxCount()                 { return maxCount; }
    public double getChance()                { return chance; }
    public boolean hasNbt()                  { return nbt != null; }

    @Nullable public CompoundTag getNbt()    { return nbt != null ? nbt.copy() : null; }
    @Nullable public CompoundTag getRawNbt() { return nbt; }

    public MouseItemStack withItemState(MouseItemState newItemState) {
        return create(newItemState, minCount, maxCount, chance, nbt);
    }

    public MouseItemStack withQuantity(int newMin, int newMax, double newChance) {
        return create(itemState, newMin, newMax, newChance, nbt);
    }

    public MouseItemStack withNbt(@Nullable CompoundTag newNbt) {
        return create(itemState, minCount, maxCount, chance, newNbt);
    }

    public ItemStack roll(Random random, QuantityDistribution distribution) {
        int count = distribution.rollInt(random, minCount, maxCount, chance);
        if (count <= 0) return ItemStack.EMPTY;

        ItemStack stack = itemState.getItemStack(count);
        if (nbt != null) stack.setTag(nbt.copy());
        return stack;
    }

    public ItemStack roll(Random random) {
        return roll(random, QuantityDistribution.CHANCE_OR_ZERO);
    }

    public ItemStack roll(QuantityDistribution distribution) {
        return roll(ThreadLocalRandom.current(), distribution);
    }

    public ItemStack roll() {
        return roll(ThreadLocalRandom.current(), QuantityDistribution.CHANCE_OR_ZERO);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(itemState, minCount, maxCount, chance, nbt); }

    @Override
    public int compareTo(MouseItemStack o) {
        return Comparator
                .comparing(MouseItemStack::getItemState)
                .thenComparingDouble(MouseItemStack::getChance)
                .thenComparingInt(MouseItemStack::getMinCount)
                .thenComparingInt(MouseItemStack::getMaxCount)
                .thenComparing(s -> s.nbt == null ? "" : s.nbt.toString())
                .compare(this, o);
    }

    @Override
    public String toString() {
        StringBuilder sb         = new StringBuilder(itemState.toString());
        boolean       isFixedOne = minCount == 1 && maxCount == 1 && chance >= 100.0D;
        if (!isFixedOne) {
            sb.append('*');
            if (minCount == maxCount && chance >= 100.0D) sb.append(minCount);
            else {
                sb.append(minCount).append('-').append(maxCount)
                        .append(':').append(MouseNumbers.formatDecimal(chance, false, true)).append('%');
            }
        }
        if (nbt != null) sb.append(nbt);
        return sb.toString();
    }
}