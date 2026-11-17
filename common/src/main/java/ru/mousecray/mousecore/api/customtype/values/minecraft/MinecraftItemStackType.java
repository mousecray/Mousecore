/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.world.item.ItemStack;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.QuantityDistribution;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.number.PercentType;
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftItemStackType extends CustomType<MinecraftItemStackType> {
    private static final MouseUtils.Equator<MinecraftItemStackType> EQUATOR = MouseUtils.Equator
            .<MinecraftItemStackType>equaling(MinecraftItemStackType::getItem)
            .thenEqualing(MinecraftItemStackType::getQuantity)
            .thenEqualing(MinecraftItemStackType::getNbt);

    public static final MinecraftItemStackType EMPTY = new MinecraftItemStackType(
            MinecraftItemType.AIR,
            RandomQuantityType.create(PercentType.MIN, IntegralType.NULL, IntegralType.NULL),
            NBTType.EMPTY
    );

    static { storage.put(MinecraftItemStackType.class, MinecraftItemStackType::parse); }

    private final MinecraftItemType  item;
    private final RandomQuantityType quantity;
    private final NBTType            nbt;

    private MinecraftItemStackType(MinecraftItemType item, RandomQuantityType quantity, NBTType nbt) {
        super();
        this.item = Objects.requireNonNull(item, "MinecraftItemType cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "RandomQuantityType cannot be null");
        this.nbt = Objects.requireNonNull(nbt, "NbtType cannot be null");
    }

    public static MinecraftItemStackType create(MinecraftItemType item, RandomQuantityType quantity, NBTType nbt) {
        return new MinecraftItemStackType(item, quantity, nbt);
    }

    public static MinecraftItemStackType create(MinecraftItemType item, RandomQuantityType quantity) {
        return create(item, quantity, NBTType.EMPTY);
    }

    public static MinecraftItemStackType create(MinecraftItemType item, long count) {
        long safeCount = Math.max(0L, count);
        return create(item, RandomQuantityType.create(
                PercentType.MAX,
                IntegralType.create(safeCount),
                IntegralType.create(safeCount)
        ), NBTType.EMPTY);
    }

    public static MinecraftItemStackType create(MinecraftItemType item) {
        return create(item, 1L);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, RandomQuantityType quantity, NBTType nbt) {
        return create(MinecraftItemType.create(block.getPrefix(), block.getName()), quantity, nbt);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, RandomQuantityType quantity) {
        return create(block, quantity, NBTType.EMPTY);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, long count) {
        return create(MinecraftItemType.create(block.getPrefix(), block.getName()), count);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block) {
        return create(block, 1L);
    }

    public MinecraftItemType getItem()                                         { return item; }
    public RandomQuantityType getQuantity()                                    { return quantity; }
    public NBTType getNbt()                                                    { return nbt; }

    public MouseItemState asItemState()                                        { return item.asItemState(); }
    public boolean hasNbt()                                                    { return !nbt.isEmpty(); }

    public MinecraftItemStackType withItem(MinecraftItemType newItem)          { return create(newItem, quantity, nbt); }
    public MinecraftItemStackType withQuantity(RandomQuantityType newQuantity) { return create(item, newQuantity, nbt); }
    public MinecraftItemStackType withNbt(NBTType newNbt)                      { return create(item, quantity, newNbt); }

    public ItemStack roll(Random random, QuantityDistribution distribution) {
        int count = quantity.roll(random, distribution);
        if (count <= 0) return ItemStack.EMPTY;

        ItemStack stack = item.asItemState().getItemStack(count);
        if (!nbt.isEmpty()) stack.setTag(nbt.asCompoundTag());
        return stack;
    }

    public ItemStack roll(Random random)                     { return roll(random, QuantityDistribution.CHANCE_OR_ZERO); }
    public ItemStack roll(QuantityDistribution distribution) { return roll(ThreadLocalRandom.current(), distribution); }
    public ItemStack roll()                                  { return roll(ThreadLocalRandom.current(), QuantityDistribution.CHANCE_OR_ZERO); }

    public static MinecraftItemStackType parse(@Nullable String raw) throws ValueFormatException {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) return EMPTY;

        NBTType parsedNbt = NBTType.EMPTY;
        int     nbtStart  = clean.indexOf('{');
        int     nbtEnd    = clean.lastIndexOf('}');
        if (nbtStart >= 0 && nbtEnd > nbtStart) {
            String nbtStr = clean.substring(nbtStart, nbtEnd + 1);
            parsedNbt = NBTType.parse(nbtStr);
            clean = MouseStrings.trimWith(clean.substring(0, nbtStart), true, '\t');
            if (clean == null || clean.isEmpty()) throw new ValueFormatException("Item identifier is missing before NBT in: '" + raw + "'");
        }

        RandomQuantityType parsedQuantity;
        int                starIdx = clean.indexOf('*');
        String             itemStr;
        if (starIdx >= 0) {
            itemStr = MouseStrings.trimWith(clean.substring(0, starIdx), true, '\t');
            String qtyStr = MouseStrings.trimWith(clean.substring(starIdx + 1), true, '\t');
            if (qtyStr == null || qtyStr.isEmpty()) throw new ValueFormatException("Missing quantity after '*' in: '" + raw + "'");

            Long simpleCount = MouseNumbers.tryParseLong(qtyStr);
            if (simpleCount != null) {
                parsedQuantity = RandomQuantityType.create(
                        PercentType.MAX,
                        IntegralType.create(simpleCount),
                        IntegralType.create(simpleCount)
                );
            } else parsedQuantity = CustomType.parse(RandomQuantityType.class, qtyStr);
            if (parsedQuantity == null) parsedQuantity = RandomQuantityType.create(PercentType.MAX, IntegralType.create(1), IntegralType.create(1));
        } else {
            itemStr = clean;
            parsedQuantity = RandomQuantityType.create(PercentType.MAX, IntegralType.create(1L), IntegralType.create(1L));
        }

        MinecraftItemType parsedItem = MinecraftItemType.parse((String) null, itemStr);
        return create(parsedItem, parsedQuantity, parsedNbt);
    }

    public static MinecraftItemStackType fromString(@Nullable String raw) { return parse(raw); }

    @Override public IValType getValType()                                { return ValType.MINECRAFT_ITEM_STACK; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(item.toString());

        boolean isFixedOne = quantity.getMin().asLong() == 1L
                && quantity.getMax().asLong() == 1L
                && quantity.getChance().asDouble() >= 100.0D;

        if (!isFixedOne) {
            boolean isSimpleCount = quantity.getMin().asLong() == quantity.getMax().asLong()
                    && quantity.getChance().asDouble() >= 100.0D;
            sb.append('*').append(isSimpleCount ? quantity.getMin().asLong() : quantity.toString());
        }

        if (!nbt.isEmpty()) sb.append(nbt);

        return sb.toString();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(item, quantity, nbt); }

    @Override
    public int compareTo(MinecraftItemStackType o) {
        return Comparator
                .comparing(MinecraftItemStackType::getItem)
                .thenComparing(MinecraftItemStackType::getQuantity)
                .thenComparing(MinecraftItemStackType::getNbt)
                .compare(this, o);
    }
}