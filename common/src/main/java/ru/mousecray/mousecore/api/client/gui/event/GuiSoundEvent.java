/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;

@SideOnly(Side.CLIENT)
public class GuiSoundEvent<T extends MGuiElement<T>> extends GuiEvent<T> {
    private SoundEvent       sound;
    private MSoundSourceType source;
    private SoundHandler     handler;

    void setSource(MSoundSourceType source) { this.source = source; }
    void setSound(SoundEvent sound)         { this.sound = sound; }
    void setHandler(SoundHandler handler)   { this.handler = handler; }
    public MSoundSourceType getSource()     { return source; }
    public SoundEvent getSound()            { return sound; }
    public SoundHandler getHandler()        { return handler; }
}