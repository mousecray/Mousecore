/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.world.item.ItemStack;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.customtype.values.number.IntegralType;
import ru.mousecray.mousecore.api.customtype.values.number.PercentType;
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseItemStack;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftItemStackType extends OtherType<MouseItemStack> {
    public static final MinecraftItemStackType EMPTY = new MinecraftItemStackType(MouseItemStack.EMPTY);

    static { storage.put(MinecraftItemStackType.class, MinecraftItemStackType::parse); }

    private MinecraftItemStackType(MouseItemStack stack) {
        super(Objects.requireNonNull(stack, "MouseItemStack cannot be null"));
    }

    public static MinecraftItemStackType create(MouseItemStack stack) {
        return new MinecraftItemStackType(stack);
    }

    public static MinecraftItemStackType create(ItemStack vanillaStack) {
        return create(MouseItemStack.create(vanillaStack));
    }

    public static MinecraftItemStackType create(MinecraftItemType item, RandomQuantityType quantity, NBTType nbt) {
        return create(MouseItemStack.create(
                item.asItemState(),
                quantity.getMin().asInt(),
                quantity.getMax().asInt(),
                quantity.getChance().asDouble(),
                nbt.isEmpty() ? null : nbt.getRawCompoundTag()
        ));
    }

    public static MinecraftItemStackType create(MinecraftItemType item, RandomQuantityType quantity) {
        return create(item, quantity, NBTType.EMPTY);
    }

    public static MinecraftItemStackType create(MinecraftItemType item, int count) {
        return create(MouseItemStack.create(item.asItemState(), count));
    }

    public static MinecraftItemStackType create(MinecraftItemType item) {
        return create(item, 1);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, RandomQuantityType quantity, NBTType nbt) {
        return create(MinecraftItemType.create(block.getPrefix(), block.getName()), quantity, nbt);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, RandomQuantityType quantity) {
        return create(block, quantity, NBTType.EMPTY);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block, int count) {
        return create(MinecraftItemType.create(block.getPrefix(), block.getName()), count);
    }

    public static MinecraftItemStackType create(MinecraftBlockType block) {
        return create(block, 1);
    }

    public MouseItemStack asMouseItemStack() { return value; }
    public MouseItemState asItemState()      { return value.getItemState(); }
    public boolean hasNbt()                  { return value.hasNbt(); }

    public MinecraftItemType getItem() {
        return MinecraftItemType.create(value.getItemState());
    }

    public RandomQuantityType getQuantity() {
        return RandomQuantityType.create(
                PercentType.create(value.getChance()),
                IntegralType.create(value.getMinCount()),
                IntegralType.create(value.getMaxCount())
        );
    }

    public NBTType getNbt() {
        return value.hasNbt()
                ? NBTType.create(Objects.requireNonNull(value.getRawNbt()))
                : NBTType.EMPTY;
    }

    public MinecraftItemStackType withItem(MinecraftItemType newItem) {
        return create(value.withItemState(newItem.asItemState()));
    }

    public MinecraftItemStackType withQuantity(RandomQuantityType newQuantity) {
        return create(value.withQuantity(
                newQuantity.getMin().asInt(),
                newQuantity.getMax().asInt(),
                newQuantity.getChance().asDouble()
        ));
    }

    public MinecraftItemStackType withNbt(NBTType newNbt) {
        return create(value.withNbt(newNbt.isEmpty() ? null : newNbt.getRawCompoundTag()));
    }

    public ItemStack roll(Random random, QuantityDistribution distribution) { return value.roll(random, distribution); }
    public ItemStack roll(Random random)                                    { return value.roll(random); }
    public ItemStack roll(QuantityDistribution distribution)                { return value.roll(distribution); }
    public ItemStack roll()                                                 { return value.roll(); }

    public static MinecraftItemStackType parse(@Nullable Object val) throws UnsupportedValException, ValueFormatException {
        if (val instanceof MinecraftItemStackType stackType) return stackType;
        else if (val instanceof MouseItemStack mouseStack) return create(mouseStack);
        else if (val instanceof ItemStack vanillaStack) return create(vanillaStack);
        else if (val instanceof String str) return parse(str);
        throw new UnsupportedValException("Cannot parse MinecraftItemStackType from " + val);
    }

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

            Integer simpleCount = MouseNumbers.tryParseInt(qtyStr);
            if (simpleCount != null) {
                parsedQuantity = RandomQuantityType.create(
                        PercentType.MAX,
                        IntegralType.create(simpleCount),
                        IntegralType.create(simpleCount)
                );
            } else {
                parsedQuantity = CustomType.parse(RandomQuantityType.class, qtyStr);
                if (parsedQuantity == null) throw new ValueFormatException("Invalid quantity format in: '" + raw + "'");
            }
        } else {
            itemStr = clean;
            parsedQuantity = RandomQuantityType.create(PercentType.MAX, IntegralType.create(1L), IntegralType.create(1L));
        }

        MinecraftItemType parsedItem = MinecraftItemType.parse((String) null, itemStr);
        return create(parsedItem, parsedQuantity, parsedNbt);
    }

    public static MinecraftItemStackType fromString(@Nullable String raw)                           { return parse(raw); }
    @Override public IValType getValType()                                                          { return ValType.MINECRAFT_ITEM_STACK; }
    @Override @SuppressWarnings("unchecked") public MinecraftItemStackType createType(Object value) { return parse(value); }
}