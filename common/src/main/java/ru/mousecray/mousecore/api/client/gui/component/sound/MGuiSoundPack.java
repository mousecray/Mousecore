/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class MGuiSoundPack {
    public static MGuiSoundPack EMPTY() { return new MGuiSoundPack(new Object2ObjectArrayMap<>()); }

    public static MGuiSoundPack CONTROL_SIMPLE() {
        return Builder.create().addSound(MSoundSourceType.PRESS, SoundEvents.UI_BUTTON_CLICK).build();
    }

    private final Object2ObjectMap<MSoundSourceType, SoundEvent> sounds;

    private MGuiSoundPack(Object2ObjectMap<MSoundSourceType, SoundEvent> sounds) { this.sounds = sounds; }

    @Nullable public SoundEvent getSound(MSoundSourceType sourceType)            { return sounds.get(sourceType); }

    @SideOnly(Side.CLIENT)
    public static class Builder {
        private final Object2ObjectMap<MSoundSourceType, SoundEvent> sounds = new Object2ObjectArrayMap<>();

        private Builder()                            { }

        public static MGuiSoundPack.Builder create() { return new MGuiSoundPack.Builder(); }

        public MGuiSoundPack.Builder addSound(MSoundSourceType sourceType, SoundEvent sound) {
            sounds.put(sourceType, sound);
            return this;
        }

        public MGuiSoundPack build() { return new MGuiSoundPack(sounds); }
    }
}