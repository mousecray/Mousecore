/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.world.item.enchantment.Enchantments;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseEnchantmentState;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftEnchantmentType extends OtherType<MouseEnchantmentState> {
    public static final MinecraftEnchantmentType SHARPNESS             = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SHARPNESS));
    public static final MinecraftEnchantmentType ALL_DAMAGE_PROTECTION = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.ALL_DAMAGE_PROTECTION));
    public static final MinecraftEnchantmentType BLAST_PROTECTION      = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.BLAST_PROTECTION));
    public static final MinecraftEnchantmentType AQUA_AFFINITY         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.AQUA_AFFINITY));
    public static final MinecraftEnchantmentType FALL_PROTECTION       = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FALL_PROTECTION));
    public static final MinecraftEnchantmentType BANE_OF_ARTHROPODS    = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.BANE_OF_ARTHROPODS));
    public static final MinecraftEnchantmentType BINDING_CURSE         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.BINDING_CURSE));
    public static final MinecraftEnchantmentType BLOCK_EFFICIENCY      = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.BLOCK_EFFICIENCY));
    public static final MinecraftEnchantmentType BLOCK_FORTUNE         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.BLOCK_FORTUNE));
    public static final MinecraftEnchantmentType CHANNELING            = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.CHANNELING));
    public static final MinecraftEnchantmentType DEPTH_STRIDER         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.DEPTH_STRIDER));
    public static final MinecraftEnchantmentType FIRE_ASPECT           = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FIRE_ASPECT));
    public static final MinecraftEnchantmentType FIRE_PROTECTION       = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FIRE_PROTECTION));
    public static final MinecraftEnchantmentType FISHING_LUCK          = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FISHING_LUCK));
    public static final MinecraftEnchantmentType FISHING_SPEED         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FISHING_SPEED));
    public static final MinecraftEnchantmentType FLAMING_ARROWS        = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FLAMING_ARROWS));
    public static final MinecraftEnchantmentType FROST_WALKER          = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.FROST_WALKER));
    public static final MinecraftEnchantmentType IMPALING              = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.IMPALING));
    public static final MinecraftEnchantmentType INFINITY_ARROWS       = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.INFINITY_ARROWS));
    public static final MinecraftEnchantmentType KNOCKBACK             = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.KNOCKBACK));
    public static final MinecraftEnchantmentType LOYALTY               = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.LOYALTY));
    public static final MinecraftEnchantmentType MENDING               = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.MENDING));
    public static final MinecraftEnchantmentType MOB_LOOTING           = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.MOB_LOOTING));
    public static final MinecraftEnchantmentType MULTISHOT             = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.MULTISHOT));
    public static final MinecraftEnchantmentType PIERCING              = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.PIERCING));
    public static final MinecraftEnchantmentType POWER_ARROWS          = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.POWER_ARROWS));
    public static final MinecraftEnchantmentType PROJECTILE_PROTECTION = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.PROJECTILE_PROTECTION));
    public static final MinecraftEnchantmentType PUNCH_ARROWS          = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.PUNCH_ARROWS));
    public static final MinecraftEnchantmentType QUICK_CHARGE          = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.QUICK_CHARGE));
    public static final MinecraftEnchantmentType RESPIRATION           = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.RESPIRATION));
    public static final MinecraftEnchantmentType RIPTIDE               = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.RIPTIDE));
    public static final MinecraftEnchantmentType SILK_TOUCH            = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SILK_TOUCH));
    public static final MinecraftEnchantmentType SMITE                 = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SMITE));
    public static final MinecraftEnchantmentType SOUL_SPEED            = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SOUL_SPEED));
    public static final MinecraftEnchantmentType SWEEPING_EDGE         = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SWEEPING_EDGE));
    public static final MinecraftEnchantmentType SWIFT_SNEAK           = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.SWIFT_SNEAK));
    public static final MinecraftEnchantmentType THORNS                = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.THORNS));
    public static final MinecraftEnchantmentType UNBREAKING            = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.UNBREAKING));
    public static final MinecraftEnchantmentType VANISHING_CURSE       = new MinecraftEnchantmentType(new MouseEnchantmentState(Enchantments.VANISHING_CURSE));

    static { storage.put(MinecraftEnchantmentType.class, MinecraftEnchantmentType::parse); }

    private MinecraftEnchantmentType(MouseEnchantmentState state) { super(Objects.requireNonNull(state, "MouseEnchantmentState cannot be null")); }

    @SuppressWarnings("DataFlowIssue")
    public static MinecraftEnchantmentType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseEnchantmentState state = MouseEnchantmentState.create(prefix, name);
        if (state == null) throw new ValueFormatException("Unknown enchantment: " + prefix + ":" + name);
        return new MinecraftEnchantmentType(state);
    }

    public static MinecraftEnchantmentType create(String name)                 { return create("minecraft", name); }
    public static MinecraftEnchantmentType create(MouseEnchantmentState state) { return new MinecraftEnchantmentType(state); }

    @Override public IValType getValType()                                     { return ValType.MINECRAFT_ENCHANTMENT; }
    public MouseEnchantmentState asEnchantmentState()                          { return value; }
    public String getPrefix()                                                  { return value.getRegistryKey().getNamespace(); }
    public String getName()                                                    { return value.getRegistryKey().getPath(); }

    public static MinecraftEnchantmentType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftEnchantmentType enchType) return enchType;
        else if (value instanceof MouseEnchantmentState state) return create(state);
        else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftEnchantmentType from " + value);
    }

    public static MinecraftEnchantmentType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty enchantment string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Enchantment name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftEnchantmentType fromString(@Nullable String value)                         { return parse((String) null, value); }
    @Override @SuppressWarnings("unchecked") public MinecraftEnchantmentType createType(Object value) { return parse(value); }
}