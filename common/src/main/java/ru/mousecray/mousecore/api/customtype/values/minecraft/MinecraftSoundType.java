/****************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 *************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.minecraft;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.minecraft.MouseRegistryEntry;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MinecraftSoundType extends OtherType<MouseRegistryEntry<SoundEvent>> {
    static { storage.put(MinecraftSoundType.class, MinecraftSoundType::parse); }

    private MinecraftSoundType(MouseRegistryEntry<SoundEvent> entry) { super(Objects.requireNonNull(entry, "MouseRegistryEntry cannot be null")); }

    @SuppressWarnings({ "DataFlowIssue", "deprecation" })
    public static MinecraftSoundType create(String prefix, String name) {
        prefix = MouseStrings.trimWith(Objects.requireNonNull(prefix), true, '\t');
        if (prefix.isEmpty()) throw new ValueFormatException("prefix cannot be empty");
        if (prefix.contains("\t")) throw new ValueFormatException("prefix cannot contains tabs");
        if (prefix.contains(" ")) throw new ValueFormatException("prefix cannot contains space");

        name = MouseStrings.trimWith(Objects.requireNonNull(name), true, '\t');
        if (name.isEmpty()) throw new ValueFormatException("name cannot be empty");
        if (name.contains("\t")) throw new ValueFormatException("name cannot contains tabs");
        if (name.contains(" ")) throw new ValueFormatException("name cannot contains space");

        MouseRegistryEntry<SoundEvent> entry = MouseRegistryEntry.create(BuiltInRegistries.SOUND_EVENT, prefix, name);
        if (entry == null) throw new ValueFormatException("Unknown sound event: " + prefix + ":" + name);
        return new MinecraftSoundType(entry);
    }

    public static MinecraftSoundType create(String name)                          { return create("minecraft", name); }
    public static MinecraftSoundType create(MouseRegistryEntry<SoundEvent> entry) { return new MinecraftSoundType(entry); }

    @SuppressWarnings("deprecation")
    public static MinecraftSoundType create(SoundEvent sound) {
        return new MinecraftSoundType(MouseRegistryEntry.create(BuiltInRegistries.SOUND_EVENT, sound));
    }

    public static MinecraftSoundType create(Holder<SoundEvent> soundHolder) { return create(soundHolder.value()); }

    @Override public IValType getValType()                                  { return ValType.MINECRAFT_SOUND; }
    public MouseRegistryEntry<SoundEvent> asRegistryEntry()                 { return value; }
    public String getPrefix()                                               { return value.getNamespace(); }
    public String getName()                                                 { return value.getPath(); }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static MinecraftSoundType parse(@Nullable Object value) throws UnsupportedValException, ValueFormatException {
        if (value instanceof MinecraftSoundType soundType) return soundType;
        else if (value instanceof MouseRegistryEntry<?> entry && entry.getRegistry() == BuiltInRegistries.SOUND_EVENT) {
            return create((MouseRegistryEntry<SoundEvent>) entry);
        } else if (value instanceof String str) return parse((String) null, str);
        throw new UnsupportedValException("Cannot parse MinecraftSoundType from " + value);
    }

    public static MinecraftSoundType parse(@Nullable String expectedPrefix, @Nullable String value) throws ValueFormatException {
        String clean = MouseStrings.trimWith(value, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty sound event string");

        String defaultNamespace = expectedPrefix != null ? expectedPrefix : "minecraft";
        int    colonIdx         = clean.indexOf(':');

        if (colonIdx == -1) return create(defaultNamespace, clean);
        else {
            String prefixPart = MouseStrings.trimWith(clean.substring(0, colonIdx), true, '\t');
            String namePart   = MouseStrings.trimWith(clean.substring(colonIdx + 1), true, '\t');
            if (prefixPart == null || prefixPart.isEmpty()) prefixPart = defaultNamespace;
            if (namePart == null || namePart.isEmpty()) throw new ValueFormatException("Sound event name is missing in: '" + clean + "'");
            return create(prefixPart, namePart);
        }
    }

    public static MinecraftSoundType fromString(@Nullable String value)                         { return parse((String) null, value); }
    @Override @SuppressWarnings("unchecked") public MinecraftSoundType createType(Object value) { return parse(value); }
}