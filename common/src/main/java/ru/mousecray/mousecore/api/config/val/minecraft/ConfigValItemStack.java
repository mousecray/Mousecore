/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.api.config.val.minecraft;

import net.minecraft.world.item.ItemStack;
import ru.mousecray.mousecore.api.config.val.base.ConfigOtherVal;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemStackType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.api.minecraft.MouseItemStack;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import java.util.Random;

public interface ConfigValItemStack extends ConfigOtherVal<MinecraftItemStackType> {
    MouseItemStack getMouseItemStack();
    MouseItemState getItemState();
    MinecraftItemType getItem();
    RandomQuantityType getQuantity();
    NBTType getNbt();
    boolean hasNbt();

    ItemStack roll(Random random, QuantityDistribution distribution);
    ItemStack roll(Random random);
    ItemStack roll(QuantityDistribution distribution);
    ItemStack roll();

    void setItemStack(MouseItemStack stack);
    void setItemStack(MinecraftItemType item, RandomQuantityType quantity, NBTType nbt);
    void setItemStack(MinecraftItemType item, int count);
}