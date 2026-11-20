/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.val.minecraft;

import net.minecraft.world.item.ItemStack;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.val.minecraft.ConfigValItemStack;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.values.collection.NBTType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemStackType;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftItemType;
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.api.minecraft.MouseItemStack;
import ru.mousecray.mousecore.api.minecraft.MouseItemState;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;
import ru.mousecray.mousecore.core.config.val.base.ConfigValMinecraftImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@FieldsAreNonnullByDefault
@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public class ConfigValItemStackImpl extends ConfigValMinecraftImpl<MinecraftItemStackType> implements ConfigValItemStack {
    @SafeVarargs
    public ConfigValItemStackImpl(
            MinecraftItemStackType defaultValue, MinecraftItemStackType disabledValue,
            @Nullable String specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<MinecraftItemStackType>... configureValues
    ) {
        super(MinecraftItemStackType.class, defaultValue, disabledValue, specificDataType, constraints, configureValues);
    }

    @Override public MouseItemStack getMouseItemStack()                               { return getValue().asMouseItemStack(); }
    @Override public MouseItemState getItemState()                                    { return getValue().asItemState(); }
    @Override public MinecraftItemType getItem()                                      { return getValue().getItem(); }
    @Override public RandomQuantityType getQuantity()                                 { return getValue().getQuantity(); }
    @Override public NBTType getNbt()                                                 { return getValue().getNbt(); }
    @Override public boolean hasNbt()                                                 { return getValue().hasNbt(); }

    @Override public ItemStack roll(Random random, QuantityDistribution distribution) { return getValue().roll(random, distribution); }
    @Override public ItemStack roll(Random random)                                    { return getValue().roll(random); }
    @Override public ItemStack roll(QuantityDistribution distribution)                { return getValue().roll(distribution); }
    @Override public ItemStack roll()                                                 { return getValue().roll(); }

    @Override public void setItemStack(MouseItemStack stack)                          { setValue(MinecraftItemStackType.create(stack)); }
    @Override
    public void setItemStack(MinecraftItemType item, RandomQuantityType quantity, NBTType nbt) { setValue(MinecraftItemStackType.create(item, quantity, nbt)); }
    @Override
    public void setItemStack(MinecraftItemType item, int count) { setValue(MinecraftItemStackType.create(item, count)); }
}