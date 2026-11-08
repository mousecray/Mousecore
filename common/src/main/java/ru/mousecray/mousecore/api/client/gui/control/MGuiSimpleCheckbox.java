/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.control.base.MGuiBaseCheckbox;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.*;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiSimpleCheckbox extends MGuiBaseCheckbox<MGuiSimpleCheckbox> {
    public MGuiSimpleCheckbox(GuiShape shape, MGuiString text, FontRenderer fontRenderer) {
        super(shape, text, fontRenderer);
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE,
                        GuiVector.of(184, 0), GuiVector.of(8)
                )
                .addTexture(0)
                .addTexture(1, HOVERED)
                .addTexture(2, PRESSED)
                .addTexture(3, SELECTED)
                .addTexture(4, SELECTED, HOVERED)
                .addTexture(5, SELECTED, PRESSED)
                .build());
        setColorPack(MGuiColorPack.Builder
                .create(14737632)
                .addColor(10526880, DISABLED)
                .addColor(14737632)
                .addColor(15592941, HOVERED)
                .addColor(13948116, PRESSED)
                .addColor(14737632, SELECTED)
                .addColor(15592941, SELECTED, HOVERED)
                .addColor(13948116, SELECTED, PRESSED)
                .build());
    }
}
